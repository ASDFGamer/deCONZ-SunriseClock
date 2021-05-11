package org.d3kad3nt.sunriseClock.util;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

public class LiveDataUtils{

    /**
     * source: https://stackoverflow.com/a/55125983
     *
     * @param liveData
     * @param observer
     * @param <T>
     */
    public static <T> void observeOnce(LiveData<T> liveData, Observer<T> observer){
        liveData.observeForever(new Observer<T>(){
            @Override
            public void onChanged(T t){
                liveData.removeObserver(this);
                observer.onChanged(t);
            }
        });
    }
}
