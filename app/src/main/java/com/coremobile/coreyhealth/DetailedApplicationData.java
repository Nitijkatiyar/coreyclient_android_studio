package com.coremobile.coreyhealth;

import android.app.ActionBar;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coremobile.coreyhealth.Checkfornotification.CMN_FieldAPIWebService;
import com.coremobile.coreyhealth.aesencryption.CMN_AES;
import com.coremobile.coreyhealth.base.CMN_AppListBaseActivity;
import com.coremobile.coreyhealth.clientcertificate.CMN_HTTPSClient;
import com.coremobile.coreyhealth.googleAnalytics.CMN_Preferences;
import com.coremobile.coreyhealth.imageloader.ImageLoader;
import com.coremobile.coreyhealth.newui.Fields;
import com.coremobile.coreyhealth.newui.MessageTabActivityCMN;
import com.coremobile.coreyhealth.newui.MsgScheduleTabActivity;
import com.coremobile.coreyhealth.newui.WebViewDialogHelper;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.coremobile.coreyhealth.MultiSelectActivityCMN.listid;
import static com.coremobile.coreyhealth.newui.MessageTabActivityCMN.activity;


public class DetailedApplicationData extends CMN_AppListBaseActivity implements
        OnClickListener, IPullDataFromServer {
    private static String TAG = "Corey_DetailedApplicationData";
    public static final String CURRENT_USER = "CurrentUser";
    MessageListActivityCMN mMessalistActivity;
    ListView mListView;
    Timer UpdateTimer;
    // private MyApplication application;
    //   String organizationName;
    ArrayList<ListItem> mListItemArray;
    Intent mIntent;
    IntentFilter mUpdateCompleteFilter;
    //   BroadcastReceiver UpdateCompleteReceiver;
    String ObjId;
    ArrayList<Fields> mFArrayList;
    List<Integer> mTracklist;
    ArrayList<String> mFieldsArrayList = new ArrayList<String>();
    HashMap<String, String> mStatusColorHashMap = new HashMap<String, String>();
    HashMap<String, ListItemObj> ListItemObjMap = new HashMap<String, ListItemObj>();
    DetailedApplicationAdapter mDetailedApplicationAdapter;
    MyApplication application;
    String mAppName;
    private final String openmailIntent = "Mail";
    String mAppTitle;
    String mObjectName;
    String mObjName;
    String mOpportunityName;
    boolean isEditable;
    private int mMsgId;
    String mAppContext;
    String mMsgContext;
    private ProgressDialog mProgressDialog;
    String mUpdateObjid;
    String mFieldId;
    String mAppId;
    String mRow0Content;
    String mAssignedUsrId;
    private String mOrganization;
    private IntentFilter mPushNotificationFilter;
    ArrayList<String> mannschaftsnamen = new ArrayList<String>();
    ArrayList<String> updatedContents = new ArrayList<String>();
    ArrayList<String> origContents = new ArrayList<String>();
    CoreyDBHelper mCoreyDBHelper;
    private boolean isCorefyInProgress;
    Boolean mAutosync;
    private ArrayList<String> mStatusArrayList;
    int mOnClickPosition, parentPosition;
    String selectedItems = "";
    String selectedreasons = "";

    @SuppressWarnings("unchecked")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailedapplicationlayout);
        mListView = getListView();
        mIntent = getIntent();
        //application = (MyApplication) getApplication();;
        TextView emptyTextView = (TextView) findViewById(R.id.empty);
        emptyTextView.setGravity(Gravity.CENTER_VERTICAL);
        emptyTextView.setText("No data to display");
        mProgressDialog = new ProgressDialog(DetailedApplicationData.this);
        MessageTabActivityCMN.isReturned = true;
        mCoreyDBHelper = new CoreyDBHelper(this);
        mTracklist = new ArrayList();
        SharedPreferences mcurrentUserPref = getSharedPreferences(
                CURRENT_USER, 0);
        mAutosync = mcurrentUserPref.getBoolean("RequireAutoSync", false);
        //   organizationName = mcurrentUserPref.getString("Organization", null);
        if (mIntent.getExtras().get("ObjID") != null) {
            //  ObjId = mIntent.getExtras().get("ObjID").toString();
            ObjId = mIntent.getStringExtra("ObjID");
            parentPosition = mIntent.getIntExtra("position", 0);
            String Appname = MyApplication.INSTANCE.AppConstants.getAppName();
            Appname = "MERITUS";
            if (Appname.equalsIgnoreCase("MERITUS") && ObjId.equalsIgnoreCase("90"))
                PopulateObject(ObjId, false);
            if (Appname.equalsIgnoreCase("MERITUS") && ObjId.equalsIgnoreCase("91"))
                PopulateEditObject(ObjId, true);
            //.getExtras().get("ObjID").toString();
            int mesid = mIntent.getIntExtra("msgid", 1);
            mMsgId = MsgScheduleTabActivity.getCache().getMsgId();
            ////Log.d(TAG, "mesid=" + mesid);
            ////Log.d(TAG, "mMsgId=" + mMsgId);
            //  mCoreyDBHelper=new CoreyDBHelper(this);
        /*
         * Display Row0
	     */
//            CMN_JsonConstants.msgId = "" + mMsgId;
//            mRow0Content = mCoreyDBHelper.getPatientDetails();

            TextView Row0View = (TextView) findViewById(R.id.text1);
            Row0View.setAutoLinkMask(0x04);
            Row0View.setMovementMethod(new ScrollingMovementMethod());
            ImageView Row0Image = (ImageView) findViewById(R.id.image);
            Button Locationbutton = (Button) findViewById(R.id.location);
            Button Searchbutton = (Button) findViewById(R.id.search);
            Appname = "MERITUS";

            if (Appname.equalsIgnoreCase("MERITUS") && ObjId.equalsIgnoreCase("90")) {

                Searchbutton.setVisibility(View.VISIBLE);
                Searchbutton.setText("Confirm");
            }
            if (Appname.equalsIgnoreCase("MERITUS") && ObjId.equalsIgnoreCase("91")) {
                Locationbutton.setVisibility(View.VISIBLE);
                Searchbutton.setVisibility(View.VISIBLE);
            }
            ////Log.d(TAG, "mMsgId= " + mMsgId);
            ////Log.d(TAG, "mRow0Content= " + mRow0Content);
            Row0View.setText(Html.fromHtml(CMN_Preferences.getrow0content(DetailedApplicationData.this)));
            String APP_PATH = MyApplication.INSTANCE.AppConstants.getAppFilesPath();
            Boolean imgurlpresent = false;
            //  String ImgUrl = mCoreyDBHelper.getObjImageUrl(mMsgId, "PatientData", "Calendar");
            String ImgUrl = mCoreyDBHelper.getObjImageUrlbyqdr(mMsgId, 1, "Calendar");
            if (ImgUrl != null && !ImgUrl.equalsIgnoreCase("") && !ImgUrl.isEmpty()) {
                imgurlpresent = true;
                Uri uri = Uri.parse(ImgUrl);
                ImageLoader imgLoader = new ImageLoader(DetailedApplicationData.this);
                try {
                    imgLoader.DisplayImage(uri.toString(), R.drawable.analytic_placeholder,
                            Row0Image);
                } catch (Exception e) {
                    //e.getMessage();
                }
            } else {
                Row0Image.setVisibility(View.GONE);

            }
        /*
         * row0 icon read from server end
		 */
            //    int imageid= MyApplication.INSTANCE.AppConstants.getRow0DrawableId();
            //    Row0Image.setImageDrawable(getResources().getDrawable(imageid));
        /*
         * Display dot after row0
	     */
            ImageView Row0Dot = (ImageView) findViewById(R.id.imagedot);
            Row0Dot.setImageDrawable(getResources().getDrawable(
                    R.drawable.smallcircle_red));

            List<Fields> fieldlist = MsgScheduleTabActivity.getCache().getObject(ObjId)
                    .getField();


            HashMap<String, Fields> fieldmap = new HashMap<>();
            for (int i = 0; i < fieldlist.size(); i++) {
                fieldmap.put("" + fieldlist.get(i).getFieldId() + "" + fieldlist.get(i).getFieldValue(), fieldlist.get(i));
            }
            mFArrayList = new ArrayList<Fields>(fieldmap.values());

            Collections.sort(mFArrayList, new Utils.FieldsComparator());
//            Collections.reverse(mFArrayList);

            List<Fields> noteslist = new ArrayList<>();
            if (fieldlist.size() > 0) {
                String fieldid = fieldlist.get(0).getFieldId();
                for (int i = 1; i < fieldlist.size(); i++) {
                    if (fieldlist.get(i).getFieldId().equalsIgnoreCase(fieldid) || fieldlist.get(i).getFieldName().equalsIgnoreCase("Note")) {
                        noteslist.add(fieldlist.get(i));
                        mFArrayList.remove(fieldlist.get(i));
                    } else {
                        fieldid = fieldlist.get(i).getFieldId();
                    }
                }
                Collections.sort(noteslist, new Utils.FieldsComparator());
                Collections.reverse(noteslist);
                mFArrayList.addAll(noteslist);

            }
            mAppTitle = MsgScheduleTabActivity.getCache().getObject(ObjId).getAppTitle();
            if (mIntent.hasExtra("ObjName")) {
                mObjectName = mIntent.getStringExtra("ObjName");
            }

            mObjName = MsgScheduleTabActivity.getCache().getObject(ObjId).getObjectName();//This is the actual object name
            mAssignedUsrId = MsgScheduleTabActivity.getCache().getObject(ObjId).getAssignedUserId();
            if (Appname.equalsIgnoreCase("MERITUS") && ObjId.equalsIgnoreCase("90")) {
                isEditable = false;
            } else if (Appname.equalsIgnoreCase("MERITUS") && ObjId.equalsIgnoreCase("90")) {
                isEditable = true;
            } else isEditable = mIntent.getBooleanExtra("IsEditable", false);
            mMsgContext = MsgScheduleTabActivity.getCache().getObject(ObjId).getMsgContext();
            mAppId = MsgScheduleTabActivity.getCache().getObject(ObjId).getAppId();
            ////Log.i(TAG, "Fieldarray" + mFArrayList);
            if (mFArrayList != null && mFArrayList.size() > 0) {
                mAppContext = mFArrayList.get(0).getMsgContext();
                mAppName = mFArrayList.get(0).getAppName();

                for (int i = 0; i < mFArrayList.size(); i++) {   //to get the salesforce Id here- needed while updating
                    if (mFArrayList.get(i).getFieldName() != null
                            && mFArrayList.get(i).getFieldName()
                            .equalsIgnoreCase("id")) {
                        mUpdateObjid = mFArrayList.get(i).getFieldValue();
                        break;
                    }
                }
                for (int i = 0; i < mFArrayList.size(); i++) {   //to get the salesforce Opportunity Name here- needed while updating in db
                    if (mFArrayList.get(i).getFieldName() != null
                            && mFArrayList.get(i).getFieldName()
                            .equalsIgnoreCase("Name")) {
                /*	if(mAutosync)
                    {
			    		mFArrayList.remove(i);
			    	}
			    	else
			    	{ */
                        mOpportunityName = mFArrayList.get(i).getFieldValue();
                        break;
                        //	}
                    }
                }

            } else {
                mListView.setEmptyView(emptyTextView);
            }


            if (TextUtils.isEmpty(mAppName))
                mAppName = "Corey";

            if (isEditable) {
                if (!(Appname.equalsIgnoreCase("MERITUS") && ObjId.equalsIgnoreCase("91"))) {
                    ActionBar actionBar = getActionBar();
                    if (actionBar != null) {
                        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP
                                | ActionBar.DISPLAY_SHOW_CUSTOM
                                | ActionBar.DISPLAY_SHOW_HOME);


                        actionBar.setCustomView(R.layout.action_save);

                        actionBar.setTitle(mObjectName);
                        View actionBarCustomView = actionBar.getCustomView();
                        if (actionBarCustomView != null) {
                            Button save = (Button) actionBarCustomView
                                    .findViewById(R.id.button2);//
                            save.setText("SEND");
                            //	save.setBackgroundColor(Color.GREEN);

                            save.setBackgroundColor(getResources().getColor(R.color.dark_green));
                            save.setVisibility(View.VISIBLE);
                            // save.setOnClickListener(DetailedApplicationData.this);
                            TextView appTextView = (TextView) actionBarCustomView
                                    .findViewById(R.id.apptextView1);
                            appTextView.setVisibility(View.VISIBLE);
                            //	appTextView.setText(mAppName);
                            appTextView.setText(mObjectName);
                        }
                    }
                }

            } else {

                ActionBar actionBar = getActionBar();
                if (actionBar != null) {
                    //		if(mAppName.equals("Outlook"))
                    //	{

                    //		actionBar.setTitle(mAppTitle);
                    //	}
                    //	else
                    //	{
                    actionBar.setTitle(mObjectName);
                    //	}
                    //    actionBar.setIcon(R.drawable.app_icon);
                    actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME
                            | ActionBar.DISPLAY_SHOW_TITLE
                            | ActionBar.DISPLAY_HOME_AS_UP);
                    actionBar.show();
                }

            }
        /*
         * Reset the Isnew flag so that dashboard will not highlight
	     */
            mCoreyDBHelper.updateObjIsNew(mMsgId, ObjId, "false");

            Map<String, Fields> map = new LinkedHashMap<String, Fields>();  // to avoid duplicates- server was sending
            for (Fields ays : mFArrayList) {
                String key = ays.getFieldName();
                if (!map.containsKey(key))
                    map.put(key, ays);
                mFieldsArrayList.add(ays.getFieldValue());
                ////Log.i(TAG, "Fieldnames" + key);
                ////Log.i(TAG, "Fieldvalues= " + ays.getFieldValue());
            }

            if (map.size() == 0) {
                for (Fields ays : mFArrayList) {
                    if (!ays.getFieldValue().isEmpty())
                        map.put(ays.getFieldDispText(), ays);
                }
            }

            //   mFArrayList.clear();
            //   mFArrayList.addAll(map.values());
        /*
         * Populate the ListItemObjMap
	     */
            if (MyApplication.INSTANCE.pullInProgress) {
                Toast.makeText(DetailedApplicationData.this,
                        "Message downloading in progress. try again", Toast.LENGTH_LONG).show();
                ////Log.d(TAG, "On create :going to finish the activity now");
                mPushNotificationReceiver = null;
                finish();

                return;
            }
            getListItemObjMap();

            if (mFArrayList != null && mFArrayList.size() > 0) {
                mDetailedApplicationAdapter = new DetailedApplicationAdapter(
                        DetailedApplicationData.this,
                        R.layout.patientdetailadapter, mFArrayList, isEditable, mOrganization, ListItemObjMap, this, ObjId, mMsgId);
                mListView.setAdapter(mDetailedApplicationAdapter);

            }

        }

        if (mDetailedApplicationAdapter != null
                && mDetailedApplicationAdapter.getCount() > 0)
            mListView.setEmptyView(emptyTextView);

        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                String openUrl = mFArrayList.get(arg2).getOpenUrl();
                String fieldvalue = mFArrayList.get(arg2).getFieldValue();
                String fieldnme = mFArrayList.get(arg2).getFieldDispText();
                String fieldtype = mFArrayList.get(arg2).getFieldType();
                String imageurl = mFArrayList.get(arg2).getImageUrl();
                Boolean isEdit = mFArrayList.get(arg2).getEditable();
                ////Log.d(TAG, "openUrl =" + openUrl);
                ////Log.d(TAG, "fieldvalue =" + fieldvalue);
                ////Log.d(TAG, "fieldname= " + fieldnme);
                if (fieldtype != null && fieldtype.equalsIgnoreCase("API")) {
                    new CMN_FieldAPIWebService(DetailedApplicationData.this).execute("" + mFArrayList.get(arg2).getOpenUrl());
                } else if (fieldtype != null && fieldtype.equalsIgnoreCase("GenericView")) {
                    Intent urlIntent = new Intent(/*DetailedApplicationData.this,
                            CMN_ViewSurveyActivity.class*/);
                    urlIntent.setClassName("" + getPackageName(), "" + getPackageName() + ".genericviewandsurvey.CMN_ViewSurveyActivity");
                    urlIntent.putExtra("model", mFArrayList.get(arg2));
                    startActivity(urlIntent);
                } else if (fieldvalue != null && fieldvalue.contains("http")
                        || openUrl != null && openUrl.contains("http")) {
                    String displayText = mFArrayList.get(arg2).getFieldDispText();
                    if ("Google Glass".equalsIgnoreCase(displayText)) {
                        ////Log.d(TAG, ">>> Google Glass >>>");
                        new WebViewDialogHelper(isEditable, mDetailedApplicationAdapter.getNewNote(), DetailedApplicationData.this, new WebViewDialogHelper.IListener() {
                            @Override
                            public void onTextChanged(String text) {
                                ////Log.d(TAG, "WebViewDialog onTextChanged: " + text);
                                mDetailedApplicationAdapter.setNewNote(text);
                                mDetailedApplicationAdapter.notifyDataSetChanged();
                            }
                        }).showDialog(openUrl);
                    } else {
                        Intent urlIntent = new Intent(/*DetailedApplicationData.this,
                                WebViewActivityCMN.class*/);
                        urlIntent.setClassName("" + getPackageName(), "" + getPackageName() + ".WebViewActivityCMN");
            /*    urlIntent.putExtra("myurl", mFArrayList.get(arg2)
                .getFieldValue().isEmpty() ? mFArrayList.get(arg2)
				.getOpenUrl() : mFArrayList.get(arg2)
				.getFieldValue());
				*/
            /* fix for COREY-2052 : crash happens because openurl is null */
        /*	if (openUrl.endsWith("pdf"))
            {
				openUrl= "https://docs.google.com/viewer?url="+openUrl;
	        }*/
                        if (openUrl != null) {
                            if (!openUrl.isEmpty()) urlIntent.putExtra("myurl", openUrl);
                        } else if (fieldvalue != null) {
                            if (!fieldvalue.isEmpty()) urlIntent.putExtra("myurl", fieldvalue);
                        } else {
                            ////Log.d(TAG, "No urls present");
                        }
                        if (fieldnme != null) {
                            if (!fieldnme.isEmpty())
                                urlIntent.putExtra("objname", fieldnme);
                        }
        /*	    urlIntent.putExtra("myurl", mFArrayList.get(arg2)
                    .getOpenUrl().isEmpty() ? mFArrayList.get(arg2)
					.getFieldValue() : mFArrayList.get(arg2)
					.getOpenUrl());
	    */
                        startActivity(urlIntent);
                    }
                } /*else if (fieldnme.equals("AdminMessages")) {
                    ////Log.d(TAG, "Admin messages click enabled");
                    Intent MessagingIntent = new Intent(*//*DetailedApplicationData.this,
                            MessageActivityCMN.class*//*);
                    MessagingIntent.setClassName("" + getPackageName(), "" + getPackageName() + ".newui.MessageActivityCMN");
                    ////Log.d(TAG, "mAssignedUsrId" + mAssignedUsrId);
                    ////Log.d(TAG, "mMsgContext" + mMsgContext);
                    MessagingIntent.putExtra("usr", mAssignedUsrId);
                    MessagingIntent.putExtra("ctxt", mMsgContext);
                    MessagingIntent.putExtra("objid",  mIntent.getStringExtra("ObjID"));
                    startActivity(MessagingIntent);

                } */ else if (fieldtype.equalsIgnoreCase(openmailIntent)) {

                    String body = mFArrayList.get(arg2).getEmailBody();
                    String subject = mFArrayList.get(arg2).getEmailSubject();
                    Intent feedback = new Intent(Intent.ACTION_VIEW);
                    Uri data = Uri.parse("mailto:?subject=" + subject
                            + "&body=" + body + "&to="
                            + "");
                    feedback.setData(data);
                    startActivity(feedback);


                } else if (fieldtype.equalsIgnoreCase("Address")) {
                    if (mFArrayList.get(arg2).getFieldValue() != null) {

                        String address = mFArrayList.get(arg2).getFieldValue();
//                        String url = "http://maps.google.com/maps/place/" + address;
//                        Intent urlIntent = new Intent(/*DetailedApplicationData.this,
//                                WebViewActivityCMN.class*/);
//                        urlIntent.setClassName("" + getPackageName(), "" + getPackageName() + ".WebViewActivityCMN");
//                        urlIntent.putExtra("myurl", url);
//                        urlIntent.putExtra("objname", "Address");
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                Uri.parse("google.navigation:q=" + address));
                        startActivity(intent);


                    }
                } else if (!isEdit) {
                    Builder alert = new Builder(DetailedApplicationData.this);


                    alert.setTitle(mFArrayList.get(arg2).getFieldDispText());

                    alert.setMessage(mFArrayList.get(arg2).getFieldValue().replace("\\n", "\n"));
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

                if (isEditable) // corey-2159 break and raised corey-2256.  When not in editable node, edit should not be allowed
                {
                    if ((mFArrayList.get(arg2).isEditable)) {
                        if (mFArrayList.get(arg2).getlistValue() != null) {
                            String mListValue = mFArrayList.get(arg2).getlistValue();
                            Boolean oldver = false;
                            JSONHelperClass jsonHelperClass = new JSONHelperClass();
                            ListItemObj listitemobj = ListItemObjMap.get(mListValue);
                            if (listitemobj == null) oldver = true;
                            if (!oldver) mListItemArray = listitemobj.ListItemArray;
                            mOnClickPosition = arg2;
                            Builder builder = new Builder(
                                    DetailedApplicationData.this);

                            if (oldver) {
                                builder.setAdapter(new StatusAdapter(
                                                DetailedApplicationData.this),
                                        new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog,
                                                                int which) {
                                                // TODO Auto-generated method stub
                                                View view = getListView().getChildAt(mOnClickPosition);
                                                if (view != null) {
                                                    EditText subscript = (EditText) view.findViewById(R.id.line2edit);

                                                    String mStatus = mStatusArrayList.get(which);
                                                    subscript.setText(mStatusArrayList
                                                            .get(which));
                                                    mFArrayList.get(mOnClickPosition).setTempData(mStatus);

                                                }
                                            }
                                        });
                            } else {
                                builder.setAdapter(new ListValueAdapter(DetailedApplicationData.this, mListItemArray),
                                        new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog,
                                                                int which) {
                                                // TODO Auto-generated method stub
                                                View view = getListView().getChildAt(mOnClickPosition);
                                                if (view != null) {
                                                    TextView subscript = (TextView) view.findViewById(R.id.line2);
                                                    String mValue = mListItemArray.get(which).getValue();
                                                    String mdisplaytext = mListItemArray.get(which).getdisplayText();
                                                    subscript.setText(mListItemArray.get(which).getdisplayText());
                                                    mFArrayList.get(mOnClickPosition).setTempData(mValue);

                                                }
                                            }
                                        });
                            }
                            builder.show();
                        }

                    }
                }
            }
        });
        ;
        JSONHelperClass jsonHelperClass = new JSONHelperClass();
        mStatusColorHashMap = jsonHelperClass.getStatusColourMap();
        if (mStatusColorHashMap != null)
            mStatusArrayList = new ArrayList<String>(mStatusColorHashMap.keySet());

    }

    public void PopulateObject(String ObjId, Boolean Editable) {
        Fields fieldobj;
        mFArrayList = new ArrayList<Fields>();
        MsgScheduleTabActivity.getCache().getObject(ObjId).fieldsList = mFArrayList;
        fieldobj = new Fields();
        fieldobj.setFieldName("CURRENT PHYSICIAN NAME");
        fieldobj.setFieldValue("JESSICA AKAPPES");
        fieldobj.setFieldDispText("CURRENT PHYSICIAN NAME");
        fieldobj.setEditable(Editable);
        fieldobj.seteditableListValue(null);
        fieldobj.setlistValue(null);
        ;
        mFArrayList.add(fieldobj);

        fieldobj = new Fields();
        fieldobj.setFieldName("ADDRESS");
        fieldobj.setFieldValue("811 S HAMILTON");
        fieldobj.setFieldDispText("ADDRESS");
        fieldobj.setEditable(Editable);
        fieldobj.seteditableListValue(null);
        fieldobj.setlistValue(null);
        mFArrayList.add(fieldobj);

        fieldobj = new Fields();
        fieldobj.setFieldName("CITY");
        fieldobj.setFieldValue("CHANDLER");
        fieldobj.setFieldDispText("CITY");
        fieldobj.setEditable(Editable);
        fieldobj.seteditableListValue(null);
        fieldobj.setlistValue(null);
        mFArrayList.add(fieldobj);

        fieldobj = new Fields();
        fieldobj.setFieldName("STATE");
        fieldobj.setFieldValue("ARIZONA");
        fieldobj.setFieldDispText("STATE");
        fieldobj.setEditable(Editable);
        fieldobj.seteditableListValue(null);
        fieldobj.setlistValue(null);
        mFArrayList.add(fieldobj);

        fieldobj = new Fields();
        fieldobj.setFieldName("ZIP");
        fieldobj.setFieldValue("85225");
        fieldobj.setFieldDispText("ZIP");
        fieldobj.setEditable(Editable);
        fieldobj.seteditableListValue(null);
        fieldobj.setlistValue(null);
        mFArrayList.add(fieldobj);

        fieldobj = new Fields();
        fieldobj.setFieldName("PHONE NUMBER");
        fieldobj.setFieldValue("480-344-6100");
        fieldobj.setFieldDispText("PHONE NUMBER");
        fieldobj.setEditable(Editable);
        fieldobj.seteditableListValue(null);
        fieldobj.setlistValue(null);
        mFArrayList.add(fieldobj);
        ////Log.d(TAG, "mFArrayList 0=" + mFArrayList.get(0).getFieldDispText());
        ////Log.d(TAG, "mFArrayList 1=" + mFArrayList.get(1).getFieldDispText());
        ////Log.d(TAG, "mFArrayList 2=" + mFArrayList.get(2).getFieldDispText());
    }

    public void PopulateEditObject(String ObjId, Boolean Editable) {
        Fields fieldobj;
        mFArrayList = new ArrayList<Fields>();
        MsgScheduleTabActivity.getCache().getObject(ObjId).fieldsList = mFArrayList;
        fieldobj = new Fields();
        fieldobj.setFieldName("PHYSICIAN LAST NAME");
        fieldobj.setFieldValue("");
        fieldobj.setFieldDispText("PHYSICIAN LAST NAME");
        fieldobj.setEditable(Editable);
        fieldobj.seteditableListValue(null);
        fieldobj.setlistValue(null);
        ;
        mFArrayList.add(fieldobj);

        fieldobj = new Fields();
        fieldobj.setFieldName("PHYSICIAN FIRST NAME");
        fieldobj.setFieldValue("");
        fieldobj.setFieldDispText("PHYSICIAN FIRST NAME");
        fieldobj.setEditable(Editable);
        fieldobj.seteditableListValue(null);
        fieldobj.setlistValue(null);
        mFArrayList.add(fieldobj);

        fieldobj = new Fields();
        fieldobj.setFieldName("SPECIALITY");
        fieldobj.setFieldValue("");
        fieldobj.setFieldDispText("SPECIALITY");
        fieldobj.setEditable(Editable);
        fieldobj.seteditableListValue(null);
        fieldobj.setlistValue(null);
        mFArrayList.add(fieldobj);

        fieldobj = new Fields();
        fieldobj.setFieldName("PLAN");
        fieldobj.setFieldValue("");
        fieldobj.setFieldDispText("PLAN");
        fieldobj.setEditable(Editable);
        fieldobj.seteditableListValue(null);
        fieldobj.setlistValue(null);
        mFArrayList.add(fieldobj);

        fieldobj = new Fields();
        fieldobj.setFieldName("CITY");
        fieldobj.setFieldValue("");
        fieldobj.setFieldDispText("CITY");
        fieldobj.setEditable(Editable);
        fieldobj.seteditableListValue(null);
        fieldobj.setlistValue(null);
        mFArrayList.add(fieldobj);

        fieldobj = new Fields();
        fieldobj.setFieldName("ZIPCODE");
        fieldobj.setFieldValue("");
        fieldobj.setFieldDispText("ZIPCODE");
        fieldobj.setEditable(Editable);
        fieldobj.seteditableListValue(null);
        fieldobj.setlistValue(null);
        mFArrayList.add(fieldobj);
        /*
        fieldobj = new Fields();
		fieldobj.setFieldName("ZIPCODE");
		fieldobj.setFieldValue("");
		fieldobj.setFieldDispText("ZIPCODE");
		fieldobj.setEditable(Editable);		
		fieldobj.seteditableListValue(null);
		fieldobj.setlistValue(null);
		mFArrayList.add(fieldobj);
		
		fieldobj = new Fields();
		fieldobj.setFieldName("ZIPCODE");
		fieldobj.setFieldValue("");
		fieldobj.setFieldDispText("ZIPCODE");
		fieldobj.setEditable(Editable);		
		fieldobj.seteditableListValue(null);
		fieldobj.setlistValue(null);
		mFArrayList.add(fieldobj);
		
		fieldobj = new Fields();
		fieldobj.setFieldName("ZIPCODE");
		fieldobj.setFieldValue("");
		fieldobj.setFieldDispText("ZIPCODE");
		fieldobj.setEditable(Editable);		
		fieldobj.seteditableListValue(null);
		fieldobj.setlistValue(null);
		mFArrayList.add(fieldobj); 
		//Log.d(TAG, "mFArrayList 0="+mFArrayList.get(0).getFieldDispText());
		//Log.d(TAG, "mFArrayList 1="+mFArrayList.get(1).getFieldDispText());
		//Log.d(TAG, "mFArrayList 2="+mFArrayList.get(2).getFieldDispText()); */
    }

    /* public void PopulateListItemArray()
        {
            String list;
            for(int i=0;i<6;i++)
            {
                list = "List"+ Integer.toString(i);
                mListItemArray.add(list);
            }

            //Log.d(TAG, "mListItemArray size ="+mListItemArray.size());
        } */
    BroadcastReceiver UpdateCompleteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ////Log.d(TAG, "updatecomplete push notification received");
            finishedParsing("dummy");
        }
    };
    BroadcastReceiver mPushNotificationReceiver = new BroadcastReceiver() {
        String FailedFields = "";

        @Override
        public void onReceive(Context arg0, Intent intent) {
            // TODO Auto-generated method stub
            ////Log.d(TAG, "mPushNotificationReceiver in OnReceive" + mPushNotificationReceiver);
    /*	if(mObjName == "MessagingObj")
        {
			if (mProgressDialog != null && mProgressDialog.isShowing()) 
		    {
			mProgressDialog.dismiss();
		    }
			String alrtmsg = intent.getExtras().getString("Message");
			if (alrtmsg.contains("Update success")) 
	    	 {
	    		 Toast.makeText(DetailedApplicationData.this,
	    				    "Update success", Toast.LENGTH_SHORT).show();
	    	 }
			else
			{
				Toast.makeText(DetailedApplicationData.this,
    				    "Update failed", Toast.LENGTH_SHORT).show();	
			}
			finish();
		} */
            if (!mAutosync) {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                    if (UpdateTimer != null) {
                        UpdateTimer.cancel();
                        UpdateTimer = null;

                    }

                }
            }
            ////Log.i(TAG, "In Onreceive of push handler ");
            if (intent != null) {
                String alrtmsg = intent.getExtras().getString("Message");
                ////Log.i(TAG, "alert message is = " + alrtmsg);
                if (alrtmsg.contains("Update success")) {
                    Toast.makeText(DetailedApplicationData.this,
                            "Update success", Toast.LENGTH_SHORT).show();

                    if (mAutosync) {
                     /*
                     isCorefyInProgress = true;
	    	           
	    	            	   Thread syncThread = new Thread()
	    	            	   {
	    	            		   @Override
	    	            		   public void run()
	    	            		   {
	    			            	  startsync();
	    	            		   }
	    	            	   };
	    	            	syncThread.start();
	    	            	checkCorefySyncInProgress(); */
                    } else {
                        if (alrtmsg.contains("Update failed")) {
                            int indx = alrtmsg.indexOf("Update failed");
                            FailedFields = alrtmsg.substring(indx);
                            ////Log.i(TAG, "FailedFields" + FailedFields);  //Failed fields are extracted in separate string
                        }
                        for (int j = 0; j < mannschaftsnamen.size(); j++) {

                            if (!(FailedFields.contains(origContents.get(j)))) {
                                //	int indxx = mTracklist.get(j);
                                //	mFArrayList.get(indxx).setFieldValue(updatedContents.get(j));
                                //	mFArrayList.get(j).setFieldValue(updatedContents.get(j));

                                //		mCoreyDBHelper.updateMsgItem(updatedContents.get(j),
                                //		mMsgId, 1, mannschaftsnamen.get(j));
                                ////Log.i(TAG, "In Onreceive of push handler- success and calling update fields " + origContents.get(j) + updatedContents.get(j));
                                mCoreyDBHelper.updateSFFielditem(mOpportunityName, updatedContents.get(j), origContents.get(j), mMsgId);
                            } else {
                                int indxx = mTracklist.get(j); // mTracklist gives the index in mFieldsArrayList corresponding to mannschaftsnamen
                                mFArrayList.get(indxx).setFieldValue(mFieldsArrayList.get(indxx));
                                //	mFArrayList.get(indxx).setFieldValue(mFArrayList.get(indxx));
                                ////Log.d(TAG, "list value & indxx are = " + j + indxx);
                                //	updatefieldvalue(origContents.get(j), updatedContents.get(j));
                                //	//Log.d(TAG, "resumed field value= " +mFArrayList.get(j).getFieldValue(mFieldsArrayList.get(j)));
                            }
                        }
                        if (alrtmsg.contains("Update failed")) {
                            mDetailedApplicationAdapter.notifyDataSetChanged();
                            ////Log.d(TAG, "Adapter refreshed");
                        }
                    }
                }
                // 	 else if (intent.getExtras().getString("Message")
                //	.equalsIgnoreCase("Update Failed"))
                else if (alrtmsg.contains("Update failed")) //fix for COREY-2225 - else was missing earlier
                {
                    ////Log.d(TAG, "Update failed in " + alrtmsg);
                    //	    for (int i = 0; i < changedArray.length; i++)
                    for (int i = 0; i < mFieldsArrayList.size(); i++) {
                        mFArrayList.get(i).setFieldValue(
                                mFieldsArrayList.get(i));
                        ////Log.i(TAG, "mFArrayList = " + mFArrayList.get(i));
                        ////Log.i(TAG, "mFieldsArrayList = " + mFieldsArrayList.get(i));
                    }

                    mDetailedApplicationAdapter.notifyDataSetChanged();
                    if (mAutosync) {
                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                        }
                    }
//                    Toast.makeText(DetailedApplicationData.this,
//                            "Update failed", Toast.LENGTH_SHORT).show();
                    ////Log.i(TAG, "In Onreceive of push handler-update failed ");

                }
//		else if (intent.getExtras().getString("Message")
//			.equalsIgnoreCase("Update Success")) 
/*		else if (intent.getExtras().getString("Message")
            .contains("Update success"))
		{

		    Toast.makeText(DetailedApplicationData.this,
			    "Update success", Toast.LENGTH_SHORT).show();

		    for (int j = 0; j < mannschaftsnamen.size(); j++) 
		    {
			
			mFArrayList.get(j).setFieldValue(
				updatedContents.get(j));

	//		mCoreyDBHelper.updateMsgItem(updatedContents.get(j),
		//		mMsgId, 1, mannschaftsnamen.get(j));
			//Log.i(TAG, "In Onreceive of push handler- success and calling update fields " );
			mCoreyDBHelper.updateSFFielditem( mOpportunityName,  updatedContents.get(j),  origContents.get(j),  mMsgId);
		    }

		}*/
                else {
                    ////Log.i(TAG, "In Onreceive of push handler-update unknownstate ");
                }
            }

        }
    };

    private String updatedValue;
