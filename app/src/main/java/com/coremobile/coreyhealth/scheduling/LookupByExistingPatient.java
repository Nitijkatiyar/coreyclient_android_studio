package com.coremobile.coreyhealth.scheduling;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.coremobile.coreyhealth.AsyncTaskCompleteListener;
import com.coremobile.coreyhealth.BaseActivityCMN;
import com.coremobile.coreyhealth.BaseAsyncTask;
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

public class LookupByExistingPatient extends BaseActivityCMN implements NetworkCallBack, AsyncTaskCompleteListener<String> {

    Spinner _patient;
    ArrayList<LookupModel> lookupModels;
    ProgressDialog progressDialog;
    List<String> allPatients;
    Button _schedule;
    RelativeLayout _patientLayout;
    boolean isSearchPatient = false;
    Intent intent;
    String DISPLAY_PATIENT_ON_DEVICE_URL;

    AsyncTaskCompleteListener<String> asyncTaskCompleteListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lookup_by_existing_patient);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setTitle("Search by Existing patient");
        ActivityPackage.setActivityList(this);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        _schedule = (Button) findViewById(R.id.viewSchedule);
        if (getIntent() != null && getIntent().hasExtra("isSearchPatient")) {
            isSearchPatient = getIntent().getBooleanExtra("isSearchPatient", false);
            if (isSearchPatient)
                _schedule.setText("Show in Dashboard");
        }

        _patient = (Spinner) findViewById(R.id.patientSpinner);
        _patientLayout = (RelativeLayout) findViewById(R.id.patientSpinnerLayout);

        _patientLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _patient.performClick();
            }
        });

        progressDialog = new ProgressDialog(LookupByExistingPatient.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        _schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(_patient.getSelectedItemPosition() == 0){
                    return;
                }
                if (isSearchPatient) {
                    //TO-DO Code for search a patient

                    String patId = lookupModels.get(_patient.getSelectedItemPosition()).getID();
                    if (patId != null && !patId.trim().equalsIgnoreCase("")) {

                        DISPLAY_PATIENT_ON_DEVICE_URL = CMN_Preferences.getBaseUrl(LookupByExistingPatient.this)
                                + "DisplayPatientInDevice.aspx?token=" + CMN_Preferences.getUserToken(LookupByExistingPatient.this);


                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("PatientIds", patId);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        NetworkTools networkTools = NetworkTools.getInstance();
                        if (networkTools.checkNetworkConnection(LookupByExistingPatient.this) && DISPLAY_PATIENT_ON_DEVICE_URL.length() > 0) {
                            //  JSONObject response = networkTools.postJsonData(LookupByExistingPatient.this, DISPLAY_PATIENT_ON_DEVICE_URL, jsonObject);
                            //      new UploadDataAsyncTask(jsonObject.toString()).execute(DISPLAY_PATIENT_ON_DEVICE_URL);

                            asyncTaskCompleteListener = LookupByExistingPatient.this;
                            new BaseAsyncTask(getApplicationContext(), "POST", asyncTaskCompleteListener, jsonObject.toString()).execute(DISPLAY_PATIENT_ON_DEVICE_URL);
                        } else {
                            Toast.makeText(LookupByExistingPatient.this, CMN_ErrorMessages.getInstance().getValue(12), Toast.LENGTH_SHORT).show();
                        }
                    } else {

                    }
                } else {
                    if (Utils.viewSchedule) {
                        Intent intent = new Intent(LookupByExistingPatient.this, CMN_GenericViewActivity.class);
                        intent.putExtra("scheduling", "" + ViewScheduleActivity.jsonObject);
                        CMN_Preferences.setScheduleContextId(LookupByExistingPatient.this, lookupModels.get(_patient.getSelectedItemPosition()).getID());
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(LookupByExistingPatient.this, CalenderActivity.class);
                        CMN_Preferences.setScheduleContextId(LookupByExistingPatient.this, lookupModels.get(_patient.getSelectedItemPosition()).getID());
                        startActivity(intent);
                    }

                }
            }
        });


        NetworkAsyncTask networkAsyncTask = NetworkAsyncTask.getInstance();
        try {
            networkAsyncTask.startNetworkCall(ThreadTaskIds.GET_PATIENT_BY_PROVIDER, LookupByExistingPatient.this);
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
            String url = CMN_Preferences.getBaseUrl(LookupByExistingPatient.this)
                    + "GetAllPatients.aspx?token=" + CMN_Preferences.getUserToken(LookupByExistingPatient.this) + "&all=true";

            NetworkTools networkTools = NetworkTools.getInstance();
            if (networkTools.checkNetworkConnection(LookupByExistingPatient.this) && url.length() > 0) {
                JSONObject response = networkTools.getJsonData(LookupByExistingPatient.this, url);
                try {
                    if (response != null) {
                        parseJsonData(response, ThreadTaskIds.GET_PATIENT_BY_PROVIDER);
                    }
                } catch (JSONException e) {
                    e.getMessage();
                }
            } else {
                Toast.makeText(LookupByExistingPatient.this, CMN_ErrorMessages.getInstance().getValue(12), Toast.LENGTH_SHORT).show();
            }
        }


        return null;
    }

    private void parseJsonData(JSONObject response, int id) throws JSONException {
        if (id == ThreadTaskIds.GET_PATIENT_BY_PROVIDER) {
            if (response.getJSONObject("Result").getInt("Code") == 0) {
                lookupModels = new ArrayList<>();
                LookupModel patientModel;
                JSONArray messagesArray = response.getJSONArray("Data");
                for (int i = 0; i < messagesArray.length(); i++) {
                    JSONObject jsonObject = messagesArray.getJSONObject(i);
                    patientModel = new LookupModel();
                    patientModel.setDisplayName(jsonObject.optString("Name"));
                    patientModel.setAnonId(jsonObject.optString("AnonId"));
                    patientModel.setID(jsonObject.optString("Id"));
                    patientModel.setDateOfSurery(jsonObject.optString("DOS"));
                    patientModel.setSurgeon(jsonObject.optString("Surgeon"));
                    lookupModels.add(patientModel);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        allPatients = new ArrayList<String>();
                        for (int i = 0; i < lookupModels.size(); i++) {
                            allPatients.add(lookupModels.get(i).getDisplayName());
                        }
                        ArrayAdapter _adapter = new ArrayAdapter<String>(LookupByExistingPatient.this,
                                android.R.layout.simple_spinner_item, allPatients);
                        _adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        _patient.setAdapter(_adapter);
                    }
                });

            }
        }

    }

    @Override
    public Object onNetworkError(int taskId, NetworkException o) {
        return null;
    }

    /**
     * Method to show alert dialog
     */
    private void showAlert(String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(LookupByExistingPatient.this, android.R.style.Theme_Dialog));
        builder.setMessage(message).setTitle("Message")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    /**
     * Parsing the feed results and get the list
     *
     * @param result
     */
    private String parseResult(String result) {
        String message = null;
        try {
            JSONObject response = new JSONObject(result);

            JSONObject posts = response.getJSONObject("Result");
            message = posts.getString("Message");


        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            return message;
        }
    }


    @Override
    public void onTaskStart() {
        if (progressDialog != null && !progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    @Override
    public void onTaskComplete(String result) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        Log.e("dd", result);
        showAlert(parseResult(result));
    }


}
