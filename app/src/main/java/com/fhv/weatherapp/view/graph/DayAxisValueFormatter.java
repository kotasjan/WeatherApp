package com.fhv.weatherapp.view.graph;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DayAxisValueFormatter implements IAxisValueFormatter {
    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, (int) value);
        SimpleDateFormat format = new SimpleDateFormat("HH:00", Locale.getDefault());
        return format.format(calendar.getTime());
    }
}
