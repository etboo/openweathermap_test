package com.etb.mainsoftweather.sources;

import android.content.Context;

import com.etb.mainsoftweather.main.ViewScope;
import com.etb.mainsoftweather.model.City;
import com.etb.mainsoftweather.sources.cities.CitiesFacade;
import com.etb.mainsoftweather.sources.weather.WeatherAPI;

import java.sql.SQLException;

import dagger.Module;
import dagger.Provides;

/**
 * Created by etb on 02.04.16.
 */
@Module
public class CitiesModule {

    private Context _context;

    public CitiesModule(Context context){
        _context = context;
    }

    @Provides @ViewScope
    public DAO<City, String> providesDAO(DatabaseHelper helper) {
        try {
            return helper.createCitiesDAO();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Provides @ViewScope
    public CitiesFacade provideFacade(WeatherAPI network, DAO<City, String> db){
        return new CitiesFacade(_context, network, db);
    }
}
