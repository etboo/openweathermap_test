package com.etb.mainsoftweather.sources.cities;

import android.util.Log;

import com.etb.mainsoftweather.model.City;
import com.etb.mainsoftweather.sources.DAO;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by etb on 02.04.16.
 */
public class CitiesORMLiteImpl extends BaseDaoImpl<City, Integer> implements DAO<City, String> {

    private static final String TAG = "CitiesORM";

    public CitiesORMLiteImpl(ConnectionSource connectionSource, Class<City> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    @Override
    public Observable<List<City>> getAllData() {
        return Observable.create(new Observable.OnSubscribe<List<City>>() {
            @Override
            public void call(Subscriber<? super List<City>> subscriber) {
                try {
                    subscriber.onNext(CitiesORMLiteImpl.this.queryForAll());
                    subscriber.onCompleted();
                } catch (SQLException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    @Override
    public Observable<List<City>> find(final String... params) {
        if(params == null || params.length == 0)
            throw new IllegalArgumentException();

        return Observable.create(new Observable.OnSubscribe<List<City>>() {
            @Override
            public void call(Subscriber<? super List<City>> subscriber) {
                try {
                    subscriber.onNext(queryBuilder().where().in("name", params).query());
                    subscriber.onCompleted();
                } catch (SQLException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    @Override
    public void saveData(List<City> data) {
        for(City city : data){
            try {
                createOrUpdate(city);
            } catch (SQLException e) {
                e.printStackTrace();
                Log.e(TAG, "can't create city with name " + city.name);
            }
        }
    }

    @Override
    public void remoteData(List<City> data) {
        try {
            delete(data);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, "can't remove data", e);
        }
    }
}
