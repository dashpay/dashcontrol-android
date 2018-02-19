package com.dash.dashapp.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import com.dash.dashapp.R;
import com.dash.dashapp.activities.AddMasternodeActivity;
import com.dash.dashapp.activities.AddWalletActivity;
import com.dash.dashapp.adapters.MasternodeView;
import com.dash.dashapp.adapters.WalletView;
import com.dash.dashapp.helpers.ApiHelper;
import com.dash.dashapp.helpers.WalletSharedPreferenceHelper;
import com.dash.dashapp.models.Masternode;
import com.dash.dashapp.models.Wallet;
import com.dash.dashapp.models.WalletBalance;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.mindorks.placeholderview.ExpandablePlaceHolderView;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PortfolioFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PortfolioFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PortfolioFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private ExpandablePlaceHolderView mExpandableView;
    private static HashMap<String, Double> wallet_map;


    public PortfolioFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PortfolioFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PortfolioFragment newInstance() {
        PortfolioFragment fragment = new PortfolioFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wallet_map = new HashMap<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_portfolio, container, false);

        mExpandableView = (ExpandablePlaceHolderView) view.findViewById(R.id.masternodeWalletExpandableView);

        // creating floating button menu
        final FloatingActionMenu fam = (FloatingActionMenu) view.findViewById(R.id.menu_masternode_wallet);
        AnimatorSet set = new AnimatorSet();

        ObjectAnimator scaleOutX = ObjectAnimator.ofFloat(fam.getMenuIconView(), "scaleX", 1.0f, 0.2f);
        ObjectAnimator scaleOutY = ObjectAnimator.ofFloat(fam.getMenuIconView(), "scaleY", 1.0f, 0.2f);

        ObjectAnimator scaleInX = ObjectAnimator.ofFloat(fam.getMenuIconView(), "scaleX", 0.2f, 1.0f);
        ObjectAnimator scaleInY = ObjectAnimator.ofFloat(fam.getMenuIconView(), "scaleY", 0.2f, 1.0f);

        scaleOutX.setDuration(50);
        scaleOutY.setDuration(50);

        scaleInX.setDuration(150);
        scaleInY.setDuration(150);

        scaleInX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                fam.getMenuIconView().setImageResource(fam.isOpened()
                        ? R.drawable.ic_add_white_24px : R.drawable.ic_close_white_24dp);
            }
        });

        set.play(scaleOutX).with(scaleOutY);
        set.play(scaleInX).with(scaleInY).after(scaleOutX);
        set.setInterpolator(new OvershootInterpolator(2));

        fam.setIconToggleAnimatorSet(set);


        final FloatingActionButton addMasternode = (FloatingActionButton) view.findViewById(R.id.button_add_masternode);
        addMasternode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddMasternodeActivity.class);
                startActivityForResult(intent,100);
            }
        });


        final FloatingActionButton addWallet = (FloatingActionButton) view.findViewById(R.id.button_add_wallet);
        addWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddWalletActivity.class);
                startActivityForResult(intent,200);
            }
        });


        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (wallet_map == null) wallet_map = new HashMap<>();
        reload_wallets();
        refreshViews();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 || requestCode == 200) {
        if(resultCode == getActivity().RESULT_OK){
            reload_wallets();
            if(mExpandableView!=null) mExpandableView.removeAllViews();
            refreshViews();
        }
        }
    }

    private void reload_wallets() {
        Set<String> address_book = WalletSharedPreferenceHelper.getWalletSharedPreferenceHelper().getWallet_address_book();
        if (address_book != null) {
            try {
                List<WalletBalance> walletBalanceList = ApiHelper.getPriceForWalletOrMasterNode(address_book.toArray(new String[address_book.size()]));
                if (walletBalanceList != null && !walletBalanceList.isEmpty()) {
                    if (wallet_map == null) wallet_map = new HashMap<>();
                    wallet_map.clear();
                    for (WalletBalance walletBalance : walletBalanceList) {
                        wallet_map.put(walletBalance.getAddress(), walletBalance.getAmount());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void refreshViews(){
        for (Map.Entry<String, Double> wallet : wallet_map.entrySet()) {
            String key = wallet.getKey();
            Double value = wallet.getValue();
            if (value != null) {
                if (value > 1000) {
                    mExpandableView.addView(new MasternodeView(getContext(), new Masternode(key + " : " + value + " DASH")));
                } else {
                    mExpandableView.addView(new WalletView(getContext(), new Wallet(key + " : " + value + " DASH")));
                }
            }
        }

    }
}
