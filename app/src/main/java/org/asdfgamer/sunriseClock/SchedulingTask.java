package org.asdfgamer.sunriseClock;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

import org.asdfgamer.sunriseClock.network.schedules.DeconzRequestSchedulesHelper;
import org.asdfgamer.sunriseClock.network.utils.response.custDeserializer.model.Success;
import org.asdfgamer.sunriseClock.network.utils.response.genericCallback.SimplifiedCallback;
import org.asdfgamer.sunriseClock.utils.ISO8601;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import retrofit2.Response;

import static org.asdfgamer.sunriseClock.utils.SettingKeys.ACTIVATED_LIGHTS;
import static org.asdfgamer.sunriseClock.utils.SettingKeys.ALARM_ACTIVE;
import static org.asdfgamer.sunriseClock.utils.SettingKeys.ALARM_TIME;
import static org.asdfgamer.sunriseClock.utils.SettingKeys.API_KEY;
import static org.asdfgamer.sunriseClock.utils.SettingKeys.IP;
import static org.asdfgamer.sunriseClock.utils.SettingKeys.PORT;
import static org.asdfgamer.sunriseClock.utils.SettingKeys.TOAST_ACTIVE;

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
        if (!preferences.getBoolean(ALARM_ACTIVE.toString(), false)) {
            Log.i(TAG, "SunriseClock is not active");
            return "SunriseClock is not active";
        }
        Date date = new Date();
        final long alarmTime = alarm.getNextAlarmClock().getTriggerTime();
        date.setTime(alarmTime);
        ISO8601 schedulingTime = new ISO8601(date);
        Log.i(TAG, "Next alarm rings at :" + schedulingTime);

        //TODO: Check if deconz url, apikey etc are valid. A 'valid' flag could be set when first adding or modifying these settings.
        //TODO: Improve defaultValues
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .encodedAuthority(preferences.getString(IP.toString(), "") + ":" + preferences.getString(PORT.toString(), ""));
        String apiKey = preferences.getString(API_KEY.toString(), "");
        Set<String> lightIds = preferences.getStringSet(ACTIVATED_LIGHTS.toString(), new HashSet<String>());
        for (String lightId : lightIds) {
            DeconzRequestSchedulesHelper deconz = new DeconzRequestSchedulesHelper(builder.build(), apiKey);
            deconz.schedulePowerOn(new SimplifiedCallback<Success>() {
                @Override
                public void onSuccess(Response<Success> response) {
                    Success success = response.body();
                    Log.i(TAG, "Successfully created schedule with id: " + Objects.requireNonNull(success).getId());
                    preferences.edit().putLong(ALARM_TIME.toString(), alarmTime).apply();
                }

                @Override
                public void onError() {
                    //TODO
                }
            }, lightId, schedulingTime);
        }


        return "TODO" + schedulingTime;
    }

    @Override
    protected void onPostExecute(String s) {

        if (preferences.getBoolean(TOAST_ACTIVE.toString(), true)) {
            Toast.makeText(this.context.get(), s,
                    Toast.LENGTH_LONG).show();
        }

        // Must call finish() so the BroadcastReceiver can be recycled.
        pendingResult.finish();
    }

}

