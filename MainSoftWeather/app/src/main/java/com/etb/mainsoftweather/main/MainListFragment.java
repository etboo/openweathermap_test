package com.etb.mainsoftweather.main;

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
import com.etb.mainsoftweather.Navigator;
import com.etb.mainsoftweather.R;
import com.etb.mainsoftweather.WeatherApp;
import com.etb.mainsoftweather.base.SearchViewWrapper;
import com.etb.mainsoftweather.base.TemperatureTransformers;
import com.etb.mainsoftweather.history.HistoryFragment;
import com.etb.mainsoftweather.model.City;
import com.etb.mainsoftweather.model.Weather;
import com.etb.mainsoftweather.sources.CitiesModule;
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
public class MainListFragment extends MvpLceViewStateFragment<SwipeRefreshLayout, List<Weather>, MainListView, MainListPresenter> implements SwipeRefreshLayout.OnRefreshListener, MainListView {

    MainComponent _di;

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;

    private MainAdapter _adapter;
    private SearchViewWrapper _searchWrapper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inject();
        setHasOptionsMenu(true);

        return inflater.inflate(R.layout.fragment_main, null, false);
    }

    @Override
    public void onStop() {
        super.onStop();
        _searchWrapper.dropView();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        setupRecyclerView();
        presenter.setupFlows(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_city_picker, menu);

        setupSearchView(menu);
    }

    private void setupRecyclerView(){
        _adapter = new MainAdapter(getContext());
        _adapter.setTemperatureTransformer(TemperatureTransformers.CELSIUS);

        recyclerView.setAdapter(_adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        contentView.setOnRefreshListener(this);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        ButterKnife.unbind(this);
        _di = null;
    }

    private void setupSearchView(Menu menu){
        MenuItem mSearchMenuItem = menu.findItem(R.id.action_search);
        _searchWrapper = new SearchViewWrapper((SearchView) MenuItemCompat.getActionView(mSearchMenuItem));
        _searchWrapper.setProvider(presenter);
        _searchWrapper.setClickListener(presenter);
    }

    private Navigator navigator(){
        return (Navigator) getActivity();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    private void inject(){
        _di = WeatherApp.injector().plus(new WeatherModule(getContext()), new CitiesModule(getContext()));
        _di.inject(this);
    }

    @Override
    public LceViewState<List<Weather>, MainListView> createViewState() {
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
    public MainListPresenter createPresenter() {
        return _di.presenter();
    }

    @Override
    public void setData(List<Weather> data) {
        _adapter.updateData(data);
        _adapter.notifyDataSetChanged();
    }

    @Override
    public void showContent() {
        super.showContent();
        contentView.setRefreshing(false);
    }

    @Override public void showLoading(boolean pullToRefresh) {
        super.showLoading(pullToRefresh);
        if (pullToRefresh && !contentView.isRefreshing()) {
            contentView.post(new Runnable() {
                @Override public void run() {
                    contentView.setRefreshing(true);
                }
            });
        }
    }

    @Override public void showError(Throwable e, boolean pullToRefresh) {
        super.showError(e, pullToRefresh);
        contentView.setRefreshing(false);
        e.printStackTrace();
    }



    @Override
    public void loadData(boolean pullToRefresh) {
        presenter.loadForecast(pullToRefresh);
    }

    @Override
    public void onRefresh() {
        loadData(true);
    }

    @Override
    public Observable<Weather> listLongClicks() {
        return _adapter.longClicks();
    }

    @Override
    public Observable<Weather> listClicks() {
        return _adapter.clicks();
    }

    @Override
    public void navigateToForecast(City city) {
        navigator().goTo(HistoryFragment.newInstance(city));
    }

    @Override
    public void setTitle(String title) {
        ((MainActivity)getActivity()).setTitle(title);
    }
}
