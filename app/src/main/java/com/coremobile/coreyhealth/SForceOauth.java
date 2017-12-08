package com.coremobile.coreyhealth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.coremobile.coreyhealth.clientcertificate.CMN_HTTPSClient;
import com.coremobile.coreyhealth.googleAnalytics.CMN_Preferences;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//import android.util.Log;

public class SForceOauth extends BaseActivityCMN implements IServerConnect {
    //private static final String TAG = "Corey_ActivityWebView";

    /**
     * Get these values after registering your oauth app at: https://foursquare.com/oauth/
     */
    public static final String CALLBACK_URL = "https%3A%2F%2Flocalhost:8443%2FRestTest%2Foauth%2F_callback";//https%3A%2F%2Flogin.salesforce.com%2Fservices%2Foauth2%2Fsuccess
    public static final String CLIENT_ID = "3MVG9QDx8IX8nP5QZaDPLcCZJnfUuA0LIctkfcE2h9tg9Vo7NttogQYb.2iQ7YwUIP1_PeUHrkf8wTFVcO7UT";
    private static final int SUCCESS = 1;
    private static final int FAILED = 2;
    private static String TAG = "Corey_SForceOauth";
    private static String LOGIN_URL = "https://login.salesforce.com/services/oauth2/authorize?type=web_server&response_type=code&client_id=";

    String appName;
    String appDomain;

    String appUsername;
    String appPassword;
    String mHint;
    String mConsumerSecret;
    String mRequestTokenURL;
    String mAuthorizeURL;
    String mConsumerKey;
    String mCallbackURL;
    public static final String CURRENT_USER = "CurrentUser";
    public ProgressDialog mDialog;
    SharedPreferences mcurrentUserPref;
    WebView webview;

    private String CMN_SERVER_BASE_URL_DEFINE;
    private final static String CMN_SERVER_APP_REGISTER_API = "UpdateLoginDetails.aspx?";

