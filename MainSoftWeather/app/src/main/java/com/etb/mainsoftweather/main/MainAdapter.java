package com.etb.mainsoftweather.main;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.etb.mainsoftweather.MaterialColorPicker;
import com.etb.mainsoftweather.R;
import com.etb.mainsoftweather.base.BaseRxRecyclerViewAdapter;
import com.etb.mainsoftweather.base.TemperatureTransformer;
import com.etb.mainsoftweather.model.Weather;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by etb on 02.04.16.
 */
public class MainAdapter extends BaseRxRecyclerViewAdapter<MainAdapter.ViewHolder, Weather> {

    private CityPresentation _presentation;

    public MainAdapter(Context context){
        super(context);
        _presentation = new CityPresentation();
    }

    @Override
    protected View inflateCustomView(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return inflater.inflate(R.layout.item_main, null, false);
    }

    @Override
    protected ViewHolder createCustomViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    protected void bindCustomHolder(ViewHolder holder, Weather forecast, int position) {
        _presentation.bind(forecast, holder);

        int color = MaterialColorPicker.getMaterialColor(getContext());
        holder.infoLayout.setBackgroundColor(color);
        holder.tempLayout.setBackgroundColor(color);
        holder.windLayout.setBackgroundColor(color);
    }

    public void setTemperatureTransformer(TemperatureTransformer temperatureTransformer){
        _presentation = new CityPresentation(temperatureTransformer);
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        @Bind(R.id.mainItem_city)TextView city;
        @Bind(R.id.mainItem_avegare_temperature)TextView temperature;
        @Bind(R.id.mainItem_min_temperature)TextView minTemperature;
        @Bind(R.id.mainItem_max_temperature)TextView maxTemperature;
        @Bind(R.id.mainItem_country)TextView country;
        @Bind(R.id.mainItem_updated)TextView updated;
        @Bind(R.id.mainItem_wind_angle)TextView windAngle;
        @Bind(R.id.mainItem_wind_speed)TextView windSpeed;

        @Bind(R.id.mainItem_infoLayout)ViewGroup infoLayout;
        @Bind(R.id.mainItem_tempLayout)ViewGroup tempLayout;
        @Bind(R.id.mainItem_windLayout)ViewGroup windLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private static class CityPresentation{

        private TemperatureTransformer __transformer;

        private static String dateFormat = "dd-MM-yyyy hh:mm";
        private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);

        private static String[] sDirections = new String[]{"N","NNE","NE","ENE","E","ESE", "SE", "SSE","S","SSW","SW","WSW","W","WNW","NW","NNW"};

        CityPresentation(){}

        CityPresentation(TemperatureTransformer trasformer){
            __transformer = trasformer;
        }

        private static String round(double value){
            return String.format("%.02f", value);
        }

        private String formatTemp(float temp){
            return __transformer == null? Float.toString(temp) :round(__transformer.transform(temp)) + " " + __transformer.symbol();
        }

        private String getDirection(float deg){
            return sDirections[(int) (((deg + ( 360 / 16) / 2) % 360) / (360 / 16))];
        }

        private static String formatDate(long millis){
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(millis);
            return simpleDateFormat.format(calendar.getTime());
        }

        public void bind(Weather weather, ViewHolder holder){
            holder.temperature.setText(formatTemp(weather.temp));
            holder.maxTemperature.setText(formatTemp(weather.temp_max));
            holder.minTemperature.setText(formatTemp(weather.temp_min));
            holder.windAngle.setText(getDirection(weather.wind_deg));
            holder.windSpeed.setText(Float.toString(weather.wind_speed) + " m/sec");
            holder.city.setText(weather.city.name);
            holder.updated.setText(formatDate(weather.millis));
            holder.country.setText(weather.city.country);

        }


    }
}
