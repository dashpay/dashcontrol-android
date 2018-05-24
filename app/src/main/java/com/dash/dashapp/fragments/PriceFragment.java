package com.dash.dashapp.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.dash.dashapp.R;
import com.dash.dashapp.charting.CustomXAxisRenderer;
import com.dash.dashapp.charting.GraphXAxisValueFormatter;
import com.dash.dashapp.models.Exchange;
import com.dash.dashapp.models.Market;
import com.dash.dashapp.models.PriceChartRecord;
import com.dash.dashapp.service.PriceDataService;
import com.dash.dashapp.utils.ChartDataHelper;
import com.dash.dashapp.utils.DateUtil;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import butterknife.Unbinder;

public class PriceFragment extends BaseFragment {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMM dd h:mm a", Locale.US);

    @BindView(R.id.price)
    TextView priceView;

    @BindView(R.id.marker_data)
    View markerDataView;

    @BindView(R.id.marker_time)
    TextView markerTimeView;

    @BindView(R.id.marker_open)
    TextView markerOpenView;

    @BindView(R.id.marker_close)
    TextView markerCloseView;

    @BindView(R.id.marker_high)
    TextView markerHighView;

    @BindView(R.id.marker_low)
    TextView markerLowView;

    @BindView(R.id.marker_volume_dash)
    TextView markerVolumeDashView;

    @BindView(R.id.marker_volume_pair)
    TextView markerVolumePairView;

    @BindView(R.id.exchanges_spinner)
    Spinner exchangesSpinnerView;

    @BindView(R.id.market_spinner)
    Spinner marketSpinnerView;

    @BindView(R.id.chart)
    CombinedChart chartView;

    @BindView(R.id.time_frame_group)
    RadioGroup timeFrameGroupView;

    @BindView(R.id.candlestick_group)
    RadioGroup candlestickGroupView;

    @BindView(R.id.radio_1d)
    RadioButton oneDayRadioView;

    @BindView(R.id.radio_24h)
    RadioButton twentyFourHoursRadioView;

    @BindView(R.id.radio_6h)
    RadioButton sixHoursRadioView;

    private Unbinder unbinder;

    public PriceFragment() {
        // Required empty public constructor
    }

    public static PriceFragment newInstance() {
        PriceFragment fragment = new PriceFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_price, container, false);
        unbinder = ButterKnife.bind(this, view);

        setupSpinnersAndPrice();

        timeFrameGroupView.setOnCheckedChangeListener(timeFrameOnCheckedChangeListener);
        candlestickGroupView.setOnCheckedChangeListener(candlestickOnCheckedChangeListener);

