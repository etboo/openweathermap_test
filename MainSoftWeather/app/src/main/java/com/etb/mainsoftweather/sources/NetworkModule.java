package com.etb.mainsoftweather.sources;

import com.etb.mainsoftweather.BuildConfig;
import com.etb.mainsoftweather.model.Weather;
import com.etb.mainsoftweather.parsing.WeatherDeserializer;
import com.etb.mainsoftweather.sources.weather.WeatherAPI;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Created by etb on 03.04.16.
 */
@Module
public class NetworkModule {

    @Provides @Singleton
    public GsonConverter provideConverter(){
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Weather.class, new WeatherDeserializer());
        Gson gson = gsonBuilder.create();

        return new GsonConverter(gson);
    }


    @Provides
    @Singleton
    public WeatherAPI providesWeatherAPI(GsonConverter converter){

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(BuildConfig.WEATHER_URL)
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestInterceptor.RequestFacade request) {
                        request.addQueryParam("APPID", BuildConfig.WEATHER_API_KEY);
                    }
                })
                .setConverter(converter)
                .build();

        return restAdapter.create(WeatherAPI.class);
    }

}
