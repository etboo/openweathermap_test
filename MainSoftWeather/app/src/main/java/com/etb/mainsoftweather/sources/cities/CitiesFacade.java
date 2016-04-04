package com.etb.mainsoftweather.sources.cities;

import android.Manifest;
import android.content.Context;

import com.etb.mainsoftweather.PermissionManager;
import com.etb.mainsoftweather.model.City;
import com.etb.mainsoftweather.model.Weather;
import com.etb.mainsoftweather.model.WeatherList;
import com.etb.mainsoftweather.sources.DAO;
import com.etb.mainsoftweather.sources.weather.WeatherAPI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by etb on 02.04.16.
 */
public class CitiesFacade {

    WeatherAPI _network;
    DAO<City, String> _db;
    Context _context;

    public CitiesFacade(Context context, WeatherAPI network, DAO<City, String> db){
        _network = network;
        _db = db;
        _context = context;
    }


    public Observable<List<City>> getData(boolean useDB, String ... strings) {
        return getSavedData();
    }

    public void removeData(City data) {

    }

    private Observable<List<City>> getSavedData(){
        return _db.getAllData().switchMap(new Func1<List<City>, Observable<? extends List<City>>>() {
            @Override
            public Observable<? extends List<City>> call(List<City> cities) {
                if (cities != null && !cities.isEmpty())
                    return Observable.just(cities);
                else {
                    return searchViaGPS();
                }
            }
        });
    }

    private Observable<List<City>> searchViaGPS(){
        return PermissionManager.instance().requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, CityLocalFinder.MY_PERMISSIONS_ACCESS_FINE_LOCATION)
                .switchMap(new Func1<Boolean, Observable<? extends List<City>>>() {
                    @Override
                    public Observable<? extends List<City>> call(Boolean aBoolean) {
                        if(!aBoolean)
                            return Observable.error(new SecurityException("permission wasn't granted"));
                        else{
                            return new CityLocalFinder(_context, _network).getCityByCurrentLocation().map(new Func1<City, List<City>>() {
                                @Override
                                public List<City> call(City city) {
                                    return Arrays.asList(city);
                                }
                            });
                        }
                    }
                }).doOnNext(new Action1<List<City>>() {
                    @Override
                    public void call(List<City> cities) {
                        _db.saveData(cities);
                    }
                });

    }

    public void saveData(City data){
        _db.saveData(Arrays.asList(data));
    }

    public Observable<List<City>> autocomplete(String term, int count){
        return _network.getWeather(term, Integer.toString(count)).map(new Func1<WeatherList, List<City>>() {
            @Override
            public List<City> call(WeatherList weatherList) {
                List<City> result = new ArrayList<City>();
                for(Weather weather : weatherList.list){
                    result.add(weather.city);
                }

                return result;
            }
        });
    }


}
