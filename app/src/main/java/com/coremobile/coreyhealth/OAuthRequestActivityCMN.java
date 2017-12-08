package com.coremobile.coreyhealth;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.coremobile.coreyhealth.googleAnalytics.CMN_Preferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

public class OAuthRequestActivityCMN extends BaseActivityCMN implements IServerConnect {
    private static final String TAG = "Corey_OAuthRequestActivityCMN";
    public static final String CURRENT_USER = "CurrentUser";
    SharedPreferences mcurrentUserPref;

    public static final String CONSUMER_KEY = "anonymous";//"btspks31dfu6";
    public static final String CONSUMER_SECRET = "anonymous";//"cWJpHJQb3wUKhNyb";"cWJpHJQb3wUKhNyb";

    public static final String USER_TOKEN = "user_token";
    public static final String USER_SECRET = "user_secret";

    public static final String SCOPE = "http://www.google.com/calendar/feeds/ http://www.google.com/m8/feeds/";/*https://www.googleapis.com/auth/userinfo.profile*/
    public static final String REQUEST_URL = "https://www.google.com/accounts/OAuthGetRequestToken";
    public static final String ACCESS_URL = "https://www.google.com/accounts/OAuthGetAccessToken";
    public static final String AUTHORIZE_URL = "https://www.google.com/accounts/OAuthAuthorizeToken";

    public static final String ENCODING = "UTF-8";

    public static final String OAUTH_CALLBACK_SCHEME = "google-oauth-corey";
    public static final String OAUTH_CALLBACK_HOST = "callback";
    public static final String OAUTH_CALLBACK_URL = OAUTH_CALLBACK_SCHEME + "://" + OAUTH_CALLBACK_HOST;

    private static final int SUCCESS = 1;
    private static final int FAILED = 2;
    private static final int ACCESS_DENIED = 3;

    private OAuthConsumer consumer;
    private OAuthProvider provider;
    private String CMN_SERVER_BASE_URL_DEFINE;
    private final static String CMN_SERVER_APP_REGISTER_API = "UpdateLoginDetails.aspx?";
    private boolean mWaitingForServer = false;


    String appName;
    String appDomain;
    String appUsername;
    String appPassword;
    String mConsumerKey = null;
    String mCallbackUrl = null;
    String mConsumerSecret;
    String mRequestTokenURL;
    String mAuthorizeURL;
    String mAccessTokenURL;
    String mScope;
    Button mButton_skip_continue;
    TextView mOAuth_Status;
    PendingIntent mActivityResultintent;
    public ProgressDialog mDialog;

    @Override
    protected void onResume() {
        super.onResume();
        if (MyApplication.shouldResetApp()) {
            finish();
            return;
        }

        if (mWaitingForServer) {
            mWaitingForServer = false;
            try {
                mActivityResultintent.send(getApplicationContext(), Activity.RESULT_CANCELED, null);
            } catch (CanceledException e) {
                e.getMessage();
            }
            finish();
        }
    }

