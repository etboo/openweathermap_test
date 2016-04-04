package com.etb.mainsoftweather.sources;

import java.util.List;

import rx.Observable;

/**
 * Created by etb on 02.04.16.
 */
public interface DAO<T, P> {

    Observable<List<T>> getAllData();

    Observable<List<T>> find(P ... params);

    void saveData(List<T> data);

    void remoteData(List<T> data);
}
