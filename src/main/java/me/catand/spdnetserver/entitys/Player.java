package me.catand.spdnetserver.entitys;

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
	private long qq;

	@Id
	@Column(unique = true)
	private String name;

	@Column(unique = true)
	private String key;

	private String power;

	private String email;

	@Lob
	private String cloudSaveData;

	@Transient
	private Status status;

	@OneToMany(mappedBy = "player")
	private List<GameRecord> gameRecords;
}
