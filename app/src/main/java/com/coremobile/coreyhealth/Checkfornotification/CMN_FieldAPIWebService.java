package com.coremobile.coreyhealth.Checkfornotification;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.coremobile.coreyhealth.MyApplication;
import com.coremobile.coreyhealth.clientcertificate.CMN_HTTPSClient;

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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nitij on 17-10-2016.
 */
public class CMN_FieldAPIWebService extends AsyncTask<String, String, JSONObject> {
    private Activity mContext;
    public static final String TAG = "Corey_CMN_FieldAPIWebService";
    ProgressDialog dialog;


    public CMN_FieldAPIWebService(Activity context) {
        mContext = context;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(mContext);
        dialog.setMessage("Sending Message...");
        dialog.setCancelable(false);
        dialog.show();


    }

    @Override
    protected JSONObject doInBackground(final String... params) {

        String url = null;


        url = params[0];


        //Log.e(TAG, "url.." + url);
        CloseableHttpClient httpclient = new CMN_HTTPSClient(MyApplication.INSTANCE, url).getClient();
        HttpGet httpGet = new HttpGet(url);
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("type", "web_server"));
        JSONObject json = null;
        try {

            httpGet.setHeader("Content-Type",
                    "application/x-www-form-urlencoded");
            HttpResponse response = httpclient.execute(httpGet);
            //Log.e(TAG, "POST response: " + response.getStatusLine());

            json = new JSONObject(EntityUtils.toString(response.getEntity()));

        } catch (JSONException e) {
            //e.getMessage();
        } catch (ClientProtocolException e) {
            //e.getMessage();
        } catch (IOException e) {
            //e.getMessage();
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
            dialog.cancel();
        }
    }
}
