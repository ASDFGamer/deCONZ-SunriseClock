package org.d3kad3nt.sunriseClock.data.remote.common;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import org.d3kad3nt.sunriseClock.data.local.BaseLightDao;
import org.d3kad3nt.sunriseClock.data.model.endpoint.BaseEndpoint;
import org.d3kad3nt.sunriseClock.data.model.light.BaseLight;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This can't be a subclass of NetworkBoundResource, because the constructor of NetworkBoundResource
 * would run before this constructor. This would prevent that the the constructor of a subclass can
 * initialise the methods, especially loadFromDB()
 */

public class BaseLightNetworkBoundResource{

    public static NetworkBoundResource<BaseLight, BaseLight> getInstance(LiveData<BaseEndpoint> endpoint, long endpointId, BaseLightDao baseLightDao, String endpointLightId){
        return new NetworkBoundResource<BaseLight, BaseLight>(){
            final LiveData<BaseEndpoint> baseEndpoint = endpoint;

            @NotNull
            @Override
            protected LiveData<ApiResponse<BaseLight>> createCall(){
                return Transformations.switchMap(baseEndpoint, input -> {
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
        };
    }
}
