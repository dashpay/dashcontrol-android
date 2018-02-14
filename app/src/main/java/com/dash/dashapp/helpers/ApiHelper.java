package com.dash.dashapp.helpers;

import android.content.Context;
import android.util.Log;

import com.dash.dashapp.utils.DeviceInfoUtil;
import com.dash.dashapp.utils.HttpUtil;

/**
 * Created by Dexter Barretto on 8/2/18.
 * Github : @dbarretto
 */

public class ApiHelper {

    private static final String TAG = ApiHelper.class.getSimpleName();


    public static void createOrUpdateDevice(Context context, String id, String password, String fcmToken) {
        String url = "https://dashpay.info/api/v0/device";
        String params = "device_id=" + id +
                "&token=" + fcmToken +
                "&model=" + DeviceInfoUtil.getDeviceModel() +
                "&os=Android" +
                "&os_version=" + DeviceInfoUtil.getOsVersion() +
                "&appName=dash-control" +
                "&version=" + DeviceInfoUtil.getAppVersion();
        Log.d(TAG, "createOrUpdateDevice: " + params);
        String response = HttpUtil.executePost(url, params);
        Log.d(TAG, "createOrUpdateDevice: " + response);
    }

    public static void updateFcmToken(String id, String fcmToken) {
        if (id != null && fcmToken != null) {
            String url = "https://dashpay.info/api/v0/device";
            String params = "device_id=" + id +
                    "&token=" + fcmToken;
            Log.d(TAG, "createOrUpdateDevice: " + params);
            String response = HttpUtil.executePost(url, params);
            Log.d(TAG, "createOrUpdateDevice: " + response);
        }
    }

    /**
     * @param triggerType: The type of the trigger. Types are : 0 - Over | 1 - Under
     */
    public static void createPriceAlert(Integer triggerType, Integer triggerValue, String market,
                                        String exchange, Boolean consume, Boolean standardizeTether,
                                        Integer ignoreFor, Integer conditionalValue) {
        String url = "https://dashpay.info/api/v0/trigger";
        String params = "type=" + triggerType +
                "&value=" + triggerValue +
                "&market=" + market +
                "&exchange=" + exchange +
                "&consume=" + consume +
                "&standardize_tether=" + standardizeTether +
                "&ignore_for=" + ignoreFor +
                "&conditional_value=" + conditionalValue;
        Log.d(TAG, "createPriceAlert: " + params);
        String response = HttpUtil.executePost(url, params);
        Log.d(TAG, "createPriceAlert: " + response);
    }

}
