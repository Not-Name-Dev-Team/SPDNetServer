package me.catand.spdnetserver.data.events;

import me.catand.spdnetserver.data.Data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SDeath extends Data {
	private String name;
	private String cause;
}