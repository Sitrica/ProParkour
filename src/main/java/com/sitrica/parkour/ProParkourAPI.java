package com.sitrica.parkour;

import java.util.Set;

import com.sitrica.parkour.managers.CourseManager;
import com.sitrica.parkour.managers.SignManager;
import com.sitrica.parkour.objects.Course;
import com.sitrica.parkour.signs.SignUpdater;

public class ProParkourAPI {

	private final CourseManager courses;
	private final SignManager signs;

	public ProParkourAPI(ProParkour instance) {
		courses = instance.getManager(CourseManager.class);
		signs = instance.getManager(SignManager.class);
	}

	/**
	 * Register a sign updater.
	 * 
	 * @param updater The class that extends SignUpdater
	 * @return boolean if successfully registered, false if name is already registered.
	 */
	public <U extends SignUpdater> boolean registerSignUpdater(U updater) {
		return signs.registerSignUpdater(updater);
	}

	/**
	 * @return All the registered SignUpdaters.
	 */
	public Set<? extends SignUpdater> getSignUpdaters() {
		return signs.getSignUpdaters();
	}

	/**
	 * @return All registered Courses.
	 */
	public Set<Course> getCourses() {
		return courses.getCourses();
	}

}
