package com.coremobile.coreyhealth;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.coremobile.coreyhealth.base.CMN_AppBaseActivity;
import com.coremobile.coreyhealth.clientcertificate.CMN_HTTPSClient;
import com.coremobile.coreyhealth.errorhandling.ActivityPackage;
import com.coremobile.coreyhealth.googleAnalytics.CMN_Preferences;
import com.coremobile.coreyhealth.newui.MessageTabActivityCMN;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//import android.location.Location;
//import android.location.LocationListener;


public class MessageListActivityCMN extends CMN_AppBaseActivity implements
        IPullDataFromServer, IDownloadJSON {

    private String TAG = "Corey_MessageListActivityCMN";
    public static String CMN_SERVER_BASE_URL_DEFINE;
    static final String CMN_SERVER_MANUAL_SYNC_API = "TriggerInstantGratification.aspx";
    static final String CMN_SERVER_IS_SYNC_IN_PROGRESS_API = "isSyncInProgress.aspx";
    static final String CMN_SERVER_LOCATION_SYNC_API = "updatelocation.aspx";
    static final String CMN_PROCESS_CLDR_API = "ProcessCalendarData.aspx";

    static final private int HELP_DIALOG = 0;
    static final private int INSTANT_GRATIFICATION_DIALOG = 1;
    static final private int MISSING_CREDENTIALS_DIALOG = 2;
    static final private int LOCATIONSUCCESS_MESSAGE = 10;
    static final private int LOCATIONFAILED_MESSAGE = 11;
    static final private int ENABLE_LOCATIONSYSTEMS_MESSAGE = 12;
    static final private int ENABLE_LOCATION_WIRELESS_SYSTEMS_MESSAGE = 14;
    static final private int PICK_FEATURES_DIALOG = 18;
    static final private int ENABLE_LOCATIONSYNC_DIALOG = 19;
    static final private int USE_OR_DEMO_DIALOG = 20;
    static final private int PICK_CLDRS_DIALOG = 21;
    static final private int DEMO_PREP_FAILED_DIALOG = 22;
    static final private int START_USING_COREY_DIALOG = 23;
    static final private int NO_DIALOG = -1;

    static final private int SIGNOUT = 5;
    static final private int GENERAL_SETTINGS = 6;
    static final private int APPLICATION_SETTINGS = 7;
    static final private int REFRESH = 8;
    static final private int LOCATIONSYNC = 9;
    static final private int CALENDARSYNC = 10;
    static final private int CONFIGSYNC = 11;
    static final private int HELP = 12;
    static final private int ABOUT = 13;
    static final private int COREFY = 114;
    public static String mDisplayName, mOrganisationName;

    static final private int DELETE_CONTEXTMENU_FIRSTLISTVIEW = 3;
    static final private int DELETE_CONTEXTMENU_SECONDLISTVIEW = 4;
    static final private int DELETE_CONTEXTMENU_THIRDLISTVIEW = 17;

    static final private int PULL_DATA_WAIT = 30000; // 30secs in millis
    public static MessageListActivityCMN MsgListActivity;

    // static final private int MAX_ALLOWED_MESSAGES = 10;

    boolean mFinishedParsing;
    String mRefreshFlag;
    boolean mSigned4first = true;
    protected ListView firstListView;
    protected ListView secondListView;
    protected ListView thirdListView;
    protected ListView seventhListView;
    private ListView eighthListView;
    ArrayList<ListView> mAllListViews = new ArrayList<ListView>();
    ArrayList<Button> mAllHeaders = new ArrayList<Button>();
    public ArrayList<MessageItem> messageItems = new ArrayList<MessageItem>();
    public ArrayList<MessageItem> instantgratificationItems = new ArrayList<MessageItem>();
    public ArrayList<MessageItem> marketTrackerItems = new ArrayList<MessageItem>();
    public ArrayList<MessageItem> phoneCallsItems = new ArrayList<MessageItem>();
    public static final String CURRENT_USER = "CurrentUser";
    SharedPreferences mcurrentUserPref;
    MessageItemAdapter messageAdapter;
    MessageItemAdapter instantGratificationAdapter;
    MessageItemAdapter marketTrackerAdapter;
    MessageItemAdapter phoneCallsAdapter;
    public static Boolean mAutosync;
    public ProgressDialog mDialog;

    // Images for applications
    ImageView app1Iv;
    boolean mTimerStarted;
    BroadcastReceiver pushNotificationReceiver;
    BroadcastReceiver showDialogReceiver;
    BroadcastReceiver refreshCallsListReceiver;
    boolean registeredPushNotificatonReceiver = false;
    boolean registeredShowDialogReceiver = false;
    boolean registeredRefreshCallsListReceiver = false;
    IntentFilter showDialogFilter;
    IntentFilter pushNotificationFilter;
    IntentFilter refreshCallsListFilter;
    Intent serviceIntent;
    String provider;
    // Button messageRefresh;
    Button planAheadRefresh;
    Button instantKnowledgeButton;
    Button planAheadButton;
    Button addCorefyButton;
    Button locationsyncButton;
    Button todoSyncButton;
    Button todoButton;
    private Button preopButton;
    Button feedbackButton;
    Button additionalButton1;
    Button additionalButton2;
    public static String mHelpUrl;

    private Button addproviderButton;
    private Button addpatientButton;
    Button phoneCallsButton, corefyButton, locationButton;
    ProgressBar syncIndicator;
    ProgressBar corefyIndicator;
    ProgressBar locationIndicator;
    ProgressBar todoIndicator;

    boolean isCorefyRunning = false;
    boolean isTodoRunning;
    boolean isSyncInProgress;
    boolean isCorefyInProgress;
    boolean isLocationSyncProgress;
    boolean isTodoInProgress;
    int whichlistviewtoshow = 0;
    private boolean mIsBound;
    // private DownloadMessageService mBoundService;
    private CoreyDBHelper mCoreyDBHelper;
    private boolean mShowUseCoreyDlg = false;
    private ProgressDialog mProgressDialog;
    protected ListView fourthListView;
    protected ListView fifthListView;
    protected ListView sixthListView;
    private ListView ninthListView;
    private ListView tenListView;
    private ListView elevenListView;
    private ListView tewleveListView;
    protected ListView thirteenListView;
    public ArrayList<MessageItem> corefyitems = new ArrayList<MessageItem>();
    public ArrayList<MessageItem> locationitems = new ArrayList<MessageItem>();
    public ArrayList<MessageItem> todoitems = new ArrayList<MessageItem>();
    MyApplication application;
    MessageItemAdapter corefyAdapter;

    MessageItemAdapter locationAdapter;

    MessageItemAdapter todoAdapter;
    private Button addButton;
    private Set<String> mAdditionalContext;
    private Button trackerButton;
    ProgressDialog mPDialog;
    int indx = 0;
    List Indxlist = new ArrayList();

    //  int[] Indxlist = new int[30];
    public static MessageListActivityCMN getInstance() {
        return MsgListActivity;
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.i(TAG, "onCreate");
        MsgListActivity = this;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            //Log.i(TAG, "API LEVEL: " + android.os.Build.VERSION.SDK_INT
//                    + " >= HoneyComb");
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .permitNetwork().build());
        } else {
            //Log.i(TAG, "API LEVEL: " + android.os.Build.VERSION.SDK_INT
//                    + " < HoneyComb");
        }
        application = (MyApplication) getApplication();
        setContentView(R.layout.messagelist);
        mPDialog = new ProgressDialog(this);
        mPDialog.setMessage("Refreshing! please wait");
        mPDialog.setCancelable(false);
        mPDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
/*
    serviceIntent = new Intent(MessageListActivityCMN.this,
		DownloadMessageService.class);
	startService(serviceIntent);
	*/
        mcurrentUserPref = getSharedPreferences(CURRENT_USER, 0);
        updateState();

        mcurrentUserPref = getSharedPreferences(CURRENT_USER, 0);
        mAutosync = mcurrentUserPref.getBoolean("RequireAutoSync", false);
        mAdditionalContext = mcurrentUserPref.getStringSet("Additional",
                new HashSet<String>());
        mDisplayName = mcurrentUserPref.getString("DisplayName",
                mOrganisationName);
        mHelpUrl = mcurrentUserPref.getString("helpurl", null);
        // Utils.setAppTitle(this, "Corey");
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            //  actionBar.setTitle("CoreySales");
            actionBar.setTitle("Corey for " + mDisplayName);
            //    actionBar.setIcon(R.drawable.app_icon);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME
                    | ActionBar.DISPLAY_SHOW_TITLE
                    | ActionBar.DISPLAY_HOME_AS_UP);
            actionBar.show();

        }

        planAheadRefresh = (Button) findViewById(R.id.instant_gratification_refresh);
        instantKnowledgeButton = (Button) findViewById(R.id.instant_knowledge_button);
        planAheadButton = (Button) findViewById(R.id.plan_ahead_button);
        syncIndicator = (ProgressBar) findViewById(R.id.sync_indicator);
        phoneCallsButton = (Button) findViewById(R.id.phone_calls_button);
        corefyButton = (Button) findViewById(R.id.coreyfy_button);
        locationButton = (Button) findViewById(R.id.location_button);
        corefyIndicator = (ProgressBar) findViewById(R.id.corefy_progress);
        addCorefyButton = (Button) findViewById(R.id.add_corefy);
        trackerButton = (Button) findViewById(R.id.trackers_button);
        if (mAutosync) {
            trackerButton.setText("Status-Messages");
            corefyButton.setText("Patient Lookup");
        }
        todoSyncButton = (Button) findViewById(R.id.todo_gratification_refresh);
        todoButton = (Button) findViewById(R.id.todo_button);
        todoIndicator = (ProgressBar) findViewById(R.id.todo_sync_indicator);
        addButton = (Button) findViewById(R.id.assignments_button);
        locationsyncButton = (Button) findViewById(R.id.location_sync);
        locationIndicator = (ProgressBar) findViewById(R.id.location_progress);

