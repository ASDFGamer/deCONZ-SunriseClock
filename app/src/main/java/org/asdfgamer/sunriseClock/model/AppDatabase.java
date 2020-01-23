package org.asdfgamer.sunriseClock.model;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import org.asdfgamer.sunriseClock.model.light.BaseLight;
import org.asdfgamer.sunriseClock.model.light.LightRemote_SwitchableUndimmableUntemperaturableUncolorable;
import org.asdfgamer.sunriseClock.model.light.LightRemote_SwitchableUndimmableUntemperaturableUncolorableDao;
import org.asdfgamer.sunriseClock.model.light.LightRemote_UnswitchableUndimmableUntemperaturableColorable;

@Database(entities = {BaseLight.class,
        LightRemote_UnswitchableUndimmableUntemperaturableColorable.class,
        LightRemote_SwitchableUndimmableUntemperaturableUncolorable.class},
        version = 1,
        exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context,
                    AppDatabase.class, "sunriseclock-db-DEV.db")
                    .allowMainThreadQueries() //TODO: Only for testing purposes. Could lock the main thread, therefore not for production!
                    .build();
        }

        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    public abstract LightRemote_SwitchableUndimmableUntemperaturableUncolorableDao dao1();

    //TODO: getDaoInstanceFor(T extends BaseLight) implementieren. Oder ist das ne schlechte Idee?

}