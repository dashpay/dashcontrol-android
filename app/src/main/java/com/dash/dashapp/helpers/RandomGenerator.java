package com.dash.dashapp.helpers;

import android.content.Context;

import com.dash.dashapp.utils.DeviceInfoUtil;

import java.security.SecureRandom;
import java.util.UUID;

/**
 * Created by Dexter Barretto on 10/2/18.
 * Github : @dbarretto
 */

public class RandomGenerator {

    private static final String characterSet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static SecureRandom secureRandom = new SecureRandom();


    /**
     * Generates a uuid using the ANDROID_ID and Current System Time as a seed
     */
    public static String generateDeviceId() {
        String uuid = UUID.randomUUID().toString();
        return uuid;
    }

    public static String generatePassword(){
        return generatePassword(12);
    }

    public static String generatePassword(int length) {
        StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            stringBuilder.append(characterSet.charAt(secureRandom.nextInt(characterSet.length())));
        }
        return stringBuilder.toString();
    }


}
