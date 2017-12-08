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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

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

public class AssignCRNAtoORActivityCMN extends CMN_AppBaseActivity implements NetworkCallBack {

    private SharedPreferences mCurrentUserPref;
    public static final String CURRENT_USER = "CurrentUser";

    String user_category, context_name;

    LinearLayout progressBar;
    ArrayList<AssignmentHospitalsModel> assignmentRolesModels;
    ArrayList<String> assignmentRoles;
    ArrayAdapter _rolesNamesAdapter;
    ArrayAdapter _orNamesAdapter;
    Spinner orUsersSpinner, roleSpinner;
    ArrayList<AssignmentPatientModel> patientList = new ArrayList<>();
    Button assign, unAssign;
    boolean isAssign;
    ToggleButton replace;
    AssignmentsTypeModel assignmentsTypeModel;
    boolean isSelfAssignment = false;
    LinearLayout staffLayout, replaceAssignmentLayout;
    ArrayList<OrUsersModel> orUsersModels;
    ArrayList<CRNAtoORPreviousAssignmentModel> previousAssignments;
    ArrayList<String> orUserNames;
    CRNAToOrPreviousAssignmentAdapter crnaToOrPreviousAssignmentAdapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assigncrnatoor);

        mCurrentUserPref = getSharedPreferences(CURRENT_USER, 0);
        user_category = mCurrentUserPref.getString("user_category", null);
        context_name = mCurrentUserPref.getString("context_name", null);

        assign = (Button) findViewById(R.id.buttonAssign);
        unAssign = (Button) findViewById(R.id.buttonUnAssign);
        replace = (ToggleButton) findViewById(R.id.isReplaceToggle);
        staffLayout = (LinearLayout) findViewById(R.id.staffLayout);
        replaceAssignmentLayout = (LinearLayout) findViewById(R.id.replaceLayout);
        listView = (ListView) findViewById(R.id.listView);

        getActionBar().setHomeButtonEnabled(true);
        if (getIntent() != null) {

            assignmentsTypeModel = (AssignmentsTypeModel) getIntent().getSerializableExtra("title");
            getActionBar().setTitle(
                    "" + assignmentsTypeModel.getName());
        } else {
            getActionBar().setTitle(
                    "Assignment");
        }
        getActionBar().setDisplayHomeAsUpEnabled(true);

        orUsersSpinner = (Spinner) findViewById(R.id.orUsers);
        roleSpinner = (Spinner) findViewById(R.id.selectRole);
        progressBar = (LinearLayout) findViewById(R.id.progressbar);


        NetworkAsyncTask networkAsyncTask = NetworkAsyncTask.getInstance();
        try {
            networkAsyncTask.startNetworkCall(ThreadTaskIds.GET_OR_USERS, AssignCRNAtoORActivityCMN
                    .this);
        } catch (NetworkException e) {
            e.getMessage();
        }


        NetworkAsyncTask networkAsyncTask1 = NetworkAsyncTask.getInstance();
        try {
            networkAsyncTask1.startNetworkCall(ThreadTaskIds.GET_HOSPITAL_DEPARTMENTS, AssignCRNAtoORActivityCMN
                    .this);
        } catch (NetworkException e) {
            e.getMessage();
        }

        NetworkAsyncTask networkAsyncTask2 = NetworkAsyncTask.getInstance();
        try {
            networkAsyncTask2.startNetworkCall(ThreadTaskIds.GET_PREVIOUS_CRNA_TO_OR_ASSIGNMENT, AssignCRNAtoORActivityCMN
                    .this);
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
        item.setTitle("Assign");
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

                if (orUsersSpinner.getSelectedItemPosition() == 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AssignCRNAtoORActivityCMN.this, CMN_ErrorMessages.getInstance().getValue(148), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if (roleSpinner.getSelectedItemPosition() == 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AssignCRNAtoORActivityCMN.this, CMN_ErrorMessages.getInstance().getValue(149), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if (crnaToOrPreviousAssignmentAdapter != null && crnaToOrPreviousAssignmentAdapter.getData().size() == 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AssignCRNAtoORActivityCMN.this, CMN_ErrorMessages.getInstance().getValue(150), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    NetworkAsyncTask networkAsyncTask3 = NetworkAsyncTask.getInstance();
                    try {
                        networkAsyncTask3.startNetworkCall(ThreadTaskIds.SAVE_ASSIGN_NURSE_TO_OR, AssignCRNAtoORActivityCMN.this);
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
        if (progressBar.getVisibility() == View.VISIBLE && taskId == ThreadTaskIds.GET_PREVIOUS_CRNA_TO_OR_ASSIGNMENT) {
            progressBar.setVisibility(View.GONE);
        }
        return null;
    }

    @Override
    public Object onNetworkCall(int taskId, Object o) throws
            JsonParsingException, JsonParsingException {
        if (taskId == ThreadTaskIds.GET_HOSPITAL_DEPARTMENTS) {
            String url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                    + "GetHospitalDepartments.aspx?token=" + CMN_Preferences.getUserToken(AssignCRNAtoORActivityCMN.this) + "&assignmentoption=" + assignmentsTypeModel.getAssignmentsOptions();

            NetworkTools networkTools = NetworkTools.getInstance();
            if (networkTools.checkNetworkConnection(AssignCRNAtoORActivityCMN
                    .this) && url.length() > 0) {
                JSONObject response = networkTools.getJsonData(AssignCRNAtoORActivityCMN
                        .this, url);
                try {
                    if (response != null) {
                        parseJsonData(response, ThreadTaskIds.GET_HOSPITAL_DEPARTMENTS);
                    }
                } catch (JSONException e) {
                    e.getMessage();
                }
            } else {
                Toast.makeText(AssignCRNAtoORActivityCMN
                        .this, CMN_ErrorMessages.getInstance().getValue(12), Toast.LENGTH_SHORT).show();
            }
        } else if (taskId == ThreadTaskIds.GET_OR_USERS) {
            String url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                    + "GetORUsers.aspx?token=" + CMN_Preferences.getUserToken(AssignCRNAtoORActivityCMN.this) + "&assignmentoption=" + assignmentsTypeModel.getAssignmentsOptions();

            NetworkTools networkTools = NetworkTools.getInstance();
            if (networkTools.checkNetworkConnection(AssignCRNAtoORActivityCMN.this) && url.length() > 0) {
                JSONObject response = networkTools.getJsonData(AssignCRNAtoORActivityCMN.this, url);
                try {
                    if (response != null) {
                        parseJsonData(response, ThreadTaskIds.GET_OR_USERS);
                    }
                } catch (JSONException e) {
                    e.getMessage();
                }
            } else {
                Toast.makeText(AssignCRNAtoORActivityCMN.this, CMN_ErrorMessages.getInstance().getValue(12), Toast.LENGTH_SHORT).show();
            }

        } else if (taskId == ThreadTaskIds.GET_PREVIOUS_CRNA_TO_OR_ASSIGNMENT) {
            String url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                    + "GetMultipleProvORAssignments.aspx?token=" + CMN_Preferences.getUserToken(AssignCRNAtoORActivityCMN.this);


            NetworkTools networkTools = NetworkTools.getInstance();
            if (networkTools.checkNetworkConnection(AssignCRNAtoORActivityCMN.this) && url.length() > 0) {
                JSONObject response = networkTools.getJsonData(AssignCRNAtoORActivityCMN.this, url);
                try {
                    if (response != null) {
                        parseJsonData(response, ThreadTaskIds.GET_PREVIOUS_CRNA_TO_OR_ASSIGNMENT);
                    }
                } catch (JSONException e) {
                    e.getMessage();
                }
            } else {
                Toast.makeText(AssignCRNAtoORActivityCMN.this, CMN_ErrorMessages.getInstance().getValue(12), Toast.LENGTH_SHORT).show();
            }
        }
        if (taskId == ThreadTaskIds.SAVE_ASSIGN_NURSE_TO_OR) {
            String url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                    + "AssignNurseToOR.aspx?token=" + CMN_Preferences.getUserToken(AssignCRNAtoORActivityCMN.this);

            NetworkTools networkTools = NetworkTools.getInstance();
            if (networkTools.checkNetworkConnection(AssignCRNAtoORActivityCMN.this) && url.length() > 0) {
                JSONObject response = networkTools.postJsonArrayData(AssignCRNAtoORActivityCMN.this, url, generateJson());
                try {
                    if (response != null) {
                        parseJsonData(response, ThreadTaskIds.SAVE_ASSIGN_NURSE_TO_OR);
                    }
                } catch (JSONException e) {
                    e.getMessage();
                }
            } else {
                Toast.makeText(AssignCRNAtoORActivityCMN.this, CMN_ErrorMessages.getInstance().getValue(12), Toast.LENGTH_SHORT).show();
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

                AssignmentHospitalsModel dmymodel = new AssignmentHospitalsModel();
                dmymodel.setHdeptId("");
                dmymodel.setHdeptName("");
                dmymodel.setAssignedPerOR(false);
                dmymodel.setAssignedPerOrg(false);
                dmymodel.setAssignedPerPatient(false);
                dmymodel.setRoleCategory(new ArrayList<String>());
                assignmentRoles.add("");
                assignmentRolesModels.add(dmymodel);


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
                        _rolesNamesAdapter = new ArrayAdapter<String>(AssignCRNAtoORActivityCMN
                                .this,
                                android.R.layout.simple_spinner_item, assignmentRoles);
                        _rolesNamesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        roleSpinner.setAdapter(_rolesNamesAdapter);

                        roleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if (position > 0) {

                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }
                });

            }
        } else if (id == ThreadTaskIds.GET_OR_USERS) {
            if (response.getJSONObject("Result").getInt("Code") == 0) {
                orUsersModels = new ArrayList<>();
                orUserNames = new ArrayList<>();
                OrUsersModel dummymodel = new OrUsersModel();
                dummymodel.setName("");
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        _orNamesAdapter = new ArrayAdapter<String>(AssignCRNAtoORActivityCMN
                                .this,
                                android.R.layout.simple_spinner_item, orUserNames);
                        _orNamesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        orUsersSpinner.setAdapter(_orNamesAdapter);

                        orUsersSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if (position > 0) {

                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }
                });
            }
        } else if (id == ThreadTaskIds.GET_PREVIOUS_CRNA_TO_OR_ASSIGNMENT) {
            if (response.getJSONObject("Result").getInt("Code") == 0) {
                previousAssignments = new ArrayList<>();
                JSONArray array = response.getJSONArray("Data");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject jsonObject = array.getJSONObject(i);
                    CRNAtoORPreviousAssignmentModel previousModel = new CRNAtoORPreviousAssignmentModel();
                    previousModel.setOrId(jsonObject.getString("ORId"));
                    previousModel.setOrName(jsonObject.getString("ORName"));
                    previousModel.sethDeptName(jsonObject.getString("HdeptName"));
                    previousModel.sethDeptId(jsonObject.getString("HdeptId"));
                    previousModel.setProviderId(jsonObject.getString("providerId"));
                    previousModel.setProviderName(jsonObject.getString("providerName"));
                    previousAssignments.add(previousModel);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        crnaToOrPreviousAssignmentAdapter = new CRNAToOrPreviousAssignmentAdapter(AssignCRNAtoORActivityCMN.this, previousAssignments);
                        listView.setAdapter(crnaToOrPreviousAssignmentAdapter);
                    }
                });
            }
        } else if (id == ThreadTaskIds.SAVE_ASSIGN_NURSE_TO_OR) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
//                        if (response.getJSONObject("Result").getInt("Code") == 0) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(AssignCRNAtoORActivityCMN
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

    private JSONArray generateJson() {

        String providers = "";
        if (crnaToOrPreviousAssignmentAdapter != null) {
            for (int i = 0; i < crnaToOrPreviousAssignmentAdapter.getData().size(); i++) {
                if (i == 0) {
                    providers = providers + "" + crnaToOrPreviousAssignmentAdapter.getData().get(i).getProviderId();
                } else {
                    providers = providers + "," + crnaToOrPreviousAssignmentAdapter.getData().get(i).getProviderId();
                }
            }
        }

        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("HdeptId", "" + assignmentRolesModels.get(roleSpinner.getSelectedItemPosition()).getHdeptId());
            jsonObject.put("ORId", "" + orUsersModels.get(orUsersSpinner.getSelectedItemPosition()).getId());
            jsonObject.put("providerId", "" + providers);
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
