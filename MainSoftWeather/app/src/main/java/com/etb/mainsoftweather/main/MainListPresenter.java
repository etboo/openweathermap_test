package com.etb.mainsoftweather.main;

import android.text.TextUtils;
import android.util.Log;

import com.etb.mainsoftweather.SettingsManager;
import com.etb.mainsoftweather.Updater;
import com.etb.mainsoftweather.base.MvpLceRxPresenter;
import com.etb.mainsoftweather.base.SearchViewWrapper;
import com.etb.mainsoftweather.model.City;
import com.etb.mainsoftweather.model.Weather;
import com.etb.mainsoftweather.sources.CitiesModule_ProvideFacadeFactory;
import com.etb.mainsoftweather.sources.cities.CitiesFacade;
import com.etb.mainsoftweather.sources.weather.WeatherFacade;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by etb on 02.04.16.
 */
public class MainListPresenter extends MvpLceRxPresenter<MainListView, List<Weather>> implements SearchViewWrapper.SuggestionProvider, SearchViewWrapper.OnSuggestionListener{

    private WeatherFacade _weather;

    private CitiesFacade _cities;

    private CompositeSubscription _subscription;
    private Subscription _updateSubscription;

    @Inject public MainListPresenter(CitiesFacade cities, WeatherFacade weather){
         _cities = cities;
        _weather = weather;
    }


    public void setupFlows(MainListView view){
        composeSubscriptions(processClicks(view));
        composeSubscriptions(processLongClicks(view));
    }

    private Subscription processClicks(final MainListView view){
        return  view.listClicks().map(new Func1<Weather, City>() {
            @Override
            public City call(Weather weather) {
                return weather.city;
            }
        }).subscribe(new Subscriber<City>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                MainListPresenter.this.onError(e, false);
            }

            @Override
            public void onNext(City city) {
                view.navigateToForecast(city);
            }
        });
    }

    private Subscription processLongClicks(final MainListView view){
        return  view.listLongClicks().map(new Func1<Weather, City>() {
            @Override
            public City call(Weather weather) {
                return weather.city;
            }
        }).subscribe(new Subscriber<City>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                MainListPresenter.this.onError(e, false);
            }

            @Override
            public void onNext(final City city) {
                subscribe(_weather.removeAllWith(city).flatMap(new Func1<Boolean, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(Boolean result) {
                        return _cities.removeData(city);
                    }
                }).flatMap(new Func1<Boolean, Observable<List<Weather>>>() {
                    @Override
                    public Observable<List<Weather>> call(Boolean result) {
                        return _cities.getData(true).flatMap(getForecast(false));
                    }
                }), false);
            }
        });
    }

    private void composeSubscriptions(Subscription subscription){
        if(_subscription == null)
            _subscription = new CompositeSubscription();

        _subscription.add(subscription);
    }

    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
        _subscription.unsubscribe();
        _subscription = null;
    }

    @Override
    public void attachView(MainListView view) {
        super.attachView(view);
        view.setTitle("Forecast");
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
    protected void onNext(List<Weather> data) {
        super.onNext(data);
        Log.d("TAG-12", "onNext");
    }

    @Override
    protected void onCompleted() {
        Log.d("TAG-12", "onCompleted");
        super.onCompleted();

        if(_updateSubscription != null)
            _updateSubscription.unsubscribe();

        _updateSubscription = Observable.just(null).delay(SettingsManager.instance().getUpdatePeriod(), TimeUnit.SECONDS).subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                loadForecast(true);
            }
        });
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
        subscribe(_cities.saveData(item).flatMap(new Func1<Boolean, Observable<List<Weather>>>() {
            @Override
            public Observable<List<Weather>> call(Boolean aVoid) {
                return _cities.getData(true).flatMap(getForecast(false));
            }
        }), false);
    }
}
