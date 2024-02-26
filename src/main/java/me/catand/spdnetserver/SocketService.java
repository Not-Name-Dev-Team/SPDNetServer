package me.catand.spdnetserver;

import com.alibaba.fastjson2.JSON;
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
import me.catand.spdnetserver.data.events.SInit;
import me.catand.spdnetserver.data.events.SJoin;
import me.catand.spdnetserver.entitys.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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
	private Map<UUID, Player> playerMap = new ConcurrentHashMap<>();
	private Sender sender;
	private Handler handler;
	private SocketIONamespace spdNetNamespace;
	public static ConcurrentHashMap<String, Long> seeds = new ConcurrentHashMap<>();

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
		seeds.put("seed1", 114514L);
		seeds.put("seed2", 114515L);
		server.start();
		startAll();
		sender = new Sender(server);
		handler = new Handler(playerRepository, this, sender, playerMap);
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
				SInit sInit = new SInit(player.getName(), spdProperties.getMotd(), seeds);
				sender.sendInit(client, sInit);
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
		spdNetNamespace.addEventListener(Actions.ACHIEVEMENT.getName(), String.class, (client, data, ackSender) -> {
			handler.handleAchievement(playerMap.get(client.getSessionId()), JSON.parseObject(data, CAchievement.class));
		});
		spdNetNamespace.addEventListener(Actions.ANKH_USED.getName(), String.class, (client, data, ackSender) -> {
			handler.handleAnkhUsed(playerMap.get(client.getSessionId()), JSON.parseObject(data, CAnkhUsed.class));
		});
		spdNetNamespace.addEventListener(Actions.ARMOR_UPDATE.getName(), String.class, (client, data, ackSender) -> {
			handler.handleArmorUpdate(playerMap.get(client.getSessionId()), JSON.parseObject(data, CArmorUpdate.class));
		});
		spdNetNamespace.addEventListener(Actions.CHAT_MESSAGE.getName(), String.class, (client, data, ackSender) -> {
			handler.handleChatMessage(playerMap.get(client.getSessionId()), JSON.parseObject(data, CChatMessage.class));
		});
		spdNetNamespace.addEventListener(Actions.DEATH.getName(), String.class, (client, data, ackSender) -> {
			handler.handleDeath(playerMap.get(client.getSessionId()), JSON.parseObject(data, CDeath.class));
		});
		spdNetNamespace.addEventListener(Actions.ENTER_DUNGEON.getName(), String.class, (client, data, ackSender) -> {
			handler.handleEnterDungeon(client, playerMap.get(client.getSessionId()), JSON.parseObject(data, CEnterDungeon.class));
		});
		spdNetNamespace.addEventListener(Actions.ERROR.getName(), String.class, (client, data, ackSender) -> {
			handler.handleError(playerMap.get(client.getSessionId()), JSON.parseObject(data, CError.class));
		});
		spdNetNamespace.addEventListener(Actions.FLOATING_TEXT.getName(), String.class, (client, data, ackSender) -> {
			handler.handleFloatingText(playerMap.get(client.getSessionId()), JSON.parseObject(data, CFloatingText.class));
		});
		spdNetNamespace.addEventListener(Actions.GIVE_ITEM.getName(), String.class, (client, data, ackSender) -> {
			handler.handleGiveItem(playerMap.get(client.getSessionId()), JSON.parseObject(data, CGiveItem.class));
		});
		spdNetNamespace.addEventListener(Actions.HERO.getName(), String.class, (client, data, ackSender) -> {
			handler.handleHero(playerMap.get(client.getSessionId()), JSON.parseObject(data, CHero.class));
		});
		spdNetNamespace.addEventListener(Actions.LEAVE_DUNGEON.getName(), String.class, (client, data, ackSender) -> {
			handler.handleLeaveDungeon(playerMap.get(client.getSessionId()), JSON.parseObject(data, CLeaveDungeon.class));
		});
		spdNetNamespace.addEventListener(Actions.PLAYER_CHANGE_FLOOR.getName(), String.class, (client, data, ackSender) -> {
			handler.handlePlayerChangeFloor(playerMap.get(client.getSessionId()), JSON.parseObject(data, CPlayerChangeFloor.class));
		});
		spdNetNamespace.addEventListener(Actions.PLAYER_MOVE.getName(), String.class, (client, data, ackSender) -> {
			handler.handlePlayerMove(playerMap.get(client.getSessionId()), JSON.parseObject(data, CPlayerMove.class));
		});
		spdNetNamespace.addEventListener(Actions.REQUEST_PLAYER_LIST.getName(), String.class, (client, data, ackSender) -> {
			handler.handleRequestPlayerList(client, JSON.parseObject(data, CRequestPlayerList.class));
		});
		spdNetNamespace.addEventListener(Actions.VIEW_HERO.getName(), String.class, (client, data, ackSender) -> {
			handler.handleViewHero(playerMap.get(client.getSessionId()), JSON.parseObject(data, CViewHero.class));
		});
		spdNetNamespace.addEventListener(Actions.WIN.getName(), String.class, (client, data, ackSender) -> {
			handler.handleWin(playerMap.get(client.getSessionId()), JSON.parseObject(data, CWin.class));
		});
	}
}