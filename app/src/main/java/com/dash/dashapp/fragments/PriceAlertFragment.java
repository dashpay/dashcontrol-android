package com.dash.dashapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.dash.dashapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Dexter Barretto on 10/2/18.
 * Github : @dbarretto
 */

public class PriceAlertFragment extends BaseFragment {

    private static final String TAG = PriceAlertFragment.class.getSimpleName();

    @BindView(R.id.price_edittext)
    EditText priceEditText;

    @BindView(R.id.trigger_type_radiogroup)
    RadioGroup triggerTypeRadioGroup;

    @BindView(R.id.trigger_type_over_radiobutton)
    RadioButton triggerOverRadioButton;

    @BindView(R.id.trigger_type_under_radiobutton)
    RadioButton triggerUnderRadioButton;

    @BindView(R.id.trigger_standardize_tether_checkedtextview)
    CheckedTextView standardizeTetherCheckedTextView;

    @BindView(R.id.trigger_consume_checkedtextview)
    CheckedTextView consumeCheckedTextView;

    @BindView(R.id.ignore_for_edittext)
    EditText ignoreForEditext;

    private Integer triggerType = 0;

    private PriceFragment.OnFragmentInteractionListener mListener;

    public PriceAlertFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PriceFragment.OnFragmentInteractionListener) {
            mListener = (PriceFragment.OnFragmentInteractionListener) context;
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_price, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    private void setRadioGroupListeners() {

        triggerTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // This will get the radiobutton that has changed in its check state
                RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);
                // This puts the value (true/false) into the variable
                boolean isChecked = checkedRadioButton.isChecked();
                // If the radiobutton that has changed in check state is now checked...
                if (isChecked) {
                    switch (checkedId) {
                        case R.id.trigger_type_over_radiobutton: {
                            triggerType = 0;
                            break;
                        }
                        case R.id.trigger_type_under_radiobutton: {
                            triggerType = 1;
                            break;
                        }
                        default:
                            break;
                    }
                }

            }
        });

    }
}
