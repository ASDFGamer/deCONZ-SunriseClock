package org.d3kad3nt.sunriseClock.data.service;

import androidx.lifecycle.LiveData;

import org.d3kad3nt.sunriseClock.data.model.light.BaseLight;
import org.d3kad3nt.sunriseClock.data.remote.common.Resource;
import org.d3kad3nt.sunriseClock.data.remote.common.Status;
import org.d3kad3nt.sunriseClock.data.repository.LightRepository;
import org.d3kad3nt.sunriseClock.util.LiveDataUtils;

public class LightService{

    LightRepository lightRepository;

    public LightService(LightRepository lightRepository){
        this.lightRepository = lightRepository;
    }

    public void setOn(LiveData<Resource<BaseLight>> light, boolean setOn){
        LiveDataUtils.observeOnce(light, baseLightResource -> {
            if (baseLightResource.getStatus().equals(Status.SUCCESS)){
                BaseLight clone = new BaseLight(baseLightResource.getData());
                clone.setOn(setOn);
                lightRepository.updateLight(clone);
            }
        });
    }
}
