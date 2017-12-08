package com.coremobile.coreyhealth;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.coremobile.coreyhealth.Checkfornotification.CMN_IsActiveDeviceWebService;
import com.coremobile.coreyhealth.base.CMN_AppBaseActivity;
import com.coremobile.coreyhealth.errorhandling.ActivityPackage;
import com.coremobile.coreyhealth.googleAnalytics.CMN_Preferences;
import com.coremobile.coreyhealth.progressdb.ProgressInfoDBControl;
import com.urbanairship.push.PushManager;
import com.urbanairship.push.PushPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class SigninActivityCMN extends CMN_AppBaseActivity implements
        /*YouTubePlayer.OnInitializedListener,*/ IServerConnect, IDownloadJSON {

    String mUserName;
    String mPassword;
    String mOrganization;
    String mContactmail;
    private static String TAG = "Corey_SigninActivityCMN";
    private boolean BaseUrlAlternate = false;
    static final private int HELP_DIALOG = 0;
    private static final int DIALOG_WRONG_CREDENTIALS = 1;
    private static final int REFRESH_CONFIGURATION = 2;
    private static final int ABOUT_CRYSTAL = 3;
    private static final int MISSING_CONFIGURATION = 5;
    private static final int CONFIGURATION_LOADED = 6;
    private static final int SHOW_PROGRESS_DIALOG = 7;
    private static final int CLOSE_PROGRESS_DIALOG = 8;
    MyApplication application;
    protected int _waitTime = 30000; // time to display the splash screen in ms- increased from 20000
    public static String mInfoUrl;
    JSONHelperClass jsonHelperClass;
    private static String APP_PATH;
    private String CMN_SERVER_BASE_URL_DEFINE;
    private String CMN_SERVER_ALTER_URL_DEFINE;
    private String CMN_SERVER_REGISTER_API;
    private String CMN_PUSH_SHARP_REGISTER_API;
    private final static String CMN_SERVER_FORGOT_PWD_API = "forgotpassword.aspx?";
    SharedPreferences mcurrentUserPref;
    String mUserNameStr, passwordStr, OrganizationStr;
    public static final String CURRENT_USER = "CurrentUser";
    public ProgressDialog mDialog;
    EditText mPasswordField;
    EditText mUserNameField;
    ImageView mlogoview;
    //  AutoCompleteTextView mOrganizationField;
    EditText mOrganizationField;
    UIHandler uiHandler;
    ArrayAdapter<String> mAutoCompleteAdapter;
    private Set<String> mAdditionalContext = new HashSet<String>();
    private BaseUrlMgr mBaseUrlMgr = new BaseUrlMgr(this);
    String Appstring = "SURG";
    String img1;
    Button watchVideo;
//    YouTubePlayerView youTubeView;
//    LinearLayout youtube_view_layout;
//    RelativeLayout videoLayout;
//    private static final int RECOVERY_DIALOG_REQUEST = 1;


    private final class UIHandler extends Handler {
        public static final int DISPLAY_UI_TOAST = 0;
        //public static final int MISSING_CONFIGURATION = 1;
        //public static final int PROGRESS_DIALOG = 1;

        public UIHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UIHandler.DISPLAY_UI_TOAST:
                    Context context = getApplicationContext();
                    Toast t = Toast.makeText(context, (String) msg.obj, Toast.LENGTH_LONG);
                    t.show();
                    break;

                case MISSING_CONFIGURATION:
                    showDialog(MISSING_CONFIGURATION);
                    break;

                case CONFIGURATION_LOADED:
                    showDialog(CONFIGURATION_LOADED);
                    populateOrganizationFileds();
                    break;

                case SHOW_PROGRESS_DIALOG:
                    showConfigurationDialog();
                    break;

                case CLOSE_PROGRESS_DIALOG:
                    closeConfigurationDialog();
                    break;

                default:
                    break;
            }
        }

    }

    public void populateOrganizationFileds() {
        //  mAutoCompleteAdapter.clear();
        for (int i = 0; i < MyApplication.organizationData.size(); i++) {
            //Log.i(TAG,"crystal data is null");
            String organization = MyApplication.organizationData.get(i).mOrgName;
//            Log.i(TAG, "orgNames: " + organization);
            //     mAutoCompleteAdapter.add(organization);
        }
    }

    protected void handleUIRequest(int type, String message) {
        Message msg = uiHandler.obtainMessage(type);
        msg.obj = message;
        uiHandler.sendMessage(msg);
    }

    private OnClickListener mSignupClicked = new OnClickListener() {

        @Override
        public void onClick(View v) {
            startActivity(new Intent(SigninActivityCMN.this, SignupActivityCMN.class));

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.INSTANCE.MsgTabActivityState = 0;
        MyApplication.INSTANCE.MsgMetaData.clear();
//        Log.d(TAG, "MyApplication.INSTANCE.MsgMetaData is empty? = " + MyApplication.INSTANCE.MsgMetaData.isEmpty());
        if (MyApplication.shouldResetApp()) {
            Toast.makeText(getApplicationContext(), "Cache Expired. Please Refresh Configuration and Signin again.", Toast.LENGTH_LONG).show();
        }
        MyApplication.resetApp(false);
        if (MyApplication.USE_GCM) {
            GCMClientManager.checkPlayServices(this);
        }
        CMN_Preferences.setCurrentContextId(SigninActivityCMN.this, "");

        CoreyDBHelper coreyDBHelper = new CoreyDBHelper(SigninActivityCMN.this);
        coreyDBHelper.deleteAllMessages();
        coreyDBHelper.deletedb(SigninActivityCMN.this);
        new ProgressInfoDBControl(SigninActivityCMN.this).deleteallProgressBarObjects();

        if (CMN_Preferences.getisSendDeviceLog(SigninActivityCMN.this)) {
            AlertDialog.Builder signoutBuilder = new AlertDialog.Builder(
                    SigninActivityCMN.this);
            signoutBuilder
                    .setTitle("Send Logs")
                    .setMessage("Device logs are generated, would you like to send these ?");
            signoutBuilder.setPositiveButton(android.R.string.yes,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            dialog.dismiss();
                            File appDirectory = new File(Environment.getExternalStorageDirectory() + "/CoreyLogs");
                            File logDirectory = new File(appDirectory + "/log");
                            String fileName = MyApplication.INSTANCE.getResources().getString(R.string.app_name) + ""
                                    + MyApplication.INSTANCE.getResources().getString(R.string.app_version) + ".txt";
                            File logFile = new File(logDirectory, "" + fileName);
                            Intent email = new Intent(Intent.ACTION_SEND);
                            email.setType("text/plain");
                            email.putExtra(Intent.EXTRA_SUBJECT, "Log - " + fileName);
                            email.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(logFile));
                            CMN_Preferences.setisSendDeviceLog(MyApplication.INSTANCE, false);
                            startActivity(Intent.createChooser(email, "Email:"));

                        }
                    });
            signoutBuilder.setNegativeButton(android.R.string.no,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                            int i) {
                            dialog.dismiss();
                            CMN_Preferences.setisSendDeviceLog(MyApplication.INSTANCE, false);
                        }
                    }).show();

        } else {
            File appDirectory = new File(Environment.getExternalStorageDirectory() + "/CoreyLogs");
            File logDirectory = new File(appDirectory + "/log");
            String fileName = MyApplication.INSTANCE.getResources().getString(R.string.app_name) + ""
                    + MyApplication.INSTANCE.getResources().getString(R.string.app_version) + ".txt";
            File logFile = new File(logDirectory, "" + fileName);
            if(logFile.delete());
        }


    }

    /**
     * Called when the activity is first created.
     */
    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.v("SigninActivityCMN", "onCreate");
        //requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        if (MyApplication.mFilesSet != null) {
            for (String filenme : MyApplication.mFilesSet) {
//                Log.d(TAG, "filename =" + filenme);
            }
        }
        if (AppConfig.isAESEnabled) {
            CMN_SERVER_REGISTER_API = "registerdeviceid_s.aspx?";
            CMN_PUSH_SHARP_REGISTER_API = "registerdeviceid_s.aspx?";
        } else {
            CMN_SERVER_REGISTER_API = "registerdeviceid.aspx?";
            CMN_PUSH_SHARP_REGISTER_API = "registerdeviceid2.aspx?";
        }

        application = (MyApplication) getApplication();
        setContentView(R.layout.signinscreen);
        RelativeLayout rLayout = (RelativeLayout) findViewById(R.id.signin_layout);
        Resources res = getResources(); //resource handle
        if (application.AppConstants.getAppName().equalsIgnoreCase("HEALTH")) {
            Drawable drawable = res.getDrawable(R.drawable.sign_bgp);

            rLayout.setBackground(drawable);
        } else if (application.AppConstants.getAppName().equalsIgnoreCase("SURG")) {
            Drawable drawable = res.getDrawable(R.drawable.sign_bgs);

            rLayout.setBackground(drawable);
        } else if (application.AppConstants.getAppName().equalsIgnoreCase("PATIENT")) {
            Drawable drawable = res.getDrawable(R.drawable.sign_bgp);

            rLayout.setBackground(drawable);
        } else if (application.AppConstants.getAppName().equalsIgnoreCase("MERITUS")) {
            Drawable drawable = res.getDrawable(R.drawable.sign_bgp);

            rLayout.setBackground(drawable);
        } else if (application.AppConstants.getAppName().equalsIgnoreCase("COREYOR")) {
            Drawable drawable = res.getDrawable(R.drawable.sign_bgs);

            rLayout.setBackground(drawable);
        }

