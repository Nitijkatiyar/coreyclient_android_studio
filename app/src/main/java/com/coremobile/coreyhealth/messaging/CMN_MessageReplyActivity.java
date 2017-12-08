package com.coremobile.coreyhealth.messaging;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coremobile.coreyhealth.R;
import com.coremobile.coreyhealth.base.CMN_AppBaseActivity;
import com.coremobile.coreyhealth.errorhandling.ActivityPackage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @author Nitij Katiyar
 * 
 */
public class CMN_MessageReplyActivity extends CMN_AppBaseActivity {

	private SharedPreferences mCurrentUserPref;
	public static final String CURRENT_USER = "CurrentUser";
	private String organizationName;
	String context, user_category, context_name;

	ImageView reply;
	CMN_MessageDataModel messageDataModel;
	EditText message;
	Intent intent;
	TextView messageTo, messageFromPatient, messageFromProvider,
			messageSubject;

	ImageView selectTo, selectFrom, selectSubject;
	public static CMN_MessageTopicsModel topicsModel = new CMN_MessageTopicsModel();
	public static ArrayList<CMN_PatientModel> patientList = new ArrayList<CMN_PatientModel>();
	public static ArrayList<CMN_MessageToModel> toList = new ArrayList<CMN_MessageToModel>();
	boolean isReplyEditable = false;
	LinearLayout toLayout, patientLayout, subjectLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_composemessages);

		message = (EditText) findViewById(R.id.message_editfield);
		toLayout = (LinearLayout) findViewById(R.id.toLayout);
		patientLayout = (LinearLayout) findViewById(R.id.patientLayout);
		subjectLayout = (LinearLayout) findViewById(R.id.subjectLayout);

		messageTo = (TextView) findViewById(R.id.message_to);
		messageFromPatient = (TextView) findViewById(R.id.message_patienttext);
		messageSubject = (TextView) findViewById(R.id.message_subject);
		messageFromProvider = (TextView) findViewById(R.id.message_patient);

		selectTo = (ImageView) findViewById(R.id.message_Selectto);
		selectFrom = (ImageView) findViewById(R.id.message_Selectfrom);
		selectSubject = (ImageView) findViewById(R.id.message_Selectsubject);

		intent = getIntent();
		mCurrentUserPref = getSharedPreferences(CURRENT_USER, 0);
		organizationName = mCurrentUserPref.getString("Organization", null);
	 	user_category = mCurrentUserPref.getString("user_category", null);
		context = mCurrentUserPref.getString("context", null);
		context_name = mCurrentUserPref.getString("context_name", null);

		if (user_category.equalsIgnoreCase("patient")) {
			messageFromPatient.setText("" + context_name);
			messageFromPatient.setVisibility(View.VISIBLE);
			messageFromProvider.setVisibility(View.GONE);
			patientLayout.setEnabled(false);
			selectFrom.setVisibility(View.GONE);
		} else {
			messageFromPatient.setVisibility(View.GONE);
			messageFromProvider.setVisibility(View.VISIBLE);
			selectFrom.setVisibility(View.VISIBLE);

		}

		if (intent.hasExtra("data")) {

			invalidateOptionsMenu();
			messageDataModel = (CMN_MessageDataModel) intent
					.getSerializableExtra("data");

			if (user_category.equalsIgnoreCase("patient")) {
				messageFromPatient.setVisibility(View.VISIBLE);
				messageFromProvider.setVisibility(View.GONE);
				messageFromPatient.setText(messageDataModel
						.getFromDisplayName());
			} else {
				messageFromPatient.setVisibility(View.GONE);
				messageFromProvider.setVisibility(View.VISIBLE);
				messageFromProvider.setText(""
						+ messageDataModel.getContextDisplayName());
			}
			messageFromProvider.setEnabled(false);
			messageSubject.setEnabled(false);
			messageTo.setText(messageDataModel.getToUsers());
			message.setText(messageDataModel.getMessage());
			messageTo.setEnabled(false);
			messageSubject.setText("Re:" + messageDataModel.getTopic());

		} else {
			invalidateOptionsMenu();
		}

		toLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClassName(getPackageName(), ActivityPackage.ToUserListActivity);
				intent.putExtra("toLoad", "to");
				startActivityForResult(intent, 1);
			}
		});
		subjectLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClassName(getPackageName(), ActivityPackage.ToUserListActivity);
				intent.putExtra("toLoad", "subject");
				startActivityForResult(intent, 3);
			}
		});
		patientLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClassName(getPackageName(), ActivityPackage.ToUserListActivity);
				intent.putExtra("toLoad", "patient");
				startActivityForResult(intent, 2);
			}
		});
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setTitle(
				"" + getResources().getString(R.string.messages));
		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		MenuItem item1 = menu.findItem(R.id.action_send);

		MenuItem item2 = menu.findItem(R.id.action_reply);
		item2.setVisible(false);

		MenuItem item3 = menu.findItem(R.id.action_done);
		item3.setVisible(false);
		this.invalidateOptionsMenu();
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.messagereply, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			return true;
		case R.id.action_reply:
			return true;
		case R.id.action_send:
			SendCoreyMessage(false);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}

	private void SendCoreyMessage(boolean isReply) {
		// TODO Auto-generated method stub
		JSONObject jsonObject = new JSONObject();
		try {

			jsonObject.put("ThreadId", messageDataModel.getThreadId());
			if (user_category.equalsIgnoreCase("patient")) {
				jsonObject.put("Context", context);
			} else {
				if (patientList.size() != 0) {
					String contextIds = "";
					for (int i = 0; i < patientList.size(); i++) {
						contextIds = contextIds + ""
								+ patientList.get(i).getPatientId().toString()
								+ ",";
					}
					jsonObject.put(
							"Context",
							""
									+ contextIds.substring(0,
											contextIds.length() - 1));
				} else {
					jsonObject.put("Context",
							"" + messageDataModel.getContextId());
				}
			}

			jsonObject.put("Topic", "" + messageDataModel.getTopicId());

			jsonObject.put("Message", "" + message.getText().toString());

			if (toList.size() != 0) {  
				String toUsers = "";
				String toUsersIds = "";
				for (int i = 0; i < toList.size(); i++) { 
					toUsers = toUsers + "" + toList.get(i).getName().toString()
							+ ",";
					toUsersIds = toUsersIds + ""
							+ toList.get(i).getCode().toString() + ",";
				}
				jsonObject.put("ToUserList",
						"" + toUsers.substring(0, toUsers.length() - 1));
				jsonObject.put("ToType",
						"" + toUsersIds.substring(0, toUsersIds.length() - 1));

			} else {
				jsonObject.put("ToType", "" + messageDataModel.getFromUsrID());
				jsonObject.put("ToUserList",
						"" + messageDataModel.getFromUsrID());
			}

		} catch (JSONException e) {
		//	e.getMessage();
		}
		//Log.e("requestJSON", "" + jsonObject.toString());
		CMN_SendCoreyMessagesWebService coreyMessagesWebService = new CMN_SendCoreyMessagesWebService(
				CMN_MessageReplyActivity.this);
		coreyMessagesWebService.execute("" + jsonObject);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK && requestCode == 1) {
			//Log.e("onActivityResult", "onActivityResult");
			toList = (ArrayList<CMN_MessageToModel>) data
					.getSerializableExtra("data");

			String toUsers = "";
			for (int i = 0; i < toList.size(); i++) {
				toUsers = toUsers + "" + toList.get(i).getName().toString()
						+ ",";
			}

			messageTo.setText(toUsers.substring(0, toUsers.length() - 1));
		} else if (resultCode == RESULT_OK && requestCode == 2) {
			//Log.e("onActivityResult", "onActivityResult");
			patientList = (ArrayList<CMN_PatientModel>) data
					.getSerializableExtra("data");

			String toPatients = "";
			for (int i = 0; i < patientList.size(); i++) {
				toPatients = toPatients + ""
						+ patientList.get(i).getPatientName().toString() + ",";
			}

			messageFromProvider.setText(toPatients.substring(0,
					toPatients.length() - 1));
		} else if (resultCode == RESULT_OK && requestCode == 3) {
			//Log.e("onActivityResult", "onActivityResult");
			topicsModel = (CMN_MessageTopicsModel) data
					.getSerializableExtra("data");

			String toUsers = "";
			for (int i = 0; i < toList.size(); i++) {
				toUsers = toUsers + "" + toList.get(i).getName().toString()
						+ ",";
			}

			messageSubject.setText(topicsModel.getTopicName());
		}
	}
}
