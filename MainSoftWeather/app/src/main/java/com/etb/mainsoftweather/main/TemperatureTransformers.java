package com.etb.mainsoftweather.main;

import com.etb.mainsoftweather.main.MainAdapter.TemperatureTrasformer;

/**
 * Created by etb on 04.04.16.
 */
public class TemperatureTransformers {

    public static TemperatureTrasformer CELSIUS = new  TemperatureTrasformer(){
        @Override
        public String transform(float kelvinValue) {
            return round(kelvinValue - 273.15) +  " °C";
        }
    };

    private static String round(double value){
        return String.format("%.02f", value);
    }

    public static TemperatureTrasformer FAHRENHEIT = new  TemperatureTrasformer(){
        @Override
        public String transform(float kelvinValue) {
            return round((kelvinValue * 9)/5 + 459.67) + " °F";
        }
    };

    public static TemperatureTrasformer KELVIN = new  TemperatureTrasformer(){
        @Override
        public String transform(float kelvinValue) {
            return round(kelvinValue) + " K";
        }
    };

}
