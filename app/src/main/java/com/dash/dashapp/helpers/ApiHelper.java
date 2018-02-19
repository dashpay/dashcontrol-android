package com.dash.dashapp.helpers;

import com.dash.dashapp.models.WalletBalance;
import com.dash.dashapp.utils.DeviceInfoUtil;
import com.dash.dashapp.utils.HttpUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Response;

/**
 * Created by Dexter Barretto on 8/2/18.
 * Github : @dbarretto
 */

public class ApiHelper {

    private static final String TAG = ApiHelper.class.getSimpleName();


    public static void createOrUpdateDevice(String id, String old_password, String password, String fcmToken) throws IOException {
        String url = "https://dashpay.info/api/v0/device";
        String params = "device_id=" + id +
                "&password=" + password +
                "&old_password=" + old_password +
                "&token=" + fcmToken +
                "&os=Android" +
                "&os_version=" + DeviceInfoUtil.getOsVersion() +
                "&model=" + DeviceInfoUtil.getDeviceModel() +
                "&app_name=dash-control" +
                "&version=" + DeviceInfoUtil.getAppVersion();
        Response response = HttpUtil.executePost(url, params);
        System.out.println(params);
        System.out.println(response.body().string());
    }

    public static void updateFcmToken(String id, String fcmToken) {
        if (id != null && fcmToken != null) {
            String url = "https://dashpay.info/api/v0/device";
            String params = "device_id=" + id +
                    "&token=" + fcmToken;
            // Log.d(TAG, "createOrUpdateDevice: " + params);
            Response response = HttpUtil.executePost(url, params);
            //Log.d(TAG, "createOrUpdateDevice: " + response);
        }
    }

    /**
     * @param triggerType: The type of the trigger. Types are : 0 - Over | 1 - Under
     */
    public static void createPriceAlert(String device_id, String password, Integer triggerType, Integer triggerValue, String market,
                                        String exchange, Boolean consume, Boolean standardizeTether,
                                        Integer ignoreFor, Integer conditionalValue) {
        String url = "https://dashpay.info/api/v0/trigger";
        String params = "device_id=" + device_id +
                "password=" + device_id +
                "&type=" + triggerType +
                "&value=" + triggerValue +
                "&market=" + market +
                "&exchange=" + exchange +
                "&consume=" + consume +
                "&standardize_tether=" + standardizeTether +
                "&ignore_for=" + ignoreFor +
                "&conditional_value=" + conditionalValue;
//        Log.d(TAG, "createPriceAlert: " + params);
        String response = HttpUtil.executePostWithAuthentication(device_id, password, url, params);
//        Log.d(TAG, "createPriceAlert: " + response);
        System.out.println(response);
    }

    public static List<WalletBalance> getPriceForWalletOrMasterNode(String... addresses) throws IOException {
        String params = null;
        for (String address : addresses) {
            params = address + ",";
        }
        if (params != null && params.endsWith(",")) {
            params = params.substring(0, params.length() - 1);
        }

        String url = "http://insight.dash.org/insight-api-dash/addrs/" + params + "/utxo";
        String url_fallback = "https://insight.dash.siampm.com/api/addrs/" + params + "/utxo";

        List<WalletBalance> walletBalanceList = null;
        Type listType = new TypeToken<List<WalletBalance>>() {
        }.getType();

        Response response = HttpUtil.executeGet(url);

        if (response != null && response.isSuccessful()) {
            walletBalanceList = new Gson().fromJson(response.body().string(), listType);
        } else {
            Response response_fallback = HttpUtil.executeGet(url_fallback);
            if (response_fallback != null && response_fallback.isSuccessful()) {
                walletBalanceList = new Gson().fromJson(response.body().string(), listType);
            }
        }
        return walletBalanceList;
    }
}
