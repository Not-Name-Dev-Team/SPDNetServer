package me.catand.spdnetserver;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIOServer;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.catand.spdnetserver.data.actions.*;
import me.catand.spdnetserver.entitys.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
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
		server.addConnectListener(client -> {
			HandshakeData handshakeData = client.getHandshakeData();
			String authToken = handshakeData.getSingleUrlParam("token");
			String spdVersion = handshakeData.getSingleUrlParam("SPDVersion");
			String netVersion = handshakeData.getSingleUrlParam("NetVersion");
			if (authToken == null || !playerRepository.existsByKey(authToken)) {
				client.sendEvent(Events.ERROR.getName(), "Key无效");
				log.info(client.getSessionId() + "连接失败: Key无效, " + authToken);
				client.disconnect();
			} else if (!spdProperties.getVersion().equals(spdVersion) || !spdProperties.getNetVersion().equals(netVersion)) {
				client.sendEvent(Events.ERROR.getName(), "版本不匹配");
				log.info(client.getSessionId() + "连接失败: 版本不匹配, SPDVersion: " + spdVersion + ", NetVersion: " + netVersion);
				client.disconnect();
			} else {
				Player player = playerRepository.findByKey(authToken);
				playerMap.put(client.getSessionId(), player);
				sender.sendBroadcastJoin(player.getName(), player.getPower());
				log.info("玩家已连接: " + player.getName() + ", " + client.getSessionId());
			}
		});
		server.addDisconnectListener(client -> {
			Player player = playerMap.get(client.getSessionId());
			playerMap.remove(client.getSessionId());
			sender.sendBroadcastExit(player.getName());
			log.info("玩家已断开连接: " + player.getName(), client.getSessionId());
		});
		server.addEventListener(Actions.ACHIEVEMENT.getName(), CAchievement.class, (client, data, ackSender) -> {
			handler.handleAchievement(playerMap.get(client.getSessionId()), data);
		});
		server.addEventListener(Actions.BACKPACK.getName(), CBackpack.class, (client, data, ackSender) -> {
			handler.handleBackpack(playerMap.get(client.getSessionId()), data);
		});
		server.addEventListener(Actions.CHAT_MESSAGE.getName(), CChatMessage.class, (client, data, ackSender) -> {
			handler.handleChatMessage(playerMap.get(client.getSessionId()), data);
		});
		server.addEventListener(Actions.DEATH.getName(), CDeath.class, (client, data, ackSender) -> {
			handler.handleDeath(playerMap.get(client.getSessionId()), data);
		});
		server.addEventListener(Actions.ENTER_DUNGEON.getName(), CEnterDungeon.class, (client, data, ackSender) -> {
			handler.handleEnterDungeon(playerMap.get(client.getSessionId()), data);
		});
		server.addEventListener(Actions.ERROR.getName(), CError.class, (client, data, ackSender) -> {
			handler.handleError(data);
		});
		server.addEventListener(Actions.GIVE_ITEM.getName(), CGiveItem.class, (client, data, ackSender) -> {
			handler.handleGiveItem(playerMap.get(client.getSessionId()), data);
		});
		server.addEventListener(Actions.FLOATING_TEXT.getName(), CFloatingText.class, (client, data, ackSender) -> {
			handler.handleFloatingText(playerMap.get(client.getSessionId()), data);
		});
		server.addEventListener(Actions.LEAVE_DUNGEON.getName(), CLeaveDungeon.class, (client, data, ackSender) -> {
			handler.handleLeaveDungeon(playerMap.get(client.getSessionId()));
		});
		server.addEventListener(Actions.PLAYER_MOVE.getName(), CPlayerMove.class, (client, data, ackSender) -> {
			handler.handlePlayerMove(playerMap.get(client.getSessionId()), data);
		});
		server.addEventListener(Actions.WIN.getName(), CWin.class, (client, data, ackSender) -> {
			handler.handleWin(playerMap.get(client.getSessionId()), data);
		});
	}
}