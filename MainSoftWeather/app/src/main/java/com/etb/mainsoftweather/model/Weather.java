package com.etb.mainsoftweather.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by etb on 04.04.16.
 */
@DatabaseTable(tableName = "weather")
public class Weather {

    @DatabaseField(id=true, useGetSet=true)
    public String key;

    public String getKey(){
        return id + "/" + millis;
    }

    public void setKey(String key){
        this.key = key;
    }


    @DatabaseField
    public long millis;

    @DatabaseField
    public long id;

    @DatabaseField(canBeNull = false, foreign = true)
    public City city;

    @DatabaseField
    public long dt;

    @DatabaseField
    public int clouds;

    @DatabaseField
    public int humidity;

    @DatabaseField
    public float pressure;

    @DatabaseField
    public float temp_max;

    @DatabaseField
    public int sea_level;

    @DatabaseField
    public float temp_min;

    @DatabaseField
    public float temp;

    @DatabaseField
    public float grnd_level;

    @DatabaseField
    public long sunrise;

    @DatabaseField
    public long sunset;

    @DatabaseField
    public float wind_speed;

    @DatabaseField
    public float wind_deg;

}
