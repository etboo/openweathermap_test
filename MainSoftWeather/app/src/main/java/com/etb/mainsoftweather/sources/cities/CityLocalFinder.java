package com.etb.mainsoftweather.sources.cities;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;

import com.etb.mainsoftweather.BuildConfig;
import com.etb.mainsoftweather.base.Utils;
import com.etb.mainsoftweather.model.City;
import com.etb.mainsoftweather.model.WeatherList;
import com.etb.mainsoftweather.sources.weather.WeatherAPI;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Created by etb on 02.04.16.
 */
public class CityLocalFinder {

    public static final int MY_PERMISSIONS_ACCESS_FINE_LOCATION = 2543;

    Context _context;
    WeatherAPI _network;

    public CityLocalFinder(Context context, WeatherAPI network) {
        _context = context;
        _network = network;
    }

    public Observable<City> getCityByCurrentLocation(){
        /*return Utils.<Location>createAndDefer(new Observable.OnSubscribe<Location>() {
            @Override
            public void call(Subscriber<? super Location> subscriber) {
                getLocation(subscriber);
            }
        }).timeout(BuildConfig.HANDLE_LOCATION_TIMEOUT, TimeUnit.MILLISECONDS).flatMap(new Func1<Location, Observable<City>>() {
            @Override
            public Observable<City> call(Location location) {
                return _network.getWeatherWithCoords(Double.toString(location.getLatitude()), Double.toString(location.getLongitude())).map(new Func1<WeatherList, City>() {
                    @Override
                    public City call(WeatherList weatherList) {
                        return weatherList.list[0].city;
                    }
                });
            }
        });*/
        return Observable.just(mockCity());

    }

    private static City mockCity(){
        City result = new City();
        result.name = "Minsk";
        result.country = "BEL";

        return result;
    }

    private void getLocation(Subscriber<? super Location> subscriber) {

        LocationManager manager = locationManagerFrom(_context);
        String provider = manager.getBestProvider(buildCriteria(), true);

        Location location = manager.getLastKnownLocation(provider);

        if(!check(location))
            manager.requestSingleUpdate(provider, getListener(_context, subscriber), Looper.getMainLooper());
        else {
            subscriber.onNext(location);
            subscriber.onCompleted();
        }
    }

    private static LocationListener getListener(final Context context, final Subscriber<? super Location> subscriber){
        return new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                locationManagerFrom(context).removeUpdates(this);
                subscriber.onNext(location);
                subscriber.onCompleted();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) { }

            @Override
            public void onProviderEnabled(String provider) { }

            @Override
            public void onProviderDisabled(String provider) { }
        };
    }

    private static LocationManager locationManagerFrom(Context context){
        return (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    private static boolean check(Location location){
        return location != null && ((SystemClock.elapsedRealtime() - location.getTime()) <= BuildConfig.LOCATION_TTL);
    }

    private static Criteria buildCriteria(){
        Criteria searchProviderCriteria = new Criteria();
        searchProviderCriteria.setPowerRequirement(Criteria.POWER_LOW);
        searchProviderCriteria.setAccuracy(Criteria.ACCURACY_COARSE);
        searchProviderCriteria.setCostAllowed(false);
        return searchProviderCriteria;
    }

}
