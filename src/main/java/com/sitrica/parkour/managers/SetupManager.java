package com.sitrica.parkour.managers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import com.sitrica.core.items.ItemStackBuilder;
import com.sitrica.core.manager.Manager;
import com.sitrica.core.messaging.ListMessageBuilder;
import com.sitrica.core.messaging.MessageBuilder;
import com.sitrica.core.sounds.SoundPlayer;
import com.sitrica.parkour.ProParkour;
import com.sitrica.parkour.inventories.AnvilMenu;
import com.sitrica.parkour.objects.Course;

public class SetupManager extends Manager {

	private final Set<Setup> setups = new HashSet<>();

	public SetupManager() {
		super(true);
	}

	public Optional<Setup> getSetup(Player player) {
		return setups.parallelStream()
				.filter(setup -> setup.player.equals(player))
				.findFirst();
	}

	public void finish(Setup setup) {
		Course course = new Course(setup.getName(), setup.getLocation("starting").get(), setup.getLocation("ending").get());
		ProParkour instance = ProParkour.getInstance();
		instance.getManager(CourseManager.class).addCourse(course);
		setups.remove(setup);
		new ListMessageBuilder(instance, "setup.complete")
				.setPlaceholderObject(setup)
				.send(setup.getPlayer());
	}

	public Setup enterSetup(Player player) {
		ProParkour instance = ProParkour.getInstance();
		new ListMessageBuilder(instance, false, "setup.1")
				.setPlaceholderObject(player)
				.send(player);
		Setup setup = new Setup(player);
		FileConfiguration inventories = instance.getConfiguration("inventories").get();
		ItemStack search = new ItemStackBuilder(instance, inventories.getConfigurationSection("inventories.setup.name-anvil"))
				.setPlaceholderObject(player)
				.build();
		new AnvilMenu(search, player, name -> {
			if (name.contains(" ")) {
				new MessageBuilder(instance, false, "setup.no-spaces")
						.setPlaceholderObject(player)
						.replace("%name%", name)
						.send(player);
				new SoundPlayer(instance, "error").playTo(player);
				return;
			}
			if (ProParkour.getInstance().getManager(CourseManager.class).getCourse(name).isPresent()) {
				new MessageBuilder(instance, "setup.already-exists")
						.setPlaceholderObject(player)
						.replace("%name%", name)
						.send(player);
				new SoundPlayer(instance, "error").playTo(player);
				return;
			}
			setups.add(setup);
			setup.setName(name);
			new ListMessageBuilder(instance, false, "setup.2")
					.setPlaceholderObject(player)
					.replace("%name%", name)
					.send(player);
		});
		return setup;
	}

	public void quit(Player player) {
		getSetup(player).ifPresent(setup -> setups.remove(setup));
		player.closeInventory();
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		quit(event.getPlayer());
	}

	public class Setup {

		private final Map<String, Location> locations = new HashMap<>();
		private final Player player;
		private String name;

		public Setup(Player player) {
			this.player = player;
		}

		public String getName() {
			return name;
		}

		public Player getPlayer() {
			return player;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Optional<Location> getLocation(String key) {
			return Optional.ofNullable(locations.get(key));
		}

		public void setStarting(Location starting) {
			locations.put("starting", starting);
		}

		public void setEnding(Location ending) {
			locations.put("ending", ending);
		}

		public boolean isComplete() {
			return !locations.isEmpty() && name != null;
		}

	}

}
