package com.coremobile.coreyhealth;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.coremobile.coreyhealth.aesencryption.CMN_AES;
import com.coremobile.coreyhealth.clientcertificate.CMN_HTTPSClient;
import com.coremobile.coreyhealth.googleAnalytics.CMN_Preferences;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class GetJSON extends AsyncTask<String, Void, JSONObject> {

    // static final String TAG = "Corey_GETJSON";
    private IDownloadJSON mActivity;
    Context context;
    boolean checkForAES = false;

    public GetJSON(IDownloadJSON _activity, Context activity) {

        mActivity = _activity;
        context = activity;
    }

    public GetJSON(IDownloadJSON _activity) {

        mActivity = _activity;
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        // TODO Auto-generated method stub
        //Log.i("TAG","doInBackground");
        MyApplication._parsing = true;
        JSONObject jsonObject = null;
        CloseableHttpClient httpclient;
String url = params[0];
        HttpGet httpGet = new HttpGet(url);
        /*
         * setting socket timeout
         */
        httpclient = new CMN_HTTPSClient(MyApplication.INSTANCE, params[0]).getClient();
        try {
            checkForAES = Boolean.parseBoolean(params[1]);
        } catch (Exception e) {
            e.getMessage();
        }
//        Logger.Error(MyApplication.INSTANCE,"Get JSON",url);
        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, 20000);
        HttpConnectionParams.setSoTimeout(httpParameters, 20000);
        httpGet.setParams(httpParameters);
        try {
            HttpResponse response = httpclient.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                String builder = EntityUtils.toString(response.getEntity());
                if (checkForAES) {
                    jsonObject = new JSONObject(CMN_AES.decryptData(builder.toString(), CMN_Preferences.getUserToken(context)));
                } else {
                    jsonObject = new JSONObject(builder.toString());
                }
//                Logger.Error(MyApplication.INSTANCE,"Get JSON Response",""+jsonObject);
            } else {
                Log.d(JSONParser.class.toString(), "Failed to download file");
            }
        } catch (ClientProtocolException e) {
            e.getMessage();
            //Log.i(TAG,"ClientProtocolException");
        } catch (IOException e) {
            e.getMessage();
            //Log.i(TAG,"IOException");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            //Log.i(TAG,"JSONException");
            e.getMessage();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            //Log.i(TAG,"JSONException");
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
        return jsonObject;
    }

    protected void onPostExecute(JSONObject json) {
        Log.d("TAG", "onPostExecute" + json);
        MyApplication._parsing = false;
        mActivity.buildUI(json);
    }

}
