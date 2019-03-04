package org.asdfgamer.sunriseClock.network.utils.response.callback;

import android.util.Log;

import org.asdfgamer.sunriseClock.network.utils.response.model.Error;

import androidx.annotation.NonNull;

public abstract class StandardCallback {

    private final static String TAG = "StandardCallback";

    protected void standardCallback(Error error) {
        Log.i(TAG, error.getDescription() + ":" + error.getAddress());
    }

    protected void standardCallback(ConnectionError error) {
        Log.i(TAG, error.toString());
    }

    protected void everytime() {
        //Nothing to do
    }

    protected enum ConnectionError {
        ServiceUnavailable("Service Unavailable"),
        InvalidErrorObject("Server response could not be parsed");

        private final String text;

        ConnectionError(String text) {
            this.text = text;
        }

        @Override
        @NonNull
        public String toString() {
            return text;
        }
    }
}