    protected void onPause() {
        super.onPause();
        mWaitingForServer = true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        Log.d(TAG, "onCreate()");
        setContentView(R.layout.sforce_oauth_layout);
        mcurrentUserPref = getSharedPreferences(CURRENT_USER, MODE_PRIVATE);
        mActivityResultintent = (PendingIntent) getIntent().getParcelableExtra("PendingIntent");
        if (getIntent().hasExtra("ConsumerKey")) {
            appName = getIntent().getStringExtra("appName");
            appDomain = getIntent().getStringExtra("domain");
            if (getIntent().hasExtra("ConsumerKey")) {
                mConsumerKey = getIntent().getStringExtra("ConsumerKey");
            }
            //Log.i(TAG,"consumer key: " + mConsumerKey);
            mCallbackUrl = getIntent().getStringExtra("Callback");
            mConsumerSecret = getIntent().getStringExtra("ConsumerSecret");
            mRequestTokenURL = getIntent().getStringExtra("RequestTokenURL");
            mAuthorizeURL = getIntent().getStringExtra("AuthorizeURL");
            Log.d(TAG, "ConsumerKey: " + mConsumerKey + " CallbackURL: " + mCallbackUrl);
        }
        JSONHelperClass jsonHelperClass = new JSONHelperClass();
        CMN_SERVER_BASE_URL_DEFINE = jsonHelperClass.getBaseURL(mcurrentUserPref.getString("Organization", ""));
        try {
            consumer = new CommonsHttpOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
            provider = new CommonsHttpOAuthProvider(
                    REQUEST_URL + "?scope=" + URLEncoder.encode(SCOPE, ENCODING),
                    ACCESS_URL,
                    AUTHORIZE_URL);
            provider.setOAuth10a(true);
            mcurrentUserPref = getSharedPreferences(CURRENT_USER, MODE_PRIVATE);
            String authUrl = provider.retrieveRequestToken(consumer, OAUTH_CALLBACK_URL);

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl)).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_FROM_BACKGROUND);
            this.startActivity(intent);

        } catch (OAuthMessageSignerException e) {
            e.getMessage();
            Log.d(TAG, "Oauth exception");
        } catch (OAuthNotAuthorizedException e) {
            e.getMessage();
            Log.d(TAG, "Oauth exception");
        } catch (OAuthExpectationFailedException e) {
            e.getMessage();
            Log.d(TAG, "Oauth exception");
        } catch (OAuthCommunicationException e) {
            e.getMessage();
            Log.d(TAG, "Oauth exception");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.getMessage();
            Log.d(TAG, "Oauth exception");
        }

        //getRequestToken();
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mcurrentUserPref = getSharedPreferences(CURRENT_USER, MODE_PRIVATE);
        final Uri uri = intent.getData();
        if (uri != null && uri.getScheme().equals(OAUTH_CALLBACK_SCHEME)) {
            mWaitingForServer = false;
            //Log.i(TAG, "Callback received : " + uri);
            //Log.i(TAG, "Retrieving Access Token");
            getAccessToken(uri);
        }
    }

    private void getAccessToken(Uri uri) {
        String otoken = uri.getQueryParameter(OAuth.OAUTH_TOKEN);
        String verifier = uri.getQueryParameter(OAuth.OAUTH_VERIFIER);

        if (otoken == null || verifier == null) {
            Log.d(TAG, "getAccessToken: otoken or verifier is null => user cancelled authorization");
            try {
                mActivityResultintent.send(getApplicationContext(), Activity.RESULT_CANCELED, null);
            } catch (CanceledException e) {
                e.getMessage();
            }
            finish();
        }

        String accessToken = null;
        String accessTokenSecret = null;
        Exception exception = null;
        try {
            provider.retrieveAccessToken(consumer, verifier);
            accessToken = consumer.getToken();
            accessTokenSecret = consumer.getTokenSecret();
        } catch (OAuthMessageSignerException e) {
            exception = e;
        } catch (OAuthNotAuthorizedException e) {
            exception = e;
        } catch (OAuthExpectationFailedException e) {
            exception = e;
        } catch (OAuthCommunicationException e) {
            exception = e;
        }
        if (exception != null) {
            Log.d(TAG, "OAuth - Access Token Retrieval Error", exception);
            accessToken = null;
            accessTokenSecret = null;
        }

        saveAuthInformation(mcurrentUserPref, accessToken, accessTokenSecret);
        sendAccessTokenToServer(appDomain, appName);
    }

    public static void saveAuthInformation(SharedPreferences settings, String token, String secret) {
        // null means to clear the old values
        SharedPreferences.Editor editor = settings.edit();
        if (token == null) {
            editor.remove(USER_TOKEN);
            //Log.d(TAG, "Clearing OAuth Token");
        } else {
            editor.putString(USER_TOKEN, token);
            //Log.d(TAG, "Saving OAuth Token: " + token);
        }
        if (secret == null) {
            editor.remove(USER_SECRET);
            //Log.d(TAG, "Clearing OAuth Secret");
        } else {
            editor.putString(USER_SECRET, secret);
            //Log.d(TAG, "Saving OAuth Secret: " + secret);
        }
        editor.commit();

    }

    private void sendAccessTokenToServer(String appDomain, String appName) {
        String organizationName = mcurrentUserPref.getString("Organization", null);
        appUsername = mcurrentUserPref.getString("user_token", "");
        appPassword = mcurrentUserPref.getString("user_secret", "");
         /*String url = String.format(CMN_SERVER_BASE_URL_DEFINE + CMN_SERVER_APP_REGISTER_API + "username=%s&password=%s&appUsername=%s&appPassword=%s&appName=%s&appContext=%s&appContext1=%s",
                userName,password,appUsername,appPassword,appName,"",appDomain);*/
        String url = CMN_SERVER_BASE_URL_DEFINE + CMN_SERVER_APP_REGISTER_API;
        Log.d(TAG, "url: " + url);

        HashMap<String, String> data = new HashMap<String, String>();
        data.put("token", CMN_Preferences.getUserToken(OAuthRequestActivityCMN.this));
        data.put("appUsername", appUsername);
        data.put("appPassword", appPassword);
        data.put("appName", appName);
        data.put("appContext", "");
        data.put("appContext1", appDomain);
        //Log.i(TAG,"url: " + url);
        new VerifyUser(OAuthRequestActivityCMN.this, data).execute(url);
    }

    @Override
    public void gotUserInfoFromServer(JSONObject json) {

        if (json != null) {
            //Log.v(TAG,"json: " + json);
            try {
                SharedPreferences.Editor editor = mcurrentUserPref.edit();
                Intent resultIntent = new Intent();
                try {
                    String result = json.getString("result");
                    Log.d(TAG, "result: " + json.getString("result"));
                    resultIntent.setClass(getApplicationContext(), GoogleOauthActivity.class);
                    if (result.equals("0")) {
                        Log.d(TAG, "AuthStatus: SUCCESS");
                        boolean userGrantedAccess = !(
                                mcurrentUserPref.getString(USER_TOKEN, "").equals("") ||
                                        mcurrentUserPref.getString(USER_SECRET, "").equals("")
                        );

                        Log.d(TAG, "USER GRANTED ACCESS? " + userGrantedAccess);

                        editor.putString(appName.toLowerCase() + "Login", appUsername);
                        //editor.putString(appName.toLowerCase() + "Password", appPassword);
                        editor.putString(appName.toLowerCase() + "Context", "");
                        editor.putString(appName.toLowerCase() + "AuthStatus",
                                userGrantedAccess ? "Successful" : "Access Denied");
                        editor.commit();
                        resultIntent.putExtra("Status", userGrantedAccess ? SUCCESS : ACCESS_DENIED);
                        mActivityResultintent.send(getApplicationContext(), Activity.RESULT_OK, resultIntent);
                        finish();
                    } else {
                        Log.d(TAG, "AuthStatus: FAILED");
                        editor.putString(appName + "AuthStatus", "Failed");
                        editor.commit();
                        resultIntent.putExtra("Status", FAILED);
                        mActivityResultintent.send(getApplicationContext(), Activity.RESULT_OK, resultIntent);
                        finish();
                    }

                } catch (JSONException e) {
                    e.getMessage();
                    Log.d(TAG, "AuthStatus: FAILED - JSONException");
                    editor.putString(appName + "AuthStatus", "Error");
                    editor.commit();
                    resultIntent.putExtra("Status", FAILED);
                    mActivityResultintent.send(getApplicationContext(), Activity.RESULT_OK, resultIntent);
                    finish();
                }
            } catch (CanceledException e) {
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
                "Validating application credentials...", true);

    }

    @Override
    public void closeDialog() {
        if (isFinishing()) {
            return;
        }
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }

    }

    @Override
    public void throwToast(String stringToToast) {
        if (isFinishing()) {
            return;
        }
    }


}


