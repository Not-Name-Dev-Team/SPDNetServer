package me.catand.spdnetserver.data.actions;

import me.catand.spdnetserver.data.Data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CFloatingText extends Data {
	private int type;
	private String text;
}
