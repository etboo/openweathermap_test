package com.etb.mainsoftweather;

import android.content.Context;
import android.content.SharedPreferences;

import com.etb.mainsoftweather.base.TemperatureTransformer;

/**
 * Created by etb on 05.04.16.
 */
public class SettingsManager {

    private static final String TAG = SettingsManager.class.getSimpleName();
    private static final String SETTINGS =  TAG + ".SETTINGS";

    private static final String TRANSFORMER_KEY = TAG + ".TRANSFORMER";
    private static final String PERIOD_KEY = TAG + ".UPDATE_PERIOD";

    public static SettingsManager sInstance;

    public static SettingsManager instance(){
        if(sInstance == null ){
            sInstance = new SettingsManager(WeatherApp.getsCurrentActivity().getApplicationContext());
        }

        return sInstance;
    }

    private Context _context;

    private SettingsManager(Context context){
        _context = context;
    }

    private SharedPreferences getPrefs(){
        return _context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
    }

    public TemperatureTransformer getTransformer(){
        String name = getPrefs().getString(TRANSFORMER_KEY, null);

        if("celsius".equalsIgnoreCase(name)){
            return TemperatureTransformer.CELSIUS;
        } else if ("fahrenheit".equalsIgnoreCase(name)){
            return TemperatureTransformer.FAHRENHEIT;
        } else if("kelvin".equalsIgnoreCase(name)){
            return TemperatureTransformer.KELVIN;
        } else
            return null;

    }

    public void setTemperatureFormat(String transformer){
        getPrefs().edit().putString(TRANSFORMER_KEY, transformer).commit();
    }

    public void setUpdatePeriod(int sec){
        getPrefs().edit().putInt(PERIOD_KEY, sec).commit();
    }

    public int getUpdatePeriod(){
        return getPrefs().getInt(PERIOD_KEY, BuildConfig.DEFAULT_UPDATE_PERIOD);
    }
}
