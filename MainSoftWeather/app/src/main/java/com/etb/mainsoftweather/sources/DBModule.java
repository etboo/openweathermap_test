package com.etb.mainsoftweather.sources;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by etb on 03.04.16.
 */
@Module
public class DBModule {

    private Context _context;

    public DBModule(Context context){
        _context = context;
    }

    @Provides
    @Singleton
    DatabaseHelper provideDatabaseHelper() {
        return new DatabaseHelper(_context);
    }
}
