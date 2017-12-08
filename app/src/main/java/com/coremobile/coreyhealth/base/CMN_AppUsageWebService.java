package com.coremobile.coreyhealth.base;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.coremobile.coreyhealth.JSONHelperClass;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nitij on 17-10-2016.
 */
public class CMN_AppUsageWebService extends AsyncTask<String, String, JSONObject> {
    private Context mContext;
    public static final String TAG = "Corey_CMN_AppUsageWebService";
    public static final String CURRENT_USER = "CurrentUser";
    private String CMN_SERVER_BASE_URL_DEFINE;
    JSONHelperClass jsonHelperClass;
    String OrganizationStr;
    SharedPreferences mcurrentUserPref;

    public CMN_AppUsageWebService(Context context) {
        mContext = context;
        mcurrentUserPref = mContext.getSharedPreferences(CURRENT_USER, 0);
        OrganizationStr = mcurrentUserPref.getString("Organization", "");
        jsonHelperClass = new JSONHelperClass();
        CMN_SERVER_BASE_URL_DEFINE = jsonHelperClass.getBaseURL(OrganizationStr);
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();


    }

    @Override
    protected JSONObject doInBackground(final String... params) {

        String url = null;

        url = CMN_SERVER_BASE_URL_DEFINE
                + "LogUserActivity.aspx?token=" + CMN_Preferences.getUserToken(mContext)
                + "&ActivityType=" + params[0];
        //Log.e("request:", "" + url);

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
            if (json.getJSONObject("Result").getInt("Code") == 0) {

            }
        } catch (JSONException e) {
            //e.getMessage();
        } catch (ClientProtocolException e) {
            //e.getMessage();
        } catch (IOException e) {
            //e.getMessage();
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
