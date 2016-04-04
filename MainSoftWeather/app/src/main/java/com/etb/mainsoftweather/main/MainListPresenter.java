package com.etb.mainsoftweather.main;

import android.text.TextUtils;

import com.etb.mainsoftweather.base.MvpLceRxPresenter;
import com.etb.mainsoftweather.base.SearchViewWrapper;
import com.etb.mainsoftweather.model.City;
import com.etb.mainsoftweather.model.Weather;
import com.etb.mainsoftweather.model.WeatherList;
import com.etb.mainsoftweather.sources.cities.CitiesFacade;
import com.etb.mainsoftweather.sources.weather.WeatherFacade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by etb on 02.04.16.
 */
public class MainListPresenter extends MvpLceRxPresenter<MainListView, List<Weather>> implements SearchViewWrapper.SuggestionProvider, SearchViewWrapper.OnSuggestionListener{

    private WeatherFacade _weather;

    private CitiesFacade _cities;

    @Inject public MainListPresenter(CitiesFacade cities, WeatherFacade weather){
         _cities = cities;
        _weather = weather;
    }

    public void loadForecast(boolean pullToRefresh){
        Observable<List<Weather>> observable = _cities.getData(true).flatMap(getForecast(!pullToRefresh));

        subscribe(observable, pullToRefresh);
    }

    private Func1<List<City>, Observable<List<Weather>>> getForecast(final boolean useDB){
        return new Func1<List<City>, Observable<List<Weather>>>() {
            @Override
            public Observable<List<Weather>> call(List<City> cities) {
                return _weather.getLast(useDB, namesOf(cities));
            }
        } ;
    }

    private static String[] namesOf(List<City> cities){
        if(cities == null)
            throw new IllegalArgumentException("cities can't be null");

        String[] names = new String[cities.size()];

        for(int i = 0; i < cities.size(); i++ ){
            names[i] = cities.get(i).name;
        }

        return names;
    }

    @Override
    protected void onError(Throwable e, boolean pullToRefresh) {
        if(isFatal(e))
            super.onError(e, pullToRefresh);
        else
            super.onCompleted();
    }

    private static boolean isFatal(Throwable e){
        //TODO:add processing of no internet connection
        return false;
    }

    @Override
    public Observable<List<City>> queryTextChanged(String text) {

        if(TextUtils.isEmpty(text))
            return Observable.empty();
        else
            return _cities.autocomplete(text, 20);

    }

    @Override
    public void onSuggestionClick(City item) {
        _cities.saveData(item);
        loadForecast(false);
    }
}