//        youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
//        videoLayout = (RelativeLayout) findViewById(R.id.videoLayout);

        watchVideo = (Button) findViewById(R.id.watchVideo);
//        findViewById(R.id.buttonCancel).setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });
        watchVideo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                watchVideo();
            }
        });
        if (!AppConfig.isAppCoreyPatient) {
            watchVideo.setVisibility(View.GONE);
        }
        mcurrentUserPref = getSharedPreferences(CURRENT_USER, 0);
        boolean signup = false;
        String path = "";
        //   application = (MyApplication) getApplication();
   /*      if((MyApplication.crystalData == null))
        {
        	 if(Utils.checkInternetConnection())
        	                refreshConfiguration();
        	 Log.d(TAG,"json data is lost, refreshing config");
        } */
        Appstring = application.AppConstants.DefaultCredentialsAppstring();
        APP_PATH = application.AppConstants.getAppFilesPath();
        jsonHelperClass = new JSONHelperClass();
        ArrayList<String> defaultCredentials = jsonHelperClass.getDefaultCredentials(Appstring);

//        Log.d(TAG, "defaultCredentials =" + defaultCredentials);
 /*   	if(defaultCredentials==null)
        {
    		if(Utils.checkInternetConnection())
                refreshConfiguration();
    		Log.d(TAG,"json data is lost, refreshing config");
    	} */
        //      Appstring=application.AppConstants.DefaultCredentialsAppstring();
        //   APP_PATH=application.AppConstants.getAppFilesPath();
        //      ArrayList<String> AppAttributes = jsonHelperClass.getAppAttributes(Appstring);


        mUserNameStr = mcurrentUserPref.getString("Username", "");
        passwordStr = (mcurrentUserPref.getString("Password", ""));
        OrganizationStr = mcurrentUserPref.getString("Organization", "");
        if ((mUserNameStr.length() == 0) && (passwordStr.length() == 0) && (OrganizationStr.length() == 0))
            signup = true;
        Utils.setAppTitle(this, getString(R.string.app_name));
        mUserNameField = (EditText) findViewById(R.id.signin_username);
        mPasswordField = (EditText) findViewById(R.id.signin_password);
        mlogoview = (ImageView) findViewById(R.id.logoview);
//        Log.d(TAG, "getAppAttributes" + jsonHelperClass.getAppAttributes(Appstring));
        img1 = mcurrentUserPref.getString("logourl", null);
        if (img1 == null) {
            if (jsonHelperClass.getAppAttributes(Appstring) != null) {
                img1 = jsonHelperClass.getAppAttributes(Appstring).get(0);
                SharedPreferences.Editor editor = mcurrentUserPref.edit();
                editor.putString("logourl", img1);
                editor.commit();
            }
        }
        //   String img1 =null;
        //     String img1 = jsonHelperClass.getAppAttributes("MERITUS").get(0);
        //    mContactmail = jsonHelperClass.getAppAttributes("MERITUS").get(1);
//        Log.d(TAG, "Logo Image is " + img1);
        if (img1 != null && !TextUtils.isEmpty(img1)) {
            Uri uri = Uri.parse(img1);
            String url = uri.getLastPathSegment();
            img1 = url.toString()
                    .toLowerCase().replaceAll("\\s+", "")
                    .replaceAll("-", "")
                    .replaceAll("/", "")
                    .replaceAll("[(+^)_]*", "");
            url = null;
        }
        if (!TextUtils.isEmpty(img1) && img1 != null) {
            path = APP_PATH
                    + img1.toString().toLowerCase()
                    .replaceAll("\\s+", "").replaceAll("-", "").replaceAll("/", "")
                    .replaceAll("[(+^)_]*", "").replace(".jpg", ".png");

        }
        //   mlogoview.setVisibility(View.VISIBLE);
        /*
        FileInputStream in;
		edInputStream buf;
		try {
			Log.d(TAG, "path in file reading ="+path);
			
		    in = new FileInputStream(path);
		    buf = new BufferedInputStream(in);
		    Bitmap bMap = BitmapFactory.decodeStream(buf);
		    Log.d(TAG, "bMap"+bMap);
		    Log.d(TAG, "bMap size"+bMap);
		    mlogoview.setImageBitmap(bMap);
		    if (in != null) {
			in.close();
		    }
		    if (buf != null) {
			buf.close();
		    }
		} catch (Exception e) {
		    Log.e("Error reading file", e.toString());
		
		    Log.d(TAG, "path ="+path);
		} */
        //   mlogoview.setVisibility(View.VISIBLE);
        //  mOrganizationField = (EditText) findViewById(R.id.signin_organization);
        mOrganizationField = (EditText) findViewById(R.id.signin_org);
        //    application = (MyApplication) getApplication();
        //   jsonHelperClass = new JSONHelperClass();
        //    mInfoUrl=jsonHelperClass.getiButtonURL();
        mInfoUrl = mcurrentUserPref.getString("InfoUrl", "");
