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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.coremobile.coreyhealth.AsyncTaskCompleteListener;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class LookupByMobilePhoneNumber extends BaseActivityCMN implements NetworkCallBack, AsyncTaskCompleteListener<String> {

    EditText _mobileNUmber;
    Spinner _patient;
    ArrayList<LookupModel> lookupModels;
    ProgressDialog progressDialog;
    List<String> allPatients;
    Button _search;
    boolean isSearchPatient = false;
    Intent intent;
    AsyncTaskCompleteListener<String> asyncTaskCompleteListener;
    String patId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lookup_by_mobile_phone_number);
        ActivityPackage.setActivityList(this);

        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setTitle("Search by Mobile phone number");
        getActionBar().setDisplayHomeAsUpEnabled(true);


        _mobileNUmber = (EditText) findViewById(R.id.mobileNumber);
        _patient = (Spinner) findViewById(R.id.selectPatient);
        _search = (Button) findViewById(R.id.searchPatient);


        if (getIntent() != null && getIntent().hasExtra("isSearchPatient")) {
            isSearchPatient = getIntent().getBooleanExtra("isSearchPatient", false);

        }
        progressDialog = new ProgressDialog(LookupByMobilePhoneNumber.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        _search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                NetworkAsyncTask networkAsyncTask = NetworkAsyncTask.getInstance();
                try {
                    networkAsyncTask.startNetworkCall(ThreadTaskIds.GET_LOOKUP_DATA, LookupByMobilePhoneNumber.this);
                } catch (NetworkException e) {
                    e.getMessage();

                }
            }
        });


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
    public Object onNetworkCall(int taskId, Object o) throws
            JsonParsingException, JsonParsingException {
        if (taskId == ThreadTaskIds.GET_LOOKUP_DATA) {
            String url = CMN_Preferences.getBaseUrl(LookupByMobilePhoneNumber.this)
                    + "PatientLookup_s.aspx?token=" + CMN_Preferences.getUserToken(LookupByMobilePhoneNumber.this);

            NetworkTools networkTools = NetworkTools.getInstance();
            if (networkTools.checkNetworkConnection(LookupByMobilePhoneNumber.this) && url.length() > 0) {
                JSONObject response = networkTools.postJsonData(LookupByMobilePhoneNumber.this, url, generateJson());
                try {
                    if (response != null) {
                        parseJsonData(response, ThreadTaskIds.GET_LOOKUP_DATA);
                    }
                } catch (JSONException e) {
                    e.getMessage();
                }
            } else {
                Toast.makeText(LookupByMobilePhoneNumber.this, CMN_ErrorMessages.getInstance().getValue(12), Toast.LENGTH_SHORT).show();
            }
        }


        return null;
    }

    private void parseJsonData(JSONObject response, int id) throws JSONException {
        if (id == ThreadTaskIds.GET_LOOKUP_DATA) {
            if (response.getJSONObject("Result").getInt("Code") == 0) {
                JSONArray messagesArray = response.getJSONArray("Data");
                if (messagesArray.length() == 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LookupByMobilePhoneNumber.this, "No patient found for the given Name.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }
                lookupModels = new ArrayList<>();

                for (int i = 0; i < messagesArray.length(); i++) {
                    JSONObject jsonObject = messagesArray.getJSONObject(i);
                    LookupModel lookupModel = new LookupModel();
                    lookupModel.setAnonId(jsonObject.optString("AnonId"));
                    lookupModel.setDateOfSurery(jsonObject.optString("DOS"));
                    lookupModel.setDisplayName(jsonObject.optString("DisplayName"));
                    lookupModel.setID(jsonObject.optString("ID"));
                    lookupModel.setSurgeon(jsonObject.optString("Surgeon"));
                    lookupModels.add(lookupModel);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        allPatients = new ArrayList<String>();

                        if(isSearchPatient) {
                            Intent intent = new Intent(getApplicationContext(), CMN_PatientOnDeviceActivity.class);


                            intent.putExtra("DATA", lookupModels);


                            startActivity(intent);
                        }
                        else{
                            _search.setText("Schedule");
                            for (int i = 0; i < lookupModels.size(); i++) {
                                allPatients.add(lookupModels.get(i).getDisplayName());
                            }
                            ArrayAdapter _adapter = new ArrayAdapter<String>(LookupByMobilePhoneNumber.this,
                                    android.R.layout.simple_spinner_item, allPatients);
                            _adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            _patient.setAdapter(_adapter);
                            _patient.performClick();

                        }
                        _patient.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                if(i==0){
                                    return;
                                }
                                if (Utils.viewSchedule) {
                                    Intent intent = new Intent(LookupByMobilePhoneNumber.this, CMN_GenericViewActivity.class);
                                    intent.putExtra("scheduling", "" + ViewScheduleActivity.jsonObject);
                                    CMN_Preferences.setScheduleContextId(LookupByMobilePhoneNumber.this, lookupModels.get(_patient.getSelectedItemPosition()).getID());
                                    startActivity(intent);
                                } else {
                                    Intent intent = new Intent(LookupByMobilePhoneNumber.this, CalenderActivity.class);
                                    CMN_Preferences.setScheduleContextId(LookupByMobilePhoneNumber.this, lookupModels.get(_patient.getSelectedItemPosition()).getID());
                                    startActivity(intent);
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

    public JSONObject generateJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("PhoneNo", "" + _mobileNUmber.getText().toString().trim());
        } catch (JSONException e) {
            e.getMessage();
        }

        return jsonObject;
    }


    /**
     * Method to show alert dialog
     */
    private void showAlert(String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(LookupByMobilePhoneNumber.this, android.R.style.Theme_Dialog));
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
