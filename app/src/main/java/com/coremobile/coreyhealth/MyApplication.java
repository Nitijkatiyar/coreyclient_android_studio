package com.coremobile.coreyhealth;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.ConditionVariable;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SearchEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.view.accessibility.AccessibilityEvent;

import com.coremobile.coreyhealth.Checkfornotification.PendingNotificationService;
import com.coremobile.coreyhealth.errorhandling.CMN_ErrorMessages;
import com.coremobile.coreyhealth.googleAnalytics.CMN_AnalyticsTrackers;
import com.coremobile.coreyhealth.googleAnalytics.CMN_Preferences;
import com.coremobile.coreyhealth.newui.MsgByCtxt;
import com.example.crashreporter.AutoErrorReporter;
import com.google.android.gms.analytics.Tracker;
import com.urbanairship.AirshipConfigOptions;
import com.urbanairship.UAirship;
import com.urbanairship.push.CustomPushNotificationBuilder;
import com.urbanairship.push.PushManager;
import com.urbanairship.push.PushPreferences;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


//For phone call support

public class MyApplication extends Application implements IPullDataFromServer, IDownloadJSON {
    private static final String TAG = "Corey_MyApplication";
    public static boolean DB_EXEPTION = false;
    public static final boolean USE_GCM = true; // use Google Cloud Messaging (for PushSharp support) for push support
    public static final boolean USE_URBAN_AIRSHIP = !USE_GCM; // use Urban Airship for push support

    private final static String CMN_SERVER_TEST_URL_DEFINE = "https://www.coremobile.us/organizations/";
    // private final static String CMN_SERVER_TEST_URL_DEFINE = "http://www.mobilizethecloud.net/ServerIntegration_Appstore/";
    //private final static String CMN_SERVER_TEST_URL_DEFINE = "http://50.23.94.90:8080/serverintegration_appstore/";

    public IAppConstants AppConstants;
    public int ImagedownloadCount = 0;
    //private final static String CMN_SERVER_retrieve_caller_URL_DEFINE =  "https://www.coremobnet.com/ServerIntegration_Appstore";
    private String CMN_SERVER_BASE_URL_DEFINE;
    public static CrystalData crystalData;
    public static ArrayList<OrganizationData> organizationData;
    public static ArrayList<MessageItem> MsgMetaData = new ArrayList<MessageItem>();
    public HashMap<String, MsgByCtxt> AllMsgsMap = new HashMap<String, MsgByCtxt>();
    public HashMap<String, String> StaffList = new HashMap<String, String>();
    public HashMap<String, String> DeptSts2ColorMap = new HashMap<String, String>();
    public HashMap<String, String> ErrorList = new HashMap<String, String>();
    public HashMap<String, String> TaskStatusList = new HashMap<String, String>();
    public ArrayList<PatientAnalyticsdDat> PatientAnalyticsList = new ArrayList<PatientAnalyticsdDat>();
    public ArrayList<DeptDef> DeptList = new ArrayList<DeptDef>();
    public ArrayList<statusDefObj> StatusDefList = new ArrayList<statusDefObj>();
    public static boolean _parsing;
    SharedPreferences mcurrentUserPref;
    public static final String CURRENT_USER = "CurrentUser";
    public static int IDLE_COUNTER_THRESHOLD = 15;
    static boolean isJSONParsed = false;
    /*
     * MsgTabActivityState =0-->Initial state :retrieving userlist followed by
     * retreiving messages
     * 1--> app is running
     */
    public boolean One2OneMsgRecvd = false; // flag for retrieving one2 one msg
    public int MsgTabActivityState = 0;
    public String MsgIdAvty;
    public String MsgIdprsr;
    public static String MessageContext = "";

    public boolean pullInProgress = false;
    public boolean pullXmlInProgress = false;
    IPullDataFromServer pullDataOwner;
    ConditionVariable mPullDataCompleted;
    TelListener mTelListener;
    Map<String, Boolean> mCallerSyncInitiated = new HashMap<String, Boolean>(); // map: phonenumber -> boolean

