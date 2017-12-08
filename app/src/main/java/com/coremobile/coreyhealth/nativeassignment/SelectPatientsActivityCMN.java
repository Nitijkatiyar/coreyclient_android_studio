package com.coremobile.coreyhealth.nativeassignment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.coremobile.coreyhealth.MyApplication;
import com.coremobile.coreyhealth.R;
import com.coremobile.coreyhealth.base.CMN_AppBaseActivity;
import com.coremobile.coreyhealth.errorhandling.CMN_ErrorMessages;
import com.coremobile.coreyhealth.googleAnalytics.CMN_Preferences;
import com.coremobile.coreyhealth.networkutils.JsonParsingException;
import com.coremobile.coreyhealth.networkutils.NetworkAsyncTask;
import com.coremobile.coreyhealth.networkutils.NetworkCallBack;
import com.coremobile.coreyhealth.networkutils.NetworkException;
import com.coremobile.coreyhealth.networkutils.NetworkTools;
import com.coremobile.coreyhealth.networkutils.ThreadTaskIds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class SelectPatientsActivityCMN extends CMN_AppBaseActivity implements NetworkCallBack {
    private static String TAG = "Corey_AssignmentsActivityCMN";
    MyApplication application;

    private SharedPreferences mCurrentUserPref;
    public static final String CURRENT_USER = "CurrentUser";
    private String organizationName, user_category;

    ListView assignmentListView;
    LinearLayout progressBar;
    ArrayList<AssignmentPatientModel> assignmentPatientModels;
    AllPatientsAdapter allPatientsAdapter;
    boolean single_selection = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignments);


        assignmentListView = (ListView) findViewById(R.id.assignmetnsListView);
        findViewById(R.id.row0).setVisibility(View.GONE);


        mCurrentUserPref = getSharedPreferences(CURRENT_USER, 0);
        organizationName = mCurrentUserPref.getString("Organization", null);

        progressBar = (LinearLayout) findViewById(R.id.progressbar);

        application = (MyApplication) getApplication();
        user_category = mCurrentUserPref.getString("user_category", null);

        getActionBar().setHomeButtonEnabled(true);
        if (getIntent() != null && getIntent().hasExtra("title")) {
            getActionBar().setTitle(
                    "" + getIntent().getStringExtra("title"));
        } else {
            getActionBar().setTitle(
                    "Patients");
        }if (getIntent() != null && getIntent().hasExtra("single_selection")) {
            single_selection = true;
        }
        getActionBar().setDisplayHomeAsUpEnabled(true);

        NetworkAsyncTask networkAsyncTask = NetworkAsyncTask.getInstance();
        try {
            networkAsyncTask.startNetworkCall(ThreadTaskIds.GET_PATIENT_BY_PROVIDER, SelectPatientsActivityCMN.this);
        } catch (NetworkException e) {
            e.getMessage();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_save);
        MenuItem selectAll = menu.findItem(R.id.action_selectall);
        if(single_selection){
            selectAll.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.selassignmentmenu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.action_save:
                if (allPatientsAdapter.getData().size() == 0) {
                    Toast.makeText(SelectPatientsActivityCMN.this,
                            "Please select a patient", Toast.LENGTH_SHORT).show();

                } else {
                    Intent intent = new Intent();
                    intent.putExtra("data",
                            (Serializable) allPatientsAdapter.getData());
                    setResult(RESULT_OK, intent);
                    finish();
                }
                return true;
            case R.id.action_selectall:
                if (allPatientsAdapter.isAllSelected) {
                    allPatientsAdapter.deselectAll();
                } else {
                    allPatientsAdapter.selectAll();
                }
                allPatientsAdapter.notifyDataSetChanged();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }

    }


    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    public void beforeNetworkCall(int taskId) {
        if (progressBar.getVisibility() == View.GONE) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public Object afterNetworkCall(int taskId, Object o) {
        if (progressBar.getVisibility() == View.VISIBLE) {
            progressBar.setVisibility(View.GONE);
        }
        return null;
    }


    @Override
    public Object onNetworkCall(int taskId, Object o) throws JsonParsingException, JsonParsingException {
        if (taskId == ThreadTaskIds.GET_PATIENT_BY_PROVIDER) {
            String url = CMN_Preferences.getBaseUrl(SelectPatientsActivityCMN.this)
                    + "GetAllPatientsInOrg.aspx?token=" + CMN_Preferences.getUserToken(SelectPatientsActivityCMN.this);

            NetworkTools networkTools = NetworkTools.getInstance();
            if (networkTools.checkNetworkConnection(SelectPatientsActivityCMN.this) && url.length() > 0) {
                JSONObject response = networkTools.getJsonData(SelectPatientsActivityCMN.this, url);
                try {
                    if (response != null) {
                        parseJsonData(response, ThreadTaskIds.GET_PATIENT_BY_PROVIDER);
                    }
                } catch (JSONException e) {
                    e.getMessage();
                }
            } else {
                Toast.makeText(SelectPatientsActivityCMN.this, CMN_ErrorMessages.getInstance().getValue(12), Toast.LENGTH_SHORT).show();
            }
        }


        return null;
    }

    private void parseJsonData(JSONObject response, int id) throws JSONException {
        if (id == ThreadTaskIds.GET_PATIENT_BY_PROVIDER) {
            if (response.getJSONObject("Result").getInt("Code") == 0) {
                assignmentPatientModels = new ArrayList<>();
                JSONArray messagesArray = response.getJSONArray("Data");
                for (int i = 0; i < messagesArray.length(); i++) {
                    JSONObject jsonObject = messagesArray.getJSONObject(i);
                    AssignmentPatientModel patientModel = new AssignmentPatientModel();
                    patientModel.setPatientId(jsonObject.getString("patientid"));
                    patientModel.setSurgeryTime(jsonObject.getString("SurgeryTime"));
                    patientModel.setPatientName(jsonObject.getString("name"));
                    assignmentPatientModels.add(patientModel);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        allPatientsAdapter = new AllPatientsAdapter(SelectPatientsActivityCMN.this, assignmentPatientModels);
                        assignmentListView.setAdapter(allPatientsAdapter);
                    }
                });

            }
        }

    }

    @Override
    public Object onNetworkError(int taskId, NetworkException o) {
        return null;
    }

    @Override
    public void onBackPressed() {
        if (progressBar.getVisibility() == View.VISIBLE) {

        } else {
            super.onBackPressed();
        }
    }
}
