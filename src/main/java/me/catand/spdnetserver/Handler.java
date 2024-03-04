package me.catand.spdnetserver;

import com.corundumstudio.socketio.SocketIOClient;
import lombok.extern.slf4j.Slf4j;
import me.catand.spdnetserver.data.Status;
import me.catand.spdnetserver.data.actions.*;
import me.catand.spdnetserver.data.events.*;
import me.catand.spdnetserver.entitys.Player;

import java.util.Map;
import java.util.UUID;

@Slf4j
public class Handler {
	private PlayerRepository playerRepository;
	private SocketService socketService;
	private Sender sender;
	private Map<UUID, Player> playerMap;

	public Handler(PlayerRepository playerRepository, SocketService socketService, Sender sender, Map<UUID, Player> playerMap) {
		this.playerRepository = playerRepository;
		this.socketService = socketService;
		this.sender = sender;
		this.playerMap = playerMap;
	}

	public void handleAchievement(Player player, CAchievement cAchievement) {
		boolean hasAchievement = playerRepository.hasAchievement(player.getName(), cAchievement.getBadgeEnumString());
		log.info("玩家{}获得了成就{}，是否已经获得：{}", player.getName(), cAchievement.getBadgeEnumString(), hasAchievement);
		if (!hasAchievement) {
			player.getAchievements().add(cAchievement.getBadgeEnumString());
		}
		sender.sendBroadcastAchievement(new SAchievement(player.getName(), cAchievement.getBadgeEnumString(), hasAchievement));
	}

	public void handleAnkhUsed(Player player, CAnkhUsed cAnkhUsed) {
	}

	public void handleArmorUpdate(Player player, CArmorUpdate cArmorUpdate) {
	}

	public void handleChatMessage(Player player, CChatMessage cChatMessage) {
		log.info("玩家{}发送了消息：{}", player.getName(), cChatMessage.getMessage());
		sender.sendBroadcastChatMessage(new SChatMessage(player.getName(), cChatMessage.getMessage()));
	}

	public void handleDeath(Player player, CDeath cDeath) {
		log.info("玩家{}死亡，死因：{}", player.getName(), cDeath.getRecord());
		sender.sendBroadcastDeath(new SDeath(player.getName(), cDeath.getRecord()));
	}

	public void handleEnterDungeon(SocketIOClient client, Player player, CEnterDungeon cEnterDungeon) {
		Status status = cEnterDungeon.getStatus();
		player.setStatus(status);
		playerMap.put(client.getSessionId(), player);
		sender.sendBroadcastEnterDungeon(new SEnterDungeon(player.getName(), status));
		log.info("玩家{}以{}挑进入了{}地牢第{}层", player.getName(), Challenges.countActiveChallenges(status.getChallenges()), status.getSeed(), status.getDepth());
	}

	public void handleError(Player player, CError cError) {
	}

	public void handleFloatingText(Player player, CFloatingText cFloatingText) {
		sender.sendBroadcastFloatingText(new SFloatingText(player.getName(), cFloatingText.getColor(), cFloatingText.getText(), cFloatingText.getIcon()));
	}

	public void handleGiveItem(Player player, CGiveItem cGiveItem) {
		playerMap.forEach((uuid, player1) -> {
			if (player1.getName().equals(cGiveItem.getTargetName())) {
				sender.sendGiveItem(socketService.getServer().getNamespace("/spdnet").getClient(uuid), new SGiveItem(player.getName(), cGiveItem.getItem()));
			}
		});
	}

	public void handleHero(Player player, CHero cHero) {
		playerMap.forEach((uuid, player1) -> {
			if (player1.getName().equals(cHero.getSourceName())) {
				sender.sendHero(socketService.getServer().getNamespace("/spdnet").getClient(uuid), new SHero(player.getName(), cHero.getHero()));
			}
		});
	}

	public void handleLeaveDungeon(Player player, CLeaveDungeon cLeaveDungeon) {
		sender.sendBroadcastLeaveDungeon(new SLeaveDungeon(player.getName()));
	}

	public void handlePlayerChangeFloor(Player player, CPlayerChangeFloor cPlayerChangeFloor) {
		sender.sendBroadcastPlayerChangeFloor(new SPlayerChangeFloor(player.getName(), cPlayerChangeFloor.getDepth()));
	}

	public void handlePlayerMove(Player player, CPlayerMove cPlayerMove) {
		sender.sendBroadcastPlayerMove(new SPlayerMove(player.getName(), cPlayerMove.getPos()));
		log.info("玩家{}移动到了{}", player.getName(), cPlayerMove.getPos());
	}

	public void handleRequestPlayerList(SocketIOClient client, CRequestPlayerList cRequestPlayerList) {
		sender.sendPlayerList(client, new SPlayerList(playerMap));
		log.info("玩家{}请求了玩家列表", playerMap.get(client.getSessionId()).getName());
	}

	public void handleViewHero(Player player, CViewHero cViewHero) {
		log.info("玩家{}请求查看玩家{}", player.getName(), cViewHero.getTargetName());
		playerMap.forEach((uuid, player1) -> {
			if (player1.getName().equals(cViewHero.getTargetName())) {
				sender.sendViewHero(socketService.getServer().getNamespace("/spdnet").getClient(uuid), new SViewHero(player.getName()));
			}
		});
	}

	public void handleWin(Player player, CWin cWin) {
	}
}
