package me.catand.spdnetserver;

import lombok.extern.slf4j.Slf4j;
import me.catand.spdnetserver.data.actions.*;
import me.catand.spdnetserver.data.events.SChatMessage;
import me.catand.spdnetserver.entitys.Player;

@Slf4j
public class Handler {
	private PlayerRepository playerRepository;
	private SocketService socketService;
	private Sender sender;

	public Handler(PlayerRepository playerRepository, SocketService socketService, Sender sender) {
		this.playerRepository = playerRepository;
		this.socketService = socketService;
		this.sender = sender;
	}

	public void handleAchievement(Player player, CAchievement cAchievement) {
	}

	public void handleBackpack(Player player, CBackpack cBackpack) {
	}

	public void handleChatMessage(Player player, CChatMessage cChatMessage) {
		log.info("玩家{}发送了消息：{}", player.getName(), cChatMessage.getMessage());
		sender.sendBroadcastChatMessage(new SChatMessage(player.getName(), cChatMessage.getMessage()));
	}

	public void handleDeath(Player player, CDeath cDeath) {
		log.info("玩家{}死亡，死因：{}", player.getName(), cDeath.getCause());
		sender.sendBroadcastDeath(player.getName(), cDeath.getCause());
	}

	public void handleEnterDungeon(Player player, CEnterDungeon cEnterDungeon) {
	}

	public void handleError(CError cError) {
	}

	public void handleGiveItem(Player player, CGiveItem cGiveItem) {
	}

	public void handleFloatingText(Player player, CFloatingText cFloatingText) {
	}

	public void handleLeaveDungeon(Player player) {
	}

	public void handlePlayerMove(Player player, CPlayerMove cPlayerMove) {
	}

	public void handleWin(Player player, CWin cWin) {
	}
}
