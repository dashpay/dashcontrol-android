package com.dash.dashapp.service.fcm;

import android.content.SharedPreferences;
import android.util.Log;

import com.dash.dashapp.helpers.ApiHelper;
import com.dash.dashapp.helpers.AuthSharedPreferenceHelper;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Dexter Barretto on 8/2/18.
 * Github : @dbarretto
 */

public class DashControlFirebaseInstanceIdService extends FirebaseInstanceIdService{

    private static final String TAG = DashControlFirebaseInstanceIdService.class.getSimpleName();

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        AuthSharedPreferenceHelper.getAuthSharedPreferenceHelper().setFcmToken(refreshedToken);
        sendRegistrationToServer(refreshedToken);
    }

    /**
     * Persist token to third-party servers.
     * Update Device Information API ()
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        ApiHelper.updateFcmToken(AuthSharedPreferenceHelper.getAuthSharedPreferenceHelper().getDeviceId(),token);
    }

}
