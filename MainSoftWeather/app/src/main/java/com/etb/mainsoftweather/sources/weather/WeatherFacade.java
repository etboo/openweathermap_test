package com.etb.mainsoftweather.sources.weather;

import android.util.Log;

import com.etb.mainsoftweather.base.Utils;
import com.etb.mainsoftweather.model.City;
import com.etb.mainsoftweather.model.Weather;
import com.etb.mainsoftweather.model.WeatherList;
import com.etb.mainsoftweather.sources.DAO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by etb on 02.04.16.
 */
public class WeatherFacade  {

    private WeatherAPI _network;
    private DAO<Weather, City> _db;

    public WeatherFacade(WeatherAPI network, DAO<Weather, City> db){
        _network = network;
        _db = db;
    }

    public Observable<Map<Long, List<Weather>>> getAll(boolean useDB, final String ... cities) {

        Observable<List<Weather>> networks = Observable.concat(Utils.createAndDefer(new Observable.OnSubscribe<Observable<? extends Weather>>() {
            @Override
            public void call(Subscriber<? super Observable<? extends Weather>> subscriber) {
                for(String city : cities)
                    subscriber.onNext(_network.getWeather(city).map(new Func1<WeatherList, Weather>() {
                        @Override
                        public Weather call(WeatherList weatherList) {
                            return weatherList.list[0];
                        }
                    }));

                subscriber.onCompleted();
            }
        })).buffer(cities.length).doOnNext(new Action1<List<Weather>>() {
            @Override
            public void call(List<Weather> forecasts) {
                _db.saveData(forecasts);
            }
        });

        return (useDB? Observable.mergeDelayError( _db.getAllData(), networks) : networks).doOnNext(new Action1<List<Weather>>() {
            @Override
            public void call(List<Weather> forecasts) {
                Log.d("TAG", "onNew");
            }
        }).map(new Func1<List<Weather>, Map<Long, List<Weather>>>() {
            @Override
            public Map<Long, List<Weather>> call(List<Weather> weathers) {

                if(weathers == null || weathers.size() == 0)
                    return Collections.<Long, List<Weather>>emptyMap();

                Map<Long, List<Weather>> result = new HashMap<Long, List<Weather>>();
                for(Weather weather : weathers){
                    if(result.containsKey(weather.id)){
                        result.get(weather.id).add(weather);
                    } else {
                        List<Weather> list = new ArrayList<Weather>();
                        list.add(weather);

                        result.put(weather.id, list);
                    }
                }

                return result;
            }
        });
    }

    public Observable<List<Weather>> getLast(boolean useDB, String ... cities){
        return getAll(useDB, cities).map(new Func1<Map<Long, List<Weather>>, List<Weather>>() {
            @Override
            public List<Weather> call(Map<Long, List<Weather>> longListMap) {

                if(longListMap.isEmpty()){
                    return Collections.<Weather>emptyList();
                }

                List<Weather> result = new ArrayList<Weather>();

                for(List<Weather> weathers : longListMap.values()){
                    result.add(weathers.get(weathers.size() - 1));
                }

                return result;
            }
        });
    }

    public Observable<List<Weather>> findInDb(City city){
        return _db.find(city);
    }

    public Observable<Boolean> removeAllWith(City city) {
        return _db.find(city).flatMap(new Func1<List<Weather>, Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call(List<Weather> weathers) {
                _db.remoteData(weathers);
                return Observable.just(true);
            }
        });
    }

}
