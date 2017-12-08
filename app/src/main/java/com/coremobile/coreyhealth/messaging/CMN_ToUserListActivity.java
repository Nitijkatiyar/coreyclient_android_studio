package com.coremobile.coreyhealth.messaging;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.coremobile.coreyhealth.R;
import com.coremobile.coreyhealth.base.CMN_AppBaseActivity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Nitij Katiyar
 * 
 */
public class CMN_ToUserListActivity extends CMN_AppBaseActivity {
	private SharedPreferences mCurrentUserPref;
	public static final String CURRENT_USER = "CurrentUser";
	private String organizationName;

	ListView listView;
	Intent intent;
	CMN_MessageToAdapter adapterTo;
	CMN_MessagePatientAdapter adapterPatient;
	CMN_MessageTopicsAdapter adapterSubject;
	boolean patient = false, to = false, subject = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_allmessages);

		listView = (ListView) findViewById(R.id.listView);
		ImageView newMessage = (ImageView) findViewById(R.id.button_newMessage);
		newMessage.setVisibility(View.GONE);

		intent = getIntent();
		if (intent.hasExtra("toLoad")) {
			if (intent.getStringExtra("toLoad").equalsIgnoreCase("patient")) {
				patient = true;
				subject = false;
				to = false;
			}
			if (intent.getStringExtra("toLoad").equalsIgnoreCase("to")) {
				patient = false;
				subject = false;
				to = true;
			}
			if (intent.getStringExtra("toLoad").equalsIgnoreCase("subject")) {
				patient = false;
				subject = true;
				to = false;
			}
		}
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setTitle("To");
		getActionBar().setDisplayHomeAsUpEnabled(true);

		mCurrentUserPref = getSharedPreferences(CURRENT_USER, 0);
		organizationName = mCurrentUserPref.getString("Organization", null);

		if (patient) {
			getActionBar().setTitle("Patient");
			CMN_GetAllPatientsWebService getToListWebService = new CMN_GetAllPatientsWebService(
					CMN_ToUserListActivity.this, "", listView);
			getToListWebService.execute();
		} else if (subject) {
			getActionBar().setTitle("Subject");
			CMN_GetAllTopicsWebService getToListWebService = new CMN_GetAllTopicsWebService(
					CMN_ToUserListActivity.this, "", listView);
			getToListWebService.execute();
		} else if (to) {

			getActionBar().setTitle("To");
			CMN_GetToListWebService getToListWebService = new CMN_GetToListWebService(
					CMN_ToUserListActivity.this, "", listView);
			getToListWebService.execute();
		}

	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		MenuItem item = menu.findItem(R.id.action_send);
		item.setVisible(false);

		MenuItem item1 = menu.findItem(R.id.action_reply);
		item1.setVisible(false);
		this.invalidateOptionsMenu();

		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.messagereply, menu);

		return true;
	}

	public void refreshToList(ArrayList<CMN_MessageToModel> list) {
		adapterTo = new CMN_MessageToAdapter(CMN_ToUserListActivity.this, list);
		listView.setAdapter(adapterTo);
	}

	public void refreshSubjectList(ArrayList<CMN_MessageTopicsModel> list) {
		adapterSubject = new CMN_MessageTopicsAdapter(CMN_ToUserListActivity.this, list);
		listView.setAdapter(adapterSubject);
	}

	public void refreshPatientList(ArrayList<CMN_PatientModel> list) {
		adapterPatient = new CMN_MessagePatientAdapter(CMN_ToUserListActivity.this,
				list);
		listView.setAdapter(adapterPatient);
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
			return true;
		case R.id.action_done:
			if (to) {
				if (adapterTo.getData().size() == 0) {
					Toast.makeText(CMN_ToUserListActivity.this,
							"Please select a value", Toast.LENGTH_SHORT).show();

				} else {
					Intent intent = new Intent();
					intent.putExtra("data", (Serializable) adapterTo.getData());
					setResult(RESULT_OK, intent);
					finish();
				}
			} else if (patient) {
				if (adapterPatient.getData().size() == 0) {
					Toast.makeText(CMN_ToUserListActivity.this,
							"Please select a value", Toast.LENGTH_SHORT).show();

				} else {
					Intent intent = new Intent();
					intent.putExtra("data",
							(Serializable) adapterPatient.getData());
					setResult(RESULT_OK, intent);
					finish();
				}
			} else if (subject) {
				if (adapterSubject == null || adapterSubject.getData() == null
						|| adapterSubject.getData().getCode() == null) {
					Toast.makeText(CMN_ToUserListActivity.this,
							"Please select a value", Toast.LENGTH_SHORT).show();

				} else {
					Intent intent = new Intent();
					intent.putExtra("data",
							(Serializable) adapterSubject.getData());
					setResult(RESULT_OK, intent);
					finish();
				}
			}

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}
}
