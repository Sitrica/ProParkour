package com.sitrica.parkour.serializers;

import java.lang.reflect.Type;
import java.util.UUID;

import com.google.common.base.Optional;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.sitrica.core.database.Serializer;
import com.sitrica.parkour.objects.Capture;
import com.sitrica.parkour.objects.Highscore;

public class HighscoreSerializer implements Serializer<Highscore> {

	@Override
	public JsonElement serialize(Highscore highscore, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject json = new JsonObject();
		if (highscore == null)
			return json;
		json.addProperty("fails", highscore.getFails());
		json.addProperty("time", highscore.getTimeTaken());
		json.addProperty("course", highscore.getCourseName());
		json.addProperty("uuid", highscore.getPlayerUUID() + "");
		Optional<Capture> capture = highscore.getCapture();
		if (capture.isPresent())
			json.add("capture", context.serialize(capture.get(), Capture.class));
		return json;
	}

	@Override
	public Highscore deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
		JsonObject object = json.getAsJsonObject();
		String stringUUID = object.get("uuid").getAsString();
		if (stringUUID == null)
			return null;
		UUID uuid = UUID.fromString(stringUUID);
		if (uuid == null)
			return null;
		JsonElement course = object.get("course");
		if (course == null)
			return null;
		JsonElement time = object.get("time");
		if (time == null)
			return null;
		JsonElement fails = object.get("fails");
		if (fails == null)
			return null;
		Highscore highscore = new Highscore(uuid, course.getAsString(), time.getAsLong(), fails.getAsLong());
		JsonElement capture = object.get("capture");
		if (capture != null)
			highscore.setCapture(context.deserialize(capture, Capture.class));
		return highscore;
	}

}
