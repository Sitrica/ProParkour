package com.sitrica.parkour.objects;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.sitrica.parkour.ProParkour;

public class Capture {

	private final Map<Location, Long> locations = new HashMap<>();
	private final ParkourPlayer player;
	private int task;

	public Capture(ParkourPlayer player, Map<Location, Long> existing) {
		locations.putAll(existing);
		this.player = player;
	}

	public Capture(ParkourPlayer player) {
		this.player = player;
	}

	public ParkourPlayer getPlayer() {
		return player;
	}

	public void start() {
		task = Bukkit.getScheduler().runTaskTimerAsynchronously(ProParkour.getInstance(), () -> {
			//TODO do better with packets.
			player.getPlayer().ifPresent(bukkit -> locations.put(bukkit.getLocation(), System.currentTimeMillis()));
		}, 0, 1).getTaskId();
	}

	public void stop() {
		Bukkit.getScheduler().cancelTask(task);
	}

	public Map<Location, Long> getSavedLocations() {
		return Collections.unmodifiableMap(locations);
	}

}