//        Log.d(TAG, "mInfoUrl= " + mInfoUrl);
        addClearButton(mUserNameField);
        addClearButton(mPasswordField);
        addClearButton(mOrganizationField);
        ArrayList<String> mOrganizationNames = new ArrayList<String>();
        if ((MyApplication.crystalData == null) || (MyApplication.organizationData == null) || (MyApplication.organizationData.size() == 0)) {
            showDialog(MISSING_CONFIGURATION);
        } else {
            for (int i = 0; i < MyApplication.organizationData.size(); i++) {
                //Log.i(TAG,"crystal data is null");
                String organization = MyApplication.organizationData.get(i).mOrgName;
//                Log.i(TAG, "orgNames: " + organization);
                mOrganizationNames.add(organization);
            }
        }
        String deviceId = mcurrentUserPref.getString("deviceId", null);
        if (MyApplication.USE_URBAN_AIRSHIP) {
            if (deviceId != null) {
                PushManager.enablePush();
            }
            PushPreferences prefs = PushManager.shared().getPreferences();
            SharedPreferences.Editor editor = mcurrentUserPref.edit();
            editor.putString("deviceId", prefs.getPushId());
            editor.commit();
        }

        if (MyApplication.USE_GCM) {
            if (GCMClientManager.checkPlayServices(this)) {
//                Log.i(TAG, "This device -  play services is supported.");
                GCMClientManager gcmClientManager = new GCMClientManager();
                String regid = gcmClientManager.getRegistrationId(this);
                if (Utils.isEmpty(regid)) {
                    gcmClientManager.registerInBackground(this);
                } else {
//                    Log.i(TAG, "This device has already been registered with Google Cloud");
                }
            } else {
//                Log.w(TAG, "This device -  play services is NOT supported.");
            }
        }
        //Read organization name from file
        /*InputStreamReader isr = null;
        FileInputStream fIn = null;

            char[] inputBuffer = new char[255];

            String data = null;

            try {

                fIn = openFileInput("orgNames.txt");

                isr = new InputStreamReader(fIn);


                isr.read(inputBuffer);

                data = new String(inputBuffer);
                Log.i(TAG,"orgnames: " + data);
                String[] firstSplit = data.split("/");
                Log.i(TAG,"split: " + firstSplit.length);
                for(int i = 0;i < firstSplit.length; i++){
                    mOrganizationNames.add(firstSplit[i]);
                    Log.i(TAG,firstSplit[i]);
                }
            }

            catch (Exception e) {

                //e.getMessage();



            }*/

        //     mAutoCompleteAdapter= new ArrayAdapter<String>(this,R.layout.autocompletelayout,mOrganizationNames);
        //   mOrganizationField.setAdapter(mAutoCompleteAdapter);
        //   Button mSignup = (Button) (findViewById(R.id.signup1));
        mUserNameField.setText(mcurrentUserPref.getString("Username", ""));
        if (!application.AppConstants.getAppName().equalsIgnoreCase("HEALTH")) {
            mPasswordField.setText(mcurrentUserPref.getString("Password", ""));
        } else mPasswordField.setText("");
        mOrganizationField.setText(mcurrentUserPref.getString("Organization", ""));
        if (Utils.checkInternetConnection()) {
            if (signup) {
                // 	String Appstring = "SURG" ;
        /*	if(AppConfig.isAppCoreyHealth) Appstring="HEALTH";
            if(AppConfig.isAppCoreySurg) Appstring="SURG";
            if(AppConfig.isAppCoreyPatient) Appstring="PATIENT";
            if(AppConfig.isAppCoreyEd) Appstring="COREYER";
            if(AppConfig.isAppCoreySales) Appstring="SALES";
            if(AppConfig.isAppCoreyFinance) Appstring="FINANCE";
            */
                //  Appstring=application.AppConstants.DefaultCredentialsAppstring();
                //      	ArrayList<String> defaultCredentials = jsonHelperClass.getDefaultCredentials(Appstring);

//                Log.d(TAG, "defaultCredentials =" + defaultCredentials);
                    /*
        mUserNameField.setText("surgdemo");
        mPasswordField.setText("demo");
        mOrganizationField.setText("surgdemo");
        */

                if (defaultCredentials != null) {
                    if (defaultCredentials.get(0) != null)
                        mUserNameField.setText(defaultCredentials.get(0));
                    if (defaultCredentials.get(1) != null)
                        mPasswordField.setText(defaultCredentials.get(1));
                    if (defaultCredentials.get(2) != null)
                        mOrganizationField.setText(defaultCredentials.get(2));
                }
            }
        }

        //Log.i(TAG,"org name: " + mOrganizationField.getText().toString());
        CMN_SERVER_BASE_URL_DEFINE = jsonHelperClass.getBaseURL(mOrganizationField.getText().toString());
