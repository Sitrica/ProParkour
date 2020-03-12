package com.sitrica.parkour.objects;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;
import com.sitrica.parkour.ProParkour;
import com.sitrica.parkour.signs.SignInfo;

public class Course {

	// The long is their starting time.
	private final transient Table<ParkourPlayer, Long, Capture> playing = HashBasedTable.create();
	private final Map<UUID, Location> fromLocations = new HashMap<>();
	private final Set<Highscore> highscores = new HashSet<>();
	private final Map<UUID, Integer> fails = new HashMap<>();
	private final Set<SignInfo> signs = new HashSet<>();
	private final Location start, end;
	private final String name;

	public Course(String name, Location start, Location end) {
		this.start = start;
		this.name = name;
		this.end = end;
	}

	public Location getStartingLocation() {
		return start;
	}

	public Location getEndingLocation() {
		return end;
	}

	public void removePlayer(ParkourPlayer player) {
		playing.cellSet().removeIf(cell -> cell.getRowKey().equals(player));
	}

	public void addPlayer(ParkourPlayer player) {
		Optional<Player> bukkit = player.getPlayer();
		if (!bukkit.isPresent())
			return;
		Capture capture = new Capture(player);
		capture.start();
		fromLocations.put(player.getUniqueId(), bukkit.get().getLocation());
		playing.put(player, System.currentTimeMillis(), capture);
		player.getPlayer().get().teleport(start);
	}

	public Table<ParkourPlayer, Long, Capture> getPlayersPlaying() {
		return playing;
	}

	public Optional<Long> getStartingTime(ParkourPlayer find) {
		return playing.cellSet().stream()
				.filter(cell -> cell.getRowKey().equals(find))
				.map(cell -> cell.getColumnKey())
				.findFirst();
	}

	public void addHighscore(Highscore highscore) {
		highscores.add(highscore);
	}

	public Set<Highscore> getHighscores() {
		return highscores;
	}

	public void removeSign(SignInfo sign) {
		signs.remove(sign);
	}

	public void addSign(SignInfo sign) {
		signs.add(sign);
	}

	public Set<SignInfo> getSigns() {
		return Collections.unmodifiableSet(signs);
	}

	public String getName() {
		return name;
	}

	public List<Highscore> getSorted(Comparator<? super Highscore> comparator) {
		List<Highscore> list = Lists.newArrayList(highscores);
		Collections.sort(list, comparator);
		return list;
	}

	public Optional<Highscore> getFastestHighscore() {
		List<Highscore> highscores = getSorted(Comparator.comparing(Highscore::getTimeTaken));
		if (highscores.isEmpty())
			return Optional.empty();
		return Optional.of(highscores.get(0));
	}

	public void restart(ParkourPlayer player) {
		removePlayer(player);
		addPlayer(player);
	}

	public void addFail(ParkourPlayer player) {
		int fails = this.fails.getOrDefault(player.getUniqueId(), 0);
		fails++;
		this.fails.put(player.getUniqueId(), fails);
		player.getPlayer().ifPresent(p -> p.teleport(end));
	}

	public void complete(ParkourPlayer player) {
		Cell<ParkourPlayer, Long, Capture> value = null;
		for (Cell<ParkourPlayer, Long, Capture> cell : playing.cellSet()) {
			if (cell.getRowKey().equals(player))
				value = cell;
		}
		if (value == null)
			return;
		ParkourPlayer parkourPlayer = value.getRowKey();
		long time = System.currentTimeMillis() - value.getColumnKey();
		int fails = this.fails.getOrDefault(player.getUniqueId(), 0);
		Highscore highscore = new Highscore(parkourPlayer.getUniqueId(), name, time, fails);
		getFastestHighscore().ifPresent(fastest -> {
			if (highscore.getTimeTaken() < fastest.getTimeTaken()) {
				// TODO message that they now have the fastest highscore.
				player.getPlayer().ifPresent(p -> p.sendMessage("Wow you got fastest time."));
			}
		});
		highscores.add(highscore);
		Capture capture = value.getValue();
		capture.stop();
		highscore.setCapture(capture);
		player.getPlayer().ifPresent(p -> {
			Bukkit.getScheduler().runTask(ProParkour.getInstance(), () -> {
				p.teleport(fromLocations.getOrDefault(p.getUniqueId(), p.getWorld().getSpawnLocation()));
				p.sendMessage("Ur time was " + highscore.getTimeTaken());
			});
			fromLocations.remove(p.getUniqueId());
		});

		// TODO message their time and maybe other times???
		//Also maybe make it before the highscore check above.
	}

}
