package com.coremobile.coreyhealth;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.coremobile.coreyhealth.newui.MessageTabActivityCMN;

public class GoogleOauthActivity extends Activity implements OnClickListener
{
    private static final String TAG = "Corey_GoogleOauthActivity";
    public static final String CURRENT_USER = "CurrentUser";
    SharedPreferences mcurrentUserPref;

    public static final String CONSUMER_KEY     = "anonymous";//"btspks31dfu6";
    public static final String CONSUMER_SECRET  = "anonymous";//"cWJpHJQb3wUKhNyb";"cWJpHJQb3wUKhNyb";

    public static final String USER_TOKEN = "user_token";
    public static final String USER_SECRET = "user_secret";
    public static final String REQUEST_TOKEN = "request_token";
    public static final String REQUEST_SECRET = "request_secret";

    public static final String SCOPE            = "http://www.google.com/calendar/feeds/";
    public static final String REQUEST_URL      = "https://www.google.com/accounts/OAuthGetRequestToken";
    public static final String ACCESS_URL       = "https://www.google.com/accounts/OAuthGetAccessToken";
    public static final String AUTHORIZE_URL    = "https://www.google.com/accounts/OAuthAuthorizeToken";

    public static final String ENCODING         = "UTF-8";

    public static final String  OAUTH_CALLBACK_SCHEME   = "google-oauth-corey";
    public static final String  OAUTH_CALLBACK_HOST     = "callback";
    public static final String  OAUTH_CALLBACK_URL      = OAUTH_CALLBACK_SCHEME + "://" + OAUTH_CALLBACK_HOST;

    private final static int OAUTH_REQUEST = 0;
    private static final int SUCCESS = 1;
    private static final int FAILED = 2;
    private static final int ACCESS_DENIED = 3;

    String userName;
    String appName;
    String appDisplayText;
    String appDomain;
    String password;
    String appUsername;
    String appPassword;
    String mConsumerKey=null;
    String mCallbackUrl=null;
    String mConsumerSecret;
    String mRequestTokenURL;
    String mAuthorizeURL;
    String mAccessTokenURL;
    String mScope;
    String mHint;
    Button mButton_continue;
    Button mGrantAccess;
    TextView mOAuth_Status;

    public ProgressDialog mDialog;

