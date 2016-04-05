package com.etb.mainsoftweather.history;

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
public class HistoryAdapter extends BaseRxRecyclerViewAdapter<HistoryAdapter.ViewHolder, Weather> {

    private CityPresentation _presentation;

    public HistoryAdapter(Context context){
        super(context);
        _presentation = new CityPresentation();
    }

    @Override
    protected View inflateCustomView(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return inflater.inflate(R.layout.item_history, null, false);
    }

    @Override
    protected ViewHolder createCustomViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    protected void bindCustomHolder(ViewHolder holder, Weather forecast, int position) {
        _presentation.bind(forecast, holder, position == 0 ? null : getItems().get(position - 1).temp);

    }

    private static int getColor(Context context, Float diff){
        if(diff == null)
            return MaterialColorPicker.getColor(context, R.color.material_green);
        else if(diff <= 0)
            return MaterialColorPicker.getColor(context, R.color.material_blue);
        else
            return MaterialColorPicker.getColor(context, R.color.material_red);

    }

    public void setTemperatureTransformer(TemperatureTransformer temperatureTransformer){
        _presentation = new CityPresentation(temperatureTransformer);
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        @Bind(R.id.historyItem_avegare_temperature)TextView temperature;
        @Bind(R.id.historyItem_temperature_diff)TextView diff;
        @Bind(R.id.historyItem_updated)TextView updated;
        @Bind(R.id.historyItem_wind_angle)TextView windAngle;
        @Bind(R.id.historyItem_wind_speed)TextView windSpeed;
        @Bind(R.id.historyItem_content)ViewGroup layout;

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
            return String.format("%.01f", value);
        }

        private String formatTemp(float temp){
            return __transformer == null? Float.toString(temp) :round(__transformer.transform(temp)) + " " + __transformer.symbol();
        }

        private float getDiff(float current, float prev){
            if(__transformer == null)
                return current - prev;
            else
                return __transformer.transform(current) - __transformer.transform(prev);
        }

        private String getDirection(float deg){
            return sDirections[(int) (((deg + ( 360 / 16) / 2) % 360) / (360 / 16))];
        }

        private static String formatDate(long millis){
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(millis);
            return simpleDateFormat.format(calendar.getTime());
        }

        public void bind(Weather weather, ViewHolder holder, Float prev){

            holder.diff.setVisibility(prev != null? View.VISIBLE : View.GONE);

            if(prev != null) {
                float diff = getDiff(weather.temp, prev);
                holder.layout.setBackgroundColor(getColor(holder.itemView.getContext(), diff));
                String text = (diff > 0 ? "+" : "") + round(diff);
                holder.diff.setText(text + " " + (__transformer != null ? __transformer.symbol() : ""));
            }

            holder.temperature.setText(formatTemp(weather.temp));
            holder.windAngle.setText(getDirection(weather.wind_deg));
            holder.windSpeed.setText(Float.toString(weather.wind_speed) + " m/sec");
            holder.updated.setText(formatDate(weather.millis));

        }


    }
}
