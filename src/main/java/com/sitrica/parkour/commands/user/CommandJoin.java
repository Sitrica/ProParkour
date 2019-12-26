package com.sitrica.parkour.commands.user;

import java.util.Optional;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sitrica.core.SourPlugin;
import com.sitrica.core.command.AbstractCommand;
import com.sitrica.core.messaging.MessageBuilder;
import com.sitrica.parkour.managers.CourseManager;
import com.sitrica.parkour.managers.PlayerManager;
import com.sitrica.parkour.objects.Course;
import com.sitrica.parkour.objects.ParkourPlayer;

public class CommandJoin extends AbstractCommand {

	public CommandJoin(SourPlugin instance) {
		super(instance, false, "join", "j");
	}

	@Override
	protected ReturnType runCommand(String command, CommandSender sender, String... arguments) {
		if (arguments.length <= 0)
			return ReturnType.SYNTAX_ERROR;
		Player player = (Player) sender;
		ParkourPlayer parkourPlayer = instance.getManager(PlayerManager.class).getParkourPlayer(player);
		if (parkourPlayer.getCurrentCourse().isPresent()) {
			new MessageBuilder(instance, "commands.join.already-on-course")
					.setPlaceholderObject(parkourPlayer)
					.replace("%input%", arguments[0])
					.send(player);
			return ReturnType.FAILURE;
		}
		CourseManager courseManager = instance.getManager(CourseManager.class);
		Optional<Course> course = courseManager.getCourse(arguments[0]);
		if (!course.isPresent()) {
			new MessageBuilder(instance, "commands.join.not-a-course")
					.setPlaceholderObject(parkourPlayer)
					.replace("%input%", arguments[0])
					.send(player);
			return ReturnType.FAILURE;
		}
		course.get().addPlayer(parkourPlayer);
		return ReturnType.SUCCESS;
	}

	@Override
	public String getConfigurationNode() {
		return "join";
	}

	@Override
	public String[] getPermissionNodes() {
		return new String[] {"proparkour.join", "proparkour.admin"};
	}

}
