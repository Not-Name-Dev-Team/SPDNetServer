package me.catand.spdnetserver.data.events;

import me.catand.spdnetserver.data.Data;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SInit extends Data {
	private String motd;
	private Map<String, String> seeds;
}
