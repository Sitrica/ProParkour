package com.sitrica.parkour.signs;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;
import com.sitrica.core.messaging.Formatting;
import com.sitrica.core.messaging.MessageBuilder;
import com.sitrica.core.sounds.SoundPlayer;
import com.sitrica.parkour.ProParkour;
import com.sitrica.parkour.objects.Course;
import com.sitrica.parkour.objects.ParkourPlayer;

public class GeneralUpdater extends SignUpdater {

	private static final String prefix = new MessageBuilder(ProParkour.getInstance(), "prefix").get();
	private final transient Map<ParkourPlayer, SignInfo> selecting = new ConcurrentHashMap<>();
	private final transient Map<UUID, Selection> selection = new HashMap<>();

	public GeneralUpdater() {
		super("general");
		Bukkit.getScheduler().runTaskTimer(ProParkour.getInstance(), () -> {
			for (Entry<ParkourPlayer, SignInfo> entry : selecting.entrySet()) {
				Optional<Player> optional = entry.getKey().getPlayer();
				if (!optional.isPresent()) {
					selecting.remove(entry.getKey());
					continue;
				}
				Player player = optional.get();
				if (player.getLocation().distance(entry.getValue().getLocation()) > 6)
					selecting.remove(entry.getKey());
			}
		}, 1, 20);
	}

	@Override
	public void update(SignInfo sign, Course course) {
		if (selecting.containsValue(sign))
			return;
		Optional<Sign> optional = sign.getSign();
		if (!optional.isPresent())
			return;
		Sign block = optional.get();
		block.setLine(0, prefix);
		block.setLine(1, "Course: " + sign.getCourseName());
		block.setLine(2, "Players " + course.getPlayersPlaying().size());
		block.setLine(3, "Rightclick for menu");
	}

	@Override
	public void onLeftClick(ParkourPlayer player, SignInfo sign, Course course) {
		// TODO
	}

	private enum Selection {

		PLAY("&aPLAY"), LEADERBOARD("&eLEADERBOARD"), GHOSTS("&4GHOSTS"), SETTINGS("&0SETTINGS");

		private final String tag;
		
		Selection(String tag) {
			this.tag = tag;
		}

		public String getTag(boolean selection) {
			if (selection)
				return Formatting.color("&0&l> &l" + tag + " &0&l<");
			return Formatting.color(tag);
		}

	}

	// Display a client side menu on the sign.

	@Override
	public void onRightClick(ParkourPlayer player, SignInfo sign, Course course) {
		// Setup and memory
		List<Selection> selections = Lists.newArrayList(Selection.values());
		selecting.put(player, sign);

		// Find which Selection the user is on.
		Selection selected = Selection.PLAY;
		if (!selection.containsKey(player.getUniqueId()))
			selection.put(player.getUniqueId(), Selection.PLAY);
		else {
			selected = selection.get(player.getUniqueId());
		}

		// Format the menu lines based on their selection.
		int start = selections.indexOf(selected);
		List<String> lines = Lists.newArrayList(selections.get(start).getTag(true));
		for (int i = 0; i < selections.size(); i++) {
			start++;
			if (start >= selections.size())
				start = 0;
			lines.add(selections.get(start).getTag(false));
		}
		Collections.reverse(lines);

		// Setup the player's next selection
		int index = selections.indexOf(selected) + 1;
		if (index >= selections.size())
			index = 0;
		selection.put(player.getUniqueId(), selections.get(index));

		// Display the menu and effects.
		lines.add(0, prefix);
		player.getPlayer().ifPresent(bukkit -> {
			new SoundPlayer(ProParkour.getInstance(), "click").playTo(bukkit);
			bukkit.sendSignChange(sign.getLocation(), lines.stream()
					.limit(4)
					.toArray(String[]::new));
		});
	}

}
