package me.catand.spdnetserver.data;

import com.alibaba.fastjson2.JSONObject;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@Getter
public class Status {
	private final int challenges;
	private final long seed;
	private final int depth;
	private final JSONObject hero;
}
