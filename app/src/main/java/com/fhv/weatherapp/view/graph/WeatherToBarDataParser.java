package com.fhv.weatherapp.view.graph;

import com.annimon.stream.Stream;
import com.fhv.weatherapp.model.HourlyWeather;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.List;

public class WeatherToBarDataParser {
    public static BarDataSet toDataSet(List<HourlyWeather.Entry> entries) {
        List<BarEntry> list = Stream.of(entries)
                .map(entry -> new BarEntry((float) entry.getI(), (float) entry.getValue()))
                .toList();
        return new BarDataSet(list, "BarDataSet");
    }
}
