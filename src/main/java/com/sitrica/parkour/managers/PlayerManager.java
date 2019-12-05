package com.sitrica.parkour.managers;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.sitrica.core.database.Database;
import com.sitrica.core.manager.Manager;
import com.sitrica.core.utils.IntervalUtils;
import com.sitrica.parkour.ProParkour;
import com.sitrica.parkour.objects.ParkourPlayer;

public class PlayerManager extends Manager {

	private final Set<ParkourPlayer> players = new HashSet<>();
	private final Database<ParkourPlayer> database;

	public PlayerManager() throws IllegalAccessException {
		super(true);
		ProParkour instance = ProParkour.getInstance();
		FileConfiguration configuration = instance.getConfig();
		database = getNewDatabase(instance, "player-table", ParkourPlayer.class);
		String interval = configuration.getString("database.autosave", "5 miniutes");
		Bukkit.getScheduler().runTaskTimerAsynchronously(instance, () -> players.forEach(player -> database.put(player.getUniqueId() + "", player)), 0, IntervalUtils.getInterval(interval));
	}

	public ParkourPlayer getParkourPlayer(Player player) {
		return getParkourPlayer(player.getUniqueId()).orElseGet(() -> {
					ParkourPlayer p = new ParkourPlayer(player.getUniqueId());
					players.add(p);
					return p;
				});
	}

	public Optional<ParkourPlayer> getParkourPlayer(OfflinePlayer player) {
		return getParkourPlayer(player.getUniqueId());
	}

	public Optional<ParkourPlayer> getParkourPlayer(UUID uuid) {
		return Optional.ofNullable(players.parallelStream()
				.filter(p -> p.getUniqueId().equals(uuid))
				.findFirst()
				.orElseGet(() -> {
					ParkourPlayer player = database.get(uuid + "");
					if (player != null)
						players.add(player);
					return player;
				}));
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		ParkourPlayer player = getParkourPlayer(event.getPlayer());
		ProParkour.getInstance().debugMessage("Loaded player " + player.getUniqueId());
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		ParkourPlayer gamePlayer = getParkourPlayer(player);
		database.put(player.getUniqueId() + "", gamePlayer);
		Bukkit.getScheduler().runTaskLaterAsynchronously(ProParkour.getInstance(), () -> players.removeIf(p -> p.getUniqueId().equals(player.getUniqueId())), 1);
	}

}
