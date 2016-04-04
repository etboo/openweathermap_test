package com.etb.mainsoftweather.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.etb.mainsoftweather.model.Weather;
import com.jakewharton.rxbinding.view.RxView;

import java.util.List;
import java.util.Map;
import java.util.Set;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.MainThreadSubscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * Created by etb on 04.04.16.
 */
public abstract class BaseRxRecyclerViewAdapter<VH extends RecyclerView.ViewHolder, D> extends RecyclerView.Adapter<VH> {

    private Context _context;
    private LayoutInflater _inflater;

    private List<D> _data;

    private Subscriber<? super D> _clicksSubscriber;
    private Subscriber<? super D> _longClicksSubscriber;

    public BaseRxRecyclerViewAdapter(Context context){
        _context = context;
        _inflater = LayoutInflater.from(_context);
    }

    protected Context getContext(){
        return _context;
    }

    public void updateData(List<D> data){
        _data = data;
    }

    public Observable<D> clicks(){
        return Observable.create(new Observable.OnSubscribe<D>() {
            @Override
            public void call(Subscriber<? super D> subscriber) {
                _clicksSubscriber = subscriber;
                _clicksSubscriber.add(new MainThreadSubscription() {
                    @Override
                    protected void onUnsubscribe() {
                        _clicksSubscriber = null;
                    }
                });

                notifyDataSetChanged();
            }
        });
    }

    @Override
    public final int getItemCount() {
        return _data == null ? 0 : _data.size();
    }

    public final List<D> getItems(){
        return _data;
    }

    public Observable<D> longClicks(){
        return Observable.create(new Observable.OnSubscribe<D>() {
            @Override
            public void call(Subscriber<? super D> subscriber) {
                _longClicksSubscriber = subscriber;
                _longClicksSubscriber.add(new MainThreadSubscription() {
                    @Override
                    protected void onUnsubscribe() {
                        _longClicksSubscriber = null;
                    }
                });

                notifyDataSetChanged();
            }
        });
    }

    @Override
    public final VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflateCustomView(_inflater, parent, viewType);
        return createCustomViewHolder(view);
    }

    protected abstract View inflateCustomView(LayoutInflater inflater, ViewGroup parent, int viewType);

    protected abstract VH createCustomViewHolder(View view);

    protected abstract void bindCustomHolder(VH holder, D data, int position);

    @Override
    public void onViewRecycled(VH holder) {
        super.onViewRecycled(holder);
    }

    @Override
    public final void onBindViewHolder(VH holder, final int position) {
        bindCustomHolder(holder, _data.get(position), position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(_clicksSubscriber != null && !_clicksSubscriber.isUnsubscribed())
                    _clicksSubscriber.onNext(_data.get(position));
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener(){

            @Override
            public boolean onLongClick(View v) {
                if(_longClicksSubscriber != null && !_longClicksSubscriber.isUnsubscribed())
                    _longClicksSubscriber.onNext(_data.get(position));
                return true;
            }
        });

    }

}
