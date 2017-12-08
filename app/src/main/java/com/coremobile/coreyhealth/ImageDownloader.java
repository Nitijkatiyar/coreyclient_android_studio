package com.coremobile.coreyhealth;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.coremobile.coreyhealth.clientcertificate.CMN_HTTPSClient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageDownloader {
    Context mContext;
    String mFileName;
    String mUrl;
    String TAG = "Corey_ImageDownloader";

    public ImageDownloader(Context context) {
        mContext = context;
    }

    public void download(String url, String filename) {
       /* BitmapDownloaderTask task = new BitmapDownloaderTask(filename);
        task.execute(url);*/
        mFileName = filename;
        mUrl = url;


        Runnable BitmapDownloadrunnable = new Runnable() {
            @Override
            public void run() {
                downloadBitmap(mUrl, mFileName);
            }
        };

        Thread BitmapDownloadThread = new Thread(BitmapDownloadrunnable);
        BitmapDownloadThread.start();


    }


    private void downloadBitmap(String url, String filename) {
        CloseableHttpClient httpclient = null;
        HttpGet getRequest = null;

        try {
            httpclient = new CMN_HTTPSClient(MyApplication.INSTANCE, url).getClient();
            getRequest = new HttpGet(url);

            org.apache.http.HttpResponse response = httpclient.execute(getRequest);
            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                Log.w("ImageDownloader", "Error " + statusCode
                        + " while retrieving bitmap from " + url);
                // return null;
            }

            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream inputStream = null;
                try {
                    inputStream = entity.getContent();
                    final Bitmap bitmap = BitmapFactory
                            .decodeStream(inputStream);
                    FileOutputStream fos = mContext.openFileOutput(filename, Context.MODE_PRIVATE);
                    bitmap.compress(CompressFormat.PNG, 100, fos);
                    fos.flush();
                    fos.close();
                    bitmap.recycle();
                    //   return bitmap;
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    entity.consumeContent();
                    MyApplication.INSTANCE.ImagedownloadCount--;
                    Log.d(TAG, "download completed for" + filename);
                    Log.d(TAG, "ImagedownloadCount= " + MyApplication.INSTANCE.ImagedownloadCount);
                }

            }
        } catch (Exception e) {
            // Could provide a more explicit error message for IOException or
            // IllegalStateException
            if (getRequest != null) {
                getRequest.abort();
            }
            MyApplication.INSTANCE.ImagedownloadCount--;
            Log.w("ID", "Error while retrieving bitmap from "
                    + url + e.toString());
        }finally {
            if (httpclient != null) {
                try {
                    httpclient.close();
                } catch (IOException e) {
                    e.getMessage();
                }
            }
        }

        //   return null;
    }

/*    class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap>
    {
        String filename;

        public BitmapDownloaderTask(String filename)
        {
            this.filename = filename;
        }

        @Override
        // Actual download method, run in the task thread
        protected Bitmap doInBackground(String... params)
        {
            Log.i("ID","starting Image download" );
            return downloadBitmap(params[0],this.filename);
        }

        @Override
        // Once the image is downloaded, associates it to the imageView
        protected void onPostExecute(Bitmap bitmap)
        {
            Log.i("ImageDownloader","completed imagedownload");
            if (isCancelled())
            {
                Log.i("ImageDownloader","isCancelled");
                bitmap = null;
            }

        }
    } */
}


