package com.coremobile.coreyhealth;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


public class SalesforceOAuthActivity extends Activity implements OnClickListener, IServerConnect{
    private static final String TAG = "Corey_SalesforceOAuthActivity";
    String mConsumerKey=null;
    String mCallbackUrl=null;
    Button mGrantAccess;
    Context context;
    
    String userName;
    String appName;
    String appDisplayText;
    String appDomain;
    String password;
    String appUsername;
    String appPassword;
    String mHint;
    String mConsumerSecret;
    String mRequestTokenURL;
    String mAuthorizeURL;
    Button mButton_skip_continue;
    TextView mOAuth_Status;
    public static final String CURRENT_USER = "CurrentUser";
    public ProgressDialog mDialog;
    SharedPreferences mcurrentUserPref;
    private final static int SALESFORCE_ACCESS = 0;
    private static final int SUCCESS = 1;
    private static final int FAILED = 2;
    
    @Override
    protected void onResume() {
        super.onResume();
        if (MyApplication.shouldResetApp()) {
            finish();
            return;
        }
    }
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.linkedinauthentication);
        mcurrentUserPref = getSharedPreferences(CURRENT_USER, MODE_PRIVATE);
        String authHint = getIntent().getStringExtra("help");
        TextView auth_app_hint = (TextView)findViewById(R.id.oauth_Help);
        auth_app_hint.setText(authHint);
        mOAuth_Status = (TextView)findViewById(R.id.oauth_status);
        mButton_skip_continue = (Button)findViewById(R.id.skip_oauth);
        mButton_skip_continue.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                ++MyApplication.INSTANCE.mAuthWFCount;
                finish();
                
            }
        });
        mGrantAccess = (Button)this.findViewById(R.id.accessButton);
        mGrantAccess.setOnClickListener(this);

        context=getApplicationContext();
        mConsumerKey=(this.getResources().getString(R.string.consumerKey).toString());
        //mCallbackUrl=(this.getResources().getString(R.string.callbackUrl).toString());
        //mCallbackUrl = "https://login.salesforce.com/services/oauth2/success";
        if(getIntent().hasExtra("domain")){
            //Log.i(TAG,"has extras");
            appName = getIntent().getStringExtra("appName");
            appDisplayText = getIntent().getStringExtra("appDisplayText");
            appDomain = getIntent().getStringExtra("domain");
            mHint = getIntent().getStringExtra("help");
            mConsumerKey = getIntent().getStringExtra("ConsumerKey");
            //Log.i(TAG,"consumer key: " + mConsumerKey);
            mCallbackUrl = getIntent().getStringExtra("Callback");
            //Log.i(TAG,"callback url: " + mCallbackUrl);
            mConsumerSecret = getIntent().getStringExtra("ConsumerSecret");
            mRequestTokenURL = getIntent().getStringExtra("RequestTokenURL");
            mAuthorizeURL = getIntent().getStringExtra("AuthorizeURL");
            //Log.i(TAG,"ConsumerKey: " + mConsumerKey + " CallbackURL: " + mCallbackUrl);
        }

        Utils.setAppTitle(this, appDisplayText);

        if(mcurrentUserPref.getString(appName.toLowerCase()+"Login", "") != "") {
            updateViews(R.string.auth_success, R.string.reauthorize_app, R.string.cont);
        } else {
            updateViews(R.string.credentials_needed, R.string.authorize_app, R.string.skip);
        }
    }

    private void updateViews(int statusRes, int accessBtnRes, int skipBtnRes) {
        mOAuth_Status.setText(
            Utils.makeAuthStatusString(this, statusRes, appDisplayText));
        mGrantAccess.setText(
            Utils.makeAuthStatusString(this, accessBtnRes, appDisplayText));
        mButton_skip_continue.setText(skipBtnRes);
    }

    public void onClick(View v) {
        Intent intent = new Intent(SalesforceOAuthActivity.this, SForceOauth.class);
        intent.putExtra("ConsumerKey", mConsumerKey);
        intent.putExtra("ConsumerSecret", mConsumerSecret);
        intent.putExtra("Callback", mCallbackUrl);
        intent.putExtra("RequestTokenURL", mRequestTokenURL);
        intent.putExtra("AuthorizeURL", mAuthorizeURL);
        intent.putExtra("appName",appName);
        intent.putExtra("domain", appDomain);
        startActivityForResult(intent, SALESFORCE_ACCESS);
        
    }
    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Log.i(TAG,"onActivityResult requestCode: " + requestCode + " resultCode" + resultCode);
        switch(requestCode){
        case SALESFORCE_ACCESS:
            if(resultCode == RESULT_OK){                
                int status = data.getIntExtra("Status", 1);
                Log.d(TAG,"onActivityResult status: " + status);
                if(status == SUCCESS){
                    updateViews(R.string.auth_success, R.string.reauthorize_app, R.string.cont);
                }else if(status == FAILED){
                    updateViews(R.string.auth_failure, R.string.authorize_app, R.string.skip);
                }
            }else if(resultCode == RESULT_CANCELED){
                Log.d(TAG,"resultCode == RESULT_CANCELED");
            }
        }
    }
    
    @Override
    public void gotUserInfoFromServer(JSONObject json) {
        if(json!= null){
            //Log.v(TAG,"json: " + json);
            try {
                //Log.i(TAG,"result: " + json.getString("result"));
                String result = json.getString("result");
                String resultText = json.getString("text");
                //Log.i(TAG,"json data: " + json.names());
                if(result.equals("0")){
                    updateViews(R.string.auth_success, R.string.reauthorize_app, R.string.cont);
                    SharedPreferences.Editor editor = mcurrentUserPref.edit();
                    editor.putString(appName + "Login", appUsername);
                    //editor.putString(appName + "Password", appPassword);
                    editor.putString(appName + "Context", appDomain);
                    editor.commit();
                }
                else {
                    closeDialog();
                    updateViews(R.string.auth_failure, R.string.authorize_app, R.string.skip);
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
        if(mDialog != null) {
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
