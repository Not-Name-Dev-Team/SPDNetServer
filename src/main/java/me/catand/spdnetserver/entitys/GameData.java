package me.catand.spdnetserver.entitys;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Entity
@Getter
@Setter
public class GameData {
	@Id
	private long id;
	private String badges;
	private int game_version;
	private String custom_seed;
	private int seed;
	@Lob
	private String stats;
	@Transient
	private JSONObject statsObject;
	private int challenges;
	@Lob
	private String handlers;
	@Transient
	private Map<String, Object> handlersObject;
	private boolean daily;
	private boolean daily_replay;
	@Lob
	private String hero;
	@Transient
	private JSONObject heroObject;
	@OneToOne
	@MapsId
	private GameRecord gameRecord;

	@Getter
	@Setter
	public static class Badges {
		private List<String> badges;
	}

	public JSONObject getStatsObject() {
		return JSON.parseObject(this.stats);
	}

	public void setStatsObject(JSONObject statsObject) {
		this.statsObject = statsObject;
		this.stats = statsObject.toJSONString();
	}

	public JSONObject getHeroObject() {
		return JSON.parseObject(this.hero);
	}

	public void setHandlersObject(Map<String, Object> handlersObject) {
		this.handlersObject = handlersObject;
		this.handlers = JSON.toJSONString(handlersObject);
	}

	public Map<String, Object> getHandlersObject() {
		return JSON.parseObject(this.handlers, Map.class);
	}

	public void setHeroObject(JSONObject heroObject) {
		this.heroObject = heroObject;
		this.hero = heroObject.toJSONString();
	}
}

