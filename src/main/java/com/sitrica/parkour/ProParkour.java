package com.sitrica.parkour;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.sitrica.core.SourPlugin;
import com.sitrica.core.command.CommandHandler;
import com.sitrica.core.manager.ExternalManager;
import com.sitrica.core.manager.Manager;
import com.sitrica.core.manager.ManagerHandler;

import fr.minuskube.inv.InventoryManager;

public class ProParkour extends SourPlugin {

	private final Map<String, FileConfiguration> configurations = new HashMap<>();
	private final String packageName = "com.sitrica.parkour";
	private static InventoryManager inventoryManager;
	private CommandHandler commandHandler;
	private ManagerHandler managerHandler;
	private ProParkourAPI API;
	private static ProParkour instance;

	public ProParkour() {
		super("&7[&dProParkour&7]&r");
	}

	@Override
	public void onEnable() {
		instance = this;
		File configFile = new File(getDataFolder(), "config.yml");
		//If newer version was found, update configuration.
		if (!getDescription().getVersion().equals(getConfig().getString("version", getDescription().getVersion()))) {
			if (configFile.exists())
				configFile.delete();
		}
		//Create all the default files.
		for (String name : Arrays.asList("config", "messages", "sounds", "inventories")) {
			File file = new File(getDataFolder(), name + ".yml");
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				saveResource(file.getName(), false);
				debugMessage("Created new default file " + file.getName());
			}
			FileConfiguration configuration = new YamlConfiguration();
			try {
				configuration.load(file);
				configurations.put(name, configuration);
			} catch (IOException | InvalidConfigurationException e) {
				e.printStackTrace();
			}
		}
		managerHandler = new ManagerHandler(this);
		commandHandler = new CommandHandler(this, "proparkour", packageName + ".commands");
		inventoryManager = new InventoryManager(this);
		inventoryManager.init();
		API = new ProParkourAPI(this);
		consoleMessage(getPrefix() + "has been enabled!");
	}

	public <T extends ExternalManager> T getExternalManager(Class<T> expected) {
		return managerHandler.getExternalManager(expected);
	}

	public static InventoryManager getInventoryManager() {
		return inventoryManager;
	}

	/**
	 * Grab a FileConfiguration if found.
	 * Call it without it's file extension, just the simple name of the file.
	 * 
	 * @param configuration The name of the configuration to search for.
	 * @return Optional<FileConfiguration> as the file may or may not exist.
	 */
	@Override
	public Optional<FileConfiguration> getConfiguration(String configuration) {
		return Optional.ofNullable(configurations.get(configuration));
	}

	/**
	 * Grab a Manager by it's class and create it if not present.
	 * 
	 * @param <T> <T extends Manager>
	 * @param expected The expected Class that extends Manager.
	 * @return The Manager that matches the defined class.
	 */
	@Override
	public <T extends Manager> T getManager(Class<T> expected) {
		return managerHandler.getManager(expected);
	}

	/**
	 * @return The CommandManager allocated to the plugin.
	 */
	@Override
	public CommandHandler getCommandHandler() {
		return commandHandler;
	}

	public ManagerHandler getManagerHandler() {
		return managerHandler;
	}

	public static ProParkour getInstance() {
		return instance;
	}

	public List<Manager> getManagers() {
		return managerHandler.getManagers();
	}

	public String getPackageName() {
		return packageName;
	}

	public ProParkourAPI getAPI() {
		return API;
	}

}
