package me.catand.spdnetserver.data.actions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CRequestLeaderboard {
	// 排行榜类型
	private String leaderboardType; // "global", "personal"
	// 排序方式
	private String sortCriteria; // "totalScore", "clearTime", "latest"
	// 无挑战筛选的时候值为 999
	private int challengeCount;
	// 只显示通关了的的记录
	private boolean winOnly;
	// 游戏模式 不筛选选择 ALL
	private String gameMode;
}