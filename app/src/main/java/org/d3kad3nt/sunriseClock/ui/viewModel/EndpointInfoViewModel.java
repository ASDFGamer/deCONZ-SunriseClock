package org.d3kad3nt.sunriseClock.ui.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.d3kad3nt.sunriseClock.model.EndpointRepository;
import org.d3kad3nt.sunriseClock.model.endpoint.EndpointConfig;

public class EndpointInfoViewModel extends AndroidViewModel {
    private final EndpointRepository endpointRepository = EndpointRepository.getInstance(getApplication().getApplicationContext());

    public EndpointInfoViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<EndpointConfig> getEndpoint(long endpointID){
        return endpointRepository.getEndpointConfig(endpointID);
    }
}