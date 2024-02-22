package me.catand.spdnetserver;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.catand.spdnetserver.data.actions.*;
import me.catand.spdnetserver.data.events.SError;
import me.catand.spdnetserver.data.events.SExit;
import me.catand.spdnetserver.data.events.SJoin;
import me.catand.spdnetserver.entitys.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Getter
@Service
public class SocketService {
	private static SocketService instance;
	@Autowired
	private PlayerRepository playerRepository;
	@Autowired
	private SpdProperties spdProperties;
	private SocketIOServer server;
	private Map<UUID, Player> playerMap = new HashMap<>();
	private Sender sender;
	private Handler handler;
	private SocketIONamespace spdNetNamespace;

	public static SocketService getInstance() {
		if (instance == null) {
			synchronized (SocketService.class) {
				if (instance == null) {
					instance = new SocketService();
				}
			}
		}
		return instance;
	}

	@PostConstruct
	public void init() {
		Configuration config = new Configuration();
		config.setHostname("0.0.0.0");
		config.setPort(21687);

		server = new SocketIOServer(config);
		spdNetNamespace = server.addNamespace("/spdnet");
		server.start();
		startAll();
		sender = new Sender(server);
		handler = new Handler(playerRepository, this, sender);
	}

	@PreDestroy
	public void stopServer() {
		server.stop();
	}

	/**
	 * 启动所有事件
	 */
	private void startAll() {
		spdNetNamespace.addConnectListener(client -> {
			HandshakeData handshakeData = client.getHandshakeData();
			// 检测Socket.IO设置的AuthToken
			LinkedHashMap<String, String> authTokenMap = (LinkedHashMap) handshakeData.getAuthToken();
			String authToken = null;
			if (authTokenMap != null && authTokenMap.containsKey("token")) {
				authToken = authTokenMap.get("token");
			}
			// 检测Query参数中的token
			String authTokenQuery = handshakeData.getSingleUrlParam("token");
			String spdVersion = handshakeData.getSingleUrlParam("SPDVersion");
			String netVersion = handshakeData.getSingleUrlParam("NetVersion");
			if (authToken == null) {
				authToken = authTokenQuery;
			}
			if (!playerRepository.existsByKey(authToken)) {
				client.sendEvent(Events.ERROR.getName(), new SError("Key无效"));
				log.info("连接失败: Key无效, " + authToken + ", " + client.getSessionId());
				client.disconnect();
			} else if (!(spdProperties.getVersion().equals(spdVersion) && (spdProperties.getNetVersion().equals(netVersion) || netVersion.equals(spdProperties.getNetVersion() + "-INDEV")))) {
				client.sendEvent(Events.ERROR.getName(), new SError("版本不匹配"));
				log.info("连接失败: 版本不匹配, SPDVersion: " + spdVersion + ", NetVersion: " + netVersion + ", " + client.getSessionId());
				client.disconnect();
			} else {
				Player player = playerRepository.findByKey(authToken);
				playerMap.put(client.getSessionId(), player);
				sender.sendBroadcastJoin(new SJoin(player.getName(), player.getPower()));
				log.info("玩家已连接: " + player.getName() + ", " + client.getSessionId());
			}
		});
		spdNetNamespace.addDisconnectListener(client -> {
			Player player = playerMap.get(client.getSessionId());
			if (player != null) {
				playerMap.remove(client.getSessionId());
				sender.sendBroadcastExit(new SExit(player.getName()));
				log.info("玩家已断开连接: " + player.getName() + ", " + client.getSessionId());
			}
		});
		spdNetNamespace.addEventListener(Actions.ACHIEVEMENT.getName(), CAchievement.class, (client, data, ackSender) -> {
			handler.handleAchievement(playerMap.get(client.getSessionId()), data);
		});
		spdNetNamespace.addEventListener(Actions.BACKPACK.getName(), CBackpack.class, (client, data, ackSender) -> {
			handler.handleBackpack(playerMap.get(client.getSessionId()), data);
		});
		spdNetNamespace.addEventListener(Actions.CHAT_MESSAGE.getName(), CChatMessage.class, (client, data, ackSender) -> {
			handler.handleChatMessage(playerMap.get(client.getSessionId()), data);
		});
		spdNetNamespace.addEventListener(Actions.DEATH.getName(), CDeath.class, (client, data, ackSender) -> {
			handler.handleDeath(playerMap.get(client.getSessionId()), data);
		});
		spdNetNamespace.addEventListener(Actions.ENTER_DUNGEON.getName(), CEnterDungeon.class, (client, data, ackSender) -> {
			handler.handleEnterDungeon(playerMap.get(client.getSessionId()), data);
		});
		spdNetNamespace.addEventListener(Actions.ERROR.getName(), CError.class, (client, data, ackSender) -> {
			handler.handleError(data);
		});
		spdNetNamespace.addEventListener(Actions.GIVE_ITEM.getName(), CGiveItem.class, (client, data, ackSender) -> {
			handler.handleGiveItem(playerMap.get(client.getSessionId()), data);
		});
		spdNetNamespace.addEventListener(Actions.FLOATING_TEXT.getName(), CFloatingText.class, (client, data, ackSender) -> {
			handler.handleFloatingText(playerMap.get(client.getSessionId()), data);
		});
		spdNetNamespace.addEventListener(Actions.LEAVE_DUNGEON.getName(), CLeaveDungeon.class, (client, data, ackSender) -> {
			handler.handleLeaveDungeon(playerMap.get(client.getSessionId()));
		});
		spdNetNamespace.addEventListener(Actions.PLAYER_MOVE.getName(), CPlayerMove.class, (client, data, ackSender) -> {
			handler.handlePlayerMove(playerMap.get(client.getSessionId()), data);
		});
		spdNetNamespace.addEventListener(Actions.WIN.getName(), CWin.class, (client, data, ackSender) -> {
			handler.handleWin(playerMap.get(client.getSessionId()), data);
		});
	}
}