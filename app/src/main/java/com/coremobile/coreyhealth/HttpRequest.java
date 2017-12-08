package com.coremobile.coreyhealth;

import android.util.Log;

import com.coremobile.coreyhealth.aesencryption.CMN_AES;
import com.coremobile.coreyhealth.clientcertificate.CMN_HTTPSClient;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class HttpRequest {
    private String url = null;
    ArrayList<NameValuePair> nameValuePair;
    HashMap<String, String> mData;
    String secretKey;
    public static String Deviceid = "Deviceid";
    public static String Username = "Username";
    //public static String Password = "Password";
    public static String DeviceType = "DeviceType";
    public static String AppVersion = "AppVersion";
    public static String AppName = "AppName";
    public static String organizationName = "organizationName";

    //private static final String TAG = "Corey_HttpRequest";
    HttpRequest(HashMap<String, String> mData, String url, ArrayList<NameValuePair> nameValuePair, String secretKey) {
        this.url = url;
        this.secretKey = secretKey;
        this.mData = mData;
        this.nameValuePair = nameValuePair;
    }

    private static String convertStreamToString(InputStream is) {
        /*
         * To convert the InputStream to String we use the BufferedReader.readLine()
         * method. We iterate until the BufferedReader return null which means
         * there's no more data to read. Each line will appended to a StringBuilder
         * and returned as String.
         */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.getMessage();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.getMessage();
            }
        }
        return sb.toString();
    }

    public JSONObject doRequest() {

        ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
        Iterator<String> it = mData.keySet().iterator();

        JSONObject jsonObject = new JSONObject();
        while (it.hasNext()) {
            String parameter = it.next();

            nameValuePair.add(new BasicNameValuePair(parameter, mData.get(parameter)));
            try {
                String tkeyForValue = parameter;
                if (parameter.equalsIgnoreCase("Deviceid")) {
                    parameter = Deviceid;
                } else if (parameter.equalsIgnoreCase("username")) {
                    parameter = Username;
                } else if (parameter.equalsIgnoreCase("password")) {
                    parameter = parameter.substring(0, 1).toUpperCase() + parameter.substring(1).toLowerCase();
                } else if (parameter.equalsIgnoreCase("DeviceType")) {
                    parameter = DeviceType;
                } else if (parameter.equalsIgnoreCase("AppVersion")) {
                    parameter = AppVersion;
                } else if (parameter.equalsIgnoreCase("AppName")) {
                    parameter = AppName;
                } else if (parameter.contains("organization")) {
                    parameter = organizationName;
                }
                jsonObject.put(parameter, mData.get(tkeyForValue));
            } catch (JSONException e) {
                e.getMessage();
            }
        }

        CloseableHttpClient  httpclient = new CMN_HTTPSClient(MyApplication.INSTANCE, url).getClient();

        HttpResponse httpResponse = null;
        //HttpRequestBase httpReq = new HttpGet(url);
        HttpPost httpReq = new HttpPost(url);

        InputStream inputStream = null;
        JSONObject json = null;


        try {
            if (AppConfig.isAESEnabled) {
                httpReq.setEntity(CMN_AES.getEntity(jsonObject.toString(), secretKey.toUpperCase(), nameValuePair));
            } else {
                httpReq.setEntity(new StringEntity(jsonObject.toString()));
            }

            httpResponse = httpclient.execute(httpReq);
            inputStream = httpResponse.getEntity().getContent();
            String result = convertStreamToString(inputStream);
            Log.d("JSON Result", result);
            json = new JSONObject(CMN_AES.decryptData(result, secretKey.toUpperCase()));
            inputStream.close();
        } catch (Exception e) {
            e.getMessage();
        } finally {
            if (httpclient != null) {
                try {
                    httpclient.close();
                } catch (IOException e) {
                    e.getMessage();
                }
            }
        }

        return json;
    }
}
