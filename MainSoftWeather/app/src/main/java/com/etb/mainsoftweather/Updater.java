package com.etb.mainsoftweather;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

/**
 * Created by etb on 05.04.16.
 */
public class Updater {

    private static final String TAG = SettingsManager.class.getSimpleName();
    private static final String UPDATER =  TAG + ".UPDATER";

    private static final String UPDATED_KEY =  TAG + ".LAST_UPDATE";

    private Context _context;
    private  OnSubscribeTimer _timerObservable;

    public Updater(Context context){
        _context = context;
    }

    private SharedPreferences getPrefs(){
        return _context.getSharedPreferences(UPDATER, Context.MODE_PRIVATE);
    }

    public Observable<Boolean> updates(){
        if(_timerObservable == null)
            _timerObservable = new OnSubscribeTimer(Schedulers.io());
        return Observable.create(_timerObservable);
    }

    public void done(){
        getPrefs().edit().putLong(UPDATED_KEY, System.currentTimeMillis()).commit();
        _timerObservable.emitAfter(SettingsManager.instance().getUpdatePeriod());
    }

    private long getDelay(){
        return (System.currentTimeMillis()  - getPrefs().getLong(UPDATED_KEY, 0)) / 1000;
    }

    private class OnSubscribeTimer implements Observable.OnSubscribe<Boolean>{

        private Scheduler __scheduler;
        private Subscriber<? super Boolean> __subscriber;
        private Scheduler.Worker __worker;

        private AtomicBoolean __key = new AtomicBoolean();

        public OnSubscribeTimer(Scheduler scheduler){
            __scheduler = scheduler;
        }

        public void emitAfter(long delay){
            __worker.schedule(new Action0() {
                @Override
                public void call() {
                    synchronized (__key){
                        if(__subscriber != null && !__subscriber.isUnsubscribed()) {
                            __subscriber.onNext(true);
                        }
                    }
                }
            }, delay, TimeUnit.SECONDS);
        }

        @Override
        public void call(final Subscriber<? super Boolean> subscriber) {
            __worker = __scheduler.createWorker();
            __subscriber = subscriber;
            subscriber.add(__worker);

            long firstDelay = getDelay();
            if(firstDelay > SettingsManager.instance().getUpdatePeriod()){
                subscriber.onNext(true);
            } else {
                emitAfter(SettingsManager.instance().getUpdatePeriod() - firstDelay);
            }

        }
    }


}
