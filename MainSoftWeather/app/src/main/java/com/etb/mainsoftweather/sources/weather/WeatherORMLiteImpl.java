package com.etb.mainsoftweather.sources.weather;

import android.util.Log;

import com.etb.mainsoftweather.base.Utils;
import com.etb.mainsoftweather.model.City;
import com.etb.mainsoftweather.model.Weather;
import com.etb.mainsoftweather.sources.DAO;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by etb on 02.04.16.
 */
public class WeatherORMLiteImpl extends BaseDaoImpl<Weather, Integer> implements DAO<Weather, City> {

    private static final String TAG = "WeatherORM";

    public WeatherORMLiteImpl(ConnectionSource connectionSource, Class<Weather> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    @Override
    public Observable<List<Weather>> getAllData() {
        return Observable.create(new Observable.OnSubscribe<List<Weather>>() {
            @Override
            public void call(Subscriber<? super List<Weather>> subscriber) {
                try {
                    subscriber.onNext(WeatherORMLiteImpl.this.queryForAll());
                    subscriber.onCompleted();
                } catch (SQLException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    @Override
    public Observable<List<Weather>> find(final City... params) {
        return Utils.createAndDefer(new Observable.OnSubscribe<List<Weather>>(){

            @Override
            public void call(Subscriber<? super List<Weather>> subscriber) {
                try {
                    List<Weather> weathers = queryBuilder().where().in("city_id", params).query();
                    subscriber.onNext(weathers);
                    subscriber.onCompleted();
                } catch (SQLException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void saveData(List<Weather> data) {
        for(Weather forecast : data){
            try {
                createOrUpdate(forecast);
            } catch (SQLException e) {
                e.printStackTrace();
                Log.e(TAG, "can't create city with name " + forecast.id);
            }
        }
    }

    @Override
    public void remoteData(List<Weather> data) {

        try {
            delete(data);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, "can't remove data", e);
        }
    }

}
