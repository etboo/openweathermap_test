package com.etb.mainsoftweather.sources;

/**
 * Created by etb on 02.04.16.
 */

import android.content.Context;

import com.etb.mainsoftweather.main.ViewScope;
import com.etb.mainsoftweather.model.Weather;
import com.etb.mainsoftweather.sources.weather.WeatherAPI;
import com.etb.mainsoftweather.sources.weather.WeatherFacade;

import java.sql.SQLException;

import dagger.Module;
import dagger.Provides;

@Module
public class WeatherModule {

    private Context _context;

    public WeatherModule(Context context){
        _context = context;
    }

    @Provides @ViewScope
    public DAO<Weather, String> providesDAO(DatabaseHelper helper){
        try {
            return helper.createWeatherDAO();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Provides @ViewScope
    public WeatherFacade provideFacade(WeatherAPI network, DAO<Weather, String> db){
        return new WeatherFacade(network, db);
    }

}
