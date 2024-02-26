package me.catand.spdnetserver.data.actions;

import com.alibaba.fastjson2.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.catand.spdnetserver.data.Data;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CGiveItem extends Data {
	private JSONObject item;
}
