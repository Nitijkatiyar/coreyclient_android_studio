package com.coremobile.coreyhealth.newui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.coremobile.coreyhealth.AppConfig;
import com.coremobile.coreyhealth.ContextData;
import com.coremobile.coreyhealth.CoreyDBHelper;
import com.coremobile.coreyhealth.DatabaseProvider;
import com.coremobile.coreyhealth.DetailedApplicationData;
import com.coremobile.coreyhealth.MsgDataCache;
import com.coremobile.coreyhealth.MultiSelectActivityCMN;
import com.coremobile.coreyhealth.MyApplication;
import com.coremobile.coreyhealth.R;
import com.coremobile.coreyhealth.TabDtl;
import com.coremobile.coreyhealth.Utils;
import com.coremobile.coreyhealth.WebViewActivityCMN;
import com.coremobile.coreyhealth.base.CMN_AppBaseActivity;
import com.coremobile.coreyhealth.errorhandling.CMN_ErrorMessages;
import com.coremobile.coreyhealth.googleAnalytics.CMN_Preferences;
import com.coremobile.coreyhealth.imageloader.ImageLoader;
import com.coremobile.coreyhealth.mypatients.CMN_MyPatientActivity;
import com.coremobile.coreyhealth.patientreminders.PatientRemindersActivityCMN;
import com.coremobile.coreyhealth.patientreminders.RemindersTypeActivityCMN;
import com.coremobile.coreyhealth.progressdb.ProgressInfoDBControl;
import com.coremobile.coreyhealth.progressdb.ProgressObjectModel;
import com.coremobile.coreyhealth.providerreminders.AddNewReminderActivityCMN;
import com.coremobile.coreyhealth.providerreminders.ProviderRemindersActivityCMN;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ChildIconActivityCMN extends CMN_AppBaseActivity {
    private static String TAG = "ChildIconActivityCMN";
    Intent mIntent;
    Calendar mcurrentTime;

    RowDisplayObject rowDisplayObject;
    public ArrayList<RowDisplayObject> mArrayList;
    ChildIconAdapter mChildIconAdapter;
    IntentFilter pushNotificationFilter;
    BroadcastReceiver pushNotificationReceiver;
    MyApplication application;
    GridView mChildIconGridview;
    String mAppName;
    int mMsgId = -1;
    CoreyDBHelper mCoreyDBHelper;
    String objectId = null;
    Boolean isPortrait;
    private SharedPreferences mCurrentUserPref;
    public static final String CURRENT_USER = "CurrentUser";
    private String organizationName, user_category;

    boolean Patient = false;
    public static ChildIconActivityCMN childIconActivity;
    int pos = -1;
    ProgressInfoDBControl progressInfoDBControl;
    TextView textView;
    private LayoutInflater inflater;

    private Map<Integer, IContextMenuItem> mMenuItemsMap; // menuId -> menuItem
    ArrayList<TabDtl> mTabDtl;

    private Calendar cal;
    private int hour;
    private int min;
    String dateTime;
    String title = "";

    MsgDataCache mMsgDataCache;
    ChildIconActivityCMN childIconActivityCMN;
    BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_icon);
        mCoreyDBHelper = new CoreyDBHelper(this);


        mMenuItemsMap = new LinkedHashMap<Integer, IContextMenuItem>();

        childIconActivity = this;
        mIntent = getIntent();
        progressInfoDBControl = new ProgressInfoDBControl(childIconActivity);
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

