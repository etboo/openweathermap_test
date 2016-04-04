package com.etb.mainsoftweather.history;

import com.etb.mainsoftweather.main.ViewScope;
import com.etb.mainsoftweather.model.City;
import com.etb.mainsoftweather.sources.CitiesModule;
import com.etb.mainsoftweather.sources.WeatherModule;

import dagger.Subcomponent;

/**
 * Created by etb on 02.04.16.
 */
@ViewScope @Subcomponent(modules = {WeatherModule.class})
public interface HistoryComponent {

    public void inject(HistoryFragment fragment);

    HistoryPresenter presenter();



}
