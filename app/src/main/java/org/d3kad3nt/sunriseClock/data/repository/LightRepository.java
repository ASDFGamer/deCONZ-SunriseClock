package org.d3kad3nt.sunriseClock.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import org.d3kad3nt.sunriseClock.data.local.AppDatabase;
import org.d3kad3nt.sunriseClock.data.local.BaseLightDao;
import org.d3kad3nt.sunriseClock.data.model.endpoint.BaseEndpoint;
import org.d3kad3nt.sunriseClock.data.model.light.BaseLight;
import org.d3kad3nt.sunriseClock.data.model.light.ICapability;
import org.d3kad3nt.sunriseClock.data.model.light.Light;
import org.d3kad3nt.sunriseClock.data.model.light.LightID;
import org.d3kad3nt.sunriseClock.data.remote.common.ApiResponse;
import org.d3kad3nt.sunriseClock.data.remote.common.BaseLightNetworkBoundResource;
import org.d3kad3nt.sunriseClock.data.remote.common.NetworkBoundResource;
import org.d3kad3nt.sunriseClock.data.remote.common.Resource;
import org.d3kad3nt.sunriseClock.data.remote.common.Status;
import org.d3kad3nt.sunriseClock.util.LiveDataUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Repository module for handling data operations (network or local database).
 */
public class LightRepository {

    private static BaseLightDao baseLightDao;

    private static volatile LightRepository INSTANCE;
    private final EndpointRepository endpointRepo;
    private final Map<LightID, MediatorLiveData<Resource<BaseLight>>> lightCache = new HashMap<>();

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
            endpointRepo.getEndpoint(endpointId);
        }catch (NullPointerException e){
            Resource<List<BaseLight>> resource = new Resource<>(Status.ERROR, null, "Endpoint doesn't exist");
            return new MutableLiveData<>(resource);
        }
        return new NetworkBoundResource<List<BaseLight>, List<BaseLight>>() {

            final LiveData<BaseEndpoint> endpoint = endpointRepo.getEndpoint(endpointId);

            @NotNull
            @Override
            protected LiveData<ApiResponse<List<BaseLight>>> createCall() {
                return Transformations.switchMap(endpoint, input -> {
                    return input.getLights();
                });
            }

            @NotNull
            @Override
            protected LiveData<List<BaseLight>> loadFromDb() {
                //TODO: return (LiveData<Light>) (LiveData<? extends Light>) baseLight;
                return baseLightDao.loadAllForEndpoint(endpointId);
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
    public LiveData<Resource<BaseLight>> getLight(long endpointId, String endpointLightId){
        LightID lightID = new LightID(endpointId, endpointLightId);
        if (!lightCache.containsKey(lightID)){
            NetworkBoundResource<BaseLight, BaseLight> networkBoundResource = new BaseLightNetworkBoundResource(
                    endpointRepo.getEndpoint(endpointId),
                    endpointId,
                    baseLightDao,
                    endpointLightId
            );
            MediatorLiveData<Resource<BaseLight>> liveData = new MediatorLiveData<>();
            liveData.addSource(networkBoundResource.asLiveData(), baseLightResource -> liveData.setValue(baseLightResource));
            lightCache.put(lightID, liveData);
        }
        return lightCache.get(lightID);

    }

    public void updateLight(BaseLight light){
        if (!lightCache.containsKey(light.getUUID())){
            lightCache.put(light.getUUID(), new MediatorLiveData<>());
        }
        lightCache.get(light.getUUID()).setValue(Resource.Companion.loading(light));

        LiveDataUtils.observeOnce(
                endpointRepo.getEndpoint(light.getEndpointId()),
                baseEndpoint -> {
                    baseEndpoint.updateLight(light);
                    NetworkBoundResource<BaseLight, BaseLight> networkBoundResource = new BaseLightNetworkBoundResource(
                            endpointRepo.getEndpoint(light.getEndpointId()),
                            light.getEndpointId(),
                            baseLightDao,
                            light.getEndpointLightId()
                    );
                    MediatorLiveData<Resource<BaseLight>> liveData = lightCache.get(light.getUUID());
                    liveData.addSource(networkBoundResource.asLiveData(), baseLightResource -> liveData.setValue(baseLightResource));
                }
        );

    }
}
