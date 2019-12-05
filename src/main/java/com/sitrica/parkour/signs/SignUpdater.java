package com.sitrica.parkour.signs;

import com.sitrica.parkour.objects.Course;
import com.sitrica.parkour.objects.ParkourPlayer;

public abstract class SignUpdater {

	private final String name;

	public SignUpdater(String name) {
		this.name = name;
	}

	/**
	 * Update the sign lines.
	 * 
	 * @param sign The SignInfo.
	 * @param course The Course linked with the sign.
	 */
	public abstract void update(SignInfo sign, Course course);

	/**
	 * Called when a player left clicks the sign.
	 * 
	 * @param player The ParkourPlayer clicking the sign.
	 * @param sign The SignInfo.
	 * @param course The Course linked with the sign.
	 */
	public abstract void onLeftClick(ParkourPlayer player, SignInfo sign, Course course);

	/**
	 * Called when a player right clicks the sign.
	 * 
	 * @param player The ParkourPlayer clicking the sign.
	 * @param sign The SignInfo.
	 * @param course The Course linked with the sign.
	 */
	public abstract void onRightClick(ParkourPlayer player, SignInfo sign, Course course);

	public String getName() {
		return name;
	}

}
