package me.catand.spdnetserver;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIOServer;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import me.catand.spdnetserver.entitys.Player;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Getter
@Service
public class SocketService {
	@Autowired
	private PlayerRepository playerRepository;
	@Autowired
	private SpdProperties spdProperties;
	private SocketIOServer server;
	private Map<String, Player> playerMap = new HashMap<>();
	Logger logger;

	@PostConstruct
	public void init() {
		Configuration config = new Configuration();
		config.setHostname("0.0.0.0");
		config.setPort(21687);

		server = new SocketIOServer(config);
		server.start();
		startAll(server);
	}

	@PreDestroy
	public void stopServer() {
		server.stop();
	}

	private void startAll(SocketIOServer server) {
		server.addConnectListener(client -> {
			HandshakeData handshakeData = client.getHandshakeData();
			String authToken = handshakeData.getSingleUrlParam("token");
			String spdVersion = handshakeData.getSingleUrlParam("SPDVersion");
			String netVersion = handshakeData.getSingleUrlParam("NetVersion");
			if (authToken == null || !playerRepository.existsByKey(authToken)) {
				client.sendEvent(Events.ERROR.getName(), "Key无效");
				client.disconnect();
			} else if (!spdProperties.getVersion().equals(spdVersion) || !spdProperties.getNetVersion().equals(netVersion)) {
				client.sendEvent(Events.ERROR.getName(), "版本不匹配");
				client.disconnect();
			} else {
				Player player = playerRepository.findByKey(authToken);
				playerMap.put(client.getSessionId().toString(), player);
				// TODO 给其他客户端发送上线消息
				logger.info("玩家已连接: " + player.getName());
			}
		});
		server.addDisconnectListener(client -> {
			Player player = playerMap.get(client.getSessionId().toString());
			playerMap.remove(client.getSessionId().toString());
			// TODO 给其他客户端发送下线消息
			logger.info("玩家已断开连接: " + player.getName());
		});
		server.addEventListener(Actions.ACHIEVEMENT.getName(), JSONArray.class, (client, data, ackSender) -> {
			String achievement = data.getFirst().toString();
			boolean unique = (boolean) data.get(1);
			Handler.handleAchievement(achievement, unique);
		});
		server.addEventListener(Actions.BACKPACK.getName(), JSONObject.class, (client, belongings, ackSender) -> {
			Handler.handleBackpack(belongings);
		});
		server.addEventListener(Actions.CHAT_MESSAGE.getName(), String.class, (client, message, ackSender) -> {
			Handler.handleChatMessage(message);
		});
		server.addEventListener(Actions.DEATH.getName(), String.class, (client, cause, ackSender) -> {
			Handler.handleDeath(cause);
		});
		server.addEventListener(Actions.ENTER_DUNGEON.getName(), JSONObject.class, (client, status, ackSender) -> {
			Handler.handleEnterDungeon(status);
		});
		server.addEventListener(Actions.ERROR.getName(), String.class, (client, message, ackSender) -> {
			Handler.handleError(message);
		});
		server.addEventListener(Actions.GIVE_ITEM.getName(), JSONObject.class, (client, item, ackSender) -> {
			Handler.handleGiveItem(item);
		});
		server.addEventListener(Actions.FLOATING_TEXT.getName(), JSONArray.class, (client, data, ackSender) -> {
			int type = (int) data.get(0);
			String text = data.get(1).toString();
			Handler.handleFloatingText(type, text);
		});
		server.addEventListener(Actions.LEAVE_DUNGEON.getName(), Void.class, (client, data, ackSender) -> {
			Handler.handleLeaveDungeon();
		});
		server.addEventListener(Actions.PLAYER_MOVE.getName(), JSONArray.class, (client, data, ackSender) -> {
			int depth = (int) data.get(0);
			int pos = (int) data.get(1);
			Handler.handlePlayerMove(depth, pos);
		});
		server.addEventListener(Actions.WIN.getName(), JSONObject.class, (client, record, ackSender) -> {
			Handler.handleWin(record);
		});
	}

	public void sendEvent(String event, Object data) {
		server.getBroadcastOperations().sendEvent(event, data);
	}
}