//        Log.d(TAG, "CMN_SERVER_BASE_URL_DEFINE =" + CMN_SERVER_BASE_URL_DEFINE);
        //     mSignup.setOnClickListener(mSignupClicked);

        Button mSignin = (Button) findViewById(R.id.signin);

        mSignin.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (MyApplication.INSTANCE.DB_EXEPTION == true) {
            /* 	   Toast.makeText(getApplicationContext(),
                        "Database encryption error occored. Please go to  app settings and delete your data( settings-->Apps-->CoreyPatient-->Clear Data).  Alternatively you an unistall this app and install again ", Toast.LENGTH_LONG).show();
    			*/
//                    Log.d("db exception", "On create :going to finish the activity now");
                    DisplayAlert();

                } else {
                    InputMethodManager inputMgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMgr.hideSoftInputFromWindow(mPasswordField.getWindowToken(), 0);
                    mUserName = mUserNameField.getText().toString().trim();
                    mPassword = mPasswordField.getText().toString().trim();
                    String orgName = mOrganizationField.getText().toString().trim();
                    //Log.i(TAG,"username: " + mUserName.length() + " password: " + mPassword.length());
                    JSONHelperClass jsonHelperClass = new JSONHelperClass();
//                    Log.i(TAG, "org name +++ : " + mOrganizationField.getText().toString());
                    mOrganization = mOrganizationField.getText().toString();
//                    Log.d(TAG, "mOrganization =" + mOrganization);
                    CMN_SERVER_BASE_URL_DEFINE = jsonHelperClass.getBaseURL(mOrganizationField.getText().toString().trim());
                    CMN_Preferences.setBaseUrl(SigninActivityCMN.this, CMN_SERVER_BASE_URL_DEFINE);
                    CMN_SERVER_ALTER_URL_DEFINE = jsonHelperClass.getAlternateURL(mOrganizationField.getText().toString().trim());
                    SharedPreferences.Editor editor = mcurrentUserPref.edit();
                    editor.putString("CMN_SERVER_BASE_URL_DEFINE", CMN_SERVER_BASE_URL_DEFINE);
                    editor.putString("CMN_SERVER_ALTER_URL_DEFINE", CMN_SERVER_ALTER_URL_DEFINE);
                    editor.putString("Organization", mOrganizationField.getText().toString().trim());
                    CMN_Preferences.setOrganizationName(SigninActivityCMN.this, mOrganizationField.getText().toString().trim());
                    editor.putString("AlternateBaseUrl", "false");
                    editor.commit();
//                    Log.i(TAG, "base url from signin: " + mcurrentUserPref.getString("CMN_SERVER_BASE_URL_DEFINE", ""));
                    if ((mUserName.length() == 0) || (mPassword.length() == 0) || (orgName.length() == 0)) {
                        Toast.makeText(getApplicationContext(), "All of the above Input fields are mandatory and can not be left empty. Please enter valid details", Toast.LENGTH_LONG).show();
                    } else if (!MyApplication.isConfigurationLoaded()) {
                        Toast.makeText(getApplicationContext(), "No Valid Organization found. Please Refresh the Configuration from the Menu.", Toast.LENGTH_LONG).show();
                    } else if ((!MyApplication.isOrganizationPresent(orgName))) {
                        Toast.makeText(getApplicationContext(), "Unsupported Organization. Please enter a valid organization name (or) Contact us to create and support your organization", Toast.LENGTH_LONG).show();
                    } else {

                        String appName = null;
                  /*      
                        if(AppConfig.isAppCoreyHealth){
                            appName = AppConfig.AppNameCoreyHealthProduction;
                        }else if (AppConfig.isAppCoreySurg) {
                            
                            if(AppConfig.isInProduction)
                                appName= AppConfig.AppNameCoreySurgProduction;
                            else
                                appName = AppConfig.AppNameCoreySurgDevelopment;
                            
                            
                        } */
                        application = (MyApplication) getApplication();
                        appName = application.AppConstants.getAppName();
                        //setProgressBarIndeterminateVisibility(true);
                        //Log.i(TAG,"username and password != null");
                        String deviceId = LocalPrefs.INSTANCE.deviceId();
//                        Log.i(TAG, "DEVICE ID: " + deviceId);
                        //String url = String.format(CMN_SERVER_BASE_URL_DEFINE + CMN_SERVER_REGISTER_API + "username=%s&password=%s&deviceid=%s&devicetype=Android",mUserName,mPassword, deviceId);
                        String url;

                        if (MyApplication.USE_URBAN_AIRSHIP) {
                            url = CMN_SERVER_BASE_URL_DEFINE + CMN_SERVER_REGISTER_API;
                        } else {
                            url = CMN_SERVER_BASE_URL_DEFINE + CMN_PUSH_SHARP_REGISTER_API;
                        }
                        HashMap<String, String> data = new HashMap<String, String>();

                        data.put("username", mUserName);
                        data.put("password", mPassword);
                        data.put("deviceid", deviceId);
                        data.put("devicetype", "Android");
                        data.put("OS", "" + Build.VERSION.RELEASE);
                        data.put("organization", mOrganization);
                        data.put("DeviceName", Build.MODEL);
                        if (AppConfig.isSupportPartialMsgLoading)
                            data.put("SupportsPartialMsgLoading", "true");
                        data.put("appname", appName);
                        data.put("AppVersion", getResources().getString(R.string.app_version));


//                        HashMap<String, String> data = new HashMap<String, String>();
//                        Log.i(TAG, "sign in url: " + url);
//                        Log.i(TAG, "organization in verify user " + mOrganization);
                        if (Utils.checkInternetConnection()) {
                            new VerifyUser(SigninActivityCMN.this, data).execute(url);
//                            new VerifyUser(SigninActivityCMN.this, SigninActivityCMN.this, data.toString()).execute(url);
                            try {
//                                new CMN_ErrorMessageWebService(SigninActivityCMN.this).execute(mUserName, mPassword, mOrganization, CMN_SERVER_BASE_URL_DEFINE);
//                                new CMN_IsActiveDeviceWebService(SigninActivityCMN.this).execute(mUserName, mPassword, mOrganization, CMN_SERVER_BASE_URL_DEFINE);
                            } catch (Exception e) {
                                //e.getMessage();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Unable to access the Network. Please check your Network connectivity and try again.", Toast.LENGTH_LONG).show();
                        }


                    }
                }
            }
        });

        Button forgotPassword = (Button) findViewById(R.id.forgotpassword);
        forgotPassword.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMgr.hideSoftInputFromWindow(mPasswordField.getWindowToken(), 0);
                mUserName = mUserNameField.getText().toString().trim();
                String orgName = mOrganizationField.getText().toString().trim();
                //Log.i(TAG,"username: " + mUserName.length() + " password: " + mPassword.length());
                JSONHelperClass jsonHelperClass = new JSONHelperClass();
//                Log.i(TAG, "org name: " + mOrganizationField.getText().toString());

                CMN_SERVER_BASE_URL_DEFINE = jsonHelperClass.getBaseURL(mOrganizationField.getText().toString().trim());
                SharedPreferences.Editor editor = mcurrentUserPref.edit();
                editor.putString("CMN_SERVER_BASE_URL_DEFINE", CMN_SERVER_BASE_URL_DEFINE);
                editor.commit();
