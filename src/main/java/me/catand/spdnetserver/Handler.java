package me.catand.spdnetserver;

import com.alibaba.fastjson2.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Handler {
	@Autowired
	private PlayerRepository playerRepository;

	public static void handleAchievement(String achievement, boolean unique) {
	}

	public static void handleBackpack(JSONObject belongings) {
	}

	public static void handleChatMessage(String message) {
	}

	public static void handleDeath(String cause) {
	}

	public static void handleEnterDungeon(JSONObject status) {
	}

	public static void handleError(String message) {
	}

	public static void handleGiveItem(JSONObject item) {
	}

	public static void handleFloatingText(int type, String text) {
	}

	public static void handleLeaveDungeon() {
	}

	public static void handlePlayerMove(int depth, int pos) {
	}

	public static void handleWin(JSONObject record) {
	}
}
