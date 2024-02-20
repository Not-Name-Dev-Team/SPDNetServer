package me.catand.spdnetserver.data.events;

import com.alibaba.fastjson2.JSONObject;
import me.catand.spdnetserver.data.Data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SGiveItem extends Data {
	private String name;
	private JSONObject item;
}
