package com.coremobile.coreyhealth.patientactivitylog;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.coremobile.coreyhealth.AsyncTaskCompleteListener;
import com.coremobile.coreyhealth.BaseAsyncTask;
import com.coremobile.coreyhealth.R;
import com.coremobile.coreyhealth.aesencryption.CMN_AES;
import com.coremobile.coreyhealth.base.CMN_AppBaseActivity;
import com.coremobile.coreyhealth.errorhandling.CMN_ErrorMessages;
import com.coremobile.coreyhealth.googleAnalytics.CMN_Preferences;
import com.coremobile.coreyhealth.networkutils.NetworkTools;
import com.coremobile.coreyhealth.newui.MessageTabActivityCMN;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.codecrafters.tableview.listeners.SwipeToRefreshListener;
import de.codecrafters.tableview.listeners.TableDataClickListener;

/**
 * Created by nitij on 05-01-2017.
 */
public class ShowActivityLogCMN extends CMN_AppBaseActivity implements View.OnClickListener, AsyncTaskCompleteListener<String> {


    ProgressDialog progressDialog;
    TextView _fromDate, _todate;
    Button _viewSelected, _viewAll;

    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;

    private SimpleDateFormat dateFormatter;

    private Calendar calendar;
    private int year, month, day;
    private boolean showAll = false;

    ListView listView;
    List<ActivityModel> models;
    ActivityLogAdapter activityLogAdapter;

    TableLayout table;
    HorizontalScrollView tableLayout;
    static String vUrl;
    static  boolean fromRefreshTable=false;
    static  int lastRowId=0;

    SortablePatientLogTableView carTableView;

    AsyncTaskCompleteListener<String> asyncTaskCompleteListener;

