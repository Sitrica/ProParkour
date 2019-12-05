package com.sitrica.parkour.managers;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import com.google.common.collect.ImmutableMap;
import com.sitrica.core.database.Database;
import com.sitrica.core.database.Serializer;
import com.sitrica.core.manager.Manager;
import com.sitrica.core.utils.IntervalUtils;
import com.sitrica.parkour.ProParkour;
import com.sitrica.parkour.objects.Course;
import com.sitrica.parkour.serializers.CourseSerializer;
import com.sitrica.parkour.serializers.SignInfoSerializer;
import com.sitrica.parkour.signs.SignInfo;

public class CourseManager extends Manager {

	private final Set<Course> courses = new HashSet<>();
	private final Database<Course> database;

	public CourseManager() throws IllegalAccessException {
		super(false);
		ProParkour instance = ProParkour.getInstance();
		FileConfiguration configuration = instance.getConfig();
		Map<Type, Serializer<?>> serializers = ImmutableMap.of(Course.class, new CourseSerializer(), SignInfo.class, new SignInfoSerializer());
		database = getNewDatabase(instance, "course-table", Course.class, serializers);
		String interval = configuration.getString("database.autosave", "5 miniutes");
		database.getKeys().forEach(name -> courses.add(database.get(name)));
		Bukkit.getScheduler().runTaskTimerAsynchronously(instance, () -> courses.forEach(course -> database.put(course.getName() + "", course)), 0, IntervalUtils.getInterval(interval));
	}

	public Optional<Course> getCourse(String name) {
		return courses.stream()
				.filter(course -> course.getName().equalsIgnoreCase(name))
				.findFirst();
	}

	public Set<Course> getCourses() {
		return courses;
	}

}
