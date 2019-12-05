package com.sitrica.parkour.serializers;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.sitrica.core.database.Serializer;
import com.sitrica.parkour.objects.Course;
import com.sitrica.parkour.signs.SignInfo;

public class CourseSerializer implements Serializer<Course> {

	@Override
	public JsonElement serialize(Course course, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject json = new JsonObject();
		if (course == null)
			return json;
		json.add("starting", context.serialize(course.getStartingLocation(), Location.class));
		json.add("ending", context.serialize(course.getEndingLocation(), Location.class));
		json.addProperty("name", course.getName());
		JsonArray array = new JsonArray();
		course.getSigns().forEach(sign -> array.add(context.serialize(sign, SignInfo.class)));
		json.add("signs", array);
		return json;
	}

	@Override
	public Course deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
		JsonObject object = json.getAsJsonObject();
		JsonElement startingElement = object.get("starting");
		if (startingElement == null)
			return null;
		Location starting = context.deserialize(startingElement, Location.class);
		if (starting == null)
			return null;
		JsonElement endingElement = object.get("ending");
		if (endingElement == null)
			return null;
		Location ending = context.deserialize(endingElement, Location.class);
		if (ending == null)
			return null;
		String name = object.get("name").getAsString();
		JsonElement signsElement = object.get("signs");
		List<SignInfo> signs = new ArrayList<>();
		if (signsElement != null && !signsElement.isJsonNull() && signsElement.isJsonArray()) {
			JsonArray array = signsElement.getAsJsonArray();
			array.forEach(element -> {
				SignInfo sign = context.deserialize(element, SignInfo.class);
				if (sign == null)
					return;
				signs.add(sign);
			});
		}
		Course course = new Course(name, starting, ending);
		signs.forEach(sign -> course.addSign(sign));
		return course;
	}

}
