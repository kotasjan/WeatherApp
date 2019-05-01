package com.fhv.weatherapp.view.graph;

import android.util.Log;

import com.fhv.weatherapp.model.HourlyWeather;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;

public class ForecastGraphHandler {
    private static final String TAG = "ForecastGraphHandler";
    private BarChart graph;
    private BarData barData;
    private HourlyWeather lastObtainedWeather;
    private GraphState graphState;

    public ForecastGraphHandler(BarChart graph) {
        this.graph = graph;
        this.barData = new BarData();
        this.graphState = GraphState.TEMPERATURE;
        initGraph();
        initAxes();
    }

    public void updateData(HourlyWeather weather) {
        lastObtainedWeather = weather;
        updateData();
    }

    public void changeGraphType() {
        this.graphState = this.graphState.toggle();
        updateData();
    }

    public String getGraphStateAsString() {
        String s = graphState.toString().toLowerCase().replace("_", " ");
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    private void initGraph() {
        graph.setData(barData);
        graph.setFitBars(true);
        graph.setDrawValueAboveBar(true);
        graph.getLegend().setEnabled(false);
        graph.getDescription().setEnabled(false);
    }

    private void initAxes() {
        XAxis xAxis = graph.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new DayAxisValueFormatter());

        YAxis yAxis = graph.getAxisLeft();
        yAxis.setEnabled(false);
        yAxis = graph.getAxisRight();
        yAxis.setEnabled(false);
    }

    private void updateData() {
        BarDataSet dataSet;
        switch (graphState) {
            case TEMPERATURE:
                dataSet = handleTemperature();
                break;
            case RAIN_PROBABILITY:
                dataSet = handleRain();
                break;
            default:
                dataSet = WeatherToBarDataParser.toDataSet(lastObtainedWeather.getTemperatures());
                dataSet.setValueFormatter(new PeriodicIntegerValueFormatter(dataSet));
        }

        removeData();
        barData.addDataSet(dataSet);
        graph.notifyDataSetChanged();
        graph.invalidate();
        Log.d(TAG, "Updated graph series");
    }

    private BarDataSet handleTemperature() {
        BarDataSet dataSet = WeatherToBarDataParser.toDataSet(lastObtainedWeather.getTemperatures());
        dataSet.setValueFormatter(new PeriodicIntegerValueFormatter(dataSet));
        YAxis yAxis = graph.getAxisLeft();
        yAxis.resetAxisMaximum();
        yAxis.resetAxisMinimum();
        return dataSet;
    }

    private BarDataSet handleRain() {
        BarDataSet dataSet = WeatherToBarDataParser.toDataSet(lastObtainedWeather.getPrecipProbabilities());
        dataSet.setValueFormatter(new PeriodicPercentValueFormatter(dataSet));
        YAxis yAxis = graph.getAxisLeft();
        yAxis.setAxisMaximum(1f);
        yAxis.setAxisMinimum(0);
        return dataSet;
    }

    private void removeData() {
        for (int i = 0; i < barData.getDataSetCount(); i++) {
            barData.removeDataSet(0);
        }
    }

    public enum GraphState {
        TEMPERATURE, RAIN_PROBABILITY;

        GraphState toggle() {
            if (this == TEMPERATURE) {
                return RAIN_PROBABILITY;
            }
            return TEMPERATURE;
        }
    }
}
