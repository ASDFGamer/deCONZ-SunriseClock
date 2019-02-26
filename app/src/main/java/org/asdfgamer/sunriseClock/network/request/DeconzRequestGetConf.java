package org.asdfgamer.sunriseClock.network.request;

import android.net.Uri;

import com.android.volley.Response;

import org.asdfgamer.sunriseClock.network.DeconzApiEndpoints;
import org.asdfgamer.sunriseClock.network.listener.ErrorListener;
import org.asdfgamer.sunriseClock.network.listener.GUIListener;
import org.asdfgamer.sunriseClock.network.listener.GetConfListener;
import org.asdfgamer.sunriseClock.network.response.DeconzResponseGetConf;
import org.asdfgamer.sunriseClock.network.response.IResponseListenerJSONObject;
import org.json.JSONObject;

public class DeconzRequestGetConf extends DeconzRequest implements IResponseListenerJSONObject {

    public DeconzRequestGetConf(Uri baseUrl, String apiKey) {
        super(baseUrl, apiKey);
    }

    private static final String TAG = "DeconzRequestGetConf";

    @Override
    public void init() {
        super.setBaseCommandPath(super.getBaseCommandPath().buildUpon().appendPath(DeconzApiEndpoints.CONFIGURATION.getApiEndpoint()).build());
    }

    @Override
    public void fire(GUIListener listener) {
        DeconzResponseGetConf deconzResponseGetConf = new DeconzResponseGetConf();
        GetConfListener getConfListener = new GetConfListener(listener, deconzResponseGetConf);

        getFromDeconz(getConfListener, new ErrorListener(listener, deconzResponseGetConf));
    }

    @Override
    public void fire() {
        getFromDeconz();
    }
}