//	addButton = (Button) findViewById(R.id.assignments_button);
        preopButton = (Button) findViewById(R.id.preop_manager);

        addproviderButton = (Button) findViewById(R.id.addprovider);
        addpatientButton = (Button) findViewById(R.id.addpatient);
        feedbackButton = (Button) findViewById(R.id.feedback);
        additionalButton1 = (Button) findViewById(R.id.additionalbutton1);

        mAllHeaders.add(planAheadButton);
        mAllHeaders.add(phoneCallsButton);
        mAllHeaders.add(trackerButton);
        mAllHeaders.add(locationButton);
        mAllHeaders.add(corefyButton);
        mAllHeaders.add(todoButton);
        mAllHeaders.add(addButton);

        planAheadRefresh.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!MyApplication.INSTANCE.inDemoMode()) {
                    mRefreshFlag = DatabaseProvider.MSG_TYPE_INSTANT_GRATIFICATION;
                    //Log.i(TAG,
//                            "User Signed In: "
//                                    + mcurrentUserPref.getBoolean(
//                                    "UserSignedIn", false));
                    planAheadRefresh.setBackgroundDrawable(null);
                    showDialog(INSTANT_GRATIFICATION_DIALOG);
                } else {
                    showDialog(MISSING_CREDENTIALS_DIALOG);
                }

                // performSync();
                // doBindService();
                // mBoundService.pullData();
                // showDialog();
            }
        });

        // locationManager = (LocationManager)
        // getSystemService(Context.LOCATION_SERVICE);
        // provider = locationManager.GPS_PROVIDER;

        firstListView = (ListView) findViewById(R.id.list1);
        secondListView = (ListView) findViewById(R.id.list2);
        thirdListView = (ListView) findViewById(R.id.list3);
        fourthListView = (ListView) findViewById(R.id.list6);
        fifthListView = (ListView) findViewById(R.id.list5);
        sixthListView = (ListView) findViewById(R.id.list7);
        seventhListView = (ListView) findViewById(R.id.list4);
        eighthListView = (ListView) findViewById(R.id.list8);

        ninthListView = (ListView) findViewById(R.id.list9);
        tenListView = (ListView) findViewById(R.id.list10);
        elevenListView = (ListView) findViewById(R.id.list11);
        tewleveListView = (ListView) findViewById(R.id.list12);
        thirteenListView = (ListView) findViewById(R.id.list13);
        // .add(firstListView);

        mAllListViews.add(secondListView); // schedule
        mAllListViews.add(thirdListView); // phonecall
        mAllListViews.add(seventhListView); // tracker
        mAllListViews.add(fifthListView);
        mAllListViews.add(fourthListView);
        mAllListViews.add(sixthListView);
        mAllListViews.add(eighthListView);
        // setupDemoContexts();

        for (int i = 0; i < mAllHeaders.size(); ++i) {
            final int myId = i;
            mAllHeaders.get(i).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Log.i(TAG, "Clicked header button id=" + myId);
                    whichlistviewtoshow = myId;
                    resetListViews();
                }
            });
        }

        final String[] additnial = mAdditionalContext.iterator().next()
                .split("[,:]");

        int length = additnial.length;
        indx = 0;
        Indxlist.clear();
        for (int i = 0; i < length; i++) {
            if (additnial[i].contains("http")) {
                Indxlist.add(i);
                i++;
            } else {
                Indxlist.add(i);
            }
        }

        //Log.i(TAG, "additoinal length=" + length);
        if (additnial != null && length > 0) {
            if (length >= 2) {
                addButton.setVisibility(View.VISIBLE);
                eighthListView.setVisibility(View.VISIBLE);
                mAllHeaders.add(addButton);
                mAllListViews.add(eighthListView);
                ArrayList<String> eight = new ArrayList<String>();
                eight.add(additnial[(Integer) Indxlist.get(0)].replace("\"", "").replaceAll(
                        "[\\{\\[\\]\\}]", ""));
                indx++;
                addButton.setText(eight.get(0).toString());
                final String functiontext = eight.get(0).toString();
                eighthListView.setAdapter(new ArrayAdapter<String>(
                        MessageListActivityCMN.this,
                        android.R.layout.simple_list_item_1, eight));
                eighthListView
                        .setOnItemClickListener(new AdapterView.OnItemClickListener() {

                            @Override
                            public void onItemClick(AdapterView<?> arg0,
                                                    View arg1, int arg2, long arg3) {
                                Intent urlIntent = new Intent(/*
                                        MessageListActivityCMN.this,
                                        WebViewActivityCMN.class*/);
                                urlIntent.setClassName("" + getPackageName(), "" + getPackageName() + ".WebViewActivityCMN");
                                //Log.i(TAG, "additoinal[1]=" + additnial[indx]);
                                //Log.i(TAG, "additoinal[2]=" + additnial[indx + 1]);
                                String url;
                                if (additnial[(Integer) Indxlist.get(1)].contains("http")) {
                                    url = additnial[(Integer) Indxlist.get(1)].replace("\\", "").replaceAll(
                                            "^\"|\"$", "")
                                            + ":"
                                            + additnial[(Integer) Indxlist.get(1) + 1].replace("\\", "").replaceAll("\"", "\\\\\"")
                                            .replaceAll("[\\{\\[\\]\\}]", "")
                                            .replaceAll("^\"|\"$", "")
                                            .replaceAll("\\\\(.)", "\\1");
                                    indx++;
                                    indx++;
                                } else {
                                    url = CMN_SERVER_BASE_URL_DEFINE
                                            + additnial[(Integer) Indxlist.get(1)]
                                            .replace("}",
                                                    "")
                                            .replace("\"",
                                                    "")
                                            .replaceAll(
                                                    "[\\{\\[\\]\\}]",
                                                    "")
                                            .replace(
                                                    "token==%@",
                                                    "token="
                                                            + CMN_Preferences.getUserToken(MessageListActivityCMN.this));
                                }
                                //Log.i(TAG, "additional url8" + url);
                                //Log.i(TAG, "Indx" + indx);
                                urlIntent.putExtra("myurl", url);
                                urlIntent.putExtra("functionality", functiontext);
                                startActivity(urlIntent);
                                indx++;
                            }
                        });
            }

            if (length >= 4) {
                preopButton.setVisibility(View.VISIBLE);
                ninthListView.setVisibility(View.VISIBLE);
                mAllHeaders.add(preopButton);
                mAllListViews.add(ninthListView);
                ArrayList<String> nine = new ArrayList<String>();
                nine.add(additnial[(Integer) Indxlist.get(2)].replace("\"", "").replaceAll(
                        "[\\{\\[\\]\\}]", ""));
                indx++;
                preopButton.setText(nine.get(0).toString());
                final String functiontext = nine.get(0).toString();
                ninthListView.setAdapter(new ArrayAdapter<String>(
                        MessageListActivityCMN.this,
                        android.R.layout.simple_list_item_1, nine));

                ninthListView
                        .setOnItemClickListener(new AdapterView.OnItemClickListener() {

                            @Override
                            public void onItemClick(AdapterView<?> arg0,
                                                    View arg1, int arg2, long arg3) {
                                Intent urlIntent = new Intent(/*
                                        MessageListActivityCMN.this,
                                        WebViewActivityCMN.class*/);
                                urlIntent.setClassName("" + getPackageName(), "" + getPackageName() + ".WebViewActivityCMN");
                                //Log.i(TAG, "additoinal[1]=" + additnial[(Integer) Indxlist.get(2)]);
                                //Log.i(TAG, "additoinal[2]=" + additnial[(Integer) Indxlist.get(3)]);
                                String url;
                                if (additnial[(Integer) Indxlist.get(3)].contains("http")) {
                                    url = additnial[(Integer) Indxlist.get(3)].replace("\\", "").replaceAll(
                                            "^\"|\"$", "")
                                            + ":"
                                            + additnial[(Integer) Indxlist.get(3) + 1].replace("\\", "").replaceAll("\"", "\\\\\"")
                                            .replaceAll("[\\{\\[\\]\\}]", "")
                                            .replaceAll("^\"|\"$", "")
                                            .replaceAll("\\\\(.)", "\\1");
                                    indx++;
                                    indx++;
                                } else {
                                    url = CMN_SERVER_BASE_URL_DEFINE
                                            + additnial[(Integer) Indxlist.get(3)]
                                            .replace("}",
                                                    "")
                                            .replace("\"",
                                                    "")
                                            .replaceAll(
                                                    "[\\{\\[\\]\\}]",
                                                    "")
                                            .replace(
                                                    "token==%@",
                                                    "token="
                                                            + CMN_Preferences.getUserToken(MessageListActivityCMN.this));
                                }
                                //Log.i(TAG, "additional url9" + url);
                                //Log.i(TAG, "Indx" + indx);
                                urlIntent.putExtra("myurl", url);
                                urlIntent.putExtra("functionality", functiontext);
                                startActivity(urlIntent);
                                indx++;
                            }
                        });

                nine = null;

            }

            if (length >= 6) {

                addproviderButton.setVisibility(View.VISIBLE);
                tenListView.setVisibility(View.VISIBLE);

                mAllListViews.add(tenListView);
                mAllHeaders.add(addproviderButton);
                ArrayList<String> ten = new ArrayList<String>();

                ten.add(additnial[(Integer) Indxlist.get(4)].replace("\"", "").replaceAll(
                        "[\\{\\[\\]\\}]", ""));
                indx++;
                tenListView.setAdapter(new ArrayAdapter<String>(
                        MessageListActivityCMN.this,
                        android.R.layout.simple_list_item_1, ten));

                addproviderButton.setText(ten.get(0).toString());
                final String functiontext = ten.get(0).toString();
                tenListView
                        .setOnItemClickListener(new AdapterView.OnItemClickListener() {

                            @Override
                            public void onItemClick(AdapterView<?> arg0,
                                                    View arg1, int arg2, long arg3) {
                                Intent urlIntent = new Intent(/*
                                        MessageListActivityCMN.this,
                                        WebViewActivityCMN.class*/);
                                urlIntent.setClassName("" + getPackageName(), "" + getPackageName() + ".WebViewActivityCMN");
                                //Log.i(TAG, "additoinal[1]=" + additnial[(Integer) Indxlist.get(4)]);
                                //Log.i(TAG, "additoinal[2]=" + additnial[(Integer) Indxlist.get(4) + 1]);
                                //Log.i(TAG, "additional url10 received from server=" + additnial[indx]);
                                String url;
                                if (additnial[(Integer) Indxlist.get(5)].contains("http")) {
                                    url = additnial[(Integer) Indxlist.get(5)].replace("\\", "").replaceAll(
                                            "^\"|\"$", "")
                                            + ":"
                                            + additnial[(Integer) Indxlist.get(5) + 1].replace("\\", "").replaceAll("\"", "\\\\\"")
                                            .replaceAll("[\\{\\[\\]\\}]", "")
                                            .replaceAll("^\"|\"$", "")
                                            .replaceAll("\\\\(.)", "\\1");
                                    indx++;
                                    indx++;
                                } else {
                                    url = CMN_SERVER_BASE_URL_DEFINE
                                            + additnial[(Integer) Indxlist.get(5)]
                                            .replace("}",
                                                    "")
                                            .replace("\"",
                                                    "")
                                            .replaceAll(
                                                    "[\\{\\[\\]\\}]",
                                                    "")
                                            .replace(
                                                    "token==%@",
                                                    "token="
                                                            + CMN_Preferences.getUserToken(MessageListActivityCMN.this));
                                }
                                urlIntent.putExtra("myurl", url);
                                //Log.i(TAG, "additional url10" + url);
                                //Log.i(TAG, "Indx" + indx);
                                indx++;
                                urlIntent.putExtra("functionality", functiontext);
                                startActivity(urlIntent);
                            }
                        });

            }

            if (length >= 8) {

                addpatientButton.setVisibility(View.VISIBLE);
                elevenListView.setVisibility(View.VISIBLE);
                mAllHeaders.add(addpatientButton);
                mAllListViews.add(elevenListView);
                ArrayList<String> eleven = new ArrayList<String>();
                eleven.add(additnial[(Integer) Indxlist.get(6)].replace("\"", "").replaceAll(
                        "[\\{\\[\\]\\}]", ""));
                indx++;
                addpatientButton.setText(eleven.get(0).toString());
                final String functiontext = eleven.get(0).toString();
                elevenListView.setAdapter(new ArrayAdapter<String>(
                        MessageListActivityCMN.this,
                        android.R.layout.simple_list_item_1, eleven));

                elevenListView
                        .setOnItemClickListener(new AdapterView.OnItemClickListener() {

                            @Override
                            public void onItemClick(AdapterView<?> arg0,
                                                    View arg1, int arg2, long arg3) {
                                Intent urlIntent = new Intent(/*
                                        MessageListActivityCMN.this,
                                        WebViewActivityCMN.class*/);
                                urlIntent.setClassName("" + getPackageName(), "" + getPackageName() + ".WebViewActivityCMN");
                                //Log.i(TAG, "additoinal[(Integer) Indxlist.get(7)]=" + additnial[(Integer) Indxlist.get(7)]);
                                //Log.i(TAG, "additoinal[(Integer) Indxlist.get(7)]=" + additnial[(Integer) Indxlist.get(7) + 1]);
                                String url;
                                if (additnial[(Integer) Indxlist.get(7)].contains("http")) {
                                    url = additnial[indx].replace("\\", "").replaceAll(
                                            "^\"|\"$", "")
                                            + ":"
                                            + additnial[indx + 1].replace("\\", "").replaceAll("\"", "\\\\\"")
                                            .replaceAll("[\\{\\[\\]\\}]", "")
                                            .replaceAll("^\"|\"$", "")
                                            .replaceAll("\\\\(.)", "\\1");
                                    indx++;
                                    indx++;
                                } else {
                                    url = CMN_SERVER_BASE_URL_DEFINE
                                            + additnial[(Integer) Indxlist.get(7)]
                                            .replace("}",
                                                    "")
                                            .replace("\"",
                                                    "")
                                            .replaceAll(
                                                    "[\\{\\[\\]\\}]",
                                                    "")
                                            .replace(
                                                    "token==%@",
                                                    "token="
                                                            + CMN_Preferences.getUserToken(MessageListActivityCMN.this));
                                }
                                urlIntent.putExtra("myurl", url);
                                urlIntent.putExtra("functionality", functiontext);
                                startActivity(urlIntent);
                                //Log.i(TAG, "Indx" + indx);
                                //Log.i(TAG, "additional url11" + url);
                                indx++;
                            }
                        });

            }
            if (length >= 10) {
                feedbackButton.setVisibility(View.VISIBLE);

                tewleveListView.setVisibility(View.VISIBLE);
                mAllHeaders.add(feedbackButton);
                mAllListViews.add(tewleveListView);
                ArrayList<String> tweleve = new ArrayList<String>();
                tweleve.add(additnial[(Integer) Indxlist.get(8)].replace("\"", "").replaceAll(
                        "[\\{\\[\\]\\}]", ""));
                indx++;
                feedbackButton.setText(tweleve.get(0).toString());
                final String functiontext = tweleve.get(0).toString();
                tewleveListView.setAdapter(new ArrayAdapter<String>(
                        MessageListActivityCMN.this,
                        android.R.layout.simple_list_item_1, tweleve));

                tewleveListView
                        .setOnItemClickListener(new AdapterView.OnItemClickListener() {

                            @Override
                            public void onItemClick(AdapterView<?> arg0,
                                                    View arg1, int arg2, long arg3) {
                                Intent urlIntent = new Intent(/*
                                        MessageListActivityCMN.this,
                                        WebViewActivityCMN.class*/);
                                urlIntent.setClassName("" + getPackageName(), "" + getPackageName() + ".WebViewActivityCMN");
                                //Log.i(TAG, "additoinal[8]=" + additnial[(Integer) Indxlist.get(8)]);
                                //Log.i(TAG, "additoinal[9]=" + additnial[(Integer) Indxlist.get(8) + 1]);
                                String url;
                                if (additnial[(Integer) Indxlist.get(9)].contains("http")) {
                                    url = additnial[indx].replace("\\", "").replaceAll(
                                            "^\"|\"$", "")
                                            + ":"
                                            + additnial[(Integer) Indxlist.get(9) + 1].replace("\\", "").replaceAll("\"", "\\\\\"")
                                            .replaceAll("[\\{\\[\\]\\}]", "")
                                            .replaceAll("^\"|\"$", "")
                                            .replaceAll("\\\\(.)", "\\1");
                                    indx++;
                                    indx++;
                                } else {
                                    url = CMN_SERVER_BASE_URL_DEFINE
                                            + additnial[(Integer) Indxlist.get(9)]
                                            .replace("}",
                                                    "")
                                            .replace("\"",
                                                    "")
                                            .replaceAll(
                                                    "[\\{\\[\\]\\}]",
                                                    "")
                                            .replace(
                                                    "token==%@",
                                                    "token="
                                                            + CMN_Preferences.getUserToken(MessageListActivityCMN.this));
                                }
                                urlIntent.putExtra("myurl", url);
                                urlIntent.putExtra("functionality", functiontext);
                                startActivity(urlIntent);
                                indx++;
                            }

                        });
            }
            if (length >= 12) {
                additionalButton1.setVisibility(View.VISIBLE);

                thirteenListView.setVisibility(View.VISIBLE);
                mAllHeaders.add(additionalButton1);
                mAllListViews.add(thirteenListView);
                ArrayList<String> thirteen = new ArrayList<String>();
                thirteen.add(additnial[(Integer) Indxlist.get(10)].replace("\"", "").replaceAll(
                        "[\\{\\[\\]\\}]", ""));
                indx++;
                additionalButton1.setText(thirteen.get(0).toString());
                final String functiontext = thirteen.get(0).toString();
                thirteenListView.setAdapter(new ArrayAdapter<String>(
                        MessageListActivityCMN.this,
                        android.R.layout.simple_list_item_1, thirteen));

                thirteenListView
                        .setOnItemClickListener(new AdapterView.OnItemClickListener() {

                            @Override
                            public void onItemClick(AdapterView<?> arg0,
                                                    View arg1, int arg2, long arg3) {
                                Intent urlIntent = new Intent(/*
                                        MessageListActivityCMN.this,
                                        WebViewActivityCMN.class*/);
                                urlIntent.setClassName("" + getPackageName(), "" + getPackageName() + ".WebViewActivityCMN");
                                String url;
                                if (additnial[(Integer) Indxlist.get(11)].contains("http")) {
                                    url = additnial[(Integer) Indxlist.get(11) + 1].replace("\\", "").replaceAll(
                                            "^\"|\"$", "")
                                            + ":"
                                            + additnial[(Integer) Indxlist.get(11) + 1].replace("\\", "").replaceAll("\"", "\\\\\"")
                                            .replaceAll("[\\{\\[\\]\\}]", "")
                                            .replaceAll("^\"|\"$", "")
                                            .replaceAll("\\\\(.)", "\\1");
                                    indx++;
                                    indx++;
                                } else {
                                    url = CMN_SERVER_BASE_URL_DEFINE
                                            + additnial[(Integer) Indxlist.get(11)]
                                            .replace("}",
                                                    "")
                                            .replace("\"",
                                                    "")
                                            .replaceAll(
                                                    "[\\{\\[\\]\\}]",
                                                    "")
                                            .replace(
                                                    "token==%@",
                                                    "token="
                                                            + CMN_Preferences.getUserToken(MessageListActivityCMN.this))
                                    ;
                                }
                                urlIntent
                                        .putExtra(
                                                "myurl", url);
                                indx++;
                                urlIntent.putExtra("functionality", functiontext);
                                startActivity(urlIntent);

                            }

                        });
            }
        }
     
     /*else {

         addButton.setVisibility(View.GONE);
         preopButton.setVisibility(View.GONE);
         eighthListView.setVisibility(View.GONE);
         ninthListView.setVisibility(View.GONE);
         tenListView.setVisibility(View.GONE);
         elevenListView.setVisibility(View.GONE);
         tewleveListView.setVisibility(View.GONE);
         preopButton.setVisibility(View.GONE);

         addproviderButton.setVisibility(View.GONE);
         addpatientButton.setVisibility(View.GONE);
         feedbackButton.setVisibility(View.GONE);
     } */
