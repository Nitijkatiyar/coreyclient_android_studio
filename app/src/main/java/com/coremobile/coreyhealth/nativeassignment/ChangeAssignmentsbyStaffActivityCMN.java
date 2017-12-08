package com.coremobile.coreyhealth.nativeassignment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.coremobile.coreyhealth.R;
import com.coremobile.coreyhealth.base.CMN_AppBaseActivity;
import com.coremobile.coreyhealth.errorhandling.ActivityPackage;
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
import java.util.Arrays;
import java.util.List;

/**
 * @author Nitij Katiyar
 */

public class ChangeAssignmentsbyStaffActivityCMN extends CMN_AppBaseActivity implements NetworkCallBack {

    private SharedPreferences mCurrentUserPref;
    public static final String CURRENT_USER = "CurrentUser";
    private String organizationName;
    String password, user_category, context_name;
    String userName;
    LinearLayout progressBar;
    ArrayList<AssignmentHospitalsModel> assignmentRolesModels;
    ArrayList<String> assignmentRoles;
    ArrayList<AllProvidersModel> assignmentProvidersModels;
    ArrayList<String> assignmentProviders;
    ArrayList<AllProvidersModel> providersModels;
    ArrayAdapter _rolesNamesAdapter;
    ArrayAdapter _providerNamesAdapter;
    Spinner roleSpinner, staffMemberSpinner;
    RelativeLayout selectPatient;
    TextView _patientNames;
    ArrayList<AssignmentPatientModel> patientList = new ArrayList<>();
    Button assign, unAssign;
    boolean isAssign;
    ToggleButton replace;
    AssignmentsTypeModel assignmentsTypeModel;
    boolean isSelfAssignment = false;
    LinearLayout staffLayout, replaceAssignmentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignmntbystaff);

        mCurrentUserPref = getSharedPreferences(CURRENT_USER, 0);
         user_category = mCurrentUserPref.getString("user_category", null);
        context_name = mCurrentUserPref.getString("context_name", null);

        assign = (Button) findViewById(R.id.buttonAssign);
        unAssign = (Button) findViewById(R.id.buttonUnAssign);
        replace = (ToggleButton) findViewById(R.id.isReplaceToggle);
        staffLayout = (LinearLayout) findViewById(R.id.staffLayout);
        replaceAssignmentLayout = (LinearLayout) findViewById(R.id.replaceLayout);


        getActionBar().setHomeButtonEnabled(true);
        if (getIntent() != null && getIntent().hasExtra("title")) {
            if (getIntent().hasExtra("SelfAssignment")) {
                isSelfAssignment = true;
            }
            assignmentsTypeModel = (AssignmentsTypeModel) getIntent().getSerializableExtra("title");
            getActionBar().setTitle(
                    "" + assignmentsTypeModel.getName());
        } else {
            getActionBar().setTitle(
                    "Assignment");
        }
        getActionBar().setDisplayHomeAsUpEnabled(true);

        roleSpinner = (Spinner) findViewById(R.id.selectRole);
        staffMemberSpinner = (Spinner) findViewById(R.id.selectStaff);
        progressBar = (LinearLayout) findViewById(R.id.progressbar);
        selectPatient = (RelativeLayout) findViewById(R.id.relativelayout);
        _patientNames = (TextView) findViewById(R.id.selectPatient);

        if (isSelfAssignment) {
            staffLayout.setVisibility(View.GONE);
            replaceAssignmentLayout.setVisibility(View.GONE);
        } else {
            staffLayout.setVisibility(View.VISIBLE);
            replaceAssignmentLayout.setVisibility(View.VISIBLE);
        }

        selectPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	   startActivityForResult(new Intent().setClassName(getPackageName(), ActivityPackage.SelectPatientsActivity), 1);
            }
        });

        NetworkAsyncTask networkAsyncTask = NetworkAsyncTask.getInstance();
        try {
            networkAsyncTask.startNetworkCall(ThreadTaskIds.GET_ALL_PROVIDERS, ChangeAssignmentsbyStaffActivityCMN
                    .this);
        } catch (NetworkException e) {
            e.getMessage();
        }


        NetworkAsyncTask networkAsyncTask1 = NetworkAsyncTask.getInstance();
        try {
            networkAsyncTask1.startNetworkCall(ThreadTaskIds.GET_HOSPITAL_DEPARTMENTS, ChangeAssignmentsbyStaffActivityCMN
                    .this);
        } catch (NetworkException e) {
            e.getMessage();
        }


        assign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (patientList.size() == 0) {
                    Toast.makeText(ChangeAssignmentsbyStaffActivityCMN.this, CMN_ErrorMessages.getInstance().getValue(155), Toast.LENGTH_SHORT).show();
                    return;
                }
                isAssign = true;
                NetworkAsyncTask networkAsyncTask = NetworkAsyncTask.getInstance();
                try {
                    networkAsyncTask.startNetworkCall(ThreadTaskIds.SAVE_ASSIGNMENT_BY_STAFF, ChangeAssignmentsbyStaffActivityCMN
                            .this);
                } catch (NetworkException e) {
                    e.getMessage();
                }
            }
        });
        unAssign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (patientList.size() == 0) {
                    Toast.makeText(ChangeAssignmentsbyStaffActivityCMN.this, CMN_ErrorMessages.getInstance().getValue(155), Toast.LENGTH_SHORT).show();
                    return;
                }
                isAssign = false;
                NetworkAsyncTask networkAsyncTask = NetworkAsyncTask.getInstance();
                try {
                    networkAsyncTask.startNetworkCall(ThreadTaskIds.SAVE_ASSIGNMENT_BY_STAFF, ChangeAssignmentsbyStaffActivityCMN
                            .this);
                } catch (NetworkException e) {
                    e.getMessage();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            {
                Log.d("onActivityResult", "onActivityResult");
                patientList = (ArrayList<AssignmentPatientModel>) data
                        .getSerializableExtra("data");

                String toPatients = "";
                for (int i = 0; i < patientList.size(); i++) {
                    toPatients = toPatients + ""
                            + patientList.get(i).getPatientName().toString() + ",";
                }
                if (patientList.size() > 2) {
                    _patientNames.setText("" + patientList.size() + " patients selected");
                } else {
                    _patientNames.setText(toPatients.substring(0,
                            toPatients.length() - 1));
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_save);
        item.setVisible(false);
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

            default:
                return super.onOptionsItemSelected(item);
        }

    }


    @Override
    public void beforeNetworkCall(int taskId) {
        if (progressBar.getVisibility() == View.GONE) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public Object afterNetworkCall(int taskId, Object o) {
        if (progressBar.getVisibility() == View.VISIBLE && taskId == ThreadTaskIds.GET_HOSPITAL_DEPARTMENTS) {
            progressBar.setVisibility(View.GONE);
        }
        return null;
    }

    @Override
    public Object onNetworkCall(int taskId, Object o) throws
            JsonParsingException, JsonParsingException {
        if (taskId == ThreadTaskIds.GET_HOSPITAL_DEPARTMENTS) {
            String url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                    + "GetHospitalDepartments.aspx?token="+ CMN_Preferences.getUserToken(ChangeAssignmentsbyStaffActivityCMN.this) + "&assignmentoption=" + assignmentsTypeModel.getAssignmentsOptions();

            NetworkTools networkTools = NetworkTools.getInstance();
            if (networkTools.checkNetworkConnection(ChangeAssignmentsbyStaffActivityCMN
                    .this) && url.length() > 0) {
                JSONObject response = networkTools.getJsonData(ChangeAssignmentsbyStaffActivityCMN
                        .this, url);
                try {
                    if (response != null) {
                        parseJsonData(response, ThreadTaskIds.GET_HOSPITAL_DEPARTMENTS);
                    }
                } catch (JSONException e) {
                    e.getMessage();
                }
            } else {
                Toast.makeText(ChangeAssignmentsbyStaffActivityCMN
                        .this, CMN_ErrorMessages.getInstance().getValue(12) , Toast.LENGTH_SHORT).show();
            }
        } else if (taskId == ThreadTaskIds.GET_ALL_PROVIDERS) {
            String url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                    + "GetAllProviders.aspx?token="+ CMN_Preferences.getUserToken(ChangeAssignmentsbyStaffActivityCMN.this) + "&assignmentoption=" + assignmentsTypeModel.getAssignmentsOptions();

            NetworkTools networkTools = NetworkTools.getInstance();
            if (networkTools.checkNetworkConnection(ChangeAssignmentsbyStaffActivityCMN
                    .this) && url.length() > 0) {
                JSONObject response = networkTools.getJsonData(ChangeAssignmentsbyStaffActivityCMN
                        .this, url);
                try {
                    if (response != null) {
                        parseJsonData(response, ThreadTaskIds.GET_ALL_PROVIDERS);
                    }
                } catch (JSONException e) {
                    e.getMessage();
                }
            } else {
                Toast.makeText(ChangeAssignmentsbyStaffActivityCMN
                        .this, CMN_ErrorMessages.getInstance().getValue(12) , Toast.LENGTH_SHORT).show();
            }
        } else if (taskId == ThreadTaskIds.SAVE_ASSIGNMENT_BY_STAFF) {
            String url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                    + "AssignByStaff.aspx?token="+ CMN_Preferences.getUserToken(ChangeAssignmentsbyStaffActivityCMN.this) + "&assign=" + isAssign;


            NetworkTools networkTools = NetworkTools.getInstance();
            if (networkTools.checkNetworkConnection(ChangeAssignmentsbyStaffActivityCMN
                    .this) && url.length() > 0) {
                JSONObject response = networkTools.postJsonData(ChangeAssignmentsbyStaffActivityCMN
                        .this, url, generateJson());
                try {
                    if (response != null) {
                        parseJsonData(response, ThreadTaskIds.SAVE_ASSIGNMENT_BY_STAFF);
                    }
                } catch (JSONException e) {
                    e.getMessage();
                }
            } else {
                Toast.makeText(ChangeAssignmentsbyStaffActivityCMN
                        .this, CMN_ErrorMessages.getInstance().getValue(12) , Toast.LENGTH_SHORT).show();
            }
        }
        return o;
    }

    @Override
    public Object onNetworkError(int taskId, NetworkException o) {
        return null;
    }

    private void parseJsonData(final JSONObject response, int id) throws JSONException {
        if (id == ThreadTaskIds.GET_HOSPITAL_DEPARTMENTS) {
            if (response.getJSONObject("Result").getInt("Code") == 0) {
                assignmentRolesModels = new ArrayList<>();
                assignmentRoles = new ArrayList<>();
                JSONArray messagesArray = response.getJSONArray("Data");
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
                    assignmentRoles.add(jsonObject.getString("HdeptName"));
                    assignmentRolesModels.add(assignmentRolesModel);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        _rolesNamesAdapter = new ArrayAdapter<String>(ChangeAssignmentsbyStaffActivityCMN
                                .this,
                                android.R.layout.simple_spinner_item, assignmentRoles);
                        _rolesNamesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        roleSpinner.setAdapter(_rolesNamesAdapter);

                        roleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                providersModels = new ArrayList<AllProvidersModel>();
                                assignmentProviders = new ArrayList<String>();
                                for (int i = 0; i < assignmentProvidersModels.size(); i++) {
                                    List<String> categories = assignmentRolesModels.get(position).getRoleCategory();
                                    for (int j = 0; j < categories.size(); j++) {
                                        if (categories.get(j).equalsIgnoreCase("" + assignmentProvidersModels.get(i).getRoleCategory())) {
                                            providersModels.add(assignmentProvidersModels.get(i));
                                            assignmentProviders.add(assignmentProvidersModels.get(i).getProviderName());
                                        }
                                    }

                                }
                                _providerNamesAdapter = new ArrayAdapter(ChangeAssignmentsbyStaffActivityCMN.this, android.R.layout.simple_spinner_item, assignmentProviders);
                                _providerNamesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                staffMemberSpinner.setAdapter(_providerNamesAdapter);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }
                });

            }
        } else if (id == ThreadTaskIds.GET_ALL_PROVIDERS) {
            if (response.getJSONObject("Result").getInt("Code") == 0) {
                assignmentProvidersModels = new ArrayList<>();
                JSONArray messagesArray = response.getJSONArray("Data");
                for (int i = 0; i < messagesArray.length(); i++) {
                    JSONObject jsonObject = messagesArray.getJSONObject(i);
                    AllProvidersModel assignmentProvidersModel = new AllProvidersModel();
                    assignmentProvidersModel.setProviderId(jsonObject.getString("ID"));
                    assignmentProvidersModel.setProviderName(jsonObject.getString("Name"));
                    assignmentProvidersModel.setRoleCategory(jsonObject.getInt("RoleCategory"));
                    assignmentProvidersModels.add(assignmentProvidersModel);
                }
            }
        } else if (id == ThreadTaskIds.SAVE_ASSIGNMENT_BY_STAFF) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