//    int[] changedArray = new int[mDetailedApplicationAdapter.getCount()];

    private void updatefieldvalue(String Fieldname, String Fieldvalue) {
        for (int i = 0; i < mFArrayList.size(); i++) {   //to get the salesforce Opportunity Name here- needed while updating in db
            if (mFArrayList.get(i).getFieldName() != null
                    && mFArrayList.get(i).getFieldName()
                    .equalsIgnoreCase(Fieldname)) {
                mFArrayList.get(i).setFieldValue(Fieldvalue);
                break;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void save(View v1) {
        mTracklist.clear();
        updatedContents.clear();
        origContents.clear();
        mannschaftsnamen.clear();
        for (int i = 0; i < mDetailedApplicationAdapter.mFieldItems.size(); i++) {
            View view = mDetailedApplicationAdapter.getView(i, null, null);
            if (view != null) {

                updatedValue = mDetailedApplicationAdapter.mFieldItems.get(i).getTempData();
                if (updatedValue != null) {
                    EditText text = (EditText) view
                            .findViewById(R.id.line2edit);

//                    changedArray[i] = i;
                    mTracklist.add(i);
                    TextView text1 = (TextView) view.findViewById(R.id.line1);
                    String contents1 = text1.getText().toString();
                    updatedContents.add(updatedValue);
                    mFArrayList.get(i).setFieldValue(updatedValue);

                    mannschaftsnamen.add(mDetailedApplicationAdapter.getItem(i)
                            .getFieldValue());

                    origContents.add(contents1);
                    mFArrayList.get(i).setTempData(null);  //COREY-2134 fix
                }

            }
        }
        UpdateTimer = new Timer();
        UpdateTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                ////Log.d(TAG, "Update timed out");
                Updatetimout();
            }
        }, CMN_Preferences.getUpdateWaitout(this));

        new MultiUpdateAsyncTask1().execute("dummystring");
        new MultiUpdateAsyncTask().execute("dummystring");
/*	for (int i = 0; i < mannschaftsnamen.size(); i++) {

	    Initiatesync(updatedContents.get(i), origContents.get(i));
	} */

    }

    protected void Initiatesync(String sendString, String origString) {
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(sendString);

        JSONArray jsonArray1 = new JSONArray();
        jsonArray1.put(sendString);

        new UpdateAsyncTask().execute(sendString, origString);

    }

    public void Updatetimout() {
        if (UpdateTimer != null) {
            UpdateTimer.cancel();
            UpdateTimer = null;

        }
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                Toast.makeText(DetailedApplicationData.this,
                        "Update timed out", Toast.LENGTH_LONG).show();

            }
        });

        finishedParsing("dummy");
    }

    class UpdateAsyncTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressDialog.setTitle("Updating");
            mProgressDialog.setMessage("Please Wait ..");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected Integer doInBackground(String... arg0) {

            SharedPreferences mcurrentUserPref = getSharedPreferences(
                    CURRENT_USER, 0);
            String url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                    + "UpdateApplicationData.aspx?token=" + CMN_Preferences.getUserToken(DetailedApplicationData.this);

            ////Log.e(TAG,
//                    "url: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% "
//                            + url);

            if (Utils.checkInternetConnection()) {
                CloseableHttpClient httpclient = new CMN_HTTPSClient(MyApplication.INSTANCE, url).getClient();
                HttpPost httppost = new HttpPost(url);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs
                        .add(new BasicNameValuePair("type", "web_server"));
                try {

                    JSONObject jObject = new JSONObject();
                    jObject.accumulate("AppID", 1);
                    jObject.accumulate("FieldName", arg0[1]);
                    jObject.accumulate("FieldValue", arg0[0]);
                    jObject.accumulate("Context", "");
                    jObject.accumulate("Action", "Update");
                    jObject.accumulate("ApplicationName", "Salesforce");
                    jObject.accumulate("ObjectName", "Opportunity");
                    jObject.accumulate("ObjectID", mUpdateObjid.trim());


                    nameValuePairs.add(new BasicNameValuePair("jsonData",
                            jObject.toString()));

                    ////Log.e(TAG, "nameValuePairs =" + nameValuePairs);
                    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
                            nameValuePairs);
                    // //Log.d(TAG, "entity = " + entity);
                    entity.setContentEncoding(HTTP.UTF_8);
                    httppost.setEntity(entity);
                    httppost.setHeader("Content-Type",
                            "application/x-www-form-urlencoded");

                    HttpResponse response = httpclient.execute(httppost);
                    ////Log.e(TAG, "UpdateApplicationData POST response: "
//                            + response.getStatusLine());
                    JSONObject json = null;
                    json = new JSONObject(EntityUtils.toString(response.getEntity()));

                    if (json != null) {

                        String jsonResult = json.getString("result");
                        ////Log.i("MyLogs",
//                                "UpdateApplicationData %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% json response: "
//                                        + jsonResult);
                        if (UpdateTimer != null) {
                            UpdateTimer.cancel();
                            UpdateTimer = null;

                        }

                    }

                } catch (UnsupportedEncodingException e1) {
                    //e1.getMessage();
                    return -1;
                } catch (ClientProtocolException e) {
                    //e.getMessage();
                } catch (IOException e) {
                    //e.getMessage();
                } catch (JSONException e) {
                    //e.getMessage();
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

        }
    }

    class MultiUpdateAsyncTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressDialog.setTitle("Updating");
            mProgressDialog.setMessage("Please Wait ..");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        public JSONObject getJSONOnject() {

            JSONObject jObject = new JSONObject();
            try {
                jObject.accumulate("AppID", mAppId);
                jObject.accumulate("Context", mMsgContext);
                if (mAutosync) jObject.accumulate("Action", "Append");
                else jObject.accumulate("Action", "Update");
                jObject.accumulate("ApplicationName", mAppName);
                jObject.accumulate("ObjectName", mObjName);
                jObject.accumulate("ObjectID", ObjId);
                jObject.accumulate("listid", listid);
                jObject.accumulate("selection", selectedItems);
                JSONArray jarray = new JSONArray();
                JSONObject jFieldObject = null;
                for (int i = 0; i < mannschaftsnamen.size(); i++) {
                    jFieldObject = new JSONObject();
                    int indxxx = mTracklist.get(i); // mTracklist gives the index in mFieldsArrayList corresponding to mannschaftsnamen
                    String fieldname = mFArrayList.get(indxxx).getFieldName();
                    mFieldId = mFArrayList.get(indxxx).getFieldId();
                    if (mFieldId != null) jFieldObject.accumulate("FieldID", mFieldId);
                    else jFieldObject.accumulate("FieldID", "");
                    jFieldObject.accumulate("FieldName", fieldname);
                    jFieldObject.accumulate("FieldValue", updatedContents.get(i));
                    jarray.put(jFieldObject);
                }
                jObject.accumulate("Data", jarray);


            } catch (Exception e) {
                //e.getMessage();
            }
            return jObject;
        }

        @Override
        protected Integer doInBackground(String... arg0) {

            SharedPreferences mcurrentUserPref = getSharedPreferences(
                    CURRENT_USER, 0);
            String url = null;
            HttpPost httpPost;
            if (AppConfig.isAESEnabled) {
                url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                        + "updateApplicationdatamultiple_s.aspx?token=" + CMN_Preferences.getUserToken(DetailedApplicationData.this);
//                url = url + "" + CMN_AES.encrypt(URLEncoder.encode(getJSONOnject().toString()), CMN_Preferences.getUserToken(DetailedApplicationData.this));
                httpPost = new HttpPost(url + "");
                try {
                    httpPost.setEntity(new StringEntity(CMN_AES.encrypt(getJSONOnject().toString(), CMN_Preferences.getUserToken(activity))));
                } catch (UnsupportedEncodingException e) {
                    //e.getMessage();
                }
            } else {
                url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                        + "updateApplicationdatamultiple.aspx?token=" + CMN_Preferences.getUserToken(DetailedApplicationData.this) + "&jsonData=";
                url = url + "" + URLEncoder.encode(getJSONOnject().toString());
                httpPost = new HttpPost(url + "");
            }
            ////Log.e(TAG, "url: " + url);

            if (Utils.checkInternetConnection()) {
                JSONObject json = null;

                CloseableHttpClient httpclient = new CMN_HTTPSClient(DetailedApplicationData.this, url).getClient();
                try {


                    ////Log.e(TAG, "url" + url);

//                    HttpPost httpPost = new HttpPost(url + "");

                    HttpResponse response = httpclient.execute(httpPost);
                    ////Log.e(TAG, "POST response: " + response.getStatusLine());
                    String result = EntityUtils.toString(response.getEntity());
                    json = new JSONObject(CMN_AES.decryptData(result, CMN_Preferences.getUserToken(DetailedApplicationData.this)));
                    ////Log.e(TAG, "result" + result);
                    if (json != null) {
                        String jsonResult = json.getString("result");
                        if (jsonResult.equalsIgnoreCase("0")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(DetailedApplicationData.this, "Update Success,This will take some time to reflect the updates", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        finish();
                        if (UpdateTimer != null) {
                            UpdateTimer.cancel();
                        }
                    }
                } catch (UnsupportedEncodingException e1) {
                    //e1.getMessage();
                    return -1;
                } catch (ClientProtocolException e) {
                    //e.getMessage();
                } catch (IOException e) {
                    //e.getMessage();
                } catch (JSONException e) {
                    //e.getMessage();
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
            //e.getMessage();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                //e.getMessage();
            }
        }
        return sb.toString();
    }

    @Override
    protected void onStop() {
        super.onStop();
        ////Log.d(TAG, "mPushNotificationReceiver in OnStop" + mPushNotificationReceiver);
/*	if (mPushNotificationReceiver != null) {
        unregisterReceiver(mPushNotificationReceiver);
	    mPushNotificationReceiver = null;
	} */

    }

    @Override
    protected void onResume() {
        super.onResume();
        mPushNotificationFilter = new IntentFilter();
        MyApplication application = (MyApplication) getApplication();
        ////Log.d(TAG, "mPushNotificationReceiver in OnResume" + mPushNotificationReceiver);
//	mPushNotificationFilter
        //	.addAction("com.coremobile.coreyhealth.receivedpush");

        mPushNotificationFilter
                .addAction(application.AppConstants.getReceivedPushIntent());

        registerReceiver(mPushNotificationReceiver, mPushNotificationFilter );

        mUpdateCompleteFilter = new IntentFilter();
        //	mUpdateCompleteFilter.addAction( application.AppConstants.getDownloadCompleteIntent());
        mUpdateCompleteFilter.addAction(application.AppConstants.getUploadCompleteIntent());
        try {
            registerReceiver(UpdateCompleteReceiver, mUpdateCompleteFilter );
        } catch (Exception e) {
            //e.getMessage();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        ////Log.d(TAG, "mPushNotificationReceiver in OnPause" + mPushNotificationReceiver);
/*	if (mPushNotificationReceiver != null) {
        unregisterReceiver(mPushNotificationReceiver);
	    mPushNotificationReceiver = null;
	} */
        try {
            unregisterReceiver(UpdateCompleteReceiver);
        } catch (IllegalArgumentException e) {
            //e.getMessage();
        } catch (Exception e) {
            //e.getMessage();
        }
        if (mProgressDialog != null && mProgressDialog.isShowing()) {

            mProgressDialog.dismiss();

        }
    }

    private void checkCorefySyncInProgress() {

        Thread waitThread = new Thread() {
            @Override
            public void run() {
                try {
                    ////Log.i(TAG, "isCorefyInProgress =" + isCorefyInProgress);
                    sleep(3000); // Wait for a few seconds...
                    while (isCorefyInProgress) {
                        int ret = isCoreyfySyncComplete();
                        ////Log.i(TAG, "checkCorefySyncInProgress Return " + ret);
                        if (ret == 0) {
                            sleep(5000);
                            continue;
                        }
                        isCorefyInProgress = false;
                    }

                    //  refreshMessages();
                } catch (InterruptedException e) {
                    //e.getMessage();
                } finally {

                    ////Log.i(TAG,
//                            "sleep again before pulling the data $$$$$$$$$$$$$$$$");
                    try {
                        sleep(2000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        //e.getMessage();
                    }
                    ////Log.i(TAG,
//                            "start the pull data $$$$$$$$$$$$$$$$");
                    refreshMessages();

                }
            }

        };

        waitThread.start();

    }

    private void refreshMessages() {
        ////Log.d(TAG, "in refreshMessages()");
        MyApplication.INSTANCE.mPullDataCompleted.block();
     /*   
        if (!MyApplication.INSTANCE.mPullDataCompleted
                .block(PULL_DATA_WAIT)) {
            //Log.i(TAG,
                    "pullData didn't complete in millis="
                            + PULL_DATA_WAIT);
        } */
        if (Utils.checkInternetConnection()) {
//            MyApplication application = (MyApplication) getApplication();
//            application.pullData(DetailedApplicationData.this);
        } else {

            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    Toast.makeText(
                            getApplicationContext(),
                            "Operation cannot be performed now.Check if your cellular data/wifi is turned ON.",
                            Toast.LENGTH_SHORT).show();

                }
            });

        }

        // TODO Auto-generated method stub

    }

    private int isCoreyfySyncComplete() {
        SharedPreferences mcurrentUserPref = getSharedPreferences(
                CURRENT_USER, 0);
        //mcurrentUserPref = getSharedPreferences(CURRENT_USER, 0);
        JSONHelperClass jsonHelperClass = new JSONHelperClass();
        String CMN_SERVER_BASE_URL_DEFINE = jsonHelperClass
                .getBaseURL(mcurrentUserPref.getString("Organization", ""));
        String url = CMN_SERVER_BASE_URL_DEFINE + "GetTriggerInProgress.aspx?token=" + CMN_Preferences.getUserToken(DetailedApplicationData.this);
        ////Log.i(TAG, "url: " + url);
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
                ////Log.i(TAG,
//                        "isCoreyfySyncComplete POST response: "
//                                + response.getStatusLine());
                JSONObject json = null;
                json = new JSONObject(EntityUtils.toString(response.getEntity()));

                if (json != null) {
                    String jsonResult = json.getString("result");
                    ////Log.i(TAG, "isCoreyfySyncComplete json response: "
//                            + jsonResult);
                    ////Log.i(TAG, "isCoreyfySyncComplete json response length: "
//                            + jsonResult.length());
                    ////Log.i(TAG, "isCoreyfySyncComplete json response empty: "
//                            + jsonResult.isEmpty());
                    if (jsonResult.length() == 4) {
                        return 1;
                    }
                /*    if(jsonResult.equals(""))
                    {
                    	return 1;
                    } */
                }

            } catch (UnsupportedEncodingException e1) {
                //e1.getMessage();
                return -1;
            } catch (IllegalStateException e) {
                //e.getMessage();
                return -1;
            } catch (ClientProtocolException e) {
                //e.getMessage();
                return -1;
            } catch (IOException e) {
                //e.getMessage();
                return -1;
            } catch (JSONException e) {
                //e.getMessage();
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

    private boolean startsync() {


        String url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                + MessageTabActivityCMN.CMN_SERVER_MANUAL_SYNC_API + "?token=" + CMN_Preferences.getUserToken(DetailedApplicationData.this);
        ////Log.i(TAG, "url: " + url);
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
                // //Log.i(TAG,"httppost: " + httppost);
                HttpResponse response = httpclient.execute(httppost);
                ////Log.i(TAG, "POST response: " + response.getStatusLine());
                JSONObject json = null;
                json = new JSONObject(EntityUtils.toString(response.getEntity()));
                // //Log.i(TAG,"json length" + json.length() + " " +
                // json.names());

                if (json != null) {
                    // closeDialog();
                    String jsonResult = json.getString("result");
                    // String resultText = json.getString("text");
                    ////Log.i(TAG, "json response: " + jsonResult);
                    if (jsonResult.equals("1"))  // sync complete here
                    {
                        //       stopTimer();
                    }
                }

            } catch (UnsupportedEncodingException e1) {
                //e1.getMessage();
                return false;
            } catch (IllegalStateException e) {
                //e.getMessage();
                return false;
            } catch (ClientProtocolException e) {
                //e.getMessage();
                return false;
            } catch (IOException e) {
                //e.getMessage();
                return false;
            } catch (JSONException e) {
                //e.getMessage();
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

    @Override
    public void finishedParsing(String _status) {
        // TODO Auto-generated method stub

        if (UpdateTimer != null) {
            UpdateTimer.cancel();
            UpdateTimer = null;

        }
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        if (UpdateCompleteReceiver != null) {
            try {
                unregisterReceiver(UpdateCompleteReceiver);
            } catch (Exception e) {
                ////Log.d(TAG, "unresgisterReceiver exceptiopn");
            }
            UpdateCompleteReceiver = null;
        }
        //  Intent myIntent = new Intent();  //not really required
        //  setResult(RESULT_OK, myIntent);
        finish();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ////Log.i(TAG, "onDestroy");
        if (mPushNotificationReceiver != null) {
            unregisterReceiver(mPushNotificationReceiver);
            mPushNotificationReceiver = null;
        }
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub


    }

    @Override
    public void showDialog() {
        // TODO Auto-generated method stub

    }

    @Override
    public void closeDialog() {
        // TODO Auto-generated method stub

    }

    public void getListItemObjMap() {
         /*
          * populate Itemlist level variables firs
    	  */

        Cursor ListItemCursor = this.getContentResolver().query(
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
                new String[]{String.valueOf(mMsgId), "ListItems", "ListItem"}, null);

        ////Log.d(TAG, "ListItemCursor" + ListItemCursor);
        ////Log.d(TAG, "ListItemCursor count" + ListItemCursor.getCount());
        if (ListItemCursor != null && ListItemCursor.getCount() > 0) {
            int i = 0;
            String ListItemName = "";
            //ListItemName=ListItemCursor.getString(2);
            //	//Log.d(TAG,"ListItemName outside loop="+ListItemName);
            /*
             * This will retreive only the listitems.  Each listitme
			 * has 4 attributes saved as rows
			 */

            while (ListItemCursor.moveToNext()) {

                switch (i % 4) {
                    case 0:
                        ListItemObj listitemobj = new ListItemObj();
                        listitemobj.setName(ListItemCursor.getString(2));
                        ListItemName = ListItemCursor.getString(2);
                        ListItemObjMap.put(listitemobj.getName(), listitemobj);
                        ////Log.d(TAG, "ListItemName=" + ListItemName);
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
        Cursor ItemCursor = this.getContentResolver().query(
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
                        + " =?",
                new String[]{String.valueOf(mMsgId), "ListItems", "Item"}, null);
        //	String ItemName=ItemCursor.getString(2);
        //	//Log.d(TAG,"ItemName outside loop="+ItemName);
        if (ItemCursor != null && ItemCursor.getCount() > 0) {
            int i = 0;
            int index = 0;
            String ListItemName = "";
            String PrevListItemName = "";

            while (ItemCursor.moveToNext()) {
                int switchval = i % 11;
                ////Log.d(TAG, "switchval" + switchval);
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
                        //	//Log.d(TAG,"Value="+ItemCursor.getString(2));
                        ListItemObjMap.get(ListItemName).ListItemArray.add(listitem);
                        //	//Log.d(TAG,"ListItemName=" +ListItemName);
                        //	//Log.d(TAG,"ListItemValue=" +ItemCursor.getString(2));
                        //	//Log.d(TAG, "i/5 =" +index);
                        //	//Log.d(TAG,"value written in =" +ListItemObjMap.get(ListItemName).ListItemArray.get(index).getValue());
                        //	//Log.d(TAG,"value written in case 0 with index0 =" +ListItemObjMap.get(ListItemName).ListItemArray.get(0).getValue());
                        break;
                    case 1:    ////Log.d(TAG,"value written in case 1 with index0 before next write=" +ListItemObjMap.get(ListItemName).ListItemArray.get(0).getValue());
                        ListItemObjMap.get(ListItemName).ListItemArray.get(index).setdisplayText(ItemCursor.getString(2));
                        ////Log.d(TAG, "ListItemName in case1 =" +ListItemName);
                        ////Log.d(TAG, "i/5 =" +index);
                        ////Log.d(TAG,"value written in- case1 =" +ListItemObjMap.get(ListItemName).ListItemArray.get(index).getValue());
                        ////Log.d(TAG,"value written in case 1 with index0 =" +ListItemObjMap.get(ListItemName).ListItemArray.get(0).getValue());
                        ////Log.d(TAG,"disptext written in- case1 =" +ListItemObjMap.get(ListItemName).ListItemArray.get(index).getdisplayText());
                        ////Log.d(TAG, "displaytext=" + ItemCursor.getString(2));
                        break;
                    case 2:
                        ListItemObjMap.get(ListItemName).ListItemArray.get(index).setImage(ItemCursor.getString(2));
                        //	//Log.d(TAG,"image="+ItemCursor.getString(2));
                        break;
                    case 3:
                        ListItemObjMap.get(ListItemName).ListItemArray.get(index).setSubscript(ItemCursor.getString(2));
                        //	//Log.d(TAG,"subscript="+ItemCursor.getString(2));
                        break;
                    case 4:
                        ListItemObjMap.get(ListItemName).ListItemArray.get(index).setBackgroundColor(ItemCursor.getString(2));
                        //	//Log.d(TAG,"bkgclr="+ItemCursor.getString(2));
                        break;
                    case 5:
                        ListItemObjMap.get(ListItemName).ListItemArray.get(index).setDescription(ItemCursor.getString(2));
                        ////Log.d(TAG,"desc="+ItemCursor.getString(2));
                        break;
                    case 6:
                        ListItemObjMap.get(ListItemName).ListItemArray.get(index).setId(ItemCursor.getString(2));
                        ////Log.d(TAG,"id="+ItemCursor.getString(2));
                        break;
                    case 7:
                        ListItemObjMap.get(ListItemName).ListItemArray.get(index).setOpenUrl(ItemCursor.getString(2));
                        // //Log.d(TAG,"openurl="+ItemCursor.getString(2));
                        break;
                    case 8:
                        break;
                    case 9:
                        break;
                    case 10:
                        break;
                    default:
                        ////Log.d(TAG, "in  switch default");
                        break;
                }
                i++;
                ////Log.d(TAG,"i in Itemcursor loop" +i);
            }
            if (ItemCursor != null) ItemCursor.close();
        }
    /*	 //Log.d(TAG,"messagelist Item value ="+ListItemObjMap.get("AdminUsersList").ListItemArray.get(0).getValue());
    	 //Log.d(TAG,"messagelist Item displaytext ="+ListItemObjMap.get("AdminUsersList").ListItemArray.get(0).getdisplayText());
    	 //Log.d(TAG,"messagelist Item image ="+ListItemObjMap.get("AdminUsersList").ListItemArray.get(0).getImage());
    	 //Log.d(TAG,"messagelist Item subscript ="+ListItemObjMap.get("AdminUsersList").ListItemArray.get(0).getSubscript());
    	 //Log.d(TAG,"messagelist Item bkgclor ="+ListItemObjMap.get("AdminUsersList").ListItemArray.get(0).getBackgroundColor());
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 211) {
            selectedItems = data.getStringExtra("data");
            selectedreasons = data.getStringExtra("reason");

            final com.coremobile.coreyhealth.widget.CoreyEditText subscript = (com.coremobile.coreyhealth.widget.CoreyEditText) DetailedApplicationAdapter.view.getTag();

//            View v = mDetailedApplicationAdapter.getView(DetailedApplicationAdapter.positionSelected,null,null);
//            DetailedApplicationAdapter.DetailedViewHolder text = (DetailedApplicationAdapter.DetailedViewHolder) v.getTag();
            subscript.setText(selectedreasons);
        }
    }

    class MultiUpdateAsyncTask1 extends AsyncTask<String, Void, Integer> {
        String jsonResult = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressDialog.setTitle("Updating");
            mProgressDialog.setMessage("Please Wait ..");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected Integer doInBackground(String... arg0) {

            SharedPreferences mcurrentUserPref = getSharedPreferences(
                    CURRENT_USER, 0);
            String url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                    + "SetMultiSelectListValues.aspx?token=" + CMN_Preferences.getUserToken(DetailedApplicationData.this);

            Log.d(TAG, "url: %%%%%%%%%%%%%%%%%%%%%%%%%%" + url);

            if (Utils.checkInternetConnection()) {
                CloseableHttpClient httpclient = new CMN_HTTPSClient(MyApplication.INSTANCE, url).getClient();
                HttpPost httppost = new HttpPost(url);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs
                        .add(new BasicNameValuePair("type", "web_server"));
                try {

                    nameValuePairs.add(new BasicNameValuePair("Context",
                            mMsgContext));
                    nameValuePairs.add(new BasicNameValuePair("listid",
                            listid));
                    nameValuePairs.add(new BasicNameValuePair("selection",
                            selectedItems));

                    Log.d(TAG, "nameValuePairs =" + nameValuePairs);

                    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
                            nameValuePairs);
                    Log.d(TAG, "entity = " + entity);
                    entity.setContentEncoding(HTTP.UTF_8);
                    httppost.setEntity(entity);
                    httppost.setHeader("Content-Type",
                            "application/x-www-form-urlencoded");

                    HttpResponse response = httpclient.execute(httppost);
                    Log.d(TAG, "SetMultiSelectListValues POST response: "
                            + response.getStatusLine());
                    JSONObject json = null;
                    json = new JSONObject(EntityUtils.toString(response.getEntity()));
                    if (json != null) {

                        jsonResult = json.getString("result");
                        Log.d(TAG,
                                "SetMultiSelectListValues %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% json response: "
                                        + jsonResult);
                        Log.d(TAG,
                                "SetMultiSelectListValues api %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% json error: "
                                        + json.getString("Error"));
                        if (UpdateTimer != null) {
                            UpdateTimer.cancel();
                            UpdateTimer = null;

                        }
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


                if (jsonResult.equals("0")) return 0;
                else return -1;
            } else return -1;

        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            Log.d(TAG, "APi resutlt=" + result);
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }

        }
    }

    @Override
    public void onBackPressed() {
//        if (mFArrayList != null && mFArrayList.size() > 4) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setCancelable(false);
//            builder.setTitle("Any unsaved data will be erased");
//            builder.setMessage("Are you sure you want to go back?");
//            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    finish();
//                }
//            });
//            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.cancel();
//                }
//            });
//            AlertDialog alert = builder.create();
//            alert.show();
//        } else {
            super.onBackPressed();
//        }
    }
}
