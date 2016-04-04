package com.etb.mainsoftweather.base;

import com.etb.mainsoftweather.main.MainComponent;
import com.etb.mainsoftweather.sources.CitiesModule;
import com.etb.mainsoftweather.sources.DBModule;
import com.etb.mainsoftweather.sources.NetworkModule;
import com.etb.mainsoftweather.sources.WeatherModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by etb on 03.04.16.
 */
@Singleton @Component(modules = {DBModule.class, NetworkModule.class})
public interface ApplicationComponent {
    MainComponent plus(WeatherModule weatherModule, CitiesModule citiesModule);
}
