package com.dash.dashapp.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.dash.dashapp.Model.Exchange;
import com.dash.dashapp.Model.Market;
import com.dash.dashapp.R;
import com.dash.dashapp.Utils.MySingleton;
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
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PriceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PriceFragment extends Fragment {

    private static final String TAG = "PriceFragment";
    private static final String URL_PRICE = "http://dashpay.info/api/v0/prices";
    private static final String URL_EXCHANGES = "https://dashpay.info/api/v0/markets";
    @BindView(R.id.spinnerExchanges)
    Spinner spinnerExchanges;
    @BindView(R.id.spinnerMarket)
    Spinner spinnerMarket;
    @BindView(R.id.priceTextview)
    TextView priceTextview;
    private OnFragmentInteractionListener mListener;

    private Context context;

    private CandleStickChart mChart;

    private String defaultExchange, defaultMarket;

    private Unbinder unbinder;


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
        context = view.getContext();

        unbinder = ButterKnife.bind(this, view);

        priceTextview = (TextView) view.findViewById(R.id.priceTextview);

        mChart = (CandleStickChart) view.findViewById(R.id.chart1);
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

        for (int i = 0; i < 60; i++) {
            float mult = (20 + 1);
            float val = (float) (Math.random() * 40) + mult;

            float high = (float) (Math.random() * 9) + 8f;
            float low = (float) (Math.random() * 9) + 8f;

            float open = (float) (Math.random() * 6) + 1f;
            float close = (float) (Math.random() * 6) + 1f;

            boolean even = i % 2 == 0;

            yVals1.add(new CandleEntry(
                    i, val + high,
                    val - low,
                    even ? val + open : val - open,
                    even ? val - close : val + close,
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


        return view;
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

        setDefaultExchanges();

        setSpinnerAndPrices();

    }

    private void setSpinnerAndPrices() {

        // Getting prices
        JsonObjectRequest jsObjRequestPrice = new JsonObjectRequest
                (Request.Method.GET, URL_PRICE, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        List<Exchange> listExchanges = new ArrayList<>();
                        List<String> listExchangesString = new ArrayList<>();
                        try {

                            Iterator<String> listExchangesKeys = response.keys();

                            // Getting exchanges
                            while (listExchangesKeys.hasNext()) {
                                String exchangeName = listExchangesKeys.next();
                                // get the value i care about
                                String marketString = response.optString(exchangeName);

                                JSONObject marketJson = response.getJSONObject(marketString);
                                Iterator<String> listMarketKeys = marketJson.keys();

                                Log.d(TAG, exchangeName);

                                //Feeding the available markets for this exchange
                                List<Market> listMarket = new ArrayList<>();
                                while (listMarketKeys.hasNext()) {
                                    String marketName = listExchangesKeys.next();
                                    Market market = new Market(marketName, marketJson.optDouble(marketName));
                                    Log.d(TAG, marketName);
                                    Log.d(TAG, marketJson.optString(marketName));

                                    listMarket.add(market);

                                }
                                Exchange exchange = new Exchange(exchangeName, listMarket);
                                listExchanges.add(exchange);
                                listExchangesString.add(exchange.getName());

                            }

                            ArrayAdapter<String> adapterExchanges = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, listExchangesString);
                            spinnerExchanges.setAdapter(adapterExchanges);

                            int indexDefaultMarket = 0;
                            for (int i = 0; i < listExchanges.size(); i++) {
                                Exchange exchange = listExchanges.get(i);
                                if (exchange.getName().equals(defaultExchange)) {
                                    spinnerExchanges.setSelection(i);
                                    List<String> listMarketString = new ArrayList<>();

                                    for (int j = 0; j < exchange.getListMarket().size(); j++) {
                                        Market market = exchange.getListMarket().get(j);
                                        listMarketString.add(market.getName());
                                        if (market.getName().equals(defaultMarket)) {
                                            indexDefaultMarket = j;
                                            priceTextview.setText(market.getPrice() + "$");
                                        } else {

                                        }
                                    }

                                    ArrayAdapter<String> adapterMarket = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, listMarketString);
                                    spinnerMarket.setAdapter(adapterMarket);
                                    spinnerMarket.setSelection(indexDefaultMarket);

                                } else {

                                }
                            }


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
        MySingleton.getInstance(context).addToRequestQueue(jsObjRequestPrice);
    }

    private void setDefaultExchanges() {

        // Getting exchanges (default exchange to display)
        JsonObjectRequest jsObjRequestExchanges = new JsonObjectRequest
                (Request.Method.GET, URL_EXCHANGES, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject price = response.getJSONObject("default");
                            Log.d(TAG, price.toString());
                            defaultExchange = price.getString("exchange");
                            defaultMarket = price.getString("market");

                            Log.d(TAG, defaultExchange);
                            Log.d(TAG, defaultMarket);

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
        MySingleton.getInstance(context).addToRequestQueue(jsObjRequestExchanges);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();

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
