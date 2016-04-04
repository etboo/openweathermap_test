package com.etb.mainsoftweather;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import com.etb.mainsoftweather.base.ApplicationComponent;
import com.etb.mainsoftweather.base.DaggerApplicationComponent;
import com.etb.mainsoftweather.base.EmptyActivityLifecycleCallbacks;
import com.etb.mainsoftweather.sources.DBModule;
import com.etb.mainsoftweather.sources.HelperFactory;
import com.etb.mainsoftweather.sources.NetworkModule;

import java.lang.ref.WeakReference;

/**
 * Created by etb on 03.04.16.
 */
public class WeatherApp extends Application {

    private static ApplicationComponent sDI;

    private static WeakReference<Activity> sCurrentActivity;

    @Override
    public void onCreate() {
        super.onCreate();

        registerActivityLifecycleCallbacks(_callbacks);

        HelperFactory.setHelper(getApplicationContext());
        prepareDI();
    }

    public static Activity getsCurrentActivity(){
        if(sCurrentActivity == null || sCurrentActivity.get() == null){
            throw new IllegalStateException("activity isn't created");
        }

        return sCurrentActivity.get();
    }

    public static ApplicationComponent injector(){
        if(sDI == null)
            throw new IllegalStateException("injector isn't prepared");

        return sDI;
    }

    private void prepareDI(){
        sDI = DaggerApplicationComponent.builder().dBModule(new DBModule(this))
                .networkModule(new NetworkModule()).build();
    }

    private ActivityLifecycleCallbacks _callbacks = new EmptyActivityLifecycleCallbacks(){
        @Override
        public void onActivityStarted(Activity activity) {
            sCurrentActivity = new WeakReference<Activity>(activity);
        }

        @Override
        public void onActivityStopped(Activity activity) {
            sCurrentActivity = null;
            PermissionManager.dropHolder();
        }
    };

    @Override
    public void onTerminate() {
        unregisterActivityLifecycleCallbacks(_callbacks);
        HelperFactory.releaseHelper();
        super.onTerminate();
    }
}
