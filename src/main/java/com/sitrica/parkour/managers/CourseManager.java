package com.sitrica.parkour.managers;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Table.Cell;
import com.sitrica.core.database.Database;
import com.sitrica.core.database.Serializer;
import com.sitrica.core.manager.Manager;
import com.sitrica.core.utils.IntervalUtils;
import com.sitrica.parkour.ProParkour;
import com.sitrica.parkour.objects.Capture;
import com.sitrica.parkour.objects.Course;
import com.sitrica.parkour.objects.Highscore;
import com.sitrica.parkour.objects.ParkourPlayer;
import com.sitrica.parkour.serializers.CaptureSerializer;
import com.sitrica.parkour.serializers.CourseSerializer;
import com.sitrica.parkour.serializers.HighscoreSerializer;
import com.sitrica.parkour.serializers.SignInfoSerializer;
import com.sitrica.parkour.signs.SignInfo;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class CourseManager extends Manager {

	private final Set<Course> courses = new HashSet<>();
	private final Database<Course> database;

	public CourseManager() throws IllegalAccessException {
		super(false);
		ProParkour instance = ProParkour.getInstance();
		FileConfiguration configuration = instance.getConfig();
		Map<Type, Serializer<?>> serializers = ImmutableMap.of(Course.class, new CourseSerializer(), SignInfo.class,
				new SignInfoSerializer(), Highscore.class, new HighscoreSerializer(), Capture.class, new CaptureSerializer());
		database = getNewDatabase(instance, "courses", Course.class, serializers);
		String interval = configuration.getString("database.autosave", "5 miniutes");
		database.getKeys().forEach(name -> {
			Course course = database.get(name);
			if (course != null)
				courses.add(course);
		});
		Bukkit.getScheduler().runTaskTimerAsynchronously(instance, () -> {
			courses.forEach(course -> database.put(course.getName(), course));
		}, 0, IntervalUtils.getInterval(interval));
		// Handle all courses
		Bukkit.getScheduler().runTaskTimerAsynchronously(instance, () -> {
			for (Course course : courses) {
				if (course == null || course.getPlayersPlaying().isEmpty())
					continue;
				for (Iterator<Cell<ParkourPlayer, Long, Capture>> iterator = course.getPlayersPlaying().cellSet().iterator(); iterator.hasNext();) {
					Cell<ParkourPlayer, Long, Capture> cell = iterator.next();
					double seconds = (System.currentTimeMillis() - cell.getColumnKey()) / 1000;
					ParkourPlayer parkourPlayer = cell.getRowKey();
					// TODO Actionbar with Spigot support, this below is for testing
					parkourPlayer.getPlayer().ifPresent(player -> {
						player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("Seconds: " + seconds));
						if (player.getLocation().distance(course.getEndingLocation()) < 2) {
							course.complete(parkourPlayer);
							iterator.remove();
						}
					});
				}
			}
		}, 0, 3);
	}

	public Optional<Course> getCourse(String name) {
		return courses.stream()
				.filter(course -> course.getName().equalsIgnoreCase(name))
				.findFirst();
	}

	public void addCourse(Course course) {
		courses.add(course);
		database.put(course.getName(), course);
	}

	public Set<Course> getCourses() {
		return courses;
	}

}
