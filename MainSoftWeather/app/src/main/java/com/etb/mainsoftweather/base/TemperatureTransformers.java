package com.etb.mainsoftweather.base;

/**
 * Created by etb on 04.04.16.
 */
public class TemperatureTransformers {

    public interface AbsTrasformer {

        public float transform(float in);

        public String symbol();
    }

    public static AbsTrasformer CELSIUS = new  AbsTrasformer(){
        @Override
        public float transform(float kelvinValue) {
            return kelvinValue - 273.15f;
        }

        @Override
        public String symbol() {
            return "°C";
        }
    };

    public static AbsTrasformer FAHRENHEIT = new  AbsTrasformer(){
        @Override
        public float transform(float kelvinValue) {
            return (kelvinValue * 9)/5 + 459.67f;
        }

        @Override
        public String symbol() {
            return "°F";
        }
    };

    public static AbsTrasformer KELVIN = new  AbsTrasformer(){
        @Override
        public float transform(float kelvinValue) {
            return kelvinValue ;
        }

        @Override
        public String symbol() {
            return "K";
        }
    };

}
