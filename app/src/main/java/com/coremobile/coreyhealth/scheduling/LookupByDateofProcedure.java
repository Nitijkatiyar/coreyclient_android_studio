package com.coremobile.coreyhealth.scheduling;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class LookupByDateofProcedure extends BaseActivityCMN implements NetworkCallBack, AsyncTaskCompleteListener<String> {


    RelativeLayout _dopLayout, _patientLayout;
    TextView _dop;
    Spinner _patient;
    ArrayList<LookupModel> lookupModels;
    ProgressDialog progressDialog;
    List<String> allPatients;

    private DatePickerDialog fromDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    private Calendar calendar;
    private int year, month, day;
    boolean isSearchPatient = false;
    Intent intent;
    Button _schedule;
    AsyncTaskCompleteListener<String> asyncTaskCompleteListener;
    String patId = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lookup_by_dateof_procedure);

        ActivityPackage.setActivityList(this);

        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setTitle("Search by Date of Procedure");
        getActionBar().setDisplayHomeAsUpEnabled(true);


        _dop = (TextView) findViewById(R.id.dateofProcedure);
        _dopLayout = (RelativeLayout) findViewById(R.id.dopLayout);
        _patient = (Spinner) findViewById(R.id.selectPatient);
        _schedule = (Button) findViewById(R.id.viewSchedule);

        if (getIntent() != null && getIntent().hasExtra("isSearchPatient")) {
            isSearchPatient = getIntent().getBooleanExtra("isSearchPatient", false);
          }
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        progressDialog = new ProgressDialog(LookupByDateofProcedure.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);


        _dopLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fromDatePickerDialog.show();
            }
        });

        progressDialog = new ProgressDialog(LookupByDateofProcedure.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                _dop.setText(dateFormatter.format(newDate.getTime()));

                NetworkAsyncTask networkAsyncTask = NetworkAsyncTask.getInstance();
                try {
                    networkAsyncTask.startNetworkCall(ThreadTaskIds.GET_LOOKUP_DATA, LookupByDateofProcedure.this);
                } catch (NetworkException e) {
                    e.getMessage();
                }
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        _schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isSearchPatient == true) {


                        NetworkAsyncTask networkAsyncTask = NetworkAsyncTask.getInstance();
                        try {
                            networkAsyncTask.startNetworkCall(ThreadTaskIds.GET_LOOKUP_DATA, LookupByDateofProcedure.this);
                        } catch (NetworkException e) {
                            e.getMessage();
                        }


                } else {
                    if (Utils.viewSchedule) {
                        Intent intent = new Intent(LookupByDateofProcedure.this, CMN_GenericViewActivity.class);
                        intent.putExtra("scheduling", "" + ViewScheduleActivity.jsonObject);
                        CMN_Preferences.setScheduleContextId(LookupByDateofProcedure.this, lookupModels.get(_patient.getSelectedItemPosition()).getID());
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(LookupByDateofProcedure.this, CalenderActivity.class);
                        CMN_Preferences.setScheduleContextId(LookupByDateofProcedure.this, lookupModels.get(_patient.getSelectedItemPosition()).getID());
                        startActivity(intent);
                    }
                }
            }


        });

        _patient.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (isSearchPatient) {
                    //TO-DO Code for search a patient
                    patId = lookupModels.get(_patient.getSelectedItemPosition()).getID();

                } else {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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
            String url = CMN_Preferences.getBaseUrl(LookupByDateofProcedure.this)
                    + "PatientLookup_s.aspx?token=" + CMN_Preferences.getUserToken(LookupByDateofProcedure.this);

            NetworkTools networkTools = NetworkTools.getInstance();
            if (networkTools.checkNetworkConnection(LookupByDateofProcedure.this) && url.length() > 0) {
                JSONObject response = networkTools.postJsonData(LookupByDateofProcedure.this, url, generateJson());
                try {
                    if (response != null) {
                        parseJsonData(response, ThreadTaskIds.GET_LOOKUP_DATA);
                    }
                } catch (JSONException e) {
                    e.getMessage();
                }
            } else {
                Toast.makeText(LookupByDateofProcedure.this, CMN_ErrorMessages.getInstance().getValue(12), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(LookupByDateofProcedure.this, "No patient found for the given Name.", Toast.LENGTH_SHORT).show();
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

//                            _schedule.setText("Show in Dashboard");
                        }
                        else{
  //                          _schedule.setText("Schedule");
                            for (int i = 0; i < lookupModels.size(); i++) {
                                allPatients.add(lookupModels.get(i).getDisplayName());
                            }
                            ArrayAdapter _adapter = new ArrayAdapter<String>(LookupByDateofProcedure.this,
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
                                    Intent intent = new Intent(LookupByDateofProcedure.this, CMN_GenericViewActivity.class);
                                    intent.putExtra("scheduling", "" + ViewScheduleActivity.jsonObject);
                                    CMN_Preferences.setScheduleContextId(LookupByDateofProcedure.this, lookupModels.get(_patient.getSelectedItemPosition()).getID());
                                    startActivity(intent);
                                } else {
                                    Intent intent = new Intent(LookupByDateofProcedure.this, CalenderActivity.class);
                                    CMN_Preferences.setScheduleContextId(LookupByDateofProcedure.this, lookupModels.get(_patient.getSelectedItemPosition()).getID());
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
            jsonObject.put("DOS", "" + _dop.getText().toString());
        } catch (JSONException e) {
            e.getMessage();
        }

        return jsonObject;
    }

    private void showAlert(String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(LookupByDateofProcedure.this, android.R.style.Theme_Dialog));
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