package com.coremobile.coreyhealth.scheduling;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.coremobile.coreyhealth.AsyncTaskCompleteListener;
import com.coremobile.coreyhealth.BaseAsyncTask;
import com.coremobile.coreyhealth.R;
import com.coremobile.coreyhealth.base.CMN_AppBaseActivity;
import com.coremobile.coreyhealth.errorhandling.CMN_ErrorMessages;
import com.coremobile.coreyhealth.googleAnalytics.CMN_Preferences;
import com.coremobile.coreyhealth.networkutils.NetworkTools;
import com.coremobile.coreyhealth.scheduling.schedulingmodels.LookupModel;
import com.coremobile.coreyhealth.uninvite_reinvite.PatientInfoRowViewAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class CMN_PatientOnDeviceActivity extends CMN_AppBaseActivity implements AsyncTaskCompleteListener<String> {

    ArrayList<LookupModel> allPatients;
    ListView pateintlistVw;
    PatientInfoRowViewAdapter patientInfoRowViewAdapter;
    public SparseBooleanArray mCheckStates;

    AsyncTaskCompleteListener<String> asyncTaskCompleteListener;

    static String deactivate_reactivate_patient_url = null;
    ProgressDialog progressDialog;
    boolean flag=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cmn__patient_display);

        pateintlistVw = (ListView) findViewById(R.id.pateintListVw);

        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setTitle("Patient List");
        getActionBar().setDisplayHomeAsUpEnabled(true);


        if (getIntent().hasExtra("DATA")) {
            allPatients = (ArrayList<LookupModel>) getIntent().getExtras().get("DATA");

            //   gson.fromJson(data,)

            mCheckStates = new SparseBooleanArray(allPatients.size());
            patientInfoRowViewAdapter = new PatientInfoRowViewAdapter(getApplicationContext(), allPatients);
            pateintlistVw.setAdapter(patientInfoRowViewAdapter);
        }
        if (getIntent().hasExtra("radio")) {
            if (getIntent().getExtras().getBoolean("radio") == true) {
                getActionBar().setTitle("Un-Invite Patients");
            } else {

                getActionBar().setTitle("Re-Invite patients");
            }


        }

        progressDialog = new ProgressDialog(CMN_PatientOnDeviceActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        pateintlistVw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {

                LookupModel dataModel = allPatients.get(position);
                dataModel.selected = !dataModel.selected;
                patientInfoRowViewAdapter.notifyDataSetChanged();

                mCheckStates.put(position, dataModel.selected);

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.displayondevicedashboardmenu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.action_display_on_device:

                List<String> selectedPatientIds = new ArrayList<>();
                for (int i = 0; i < mCheckStates.size(); i++) {


                    int key = mCheckStates.keyAt(i);
                    // get the object by the key.
                    Boolean obj = mCheckStates.get(key);
                    if(obj==true) {
                        selectedPatientIds.add(allPatients.get(key).getID());
                        flag=true;
                    }

                }

                if(flag==false){
                    Toast.makeText(getApplicationContext(),"No patient selected",Toast.LENGTH_SHORT).show();
                    return false;
                }

                JSONObject jsonObject = new JSONObject();
                try {


                   // String ids = builder.toString();
                    String data=listToCsv(selectedPatientIds,',');
                    jsonObject.put("PatientIds", data);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                deactivate_reactivate_patient_url = CMN_Preferences.getBaseUrl(CMN_PatientOnDeviceActivity.this)
                        + "DisplayPatientInDevice.aspx?token=" + CMN_Preferences.getUserToken(CMN_PatientOnDeviceActivity.this);

                NetworkTools networkTools = NetworkTools.getInstance();
                if (networkTools.checkNetworkConnection(CMN_PatientOnDeviceActivity.this) && deactivate_reactivate_patient_url.length() > 0) {

                    asyncTaskCompleteListener = CMN_PatientOnDeviceActivity.this;
                    new BaseAsyncTask(getApplicationContext(), "POST", asyncTaskCompleteListener, jsonObject.toString()).execute(deactivate_reactivate_patient_url);
                } else {
                    Toast.makeText(CMN_PatientOnDeviceActivity.this, CMN_ErrorMessages.getInstance().getValue(12), Toast.LENGTH_SHORT).show();

                }


            default:
                return super.onOptionsItemSelected(item);
        }
    }
    String listToCsv(List<String> listOfStrings, char separator) {
        StringBuilder sb = new StringBuilder();

        // all but last
        for(int i = 0; i < listOfStrings.size() - 1 ; i++) {
            sb.append(listOfStrings.get(i));
            sb.append(separator);
        }

        // last string, no separator
        if(listOfStrings.size() > 0){
            sb.append(listOfStrings.get(listOfStrings.size()-1));
        }

        return sb.toString();
    }

    /**
     * Method to show alert dialog
     */
    private void showAlert(String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(CMN_PatientOnDeviceActivity.this, android.R.style.Theme_Dialog));
        builder.setMessage(message).setTitle("Message")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                        finish();

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
    public void onTaskComplete(String result) {

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        Log.e("dd", result);
        showAlert(parseResult(result));
    }

    @Override
    public void onTaskStart() {
        if (progressDialog != null && !progressDialog.isShowing()) {
            progressDialog.show();
        }
    }


}
