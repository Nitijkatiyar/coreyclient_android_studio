package com.coremobile.coreyhealth.analytics;

import android.os.AsyncTask;
import android.util.Log;

import com.coremobile.coreyhealth.MyApplication;
import com.coremobile.coreyhealth.clientcertificate.CMN_HTTPSClient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class CMN_GetCSV extends AsyncTask<String, Void, ArrayList<String[]>> {

    // static final String TAG = "Corey_GETCSV";
    private CMN_IDownloadCSV mActivity;
    String TAG = "Corey_CMN_GetCSV";
    ArrayList<String[]> resultList = new ArrayList<String[]>();

    public CMN_GetCSV(CMN_IDownloadCSV _activity) {

        mActivity = _activity;
    }

    @Override
    protected ArrayList<String[]> doInBackground(String... params) {
        // TODO Auto-generated method stub
        //Log.i("TAG","doInBackground");

        StringBuilder builder = new StringBuilder();
        JSONObject jsonObject = null;
        CloseableHttpClient httpclient = new CMN_HTTPSClient(MyApplication.INSTANCE, params[0]).getClient();
        HttpGet httpGet = new HttpGet(params[0]);
        /*
         * setting socket timeout
         */
        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, 20000);
        HttpConnectionParams.setSoTimeout(httpParameters, 20000);
        httpGet.setParams(httpParameters);
        InputStream is = null;
        BufferedReader reader = null;
        try {
            HttpResponse response = httpclient.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();


                is = entity.getContent();
                reader = new BufferedReader(
                        new InputStreamReader(is));


                String csvLine;
                while ((csvLine = reader.readLine()) != null) {
                    String[] row = csvLine.split(",");
                    resultList.add(row);
                }

//                EntityUtils.consume(entity);
            } else {
                Log.d("CSV download file", "Failed to download file");
            }
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        } finally {
            if (is != null) {
                try {
                    is.close();
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (httpclient != null) {
                try {
                    httpclient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        return resultList;
    }

    protected void onPostExecute(ArrayList<String[]> resultList) {
        //Log.i("TAG", "onPostExecute" + resultList);

        mActivity.buildUI(resultList);
        for (String[] st : resultList) {
            int len = st.length;
            for (int i = 0; i < len; i++) {
                //Log.d(TAG, "string are " + st[i]);
            }
        }

    }


}