//     switch(length)
        //    {
        if (length == 0) {
            addButton.setVisibility(View.GONE);
            preopButton.setVisibility(View.GONE);
            addproviderButton.setVisibility(View.GONE);
            addpatientButton.setVisibility(View.GONE);
            feedbackButton.setVisibility(View.GONE);

            eighthListView.setVisibility(View.GONE);
            ninthListView.setVisibility(View.GONE);
            tenListView.setVisibility(View.GONE);
            elevenListView.setVisibility(View.GONE);
            tewleveListView.setVisibility(View.GONE);
        } else if (length <= 3) {
            preopButton.setVisibility(View.GONE);
            addproviderButton.setVisibility(View.GONE);
            addpatientButton.setVisibility(View.GONE);
            feedbackButton.setVisibility(View.GONE);

            ninthListView.setVisibility(View.GONE);
            tenListView.setVisibility(View.GONE);
            elevenListView.setVisibility(View.GONE);
            tewleveListView.setVisibility(View.GONE);
        } else if (length <= 5) {
            addproviderButton.setVisibility(View.GONE);
            addpatientButton.setVisibility(View.GONE);
            feedbackButton.setVisibility(View.GONE);

            tenListView.setVisibility(View.GONE);
            elevenListView.setVisibility(View.GONE);
            tewleveListView.setVisibility(View.GONE);
        } else if (length <= 7) {
            addpatientButton.setVisibility(View.GONE);
            feedbackButton.setVisibility(View.GONE);

            elevenListView.setVisibility(View.GONE);
            tewleveListView.setVisibility(View.GONE);
        } else if (length <= 9) {
            feedbackButton.setVisibility(View.GONE);
            tewleveListView.setVisibility(View.GONE);
        }

        // setupDemoContexts();

        for (int i = 0; i < mAllHeaders.size(); ++i) {
            final int myId = i;

            mAllHeaders.get(i).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Log.i(TAG, "Clicked header button id=" + myId);
                    whichlistviewtoshow = myId;
                    resetListViews();
                }
            });

        }

        mCoreyDBHelper = new CoreyDBHelper(this);
        loadMessageArrays();
        refreshListItems();

        secondListView
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int arg2, long arg3) {
                        Intent myIntent = new Intent(/*MessageListActivityCMN.this,
                                MessageTabActivityCMN.class*/);
                        myIntent.setClassName("" + getPackageName(), "" + getPackageName() + ".newui.MessageTabActivityCMN");
                        myIntent.setAction("com.for.view");
                        myIntent.putExtra("CallerTab", 0);
                        myIntent.putExtra("Position", arg2);
                        myIntent.putExtra("isFromList", true);

                        MessageTabActivityCMN.isReturned = false;
                        startActivity(myIntent);
                    }
                });

        thirdListView
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int arg2, long arg3) {

                        final int pos = arg2;
                        final String name = phoneCallsItems.get(arg2).name;
                        final String phonenum = phoneCallsItems.get(arg2).phonenumber;
                        final String company_name = phoneCallsItems.get(arg2).company;
            /*
             * if(mCoreyDBHelper.getMsgIdForCaller(phonenum) == -1)
			 * { ProgressDialog mDlg = null; mDlg =
			 * ProgressDialog.show(MessageListActivityCMN.this, "",
			 * "Retrieving messages from server...", true,true);
			 * Log.i(TAG, "phonecall wait call dialogue started" );
			 * Thread mythread = new Thread() {
			 * 
			 * @Override public void run() { try {
			 * 
			 * //
			 * MyApplication.INSTANCE.pullData(MessageListActivityCMN
			 * .this); sleep(60000); } catch (InterruptedException
			 * e) { e.getMessage(); } } }; mythread.start();
			 * 
			 * if (!MyApplication.INSTANCE.mPullDataCompleted.block(
			 * PULL_DATA_WAIT)) { Log.i(TAG,
			 * "pullData didn't complete in millis=" +
			 * PULL_DATA_WAIT); if (mDlg != null) { mDlg.dismiss();
			 * Toast.makeText(getApplicationContext(),
			 * "Data still being pulled from server.  Please try again after a short while"
			 * , Toast.LENGTH_LONG).show(); }
			 * 
			 * } else { Log.i(TAG,
			 * "pullData completed in phonecall"); if (mDlg != null)
			 * { mDlg.dismiss(); } } } else { Intent myIntent = new
			 * Intent(MessageListActivityCMN.this,
			 * MessageTabActivityCMN.class);
			 * myIntent.setAction("com.for.view");
			 * myIntent.putExtra("CallerTab", ID_PHONE_CALLS);
			 * myIntent.putExtra("Position", pos);
			 * myIntent.putExtra("isFromList", true);
			 * 
			 * MessageTabActivityCMN.isReturned = false;
			 * startActivity(myIntent); }
			 */
                        if (mCoreyDBHelper.getMsgIdForCaller(phonenum) == -1) {
                            new AsyncTask<Void, Void, Integer>() {
                                private ProgressDialog mDlg = null;

                                protected void onPreExecute() {
                                    mDlg = ProgressDialog
                                            .show(MessageListActivityCMN.this,
                                                    "",
                                                    "Retrieving messages from server...",
                                                    true, true);
                                    // pullData starts an async task

//				    MyApplication.INSTANCE
//					    .pullData(MessageListActivityCMN.this);
                                }

                                protected Integer doInBackground(Void... params) {
                                    if (!MyApplication.INSTANCE.mPullDataCompleted
                                            .block(PULL_DATA_WAIT)) {
                                        //Log.i(TAG,
//                                                "pullData didn't complete in millis="
//                                                        + PULL_DATA_WAIT);
                                    } else {
                                        //Log.i(TAG, "pullData completed");
                                    }
                                    // mCoreyDBHelper.deleteMessagesForCaller(phonenum,
                                    // true);
                                    if (Utils.checkInternetConnection()) {
                                        MyApplication.INSTANCE
                                                .pullDataForCaller(name,
                                                        phonenum, company_name);
                                    } else {
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                                Toast.makeText(
                                                        getApplicationContext(),
                                                        "Unable to access the Network. Caller info may be stale or unavailable.",
                                                        Toast.LENGTH_LONG)
                                                        .show();
                                            }
                                        });
                                    }
                                    return mCoreyDBHelper
                                            .getMsgIdForCaller(phonenum);
                                }

                                protected void onPostExecute(Integer msgId) {
                                    if (mDlg != null) {
                                        mDlg.dismiss();
                                    }
                                    if (msgId == -1) {
                                        if (Utils.checkInternetConnection()) {
                                            Toast.makeText(
                                                    getApplicationContext(),
                                                    "Data still being pulled from server.  Please try again after a short while",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                        // else a toast is already shown in
                                        // doInBackground, nothing more todo
                                    } else {
                                        // showDashboard(msgId,
                                        // ID_PHONE_CALLS,appID_ID_PHONE_CALLS);
                                        Intent myIntent = new Intent(/*MessageListActivityCMN.this,
                                MessageTabActivityCMN.class*/);
                                        myIntent.setClassName("" + getPackageName(), "" + getPackageName() + ".newui.MessageTabActivityCMN");
                                        myIntent.setAction("com.for.view");
                                        myIntent.putExtra("CallerTab",
                                                ID_PHONE_CALLS);
                                        myIntent.putExtra("Position", pos);
                                        myIntent.putExtra("isFromList", true);

                                        MessageTabActivityCMN.isReturned = false;
                                        startActivity(myIntent);
                                    }
                                }
                            }.execute();
                        } else {
                            Intent myIntent = new Intent(/*MessageListActivityCMN.this,
                                MessageTabActivityCMN.class*/);
                            myIntent.setClassName("" + getPackageName(), "" + getPackageName() + ".newui.MessageTabActivityCMN");
                            myIntent.setAction("com.for.view");
                            myIntent.putExtra("CallerTab", ID_PHONE_CALLS);
                            myIntent.putExtra("Position", arg2);
                            myIntent.putExtra("isFromList", true);

                            MessageTabActivityCMN.isReturned = false;
                            startActivity(myIntent);
                        }
                    }

                });

        seventhListView
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        // MessageItem msg = messageItems.get(position);
                        // mCoreyDBHelper.markMessageAsRead(msg);
                        Intent myIntent = new Intent(/*MessageListActivityCMN.this,
                                MessageTabActivityCMN.class*/);
                        myIntent.setClassName("" + getPackageName(), "" + getPackageName() + ".newui.MessageTabActivityCMN");
                        myIntent.setAction("com.for.view");
                        myIntent.putExtra("CallerTab", ID_TRACKERS);
                        myIntent.putExtra("Position", position);
                        myIntent.putExtra("isFromList", true);
                        MessageTabActivityCMN.isReturned = false;

                        startActivity(myIntent);

                    }
                });

        sixthListView
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int arg2, long arg3) {

                        Intent myIntent = new Intent(/*MessageListActivityCMN.this,
                                MessageTabActivityCMN.class*/);
                        myIntent.setClassName("" + getPackageName(), "" + getPackageName() + ".newui.MessageTabActivityCMN");
                        myIntent.setAction("com.for.view");
                        myIntent.putExtra("CallerTab", ID_TODO);
                        myIntent.putExtra("Position", arg2);
                        myIntent.putExtra("isFromList", true);
                        MessageTabActivityCMN.isReturned = false;

                        startActivity(myIntent);
                    }
                });

        firstListView
                .setOnCreateContextMenuListener(new AdapterView.OnCreateContextMenuListener() {

                    @Override
                    public void onCreateContextMenu(ContextMenu menu, View v,
                                                    ContextMenuInfo menuInfo) {
                        menu.setHeaderTitle("Edit");
                        menu.add(0, DELETE_CONTEXTMENU_FIRSTLISTVIEW, 0,
                                "Delete Item");
                    }
                });

        secondListView
                .setOnCreateContextMenuListener(new AdapterView.OnCreateContextMenuListener() {

                    @Override
                    public void onCreateContextMenu(ContextMenu menu, View v,
                                                    ContextMenuInfo menuInfo) {
                        menu.setHeaderTitle("Edit");
                        menu.add(0, DELETE_CONTEXTMENU_SECONDLISTVIEW, 0,
                                "Delete Item");
                    }
                });

        thirdListView
                .setOnCreateContextMenuListener(new AdapterView.OnCreateContextMenuListener() {

                    @Override
                    public void onCreateContextMenu(ContextMenu menu, View v,
                                                    ContextMenuInfo menuInfo) {
                        menu.setHeaderTitle("Edit");
                        menu.add(0, DELETE_CONTEXTMENU_THIRDLISTVIEW, 0,
                                "Delete Item");
                    }
                });

        fourthListView
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int arg2, long arg3) {

                        Intent myIntent = new Intent(/*MessageListActivityCMN.this,
                                MessageTabActivityCMN.class*/);
                        myIntent.setClassName("" + getPackageName(), "" + getPackageName() + ".newui.MessageTabActivityCMN");
                        myIntent.putExtra("CallerTab", 3);
                        myIntent.putExtra("Position", arg2);
                        myIntent.putExtra("isFromList", true);

                        MessageTabActivityCMN.isReturned = false;
                        startActivity(myIntent);

                    }
                });

        fifthListView
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int arg2, long arg3) {
                        Intent myIntent = new Intent(/*MessageListActivityCMN.this,
                                MessageTabActivityCMN.class*/);
                        myIntent.setClassName("" + getPackageName(), "" + getPackageName() + ".newui.MessageTabActivityCMN");
                        myIntent.setAction("com.for.view");
                        myIntent.putExtra("CallerTab", 4);
                        myIntent.putExtra("Position", arg2);
                        myIntent.putExtra("isFromList", true);

                        MessageTabActivityCMN.isReturned = false;
                        startActivity(myIntent);

                    }
                });
        showDialogReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                showDialog();

            }
        };

        // Creating intent receiver for push notification
        pushNotificationReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent arg1) {
                //Log.i(TAG, "pushNotificationReceiver Data download complete");
                if (!mcurrentUserPref.getBoolean("UserSignedIn", false)) {
                    //Log.i(TAG,
//                            "pushNotificationReceiver user not signed in returning");
                    return;
                }
                if (mDialog != null) {
                    mDialog.dismiss();
                    mDialog = null;
                }

                if (mPDialog != null && mPDialog.isShowing())
                    mPDialog.cancel();

                // doUnbindService();
                String status = arg1.getStringExtra("Status");
                //Log.i(TAG, "status: " + status);
                if ("success".equalsIgnoreCase(status)) {
                    //Log.i(TAG, "status is not error");
                    //Log.i(TAG, "onReceive updateUI");
            /*
             * if (mSigned4first=true) { mSigned4first=false; Intent
		     * nextActivity = new
		     * Intent(MessageListActivityCMN.this,MessageTabActivityCMN
		     * .class); nextActivity.putExtra("CallingActivity",
		     * getLocalClassName()); startActivity(nextActivity); }
		     */
                    loadMessageArrays();
                    updateUI();
                    if (isCorefyRunning) {
                        //Log.d(TAG, "Stopping the corfeying progress bar");
                        addCorefyButton.setVisibility(View.VISIBLE);
                        corefyIndicator.setVisibility(View.INVISIBLE);
                        if (mProgressDialog != null
                                && mProgressDialog.isShowing())
                            mProgressDialog.cancel();
                        isCorefyRunning = false;
                    }
                    refreshListItems();
                }
            }

        };

        pushNotificationFilter = new IntentFilter();
        // application = (MyApplication) getApplication();
        pushNotificationFilter.addAction(application.AppConstants.getDownloadCompleteIntent());
