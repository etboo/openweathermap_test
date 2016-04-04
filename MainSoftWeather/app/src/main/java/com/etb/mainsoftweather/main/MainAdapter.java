package com.etb.mainsoftweather.main;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.etb.mainsoftweather.R;
import com.etb.mainsoftweather.model.Weather;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by etb on 02.04.16.
 */
public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private List<Weather> _forecasts;
    private LayoutInflater _inflater;

    public MainAdapter(Context context){
        _inflater = LayoutInflater.from(context);
    }

    public void updateForecast(List<Weather> newOnes){
        _forecasts = newOnes;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = _inflater.inflate(R.layout.item_main, null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Weather forecast = _forecasts.get(position);

        holder.city.setText(forecast.city.name);
        holder.temperature.setText(Float.toString(forecast.temp));

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
        @Bind(R.id.mainItem_icon)TextView icon;
        @Bind(R.id.mainItem_details_field)TextView details;
        @Bind(R.id.mainItem_temperature)TextView temperature;
        @Bind(R.id.mainItem_updated)TextView updated;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