    public boolean forPatientData=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patientactivitylog);

        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setTitle(
                "" + getResources().getString(R.string.patient_activity_log));
        getActionBar().setDisplayHomeAsUpEnabled(true);

        dateFormatter = new SimpleDateFormat("MM-dd-yyyy", Locale.US);

        tableLayout = (HorizontalScrollView) findViewById(R.id.table_layout);

        table = (TableLayout) findViewById(R.id.table);

        listView = (ListView) findViewById(R.id.listview);

        Calendar calendar = Calendar.getInstance();
        Date toDate = calendar.getTime();
        String selectedToDate = dateFormatter.format(toDate);
        calendar.add(Calendar.MONTH, -1);
        Date fromDate = calendar.getTime();
        String selectedFromDate = dateFormatter.format(fromDate);
        _fromDate = (TextView) findViewById(R.id.activitylog_fromdate);
        _fromDate.setText(selectedFromDate);
        _todate = (TextView) findViewById(R.id.activitylog_todate);
        _todate.setText(selectedToDate);

        _viewSelected = (Button) findViewById(R.id.activitylog_viewselected);
        _viewAll = (Button) findViewById(R.id.activitylog_viewall);

        _fromDate.setOnClickListener(this);
        _todate.setOnClickListener(this);
        _viewSelected.setOnClickListener(this);
        _viewAll.setOnClickListener(this);


        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                _fromDate.setText(dateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        toDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                _todate.setText(dateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        /**
         * sortable table view
         */
        carTableView = (SortablePatientLogTableView) findViewById(R.id.tableView);

        carTableView.addDataClickListener(new LogClickListener());

        String url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                + "GetUserPreferences.aspx?token=" + CMN_Preferences.getUserToken(ShowActivityLogCMN.this);

        NetworkTools networkTools = NetworkTools.getInstance();
        if (networkTools.checkNetworkConnection(ShowActivityLogCMN.this) && url.length() > 0) {

            showLoading();
            asyncTaskCompleteListener = ShowActivityLogCMN.this;
            new BaseAsyncTask(getApplicationContext(), "GET", asyncTaskCompleteListener, "").execute(url);
        } else {
            Toast.makeText(ShowActivityLogCMN.this, CMN_ErrorMessages.getInstance().getValue(12), Toast.LENGTH_SHORT).show();
        }

//        fromDatePickerDialog.show();


    }

    private class LogClickListener implements TableDataClickListener<ActivityModel> {

        @Override
        public void onDataClicked(final int rowIndex, final ActivityModel clickedData) {


          /*  Intent intent=new Intent(getApplicationContext(),SurveyFormActivity.class);
            String url=models.get(rowIndex).getViewForm();
            intent.putExtra("url",url);
            if(url==null)
                return;
            else if(url.isEmpty())
                return;
            else
                startActivity(intent);*/
        }
    }



    @Override
    public void onClick(View v) {
        if (v.equals(_viewSelected)) {
            if (_fromDate.getText().toString().length() <= 0 || _todate.getText().toString().length() <= 0) {
                Toast.makeText(ShowActivityLogCMN.this, "Please select the dates", Toast.LENGTH_SHORT).show();
                return;
            }
            showAll = false;

            fromRefreshTable=false;
            String url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                    + "GetPatientActivityLog_s.aspx?token=" + CMN_Preferences.getUserToken(ShowActivityLogCMN.this);

            this.vUrl=url;
            NetworkTools networkTools = NetworkTools.getInstance();
            if (networkTools.checkNetworkConnection(ShowActivityLogCMN.this) && this.vUrl.length() > 0) {

                showLoading();
                asyncTaskCompleteListener = ShowActivityLogCMN.this;
                new BaseAsyncTask(getApplicationContext(), "POST", asyncTaskCompleteListener, generateJson().toString()).execute(this.vUrl);
            } else {
                Toast.makeText(ShowActivityLogCMN.this, CMN_ErrorMessages.getInstance().getValue(12), Toast.LENGTH_SHORT).show();
            }

        } else if (v.equals(_viewAll)) {
            showAll = true;

            String url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                    + "GetPatientActivityLog_s.aspx?token=" + CMN_Preferences.getUserToken(ShowActivityLogCMN.this);

            fromRefreshTable=false;
            this.vUrl=url;
            NetworkTools networkTools = NetworkTools.getInstance();
            if (networkTools.checkNetworkConnection(ShowActivityLogCMN.this) && this.vUrl.length() > 0) {

                showLoading();
                asyncTaskCompleteListener = ShowActivityLogCMN.this;
                new BaseAsyncTask(getApplicationContext(), "POST", asyncTaskCompleteListener, generateJson().toString()).execute(this.vUrl);
            } else {
                Toast.makeText(ShowActivityLogCMN.this, CMN_ErrorMessages.getInstance().getValue(12), Toast.LENGTH_SHORT).show();
            }

        } else if (v.equals(_fromDate)) {
            fromDatePickerDialog.show();
        } else if (v.equals(_todate)) {
            toDatePickerDialog.show();
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


    public void onTaskStart() {
        if (progressDialog != null && !progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    @Override
    public void onTaskComplete(String result) {


        if(forPatientData) {
            JSONObject json = null;
            try {

                json = new JSONObject(CMN_AES.decryptData(result, CMN_Preferences.getUserToken(ShowActivityLogCMN.this)));

                if (!fromRefreshTable)
                    parseJsonData(json, 0);

                else
                    parseJsonDataForRefresh(json, 0);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            forPatientData=true;
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("Data");

                for (int i = 0; i < jsonArray.length(); i++) {

                    if (jsonArray.getJSONObject(i).getString("Code").equalsIgnoreCase("FromDateInPatientActivityLog")) {

                        if (!jsonArray.getJSONObject(i).getString("Value").isEmpty() || jsonArray.getJSONObject(i).getString("Value") != null)
                            _fromDate.setText(jsonArray.getJSONObject(i).getString("Value"));
                        else {
                            // fromDatePickerDialog.show();
                        }
                    }
                }
            }
            catch (Exception e) {

                e.printStackTrace();
            }
        }
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }


    }


    public void showLoading() {
        progressDialog = new ProgressDialog(ShowActivityLogCMN.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
    }

    public JSONObject generateJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            if (!showAll) {
                jsonObject.put("todosdate", "" + _todate.getText().toString().trim());
                jsonObject.put("fromdosdate", "" + _fromDate.getText().toString().trim());
            } else {
                jsonObject.put("todosdate", "");
                jsonObject.put("fromdosdate", "");
            }
        } catch (JSONException e) {
            e.getMessage();
        }

        return jsonObject;
    }

    public JSONObject generateJsonForRefresh(int vlastRowId) {
        JSONObject jsonObject = new JSONObject();
        try {
            if (!showAll) {
                jsonObject.put("todosdate", "" + _todate.getText().toString().trim());
                jsonObject.put("fromdosdate", "" + _fromDate.getText().toString().trim());
                jsonObject.put("Sequence","Forward");
                jsonObject.put("LastRowId",  vlastRowId);



            } else {
                jsonObject.put("todosdate", "");
                jsonObject.put("fromdosdate", "");
                jsonObject.put("Sequence","Forward");
                jsonObject.put("LastRowId",  vlastRowId);

            }
        } catch (JSONException e) {
            e.getMessage();
        }

        return jsonObject;
    }




    private void parseJsonDataForRefresh(final JSONObject response, int taskId) throws JSONException {

        final List dataModels = new ArrayList<>();
        if (response.getJSONObject("Result").getInt("Code") == 0) {
            JSONObject jsonObject = response.getJSONObject("Data");
            JSONArray jsonArray = jsonObject.getJSONArray("Rows");

            JSONArray valuesJsonArray=jsonArray.getJSONObject(0).getJSONArray("Values");
                for (int i = 0; i < valuesJsonArray.length(); i++) {
                    JSONObject jsonObject1 = valuesJsonArray.getJSONObject(i);
                    JSONArray jsonArray1 = jsonObject1.getJSONArray("RowValues");
                    ActivityModel activityModel = new ActivityModel();
                    for (int j = 0; j < jsonArray1.length(); j++) {
                        JSONObject jsonObject2 = jsonArray1.getJSONObject(j);
                        if (jsonObject2.getString("Column").equalsIgnoreCase("Patient Name")) {
                            activityModel.setPatientName(jsonObject2.getString("Value"));
                        } else if (jsonObject2.getString("Column").equalsIgnoreCase("D.O.S")) {
                            activityModel.setDos(jsonObject2.getString("Value"));
                            activityModel.setDosToConvert(jsonObject2.getBoolean("ConvertTime"));
                        } else if (jsonObject2.getString("Column").equalsIgnoreCase("Last Login")) {
                            activityModel.setLastLogin(jsonObject2.getString("Value"));
                            activityModel.setLastLoginToConvert(jsonObject2.getBoolean("ConvertTime"));
                        } else if (jsonObject2.getString("Column").equalsIgnoreCase("First Invitation")) {
                            activityModel.setFirstInvitation(jsonObject2.getString("Value"));
                            activityModel.setFirstInvitationToConvert(jsonObject2.getBoolean("ConvertTime"));
                        } else if (jsonObject2.getString("Column").equalsIgnoreCase("Invitation Reminders")) {
                            activityModel.setInvitationReminders(jsonObject2.getString("Value"));
                            activityModel.setInvitationRemindersToConvert(jsonObject2.getBoolean("ConvertTime"));
                        }
                    }
                dataModels.add(activityModel);

            }
            models.addAll(dataModels);

            activityLogAdapter.notifyDataSetChanged();

        }
    }

    private void parseJsonData(final JSONObject response, int taskId) throws JSONException {

            models = new ArrayList<>();
            if (response.getJSONObject("Result").getInt("Code") == 0) {
                JSONObject jsonObject = response.getJSONObject("Data");
                JSONArray jsonArray = jsonObject.getJSONArray("Rows");

                JSONArray valuesJsonArray=jsonArray.getJSONObject(0).getJSONArray("Values");
                for (int i = 0; i < valuesJsonArray.length(); i++) {
                    JSONObject jsonObject1 = valuesJsonArray.getJSONObject(i);
                    JSONArray jsonArray1 = jsonObject1.getJSONArray("RowValues");
                    ActivityModel activityModel = new ActivityModel();
                    for (int j = 0; j < jsonArray1.length(); j++) {
                        JSONObject jsonObject2 = jsonArray1.getJSONObject(j);
                        if (jsonObject2.getString("Column").equalsIgnoreCase("Patient Name")) {
                            activityModel.setPatientName(jsonObject2.getString("Value"));
                        } else if (jsonObject2.getString("Column").equalsIgnoreCase("D.O.S")) {
                            activityModel.setDos(jsonObject2.getString("Value"));
                            activityModel.setDosToConvert(jsonObject2.getBoolean("ConvertTime"));
                        } else if (jsonObject2.getString("Column").equalsIgnoreCase("Last Login")) {
                            activityModel.setLastLogin(jsonObject2.getString("Value"));
                            activityModel.setLastLoginToConvert(jsonObject2.getBoolean("ConvertTime"));
                        } else if (jsonObject2.getString("Column").equalsIgnoreCase("First Invitation")) {
                            activityModel.setFirstInvitation(jsonObject2.getString("Value"));
                            activityModel.setFirstInvitationToConvert(jsonObject2.getBoolean("ConvertTime"));
                        } else if (jsonObject2.getString("Column").equalsIgnoreCase("Invitation Reminders")) {
                            activityModel.setInvitationReminders(jsonObject2.getString("Value"));
                            activityModel.setInvitationRemindersToConvert(jsonObject2.getBoolean("ConvertTime"));
                        }
                    }
                    models.add(activityModel);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (models.size() == 0) {
                            Toast.makeText(ShowActivityLogCMN.this, "No activities to show for the selected dates.", Toast.LENGTH_SHORT).show();
                        } else {
                            carTableView.setVisibility(View.VISIBLE);

                            activityLogAdapter = new ActivityLogAdapter(getApplicationContext(), models, carTableView);

                            carTableView.setDataAdapter(activityLogAdapter);


                            carTableView.setSwipeToRefreshEnabled(true);

                            carTableView.setSwipeToRefreshListener(new SwipeToRefreshListener() {
                                @Override
                                public void onRefresh(final RefreshIndicator refreshIndicator) {
                                    carTableView.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {

                                            NetworkTools networkTools = NetworkTools.getInstance();
                                            if (networkTools.checkNetworkConnection(ShowActivityLogCMN.this) && vUrl.length() > 0) {

                                                showLoading();
                                                asyncTaskCompleteListener = ShowActivityLogCMN.this;

                                                fromRefreshTable=true;
                                                lastRowId=lastRowId+20;
                                                new BaseAsyncTask(getApplicationContext(), "POST", asyncTaskCompleteListener, generateJsonForRefresh(lastRowId).toString()).execute(vUrl);
                                            } else {
                                                Toast.makeText(ShowActivityLogCMN.this, CMN_ErrorMessages.getInstance().getValue(12), Toast.LENGTH_SHORT).show();
                                            }

                                            //  carTableDataAdapter.getData().add(models);
                                            refreshIndicator.hide();
                                        }
                                    }, 3000);
                                }
                            });



                            listView.setAdapter(activityLogAdapter);
                        }
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Toast.makeText(ShowActivityLogCMN.this, response.getJSONObject("Result").getString("Message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.getMessage();
                        }
                    }
                });
            }



    }

}
