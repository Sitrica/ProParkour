package com.sitrica.parkour.objects;

import java.util.UUID;

public class Highscore {

	private final long time, fails;
	private final String course;
	private final UUID uuid;

	public Highscore(UUID uuid, String course, long time, long fails) {
		this.course = course;
		this.fails = fails;
		this.uuid = uuid;
		this.time = time;
	}

	public String getCourseName() {
		return course;
	}

	public UUID getPlayerUUID() {
		return uuid;
	}

	public long getTimeTaken() {
		return time;
	}

	public long getFails() {
		return fails;
	}

}
