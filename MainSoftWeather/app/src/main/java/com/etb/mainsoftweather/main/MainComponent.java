package com.etb.mainsoftweather.main;

import com.etb.mainsoftweather.sources.CitiesModule;
import com.etb.mainsoftweather.sources.WeatherModule;
import com.etb.mainsoftweather.sources.weather.WeatherFacade;

import javax.inject.Singleton;

import dagger.Component;
import dagger.Subcomponent;

/**
 * Created by etb on 02.04.16.
 */
@ViewScope @Subcomponent(modules = {WeatherModule.class, CitiesModule.class})
public interface MainComponent {

    public void inject(MainListFragment fragment);

    MainListPresenter presenter();



}
