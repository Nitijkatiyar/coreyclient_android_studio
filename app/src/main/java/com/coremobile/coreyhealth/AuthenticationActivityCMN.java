package com.coremobile.coreyhealth;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.coremobile.coreyhealth.errorhandling.ActivityPackage;
import com.coremobile.coreyhealth.googleAnalytics.CMN_Preferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;



public class AuthenticationActivityCMN extends BaseActivityCMN implements IServerConnect {
    private static String TAG = "Corey_AuthenticationActivityCMN";

    String organizationName;

    TextView auth_appname;
    TextView auth_app_hint;
    EditText input_field1;
    EditText input_field2;
    EditText input_field3;
    Button auth_submit;
    Button mButton_skip_continue;
    String appUsername;
    String appPassword;
    String appContext;
    String appDomain;
    SharedPreferences mcurrentUserPref;
    public static final String CURRENT_USER = "CurrentUser";
    public ProgressDialog mDialog;
    private String CMN_SERVER_BASE_URL_DEFINE;
    private final static String CMN_SERVER_APP_REGISTER_API = "Update////Log.nDetails.aspx?";
    JSONHelperClass jsonhelperClass;
    OrganizationData mOrganizationData;
    ApplicationData mAppData;
    int mCurAppIndex = 0;
    int mNumApps;

    private static final int SALESFORCE_ACTIVITY = 0;
    private static final int LINKEDIN_ACTIVITY = 1;
    private static final int ASYNCTEXT_DIALOG = 2;
    private static final int GCAL_ACTIVITY = 3;