//                Log.i(TAG, "base url from signin: " + mcurrentUserPref.getString("CMN_SERVER_BASE_URL_DEFINE", ""));
                if (mUserName.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Username is mandatory to retreive your password. Please enter your username", Toast.LENGTH_LONG).show();
                } else if (orgName.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Organization name is mandatory to retreive your password. Please enter the Organization name", Toast.LENGTH_LONG).show();
                } else if (!MyApplication.isConfigurationLoaded()) {
                    Toast.makeText(getApplicationContext(), "No Valid Organization found. Please Refresh the Configuration from the Menu.", Toast.LENGTH_LONG).show();
                } else if ((!MyApplication.isOrganizationPresent(orgName))) {
                    Toast.makeText(getApplicationContext(), "Unsupported Organization. Please enter a valid organization name (or) Contact us to create and support your organization", Toast.LENGTH_LONG).show();
                } else {
                    //setProgressBarIndeterminateVisibility(true);
                    //Log.i(TAG,"username and password != null");
                    String url = CMN_SERVER_BASE_URL_DEFINE + CMN_SERVER_FORGOT_PWD_API;
                    HashMap<String, String> data = new HashMap<String, String>();
                    data.put("username", mUserName);
                    data.put("organization", mOrganization);
//                    Log.i(TAG, "forgotpassword in url: " + url);
                    if (Utils.checkInternetConnection()) {
                        performForgotPassword(mUserName);
                    } else {
                        Toast.makeText(getApplicationContext(), "Unable to access the Network. Please check your Network connectivity and try again.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        Button contactus = (Button) findViewById(R.id.contactus);
        contactus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.performSendEmail(SigninActivityCMN.this, mContactmail);
            }
        });


        if (application.AppConstants.getAppName().equalsIgnoreCase("MERITUS")) {
            contactus = (Button) findViewById(R.id.coremobilenetworks);
            contactus.setText(MyApplication.INSTANCE.AppConstants.getCompanyName());
            contactus.setTypeface(null, Typeface.ITALIC);
            contactus.setTextSize(12);
            contactus.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.performSendEmail(SigninActivityCMN.this, mContactmail);
                }
            });
        }
        mUserNameField.requestFocus();

    }


    public void watchVideo() {
//        if (videoLayout.getVisibility() == View.GONE) {
//            youTubeView.initialize(CMN_Config.DEVELOPER_KEY, this);
//            videoLayout.setVisibility(View.VISIBLE);
//        }
        startActivity(new Intent().setClassName(getPackageName(), ActivityPackage.YoutubeVideoActivity));

    }

