package me.catand.spdnetserver.data.events;

import com.alibaba.fastjson2.JSON;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.catand.spdnetserver.data.Data;
import me.catand.spdnetserver.entitys.GameRecord;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SDeath extends Data {
	private String name;
	private String record;

	public SDeath(String name, GameRecord record) {
		this.name = name;
		this.record = JSON.toJSONString(record);
	}
}
