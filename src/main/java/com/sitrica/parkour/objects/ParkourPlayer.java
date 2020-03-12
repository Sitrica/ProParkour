package com.sitrica.parkour.objects;

import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class ParkourPlayer {

	// Used internally for course manager data.
	private transient Course course;

	private ItemStack[] inventory;
	private Location location;
	private final UUID uuid;
	private long experience;

	public ParkourPlayer(UUID uuid) {
		this.uuid = uuid;
	}

	public void saveInventory() {
		getPlayer().ifPresent(player -> inventory = player.getInventory().getContents());
	}

	public void loadInventory() {
		getPlayer().ifPresent(player -> {
			PlayerInventory playerInventory = player.getInventory();
			playerInventory.clear();
			playerInventory.setContents(inventory);
		});
	}

	public Optional<Course> getCurrentCourse() {
		return Optional.ofNullable(course);
	}

	public void setCurrentCourse(Course course) {
		this.course = course;
	}

	public long getExperience() {
		return experience;
	}

	public void setExperience(long experience) {
		this.experience = experience;
	}

	public void addExperience(long experience) {
		this.experience = this.experience + experience;
	}

	public Location getJoinLocation() {
		return location;
	}

	public void setJoinLocation(Location location) {
		this.location = location;
	}

	public UUID getUniqueId() {
		return uuid;
	}

	public Optional<Player> getPlayer() {
		return Optional.ofNullable(Bukkit.getPlayer(uuid));
	}

	public boolean isOnline() {
		return getPlayer().isPresent();
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof ParkourPlayer))
			return false;
		ParkourPlayer other = (ParkourPlayer) object;
		if (!other.getUniqueId().equals(uuid))
			return false;
		return true;
	}

}
