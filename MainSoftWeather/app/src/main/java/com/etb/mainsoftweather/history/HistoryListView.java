package com.etb.mainsoftweather.history;

import com.etb.mainsoftweather.model.City;
import com.etb.mainsoftweather.model.Weather;
import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;

import java.util.List;

import rx.Observable;

/**
 * Created by etb on 02.04.16.
 */
public interface HistoryListView extends MvpLceView<List<Weather>> {

}
