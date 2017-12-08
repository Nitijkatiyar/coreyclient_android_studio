package com.coremobile.coreyhealth.nativeassignment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.coremobile.coreyhealth.patientreminders.ReminderTypesModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Nitij Katiyar
 */

public class ChangeAssignmentsbyPatientActivityCMN extends CMN_AppBaseActivity implements NetworkCallBack {

    private SharedPreferences mCurrentUserPref;
    public static final String CURRENT_USER = "CurrentUser";
    private String organizationName;
    String password, user_category, context_name;
    String userName;
    Intent intent;

    ReminderTypesModel typesModel;
    LinearLayout progressBar;
    ArrayList<AssignmentPatientModel> assignmentPatientModels;
    ArrayList<AssignmentsForPatientsModel> assignmentsForPatientsModels;
    ArrayList<ProviderAssignmentPatientMergedModel> assignmentProvidersModels;
    ArrayList<String> assignmentPatientsNames;
    ArrayAdapter<String> _patientNamesAdapter;
    ArrayList<AssignmentHospitalsModel> assignmentHospitalModels;
    AssignmentForPatientAdapter assignmentForPatientAdapter;
    Spinner patients;
    ListView assignmentsforproviders;
    String conntextid;
    JSONArray jsonArray;
    AssignmentsTypeModel assignmentsTypeModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignmntbytaskpatient);

        mCurrentUserPref = getSharedPreferences(CURRENT_USER, 0);
        user_category = mCurrentUserPref.getString("user_category", null);
        context_name = mCurrentUserPref.getString("context_name", null);


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

        progressBar = (LinearLayout) findViewById(R.id.progressbar);

        assignmentsforproviders = (ListView) findViewById(R.id.assignmentListView);
        patients = (Spinner) findViewById(R.id.selectPatient);

        patients.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    conntextid = assignmentPatientModels.get(position).getPatientId();
                    NetworkAsyncTask networkAsyncTask = NetworkAsyncTask.getInstance();
                    try {
                        networkAsyncTask.startNetworkCall(ThreadTaskIds.GET_ASSIGNMENTS_FOR_PATIENT, ChangeAssignmentsbyPatientActivityCMN.this);
                    } catch (NetworkException e) {
                        e.getMessage();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        NetworkAsyncTask networkAsyncTask = NetworkAsyncTask.getInstance();
        try {
            networkAsyncTask.startNetworkCall(ThreadTaskIds.GET_ALL_PROVIDERS, ChangeAssignmentsbyPatientActivityCMN.this);
        } catch (NetworkException e) {
            e.getMessage();
        }
        NetworkAsyncTask networkAsyncTask1 = NetworkAsyncTask.getInstance();
        try {
            networkAsyncTask1.startNetworkCall(ThreadTaskIds.GET_HOSPITAL_DEPARTMENTS, ChangeAssignmentsbyPatientActivityCMN.this);
        } catch (NetworkException e) {
            e.getMessage();
        }
        NetworkAsyncTask networkAsyncTask2 = NetworkAsyncTask.getInstance();
        try {
            networkAsyncTask2.startNetworkCall(ThreadTaskIds.GET_PATIENT_BY_PROVIDER, ChangeAssignmentsbyPatientActivityCMN.this);
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
        MenuItem item = menu.findItem(R.id.action_save);
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

                if (patients.getSelectedItemPosition() == 0) {
                    Toast.makeText(ChangeAssignmentsbyPatientActivityCMN.this, CMN_ErrorMessages.getInstance().getValue(152), Toast.LENGTH_SHORT).show();
                    return false;
                } else if (generateJson().length() == 0) {
                    Toast.makeText(ChangeAssignmentsbyPatientActivityCMN.this, CMN_ErrorMessages.getInstance().getValue(154), Toast.LENGTH_SHORT).show();
                    return false;
                } else {
                    NetworkAsyncTask networkAsyncTask = NetworkAsyncTask.getInstance();
                    try {
                        networkAsyncTask.startNetworkCall(ThreadTaskIds.SAVE_ASSIGN_TO_PATIENT, ChangeAssignmentsbyPatientActivityCMN.this);
                    } catch (NetworkException e) {
                        e.getMessage();
                    }
                    return true;
                }

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private JSONArray generateJson() {
        jsonArray = new JSONArray();
        for (int i = 0; i < assignmentForPatientAdapter.getCount(); i++) {
            View view = assignmentForPatientAdapter.getView(i, null, null);
            Spinner spinner = (Spinner) view.findViewById(R.id.selectRole);
            if (spinner.getSelectedItemPosition() != 0) {
                ProviderAssignmentPatientMergedModel item = (ProviderAssignmentPatientMergedModel) assignmentForPatientAdapter.getItem(i);

                if (!item.gethDeptId().equalsIgnoreCase("00000")) {
                    JSONObject jsonObject = new JSONObject();
                    List<AssignmentHospitalsModel> allProvidersModels = assignmentForPatientAdapter.providersAsPerHospitalDepartment.get(i);
                    try {
                        jsonObject.put("providerId", item.getproviderid());
                        jsonObject.put("isLead", item.isLead());
                        jsonObject.put("HdeptId", "" + allProvidersModels.get(spinner.getSelectedItemPosition() - 1).getHdeptId());
                    } catch (JSONException e) {
                        e.getMessage();
                    }
                    jsonArray.put(jsonObject);
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
        if (progressBar.getVisibility() == View.VISIBLE && taskId == ThreadTaskIds.GET_PATIENT_BY_PROVIDER || taskId == ThreadTaskIds.GET_ASSIGNMENTS_FOR_PATIENT) {
            progressBar.setVisibility(View.GONE);
        }
        return null;
    }


    @Override
    public Object onNetworkCall(int taskId, Object o) throws JsonParsingException, JsonParsingException {
        if (taskId == ThreadTaskIds.GET_ALL_PROVIDERS) {
            String url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                    + "GetAllProviders.aspx?token=" + CMN_Preferences.getUserToken(ChangeAssignmentsbyPatientActivityCMN.this) + "&AssignmentOption=" + assignmentsTypeModel.getAssignmentsOptions();

            NetworkTools networkTools = NetworkTools.getInstance();
            if (networkTools.checkNetworkConnection(ChangeAssignmentsbyPatientActivityCMN.this) && url.length() > 0) {
                JSONObject response = networkTools.getJsonData(ChangeAssignmentsbyPatientActivityCMN.this, url);
                try {
                    if (response != null) {
                        parseJsonData(response, ThreadTaskIds.GET_ALL_PROVIDERS);
                    }
                } catch (JSONException e) {
                    e.getMessage();
                }
            } else {
                Toast.makeText(ChangeAssignmentsbyPatientActivityCMN.this, CMN_ErrorMessages.getInstance().getValue(12), Toast.LENGTH_SHORT).show();
            }
        }
        if (taskId == ThreadTaskIds.GET_PATIENT_BY_PROVIDER) {
            String url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                    + "GetAllPatientsInOrg.aspx?token=" + CMN_Preferences.getUserToken(ChangeAssignmentsbyPatientActivityCMN.this);

            NetworkTools networkTools = NetworkTools.getInstance();
            if (networkTools.checkNetworkConnection(ChangeAssignmentsbyPatientActivityCMN.this) && url.length() > 0) {
                JSONObject response = networkTools.getJsonData(ChangeAssignmentsbyPatientActivityCMN.this, url);
                try {
                    if (response != null) {
                        parseJsonData(response, ThreadTaskIds.GET_PATIENT_BY_PROVIDER);
                    }
                } catch (JSONException e) {
                    e.getMessage();
                }
            } else {
                Toast.makeText(ChangeAssignmentsbyPatientActivityCMN.this, CMN_ErrorMessages.getInstance().getValue(12), Toast.LENGTH_SHORT).show();
            }
        }
        if (taskId == ThreadTaskIds.GET_ASSIGNMENTS_FOR_PATIENT) {
            String url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                    + "getAssignmentsforPatient.aspx?token=" + CMN_Preferences.getUserToken(ChangeAssignmentsbyPatientActivityCMN.this) + "&patientid=" + conntextid;

            NetworkTools networkTools = NetworkTools.getInstance();
            if (networkTools.checkNetworkConnection(ChangeAssignmentsbyPatientActivityCMN.this) && url.length() > 0) {
                JSONObject response = networkTools.getJsonData(ChangeAssignmentsbyPatientActivityCMN.this, url);
                try {
                    if (response != null) {
                        parseJsonData(response, ThreadTaskIds.GET_ASSIGNMENTS_FOR_PATIENT);
                    }
                } catch (JSONException e) {
                    e.getMessage();
                }
            } else {
                Toast.makeText(ChangeAssignmentsbyPatientActivityCMN.this, CMN_ErrorMessages.getInstance().getValue(12), Toast.LENGTH_SHORT).show();
            }
        }
        if (taskId == ThreadTaskIds.GET_HOSPITAL_DEPARTMENTS) {
            String url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                    + "getHospitalDepartments.aspx?token=" + CMN_Preferences.getUserToken(ChangeAssignmentsbyPatientActivityCMN.this) + "&Assignmenttype=Patient";

            NetworkTools networkTools = NetworkTools.getInstance();
            if (networkTools.checkNetworkConnection(ChangeAssignmentsbyPatientActivityCMN.this) && url.length() > 0) {
                JSONObject response = networkTools.getJsonData(ChangeAssignmentsbyPatientActivityCMN.this, url);
                try {
                    if (response != null) {
                        parseJsonData(response, ThreadTaskIds.GET_HOSPITAL_DEPARTMENTS);
                    }
                } catch (JSONException e) {
                    e.getMessage();
                }
            } else {
                Toast.makeText(ChangeAssignmentsbyPatientActivityCMN.this, CMN_ErrorMessages.getInstance().getValue(12), Toast.LENGTH_SHORT).show();
            }
        }
        if (taskId == ThreadTaskIds.SAVE_ASSIGN_TO_PATIENT) {

            String url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                    + "AssignbyPatient.aspx?token=" + CMN_Preferences.getUserToken(ChangeAssignmentsbyPatientActivityCMN.this) + "&patientid=" + conntextid;

            NetworkTools networkTools = NetworkTools.getInstance();
            if (networkTools.checkNetworkConnection(ChangeAssignmentsbyPatientActivityCMN.this) && url.length() > 0) {
                JSONObject response = networkTools.postJsonArrayData(ChangeAssignmentsbyPatientActivityCMN.this, url, jsonArray);
                try {
                    if (response != null) {
                        parseJsonData(response, ThreadTaskIds.SAVE_ASSIGN_TO_PATIENT);
                    }
                } catch (JSONException e) {
                    e.getMessage();
                }
            } else {
                Toast.makeText(ChangeAssignmentsbyPatientActivityCMN.this, CMN_ErrorMessages.getInstance().getValue(12), Toast.LENGTH_SHORT).show();
            }
        }
        return null;
    }

    @Override
    public Object onNetworkError(int taskId, NetworkException o) {
        return null;
    }

    private void parseJsonData(final JSONObject response, int id) throws JSONException {
        if (id == ThreadTaskIds.GET_ALL_PROVIDERS) {
            if (response.getJSONObject("Result").getInt("Code") == 0) {
                assignmentProvidersModels = new ArrayList<>();
                JSONArray messagesArray = response.getJSONArray("Data");
                for (int i = 0; i < messagesArray.length(); i++) {
                    JSONObject jsonObject = messagesArray.getJSONObject(i);
                    ProviderAssignmentPatientMergedModel assignmentProvidersModel = new ProviderAssignmentPatientMergedModel();
                    assignmentProvidersModel.setproviderid(jsonObject.getString("ID"));
                    assignmentProvidersModel.setProvidername(jsonObject.getString("Name"));
                    assignmentProvidersModel.setRoleCategory(jsonObject.getString("RoleCategory"));
                    assignmentProvidersModel.setAssignmentProviderId("");
                    assignmentProvidersModel.setAssignmentProviderName("Staff not selected");
                    assignmentProvidersModel.sethDeptId("");
                    assignmentProvidersModel.sethDeptName("");
                    assignmentProvidersModel.sethDeptNameToShow("Staff not selected");
                    assignmentProvidersModel.setLead(false);
                    assignmentProvidersModel.setEdited(false);
                    assignmentProvidersModels.add(assignmentProvidersModel);
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                ChangeAssignmentsbyPatientActivityCMN.this);
                        builder.setTitle("Assignments");
                        try {
                            builder.setMessage(response.getJSONObject("Result").getString("Message"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                try {
                                    dialog.dismiss();
//                                    finish();
                                } catch (Exception e) {

                                }
                            }
                        });
                        builder.show();

                    }
                });
            }


        } else if (id == ThreadTaskIds.GET_PATIENT_BY_PROVIDER) {
            if (response.getJSONObject("Result").getInt("Code") == 0) {
                assignmentPatientModels = new ArrayList<>();
                assignmentPatientsNames = new ArrayList<>();
                assignmentPatientsNames.add("");
                AssignmentPatientModel dummypatientmodel = new AssignmentPatientModel();
                dummypatientmodel.setPatientId("00000");
                dummypatientmodel.setPatientName("");
                assignmentPatientModels.add(dummypatientmodel);
                JSONArray messagesArray = response.getJSONArray("Data");
                for (int i = 0; i < messagesArray.length(); i++) {
                    JSONObject jsonObject = messagesArray.getJSONObject(i);
                    AssignmentPatientModel patientModel = new AssignmentPatientModel();
                    patientModel.setPatientId(jsonObject.getString("patientid"));
                    patientModel.setSurgeryTime(jsonObject.getString("SurgeryTime"));
                    patientModel.setPatientName(jsonObject.getString("name"));
                    assignmentPatientsNames.add(jsonObject.getString("name"));
                    assignmentPatientModels.add(patientModel);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        _patientNamesAdapter = new ArrayAdapter<String>(ChangeAssignmentsbyPatientActivityCMN.this,
                                android.R.layout.simple_spinner_item, assignmentPatientsNames);
                        _patientNamesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        patients.setAdapter(_patientNamesAdapter);
                    }
                });

            }
        } else if (id == ThreadTaskIds.GET_ASSIGNMENTS_FOR_PATIENT) {
            if (response.getJSONObject("Result").getInt("Code") == 0) {
                assignmentsForPatientsModels = new ArrayList<>();
                JSONArray messagesArray = response.getJSONArray("Data");
                for (int i = 0; i < messagesArray.length(); i++) {
                    JSONObject jsonObject = messagesArray.getJSONObject(i);
                    AssignmentsForPatientsModel assignmentsForPatientsModel = new AssignmentsForPatientsModel();
                    assignmentsForPatientsModel.setProviderId(jsonObject.getString("providerId"));
                    assignmentsForPatientsModel.setProviderName(jsonObject.getString("providerName"));
                    assignmentsForPatientsModel.sethDeptId(jsonObject.getString("HdeptId"));
                    assignmentsForPatientsModel.sethDeptName(jsonObject.getString("HdeptName"));
                    assignmentsForPatientsModel.sethDeptNameToShow(jsonObject.getString("HdeptName"));
                    assignmentsForPatientsModel.setLead(Boolean.parseBoolean(jsonObject.getString("isLead")));
                    assignmentsForPatientsModel.setEdited(false);
                    assignmentsForPatientsModels.add(assignmentsForPatientsModel);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < assignmentsForPatientsModels.size(); i++) {
                            for (int j = 0; j < assignmentProvidersModels.size(); j++) {
                                if (assignmentsForPatientsModels.get(i).getProviderId().equalsIgnoreCase(assignmentProvidersModels.get(j).getproviderid())) {
                                    ProviderAssignmentPatientMergedModel providerAssignmentPatientMergedModel = new ProviderAssignmentPatientMergedModel();
                                    providerAssignmentPatientMergedModel.setproviderid(assignmentProvidersModels.get(j).getproviderid());
                                    providerAssignmentPatientMergedModel.setProvidername(assignmentProvidersModels.get(j).getProvidername());
                                    providerAssignmentPatientMergedModel.setRoleCategory(assignmentProvidersModels.get(j).getRoleCategory());
                                    providerAssignmentPatientMergedModel.setAssignmentProviderId(assignmentsForPatientsModels.get(i).getProviderId());
                                    providerAssignmentPatientMergedModel.setAssignmentProviderName(assignmentsForPatientsModels.get(i).getProviderName());
                                    providerAssignmentPatientMergedModel.sethDeptId(assignmentsForPatientsModels.get(i).gethDeptId());
                                    providerAssignmentPatientMergedModel.sethDeptName(assignmentsForPatientsModels.get(i).gethDeptName());
                                    providerAssignmentPatientMergedModel.sethDeptNameToShow(assignmentsForPatientsModels.get(i).gethDeptNameToShow());
                                    providerAssignmentPatientMergedModel.setLead(false);
                                    providerAssignmentPatientMergedModel.setEdited(false);
                                    assignmentProvidersModels.remove(j);
                                    assignmentProvidersModels.add(j, providerAssignmentPatientMergedModel);
                                }
                            }
                        }

                        assignmentForPatientAdapter = new AssignmentForPatientAdapter(ChangeAssignmentsbyPatientActivityCMN.this, assignmentProvidersModels, assignmentHospitalModels);
                        assignmentsforproviders.setAdapter(assignmentForPatientAdapter);

                    }
                });

            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                ChangeAssignmentsbyPatientActivityCMN.this);
                        builder.setTitle("Assignments");
                        try {
                            builder.setMessage(response.getJSONObject("Result").getString("Message"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                try {
                                    dialog.dismiss();
//                                    finish();
                                } catch (Exception e) {

                                }
                            }
                        });
                        builder.show();

                    }
                });
            }
        } else if (id == ThreadTaskIds.GET_HOSPITAL_DEPARTMENTS) {
            if (response.getJSONObject("Result").getInt("Code") == 0) {
                assignmentHospitalModels = new ArrayList<>();
                JSONArray messagesArray = response.getJSONArray("Data");
                AssignmentHospitalsModel assignmentRolesModel1 = new AssignmentHospitalsModel();
                assignmentRolesModel1.setHdeptId("00000");
                assignmentRolesModel1.setHdeptName("Set new role");
                assignmentHospitalModels.add(assignmentRolesModel1);
                for (int i = 0; i < messagesArray.length(); i++) {
                    JSONObject jsonObject = messagesArray.getJSONObject(i);
                    AssignmentHospitalsModel assignmentRolesModel = new AssignmentHospitalsModel();
                    assignmentRolesModel.setHdeptId(jsonObject.getString("HdeptId"));
                    assignmentRolesModel.setHdeptName(jsonObject.getString("HdeptName"));
                    assignmentRolesModel.setAssignedPerOR(jsonObject.getBoolean("AssignedPerOR"));
                    assignmentRolesModel.setAssignedPerOrg(jsonObject.getBoolean("AssignedPerOrg"));
                    assignmentRolesModel.setAssignedPerPatient(jsonObject.getBoolean("AssignedPerPatient"));
                    List<String> list = new ArrayList<>();
                    if (jsonObject.getString("RoleCategory").contains(",")) {
                        list = new ArrayList<String>(Arrays.asList(jsonObject.getString("RoleCategory").split(",")));
                    } else {
                        list.add(jsonObject.getString("RoleCategory"));
                    }
                    assignmentRolesModel.setRoleCategory(list);

                    assignmentHospitalModels.add(assignmentRolesModel);
                }


            }
        } else if (id == ThreadTaskIds.SAVE_ASSIGN_TO_PATIENT) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
//                        if (response.getJSONObject("Result").getInt("Code") == 0) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(ChangeAssignmentsbyPatientActivityCMN.this);
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