//    @Override
//    public void onInitializationFailure(YouTubePlayer.Provider provider,
//                                        YouTubeInitializationResult errorReason) {
//        if (errorReason.isUserRecoverableError()) {
//            errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
//        } else {
//            String errorMessage = String.format("Some error occured", errorReason.toString());
//            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
//        }
//    }
//
//    @Override
//    public void onInitializationSuccess(YouTubePlayer.Provider provider,
//                                        YouTubePlayer player, boolean wasRestored) {
//        if (!wasRestored) {
//
//            // loadVideo() will auto play video
//            // Use cueVideo() method, if you don't want to play it automatically
//            player.loadVideo(CMN_Config.YOUTUBE_VIDEO_CODE);
//
//            // Hiding player controls
////            player.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == RECOVERY_DIALOG_REQUEST) {
//            // Retry initialization if user performed a recovery action
//            getYouTubePlayerProvider().initialize(CMN_Config.DEVELOPER_KEY, this);
//        }
//    }
//
//    private YouTubePlayer.Provider getYouTubePlayerProvider() {
//        return (YouTubePlayerView) findViewById(R.id.youtube_view);
//    }

    private boolean performForgotPassword(final String userName) {
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                Toast.makeText(
                        getApplicationContext(),
                        "Sending request to server...",
                        Toast.LENGTH_SHORT).show();
            }

            protected String doInBackground(Void... params) {
                return Utils.httpPost(
                        CMN_SERVER_BASE_URL_DEFINE + CMN_SERVER_FORGOT_PWD_API,
                        "username", userName
                );
            }

            protected void onPostExecute(String result) {
//                Log.i(TAG, "POST response: " + result);
                String msg = (result != null) ?
                        "Your Password is retreived successfully and reset instructions are sent to your registered email-address."
                        : "Failed to retrieve password. Please check your Network connectivity and try again.";
                Toast.makeText(
                        getApplicationContext(),
                        msg,
                        Toast.LENGTH_LONG).show();
            }
        }.execute();

        return true;
    }

    public void DisplayAlert() {
        Builder alert = new Builder(this);
            /*	LayoutInflater li = LayoutInflater.from(DetailedApplicationData.this);
                View view = li.inflate(R.layout.alert_textview, null);
    			TextView label=(TextView)view.findViewById(R.id.text);
    			alert.setView(view).create().show(); */

        alert.setTitle("Database encryption error");

        alert.setMessage("Please uninstall this app and install again( Go to settings-->General-->Application Manager-->CoreyPatient-->Uninstall).");

        alert.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        dialog.dismiss();
                    }
                });

        alert.show();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, REFRESH_CONFIGURATION, Menu.NONE, "Refresh Configuration");
        //   menu.add(0,HELP_DIALOG,Menu.NONE,"Help");
        menu.add(0, ABOUT_CRYSTAL, Menu.NONE, "About");
        getMenuInflater().inflate(R.menu.signin, menu);

        MenuItem menuItem = menu.findItem(R.id.action_info);

        if (mInfoUrl != null)
            menuItem.setVisible(true);

        return super.onCreateOptionsMenu(menu);
    }

    public void showConfigurationDialog() {
//        Log.i(TAG, "show Dialog");
        if (mDialog == null) {
            //refreshData.setVisibility(View.INVISIBLE);
            mDialog = ProgressDialog.show(this, "",
                    "Loading Configuration from server...", true, true);

        }
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        if (mDialog != null) {
            //Log.i(TAG,"dismissDialog");
            //refreshData.setVisibility(View.VISIBLE);
            mDialog.dismiss();
            mDialog = null;
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        if (mDialog != null) {
            //Log.i(TAG,"dismissDialog");
            //refreshData.setVisibility(View.VISIBLE);
            mDialog.dismiss();
            mDialog = null;
        }
    }

    public void closeConfigurationDialog() {
        if (mDialog != null) {
            //Log.i(TAG,"dismissDialog");
            //refreshData.setVisibility(View.VISIBLE);
            mDialog.dismiss();
            mDialog = null;
        }
    }

    public void showDialog() {
        if (isFinishing()) {
            return;
        }
//        Log.i(TAG, "show Dialog");
        if (mDialog == null) {
            //refreshData.setVisibility(View.INVISIBLE);
            mDialog = ProgressDialog.show(this, "",
                    "Retrieving settings from server...", true, true);
        }
    }

    public void closeDialog() {
        if (isFinishing()) {
            return;
        }
        if (mDialog != null) {
            //Log.i(TAG,"dismissDialog");
            //refreshData.setVisibility(View.VISIBLE);
            mDialog.dismiss();
            mDialog = null;
        }
    }

    public void refreshConfiguration() {

        Thread wThread = new HandlerThread("UIHandler");
        wThread.start();


        uiHandler = new UIHandler(Looper.getMainLooper());

        ((MyApplication) getApplication()).loadOrganizations();
        Thread waitThread = new Thread() {
            @Override
            public void run() {

                try {
                    handleUIRequest(SHOW_PROGRESS_DIALOG, "");
                    int waited = 0;
                    while (MyApplication._parsing && (waited < _waitTime)) {
                        //Log.i(TAG,"MyApplication._parsing && (waited < _splashTime)");
                        sleep(100);
                        if (MyApplication._parsing) {
                            //Log.i(TAG,"if(MyApplication._parsing) ");
                            waited += 100;
                        }
                    }
                } catch (InterruptedException e) {
                    //e.getMessage();
                } finally {
                    handleUIRequest(CLOSE_PROGRESS_DIALOG, "");
                    if ((MyApplication.crystalData == null) || (MyApplication.organizationData == null) || (MyApplication.organizationData.size() == 0)) {
//                        Log.i(TAG, "CONFIGURATION STILL MISSING");
                        handleUIRequest(MISSING_CONFIGURATION, "Failed to load basic configuration from the server. Please check your network settings and perform Refresh Configuration from the Menu");
                    } else {
                        handleUIRequest(CONFIGURATION_LOADED, "Configuration loaded successfully");
//                        Log.i(TAG, "CONFIGURATION AVAILABLE");
                    }
                }
            }
        };

        waitThread.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case HELP_DIALOG:
                showDialog(HELP_DIALOG);
                break;
            case REFRESH_CONFIGURATION:
                if (Utils.checkInternetConnection())
                    refreshConfiguration();
                else
                    Toast.makeText(
                            getApplicationContext(),
                            "Please check your network connectivity and try again.",
                            Toast.LENGTH_SHORT).show();
                break;
            case ABOUT_CRYSTAL:
                startActivity(new Intent(SigninActivityCMN.this, AboutActivityCMN.class));
                break;
            case R.id.action_info:

                Intent urlIntent = new Intent(SigninActivityCMN.this,
                        WebViewActivityCMN.class);
                urlIntent.putExtra("myurl", mInfoUrl);
                String Objname = mcurrentUserPref.getString("DisplayName", "Corey");
                String version = "  version " + getResources().getString(R.string.app_version);
                urlIntent.putExtra("verno", version);
                urlIntent.putExtra("objname", Objname);
                startActivity(urlIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public Dialog onCreateDialog(int id) {
        switch (id) {
            case (HELP_DIALOG):
                Builder helpDialog = new Builder(this);
                helpDialog.setTitle(String.format(
                        getResources().getString(R.string.login_help_title),
                        getResources().getString(R.string.app_version)));
                helpDialog.setMessage(MyApplication.crystalData.mCrystalHelp);
                return helpDialog.create();

            case MISSING_CONFIGURATION:
                Builder missingDialog = new Builder(this);
                missingDialog.setTitle("Message");
                missingDialog.setMessage("Failed to load basic configuration.\nPlease check your network settings and Refresh Configuration from the Menu\n\nPlease Contact us if the problem persists");
                missingDialog.setCancelable(false);
                missingDialog.setPositiveButton("Ok", null);
                return missingDialog.create();

            case CONFIGURATION_LOADED:
                Builder loadedDialog = new Builder(this);
                loadedDialog.setTitle("Message");
                loadedDialog.setMessage("Configuration loaded successfully.");
                loadedDialog.setCancelable(false);
                loadedDialog.setPositiveButton("Ok", null);
                return loadedDialog.create();

            case DIALOG_WRONG_CREDENTIALS:
                return new Builder(this)
                        .setMessage("Try Again")
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        })
                        .create();

        }
        return null;
    }


    @Override
    public void gotUserInfoFromServer(JSONObject json) {
        SharedPreferences.Editor editor = mcurrentUserPref.edit();
        MyApplication.INSTANCE.MsgTabActivityState = 0;
//        Log.d(TAG, "Entering gotuserinfofromserver");
//        Log.d("JSON", "" + json);


        if (json instanceof CredentialsJSONObject) {
            //Log.i(TAG,"new User: " + mcurrentUserPref.getBoolean("NewUser", true));

            boolean fromSignup = "SignupActivityCMN".equals(getIntent().getStringExtra("CallingActivity"));
//            Log.i(TAG, "fromSignup = " + fromSignup);
            //       if(!fromSignup && mUserName.equals(mcurrentUserPref.getString("Username","")))
            if (!fromSignup && (mUserName.equals(mcurrentUserPref.getString("Username", "")))
                    && (mPassword.equals(mcurrentUserPref.getString("Password", "")))) {
//                Log.i(TAG, "same user");
                editor.putBoolean("NewUser", false);
                editor.putBoolean("UserSignedIn", true);
                editor.commit();

//                Log.i(TAG, "callingActivity: " + getLocalClassName());
                //     Intent nextActivity = new Intent(SigninActivityCMN.this,MessageTabActivityCMN.class);
                Intent nextActivity = null;
                jsonHelperClass = new JSONHelperClass();
                if (jsonHelperClass.getTabDtl() != null)
//                    Log.d(TAG, "tabdata size is  = " + (jsonHelperClass.getTabDtl().size()));
                    if (jsonHelperClass.getTabDtl() != null && (jsonHelperClass.getTabDtl().size() != 0)) {
                        nextActivity = new Intent().setClassName(getPackageName(), ActivityPackage.SumMenuActivity);
                    } else {
                        nextActivity = new Intent().setClassName(getPackageName(), ActivityPackage.MessageTabActivity);
                    }
                //        Intent nextActivity = new Intent(SigninActivityCMN.this,MessageListActivityCMN.class);
                if (nextActivity != null && getLocalClassName() != null) {
                    nextActivity.putExtra("CallingActivity", getLocalClassName());
                }
                if (!MyApplication.INSTANCE.AppConstants.getAppName().equalsIgnoreCase("COREYOR")) {
                    AutoLogoutManager.INSTANCE.start();
//                    Log.d(TAG, "Autologout manager instantiated");
                }
                NetworkChangeManager.INSTANCE.start();


                startActivity(nextActivity);
            } else {
//                Log.i(TAG, "different user");
                editor.putBoolean("NewUser", true);
                editor.commit();
//                getContentResolver().delete(DatabaseProvider.CONTENT_URI, null, null);
//                getContentResolver().delete(DatabaseProvider.CONTENT_URI_phonecalls, null, null);
                MyApplication.INSTANCE.mGotPhoneCall = false;
                //  deleteImages();

                // TODO:mahesh Kludge to be removed after
                // COREY-348 Add more client parameters to UpdateUserConfigInfo.aspx
                // is completed
                LocalPrefs.INSTANCE.setLocationSync(false);
                LocalPrefs.INSTANCE.setHasPickedFeatures(false);

                //Log.i(TAG,"DownloadImage: " + mcurrentUserPref.getBoolean("DownloadImage", true));
                editor.putString("Username", mUserName);
                editor.putString("Password", mPassword);
                editor.putString("Organization", mOrganizationField.getText().toString().trim());
                editor.putBoolean("UserSignedIn", true);
                editor.putBoolean("DownloadImage", true);
                editor.commit();
                //Log.i(TAG,"username: " + mcurrentUserPref.getString("Username", "") + " password: " + mcurrentUserPref.getString("Password",""));
                //Log.i(TAG,"new User: " + mcurrentUserPref.getBoolean("NewUser", true));
//                Log.i(TAG, "callingActivity: " + getLocalClassName());
                //   Intent nextActivity = new Intent(SigninActivityCMN.this,MessageTabActivityCMN.class);
                Intent nextActivity = null;
                jsonHelperClass = new JSONHelperClass();
                if (jsonHelperClass.getTabDtl() != null)
//                    Log.d(TAG, "tabdata size is  = " + (jsonHelperClass.getTabDtl().size()));

                    if (jsonHelperClass.getTabDtl() != null && (jsonHelperClass.getTabDtl().size() != 0)) {
                        nextActivity = new Intent().setClassName(getPackageName(), ActivityPackage.SumMenuActivity);
                    } else {
                        nextActivity = new Intent().setClassName(getPackageName(), ActivityPackage.MessageTabActivity);
                    }
                //        Intent nextActivity = new Intent(SigninActivityCMN.this,MessageListActivityCMN.class);
                if (nextActivity != null && getLocalClassName() != null) {
                    nextActivity.putExtra("CallingActivity", getLocalClassName());
                }
                if (!MyApplication.INSTANCE.AppConstants.getAppName().equalsIgnoreCase("COREYOR")) {
                    AutoLogoutManager.INSTANCE.start();
//                    Log.d(TAG, "Autologout manager instantiated");
                }
                NetworkChangeManager.INSTANCE.start();
                startActivity(nextActivity);
            }
            boolean phoneCalls;
            if (LocalPrefs.INSTANCE.hasPickedFeatures()) {
                phoneCalls = LocalPrefs.INSTANCE.phoneCalls();
            } else {
                phoneCalls = !MyApplication.INSTANCE.inDemoMode();
                LocalPrefs.INSTANCE.setPhoneCalls(phoneCalls);
            }
/*
            if(MyApplication.INSTANCE.AppConstants.getAppName().equalsIgnoreCase("SALES") || MyApplication.INSTANCE.AppConstants.getAppName().equalsIgnoreCase("FINANCE")  )
            {	
            if (phoneCalls)
            {
                MyApplication.INSTANCE.registerPhoneCallsListener();
            }
            else
            {
                MyApplication.INSTANCE.unregisterPhoneCallsListener();
            }
            }
*/
            //finish();
        } else if (json != null) {
            //setProgressBarIndeterminateVisibility(false);
            try {
//                Log.i(TAG, "json data " + json.toString());
                String result = json.getString("result");
                String resultText = json.getString("text");

                if (result.equals("0")) {
                    //SharedPreferences.Editor editor = mcurrentUserPref.edit();
                    //editor.clear();
                    //editor.commit();
                    String orgTitle = json.getString("org_title");
                    String location = json.getString("location");
                    String enable_push = json.getString("enable_push");
                    String enable_weekly_auto_sync = json.getString("enable_weekly_auto_sync");
                    String auto_delete_past_meeting = json.getString("auto_delete_past_meeting");
                    String meeting_reminder = json.getString("meeting_reminder");
                    String ignore_appointments = json.getString("ignore_appointments");
                    String num_plan_ahead = json.getString("num_plan_ahead");
                    String user_category = null;
                    String context = null;
                    String context_name = null;
                    String userId = json.optString("userid");
                    CMN_Preferences.setLoggedInUserId(SigninActivityCMN.this, userId);
                    if (json.has("user_category")) user_category = json.getString("user_category");
                    if (json.has("context")) context = json.getString("context");
                    if (json.has("context_name")) context_name = json.getString("context_name");
                    editor.putString("user_category", user_category);
                    if (context != null) {
                        editor.putString("context", context.toUpperCase());
                    }
                    editor.putString("Organization", mOrganizationField.getText().toString().trim());
//                    CMN_Preferences.setContextId(SigninActivityCMN.this, context.toUpperCase());
//                    Log.e("context", "123..." + context.toUpperCase());
                    editor.putString("context_name", context_name);
                    editor.putString("DisplayName", orgTitle);

                    if (json.has("enable_auto_logout")) {
                        CMN_Preferences.setEnableAutoLogout(SigninActivityCMN.this, Boolean.parseBoolean(json.getString("enable_auto_logout")));
                    }
                    if (json.has("enable_auto_logout_timer")) {
                        CMN_Preferences.setEnableTimeInactivityLogout(SigninActivityCMN.this, Boolean.parseBoolean(json.getString("enable_auto_logout_timer")));
                    }
                    if (json.has("enable_auto_logout_network_chg")) {
                        CMN_Preferences.setEnableNetworkChangeLogout(SigninActivityCMN.this, Boolean.parseBoolean(json.getString("enable_auto_logout_network_chg")));
                    }
                    if (json.has("token")) {
                        CMN_Preferences.setUserToken(SigninActivityCMN.this, json.getString("token"));
                        try {
//                                new CMN_ErrorMessageWebService(SigninActivityCMN.this).execute(mUserName, mPassword, mOrganization, CMN_SERVER_BASE_URL_DEFINE);
                            new CMN_IsActiveDeviceWebService(SigninActivityCMN.this).execute(mUserName, mPassword, mOrganization, CMN_SERVER_BASE_URL_DEFINE);
                        } catch (Exception e) {
                            //e.getMessage();
                        }
                    }

                    if (location.equals("0")) {
                        editor.putBoolean("location", false);
                    } else {
                        editor.putBoolean("location", true);
                    }

                    if (ignore_appointments.equals("0")) {
                        editor.putBoolean("ignore_appointments", false);
                    } else {
                        editor.putBoolean("ignore_appointments", true);
                    }


                    if (enable_push.equals("0")) {
                        editor.putBoolean("enable_push", false);
                    } else {
                        editor.putBoolean("enable_push", true);
                    }
                    if (enable_weekly_auto_sync.equals("0")) {
                        editor.putBoolean("enable_weekly_auto_sync", false);
                    } else {
                        editor.putBoolean("enable_weekly_auto_sync", true);
                    }
                    if (auto_delete_past_meeting.equals("0")) {
                        editor.putBoolean("auto_delete_past_meeting", false);
                    } else {
                        editor.putBoolean("auto_delete_past_meeting", true);
                    }

                    LocalPrefs.INSTANCE.setHideLogo(json.getString("hide_logo").equals("1"));

                    if (json.has("location_sync")) {
                        LocalPrefs.INSTANCE.setLocationSync(json.getString("location_sync").equals("1"));
                    }

                    if (json.has("has_picked_features")) {
                        LocalPrefs.INSTANCE.setHasPickedFeatures(
                                json.getString("has_picked_features").equals("1"));
                    }

                    editor.putString("meeting_reminder", meeting_reminder);
                    editor.putString("num_plan_ahead", num_plan_ahead);
                    editor.commit();

                    loadConfiguration();
                } else {
                    closeDialog();
                    Toast.makeText(getApplicationContext(), resultText, Toast.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                //e.getMessage();
            }
        } else if (!BaseUrlAlternate)  //base url fails- change to alternate url
        {
            BaseUrlAlternate = true;
      /*	editor.putString("AlternateBaseUrl", "false");
            CMN_SERVER_ALTER_URL_DEFINE= mcurrentUserPref.getString(CMN_SERVER_ALTER_URL_DEFINE, null);
        	editor.putString("CMN_SERVER_BASE_URL_DEFINE",CMN_SERVER_ALTER_URL_DEFINE);
        	 jsonHelperClass.updateBaseURL(mOrganization, CMN_SERVER_ALTER_URL_DEFINE);
        	editor.commit();
        	*/
            jsonHelperClass.setBaseURLFailed(mOrganization);


            String deviceId = LocalPrefs.INSTANCE.deviceId();
            application = (MyApplication) getApplication();
            String appName = application.AppConstants.getAppName();
            String url;
            if (MyApplication.USE_URBAN_AIRSHIP) {
                url = CMN_SERVER_ALTER_URL_DEFINE + CMN_SERVER_REGISTER_API;
            } else {
                url = CMN_SERVER_ALTER_URL_DEFINE + CMN_PUSH_SHARP_REGISTER_API;
            }
            HashMap<String, String> data = new HashMap<>();
            data.put("username", mUserName);
            data.put("password", mPassword);
            data.put("deviceid", deviceId);
            data.put("devicetype", "Android");
            data.put("organization", mOrganization);
            data.put("appname", appName);
            data.put("AppVersion", getResources().getString(R.string.app_version));


            if (Utils.checkInternetConnection()) {
                new VerifyUser(SigninActivityCMN.this, data).execute(url);
            } else {
                Toast.makeText(getApplicationContext(), "Unable to access the Network. Please check your Network connectivity and try again.", Toast.LENGTH_LONG).show();
            }
        } else //all urls fail
        {
            BaseUrlAlternate = false;

            Toast.makeText(getApplicationContext(), "Unable to access the servers. Please try again after some time.", Toast.LENGTH_LONG).show();
            jsonHelperClass.resetAltURLFailed(mOrganization);
            jsonHelperClass.resetBaseURLFailed(mOrganization);
        }

    }


    public void throwToast(final String stringToToast) {
        if (isFinishing()) {
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), stringToToast, Toast.LENGTH_LONG).show();
            }
        });
    }

    protected void loadConfiguration() {
        //  CMN_SERVER_BASE_URL_DEFINE = CMN_SERVER_TEST_URL_DEFINE;
//        Log.d(TAG, "Load configuration called here");
        String UsrName;
        String Password;
        String orgname;
        try {
            UsrName = URLEncoder.encode(mUserName, "UTF-8");
            Password = URLEncoder.encode(mPassword, "UTF-8");
            orgname = URLEncoder.encode(mOrganization, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            UsrName = "";
            //Password = "demo";
            orgname = "";
            // TODO Auto-generated catch block
            //e1.getMessage();
//            Log.d(TAG, " encoding exception in load configurtion, orgname=" + orgname);
        }
        String url = CMN_SERVER_BASE_URL_DEFINE;
        if (AppConfig.isAESEnabled) {
            url = url + "getConfiguration_s.aspx?token=" + CMN_Preferences.getUserToken(SigninActivityCMN.this);
        } else {
            url = url + "getConfiguration.aspx?token=" + CMN_Preferences.getUserToken(SigninActivityCMN.this);
        }
        if (Utils.checkInternetConnection()) {
//            Log.i(TAG, "start json download");
            try {
                new GetJSON(this, this).execute(url, "true");

            } catch (Exception e) {
//                Log.v(TAG, "GetJSON failed to download crystal.json");
            }
        }
    }

    @SuppressLint("DefaultLocale")
    public void buildUI(JSONObject jsonObject) {
        MyApplication.crystalData = new CrystalData();
        JSONParser jsonParser = new JSONParser();

        CrystalData result = jsonParser.buildUI(jsonObject, MyApplication.crystalData, SigninActivityCMN.this);
        if (result != null) {
            MyApplication.crystalData = result;
//            Log.d(TAG, "Tabdata: " + MyApplication.crystalData.mTabData);
//            Log.d(TAG, "Tabdata size: " + MyApplication.crystalData.mTabData.size());
            String url = String.format(CMN_SERVER_BASE_URL_DEFINE + "getUserDetails.aspx?" + "username=%s&password=%s&organization=%s", mUserName, mPassword, mOrganization);
            if (Utils.checkInternetConnection()) {
                new GetAppCredentials(getApplicationContext(), this).execute(url);
//                Log.i(TAG, "url:" + url);
            }
        } else {
//            Log.i(TAG, "Failed to get JSON data for the user");
        }

        if (MyApplication.crystalData.mOrganizationData.size() > 0) {

            mAdditionalContext = MyApplication.crystalData.mOrganizationData
                    .get(0).additionalContext;

            SharedPreferences.Editor editor = mcurrentUserPref.edit();
            editor.putStringSet("Additional", mAdditionalContext);
            //    editor.putString("ImageString", sImageFromServer);
    /*    editor.putString("DisplayName", MyApplication.crystalData.mOrganizationData
                .get(0).mDiplayName);  */
            editor.putBoolean("RequireAutoSync", MyApplication.crystalData.mOrganizationData
                    .get(0).mRequireAutoSync);
            editor.putString("helpurl", MyApplication.crystalData.mOrganizationData
                    .get(0).mHelpUrl);
            editor.commit();
//            Log.d(TAG, "Json parsing completed in buildui");

        }
    }

    @Override
    public void onBackPressed() {
        //Log.i(TAG, "onBackPressed Called");
//        if (AppConfig.isAppCoreyPatient && videoLayout.getVisibility() == View.VISIBLE) {
//            videoLayout.setVisibility(View.GONE);
//        } else {
        Intent setIntent = new Intent(Intent.ACTION_MAIN);
        setIntent.addCategory(Intent.CATEGORY_HOME);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
//        }
    }
}
