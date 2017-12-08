package com.coremobile.coreyhealth.partialsync;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.coremobile.coreyhealth.MessageItem;
import com.coremobile.coreyhealth.MyApplication;
import com.coremobile.coreyhealth.clientcertificate.CMN_HTTPSClient;
import com.coremobile.coreyhealth.googleAnalytics.CMN_Preferences;
import com.coremobile.coreyhealth.loggerUtility.Logger;
import com.coremobile.coreyhealth.newui.MessageTabActivityCMN;
import com.coremobile.coreyhealth.newui.NavDrawerManager;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Nitij Katiyar
 */
public class GetAllContextWebService extends
        AsyncTask<String, String, JSONObject> {
    public static ProgressDialog dialog;
    MessageTabActivityCMN activity;
    private String TAG = "Corey_GetAllContext";
    boolean aBoolean = false;
    public static ArrayList<MessageItem> patientsContexts;
    public static boolean serviceRunning = false;
    HashMap<String, String> patientIds;

    public static HashMap<String, String> patientsContextsMap;
    NavDrawerManager mNavDrawerManager;

    public GetAllContextWebService(MessageTabActivityCMN activity, boolean b, NavDrawerManager mNavDrawerManager) {
        this.activity = activity;
        this.aBoolean = b;
        this.mNavDrawerManager = mNavDrawerManager;
    }

    public GetAllContextWebService(MessageTabActivityCMN activity) {
        this.activity = activity;
        this.aBoolean = false;
        mNavDrawerManager = null;
    }


    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(activity);
        dialog.setMessage("Getting Patients...");
        serviceRunning = true;
//        dialog.setCancelable(false);
        try {
            if (dialog != null && !activity.isFinishing()) {
                dialog.show();
            }
        } catch (Exception e) {
            e.getMessage();
        }
        patientsContexts = new ArrayList<MessageItem>();
        patientsContextsMap = new HashMap<>();
        super.onPreExecute();
    }

    @Override
    protected JSONObject doInBackground(String... params) {

        String url = "";
//        if (AppConfig.isAESEnabled) {
//            url = CMN_Preferences.getBaseUrl(activity)
//                    + "GetAllContexts_s.aspx?token=" + CMN_Preferences.getUserToken(activity);
//        } else {
        url = CMN_Preferences.getBaseUrl(activity)
                + "GetAllContexts.aspx?token=" + CMN_Preferences.getUserToken(activity);
//        }
        Logger.Error(activity, "GetAllContexts", url);
        CloseableHttpClient httpclient = new CMN_HTTPSClient(MyApplication.INSTANCE, url).getClient();
        HttpGet httpGet = new HttpGet(url);
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("type", "web_server"));

        JSONObject json = null;
        try {

            httpGet.setHeader("Content-Type",
                    "application/x-www-form-urlencoded");
            HttpResponse response = httpclient.execute(httpGet);
            Log.d(TAG, "POST response: " + response.getStatusLine());
            json = new JSONObject(EntityUtils.toString(response.getEntity()));
            Logger.Error(activity, "GetAllContexts Response", "" + json);
            if (json.getJSONObject("Result").getInt("Code") == 0) {
                JSONArray messagesArray = json.getJSONArray("Data");
                patientsContexts = new ArrayList<>();
                for (int i = 0; i < messagesArray.length(); i++) {
                    JSONObject jsonObject = messagesArray.getJSONObject(i);
                    MessageItem patientContext = new MessageItem();
                    patientContext.ContextId = jsonObject.getString("Id").toString().toUpperCase();
                    patientContext.subject = jsonObject.getString("DisplayName");
                    patientContext.type = "instant_gratification";
                    patientsContexts.add(patientContext);
                    patientsContextsMap.put(patientContext.ContextId, patientContext.subject);
                }
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

    @Override
    protected void onPostExecute(JSONObject result) {
        super.onPostExecute(result);
        serviceRunning = false;
        if (dialog.isShowing()) {
            try {
                dialog.dismiss();
            } catch (IllegalStateException e) {
                e.getMessage();
            } catch (Exception e) {
                e.getMessage();
            }
            if (aBoolean) {
                mNavDrawerManager = new NavDrawerManager(activity);
            }
            if (patientsContexts.size() > 0) {
//                if (patientsContextsMap.get(CMN_Preferences.getLastViewedContextId(activity)) != null) {
//                    activity.getSinglePatientData(activity, CMN_Preferences.getLastViewedContextId(activity));
//                } else {
                    activity.getSinglePatientData(activity, patientsContexts.get(0).ContextId);
//                }
            } else {
                activity.dismissDialogue();
            }

        }
    }
}
