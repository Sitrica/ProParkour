package com.sitrica.parkour.commands;

import java.util.Arrays;

import org.bukkit.command.CommandSender;

import com.sitrica.core.SourPlugin;
import com.sitrica.core.command.AbstractCommand;
import com.sitrica.core.command.AdminCommand;
import com.sitrica.core.messaging.Formatting;
import com.sitrica.core.messaging.MessageBuilder;

public class ProParkourCommand extends AbstractCommand {

	public ProParkourCommand(SourPlugin instance) {
		super(instance, true, "pp", "prop", "proparkour", "parkour");
	}

	@Override
	protected ReturnType runCommand(String input, CommandSender sender, String... args) {
		sender.sendMessage("");
		new MessageBuilder(instance, "messages.version")
				.replace("%version%", instance.getDescription().getVersion())
				.send(sender);
		for (AbstractCommand command : instance.getCommandHandler().getCommands()) {
			if (command instanceof AdminCommand)
				continue;
			if (command.getConfigurationNode() == null)
				continue;
			if (command.getPermissionNodes() == null || Arrays.stream(command.getPermissionNodes()).parallel().anyMatch(permission -> sender.hasPermission(permission))) {
				sender.sendMessage(Formatting.color("&8 - &6" + command.getSyntax(sender) + "&7 - " + command.getDescription(sender)));
			}
		}
		sender.sendMessage("");
		return ReturnType.SUCCESS;
	}

	@Override
	public String[] getPermissionNodes() {
		return null;
	}

	@Override
	public String getConfigurationNode() {
		return "proparkour";
	}

}
