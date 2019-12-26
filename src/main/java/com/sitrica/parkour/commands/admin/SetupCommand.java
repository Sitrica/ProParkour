package com.sitrica.parkour.commands.admin;

import java.util.Optional;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sitrica.core.SourPlugin;
import com.sitrica.core.command.AdminCommand;
import com.sitrica.core.messaging.ListMessageBuilder;
import com.sitrica.core.messaging.MessageBuilder;
import com.sitrica.parkour.managers.SetupManager;
import com.sitrica.parkour.managers.SetupManager.Setup;

public class SetupCommand extends AdminCommand {

	public SetupCommand(SourPlugin instance) {
		super(instance, false, "setup", "new", "create");
	}

	@Override
	protected ReturnType runCommand(String command, CommandSender sender, String... arguments) {
		Player player = (Player) sender;
		SetupManager manager = instance.getManager(SetupManager.class);
		Optional<Setup> optional = manager.getSetup(player);
		if (!optional.isPresent()) {
			if (arguments.length != 0) {
				new MessageBuilder(instance, "setup.not-in-setup")
						.setPlaceholderObject(player)
						.send(player);
				return ReturnType.FAILURE;
			}
			manager.enterSetup(player);
			return ReturnType.SUCCESS;
		}
		Setup setup = optional.get();
		switch (arguments[0].toLowerCase()) {
			case "start":
				setup.setStarting(player.getLocation().add(0, 0.4, 0));
				new MessageBuilder(instance, "setup.start")
						.setPlaceholderObject(setup)
						.send(player);
				new ListMessageBuilder(instance, "setup.3")
						.setPlaceholderObject(setup)
						.send(player);
				break;
			case "end":
				setup.setEnding(player.getLocation().add(0, 0.4, 0));
				new MessageBuilder(instance, "setup.end")
						.setPlaceholderObject(setup)
						.send(player);
				new ListMessageBuilder(instance, "setup.4")
						.setPlaceholderObject(setup)
						.send(player);
				break;
			case "finish":
				if (!setup.isComplete()) {
					new MessageBuilder(instance, "setup.setup-not-completed")
							.setPlaceholderObject(setup)
							.send(player);
					return ReturnType.FAILURE;
				}
				manager.finish(setup);
				break;
			case "quit":
				new MessageBuilder(instance, "setup.quit")
						.setPlaceholderObject(setup)
						.send(player);
				manager.quit(player);
				break;
			default:
				return ReturnType.SYNTAX_ERROR;
		}
		return ReturnType.SUCCESS;
	}

	@Override
	public String getConfigurationNode() {
		return "setup";
	}

	@Override
	public String[] getPermissionNodes() {
		return new String[] {"proparkour.setup", "proparkour.admin"};
	}

}