        return view;
    }

    private RadioGroup.OnCheckedChangeListener timeFrameOnCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            RadioButton checkedRadioButton = group.findViewById(checkedId);
            boolean isChecked = checkedRadioButton.isChecked();
            if (isChecked) {
                boolean selected6hOr24h = (checkedId == R.id.radio_6h || checkedId == R.id.radio_24h);
                oneDayRadioView.setEnabled(!selected6hOr24h);

                drawChart();
            }
        }
    };

    private RadioGroup.OnCheckedChangeListener candlestickOnCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            RadioButton checkedRadioButton = group.findViewById(checkedId);
            boolean isChecked = checkedRadioButton.isChecked();
            if (isChecked) {
                boolean selected24h = (checkedId == R.id.radio_1d);
                twentyFourHoursRadioView.setEnabled(!selected24h);
                sixHoursRadioView.setEnabled(!selected24h);

                drawChart();
            }
        }
    };

    private void drawChart() {

        int checkedCandlestickRadioId = candlestickGroupView.getCheckedRadioButtonId();
        ChartDataHelper.Candlestick candlestick = candlestickButtonMap.get(checkedCandlestickRadioId);

        int checkedTimeFrameRadioId = timeFrameGroupView.getCheckedRadioButtonId();
        ChartDataHelper.TimeFrame timeFrame = timeFrameButtonMap.get(checkedTimeFrameRadioId);

        String exchange = ((Exchange) exchangesSpinnerView.getSelectedItem()).name;
        Market market = (Market) marketSpinnerView.getSelectedItem();

        priceView.setText(String.valueOf(market.price));

        drawChart(timeFrame, candlestick, exchange, market.name);
    }

    private void drawChart(ChartDataHelper.TimeFrame timeFrame, ChartDataHelper.Candlestick candlestick, String exchange, String market) {

        long currentDate = System.currentTimeMillis();
        long startDate = DateUtil.roundDownToNearest(currentDate - timeFrame.getDuration(), candlestick.getDuration());

        List<PriceChartRecord> priceChartRecordList = ChartDataHelper.getChartData(exchange, market, startDate, candlestick);
        if (priceChartRecordList.size() != 0) {

            List<CandleEntry> candleEntries = new ArrayList<>();
            List<BarEntry> volumeEntries = new ArrayList<>();
            List<Long> xVals = new ArrayList<>();

            for (int i = 0; i < priceChartRecordList.size(); i++) {
                PriceChartRecord record = priceChartRecordList.get(i);
                xVals.add(record.time);
                volumeEntries.add(new BarEntry(i, record.volume));
                candleEntries.add(new CandleEntry(i, record.high, record.low, record.open, record.close, record));
            }

            setupChart(xVals);

            CandleDataSet candleDataSet = new CandleDataSet(candleEntries, null);

            candleDataSet.setDrawIcons(false);
            candleDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
            candleDataSet.setShadowColor(Color.WHITE);
            candleDataSet.setShadowWidth(0.7f);
            candleDataSet.setDecreasingColor(Color.RED);
            candleDataSet.setDecreasingPaintStyle(Paint.Style.FILL);
            candleDataSet.setIncreasingColor(Color.rgb(122, 242, 84));
            candleDataSet.setIncreasingPaintStyle(Paint.Style.FILL);
            candleDataSet.setNeutralColor(Color.BLUE);

            CandleData candleData = new CandleData(candleDataSet);

            BarDataSet volumeDataSet = new BarDataSet(volumeEntries, null);
            volumeDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
            volumeDataSet.setColor(Color.DKGRAY);
            BarData volumeBarData = new BarData(volumeDataSet);

            CombinedData combinedData = new CombinedData();
            combinedData.setData(candleData);
            combinedData.setData(volumeBarData);

            chartView.setData(combinedData);
            chartView.invalidate();

        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
            alertDialog.setTitle("Error");
            alertDialog.setMessage("There's no data with current parameters");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }
    }

    private void setupChart(List<Long> xVals) {
        chartView.setBackgroundResource(R.color.dark_background);
        chartView.setNoDataTextColor(Color.WHITE);
        chartView.getDescription().setEnabled(false);
        chartView.setMaxVisibleValueCount(20);
        chartView.setPinchZoom(false);
        chartView.setDrawGridBackground(false);
        chartView.getLegend().setEnabled(false);

        chartView.getXAxis().setLabelCount(4);
        chartView.getXAxis().setTextColor(Color.WHITE);

        chartView.getAxisLeft().setTextColor(Color.WHITE);
        chartView.getAxisRight().setTextColor(Color.GRAY);

        XAxis xAxis = chartView.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(24);

        xAxis.setValueFormatter(new GraphXAxisValueFormatter(xVals));
        chartView.setXAxisRenderer(new CustomXAxisRenderer(chartView));

        YAxis leftAxis = chartView.getAxisLeft();
        leftAxis.setLabelCount(7, false);
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawAxisLine(true);

        YAxis rightAxis = chartView.getAxisRight();
        rightAxis.setLabelCount(7, false);
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawAxisLine(true);
        rightAxis.setSpaceBottom(0);

        chartView.setOnChartValueSelectedListener(onChartValueSelectedListener);
        chartView.resetTracking();
    }

    private OnChartValueSelectedListener onChartValueSelectedListener = new OnChartValueSelectedListener() {
        @Override
        public void onValueSelected(Entry e, Highlight h) {
            markerDataView.setVisibility(View.VISIBLE);
            Object data = e.getData();
            if (data != null) {
                PriceChartRecord record = (PriceChartRecord) data;
                markerOpenView.setText(getString(R.string.chart_marker_open, record.open));
                markerCloseView.setText(getString(R.string.chart_marker_close, record.close));
                markerHighView.setText(getString(R.string.chart_marker_high, record.high));
                markerLowView.setText(getString(R.string.chart_marker_low, record.low));
                markerTimeView.setText(getString(R.string.chart_marker_date, DATE_FORMAT.format(new Date(record.time))));
                markerVolumeDashView.setText(getString(R.string.chart_marker_volume_dash, record.volume));
                markerVolumePairView.setText(getString(R.string.chart_marker_volume, record.pairVolume));
            }
        }

        @Override
        public void onNothingSelected() {
            markerDataView.setVisibility(View.GONE);
        }
    };

    @OnClick(R.id.marker_data)
    public void onMarkerDataClick(View view) {
        view.setVisibility(View.GONE);
    }

    private void setupSpinnersAndPrice() {
        List<Exchange> listExchange = PriceDataService.findExchanges();
        Exchange defaultExchange = null;
        for (Exchange exchange : listExchange) {
            for (Market market : exchange.markets) {
                if (market.isDefault) {
                    defaultExchange = exchange;
                    break;
                }
            }
        }

        final Context context = Objects.requireNonNull(getContext());

        ArrayAdapter<Exchange> exchangesAdapter = new ArrayAdapter<>(context, R.layout.view_spinner_item, listExchange);
        exchangesAdapter.setDropDownViewResource(R.layout.view_spinner_drop_down_item);
        exchangesSpinnerView.setAdapter(exchangesAdapter);
        int defaultExchangePosition = exchangesAdapter.getPosition(defaultExchange);
        exchangesSpinnerView.setSelection(defaultExchangePosition);
    }

    @OnItemSelected(R.id.exchanges_spinner)
    public void onExchangesItemSelected() {
        Exchange exchange = (Exchange) exchangesSpinnerView.getSelectedItem();
        setupMarketSpinner(exchange);
    }

    @OnItemSelected(R.id.market_spinner)
    public void onMarketItemSelected() {
        drawChart();
    }

    private void setupMarketSpinner(Exchange exchange) {
        Context context = Objects.requireNonNull(getContext());

        ArrayAdapter<Market> marketsAdapter = new ArrayAdapter<>(context, R.layout.view_spinner_item, exchange.markets);
        marketsAdapter.setDropDownViewResource(R.layout.view_spinner_drop_down_item);

        Market selectMarket = exchange.markets.get(0);
        for (Market market : exchange.markets) {
            if (market.isDefault) {
                selectMarket = market;
                break;
            }
        }

        marketSpinnerView.setAdapter(marketsAdapter);
        int defaultMarketPosition = marketsAdapter.getPosition(selectMarket);
        marketSpinnerView.setSelection(defaultMarketPosition);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private static SparseArray<ChartDataHelper.Candlestick> candlestickButtonMap = new SparseArray<>();
    private static SparseArray<ChartDataHelper.TimeFrame> timeFrameButtonMap = new SparseArray<>();

    static {
        candlestickButtonMap.append(R.id.radio_5m, ChartDataHelper.Candlestick.FIVE_MINUTES);
        candlestickButtonMap.append(R.id.radio_15m, ChartDataHelper.Candlestick.FIFTEEN_MINUTES);
        candlestickButtonMap.append(R.id.radio_30m, ChartDataHelper.Candlestick.THIRTY_MINUTES);
        candlestickButtonMap.append(R.id.radio_2h, ChartDataHelper.Candlestick.TWO_HOURS);
        candlestickButtonMap.append(R.id.radio_4h, ChartDataHelper.Candlestick.FOUR_HOURS);
        candlestickButtonMap.append(R.id.radio_1d, ChartDataHelper.Candlestick.TWENTY_FOUR_HOURS);

        timeFrameButtonMap.append(R.id.radio_6h, ChartDataHelper.TimeFrame.SIX_HOURS);
        timeFrameButtonMap.append(R.id.radio_24h, ChartDataHelper.TimeFrame.TWENTY_FOUR_HOURS);
        timeFrameButtonMap.append(R.id.radio_2d, ChartDataHelper.TimeFrame.TWO_DAYS);
        timeFrameButtonMap.append(R.id.radio_4d, ChartDataHelper.TimeFrame.FOUR_DAYS);
        timeFrameButtonMap.append(R.id.radio_1w, ChartDataHelper.TimeFrame.ONE_WEEK);
        timeFrameButtonMap.append(R.id.radio_2w, ChartDataHelper.TimeFrame.TWO_WEEKS);
        timeFrameButtonMap.append(R.id.radio_1m, ChartDataHelper.TimeFrame.ONE_MONTH);
        timeFrameButtonMap.append(R.id.radio_3m, ChartDataHelper.TimeFrame.THREE_MONTHS);
    }
}
