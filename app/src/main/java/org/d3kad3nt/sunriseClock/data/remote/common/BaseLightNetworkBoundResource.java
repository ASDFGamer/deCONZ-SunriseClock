package org.d3kad3nt.sunriseClock.data.remote.common;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import org.d3kad3nt.sunriseClock.data.local.BaseLightDao;
import org.d3kad3nt.sunriseClock.data.model.endpoint.BaseEndpoint;
import org.d3kad3nt.sunriseClock.data.model.light.BaseLight;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BaseLightNetworkBoundResource extends NetworkBoundResource<BaseLight, BaseLight>{
    private final LiveData<BaseEndpoint> endpoint;
    private final String endpointLightId;
    private final BaseLightDao baseLightDao;
    private final long endpointId;

    public BaseLightNetworkBoundResource(LiveData<BaseEndpoint> endpoint, long endpointId, BaseLightDao baseLightDao, String endpointLightId){
        this.endpoint = endpoint;
        this.endpointId = endpointId;
        this.endpointLightId = endpointLightId;
        this.baseLightDao = baseLightDao;
    }

    @NotNull
    @Override
    protected LiveData<ApiResponse<BaseLight>> createCall(){
        return Transformations.switchMap(endpoint, input -> {
            return input.getLight(endpointLightId);
        });
    }

    @NotNull
    @Override
    protected LiveData<BaseLight> loadFromDb(){
        //TODO: return (LiveData<Light>) (LiveData<? extends Light>) baseLight;
        return baseLightDao.load(endpointId, endpointLightId);
    }

    @Override
    protected boolean shouldFetch(@Nullable BaseLight data){
        //TODO
        return true;
    }

    @Override
    protected void saveCallResult(BaseLight item){
        baseLightDao.upsert(item);
    }
}
