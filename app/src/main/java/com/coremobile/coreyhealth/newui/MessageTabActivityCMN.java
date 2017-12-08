package com.coremobile.coreyhealth.newui;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.coremobile.coreyhealth.AppConfig;
import com.coremobile.coreyhealth.ContextData;
import com.coremobile.coreyhealth.CrystalData;
import com.coremobile.coreyhealth.DatabaseProvider;
import com.coremobile.coreyhealth.DownloadMessageService;
import com.coremobile.coreyhealth.DownloadMessageService.LocalBinder;
import com.coremobile.coreyhealth.GCMClientManager;
import com.coremobile.coreyhealth.GetJSON;
import com.coremobile.coreyhealth.IDownloadJSON;
import com.coremobile.coreyhealth.IPullDataFromServer;
import com.coremobile.coreyhealth.JSONHelperClass;
import com.coremobile.coreyhealth.JSONParser;
import com.coremobile.coreyhealth.MyApplication;
import com.coremobile.coreyhealth.R;
import com.coremobile.coreyhealth.SumMenuActivity;
import com.coremobile.coreyhealth.TabDtl;
import com.coremobile.coreyhealth.Utils;
import com.coremobile.coreyhealth.analytics.CMN_AnalyticsFragment;
import com.coremobile.coreyhealth.base.CMN_AppFragmentBaseActivity;
import com.coremobile.coreyhealth.clientcertificate.CMN_HTTPSClient;
import com.coremobile.coreyhealth.errorhandling.CMN_ErrorMessages;
import com.coremobile.coreyhealth.googleAnalytics.CMN_Preferences;
import com.coremobile.coreyhealth.messaging.CMN_AllMessagesFragment;
import com.coremobile.coreyhealth.mypatients.CMN_MyPatientActivity;
import com.coremobile.coreyhealth.partialsync.GetAllContextWebService;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MessageTabActivityCMN extends CMN_AppFragmentBaseActivity implements
        ActionBar.TabListener, IPullDataFromServer, IDownloadJSON {

    public static boolean shouldCallTriggerAPI = true;
    private String TAG = "Corey_MessageTabActivityCMN";
    public static String CMN_SERVER_BASE_URL_DEFINE;
    public static final String CMN_SERVER_MANUAL_SYNC_API = "TriggerInstantGratification.aspx";
    public static final String CURRENT_USER = "CurrentUser";
    public static boolean isReturned = false;
    public static boolean isUpdated = false;
    public static int mPosition = 0;
    public static int mTabno = 0;
    public static int mTabCount;
    private static WeakReference<MessageTabActivityCMN> wrActivity = null;
    static final int ID_PLAN_AHEAD = 0;
    private ActionBar mActionBar;
    private static DownloadMessageService mDownloadMessageService;
    private static boolean mDownloadMessageServiceBound;
    public SharedPreferences mCurrentUserPref;
    private String organizationName;
    private Handler mHandler;
    private ProgressDialog mProgressDialog;
    public static String mDisplayName;
    public static String mHelpUrl;
    public static Boolean mAutosync;
    static MessageTabActivityCMN MsgTabActivity;
    private MyApplication application;
    private BroadcastReceiver mSyncCompleteReceiver;
    private boolean mRegisteredSyncCompleteReceiver;
    private Map<Integer, IContextMenuItem> mMenuItemsMap; // menuId -> menuItem
    ArrayList<TabDtl> mTabDtl;
    public static Activity activity;
    Intent serviceIntent;
    public static boolean triggerRunning = false;
    public static int patientPosition = 0;
    public static String patientInfo;
    public static HashMap<String, String> checkedState = new HashMap<>();
    private IntentFilter mPushNotificationFilter;
    private NavDrawerManager mNavDrawerManager;
    public ProgressDialog mDialog;
    private boolean isFromList;
    ProgressDialog mRefreshDialog;
    private IntentFilter mDownloadCOmpleteFilter;

    public MessageTabActivityCMN() {
        super();
    }

    public static MessageTabActivityCMN getInstance() {
        return MsgTabActivity;
    }

    @Override
    protected void onCreate(Bundle arg0) {
        arg0 = null;
        super.onCreate(arg0);
        mCurrentUserPref = getSharedPreferences(CURRENT_USER, 0);
        organizationName = mCurrentUserPref.getString("Organization", null);
        Intent mIntent = getIntent();
        mTabno = mIntent.getIntExtra("Tabno", 0);

        activity = MessageTabActivityCMN.this;
        application = (MyApplication) getApplication();

        JSONHelperClass jsonHelperClass = new JSONHelperClass();

        GetAllContextWebService getAllContextWebService = new GetAllContextWebService(MessageTabActivityCMN.this);
        getAllContextWebService.execute();

        CMN_SERVER_BASE_URL_DEFINE = jsonHelperClass
                .getBaseURL(organizationName);

        mTabDtl = jsonHelperClass.getTabDtl();
        if (mTabDtl != null)
            mTabCount = mTabDtl.size();
        else
            mTabCount = 0;
        if ((organizationName == null)
                || (organizationName.trim().length() == 0)) {
            MyApplication.resetApp(true);
            logout();
            finish();
            Toast.makeText(
                    getApplicationContext(),
                    CMN_ErrorMessages.getInstance().getValue(156),
                    Toast.LENGTH_LONG).show();
            return;

        }
        Object mAllAppsData = jsonHelperClass
                .getAllApplicationData(organizationName);
        if (mAllAppsData == null) {
            MyApplication.resetApp(true);
            finish();
            Toast.makeText(
                    getApplicationContext(),
                    CMN_ErrorMessages.getInstance().getValue(156),
                    Toast.LENGTH_LONG).show();
            return;
        }
        MsgTabActivity = this;

        setContentView(R.layout.activity_main);
        application = (MyApplication) getApplication();
        if (application.AppConstants.getAppName().equalsIgnoreCase("COREYOR"))
            getWindow()
                    .addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Utils.forceOverflowMenu(this);
        mNavDrawerManager = new NavDrawerManager(this);
        mMenuItemsMap = new LinkedHashMap<Integer, IContextMenuItem>();

        mHandler = new Handler();


        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Syncing");
        mProgressDialog.setMessage("Please Wait ..");
        mProgressDialog.setCancelable(false);
        mProgressDialog.getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mProgressDialog.show();
        mDisplayName = mCurrentUserPref.getString("DisplayName",
                organizationName);
        mHelpUrl = mCurrentUserPref.getString("helpurl", null);
        mAutosync = mCurrentUserPref.getBoolean("RequireAutoSync", false);
        Log.e("MessageTabActivityCMN", "mHelpUrl" + mHelpUrl);

        wrActivity = new WeakReference<MessageTabActivityCMN>(
                MessageTabActivityCMN.this);

        mActionBar = getActionBar();

        // enable ActionBar app icon to behave as action to toggle nav drawer
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(true);
        mActionBar.setTitle(mDisplayName);
        if (!mAutosync) {
            mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
            mActionBar.setTitle(mDisplayName);
            mActionBar.addTab(mActionBar.newTab().setText("Schedule")
                    .setTabListener(this));
            mActionBar.addTab(mActionBar.newTab().setText("Phone Calls")
                    .setTabListener(this));
            mActionBar.addTab(mActionBar.newTab().setText("Tracker")
                    .setTabListener(this));
            mActionBar.addTab(mActionBar.newTab().setText("Coreyfy")
                    .setTabListener(this));
            mActionBar.addTab(mActionBar.newTab().setText("Location")
                    .setTabListener(this));
            mActionBar.addTab(mActionBar.newTab().setText("To do")
                    .setTabListener(this));
            mActionBar.setSelectedNavigationItem(ID_PLAN_AHEAD);
            Utils.stopDownloadMessageService(MessageTabActivityCMN.this);
            Utils.startDownloadMessageService(MessageTabActivityCMN.this);
        } else {
            if (mTabDtl != null && (mTabDtl.size() != 0)) {
                mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
                mActionBar.setTitle(mDisplayName);
                mActionBar.addTab(mActionBar.newTab()
                        .setText(mTabDtl.get(0).getTabDispText())
                        .setTabListener(this));
                mActionBar.addTab(mActionBar.newTab()
                        .setText(mTabDtl.get(1).getTabDispText())
                        .setTabListener(this));
                mActionBar.addTab(mActionBar.newTab()
                        .setText(mTabDtl.get(2).getTabDispText())
                        .setTabListener(this));

            }
            Log.e(TAG, "MsgTabactivitystate in oncreate ="
                    + MyApplication.INSTANCE.MsgTabActivityState);

        }

        mSyncCompleteReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String coreyCxtName = intent
                        .getStringExtra(MyApplication.INSTANCE.AppConstants.EXTRA_COREY_CONTEXT);
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (!mAutosync) {
                            Log.e(TAG, "Received SYNC_COMPLETED "
                                    + coreyCxtName);
                            int curTabId = mActionBar
                                    .getSelectedNavigationIndex();
                            int newTabId = MessageTabActivityCMN
                                    .getTabId(coreyCxtName);
                            if (curTabId != newTabId) {
                                mActionBar.setSelectedNavigationItem(newTabId);
                            } else {
                                refreshTab(curTabId);
                            }
                        } else
                            refreshTab(0);
                    }
                });
            }
        };
        MyApplication.INSTANCE.mMessageTabActivity = this;

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dashboard_menu, menu);
        if (mMenuItemsMap != null) {
            mMenuItemsMap.clear();
        }
        for (ContextData coreyCxt : MyApplication.INSTANCE.crystalData.mRightContextData) {
            IContextMenuItem item = ContextMenuItemFactory
                    .createContextMenuItem(this, coreyCxt);
            mMenuItemsMap.put(item.getId(), item);
            item.addToMenu(menu);
        }
        if (mTabDtl != null && (mTabDtl.size() != 0)) {
            MenuItem item1 = menu.findItem(R.id.action_home);
            item1.setVisible(true);
            this.invalidateOptionsMenu();
        } else {
            MenuItem item1 = menu.findItem(R.id.action_home);
            item1.setVisible(false);
            this.invalidateOptionsMenu();

        }
        MenuItem item2 = menu.findItem(R.id.action_patients);
        if (AppConfig.isAppCoreyHealth) {
            item2.setVisible(true);
        } else {
            item2.setVisible(false);
        }
        return true;
    }

    public void refreshConfiguration() {
        // CMN_SERVER_BASE_URL_DEFINE = CMN_SERVER_TEST_URL_DEFINE;
        mCurrentUserPref = getSharedPreferences(CURRENT_USER, 0);

        organizationName = mCurrentUserPref.getString("organization", null);
        String url = CMN_SERVER_BASE_URL_DEFINE;
        if (AppConfig.isAESEnabled) {
            url = url + "getConfiguration_s.aspx?token=" + CMN_Preferences.getUserToken(MessageTabActivityCMN.this);
        } else {
            url = url + "getConfiguration.aspx?token=" + CMN_Preferences.getUserToken(MessageTabActivityCMN.this);
        }
        if (Utils.checkInternetConnection()) {
            // Log.i(TAG, "Start Configuration refresh");
            try {
                new GetJSON(this, MessageTabActivityCMN.this).execute(url, "true");
            } catch (Exception e) {
                Toast.makeText(
                        getApplicationContext(),
                        CMN_ErrorMessages.getInstance().getValue(14),
                        Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    CMN_ErrorMessages.getInstance().getValue(14),
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mNavDrawerManager.onOptionsItemSelected(item)) {
            return true;
        }

        if (mMenuItemsMap.containsKey(item.getItemId())) {
            mMenuItemsMap.get(item.getItemId()).onClick();
        } else if (item.getItemId() == R.id.action_home) {
            Intent homeIntent = new Intent(this, SumMenuActivity.class);
            startActivity(homeIntent);
            isReturned = true;
        } else if (item.getItemId() == R.id.action_patients) {
            Intent intent = new Intent(MessageTabActivityCMN.getInstance(), CMN_MyPatientActivity.class);
            startActivity(intent);
            isReturned = true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static ServiceConnection mDMServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mDownloadMessageServiceBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName componentName,
                                       IBinder iBinder) {
            LocalBinder binder = (LocalBinder) iBinder;
            mDownloadMessageService = binder.getService();
            mDownloadMessageServiceBound = true;

        }
    };
    BroadcastReceiver mPushDownloadCompleteReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            if ((application.AppConstants.getReceivedPushIntent()).equals(arg1
                    .getAction())) {
            }
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
                if (mActionBar != null) {
                    if ((wrActivity.get() != null)
                            && (wrActivity.get().isFinishing() != true)) {

                        if (mTabDtl != null && (mTabDtl.size() != 0)) {
                            mActionBar.setSelectedNavigationItem(mTabno);
                            switch (mTabno) {
                                case 0:
                                    MsgScheduleTabActivity viewpActivity = new MsgScheduleTabActivity(
                                            DatabaseProvider.MSG_TYPE_INSTANT_GRATIFICATION,
                                            mAutosync);
                                    viewpActivity.setRetainInstance(true);
                                    wrActivity.get().getSupportFragmentManager()
                                            .beginTransaction()
                                            .replace(R.id.container, viewpActivity)
                                            .commitAllowingStateLoss();
                                    break;
                                case 1:

                                    CMN_AllMessagesFragment MessageTab = new CMN_AllMessagesFragment();
                                    wrActivity.get().getSupportFragmentManager()
                                            .beginTransaction()
                                            .replace(R.id.container, MessageTab)
                                            .commitAllowingStateLoss();
                                    break;
                                case 2:
                                    CMN_AnalyticsFragment AnalyticsTab = new CMN_AnalyticsFragment();

                                    wrActivity.get().getSupportFragmentManager()
                                            .beginTransaction()
                                            .replace(R.id.container, AnalyticsTab)
                                            .commitAllowingStateLoss();
                                    break;
                            }

                        } else {
                            MsgScheduleTabActivity viewpActivity = new MsgScheduleTabActivity(
                                    DatabaseProvider.MSG_TYPE_INSTANT_GRATIFICATION,
                                    mAutosync);
                            viewpActivity.setRetainInstance(true);
                            wrActivity.get().getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.container, viewpActivity)
                                    .commitAllowingStateLoss();

                        }

                        mNavDrawerManager.closeDrawer();

                    }
                }
            }

            if (mRefreshDialog != null && mRefreshDialog.isShowing()) {
                mRefreshDialog.dismiss();
            }
            /* Tab changes */
            if (!mAutosync) {
                switch (mActionBar.getSelectedTab().getPosition()) {
                    case 0:
                        MsgScheduleTabActivity viewpActivity = new MsgScheduleTabActivity(
                                DatabaseProvider.MSG_TYPE_INSTANT_GRATIFICATION,
                                mAutosync);
                        viewpActivity.setRetainInstance(true);
                        wrActivity.get().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.container, viewpActivity)
                                .commitAllowingStateLoss();
                        break;
                    case 2:
                        MsgScheduleTabActivity viewtrackerActivity = new MsgScheduleTabActivity(
                                DatabaseProvider.MSG_TYPE_MARKET_TRACKER, mAutosync);
                        wrActivity.get().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.container, viewtrackerActivity)
                                .commitAllowingStateLoss();
                        break;

                    case 3:
                        MsgScheduleTabActivity viewCoreyfyActivity = new MsgScheduleTabActivity(
                                DatabaseProvider.MSG_TYPE_MARKET_COREFY, mAutosync);
                        wrActivity.get().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.container, viewCoreyfyActivity)
                                .commitAllowingStateLoss();
                        break;

                    case 4:
                        MsgScheduleTabActivity viewLocationActivity = new MsgScheduleTabActivity(
                                DatabaseProvider.MSG_TYPE_LOCATION, mAutosync);
                        wrActivity.get().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.container, viewLocationActivity)
                                .commitAllowingStateLoss();
                        break;

                    case 5:
                        MsgScheduleTabActivity viewTodoActivity = new MsgScheduleTabActivity(
                                DatabaseProvider.MSG_TYPE_TODO, mAutosync);
                        wrActivity.get().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.container, viewTodoActivity)
                                .commitAllowingStateLoss();
                        break;

                    default:
                        break;
                }
            } else {

                if (mTabDtl != null && (mTabDtl.size() != 0)) {
                    mActionBar.setSelectedNavigationItem(mTabno);
                    switch (mTabno) {
                        case 0:
                            MsgScheduleTabActivity viewpActivity = new MsgScheduleTabActivity(
                                    DatabaseProvider.MSG_TYPE_INSTANT_GRATIFICATION,
                                    mAutosync);
                            viewpActivity.setRetainInstance(true);
                            wrActivity.get().getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.container, viewpActivity)
                                    .commitAllowingStateLoss();
                            break;
                        case 1:

                            CMN_AllMessagesFragment MessageTab = new CMN_AllMessagesFragment();
                            wrActivity.get().getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.container, MessageTab)
                                    .commitAllowingStateLoss();
                            break;
                        case 2:
                            CMN_AnalyticsFragment AnalyticsTab = new CMN_AnalyticsFragment();
                            wrActivity.get().getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.container, AnalyticsTab)
                                    .commitAllowingStateLoss();
                            break;
                    }

                } else {
                    MsgScheduleTabActivity viewpActivity = new MsgScheduleTabActivity(
                            DatabaseProvider.MSG_TYPE_INSTANT_GRATIFICATION,
                            mAutosync);
                    viewpActivity.setRetainInstance(true);
                    wrActivity.get().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container, viewpActivity)
                            .commitAllowingStateLoss();

                }
            }
        }
    };


    BroadcastReceiver mPushNotificationReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context arg0, Intent intent) {
            // TODO Auto-generated method stub

            boolean InitialSync = false;
            Log.e(TAG, "Push received" + intent);
            if (mRefreshDialog != null && mRefreshDialog.isShowing()) {
                mRefreshDialog.dismiss();
            }

            if (CMN_Preferences.getisNewPatientAdded(MessageTabActivityCMN.this)) {
                CMN_Preferences.setisNewPatientAdded(MessageTabActivityCMN.this, false);
                GetAllContextWebService getAllContextWebService = new GetAllContextWebService(MessageTabActivityCMN.this, true, mNavDrawerManager);
                getAllContextWebService.execute();
            }

            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
                mDialog = null;
            }
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
                InitialSync = true;
            } // fix to check if screen refresh on diff update
            else {
                String MessgeID = MyApplication.INSTANCE.MsgIdprsr;

                if (application.AppConstants.getAppName().equalsIgnoreCase(
                        "COREYOR")) {
                    MsgScheduleTabActivity viewpActivity = new MsgScheduleTabActivity(
                            DatabaseProvider.MSG_TYPE_INSTANT_GRATIFICATION,
                            mAutosync);
                    viewpActivity.setRetainInstance(true);
                    wrActivity.get().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container, viewpActivity)
                            .commitAllowingStateLoss();
                } else if (MessgeID != null) {
                    Intent partialupdateIntent = new Intent(
                            "corey.partialupdate");
                    partialupdateIntent.putExtra("MessageID", MessgeID);
                    Log.e(TAG, "sending partial update intent  messageid ="
                            + MessgeID);
                    LocalBroadcastManager.getInstance(MsgTabActivity)
                            .sendBroadcast(partialupdateIntent);
                }
            }
            if (InitialSync) {
                if (mActionBar != null) {
                    if ((wrActivity.get() != null)
                            && (wrActivity.get().isFinishing() != true)) {
                        Log.e(TAG, "creating new MsgScheduleTABavtivity"
                                + wrActivity.get());
                        if (mTabDtl != null && (mTabDtl.size() != 0)) {
                            switch (mActionBar.getSelectedTab().getPosition()) {
                                case 0:
                                    MsgScheduleTabActivity viewpActivity = new MsgScheduleTabActivity(
                                            DatabaseProvider.MSG_TYPE_INSTANT_GRATIFICATION,
                                            mAutosync);
                                    viewpActivity.setRetainInstance(true);
                                    wrActivity.get().getSupportFragmentManager()
                                            .beginTransaction()
                                            .replace(R.id.container, viewpActivity)
                                            .commitAllowingStateLoss();
                                    break;
                                case 1:

                                    CMN_AllMessagesFragment MessageTab = new CMN_AllMessagesFragment();
                                    wrActivity.get().getSupportFragmentManager()
                                            .beginTransaction()
                                            .replace(R.id.container, MessageTab)
                                            .commitAllowingStateLoss();
                                    break;
                                case 2:
                                    CMN_AnalyticsFragment AnalyticsTab = new CMN_AnalyticsFragment();
                                    wrActivity.get().getSupportFragmentManager()
                                            .beginTransaction()
                                            .replace(R.id.container, AnalyticsTab)
                                            .commitAllowingStateLoss();
                                    break;
                            }

                        } else {
                            MsgScheduleTabActivity viewpActivity = new MsgScheduleTabActivity(
                                    DatabaseProvider.MSG_TYPE_INSTANT_GRATIFICATION,
                                    mAutosync);
                            viewpActivity.setRetainInstance(true);
                            wrActivity.get().getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.container, viewpActivity)
                                    .commitAllowingStateLoss();

                        }

                        mNavDrawerManager.closeDrawer();

                    }
                }
                /* Tab changes */
                if (!mAutosync) {
                    Log.e(TAG, "Switch condition"
                            + mActionBar.getSelectedTab().getPosition());
                    switch (mActionBar.getSelectedTab().getPosition()) {
                        case 0:
                            MsgScheduleTabActivity viewpActivity = new MsgScheduleTabActivity(
                                    DatabaseProvider.MSG_TYPE_INSTANT_GRATIFICATION,
                                    mAutosync);
                            viewpActivity.setRetainInstance(true);
                            wrActivity.get().getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.container, viewpActivity)
                                    .commitAllowingStateLoss();
                            break;
                        case 2:
                            MsgScheduleTabActivity viewtrackerActivity = new MsgScheduleTabActivity(
                                    DatabaseProvider.MSG_TYPE_MARKET_TRACKER,
                                    mAutosync);
                            Log.e(TAG,
                                    "creating new MsgScheduleTABavtivity in switch structure");
                            wrActivity.get().getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.container, viewtrackerActivity)
                                    .commitAllowingStateLoss();
                            break;

                        case 3:
                            MsgScheduleTabActivity viewCoreyfyActivity = new MsgScheduleTabActivity(
                                    DatabaseProvider.MSG_TYPE_MARKET_COREFY,
                                    mAutosync);
                            wrActivity.get().getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.container, viewCoreyfyActivity)
                                    .commitAllowingStateLoss();
                            break;

                        case 4:
                            MsgScheduleTabActivity viewLocationActivity = new MsgScheduleTabActivity(
                                    DatabaseProvider.MSG_TYPE_LOCATION, mAutosync);
                            wrActivity.get().getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.container, viewLocationActivity)
                                    .commitAllowingStateLoss();
                            break;

                        case 5:
                            MsgScheduleTabActivity viewTodoActivity = new MsgScheduleTabActivity(
                                    DatabaseProvider.MSG_TYPE_TODO, mAutosync);
                            wrActivity.get().getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.container, viewTodoActivity)
                                    .commitAllowingStateLoss();
                            break;

                        default:
                            break;
                    }
                } else {
                    if (mTabDtl != null && (mTabDtl.size() != 0)) {
                        switch (mActionBar.getSelectedTab().getPosition()) {
                            case 0:
                                MsgScheduleTabActivity viewpActivity = new MsgScheduleTabActivity(
                                        DatabaseProvider.MSG_TYPE_INSTANT_GRATIFICATION,
                                        mAutosync);
                                viewpActivity.setRetainInstance(true);
                                wrActivity.get().getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.container, viewpActivity)
                                        .commitAllowingStateLoss();
                                break;
                            case 1:

                                CMN_AllMessagesFragment MessageTab = new CMN_AllMessagesFragment();
                                wrActivity.get().getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.container, MessageTab)
                                        .commitAllowingStateLoss();
                                break;
                            case 2:
                                CMN_AnalyticsFragment AnalyticsTab = new CMN_AnalyticsFragment();
                                wrActivity.get().getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.container, AnalyticsTab)
                                        .commitAllowingStateLoss();
                                break;
                        }

                    } else {
                        MsgScheduleTabActivity viewpActivity = new MsgScheduleTabActivity(
                                DatabaseProvider.MSG_TYPE_INSTANT_GRATIFICATION,
                                mAutosync);
                        viewpActivity.setRetainInstance(true);
                        wrActivity.get().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.container, viewpActivity)
                                .commitAllowingStateLoss();

                    }
                }
            }
        }
    };


    private void refreshTab(int tabId) {
        /* Tab changes */
        if (!mAutosync) {
            Log.e(TAG, "refresh tab " + tabId);
            if (tabId == 0) {
                mPosition = 0;
                MsgScheduleTabActivity viewpActivity = new MsgScheduleTabActivity(
                        DatabaseProvider.MSG_TYPE_INSTANT_GRATIFICATION,
                        mAutosync);
                wrActivity.get().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, viewpActivity).commit();
                viewpActivity.setRetainInstance(true);

            } else if (tabId == 1) {
                mPosition = 0;

                MsgScheduleTabActivity viewpActivity = new MsgScheduleTabActivity(
                        DatabaseProvider.MSG_TYPE_PHONE_CALL, mAutosync);
                // MsgPhoneCallsTabActivity viewpActivity = new
                // MsgPhoneCallsTabActivity();
                wrActivity.get().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, viewpActivity).commit();
            } else if (tabId == 2) {
                mPosition = 0;
                MsgScheduleTabActivity viewtrackerActivity = new MsgScheduleTabActivity(
                        DatabaseProvider.MSG_TYPE_MARKET_TRACKER, mAutosync);
                // MsgTrackerTabActivity viewtrackerActivity = new
                // MsgTrackerTabActivity();
                wrActivity.get().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, viewtrackerActivity).commit();
            } else if (tabId == 3) {
                mPosition = 0;

                // MsgCoreyfyTabActivity viewpActivity = new
                // MsgCoreyfyTabActivity();
                MsgScheduleTabActivity viewCoreyfyActivity = new MsgScheduleTabActivity(
                        DatabaseProvider.MSG_TYPE_MARKET_COREFY, mAutosync);
                wrActivity.get().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, viewCoreyfyActivity).commit();

            } else if (tabId == 4) {
                mPosition = 0;

                MsgScheduleTabActivity viewpActivity = new MsgScheduleTabActivity(
                        DatabaseProvider.MSG_TYPE_LOCATION, mAutosync);
                wrActivity.get().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, viewpActivity).commit();
            } else if (tabId == 5) {
                mPosition = 0;
                MsgScheduleTabActivity viewpActivity = new MsgScheduleTabActivity(
                        DatabaseProvider.MSG_TYPE_TODO, mAutosync);
                wrActivity.get().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, viewpActivity).commit();
            } else {
                Log.w(TAG, "Unknown tab id to refreshTab: " + tabId);
            }
        } else {
            if (mTabDtl != null && (mTabDtl.size() != 0)) {
                if (tabId == 0) {
                    mPosition = 0;
                    MsgScheduleTabActivity viewpActivity = new MsgScheduleTabActivity(
                            DatabaseProvider.MSG_TYPE_INSTANT_GRATIFICATION,
                            mAutosync);
                    wrActivity.get().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container, viewpActivity).commit();
                    viewpActivity.setRetainInstance(true);
                } else if (tabId == 1) {
                    mPosition = 0;
                    CMN_AllMessagesFragment viewpActivity = new CMN_AllMessagesFragment();
                    wrActivity.get().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container, viewpActivity).commit();
                    viewpActivity.setRetainInstance(true);
                } else if (tabId == 2) {
                    mPosition = 0;
                    CMN_AnalyticsFragment AnalyticsTab = new CMN_AnalyticsFragment();
                    wrActivity.get().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container, AnalyticsTab).commit();
                    AnalyticsTab.setRetainInstance(true);
                }
            } else {
                mPosition = 0;
                MsgScheduleTabActivity viewpActivity = new MsgScheduleTabActivity(
                        DatabaseProvider.MSG_TYPE_INSTANT_GRATIFICATION,
                        mAutosync);
                wrActivity.get().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, viewpActivity).commit();
                viewpActivity.setRetainInstance(true);
            }
        }
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();

        mDownloadCOmpleteFilter = new IntentFilter();

        mDownloadCOmpleteFilter.addAction(application.AppConstants
                .getDownloadMessageServiceActionCompleteIntent());
        mPushNotificationFilter = new IntentFilter();
        mPushNotificationFilter.addAction(application.AppConstants
                .getDownloadCompleteIntent());
        registerReceiver(mPushDownloadCompleteReceiver, mDownloadCOmpleteFilter);
        registerReceiver(mPushNotificationReceiver, mPushNotificationFilter);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        isReturned = false;
        setIntent(intent);
        if (intent.getBooleanExtra("AppCredentialsUpdated", false)) {
            Log.e(TAG, "AppCredentialsUpdated ask for schedule sync");
            IContextAction scheduleSyncAction = ContextControllerFactory
                    .getContextAction(this, MyApplication.crystalData
                            .getContext("Schedule",
                                    CrystalData.ContextListType.LEFT));
            scheduleSyncAction.onClick(null);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter syncCompleteIntentFilter = new IntentFilter();
        syncCompleteIntentFilter
                .addAction(MyApplication.INSTANCE.AppConstants.ACTION_SYNC_COMPLETE);
        registerReceiver(mSyncCompleteReceiver, syncCompleteIntentFilter);
        mRegisteredSyncCompleteReceiver = true;
        if (getIntent().getAction() != null
                && getIntent().getAction().equalsIgnoreCase("com.for.view")
                && !isReturned) {
            Log.e(TAG, "isReturned = " + isReturned);
            if (mActionBar != null) {

                Bundle bundle = new Bundle();

                bundle.putInt("position", getIntent()
                        .getIntExtra("Position", 0));

                isFromList = getIntent().getBooleanExtra("isFromList", false);

                bundle.putBoolean("isFromList", isFromList);
                /* Tab changes */
                if (!mAutosync) {
                    mActionBar.setSelectedNavigationItem(getIntent()
                            .getIntExtra("CallerTab", 0));

                    switch (getIntent().getIntExtra("CallerTab", 0)) {
                        case 0:
                            MsgScheduleTabActivity viewpActivity = new MsgScheduleTabActivity(
                                    DatabaseProvider.MSG_TYPE_INSTANT_GRATIFICATION,
                                    mAutosync);
                            viewpActivity.setRetainInstance(true);
                            viewpActivity.setArguments(bundle);
                            wrActivity.get().getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.container, viewpActivity)
                                    .commitAllowingStateLoss();
                            break;
                        case 1:
                            MsgScheduleTabActivity viewphActivity = new MsgScheduleTabActivity(
                                    DatabaseProvider.MSG_TYPE_PHONE_CALL, mAutosync);
                            viewphActivity.setRetainInstance(true);
                            viewphActivity.setArguments(bundle);
                            wrActivity.get().getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.container, viewphActivity)
                                    .commitAllowingStateLoss();
                            break;

                        case 2:
                            MsgScheduleTabActivity viewtrackerActivity = new MsgScheduleTabActivity(
                                    DatabaseProvider.MSG_TYPE_MARKET_TRACKER,
                                    mAutosync);
                            viewtrackerActivity.setRetainInstance(true);
                            viewtrackerActivity.setArguments(bundle);
                            wrActivity.get().getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.container, viewtrackerActivity)
                                    .commitAllowingStateLoss();
                            break;

                        case 3:

                            MsgScheduleTabActivity viewCoreyfyActivity = new MsgScheduleTabActivity(
                                    DatabaseProvider.MSG_TYPE_MARKET_COREFY,
                                    mAutosync);
                            viewCoreyfyActivity.setRetainInstance(true);
                            viewCoreyfyActivity.setArguments(bundle);
                            wrActivity.get().getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.container, viewCoreyfyActivity)
                                    .commitAllowingStateLoss();
                            break;

                        case 4:
                            MsgScheduleTabActivity viewLocationActivity = new MsgScheduleTabActivity(
                                    DatabaseProvider.MSG_TYPE_LOCATION, mAutosync);
                            viewLocationActivity.setRetainInstance(true);
                            viewLocationActivity.setArguments(bundle);
                            wrActivity.get().getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.container, viewLocationActivity)
                                    .commitAllowingStateLoss();
                            break;

                        case 5:
                            MsgScheduleTabActivity viewTodoActivity = new MsgScheduleTabActivity(
                                    DatabaseProvider.MSG_TYPE_TODO, mAutosync);
                            viewTodoActivity.setRetainInstance(true);
                            viewTodoActivity.setArguments(bundle);
                            wrActivity.get().getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.container, viewTodoActivity)
                                    .commitAllowingStateLoss();
                            break;

                        default:
                            break;
                    }
                } else {

                    if (mTabDtl != null && (mTabDtl.size() != 0)) {
                        mActionBar.setSelectedNavigationItem(getIntent()
                                .getIntExtra("CallerTab", 0));
                        // switch (mActionBar.getSelectedTab().getPosition()) {
                        switch (getIntent().getIntExtra("CallerTab", 0)) {
                            case 0:
                                MsgScheduleTabActivity viewpActivity = new MsgScheduleTabActivity(
                                        DatabaseProvider.MSG_TYPE_INSTANT_GRATIFICATION,
                                        mAutosync);
                                viewpActivity.setRetainInstance(true);
                                viewpActivity.setArguments(bundle);
                                wrActivity.get().getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.container, viewpActivity)
                                        .commitAllowingStateLoss();
                                break;
                            case 1:

                                CMN_AllMessagesFragment MessageTab = new CMN_AllMessagesFragment();
                                wrActivity.get().getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.container, MessageTab)
                                        .commitAllowingStateLoss();
                                break;
                            case 2:
                                CMN_AnalyticsFragment AnalyticsTab = new CMN_AnalyticsFragment();
                                wrActivity.get().getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.container, AnalyticsTab)
                                        .commitAllowingStateLoss();
                                break;
                        }

                    } else {

                        MsgScheduleTabActivity viewpActivity = new MsgScheduleTabActivity(
                                DatabaseProvider.MSG_TYPE_INSTANT_GRATIFICATION,
                                mAutosync);
                        viewpActivity.setRetainInstance(true);
                        viewpActivity.setArguments(bundle);
                        wrActivity.get().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.container, viewpActivity)
                                .commitAllowingStateLoss();

                    }
                }
            }
        } else if (isReturned) {
            isUpdated = false;
            Bundle bundle = new Bundle();
            bundle.putInt("position",
                    getIntent().getIntExtra("Position", mPosition));
            bundle.putBoolean("isFromList", true);
            if (!mAutosync) {
                switch (mActionBar.getSelectedTab().getPosition()) {
                    case 0:
                        MsgScheduleTabActivity viewpActivity = new MsgScheduleTabActivity(
                                DatabaseProvider.MSG_TYPE_INSTANT_GRATIFICATION,
                                mAutosync);
                        viewpActivity.setRetainInstance(true);
                        viewpActivity.setArguments(bundle);
                        wrActivity.get().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.container, viewpActivity)
                                .commitAllowingStateLoss();
                        break;

                    case 1:
                        MsgScheduleTabActivity viewphActivity = new MsgScheduleTabActivity(
                                DatabaseProvider.MSG_TYPE_PHONE_CALL, mAutosync);
                        viewphActivity.setRetainInstance(true);
                        viewphActivity.setArguments(bundle);
                        wrActivity.get().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.container, viewphActivity)
                                .commitAllowingStateLoss();
                        break;

                    case 2:
                        MsgScheduleTabActivity viewtrackerActivity = new MsgScheduleTabActivity(
                                DatabaseProvider.MSG_TYPE_MARKET_TRACKER, mAutosync);
                        viewtrackerActivity.setRetainInstance(true);
                        viewtrackerActivity.setArguments(bundle);
                        wrActivity.get().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.container, viewtrackerActivity)
                                .commitAllowingStateLoss();
                        break;

                    case 3:

                        MsgScheduleTabActivity viewCoreyfyActivity = new MsgScheduleTabActivity(
                                DatabaseProvider.MSG_TYPE_MARKET_COREFY, mAutosync);
                        viewCoreyfyActivity.setRetainInstance(true);
                        viewCoreyfyActivity.setArguments(bundle);
                        wrActivity.get().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.container, viewCoreyfyActivity)
                                .commitAllowingStateLoss();
                        break;

                    case 4:
                        MsgScheduleTabActivity viewLocationActivity = new MsgScheduleTabActivity(
                                DatabaseProvider.MSG_TYPE_LOCATION, mAutosync);
                        viewLocationActivity.setRetainInstance(true);
                        viewLocationActivity.setArguments(bundle);
                        wrActivity.get().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.container, viewLocationActivity)
                                .commitAllowingStateLoss();
                        break;

                    case 5:
                        MsgScheduleTabActivity viewTodoActivity = new MsgScheduleTabActivity(
                                DatabaseProvider.MSG_TYPE_TODO, mAutosync);
                        viewTodoActivity.setRetainInstance(true);
                        viewTodoActivity.setArguments(bundle);
                        wrActivity.get().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.container, viewTodoActivity)
                                .commitAllowingStateLoss();
                        break;

                    default:
                        break;
                }
            } else {
                if (mTabDtl != null && (mTabDtl.size() != 0)) {
                    Log.e(TAG, "mActionBar.getSelectedTab().getPosition()= "
                            + mActionBar.getSelectedTab().getPosition());
                    switch (mActionBar.getSelectedTab().getPosition()) {
                        case 0:
                            MsgScheduleTabActivity viewpActivity = new MsgScheduleTabActivity(
                                    DatabaseProvider.MSG_TYPE_INSTANT_GRATIFICATION,
                                    mAutosync);
                            viewpActivity.setRetainInstance(true);
                            viewpActivity.setArguments(bundle);
                            wrActivity.get().getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.container, viewpActivity)
                                    .commitAllowingStateLoss();
                            break;
                        case 1:

                            CMN_AllMessagesFragment MessageTab = new CMN_AllMessagesFragment();
                            wrActivity.get().getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.container, MessageTab)
                                    .commitAllowingStateLoss();
                            break;
                        case 2:
                            CMN_AnalyticsFragment AnalyticsTab = new CMN_AnalyticsFragment();
                            wrActivity.get().getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.container, AnalyticsTab)
                                    .commitAllowingStateLoss();
                            break;
                    }

                } else {

                    MsgScheduleTabActivity viewpActivity = new MsgScheduleTabActivity(
                            DatabaseProvider.MSG_TYPE_INSTANT_GRATIFICATION,
                            mAutosync);
                    viewpActivity.setRetainInstance(true);
                    viewpActivity.setArguments(bundle);
                    wrActivity.get().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container, viewpActivity)
                            .commitAllowingStateLoss();

                }
            }

        }

        if (MyApplication.USE_GCM) {
            GCMClientManager.checkPlayServices(this);
        }

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mNavDrawerManager.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mNavDrawerManager.onConfigurationChanged(newConfig);
    }

    /* *
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mNavDrawerManager != null && mNavDrawerManager.isDrawerOpen()) {
            menu.clear();
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.INSTANCE.mMessageTabActivity = null;
        MyApplication.INSTANCE.MsgTabActivityState = 0;

        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        try {
            if (mPushDownloadCompleteReceiver != null)
                unregisterReceiver(mPushDownloadCompleteReceiver);
            if (mPushNotificationReceiver != null)
                unregisterReceiver(mPushNotificationReceiver);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Unregistering receiver which was not registered");
        }

    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
        if (mRegisteredSyncCompleteReceiver) {
            unregisterReceiver(mSyncCompleteReceiver);
            mRegisteredSyncCompleteReceiver = false;
        }
        if (mDownloadMessageServiceBound) {
            try {
                unbindService(mDMServiceConnection);
            } catch (IllegalArgumentException e) {
                e.getMessage();
            }
        }
        isReturned = true;
        Log.e(TAG, "In OnPause isReturned  = " + isReturned);
        // getIntent().setAction("xyz");

    }


    public void signout() {

        AlertDialog.Builder signoutBuilder = new AlertDialog.Builder(
                MessageTabActivityCMN.this);
        signoutBuilder
                .setTitle("Signout")
                .setMessage("Do you want to signout ?")
                .setPositiveButton(android.R.string.yes,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                                Utils.signout();
                            }
                        }).setNegativeButton(android.R.string.no, null).show();

    }

    public void logout() {
        Utils.signout();
    }

    @Override
    public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction fragmentTransaction) {

        try {
            refreshTab(tab.getPosition());
        } catch (IllegalStateException e) {
            e.getMessage();
        }
        mNavDrawerManager.closeDrawer();
    }

    @Override
    public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
    }

    public class GetAllDataAsyncTask extends AsyncTask<String, Void, Void> {
        Context activity;
        Boolean ret;
        ProgressDialog progressDialog;

        public GetAllDataAsyncTask(Context activity) {
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(activity);
            progressDialog.setTitle("Syncing");
            progressDialog.setMessage("Please Wait ..");
            progressDialog.setCancelable(false);
            progressDialog.getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        if (GetAllContextWebService.patientsContexts != null && GetAllContextWebService.patientsContexts.size() != 0) {


        }

        @Override
        protected Void doInBackground(String... arg0) {
            triggerRunning = true;
            ret = startSync(activity, arg0[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (ret != null && ret) {
                serviceIntent = new Intent(activity,
                        DownloadMessageService.class);
                activity.stopService(serviceIntent);
                if (mDMServiceConnection != null) {
                    try {
                        activity.bindService(serviceIntent, mDMServiceConnection,
                                Context.BIND_AUTO_CREATE);
                    } catch (Exception e) {
                        e.getMessage();
                    }
                }
            }
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

        }
    }

    private boolean startSync(final Context activity, String context) {

        if (CMN_Preferences.getCurrentContextId(activity).equalsIgnoreCase(context)) {
            return false;
        } else {
            CMN_Preferences.setCurrentContextId(activity, context);
            CMN_Preferences.setLastViewedContextId(activity,context);
            CMN_Preferences.setContextId(activity, context);
        }

        mCurrentUserPref = activity.getSharedPreferences(CURRENT_USER, 0);

        String url = CMN_SERVER_BASE_URL_DEFINE
                + MessageTabActivityCMN.CMN_SERVER_MANUAL_SYNC_API + "?token=" + CMN_Preferences.getUserToken(MessageTabActivityCMN.this);
        ;
        String contextId = "";

        if (context.trim().length() > 0) {
            contextId = context.toUpperCase();
            CMN_Preferences.setCurrentContextId(MessageTabActivityCMN.activity, context);
        } else {
            contextId = CMN_Preferences.getCurrentContextId(MessageTabActivityCMN.activity).toUpperCase();
        }


        if (Utils.checkInternetConnection()) {
            CloseableHttpClient httpclient = new CMN_HTTPSClient(MyApplication.INSTANCE, url).getClient();
            HttpPost httppost = new HttpPost(url);
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("type", "web_server"));
            try {
                if (GetAllContextWebService.patientsContexts.size() > 0) {
                    nameValuePairs.add(new BasicNameValuePair("context",
                            contextId));
//                    Log.e(TAG, "url: " + url + "?username=" + userName + "&password=" + password + "&organization=" + orgname + "&context=" + contextId);
                } else {
//                    Log.e(TAG, "url: " + url + "?username=" + userName + "&password=" + password + "&organization=" + orgname);
                }

                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
                        nameValuePairs);
                entity.setContentEncoding(HTTP.UTF_8);
                httppost.setEntity(entity);
                httppost.setHeader("Content-Type",
                        "application/x-www-form-urlencoded");
                // Log.i(TAG,"httppost: " + httppost);
                HttpResponse response = httpclient.execute(httppost);
                Log.e(TAG, "POST response: " + response.getStatusLine());
                InputStream inputStream = null;
                JSONObject json = null;
                json = new JSONObject(EntityUtils.toString(response.getEntity()));
                if (json != null) {
                    // closeDialog();
                    String jsonResult = json.getString("result");
                    // String resultText = json.getString("text");
                    Log.e(TAG, "trigger json response: " + jsonResult);
                    if (jsonResult.equals("0") || jsonResult.equals("1")) {
                        triggerRunning = false;
                        stopTimer();
                        return true;
                    } else {
                        return false;
                    }
                }

            } catch (UnsupportedEncodingException e1) {
                e1.getMessage();
                return false;
            } catch (IllegalStateException e) {
                e.getMessage();
                return false;
            } catch (ClientProtocolException e) {
                e.getMessage();
                return false;
            } catch (IOException e) {
                e.getMessage();
                return false;
            } catch (JSONException e) {
                e.getMessage();
                return false;
            } finally {
                if (httpclient != null) {
                    try {
                        httpclient.close();
                    } catch (IOException e) {
                        e.getMessage();
                    }
                }
            }

        } else {
            return false;
        }
        return true;
    }


    private void stopTimer() {
        Log.e(TAG, "stopTimer");
        if (application != null) {

            if (application.AppConstants != null && application.AppConstants
                    .getMessageTabActivityControlTimerFilter() != null) {
                Intent intent = new Intent(
                        application.AppConstants
                                .getMessageTabActivityControlTimerFilter());
                intent.putExtra("EXTRA_ACTION", "STOP");
                sendBroadcast(intent);
            }
        }
    }


    public void getSinglePatientData(Context activity, String context) {
        new GetAllDataAsyncTask(activity).execute(context);
    }

    public void dismissDialogue() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void finishedParsing(String _status) {
        Log.e(TAG, "finishedParsing");
        if (GetAllContextWebService.patientsContexts.size() == 0) {
            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }
        if (MyApplication.INSTANCE.MsgTabActivityState == 0) {
            MyApplication.INSTANCE.MsgTabActivityState++;
            Log.e(TAG, "MsgTabActivityState after getting user"
                    + MyApplication.INSTANCE.MsgTabActivityState);
            Log.e(TAG, "userlist"
                    + ((MyApplication) getApplication()).StaffList);
        } else {
            Log.e(TAG, "Finished parsing entered here");
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            closeDialog();
            if (!mAutosync) {
                switch (mActionBar.getSelectedTab().getPosition()) {
                    case 0:
                        MsgScheduleTabActivity viewpActivity = new MsgScheduleTabActivity(
                                DatabaseProvider.MSG_TYPE_INSTANT_GRATIFICATION,
                                mAutosync);
                        viewpActivity.setRetainInstance(true);
                        wrActivity.get().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.container, viewpActivity)
                                .commitAllowingStateLoss();
                        break;

                    case 1:
                        MsgScheduleTabActivity viewphActivity = new MsgScheduleTabActivity(
                                DatabaseProvider.MSG_TYPE_PHONE_CALL, mAutosync);
                        viewphActivity.setRetainInstance(true);
                        wrActivity.get().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.container, viewphActivity)
                                .commitAllowingStateLoss();
                        break;
                    case 2:
                        MsgScheduleTabActivity viewtrackerActivity = new MsgScheduleTabActivity(
                                DatabaseProvider.MSG_TYPE_MARKET_TRACKER, mAutosync);
                        viewtrackerActivity.setRetainInstance(true);
                        wrActivity.get().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.container, viewtrackerActivity)
                                .commitAllowingStateLoss();
                        break;

                    case 3:
                        MsgScheduleTabActivity viewCoreyfyActivity = new MsgScheduleTabActivity(
                                DatabaseProvider.MSG_TYPE_MARKET_COREFY, mAutosync);
                        viewCoreyfyActivity.setRetainInstance(true);
                        wrActivity.get().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.container, viewCoreyfyActivity)
                                .commitAllowingStateLoss();
                        break;

                    case 4:
                        MsgScheduleTabActivity viewLocationActivity = new MsgScheduleTabActivity(
                                DatabaseProvider.MSG_TYPE_LOCATION, mAutosync);
                        wrActivity.get().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.container, viewLocationActivity)
                                .commitAllowingStateLoss();
                        break;

                    case 5:
                        MsgScheduleTabActivity viewTodoActivity = new MsgScheduleTabActivity(
                                DatabaseProvider.MSG_TYPE_TODO, mAutosync);
                        viewTodoActivity.setRetainInstance(true);
                        wrActivity.get().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.container, viewTodoActivity)
                                .commitAllowingStateLoss();
                        break;

                    default:
                        break;
                }
            } else {
                MsgScheduleTabActivity viewpActivity = new MsgScheduleTabActivity(
                        DatabaseProvider.MSG_TYPE_INSTANT_GRATIFICATION,
                        mAutosync);
                viewpActivity.setRetainInstance(true);
                wrActivity.get().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, viewpActivity)
                        .commitAllowingStateLoss();
            }
        }
    }

    @Override
    public void showDialog() {
        if (isFinishing()) {
            return;
        }
//        Log.i(TAG, "show Dialog");
        if (mDialog == null) {
            // refreshData.setVisibility(View.INVISIBLE);
            mDialog = ProgressDialog.show(this, "",
                    "Retrieving messages from server...", true, false);
        }
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
    public static void getNewPatients() {
        if (CMN_Preferences.getisNewPatientAdded(MsgTabActivity)) {
            if (MsgTabActivity == null) {
                return;
            }
            NavDrawerManager mNavDrawerManager = new NavDrawerManager(MsgTabActivity);
            CMN_Preferences.setisNewPatientAdded(MsgTabActivity, false);
            GetAllContextWebService getAllContextWebService = new GetAllContextWebService(MsgTabActivity, true, mNavDrawerManager);
            getAllContextWebService.execute();
        }
    }

    @Override
    public void buildUI(JSONObject jsonObject) {
        CrystalData source = new CrystalData();
        JSONParser jsonParser = new JSONParser();

        CrystalData result = jsonParser.buildUI(jsonObject, source, this);
        if (result != null) {
            MyApplication.crystalData = result;
            Toast.makeText(getApplicationContext(),
                    CMN_ErrorMessages.getInstance().getValue(159), Toast.LENGTH_LONG)
                    .show();
        } else {
            Log.e(TAG, "Failed to get JSON data for the user");
        }

    }

    @Override
    public void onBackPressed() {
        // consume back pressed, either press home or signout to get out of the
        // app
    }


    private static Map<String, Integer> mContextToTabIdMap = null;

    public static int getTabId(String contextName) {
        if (mContextToTabIdMap == null) {
            mContextToTabIdMap = new HashMap<String, Integer>();
            // --- mapping for CoreySales ---
            // Schedule, instant_gratification
            mContextToTabIdMap.put("Schedule", 0);

            // Phone, retrievecallerinfo
            mContextToTabIdMap.put("Phone", 1);

            // Tracker, market_tracker
            mContextToTabIdMap.put("Tracker", 2);

            // Coreyfy, corefy
            mContextToTabIdMap.put("Coreyfy", 3);

            // Location, location
            mContextToTabIdMap.put("Location", 4);

            // ToDo, todo
            mContextToTabIdMap.put("ToDo", 5);
        }
        if (mContextToTabIdMap.containsKey(contextName)) {
            return mContextToTabIdMap.get(contextName);
        } else {
            return -1;
        }
    }

    public void closeDrawer() {
        mNavDrawerManager.closeDrawer();
    }

}
