package com.guohui.fasttransfer.utils;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Dikaros on 2016/5/10.
 */
public class NetUtil {

    /**
     * 发送短信验证码
     *
     * @param phoneNumber
     * @param content
     */
    public String sendSmsCode(String phoneNumber, String content) {
        if (phoneNumber == null || content == null) {
            return null;
        }
        BufferedReader reader = null;
        String result = null;
        StringBuffer sbf = new StringBuffer();
        String httpUrl = "http://apis.baidu.com/baidu_communication/sms_verification_code/smsverifycode";
        String httpArg = "phone=" + phoneNumber + "&content=" + content;
        httpUrl = httpUrl + "?" + httpArg;

        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setRequestMethod("GET");
            // ????apikey??HTTP header
            connection.setRequestProperty("apikey",
                    "db642b2fac4fafe26849179ad8883592 ");
            connection.connect();
            InputStream is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
                sbf.append("\r\n");
            }
            reader.close();
            result = sbf.toString();
            Log.i("result", result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
