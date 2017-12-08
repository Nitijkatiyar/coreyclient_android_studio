package com.coremobile.coreyhealth.eproresponses;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.coremobile.coreyhealth.*;
import com.coremobile.coreyhealth.BaseAsyncTask;
import com.coremobile.coreyhealth.googleAnalytics.CMN_Preferences;
import com.coremobile.coreyhealth.networkutils.NetworkTools;
import com.coremobile.coreyhealth.newui.MessageTabActivityCMN;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CMN_EproResponseListActivity extends Activity implements com.coremobile.coreyhealth.AsyncTaskCompleteListener<String> {


    Spinner spinnerVw;
    static String vToken, vPatientId;
    static int ASYNC_SELECT;
    String PATIENT_URL;
    String EPRO_RSPONSE_URL;
    com.coremobile.coreyhealth.BaseAsyncTask baseAsyncTask;

    ProgressDialog progressDialog;

    ArrayAdapter<String> spinnerDataAdapter;
    EproListRowViewAdapter eproListRowViewAdapter;
    ListView listView;

    List<String> patientList = new ArrayList<>();
    List<String> patientIdList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_epro_response_list);

        spinnerVw = (Spinner) findViewById(R.id.selectPatientSpinnerVw);

        listView = (ListView) findViewById(R.id.responseListVw);
        vToken = CMN_Preferences.getUserToken(getApplicationContext());
        String title = "ePRO Response";
        if (getIntent().hasExtra("objname")) {
            title = getIntent().getExtras().getString("objname");
        }

        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setTitle(title);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        PATIENT_URL =
                MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE + "GetAllPatients.aspx?token=" + vToken;

        NetworkTools networkTools = NetworkTools.getInstance();


        progressDialog = new ProgressDialog(CMN_EproResponseListActivity.this);
        progressDialog.setMessage("Processing..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        if (!networkTools.checkNetworkConnection(CMN_EproResponseListActivity.this)) {

            return;
        }

        if (networkTools.checkNetworkConnection(CMN_EproResponseListActivity.this)) {

            baseAsyncTask = new com.coremobile.coreyhealth.BaseAsyncTask(getApplicationContext(),"GET",this,null);
            baseAsyncTask.execute(PATIENT_URL);

            ASYNC_SELECT = 0;
            spinnerDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, patientList);

            // Drop down layout style - list view with radio button
            spinnerDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // attaching data adapter to spinner
            spinnerVw.setAdapter(spinnerDataAdapter);

            eproListRowViewAdapter = new EproListRowViewAdapter(getApplicationContext(), responseList);
            listView.setAdapter(eproListRowViewAdapter);
        } else {
            Toast.makeText(getApplicationContext(), "Please check your internet connection.", Toast.LENGTH_SHORT).show();
        }
        spinnerVw.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                EPRO_RSPONSE_URL = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE + "GetPatientePROResponses.aspx?token=" + vToken + "&patientid=" + patientIdList.get(position);
                NetworkTools networkTools = NetworkTools.getInstance();

                if (networkTools.checkNetworkConnection(CMN_EproResponseListActivity.this)) {

                    ASYNC_SELECT = 1;
                    progressDialog = new ProgressDialog(CMN_EproResponseListActivity.this);
                    progressDialog.setMessage("Processing..");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    baseAsyncTask.execute(EPRO_RSPONSE_URL);
                } else {
                    Toast.makeText(getApplicationContext(), "Please check your internet connection.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getApplicationContext(), CMN_EproDetailsActivity.class);


                try {
                    intent.putExtra("epro", responseList.get(position).toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                startActivity(intent);
            }
        });
    }

    @Override
    public void onTaskComplete(String str) {


        progressDialog.dismiss();
        if (ASYNC_SELECT == 0) {

            boolean result = parsePatientList(str);

            if (result == false) {
                Toast.makeText(getApplicationContext(), "Some Problem occurs.", Toast.LENGTH_LONG).show();
            } else {

                baseAsyncTask = new com.coremobile.coreyhealth.BaseAsyncTask(getApplicationContext(),"GET", this,null);

                spinnerDataAdapter.notifyDataSetChanged();
            }
        } else if (ASYNC_SELECT == 1) {
            responseList.clear();
            parseEproRespnse(str);
            baseAsyncTask = new BaseAsyncTask(getApplicationContext(),"GET",this,null);

            eproListRowViewAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onTaskStart(){

    }

    public Boolean parsePatientList(String str) {

        try {
            JSONObject jsonObject = new JSONObject(str);
            JSONObject jsonObject1 = jsonObject.getJSONObject("Result");
            if (!jsonObject1.getString("Message").equalsIgnoreCase("SUCCESS")) {
                return false;
            } else {
                JSONArray jsonArray = jsonObject.getJSONArray("Data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject2 = (JSONObject) jsonArray.get(i);
                    patientList.add(jsonObject2.getString("Name"));
                    patientIdList.add(jsonObject2.getString("Id"));

                }
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return false;
    }

    List<JSONObject> responseList = new ArrayList<>();

    public List<JSONObject> parseEproRespnse(String str) {

        if (str == null || str.equalsIgnoreCase("null")) {
            Toast.makeText(CMN_EproResponseListActivity.this, "No responses to display", Toast.LENGTH_SHORT).show();
            return new ArrayList<>();
        }else  {
            try {
                JSONObject object = new JSONObject(str);
                if (object.getJSONObject("Result").getInt("Code") == 0){
                    JSONArray jsonArray = object.getJSONArray("Data");
                    if(jsonArray.length() == 0){
                        Toast.makeText(CMN_EproResponseListActivity.this, "No responses to display", Toast.LENGTH_SHORT).show();
                        return new ArrayList<>();
                    }
                }
            } catch (JSONException e) {

                    e.printStackTrace();
            }
        }

        try {
            JSONObject jsonObject = new JSONObject(str);
            JSONArray jsonArray = jsonObject.getJSONArray("Data");
            for (int i = 0; i < jsonArray.length(); i++) {
                responseList.add((JSONObject) jsonArray.get(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseList;
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

}
