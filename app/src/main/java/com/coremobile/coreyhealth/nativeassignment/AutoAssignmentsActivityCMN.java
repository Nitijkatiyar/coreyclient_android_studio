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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
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
import java.util.Arrays;
import java.util.List;

import static com.coremobile.coreyhealth.networkutils.ThreadTaskIds.GET_ALL_PROVIDERS;

/**
 * @author Nitij Katiyar
 */

public class AutoAssignmentsActivityCMN extends CMN_AppBaseActivity implements NetworkCallBack {

    private SharedPreferences mCurrentUserPref;
    public static final String CURRENT_USER = "CurrentUser";
    private String organizationName;
    String password, user_category, context_name;
    String userName;
    LinearLayout progressBar;
    ArrayList<AssignmentHospitalsModel> assignmentRolesModels;
    ArrayList<String> assignmentRoles;
    ArrayList<AllProvidersModel> assignmentProvidersModels;
    ArrayList<AllProvidersModel> providersModels;
    ArrayList<String> assignmentProviders;
    ArrayAdapter _rolesNamesAdapter;
    ArrayAdapter _providerNamesAdapter;
    Spinner roleSpinner, staffMemberSpinner;
    AssignmentsTypeModel assignmentsTypeModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autoassignmnt);

        mCurrentUserPref = getSharedPreferences(CURRENT_USER, 0);
          user_category = mCurrentUserPref.getString("user_category", null);
        context_name = mCurrentUserPref.getString("context_name", null);

        roleSpinner = (Spinner) findViewById(R.id.selectRole);
        staffMemberSpinner = (Spinner) findViewById(R.id.selectStaffMember);
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
            networkAsyncTask.startNetworkCall(GET_ALL_PROVIDERS, AutoAssignmentsActivityCMN.this);
        } catch (NetworkException e) {
            e.getMessage();
        }


        NetworkAsyncTask networkAsyncTask1 = NetworkAsyncTask.getInstance();
        try {
            networkAsyncTask1.startNetworkCall(ThreadTaskIds.GET_ASSIGN_ROLES, AutoAssignmentsActivityCMN.this);
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
                NetworkAsyncTask networkAsyncTask = NetworkAsyncTask.getInstance();
                try {
                    networkAsyncTask.startNetworkCall(ThreadTaskIds.SAVE_AUTO_ASSIGNMENT, AutoAssignmentsActivityCMN.this);
                } catch (NetworkException e) {
                    e.getMessage();
                }
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
        if (progressBar.getVisibility() == View.VISIBLE && taskId == GET_ALL_PROVIDERS || taskId == ThreadTaskIds.SAVE_AUTO_ASSIGNMENT) {
            progressBar.setVisibility(View.GONE);
        }
        return null;
    }

    @Override
    public Object onNetworkCall(int taskId, Object o) throws JsonParsingException, JsonParsingException {
        if (taskId == ThreadTaskIds.GET_ASSIGN_ROLES) {
            String url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                    + "GetHospitalDepartments.aspx?token="+ CMN_Preferences.getUserToken(AutoAssignmentsActivityCMN.this) + "&roleCategory=Auto";

            NetworkTools networkTools = NetworkTools.getInstance();
            if (networkTools.checkNetworkConnection(AutoAssignmentsActivityCMN.this) && url.length() > 0) {
                JSONObject response = networkTools.getJsonData(AutoAssignmentsActivityCMN.this, url);
                try {
                    if (response != null) {
                        parseJsonData(response, ThreadTaskIds.GET_ASSIGN_ROLES);
                    }
                } catch (JSONException e) {
                    Log.d("JSON Exception", e.getMessage());
                    e.getMessage();
                }
            } else {
                Toast.makeText(AutoAssignmentsActivityCMN.this, CMN_ErrorMessages.getInstance().getValue(12) , Toast.LENGTH_SHORT).show();
            }
        } else if (taskId == GET_ALL_PROVIDERS) {
            String url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                    + "GetAllProviders.aspx?token="+ CMN_Preferences.getUserToken(AutoAssignmentsActivityCMN.this);

            NetworkTools networkTools = NetworkTools.getInstance();
            if (networkTools.checkNetworkConnection(AutoAssignmentsActivityCMN.this) && url.length() > 0) {
                JSONObject response = networkTools.getJsonData(AutoAssignmentsActivityCMN.this, url);
                try {
                    if (response != null) {
                        parseJsonData(response, GET_ALL_PROVIDERS);
                    }
                } catch (JSONException e) {
                    Log.d("JSON Exception", e.getMessage());
                    e.getMessage();
                }
            } else {
                Toast.makeText(AutoAssignmentsActivityCMN.this, CMN_ErrorMessages.getInstance().getValue(12) , Toast.LENGTH_SHORT).show();
            }
        } else if (taskId == ThreadTaskIds.SAVE_AUTO_ASSIGNMENT) {
            String url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                    + "AutoAssignment.aspx?token="+ CMN_Preferences.getUserToken(AutoAssignmentsActivityCMN.this);

            NetworkTools networkTools = NetworkTools.getInstance();
            if (networkTools.checkNetworkConnection(AutoAssignmentsActivityCMN.this) && url.length() > 0) {
                JSONObject response = networkTools.postJsonArrayData(AutoAssignmentsActivityCMN.this, url, generateJson());
                try {
                    if (response != null) {
                        parseJsonData(response, ThreadTaskIds.SAVE_AUTO_ASSIGNMENT);
                    }
                } catch (JSONException e) {
                    Log.d("JSON Exception", e.getMessage());
                    e.getMessage();
                }
            } else {
                Toast.makeText(AutoAssignmentsActivityCMN.this, CMN_ErrorMessages.getInstance().getValue(12) , Toast.LENGTH_SHORT).show();
            }
        }
        return o;
    }

    @Override
    public Object onNetworkError(int taskId, NetworkException o) {
        return null;
    }

    private void parseJsonData(final JSONObject response, int id) throws JSONException {
        if (id == ThreadTaskIds.GET_ASSIGN_ROLES) {
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
                        _rolesNamesAdapter = new ArrayAdapter<String>(AutoAssignmentsActivityCMN.this,
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

//                                    if (assignmentRolesModels.get(position).gethDeptId() == assignmentProvidersModels.get(i).gethDeptId()) {
//                                        providersModels.add(assignmentProvidersModels.get(i));
//                                        assignmentProviders.add(assignmentProvidersModels.get(i).getProviderName());
//                                    }
                                }
                                _providerNamesAdapter = new ArrayAdapter(AutoAssignmentsActivityCMN.this, android.R.layout.simple_spinner_item, assignmentProviders);
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
        } else if (id == GET_ALL_PROVIDERS) {
            if (response.getJSONObject("Result").getInt("Code") == 0) {
                assignmentProvidersModels = new ArrayList<>();
                assignmentProviders = new ArrayList<>();
                JSONArray messagesArray = response.getJSONArray("Data");
                for (int i = 0; i < messagesArray.length(); i++) {
                    JSONObject jsonObject = messagesArray.getJSONObject(i);
                    AllProvidersModel assignmentProvidersModel = new AllProvidersModel();
                    assignmentProvidersModel.setProviderId(jsonObject.getString("ID"));
                    assignmentProvidersModel.setProviderName(jsonObject.getString("Name"));
                    assignmentProvidersModel.setRoleCategory(jsonObject.getInt("RoleCategory"));
                    assignmentProviders.add(jsonObject.getString("Name"));
                    assignmentProvidersModels.add(assignmentProvidersModel);
                }
            }
        } else if (id == ThreadTaskIds.SAVE_AUTO_ASSIGNMENT) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
//                        if (response.getJSONObject("Result").getInt("Code") == 0) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(AutoAssignmentsActivityCMN.this);
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

    private JSONArray generateJson() {

        JSONArray jsonArray = new JSONArray();


        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("providerId", assignmentProvidersModels.get(staffMemberSpinner.getSelectedItemPosition()).getProviderId());
            jsonObject.put("HdeptId", assignmentRolesModels.get(roleSpinner.getSelectedItemPosition()).getHdeptId());
        } catch (JSONException e) {
            e.getMessage();
        }
        jsonArray.put(jsonObject);


        return jsonArray;
    }

    @Override
    public void onBackPressed() {
        if (progressBar.getVisibility() == View.VISIBLE) {

        } else {
            super.onBackPressed();
        }
    }
}
