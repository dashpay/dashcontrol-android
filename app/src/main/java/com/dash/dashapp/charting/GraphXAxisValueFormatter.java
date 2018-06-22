package com.dash.dashapp.charting;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GraphXAxisValueFormatter implements IAxisValueFormatter {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMM dd\nh:mm a", Locale.US);

    private List<Long> timeValues;

    public GraphXAxisValueFormatter(List<Long> timeValues) {
        this.timeValues = timeValues;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        int valuePosition = (int) value;
        if (valuePosition < timeValues.size()) {
            long time = timeValues.get(valuePosition);
            return DATE_FORMAT.format(new Date(time));
        } else {
            return String.valueOf(value);
        }
    }
}