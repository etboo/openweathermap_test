package com.etb.mainsoftweather.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "cities")
public class City implements Parcelable{

    @DatabaseField(id = true)
    public String name;

    @DatabaseField
    public float lat;

    @DatabaseField
    public float lon;

    @DatabaseField
    public String country;

    @Override
    public int describeContents() {
        return 0;
    }

    public City(){}

    public City(Parcel parcel){
        name = parcel.readString();
        lat = parcel.readFloat();
        lon = parcel.readFloat();
        country = parcel.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeFloat(lat);
        dest.writeFloat(lon);
        dest.writeString(country);
    }

    public Parcelable.Creator<City> CREATOR
            = new Parcelable.Creator<City>() {
        public City createFromParcel(Parcel in) {
            return new City(in);
        }

        public City[] newArray(int size) {
            return new City[size];
        }
    };
}
