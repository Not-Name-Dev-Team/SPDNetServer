package me.catand.spdnetserver;

import lombok.Getter;

/**
 * 消息发送类型
 */
@Getter
public enum Actions {
	ACHIEVEMENT("achievement"),
	BACKPACK("backpack"),
	CHAT_MESSAGE("chatMessage"),
	DEATH("death"),
	ENTER_DUNGEON("enterDungeon"),
	ERROR("error"),
	FLOATING_TEXT("floatingText"),
	GIVE_ITEM("giveItem"),
	LEAVE_DUNGEON("leaveDungeon"),
	PLAYER_MOVE("playerMove"),
	WIN("win");
	private final String name;

	Actions(String name) {
		this.name = name;
	}

}
