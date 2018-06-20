package com.dash.dashapp.helpers;

import com.dash.dashapp.utils.DeviceInfoUtil;
import com.dash.dashapp.utils.HttpUtil;

import java.io.IOException;
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
}
