package me.catand.spdnetserver.entitys;

import com.alibaba.fastjson2.JSONObject;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class GameRecord {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Transient
	private Class cause;
	private String causeClassName;
	private boolean win;
	private String heroClass;
	private int armorTier;
	private int herolevel;
	private int depth;
	private boolean ascending;
	@Transient
	private JSONObject gameData;
	private String gameDataString;
	private String gameID;
	private int score;
	private String customSeed;
	private boolean daily;
	private String date;
	private String version;
	@ManyToOne
	private Player player;

	public void setCause(Class cause) {
		this.cause = cause;
		this.causeClassName = (cause != null) ? cause.getName() : null;
	}

	public Class getCause() {
		if (cause == null && causeClassName != null) {
			try {
				cause = Class.forName(causeClassName);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return cause;
	}

	public void setGameData(JSONObject gameData) {
		this.gameData = gameData;
		this.gameDataString = gameData.toJSONString();
	}

	public JSONObject getGameData() {
		if (gameData == null && gameDataString != null) {
			gameData = JSONObject.parseObject(gameDataString);
		}
		return gameData;
	}
}
