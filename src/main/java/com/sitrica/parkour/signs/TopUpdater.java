package com.sitrica.parkour.signs;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.block.Sign;

import com.sitrica.parkour.objects.Course;
import com.sitrica.parkour.objects.Highscore;
import com.sitrica.parkour.objects.ParkourPlayer;

public class TopUpdater extends SignUpdater {

	public TopUpdater() {
		super("top");
	}

	@Override
	public void update(SignInfo sign, Course course) {
		Optional<Sign> optional = sign.getSign();
		if (!optional.isPresent())
			return;
		Sign block = optional.get();
		List<Highscore> highscores = course.getSorted(Comparator.comparingLong(Highscore::getTimeTaken));
		block.setLine(0, "Course: " + sign.getCourseName());
		block.setLine(1, "#1 " + getHighscore(highscores.get(0)));
		block.setLine(2, "#2 " + getHighscore(highscores.get(1)));
		block.setLine(3, "#3 " + getHighscore(highscores.get(2)));
	}

	private String getHighscore(Highscore highscore) {
		if (highscore == null)
			return "Not set";
		return Bukkit.getOfflinePlayer(highscore.getPlayerUUID()).getName();
	}

	@Override
	public void onLeftClick(ParkourPlayer player, SignInfo sign, Course course) {
		// TODO
	}

	@Override
	public void onRightClick(ParkourPlayer player, SignInfo sign, Course course) {
		// TODO
	}

}
