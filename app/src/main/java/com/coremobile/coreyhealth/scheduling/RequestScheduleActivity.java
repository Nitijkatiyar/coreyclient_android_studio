package com.coremobile.coreyhealth.scheduling;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.coremobile.coreyhealth.BaseActivityCMN;
import com.coremobile.coreyhealth.R;
import com.coremobile.coreyhealth.Utils;
import com.coremobile.coreyhealth.errorhandling.ActivityPackage;
import com.coremobile.coreyhealth.errorhandling.CMN_ErrorMessages;
import com.coremobile.coreyhealth.genericviewandsurvey.CMN_GenericViewActivity;
import com.coremobile.coreyhealth.googleAnalytics.CMN_Preferences;
import com.coremobile.coreyhealth.networkutils.JsonParsingException;
import com.coremobile.coreyhealth.networkutils.NetworkAsyncTask;
import com.coremobile.coreyhealth.networkutils.NetworkCallBack;
import com.coremobile.coreyhealth.networkutils.NetworkException;
import com.coremobile.coreyhealth.networkutils.NetworkTools;
import com.coremobile.coreyhealth.networkutils.ThreadTaskIds;
import com.coremobile.coreyhealth.scheduling.schedulingcalender.CalenderActivity;
import com.coremobile.coreyhealth.scheduling.schedulingmodels.LookupModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RequestScheduleActivity extends BaseActivityCMN implements NetworkCallBack {

    Button _patientLookup;
    Spinner _patient;
    RelativeLayout _patientLayout;
    ArrayList<LookupModel> assignmentPatientModels;
    ProgressDialog progressDialog;
    List<String> allPatients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_schedule);
        ActivityPackage.setActivityList(this);

        _patientLookup = (Button) findViewById(R.id.patientLookup);
        _patient = (Spinner) findViewById(R.id.patientSpinner);
        _patientLayout = (RelativeLayout) findViewById(R.id.patientSpinnerLayout);

        _patientLookup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RequestScheduleActivity.this, PatientLookupActivity.class));

            }
        });
        _patientLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _patient.performClick();
            }
        });

        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setTitle("Request Scheduling");
        getActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(RequestScheduleActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        NetworkAsyncTask networkAsyncTask = NetworkAsyncTask.getInstance();
        try {
            networkAsyncTask.startNetworkCall(ThreadTaskIds.GET_PATIENT_BY_PROVIDER, RequestScheduleActivity.this);
        } catch (NetworkException e) {
            e.getMessage();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void beforeNetworkCall(int taskId) {
        if (progressDialog != null && !progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    @Override
    public Object afterNetworkCall(int taskId, Object o) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        return null;
    }


    @Override
    public Object onNetworkCall(int taskId, Object o) throws JsonParsingException, JsonParsingException {
        if (taskId == ThreadTaskIds.GET_PATIENT_BY_PROVIDER) {
            String url = CMN_Preferences.getBaseUrl(RequestScheduleActivity.this)
                    + "GetAllPatients.aspx?token=" + CMN_Preferences.getUserToken(RequestScheduleActivity.this) + "&all=false";

            NetworkTools networkTools = NetworkTools.getInstance();
            if (networkTools.checkNetworkConnection(RequestScheduleActivity.this) && url.length() > 0) {
                JSONObject response = networkTools.getJsonData(RequestScheduleActivity.this, url);
                try {
                    if (response != null) {
                        parseJsonData(response, ThreadTaskIds.GET_PATIENT_BY_PROVIDER);
                    }
                } catch (JSONException e) {
                    e.getMessage();
                }
            } else {
                Toast.makeText(RequestScheduleActivity.this, CMN_ErrorMessages.getInstance().getValue(12), Toast.LENGTH_SHORT).show();
            }
        }


        return null;
    }

    private void parseJsonData(JSONObject response, int id) throws JSONException {
        if (id == ThreadTaskIds.GET_PATIENT_BY_PROVIDER) {
            if (response.getJSONObject("Result").getInt("Code") == 0) {
                assignmentPatientModels = new ArrayList<>();
                LookupModel patientModel = new LookupModel();
                patientModel.setDisplayName("");
                patientModel.setSurgeon("");
                patientModel.setDateOfSurery("");
                patientModel.setID("");
                patientModel.setAnonId("");
                assignmentPatientModels.add(patientModel);
                JSONArray messagesArray = response.getJSONArray("Data");
                for (int i = 0; i < messagesArray.length(); i++) {
                    JSONObject jsonObject = messagesArray.getJSONObject(i);
                    patientModel = new LookupModel();
                    patientModel.setDisplayName(jsonObject.optString("Name"));
                    patientModel.setAnonId(jsonObject.optString("AnonId"));
                    patientModel.setID(jsonObject.optString("Id"));
                    patientModel.setDateOfSurery(jsonObject.optString("DOS"));
                    patientModel.setSurgeon(jsonObject.optString("Surgeon"));
                    assignmentPatientModels.add(patientModel);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        allPatients = new ArrayList<String>();
                        for (int i = 0; i < assignmentPatientModels.size(); i++) {
                            allPatients.add(assignmentPatientModels.get(i).getDisplayName());
                        }
                        ArrayAdapter _adapter = new ArrayAdapter<String>(RequestScheduleActivity.this,
                                android.R.layout.simple_spinner_item, allPatients);
                        _adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        _patient.setAdapter(_adapter);

                        _patient.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                if (i > 0) {
                                    if(Utils.viewSchedule) {
                                        Intent intent = new Intent(RequestScheduleActivity.this, CMN_GenericViewActivity.class);
                                        intent.putExtra("scheduling", "" + ViewScheduleActivity.jsonObject);
                                        CMN_Preferences.setScheduleContextId(RequestScheduleActivity.this, assignmentPatientModels.get(i).getID());
                                        startActivity(intent);

                                    }else{
                                        Intent intent = new Intent(RequestScheduleActivity.this, CalenderActivity.class);
                                        CMN_Preferences.setScheduleContextId(RequestScheduleActivity.this, assignmentPatientModels.get(i).getID());
                                        startActivity(intent);

                                    }
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });
                    }
                });

            }
        }

    }

    @Override
    public Object onNetworkError(int taskId, NetworkException o) {
        return null;
    }

}
