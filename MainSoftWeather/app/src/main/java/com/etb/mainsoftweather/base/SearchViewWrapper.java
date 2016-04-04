package com.etb.mainsoftweather.base;

import android.app.SearchManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.provider.BaseColumns;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.SearchView;
import android.util.Log;

import com.etb.mainsoftweather.model.City;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by etb on 03.04.16.
 */
public class SearchViewWrapper {

    private static final String TAG = SearchViewWrapper.class.getSimpleName();

    public interface OnSuggestionListener {
        public void onSuggestionClick(City item);
    }

    public interface SuggestionProvider {
        public Observable<List<City>> queryTextChanged(String text);
    }

    private SearchView _view;

    private SuggestionProvider _provider;
    private Subscription _subscription;

    private List<City> _suggestions;

    private OnSuggestionListener _clickListener;

    public SearchViewWrapper(SearchView view){
        _view = view;
        _suggestions = new ArrayList<>();
        autoUnsubscribe();

        setupSearchView();
    }

    private void autoUnsubscribe(){
        Observable.create(new AttachEventOnSubscribe(_view)).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                if(aBoolean)
                    return;

                unsubscribe();
            }
        });
    }

    public void dropView(){
        unsubscribe();
    }

    private void unsubscribe(){
        if(_subscription != null)
            _subscription.unsubscribe();
    }

    private void setupSearchView(){
        final CursorAdapter suggestionAdapter = new SimpleCursorAdapter(_view.getContext(),
                android.R.layout.simple_list_item_1,
                null,
                new String[]{SearchManager.SUGGEST_COLUMN_TEXT_1},
                new int[]{android.R.id.text1},
                0);

        _view.setSuggestionsAdapter(suggestionAdapter);
        _view.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                _view.setQuery(_suggestions.get(position).name, false);
                _view.clearFocus();

                if(_clickListener != null)
                    _clickListener.onSuggestionClick(_suggestions.get(position));

                return true;
            }
        });


        _view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                unsubscribe();

                _subscription = _provider.queryTextChanged(newText).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List<City>>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError", e);
                    }

                    @Override
                    public void onNext(List<City> ts) {
                        Log.d(TAG, "onNext");
                        _suggestions.clear();
                        _suggestions.addAll(ts);

                        suggestionAdapter.swapCursor(getMatrixCursorFrom(_suggestions));
                    }
                });

                return false;
            }
        });

    }

    private static Cursor getMatrixCursorFrom(List<City> suggestions){
        String[] columns = {
                BaseColumns._ID,
                SearchManager.SUGGEST_COLUMN_TEXT_1,
                SearchManager.SUGGEST_COLUMN_INTENT_DATA
        };

        MatrixCursor cursor = new MatrixCursor(columns);

        for (int i = 0; i < suggestions.size(); i++) {
            String[] tmp = {Integer.toString(i), suggestions.get(i).name, suggestions.get(i).name};
            cursor.addRow(tmp);
        }

        return cursor;
    }

    public void setClickListener(OnSuggestionListener listener){
        _clickListener = listener;
    }

    public void setProvider(SuggestionProvider provider){
        _provider = provider;

    }
}
