package com.coremobile.coreyhealth.errorhandling;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.coremobile.coreyhealth.MyApplication;
import com.coremobile.coreyhealth.clientcertificate.CMN_HTTPSClient;
import com.coremobile.coreyhealth.googleAnalytics.CMN_Preferences;
import com.coremobile.coreyhealth.newui.MessageTabActivityCMN;

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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nitij on 17-10-2016.
 */
public class CMN_ErrorMessageWebService extends AsyncTask<String, String, JSONObject> {
    private Activity mContext;
    public static final String TAG = "Corey_GetGraphData";


    public CMN_ErrorMessageWebService(Activity context) {
        mContext = context;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();


    }

    @Override
    protected JSONObject doInBackground(String... params) {

        String url = null;
        String BaseUrl = null;
        if (params[3] != null) {
            BaseUrl = params[3];
        } else {
            BaseUrl = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE;
        }

        url = BaseUrl
                + "GetUserMessages.aspx?token=" + CMN_Preferences.getUserToken(mContext);


        Log.d(TAG, "url.." + url);
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
            if (json.getJSONObject("Result").getInt("Code") == 0) {
                JSONArray data = json.getJSONArray("Data");
                for (int i = 0; i < data.length(); i++) {
                    JSONObject jsonObject = data.getJSONObject(i);
                    CMN_ErrorMessages.getInstance().getMap().put(jsonObject.getInt("ID"), jsonObject.getString("Text"));
                }

            }
        } catch (JSONException e) {
            e.getMessage();
        } catch (ClientProtocolException e) {
            e.getMessage();
        } catch (IOException e) {
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
    }
}
