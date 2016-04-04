package com.etb.mainsoftweather.main;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.etb.mainsoftweather.MaterialColorPicker;
import com.etb.mainsoftweather.R;
import com.etb.mainsoftweather.model.Weather;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by etb on 02.04.16.
 */
public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private List<Weather> _forecasts;
    private LayoutInflater _inflater;

    private Context _context;

    private CityPresentation _presentation;

    public MainAdapter(Context context){
        _inflater = LayoutInflater.from(context);
        _presentation = new CityPresentation();
        _context = context;
    }

    public void updateForecast(List<Weather> newOnes){
        _forecasts = newOnes;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = _inflater.inflate(R.layout.item_main, null, false);
        return new ViewHolder(view);
    }

    public void setTemperatureTransformer(TemperatureTrasformer temperatureTransformer){
        _presentation = new CityPresentation(temperatureTransformer);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Weather forecast = _forecasts.get(position);
        _presentation.bind(forecast, holder);

        int color = MaterialColorPicker.getMaterialColor(_context);
        holder.infoLayout.setBackgroundColor(color);
        holder.tempLayout.setBackgroundColor(color);
        holder.windLayout.setBackgroundColor(color);
    }

    public List<Weather> getItems(){
        return _forecasts;
    }

    @Override
    public int getItemCount() {
        return _forecasts == null? 0 : _forecasts.size();
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

    public interface TemperatureTrasformer{
        public String transform(float in);
    }

    private static class CityPresentation{

        private TemperatureTrasformer __transformer;

        private static String dateFormat = "dd-MM-yyyy hh:mm";
        private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);

        private static String[] sDirections = new String[]{"N","NNE","NE","ENE","E","ESE", "SE", "SSE","S","SSW","SW","WSW","W","WNW","NW","NNW"};

        CityPresentation(){}

        CityPresentation(TemperatureTrasformer trasformer){
            __transformer = trasformer;
        }

        private String formatTemp(float temp){
            return __transformer == null? Float.toString(temp) : __transformer.transform(temp);
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
