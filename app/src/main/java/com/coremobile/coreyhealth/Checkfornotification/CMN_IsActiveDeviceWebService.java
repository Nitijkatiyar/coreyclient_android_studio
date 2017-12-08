package com.coremobile.coreyhealth.Checkfornotification;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.coremobile.coreyhealth.LocalPrefs;
import com.coremobile.coreyhealth.MyApplication;
import com.coremobile.coreyhealth.clientcertificate.CMN_HTTPSClient;
import com.coremobile.coreyhealth.googleAnalytics.CMN_Preferences;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nitij on 17-10-2016.
 */
public class CMN_IsActiveDeviceWebService extends AsyncTask<String, String, JSONObject> {
    private Activity mContext;
    public static final String TAG = "Corey_CMN_IsActiveDeviceWebService";


    public CMN_IsActiveDeviceWebService(Activity context) {
        mContext = context;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();


    }

    @Override
    protected JSONObject doInBackground(final String... params) {

        String url = null;

        try {
            url = params[3]
                    + "IsActiveDevice.aspx?token=" + CMN_Preferences.getUserToken(mContext)
                    + "&deviceid=" + URLEncoder.encode(LocalPrefs.INSTANCE.deviceId(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.getMessage();
        }

        CloseableHttpClient httpclient = new CMN_HTTPSClient(MyApplication.INSTANCE, url).getClient();
        JSONObject json = null;
        HttpResponse response = null;
        if (url != null) {
            HttpGet httpGet = new HttpGet(url);
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("type", "web_server"));

            try {

                httpGet.setHeader("Content-Type",
                        "application/x-www-form-urlencoded");
                  response = httpclient.execute(httpGet);
                Log.d(TAG, "POST response: " + response.getStatusLine());
                json = new JSONObject(EntityUtils.toString(response.getEntity()));
//            if (json.getJSONObject("Result").getInt("Code") == 0) {
//                if (json.getBoolean("Data")) {
                Log.d("Create Service", "Create Service");
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                        Intent intent = new Intent(mContext, CMN_AlarmReceiver.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0);
                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), CMN_Preferences.getCheckNotificationime(mContext),
                                pendingIntent);
//                        mContext.startService(new Intent(mContext,PendingNotificationService.class));
                    }
                });
//                }
//            }
            } catch (JSONException e) {
                e.getMessage();
            } catch (ClientProtocolException e) {
                e.getMessage();
            } catch (IOException e) {
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
        }
        return json;

    }


    @Override
    protected void onPostExecute(JSONObject result) {
        super.onPostExecute(result);
    }
}
