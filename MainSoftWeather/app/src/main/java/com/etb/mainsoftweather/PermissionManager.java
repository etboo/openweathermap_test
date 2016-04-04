package com.etb.mainsoftweather;

import android.util.Log;

import com.etb.mainsoftweather.base.Utils;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;

/**
 * Created by etb on 03.04.16.
 */
public class PermissionManager {

    public static final String TAG = PermissionManager.class.getSimpleName();

    public static interface PermissionsHolder{
        public void showPermissionDialog(String permission, int permissionId);
    }

    public static PermissionManager sInstance;

    public static PermissionManager instance(){
        if(sInstance == null || sInstance._holder == null){
            if(!(WeatherApp.getsCurrentActivity() instanceof PermissionsHolder))
                throw new IllegalStateException("current activity doesn't provide permissions");
            sInstance = new PermissionManager((PermissionsHolder) WeatherApp.getsCurrentActivity());
        }

        return sInstance;
    }

    private PermissionsHolder _holder;
    private Map<Integer, WeakReference<Subscriber<? super Boolean>>> _subscriberRefs;

    private PermissionManager(PermissionsHolder holder){
        _holder = holder;
    }

    static void dropHolder(){
        if(sInstance == null)
            return;

        sInstance._holder = null;

    }

    private void subscribe(int permissionId, Subscriber<? super Boolean> subscriber){
        if(_subscriberRefs == null)
            _subscriberRefs = new HashMap<>();

        _subscriberRefs.put(permissionId, new WeakReference<Subscriber<? super Boolean>>(subscriber));
    }

    public Observable<Boolean> requestPermission(final String permission, final int permissionId){
        if(Utils.checkPermissions(WeatherApp.getsCurrentActivity(), permission))
            return Observable.just(true);
        else {
            return Observable.create(new Observable.OnSubscribe<Boolean>() {
                @Override
                public void call(Subscriber<? super Boolean> subscriber) {
                    subscribe(permissionId, subscriber);
                    _holder.showPermissionDialog(permission, permissionId);
                }
            }).timeout(30, TimeUnit.SECONDS).doOnError(new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    _subscriberRefs.remove(permissionId);
                }
            });
        }
    }

    void notify(int permissionId, boolean granted){
        if(! _subscriberRefs.containsKey(permissionId))
            return;

        Subscriber<? super Boolean> subscriber = _subscriberRefs.get(permissionId).get();

        if(subscriber == null) {
            Log.v(TAG, "can't find subscriber for permissionId =" + permissionId);
            return;
        }

        subscriber.onNext(granted);
        subscriber.onCompleted();

        _subscriberRefs.remove(permissionId);

    }


}
