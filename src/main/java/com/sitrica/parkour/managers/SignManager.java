package com.sitrica.parkour.managers;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.sitrica.core.manager.Manager;
import com.sitrica.core.messaging.MessageBuilder;
import com.sitrica.parkour.ProParkour;
import com.sitrica.parkour.objects.Course;
import com.sitrica.parkour.objects.ParkourPlayer;
import com.sitrica.parkour.signs.GeneralUpdater;
import com.sitrica.parkour.signs.SignInfo;
import com.sitrica.parkour.signs.SignUpdater;
import com.sitrica.parkour.signs.TopUpdater;

public class SignManager extends Manager {

	private final Set<SignUpdater> updaters = new HashSet<>();

	public SignManager() throws IllegalAccessException {
		super(true);
		updaters.add(new GeneralUpdater());
		updaters.add(new TopUpdater());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
	public void onSignClick(PlayerInteractEvent event) {
		Block block = event.getClickedBlock();
		if (block == null)
			return;
		Optional<SignInfo> optional = getAllSigns().stream()
				.filter(sign -> sign.getLocation().equals(block.getLocation()))
				.findFirst();
		if (!optional.isPresent())
			return;
		SignInfo sign = optional.get();
		ProParkour instance = ProParkour.getInstance();
		Optional<Course> course = instance.getManager(CourseManager.class).getCourse(sign.getCourseName());
		if (!course.isPresent())
			return;
		ParkourPlayer parkourPlayer = instance.getManager(PlayerManager.class).getParkourPlayer(event.getPlayer());
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
			getSignUpdater(sign).orElse(new GeneralUpdater()).onRightClick(parkourPlayer, sign, course.get());
		else if (event.getAction() == Action.LEFT_CLICK_BLOCK)
			getSignUpdater(sign).orElse(new GeneralUpdater()).onLeftClick(parkourPlayer, sign, course.get());
	}

	@EventHandler(ignoreCancelled = true)
	public void onSignChange(SignChangeEvent event) {
		String[] lines = event.getLines();
		if (!lines[0].equalsIgnoreCase("[proparkour]"))
			return;
		Player player = event.getPlayer();
		if (!player.hasPermission("proparkour.signs.create"))
			return;
		ProParkour instance = ProParkour.getInstance();
		String type = lines[1];
		Optional<? extends SignUpdater> updaterOptional = updaters.stream()
				.filter(updater -> updater.getName().equalsIgnoreCase(type))
				.findFirst();
		if (!updaterOptional.isPresent()) {
			event.getBlock().breakNaturally();
			new MessageBuilder(instance, "setup.sign.no-type")
					.replace("%types%", updaters, updater -> updater.getName())
					.replace("%type%", type)
					.send(player);
			return;
		}
		SignUpdater updater = updaterOptional.get();
		String name = lines[2];
		Optional<Course> optional = instance.getManager(CourseManager.class).getCourse(name);
		if (!optional.isPresent()) {
			event.getBlock().breakNaturally();
			new MessageBuilder(instance, "setup.sign.no-course")
					.replace("%name%", name)
					.send(player);
			return;
		}
		Course course = optional.get();
		SignInfo sign = new SignInfo(event.getBlock().getLocation(), course.getName(), type);
		course.addSign(sign);
		updater.update(sign, course);
		new MessageBuilder(instance, "setup.sign.created")
				.replace("%type%", type)
				.replace("%name%", name)
				.send(player);
	}

	public Set<SignInfo> getAllSigns() {
		return ProParkour.getInstance().getManager(CourseManager.class).getCourses().stream()
				.flatMap(course -> course.getSigns().stream())
				.collect(Collectors.toSet());
	}

	public Set<? extends SignUpdater> getSignUpdaters() {
		return Collections.unmodifiableSet(updaters);
	}

	public boolean registerSignUpdater(SignUpdater updater) {
		if (updaters.stream().anyMatch(existing -> existing.getName().equalsIgnoreCase(updater.getName())))
			return false;
		return updaters.add(updater);
	}

	public Optional<SignUpdater> getSignUpdater(SignInfo sign) {
		return updaters.stream()
				.filter(updater -> updater.getName().equalsIgnoreCase(sign.getType()))
				.findFirst();
	}

	public void update(SignInfo sign) {
		getSignUpdater(sign).ifPresent(updater ->  {
			Optional<Course> course = ProParkour.getInstance().getManager(CourseManager.class).getCourse(sign.getCourseName());
			if (!course.isPresent())
				return;
			updater.update(sign, course.get());
		});
	}

}
