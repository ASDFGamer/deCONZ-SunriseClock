package org.asdfgamer.sunriseClock.network;

import android.net.Uri;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.asdfgamer.sunriseClock.network.utils.RequestPathBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

public class DeconzRequest extends DeconzConnection {

    DeconzRequest(Uri baseUrl, String apiKey, Uri baseCommandPath) {
        super(baseUrl, apiKey);
        this.baseCommandPath = baseCommandPath;
        this.requestPathBuilder = new RequestPathBuilder(super.getFullApiUrl());
    }

    private static final String TAG = "DeconzRequest";

    /* Path of the command address (e.g. 'lights' or 'lights/1')*/
    private Uri baseCommandPath;

    /* Helper class for building request URLs */
    RequestPathBuilder requestPathBuilder;

    private VolleyErrorNetworkReponse volleyErrorNetworkReponse;
    private VolleyParseNetworkReponse volleyParseNetworkReponse;

    /**
     * Uses GET to get data (duh!) from the deconz endpoint. Uses default listeners for response and Error Listeners.
     * Default listeners are useful for debugging purposes because they print several status messages.
     */
    void getFromDeconz() {
        Log.d("getFromDeconz", "GET from: " + this.buildFullRequestPath());
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, this.buildFullRequestPath().toString(), null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG, "Connection succeeded. Response: " + response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                volleyErrorNetworkReponse = new VolleyErrorNetworkReponse(error);
                volleyErrorNetworkReponse.printError();
            }
        }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                volleyParseNetworkReponse = new VolleyParseNetworkReponse(response);
                volleyParseNetworkReponse.printReturnCode();
                return super.parseNetworkResponse(response);
            }
        };
        networkRequestQueue.add(jsObjRequest);
    }

    /**
     * Uses GET to get data (duh!) from the deconz endpoint. Uses own listeners for response and Error Listeners.
     * Own listeners can be used to update GUI elements (eg in fragments). Don't use default listeners for these situations.
     *
     * @param customListenerSuccess Custom listener for when the request returns successfully.
     * @param customListenerError   Custom listener for when the request returns unsuccessfully.
     */
    void getFromDeconz(Response.Listener<JSONObject> customListenerSuccess, Response.ErrorListener customListenerError) {
        Log.d("getFromDeconz", "GET from: " + this.buildFullRequestPath());
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, this.buildFullRequestPath().toString(), null, customListenerSuccess, customListenerError);
        networkRequestQueue.add(jsObjRequest);
    }

    /**
     * Uses POST to send data to the deconz endpoint. Uses default listeners for Response and Error listeners.
     * Default listeners are useful for debugging purposes because they print several status messages.
     * Expects to receive a JSON array (not object!): This is, at least, the case for the /schedules endpoint.
     *
     * @param postJsonData Json object to send to the deconz API.
     */
    void sendToDeconz(JSONObject postJsonData) {
        Log.d("sendToDeconz", "POSTing " + postJsonData.toString() + " to: " + this.buildFullRequestPath());
        final VolleyJsonArrayRequest jsObjRequest = new VolleyJsonArrayRequest(Request.Method.POST, this.buildFullRequestPath().toString(), postJsonData
                , new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.i(TAG, "Connection succeeded. Response: " + response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                volleyErrorNetworkReponse = new VolleyErrorNetworkReponse(error);
                volleyErrorNetworkReponse.printError();
            }
        }) {
            @Override
            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
                volleyParseNetworkReponse = new VolleyParseNetworkReponse(response);
                volleyParseNetworkReponse.printReturnCode();
                return super.parseNetworkResponse(response);
            }
        };
        networkRequestQueue.add(jsObjRequest);
    }

    /**
     * Uses POST to send data to the deconz endpoint. Uses own listeners for Response and Error listeners.
     * Own listeners can be used to update GUI elements (eg in fragments). Don't use default listeners for these situations.
     * Expects to receive a JSON array (not object!): This is, at least, the case for the /schedules endpoint.
     *
     * @param postJsonData          Json object to send to the deconz API.
     * @param customListenerSuccess Custom listener for when the request returns successfully.
     * @param customListenerError   Custom listener for when the request returns unsuccessfully.
     */
    void sendToDeconz(JSONObject postJsonData, Response.Listener<JSONArray> customListenerSuccess, Response.ErrorListener customListenerError) {
        Log.d("sendToDeconz", "POSTing " + postJsonData.toString() + " to: " + this.buildFullRequestPath());
        final VolleyJsonArrayRequest jsObjRequest = new VolleyJsonArrayRequest(Request.Method.POST, this.buildFullRequestPath().toString(), postJsonData, customListenerSuccess, customListenerError);
        networkRequestQueue.add(jsObjRequest);
    }

    /**
     * @return Full path to the used deconz API endpoint, e.g. 'deconz.example.org:8080/lights/1'
     */
    private Uri buildFullRequestPath() {
        return super.getFullApiUrl()
                .buildUpon()
                .appendEncodedPath(this.baseCommandPath.toString())
                .build();
    }

}
