package com.coremobile.coreyhealth.newui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.coremobile.coreyhealth.AppConfig;
import com.coremobile.coreyhealth.ApplicationData;
import com.coremobile.coreyhealth.Checkfornotification.PendingNotificationService;
import com.coremobile.coreyhealth.CoreyDBHelper;
import com.coremobile.coreyhealth.DashboardListAdapter;
import com.coremobile.coreyhealth.DatabaseProvider;
import com.coremobile.coreyhealth.DetailedApplicationData;
import com.coremobile.coreyhealth.GridImageActivityCMN;
import com.coremobile.coreyhealth.IDashboardListItem;
import com.coremobile.coreyhealth.JSONHelperClass;
import com.coremobile.coreyhealth.ListItem;
import com.coremobile.coreyhealth.ListItemObj;
import com.coremobile.coreyhealth.ListValueAdapter;
import com.coremobile.coreyhealth.MessageItem;
import com.coremobile.coreyhealth.MsgDataCache;
import com.coremobile.coreyhealth.MultiSelectActivityCMN;
import com.coremobile.coreyhealth.MyApplication;
import com.coremobile.coreyhealth.PatientAnalyticsActivityCMN;
import com.coremobile.coreyhealth.R;
import com.coremobile.coreyhealth.RatingSelectActivityCMN;
import com.coremobile.coreyhealth.RetrieveCallerInfoTask;
import com.coremobile.coreyhealth.Utils;
import com.coremobile.coreyhealth.WebViewActivityCMN;
import com.coremobile.coreyhealth.aesencryption.CMN_AES;
import com.coremobile.coreyhealth.analytics.CMN_GetGraphDataWebService;
import com.coremobile.coreyhealth.clientcertificate.CMN_HTTPSClient;
import com.coremobile.coreyhealth.errorhandling.CMN_ErrorMessages;
import com.coremobile.coreyhealth.googleAnalytics.CMN_Preferences;
import com.coremobile.coreyhealth.journal.CMN_JournalActivity;
import com.coremobile.coreyhealth.messaging.CMN_NeworViewMessagesActivity;
import com.coremobile.coreyhealth.partialsync.GetAllContextWebService;
import com.coremobile.coreyhealth.patientreminders.PatientRemindersActivityCMN;
import com.coremobile.coreyhealth.patientreminders.RemindersTypeActivityCMN;
import com.coremobile.coreyhealth.progressdb.ProgressInfoDBControl;
import com.coremobile.coreyhealth.progressdb.ProgressObjectModel;
import com.coremobile.coreyhealth.providerreminders.AddNewReminderActivityCMN;
import com.coremobile.coreyhealth.providerreminders.ProviderRemindersActivityCMN;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static com.coremobile.coreyhealth.newui.MessageTabActivityCMN.activity;


@SuppressLint("ValidFragment")
public class MsgScheduleTabActivity extends Fragment {


    ArrayList<MessageItem> mMsgScheduleItems = new ArrayList<MessageItem>();
    JSONHelperClass mJSONHelperClass;
    ArrayList<ApplicationData> mAllAppsData;
    MsgDataCache mMsgDataCache;
    static MsgDataCache mMsgDataCache1;
    private String TAG = "Corey_MessageSchduelTabActivity";
    HashMap<String, ListItemObj> ListItemObjMap = new HashMap<String, ListItemObj>();
    public static int mMessageId;
    boolean isActive = true;
    Boolean mAutosync;
    Boolean mCoreyOR = false;
    Button mOrLocBtn;
    Button mSafetyBtn;
    Button mStatusBtn;
/*    private static final int ORLOC_OBJID = 275;
    private static final int SAFETY_OBJID = 276;
    private static final int STATUS_OBJID = 277; */

    String ORLOC_OBJID = "275";
    String SAFETY_OBJID = "276";
    String STATUS_OBJID = "277";

    String OrLocationVal;
    String OrSafetyVal;
    String PrStatusVal;
    WebView mYWeb;
    //    ArrayList<Float> mImageParms = new ArrayList<Float>();
    float[] mImageParms = new float[5];
    DashboardListAdapter mRowTwoDashboardListAdapter;
    DashboardListAdapter mRowThreeDashboardListAdapter;
    DashboardListAdapter mRowFourDashboardListAdapter;

    ArrayList<RowDisplayObject> mRowTwoObjectData;
    ArrayList<RowDisplayObject> mRowThreeObjectData;
    ArrayList<RowDisplayObject> mRowFourObjectData;

    private CoreyDBHelper mCoreyDBHelper;
    public AddSlideFragment mSlideFragment;

    private IntentFilter mUpdateNotificationFilter;
    public SharedPreferences mcurrentUserPref;
    public static final String CURRENT_USER = "CurrentUser";
    private String organizationName;
    String password;
    String userName;

    HorizontalListView mVisitListview;
    HorizontalListView mPhListview;
    HorizontalListView mOrderandChargesListview;

    String[] mObjectNames;
    String[] mAppNames;

    LinearLayout mPatientContainerLayout;
    String mMsgType;
    public static ProgressDialog progressDialogGetDataAsync;

    // number of images to select
    private static final int PICK_IMAGE = 1;

    public static ProgressDialog loadingDataFromServer;

    ViewGroup root;
    public static ProgressDialog firstlaunchdialog;

    @SuppressLint("UseSparseArrays")
    HashMap<Integer, ArrayList<IDashboardListItem>> mHashMap = new HashMap<Integer, ArrayList<IDashboardListItem>>();
    public static HashMap<String, List<ProgressObjectModel>> progressData = new HashMap<>();

    int positionChild;
    ProgressInfoDBControl progressInfoDBControl;

    private LayoutInflater inflater = (LayoutInflater) MessageTabActivityCMN.activity
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


