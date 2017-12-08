package com.coremobile.coreyhealth;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.coremobile.coreyhealth.clientcertificate.CMN_HTTPSClient;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;
import java.io.InputStream;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

    @Override
    protected Bitmap doInBackground(String... params) {
        Bitmap bitmap = null;
        InputStream in = null;
        BitmapFactory.Options bmOptions;
        bmOptions = new BitmapFactory.Options();
        bmOptions.inSampleSize = 1;
        CloseableHttpClient httpclient = new CMN_HTTPSClient(MyApplication.INSTANCE, params[0]).getClient();
        HttpResponse httpResponse = null;
        HttpRequestBase httpReq = new HttpGet(params[0]);

        try {
            httpResponse = httpclient.execute(httpReq);
            in = httpResponse.getEntity().getContent();
            bitmap = BitmapFactory.decodeStream(in, null, bmOptions);
            in.close();
        } catch (Exception e) {
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


        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);

    }


}