    @Override
    protected void onResume()
    {
        super.onResume();
        if (MyApplication.shouldResetApp())
        {
            finish();
            return;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate()");
        mcurrentUserPref = getSharedPreferences(CURRENT_USER, MODE_PRIVATE);
        //Log.i(TAG,"AuthenticationStatus: " + getIntent().getStringExtra("AuthenticationStatus"));
        //String status = getIntent().getStringExtra("AuthenticationStatus");
        //Log.i(TAG,"status: " + status);
        Log.d(TAG,"hasExtra" + getIntent().hasExtra("domain"));
        if(getIntent().hasExtra("domain"))
        {
            //Log.i(TAG,"has extras");
            appName = getIntent().getStringExtra("appName");
            appDisplayText = getIntent().getStringExtra("appDisplayText");
            appDomain = getIntent().getStringExtra("domain");
            mHint = getIntent().getStringExtra("help");
            mConsumerKey = getIntent().getStringExtra("ConsumerKey");
            Log.d(TAG,"consumer key: " + mConsumerKey);
            mCallbackUrl = getIntent().getStringExtra("Callback");
            Log.d(TAG,"callback url: " + mCallbackUrl);
            mConsumerSecret = getIntent().getStringExtra("ConsumerSecret");
            mRequestTokenURL = getIntent().getStringExtra("RequestTokenURL");
            mAuthorizeURL = getIntent().getStringExtra("AuthorizeURL");
            //Log.i(TAG,"ConsumerKey: " + mConsumerKey + " CallbackURL: " + mCallbackUrl);
        }
        /*if(mcurrentUserPref.getString(appName.toLowerCase()+"Login", "") != ""){
               mOAuth_Status.setText("Authorization Successful!");
               mOAuth_Status.setVisibility(View.VISIBLE);
           }*/
        setContentView(R.layout.linkedinauthentication);
        Utils.setAppTitle(this, appDisplayText);
        String authHint = getIntent().getStringExtra("help");

        TextView auth_app_hint = (TextView)findViewById(R.id.oauth_Help);
        auth_app_hint.setText(authHint);
        mGrantAccess = (Button)findViewById(R.id.accessButton);
        mGrantAccess.setText(
            Utils.makeAuthStatusString(this, R.string.authorize_app, appDisplayText));

        mOAuth_Status = (TextView)findViewById(R.id.oauth_status);

        mOAuth_Status.setText(
            Utils.makeAuthStatusString(this, R.string.credentials_needed, appDisplayText));
        mButton_continue = (Button)findViewById(R.id.skip_oauth);
        mButton_continue.setVisibility(View.INVISIBLE);
        mButton_continue.setText(R.string.cont);
        mButton_continue.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                Log.d(TAG,"onClick");
                //setResult(RESULT_OK);
                finish();

            }
        });
        mGrantAccess.setOnClickListener(this);
    }

    public void onClick(View v)
    {
        Intent intent = new Intent(GoogleOauthActivity.this, OAuthRequestActivityCMN.class);
        intent.putExtra("PendingIntent", createPendingResult(OAUTH_REQUEST, intent, PendingIntent.FLAG_ONE_SHOT));
        intent.putExtra("ConsumerKey", mConsumerKey);
        intent.putExtra("ConsumerSecret", mConsumerSecret);
        intent.putExtra("Callback", mCallbackUrl);
        intent.putExtra("RequestTokenURL", mRequestTokenURL);
        intent.putExtra("AuthorizeURL", mAuthorizeURL);
        intent.putExtra("appName",appName);
        intent.putExtra("domain", appDomain);
        startActivityForResult(intent, OAUTH_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG,"onActivityResult requestCode: " + requestCode + " resultCode" + resultCode);
        switch(requestCode)
        {
        case OAUTH_REQUEST:
            if(resultCode == RESULT_OK)
            {
                int status = data.getIntExtra("Status", 1);
                int statusRes = R.string.credentials_needed;
                int accessBtnRes = R.string.authorize_app;
                int contBtnVisibility = View.INVISIBLE;

                Log.d(TAG,"onActivityResult status: " + status);
                if (status == SUCCESS)
                {
                    statusRes = R.string.auth_success;
                    accessBtnRes = R.string.reauthorize_app;
                    contBtnVisibility = View.VISIBLE;
                    ++MyApplication.INSTANCE.mAuthWFCount;
                    LocalPrefs.INSTANCE.setHasAppCredentials(true);
                }
                else if (status == FAILED)
                {
                    statusRes = R.string.auth_failure;
                    accessBtnRes = R.string.authorize_app;
                }
                else if (status == ACCESS_DENIED)
                {
                    statusRes = R.string.auth_denied;
                    accessBtnRes = R.string.authorize_app;
                }
                mOAuth_Status.setText(
                    Utils.makeAuthStatusString(this, statusRes, appDisplayText));
                mGrantAccess.setText(
                    Utils.makeAuthStatusString(this, accessBtnRes, appDisplayText));
                mButton_continue.setVisibility(contBtnVisibility);
            }
            else if(resultCode == RESULT_CANCELED)
            {
                Log.d(TAG,"resultCode == RESULT_CANCELED");
            }
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event)
    {
        if (event.getAction() == KeyEvent.ACTION_UP)
        {
            switch(event.getKeyCode())
            {
            case KeyEvent.KEYCODE_BACK:
                Intent nextActivity = new Intent(GoogleOauthActivity.this, MessageTabActivityCMN.class);
                nextActivity.setAction("com.for.view");
                nextActivity.putExtra("CallerTab", 0);
                nextActivity.putExtra("Position", 0);
                nextActivity.putExtra("isFromList", true);
                startActivity(nextActivity);
            }
        }
        return super.dispatchKeyEvent(event);
    }
}
