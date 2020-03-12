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

import com.google.common.collect.ImmutableMap;
import com.sitrica.core.database.Database;
import com.sitrica.core.manager.Manager;
import com.sitrica.core.messaging.MessageBuilder;
import com.sitrica.core.placeholders.Placeholder;
import com.sitrica.core.placeholders.Placeholders;
import com.sitrica.core.utils.IntervalUtils;
import com.sitrica.parkour.ProParkour;
import com.sitrica.parkour.objects.Course;
import com.sitrica.parkour.objects.ParkourPlayer;
import com.sitrica.parkour.serializers.ParkourPlayerSerializer;

public class PlayerManager extends Manager {

	private final Set<ParkourPlayer> players = new HashSet<>();
	private final Database<ParkourPlayer> database;

	public PlayerManager() throws IllegalAccessException {
		super(true);
		ProParkour instance = ProParkour.getInstance();
		FileConfiguration configuration = instance.getConfig();
		database = getNewDatabase(instance, "player-table", ParkourPlayer.class, ImmutableMap.of(ParkourPlayer.class, new ParkourPlayerSerializer()));
		String interval = configuration.getString("database.autosave", "5 miniutes");
		Bukkit.getScheduler().runTaskTimerAsynchronously(instance, () -> players.forEach(player -> database.put(player.getUniqueId() + "", player)), 0, IntervalUtils.getInterval(interval));

		// Default Placeholders
		Placeholders.registerPlaceholder(new Placeholder<ParkourPlayer>("%course%") {
			@Override
			public String replace(ParkourPlayer player) {
				Optional<Course> course = player.getCurrentCourse();
				if (course.isPresent())
					return course.get().getName();
				return new MessageBuilder(instance, "course.not-in-course").get();
			}
		});
	}

	public ParkourPlayer getParkourPlayer(Player player) {
		return getParkourPlayer(player.getUniqueId()).orElseGet(() -> {
					ParkourPlayer p = new ParkourPlayer(player.getUniqueId());
					database.put(player.getUniqueId() + "", p);
					players.add(p);
					return p;
				});
	}

	public Optional<ParkourPlayer> getParkourPlayer(OfflinePlayer player) {
		if (player.isOnline())
			return Optional.of(getParkourPlayer(player.getPlayer()));
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
		ProParkour instance = ProParkour.getInstance();
		ParkourPlayer gamePlayer = getParkourPlayer(player);
		gamePlayer.getCurrentCourse().ifPresent(course -> course.removePlayer(gamePlayer));
		database.put(player.getUniqueId() + "", gamePlayer);
		Bukkit.getScheduler().runTaskLaterAsynchronously(instance, () -> players.removeIf(p -> p.getUniqueId().equals(player.getUniqueId())), 1);
	}

}
