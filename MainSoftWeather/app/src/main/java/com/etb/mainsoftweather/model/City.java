package com.etb.mainsoftweather.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "cities")
public class City {

    @DatabaseField(id = true)
    public String name;

    @DatabaseField
    public float lat;

    @DatabaseField
    public float lon;

    @DatabaseField
    public String country;

}
