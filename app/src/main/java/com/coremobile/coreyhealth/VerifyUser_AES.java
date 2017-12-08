package com.coremobile.coreyhealth;

import android.content.Context;
import android.os.AsyncTask;

import com.coremobile.coreyhealth.clientcertificate.CMN_HTTPSClient;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
//import android.util.Log;

class VerifyUser_AES extends AsyncTask<String, Void, JSONObject> {
    private String mData = null;// post data
    private IServerConnect mActivity = null;
    Context context;

    /**
     * constructor
     */
    VerifyUser_AES(IServerConnect activity, Context context, String data) {
        mActivity = activity;
        mData = data;
        this.context = context;
    }


    @Override
    protected JSONObject doInBackground(String... urls) {
        // TODO Auto-generated method stub
        if (urls == null)
            return null;
        // set up post data
//        ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
//        Iterator<String> it = mData.keySet().iterator();
//        while (it.hasNext())
//        {
//            String key = it.next();
//            nameValuePair.add(new BasicNameValuePair(key, mData.get(key)));
//        }


        CloseableHttpClient httpclient = new CMN_HTTPSClient(MyApplication.INSTANCE, urls[0]).getClient();
        HttpPost httpPost = new HttpPost(urls[0]);

        JSONObject json = null;
        try {
            httpPost.setEntity(new StringEntity(mData, "utf-8"));
            httpPost.setHeader("Content-Type",
                    "application/x-www-form-urlencoded");
            HttpResponse response = httpclient.execute(httpPost);

            String result = EntityUtils.toString(response.getEntity());
            json = new JSONObject(result);


            //if (json == null) mActivity.throwToast("Unable to access the Network. Please check your Network connectivity and try again.");

            return json;
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

    protected void onPostExecute(JSONObject json) {
        //Log.v(TAG, "onPostExecute json: " + json);
//        mActivity.closeDialog();
        mActivity.gotUserInfoFromServer(json);
    }

    protected void onPreExecute() {
        //Log.e(TAG, "Creating Dialog");
        mActivity.showDialog();
    }
}
