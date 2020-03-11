package org.asdfgamer.sunriseClock.model.endpoint;

import org.asdfgamer.sunriseClock.model.LightEndpoint;

public abstract class BaseEndpoint implements LightEndpoint {

    EndpointConfig originalEndpointConfig;

    /**
     * Must be used to initialize the specific endpoint implementation after its (primitive) fields
     * have been populated by eg. Gson. After initialization the endpoint must be ready to perform
     * remote (network) operations.
     */
    public abstract BaseEndpoint init();

    public EndpointConfig getOriginalEndpointConfig() {
        return originalEndpointConfig;
    }

    public void setOriginalEndpointConfig(EndpointConfig originalEndpointConfig) {
        this.originalEndpointConfig = originalEndpointConfig;
    }

}
