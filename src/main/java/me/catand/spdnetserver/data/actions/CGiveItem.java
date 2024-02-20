package me.catand.spdnetserver.data.actions;

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
public class CGiveItem extends Data {
	private JSONObject item;
}
