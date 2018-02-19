package com.dash.dashapp;

import com.dash.dashapp.helpers.ApiHelper;
import com.dash.dashapp.helpers.RandomGenerator;
import com.dash.dashapp.models.WalletBalance;
import com.dash.dashapp.utils.HttpUtil;

import org.junit.Test;

import java.util.List;

import okhttp3.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ApiTest {
    @Test
    public void executeGet() throws Exception {
        String url = "http://insight.dash.org/insight-api-dash/addrs/XbxSUCvMN1bCCTWguGLFcWbaNZkQS4XjEy,XbxSUCvMN1bCCTWguGLFcWbaNZkQS4XjEy/utxo";
        Response response = HttpUtil.executeGet(url);
        assertEquals(response.code(), 200);
        System.out.println(response.body().string());
    }

    @Test
    public void executeGetWalletPrice() throws Exception {
        List<WalletBalance> walletBalanceList = ApiHelper.getPriceForWalletOrMasterNode("XbxSUCvMN1bCCTWguGLFcWbaNZkQS4XjEy,XbxSUCvMN1bCCTWguGLFcWbaNZkQS4XjEy,XbxSUCvMN1bCCTWguGLFcWbaNZkQS4XjEy,XbxSUCvMN1bCCTWguGLFcWbaNZkQS4XjEy");
        assertNotNull(walletBalanceList);
        for(WalletBalance walletBalance:walletBalanceList){
            System.out.println(walletBalance.getAddress()+":"+walletBalance.getAmount());
        }
    }

    @Test
    public void executeRegistrationPost() throws Exception {
        String uuid = RandomGenerator.generateDeviceId();
        String password = RandomGenerator.generatePassword();
        ApiHelper.createOrUpdateDevice(uuid, null, password, "abcdefdkfnlhsncdn");
        //assertNull(response);
        //System.out.println(response);
    }

    @Test
    public void executeTriggerPost() throws Exception {
        String uuid = RandomGenerator.generateDeviceId();
        ApiHelper.createPriceAlert("194148ea-cd03-42fd-9439-e1aff4cb0c01", "abcdef", 0, 600, "USD", null, true, false, 0, null);
        //assertNull(response);
        //System.out.println(response);
    }
}