    @Override
    public void onResume() {
        super.onResume();
        if (MyApplication.shouldResetApp()) {
            finish();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ////Log.i(TAG, "onCreate");
        setContentView(R.layout.authenticationlayout);
        MyApplication.INSTANCE.mAuthWFCount = 0; // to be incremented at the finish of every step

        mcurrentUserPref = getSharedPreferences(CURRENT_USER, 0);
        jsonhelperClass = new JSONHelperClass();
        CMN_SERVER_BASE_URL_DEFINE = jsonhelperClass.getBaseURL(organizationName);
        ////Log.i(TAG, "organization name: " + organizationName);
        mOrganizationData = jsonhelperClass.getOrganization(organizationName);

        ArrayList<ApplicationData> allApplicationData = new ArrayList<ApplicationData>();

        if ((organizationName == null) || (organizationName.trim().length() == 0)) {
            MyApplication.resetApp(true);
            finish();
            //Toast.makeText(getApplicationContext(), "Unrecoverable Error detected.\nPlease Signout, Refresh Configuration and Signin again.", Toast.LENGTH_LONG).show();
            return;
        }
        allApplicationData = jsonhelperClass.getAllApplicationData(organizationName);
        if (allApplicationData == null) {
            MyApplication.resetApp(true);
            finish();
            //Toast.makeText(getApplicationContext(), "Unrecoverable Error detected.\nPlease Signout, Refresh Configuration and Signin again.", Toast.LENGTH_LONG).show();
            return;
        }
        mNumApps = jsonhelperClass.getAllApplicationData(organizationName).size();
        ////Log.i(TAG, "mNumApps:" + mNumApps);
        auth_app_hint = (TextView) findViewById(R.id.auth_app_hint);
        input_field1 = (EditText) findViewById(R.id.auth_inputfield1);
        input_field2 = (EditText) findViewById(R.id.auth_inputfield2);
        input_field3 = (EditText) findViewById(R.id.auth_inputfield3);
        addClearButton(input_field1);
        addClearButton(input_field2);
        addClearButton(input_field3);
        auth_submit = (Button) findViewById(R.id.auth_submit);
        mButton_skip_continue = (Button) findViewById(R.id.skip_auth);
        mButton_skip_continue.setText("Continue");
        mButton_skip_continue.setVisibility(View.GONE);
        populateView();
        auth_submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (Utils.checkInternetConnection()) {
                    InputMethodManager inputMgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMgr.hideSoftInputFromWindow(input_field3.getWindowToken(), 0);
                    appUsername = input_field1.getText().toString().trim();
                    appPassword = input_field2.getText().toString().trim();
                    appContext = input_field3.getText().toString().trim();
                    if (getAppData().isAsynchronous) {
                        showDialog(ASYNCTEXT_DIALOG);
                    } else {
                        verifyAppCredentials();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Unable to access the Network. Please check your Network connectivity and try again.", Toast.LENGTH_LONG).show();
                }


            }
        });
        mButton_skip_continue.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                processNextApp();

            }
        });
    }

    private void verifyAppCredentials() {
        appDomain = getAppData().mDomain;
        /*////Log.i(TAG,"CMN_SERVER_BASE_URL_DEFINE + CMN_SERVER_APP_REGISTER_API" +
                "username=%s&password=%s&appUsername=%s&appPassword=%s&appName=%s&appContext=%s&appContext1=%s",userName,password,appUsername,appPassword,appContext,appDomain);*/
        String url = null;
        /*try {
            url = String.format(CMN_SERVER_BASE_URL_DEFINE + CMN_SERVER_APP_REGISTER_API + "username=%s&password=%s&appUsername=%s&appPassword=%s&appName=%s&appContext=%s&appContext1=%s",
                    userName,password,URLEncoder.encode(appUsername,"UTF-8"),URLEncoder.encode(appPassword,"UTF-8"),getAppData().mApplicationName,URLEncoder.encode(appContext,"UTF-8"),appDomain);
            
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            //e.getMessage();
        }*/
        url = CMN_SERVER_BASE_URL_DEFINE + CMN_SERVER_APP_REGISTER_API;
        //////Log.i(TAG,"url: " + url);

        HashMap<String, String> data = new HashMap<String, String>();
        data.put("token", CMN_Preferences.getUserToken(AuthenticationActivityCMN.this));
        data.put("appUsername", appUsername);
        data.put("appPassword", appPassword);
        data.put("appName", getAppData().mApplicationName);
        data.put("appContext", appContext);
        data.put("appContext1", appDomain);
        new VerifyUser(AuthenticationActivityCMN.this, data).execute(url);
    }

    private void populateView() {
        input_field1.clearFocus();
        input_field2.clearFocus();
        input_field3.clearFocus();
        input_field1.setText("");
        input_field2.setText("");
        input_field3.setText("");

        mAppData = mOrganizationData.applications.get(mCurAppIndex);
        if (mCurAppIndex > 0) mButton_skip_continue.setVisibility(View.VISIBLE);
        if (mAppData.hasAuthentication()) {
            ////Log.i(TAG, "appName: " + mAppData.mApplicationName + " authType: " + mAppData.mAuthType);
            if (mAppData.mAuthType.equals("traditional")) {
                Utils.setAppTitle(this, mAppData.mApplicationName);
                auth_app_hint.setText(mAppData.mHint);
                //////Log.i("AuthenticationActivityCMN", "textsize: " + auth_app_hint.getTextSize());
                //////Log.i(TAG,"appName: " + mAppData.mApplicationName.toLowerCase());
                //////Log.i(TAG,"data: " + mcurrentUserPref.getString(mAppData.mApplicationName.toLowerCase() + "////Log.n", null));
                //////Log.i(TAG,"New User: " + mcurrentUserPref.getBoolean("NewUser", true));

                input_field1.setHint(mAppData.credentialData.get(0).mCredentialDisplayText);
                input_field2.setHint(mAppData.credentialData.get(1).mCredentialDisplayText);
                input_field3.setHint(mAppData.credentialData.get(2).mCredentialDisplayText);

                if (mcurrentUserPref.getString(mAppData.mApplicationName.toLowerCase() + "////Log.n", null) != null)
                    input_field1.setText(mcurrentUserPref.getString(mAppData.mApplicationName.toLowerCase() + "////Log.n", null));
                else if (mAppData.credentialData.get(0).mDefault != null)
                    input_field1.setText(mAppData.credentialData.get(0).mDefault);
                if (mcurrentUserPref.getString(mAppData.mApplicationName.toLowerCase() + "Password", null) != null)
                    input_field2.setText(mcurrentUserPref.getString(mAppData.mApplicationName.toLowerCase() + "Password", null));
                else if (mAppData.credentialData.get(1).mDefault != null)
                    input_field2.setText(mAppData.credentialData.get(1).mDefault);
                if (mcurrentUserPref.getString(mAppData.mApplicationName.toLowerCase() + "Context", null) != null)
                    input_field3.setText(mcurrentUserPref.getString(mAppData.mApplicationName.toLowerCase() + "Context", null));
                else if (mAppData.credentialData.get(2).mDefault != null)
                    input_field3.setText(mAppData.credentialData.get(2).mDefault);

                View view = findViewById(android.R.id.content).getRootView();
                view.invalidate();

            } else if (mAppData.mAuthType.equals("oauth")) {
                ////Log.i(TAG, "appName: " + mAppData.mApplicationName);
                if (mAppData.mApplicationName.equalsIgnoreCase("GoogleCalendar")) {
                    ////Log.i(TAG, "GoogleCalendar");
                    Intent intent = new Intent().setClassName(getPackageName(), ActivityPackage.GoogleOauthActivity);
                    intent.putExtra("help", mAppData.mHint);
                    intent.putExtra("domain", getAppData().mDomain);
                    intent.putExtra("appName", getAppData().mApplicationName);
                    intent.putExtra("appDisplayText", "Google Calendar");
                    intent.putExtra("ConsumerKey", getAppData().mConsumerKey);
                    intent.putExtra("ConsumerSecret", getAppData().mConsumerSecret);
                    intent.putExtra("Callback", getAppData().mCallback);
                    intent.putExtra("RequestTokenURL", getAppData().mRequestTokenURL);
                    intent.putExtra("AccessTokenURL", getAppData().mAccessTokenURL);
                    intent.putExtra("AuthorizeURL", getAppData().mAuthorizeURL);
                    intent.putExtra("Scope", getAppData().mScope);
                    startActivityForResult(intent, GCAL_ACTIVITY);
                }
            } else if (mAppData.mAuthType.equals("oauth2")) {
                if (mAppData.mApplicationName.equalsIgnoreCase("salesforce")) {
                    Intent intent = new Intent().setClassName(getPackageName(),ActivityPackage.SalesforceOAuthActivity);
                    intent.putExtra("help", mAppData.mHint);
                    intent.putExtra("domain", getAppData().mDomain);
                    intent.putExtra("appName", getAppData().mApplicationName);
                    intent.putExtra("appDisplayText", "Salesforce");
                    intent.putExtra("ConsumerKey", mAppData.mConsumerKey);
                    intent.putExtra("ConsumerSecret", mAppData.mConsumerSecret);
                    intent.putExtra("RequestTokenURL", mAppData.mRequestTokenURL);
                    intent.putExtra("Callback", mAppData.mCallback);
                    intent.putExtra("AuthorizeURL", mAppData.mAuthorizeURL);
                    startActivityForResult(intent, SALESFORCE_ACTIVITY);
                }
            }
        } else {
            processNextApp();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ////Log.i(TAG, "onActivityResult: " + requestCode + " " + resultCode);
        switch (requestCode) {
            case GCAL_ACTIVITY:
            case SALESFORCE_ACTIVITY:
                processNextApp();
                break;
            case LINKEDIN_ACTIVITY:
                finish();
                break;
        }
    }

    private ApplicationData getAppData() {
        ApplicationData appData;
        if (mOrganizationData.applications.size() > 0) {
            appData = jsonhelperClass.getApplicationData(mCurAppIndex, organizationName);
            return appData;
        }
        return null;
    }

    @Override
    public Dialog onCreateDialog(int id) {
        switch (id) {
            case ASYNCTEXT_DIALOG:
                return new AlertDialog.Builder(this)
                        .setMessage(getAppData().mAsyncText)
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                verifyAppCredentials();
                            }
                        })
                        .create();

        }
        return null;
    }

    @Override
    public void gotUserInfoFromServer(JSONObject json) {
        if (json != null) {
            ////Log.v(TAG, "json: " + json);
            try {
                //////Log.i(TAG,"result: " + json.getString("result"));
                String result = json.getString("result");
                String resultText = json.getString("text");
                //////Log.i(TAG,"json data: " + json.names());
                if (result.equals("0")) {
                    SharedPreferences.Editor editor = mcurrentUserPref.edit();
                    editor.putString(mAppData.mApplicationName.toLowerCase() + "////Log.n", appUsername);
                    //editor.putString(mAppData.mApplicationName.toLowerCase() + "Password", appPassword);
                    editor.putString(mAppData.mApplicationName.toLowerCase() + "Context", appContext);
                    editor.commit();
                    ++MyApplication.INSTANCE.mAuthWFCount;
                    processNextApp();
                } else {
                    closeDialog();
                    Toast.makeText(getApplicationContext(), resultText, Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                //e.getMessage();
            }
        }

    }

    //From interface
    public void showDialog() {
        if (isFinishing()) {
            return;
        }
        mDialog = ProgressDialog.show(this, "",
                "Validating application credentials..", true, true);
    }

    public void closeDialog() {
        if (isFinishing()) {
            return;
        }
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    private void addClearButton(final EditText editText) {
        final Drawable x = getResources().getDrawable(R.drawable.presence_offline);//your x image, this one from standard android images looks pretty good actually
        x.setBounds(0, 0, x.getIntrinsicWidth(), x.getIntrinsicHeight());
        editText.setCompoundDrawables(null, null, editText.getText().toString().equals("") ? null : x, null);
        editText.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (editText.getCompoundDrawables()[2] == null) {
                    return false;
                }
                if (event.getAction() != MotionEvent.ACTION_UP) {
                    return false;
                }
                if (event.getX() > editText.getWidth() - editText.getPaddingRight() - x.getIntrinsicWidth()) {
                    editText.setText("");
                    editText.setCompoundDrawables(null, null, null, null);
                }
                return false;
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editText.setCompoundDrawables(null, null, editText.getText().toString().equals("") ? null : x, null);
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });
    }


    /*@Override
    public boolean moveTaskToBack(boolean nonRoot) {
        // TODO Auto-generated method stub
        return super.moveTaskToBack(true);
    }*/


    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        SharedPreferences.Editor editor = mcurrentUserPref.edit();
        editor.putBoolean("showDialog", false);
        editor.commit();
    }


    @Override
    public void throwToast(String stringToToast) {
        if (isFinishing()) {
            return;
        }
    }

    private void processNextApp() {
        if (mCurAppIndex == 0) {
            // first step of mandatory credentials collection (outlook or
            // google calendar) is completed
            LocalPrefs.INSTANCE.setHasAppCredentials(true);
        }

        if (mCurAppIndex + 1 < mNumApps) {
            ++mCurAppIndex;
            ////Log.i(TAG, "mcurappInded in prcessNextapp" + mCurAppIndex);
            populateView();
        } else {
            // launch message list activity
            ////Log.i(TAG, "we are in end and exiting authentication");
            Intent nextActivity = new Intent().setClassName(getPackageName(),ActivityPackage.MessageTabActivity);
            nextActivity.putExtra("CallingActivity", getLocalClassName());
            startActivity(nextActivity);
        }
    }


}
