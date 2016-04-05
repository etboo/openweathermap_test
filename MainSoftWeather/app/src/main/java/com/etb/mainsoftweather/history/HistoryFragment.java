package com.etb.mainsoftweather.history;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.etb.mainsoftweather.MainActivity;
import com.etb.mainsoftweather.R;
import com.etb.mainsoftweather.WeatherApp;
import com.etb.mainsoftweather.base.SearchViewWrapper;
import com.etb.mainsoftweather.base.TemperatureTransformers;
import com.etb.mainsoftweather.model.City;
import com.etb.mainsoftweather.model.Weather;
import com.etb.mainsoftweather.sources.WeatherModule;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.LceViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.MvpLceViewStateFragment;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.data.RetainingLceViewState;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;

/**
 * Created by etb on 02.04.16.
 */
public class HistoryFragment extends MvpLceViewStateFragment<RecyclerView, List<Weather>, HistoryListView, HistoryPresenter> implements SwipeRefreshLayout.OnRefreshListener, HistoryListView {

    private static final String CITY_KEY = HistoryFragment.class.getSimpleName() + ".City";

    public static HistoryFragment newInstance(City city){
        HistoryFragment result = new HistoryFragment();
        Bundle args = new Bundle();
        args.putParcelable(CITY_KEY, city);

        result.setArguments(args);
        return result;
    }

    HistoryComponent _di;

    @Bind(R.id.contentView)
    RecyclerView recyclerView;

    private HistoryAdapter _adapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inject();
        setHasOptionsMenu(true);

        return inflater.inflate(R.layout.fragment_history, null, false);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        setupRecyclerView();
    }


    private void setupRecyclerView(){
        _adapter = new HistoryAdapter(getContext());
        _adapter.setTemperatureTransformer(TemperatureTransformers.CELSIUS);

        recyclerView.setAdapter(_adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        ButterKnife.unbind(this);
        _di = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    private void inject(){
        _di = WeatherApp.injector().plus(new WeatherModule(getContext()));

        _di.inject(this);
    }

    @Override
    public LceViewState<List<Weather>, HistoryListView> createViewState() {
        return  new RetainingLceViewState<>();
    }

    @Override
    public List<Weather> getData() {
        return _adapter.getItems();
    }

    @Override
    protected String getErrorMessage(Throwable e, boolean pullToRefresh) {
        return null;
    }

    @Override
    public HistoryPresenter createPresenter() {
        HistoryPresenter presenter =  _di.presenter();
        //TODO: put city in constructor
        presenter.injectCity((City) getArguments().getParcelable(CITY_KEY));
        return presenter;
    }

    @Override
    public void setData(List<Weather> data) {
        _adapter.updateData(data);
        _adapter.notifyDataSetChanged();
    }

    @Override public void showError(Throwable e, boolean pullToRefresh) {
        super.showError(e, pullToRefresh);
        e.printStackTrace();
    }



    @Override
    public void loadData(boolean pullToRefresh) {
        presenter.loadForecast();
    }

    @Override
    public void onRefresh() {
        loadData(true);
    }

    @Override
    public void setTitle(String title) {
        ((MainActivity)getActivity()).setTitle(title);
    }
}
