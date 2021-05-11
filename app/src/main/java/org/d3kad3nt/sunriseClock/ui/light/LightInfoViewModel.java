package org.d3kad3nt.sunriseClock.ui.light;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.d3kad3nt.sunriseClock.data.model.light.BaseLight;
import org.d3kad3nt.sunriseClock.data.model.light.LightID;
import org.d3kad3nt.sunriseClock.data.remote.common.Resource;
import org.d3kad3nt.sunriseClock.data.repository.LightRepository;
import org.d3kad3nt.sunriseClock.data.service.LightService;

public class LightInfoViewModel extends AndroidViewModel{

    private final LightRepository lightRepository = LightRepository.getInstance(getApplication().getApplicationContext());

    private final LightService lightService = new LightService(lightRepository);

    public LightInfoViewModel(@NonNull Application application){
        super(application);
        //TODO use something better
    }

    public LiveData<Resource<BaseLight>> getLight(LightID lightID){
        return lightRepository.getLight(lightID);

    }

    public void setOn(LightID lightID, boolean newState){
        lightService.setOn(lightRepository.getLight(lightID), newState);
    }
}
