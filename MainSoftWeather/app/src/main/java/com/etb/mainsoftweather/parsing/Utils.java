package com.etb.mainsoftweather.parsing;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Created by etb on 04.04.16.
 */
public class Utils {
    public static String getString(JsonObject obj, String key){
        JsonElement element = obj.get(key);
        if(element != null)
            return element.getAsString();
        else
            return null;
    }

    public static Integer getInteger(JsonObject object, String key){
        JsonElement element = object.get(key);
        if(element != null)
            return element.getAsInt();
        else
            return null;
    }

    public static Float getFloat(JsonObject jsonObject, String key){
        JsonElement element = jsonObject.get(key);
        if(element != null)
            return element.getAsFloat();
        else
            return null;
    }

    public static Double getDouble(JsonObject jsonObject, String key){
        JsonElement element = jsonObject.get(key);
        if(element != null)
            return element.getAsDouble();
        else
            return null;
    }

    public static Long getLong(JsonObject jsonObject, String key){
        JsonElement element = jsonObject.get(key);
        if(element != null)
            return element.getAsLong();
        else
            return null;
    }


}
