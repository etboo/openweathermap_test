package com.etb.mainsoftweather.base;

/**
 * Created by etb on 04.04.16.
 */
public interface TemperatureTransformer {

        public float transform(float in);

        public String symbol();

    public static TemperatureTransformer CELSIUS = new  TemperatureTransformer(){
        @Override
        public float transform(float kelvinValue) {
            return kelvinValue - 273.15f;
        }

        @Override
        public String symbol() {
            return "°C";
        }
    };

    public static TemperatureTransformer FAHRENHEIT = new  TemperatureTransformer(){
        @Override
        public float transform(float kelvinValue) {
            return (kelvinValue * 9)/5 + 459.67f;
        }

        @Override
        public String symbol() {
            return "°F";
        }
    };

    public static TemperatureTransformer KELVIN = new  TemperatureTransformer(){
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
