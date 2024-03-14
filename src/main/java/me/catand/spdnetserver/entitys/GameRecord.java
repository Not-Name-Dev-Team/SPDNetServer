package me.catand.spdnetserver.entitys;

import com.alibaba.fastjson2.annotation.JSONField;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class GameRecord {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JSONField(serialize = false)
	private long id;
	@JSONField(serialize = false)
	private int gameMode;
	private String cause;
	private boolean win;
	private int score;
	private String custom_seed;
	private boolean daily;
	private String _class;
	private int tier;
	private int level;
	private int depth;
	private boolean ascending;
	private String date;
	private String version;
	@OneToOne(cascade = CascadeType.ALL, mappedBy = "gameRecord")
	@JSONField(serialize = false)
	private GameData gameData;
	@ManyToOne
	@JoinColumn(name = "player_id")
	@JSONField(serialize = false)
	private Player player;
	public void setGameData(GameData gameData) {
		this.gameData = gameData;
		gameData.setGameRecord(this);
	}
}
