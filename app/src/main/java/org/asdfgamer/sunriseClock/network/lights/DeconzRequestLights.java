package org.asdfgamer.sunriseClock.network.lights;

import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.asdfgamer.sunriseClock.network.lights.model.Light;
import org.asdfgamer.sunriseClock.network.DeconzRequest;
import org.asdfgamer.sunriseClock.network.utils.response.genericCallback.DeconzBaseCallback;
import org.asdfgamer.sunriseClock.network.utils.response.genericCallback.DeconzBaseCallbackAdapter;
import org.asdfgamer.sunriseClock.network.utils.response.genericCallback.DeconzGetCallbackAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class DeconzRequestLights extends DeconzRequest {

    public DeconzRequestLights(Uri baseUrl, String apiKey) {
        super(baseUrl, apiKey);
        init();
    }

    private static final String TAG = "DeconzRequestLights";

    private LightsEndpoint lightsEndpoint;

    @Override
    public void init() {
        Log.d(TAG, "Init() called.");

        //Set custom Gson deserializer
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(List.class, new GetLightsDeserializer())
                .create();
        super.setGsonDeserializer(gson);

        this.lightsEndpoint = super.createService(LightsEndpoint.class);
    }

    public void getLights(GetLightsCallback callback) {
        Call<List<Light>> call = lightsEndpoint.getLights();
        call.enqueue(new DeconzBaseCallbackAdapter<>(callback));
    }

    public void getLight(GetLightCallback callback, String lightId) {
        Call<Light> call = lightsEndpoint.getLight(lightId);
        call.enqueue(new DeconzGetCallbackAdapter<>(callback));
    }

    private interface LightsEndpoint {

        @GET("lights/")
        Call<List<Light>> getLights();

        @GET("lights/{lightId}/")
        Call<Light> getLight(@Path("lightId") String lightId);
    }
}
