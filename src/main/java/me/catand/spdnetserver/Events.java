package me.catand.spdnetserver;

import lombok.Getter;

/**
 * 消息接收类型
 */
@Getter
public enum Events {
	ACHIEVEMENT("achievement"),
	ANKH_USED("ankhUsed"),
	ARMOR_UPDATE("armorUpdate"),
	CHAT_MESSAGE("chatMessage"),
	DEATH("death"),
	ENTER_DUNGEON("enterDungeon"),
	ERROR("error"),
	EXIT("exit"),
	FLOATING_TEXT("floatingText"),
	GIVE_ITEM("giveItem"),
	HERO("hero"),
	INIT("init"),
	JOIN("join"),
	LEAVE_DUNGEON("leaveDungeon"),
	PLAYER_CHANGE_FLOOR("playerChangeFloor"),
	PLAYER_LIST("playerList"),
	PLAYER_MOVE("playerMove"),
	SERVER_MESSAGE("serverMessage"),
	VIEW_HERO("viewHero"),
	WIN("win");


	private final String name;

	Events(String name) {
		this.name = name;
	}

}
