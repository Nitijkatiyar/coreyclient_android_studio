package com.coremobile.coreyhealth.newui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Toast;

import com.coremobile.coreyhealth.AppConfig;
import com.coremobile.coreyhealth.aesencryption.CMN_AES;
import com.coremobile.coreyhealth.clientcertificate.CMN_HTTPSClient;
import com.coremobile.coreyhealth.errorhandling.CMN_ErrorMessages;
import com.coremobile.coreyhealth.googleAnalytics.CMN_Preferences;
import com.coremobile.coreyhealth.messaging.CMN_MessagesModel;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * @author Nitij Katiyar
 */
public class UpdateApplicationdatamultipleWebService extends
        AsyncTask<String, String, JSONObject> {
    public ProgressDialog dialog;
    Activity activity;
    private String TAG = "Corey_GetAllMessages";
    RowDisplayObject rowDisplayObject;
    ChildIconAdapter mChildIconAdapter;
    ArrayList<CMN_MessagesModel> messagesList = new ArrayList<CMN_MessagesModel>();

    public UpdateApplicationdatamultipleWebService(Activity activity, RowDisplayObject rowDisplayObject, ChildIconAdapter mChildIconAdapter) {
        this.activity = activity;
        this.rowDisplayObject = rowDisplayObject;
        this.mChildIconAdapter = mChildIconAdapter;
    }

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(activity);
        dialog.setMessage(CMN_ErrorMessages.getInstance().getValue(131));
        dialog.setCancelable(false);
        dialog.show();

        new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                //the progressBar will be invisible after 60 000 miliseconds ( 1 minute)
                dialog.dismiss();
            }

        }.start();


        super.onPreExecute();
    }

    @Override
    protected JSONObject doInBackground(String... params) {


        String url = null;
        HttpPost httpPost;
        if (AppConfig.isAESEnabled) {
            url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                    + "updateApplicationdatamultiple_s.aspx?token=" + CMN_Preferences.getUserToken(activity);
//            String temp = url+URLEncoder.encode(params[0]);
//            url = url + "" + CMN_AES.encrypt(params[0], CMN_Preferences.getUserToken(activity));
            httpPost = new HttpPost(url + "");
            try {
                httpPost.setEntity(new StringEntity(CMN_AES.encrypt(params[0], CMN_Preferences.getUserToken(activity))));
            } catch (UnsupportedEncodingException e) {
                e.getMessage();
            }

        } else {
            url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                    + "updateApplicationdatamultiple.aspx?token=" + CMN_Preferences.getUserToken(activity) + "&jsonData=";
            url = url + "" + URLEncoder.encode(params[0]);
            httpPost = new HttpPost(url + "");
        }

        HttpParams httpParameters = new BasicHttpParams();
        int timeoutConnection = 3000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        int timeoutSocket = 3000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);


        CloseableHttpClient httpclient = new CMN_HTTPSClient(activity, url).getClient();
        Log.d(TAG, "url" + url);


//        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
//        nameValuePair.add(new BasicNameValuePair("jsonData", params[3]));


        JSONObject json = null;
        try {
            HttpResponse response = httpclient.execute(httpPost);
            Log.d(TAG, "POST response: " + response.getStatusLine());

            String result = EntityUtils.toString(response.getEntity());
            json = new JSONObject(CMN_AES.decryptData(result, CMN_Preferences.getUserToken(activity)));
            Log.d(TAG, "result" + result);

            if (json.getString("result").equalsIgnoreCase("0")) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CMN_Preferences.setNexttimeToUpdate(activity, (System.currentTimeMillis() + CMN_Preferences.getUpdateWaittime(activity)));
                        if (rowDisplayObject.getObjectStatus() != null && rowDisplayObject.getListValue().equalsIgnoreCase("YesNoStatus")) {
                            if (rowDisplayObject.getObjectStatus() != null && rowDisplayObject.getObjectStatus().equalsIgnoreCase("YES")) {
                                MessageTabActivityCMN.checkedState.put(rowDisplayObject.getObjectId(), "NO");
                            } else {
                                MessageTabActivityCMN.checkedState.put(rowDisplayObject.getObjectId(), "YES");
                            }
                        } else {
                            if (rowDisplayObject.getObjectStatus() != null && rowDisplayObject.getObjectStatus().equalsIgnoreCase("Completed")) {
                                MessageTabActivityCMN.checkedState.put(rowDisplayObject.getObjectId(), "Not Started");
                            } else {
                                MessageTabActivityCMN.checkedState.put(rowDisplayObject.getObjectId(), "Completed");
                            }
                        }
                        mChildIconAdapter.notifyDataSetChanged();
                    }
                });
            } else {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, CMN_ErrorMessages.getInstance().getValue(164), Toast.LENGTH_SHORT).show();
                    }
                });

            }

        } catch (UnsupportedEncodingException e1) {
            e1.getMessage();
        } catch (IllegalStateException e) {
            e.getMessage();
        } catch (ClientProtocolException e) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, CMN_ErrorMessages.getInstance().getValue(160), Toast.LENGTH_SHORT).show();
                }
            });
            e.getMessage();
        } catch (IOException e) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, CMN_ErrorMessages.getInstance().getValue(160), Toast.LENGTH_SHORT).show();
                }
            });
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
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
