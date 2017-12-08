package com.coremobile.coreyhealth.googleFit;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.coremobile.coreyhealth.MyApplication;
import com.coremobile.coreyhealth.clientcertificate.CMN_HTTPSClient;
import com.coremobile.coreyhealth.errorhandling.CMN_ErrorMessages;
import com.coremobile.coreyhealth.googleAnalytics.CMN_Preferences;
import com.coremobile.coreyhealth.newui.MessageTabActivityCMN;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * @author Nitij Katiyar
 */
public class CMN_UploadWearableDataWebService extends
        AsyncTask<String, String, JSONObject> {
    ProgressDialog dialog;
    Activity activity;
    private String TAG = "Corey_SendCoreyMessages";

    public CMN_UploadWearableDataWebService(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(activity);
        dialog.setMessage(CMN_ErrorMessages.getInstance().getValue(131));
        dialog.setCancelable(false);
        dialog.show();

        super.onPreExecute();
    }

    @Override
    protected JSONObject doInBackground(String... params) {

        String url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                + "UploadHealthKitData.aspx?token=" + CMN_Preferences.getUserToken(activity);
        Log.d(TAG, "url" + url);
        CloseableHttpClient httpclient = new CMN_HTTPSClient(MyApplication.INSTANCE, url).getClient();
        HttpPost httpPost = new HttpPost(url);

        JSONObject json = null;
        try {
            httpPost.setEntity(new StringEntity(params[0].toString(), "utf-8"));
            httpPost.setHeader("Content-Type",
                    "application/x-www-form-urlencoded");
            HttpResponse response = httpclient.execute(httpPost);
            Log.d(TAG, "POST response: " + response.getStatusLine());
            json = new JSONObject(EntityUtils.toString(response.getEntity()));
            if (json.getJSONObject("Result").getInt("Code") == 0) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, CMN_ErrorMessages.getInstance().getValue(136), Toast.LENGTH_SHORT).show();
                    }
                });

            } else {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, CMN_ErrorMessages.getInstance().getValue(84), Toast.LENGTH_SHORT).show();
                    }
                });
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
        }finally {
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
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
