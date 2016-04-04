package com.etb.mainsoftweather.base;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;

import java.util.List;

import rx.Observable;
import rx.functions.Func0;

/**
 * Created by etb on 02.04.16.
 */
public class Utils {

    public static <T> Observable<T> createAndDefer(final Observable.OnSubscribe<T> body){
        return Observable.defer(new Func0<Observable<T>>() {
            @Override
            public Observable<T> call() {
                return Observable.create(body);
            }
        });
    }

    public static boolean checkPermissions(Context context, String ... permissions){
        for(String permission : permissions){
            if(ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }

        return true;
    }

    public static boolean ensureMainThread(){
        return Looper.myLooper() == Looper.getMainLooper();
    }
}
