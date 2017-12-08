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
import android.widget.AdapterView;
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
import com.coremobile.coreyhealth.scheduling.schedulingmodels.GetListItemModel;
import com.coremobile.coreyhealth.scheduling.schedulingmodels.LookupModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class LookupBySurgeonandPatient extends BaseActivityCMN implements NetworkCallBack, AsyncTaskCompleteListener<String> {

    Spinner _patient, _surgeon;
    ArrayList<LookupModel> lookupModels;
    ArrayList<GetListItemModel> surgeonModels;
    ProgressDialog progressDialog;
    List<String> allPatients, allSurgeons;
    Button _schedule;
    RelativeLayout _patientLayout, _surgeonLayout;
    boolean isSearchPatient = false;
    Intent intent;
    String DISPLAY_PATIENT_ON_DEVICE_URL;

    AsyncTaskCompleteListener<String> asyncTaskCompleteListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lookup_by_surgeonand_patient);
        ActivityPackage.setActivityList(this);

        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setTitle("Search by Surgeon and patient");
        getActionBar().setDisplayHomeAsUpEnabled(true);


        _schedule = (Button) findViewById(R.id.viewSchedule);
        _patient = (Spinner) findViewById(R.id.patientSpinner);
        _patientLayout = (RelativeLayout) findViewById(R.id.patientSpinnerLayout);

        _surgeon = (Spinner) findViewById(R.id.surgeonSpinner);
        _surgeonLayout = (RelativeLayout) findViewById(R.id.surgeonSpinnerLayout);

        if (getIntent() != null && getIntent().hasExtra("isSearchPatient")) {
            isSearchPatient = getIntent().getBooleanExtra("isSearchPatient", false);
            if (isSearchPatient)
                _schedule.setText("Show in Dashboard");
        }

        _schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (_patient.getSelectedItemPosition() == 0) {
                    return;
                }
                if (isSearchPatient) {
                    //TO-DO Code for search a patient
                    String patId = lookupModels.get(_patient.getSelectedItemPosition()).getID();
                    if (patId != null && !patId.trim().equalsIgnoreCase("")) {

                        DISPLAY_PATIENT_ON_DEVICE_URL = CMN_Preferences.getBaseUrl(LookupBySurgeonandPatient.this)
                                + "DisplayPatientInDevice.aspx?token=" + CMN_Preferences.getUserToken(LookupBySurgeonandPatient.this);


                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("PatientIds", patId);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        NetworkTools networkTools = NetworkTools.getInstance();
                        if (networkTools.checkNetworkConnection(LookupBySurgeonandPatient.this) && DISPLAY_PATIENT_ON_DEVICE_URL.length() > 0) {
                            //  JSONObject response = networkTools.postJsonData(LookupByExistingPatient.this, DISPLAY_PATIENT_ON_DEVICE_URL, jsonObject);

                            asyncTaskCompleteListener = LookupBySurgeonandPatient.this;

                            new BaseAsyncTask(getApplicationContext(), "POST", asyncTaskCompleteListener, jsonObject.toString()).execute(DISPLAY_PATIENT_ON_DEVICE_URL);

                        } else {
                            Toast.makeText(LookupBySurgeonandPatient.this, CMN_ErrorMessages.getInstance().getValue(12), Toast.LENGTH_SHORT).show();
                        }
                    } else {

                    }
                } else {
                    if (Utils.viewSchedule) {
                        Intent intent = new Intent(LookupBySurgeonandPatient.this, CMN_GenericViewActivity.class);
                        intent.putExtra("scheduling", "" + ViewScheduleActivity.jsonObject);
                        CMN_Preferences.setScheduleContextId(LookupBySurgeonandPatient.this, lookupModels.get(_patient.getSelectedItemPosition()).getID());
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(LookupBySurgeonandPatient.this, CalenderActivity.class);
                        CMN_Preferences.setScheduleContextId(LookupBySurgeonandPatient.this, lookupModels.get(_patient.getSelectedItemPosition()).getID());
                        startActivity(intent);
                    }
                }

            }
        });


        _surgeon.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                if (pos == 0) {
                    return;
                }
                NetworkAsyncTask networkAsyncTask = NetworkAsyncTask.getInstance();
                try {
                    networkAsyncTask.startNetworkCall(ThreadTaskIds.GET_LOOKUP_DATA, LookupBySurgeonandPatient.this);
                } catch (NetworkException e) {
                    e.getMessage();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        _patientLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _patient.performClick();
            }
        });
        _surgeonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _surgeon.performClick();
            }
        });


        progressDialog = new ProgressDialog(LookupBySurgeonandPatient.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);


        NetworkAsyncTask networkAsyncTask = NetworkAsyncTask.getInstance();
        try {
            networkAsyncTask.startNetworkCall(ThreadTaskIds.GET_LIST_ITEM, LookupBySurgeonandPatient.this);
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
        if (taskId == ThreadTaskIds.GET_LOOKUP_DATA) {
            String url = CMN_Preferences.getBaseUrl(LookupBySurgeonandPatient.this)
                    + "PatientLookup_s.aspx?token=" + CMN_Preferences.getUserToken(LookupBySurgeonandPatient.this) + "&all=false";

            NetworkTools networkTools = NetworkTools.getInstance();
            if (networkTools.checkNetworkConnection(LookupBySurgeonandPatient.this) && url.length() > 0) {
                JSONObject response = networkTools.postJsonData(LookupBySurgeonandPatient.this, url, getJSONObject());
                try {
                    if (response != null) {
                        parseJsonData(response, ThreadTaskIds.GET_LOOKUP_DATA);
                    }
                } catch (JSONException e) {
                    e.getMessage();
                }
            } else {
                Toast.makeText(LookupBySurgeonandPatient.this, CMN_ErrorMessages.getInstance().getValue(12), Toast.LENGTH_SHORT).show();
            }
        }
        if (taskId == ThreadTaskIds.GET_LIST_ITEM) {
            String url = CMN_Preferences.getBaseUrl(LookupBySurgeonandPatient.this)
                    + "GetListItems.aspx?token=" + CMN_Preferences.getUserToken(LookupBySurgeonandPatient.this) + "&ListName=Surgeons";

            NetworkTools networkTools = NetworkTools.getInstance();
            if (networkTools.checkNetworkConnection(LookupBySurgeonandPatient.this) && url.length() > 0) {
                JSONObject response = networkTools.getJsonData(LookupBySurgeonandPatient.this, url);
                try {
                    if (response != null) {
                        parseJsonData(response, ThreadTaskIds.GET_LIST_ITEM);
                    }
                } catch (JSONException e) {
                    e.getMessage();
                }
            } else {
                Toast.makeText(LookupBySurgeonandPatient.this, CMN_ErrorMessages.getInstance().getValue(12), Toast.LENGTH_SHORT).show();
            }
        }


        return null;
    }

    private JSONObject getJSONObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Surgeon", surgeonModels.get(_surgeon.getSelectedItemPosition()).getCode());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private void parseJsonData(JSONObject response, int id) throws JSONException {
        if (id == ThreadTaskIds.GET_LOOKUP_DATA) {
            if (response.getJSONObject("Result").getInt("Code") == 0) {
                lookupModels = new ArrayList<>();
                JSONArray messagesArray = response.getJSONArray("Data");
                if (messagesArray.length() == 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LookupBySurgeonandPatient.this, "No patient found for the given data.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }
                LookupModel patientModel;
                for (int i = 0; i < messagesArray.length(); i++) {
                    JSONObject jsonObject = messagesArray.getJSONObject(i);
                    patientModel = new LookupModel();
                    patientModel.setDisplayName(jsonObject.optString("DisplayName"));
                    patientModel.setAnonId(jsonObject.optString("AnonId"));
                    patientModel.setID(jsonObject.optString("ID"));
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
                        ArrayAdapter _adapter = new ArrayAdapter<String>(LookupBySurgeonandPatient.this,
                                android.R.layout.simple_spinner_item, allPatients);
                        _adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        _patient.setAdapter(_adapter);
                    }
                });

            }
        }
        if (id == ThreadTaskIds.GET_LIST_ITEM) {
            if (response.getJSONObject("Result").getInt("Code") == 0) {
                surgeonModels = new ArrayList<>();
                JSONObject data = response.getJSONObject("Data");
                JSONArray topicsArray = data
                        .getJSONArray("Surgeons");
                GetListItemModel topicModel;
                for (int i = 0; i < topicsArray.length(); i++) {
                    JSONObject jsonObject = topicsArray.getJSONObject(i);
                    topicModel = new GetListItemModel();
                    topicModel.setCode(jsonObject.getString("Code"));
                    topicModel.setTopicName(jsonObject.getString("Name"));
                    surgeonModels.add(topicModel);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        allSurgeons = new ArrayList<String>();
                        for (int i = 0; i < surgeonModels.size(); i++) {
                            allSurgeons.add(surgeonModels.get(i).getTopicName());
                        }
                        ArrayAdapter _adapter = new ArrayAdapter<String>(LookupBySurgeonandPatient.this,
                                android.R.layout.simple_spinner_item, allSurgeons);
                        _adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        _surgeon.setAdapter(_adapter);
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
        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(LookupBySurgeonandPatient.this, android.R.style.Theme_Dialog));
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

    String streamToString(InputStream stream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
        String line;
        String result = "";
        while ((line = bufferedReader.readLine()) != null) {
            result += line;
        }
        // Close stream
        if (null != stream) {
            stream.close();
        }
        return result;
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