//                        if (response.getJSONObject("Result").getInt("Code") == 0) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(ChangeAssignmentsbyStaffActivityCMN
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

    private JSONObject generateJson() {

//        JSONArray jsonArray = new JSONArray();


        JSONObject jsonObject = new JSONObject();


        try {
            if (isSelfAssignment) {
                jsonObject.put("providerId", CMN_Preferences.getLoggedInUserId(ChangeAssignmentsbyStaffActivityCMN.this));
            } else {
                jsonObject.put("providerId", providersModels.get(staffMemberSpinner.getSelectedItemPosition()).getProviderId());
                jsonObject.put("replace", replace.isChecked());
            }
            jsonObject.put("HdeptId", assignmentRolesModels.get(roleSpinner.getSelectedItemPosition()).getHdeptId());
            String patIds = "";
            for (int i = 0; i < patientList.size(); i++) {
                if (i == 0) {
                    patIds = patIds + "" + patientList.get(i).getPatientId();
                } else {
                    patIds = patIds + "," + patientList.get(i).getPatientId();
                }
            }
            jsonObject.put("patientids", patIds);
        } catch (JSONException e) {
            e.getMessage();
        }
        return jsonObject;
    }

    @Override
    public void onBackPressed() {
        if (progressBar.getVisibility() == View.VISIBLE) {

        } else {
            super.onBackPressed();
        }
    }
}
