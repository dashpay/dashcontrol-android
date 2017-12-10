package com.dash.dashapp.Fragments;

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

import com.dash.dashapp.Activities.AddMasternodeActivity;
import com.dash.dashapp.Activities.AddWalletActivity;
import com.dash.dashapp.Adapters.HeadingView;
import com.dash.dashapp.Adapters.MasternodeView;
import com.dash.dashapp.Adapters.WalletView;
import com.dash.dashapp.Model.Masternode;
import com.dash.dashapp.Model.Wallet;
import com.dash.dashapp.R;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.mindorks.placeholderview.ExpandablePlaceHolderView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PortfolioFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PortfolioFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PortfolioFragment extends BaseFragment {

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

        mUnbinder = ButterKnife.bind(this, view);


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
                startActivity(intent);
            }
        });


        final FloatingActionButton addWallet = (FloatingActionButton) view.findViewById(R.id.button_add_wallet);
        addWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddWalletActivity.class);
                startActivity(intent);
            }
        });


        List<Masternode> listMasternode = new ArrayList<>();
        Masternode m1 = new Masternode("Masternode 1");
        Masternode m2 = new Masternode("Masternode 2");
        listMasternode.add(m1);
        listMasternode.add(m2);

        List<Wallet> listWallet = new ArrayList<>();
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
