package com.dash.dashapp.utils;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

import com.dash.dashapp.BuildConfig;

/**
 * Created by Dexter Barretto on 8/2/18.
 * Github : @dbarretto
 */

public class DeviceInfoUtil {

    public static String getDeviceModel(){
        return Build.MANUFACTURER +" "+ Build.MODEL;
    }

    //Returns Android ID
    public static String getDeviceId(Context context){
        return Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    public static String getOsVersion(){
        return Build.VERSION.RELEASE;
    }

    public static Integer getAppVersion(){
        return BuildConfig.VERSION_CODE;
    }
}
