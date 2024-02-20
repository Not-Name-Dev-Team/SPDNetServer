package me.catand.spdnetserver;

import com.corundumstudio.socketio.SocketIOServer;
import me.catand.spdnetserver.data.events.*;

public class Sender {
	private SocketIOServer server;

	public Sender(SocketIOServer server) {
		this.server = server;
	}

	public void sendBroadcastAchievement(SAchievement data) {
		server.getBroadcastOperations().sendEvent(Events.ACHIEVEMENT.getName(), data);
	}

	public void sendBroadcastBackpack(SBackpack data) {
		server.getBroadcastOperations().sendEvent(Events.BACKPACK.getName(), data);
	}

	public void sendBroadcastChatMessage(SChatMessage sChatMessage) {
		server.getBroadcastOperations().sendEvent(Events.CHAT_MESSAGE.getName(), sChatMessage);
	}

	public void sendBroadcastDeath(SDeath data) {
		server.getBroadcastOperations().sendEvent(Events.DEATH.getName(), data);
	}

	public void sendBroadcastEnterDungeon(SEnterDungeon data) {
		server.getBroadcastOperations().sendEvent(Events.ENTER_DUNGEON.getName(), data);
	}

	public void sendBroadcastError(SError data) {
		server.getBroadcastOperations().sendEvent(Events.ERROR.getName(), data);
	}

	public void sendBroadcastExit(SExit data) {
		server.getBroadcastOperations().sendEvent(Events.EXIT.getName(), data);
	}

	public void sendBroadcastGiveItem(SGiveItem data) {
		server.getBroadcastOperations().sendEvent(Events.GIVE_ITEM.getName(), data);
	}

	public void sendBroadcastFloatingText(SFloatingText data) {
		server.getBroadcastOperations().sendEvent(Events.FLOATING_TEXT.getName(), data);
	}

	public void sendBroadcastJoin(SJoin data) {
		server.getBroadcastOperations().sendEvent(Events.JOIN.getName(), data);
	}

	public void sendBroadcastLeaveDungeon(SLeaveDungeon data) {
		server.getBroadcastOperations().sendEvent(Events.LEAVE_DUNGEON.getName(), data);
	}

	public void sendBroadcastPlayerMove(SPlayerMove data) {
		server.getBroadcastOperations().sendEvent(Events.PLAYER_MOVE.getName(), data);
	}

	public void sendBroadcastServerMessage(SServerMessage data) {
		server.getBroadcastOperations().sendEvent(Events.SERVER_MESSAGE.getName(), data);
	}

	public void sendBroadcastWin(SWin data) {
		server.getBroadcastOperations().sendEvent(Events.WIN.getName(), data);
	}
}