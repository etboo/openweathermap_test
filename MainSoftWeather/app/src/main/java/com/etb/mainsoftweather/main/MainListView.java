package com.etb.mainsoftweather.main;

import android.content.Context;

import com.etb.mainsoftweather.model.City;
import com.etb.mainsoftweather.model.Weather;
import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;

import java.util.List;

import rx.Observable;

/**
 * Created by etb on 02.04.16.
 */
public interface MainListView extends MvpLceView<List<Weather>> {

    public Observable<Weather> listLongClicks();

    public Observable<Weather> listClicks();

    public void navigateToForecast(City city);

    public void setTitle(String title);

    public void showToast(String text);

    public Context getContext();

}
