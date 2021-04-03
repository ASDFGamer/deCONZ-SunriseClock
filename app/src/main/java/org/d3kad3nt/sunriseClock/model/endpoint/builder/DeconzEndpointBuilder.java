package org.d3kad3nt.sunriseClock.model.endpoint.builder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.d3kad3nt.sunriseClock.model.endpoint.BaseEndpoint;
import org.d3kad3nt.sunriseClock.model.endpoint.EndpointConfig;
import org.d3kad3nt.sunriseClock.model.endpoint.deconz.DeconzEndpoint;

public class DeconzEndpointBuilder implements EndpointBuilder {

    private EndpointConfig config = null;

    @Override
    public EndpointBuilder setConfig(EndpointConfig config) {
        this.config = config;
        return this;
    }

    @Override
    public BaseEndpoint build() {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        BaseEndpoint endpoint = gson.fromJson(config.getJsonConfig(), DeconzEndpoint.class);
        // Postprocessing: Set original endpoint config.
        endpoint.setOriginalEndpointConfig(config);

        return endpoint.init();
    }
}
