package org.d3kad3nt.sunriseClock.data.remote.deconz;

import androidx.lifecycle.LiveData;

import org.d3kad3nt.sunriseClock.data.model.light.BaseLight;
import org.d3kad3nt.sunriseClock.data.remote.common.ApiResponse;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Defines all relevant deconz API endpoint addresses for usage with the retrofit library.
 */
public interface IServices{

    String endpointLightIdHeader = "X-Deconz-EndpointLightId";

    @GET("lights/")
    LiveData<ApiResponse<List<BaseLight>>> getLights();

    @GET("lights/{lightId}/")
    LiveData<ApiResponse<BaseLight>> getLight(@Path("lightId") String lightId, @Header(endpointLightIdHeader) String headerLightId);

    //TODO return response as something like LiveData<ApiResponse<List<Change>>>
    @PUT("lights/{lightId}/state")
    void updateLight(@Path("lightID") String lightID, @Body BaseLight light, @Header(endpointLightIdHeader) String headerLightId);
}
