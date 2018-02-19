package com.dash.dashapp.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import com.dash.dashapp.application.DashControlApplication;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Dexter Barretto on 11/2/18.
 * Github : @dbarretto
 */

public class WalletSharedPreferenceHelper {

    private Context context;
    private SharedPreferences sharedPrefWallet;
    private SharedPreferences.Editor editor;

    private static WalletSharedPreferenceHelper walletSharedPreferenceHelper;


    public static final String address_book = "address_book";


    public WalletSharedPreferenceHelper() {
        this.context = DashControlApplication.getAppContext();
        this.sharedPrefWallet = context.getSharedPreferences("wallet",
                Context.MODE_PRIVATE);
        this.editor = sharedPrefWallet.edit();
    }

    public Set<String> getWallet_address_book() {
        return sharedPrefWallet.getStringSet(address_book, null);
    }

    public void setWallet_address_book(Set<String> wallet_address_book) {
        editor.putStringSet(address_book, wallet_address_book);
        editor.commit();
    }

    public void addWalletToAddressBook(String address) {
        Set<String> wallet_address_book = sharedPrefWallet.getStringSet(address_book, null);
        if(wallet_address_book == null)
            wallet_address_book = new HashSet<>();
        if (address != null && !address.isEmpty())
            wallet_address_book.add(address);
        setWallet_address_book(wallet_address_book);
    }

    public SharedPreferences getSharedPrefWallet() {
        return sharedPrefWallet;
    }

    public static WalletSharedPreferenceHelper getWalletSharedPreferenceHelper() {
        if (walletSharedPreferenceHelper == null) {
            walletSharedPreferenceHelper = new WalletSharedPreferenceHelper();
        }
        return walletSharedPreferenceHelper;
    }

    public void deleteAuthPreference(String string) {
        editor.remove(string).commit();
    }
}