//		.addAction("com.coremobile.corey.downloadcomplete");
        registerReceiver(pushNotificationReceiver, pushNotificationFilter );
        registeredPushNotificatonReceiver = true;

        refreshCallsListReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Log.i(TAG, "refreshCallsListReceiver.onReceive");
                MyApplication.INSTANCE.mGotPhoneCall = true;
            }
        };
        //refreshCallsListFilter = new IntentFilter(
        //	"com.coremobile.corey.refresh_callslist");
        refreshCallsListFilter = new IntentFilter();
//	refreshCallsListFilter.addAction("com.coremobile.coreyhealth.refresh_callslist");
        // MyApplication application = (MyApplication) getApplication();
        refreshCallsListFilter.addAction(application.AppConstants.getRefreshCallsListIntent());
        registerReceiver(refreshCallsListReceiver, refreshCallsListFilter );
        registeredRefreshCallsListReceiver = true;

        showDialogFilter = new IntentFilter();
        showDialogFilter.addAction("com.coremobile.corey.showdialog");

        // Acquire a reference to the system Location Manager


        processLaunchIntent(getIntent());

    }

    private void performSync() {
        boolean syncBegun = startSync();
        if (!syncBegun) {
            Toast.makeText(
                    getApplicationContext(),
                    "Operation cannot be performed.Please check your network connectivity and try again.",
                    Toast.LENGTH_SHORT).show();
        } else {
            // showDialog();
            if (mRefreshFlag != null
                    && mRefreshFlag
                    .equals(DatabaseProvider.MSG_TYPE_INSTANT_GRATIFICATION)) {
                planAheadRefresh.setBackgroundResource(R.drawable.refresh_2);
                mRefreshFlag = null;
            }
            planAheadRefresh.setVisibility(View.INVISIBLE);
            syncIndicator.setVisibility(View.VISIBLE);
            if (mPDialog != null) {
                mPDialog.show();
                mPDialog.getWindow().addFlags(
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
            isSyncInProgress = true;
            checkIfSyncInProgress();
        }
    }

    private void resetListViews() {
        for (int i = 0; i < mAllListViews.size(); ++i) {
            mAllListViews.get(i).setVisibility(
                    (i == whichlistviewtoshow) ? View.VISIBLE : View.GONE);
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service. Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            //Log.i(TAG, "Service Bound");
            // mBoundService =
            // ((DownloadMessageService.LocalBinder)service).getService();

        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            // mBoundService = null;
        }
    };

    void doBindService() {
        // Establish a connection with the service. We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).
        bindService(new Intent(MessageListActivityCMN.this,
                        DownloadMessageService.class), mConnection,
                Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    private void loadMessageArrays() {
        messageItems.clear();
        instantgratificationItems.clear();
        marketTrackerItems.clear();
        phoneCallsItems.clear();
        corefyitems.clear();
        locationitems.clear();
        todoitems.clear();
        for (MessageItem msg : mCoreyDBHelper.getAllMessages(this)) {

            //Log.d(TAG, "In loadMessageArrays");
            if (msg.type.equals(DatabaseProvider.MSG_TYPE_MESSAGE)) {
                messageItems.add(msg);
            } else if (msg.type.equals(DatabaseProvider.MSG_TYPE_MARKET_COREFY)) {
                corefyitems.add(msg);
            } else if (msg.type.equals(DatabaseProvider.MSG_TYPE_LOCATION)) {
                locationitems.add(msg);
            } else if (msg.type
                    .equals(DatabaseProvider.MSG_TYPE_INSTANT_GRATIFICATION)) {
                instantgratificationItems.add(msg);
            } else if (msg.type
                    .equals(DatabaseProvider.MSG_TYPE_MARKET_TRACKER)) {
                marketTrackerItems.add(msg);
            } else if (msg.type.equals(DatabaseProvider.MSG_TYPE_TODO)) {
                todoitems.add(msg);
            } else {
                ; // MSG_TYPE_PHONE_CALL do nothing
            }

            for (int i = 0; i < marketTrackerItems.size(); i++) {
                //Log.d(TAG, " marketTrackerItems " + marketTrackerItems.get(i));
            }

        }

        phoneCallsItems.addAll(mCoreyDBHelper.getAllPhoneCallMessages(this));
    }

    private void refreshListItems() {
    /*
     * if(messageItems.size() > MAX_ALLOWED_MESSAGES){ deleteMessages(); }
	 */
        // Create the array adapter to bind the array to the listview
        messageAdapter = new MessageItemAdapter(this, R.layout.adapterlayout,
                messageItems);

        instantGratificationAdapter = new MessageItemAdapter(this,
                R.layout.adapterlayout, instantgratificationItems);

        marketTrackerAdapter = new MessageItemAdapter(this,
                R.layout.adapterlayout, marketTrackerItems);

        phoneCallsAdapter = new MessageItemAdapter(this,
                R.layout.adapterlayoutphone, phoneCallsItems);

        corefyAdapter = new MessageItemAdapter(this, R.layout.adapterlayout,
                corefyitems);

        locationAdapter = new MessageItemAdapter(this, R.layout.adapterlayout,
                locationitems);

        todoAdapter = new MessageItemAdapter(this, R.layout.adapterlayout,
                todoitems);

        // Bind the array adapter to the listview.

        if (messageAdapter != null) {
            firstListView.setAdapter(messageAdapter);
        }
        if (instantGratificationAdapter != null) {
            secondListView.setAdapter(instantGratificationAdapter);
            // planAheadButton.append(" "
            // +instantGratificationAdapter.getCount());
        }
        if (marketTrackerAdapter != null) {
            seventhListView.setAdapter(marketTrackerAdapter);
        }
        thirdListView.setAdapter(phoneCallsAdapter);
        // ((ViewGroup)thirdListView).setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        fifthListView.setAdapter(locationAdapter);
        fourthListView.setAdapter(corefyAdapter);
        sixthListView.setAdapter(todoAdapter);

        deleteOldMessages();

        messageAdapter.notifyDataSetChanged();
        instantGratificationAdapter.notifyDataSetChanged();
        marketTrackerAdapter.notifyDataSetChanged();
        //Log.i(TAG, "phonecalladapter being refreshed");
        phoneCallsAdapter.notifyDataSetChanged();
        corefyAdapter.notifyDataSetChanged();
        locationAdapter.notifyDataSetChanged();
        todoAdapter.notifyDataSetChanged();

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item
                .getMenuInfo();
        switch (item.getItemId()) {
            case DELETE_CONTEXTMENU_FIRSTLISTVIEW:
                MessageItem msgItem1 = (MessageItem) firstListView.getAdapter()
                        .getItem(menuInfo.position);
                msgItem1.isDeleted = true;
                // messageItems.remove(msgItem1);
                deleteMessage(messageItems, menuInfo.position);
                messageAdapter.notifyDataSetChanged();
                break;

            case DELETE_CONTEXTMENU_SECONDLISTVIEW:
                MessageItem msgItem2 = (MessageItem) secondListView.getAdapter()
                        .getItem(menuInfo.position);
                msgItem2.isDeleted = true;
                // instantgratificationItems.remove(msgItem2);
                deleteMessage(instantgratificationItems, menuInfo.position);
                instantGratificationAdapter.notifyDataSetChanged();
                break;

            case DELETE_CONTEXTMENU_THIRDLISTVIEW:
                MessageItem msgItem3 = (MessageItem) thirdListView.getAdapter()
                        .getItem(menuInfo.position);
                msgItem3.isDeleted = true;
                // instantgratificationItems.remove(msgItem2);
                deleteMessage(phoneCallsItems, menuInfo.position);
                phoneCallsAdapter.notifyDataSetChanged();
                break;
        }
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //Log.i(TAG, "onNewIntent");

        updateState();
        processLaunchIntent(intent);

        //Log.i(TAG, "onNewIntent updateUI");
        //Log.i(TAG, "onNewIntent messageItems.size(): " + messageItems.size());

        updateUI();

        refreshListItems();
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onResume() {
        super.onResume();
        indx = 0;
        if (MyApplication.shouldResetApp()) {
            signout();
            return;
        }

        if (CMN_SERVER_BASE_URL_DEFINE == null) {
            JSONHelperClass jsonHelperClass = new JSONHelperClass();
            CMN_SERVER_BASE_URL_DEFINE = jsonHelperClass
                    .getBaseURL(mcurrentUserPref.getString("Organization", ""));
        }

        if (CMN_Preferences.getUserToken(MessageListActivityCMN.this) == null || CMN_Preferences.getUserToken(MessageListActivityCMN.this).length() == 0) {
            //Log.d(TAG, "Inside free trial");
            todoButton.setClickable(false);
            todoSyncButton.setVisibility(View.GONE);

        }

        //Log.d(TAG, "CMN_SERVER_BASE_URL_DEFINE =" + CMN_SERVER_BASE_URL_DEFINE);

        SharedPreferences.Editor editor = mcurrentUserPref.edit();
        editor.putBoolean("IsMessageListShown", true);
        editor.commit();

        if (MyApplication.INSTANCE.mGotPhoneCall) {
            phoneCallsItems.clear();
            phoneCallsItems.addAll(mCoreyDBHelper
                    .getAllPhoneCallMessages(MessageListActivityCMN.this));
            //Log.i(TAG, "Phonecallitems printed" + phoneCallsItems);
            MyApplication.INSTANCE.mGotPhoneCall = false;
        }
        loadMessageArrays();
        updateUI();
        refreshListItems();

        if (!registeredShowDialogReceiver) {
            registerReceiver(showDialogReceiver, showDialogFilter );
            registeredShowDialogReceiver = true;
        }

        if (mShowUseCoreyDlg) {
            mShowUseCoreyDlg = false;
            showDialog(START_USING_COREY_DIALOG);
        }
        // locationManager.requestLocationUpdates(provider, 1000L, 500f,
        // locationListener);
    }

    /*
     * private final LocationListener locationListener = new LocationListener()
     * { public void onLocationChanged(Location location) {
     * //updateWithNewLocation(location); }
     * 
     * public void onProviderDisabled(String provider) {
     * //updateWithNewLocation(null); }
     * 
     * public void onProviderEnabled(String provider) { }
     * 
     * public void onStatusChanged(String provider, int status, Bundle extras) {
     * } };
     */

    @Override
    protected void onPause() {
        super.onPause();
        //Log.i(TAG, "onPause");
        SharedPreferences.Editor editor = mcurrentUserPref.edit();
        editor.putBoolean("IsMessageListShown", false);
        editor.commit();
        if (registeredShowDialogReceiver) {
            unregisterReceiver(showDialogReceiver);
            registeredShowDialogReceiver = false;
        }

        if (mPDialog != null && mPDialog.isShowing())
            mPDialog.cancel();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //Log.i(TAG, "onRestart");
        //startService(serviceIntent);
        mcurrentUserPref = getSharedPreferences(CURRENT_USER, 0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Log.i(TAG, "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Log.i(TAG, "onStop");
        closeDialog();
        // corefyIndicator.setVisibility(View.INVISIBLE);
        // addCorefyButton.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Log.i(TAG, "onDestroy");
        SharedPreferences.Editor editor = mcurrentUserPref.edit();
        editor.putBoolean("showDialog", false);
        editor.commit();

        if (registeredPushNotificatonReceiver) {
            unregisterReceiver(pushNotificationReceiver);
            registeredPushNotificatonReceiver = false;
        }

        if (registeredRefreshCallsListReceiver) {
            unregisterReceiver(refreshCallsListReceiver);
            registeredRefreshCallsListReceiver = false;
        }

        // stopService(new
        // Intent(MessageListActivityCMN.this,DownloadMessageService.class));
        //stopService(serviceIntent);
    }

    @Override
    public void finishedParsing(String status) {
        //Log.i(TAG, "finishedParsing");
        closeDialog();

        loadMessageArrays();
        updateUI();
        // Log.i(TAG,"refresh Complete");
    /*
     * if(mRefreshFlag.equals(DatabaseProvider.MSG_TYP_MESSAGE)){
	 * messageRefresh.setBackgroundResource(R.drawable.refresh_2); }
	 */
    /*
	 * else
	 * if(mRefreshFlag.equals(DatabaseProvider.MSG_TYPE_INSTANT_GRATIFICATION
	 * )){ planAheadRefresh.setBackgroundResource(R.drawable.refresh_2); }
	 */
        mRefreshFlag = null;
        // (re)start the timer if not in demo mode.
        // In demo mode, we come here after uploading local calendar events and
        // pullData. In that workflow, stopTimer() was not called, so no need
        // to call startTimer() as well.
        if (!MyApplication.INSTANCE.inDemoMode()) {
            startTimer();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // menu.add(0, COREFY, Menu.NONE, "COREFY");
        if (!mAutosync) {
            menu.add(0, APPLICATION_SETTINGS, Menu.NONE, "Application Settings");
            menu.add(0, GENERAL_SETTINGS, Menu.NONE, "General Settings");
            //menu.add(0, HELP, Menu.NONE, "Help");
        }

        // menu.add(0, CALENDARSYNC, Menu.NONE, "Calendar Sync");
        //menu.add(0, LOCATIONSYNC, Menu.NONE, "Location Sync");
        menu.add(0, CONFIGSYNC, Menu.NONE, "Configuration Sync");
        //menu.add(0, REFRESH, Menu.NONE, "Refresh Messages");

        menu.add(0, ABOUT, Menu.NONE, "About");
        menu.add(0, SIGNOUT, Menu.NONE, "Signout");

        // menu.add(0,2,Menu.NONE,"Refresh");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("listItems", messageItems);
        outState.putParcelableArrayList("listItemscorefy", corefyitems);
        outState.putParcelableArrayList("listItemLocation", locationitems);
        outState.putParcelableArrayList("todoitems", todoitems);
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        messageItems = state.getParcelableArrayList("listItems");
        corefyitems = state.getParcelableArrayList("listItemscorefy");
        locationitems = state.getParcelableArrayList("listItemLocation");
        todoitems = state.getParcelableArrayList("todoitems");

    }

    protected void refreshConfiguration() {
        // CMN_SERVER_BASE_URL_DEFINE = CMN_SERVER_TEST_URL_DEFINE;
        mcurrentUserPref = getSharedPreferences(CURRENT_USER, 0);


        String url = CMN_SERVER_BASE_URL_DEFINE;
        if (AppConfig.isAESEnabled) {
            url = url + "getConfiguration_s.aspx?token=" + CMN_Preferences.getUserToken(MessageListActivityCMN.this);
        } else {
            url = url + "getConfiguration.aspx?token=" + CMN_Preferences.getUserToken(MessageListActivityCMN.this);
        }
        if (Utils.checkInternetConnection()) {
            //Log.i(TAG, "Start Configuration refresh");
            try {
                new GetJSON(this, MsgListActivity).execute(url, "true");
            } catch (Exception e) {
                Toast.makeText(
                        getApplicationContext(),
                        "Could not refresh the Configuration. Please check your network connectivity and try again",
                        Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    "Could not refresh the Configuration. Please check your network connectivity and try again",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void buildUI(JSONObject jsonObject) {
        CrystalData source = new CrystalData();
        JSONParser jsonParser = new JSONParser();

        CrystalData result = jsonParser.buildUI(jsonObject, source, this);
        if (result != null) {
            MyApplication.crystalData = result;
            Toast.makeText(getApplicationContext(),
                    "Configuration refreshed successfully", Toast.LENGTH_LONG)
                    .show();
        } else {
            //Log.i(TAG, "Failed to get JSON data for the user");
        }

        // Log.i("MyApplication","crystalData: " +
        // MyApplication.crystalData.mOrganizationData.size() +
        // MyApplication.crystalData.mOrganizationData.get(0).mOrgName +
        // MyApplication.crystalData.mOrganizationData.get(1).mOrgName);
    }

    private void signout() {

        if (isCorefyRunning) {
            //Log.d(TAG, "Stopping the corfeying progress bar");
            addCorefyButton.setVisibility(View.VISIBLE);
            corefyIndicator.setVisibility(View.INVISIBLE);
            isCorefyRunning = false;
        } else if (isTodoRunning) {
            //Log.d(TAG, "Stoppin Todo  progress bar");
            todoSyncButton.setVisibility(View.VISIBLE);
            todoIndicator.setVisibility(View.INVISIBLE);
            if (mProgressDialog != null && mProgressDialog.isShowing())
                mProgressDialog.dismiss();
        }


        AlertDialog.Builder signoutBuilder = new AlertDialog.Builder(
                MessageListActivityCMN.this);
        signoutBuilder
                .setTitle("Signout")
                .setMessage("Do you want to signout ?")
                .setPositiveButton(android.R.string.yes,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // TODO Auto-generated method stub
                                //	stopService(serviceIntent);
                                SharedPreferences.Editor editor = mcurrentUserPref
                                        .edit();
                                editor.putBoolean("UserSignedIn", false);
                                editor.commit();
                                dialog.dismiss();

                                MyApplication.INSTANCE
                                        .unregisterPhoneCallsListener();
                                Intent signinActivityIntent = new Intent().setClassName(getPackageName(), ActivityPackage.SigninActivity);
                                startActivity(signinActivityIntent);
                                MessageTabActivityCMN.getInstance().finish();
                                finish();
                            }
                        }).setNegativeButton(android.R.string.no, null).show();


    }

    public void refreshMessages() {
        mRefreshFlag = DatabaseProvider.MSG_TYPE_MESSAGE;
        //Log.d(TAG, "in refreshMessages()");
        if (Utils.checkInternetConnection()) {
//	    showDialog();
//	    MyApplication application = (MyApplication) getApplication();
//	    application.pullData(this);
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    "Operation cannot be performed now.Check if your cellular data/wifi is turned ON.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    private int isSyncComplete() {
        mcurrentUserPref = getSharedPreferences(CURRENT_USER, 0);

        String url = CMN_SERVER_BASE_URL_DEFINE
                + CMN_SERVER_IS_SYNC_IN_PROGRESS_API + "?token=" + CMN_Preferences.getUserToken(MessageListActivityCMN.this);
        //Log.i(TAG, "url: " + url);
        if (Utils.checkInternetConnection()) {
            CloseableHttpClient httpclient = new CMN_HTTPSClient(MyApplication.INSTANCE, url).getClient();
            HttpPost httppost = new HttpPost(url);
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("type", "web_server"));
            try {

                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
                        nameValuePairs);
                entity.setContentEncoding(HTTP.UTF_8);
                httppost.setEntity(entity);
                httppost.setHeader("Content-Type",
                        "application/x-www-form-urlencoded");
                // Log.i(TAG,"httppost: " + httppost);
                HttpResponse response = httpclient.execute(httppost);
                //Log.i(TAG, "POST response: " + response.getStatusLine());
                JSONObject json = null;
                json = new JSONObject(EntityUtils.toString(response.getEntity()));
                if (json != null) {
                    closeDialog();
                    String jsonResult = json.getString("result");
                    // String resultText = json.getString("text");
                    //Log.i(TAG, "json response: " + jsonResult);
                    if (jsonResult.equals("0")) {
                        return 1;
                    }
                }
                // parseJSON(json);
            } catch (UnsupportedEncodingException e1) {
                e1.getMessage();
                return -1;
            } catch (IllegalStateException e) {
                e.getMessage();
                return -1;
            } catch (ClientProtocolException e) {
                e.getMessage();
                return -1;
            } catch (IOException e) {
                e.getMessage();
                return -1;
            } catch (JSONException e) {
                e.getMessage();
                return -1;
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
            return -1;
        }
        return 0;
    }

    public void checkIfSyncInProgress() {
        Thread waitThread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(3000); // Wait for a few seconds...
                    while (isSyncInProgress) {
                        //Log.i(TAG, "SyncInProgress Check...");
                        int ret = isSyncComplete();
                        //Log.i(TAG, "Return " + ret);
                        if (ret == 0) {
                            sleep(10000);
                            continue;
                        }
                        isSyncInProgress = false;
                    }
                } catch (InterruptedException e) {
                    e.getMessage();
                } finally {
                    // resetSyncInProgress();
                    //Log.i(TAG, "CheckIfSyncInProgress reseting the view");
                    runOnUiThread(new Runnable() {
                        public void run() {
                            syncIndicator.setVisibility(View.INVISIBLE);
                            planAheadRefresh.setVisibility(View.VISIBLE);
                            if (mPDialog != null && mPDialog.isShowing())
                                mPDialog.cancel();
                            // stopTimer();
                            refreshMessages();
                        }
                    });

                }
            }
        };

        waitThread.start();
    }

    @Override
    public Dialog onCreateDialog(int id) {
        switch (id) {
            case (HELP_DIALOG):
                AlertDialog.Builder helpDialog = new AlertDialog.Builder(this);
                helpDialog.setTitle(String.format(
                        getResources().getString(R.string.login_help_title),
                        getResources().getString(R.string.app_version)));
                helpDialog.setMessage(MyApplication.crystalData.mCrystalHelp);
                return helpDialog.create();
            case MISSING_CREDENTIALS_DIALOG:
                AlertDialog.Builder credentialsMissingDialog = new AlertDialog.Builder(
                        this);
                credentialsMissingDialog.setTitle(R.string.mc_dlg_title);
                credentialsMissingDialog.setMessage(R.string.mc_dlg_msg);
                credentialsMissingDialog.setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {

                            @SuppressWarnings("deprecation")
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Log.i(TAG, "User Clicked Yes");
                                dismissDialog(MISSING_CREDENTIALS_DIALOG);
                                startActivity(new Intent(MessageListActivityCMN.this,
                                        AuthenticationActivityCMN.class));
                                determineAndShowDialog(DLG_EVENT_YES);
                            }
                        });
                credentialsMissingDialog.setNegativeButton("NO",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dismissDialog(MISSING_CREDENTIALS_DIALOG);
                                determineAndShowDialog(DLG_EVENT_NO);
                            }
                        });
                credentialsMissingDialog
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dlg) {
                                determineAndShowDialog(DLG_EVENT_CANCEL);
                            }
                        });
                return credentialsMissingDialog.create();
            case INSTANT_GRATIFICATION_DIALOG:
                AlertDialog.Builder instantGratificationDialog = new AlertDialog.Builder(
                        this);
                instantGratificationDialog.setTitle("Sync with server");
                instantGratificationDialog
                        .setMessage("This action will begin Sync operation in the background."
                                + " You will be notified once the update operation has completed. Do you wish to continue?");
                instantGratificationDialog.setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Log.i(TAG, "User Clicked Yes");
                                dismissDialog(INSTANT_GRATIFICATION_DIALOG);
                                determineAndShowDialog(DLG_EVENT_YES);

                                performSync();
                            }
                        });
                instantGratificationDialog.setNegativeButton("NO",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dismissDialog(INSTANT_GRATIFICATION_DIALOG);
                                determineAndShowDialog(DLG_EVENT_NO);
                                planAheadRefresh
                                        .setBackgroundResource(R.drawable.refresh_2);
                                mRefreshFlag = null;
                            }
                        });
                instantGratificationDialog
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dlg) {
                                determineAndShowDialog(DLG_EVENT_CANCEL);
                            }
                        });
                return instantGratificationDialog.create();

            case LOCATIONSUCCESS_MESSAGE:
                AlertDialog.Builder locationDialog = new AlertDialog.Builder(this);
                locationDialog.setTitle("Location Sync started");
                locationDialog
                        .setMessage("Location Sync began on the Corey server. You will be notified after the Sync is completed.");
                return locationDialog.create();

            case LOCATIONFAILED_MESSAGE:
                AlertDialog.Builder locationFailedDialog = new AlertDialog.Builder(
                        this);
                locationFailedDialog.setTitle("Location Sync failed");
                locationFailedDialog
                        .setMessage("Failed to perform Location sync. Please check your network connectivity and try again.");
                return locationFailedDialog.create();

            case ENABLE_LOCATIONSYSTEMS_MESSAGE:
                AlertDialog.Builder locationSystemsDialog = new AlertDialog.Builder(
                        this);
                locationSystemsDialog.setTitle("Enable Location service");
                locationSystemsDialog
                        .setMessage("Please enable Location services (Wireless networks or GPS) for Location Sync to work.\n\nLocation Services can be enabled under Settings > Location & security");
                return locationSystemsDialog.create();
            case ENABLE_LOCATION_WIRELESS_SYSTEMS_MESSAGE:
                AlertDialog.Builder locationWirelessSystemsDialog = new AlertDialog.Builder(
                        this);
                locationWirelessSystemsDialog
                        .setTitle("Enable Wireless Location service");
                locationWirelessSystemsDialog
                        .setMessage("Unable to fix on GPS location. \n\nPlease enable Wireless networks based Location under \nSettings > Location & security");
                return locationWirelessSystemsDialog.create();

            case PICK_FEATURES_DIALOG:
                return makePickFeaturesDialog();

            case ENABLE_LOCATIONSYNC_DIALOG:
                return makeEnableLocationSyncDialog();

            case USE_OR_DEMO_DIALOG:
                return makeUseOrDemoDialog();


            case DEMO_PREP_FAILED_DIALOG:
                return makeDemoPrepFailedDialog();

            case START_USING_COREY_DIALOG:
                return makeStartUsingCoreyDialog();
        }
        return null;
    }

    @Override
    public void onPrepareDialog(int idx, Dialog dlg, Bundle bdl) {
        if (idx == PICK_FEATURES_DIALOG) {
            ((CheckBox) dlg.findViewById(R.id.location_checkbox))
                    .setChecked(LocalPrefs.INSTANCE.locationSync());
            ((CheckBox) dlg.findViewById(R.id.phone_checkbox))
                    .setChecked(LocalPrefs.INSTANCE.phoneCalls());
        }
    }


    private static String convertStreamToString(InputStream is) {
	/*
	 * To convert the InputStream to String we use the
	 * BufferedReader.readLine() method. We iterate until the BufferedReader
	 * return null which means there's no more data to read. Each line will
	 * appended to a StringBuilder and returned as String.
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

    public void showDialog() {
        if (isFinishing()) {
            return;
        }
        // Log.i(TAG,"show Dialog");

        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();

        if (mDialog == null) {
            // refreshData.setVisibility(View.INVISIBLE);
            mDialog = ProgressDialog.show(this, "",
                    "Retrieving messages from server...", true, true);
        }
    }

    public void closeDialog() {
        if (isFinishing()) {
            return;
        }
        if (mDialog != null) {
            // Log.i(TAG,"dismissDialog");
            // refreshData.setVisibility(View.VISIBLE);
            mDialog.dismiss();
            mDialog = null;
        }
    }

    private boolean downloadImage() {
        JSONHelperClass jsonHelperClass = new JSONHelperClass();
        ArrayList<ApplicationData> appData = jsonHelperClass
                .getAllApplicationData(mcurrentUserPref.getString(
                        "Organization", null));
        if ((appData != null) && appData.size() > 0) {
            for (int i = 0; i < appData.size(); i++) {
                ImageDownloader imageDownloader = new ImageDownloader(
                        getApplicationContext());
                imageDownloader.download(appData.get(i).mImage,
                        appData.get(i).mApplicationName + ".jpg");
            }
        } else {
            return false;
        }
        return true;
    }

    private void updateUI() {
        //Log.i(TAG, "updateUI");
        corefyAdapter.notifyDataSetChanged();
        messageAdapter.notifyDataSetChanged();
        locationAdapter.notifyDataSetChanged();
        // corefyAdapter.notifyDataSetChanged();
        instantGratificationAdapter.notifyDataSetChanged();
        marketTrackerAdapter.notifyDataSetChanged();
        phoneCallsAdapter.notifyDataSetChanged();
        //Log.i(TAG, "phonecalladapter data notified in updateui");
        todoAdapter.notifyDataSetChanged();
        deleteCorefyItems();
        deleteLocationItems();
    }

    public void deleteMessage(ArrayList<MessageItem> meetings, int id) {
        mCoreyDBHelper.deleteMessage(meetings.get(id).msgid);
        meetings.remove(id);
    }

    private void deleteCorefyItems() {
        int curSizeCF = corefyitems.size();
        //Log.d(TAG, "In deleteCorefyItems curSizeCF=" + curSizeCF);
        if (curSizeCF > 5) {
            //Log.d(TAG, "Removing  Corefy Item in deleteCorefyItems="
//                    + corefyitems.get(0).msgid);
            mCoreyDBHelper.deleteMessage(corefyitems.get(0).msgid);
            corefyitems.remove(0);
        }
    }

    private void deleteLocationItems() {
        int locationItems = locationitems.size();
        //Log.d(TAG, "In deleteLocationItems locationItems=" + locationItems);
        if (locationItems > 5) {
            //Log.d(TAG, "Removing  Location Items in deleteLocationItems="
//                    + locationitems.get(0).msgid);
            mCoreyDBHelper.deleteMessage(locationitems.get(0).msgid);
            locationitems.remove(0);
        }
    }

    private void deleteOldMessages() {

        int maxSize = Integer.parseInt(mcurrentUserPref.getString(
                "num_plan_ahead", "10"));
        int targetSizeIK; // IK -> instant knowledge
        int targetSizePC; // PC -> Phone calls
        int targetSizeCF;// Corefy

        //Log.d(TAG, "In deleteOldMessages");
        if (mcurrentUserPref.getBoolean("auto_delete_past_meeting", false)) {
            // AUTO-PURGE ON
            targetSizeIK = 1;
            targetSizePC = 1;
            targetSizeCF = 1;
        } else {
            // AUTO-PURGE OFF
            targetSizeIK = maxSize;
            targetSizePC = maxSize;
            targetSizeCF = 5; // Corefy Delete 5 messages
        }

        //Log.i(TAG, "deleteOldMessages: targetSizeIK=" + targetSizeIK
//                + ", targetSizePC=" + targetSizePC);

        int i;
        int curSizeIK = messageItems.size();
        int curSizePC = phoneCallsItems.size();
        int curSizeCF = corefyitems.size();

        //Log.d(TAG, "Size of Corefy Items=" + curSizeCF);

        for (i = targetSizeIK; i < curSizeIK; ++i) {
            mCoreyDBHelper.deleteMessage(messageItems.get(0).msgid);
            messageItems.remove(0);
        }
        if (curSizeCF > 5) {
            //Log.d(TAG, "Removing  Corefy Item=" + corefyitems.get(0).msgid);
            mCoreyDBHelper.deleteMessage(corefyitems.get(0).msgid);
            corefyitems.remove(0);
        }
        // }
        for (i = targetSizePC; i < curSizePC; ++i) {
            mCoreyDBHelper
                    .deletePhoneCallLog(phoneCallsItems.get(targetSizePC).msgid);
            phoneCallsItems.remove(targetSizePC);
        }

        HashSet<String> phoneNumbersToKeep = new HashSet<String>();
        for (MessageItem msg : phoneCallsItems) {
            phoneNumbersToKeep.add("'" + msg.phonenumber + "'");
        }
        mCoreyDBHelper.deleteMessagesForOtherCallers(phoneNumbersToKeep);
    }

    private void stopTimer() {
        //Log.i(TAG, "stopTimer");
        Intent intent = new Intent("com.coremobile.corey.timercontrol");
        intent.putExtra("EXTRA_ACTION", "STOP");
        sendBroadcast(intent);
    }

    private void startTimer() {
        //Log.i(TAG, "startTimer");
        Intent intent = new Intent("com.coremobile.corey.timercontrol");
        intent.putExtra("EXTRA_ACTION", "START");
        sendBroadcast(intent);
    }

    private boolean startSync() {
        mcurrentUserPref = getSharedPreferences(CURRENT_USER, 0);


        String url = CMN_SERVER_BASE_URL_DEFINE + CMN_SERVER_MANUAL_SYNC_API + "?token=" + CMN_Preferences.getUserToken(MessageListActivityCMN.this);
        ;
        //Log.i(TAG, "url: " + url);
        if (Utils.checkInternetConnection()) {
            CloseableHttpClient httpclient = new CMN_HTTPSClient(MyApplication.INSTANCE, url).getClient();
            HttpPost httppost = new HttpPost(url);
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("type", "web_server"));
            try {

                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
                        nameValuePairs);
                entity.setContentEncoding(HTTP.UTF_8);
                httppost.setEntity(entity);
                httppost.setHeader("Content-Type",
                        "application/x-www-form-urlencoded");
                //Log.i(TAG, "httppost: " + httppost);
                HttpResponse response = httpclient.execute(httppost);
                //Log.i(TAG, "POST response: " + response.getStatusLine());

                JSONObject json = null;
                json = new JSONObject(EntityUtils.toString(response.getEntity()));
                if (json != null) {
                    closeDialog();
                    String jsonResult = json.getString("result");
                    // String resultText = json.getString("text");
                    //Log.i(TAG, "json response: " + jsonResult);
                    if (jsonResult.equals("1")) {
                        stopTimer();
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


    private Dialog makePickFeaturesDialog() {
        AlertDialog.Builder dlg = new AlertDialog.Builder(
                new ContextThemeWrapper(this, R.style.AppDialog));
        dlg.setTitle(R.string.pf_dlg_title);

        final View body = View.inflate(new ContextThemeWrapper(this,
                R.style.AppDialog), R.layout.pick_features_dlg_body, null);
        dlg.setView(body);

        dlg.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                boolean phoneCalls = ((CheckBox) body
                        .findViewById(R.id.phone_checkbox)).isChecked();
                boolean locationSync = ((CheckBox) body
                        .findViewById(R.id.location_checkbox)).isChecked();
                dismissDialog(PICK_FEATURES_DIALOG);
                updateUserConfig(phoneCalls, locationSync);
                determineAndShowDialog(DLG_EVENT_YES);
            }
        });
        dlg.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismissDialog(PICK_FEATURES_DIALOG);
                determineAndShowDialog(DLG_EVENT_NO);
            }
        });
        dlg.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dlg) {
                determineAndShowDialog(DLG_EVENT_CANCEL);
            }
        });

        return dlg.create();
    }

    private Dialog makeEnableLocationSyncDialog() {
        AlertDialog.Builder dlg = new AlertDialog.Builder(this);
        dlg.setTitle(R.string.ls_dlg_title);
        dlg.setMessage(R.string.ls_dlg_msg);
        dlg.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismissDialog(ENABLE_LOCATIONSYNC_DIALOG);
                LocalPrefs.INSTANCE.setLocationSync(true);
                LocalPrefs.INSTANCE.setHasPickedFeatures(true);
            }
        });
        dlg.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismissDialog(ENABLE_LOCATIONSYNC_DIALOG);
            }
        });
        return dlg.create();
    }

    private Dialog makeUseOrDemoDialog() {
        AlertDialog.Builder dlg = new AlertDialog.Builder(this);
        dlg.setMessage(R.string.ud_dlg_msg);
        dlg.setCancelable(false);
        dlg.setPositiveButton(R.string.ud_dlg_yes,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismissDialog(USE_OR_DEMO_DIALOG);
                        determineAndShowDialog(DLG_EVENT_YES);
                    }
                });
        dlg.setNegativeButton(R.string.ud_dlg_no,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismissDialog(USE_OR_DEMO_DIALOG);
                        determineAndShowDialog(DLG_EVENT_NO);
                    }
                });
        dlg.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dlg) {
                determineAndShowDialog(DLG_EVENT_CANCEL);
            }
        });
        return dlg.create();
    }

    private Dialog makeStartUsingCoreyDialog() {
        AlertDialog.Builder dlg = new AlertDialog.Builder(this);
        dlg.setMessage(R.string.uc_dlg_msg);
        dlg.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismissDialog(START_USING_COREY_DIALOG);
                showDialog(MISSING_CREDENTIALS_DIALOG);
            }
        });
        dlg.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismissDialog(START_USING_COREY_DIALOG);
            }
        });
        return dlg.create();
    }

    private Dialog makeDemoPrepFailedDialog() {
        AlertDialog.Builder dlg = new AlertDialog.Builder(this);
        dlg.setTitle(R.string.df_dlg_title).setMessage(R.string.df_dlg_msg)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismissDialog(DEMO_PREP_FAILED_DIALOG);
                    }
                });
        return dlg.create();
    }

    private void updateUserConfig(boolean phoneCalls, boolean locationSync) {
        System.out.println("updateUserConfig " + phoneCalls + " "
                + locationSync);
        LocalPrefs.INSTANCE.setPhoneCalls(phoneCalls);
        LocalPrefs.INSTANCE.setLocationSync(locationSync);
        LocalPrefs.INSTANCE.setHasPickedFeatures(true);

        if (phoneCalls) {
            MyApplication.INSTANCE.registerPhoneCallsListener();
        } else {
            MyApplication.INSTANCE.unregisterPhoneCallsListener();
        }
    }

    static int DLG_EVENT_START = 0;
    static int DLG_EVENT_YES = 1;
    static int DLG_EVENT_NO = 2;
    static int DLG_EVENT_CANCEL = 3;

    private int mCurDlg = NO_DIALOG;

    boolean determineAndShowDialog(int event) {
        int nextDlg = NO_DIALOG;

        if (mCurDlg == NO_DIALOG) {
            if (event == DLG_EVENT_START) {
                if (!MyApplication.INSTANCE.inDemoMode()) {
                    if (LocalPrefs.INSTANCE.hasPickedFeatures()) {
                        if (MyApplication.INSTANCE.mAuthWFCount > 0) {
                            MyApplication.INSTANCE.mAuthWFCount = 0;
                            nextDlg = INSTANT_GRATIFICATION_DIALOG;
                        } else {
                            nextDlg = NO_DIALOG;
                        }
                    } else {
                        nextDlg = PICK_FEATURES_DIALOG;
                    }
                } else {

                    nextDlg = MISSING_CREDENTIALS_DIALOG;

                }
            }
        } else if (mCurDlg == PICK_FEATURES_DIALOG) {
            if (event == DLG_EVENT_YES || event == DLG_EVENT_NO
                    || event == DLG_EVENT_CANCEL) {
                if (!MyApplication.INSTANCE.inDemoMode()) {
                    if (MyApplication.INSTANCE.mAuthWFCount > 0) {
                        MyApplication.INSTANCE.mAuthWFCount = 0;
                        nextDlg = INSTANT_GRATIFICATION_DIALOG;
                    } else {
                        nextDlg = NO_DIALOG;
                    }
                } else {
                    nextDlg = MISSING_CREDENTIALS_DIALOG;
                }
            }
        } else if (mCurDlg == MISSING_CREDENTIALS_DIALOG) {
            if (event == DLG_EVENT_YES || event == DLG_EVENT_NO
                    || event == DLG_EVENT_CANCEL) {
                nextDlg = NO_DIALOG;
            }
        } else if (mCurDlg == USE_OR_DEMO_DIALOG) {
            if (event == DLG_EVENT_NO) { // USE COREY
                nextDlg = MISSING_CREDENTIALS_DIALOG;
            } else if (event == DLG_EVENT_YES) { // TRY DEMO - LOCAL CALENDAR
                nextDlg = PICK_CLDRS_DIALOG;
            }
        } else if (mCurDlg == PICK_CLDRS_DIALOG) {
            if (event == DLG_EVENT_YES || event == DLG_EVENT_NO
                    || event == DLG_EVENT_CANCEL) {
                nextDlg = NO_DIALOG;
            }
        } else if (mCurDlg == INSTANT_GRATIFICATION_DIALOG) {
            nextDlg = NO_DIALOG;
        }

        //Log.i(TAG, "determineAndShowDialog curDlg=" + mCurDlg + ", event="
//                + event + ", nextDlg=" + nextDlg);
        if (nextDlg != NO_DIALOG) {
            if (nextDlg == INSTANT_GRATIFICATION_DIALOG) {
                mRefreshFlag = DatabaseProvider.MSG_TYPE_INSTANT_GRATIFICATION;
                planAheadRefresh.setBackgroundDrawable(null);
            }
            showDialog(nextDlg);
        }
        mCurDlg = nextDlg;
        return (mCurDlg != NO_DIALOG);
    }

    void showDemoPrepFailedDialog() {
        showDialog(DEMO_PREP_FAILED_DIALOG);
    }

    private void processLaunchIntent(Intent intent) {
        whichlistviewtoshow = 0; // plan ahead list is the landing place
        resetListViews();

        String callingActivity = intent.getStringExtra("CallingActivity");
        if (callingActivity == null) {
            return;
        }

        //Log.i(TAG, "parent activity: " + callingActivity + ", AuthWFCount = "
//                + MyApplication.INSTANCE.mAuthWFCount);
        if (callingActivity.equals("SigninActivityCMN")) {
            if (mcurrentUserPref.getBoolean("NewUser", true)) {
                messageItems.clear();
                instantgratificationItems.clear();
                marketTrackerItems.clear();
                phoneCallsItems.clear();
                corefyitems.clear();

            }
        }
        if (!intent.getBooleanExtra("BackKey", false)
                || MyApplication.INSTANCE.mAuthWFCount > 0) {
            determineAndShowDialog(DLG_EVENT_START);
        }
    }

    private void updateState() {
        mRefreshFlag = null;


        JSONHelperClass jsonHelperClass = new JSONHelperClass();
        CMN_SERVER_BASE_URL_DEFINE = jsonHelperClass
                .getBaseURL(mcurrentUserPref.getString("Organization", ""));
        //Log.i(TAG,
//                "downloadImage: "
//                        + mcurrentUserPref.getBoolean("DownloadImage", true));
        if (mcurrentUserPref.getBoolean("DownloadImage", true)) {
            if (downloadImage()) {
                final SharedPreferences.Editor editor = mcurrentUserPref.edit();
                editor.putBoolean("DownloadImage", false);
                editor.commit();
            } else {
                MyApplication.resetApp(true);
                signout();
                return;
            }
        }
    }

    private void showDashboard(int msgid, int callerTab, int appid) {
    }

    static final int ID_INSTANT_KNOWLEDGE = 11;
    static final int ID_PLAN_AHEAD = 0;
    static final int ID_PHONE_CALLS = 1;
    static final int ID_COREYFY = 3;
    public static final int ID_TRACKERS = 2;
    static final int ID_LOCATION = 4;
    static final int ID_TODO = 5;

    // Start Tasks Feature

    public void todo(View v) {
        //Log.d(TAG, "Enter Todo On Click");
        if (!MyApplication.INSTANCE.inDemoMode()) {
            mRefreshFlag = DatabaseProvider.MSG_TYPE_INSTANT_GRATIFICATION;
            //Log.i(TAG,
//                    "User Signed In: "
//                            + mcurrentUserPref
//                            .getBoolean("UserSignedIn", false));
        } else {
            showDialog(MISSING_CREDENTIALS_DIALOG);
            return;
        }

        mRefreshFlag = DatabaseProvider.MSG_TYPE_TODO;
        if (!mAutosync) {
            new triggerTodoAsyncTask().execute();
        } else {
            Intent urlIntent = new Intent(MessageListActivityCMN.this, WebViewActivityCMN.class);
            String url = mHelpUrl;
            urlIntent.putExtra("myurl", url);
            startActivity(urlIntent);
        }

    }

    class triggerTodoAsyncTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isTodoRunning = true;
            //Log.d(TAG, "Starting Todo  progress bar");
            todoSyncButton.setVisibility(View.INVISIBLE);
            todoIndicator.setVisibility(View.VISIBLE);
            mProgressDialog = new ProgressDialog(MessageListActivityCMN.this);
            mProgressDialog.setTitle("Todo");
            mProgressDialog.setMessage("Please wait");
            mProgressDialog.show();

        }

        @Override
        protected Integer doInBackground(Void... params) {
            mcurrentUserPref = getSharedPreferences(CURRENT_USER, 0);

            String url = CMN_SERVER_BASE_URL_DEFINE + "TriggerToDo.aspx?token=" + CMN_Preferences.getUserToken(MessageListActivityCMN.this);
            // String url = CMN_SERVER_BASE_URL_DEFINE +
            // CMN_SERVER_MANUAL_SYNC_API;
            //Log.i(TAG, "url: " + url);
            if (Utils.checkInternetConnection()) {
                CloseableHttpClient httpclient = new CMN_HTTPSClient(MyApplication.INSTANCE, url).getClient();
                HttpPost httppost = new HttpPost(url);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs
                        .add(new BasicNameValuePair("type", "web_server"));
                try {
                    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
                            nameValuePairs);
                    entity.setContentEncoding(HTTP.UTF_8);
                    httppost.setEntity(entity);
                    httppost.setHeader("Content-Type",
                            "application/x-www-form-urlencoded");

                    HttpResponse response = httpclient.execute(httppost);
                    //Log.i(TAG,
//                            "TODO POST response: " + response.getStatusLine());
                    JSONObject json = null;
                    json = new JSONObject(EntityUtils.toString(response.getEntity()));
                    if (json != null) {
                        closeDialog();
                        String jsonResult = json.getString("result");
                        //Log.i(TAG, "TODO json response: " + jsonResult);
                        if (jsonResult.equals("1")) {
                            stopTimer();
                        }
                    }

                } catch (UnsupportedEncodingException e1) {
                    e1.getMessage();
                    return -1;
                } catch (IllegalStateException e) {
                    e.getMessage();
                    return -1;
                } catch (ClientProtocolException e) {
                    e.getMessage();
                    return -1;
                } catch (IOException e) {
                    e.getMessage();
                    return -1;
                } catch (JSONException e) {
                    e.getMessage();
                    return -1;
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
                return -1;
            }
            return 1;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (result == 1) {
                isTodoRunning = true;
                checkTodoSyncInProgress();
            }
        }

    }

    private void checkTodoSyncInProgress() {

        Thread waitThread = new Thread() {
            @Override
            public void run() {
                try {
                    //Log.i(TAG, "checkTodoSyncInProgress =" + isTodoRunning);
                    sleep(3000); // Wait for a few seconds...
                    while (isTodoRunning) {
                        int ret = isCoreyfySyncComplete(); // Common API to
                        // check Sync
                        // Progress
                        //Log.i(TAG, "checkTodoSyncInProgress Return " + ret);
                        if (ret == 0) {
                            sleep(10000);
                            continue;
                        }
                        isTodoRunning = false;
                    }
                } catch (InterruptedException e) {
                    e.getMessage();
                } finally {

                    //Log.i(TAG,
//                            "checkTodoSyncInProgress reseting the view  $$$$$$$$$$$$$$$$");
                    runOnUiThread(new Runnable() {
                        public void run() {
                            // stopTimer();
                            //Log.d(TAG,
//                                    "checkTodoSyncInProgress Disbaling Tody  Progress Bar");
                            todoSyncButton.setVisibility(View.VISIBLE);
                            todoIndicator.setVisibility(View.INVISIBLE);
                            if (mProgressDialog != null
                                    && mProgressDialog.isShowing())
                                mProgressDialog.dismiss();
                            isTodoRunning = false;
                            refreshMessages();
                            deleteCorefyItems();
                        }
                    });

                }
            }
        };

        waitThread.start();

    }

    // End Tasks Feature
    // location
    public void MyLocationsync(View v) {
        if (!mAutosync) {
            if (!MyApplication.INSTANCE.inDemoMode()) {
            } else {
                showDialog(MISSING_CREDENTIALS_DIALOG);
            }
        } else {
            Intent urlIntent = new Intent(MessageListActivityCMN.this, WebViewActivityCMN.class);
            String url = mHelpUrl;
            urlIntent.putExtra("myurl", url);
            startActivity(urlIntent);
        }
    }

    // Start Coreyfy Feature

    public void corefy(View v) {

        View view = LayoutInflater.from(getApplicationContext()).inflate(
                R.layout.prompt, null);

        final EditText titleBox = (EditText) view
                .findViewById(R.id.contextedittext);
        titleBox.setHint("Context");
        titleBox.setLines(1);
        if (!MyApplication.INSTANCE.inDemoMode()) {
            mRefreshFlag = DatabaseProvider.MSG_TYPE_INSTANT_GRATIFICATION;
            //Log.i(TAG,
//                    "User Signed In: "
//                            + mcurrentUserPref
//                            .getBoolean("UserSignedIn", false));
        } else {
            showDialog(MISSING_CREDENTIALS_DIALOG);
            return;
        }
/*	if(!mAutosync)
	{ */
        final AlertDialog.Builder inputBuilder = new AlertDialog.Builder(
                MessageListActivityCMN.this);

        inputBuilder
                .setTitle("Input Text")
                .setView(view)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int arg1) {

                                dialog.dismiss();
                                mRefreshFlag = DatabaseProvider.MSG_TYPE_MARKET_COREFY;
                                if (titleBox.getText().toString().trim()
                                        .length() > 0) {

                                    if (Utils.checkInternetConnection()) {

                                        doInasync(titleBox.getText().toString()
                                                .trim());

                                    } else {
                                        Toast.makeText(
                                                getApplicationContext(),
                                                "Unable to access the Network. Caller info may be stale or unavailable.",
                                                Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(
                                            getApplicationContext(),
                                            "Error!  Context length cannot be empty",
                                            Toast.LENGTH_LONG).show();
                                }
                            }

                        });
        inputBuilder.setNegativeButton(android.R.string.cancel,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = inputBuilder.create();

        dialog.getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
        dialog.setOnShowListener(new OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                titleBox.requestFocus();
            }
        });
