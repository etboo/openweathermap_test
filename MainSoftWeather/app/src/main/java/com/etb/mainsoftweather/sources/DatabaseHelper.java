package com.etb.mainsoftweather.sources;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.etb.mainsoftweather.model.City;
import com.etb.mainsoftweather.model.Weather;
import com.etb.mainsoftweather.sources.cities.CitiesORMLiteImpl;
import com.etb.mainsoftweather.sources.weather.WeatherORMLiteImpl;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Created by etb on 03.04.16.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String TAG = DatabaseHelper.class.getSimpleName();

    //имя файла базы данных который будет храниться в /data/data/APPNAME/DATABASE_NAME.db
    private static final String DATABASE_NAME ="myappname.db";

    //с каждым увеличением версии, при нахождении в устройстве БД с предыдущей версией будет выполнен метод onUpgrade();
    private static final int DATABASE_VERSION = 1;

    //ссылки на DAO соответсвующие сущностям, хранимым в БД

    public DatabaseHelper(Context context){
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Выполняется, когда файл с БД не найден на устройстве
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource){
        try
        {
            for(Class clazz : dbClasses()){
                TableUtils.createTable(connectionSource, clazz);
            }

        }
        catch (SQLException e){
            Log.e(TAG, "error creating DB " + DATABASE_NAME);
            throw new RuntimeException(e);
        }
    }

    private static Class[] dbClasses(){
        return new Class[]{City.class, Weather.class};
    }

    //Выполняется, когда БД имеет версию отличную от текущей
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVer,
                          int newVer){
        try{
            for(Class clazz : dbClasses()){
                TableUtils.dropTable(connectionSource, clazz, true);
            }

            onCreate(db, connectionSource);
        }
        catch (SQLException e){
            Log.e(TAG,"error upgrading db "+DATABASE_NAME+"from ver "+oldVer);
            throw new RuntimeException(e);
        }
    }

    public CitiesORMLiteImpl createCitiesDAO() throws SQLException{
        return new CitiesORMLiteImpl(getConnectionSource(), City.class);
    }

    public WeatherORMLiteImpl createWeatherDAO() throws SQLException{
        return new WeatherORMLiteImpl(getConnectionSource(), Weather.class);
    }

}
