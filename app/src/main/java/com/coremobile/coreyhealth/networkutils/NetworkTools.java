package com.coremobile.coreyhealth.networkutils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.coremobile.coreyhealth.MyApplication;
import com.coremobile.coreyhealth.aesencryption.CMN_AES;
import com.coremobile.coreyhealth.clientcertificate.CMN_HTTPSClient;
import com.coremobile.coreyhealth.googleAnalytics.CMN_Preferences;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nitij on 20/04/2016.
 */
public class NetworkTools {


    private static NetworkTools ourInstance = new NetworkTools();
    private static String sessionId = "";

    private static final String TAG = "Corey_NetworkTool";

    public static NetworkTools getInstance() {
        return ourInstance;
    }

    private NetworkTools() {

    }

    public boolean checkNetworkConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }


    public JSONObject getJsonData(final Activity context, String urlString) {


        Log.d(TAG, "requesturl" + urlString);
        CloseableHttpClient httpclient = new CMN_HTTPSClient(MyApplication.INSTANCE, urlString).getClient();
        HttpGet httpGet = new HttpGet(urlString);
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("type", "web_server"));

        JSONObject json = null;
        try {

            httpGet.setHeader("Content-Type",
                    "application/x-www-form-urlencoded");
            HttpResponse response = httpclient.execute(httpGet);
            Log.d(TAG, "POST response: " + response.getStatusLine());
            String result = EntityUtils.toString(response.getEntity());
            json = new JSONObject(CMN_AES.decryptData(result, CMN_Preferences.getUserToken(context)));
            Log.d("response", "" + json);
            if (json.getJSONObject("Result").getInt("Code") == 0) {

            }

        } catch (UnsupportedEncodingException e1) {
            e1.getMessage();
        } catch (IllegalStateException e) {
            e.getMessage();
        } catch (ClientProtocolException e) {
            e.getMessage();
        } catch (IOException e) {
            e.getMessage();
        } catch (JSONException e) {
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


    public JSONObject postJsonData(final Activity context, String urlString, final JSONObject jsonObject) {


        Log.d(TAG, "url" + urlString);
        Log.d(TAG, "requestjson" + jsonObject);
        CloseableHttpClient httpclient = new CMN_HTTPSClient(MyApplication.INSTANCE, urlString).getClient();
        HttpPost httpPost = new HttpPost(urlString);

        JSONObject json = null;
        try {
            if (urlString.contains("_s")) {
                httpPost.setEntity(new StringEntity(CMN_AES.encrypt(jsonObject.toString(), CMN_Preferences.getUserToken(context))));
            } else {
                httpPost.setEntity(new StringEntity(jsonObject.toString()));
            }


            httpPost.setEntity(new StringEntity(jsonObject.toString(), "UTF-8"));
            HttpResponse response = httpclient.execute(httpPost);
            Log.d(TAG, "POST response: " + response.getStatusLine());
            String result = EntityUtils.toString(response.getEntity());
            json = new JSONObject(CMN_AES.decryptData(result, CMN_Preferences.getUserToken(context)));
            Log.d("response", "" + json);
            if (json.getJSONObject("Result").getInt("Code") == 0) {

            }

        } catch (UnsupportedEncodingException e1) {
            e1.getMessage();
        } catch (IllegalStateException e) {
            e.getMessage();
        } catch (ClientProtocolException e) {
            e.getMessage();
        } catch (IOException e) {
            e.getMessage();
        } catch (JSONException e) {
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

    public JSONObject postJsonArrayData(final Activity context, String urlString, final JSONArray jsonObject) {


        Log.d(TAG, "url" + urlString);
        Log.d(TAG, "requestjson" + jsonObject);
        CloseableHttpClient httpclient = new CMN_HTTPSClient(MyApplication.INSTANCE, urlString).getClient();
        HttpPost httpPost = new HttpPost(urlString);

        JSONObject json = null;
        try {
            httpPost.setEntity(new StringEntity(jsonObject.toString()));
            HttpResponse response = httpclient.execute(httpPost);
            Log.d(TAG, "POST response: " + response.getStatusLine());
            json = new JSONObject(EntityUtils.toString(response.getEntity()));
            Log.d(TAG, " response: " + json);
            if (json.getJSONObject("Result").getInt("Code") == 0) {

            }

        } catch (UnsupportedEncodingException e1) {
            e1.getMessage();
        } catch (IllegalStateException e) {
            e.getMessage();
        } catch (ClientProtocolException e) {
            e.getMessage();
        } catch (IOException e) {
            e.getMessage();
        } catch (JSONException e) {
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
