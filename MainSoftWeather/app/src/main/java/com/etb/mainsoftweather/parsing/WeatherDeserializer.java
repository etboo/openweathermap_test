package com.etb.mainsoftweather.parsing;

import com.etb.mainsoftweather.model.City;
import com.etb.mainsoftweather.model.Weather;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import static com.etb.mainsoftweather.parsing.Utils.getFloat;
import static com.etb.mainsoftweather.parsing.Utils.getInteger;
import static com.etb.mainsoftweather.parsing.Utils.getString;

/**
 * Created by etb on 04.04.16.
 */
public class WeatherDeserializer implements JsonDeserializer<Weather> {

    @Override
    public Weather deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = (JsonObject) json;

        Weather result = new Weather();

        result.id = getInteger(object, "id");
        result.city = getCity(object);

        result.dt = Utils.getLong(object, "dt");

        parseMain(object.getAsJsonObject("main"), result);
        parseWind(object.getAsJsonObject("wind"), result);
        parseClouds(object.getAsJsonObject("clouds"), result);

        result.millis = System.currentTimeMillis();

        return result;
    }

    private void parseWind(JsonObject wind, Weather result){
        if(wind == null)
            return;

        result.wind_deg = Utils.getFloat(wind, "deg");
        result.wind_speed = Utils.getFloat(wind, "speed");
    }

    private void parseClouds(JsonObject clouds, Weather result){
        if(clouds == null)
            return;

        result.clouds = Utils.getInteger(clouds, "all");
    }

    private void parseMain(JsonObject main, Weather result){
        if(main == null)
            return;

        result.temp = Utils.getFloat(main, "temp");
        result.humidity = Utils.getInteger(main, "humidity");
        result.pressure = Utils.getFloat(main, "pressure");
        result.temp_max = Utils.getFloat(main, "temp_max");
        result.temp_min = Utils.getFloat(main, "temp_min");

    }

    private static City getCity(JsonObject object){
        City result = new City();

        result.name = Utils.getString(object, "name");
        JsonObject coord = object.getAsJsonObject("coord");
        result.lat = getFloat(coord, "lat");
        result.lon = getFloat(coord, "lon");

        JsonObject sys = object.getAsJsonObject("sys");
        result.country = getString(sys, "country");

        return result;

    }


}