//        mTabDtl = jsonHelperClass.getTabDtl();
//        if (mTabDtl != null)
//            mTabCount = mTabDtl.size();
//        else
//            mTabCount = 0;

        TextView Row0View = (TextView) findViewById(R.id.text1);
        Row0View.setAutoLinkMask(0x04);
        Row0View.setMovementMethod(new ScrollingMovementMethod());
        ImageView Row0Image = (ImageView) findViewById(R.id.image);
        mcurrentTime = Calendar.getInstance();


        application = (MyApplication) getApplication();


        mCurrentUserPref = getSharedPreferences(CURRENT_USER, 0);
        user_category = mCurrentUserPref.getString("user_category", null);
        if (user_category.equalsIgnoreCase("patient")) {
            Patient = true;
        } else {
            Patient = false;

        }
        mChildIconGridview = (GridView) findViewById(R.id.gridview1);
        textView = (TextView) findViewById(R.id.nodata);
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            isPortrait = false;

        }
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            isPortrait = true;
        }


        if (mIntent.getExtras().get("ObjectsList") != null) {
            if (mIntent.hasExtra("pos")) {
                pos = mIntent.getIntExtra("pos", 0);
            }
            objectId = mIntent.getExtras().getString("ObjectsList");
            mMsgId = mIntent.getIntExtra("msgid", 1);


//            CMN_JsonConstants.msgId = ""+mMsgId;
//            String mRow0Content = mCoreyDBHelper.getPatientDetails();
            Row0View.setText(Html.fromHtml(CMN_Preferences.getrow0content(ChildIconActivityCMN.this)));
            String APP_PATH = MyApplication.INSTANCE.AppConstants.getAppFilesPath();
            String ImgUrl = mCoreyDBHelper.getObjImageUrlbyqdr(mMsgId, 1, "Calendar");
            if (ImgUrl != null && !ImgUrl.equalsIgnoreCase("") && !ImgUrl.isEmpty()) {
                Uri uri = Uri.parse(ImgUrl);
                ImgUrl = uri.getLastPathSegment();

                ImageLoader imgLoader = new ImageLoader(ChildIconActivityCMN.this);
                try {
                    imgLoader.DisplayImage(uri.toString(), R.drawable.analytic_placeholder,
                            Row0Image);
                } catch (Exception e) {
                    e.getMessage();
                }
            } else {
                Row0Image.setVisibility(View.GONE);

            }
        }

//        for (int i = 0; i < progressInfoDBControl.getProgressBarObjects().size(); i++) {
//            if (progressInfoDBControl.getProgressBarObjects().get(i).getObjectId().equalsIgnoreCase(objectId)) {
//                objectModels.add(progressInfoDBControl.getProgressBarObjects().get(i));
//            }
//        }
        List<ProgressObjectModel> objectModels;
        progressInfoDBControl = new ProgressInfoDBControl(childIconActivity);
        objectModels = progressInfoDBControl.getProgressBarObjects(objectId);
        View progressView = inflater.inflate(
                R.layout.progressview, null);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.progressFrame);
        addProgressbar();

        TextView emptyTextView = (TextView) findViewById(R.id.emptyView);
        emptyTextView.setText("No data");
        if (mChildIconAdapter != null
                && mChildIconAdapter.getCount() > 0)
            mChildIconGridview.setEmptyView(emptyTextView);
        if (mIntent.getStringExtra("objname") != null) {
            mAppName = mIntent.getStringExtra("objname");
        }

		/*   if(mArrayList!=null && mArrayList.size()>0){
               mAppName = mArrayList.get(0).getAppName();
		   } */
        if (TextUtils.isEmpty(mAppName))
            mAppName = "Corey";

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setTitle(mAppName);
            //    actionBar.setIcon(R.drawable.app_icon);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME
                    | ActionBar.DISPLAY_SHOW_TITLE
                    | ActionBar.DISPLAY_HOME_AS_UP);
            actionBar.show();

        }
        mChildIconGridview.setOnItemClickListener(mOnItemClickListner);


    }
