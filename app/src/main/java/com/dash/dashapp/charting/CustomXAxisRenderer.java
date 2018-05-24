package com.dash.dashapp.charting;

import android.graphics.Canvas;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class CustomXAxisRenderer extends XAxisRenderer {

    public CustomXAxisRenderer(ViewPortHandler viewPortHandler, XAxis xAxis, Transformer trans) {
        super(viewPortHandler, xAxis, trans);
    }

    public CustomXAxisRenderer(CombinedChart chart) {
        super(chart.getViewPortHandler(), chart.getXAxis(), chart.getTransformer(YAxis.AxisDependency.LEFT));
    }

    @Override
    protected void drawLabel(Canvas c, String formattedLabel, float x, float y, MPPointF anchor, float angleDegrees) {
        String line[] = formattedLabel.split("\n");
        float textSize = mAxisLabelPaint.getTextSize();
        mAxisLabelPaint.setTextSize(Utils.convertDpToPixel(10));
        Utils.drawXAxisValue(c, line[0], x, y, mAxisLabelPaint, anchor, angleDegrees);
        Utils.drawXAxisValue(c, line[1], x, y + mAxisLabelPaint.getTextSize(), mAxisLabelPaint, anchor, angleDegrees);
        mAxisLabelPaint.setTextSize(textSize);
    }
}
