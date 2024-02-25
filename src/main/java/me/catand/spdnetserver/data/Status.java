package me.catand.spdnetserver.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Status {
	private int challenges;
	private long seed;
	private int depth;
	private int heroClass;
	/**
	 * 护甲等阶, 大多数情况下就是Armor的tier
	 * 但是有两种特殊情况:
	 * 没穿护甲的时候是0
	 * 穿职业护甲的时候是10(和板甲区分开)
	 */
	private int armorTier;
}
