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

    public static void createOrUpdateDevice(Context context,String fcm_token){
        String url = "https://dashpay.info/api/v0/device";
        String params = "device_id="+ DeviceInfoUtil.getDeviceId(context)+
                "&token="+fcm_token+
                "&model="+DeviceInfoUtil.getDeviceModel()+
                "&os=Android" +
                "&os_version="+DeviceInfoUtil.getOsVersion()+
                "&appName=dash-control"+
                "&version="+DeviceInfoUtil.getAppVersion();
        Log.i(TAG, "createOrUpdateDevice: "+ params);
        String response = HttpUtil.executePost(url,params);
        Log.i(TAG, "createOrUpdateDevice: "+ response);
    }

}