    public MsgScheduleTabActivity(String msgType, Boolean autosync) {
        // TODO Auto-generated constructor stub
        mJSONHelperClass = new JSONHelperClass();
        mMsgType = msgType;
        mAutosync = autosync;
        if (AppConfig.isAppCoreyOr) mCoreyOR = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        if (getArguments() != null)
            positionChild = MessageTabActivityCMN.patientPosition;

        if (!mCoreyOR) {
            root = (ViewGroup) inflater.inflate(
                    R.layout.msg_data_container, null);
            setHasOptionsMenu(true);

        } else {
            root = (ViewGroup) inflater.inflate(
                    R.layout.msg_data_container_or, null);
            setHasOptionsMenu(true);

        }

        if (!GetAllContextWebService.serviceRunning) {
            new LoadMsgAsyncTask().executeOnExecutor(
                    AsyncTask.SERIAL_EXECUTOR,
                    0);
        }
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onViewCreated(view, savedInstanceState);
        mCoreyDBHelper = new CoreyDBHelper(getActivity());
        mMsgDataCache = new MsgDataCache(getActivity());
        Log.d(TAG, "In onView creaed");

        Log.d(TAG, "starting async task in onviewcreated");

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mPatientContainerLayout = (LinearLayout) getActivity().findViewById(
                R.id.add_slide_frame);
        mVisitListview = (HorizontalListView) getActivity().findViewById(
                R.id.listview);
        if (!mCoreyOR) {
            mPhListview = (HorizontalListView) getActivity().findViewById(
                    R.id.listview1);
            mOrderandChargesListview = (HorizontalListView) getActivity()
                    .findViewById(R.id.listview2);
        } else {
            mOrLocBtn = (Button) getActivity().findViewById(R.id.orloc);
            mSafetyBtn = (Button) getActivity().findViewById(R.id.safety);
            mStatusBtn = (Button) getActivity().findViewById(R.id.row3status);
            mOrLocBtn.setVisibility(View.VISIBLE);
            mSafetyBtn.setVisibility(View.VISIBLE);
            mStatusBtn.setVisibility(View.VISIBLE);

            mOrLocBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    //	 String ORLOC_OBJID = "275";
                    Toast.makeText(MessageTabActivityCMN.getInstance(),
                            CMN_ErrorMessages.getInstance().getValue(128), Toast.LENGTH_SHORT).show();
                }
            });
            mSafetyBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    row2row3Action(SAFETY_OBJID);


                }
            });

            mStatusBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    row2row3Action(STATUS_OBJID);

                }
            });


        }
        mYWeb = (WebView) root.findViewById(R.id.webView1);
        DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
        int densitydp = displayMetrics.densityDpi;
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        Log.d(TAG, "view width =" + width + "height = " + height);
        float density1 = displayMetrics.density;
        float scaleddensity = displayMetrics.scaledDensity;
        Log.d(TAG, "view scaleddensity =" + scaleddensity + "density = " + density1);
        Log.d(TAG, "densitydpi =" + densitydp);
        Log.d(TAG, "view width =" + width + "height = " + height);
        float textwidth = (float) (25 * density1);
        float imageheight = (height / 4) - textwidth;
        float imagewidth = (width / 3) - 10;
        float scale = imageheight / 200;
        mImageParms[0] = imageheight;
        mImageParms[1] = imagewidth;
        mImageParms[2] = scale;
        Log.d(TAG, "imageheight" + imageheight + "imagewidth" + imagewidth + "scale" + scale);

    }

    public void row2row3Action(String objid) {
        final String objectId = objid;
        RowDisplayObject r2r3ORObj = mMsgDataCache1.getObject(objid);
        String Lstvalname = getListValueName(mMsgDataCache.getObject(objid));
        Log.d(TAG, "Lstvalname=" + Lstvalname);
        if (Lstvalname != null) {
            ListItemObj listitemobj = ListItemObjMap.get(Lstvalname);
            Log.d(TAG, "listitemobj" + listitemobj.getName());


            final ArrayList<ListItem> mListItemArray = listitemobj.ListItemArray;
            Builder builder = new Builder(
                    MessageTabActivityCMN.getInstance());
            {
                builder.setAdapter(new ListValueAdapter(MessageTabActivityCMN.getInstance(), mListItemArray),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {


                                Log.d(TAG, "which in listvalueadopter on click =" + which);
                                Log.d(TAG, "mListItemArray =" + mListItemArray);

                                String mValue = mListItemArray.get(which).getValue();
                                String mdisplaytext = mListItemArray.get(which).getdisplayText();
                                int indx = mMsgDataCache1.getObject(objectId).getStsFieldIndex();
                                mMsgDataCache1.getObject(objectId).fieldsList.get(indx).setTempData(mValue);
                                Log.d(TAG, "mValue =" + mValue);
                                Log.d(TAG, "mdisplaytext =" + mdisplaytext);
                                Log.d(TAG, "mImage =" + mListItemArray.get(which).getImage());
                                Log.d(TAG, "msubscript =" + mListItemArray.get(which).getSubscript());
                                Log.d(TAG, "mbkgcolour =" + mListItemArray.get(which).getBackgroundColor());

                                new MultiUpdateAsyncTask().execute(objectId);


                            }
                        });
            }
            builder.show();
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        mcurrentUserPref = getMyActivity()
                .getSharedPreferences(CURRENT_USER, 0);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                mMessageReceiver, new IntentFilter("my-event"));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                mPartUpdateReceiver, new IntentFilter("corey.partialupdate"));

        if (mRowTwoDashboardListAdapter != null)
            mRowTwoDashboardListAdapter.notifyDataSetChanged();
        Log.d(TAG, "view adaptor refreshed");
        if (!mCoreyOR) {
            if (mRowThreeDashboardListAdapter != null)
                mRowThreeDashboardListAdapter.notifyDataSetChanged();

            if (mRowFourDashboardListAdapter != null)
                mRowFourDashboardListAdapter.notifyDataSetChanged();
        }
        if (isActive) {
            //	Log.d(TAG, "mMsgScheduleItems.size()"+mMsgScheduleItems.size());
            if (mMsgScheduleItems != null && mMsgScheduleItems.size() > 0) {
                Log.d(TAG, "Inside if loop");
                int msgId;
                MessageItem msg = mMsgScheduleItems.get(0);
                //	Log.d(TAG, "msg.type"+msg.type);
                if (msg.type == DatabaseProvider.MSG_TYPE_PHONE_CALL) {
                    msgId = mCoreyDBHelper.getMsgIdForCaller(msg.phonenumber);
                    if (!mAutosync) {
                        Log.d(TAG, "now on a phone msg " + msgId);
                        if (msgId == -1) {
                            new RetrieveCallerInfoTask(getActivity(), msg.name, msg.phonenumber, msg.company) {
                                protected void onPostExecute(Integer msgId) {
                                    super.onPostExecute(msgId);
                                    Log.d(TAG, "retrieved phone msgId " + msgId);
                                    if (msgId >= 0) {
                                        Log.d(TAG, "starting async task in onresume1");
                                        new GetDataAsyncTask().executeOnExecutor(
                                                AsyncTask.SERIAL_EXECUTOR,
                                                msgId);
                                    }
                                }
                            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        }
                    } else {
                        Log.d(TAG, "starting async task in onresume2");
                        new GetDataAsyncTask().executeOnExecutor(
                                AsyncTask.SERIAL_EXECUTOR,
                                msgId);
                    }
                } else {
                    msgId = msg.msgid;
                    Log.d(TAG, "getting the dashboard data-????");
                    Log.d(TAG, "starting async task in onresume3");
                    new GetDataAsyncTask().executeOnExecutor(
                            AsyncTask.SERIAL_EXECUTOR,
                            msgId);
                }

                if (msg.type != DatabaseProvider.MSG_TYPE_PHONE_CALL) {
                    mCoreyDBHelper.markMessageAsRead(msg);
                }
                Log.d(TAG, "installing addslidegragment");
                mSlideFragment = new AddSlideFragment(mMsgScheduleItems);
                mSlideFragment.setArguments(getArguments());
                FragmentTransaction t = this.getChildFragmentManager()
                        .beginTransaction();
                t.replace(R.id.add_slide_frame, mSlideFragment);
                t.commit();
            } else {
                String mHelpUrl = mcurrentUserPref.getString("helpurl", null);

                Log.d(TAG, "Oncreate mhelpfourl=" + mHelpUrl);

                mYWeb.setVisibility(View.VISIBLE);
                mYWeb.loadUrl("http://www.coremobileinc.com/");
            }
        }

    }

    public void DisplayFragment() {

        if (mRowTwoDashboardListAdapter != null)
            mRowTwoDashboardListAdapter.notifyDataSetChanged();
        if (!mCoreyOR) {
            if (mRowThreeDashboardListAdapter != null)
                mRowThreeDashboardListAdapter.notifyDataSetChanged();

            if (mRowFourDashboardListAdapter != null)
                mRowFourDashboardListAdapter.notifyDataSetChanged();
        }
        if (isActive) {
            if (mMsgScheduleItems != null && mMsgScheduleItems.size() > 0) {
                int msgId;
                mYWeb.setVisibility(View.GONE);
                MessageItem msg = mMsgScheduleItems.get(0);
                msgId = msg.msgid;
                new GetDataAsyncTask().executeOnExecutor(
                        AsyncTask.SERIAL_EXECUTOR,
                        msgId);
                mSlideFragment = new AddSlideFragment(mMsgScheduleItems);
                mSlideFragment.setArguments(getArguments());
                FragmentTransaction t = this.getChildFragmentManager()
                        .beginTransaction();
                t.replace(R.id.add_slide_frame, mSlideFragment);
                t.commit();


                List<ProgressObjectModel> objectModels;
                progressInfoDBControl = new ProgressInfoDBControl(MessageTabActivityCMN.activity);
                objectModels = progressInfoDBControl.getProgressBarObjects("0");
                View progressView = inflater.inflate(
                        R.layout.progressview, null);
                LinearLayout linearLayout = (LinearLayout) root.findViewById(R.id.progressFrame);
                addProgressbar(progressView, objectModels, linearLayout);
            } else {
                String mHelpUrl = mcurrentUserPref.getString("helpurl", null);
                mYWeb.setVisibility(View.VISIBLE);
                mYWeb.getSettings().setJavaScriptEnabled(true);
                mYWeb.setInitialScale(0);
                mYWeb.getSettings().setBuiltInZoomControls(true);
                mYWeb.getSettings().setLoadWithOverviewMode(true);
                mYWeb.getSettings().setUseWideViewPort(true);
                mYWeb.setVerticalScrollBarEnabled(true);
                mYWeb.setHorizontalScrollBarEnabled(true);
                //   mYWeb.setWebViewClient(new WebViewClient());
                mYWeb.setWebChromeClient(new WebChromeClient());
                mYWeb.setWebViewClient(new WebViewClient());
                mYWeb.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                mYWeb.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
                mYWeb.setVerticalScrollbarOverlay(true);
                mYWeb.loadUrl("http://www.coremobileinc.com/");
            }
        }
    }


    @Override
    public void onPause() {
        // Unregister since the activity is not visible
        super.onPause();
        isActive = false;
        if (progressDialogGetDataAsync != null && progressDialogGetDataAsync.isShowing()) {
            progressDialogGetDataAsync.dismiss();
        }
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(
                mMessageReceiver);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(
                mPartUpdateReceiver);

    }


    BroadcastReceiver mPartUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int RecvdMsgId = Integer.valueOf(intent.getStringExtra("MessageID"));
            Log.d(TAG, "partialupdate intent received  RecvdMsgId =" + RecvdMsgId);
            Log.d(TAG, "partialupdate intent received  mMessageId =" + mMessageId);
