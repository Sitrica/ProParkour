package com.sitrica.parkour.serializers;

import java.lang.reflect.Type;

import org.bukkit.Location;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.sitrica.core.database.Serializer;
import com.sitrica.parkour.signs.SignInfo;

public class SignInfoSerializer implements Serializer<SignInfo> {

	@Override
	public JsonElement serialize(SignInfo sign, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject json = new JsonObject();
		if (sign == null)
			return json;
		// Location serializer should always be present assuming the resource is using the Sitrica Core.
		json.add("location", context.serialize(sign.getLocation(), Location.class));
		json.addProperty("course", sign.getCourseName());
		json.addProperty("type", sign.getType());
		return json;
	}

	@Override
	public SignInfo deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		JsonObject object = json.getAsJsonObject();
		JsonElement locationElement = object.get("location");
		if (locationElement == null)
			return null;
		Location location = context.deserialize(locationElement, Location.class);
		if (location == null)
			return null;
		String course = object.get("course").getAsString();
		String type = object.get("type").getAsString();
		return new SignInfo(location, course, type);
	}

}
