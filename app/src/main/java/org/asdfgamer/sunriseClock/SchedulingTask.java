package org.asdfgamer.sunriseClock;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.asdfgamer.sunriseClock.network.DeconzConnection;
import org.asdfgamer.sunriseClock.utils.ISO8601;

import java.lang.ref.WeakReference;
import java.util.Date;

import androidx.preference.PreferenceManager;

public class SchedulingTask extends AsyncTask<Void, Void, String> {

    private final String TAG = "SchedulingTask";

    private final BroadcastReceiver.PendingResult pendingResult;
    private final AlarmManager alarm;

    private WeakReference<Context> context;
    private SharedPreferences preferences;

    SchedulingTask(BroadcastReceiver.PendingResult pendingResult, AlarmManager alarm, Context context) {
        this.pendingResult = pendingResult;
        this.alarm = alarm;

        this.context = new WeakReference<>(context);
        this.preferences = PreferenceManager.getDefaultSharedPreferences(this.context.get());
    }

    @Override
    protected String doInBackground(Void... voids) {
        Date date = new Date();
        date.setTime(alarm.getNextAlarmClock().getTriggerTime());
        ISO8601 schedulingTime = new ISO8601(date);
        Log.i(TAG, "Next alarm rings at :" + schedulingTime);

        //TODO: Check if deconz url, apikey etc are valid. A 'valid' flag could be set when first adding or modifying these settings.
        //TODO: Improve defaultValues
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .encodedAuthority(preferences.getString("pref_ip", "") + ":" + preferences.getString("pref_port", ""));
        String apiKey = preferences.getString("pref_api_key", "");
        String lightId = preferences.getString("1", ""); //TODO: setting options for light ids

        DeconzConnection deconz = new DeconzConnection(builder.build(), apiKey);
        deconz.scheduleLight(lightId, schedulingTime);

        return "TODO" + schedulingTime;
    }

    @Override
    protected void onPostExecute(String s) {

        Toast.makeText(this.context.get(), s,
                Toast.LENGTH_LONG).show();

        // Must call finish() so the BroadcastReceiver can be recycled.
        pendingResult.finish();
    }

}

