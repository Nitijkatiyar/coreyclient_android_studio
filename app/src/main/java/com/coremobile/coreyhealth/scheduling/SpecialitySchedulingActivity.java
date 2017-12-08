package com.coremobile.coreyhealth.scheduling;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.coremobile.coreyhealth.BaseActivityCMN;
import com.coremobile.coreyhealth.R;
import com.coremobile.coreyhealth.Utils;
import com.coremobile.coreyhealth.errorhandling.CMN_ErrorMessages;
import com.coremobile.coreyhealth.googleAnalytics.CMN_Preferences;
import com.coremobile.coreyhealth.networkutils.JsonParsingException;
import com.coremobile.coreyhealth.networkutils.NetworkAsyncTask;
import com.coremobile.coreyhealth.networkutils.NetworkCallBack;
import com.coremobile.coreyhealth.networkutils.NetworkException;
import com.coremobile.coreyhealth.networkutils.NetworkTools;
import com.coremobile.coreyhealth.networkutils.ThreadTaskIds;
import com.coremobile.coreyhealth.scheduling.schedulingcalender.CalenderActivity;
import com.coremobile.coreyhealth.scheduling.schedulingmodels.GetListItemModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SpecialitySchedulingActivity extends BaseActivityCMN implements NetworkCallBack {

    Button _viewSchedule, _requestSchedule ,_requestDateSchedule;
    ArrayList<GetListItemModel> surgeonModels;
    ProgressDialog progressDialog;
    List<String> allPatients, allSurgeons;
    Spinner speciality;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speciality_scheduling);

        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setTitle("Speciality Scheduling");
        getActionBar().setDisplayHomeAsUpEnabled(true);

        speciality = (Spinner) findViewById(R.id.selectRole);
        _viewSchedule = (Button) findViewById(R.id.viewSchedule);
        _requestSchedule = (Button) findViewById(R.id.requestSchedule);
        _requestDateSchedule = (Button) findViewById(R.id.requestDateSchedule);

        _viewSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (speciality.getSelectedItemPosition() == 0) {
                    Toast.makeText(SpecialitySchedulingActivity.this, "Please select speciality", Toast.LENGTH_SHORT).show();
                    return;
                }
                Utils.viewSchedule = true;
                Utils.dateSchedule =false;
                startActivity(new Intent(SpecialitySchedulingActivity.this, CalenderActivity.class).putExtra("speciality", surgeonModels.get(speciality.getSelectedItemPosition()).getCode()));
            }
        });
        _requestSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (speciality.getSelectedItemPosition() == 0) {
                    Toast.makeText(SpecialitySchedulingActivity.this, "Please select speciality", Toast.LENGTH_SHORT).show();
                    return;
                }
                Utils.viewSchedule = Utils.dateSchedule =false;
                CalenderActivity.speciality = surgeonModels.get(speciality.getSelectedItemPosition()).getCode();
                if(Utils.fromIntake){
                    Intent intent = new Intent(SpecialitySchedulingActivity.this, CalenderActivity.class);
                    startActivity(intent);
                }else {
                    startActivity(new Intent(SpecialitySchedulingActivity.this, RequestScheduleActivity.class));
                }
            }
        });
        _requestDateSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (speciality.getSelectedItemPosition() == 0) {
                    Toast.makeText(SpecialitySchedulingActivity.this, "Please select speciality", Toast.LENGTH_SHORT).show();
                    return;
                }
                Utils.viewSchedule = false;
                Utils.dateSchedule = true;
                CalenderActivity.speciality = surgeonModels.get(speciality.getSelectedItemPosition()).getCode();
                if(Utils.fromIntake){
                    Intent intent = new Intent(SpecialitySchedulingActivity.this, CalenderActivity.class);
                    startActivity(intent);
                }else {
                    startActivity(new Intent(SpecialitySchedulingActivity.this, RequestScheduleActivity.class));
                }
            }
        });

        progressDialog = new ProgressDialog(SpecialitySchedulingActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        NetworkAsyncTask networkAsyncTask = NetworkAsyncTask.getInstance();
        try {
            networkAsyncTask.startNetworkCall(ThreadTaskIds.GET_LIST_ITEM, SpecialitySchedulingActivity.this);
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
        if (taskId == ThreadTaskIds.GET_LIST_ITEM) {
            String url = CMN_Preferences.getBaseUrl(SpecialitySchedulingActivity.this)
                    + "GetListItems.aspx?token=" + CMN_Preferences.getUserToken(SpecialitySchedulingActivity.this) + "&listname=Specialty";

            NetworkTools networkTools = NetworkTools.getInstance();
            if (networkTools.checkNetworkConnection(SpecialitySchedulingActivity.this) && url.length() > 0) {
                JSONObject response = networkTools.postJsonData(SpecialitySchedulingActivity.this, url, getJSONObject());
                try {
                    if (response != null) {
                        parseJsonData(response, ThreadTaskIds.GET_LIST_ITEM);
                    }
                } catch (JSONException e) {
                    e.getMessage();
                }
            } else {
                Toast.makeText(SpecialitySchedulingActivity.this, CMN_ErrorMessages.getInstance().getValue(12), Toast.LENGTH_SHORT).show();
            }
        }


        return null;
    }

    private JSONObject getJSONObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("For", Utils.schType);
            jsonObject.put("ListName", "Specialty");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private void parseJsonData(JSONObject response, int id) throws JSONException {

        if (id == ThreadTaskIds.GET_LIST_ITEM) {
            if (response.getJSONObject("Result").getInt("Code") == 0) {
                surgeonModels = new ArrayList<>();
                JSONObject data = response.getJSONObject("Data");
                JSONArray topicsArray = data
                        .getJSONArray("Specialty");
                GetListItemModel topicModel = new GetListItemModel();
                topicModel.setCode("");
                topicModel.setTopicName("");
                surgeonModels.add(topicModel);
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
                        ArrayAdapter _adapter = new ArrayAdapter<String>(SpecialitySchedulingActivity.this,
                                android.R.layout.simple_spinner_item, allSurgeons);
                        _adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        speciality.setAdapter(_adapter);
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
