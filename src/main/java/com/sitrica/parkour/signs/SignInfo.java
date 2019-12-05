package com.sitrica.parkour.signs;

import java.util.Optional;

import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;

public class SignInfo {

	public final String course, type;
	public final Location location;

	public SignInfo(Location location, String course, String type) {
		this.location = location;
		this.course = course;
		this.type = type;
	}

	public Location getLocation() {
		return location;
	}

	/**
	 * @return Optional<Sign> if the block is still a sign.
	 */
	public Optional<Sign> getSign() {
		BlockState state = location.getBlock().getState();
		if (!(state instanceof Sign))
			return Optional.empty();
		return Optional.ofNullable((Sign) state);
	}

	public String getCourseName() {
		return course;
	}

	public String getType() {
		return type;
	}

}
