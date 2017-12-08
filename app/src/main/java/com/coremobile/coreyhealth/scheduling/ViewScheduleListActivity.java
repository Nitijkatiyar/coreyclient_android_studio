package com.coremobile.coreyhealth.scheduling;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
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
import com.coremobile.coreyhealth.scheduling.schedulingmodels.ScheduleListModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ViewScheduleListActivity extends BaseActivityCMN implements NetworkCallBack {

    ListView listView;
    ProgressDialog progressDialog;
    Intent intent;
    boolean mySchedule = false;
    String date;
    List<ScheduleListModel> scheduleListModels;
    ScheduleListAdapter scheduleListAdapter;
    String action = "";
    ScheduleListModel scheduleModel;
    TextView noData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_schedulelist);
        ActivityPackage.setActivityList(this);
        
        intent = getIntent();
        if (intent != null && intent.hasExtra("date")) {
            mySchedule = intent.getBooleanExtra("mySchedule", false);
            getActionBar().setHomeButtonEnabled(true);
            date = intent.getStringExtra("date");
            getActionBar().setTitle(date);
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        listView = (ListView) findViewById(R.id.schedulelist);
        noData = (TextView) findViewById(R.id.noData);
        progressDialog = new ProgressDialog(ViewScheduleListActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);


        NetworkAsyncTask networkAsyncTask = NetworkAsyncTask.getInstance();
        try {
            networkAsyncTask.startNetworkCall(ThreadTaskIds.GET_SCHEDULE_DATA, ViewScheduleListActivity.this);
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

    public JSONObject generateJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("SchType", "" + Utils.schType);
            jsonObject.put("Date", "" + date);
            jsonObject.put("MyScheduleOnly", mySchedule);

        } catch (JSONException e) {
            e.getMessage();
        }

        return jsonObject;
    }

    @Override
    public Object onNetworkCall(int taskId, Object o) throws
            JsonParsingException, JsonParsingException {
        if (taskId == ThreadTaskIds.GET_SCHEDULE_DATA) {
            String url = CMN_Preferences.getBaseUrl(ViewScheduleListActivity.this)
                    + "GetAllSchedulesList.aspx?token=" + CMN_Preferences.getUserToken(ViewScheduleListActivity.this);

            NetworkTools networkTools = NetworkTools.getInstance();
            if (networkTools.checkNetworkConnection(ViewScheduleListActivity.this) && url.length() > 0) {
                JSONObject response = networkTools.postJsonData(ViewScheduleListActivity.this, url, generateJson());
                try {
                    if (response != null) {
                        parseJsonData(response, ThreadTaskIds.GET_SCHEDULE_DATA);
                    }
                } catch (JSONException e) {
                    e.getMessage();
                }
            } else {
                Toast.makeText(ViewScheduleListActivity.this, CMN_ErrorMessages.getInstance().getValue(12), Toast.LENGTH_SHORT).show();
            }
        } else if (taskId == ThreadTaskIds.UPDATE_SCHEDULE_DATA) {
            String url = CMN_Preferences.getBaseUrl(ViewScheduleListActivity.this)
                    + "UpdateSchedule.aspx?token=" + CMN_Preferences.getUserToken(ViewScheduleListActivity.this) + "&id=" + scheduleModel.getId() + "&action=" + action;

            NetworkTools networkTools = NetworkTools.getInstance();
            if (networkTools.checkNetworkConnection(ViewScheduleListActivity.this) && url.length() > 0) {
                JSONObject response;

                response = networkTools.getJsonData(ViewScheduleListActivity.this, url);

                try {
                    if (response != null) {
                        parseJsonData(response, ThreadTaskIds.UPDATE_SCHEDULE_DATA);
                    }
                } catch (JSONException e) {
                    e.getMessage();
                }
            } else {
                Toast.makeText(ViewScheduleListActivity.this, CMN_ErrorMessages.getInstance().getValue(12), Toast.LENGTH_SHORT).show();
            }
        }


        return null;
    }

    private void parseJsonData(final JSONObject response, int id) throws JSONException {
        if (id == ThreadTaskIds.UPDATE_SCHEDULE_DATA) {
            if (response.getJSONObject("Result").getInt("Code") == 0) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ViewScheduleListActivity.this, "Schedule updated successfully", Toast.LENGTH_SHORT).show();
                        NetworkAsyncTask networkAsyncTask = NetworkAsyncTask.getInstance();
                        try {
                            networkAsyncTask.startNetworkCall(ThreadTaskIds.GET_SCHEDULE_DATA, ViewScheduleListActivity.this);
                        } catch (NetworkException e) {
                            e.getMessage();
                        }
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Toast.makeText(ViewScheduleListActivity.this, response.getJSONObject("Result").getString("Message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
        if (id == ThreadTaskIds.GET_SCHEDULE_DATA) {
            if (response.getJSONObject("Result").getInt("Code") == 0) {
                scheduleListModels = new ArrayList<>();
                JSONArray jsonArray = response.getJSONArray("Data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    ScheduleListModel listModel = new ScheduleListModel();
                    listModel.setStartTime(Utils.converttimeutc2local(jsonObject.optString("StartTime")));
                    listModel.setEndTime(Utils.converttimeutc2local(jsonObject.optString("EndTime")));
                    listModel.setExam(jsonObject.optString("Exam"));
                    listModel.setId(jsonObject.optString("Id"));
                    listModel.setIsConfirmed(jsonObject.optString("IsConfirmed"));
                    listModel.setIsRequested(jsonObject.optString("IsRequested"));
                    listModel.setModality(jsonObject.optString("Modality"));
                    listModel.setPatient(jsonObject.optString("Patient"));
                    listModel.setProcedure(jsonObject.optString("Procedure"));
                    listModel.setRequestedForDate(jsonObject.optString("RequestedForDate"));
                    listModel.setRequestedOnDate(jsonObject.optString("RequestedOnDate"));
                    listModel.setRoom(jsonObject.optString("Room"));
                    listModel.setSpecialty(jsonObject.optString("Specialty"));

                    scheduleListModels.add(listModel);


                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (scheduleListModels.size() == 0) {
                            noData.setVisibility(View.VISIBLE);
                            listView.setVisibility(View.GONE);
                            return;
                        } else {
                            noData.setVisibility(View.GONE);
                            listView.setVisibility(View.VISIBLE);
                        }

                        scheduleListAdapter = new ScheduleListAdapter(ViewScheduleListActivity.this, scheduleListModels);
                        listView.setAdapter(scheduleListAdapter);

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                showSchedulingPopup(ViewScheduleListActivity.this, i);
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

    public void showSchedulingPopup(final Context context, int pos) {

        scheduleModel = scheduleListModels.get(pos);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        View popupView = layoutInflater.inflate(R.layout.popup, null);

        final PopupWindow popup = new PopupWindow(popupView,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT,
                true);


        popup.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        Button move = (Button) popupView.findViewById(R.id.buttonMoveSchedule);
        View viewmove = (View) popupView.findViewById(R.id.viewMove);
        move.setVisibility(View.GONE);
        viewmove.setVisibility(View.GONE);
        Button view = (Button) popupView.findViewById(R.id.buttonViewSchedule);
        View viewview = (View) popupView.findViewById(R.id.viewView);
        view.setVisibility(View.GONE);
        viewview.setVisibility(View.GONE);
        Button confirm = (Button) popupView.findViewById(R.id.buttonConfirmSchedule);
        View viewconfirm = (View) popupView.findViewById(R.id.viewConfirm);
        confirm.setVisibility(View.GONE);
        viewconfirm.setVisibility(View.GONE);

        Button edit = (Button) popupView.findViewById(R.id.buttonEditSchedule);
        Button delete = (Button) popupView.findViewById(R.id.buttonDeleteSchedule);
        Button close = (Button) popupView.findViewById(R.id.buttonCancel);
        close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                popup.dismiss();
            }
        });


        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("SchType", Utils.schType);
                    jsonObject.put("ScheduleId", Integer.parseInt(scheduleModel.getId()));
                    jsonObject.put("StartTime", "");
                    jsonObject.put("EndTime", "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                Intent intent = new Intent(ViewScheduleListActivity.this, CMN_GenericViewActivity.class);
                intent.putExtra("scheduling", "" + jsonObject);
                startActivity(intent);
                popup.dismiss();

            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(ViewScheduleListActivity.this);
                builder1.setTitle("Delete Schedule");
                builder1.setMessage("Are you sure you want to delete schedule.");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                updateSchedule("delete");
                                dialog.cancel();
                            }
                        });
                builder1.setNegativeButton(
                        "NO",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert11 = builder1.create();
                alert11.show();

                popup.dismiss();
            }
        });
    }

    private void updateSchedule(String act) {
        action = act;
        NetworkAsyncTask networkAsyncTask = NetworkAsyncTask.getInstance();
        try {
            networkAsyncTask.startNetworkCall(ThreadTaskIds.UPDATE_SCHEDULE_DATA, ViewScheduleListActivity.this);
        } catch (NetworkException e) {
            e.getMessage();
        }
    }
}
