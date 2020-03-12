package com.sitrica.parkour.objects;

import java.util.UUID;

import com.google.common.base.Optional;

public class Highscore {

	private final long time, fails;
	private final String course;
	private final UUID uuid;
	private Capture capture;

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

	public void setCapture(Capture capture) {
		this.capture = capture;
	}

	/**
	 * Returns the Capture in an optional, it's an optional because the player may not
	 * want their ghost to be captured, so this is optional.
	 * 
	 * @return Optional<Capture> if the player has a capture.
	 */
	public Optional<Capture> getCapture() {
		return Optional.of(capture);
	}

	public boolean hasCapture() {
		return capture != null;
	}

	public long getTimeTaken() {
		return time;
	}

	public long getFails() {
		return fails;
	}

}
