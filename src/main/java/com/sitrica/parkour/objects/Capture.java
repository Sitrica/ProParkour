package com.sitrica.parkour.objects;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.sitrica.parkour.ProParkour;

public class Capture {

	private final List<Location> locations = new ArrayList<>();
	private final ParkourPlayer player;
	private int task;

	public Capture(ParkourPlayer player) {
		this.player = player;
	}

	public void start() {
		task = Bukkit.getScheduler().runTaskTimer(ProParkour.getInstance(), () -> {
			//TODO do better with packets.
			player.getPlayer().ifPresent(bukkit -> locations.add(bukkit.getLocation()));
		}, 0, 20).getTaskId();
	}

	public void stop() {
		Bukkit.getScheduler().cancelTask(task);
	}

}
