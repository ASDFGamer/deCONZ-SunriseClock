package org.d3kad3nt.sunriseClock.model.endpoint.deconz;

import androidx.lifecycle.LiveData;

import org.d3kad3nt.sunriseClock.model.endpoint.remoteApi.ApiResponse;
import org.d3kad3nt.sunriseClock.model.light.BaseLight;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

/**
 * Defines all relevant deconz API endpoint addresses for usage with the retrofit library.
 */
public interface IServices {

    String endpointLightIdHeader = "X-Deconz-EndpointLightId";

    @GET("lights/")
    LiveData<ApiResponse<List<BaseLight>>> getLights();

    @GET("lights/{lightId}/")
    LiveData<ApiResponse<BaseLight>> getLight(@Path("lightId") String lightId, @Header(endpointLightIdHeader) String headerLightId);
}