package me.catand.spdnetserver.data;

import com.alibaba.fastjson2.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Status {
	private int challenges;
	private long seed;
	private int depth;
}
