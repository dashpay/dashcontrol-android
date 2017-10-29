package com.dash.dashapp.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dash.dashapp.Adapters.HeadingView;
import com.dash.dashapp.Adapters.MasternodeView;
import com.dash.dashapp.Adapters.WalletView;
import com.dash.dashapp.Model.Masternode;
import com.dash.dashapp.Model.Wallet;
import com.dash.dashapp.R;
import com.mindorks.placeholderview.ExpandablePlaceHolderView;

import java.util.ArrayList;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_portfolio, container, false);

        ArrayList<Masternode> listMasternode = new ArrayList<>();
        Masternode m1 = new Masternode("Masternode 1");
        Masternode m2 = new Masternode("Masternode 2");
        listMasternode.add(m1);
        listMasternode.add(m2);

        ArrayList<Wallet> listWallet = new ArrayList<>();
        Wallet w1 = new Wallet("Wallet 1");
        Wallet w2 = new Wallet("Wallet 2");
        listWallet.add(w1);
        listWallet.add(w2);

        mExpandableView = (ExpandablePlaceHolderView) view.findViewById(R.id.masternodeWalletExpandableView);

        mExpandableView.addView(new HeadingView(getContext(), "MY MASTERNODES"));
        for (Masternode masternode : listMasternode) {
            mExpandableView.addView(new MasternodeView(getContext(), masternode));
        }

        mExpandableView.addView(new HeadingView(getContext(), "MY WALLETS"));
        for (Wallet wallet : listWallet) {
            mExpandableView.addView(new WalletView(getContext(), wallet));
        }


        // Inflate the layout for this fragment
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