/*
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.child_icon, menu);
		return true;
	} */

    public void refreshViews() {

//        List<ProgressObjectModel> objectModels = new ArrayList<>();
//        progressInfoDBControl = new ProgressInfoDBControl(childIconActivity);
//        for (int i = 0; i < progressInfoDBControl.getProgressBarObjects().size(); i++) {
//            if (progressInfoDBControl.getProgressBarObjects().get(i).getObjectId().equalsIgnoreCase(objectId)) {
//                objectModels.add(progressInfoDBControl.getProgressBarObjects().get(i));
//            }
//        }

        addProgressbar();
        if (mMsgId != -1 && objectId != null && childIconActivity != null) {
            mMsgDataCache = new MsgDataCache(childIconActivity);
            mMsgDataCache.populateData(mMsgId, DatabaseProvider.MSG_TYPE_INSTANT_GRATIFICATION);

            if (mArrayList == null) {
                mArrayList = new ArrayList<>();
            }
            List<RowDisplayObject> list = mMsgDataCache.getObject(objectId).getobjectlist();
            if (list == null) {
                list = new ArrayList<>();
            }
//        ArrayList<RowDisplayObject> ltc2 = new ArrayList<RowDisplayObject>();// unique
//        for (RowDisplayObject element : mArrayList) {
//            if (!ltc2.equals(element)) {
//                System.out.println(element);
//                ltc2.add(element);
//            }
//        }


            HashMap<String, RowDisplayObject> map = new HashMap<>();
            for (int i = 0; i < list.size(); i++) {
                map.put(list.get(i).getObjectId(), list.get(i));
            }
            mArrayList = new ArrayList<RowDisplayObject>(map.values());


            Collections.sort(mArrayList, new Utils.ObjectComparator());

            if (mCoreyDBHelper != null) {
                mChildIconAdapter = new ChildIconAdapter(this, mChildIconGridview, R.layout.child_iconlist, mArrayList, mMsgId, mCoreyDBHelper.getListItemObjMap(mMsgId));
                mChildIconGridview.setAdapter(mChildIconAdapter);
            }

        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        //emr.notifyDataSetChanged();
        childIconActivity = this;
        refreshViews();

        IntentFilter filter = new IntentFilter(getPackageName() + ".update");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "pushNotificationReceiver Data download complete");
                if (!mCurrentUserPref.getBoolean("UserSignedIn", false)) {
                    Log.d(TAG,
                            "pushNotificationReceiver user not signed in returning");
                    return;
                }
                String status = intent.getStringExtra("Status");
                Log.d(TAG, "status: " + status);
                if ("success".equalsIgnoreCase(status)) {
                    Log.d(TAG, "onReceive updateUI");
                    refreshViews();

                }
            }
        };
        registerReceiver(receiver, filter);


        if (mArrayList == null) MyApplication.resetApp(true);

        if (MyApplication.shouldResetApp()) {
            Toast.makeText(getApplicationContext(), CMN_ErrorMessages.getInstance().getValue(156), Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        childIconActivity = this;
    }

    OnItemClickListener mOnItemClickListner = new OnItemClickListener() {


        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1,
                                int arg2, long arg3) {


            if (mArrayList.get(arg2).isParent) {
                pos = arg2;
                showSubLevelList(mArrayList.get(arg2).getObjectId(), mArrayList.get(arg2).getDisplayName(), pos);
            } else if (mArrayList.get(arg2).getObjType().equalsIgnoreCase("Switch")) {
                if (CMN_Preferences.getNexttimeToUpdate(ChildIconActivityCMN.this) <= System.currentTimeMillis()) {

                    rowDisplayObject = mArrayList.get(arg2);
                    title = mArrayList.get(arg2).getDisplayName() + "?";

                    organizationName = mCurrentUserPref.getString("Organization", null);

                    CMN_Preferences.setisOneTouch(ChildIconActivityCMN.this, true);

                    if (!mArrayList.get(arg2).getListValue().equalsIgnoreCase("YesNoStatus")) {
                        if (Boolean.parseBoolean(mArrayList.get(arg2).getsNeedTimeStamp()) && !mArrayList.get(arg2).getObjectStatus().equalsIgnoreCase("Completed")) {
                            showDialog(0);
                        } else {
                            showalertDialogue(title, "Reset", rowDisplayObject);
                        }
                    } else {
                        if (mArrayList.get(arg2).getObjectStatus().equalsIgnoreCase("NO")) {
                            showalertDialogue(title, "Confirm", rowDisplayObject);
                        } else {
                            showalertDialogue(title, "Reset", rowDisplayObject);
                        }
                    }
                    Log.d("Switch", "Click");
                } else {
//                    Toast.makeText(ChildIconActivityCMN.this, CMN_ErrorMessages.getInstance().getValue(131) + " for " + (CMN_Preferences.getNexttimeToUpdate(ChildIconActivityCMN.this) - System.currentTimeMillis()) / 1000 + " Seconds to update", Toast.LENGTH_SHORT).show();
                }
            } else if (mArrayList.get(arg2).getObjectId().equalsIgnoreCase("122")) {
                if (Patient) {
                    Intent myIntent = new Intent(ChildIconActivityCMN.this, PatientRemindersActivityCMN.class);
                    startActivity(myIntent);
                } else {
                    showPopup(ChildIconActivityCMN.this);
                }
            } else if (mArrayList.get(arg2).getObjectId().equalsIgnoreCase("56")) {
                Intent myIntent = new Intent(ChildIconActivityCMN.this, RemindersTypeActivityCMN.class);
                startActivity(myIntent);
            } else {
                String openUrl = mArrayList.get(arg2).getOpenUrl();
                boolean isMultiselect = mArrayList.get(arg2).IsMultiselecUi();
                Log.d(TAG, "isMultiselect=" + isMultiselect);
                Log.d(TAG, "opwnUrl =" + openUrl);
                if (openUrl != null && !openUrl.isEmpty()) {
                    if (mArrayList.get(arg2).getIsNew() != null) {
                        mArrayList.get(arg2).setIsNew("false");
                        String ObjId = mArrayList.get(arg2).getObjectid();
                        mCoreyDBHelper.updateObjIsNew(mMsgId, ObjId, "false");
                    }
                    Intent urlIntent = new Intent(ChildIconActivityCMN.this,
                            WebViewActivityCMN.class);
                    urlIntent.putExtra("myurl", openUrl);
                    urlIntent.putExtra("objname", mArrayList.get(arg2).getDisplayName());
                    startActivity(urlIntent);
                } else if (isMultiselect) {
                    if (mArrayList.get(arg2).getIsNew() != null) {
                        mArrayList.get(arg2).setIsNew("false");
                        String ObjId = mArrayList.get(arg2).getObjectid();
                        mCoreyDBHelper.updateObjIsNew(mMsgId, ObjId, "false");
                    }
                    Intent myIntent = new Intent(ChildIconActivityCMN.this, MultiSelectActivityCMN.class);
                    String listitemname = mArrayList.get(arg2).getListValue();
                    Log.d(TAG, "object name=" + mArrayList.get(arg2).getDisplayName());
                    Log.d(TAG, "listitemname=" + listitemname);
                    myIntent.putExtra("listvaluename", listitemname);
                    myIntent.putExtra("title", mArrayList.get(arg2).getDisplayName());
                    myIntent.putExtra("fieldId", "");
                    myIntent.putExtra("ObjID", mArrayList.get(arg2).getObjectid());
                    myIntent.putExtra("listid", mArrayList.get(arg2).getAppId());//redundant
                    myIntent.putExtra("msgid", mMsgId);
                    // launch intent
                    startActivity(myIntent);
                } else {
                    if (mArrayList.get(arg2).getIsNew() != null) {
                        mArrayList.get(arg2).setIsNew("false");
                        String ObjId = mArrayList.get(arg2).getObjectid();
                        mCoreyDBHelper.updateObjIsNew(mMsgId, ObjId, "false");
                    }
                    Intent detailedIntent = new Intent(
                            ChildIconActivityCMN.this,
                            DetailedApplicationData.class);
                    detailedIntent.putExtra("ObjID", mArrayList
                            .get(arg2).getObjectid());
                    detailedIntent.putExtra("ObjName", mArrayList.get(arg2)
                            .getDisplayName());
                    detailedIntent.putExtra("ObjID", mArrayList.get(arg2).getObjectid());
                    detailedIntent.putExtra("listid", mArrayList.get(arg2).getAppId());//redundant
                    detailedIntent.putExtra("msgid", mMsgId);
                    detailedIntent.putExtra("IsEditable", mArrayList.get(arg2).isEditable);
                    detailedIntent.putExtra("msgid", mMsgId);
                    detailedIntent.putExtra("position", arg2);
                    startActivity(detailedIntent);
                }
            }
        }
    };

    private void showPopup(final Activity context) {

        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);

        View popupView = layoutInflater.inflate(R.layout.reminder_popup_window, null);

        final PopupWindow popup = new PopupWindow(popupView,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT,
                true);


        popup.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        Button addReminder = (Button) popupView.findViewById(R.id.buttonAddNewReminder);
        addReminder.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChildIconActivityCMN.this, AddNewReminderActivityCMN.class));
                popup.dismiss();
            }
        });

        Button editReminder = (Button) popupView.findViewById(R.id.buttonEditReminder);
        editReminder.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChildIconActivityCMN.this, ProviderRemindersActivityCMN.class);
                intent.putExtra("isEdit", true);
                startActivity(intent);
                popup.dismiss();
            }
        });

        Button useReminder = (Button) popupView.findViewById(R.id.buttonUseCreatedReminder);
        useReminder.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChildIconActivityCMN.this, ProviderRemindersActivityCMN.class);
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


    private void showSubLevelList(String ObjID, String Objname, int pos) {


        Intent detailedIntent = new Intent(ChildIconActivityCMN.this, ChildIconActivityCMN.class);
        detailedIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        detailedIntent.putExtra("ObjectsList", ObjID);
        detailedIntent.putExtra("msgid", mMsgId);
        detailedIntent.putExtra("objname", Objname);
        detailedIntent.putExtra("pos", pos);
        startActivity(detailedIntent);
    }

    public JSONObject getRequestJson(RowDisplayObject object, String datetosend) {
        JSONObject object1 = new JSONObject();
        Log.d("objectContext", "" + object.getMsgContext());
        try {
            if (Patient) {
                object1.put("Context", "" + mCurrentUserPref.getString("context", null));
            } else {
                object1.put("Context", "" + object.getMsgContext());
            }
            object1.put("ApplicationName", "" + object.getAppName());
            object1.put("AppID", "" + object.getAppId());
            object1.put("Action", "Replace");
            object1.put("ObjectName", "" + object.getObjectName());
            object1.put("ObjectID", "" + object.getObjectId());

            JSONArray jsonArray = new JSONArray();

            JSONObject object2 = new JSONObject();
            if (object.getObjectStatus() != null && object.getListValue().equalsIgnoreCase("YesNoStatus")) {
                if (object.getObjectStatus() != null && object.getObjectStatus().equalsIgnoreCase("YES")) {
                    object2.put("FieldValue", "NO");
                } else {
                    object2.put("FieldValue", "YES");
                }
            } else {

                if (object.getObjectStatus() != null && object.getObjectStatus().equalsIgnoreCase("Completed")) {
                    object2.put("FieldValue", "Not Started");
                } else {
//                    object2.put("FieldValue", "Completed");
                    object2.put("FieldValue", datetosend);
                }
            }
            jsonArray.put(object2);
            Log.d("data", "" + object2);

            object1.put("Data", jsonArray);
        } catch (JSONException e) {
            e.getMessage();
        }
        Log.d("data", "" + object1);

        return object1;
    }

    private void addProgressbar() {
        List<ProgressObjectModel> objectModels;
        progressInfoDBControl = new ProgressInfoDBControl(childIconActivity);
        objectModels = progressInfoDBControl.getProgressBarObjects(objectId);
        View progressView = inflater.inflate(
                R.layout.progressview, null);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.progressFrame);
        if (objectModels.size() == 1) {
            ProgressObjectModel progressObjectModel1 = objectModels.get(0);
            progressView.findViewById(R.id.progress2).setVisibility(View.GONE);
            progressView.findViewById(R.id.progress3).setVisibility(View.GONE);
            progressView.findViewById(R.id.progress4).setVisibility(View.GONE);
            TextView textView_progress1 = (TextView) progressView.findViewById(R.id.progressText1);
            ImageView imageView1 = (ImageView) progressView.findViewById(R.id.progressImage1);
            String titleBar = "";
            if (progressObjectModel1 != null && progressObjectModel1.getStageName() != null) {
                titleBar = progressObjectModel1.getStageName();
            }
            textView_progress1.setText("" + titleBar);
            if (objectModels.get(0).getColor().startsWith("#")) {
                imageView1.setBackgroundColor(Color.parseColor(objectModels.get(0).getColor()));
            } else {
                imageView1.setBackgroundColor(Color.parseColor("#" + objectModels.get(0).getColor()));
            }

        } else if (objectModels.size() == 2) {
            ProgressObjectModel progressObjectModel1 = objectModels.get(0);
            ProgressObjectModel progressObjectModel2 = objectModels.get(1);
            progressView.findViewById(R.id.progress2).setVisibility(View.GONE);
            progressView.findViewById(R.id.progress3).setVisibility(View.GONE);
            TextView textView_progress1 = (TextView) progressView.findViewById(R.id.progressText1);
            ImageView imageView1 = (ImageView) progressView.findViewById(R.id.progressImage1);
            String titleBar = "";
            if (progressObjectModel1 != null && progressObjectModel1.getStageName() != null) {
                titleBar = progressObjectModel1.getStageName();
            }
            textView_progress1.setText("" + titleBar);
            if (objectModels.get(0).getColor().startsWith("#")) {
                imageView1.setBackgroundColor(Color.parseColor(objectModels.get(0).getColor()));
            } else {
                imageView1.setBackgroundColor(Color.parseColor("#" + objectModels.get(0).getColor()));
            }
            TextView textView_progress2 = (TextView) progressView.findViewById(R.id.progressText4);
            ImageView imageView2 = (ImageView) progressView.findViewById(R.id.progressImage4);
            String titleBar1 = "";
            if (progressObjectModel2 != null && progressObjectModel2.getStageName() != null) {
                titleBar1 = progressObjectModel2.getStageName();
            }
            textView_progress2.setText("" + titleBar1);
            if (objectModels.get(1).getColor().startsWith("#")) {
                imageView2.setBackgroundColor(Color.parseColor(objectModels.get(1).getColor()));
            } else {
                imageView2.setBackgroundColor(Color.parseColor("#" + objectModels.get(1).getColor()));
            }
        } else if (objectModels.size() == 3) {
            progressView.findViewById(R.id.progress3).setVisibility(View.GONE);
            TextView textView_progress1 = (TextView) progressView.findViewById(R.id.progressText1);
            ImageView imageView1 = (ImageView) progressView.findViewById(R.id.progressImage1);
            textView_progress1.setTextKeepState(objectModels.get(0).getStageName());
            if (objectModels.get(0).getColor().startsWith("#")) {
                imageView1.setBackgroundColor(Color.parseColor(objectModels.get(0).getColor()));
            } else {
                imageView1.setBackgroundColor(Color.parseColor("#" + objectModels.get(0).getColor()));
            }
            TextView textView_progress2 = (TextView) progressView.findViewById(R.id.progressText2);
            ImageView imageView2 = (ImageView) progressView.findViewById(R.id.progressImage2);
            textView_progress2.setText(objectModels.get(1).getStageName());
            if (objectModels.get(1).getColor().startsWith("#")) {
                imageView2.setBackgroundColor(Color.parseColor(objectModels.get(1).getColor()));
            } else {
                imageView2.setBackgroundColor(Color.parseColor("#" + objectModels.get(1).getColor()));
            }
            TextView textView_progress3 = (TextView) progressView.findViewById(R.id.progressText4);
            ImageView imageView3 = (ImageView) progressView.findViewById(R.id.progressImage4);
            textView_progress3.setText(objectModels.get(2).getStageName());
            if (objectModels.get(2).getColor().startsWith("#")) {
                imageView3.setBackgroundColor(Color.parseColor(objectModels.get(2).getColor()));
            } else {
                imageView3.setBackgroundColor(Color.parseColor("#" + objectModels.get(2).getColor()));
            }
        } else if (objectModels.size() == 4) {
            TextView textView_progress1 = (TextView) progressView.findViewById(R.id.progressText1);
            ImageView imageView1 = (ImageView) progressView.findViewById(R.id.progressImage1);
            textView_progress1.setText(objectModels.get(0).getStageName());
            if (objectModels.get(0).getColor().startsWith("#")) {
                imageView1.setBackgroundColor(Color.parseColor(objectModels.get(0).getColor()));
            } else {
                imageView1.setBackgroundColor(Color.parseColor("#" + objectModels.get(0).getColor()));
            }
            TextView textView_progress2 = (TextView) progressView.findViewById(R.id.progressText2);
            ImageView imageView2 = (ImageView) progressView.findViewById(R.id.progressImage2);
            textView_progress2.setText(objectModels.get(1).getStageName());
            if (objectModels.get(1).getColor().startsWith("#")) {
                imageView2.setBackgroundColor(Color.parseColor(objectModels.get(1).getColor()));
            } else {
                imageView2.setBackgroundColor(Color.parseColor("#" + objectModels.get(1).getColor()));
            }
            TextView textView_progress3 = (TextView) progressView.findViewById(R.id.progressText3);
            ImageView imageView3 = (ImageView) progressView.findViewById(R.id.progressImage3);
            textView_progress3.setText(objectModels.get(2).getStageName());
            if (objectModels.get(2).getColor().startsWith("#")) {
                imageView3.setBackgroundColor(Color.parseColor(objectModels.get(2).getColor()));
            } else {
                imageView3.setBackgroundColor(Color.parseColor("#" + objectModels.get(2).getColor()));
            }
            TextView textView_progress4 = (TextView) progressView.findViewById(R.id.progressText4);
            ImageView imageView4 = (ImageView) progressView.findViewById(R.id.progressImage4);
            textView_progress4.setText(objectModels.get(3).getStageName());
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

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
        childIconActivity = null;
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

        MenuItem item1 = menu.findItem(R.id.action_home);
        item1.setVisible(false);

        MenuItem item2 = menu.findItem(R.id.action_patients);
        if (AppConfig.isAppCoreyHealth) {
            item2.setVisible(true);
        } else {
            item2.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if(item.getItemId()==R.id.action_patients){
            Intent intent=new Intent(MessageTabActivityCMN.getInstance(),CMN_MyPatientActivity.class);
            startActivity(intent);
        }else if (mMenuItemsMap.containsKey(item.getItemId())) {
            mMenuItemsMap.get(item.getItemId()).onClick();
        }
        return super.onOptionsItemSelected(item);


    }


    public void showalertDialogue(String title, String button1text, final RowDisplayObject rowDisplayObject) {
        new AlertDialog.Builder(ChildIconActivityCMN.this)
                .setTitle(title)
                .setMessage("Do you want to continue?")
                .setPositiveButton(button1text, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (CMN_Preferences.getNexttimeToUpdate(ChildIconActivityCMN.this) <= System.currentTimeMillis()) {
                            UpdateApplicationdatamultipleWebService updateApplicationdatamultipleWebService = new UpdateApplicationdatamultipleWebService(ChildIconActivityCMN.this, rowDisplayObject, mChildIconAdapter);
                            updateApplicationdatamultipleWebService.execute("" + getRequestJson(rowDisplayObject, ""));
                        } else {
                            Toast.makeText(ChildIconActivityCMN.this, CMN_ErrorMessages.getInstance().getValue(131) + " for " + (CMN_Preferences.getNexttimeToUpdate(ChildIconActivityCMN.this) - System.currentTimeMillis()) / 1000 + " Seconds to update", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    @Deprecated
    protected Dialog onCreateDialog(int id) {
//          mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int min = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog dpd = new TimePickerDialog(this, timePickerListener, hour, min, false);
        dpd.setTitle(title);
        dpd.setButton(DatePickerDialog.BUTTON_POSITIVE, "Confirm", dpd);
        dpd.setButton(DatePickerDialog.BUTTON_NEGATIVE, "Cancel", dpd);
        return dpd;
    }

    private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            int hour;

            hour = hourOfDay;

            String h = "" + hour, m = "" + minute;
            if (hour < 10) {
                h = "0" + hour;
            }
            if (minute < 10) {
                m = "0" + minute;
            }
            dateTime = "" + h + ":" + m;


            Calendar c = Calendar.getInstance();
            System.out.println("Current time => " + c.getTime());

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = df.format(c.getTime());


            String datetosend = Utils.converttime2utc(formattedDate + " " + dateTime);
            Log.d("LocalTime", "" + formattedDate + " " + dateTime);
            Log.d("UTCTime", "" + datetosend);

            if (CMN_Preferences.getNexttimeToUpdate(ChildIconActivityCMN.this) <= System.currentTimeMillis()) {
                UpdateApplicationdatamultipleWebService updateApplicationdatamultipleWebService = new UpdateApplicationdatamultipleWebService(ChildIconActivityCMN.this, rowDisplayObject, mChildIconAdapter);
                updateApplicationdatamultipleWebService.execute("" + getRequestJson(rowDisplayObject, datetosend));
            } else {
                Toast.makeText(ChildIconActivityCMN.this, CMN_ErrorMessages.getInstance().getValue(131) + " for " + (CMN_Preferences.getNexttimeToUpdate(ChildIconActivityCMN.this) - System.currentTimeMillis()) / 1000 + " Seconds to update", Toast.LENGTH_SHORT).show();
            }
        }
    };


}
