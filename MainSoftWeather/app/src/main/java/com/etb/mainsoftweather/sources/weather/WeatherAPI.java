package com.etb.mainsoftweather.sources.weather;

import com.etb.mainsoftweather.model.City;
import com.etb.mainsoftweather.model.Weather;
import com.etb.mainsoftweather.model.WeatherList;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by etb on 02.04.16.
 */
public interface WeatherAPI {

    @GET("/data/2.5/find?mode=json")
    Observable<WeatherList> getWeather(@Query("q") String fullName);

    @GET("/data/2.5/find?mode=json")
    Observable<WeatherList> getWeatherWithCoords(@Query("lat") String lat, @Query("lon") String lon);

    @GET("/data/2.5/find?mode=json&type=like&cnt=10")
    Observable<WeatherList> getWeatherLike(@Query("q") String city);

    @GET("/data/2.5/find?mode=json&type=like")
    Observable<WeatherList> getWeather(@Query("q") String city, @Query("cnt") String count);

}
