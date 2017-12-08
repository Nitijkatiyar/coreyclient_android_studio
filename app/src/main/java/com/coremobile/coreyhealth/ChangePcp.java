package com.coremobile.coreyhealth;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.coremobile.coreyhealth.newui.Fields;
import com.coremobile.coreyhealth.newui.MsgScheduleTabActivity;

import java.util.ArrayList;
import java.util.Timer;

public class ChangePcp extends ListActivity {
	
	 private static String TAG = "Corey_ChangePcp";
	    public static final String CURRENT_USER = "CurrentUser";
	    Timer UpdateTimer;
	    ArrayList<ListItem> mListItemArray;
	    Intent mIntent;
	    String ObjectId;
	    ArrayList<Fields> mFArrayList;
	    DetailedApplicationAdapter mDetailedApplicationAdapter;
	    MyApplication application;
	    String mAppName;
	    String mAppTitle;
	    String mObjectName;
	    String mObjName;
	    boolean isEditable;
	    boolean mAutosync;
	    private int mMsgId;
	    String mAppContext;
	    String mMsgContext;
	    private ProgressDialog mProgressDialog;
	    String mFieldId;
	    String mAppId;
	    String mRow0Content;
	    String mAssignedUsrId;
	    ListView mListView;
	    private String mOrganization;
	    private IntentFilter mPushNotificationFilter;
	    CoreyDBHelper mCoreyDBHelper;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change_pcp);
		mListView = getListView();
		mIntent = getIntent();
		//application = (MyApplication) getApplication();;
		TextView emptyTextView = (TextView) findViewById(R.id.empty);
		emptyTextView.setGravity(Gravity.CENTER_VERTICAL);
		emptyTextView.setText("No data to display");
		mProgressDialog = new ProgressDialog(ChangePcp.this);
		mCoreyDBHelper = new CoreyDBHelper(this);
		SharedPreferences mcurrentUserPref = getSharedPreferences(
			    CURRENT_USER, 0);
		mAutosync= mcurrentUserPref.getBoolean("RequireAutoSync", false);

	    
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.change_pcp, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	 public void PopulateObject(String ObjId, Boolean Editable)    
	 {
		 Fields fieldobj;
		 mFArrayList = new ArrayList<Fields>();
		 MsgScheduleTabActivity.getCache().getObject(ObjectId).fieldsList=mFArrayList;
			fieldobj = new Fields();
			fieldobj.setFieldName("CURRENT PHYSICIAN NAME");
			fieldobj.setFieldValue("JESSICA AKAPPES");
			fieldobj.setFieldDispText("CURRENT PHYSICIAN NAME");
			fieldobj.setEditable(Editable);
			fieldobj.seteditableListValue(null);
			fieldobj.setlistValue(null);;
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
			fieldobj.setFieldDispText("fld5");
			fieldobj.setEditable(Editable);		
			fieldobj.seteditableListValue(null);
			fieldobj.setlistValue(null);
			mFArrayList.add(fieldobj);
			Log.d(TAG, "mFArrayList 0="+mFArrayList.get(0).getFieldDispText());
			Log.d(TAG, "mFArrayList 1="+mFArrayList.get(1).getFieldDispText());
			Log.d(TAG, "mFArrayList 2="+mFArrayList.get(2).getFieldDispText());
	 }
	 public void PopulateEditObject(String ObjId, Boolean Editable)    
	 {
		 Fields fieldobj;
		 mFArrayList = new ArrayList<Fields>();
		 MsgScheduleTabActivity.getCache().getObject(ObjectId).fieldsList=mFArrayList;
			fieldobj = new Fields();
			fieldobj.setFieldName("PHYSICIAN LAST NAME");
			fieldobj.setFieldValue("");
			fieldobj.setFieldDispText("PHYSICIAN LAST NAME");
			fieldobj.setEditable(Editable);
			fieldobj.seteditableListValue(null);
			fieldobj.setlistValue(null);;
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
			Log.d(TAG, "mFArrayList 0="+mFArrayList.get(0).getFieldDispText());
			Log.d(TAG, "mFArrayList 1="+mFArrayList.get(1).getFieldDispText());
			Log.d(TAG, "mFArrayList 2="+mFArrayList.get(2).getFieldDispText());
	 }
}
