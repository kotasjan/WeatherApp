package com.fhv.weatherapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fhv.weatherapp.model.SmallWeather;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<SmallWeather> {

    private ArrayList<SmallWeather> dataSet;
    Context mContext;

    private static class ViewHolder {
        TextView txtLocation;
        TextView txtDegree;
        WebView icon;
    }

    public CustomAdapter(ArrayList<SmallWeather> data, Context context) {
        super(context, R.layout.row_item, data);
        this.dataSet = data;
        this.mContext = context;

    }

    private int lastPosition = -1;

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SmallWeather dataModel = getItem(position);
        ViewHolder viewHolder;
        final View result;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item, parent, false);
            viewHolder.txtLocation = (TextView) convertView.findViewById(R.id.location);
            viewHolder.txtDegree = (TextView) convertView.findViewById(R.id.celcius_degree);
            viewHolder.icon = (WebView) convertView.findViewById(R.id.icon);
            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.txtLocation.setText(dataModel.getLocation());
        viewHolder.txtDegree.setText(dataModel.getDegree());
        prepareIcon(viewHolder.icon, dataModel.getType());
        return convertView;
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


