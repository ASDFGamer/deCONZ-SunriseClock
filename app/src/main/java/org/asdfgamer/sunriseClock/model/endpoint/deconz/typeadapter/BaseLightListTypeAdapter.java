package org.asdfgamer.sunriseClock.model.endpoint.deconz.typeadapter;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import org.asdfgamer.sunriseClock.model.light.BaseLight;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BaseLightListTypeAdapter implements JsonDeserializer<List<BaseLight>> {

    private static final String TAG = "BaseLightListTypeAdapt.";

    @Override
    public List<BaseLight> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(BaseLight.class, new BaseLightTypeAdapter())
                .create();

        JsonObject rawJson = json.getAsJsonObject();
        List<BaseLight> lights = new ArrayList<>();

        Log.d(TAG, "Parsing JSON for list of lights: " + rawJson.toString());

        for (String lightId : rawJson.keySet()) {
            JsonElement jsonLight = rawJson.get(lightId);

            BaseLight light = gson.fromJson(jsonLight.getAsJsonObject(), BaseLight.class);
            light.setId(Integer.parseInt(lightId));

            lights.add(light);
        }

        return lights;
    }
}
