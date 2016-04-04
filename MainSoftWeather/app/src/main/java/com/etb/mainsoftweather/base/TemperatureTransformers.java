package com.etb.mainsoftweather.base;

/**
 * Created by etb on 04.04.16.
 */
public class TemperatureTransformers {

    public interface AbsTrasformer {
        public String transform(float in);
    }

    public static AbsTrasformer CELSIUS = new  AbsTrasformer(){
        @Override
        public String transform(float kelvinValue) {
            return round(kelvinValue - 273.15) +  " °C";
        }
    };

    public static AbsTrasformer FAHRENHEIT = new  AbsTrasformer(){
        @Override
        public String transform(float kelvinValue) {
            return round((kelvinValue * 9)/5 + 459.67) + " °F";
        }
    };

    public static AbsTrasformer KELVIN = new  AbsTrasformer(){
        @Override
        public String transform(float kelvinValue) {
            return round(kelvinValue) + " K";
        }
    };

    private static String round(double value){
        return String.format("%.02f", value);
    }

}
