package com.fhv.weatherapp.adapters;
;
import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fhv.weatherapp.R;
import com.fhv.weatherapp.model.DailyWeather;
import com.fhv.weatherapp.model.Details;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DailyWeatherListAdapter extends RecyclerView.Adapter<DailyWeatherListAdapter.MyViewHolder> {

    List<DailyWeather.Entry> dataSet;
    private int lastPosition = -1;
    Context context;
    public DailyWeatherListAdapter(List dailyWeatherList, Context context) {
        setHasStableIds(true);
        this.dataSet = dailyWeatherList;
        this.context = context;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.one_day_view, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int i) {
        DailyWeather.Entry data = dataSet.get(i);
        viewHolder.minMaxTemp.setText(String.valueOf(data.getMaxTemperature() + " \u2103" + " / " + data.getMinTemperature() + " \u2103"));
        viewHolder.day.setText(getStringDay(LocalDate.fromDateFields(new Date(data.getDate() * 1000)).getDayOfWeek()));
        prepareIcon(viewHolder.icon, data.getIcon());
        setAnimation(viewHolder.parent, i);
    }
    @Override
    public int getItemCount() {
        return dataSet.size();
    }
    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView minMaxTemp,day;
        WebView icon;
        LinearLayout parent;
        public MyViewHolder(View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.parent);
            minMaxTemp = itemView.findViewById(R.id.min_max_temp);
            day = itemView.findViewById(R.id.day);
            icon = itemView.findViewById(R.id.daily_weather_icon);
        }
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    private String getStringDay(int day) {
        switch (day) {
            case 1:
                return context.getString(R.string.monday);
            case 2:
                return context.getString(R.string.tuesday);
            case 3:
                return context.getString(R.string.wednesday);
            case 4:
                return context.getString(R.string.thursday);
            case 5:
                return context.getString(R.string.friday);
            case 6:
                return context.getString(R.string.saturday);
            case 7:
                return context.getString(R.string.sunday);
            default:
                return context.getString(R.string.no_day);
        }
    }





    @SuppressLint("SetJavaScriptEnabled")
    private void prepareIcon(WebView icon, final String weatherIconType) {
        icon.getSettings().setJavaScriptEnabled(true);
        icon.setLayerType(View.LAYER_TYPE_SOFTWARE, null);  //disabled hardware acceleration.. strangely, it significantly improves performance
        icon.loadUrl("file:///android_asset/smallWeatherImage.html");
        icon.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                view.loadUrl("javascript:set_icon_type('" + weatherIconType + "')");
            }
        });
    }
}



