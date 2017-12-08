package com.coremobile.coreyhealth.nativeassignment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.coremobile.coreyhealth.R;
import com.coremobile.coreyhealth.base.CMN_AppBaseActivity;
import com.coremobile.coreyhealth.errorhandling.CMN_ErrorMessages;
import com.coremobile.coreyhealth.googleAnalytics.CMN_Preferences;
import com.coremobile.coreyhealth.networkutils.JsonParsingException;
import com.coremobile.coreyhealth.networkutils.NetworkAsyncTask;
import com.coremobile.coreyhealth.networkutils.NetworkCallBack;
import com.coremobile.coreyhealth.networkutils.NetworkException;
import com.coremobile.coreyhealth.networkutils.NetworkTools;
import com.coremobile.coreyhealth.networkutils.ThreadTaskIds;
import com.coremobile.coreyhealth.newui.MessageTabActivityCMN;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @author Nitij Katiyar
 */

public class AssignPatienttoORActivityCMN extends CMN_AppBaseActivity implements NetworkCallBack {

    private SharedPreferences mCurrentUserPref;
    public static final String CURRENT_USER = "CurrentUser";
    private String organizationName;
    String password, user_category, context_name;
    String userName;
    AssignmentsTypeModel assignmentsTypeModel;
    ArrayList<OrUsersModel> orUsersModels;
    ArrayList<String> orUserNames;
    ArrayList<PatientOrAssignmentModel> patientOrAssignmentModels;
    LinearLayout progressBar;
    ListView listView;
    AssignmentPatientToOrAdapter assignmentPatientToOrAdapter;
    JSONArray jsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignmntbyfacility);

        mCurrentUserPref = getSharedPreferences(CURRENT_USER, 0);

        user_category = mCurrentUserPref.getString("user_category", null);
        context_name = mCurrentUserPref.getString("context_name", null);

        listView = (ListView) findViewById(R.id.facilityassignment);
        progressBar = (LinearLayout) findViewById(R.id.progressbar);

        getActionBar().setHomeButtonEnabled(true);
        if (getIntent() != null && getIntent().hasExtra("title")) {
            assignmentsTypeModel = (AssignmentsTypeModel) getIntent().getSerializableExtra("title");
            getActionBar().setTitle(
                    "" + assignmentsTypeModel.getName());
        } else {
            getActionBar().setTitle(
                    "Assignment");
        }
        getActionBar().setDisplayHomeAsUpEnabled(true);


        NetworkAsyncTask networkAsyncTask = NetworkAsyncTask.getInstance();
        try {
            networkAsyncTask.startNetworkCall(ThreadTaskIds.GET_OR_USERS, AssignPatienttoORActivityCMN.this);
        } catch (NetworkException e) {
            e.getMessage();
        }

        NetworkAsyncTask networkAsyncTask2 = NetworkAsyncTask.getInstance();
        try {
            networkAsyncTask2.startNetworkCall(ThreadTaskIds.GET_PATIENT_OR_ASSIGNMENT, AssignPatienttoORActivityCMN.this);
        } catch (NetworkException e) {
            e.getMessage();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.savesurveydata, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.action_save:

                if (generateJson().length() == 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AssignPatienttoORActivityCMN.this, CMN_ErrorMessages.getInstance().getValue(154), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {

                    NetworkAsyncTask networkAsyncTask3 = NetworkAsyncTask.getInstance();
                    try {
                        networkAsyncTask3.startNetworkCall(ThreadTaskIds.SAVE_PATIENT_OR_ASSIGNMENT, AssignPatienttoORActivityCMN.this);
                    } catch (NetworkException e) {
                        e.getMessage();
                    }
                }
                Log.d("array", "....." + jsonArray);
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private JSONArray generateJson() {
        jsonArray = new JSONArray();
        for (int i = 0; i < assignmentPatientToOrAdapter.getCount(); i++) {
            View view = assignmentPatientToOrAdapter.getView(i, null, null);
            PatientOrAssignmentModel assignmentModel = (PatientOrAssignmentModel) assignmentPatientToOrAdapter.getItem(i);
            Spinner spinner = (Spinner) view.findViewById(R.id.selectRole);

            if (!orUsersModels.get(spinner.getSelectedItemPosition()).getId().isEmpty()) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("ORid", "" + orUsersModels.get(spinner.getSelectedItemPosition()).getId());
                    jsonObject.put("patientid", "" + assignmentModel.getPatientId());
                    jsonArray.put(jsonObject);
                } catch (JSONException e) {
                    e.getMessage();
                }
            }

        }
        return jsonArray;
    }


    @Override
    public void beforeNetworkCall(int taskId) {
        if (progressBar.getVisibility() == View.GONE) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public Object afterNetworkCall(int taskId, Object o) {
        if (progressBar.getVisibility() == View.VISIBLE && taskId == ThreadTaskIds.GET_PATIENT_OR_ASSIGNMENT) {
            progressBar.setVisibility(View.GONE);
        }
        return null;
    }


    @Override
    public Object onNetworkCall(int taskId, Object o) throws JsonParsingException, JsonParsingException {
        if (taskId == ThreadTaskIds.GET_OR_USERS) {
            String url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                    + "GetORUsers.aspx?token="+ CMN_Preferences.getUserToken(AssignPatienttoORActivityCMN.this) + "&assignmentoption=" + assignmentsTypeModel.getAssignmentsOptions();

            NetworkTools networkTools = NetworkTools.getInstance();
            if (networkTools.checkNetworkConnection(AssignPatienttoORActivityCMN.this) && url.length() > 0) {
                JSONObject response = networkTools.getJsonData(AssignPatienttoORActivityCMN.this, url);
                try {
                    if (response != null) {
                        parseJsonData(response, ThreadTaskIds.GET_OR_USERS);
                    }
                } catch (JSONException e) {
                    e.getMessage();
                }
            } else {
                Toast.makeText(AssignPatienttoORActivityCMN.this, CMN_ErrorMessages.getInstance().getValue(12), Toast.LENGTH_SHORT).show();
            }
        }
        if (taskId == ThreadTaskIds.GET_PATIENT_OR_ASSIGNMENT) {
            String url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                    + "GetPatientORAssignments.aspx?token="+ CMN_Preferences.getUserToken(AssignPatienttoORActivityCMN.this);

            NetworkTools networkTools = NetworkTools.getInstance();
            if (networkTools.checkNetworkConnection(AssignPatienttoORActivityCMN.this) && url.length() > 0) {
                JSONObject response = networkTools.getJsonData(AssignPatienttoORActivityCMN.this, url);
                try {
                    if (response != null) {
                        parseJsonData(response, ThreadTaskIds.GET_PATIENT_OR_ASSIGNMENT);
                    }
                } catch (JSONException e) {
                    e.getMessage();
                }
            } else {
                Toast.makeText(AssignPatienttoORActivityCMN.this, CMN_ErrorMessages.getInstance().getValue(12), Toast.LENGTH_SHORT).show();
            }
        }
        if (taskId == ThreadTaskIds.SAVE_PATIENT_OR_ASSIGNMENT) {
            String url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                    + "AssignORToAPatient.aspx?token="+ CMN_Preferences.getUserToken(AssignPatienttoORActivityCMN.this);

            NetworkTools networkTools = NetworkTools.getInstance();
            if (networkTools.checkNetworkConnection(AssignPatienttoORActivityCMN.this) && url.length() > 0) {
                JSONObject response = networkTools.postJsonArrayData(AssignPatienttoORActivityCMN.this, url, jsonArray);
                try {
                    if (response != null) {
                        parseJsonData(response, ThreadTaskIds.SAVE_PATIENT_OR_ASSIGNMENT);
                    }
                } catch (JSONException e) {
                    e.getMessage();
                }
            } else {
                Toast.makeText(AssignPatienttoORActivityCMN.this, CMN_ErrorMessages.getInstance().getValue(12), Toast.LENGTH_SHORT).show();
            }
        }
        return o;
    }

    @Override
    public Object onNetworkError(int taskId, NetworkException o) {
        return null;
    }

    private void parseJsonData(final JSONObject response, int id) throws JSONException {
        if (id == ThreadTaskIds.GET_OR_USERS) {
            if (response.getJSONObject("Result").getInt("Code") == 0) {
                orUsersModels = new ArrayList<>();
                orUserNames = new ArrayList<>();
                OrUsersModel dummymodel = new OrUsersModel();
                dummymodel.setName("Set new OR");
                dummymodel.setId("");
                orUsersModels.add(dummymodel);
                orUserNames.add("");
                JSONArray messagesArray = response.getJSONArray("Data");
                for (int i = 0; i < messagesArray.length(); i++) {
                    JSONObject jsonObject = messagesArray.getJSONObject(i);
                    OrUsersModel orNursesModel = new OrUsersModel();
                    orNursesModel.setId(jsonObject.getString("ORId"));
                    orNursesModel.setName(jsonObject.getString("Name"));
                    orUserNames.add(jsonObject.getString("Name"));
                    orUsersModels.add(orNursesModel);
                }
            }
        } else if (id == ThreadTaskIds.GET_PATIENT_OR_ASSIGNMENT) {
            if (response.getJSONObject("Result").getInt("Code") == 0) {
                patientOrAssignmentModels = new ArrayList<>();
                JSONArray messagesArray = response.getJSONArray("Data");
                for (int i = 0; i < messagesArray.length(); i++) {
                    JSONObject jsonObject = messagesArray.getJSONObject(i);
                    PatientOrAssignmentModel assignmentModel = new PatientOrAssignmentModel();
                    assignmentModel.setORid(jsonObject.getString("ORId"));
                    assignmentModel.setPatientId(jsonObject.getString("patientId"));
                    assignmentModel.setOrname(jsonObject.getString("ORName"));
                    assignmentModel.setPatientName(jsonObject.getString("name"));
                    assignmentModel.setSurgeryTime(jsonObject.getString("SurgeryTime"));
                    patientOrAssignmentModels.add(assignmentModel);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        assignmentPatientToOrAdapter = new AssignmentPatientToOrAdapter(AssignPatienttoORActivityCMN.this, patientOrAssignmentModels, orUsersModels);
                        listView.setAdapter(assignmentPatientToOrAdapter);
                    }
                });
            }
        } else if (id == ThreadTaskIds.SAVE_PATIENT_OR_ASSIGNMENT) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
//                        if (response.getJSONObject("Result").getInt("Code") == 0) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(AssignPatienttoORActivityCMN
                                .this);
                        builder1.setTitle("Assignment");
                        builder1.setMessage(response.getJSONObject("Result").getString("Message"));
                        builder1.setCancelable(false);

                        builder1.setNeutralButton(
                                "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        finish();
                                    }
                                });
                        AlertDialog alert11 = builder1.create();
                        alert11.show();
//                        }
                    } catch (JSONException e) {
                        e.getMessage();
                    }
                }
            });

        }
    }

    @Override
    public void onBackPressed() {
        if (progressBar.getVisibility() == View.VISIBLE) {

        } else {
            super.onBackPressed();
        }
    }
}