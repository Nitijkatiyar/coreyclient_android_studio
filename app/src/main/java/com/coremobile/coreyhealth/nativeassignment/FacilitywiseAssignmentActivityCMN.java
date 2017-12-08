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

/**
 * @author Nitij Katiyar
 */

public class FacilitywiseAssignmentActivityCMN extends CMN_AppBaseActivity implements NetworkCallBack {

    private SharedPreferences mCurrentUserPref;
    public static final String CURRENT_USER = "CurrentUser";
    private String organizationName;
    String   user_category, context_name;

    ListView listView;
    LinearLayout progressBar;
    AssignmentsTypeModel assignmentsTypeModel;
    ArrayList<AllProvidersModel> assignmentProvidersModels;
    ArrayList<FacilityAssignmentModel> facilityAssignmentModels;
    AssignmentForFacilityAdapter assignmentForFacilityAdapter;
    JSONArray jsonArray;
    ArrayList<AssignmentHospitalsModel> assignmentRolesModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignmntbyfacility);

        mCurrentUserPref = getSharedPreferences(CURRENT_USER, 0);
        organizationName = mCurrentUserPref.getString("Organization", null);
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

        NetworkAsyncTask networkAsyncTask1 = NetworkAsyncTask.getInstance();
        try {
            networkAsyncTask1.startNetworkCall(ThreadTaskIds.GET_ALL_PROVIDERS, FacilitywiseAssignmentActivityCMN.this);
        } catch (NetworkException e) {
            e.getMessage();
        }

        NetworkAsyncTask networkAsyncTask = NetworkAsyncTask.getInstance();
        try {
            networkAsyncTask.startNetworkCall(ThreadTaskIds.GET_HOSPITAL_DEPARTMENTS, FacilitywiseAssignmentActivityCMN.this);
        } catch (NetworkException e) {
            e.getMessage();
        }

        NetworkAsyncTask networkAsyncTask2 = NetworkAsyncTask.getInstance();
        try {
            networkAsyncTask2.startNetworkCall(ThreadTaskIds.GET_ASSIGNMENT_FOR_FACILITY, FacilitywiseAssignmentActivityCMN.this);
        } catch (NetworkException e) {
            e.getMessage();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (assignmentForFacilityAdapter != null) {
            assignmentForFacilityAdapter.notifyDataSetChanged();
        }
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
                            Toast.makeText(FacilitywiseAssignmentActivityCMN.this, CMN_ErrorMessages.getInstance().getValue(151), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {

                    NetworkAsyncTask networkAsyncTask3 = NetworkAsyncTask.getInstance();
                    try {
                        networkAsyncTask3.startNetworkCall(ThreadTaskIds.SAVE_ASSIGNMENT_FOR_FACILITY, FacilitywiseAssignmentActivityCMN.this);
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
        for (int i = 0; i < assignmentForFacilityAdapter.getCount(); i++) {
            View view = assignmentForFacilityAdapter.getView(i, null, null);
            AssignmentHospitalsModel facilityAssignmentModel = (AssignmentHospitalsModel) assignmentForFacilityAdapter.getItem(i);

            if (AssignmentForFacilityAdapter.selectedProviders.get(i) != null) {
                JSONObject jsonObject = new JSONObject();
                String providers = "";
                List<AllProvidersModel> allProvidersModels = AssignmentForFacilityAdapter.selectedProviders.get(i);
                for (int j = 0; j < allProvidersModels.size(); j++) {
                    if (j == 0) {
                        providers = providers + "" + allProvidersModels.get(j).getProviderId();
                    } else {
                        providers = providers + "," + allProvidersModels.get(j).getProviderId();
                    }
                }
                try {
                    jsonObject.put("HdeptId", "" + facilityAssignmentModel.getHdeptId());
                    jsonObject.put("providerid", "" + providers);
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
        if (progressBar.getVisibility() == View.VISIBLE && taskId == ThreadTaskIds.GET_ASSIGNMENT_FOR_FACILITY) {
            progressBar.setVisibility(View.GONE);
        }
        return null;
    }


    @Override
    public Object onNetworkCall(int taskId, Object o) throws JsonParsingException, JsonParsingException {
        if (taskId == ThreadTaskIds.GET_ALL_PROVIDERS) {
            String url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                    + "GetAllProviders.aspx?token=" + CMN_Preferences.getUserToken(FacilitywiseAssignmentActivityCMN.this) + "&AssignmentOption=" + assignmentsTypeModel.getAssignmentsOptions();

            NetworkTools networkTools = NetworkTools.getInstance();
            if (networkTools.checkNetworkConnection(FacilitywiseAssignmentActivityCMN.this) && url.length() > 0) {
                JSONObject response = networkTools.getJsonData(FacilitywiseAssignmentActivityCMN.this, url);
                try {
                    if (response != null) {
                        parseJsonData(response, ThreadTaskIds.GET_ALL_PROVIDERS);
                    }
                } catch (JSONException e) {
                    e.getMessage();
                }
            } else {
                Toast.makeText(FacilitywiseAssignmentActivityCMN.this, CMN_ErrorMessages.getInstance().getValue(12), Toast.LENGTH_SHORT).show();
            }
        }
        if (taskId == ThreadTaskIds.GET_HOSPITAL_DEPARTMENTS) {
            String url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                    + "getHospitalDepartments.aspx?token=" + CMN_Preferences.getUserToken(FacilitywiseAssignmentActivityCMN.this) + "&AssignmentOption=" + assignmentsTypeModel.getAssignmentsOptions();

            NetworkTools networkTools = NetworkTools.getInstance();
            if (networkTools.checkNetworkConnection(FacilitywiseAssignmentActivityCMN.this) && url.length() > 0) {
                JSONObject response = networkTools.getJsonData(FacilitywiseAssignmentActivityCMN.this, url);
                try {
                    if (response != null) {
                        parseJsonData(response, ThreadTaskIds.GET_HOSPITAL_DEPARTMENTS);
                    }
                } catch (JSONException e) {
                    e.getMessage();
                }
            } else {
                Toast.makeText(FacilitywiseAssignmentActivityCMN.this,  CMN_ErrorMessages.getInstance().getValue(12), Toast.LENGTH_SHORT).show();
            }
        }
        if (taskId == ThreadTaskIds.GET_ASSIGNMENT_FOR_FACILITY) {
            String url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                    + "GetAssingmentsForFacilityMultipleProv.aspx?token=" + CMN_Preferences.getUserToken(FacilitywiseAssignmentActivityCMN.this);

            NetworkTools networkTools = NetworkTools.getInstance();
            if (networkTools.checkNetworkConnection(FacilitywiseAssignmentActivityCMN.this) && url.length() > 0) {
                JSONObject response = networkTools.getJsonData(FacilitywiseAssignmentActivityCMN.this, url);
                try {
                    if (response != null) {
                        parseJsonData(response, ThreadTaskIds.GET_ASSIGNMENT_FOR_FACILITY);
                    }
                } catch (JSONException e) {
                    e.getMessage();
                }
            } else {
                Toast.makeText(FacilitywiseAssignmentActivityCMN.this,  CMN_ErrorMessages.getInstance().getValue(12), Toast.LENGTH_SHORT).show();
            }
        }
        if (taskId == ThreadTaskIds.SAVE_ASSIGNMENT_FOR_FACILITY) {
            String url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                    + "AssignByFacilityForMultipleProviders.aspx?token=" + CMN_Preferences.getUserToken(FacilitywiseAssignmentActivityCMN.this);

            NetworkTools networkTools = NetworkTools.getInstance();
            if (networkTools.checkNetworkConnection(FacilitywiseAssignmentActivityCMN.this) && url.length() > 0) {
                JSONObject response = networkTools.postJsonArrayData(FacilitywiseAssignmentActivityCMN.this, url, jsonArray);
                try {
                    if (response != null) {
                        parseJsonData(response, ThreadTaskIds.SAVE_ASSIGNMENT_FOR_FACILITY);
                    }
                } catch (JSONException e) {
                    e.getMessage();
                }
            } else {
                Toast.makeText(FacilitywiseAssignmentActivityCMN.this,  CMN_ErrorMessages.getInstance().getValue(12), Toast.LENGTH_SHORT).show();
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
                AllProvidersModel dummymodel = new AllProvidersModel();
                dummymodel.setProviderId("000");
                dummymodel.setProviderName("Select new staff");
                dummymodel.setRoleCategory(0);
                assignmentProvidersModels.add(dummymodel);
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
        } else if (id == ThreadTaskIds.GET_HOSPITAL_DEPARTMENTS) {
            if (response.getJSONObject("Result").getInt("Code") == 0) {
                assignmentRolesModels = new ArrayList<>();
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

                    assignmentRolesModels.add(assignmentRolesModel);
                }

            }
        } else if (id == ThreadTaskIds.GET_ASSIGNMENT_FOR_FACILITY) {
            if (response.getJSONObject("Result").getInt("Code") == 0) {
                facilityAssignmentModels = new ArrayList<>();

                JSONArray messagesArray = response.getJSONArray("Data");
                for (int i = 0; i < messagesArray.length(); i++) {
                    JSONObject jsonObject = messagesArray.getJSONObject(i);
                    FacilityAssignmentModel facilityAssignmentModel = new FacilityAssignmentModel();
                    JSONArray messagesArray1 = jsonObject.getJSONArray("providers");
                    List<FacilityAssignmentProviderModel> facilityAssignmentProviderModels = new ArrayList<>();
                    for (int j = 0; j < messagesArray1.length(); j++) {
                        JSONObject jsonObject1 = messagesArray1.getJSONObject(j);
                        FacilityAssignmentProviderModel facilityAssignmentProviderModel = new FacilityAssignmentProviderModel();
                        facilityAssignmentProviderModel.setId(jsonObject1.getString("ID"));
                        facilityAssignmentProviderModel.setName(jsonObject1.getString("Name"));
                        facilityAssignmentProviderModel.setRoleCategory(jsonObject1.getString("RoleCategory"));
                        facilityAssignmentProviderModels.add(facilityAssignmentProviderModel);
                    }
                    facilityAssignmentModel.setFacilityAssignmentProviderModels(facilityAssignmentProviderModels);
                    facilityAssignmentModel.sethDeptId(jsonObject.getString("HdeptId"));
                    facilityAssignmentModel.setHdeptname(jsonObject.getString("HdeptName"));
                    facilityAssignmentModels.add(facilityAssignmentModel);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        assignmentForFacilityAdapter = new AssignmentForFacilityAdapter(FacilitywiseAssignmentActivityCMN.this, facilityAssignmentModels, assignmentProvidersModels, assignmentRolesModels);
                        listView.setAdapter(assignmentForFacilityAdapter);
                    }
                });
            }
        } else if (id == ThreadTaskIds.SAVE_ASSIGNMENT_FOR_FACILITY) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
//                        if (response.getJSONObject("Result").getInt("Code") == 0) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(FacilitywiseAssignmentActivityCMN
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