    @Override
    protected void onResume() {
        super.onResume();
        if (MyApplication.shouldResetApp()) {
            finish();
            return;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sforce_oauth_layout);
        mcurrentUserPref = getSharedPreferences(CURRENT_USER, MODE_PRIVATE);
        if (getIntent().hasExtra("ConsumerKey")) {
            appName = getIntent().getStringExtra("appName");
            appDomain = getIntent().getStringExtra("domain");
            mConsumerKey = getIntent().getStringExtra("ConsumerKey");
            //Log.i(TAG,"consumer key: " + mConsumerKey);
            mCallbackURL = getIntent().getStringExtra("Callback");
            mConsumerSecret = getIntent().getStringExtra("ConsumerSecret");
            mRequestTokenURL = getIntent().getStringExtra("RequestTokenURL");
            mAuthorizeURL = getIntent().getStringExtra("AuthorizeURL");
            //Log.i(TAG,"ConsumerKey: " + mConsumerKey + " CallbackURL: " + mCallbackUrl);
        }
        JSONHelperClass jsonHelperClass = new JSONHelperClass();
        CMN_SERVER_BASE_URL_DEFINE = jsonHelperClass.getBaseURL(mcurrentUserPref.getString("Organization", ""));
        String url = null;
        try {
            url = LOGIN_URL +
                    mConsumerKey + "&redirect_uri=" + URLEncoder.encode(mCallbackURL, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.getMessage();
        }
        // We can override onPageStarted() in the web client and grab the token out.

        webview = (WebView) findViewById(R.id.webView);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(new WebViewClient() {
            boolean callbackCompleted = false;

            public void onPageStarted(WebView view, String url1, Bitmap favicon) {
                Log.d(TAG, "onPageStarted mCallbackURL: " + mCallbackURL);
                final String url = url1;
                if (url.startsWith(mCallbackURL)) {
                    webview.setVisibility(View.INVISIBLE);
                    if (callbackCompleted) {
                        //Log.i(TAG, "Ignoring Redundant Callback");
                        return;
                    }
                    callbackCompleted = true;
                    String fragment = "code=";
                    int start = url.indexOf(fragment);
                    if (start > -1) {
                        // You can use the accessToken for api calls now.
                        Thread t = new Thread(new Runnable() {

                            @Override
                            public void run() {
                                String[] firstSplit = url.split("code=");
                                if (firstSplit.length > 0) {
                                    String data = firstSplit[1];
                                    String url2 = "https://login.salesforce.com/services/oauth2/token";
                                    CloseableHttpClient httpclient = new CMN_HTTPSClient(MyApplication.INSTANCE, url2).getClient();
                                    HttpPost httppost = new HttpPost(url2);

                                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                                    nameValuePairs.add(new BasicNameValuePair("type", "web_server"));
                                    try {
                                        nameValuePairs.add(new BasicNameValuePair("code", URLDecoder.decode(data, "UTF-8")));
                                        //Log.i(TAG,"decoded code: " + URLDecoder.decode(data, "UTF-8"));
                                        nameValuePairs.add(new BasicNameValuePair("grant_type", "authorization_code"));
                                        nameValuePairs.add(new BasicNameValuePair("client_id", mConsumerKey));
                                        nameValuePairs.add(new BasicNameValuePair("client_secret", mConsumerSecret));
                                        nameValuePairs.add(new BasicNameValuePair("redirect_uri", mCallbackURL));
                                        Log.d(TAG, "namevaluepair: " + nameValuePairs.toString());
                                        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValuePairs);
                                        entity.setContentEncoding(HTTP.UTF_8);
                                        httppost.setEntity(entity);
                                        httppost.setHeader("Content-Type", "application/x-www-form-urlencoded");
                                        // Log.i(TAG,"httppost: " + httppost);

                                        HttpResponse response = httpclient.execute(httppost);
                                        InputStream inputStream = null;
                                        JSONObject json = null;
                                        json = new JSONObject(EntityUtils.toString(response.getEntity()));
                                        parseJSON(json);


                                    } catch (UnsupportedEncodingException e1) {
                                        // TODO Auto-generated catch block
                                        e1.getMessage();
                                    } catch (IllegalStateException e) {
                                        // TODO Auto-generated catch block
                                        e.getMessage();
                                    } catch (IOException e) {
                                        // TODO Auto-generated catch block
                                        e.getMessage();
                                    } catch (JSONException e) {
                                        // TODO Auto-generated catch block
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

                                }
                            }
                        });

                        t.start();
                        sendAccessTokenToServer(appDomain, appName);

                    } //if (start > -1)

                } //if(url.startsWith
            } //onPageStarted

        }); //setWebViewClient
//        webview.loadUrl(url);
    } //oncreate

    private void parseJSON(JSONObject json) {
        SharedPreferences.Editor editor = mcurrentUserPref.edit();

        try {
            editor.putString(appName + "access_token", json.getString("access_token"));
            editor.putString(appName + "refresh_token", json.getString("refresh_token"));
            editor.putString(appName + "instance_url", json.getString("instance_url"));
            editor.commit();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.getMessage();
        }

    }

    private static String convertStreamToString(InputStream is) {
        /*
         * To convert the InputStream to String we use the BufferedReader.readLine()
         * method. We iterate until the BufferedReader return null which means
         * there's no more data to read. Each line will appended to a StringBuilder
         * and returned as String.
         */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.getMessage();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.getMessage();
            }
        }
        return sb.toString();
    }

    private void sendAccessTokenToServer(String appDomain, String appName) {

        appUsername = mcurrentUserPref.getString(appName + "access_token", "");
        appPassword = mcurrentUserPref.getString(appName + "refresh_token", "");
        appDomain = mcurrentUserPref.getString(appName + "instance_url", "");
        
        /*String url = String.format(CMN_SERVER_BASE_URL_DEFINE + CMN_SERVER_APP_REGISTER_API + "username=%s&password=%s&appUsername=%s&appPassword=%s&appName=%s&appContext=%s&appContext1=%s",
                userName,password,appUsername,appPassword,appName,appDomain,"");*/
        String url = CMN_SERVER_BASE_URL_DEFINE + CMN_SERVER_APP_REGISTER_API;
        //Log.i(TAG,"url: " + url);

        HashMap<String, String> data = new HashMap<String, String>();
        data.put("token", CMN_Preferences.getUserToken(SForceOauth.this));
        data.put("appUsername", appUsername);
        data.put("appPassword", appPassword);
        data.put("appName", appName);
        data.put("appContext", appDomain);
        data.put("appContext1", "");
        //Log.i(TAG,"url: " + url);

        new VerifyUser(SForceOauth.this, data).execute(url);
    }

    @Override
    public void gotUserInfoFromServer(JSONObject json) {
        if (json != null) {
            //Log.v(TAG,"json: " + json);
            try {
                //Log.i(TAG,"result: " + json.getString("result"));
                String result = json.getString("result");
                String resultText = json.getString("text");
                //Log.i(TAG,"json data: " + json.names());
                Intent resultIntent = new Intent();
                SharedPreferences.Editor editor = mcurrentUserPref.edit();
                if (result.equals("0")) {
                    //Log.i(TAG,"parsing json");
                    editor.putString(appName.toLowerCase() + "Login", appUsername);
                    //editor.putString(appName.toLowerCase() + "Password", appPassword);
                    editor.putString(appName.toLowerCase() + "Context", appDomain);
                    editor.putString(appName.toLowerCase() + "AuthStatus", "Successful");
                    editor.commit();
                    resultIntent.putExtra("Status", SUCCESS);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    closeDialog();
                    editor.putString(appName + "AuthStatus", "Failed");
                    editor.commit();
                    finish();
                    resultIntent.putExtra("Status", FAILED);
                    setResult(RESULT_OK, resultIntent);
                    Toast.makeText(getApplicationContext(), resultText, Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.getMessage();
            }
        }


    }

    @Override
    public void showDialog() {
        if (isFinishing()) {
            return;
        }
        mDialog = ProgressDialog.show(this, "",
                "Validating application credentials..", true);

    }

    @Override
    public void closeDialog() {
        if (isFinishing()) {
            return;
        }
        //Fix for Corey 696 start

        try {
            if (mDialog != null) {
                mDialog.dismiss();
                mDialog = null;
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void throwToast(String stringToToast) {
        if (isFinishing()) {
            return;
        }
    }


}
