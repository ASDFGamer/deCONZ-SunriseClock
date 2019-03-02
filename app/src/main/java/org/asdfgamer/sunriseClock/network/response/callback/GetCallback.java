package org.asdfgamer.sunriseClock.network.response.callback;

import org.asdfgamer.sunriseClock.network.response.model.Error;

/**
 * Callback interface for deconz network requests returning the following HTTP status codes:
 * HTTP status code 404 and the ones from {@see BaseCallback}.
 * <p>
 * These status codes are usually returned by all *specific* GET operations: eg. GET /lights doesn't
 * need this status code but a 'more specific' GET /lights/1 could return this status code.
 *
 * @param <T>
 */
public interface GetCallback<T> extends BaseCallback<T> {

    /**
     * Called for all HTTP status codes representing an request for which the requested resource was not found:
     * HTTP status code 404.
     *
     * @param error A custom error object describing the error that occurred in deconz.
     */
    void onRessourceNotFound(Error error);

}