    // *** START: GLOBAL STATE ***
    public boolean mGotPhoneCall = false;
    public int mAuthWFCount = 0; // current completed work flow stage
    // *** END:   GLOBAL STATE ***

    static boolean resetApp = false;
    int current_message_num = 0;
    boolean mRegisteredPhoneListener = false;

    private OrgsLoader mOrgsLoader = new OrgsLoader(this);
    private Map<String, IDialogListener> mDialogListenerRegistry = new HashMap<String, IDialogListener>();

    public static MyApplication INSTANCE;


    public static Set<String> mFilesSet = new HashSet<String>();

    public static boolean shouldResetApp() {
        return resetApp;
    }

    private static boolean activityVisible;


    public void deletedb() {
        Boolean deleted;

        String path = "/data/data/" + getPackageName() + "/files/databases/corey";
        File file = new File(path);
        Log.d(TAG, "file path  is= " + file.getAbsolutePath());
        Log.d(TAG, "file name is= " + file.getName());
        deleted = file.delete();
        Log.d(TAG, "file deleted status = " + deleted);
        // SQLiteDatabase.deleteDatabase(new File(path));
        // SQLiteDatabase.

    }

    public static void resetApp(boolean reset) {
        resetApp = reset;
    }

    @Override
    public void onCreate() {

        super.onCreate();

        INSTANCE = this;

        Log.d(TAG, "Starting COREY Application V" + getResources().getString(R.string.app_version));
        mcurrentUserPref = getSharedPreferences(CURRENT_USER, 0);
        LocalPrefs.INSTANCE.setSharedPrefs(mcurrentUserPref);
        deletedb();

        AutoErrorReporter.get(this)
                .setEmailAddresses("nitij@coremobileinc.com")
                .setEmailSubject("Crash Report - " + getResources().getString(R.string.app_name) + "_" + getResources().getString(R.string.app_version))
                .start();


        ClassLoader myClassLoader = MyApplication.class.getClassLoader();
        Thread.currentThread().setContextClassLoader(myClassLoader);
        loadOrganizations();
        if (AppConfig.isAppCoreyHealth) AppConstants = new HealthConstants();
        else if (AppConfig.isAppCoreySurg) AppConstants = new SurgConstants();
        else if (AppConfig.isAppCoreyPatient) AppConstants = new PatientConstants();
        else if (AppConfig.isAppCoreyEd) AppConstants = new ErConstants();
        else if (AppConfig.isAppCoreySales) AppConstants = new SalesConstants();
        else if (AppConfig.isAppCoreyFinance) AppConstants = new FinanceConstants();
        else if ((AppConfig.isAppCoreyPeriop)) AppConstants = new PeriopConstants();
        else if ((AppConfig.isAppCoreyMeritus)) AppConstants = new MeritusConstants();
        else if ((AppConfig.isAppCoreyOr)) AppConstants = new OrConstants();
        else {
            Log.d(TAG, "Appconfig error");
        }
        if (USE_URBAN_AIRSHIP) {
            AirshipConfigOptions options = AirshipConfigOptions.loadDefaultOptions(this);


            options.inProduction = AppConfig.isInProduction;
            //options.gcmSender = "889183290689";
            options.gcmSender = AppConstants.getGcmSender();
            options.transport = "gcm";
            options.pushServiceEnabled = true;
            if (AppConfig.isInProduction) {
                //	options.productionAppKey = "e3f-JguJQFerAbzjQD3fRg";
                //	options.productionAppSecret  = "rIW6JDNESeeI4PCQIeEgzw";
                options.productionAppKey = AppConstants.getProductionAppKey();
                options.productionAppSecret = AppConstants.getProductionAppSecret();
            } else {
                //	options.developmentAppKey = "STCHoMWcSE6TzwfX3ZZhsQ";
                // 	options.developmentAppSecret = "d4YJYGtvQfaLw59pF-swpw";
                options.developmentAppKey = AppConstants.getDevelopmentAppKey();
                options.developmentAppSecret = AppConstants.getDevelopmentAppSecret();
            }


            UAirship.takeOff(this, options);
            PushManager.enablePush();


            //use CustomPushNotificationBuilder to specify a custom layout
            CustomPushNotificationBuilder nb = new CustomPushNotificationBuilder();

            nb.statusBarIconDrawableId = R.drawable.app_icon;//custom status bar icon

            nb.layout = R.layout.notification;
            nb.layoutIconDrawableId = R.drawable.app_icon;//custom layout icon
            nb.layoutIconId = R.id.icon;
            nb.layoutSubjectId = R.id.subject;
            nb.layoutMessageId = R.id.message;
            nb.constantNotificationId = 100;
            // customize the sound played when a push is received
            //nb.soundUri = Uri.parse("android.resource://"+this.getPackageName()+"/" +R.raw.cat);


            PushManager.shared().setNotificationBuilder(nb);
            PushManager.shared().setIntentReceiver(IntentReceiver.class);

            PushPreferences prefs = PushManager.shared().getPreferences();
            SharedPreferences.Editor editor = mcurrentUserPref.edit();
            editor.putString("deviceId", prefs.getPushId());
            editor.commit();
            Log.d(TAG, "PushId=" + prefs.getPushId());
        }
        //loadConfiguration();
        //   loadOrganizations();

        //  For phone support
        mTelListener = new TelListener(getApplicationContext());

        mPullDataCompleted = new ConditionVariable(true); // initialize as pull completed


        registerActivityLifecycleCallbacks(new ActivityLifeCycleListener());
        Log.d(TAG, "My application on create end here and parsing =" + _parsing);

        CMN_AnalyticsTrackers.initialize(this);
//        CMN_AnalyticsTrackers.getInstance().get(CMN_AnalyticsTrackers.Target.APP_TRACKER);
        if (isExternalStorageWritable()) {
            File appDirectory = new File(Environment.getExternalStorageDirectory() + "/CoreyLogs");
            File logDirectory = new File(appDirectory + "/log");
            File logFile = new File(logDirectory, "" + getResources().getString(R.string.app_name) + "" + getResources().getString(R.string.app_version) + ".txt");
            if (!appDirectory.exists()) {
                appDirectory.mkdir();
            }
            if (!logDirectory.exists()) {
                logDirectory.mkdir();
            }
            try {
                Process process = Runtime.getRuntime().exec("logcat -f " + logFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (isExternalStorageReadable()) {
            // only readable
        } else {
            // not accessible
        }
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    @Override
    public void onTerminate() {
        MyApplication.INSTANCE.stopService(new Intent(MyApplication.INSTANCE, PendingNotificationService.class));
        super.onTerminate();
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }


    protected void loadOrganizations() {
        crystalData = new CrystalData();
        organizationData = new ArrayList<OrganizationData>();
        CMN_SERVER_BASE_URL_DEFINE = CMN_SERVER_TEST_URL_DEFINE;
        _parsing = false;
        if (Utils.checkInternetConnection() && !(_parsing)) {
            _parsing = true;
            Log.d(TAG, "start json download");
            mOrgsLoader.reset();
            mOrgsLoader.tryNextUrl();
        }
    }

    protected void loadConfiguration() {
        crystalData = new CrystalData();
        CMN_SERVER_BASE_URL_DEFINE = CMN_SERVER_TEST_URL_DEFINE;
        String url = CMN_SERVER_BASE_URL_DEFINE + "resources/crystal.json";

        _parsing = false;
        if (Utils.checkInternetConnection() && !(_parsing)) {
            _parsing = true;
            Log.d(TAG, "start json download");
            try {
                new GetJSON(this, INSTANCE).execute(url);
            } catch (Exception e) {
                _parsing = false;
            }
        }
    }

    public void buildUI(JSONObject jsonObject) {
        if (jsonObject == null) {
            mOrgsLoader.tryNextUrl();
            return;
        }
        //Log.i(TAG,"json download complete");
        JSONParser jsonParser = new JSONParser();
        //crystalData = jsonParser.buildUI(jsonObject,crystalData);
        organizationData = jsonParser.buildOrganizations(jsonObject, organizationData, crystalData, this);

        saveorgNames();
        loadFileset();

        _parsing = false;
        Log.d("MyApplication", "crystalData: " + organizationData.size() + organizationData.get(0).mOrgName + organizationData.get(1).mOrgName);
        JSONHelperClass jsonHelperClass = new JSONHelperClass();

        String InfoUrl = jsonHelperClass.getiButtonURL(AppConstants.getAppName());

        SharedPreferences.Editor editor = mcurrentUserPref.edit();
        editor.putString("InfoUrl", InfoUrl);
        String AppDispname = getResources().getString(R.string.app_name);
        editor.putString("DisplayName", AppDispname);

        editor.commit();

        CMN_ErrorMessages.getInstance().getMap().put(12, "Please check your internet connection.");
        CMN_ErrorMessages.getInstance().getMap().put(14, "Could not refresh the Configuration. Please check your network connectivity and try again");
        CMN_ErrorMessages.getInstance().getMap().put(21, "unknown error");
        CMN_ErrorMessages.getInstance().getMap().put(28, "Operation cannot be performed.Please check your network connectivity and try again.");
        CMN_ErrorMessages.getInstance().getMap().put(25, "No Data to generate graph");
        CMN_ErrorMessages.getInstance().getMap().put(42, "Please update some data before saving.");
        CMN_ErrorMessages.getInstance().getMap().put(59, "Please enter text for ");
        CMN_ErrorMessages.getInstance().getMap().put(62, "No step counts to upload.");
        CMN_ErrorMessages.getInstance().getMap().put(79, "Update timed out");
        CMN_ErrorMessages.getInstance().getMap().put(84, "Some error occurred while uploading the data.");
        CMN_ErrorMessages.getInstance().getMap().put(128, "Location service is not enabled ");
        CMN_ErrorMessages.getInstance().getMap().put(131, "Please wait...");
        CMN_ErrorMessages.getInstance().getMap().put(132, "Please use your registered finger print to sign in to ");
        CMN_ErrorMessages.getInstance().getMap().put(133, "Fingerprint sensor has expired due to long time inactivity or it has reaches the maximum number of incorrect attempts. Please wait for 30 seconds and again launch the app to continue using TouchId.");
        CMN_ErrorMessages.getInstance().getMap().put(134, "Use username/password to login");
        CMN_ErrorMessages.getInstance().getMap().put(135, "Signin with UserName/Password");
        CMN_ErrorMessages.getInstance().getMap().put(136, "Upload successful");
        CMN_ErrorMessages.getInstance().getMap().put(137, "No step counts to display.");
        CMN_ErrorMessages.getInstance().getMap().put(138, "No data to display");
        CMN_ErrorMessages.getInstance().getMap().put(139, "Please select network & zipcode and try again");
        CMN_ErrorMessages.getInstance().getMap().put(140, "Location not enabled. Please enable from settings-->Location ");
        CMN_ErrorMessages.getInstance().getMap().put(141, "In Progress");
        CMN_ErrorMessages.getInstance().getMap().put(142, "New Staff Member added");
        CMN_ErrorMessages.getInstance().getMap().put(143, "Searching");
        CMN_ErrorMessages.getInstance().getMap().put(144, "No provider found. Please change search parameters and try again.");
        CMN_ErrorMessages.getInstance().getMap().put(145, "Search timed out");
        CMN_ErrorMessages.getInstance().getMap().put(146, "Zip code fetched successfully");
        CMN_ErrorMessages.getInstance().getMap().put(147, "Zip code not available");
        CMN_ErrorMessages.getInstance().getMap().put(148, "Please select an OR");
        CMN_ErrorMessages.getInstance().getMap().put(149, "Please select a role");
        CMN_ErrorMessages.getInstance().getMap().put(150, "Please select at least one OR");
        CMN_ErrorMessages.getInstance().getMap().put(151, "Please change Staff to update");
        CMN_ErrorMessages.getInstance().getMap().put(152, "Please select atleast one patient");
        CMN_ErrorMessages.getInstance().getMap().put(153, "Please select a staff member");
        CMN_ErrorMessages.getInstance().getMap().put(154, "Please change roles to update");
        CMN_ErrorMessages.getInstance().getMap().put(155, "Please select patient(s)");
        CMN_ErrorMessages.getInstance().getMap().put(156, "Session Expired.\nSigning out, Refresh Configuration and Signin again.");
        CMN_ErrorMessages.getInstance().getMap().put(157, "Message sent successfully");
        CMN_ErrorMessages.getInstance().getMap().put(158, "Refreshing");
        CMN_ErrorMessages.getInstance().getMap().put(159, "Configuration refreshed successfully");
        CMN_ErrorMessages.getInstance().getMap().put(160, "Connection Timeout");
        CMN_ErrorMessages.getInstance().getMap().put(161, "Please select a reminder to use");
        CMN_ErrorMessages.getInstance().getMap().put(162, "Reminder Updated Successfully");
        CMN_ErrorMessages.getInstance().getMap().put(163, "Please fill all fields");
        CMN_ErrorMessages.getInstance().getMap().put(164, "Some error occured. Please try again later");
        CMN_ErrorMessages.getInstance().getMap().put(165, "Please enter title");
        CMN_ErrorMessages.getInstance().getMap().put(166, "Please select a value");

    }

    @SuppressLint("SdCardPath")
    public Set<String> loadFileset() {
        String path = AppConstants.getAppFilesPath();
        File f = new File(path);

        File[] files = f.listFiles();

        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            String filePath = file.getPath();
            if (filePath.endsWith(".png"))
                mFilesSet.add(file.getName().toString());
            Log.d(TAG, "filePath= " + filePath);
        }
        return mFilesSet;
    }

    private void saveorgNames() {
        FileOutputStream fOut = null;
        OutputStreamWriter osw = null;
        try {
            fOut = openFileOutput("orgNames.txt", MODE_PRIVATE);
            osw = new OutputStreamWriter(fOut);
            for (int i = 0; i < organizationData.size(); i++) {
                osw.write(organizationData.get(i).mOrgName + "/");
            }
            osw.flush();
        } catch (Exception e) {
            e.getMessage();
        }

    }

    public static boolean isConfigurationLoaded() {
        if ((MyApplication.organizationData == null) || (MyApplication.organizationData.size() == 0))
            return false;
        return true;
    }

    public static boolean isOrganizationPresent(String orgName) {
        if (crystalData == null) return false;
        for (int i = 0; i < organizationData.size(); i++) {
            String organization = MyApplication.organizationData.get(i).mOrgName;
            Log.d(TAG, "orgNames: " + organization);
            if (orgName.equalsIgnoreCase(organization)) return true;
        }
        return false;
    }

    public int getLastMessageid() {
        String[] projection = new String[]{DatabaseProvider.KEY_MSGID};
        String sortOrder = DatabaseProvider.KEY_MSGID + " DESC";
        Cursor cursor = getContentResolver().query(DatabaseProvider.CONTENT_URI, projection, null, null, sortOrder);
        cursor.moveToFirst();
        if (cursor.getCount() == 0) {
            cursor.close();
            return -1;
        }
        int lastmessageid = cursor.getInt(0);
        Log.d(TAG, "getLastMessageid returning " + lastmessageid);
        cursor.close();
        return lastmessageid;
    }


    public void pullData(IPullDataFromServer owner) {
        Log.d(TAG, "Pull initiated by " + owner.getClass().getName());
        try {
            mPullDataCompleted.close();
            pullInProgress = true;
            String baseURL = mcurrentUserPref.getString("CMN_SERVER_BASE_URL_DEFINE", "");
            if ((baseURL != null) && (baseURL.length() > 0)) CMN_SERVER_BASE_URL_DEFINE = baseURL;
            String url = CMN_SERVER_BASE_URL_DEFINE + "pull.aspx?";
            HashMap<String, String> data = new HashMap<String, String>();
            data.put("token", CMN_Preferences.getUserToken(MyApplication.this));
            data.put("context", CMN_Preferences.getCurrentContextId(this));
            int messageid = getLastMessageid();
            if (messageid < 0) messageid = 0;
            data.put("messageid", Integer.toString(messageid));
            pullDataOwner = owner;
            new GetXML(getApplicationContext(), "Message", this, data).execute(url);
        } catch (Exception e) {
            pullInProgress = false;
            pullDataOwner = null;
            mPullDataCompleted.open();
            Log.d(TAG, "mPullDataCompleted opened on exception");
        }
    }

    public void pullPartialData(IPullDataFromServer owner) {
        Log.d(TAG, "Partial Pull initiated by " + owner.getClass().getName());
        try {
            mPullDataCompleted.close();
            pullInProgress = true;
            String baseURL = mcurrentUserPref.getString("CMN_SERVER_BASE_URL_DEFINE", "");
            if ((baseURL != null) && (baseURL.length() > 0)) CMN_SERVER_BASE_URL_DEFINE = baseURL;

            String url = "";
            if (AppConfig.isAESEnabled) {
                url = CMN_SERVER_BASE_URL_DEFINE + "PartialPull_s.aspx?";
            } else {
                url = CMN_SERVER_BASE_URL_DEFINE + "PartialPull.aspx?";
            }
            HashMap<String, String> data = new HashMap<String, String>();
            data.put("token", CMN_Preferences.getUserToken(MyApplication.this));
            pullDataOwner = owner;
            new GetXML(getApplicationContext(), "Message", this, data).execute(url, "true");
        } catch (Exception e) {
            pullInProgress = false;
            pullDataOwner = null;
            mPullDataCompleted.open();
            Log.d(TAG, "mPartialPullDataCompleted opened on exception");
        }
    }

    public void getOne2OneMessages(IPullDataFromServer owner, String MsgContext, String ObjId, String LastMsgID, Boolean PastMessage) {
        Log.d(TAG, "One2one Pull initiated by " + owner.getClass().getName());
        try {
            mPullDataCompleted.close();
            pullInProgress = true;
            String baseURL = mcurrentUserPref.getString("CMN_SERVER_BASE_URL_DEFINE", "");
            if ((baseURL != null) && (baseURL.length() > 0)) CMN_SERVER_BASE_URL_DEFINE = baseURL;
            String url = CMN_SERVER_BASE_URL_DEFINE + "GetMessages.aspx?";
            HashMap<String, String> data = new HashMap<String, String>();
            data.put("token", CMN_Preferences.getUserToken(MyApplication.this));
            data.put("context", CMN_Preferences.getCurrentContextId(this));
            if (MsgContext != null) data.put("context", MsgContext);
            if (ObjId != null) data.put("objectid", ObjId);
            if (LastMsgID != null) data.put("lastmessageid", LastMsgID);
            if (PastMessage) data.put("pastmessages", "true");
            else data.put("pastmessages", "false");
            pullDataOwner = owner;
            new GetXML(getApplicationContext(), "Message", this, data).execute(url);
        } catch (Exception e) {
            pullInProgress = false;
            pullDataOwner = null;
            mPullDataCompleted.open();
            Log.d(TAG, "mOne2OnePullDataCompleted opened on exception");
        }
    }


    public void pullDataForCaller(String name, String phonenum, String company) {

        if (Utils.checkInternetConnection()) {
            mcurrentUserPref = getSharedPreferences(CURRENT_USER, 0);
            String baseURL = mcurrentUserPref.getString("CMN_SERVER_BASE_URL_DEFINE", "");
            if ((baseURL != null) && (baseURL.length() > 0)) CMN_SERVER_BASE_URL_DEFINE = baseURL;
            String result = Utils.httpPost(
                    CMN_SERVER_BASE_URL_DEFINE + "RetrieveCallerInfo.aspx",
                    "token", CMN_Preferences.getUserToken(MyApplication.this),
                    "callername", name.trim(),
                    "phonenumber", phonenum,
                    "companyname", company.trim(),
                    "sync", "1");
            Log.d(TAG, "pullDataForCaller result='" + result + "'");
            if (result == null) {
                Log.w(TAG, "pullDataForCaller RetrieveCallerInfo.aspx request failed");
            } else {
                mCallerSyncInitiated.put(phonenum, true);
            }
        } else {
            Log.w(TAG, "Unable to access network. RetrieveCallerInfo request not initiated");
        }
    }

    public void registerPhoneCallsListener() {
        if (!mRegisteredPhoneListener) {
            Log.d(TAG, "registerPhoneCallsListener");
            TelephonyManager telManager;
            mRegisteredPhoneListener = true;
        } else {
            Log.d(TAG, "registerPhoneCallsListener already registered.");
        }
    }

    public void unregisterPhoneCallsListener() {
        if (mRegisteredPhoneListener) {
            Log.d(TAG, "unregisterPhoneCallsListener");
            TelephonyManager telManager;
            mRegisteredPhoneListener = false;
        } else {
            Log.d(TAG, "unregisterPhoneCallsListener already unregistered");
        }
    }


    public void finishedParsing(String _status) {
        pullInProgress = false;
        Log.d(TAG, "pulldataowner in Myapplication finishedparsing =" + pullDataOwner);
        if (pullDataOwner != null) pullDataOwner.finishedParsing(_status);
        mPullDataCompleted.open();
        Log.d(TAG, "mPullDataCompleted opened");
    }

    public void showDialog() {
        if (pullDataOwner != null) pullDataOwner.showDialog();
    }

    public void closeDialog() {
        if (pullDataOwner != null) pullDataOwner.closeDialog();
    }

    public boolean inDemoMode() {
        return !LocalPrefs.INSTANCE.hasAppCredentials();
    }

    static class OrgsLoader {
        public static String[] SERVER_URLS = {
                "https://corey.mobilecorey.com/CoreyData/Organizations/organizations_s.json",
                "https://dev.mobilecorey.com/CoreyData/Organizations/organizations_s.json",
        };
        private List<String> mServerUrls = Arrays.asList(SERVER_URLS);
        private Iterator<String> mIter;
        IDownloadJSON mHandler;

        public OrgsLoader(IDownloadJSON handler) {
            mHandler = handler;
            reset();
        }

        public void reset() {
            mIter = mServerUrls.iterator();
        }

        public void tryNextUrl() {
            if (mIter.hasNext()) {
                String url = mIter.next();
                System.out.println("OrgsLoader: Trying Url: " + url);
                new GetJSON(INSTANCE).execute(url);
            } else {
                System.out.println("OrgsLoader: no more urls to try");
            }
        }
    }

    public IDialogListener getDialogListener(String id) {
        return mDialogListenerRegistry.get(id);
    }

    public void putDialogListener(String id, IDialogListener listener) {
        mDialogListenerRegistry.put(id, listener);
    }

    public boolean isCallerSyncInitiated(String phonenumber) {
        Boolean val = mCallerSyncInitiated.get(phonenumber);
        return (val != null) && (val == true);
    }

    public Activity mCurActivity = null;
    public Activity mMessageTabActivity = null;
    public Activity mSumMenuActivity = null;

    class ActivityLifeCycleListener implements ActivityLifecycleCallbacks {
        @Override
        public void onActivityCreated(Activity activity, Bundle bundle) {
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
        }

        @Override
        public void onActivityPaused(Activity activity) {
            Log.d(TAG, "onActivityPaused " + activity + ", " + activity.getWindow() + ", " + activity.getWindow().getCallback());
            Window.Callback callback = activity.getWindow().getCallback();
            if (callback instanceof WindowCallback) {
                Log.d(TAG, "onActivityPaused restore window.callback");
                WindowCallback windowCallback = (WindowCallback) callback;
                activity.getWindow().setCallback(windowCallback.getDelegate());
            }
        }

        @Override
        public void onActivityResumed(Activity activity) {
            Log.d(TAG, "onActivityResumed " + activity + ", " + activity.getWindow() + ", " + activity.getWindow().getCallback());
            mCurActivity = activity;
            Window.Callback callback = activity.getWindow().getCallback();
            if (!(callback instanceof WindowCallback)) {
                Log.d(TAG, "onActivityResumed install window.callback");
                activity.getWindow().setCallback(new WindowCallback(callback));
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        }

        @Override
        public void onActivityStarted(Activity activity) {
        }

        @Override
        public void onActivityStopped(Activity activity) {
        }
    }

    class WindowCallback implements Window.Callback {
        private Window.Callback mDelegate;

        public WindowCallback(Window.Callback delegate) {
            mDelegate = delegate;
        }

        public Window.Callback getDelegate() {
            return mDelegate;
        }

        @Override
        public boolean dispatchGenericMotionEvent(MotionEvent event) {
            return mDelegate.dispatchGenericMotionEvent(event);
        }

        @Override
        public boolean dispatchKeyEvent(KeyEvent event) {
            return mDelegate.dispatchKeyEvent(event);
        }

        @Override
        public boolean dispatchKeyShortcutEvent(KeyEvent event) {
            return mDelegate.dispatchKeyShortcutEvent(event);
        }

        @Override
        public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
            return mDelegate.dispatchPopulateAccessibilityEvent(event);
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent event) {
            AutoLogoutManager.INSTANCE.mIdleCounter = 0;
            return mDelegate.dispatchTouchEvent(event);
        }

        @Override
        public boolean dispatchTrackballEvent(MotionEvent event) {
            return mDelegate.dispatchTrackballEvent(event);
        }

        @Override
        public void onActionModeFinished(ActionMode mode) {
            mDelegate.onActionModeFinished(mode);
        }

        @Override
        public void onActionModeStarted(ActionMode mode) {
            mDelegate.onActionModeStarted(mode);
        }

        @Override
        public void onAttachedToWindow() {
            mDelegate.onAttachedToWindow();
        }

        @Override
        public void onContentChanged() {
            mDelegate.onContentChanged();
        }

        @Override
        public boolean onCreatePanelMenu(int arg0, Menu arg1) {
            return mDelegate.onCreatePanelMenu(arg0, arg1);
        }

        @Override
        public View onCreatePanelView(int arg0) {
            return mDelegate.onCreatePanelView(arg0);
        }

        @SuppressLint("MissingSuperCall")
        @Override
        public void onDetachedFromWindow() {
            mDelegate.onDetachedFromWindow();
        }

        @Override
        public boolean onMenuItemSelected(int arg0, MenuItem arg1) {
            return mDelegate.onMenuItemSelected(arg0, arg1);
        }

        @Override
        public boolean onMenuOpened(int arg0, Menu arg1) {
            return mDelegate.onMenuOpened(arg0, arg1);
        }

        @Override
        public void onPanelClosed(int arg0, Menu arg1) {
            mDelegate.onPanelClosed(arg0, arg1);
        }

        @Override
        public boolean onPreparePanel(int arg0, View arg1, Menu arg2) {
            return mDelegate.onPreparePanel(arg0, arg1, arg2);
        }

        @Override
        public boolean onSearchRequested() {
            return mDelegate.onSearchRequested();
        }

        @Override
        public boolean onSearchRequested(SearchEvent searchEvent) {
            return false;
        }

//        @Override
//        public boolean onSearchRequested(SearchEvent searchEvent) {
//            return false;
//        }


        @Override
        public void onWindowAttributesChanged(LayoutParams arg0) {
            mDelegate.onWindowAttributesChanged(arg0);
        }

        @Override
        public void onWindowFocusChanged(boolean arg0) {
            mDelegate.onWindowFocusChanged(arg0);
        }

        @Override
        public ActionMode onWindowStartingActionMode(Callback arg0) {
            return mDelegate.onWindowStartingActionMode(arg0);
        }

        @Nullable
        @Override
        public ActionMode onWindowStartingActionMode(Callback callback, int type) {
            return null;
        }


    }


    public synchronized Tracker getGoogleAnalyticsTracker() {
        CMN_AnalyticsTrackers analyticsTrackers = CMN_AnalyticsTrackers.getInstance();
        return analyticsTrackers.get(CMN_AnalyticsTrackers.Target.APP_TRACKER);
    }


}



