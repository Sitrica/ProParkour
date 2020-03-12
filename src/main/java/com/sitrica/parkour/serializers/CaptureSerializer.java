package com.sitrica.parkour.serializers;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Location;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.sitrica.core.database.Serializer;
import com.sitrica.parkour.ProParkour;
import com.sitrica.parkour.managers.PlayerManager;
import com.sitrica.parkour.objects.Capture;
import com.sitrica.parkour.objects.ParkourPlayer;

public class CaptureSerializer implements Serializer<Capture> {

	@Override
	public JsonElement serialize(Capture capture, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject json = new JsonObject();
		if (capture == null)
			return json;
		JsonArray array = new JsonArray();
		capture.getSavedLocations().entrySet().forEach(entry -> {
			JsonObject object = new JsonObject();
			object.add("location", context.serialize(entry.getKey(), Location.class));
			object.addProperty("timestamp", entry.getValue());
			array.add(object);
		});
		json.add("saves", array);
		json.addProperty("uuid", capture.getPlayer().getUniqueId() + "");
		return json;
	}

	@Override
	public Capture deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
		JsonObject object = json.getAsJsonObject();
		String stringUUID = object.get("uuid").getAsString();
		if (stringUUID == null)
			return null;
		UUID uuid = UUID.fromString(stringUUID);
		if (uuid == null)
			return null;
		Optional<ParkourPlayer> player = ProParkour.getInstance().getManager(PlayerManager.class).getParkourPlayer(uuid);
		if (!player.isPresent())
			return null;
		JsonElement savesElement = object.get("saves");
		Map<Location, Long> saves = new HashMap<>();
		if (savesElement != null && !savesElement.isJsonNull() && savesElement.isJsonArray()) {
			JsonArray array = savesElement.getAsJsonArray();
			array.forEach(element -> {
				JsonObject saveObject = element.getAsJsonObject();
				JsonElement locationElement = saveObject.get("location");
				if (locationElement == null)
					return;
				Location location = context.deserialize(locationElement, Location.class);
				if (location == null)
					return;
				JsonElement timeElement = saveObject.get("timestamp");
				if (timeElement == null)
					return;
				Long time = timeElement.getAsLong();
				saves.put(location, time);
			});
		}
		return new Capture(player.get(), saves);
	}

}
