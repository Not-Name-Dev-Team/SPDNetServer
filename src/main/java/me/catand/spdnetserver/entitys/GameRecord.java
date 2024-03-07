package me.catand.spdnetserver.entitys;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.annotation.JSONField;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class GameRecord {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long gameID;
	private String cause;
	private boolean win;
	private int score;
	private String custom_seed;
	private boolean daily;
	@JSONField(name="class")
	private String classValue;
	private int tier;
	private int level;
	private int depth;
	private boolean ascending;
	private String date;
	private String version;
	private String gameData;
	@Transient
	@JSONField(serialize = false)
	private JSONObject gameDataObject;
	@ManyToOne
	@JSONField(serialize = false)
	private Player player;
	public void setGameData(JSONObject gameData) {
		this.gameData = gameData.toJSONString();
		this.gameDataObject = JSON.parseObject(this.gameData);
	}
}
