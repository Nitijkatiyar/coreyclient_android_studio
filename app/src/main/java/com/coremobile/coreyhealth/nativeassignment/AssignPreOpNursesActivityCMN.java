package com.coremobile.coreyhealth.nativeassignment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

public class AssignPreOpNursesActivityCMN extends CMN_AppBaseActivity implements NetworkCallBack {

    private SharedPreferences mCurrentUserPref;
    public static final String CURRENT_USER = "CurrentUser";
    private String organizationName;
    String password, user_category, context_name;
    String userName;
    AssignmentsTypeModel assignmentsTypeModel;
    JSONObject jsonObject;
    LinearLayout progressBar;
    ArrayList<AllProvidersModel> assignmentProvidersModels;
    ArrayList<String> providerNames;
    ArrayList<PatientNurseAssignmentModel> nurseAssignmentModels;
    ArrayAdapter providersNameAdapter;
    CNM_AdapterAssignNurseToPatient assignNurseToPatientAdapter;
    Spinner providers;
    ListView listView;
    Button assign, unAssign;
    boolean isAssign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignments_wip);

        mCurrentUserPref = getSharedPreferences(CURRENT_USER, 0);

        user_category = mCurrentUserPref.getString("user_category", null);
        context_name = mCurrentUserPref.getString("context_name", null);
        progressBar = (LinearLayout) findViewById(R.id.progressbar);

        providers = (Spinner) findViewById(R.id.selectPatient);
        listView = (ListView) findViewById(R.id.listview);
        assign = (Button) findViewById(R.id.buttonAssign);
        unAssign = (Button) findViewById(R.id.buttonUnAssign);

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

        NetworkAsyncTask networkAsyncTask1 = NetworkAsyncTask.getInstance();
        try {
            networkAsyncTask1.startNetworkCall(ThreadTaskIds.GET_ALL_PROVIDERS, AssignPreOpNursesActivityCMN.this);
        } catch (NetworkException e) {
            e.getMessage();
        }

        NetworkAsyncTask networkAsyncTask2 = NetworkAsyncTask.getInstance();
        try {
            networkAsyncTask2.startNetworkCall(ThreadTaskIds.GET_PATIENT_NURES_ASSIGNMENT, AssignPreOpNursesActivityCMN.this);
        } catch (NetworkException e) {
            e.getMessage();
        }

        assign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (generateJson() == null) {
                    return;
                }
                isAssign = true;
                NetworkAsyncTask networkAsyncTask = NetworkAsyncTask.getInstance();
                try {
                    networkAsyncTask.startNetworkCall(ThreadTaskIds.SAVE_ASSIGN_NURSE_T0_PATIENT, AssignPreOpNursesActivityCMN.this);
                } catch (NetworkException e) {
                    e.getMessage();
                }
            }
        });
        unAssign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (generateJson() == null) {
                    return;
                }
                isAssign = false;
                NetworkAsyncTask networkAsyncTask = NetworkAsyncTask.getInstance();
                try {
                    networkAsyncTask.startNetworkCall(ThreadTaskIds.SAVE_ASSIGN_NURSE_T0_PATIENT, AssignPreOpNursesActivityCMN.this);
                } catch (NetworkException e) {
                    e.getMessage();
                }
            }
        });
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
//        inflater.inflate(R.menu.savesurveydata, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.action_save:

                return true;


            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private JSONObject generateJson() {
        jsonObject = new JSONObject();
        String patIds = assignNurseToPatientAdapter.getSelectedIds();
        if (patIds.isEmpty() || patIds.length() == 0) {
            jsonObject = null;
            Toast.makeText(AssignPreOpNursesActivityCMN.this, CMN_ErrorMessages.getInstance().getValue(152), Toast.LENGTH_SHORT).show();
        } else if (providers.getSelectedItemPosition() == 0) {
            Toast.makeText(AssignPreOpNursesActivityCMN.this, "Please select a staff member", Toast.LENGTH_SHORT).show();
            jsonObject = null;
        } else {
            try {
                jsonObject.put("patientids", "" + patIds);
                jsonObject.put("providerid", "" + assignmentProvidersModels.get(providers.getSelectedItemPosition()).getProviderId());

            } catch (JSONException e) {
                e.getMessage();
            }
        }
        return jsonObject;
    }


    @Override
    public void beforeNetworkCall(int taskId) {
        if (progressBar.getVisibility() == View.GONE) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public Object afterNetworkCall(int taskId, Object o) {
        if (progressBar.getVisibility() == View.VISIBLE && taskId == ThreadTaskIds.GET_PATIENT_NURES_ASSIGNMENT) {
            progressBar.setVisibility(View.GONE);
        }
        return null;
    }


    @Override
    public Object onNetworkCall(int taskId, Object o) throws JsonParsingException, JsonParsingException {
        if (taskId == ThreadTaskIds.GET_ALL_PROVIDERS) {
            String url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                    + "GetAllProviders.aspx?token="+ CMN_Preferences.getUserToken(AssignPreOpNursesActivityCMN.this) + "&AssignmentOption=" + assignmentsTypeModel.getAssignmentsOptions();

            NetworkTools networkTools = NetworkTools.getInstance();
            if (networkTools.checkNetworkConnection(AssignPreOpNursesActivityCMN.this) && url.length() > 0) {
                JSONObject response = networkTools.getJsonData(AssignPreOpNursesActivityCMN.this, url);
                try {
                    if (response != null) {
                        parseJsonData(response, ThreadTaskIds.GET_ALL_PROVIDERS);
                    }
                } catch (JSONException e) {
                    e.getMessage();
                }
            } else {
                Toast.makeText(AssignPreOpNursesActivityCMN.this, CMN_ErrorMessages.getInstance().getValue(12) , Toast.LENGTH_SHORT).show();
            }
        }
        if (taskId == ThreadTaskIds.GET_PATIENT_NURES_ASSIGNMENT) {
            String url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                    + "GetPatientNurseAssignments.aspx?token="+ CMN_Preferences.getUserToken(AssignPreOpNursesActivityCMN.this);

            NetworkTools networkTools = NetworkTools.getInstance();
            if (networkTools.checkNetworkConnection(AssignPreOpNursesActivityCMN.this) && url.length() > 0) {
                JSONObject response = networkTools.getJsonData(AssignPreOpNursesActivityCMN.this, url);
                try {
                    if (response != null) {
                        parseJsonData(response, ThreadTaskIds.GET_PATIENT_NURES_ASSIGNMENT);
                    }
                } catch (JSONException e) {
                    e.getMessage();
                }
            } else {
                Toast.makeText(AssignPreOpNursesActivityCMN.this, CMN_ErrorMessages.getInstance().getValue(12) , Toast.LENGTH_SHORT).show();
            }
        }
        if (taskId == ThreadTaskIds.SAVE_ASSIGN_NURSE_T0_PATIENT) {
            String url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                    + "AssignNurseToPatient.aspx?token="+ CMN_Preferences.getUserToken(AssignPreOpNursesActivityCMN.this) + "&assign=" + isAssign+ "&AssignmentOption=" + assignmentsTypeModel.getAssignmentsOptions();

            NetworkTools networkTools = NetworkTools.getInstance();
            if (networkTools.checkNetworkConnection(AssignPreOpNursesActivityCMN.this) && url.length() > 0) {
                JSONObject response = networkTools.postJsonData(AssignPreOpNursesActivityCMN.this, url, jsonObject);
                try {
                    if (response != null) {
                        parseJsonData(response, ThreadTaskIds.SAVE_ASSIGN_NURSE_T0_PATIENT);
                    }
                } catch (JSONException e) {
                    e.getMessage();
                }
            } else {
                Toast.makeText(AssignPreOpNursesActivityCMN.this, CMN_ErrorMessages.getInstance().getValue(12) , Toast.LENGTH_SHORT).show();
            }
        }
        return o;
    }

    @Override
    public Object onNetworkError(int taskId, NetworkException o) {
        return null;
    }

    private void parseJsonData(final JSONObject response, int id) throws JSONException {
        if (id == ThreadTaskIds.GET_ALL_PROVIDERS) {
            if (response.getJSONObject("Result").getInt("Code") == 0) {
                assignmentProvidersModels = new ArrayList<>();
                providerNames = new ArrayList<>();
                AllProvidersModel dummymodel = new AllProvidersModel();
                dummymodel.setProviderId("");
                dummymodel.setProviderName("");
                dummymodel.setRoleCategory(0);
                providerNames.add("");
                assignmentProvidersModels.add(dummymodel);
                JSONArray messagesArray = response.getJSONArray("Data");
                for (int i = 0; i < messagesArray.length(); i++) {
                    JSONObject jsonObject = messagesArray.getJSONObject(i);
                    AllProvidersModel assignmentProvidersModel = new AllProvidersModel();
                    assignmentProvidersModel.setProviderId(jsonObject.getString("ID"));
                    assignmentProvidersModel.setProviderName(jsonObject.getString("Name"));
                    assignmentProvidersModel.setRoleCategory(jsonObject.getInt("RoleCategory"));
                    providerNames.add(jsonObject.getString("Name"));
                    assignmentProvidersModels.add(assignmentProvidersModel);
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    providersNameAdapter = new ArrayAdapter<String>(AssignPreOpNursesActivityCMN.this,
                            android.R.layout.simple_spinner_item, providerNames);
                    providersNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    providers.setAdapter(providersNameAdapter);
                }
            });
        }

        if (id == ThreadTaskIds.GET_PATIENT_NURES_ASSIGNMENT) {
            if (response.getJSONObject("Result").getInt("Code") == 0) {
                nurseAssignmentModels = new ArrayList<>();
                JSONArray messagesArray = response.getJSONArray("Data");
                for (int i = 0; i < messagesArray.length(); i++) {
                    JSONObject jsonObject = messagesArray.getJSONObject(i);
                    PatientNurseAssignmentModel assignmentModels = new PatientNurseAssignmentModel();
                    assignmentModels.setProviderId(jsonObject.getString("ProviderId"));
                    assignmentModels.setProviderName(jsonObject.getString("ProviderName"));
                    assignmentModels.setOrId(jsonObject.getString("ORId"));
                    assignmentModels.setOrName(jsonObject.getString("OrName"));
                    assignmentModels.setPatientId(jsonObject.getString("PatientId"));
                    assignmentModels.setPatientName(jsonObject.getString("PatientName"));
                    nurseAssignmentModels.add(assignmentModels);
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    assignNurseToPatientAdapter = new CNM_AdapterAssignNurseToPatient(AssignPreOpNursesActivityCMN.this, nurseAssignmentModels);
                    listView.setAdapter(assignNurseToPatientAdapter);
                }
            });
        } else if (id == ThreadTaskIds.SAVE_ASSIGN_NURSE_T0_PATIENT) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
//                        if (response.getJSONObject("Result").getInt("Code") == 0) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(AssignPreOpNursesActivityCMN
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
