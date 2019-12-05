package com.sitrica.parkour.objects;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;

import com.google.common.collect.Lists;
import com.sitrica.parkour.signs.SignInfo;

public class Course {

	private final transient Set<ParkourPlayer> playing = new HashSet<>();
	private final Set<Highscore> highscores = new HashSet<>();
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
		playing.remove(player);
	}

	public void addPlayer(ParkourPlayer player) {
		playing.add(player);
	}

	public Set<ParkourPlayer> getPlayersPlaying() {
		return playing;
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

}
