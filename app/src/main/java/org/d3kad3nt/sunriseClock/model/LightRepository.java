package org.d3kad3nt.sunriseClock.model;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import org.d3kad3nt.sunriseClock.model.endpoint.BaseEndpoint;
import org.d3kad3nt.sunriseClock.model.endpoint.remoteApi.ApiResponse;
import org.d3kad3nt.sunriseClock.model.endpoint.remoteApi.NetworkBoundResource;
import org.d3kad3nt.sunriseClock.model.endpoint.remoteApi.Resource;
import org.d3kad3nt.sunriseClock.model.endpoint.remoteApi.Status;
import org.d3kad3nt.sunriseClock.model.light.BaseLight;
import org.d3kad3nt.sunriseClock.model.light.BaseLightDao;
import org.d3kad3nt.sunriseClock.model.light.ICapability;
import org.d3kad3nt.sunriseClock.model.light.Light;
import org.d3kad3nt.sunriseClock.model.light.LightID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Repository module for handling data operations (network or local database).
 */
public class LightRepository {

    private static BaseLightDao baseLightDao;

    private static volatile LightRepository INSTANCE;
    private final EndpointRepository endpointRepo;

    /**
     * Using singleton pattern as of now. With dependency injection (Dagger, ...) this class could be mocked when unit testing.
     * TODO: Dependency Injection, optional
     */
    private LightRepository (Context context) {
        baseLightDao = AppDatabase.getInstance(context.getApplicationContext()).baseLightDao();
        endpointRepo = EndpointRepository.getInstance(context);
    }

    public static LightRepository getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (LightRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new LightRepository(context);
                }
            }
        }
        return INSTANCE;
    }

    private void refreshLight(int endpointId, String endpointLightId){
        //TODO: implement, still needed? (as getLight could be called to update light)
    }

    public LiveData<List<Light>> getLightByCapability(Class<? extends ICapability>... capabilities ){
        return (LiveData<List<Light>>)(LiveData<? extends List<? extends Light>>) baseLightDao.loadWithCap(capabilities);
    }

    //TODO: return Light interface instead of raw BaseLight
    public LiveData<Resource<List<BaseLight>>> getLightsForEndpoint(long endpointId) {
        try {
            endpointRepo.getSynchronEndpoint(endpointId);
        }catch (NullPointerException e){
            Resource<List<BaseLight>> resource = new Resource<>(Status.ERROR, null, "Endpoint doesn't exist");
            return new MutableLiveData<>(resource);
        }
        return new NetworkBoundResource<List<BaseLight>, List<BaseLight>>() {

            BaseEndpoint endpoint = endpointRepo.getSynchronEndpoint(endpointId);

            @NotNull
            @Override
            protected LiveData<ApiResponse<List<BaseLight>>> createCall() {
                return endpoint.getLights();
            }

            @NotNull
            @Override
            protected LiveData<List<BaseLight>> loadFromDb() {
                LiveData<List<BaseLight>> livedata =  baseLightDao.loadAllForEndpoint(endpointId);

                // Transform LiveData so that lights have non-null endpoint.

                livedata = Transformations.map(livedata, baseLights -> {
                    for (BaseLight baseLight : baseLights) {
                        baseLight.endpoint = endpointRepo.getSynchronEndpoint(baseLight.getEndpointId());
                    }
                    return baseLights;
                });

                //TODO: return (LiveData<Light>) (LiveData<? extends Light>) baseLight;
                return livedata;
            }

            @Override
            protected boolean shouldFetch(@Nullable List<BaseLight> data) {
                //TODO
                return true;
            }

            @Override
            protected void saveCallResult(List<BaseLight> items) {
                for (BaseLight light : items) {
                    baseLightDao.upsert(light);
                }
            }
        }.asLiveData();
    }

    public LiveData<Resource<BaseLight>> getLight(LightID lightID) {
        return getLight(lightID.getEndpointID(), lightID.getEndpointLightID());
    }


    //TODO: return Light interface instead of raw BaseLight
    public LiveData<Resource<BaseLight>> getLight(long endpointId, String endpointLightId) {
        return new NetworkBoundResource<BaseLight, BaseLight>() {
            BaseEndpoint endpoint = endpointRepo.getSynchronEndpoint(endpointId);

            @NotNull
            @Override
            protected LiveData<ApiResponse<BaseLight>> createCall() {
                return endpoint.getLight(endpointLightId);
            }

            @NotNull
            @Override
            protected LiveData<BaseLight> loadFromDb() {
                LiveData<BaseLight> livedata =  baseLightDao.load(endpointId, endpointLightId);

                // Transform LiveData so that lights have non-null endpoint.
                livedata = Transformations.map(livedata, baseLight -> {
                    baseLight.endpoint = endpointRepo.getSynchronEndpoint(baseLight.getEndpointId());
                    return baseLight;
                });

                //TODO: return (LiveData<Light>) (LiveData<? extends Light>) baseLight;
                return livedata;
            }

            @Override
            protected boolean shouldFetch(@Nullable BaseLight data) {
                //TODO
                return true;
            }

            @Override
            protected void saveCallResult(BaseLight item) {
                baseLightDao.upsert(item);
            }
        }.asLiveData();
    }

}
