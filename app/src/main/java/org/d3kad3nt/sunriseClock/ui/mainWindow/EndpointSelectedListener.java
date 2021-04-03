package org.d3kad3nt.sunriseClock.ui.mainWindow;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;

import org.d3kad3nt.sunriseClock.model.SettingsRepository;
import org.d3kad3nt.sunriseClock.model.endpoint.BaseEndpoint;

public class EndpointSelectedListener implements android.widget.AdapterView.OnItemSelectedListener {

    private final Context context;

    public EndpointSelectedListener(Context context){
        this.context = context;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        BaseEndpoint selected = (BaseEndpoint) parent.getItemAtPosition(position);
        SettingsRepository settingsRepository = SettingsRepository.getInstance(context);
        settingsRepository.setSetting("endpoint_id",selected.getId());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
