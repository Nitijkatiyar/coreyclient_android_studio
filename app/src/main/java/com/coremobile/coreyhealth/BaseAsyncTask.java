package com.coremobile.coreyhealth;

import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by Aman on 8/10/2017.
 */

public class BaseAsyncTask extends AsyncTask<String,Void,String> {


    private AsyncTaskCompleteListener<String> callback;

    private Context context = null;

    private final HttpClient httpClient = new DefaultHttpClient();

    private String content = null;
    //private String error = null;
    private String finalResult = null;
    private static boolean isResult = false;

    String methodType;
    String body;

    public BaseAsyncTask(Context context,String methodType,AsyncTaskCompleteListener<String> callback,String body)
    {
        this.context = context;
        this.callback=callback;

        this.methodType=methodType;
        this.body=body;
    }

    protected void onPreExecute()
    {
        this.callback.onTaskStart();
          }

    protected String doInBackground(String... urls)
    {
        if(this.methodType.equalsIgnoreCase("GET"))
      return invokeGet(urls[0]);
        else
            return  invokePost(urls[0]);
    }

    public String invokeGet(String vUrl){
        BufferedReader reader;
        StringBuffer buffer;
        String res = null;

        try {
            URL url = new URL(vUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
//            con.setReadTimeout(40000);
//            con.setConnectTimeout(40000);
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");
            int status = con.getResponseCode();
            InputStream inputStream;
            if (status == HttpURLConnection.HTTP_OK) {
                inputStream = con.getInputStream();
            } else {
                inputStream = con.getErrorStream();
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            buffer = new StringBuffer();
            String line = "";
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            res = buffer.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    public String invokePost(String vUrl){

                Integer result = 0;
                String message = null;
                try {
                    // Create Apache HttpClient
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(vUrl);

                    httppost.setEntity(new StringEntity(this.body, "UTF8"));
                    httppost.setHeader("Content-type", "application/json");
                    HttpResponse httpResponse = httpclient.execute(httppost);
                    int statusCode = httpResponse.getStatusLine().getStatusCode();

                    // 200 represents HTTP OK
                    if (statusCode == 200) {
                        message = streamToString(httpResponse.getEntity().getContent());
                    } else {
                        message = "Failed";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    return message;
                }

            }
    protected void onPostExecute(String result)
    {
        finalResult = result;
        System.out.println("on Post execute called");
        callback.onTaskComplete(finalResult);
    }

    public boolean getIsResult()
    {
        return isResult;
    }

    public void setIsResult(boolean flag)
    {
        isResult = flag;
    }

    public String getResult()
    {
        return finalResult;
    }
    String streamToString(InputStream stream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
        String line;
        String result = "";
        while ((line = bufferedReader.readLine()) != null) {
            result += line;
        }
        // Close stream
        if (null != stream) {
            stream.close();
        }
        return result;
    }


}
