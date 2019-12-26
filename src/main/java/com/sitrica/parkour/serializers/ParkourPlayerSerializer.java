package com.sitrica.parkour.serializers;

import java.lang.reflect.Type;
import java.util.UUID;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.sitrica.core.database.Serializer;
import com.sitrica.parkour.objects.ParkourPlayer;

public class ParkourPlayerSerializer implements Serializer<ParkourPlayer> {

	@Override
	public JsonElement serialize(ParkourPlayer player, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject json = new JsonObject();
		if (player == null)
			return json;
		json.addProperty("experience", player.getExperience());
		json.addProperty("uuid", player.getUniqueId() + "");
		return json;
	}

	@Override
	public ParkourPlayer deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
		JsonObject object = json.getAsJsonObject();
		String stringUUID = object.get("uuid").getAsString();
		if (stringUUID == null)
			return null;
		UUID uuid = UUID.fromString(stringUUID);
		if (uuid == null)
			return null;
		ParkourPlayer player = new ParkourPlayer(uuid);
		JsonElement levelElement = object.get("experience");
		if (levelElement != null)
			player.setExperience(levelElement.getAsLong());
		return player;
	}

}
