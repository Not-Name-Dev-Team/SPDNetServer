package me.catand.spdnetserver.entitys;

import com.alibaba.fastjson2.annotation.JSONField;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import me.catand.spdnetserver.data.Status;

import java.util.List;

@Entity
@Getter
@Setter
public class Player {

	@Column(unique = true)
	@JSONField(serialize = false)
	private long qq;

	@Id
	@Column(unique = true)
	private String name;

	@Column(unique = true)
	@JSONField(serialize = false)
	private String key;

	private String power;

	@JSONField(serialize = false)
	private String email;

	@Lob
	@JSONField(serialize = false)
	private String cloudSaveData;

	@Transient
	private Status status;

	@OneToMany(mappedBy = "player")
	@JSONField(serialize = false)
	private List<GameRecord> gameRecords;
}
