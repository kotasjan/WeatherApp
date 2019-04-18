package com.fhv.weatherapp.adapters;
;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fhv.weatherapp.R;
import com.fhv.weatherapp.model.Details;

import java.util.ArrayList;

public class DetailsListAdapter extends ArrayAdapter<Details> {

    private ArrayList<Details> dataSet;
    Context mContext;

    private static class ViewHolder {
        TextView detail;
        TextView info;
    }

    public DetailsListAdapter(ArrayList<Details> data, Context context) {
        super(context, R.layout.row_item_details, data);
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
        Details dataModel = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item_details, parent, false);
            viewHolder.detail = (TextView) convertView.findViewById(R.id.detail);
            viewHolder.info = (TextView) convertView.findViewById(R.id.info);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        lastPosition = position;

        viewHolder.detail.setText(dataModel.getDetail());
        viewHolder.info.setText(dataModel.getInfo());
        return convertView;
    }
}



