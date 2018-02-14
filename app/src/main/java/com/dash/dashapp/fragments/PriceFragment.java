package com.dash.dashapp.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.dash.dashapp.R;
import com.dash.dashapp.models.Exchange;
import com.dash.dashapp.models.Market;
import com.dash.dashapp.models.PriceChartData;
import com.dash.dashapp.utils.DateUtil;
import com.dash.dashapp.utils.MyDBHandler;
import com.dash.dashapp.utils.MySingleton;
import com.dash.dashapp.utils.URLs;
import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PriceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PriceFragment extends BaseFragment {

    private static final String TAG = "PriceFragment";
    @BindView(R.id.spinnerExchanges)
    Spinner spinnerExchanges;
    @BindView(R.id.spinnerMarket)
    Spinner spinnerMarket;
    @BindView(R.id.priceTextview)
    TextView priceTextview;
    @BindView(R.id.chart1)
    CandleStickChart mChart;
    @BindView(R.id.radioGroup_scale)
    RadioGroup timeFrameRadioGroup;
    @BindView(R.id.radioGroup_gap)
    RadioGroup gapRadioGroup;
    @BindView(R.id.radio1d)
    RadioButton oneDayRadioButton;


    private OnFragmentInteractionListener mListener;

    private Exchange currentExchange = new Exchange();
    private Market currentMarket = new Market();

    private List<Exchange> listExchanges;

    private long timeFrame = 0;
    private long gap = 0;


    public PriceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PriceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PriceFragment newInstance() {
        PriceFragment fragment = new PriceFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_price, container, false);

        ButterKnife.bind(this, view);

        timeFrameRadioGroup.check(R.id.radio6h);
        gapRadioGroup.check(R.id.radio5m);

        timeFrame = DateUtil.SIX_HOURS_INTERVAL;
        oneDayRadioButton.setEnabled(false);

        gap = DateUtil.FIVE_MINUTES_GAP;

        drawChart(timeFrame, gap);

        setRadioGroupListeners();

        return view;
    }

    private void setRadioGroupListeners() {

        // This overrides the radiogroup onCheckListener
        timeFrameRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // This will get the radiobutton that has changed in its check state
                RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);
                // This puts the value (true/false) into the variable
                boolean isChecked = checkedRadioButton.isChecked();
                // If the radiobutton that has changed in check state is now checked...
                if (isChecked) {
                    // Changes the textview's text to "Checked: example radiobutton text"
                    Log.d(TAG, checkedRadioButton.getText() + "");

                    String selectedtimeFrame = checkedRadioButton.getText() + "";

                    switch (selectedtimeFrame) {
                        case DateUtil.SIX_HOURS_INTERVAL_STRING:
                            timeFrame = DateUtil.SIX_HOURS_INTERVAL;
                            oneDayRadioButton.setEnabled(false);
                            break;

                        case DateUtil.TWENTY_FOUR_HOURS_INTERVAL_STRING:
                            timeFrame = DateUtil.TWENTY_FOUR_HOURS_INTERVAL;
                            oneDayRadioButton.setEnabled(false);
                            break;

                        case DateUtil.TWO_DAYS_INTERVAL_STRING:
                            timeFrame = DateUtil.TWO_DAYS_INTERVAL;
                            if (!oneDayRadioButton.isEnabled())
                                oneDayRadioButton.setEnabled(true);
                            break;

                        case DateUtil.FOUR_DAYS_INTERVAL_STRING:
                            timeFrame = DateUtil.FOUR_DAYS_INTERVAL;
                            if (!oneDayRadioButton.isEnabled())
                                oneDayRadioButton.setEnabled(true);
                            break;

                        case DateUtil.ONE_WEEK_INTERVAL_STRING:
                            timeFrame = DateUtil.ONE_WEEK_INTERVAL;
                            if (!oneDayRadioButton.isEnabled())
                                oneDayRadioButton.setEnabled(true);
                            break;

                        case DateUtil.TWO_WEEKS_INTERVAL_STRING:
                            timeFrame = DateUtil.TWO_WEEKS_INTERVAL;
                            if (!oneDayRadioButton.isEnabled())
                                oneDayRadioButton.setEnabled(true);
                            break;

                        case DateUtil.ONE_MONTH_INTERVAL_STRING:
                            timeFrame = DateUtil.ONE_MONTH_INTERVAL;
                            if (!oneDayRadioButton.isEnabled())
                                oneDayRadioButton.setEnabled(true);
                            break;

                        case DateUtil.THREE_MONTHS_INTERVAL_STRING:
                            timeFrame = DateUtil.THREE_MONTHS_INTERVAL;
                            if (!oneDayRadioButton.isEnabled())
                                oneDayRadioButton.setEnabled(true);
                            break;
                        default:
                            break;
                    }
                    drawChart(timeFrame, gap);
                }
            }
        });

        // This overrides the radiogroup onCheckListener
        gapRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // This will get the radiobutton that has changed in its check state
                RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);
                // This puts the value (true/false) into the variable
                boolean isChecked = checkedRadioButton.isChecked();
                // If the radiobutton that has changed in check state is now checked...
                if (isChecked) {
                    // Changes the textview's text to "Checked: example radiobutton text"
                    Log.d(TAG, checkedRadioButton.getText() + "");

                    String selectedtimeFrame = checkedRadioButton.getText() + "";

                    switch (selectedtimeFrame) {
                        case DateUtil.FIVE_MINUTES_GAP_STRING:
                            gap = DateUtil.FIVE_MINUTES_GAP;
                            break;
                        case DateUtil.FIFTEEN_MINUTES_GAP_STRING:
                            gap = DateUtil.FIFTEEN_MINUTES_GAP;
                            break;
                        case DateUtil.THIRTY_MINUTES_GAP_STRING:
                            gap = DateUtil.THIRTY_MINUTES_GAP;
                            break;
                        case DateUtil.TWO_HOURS_GAP_STRING:
                            gap = DateUtil.TWO_HOURS_GAP;
                            break;
                        case DateUtil.FOUR_HOURS_GAP_STRING:
                            gap = DateUtil.FOUR_HOURS_GAP;
                            break;
                        case DateUtil.TWENTY_FOUR_HOURS_GAP_STRING:
                            gap = DateUtil.TWENTY_FOUR_HOURS_GAP;
                            break;
                        default:
                            break;
                    }
                    drawChart(timeFrame, gap);
                }
            }
        });
    }

    private void drawChart(long timeframe, long gap) {

        long currentDate = System.currentTimeMillis();

        long startDate = currentDate - timeframe;
        long endDate = currentDate;

        MyDBHandler dbHandler = new MyDBHandler(getContext(), null);
        List<PriceChartData> priceChartDataList = dbHandler.findPriceChart(startDate, endDate, gap);

        Log.d("DateDebug", "Reading database startDate : " + DateUtil.getDate(startDate));
        Log.d("DateDebug", "Reading database endDate : " + DateUtil.getDate(endDate));

        if (priceChartDataList.size() != 0) {

            mChart.setBackgroundColor(Color.WHITE);
            mChart.getDescription().setEnabled(false);

            // if more than 60 entries are displayed in the chart, no values will be
            // drawn
            mChart.setMaxVisibleValueCount(60);

            // scaling can now only be done on x- and y-axis separately
            mChart.setPinchZoom(false);

            mChart.setDrawGridBackground(false);

            XAxis xAxis = mChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);

            YAxis leftAxis = mChart.getAxisLeft();
//        leftAxis.setEnabled(false);
            leftAxis.setLabelCount(7, false);
            leftAxis.setDrawGridLines(false);
            leftAxis.setDrawAxisLine(false);

            YAxis rightAxis = mChart.getAxisRight();
            rightAxis.setEnabled(false);

            mChart.getLegend().setEnabled(false);


            //Setting the data
            mChart.resetTracking();

            List<CandleEntry> yVals1 = new ArrayList<>();

            for (int i = 0; i < priceChartDataList.size(); i++) {

                PriceChartData pcd = priceChartDataList.get(i);

                //float val = (float) pcd.getVolume();

                float high = (float) pcd.getHigh();
                float low = (float) pcd.getLow();

                float open = (float) pcd.getOpen();
                float close = (float) pcd.getClose();

                yVals1.add(new CandleEntry(
                        i,
                        high,
                        low,
                        open,
                        close,
                        getResources().getDrawable(R.drawable.star)
                ));
            }

            CandleDataSet set1 = new CandleDataSet(yVals1, "Data Set");

            set1.setDrawIcons(false);
            set1.setAxisDependency(YAxis.AxisDependency.LEFT);
//        set1.setColor(Color.rgb(80, 80, 80));
            set1.setShadowColor(Color.DKGRAY);
            set1.setShadowWidth(0.7f);
            set1.setDecreasingColor(Color.RED);
            set1.setDecreasingPaintStyle(Paint.Style.FILL);
            set1.setIncreasingColor(Color.rgb(122, 242, 84));
            set1.setIncreasingPaintStyle(Paint.Style.STROKE);
            set1.setNeutralColor(Color.BLUE);
            //set1.setHighlightLineWidth(1f);

            CandleData data = new CandleData(set1);

            mChart.setData(data);
            mChart.invalidate();

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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onStart() {
        super.onStart();

        try {
            setSpinnerAndPrices();
        } catch (Exception e) {
            e.getMessage();
        }


    }

    private void setSpinnerAndPrices() {

        // Getting prices
        JsonObjectRequest jsObjRequestPrice = new JsonObjectRequest
                (Request.Method.GET, URLs.URL_PRICE, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        listExchanges = new ArrayList<>();
                        List<String> listExchangesString = new ArrayList<>();

                        try {

                            Iterator<String> listExchangesKeys = response.keys();

                            // Getting exchanges
                            while (listExchangesKeys.hasNext()) {

                                //Foreach exchange getting the market
                                List<Market> listMarket = new ArrayList<>();


                                String exchangeName = listExchangesKeys.next();
                                // get the value i care about
                                JSONObject exchangeJson = (JSONObject) response.get(exchangeName);

                                try {
                                    double dash_btc = exchangeJson.getDouble("DASH_BTC");
                                    Market market = new Market("DASH_BTC", dash_btc);
                                    listMarket.add(market);
                                } catch (Exception e) {
                                    e.getMessage();
                                }
                                try {
                                    double dash_usd = exchangeJson.getDouble("DASH_USD");
                                    Market market = new Market("DASH_USD", dash_usd);
                                    listMarket.add(market);
                                } catch (Exception e) {
                                    e.getMessage();
                                }

                                Exchange exchange = new Exchange(exchangeName, listMarket);
                                listExchanges.add(exchange);
                                listExchangesString.add(exchange.getName());

                            }

                            ArrayAdapter<String> adapterExchanges = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, listExchangesString);
                            spinnerExchanges.setAdapter(adapterExchanges);

                            setDefaultExchanges();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        error.getMessage();
                    }
                });
        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(mContext).addToRequestQueue(jsObjRequestPrice);
    }

    private void setDefaultExchanges() {

        // Getting exchanges (default exchange to display)
        JsonObjectRequest jsObjRequestExchanges = new JsonObjectRequest
                (Request.Method.GET, URLs.URL_EXCHANGES, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            List<String> listMarketString = new ArrayList<>();

                            JSONObject price = response.getJSONObject("default");
                            currentExchange.setName(price.getString("exchange"));
                            currentMarket.setName(price.getString("market"));
                            int indexDefaultMarket = 0;

                            for (int i = 0; i < listExchanges.size(); i++) {
                                if (listExchanges.get(i).getName().equals(currentExchange.getName())) {
                                    spinnerExchanges.setSelection(i);
                                    currentExchange = listExchanges.get(i);
                                    for (int j = 0; j < listExchanges.get(i).getListMarket().size(); j++) {
                                        listMarketString.add(listExchanges.get(i).getListMarket().get(j).getName());
                                        if (listExchanges.get(i).getListMarket().get(j).getName().equals(currentMarket.getName())) {
                                            indexDefaultMarket = j;
                                        }
                                    }

                                    ArrayAdapter<String> adapterMarket = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, listMarketString);
                                    spinnerMarket.setAdapter(adapterMarket);
                                    spinnerMarket.setSelection(indexDefaultMarket);
                                    priceTextview.setText(currentExchange.getListMarket().get(indexDefaultMarket).getPrice() + "");

                                }
                            }

                            spinnerExchanges.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                                    // your code here
                                    currentExchange = listExchanges.get(position);

                                    List<String> listMarketString = new ArrayList<>();

                                    for (int j = 0; j < currentExchange.getListMarket().size(); j++) {
                                        listMarketString.add(currentExchange.getListMarket().get(j).getName());
                                    }

                                    ArrayAdapter<String> adapterMarket = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, listMarketString);

                                    spinnerMarket.setAdapter(adapterMarket);
                                    spinnerMarket.setSelection(0);
                                    currentMarket = currentExchange.getListMarket().get(0);
                                    priceTextview.setText(currentMarket.getPrice() + "");

                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parentView) {
                                    // your code here
                                }

                            });


                            spinnerMarket.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                                    currentMarket = currentExchange.getListMarket().get(position);
                                    priceTextview.setText(currentMarket.getPrice() + "");
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parentView) {
                                    // your code here
                                }

                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        error.getMessage();
                        Log.d(TAG, "Error : " + error.getMessage());

                    }
                });

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(mContext).addToRequestQueue(jsObjRequestExchanges);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
