package com.fhv.weatherapp.view.graph;

import android.util.Log;

import com.annimon.stream.Stream;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class PeriodicPercentValueFormatter implements IValueFormatter {
    private DecimalFormat decimalFormat = new DecimalFormat("##%");
    private List<Integer> indexesToShow;

    public PeriodicPercentValueFormatter(BarDataSet dataSet) {
        super();
        indexesToShow = new IndexCalculator(dataSet).calculateIndexesToShow();
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        if (indexesToShow.contains((int) entry.getX())) {
            return decimalFormat.format(value);
        } else {
            return "";
        }
    }

    private class IndexCalculator {
        private BarDataSet dataSet;
        private static final String TAG = "PPVF.IndexCalculator";
        private int indexOfMaxValue;

        private IndexCalculator(BarDataSet dataSet) {
            this.dataSet = dataSet;
            indexesToShow = new ArrayList<>();
        }

        private List<Integer> calculateIndexesToShow() {
            putIndexOfMaxValue();
            putIndexesWithSimilarValues();
            return indexesToShow;
        }

        private void putIndexOfMaxValue() {
            BarEntry max = Stream.of(dataSet.getValues()).max((e1, e2) -> Float.compare(e1.getY(), e2.getY())).get();
            indexOfMaxValue = (int) max.getX();
            indexesToShow.add(indexOfMaxValue);
            Log.d(TAG, "Found max value " + max.getY() + " with index " + indexOfMaxValue);
        }

        private void putIndexesWithSimilarValues() {
            for (int i = 1; i < dataSet.getValues().size() - 1; i++) {
                float previous = dataSet.getEntryForIndex(i - 1).getY();
                float current = dataSet.getEntryForIndex(i).getY();
                float next = dataSet.getEntryForIndex(i + 1).getY();
                if (isInBounds(previous, current, next) && !isNearAnotherOne(i)) {
                    indexesToShow.add(i);
                    Log.d(TAG, "Added index " + i + "; values(previous,current,next): " + previous + current + next);
                }
            }
        }

        private boolean isInBounds(float var1, float var2, float var3) {
            final float MAX_DIFFERENCE = 0.05f;
            return var2 - var1 < MAX_DIFFERENCE && var3 - var2 < MAX_DIFFERENCE;
        }

        private boolean isNearAnotherOne(int index) {
            return indexesToShow.contains(index + 2)
                    || indexesToShow.contains(index + 1)
                    || indexesToShow.contains(index - 1)
                    || indexesToShow.contains(index - 2);
        }

    }
}
