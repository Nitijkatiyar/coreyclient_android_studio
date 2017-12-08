package com.coremobile.coreyhealth.nativeassignment;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @author Nitij Katiyar
 */

public class AssignNursestoORActivityCMN extends CMN_AppBaseActivity implements NetworkCallBack {

    private SharedPreferences mCurrentUserPref;
    public static final String CURRENT_USER = "CurrentUser";
    private String organizationName;
    String password, user_category, context_name;
    String userName;
    Spinner orNurses;
    ListView listview;
    String conntextid;
    LinearLayout progressBar;
    AssignNursetoORAdapter assignNursetoORAdapter;
    HashMap<String, AllProvidersModel> providersAsperHospitals;

    ArrayList<NurseToOrMergedModel> assignmentHospitalModels;
    ArrayList<AllProvidersModel> assignmentProvidersModels;
    ArrayList<OrUsersModel> orNursesModels;
    ArrayList<NurseToORAssignmentModel> nurseToORAssignmentModels;
    ArrayList<String> orNursesNames;
    ArrayAdapter _nursesNamesAdapter;
    AssignmentsTypeModel assignmentsTypeModel;
    ArrayList<NurseToOrMergedModel> assignmentsModel;
    String orId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignnursestoor);

        mCurrentUserPref = getSharedPreferences(CURRENT_USER, 0);

        user_category = mCurrentUserPref.getString("user_category", null);
        context_name = mCurrentUserPref.getString("context_name", null);

        progressBar = (LinearLayout) findViewById(R.id.progressbar);

        listview = (ListView) findViewById(R.id.assignmentListView);
        orNurses = (Spinner) findViewById(R.id.selectPatient);

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
            networkAsyncTask1.startNetworkCall(ThreadTaskIds.GET_ALL_PROVIDERS, AssignNursestoORActivityCMN.this);
        } catch (NetworkException e) {
            e.getMessage();
        }

        NetworkAsyncTask networkAsyncTask2 = NetworkAsyncTask.getInstance();
        try {
            networkAsyncTask2.startNetworkCall(ThreadTaskIds.GET_HOSPITAL_DEPARTMENTS, AssignNursestoORActivityCMN.this);
        } catch (NetworkException e) {
            e.getMessage();
        }

        NetworkAsyncTask networkAsyncTask3 = NetworkAsyncTask.getInstance();
        try {
            networkAsyncTask3.startNetworkCall(ThreadTaskIds.GET_NURSES_TO_OR_ASSIGNMENTS, AssignNursestoORActivityCMN.this);
        } catch (NetworkException e) {
            e.getMessage();
        }

        NetworkAsyncTask networkAsyncTask = NetworkAsyncTask.getInstance();
        try {
            networkAsyncTask.startNetworkCall(ThreadTaskIds.GET_OR_USERS, AssignNursestoORActivityCMN.this);
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

                if (orNurses.getSelectedItemPosition() == 0) {
                    Toast.makeText(AssignNursestoORActivityCMN.this, CMN_ErrorMessages.getInstance().getValue(148), Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (generateJson().length() == 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AssignNursestoORActivityCMN.this, CMN_ErrorMessages.getInstance().getValue(151), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    NetworkAsyncTask networkAsyncTask3 = NetworkAsyncTask.getInstance();
                    try {
                        networkAsyncTask3.startNetworkCall(ThreadTaskIds.SAVE_ASSIGN_NURSE_TO_OR, AssignNursestoORActivityCMN.this);
                    } catch (NetworkException e) {
                        e.getMessage();
                    }
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
        if (progressBar.getVisibility() == View.VISIBLE && taskId == ThreadTaskIds.GET_OR_USERS) {
            progressBar.setVisibility(View.GONE);
        }
        return null;
    }


    @Override
    public Object onNetworkCall(int taskId, Object o) throws JsonParsingException, JsonParsingException {
        if (taskId == ThreadTaskIds.GET_OR_USERS) {
            String url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                    + "GetORUsers.aspx?token="+ CMN_Preferences.getUserToken(AssignNursestoORActivityCMN.this) + "&assignmentoption=" + assignmentsTypeModel.getAssignmentsOptions();

            NetworkTools networkTools = NetworkTools.getInstance();
            if (networkTools.checkNetworkConnection(AssignNursestoORActivityCMN.this) && url.length() > 0) {
                JSONObject response = networkTools.getJsonData(AssignNursestoORActivityCMN.this, url);
                try {
                    if (response != null) {
                        parseJsonData(response, ThreadTaskIds.GET_OR_USERS);
                    }
                } catch (JSONException e) {
                    e.getMessage();
                }
            } else {
                Toast.makeText(AssignNursestoORActivityCMN.this, CMN_ErrorMessages.getInstance().getValue(12), Toast.LENGTH_SHORT).show();
            }
        }
        if (taskId == ThreadTaskIds.GET_ALL_PROVIDERS) {
            String url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                    + "GetAllProviders.aspx?token="+ CMN_Preferences.getUserToken(AssignNursestoORActivityCMN.this) + "&AssignmentOption=" + assignmentsTypeModel.getAssignmentsOptions();

            NetworkTools networkTools = NetworkTools.getInstance();
            if (networkTools.checkNetworkConnection(AssignNursestoORActivityCMN.this) && url.length() > 0) {
                JSONObject response = networkTools.getJsonData(AssignNursestoORActivityCMN.this, url);
                try {
                    if (response != null) {
                        parseJsonData(response, ThreadTaskIds.GET_ALL_PROVIDERS);
                    }
                } catch (JSONException e) {
                    e.getMessage();
                }
            } else {
                Toast.makeText(AssignNursestoORActivityCMN.this, CMN_ErrorMessages.getInstance().getValue(12), Toast.LENGTH_SHORT).show();
            }
        }
        if (taskId == ThreadTaskIds.GET_HOSPITAL_DEPARTMENTS) {
            String url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                    + "getHospitalDepartments.aspx?token="+ CMN_Preferences.getUserToken(AssignNursestoORActivityCMN.this) + "&AssignmentOption=" + assignmentsTypeModel.getAssignmentsOptions();

            NetworkTools networkTools = NetworkTools.getInstance();
            if (networkTools.checkNetworkConnection(AssignNursestoORActivityCMN.this) && url.length() > 0) {
                JSONObject response = networkTools.getJsonData(AssignNursestoORActivityCMN.this, url);
                try {
                    if (response != null) {
                        parseJsonData(response, ThreadTaskIds.GET_HOSPITAL_DEPARTMENTS);
                    }
                } catch (JSONException e) {
                    e.getMessage();
                }
            } else {
                Toast.makeText(AssignNursestoORActivityCMN.this, CMN_ErrorMessages.getInstance().getValue(12), Toast.LENGTH_SHORT).show();
            }
        }
        if (taskId == ThreadTaskIds.GET_NURSES_TO_OR_ASSIGNMENTS) {
            String url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                    + "GetNurseToORAssignments.aspx?token="+ CMN_Preferences.getUserToken(AssignNursestoORActivityCMN.this);

            NetworkTools networkTools = NetworkTools.getInstance();
            if (networkTools.checkNetworkConnection(AssignNursestoORActivityCMN.this) && url.length() > 0) {
                JSONObject response = networkTools.getJsonData(AssignNursestoORActivityCMN.this, url);
                try {
                    if (response != null) {
                        parseJsonData(response, ThreadTaskIds.GET_NURSES_TO_OR_ASSIGNMENTS);
                    }
                } catch (JSONException e) {
                    e.getMessage();
                }
            } else {
                Toast.makeText(AssignNursestoORActivityCMN.this, CMN_ErrorMessages.getInstance().getValue(12), Toast.LENGTH_SHORT).show();
            }
        }
        if (taskId == ThreadTaskIds.SAVE_ASSIGN_NURSE_TO_OR) {
            String url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                    + "AssignNurseToOR.aspx?token="+ CMN_Preferences.getUserToken(AssignNursestoORActivityCMN.this);

            NetworkTools networkTools = NetworkTools.getInstance();
            if (networkTools.checkNetworkConnection(AssignNursestoORActivityCMN.this) && url.length() > 0) {
                JSONObject response = networkTools.postJsonArrayData(AssignNursestoORActivityCMN.this, url, generateJson());
                try {
                    if (response != null) {
                        parseJsonData(response, ThreadTaskIds.SAVE_ASSIGN_NURSE_TO_OR);
                    }
                } catch (JSONException e) {
                    e.getMessage();
                }
            } else {
                Toast.makeText(AssignNursestoORActivityCMN.this, CMN_ErrorMessages.getInstance().getValue(12), Toast.LENGTH_SHORT).show();
            }
        }
        return o;
    }

    private JSONArray generateJson() {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < assignNursetoORAdapter.getCount(); i++) {
            View view = assignNursetoORAdapter.getView(i, null, null);
            Spinner spinner = (Spinner) view.findViewById(R.id.selectRole);
            NurseToOrMergedModel model = (NurseToOrMergedModel) assignNursetoORAdapter.getItem(i);
            JSONObject jsonObject = new JSONObject();
            if (spinner.getSelectedItemPosition() > 0) {
                try {
                    List<AllProvidersModel> allProvidersModels = assignNursetoORAdapter.providersAsPerHospitalDepartment.get(i);

                    jsonObject.put("HdeptId", "" + model.getHdeptId());
                    jsonObject.put("ORId", orId);
                    jsonObject.put("providerid", "" + allProvidersModels.get(spinner.getSelectedItemPosition() - 1).getProviderId());
                    jsonArray.put(jsonObject);
                } catch (JSONException e) {
                    e.getMessage();
                }
            }

        }

        return jsonArray;
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
        } else if (id == ThreadTaskIds.GET_OR_USERS) {
            if (response.getJSONObject("Result").getInt("Code") == 0) {
                orNursesModels = new ArrayList<>();
                orNursesNames = new ArrayList<>();
                OrUsersModel dummymodel = new OrUsersModel();
                dummymodel.setName("");
                dummymodel.setId("");
                orNursesModels.add(dummymodel);
                orNursesNames.add("");
                JSONArray messagesArray = response.getJSONArray("Data");
                for (int i = 0; i < messagesArray.length(); i++) {
                    JSONObject jsonObject = messagesArray.getJSONObject(i);
                    OrUsersModel orNursesModel = new OrUsersModel();
                    orNursesModel.setId(jsonObject.getString("ORId"));
                    orNursesModel.setName(jsonObject.getString("Name"));
                    orNursesNames.add(jsonObject.getString("Name"));
                    orNursesModels.add(orNursesModel);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        _nursesNamesAdapter = new ArrayAdapter<String>(AssignNursestoORActivityCMN.this,
                                android.R.layout.simple_spinner_item, orNursesNames);
                        _nursesNamesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        orNurses.setAdapter(_nursesNamesAdapter);
                        orNurses.setSelection(0);
                        orNurses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                orId = orNursesModels.get(position).getId();
                                ArrayList<NurseToORAssignmentModel> models = new ArrayList<NurseToORAssignmentModel>();
                                assignmentsModel = new ArrayList<NurseToOrMergedModel>();
                                if (position != 0) {
                                    for (int i = 0; i < nurseToORAssignmentModels.size(); i++) {
                                        if (nurseToORAssignmentModels.get(i).getORId().equalsIgnoreCase(orNursesModels.get(position).getId())) {
                                            models.add(nurseToORAssignmentModels.get(i));
                                        }
                                    }
                                    for (int j = 0; j < assignmentHospitalModels.size(); j++) {
                                        for (int k = 0; k < models.size(); k++) {
                                            if (assignmentHospitalModels.get(j).getHdeptId().equalsIgnoreCase(models.get(k).gethDeptId())) {
                                                NurseToOrMergedModel nurseToOrMergedModel = new NurseToOrMergedModel();
                                                nurseToOrMergedModel.setHdeptId(assignmentHospitalModels.get(j).getHdeptId());
                                                nurseToOrMergedModel.setHdeptName(assignmentHospitalModels.get(j).getHdeptName());
                                                nurseToOrMergedModel.setAssignedPerOR(assignmentHospitalModels.get(j).isAssignedPerOR());
                                                nurseToOrMergedModel.setAssignedPerOrg(assignmentHospitalModels.get(j).isAssignedPerOrg());
                                                nurseToOrMergedModel.setAssignedPerPatient(assignmentHospitalModels.get(j).isAssignedPerPatient());
                                                nurseToOrMergedModel.setRoleCategory(assignmentHospitalModels.get(j).getRoleCategory());


                                                nurseToOrMergedModel.setProviderId(models.get(k).getProviderId());
                                                nurseToOrMergedModel.setProviderName(models.get(k).getProviderName());
                                                nurseToOrMergedModel.sethDeptId(models.get(k).gethDeptId());
                                                nurseToOrMergedModel.sethDeptName(models.get(k).gethDeptName());
                                                nurseToOrMergedModel.sethDeptNameToShow(models.get(k).gethDeptNameToShow());
                                                nurseToOrMergedModel.setORId(models.get(k).getORId());
                                                nurseToOrMergedModel.setEdited(false);
                                                assignmentHospitalModels.remove(j);
                                                assignmentHospitalModels.add(j, nurseToOrMergedModel);
                                            }
                                        }
                                    }
//                                    if (models.size() > 0) {


                                    listview.setVisibility(View.VISIBLE);
                                    assignNursetoORAdapter = new AssignNursetoORAdapter(AssignNursestoORActivityCMN.this, assignmentProvidersModels, assignmentHospitalModels);
                                    listview.setAdapter(assignNursetoORAdapter);
//                                    } else {
//                                        listview.setVisibility(View.GONE);
//                                        listview.invalidate();
//                                    }
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }
                });
            }
        } else if (id == ThreadTaskIds.GET_HOSPITAL_DEPARTMENTS) {
            if (response.getJSONObject("Result").getInt("Code") == 0) {
                assignmentHospitalModels = new ArrayList<>();
                JSONArray messagesArray = response.getJSONArray("Data");
                for (int i = 0; i < messagesArray.length(); i++) {
                    JSONObject jsonObject = messagesArray.getJSONObject(i);
                    NurseToOrMergedModel nurseToOrMergedModel = new NurseToOrMergedModel();
                    nurseToOrMergedModel.setHdeptId(jsonObject.getString("HdeptId"));
                    nurseToOrMergedModel.setHdeptName(jsonObject.getString("HdeptName"));
                    nurseToOrMergedModel.setAssignedPerOR(jsonObject.getBoolean("AssignedPerOR"));
                    nurseToOrMergedModel.setAssignedPerOrg(jsonObject.getBoolean("AssignedPerOrg"));
                    nurseToOrMergedModel.setAssignedPerPatient(jsonObject.getBoolean("AssignedPerPatient"));
                    String roleid = jsonObject.getString("RoleCategory");
                    List<String> list = new ArrayList<>();
                    if (roleid.contains(",")) {
                        list = new ArrayList<String>(Arrays.asList(roleid.split(",")));
                    } else {
                        list.add(jsonObject.getString("RoleCategory"));
                    }
                    nurseToOrMergedModel.setRoleCategory(list);

                    nurseToOrMergedModel.setProviderId("");
                    nurseToOrMergedModel.setProviderName("Staff not selected");
                    nurseToOrMergedModel.sethDeptId("");
                    nurseToOrMergedModel.sethDeptName("");
                    nurseToOrMergedModel.sethDeptNameToShow("Staff not selected");
                    nurseToOrMergedModel.setORId("");
                    nurseToOrMergedModel.setEdited(false);

                    assignmentHospitalModels.add(nurseToOrMergedModel);
                }


            }
        } else if (id == ThreadTaskIds.GET_NURSES_TO_OR_ASSIGNMENTS) {
            if (response.getJSONObject("Result").getInt("Code") == 0) {
                nurseToORAssignmentModels = new ArrayList<>();
                JSONArray messagesArray = response.getJSONArray("Data");
                for (int i = 0; i < messagesArray.length(); i++) {
                    JSONObject jsonObject = messagesArray.getJSONObject(i);
                    NurseToORAssignmentModel nurseToORAssignmentModel = new NurseToORAssignmentModel();
                    nurseToORAssignmentModel.setProviderId(jsonObject.getString("providerId"));
                    nurseToORAssignmentModel.setProviderName(jsonObject.getString("providerName"));
                    nurseToORAssignmentModel.sethDeptId(jsonObject.getString("HdeptId"));
                    nurseToORAssignmentModel.sethDeptName(jsonObject.getString("HdeptName"));
                    nurseToORAssignmentModel.sethDeptNameToShow(jsonObject.getString("providerName"));
                    nurseToORAssignmentModel.setORId(jsonObject.getString("ORId"));
                    nurseToORAssignmentModel.setEdited(false);
                    nurseToORAssignmentModels.add(nurseToORAssignmentModel);
                }
            }
        } else if (id == ThreadTaskIds.SAVE_ASSIGN_NURSE_TO_OR) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
//                        if (response.getJSONObject("Result").getInt("Code") == 0) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(AssignNursestoORActivityCMN
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

    public ArrayList<NurseToORAssignmentModel> getORAssignments(String ORId) {
        List<NurseToORAssignmentModel> orAssignmentList = new ArrayList<>();
        for (int i = 0; i < nurseToORAssignmentModels.size(); i++) {
            if (nurseToORAssignmentModels.get(i).getORId().equalsIgnoreCase(ORId)) {
                orAssignmentList.add(nurseToORAssignmentModels.get(i));
            }
        }
        return (ArrayList<NurseToORAssignmentModel>) orAssignmentList;
    }

    @Override
    public void onBackPressed() {
        if (progressBar.getVisibility() == View.VISIBLE) {

        } else {
            super.onBackPressed();
        }
    }
}
