package com.etb.mainsoftweather.history;

import com.etb.mainsoftweather.base.MvpLceRxPresenter;
import com.etb.mainsoftweather.model.City;
import com.etb.mainsoftweather.model.Weather;
import com.etb.mainsoftweather.sources.weather.WeatherFacade;

import java.util.List;

import javax.inject.Inject;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by etb on 02.04.16.
 */
public class HistoryPresenter extends MvpLceRxPresenter<HistoryListView, List<Weather>>{

    private WeatherFacade _weather;
    private City _city;

    @Inject public HistoryPresenter( WeatherFacade weather){
        _weather = weather;
    }

    public void injectCity(City city){
        _city = city;
    }

    @Override
    public void attachView(HistoryListView view) {
        super.attachView(view);
        view.setTitle(_city.name);
    }

    public void loadForecast(){
        subscribe(_weather.findInDb(_city), false);
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

}