//            if (RecvdMsgId == mMessageId) {
            Log.d(TAG, "Same Message id- refresh the screen");
            Log.d(TAG, "starting async task in partupdate receiver");
            List<ProgressObjectModel> objectModels;
            progressInfoDBControl = new ProgressInfoDBControl(MessageTabActivityCMN.activity);
            objectModels = progressInfoDBControl.getProgressBarObjects("0");
            View progressView = inflater.inflate(
                    R.layout.progressview, null);
            LinearLayout linearLayout = (LinearLayout) root.findViewById(R.id.progressFrame);
            addProgressbar(progressView, objectModels, linearLayout);

            Intent myIntent = new Intent(getActivity(), MessageTabActivityCMN.class);
            myIntent.putExtra("isPatient", true);
            myIntent.setAction("com.for.view");
            myIntent.putExtra("CallerTab", 0);
            myIntent.putExtra("Position", positionChild);
            myIntent.putExtra("isFromList", true);

            // launch intent
            getActivity().startActivity(myIntent);
//            getActivity().finish();


            new GetDataAsyncTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, mMessageId);
//            onResume();
            refreshFragment();
//            }

        }
    };

    // handler for received Intents for the "my-event" event
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent

            int recpostion = intent.getIntExtra("position", 0);
            MessageTabActivityCMN.mPosition = recpostion;
            MessageItem msg = mMsgScheduleItems.get(recpostion);
            int msgId;
            if (msg.type == DatabaseProvider.MSG_TYPE_PHONE_CALL) {
                msgId = mCoreyDBHelper.getMsgIdForCaller(msg.phonenumber);
                Log.d(TAG, "now on a phone msg " + msgId);
                if (msgId == -1) {
                    if (!mAutosync) {
                        new RetrieveCallerInfoTask(getActivity(), msg.name, msg.phonenumber, msg.company) {
                            protected void onPostExecute(Integer msgId) {
                                super.onPostExecute(msgId);
                                Log.d(TAG, "retrieved phone msgId " + msgId);
                                if (msgId >= 0) {
                                    Log.d(TAG, "starting async task in messagerciever- onpage selected?1");
                                    new GetDataAsyncTask().executeOnExecutor(
                                            AsyncTask.SERIAL_EXECUTOR,
                                            msgId);
                                }
                            }
                        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                } else {
                    Log.d(TAG, "starting async task in messagerciever- onpage selected?2");
                    new GetDataAsyncTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, msgId);
                }
            } else {
                msgId = Integer.valueOf(intent.getStringExtra("message"));
                Log.d(TAG, "msgid in broadcast reciver =" + msgId);
                Log.d(TAG, "starting async task in messagerciever- onpage selected?3");
                new GetDataAsyncTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, msgId);
            }


            if (msg.type != DatabaseProvider.MSG_TYPE_PHONE_CALL) {
                mCoreyDBHelper.markMessageAsRead(msg);
            }

            if (mRowTwoDashboardListAdapter != null)
                mRowTwoDashboardListAdapter.notifyDataSetChanged();
            if (!mCoreyOR) {
                if (mRowThreeDashboardListAdapter != null)
                    mRowThreeDashboardListAdapter.notifyDataSetChanged();

                if (mRowFourDashboardListAdapter != null)
                    mRowFourDashboardListAdapter.notifyDataSetChanged();
            } else {
                OrLocationVal = mMsgDataCache.getObject(ORLOC_OBJID).getDisplaySubscript();
                OrSafetyVal = mMsgDataCache.getObject(SAFETY_OBJID).getDisplaySubscript();
                PrStatusVal = mMsgDataCache.getObject(STATUS_OBJID).getDisplaySubscript();
                mOrLocBtn.setText(OrLocationVal);
                mSafetyBtn.setText(OrSafetyVal);
                mStatusBtn.setText(PrStatusVal);
            }

        }
    };

    public void refreshFragment() {
        mSlideFragment = new AddSlideFragment(mMsgScheduleItems);
        mSlideFragment.setArguments(getArguments());
        FragmentTransaction t = this.getFragmentManager()
                .beginTransaction();
        t.replace(R.id.add_slide_frame, mSlideFragment);
        t.commit();

    }

    class GetDataAsyncTask extends AsyncTask<Integer, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "	mActivity in getdataasynctask =" + MessageTabActivityCMN.getInstance());
            if (progressDialogGetDataAsync != null && progressDialogGetDataAsync.isShowing()) {
                progressDialogGetDataAsync.dismiss();
            }
            if (GetAllContextWebService.dialog != null && GetAllContextWebService.dialog.isShowing()) {
                GetAllContextWebService.dialog.dismiss();
            }
            progressDialogGetDataAsync = new ProgressDialog(MessageTabActivityCMN.getInstance());
            progressDialogGetDataAsync.setTitle("Retrieving patient data");
            progressDialogGetDataAsync.setMessage("Please wait");
            progressDialogGetDataAsync.setCancelable(false);
            try {
                if (!progressDialogGetDataAsync.isShowing()) {
                    progressDialogGetDataAsync.show();
                }
            } catch (Exception e) {
                e.getMessage();
            }
        }

        @Override
        protected Void doInBackground(Integer... params) {
            mMessageId = params[0];
            Log.d(TAG, "Msgid passed to populatedata=" + mMessageId);
            mCoreyDBHelper = new CoreyDBHelper(getMyActivity());
            if (CMN_Preferences.getisPatientDisplayedonDashboard(getActivity())) {
                mMessageId = mCoreyDBHelper.getAllMessages(getActivity()).get(mCoreyDBHelper.getAllMessages(getActivity()).size() - 1).msgid;
            }
            mMsgDataCache.populateData(mMessageId,
                    DatabaseProvider.MSG_TYPE_INSTANT_GRATIFICATION);
            mMsgDataCache1 = mMsgDataCache;
            getListItemObjMap();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            try {
                mRowTwoObjectData = mMsgDataCache.mRows.get(1).getobjectlist();

                Collections.sort(mRowTwoObjectData, new ObjectComparator());
                Collections.reverse(mRowTwoObjectData);
                if (!mCoreyOR) {
                    mRowThreeObjectData = mMsgDataCache.mRows.get(2)
                            .getobjectlist();
                    Collections.sort(mRowThreeObjectData, new ObjectComparator());
                    Collections.reverse(mRowThreeObjectData);
                    mRowFourObjectData = mMsgDataCache.mRows.get(3).getobjectlist();
                    Collections.sort(mRowFourObjectData, new ObjectComparator());
                    Collections.reverse(mRowFourObjectData);
                } else {
                    OrLocationVal = mMsgDataCache.getObject(ORLOC_OBJID).getDisplaySubscript();
                    OrSafetyVal = mMsgDataCache.getObject(SAFETY_OBJID).getDisplaySubscript();
                    PrStatusVal = mMsgDataCache.getObject(STATUS_OBJID).getDisplaySubscript();
                    mOrLocBtn.setText(OrLocationVal);
                    mSafetyBtn.setText(OrSafetyVal);
                    mStatusBtn.setText(PrStatusVal);
                }
            } catch (Exception e) {
                Log.d("MST", " Some exception while fetching data");
            }

            mRowTwoDashboardListAdapter = new DashboardListAdapter(
                    getMyActivity(), mVisitListview, R.layout.dashboardlist,
                    mRowTwoObjectData, MessageTabActivityCMN.ID_PLAN_AHEAD, 2,
                    mMessageId, ListItemObjMap, mAutosync, mImageParms);

            mVisitListview.setAdapter(mRowTwoDashboardListAdapter);
            mRowTwoDashboardListAdapter.notifyDataSetChanged();

            mVisitListview
                    .setOnItemClickListener(makeAppItemClickListener(mRowTwoObjectData));
            if (!mCoreyOR) {
                mRowThreeDashboardListAdapter = new DashboardListAdapter(
                        getMyActivity(), mPhListview, R.layout.dashboardlist,
                        mRowThreeObjectData, MessageTabActivityCMN.ID_PLAN_AHEAD, 3,
                        mMessageId, ListItemObjMap, mAutosync, mImageParms);

                mRowThreeDashboardListAdapter.notifyDataSetChanged();

                mPhListview.setAdapter(mRowThreeDashboardListAdapter);

                mPhListview
                        .setOnItemClickListener(makeAppItemClickListener(mRowThreeObjectData));

                mRowFourDashboardListAdapter = new DashboardListAdapter(
                        getMyActivity(), mPhListview, R.layout.dashboardlist,
                        mRowFourObjectData, MessageTabActivityCMN.ID_PLAN_AHEAD, 4,
                        mMessageId, ListItemObjMap, mAutosync, mImageParms);

                mOrderandChargesListview.setAdapter(mRowFourDashboardListAdapter);

                mRowFourDashboardListAdapter.notifyDataSetChanged();

                mOrderandChargesListview
                        .setOnItemClickListener(makeAppItemClickListener(mRowFourObjectData));
            } else {
                OrLocationVal = mMsgDataCache.getObject(ORLOC_OBJID).getDisplaySubscript();
                OrSafetyVal = mMsgDataCache.getObject(SAFETY_OBJID).getDisplaySubscript();
                PrStatusVal = mMsgDataCache.getObject(STATUS_OBJID).getDisplaySubscript();
                mOrLocBtn.setText(OrLocationVal);
                mSafetyBtn.setText(OrSafetyVal);
                mStatusBtn.setText(PrStatusVal);
                String colorRaw = null;
                String colour;
                String Lstvalname;
                int hex;
                Lstvalname = getListValueName(mMsgDataCache.getObject(SAFETY_OBJID));
                if (Lstvalname != null) {
                    ListItemObj listitemobj = ListItemObjMap.get(Lstvalname);
                    //    Log.d(TAG, "listitemobj" + listitemobj.getName());
                    String sts = mMsgDataCache.getObject(SAFETY_OBJID).getObjectStatus();
                    //    Log.d(TAG, "sts" + sts);
                    colorRaw = listitemobj.getColor(sts);
                    //   Log.d(TAG, "colorRaw=" + colorRaw);
                }
                if (colorRaw != null) {
                    colour = "#" + colorRaw;

                    hex = Color.parseColor(colour);
                    mSafetyBtn.setBackgroundColor(hex);
                }
                /*
                 * set color for status button
				 */
                Lstvalname = getListValueName(mMsgDataCache.getObject(STATUS_OBJID));
                // Log.d(TAG, "Lstvalname=" + Lstvalname);
                if (Lstvalname != null) {
                    ListItemObj listitemobj = ListItemObjMap.get(Lstvalname);
                    //    Log.d(TAG, "listitemobj" + listitemobj.getName());
                    String sts = mMsgDataCache.getObject(STATUS_OBJID).getObjectStatus();
                    //    Log.d(TAG, "sts" + sts);
                    colorRaw = listitemobj.getColor(sts);
                    //    Log.d(TAG, "colorRaw=" + colorRaw);
                }
                if (colorRaw != null) {
                    colour = "#" + colorRaw;

                    hex = Color.parseColor(colour);
                    mStatusBtn.setBackgroundColor(hex);
                }

            }
            if (progressDialogGetDataAsync != null && progressDialogGetDataAsync.isShowing()) {
                progressDialogGetDataAsync.dismiss();
                progressDialogGetDataAsync = null;
            }
            Log.d(TAG, " progressdialog closed in getdataAsync");

        }
    }


    class LoadMsgAsyncTask extends AsyncTask<Integer, Void, Void> {
        ProgressDialog progressDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "	mActivity in getdataasynctask =" + MessageTabActivityCMN.getInstance());
            progressDialog = new ProgressDialog(MessageTabActivityCMN.getInstance());
            progressDialog.setTitle("Loading patient meta data");
            progressDialog.setMessage("Please wait");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Integer... params) {


            if (CMN_Preferences.getisPatientDisplayedonDashboard(MessageTabActivityCMN.activity)) {
                loadMessageArraysForSearchedPatient(MessageTabActivityCMN.activity);
            } else if (loadMessageArrays(MessageTabActivityCMN.activity)) {
                if (loadingDataFromServer != null && loadingDataFromServer.isShowing()) {
                    loadingDataFromServer.dismiss();
                }
                if (progressDialogGetDataAsync != null && progressDialogGetDataAsync.isShowing()) {
                    progressDialogGetDataAsync.dismiss();
                    progressDialogGetDataAsync = null;
                }
            } else {

                MessageTabActivityCMN.activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mCoreyDBHelper = new CoreyDBHelper(MessageTabActivityCMN.activity);

                        if (GetAllContextWebService.patientsContexts.size() > 0) {
                            MessageTabActivityCMN.shouldCallTriggerAPI = true;
                            if (positionChild > GetAllContextWebService.patientsContexts.size())
                                return;
                            MyApplication.INSTANCE.stopService(new Intent(MyApplication.INSTANCE,PendingNotificationService.class));
                            new MessageTabActivityCMN().getSinglePatientData(MessageTabActivityCMN.activity, GetAllContextWebService.patientsContexts.get(positionChild).ContextId);
                        }
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                progressDialog = null;
                try {
                    DisplayFragment();
                    if (!PendingNotificationService.isRunning)
                        getActivity().startService(new Intent(getActivity(), PendingNotificationService.class));
                } catch (Exception e) {
                    e.getMessage();
                }
            }
        }

    }

    public void CallOnResume() {
        this.onResume();
    }

    public String getListValueName(RowDisplayObject lItem) {

        ArrayList<Fields> ListofFields;
        ListofFields = lItem.fieldsList;
        String lstval = null;
        for (Fields flst : ListofFields) {
            //	 if(flst.getlistValue()!= null)
            if (flst.getHasStatus().equalsIgnoreCase("true")) {
                lstval = flst.getlistValue();
                break;
            }
        }


        return lstval;
    }

    private boolean loadMessageArraysForSearchedPatient(Activity activity) {
        // Convention
        boolean toReturn = false;
        mCoreyDBHelper = new CoreyDBHelper(activity);
        if (mCoreyDBHelper.getAllMessages(activity) == null || mCoreyDBHelper.getAllMessages(activity).size() == 0) {
            return false;
        }
        try {
            MessageItem msg = mCoreyDBHelper.getAllMessages(getActivity()).get(mCoreyDBHelper.getAllMessages(getActivity()).size() - 1);
            if (!mMsgScheduleItems.contains(msg)) {
                String patdetails = mCoreyDBHelper.getPatientDetails(msg.msgid);
                msg.PatientDetails = patdetails;
                msg.ImageUrl1 = mCoreyDBHelper.getObjImageUrlbyqdr(msg.msgid, 1, "Calendar");
                msg.ImageUrl2 = mCoreyDBHelper.getObjImageUrlbyqdr(msg.msgid, 1, "MessagingObj");
                if (mCoreyDBHelper.getObjID(msg.msgid, "PatientData", "MessagingObj") != null) {

                    msg.MsgObjId = mCoreyDBHelper.getObjID(msg.msgid, "PatientData", "MessagingObj");
                }
                mMsgScheduleItems.add(msg);
            }
            toReturn = true;
        } catch (IndexOutOfBoundsException e) {
            e.getMessage();
        } catch (Exception e) {
            e.getMessage();
        }
        MyApplication.INSTANCE.MsgMetaData = mMsgScheduleItems;
        Log.d(TAG, "	dialog in load message Arrays closed=");
        return toReturn;
    }

    private boolean loadMessageArrays(Activity activity) {
        boolean toReturn = false;
        mCoreyDBHelper = new CoreyDBHelper(activity);
        if (mCoreyDBHelper.getAllMessages(activity) == null || mCoreyDBHelper.getAllMessages(activity).size() == 0) {
            return false;
        }

        try {
            for (int i = 0; i < mCoreyDBHelper.getAllMessages(getActivity()).size(); i++) {
                MessageItem msg = mCoreyDBHelper.getAllMessages(getActivity()).get(i);
                if (GetAllContextWebService.patientsContexts.get(MessageTabActivityCMN.patientPosition).ContextId.toString().trim().equalsIgnoreCase(msg.ContextId.toString().trim())) {
                    Log.d("Compairing_objects", "" + GetAllContextWebService.patientsContexts.get(positionChild).ContextId.trim() + " with " + msg.ContextId.toString().trim());
//                    CMN_Preferences.setContextId(activity, GetAllContextWebService.patientsContexts.get(positionChild).ContextId);
                    if (!mMsgScheduleItems.contains(msg)) {
                        String patdetails = mCoreyDBHelper.getPatientDetails(msg.msgid);
                        msg.PatientDetails = patdetails;
                        msg.ImageUrl1 = mCoreyDBHelper.getObjImageUrlbyqdr(msg.msgid, 1, "Calendar");
                        msg.ImageUrl2 = mCoreyDBHelper.getObjImageUrlbyqdr(msg.msgid, 1, "MessagingObj");

                        if (mCoreyDBHelper.getObjID(msg.msgid, "PatientData", "MessagingObj") != null) {

                            msg.MsgObjId = mCoreyDBHelper.getObjID(msg.msgid, "PatientData", "MessagingObj");
                        }
                        mMsgScheduleItems.add(msg);
                    }
                    toReturn = true;
                }
            }
        } catch (IndexOutOfBoundsException e) {
            e.getMessage();
        } catch (Exception e) {
            e.getMessage();
        }
        MyApplication.INSTANCE.MsgMetaData = mMsgScheduleItems;
        Log.d(TAG, "	dialog in load message Arrays closed=");
        return toReturn;
    }

    private Context getMyActivity() {
        return MessageTabActivityCMN.getInstance();
    }

    public static MsgDataCache getCache() {
        return mMsgDataCache1;
    }

    private void showSubLevelList(String ObjID, String Objname) {


        Intent detailedIntent = new Intent(MessageTabActivityCMN.getInstance(), ChildIconActivityCMN.class);
        detailedIntent.putExtra("ObjectsList", ObjID);
        detailedIntent.putExtra("msgid", mMessageId);
        detailedIntent.putExtra("objname", Objname);
        startActivity(detailedIntent);

    }


    private AdapterView.OnItemClickListener makeAppItemClickListener(
            final ArrayList<RowDisplayObject> data) {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos,
                                    long id) {
                int row = pos;

                final RowDisplayObject item = data.get(row);

                boolean isParent = item.isParent;
                boolean isMultiselect = item.IsMultiselecUi();
                Log.d(TAG, "isMultiselect=" + isMultiselect);
                if (item.isLoading()) {
                    return;
                }

                if (isParent) {

//                    showSubLevelList(data.get(row).getObjectId(), data.get(row).getDisplayName());

                    String subobjid = "" + data.get(row).getObjectId();
                    if (mcurrentUserPref.getString("user_category", "").equalsIgnoreCase("patient") && subobjid != null && subobjid.equalsIgnoreCase("122")) {
                        Intent myIntent = new Intent(MessageTabActivityCMN.getInstance(), PatientRemindersActivityCMN.class);
                        startActivity(myIntent);
                    } else {
                        showSubLevelList(data.get(row).getObjectId(), data.get(row).getDisplayName());
                    }


                } else {
                    String openUrl = item.getOpenUrl();
                    Log.d(TAG, "object type = " + item.getObjType());
                    if (item.getObjectName().equalsIgnoreCase("Additional Analytics")) {
                        Intent myIntent = new Intent(MessageTabActivityCMN.getInstance(), PatientAnalyticsActivityCMN.class);
                        String PatId = item.getMsgContext();

                        myIntent.putExtra("source", "DASHBOARD");
                        myIntent.putExtra("Patientid", PatId);
                        // launch intent
                        startActivity(myIntent);

                    } else if (item.getObjectName().equalsIgnoreCase("PictureSend")) {
                        if (item.getObjectid().equalsIgnoreCase("65")) {
                            Intent myIntent = new Intent(MessageTabActivityCMN.getInstance(), com.coremobile.coreyhealth.newui.CMN_ImageUploadActivity.class);
                            String PatId = item.getMsgContext();

                            // myIntent.putExtra("source", "DASHBOARD");
                            myIntent.putExtra("PATIENT_ID", PatId);
                            myIntent.putExtra("OBJECT_ID", item.getObjectid());
                            // launch intent

                            startActivity(myIntent);
                        }

                    } else if (item.getAppId().equalsIgnoreCase("24") && item.getObjectid().equalsIgnoreCase("217")) {

                        Intent myIntent = new Intent(MessageTabActivityCMN.getInstance(), RatingSelectActivityCMN.class);
                        String PatId = item.getMsgContext();

                        myIntent.putExtra("source", "DASHBOARD");
                        myIntent.putExtra("Patientid", PatId);
                        myIntent.putExtra("objectId", item.getObjectid());
                        // launch intent
                        startActivity(myIntent);


                    } else if (item.getObjType() != null && item.getObjType().equalsIgnoreCase("Messaging")) {
                        Log.d(TAG, "invoking message for object =" + item.getObjectid());

                        Intent myIntent = new Intent(MessageTabActivityCMN.getInstance(), CMN_NeworViewMessagesActivity.class);
                        String PatId = item.getMsgContext();

                        myIntent.putExtra("source", "DASHBOARD");
                        myIntent.putExtra("Patientid", PatId);
                        myIntent.putExtra("ObjID", item.getObjectid());
                        myIntent.putExtra("MessageTo", item.getDisplaySubscript());
                        myIntent.putExtra("PatientName", item.getDisplayName());
                        // launch intent
                        startActivity(myIntent);
                    } else if (item.getObjectid() != null && item.getObjectid().equalsIgnoreCase("56")) {
//                        showPopup();/
                        Intent myIntent = new Intent(MessageTabActivityCMN.getInstance(), RemindersTypeActivityCMN.class);
                        myIntent.putExtra("toLoad", "Response");
                        startActivity(myIntent);
                    } else if (item.getObjectid() != null && item.getObjectid().equalsIgnoreCase("114")) {
//                        showPopup();/
                        Intent intent = new Intent(getActivity(), GridImageActivityCMN.class);
                        intent.putExtra("OBJECT_ID", item.getObjectid());
                        intent.putExtra("OBJECT_NAME", item.getDisplayName() + " " + item.getDisplaySubscript());
                        intent.putExtra("PATIENT_ID", item.getMsgContext());
                        startActivity(intent);
                    } else if (item.getObjectName().equalsIgnoreCase("ProviderPictureSend")) {
                        if (item.getObjectid().equalsIgnoreCase("450")) {
                            Intent myIntent = new Intent(MessageTabActivityCMN.getInstance(), GridImageActivityCMN.class);
                            String PatId = item.getMsgContext();
                            myIntent.putExtra("OBJECT_NAME", item.getDisplayName() + " " + item.getDisplaySubscript());
                            myIntent.putExtra("PATIENT_ID", PatId);
                            myIntent.putExtra("OBJECT_ID", item.getObjectid());
                            // launch intent

                            startActivity(myIntent);
                        }
                    } else if (item.getObjectName().equalsIgnoreCase("ProviderPictures")) {
                        if (item.getObjectid().equalsIgnoreCase("451")) {
                            Intent myIntent = new Intent(MessageTabActivityCMN.getInstance(), GridImageActivityCMN.class);
                            String PatId = item.getMsgContext();
                            myIntent.putExtra("OBJECT_NAME", item.getDisplayName() + " " + item.getDisplaySubscript());
                            myIntent.putExtra("PATIENT_ID", PatId);
                            myIntent.putExtra("OBJECT_ID", item.getObjectid());
                            // launch intent

                            startActivity(myIntent);
                        }
                    } /*
                    For journal
                     */ else if (item.getObjectid() != null && item.getObjectid().equalsIgnoreCase("59")) {
//                        showPopup();/
                        Intent intent = new Intent(getActivity(), CMN_JournalActivity.class);
                        intent.putExtra("OBJECT_ID", item.getObjectid());
                        intent.putExtra("OBJECT_NAME", item.getDisplayName() + " " + item.getDisplaySubscript());
                        intent.putExtra("PATIENT_ID", item.getMsgContext());
                        startActivity(intent);
                    } else if (isMultiselect) {
                        Intent myIntent = new Intent(MessageTabActivityCMN.getInstance(), MultiSelectActivityCMN.class);
                        String listitemname = item.getListValue();
                        String objtitle = item.getDisplayName();
                        myIntent.putExtra("ObjID", item.getObjectid());
                        //    myIntent.putExtra("listvaluename", objtitle);
                        myIntent.putExtra("listvaluename", listitemname);
                        myIntent.putExtra("msgid", mMessageId);
                        // launch intent
                        startActivity(myIntent);
                    } else if (openUrl != null && item.getField().size() == 0) {
                        mCoreyDBHelper.updateObjIsNew(mMessageId, item.getObjectid(), "false");
                        Intent urlIntent = new Intent(MessageTabActivityCMN.getInstance(),
                                WebViewActivityCMN.class);
            /*	if (openUrl.endsWith("pdf"))
                {
					openUrl= "https://docs.google.com/viewer?url="+openUrl;
		        } */
                        urlIntent.putExtra("myurl", openUrl);
                        urlIntent.putExtra("objname", item.getDisplayName());
                        startActivity(urlIntent);


                    } else if (item.getsGraphType() != null && !item.getsGraphType().isEmpty()) {
                        organizationName = mcurrentUserPref.getString("Organization", null);


                        CMN_GetGraphDataWebService dataWebService = new CMN_GetGraphDataWebService(MessageTabActivityCMN.activity);
                        dataWebService.execute("" + item.getsGraphId(), item.getDisplayName());
                    } else {
                        Intent detailedIntent = new Intent(MessageTabActivityCMN.getInstance(),
                                DetailedApplicationData.class);
                        detailedIntent.putExtra("ObjName", item.getDisplayName());

                        detailedIntent.putExtra("ObjID", item.getObjectid());
                        detailedIntent.putExtra("IsEditable", item.isEditable);
                        detailedIntent.putExtra("msgid", mMessageId);
                        startActivity(detailedIntent);
                    }
                }

            }

        };

    }

    private void showPopup() {

        LayoutInflater layoutInflater = (LayoutInflater) MessageTabActivityCMN.activity.getBaseContext()
                .getSystemService(MessageTabActivityCMN.activity.LAYOUT_INFLATER_SERVICE);

        View popupView = layoutInflater.inflate(R.layout.reminder_popup_window, null);

        final PopupWindow popup = new PopupWindow(popupView,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT,
                true);


        popup.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        Button addReminder = (Button) popupView.findViewById(R.id.buttonAddNewReminder);
        addReminder.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(MessageTabActivityCMN.activity, AddNewReminderActivityCMN.class));
                popup.dismiss();
            }
        });

        Button editReminder = (Button) popupView.findViewById(R.id.buttonEditReminder);
        editReminder.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessageTabActivityCMN.activity, ProviderRemindersActivityCMN.class);
                intent.putExtra("isEdit", true);
                startActivity(intent);
                popup.dismiss();
            }
        });

        Button useReminder = (Button) popupView.findViewById(R.id.buttonUseCreatedReminder);
        useReminder.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessageTabActivityCMN.activity, ProviderRemindersActivityCMN.class);
                intent.putExtra("isUseForNewReminder", true);
                startActivity(intent);
                popup.dismiss();
            }
        });

        Button close = (Button) popupView.findViewById(R.id.buttonCancel);
        close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                popup.dismiss();
            }
        });
    }

    public class ObjectComparator implements Comparator<RowDisplayObject> {

        @Override
        public int compare(RowDisplayObject o1, RowDisplayObject o2) {
            return ((o1.getDispCol()) > (o2.getDispCol()) ? -1 : ((o1.getDispCol()) == (o1.getDispCol()) ? 0 : 1));
        }
    }

    public void getListItemObjMap() {
         /*
          * populate Itemlist level variables firs
    	  */

        if (MessageTabActivityCMN.getInstance().getContentResolver() != null) {
            Cursor ListItemCursor = MessageTabActivityCMN.getInstance().getContentResolver().query(
                    DatabaseProvider.CONTENT_URI,
                    new String[]{DatabaseProvider.KEY_ID,
                            DatabaseProvider.KEY_FIELD_NAME,
                            DatabaseProvider.KEY_FIELD_DISPLAYTEXT,
                            DatabaseProvider.KEY_FIELD_VALUE,
                            DatabaseProvider.KEY_PARENT_ID_CONTEXT
                    },
                    DatabaseProvider.KEY_MSGID + " =? AND "
                            + DatabaseProvider.KEY_APPLICATION
                            + " =? AND "
                            + DatabaseProvider.KEY_OBJECT_NAME
                            + " =?  ",
                    new String[]{String.valueOf(mMessageId), "ListItems", "ListItem"}, null);

            //   Log.d(TAG, "ListItemCursor" + ListItemCursor);
            //   Log.d(TAG, "ListItemCursor count" + ListItemCursor.getCount());
            if (ListItemCursor != null && ListItemCursor.getCount() > 0) {
                int i = 0;
                String ListItemName = "";
                //ListItemName=ListItemCursor.getString(2);
                //	Log.d(TAG,"ListItemName outside loop="+ListItemName);
            /*
             * This will retreive only the listitems.  Each listitme
			 * has 4 attributes saved as rows
			 */

                while (ListItemCursor.moveToNext()) {
                    //	Log.d (TAG, "listitemvalue ="+ListItemCursor.getString(2));
                    switch (i % 4) {
                        case 0:
                            ListItemObj listitemobj = new ListItemObj();
                            listitemobj.setName(ListItemCursor.getString(2));
                            ListItemName = ListItemCursor.getString(2);
                            ListItemObjMap.put(listitemobj.getName(), listitemobj);
                            //     Log.d(TAG, "ListItemName=" + ListItemName);
                            break;

                        case 1:
                            ListItemObjMap.get(ListItemName).setScrollDirection(ListItemCursor.getString(2));
                            break;
                        case 2:
                            ListItemObjMap.get(ListItemName).setOnTouch(ListItemCursor.getString(2));
                            break;
                        case 3:
                            ListItemObjMap.get(ListItemName).setCommonTag(ListItemCursor.getString(2));
                            break;
                    }
                    //  Log.d(TAG, "ListItemObjMap size = " + ListItemObjMap.size());
                    //  Log.d(TAG, "ListItemObj size = " + ListItemObjMap.get(ListItemName).getName());
                    i++;
                }
                if (ListItemCursor != null) ListItemCursor.close();
            }
        /*
         if (ListItemCursor != null && ListItemCursor.getCount() > 0)
    	 { 
    	   		
    		 int levelobjcount=0;
    		 for(int i=0; i<(ListItemCursor.getCount()/4); i++)
    		 {
    			 ListItemObj listitemobj=new ListItemObj();
					listitemobj.setName(ListItemCursor.getString(2));
					ListItemCursor.moveToNext();
					listitemobj.setScrollDirection(ListItemCursor.getString(2));
					ListItemCursor.moveToNext();
					listitemobj.setOnTouch(ListItemCursor.getString(2));
					ListItemCursor.moveToNext();
					listitemobj.setCommonTag(ListItemCursor.getString(2));
					ListItemObjMap.put(listitemobj.getName(), listitemobj);
					ListItemCursor.moveToNext();
			 }    	
    	 }
    	 if (ListItemCursor != null)ListItemCursor.close();
    	  
    	 */
         /*
          * populate Item level variables next
    	  * Get all the rows with item as object name.
    	  * Extract the list from the rows which has the definit listname 	  * 
    	  */
            Cursor ItemCursor = MessageTabActivityCMN.getInstance().getContentResolver().query(
                    DatabaseProvider.CONTENT_URI,
                    new String[]{DatabaseProvider.KEY_ID,
                            DatabaseProvider.KEY_FIELD_NAME,
                            DatabaseProvider.KEY_FIELD_DISPLAYTEXT,
                            DatabaseProvider.KEY_FIELD_VALUE,
                            DatabaseProvider.KEY_PARENT_ID_CONTEXT
                    },
                    DatabaseProvider.KEY_MSGID + " =? AND "
                            + DatabaseProvider.KEY_APPLICATION
                            + " =? AND "
                            + DatabaseProvider.KEY_OBJECT_NAME
                            + " =?  ",
                    new String[]{String.valueOf(mMessageId), "ListItems", "Item"}, null);
            //	String ItemName=ItemCursor.getString(2);
            //	Log.d(TAG,"ItemName outside loop="+ItemName);
            if (ItemCursor != null && ItemCursor.getCount() > 0) {
                int i = 0;
                int index = 0;
                String ListItemName = "";
                String PrevListItemName = "";

                while (ItemCursor.moveToNext()) {
                    int switchval = i % 11;
                    //   Log.d(TAG, "switchval" + switchval);
                    switch (switchval) {
                        case 0:
                            ListItemName = ItemCursor.getString(4);
                            if (!ListItemName.equals(PrevListItemName)) {
                                i = 0;
                                PrevListItemName = ListItemName;
                            }
                            index = i / 11;
                            ListItem listitem = new ListItem();
                            listitem.setValue(ItemCursor.getString(2));
                            //   Log.d(TAG, "Value=" + ItemCursor.getString(2));
                            //   Log.d(TAG, "ListItemName=" + ListItemName);
                            ListItemObjMap.get(ListItemName).ListItemArray.add(listitem);
                            //
                            //	Log.d(TAG,"ListItemValue=" +ItemCursor.getString(2));
                            //	Log.d(TAG, "i/5 =" +index);
                            //	Log.d(TAG,"value written in =" +ListItemObjMap.get(ListItemName).ListItemArray.get(index).getValue());
                            //	Log.d(TAG,"value written in case 0 with index0 =" +ListItemObjMap.get(ListItemName).ListItemArray.get(0).getValue());
                            break;
                        case 1:    //Log.d(TAG,"value written in case 1 with index0 before next write=" +ListItemObjMap.get(ListItemName).ListItemArray.get(0).getValue());
                            ListItemObjMap.get(ListItemName).ListItemArray.get(index).setdisplayText(ItemCursor.getString(2));
                            //Log.d(TAG, "ListItemName in case1 =" +ListItemName);
                            //Log.d(TAG, "i/5 =" +index);
                            //Log.d(TAG,"value written in- case1 =" +ListItemObjMap.get(ListItemName).ListItemArray.get(index).getValue());
                            //Log.d(TAG,"value written in case 1 with index0 =" +ListItemObjMap.get(ListItemName).ListItemArray.get(0).getValue());
                            //Log.d(TAG,"disptext written in- case1 =" +ListItemObjMap.get(ListItemName).ListItemArray.get(index).getdisplayText());
                            //Log.d(TAG,"displaytext="+ItemCursor.getString(2));
                            break;
                        case 2:
                            ListItemObjMap.get(ListItemName).ListItemArray.get(index).setImage(ItemCursor.getString(2));
                            //Log.d(TAG,"image="+ItemCursor.getString(2));
                            break;
                        case 3:
                            ListItemObjMap.get(ListItemName).ListItemArray.get(index).setSubscript(ItemCursor.getString(2));
                            //Log.d(TAG,"subscript="+ItemCursor.getString(2));
                            break;
                        case 4:
                            ListItemObjMap.get(ListItemName).ListItemArray.get(index).setBackgroundColor(ItemCursor.getString(2));
                            //Log.d(TAG,"bkgclr="+ItemCursor.getString(2));
                            break;
                        case 5:
                            ListItemObjMap.get(ListItemName).ListItemArray.get(index).setDescription(ItemCursor.getString(2));
                            //Log.d(TAG,"bkgclr="+ItemCursor.getString(2));
                            break;
                        case 6:
                            ListItemObjMap.get(ListItemName).ListItemArray.get(index).setId(ItemCursor.getString(2));
                            //Log.d(TAG,"bkgclr="+ItemCursor.getString(2));
                            break;
                        case 7:
                            ListItemObjMap.get(ListItemName).ListItemArray.get(index).setOpenUrl(ItemCursor.getString(2));
                            //Log.d(TAG,"bkgclr="+ItemCursor.getString(2));
                            break;
                        case 8:
                            break;
                        case 9:
                            break;
                        case 10:
                            break;

                        default:
                            Log.d(TAG, "in  switch default");
                            break;
                    }
                    i++;
                    //Log.d(TAG,"i in Itemcursor loop" +i);
                }
                if (ItemCursor != null) ItemCursor.close();
            }
    /*	 Log.d(TAG,"messagelist Item value ="+ListItemObjMap.get("AdminUsersList").ListItemArray.get(0).getValue());
         Log.d(TAG,"messagelist Item displaytext ="+ListItemObjMap.get("AdminUsersList").ListItemArray.get(0).getdisplayText());
    	 Log.d(TAG,"messagelist Item image ="+ListItemObjMap.get("AdminUsersList").ListItemArray.get(0).getImage());
    	 Log.d(TAG,"messagelist Item subscript ="+ListItemObjMap.get("AdminUsersList").ListItemArray.get(0).getSubscript());
    	 Log.d(TAG,"messagelist Item bkgclor ="+ListItemObjMap.get("AdminUsersList").ListItemArray.get(0).getBackgroundColor());
   */
    /*	 
         if (ItemCursor != null && ItemCursor.getCount() > 0)
    	 {     		
    		 int levelobjcount=0;
    		 for(int i=0; i<(ItemCursor.getCount()/4); i++)
    		 {
    			 ListItem listitem=new ListItem();
					listitem.setValue(ItemCursor.getString(2));
					ItemCursor.moveToNext();
					listitem.setImage(ItemCursor.getString(2));
					ItemCursor.moveToNext();
					listitem.setSubscript(ItemCursor.getString(2));
					ItemCursor.moveToNext();
					listitem.setBackgroundColor(ItemCursor.getString(2));
					ListItemObjMap.get(ListItemCursor.getString(4)).ListItemArray.add(listitem);
					ItemCursor.moveToNext();
			 }    	
    	 }
    	 if (ListItemCursor != null)ListItemCursor.close();
    	*/
        /*
         ArrayList<String> ListItemObjNames =  new ArrayList<String> (ListItemObjMap.keySet());
    	 for (String listobjnames:  ListItemObjNames )
    	 {
    		 
    	 } */

        }
    }

    public JSONObject getJSONOnject(String objectid, RowDisplayObject ORObj) {
        JSONObject jObject = new JSONObject();
        try {
            jObject.accumulate("AppID", ORObj.getAppId());

            jObject.accumulate("Context", ORObj.getMsgContext());
            if (mAutosync) jObject.accumulate("Action", "Append");
            else jObject.accumulate("Action", "Update");

            jObject.accumulate("ApplicationName", ORObj.getAppName());

            jObject.accumulate("ObjectName", ORObj.getObjectName());


            jObject.accumulate("ObjectID", objectid);

            JSONArray jarray = new JSONArray();
            JSONObject jFieldObject = null;
            ArrayList<Fields> mFArrayList = ORObj.getField();
            for (int i = 0; i < mFArrayList.size(); i++) {

                if (mFArrayList.get(i).getTempData() != null) {
                    jFieldObject = new JSONObject();
                    jFieldObject.accumulate("FieldID", mFArrayList.get(i).getFieldId());
                    jFieldObject.accumulate("FieldName", mFArrayList.get(i).getFieldName());
                    jFieldObject.accumulate("FieldValue", mFArrayList.get(i).getTempData());
                    jarray.put(jFieldObject);

                }

            }


            jObject.accumulate("Data", jarray);
        } catch (Exception e) {
            e.getMessage();
        }
        return jObject;
    }


    class MultiUpdateAsyncTask extends AsyncTask<String, Void, Integer> {
        ProgressDialog mProgressDialog;
        ArrayList<Fields> mFArrayList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(MessageTabActivityCMN.getInstance());
            mProgressDialog.setTitle("Updating");
            mProgressDialog.setMessage("Please Wait ..");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected Integer doInBackground(String... arg0) {


            String objectid = arg0[0];
            Log.d(TAG, "objectid in multiupdate async task" + objectid);
            RowDisplayObject ORObj = mMsgDataCache1.getObject(objectid);

            String url = null;
            HttpPost httpPost;

            if (AppConfig.isAESEnabled) {
                url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                        + "updateApplicationdatamultiple_s.aspx?token=" + CMN_Preferences.getUserToken(getActivity());
//                url = url + "" + CMN_AES.encrypt(URLEncoder.encode(getJSONOnject(objectid, ORObj).toString()), CMN_Preferences.getUserToken(getActivity()));
                httpPost = new HttpPost(url + "");
                try {
                    httpPost.setEntity(new StringEntity(CMN_AES.encrypt(getJSONOnject(objectid, ORObj).toString(), CMN_Preferences.getUserToken(activity))));
                } catch (UnsupportedEncodingException e) {
                    e.getMessage();
                }
            } else {
                url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                        + "updateApplicationdatamultiple.aspx?token=" + CMN_Preferences.getUserToken(getActivity()) + "&jsonData=";
                url = url + "" + URLEncoder.encode(getJSONOnject(objectid, ORObj).toString());
                httpPost = new HttpPost(url + "");

            }
            //Log.i(TAG,
            //"url: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% "
            //+ url);
            if (Utils.checkInternetConnection()) {
                JSONObject json = null;
                HttpParams httpParameters = new BasicHttpParams();
                int timeoutConnection = 3000;
                HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
                int timeoutSocket = 3000;
                HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);


                CloseableHttpClient httpclient = new CMN_HTTPSClient(MessageTabActivityCMN.activity, url).getClient();
                try {
                    //Log.e(TAG, "url" + url);

//                    HttpPost httpPost = new HttpPost(url + "");

                    HttpResponse response = httpclient.execute(httpPost);
                    Log.d(TAG, "POST response: " + response.getStatusLine());

                    String result = EntityUtils.toString(response.getEntity());

                    json = new JSONObject(CMN_AES.decryptData(result, CMN_Preferences.getUserToken(getActivity())));
                    if (json != null) {
                        String jsonResult = json.getString("result");
                        Log.d("MyLogs",
                                "UpdateApplicationData %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% json response: "
                                        + jsonResult);
                    }
                } catch (UnsupportedEncodingException e1) {
                    e1.getMessage();
                    return -1;
                } catch (ClientProtocolException e) {
                    e.getMessage();
                } catch (IOException e) {
                    e.getMessage();
                } catch (JSONException e) {
                    e.getMessage();
                } finally {
                    if (httpclient != null) {
                        try {
                            httpclient.close();
                        } catch (IOException e) {
                            e.getMessage();
                        }
                    }
                }

                return -1;
            }
            return -1;

        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }

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


    public String getPackageName() {
        return getActivity().getPackageName();
    }

    private void addProgressbar(View progressView, List<ProgressObjectModel> objectModels, LinearLayout linearLayout) {
        if (objectModels.size() == 1) {
            progressView.findViewById(R.id.progress2).setVisibility(View.GONE);
            progressView.findViewById(R.id.progress3).setVisibility(View.GONE);
            progressView.findViewById(R.id.progress4).setVisibility(View.GONE);
            TextView textView1 = (TextView) progressView.findViewById(R.id.progressText1);
            ImageView imageView1 = (ImageView) progressView.findViewById(R.id.progressImage1);
            textView1.setText(objectModels.get(0).getStageName());
            if (objectModels.get(0).getColor().startsWith("#")) {
                imageView1.setBackgroundColor(Color.parseColor(objectModels.get(0).getColor()));
            } else {
                imageView1.setBackgroundColor(Color.parseColor("#" + objectModels.get(0).getColor()));
            }

        }
        if (objectModels.size() == 2) {
            progressView.findViewById(R.id.progress2).setVisibility(View.GONE);
            progressView.findViewById(R.id.progress3).setVisibility(View.GONE);
            TextView textView1 = (TextView) progressView.findViewById(R.id.progressText1);
            ImageView imageView1 = (ImageView) progressView.findViewById(R.id.progressImage1);
            textView1.setText(objectModels.get(0).getStageName());
            if (objectModels.get(0).getColor().startsWith("#")) {
                imageView1.setBackgroundColor(Color.parseColor(objectModels.get(0).getColor()));
            } else {
                imageView1.setBackgroundColor(Color.parseColor("#" + objectModels.get(0).getColor()));
            }
            TextView textView2 = (TextView) progressView.findViewById(R.id.progressText4);
            ImageView imageView2 = (ImageView) progressView.findViewById(R.id.progressImage4);
            textView2.setText(objectModels.get(1).getStageName());
            if (objectModels.get(1).getColor().startsWith("#")) {
                imageView2.setBackgroundColor(Color.parseColor(objectModels.get(1).getColor()));
            } else {
                imageView2.setBackgroundColor(Color.parseColor("#" + objectModels.get(1).getColor()));
            }
        }
        if (objectModels.size() == 3) {
            progressView.findViewById(R.id.progress3).setVisibility(View.GONE);
            TextView textView1 = (TextView) progressView.findViewById(R.id.progressText1);
            ImageView imageView1 = (ImageView) progressView.findViewById(R.id.progressImage1);
            textView1.setText(objectModels.get(0).getStageName());
            if (objectModels.get(0).getColor().startsWith("#")) {
                imageView1.setBackgroundColor(Color.parseColor(objectModels.get(0).getColor()));
            } else {
                imageView1.setBackgroundColor(Color.parseColor("#" + objectModels.get(0).getColor()));
            }
            TextView textView2 = (TextView) progressView.findViewById(R.id.progressText2);
            ImageView imageView2 = (ImageView) progressView.findViewById(R.id.progressImage2);
            textView2.setText(objectModels.get(1).getStageName());
            if (objectModels.get(1).getColor().startsWith("#")) {
                imageView2.setBackgroundColor(Color.parseColor(objectModels.get(1).getColor()));
            } else {
                imageView2.setBackgroundColor(Color.parseColor("#" + objectModels.get(1).getColor()));
            }
            TextView textView3 = (TextView) progressView.findViewById(R.id.progressText4);
            ImageView imageView3 = (ImageView) progressView.findViewById(R.id.progressImage4);
            textView3.setText(objectModels.get(2).getStageName());
            if (objectModels.get(2).getColor().startsWith("#")) {
                imageView3.setBackgroundColor(Color.parseColor(objectModels.get(2).getColor()));
            } else {
                imageView3.setBackgroundColor(Color.parseColor("#" + objectModels.get(2).getColor()));
            }
        } else if (objectModels.size() == 4) {
            TextView textView1 = (TextView) progressView.findViewById(R.id.progressText1);
            ImageView imageView1 = (ImageView) progressView.findViewById(R.id.progressImage1);
            textView1.setText(objectModels.get(0).getStageName());
            if (objectModels.get(0).getColor().startsWith("#")) {
                imageView1.setBackgroundColor(Color.parseColor(objectModels.get(0).getColor()));
            } else {
                imageView1.setBackgroundColor(Color.parseColor("#" + objectModels.get(0).getColor()));
            }
            TextView textView2 = (TextView) progressView.findViewById(R.id.progressText2);
            ImageView imageView2 = (ImageView) progressView.findViewById(R.id.progressImage2);
            textView2.setText(objectModels.get(1).getStageName());
            if (objectModels.get(1).getColor().startsWith("#")) {
                imageView2.setBackgroundColor(Color.parseColor(objectModels.get(1).getColor()));
            } else {
                imageView2.setBackgroundColor(Color.parseColor("#" + objectModels.get(1).getColor()));
            }
            TextView textView3 = (TextView) progressView.findViewById(R.id.progressText3);
            ImageView imageView3 = (ImageView) progressView.findViewById(R.id.progressImage3);
            textView3.setText(objectModels.get(2).getStageName());
            if (objectModels.get(2).getColor().startsWith("#")) {
                imageView3.setBackgroundColor(Color.parseColor(objectModels.get(2).getColor()));
            } else {
                imageView3.setBackgroundColor(Color.parseColor("#" + objectModels.get(2).getColor()));
            }
            TextView textView4 = (TextView) progressView.findViewById(R.id.progressText4);
            ImageView imageView4 = (ImageView) progressView.findViewById(R.id.progressImage4);
            textView4.setText(objectModels.get(3).getStageName());
            if (objectModels.get(1).getColor().startsWith("#")) {
                imageView4.setBackgroundColor(Color.parseColor(objectModels.get(3).getColor()));
            } else {
                imageView4.setBackgroundColor(Color.parseColor("#" + objectModels.get(3).getColor()));
            }
        } else {
            linearLayout.setVisibility(View.GONE);
        }

        linearLayout.removeAllViews();
        linearLayout.addView(progressView);

    }
}