/*	}
	else
	{
		Intent urlIntent = new Intent(MessageListActivityCMN.this,WebViewActivityCMN.class);
        String url=mHelpUrl;
		urlIntent.putExtra("myurl",url);
		startActivity(urlIntent);	
	} */
    }

    public void doInasync(String trim) {

        JSONArray jsonArray = new JSONArray();
        jsonArray.put(trim);

        new triggerCoreyfyAsyncTask().execute(jsonArray.toString());

    }

    class triggerCoreyfyAsyncTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isCorefyRunning = true;
            //Log.d(TAG, "Starting Corefying progress bar");
            addCorefyButton.setVisibility(View.INVISIBLE);
            corefyIndicator.setVisibility(View.VISIBLE);
            mProgressDialog.setMessage("Corefying! Please wait");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected Integer doInBackground(String... params) {
            mcurrentUserPref = getSharedPreferences(CURRENT_USER, 0);

            String url = CMN_SERVER_BASE_URL_DEFINE + "TriggerCorefy.aspx?token=" + CMN_Preferences.getUserToken(MessageListActivityCMN.this);
            //Log.i(TAG, "url: " + url);
            if (Utils.checkInternetConnection()) {
                CloseableHttpClient httpclient = new CMN_HTTPSClient(MyApplication.INSTANCE, url).getClient();
                HttpPost httppost = new HttpPost(url);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs
                        .add(new BasicNameValuePair("type", "web_server"));
                try {

                    nameValuePairs.add(new BasicNameValuePair("Context",
                            params[0]));
                    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
                            nameValuePairs);
                    entity.setContentEncoding(HTTP.UTF_8);
                    httppost.setEntity(entity);
                    httppost.setHeader("Content-Type",
                            "application/x-www-form-urlencoded");

                    HttpResponse response = httpclient.execute(httppost);
                    //Log.i(TAG,
//                            "corefySync POST response: "
//                                    + response.getStatusLine());
                    JSONObject json = null;
                    json = new JSONObject(EntityUtils.toString(response.getEntity()));
                    if (json != null) {
                        closeDialog();
                        String jsonResult = json.getString("result");
                        //Log.i(TAG, "corefySync json response: " + jsonResult);
                        if (jsonResult.equals("1")) {
                            stopTimer();
                        }
                    }

                } catch (UnsupportedEncodingException e1) {
                    e1.getMessage();
                    return -1;
                } catch (IllegalStateException e) {
                    e.getMessage();
                    return -1;
                } catch (ClientProtocolException e) {
                    e.getMessage();
                    return -1;
                } catch (IOException e) {
                    e.getMessage();
                    return -1;
                } catch (JSONException e) {
                    e.getMessage();
                    return -1;
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
                return -1;
            }
            return 1;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (result == 1) {
                isCorefyInProgress = true;
                checkCorefySyncInProgress();
            }
        }

    }

    private void checkCorefySyncInProgress() {

        Thread waitThread = new Thread() {
            @Override
            public void run() {
                try {
                    //Log.i(TAG, "isCorefyInProgress =" + isCorefyInProgress);
                    sleep(3000); // Wait for a few seconds...
                    while (isCorefyInProgress) {
                        int ret = isCoreyfySyncComplete();
                        //Log.i(TAG, "checkCorefySyncInProgress Return " + ret);
                        if (ret == 0) {
                            sleep(10000);
                            continue;
                        }
                        isCorefyInProgress = false;
                    }
                } catch (InterruptedException e) {
                    e.getMessage();
                } finally {

                    //Log.i(TAG,
//                            "checkCorefySyncInProgress reseting the view  $$$$$$$$$$$$$$$$");
                    runOnUiThread(new Runnable() {
                        public void run() {
                            // stopTimer();
                            //Log.d(TAG,
//                                    "checkCorefySyncInProgress Disbaling Coreyfy Progress Bar");
                            addCorefyButton.setVisibility(View.VISIBLE);
                            corefyIndicator.setVisibility(View.INVISIBLE);
                            isCorefyRunning = false;
                            if (mProgressDialog != null
                                    && mProgressDialog.isShowing())
                                mProgressDialog.dismiss();
                            refreshMessages();
                            deleteCorefyItems();
                        }
                    });

                }
            }
        };

        waitThread.start();

    }

    private int isCoreyfySyncComplete() {
        mcurrentUserPref = getSharedPreferences(CURRENT_USER, 0);

        String url = CMN_SERVER_BASE_URL_DEFINE + "GetTriggerInProgress.aspx?token=" + CMN_Preferences.getUserToken(MessageListActivityCMN.this);
        //Log.i(TAG, "url: " + url);
        if (Utils.checkInternetConnection()) {
            CloseableHttpClient httpclient = new CMN_HTTPSClient(MyApplication.INSTANCE, url).getClient();
            HttpPost httppost = new HttpPost(url);
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("type", "web_server"));
            try {

                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
                        nameValuePairs);
                entity.setContentEncoding(HTTP.UTF_8);
                httppost.setEntity(entity);
                httppost.setHeader("Content-Type",
                        "application/x-www-form-urlencoded");
                HttpResponse response = httpclient.execute(httppost);
                //Log.i(TAG,
//                        "isCoreyfySyncComplete POST response: "
//                                + response.getStatusLine());
                JSONObject json = null;
                json = new JSONObject(EntityUtils.toString(response.getEntity()));
                if (json != null) {
                    closeDialog();
                    String jsonResult = json.getString("result");
                    //Log.i(TAG, "isCoreyfySyncComplete json response: "
//                            + jsonResult);
                    //Log.i(TAG, "isCoreyfySyncComplete json response length: "
//                            + jsonResult.length());
                    //Log.i(TAG, "isCoreyfySyncComplete json response empty: "
//                            + jsonResult.isEmpty());
                    if (jsonResult.length() == 4) {
                        return 1;
                    }
                }

            } catch (UnsupportedEncodingException e1) {
                e1.getMessage();
                return -1;
            } catch (IllegalStateException e) {

                e.getMessage();
                return -1;
            } catch (ClientProtocolException e) {

                e.getMessage();
                return -1;
            } catch (IOException e) {

                e.getMessage();
                return -1;
            } catch (JSONException e) {

                e.getMessage();
                return -1;
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
            return -1;
        }
        return 0;

    }

    // End Coreyfy Feature

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                break;

            case SIGNOUT:
                signout();
                break;

            case APPLICATION_SETTINGS:
                startActivity(new Intent(MessageListActivityCMN.this,
                        AuthenticationActivityCMN.class));
                break;

            case GENERAL_SETTINGS:
                startActivity(new Intent(MessageListActivityCMN.this,
                        GeneralSettingsActivityCMN.class));
                break;

            case REFRESH:
                refreshMessages();
                break;
            case LOCATIONSYNC:
                if (!MyApplication.INSTANCE.inDemoMode()) {
                } else {
                    showDialog(MISSING_CREDENTIALS_DIALOG);
                }
                break;
            case CALENDARSYNC:
                if (!MyApplication.INSTANCE.inDemoMode()) {
                    if (isSyncInProgress) {
                        Toast.makeText(getApplicationContext(),
                                "A calendar sync is already in progress.",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        mRefreshFlag = DatabaseProvider.MSG_TYPE_INSTANT_GRATIFICATION;
                        //Log.i(TAG,
//                                "User Signed In: "
//                                        + mcurrentUserPref.getBoolean(
//                                        "UserSignedIn", false));
                        planAheadRefresh.setBackgroundDrawable(null);
                        showDialog(INSTANT_GRATIFICATION_DIALOG);
                    }
                } else {
                    showDialog(MISSING_CREDENTIALS_DIALOG);
                }
                break;
            case CONFIGSYNC:
                refreshConfiguration();
                break;
            case HELP:
                showDialog(HELP_DIALOG);
                break;
            case ABOUT:
                startActivity(new Intent(this, AboutActivityCMN.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
