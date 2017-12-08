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
import java.util.HashMap;

/**
 * @author Nitij Katiyar
 */

public class AssignSpecialitytoORActivityCMN extends CMN_AppBaseActivity implements NetworkCallBack {

    private SharedPreferences mCurrentUserPref;
    public static final String CURRENT_USER = "CurrentUser";
    private String organizationName;
    String password, user_category, context_name;
    String userName;
    AssignmentsTypeModel assignmentsTypeModel;
    ArrayList<OrUsersModel> orUsersModels;
    ArrayList<String> orUserNames;
    ArrayList<SpecialityModel> specialityModels;
    ArrayList<SpecialityOrAssignmentModel> specialityOrAssignmentModels;
    HashMap<String, String> specialities;
    LinearLayout progressBar;
    ListView listView;
    AssignmentSpecialityToOrAdapter assignmentSpecialityToOrAdapter;
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
            networkAsyncTask.startNetworkCall(ThreadTaskIds.GET_SPECIALITY, AssignSpecialitytoORActivityCMN.this);
        } catch (NetworkException e) {
            e.getMessage();
        }

        NetworkAsyncTask networkAsyncTask2 = NetworkAsyncTask.getInstance();
        try {
            networkAsyncTask2.startNetworkCall(ThreadTaskIds.GET_OR_USERS, AssignSpecialitytoORActivityCMN.this);
        } catch (NetworkException e) {
            e.getMessage();
        }

        NetworkAsyncTask networkAsyncTask3 = NetworkAsyncTask.getInstance();
        try {
            networkAsyncTask3.startNetworkCall(ThreadTaskIds.GET_SPECIALITY_OR_ASSIGNMENT, AssignSpecialitytoORActivityCMN.this);
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
                            Toast.makeText(AssignSpecialitytoORActivityCMN.this, CMN_ErrorMessages.getInstance().getValue(154), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {

                    NetworkAsyncTask networkAsyncTask3 = NetworkAsyncTask.getInstance();
                    try {
                        networkAsyncTask3.startNetworkCall(ThreadTaskIds.SAVE_SPECIALITY_OR_ASSIGNMENT, AssignSpecialitytoORActivityCMN.this);
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
        for (int i = 0; i < assignmentSpecialityToOrAdapter.getCount(); i++) {
            View view = assignmentSpecialityToOrAdapter.getView(i, null, null);
            SpecialityOrAssignmentModel assignmentModel = (SpecialityOrAssignmentModel) assignmentSpecialityToOrAdapter.getItem(i);
            Spinner spinner = (Spinner) view.findViewById(R.id.selectRole);
            if (spinner.getSelectedItemPosition() != 0) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("ORid", "" + orUsersModels.get(spinner.getSelectedItemPosition()).getId());
                    jsonObject.put("specialityid", "" + assignmentModel.getSpecialityId());
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
        if (progressBar.getVisibility() == View.VISIBLE && taskId == ThreadTaskIds.GET_SPECIALITY_OR_ASSIGNMENT) {
            progressBar.setVisibility(View.GONE);
        }
        return null;
    }


    @Override
    public Object onNetworkCall(int taskId, Object o) throws JsonParsingException, JsonParsingException {
        if (taskId == ThreadTaskIds.GET_OR_USERS) {
            String url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                    + "GetORUsers.aspx?token="+ CMN_Preferences.getUserToken(AssignSpecialitytoORActivityCMN.this) + "&assignmentoption=" + assignmentsTypeModel.getAssignmentsOptions();

            NetworkTools networkTools = NetworkTools.getInstance();
            if (networkTools.checkNetworkConnection(AssignSpecialitytoORActivityCMN.this) && url.length() > 0) {
                JSONObject response = networkTools.getJsonData(AssignSpecialitytoORActivityCMN.this, url);
                try {
                    if (response != null) {
                        parseJsonData(response, ThreadTaskIds.GET_OR_USERS);
                    }
                } catch (JSONException e) {
                    e.getMessage();
                }
            } else {
                Toast.makeText(AssignSpecialitytoORActivityCMN.this, CMN_ErrorMessages.getInstance().getValue(12) , Toast.LENGTH_SHORT).show();
            }
        }
        if (taskId == ThreadTaskIds.GET_SPECIALITY) {
            String url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                    + "GetSpecialities.aspx?token="+ CMN_Preferences.getUserToken(AssignSpecialitytoORActivityCMN.this);

            NetworkTools networkTools = NetworkTools.getInstance();
            if (networkTools.checkNetworkConnection(AssignSpecialitytoORActivityCMN.this) && url.length() > 0) {
                JSONObject response = networkTools.getJsonData(AssignSpecialitytoORActivityCMN.this, url);
                try {
                    if (response != null) {
                        parseJsonData(response, ThreadTaskIds.GET_SPECIALITY);
                    }
                } catch (JSONException e) {
                    e.getMessage();
                }
            } else {
                Toast.makeText(AssignSpecialitytoORActivityCMN.this, CMN_ErrorMessages.getInstance().getValue(12) , Toast.LENGTH_SHORT).show();
            }
        }
        if (taskId == ThreadTaskIds.GET_SPECIALITY_OR_ASSIGNMENT) {
            String url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                    + "GetSpecialityORAssignments.aspx?token="+ CMN_Preferences.getUserToken(AssignSpecialitytoORActivityCMN.this);

            NetworkTools networkTools = NetworkTools.getInstance();
            if (networkTools.checkNetworkConnection(AssignSpecialitytoORActivityCMN.this) && url.length() > 0) {
                JSONObject response = networkTools.getJsonData(AssignSpecialitytoORActivityCMN.this, url);
                try {
                    if (response != null) {
                        parseJsonData(response, ThreadTaskIds.GET_SPECIALITY_OR_ASSIGNMENT);
                    }
                } catch (JSONException e) {
                    e.getMessage();
                }
            } else {
                Toast.makeText(AssignSpecialitytoORActivityCMN.this, CMN_ErrorMessages.getInstance().getValue(12) , Toast.LENGTH_SHORT).show();
            }
        }
        if (taskId == ThreadTaskIds.SAVE_SPECIALITY_OR_ASSIGNMENT) {
            String url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                    + "AssignSpecialityToOR.aspx?token="+ CMN_Preferences.getUserToken(AssignSpecialitytoORActivityCMN.this);

            NetworkTools networkTools = NetworkTools.getInstance();
            if (networkTools.checkNetworkConnection(AssignSpecialitytoORActivityCMN.this) && url.length() > 0) {
                JSONObject response = networkTools.postJsonArrayData(AssignSpecialitytoORActivityCMN.this, url, jsonArray);
                try {
                    if (response != null) {
                        parseJsonData(response, ThreadTaskIds.SAVE_SPECIALITY_OR_ASSIGNMENT);
                    }
                } catch (JSONException e) {
                    e.getMessage();
                }
            } else {
                Toast.makeText(AssignSpecialitytoORActivityCMN.this, CMN_ErrorMessages.getInstance().getValue(12) , Toast.LENGTH_SHORT).show();
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
        } else if (id == ThreadTaskIds.GET_SPECIALITY) {
            if (response.getJSONObject("Result").getInt("Code") == 0) {
                specialityModels = new ArrayList<>();
                specialities = new HashMap<>();
                JSONArray messagesArray = response.getJSONArray("Data");
                for (int i = 0; i < messagesArray.length(); i++) {
                    JSONObject jsonObject = messagesArray.getJSONObject(i);
                    SpecialityModel specialityModel = new SpecialityModel();
                    specialityModel.setSpecialityId(jsonObject.getString("SpecialityId"));
                    specialityModel.setSpecialityName(jsonObject.getString("SpecialityName"));
                    specialities.put(jsonObject.getString("SpecialityId"), jsonObject.getString("SpecialityName"));
                    specialityModels.add(specialityModel);
                }


            }
        } else if (id == ThreadTaskIds.GET_SPECIALITY_OR_ASSIGNMENT) {
            specialityOrAssignmentModels = new ArrayList<>();
            if (response.getJSONObject("Result").getInt("Code") == 0) {

                JSONArray messagesArray = response.getJSONArray("Data");
                for (int i = 0; i < messagesArray.length(); i++) {
                    JSONObject jsonObject = messagesArray.getJSONObject(i);
                    SpecialityOrAssignmentModel orAssignmentModel = new SpecialityOrAssignmentModel();
                    orAssignmentModel.setSpecialityId(jsonObject.getString("SpecialityId"));
                    orAssignmentModel.setORId(jsonObject.getString("ORId"));
                    orAssignmentModel.setORName(jsonObject.getString("ORName"));
                    if (specialities.get(jsonObject.getString("SpecialityId")) != null) {
                        orAssignmentModel.setSpecialityName(specialities.get(jsonObject.getString("SpecialityId")));
                    }
                    specialityOrAssignmentModels.add(orAssignmentModel);
                }
            }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        assignmentSpecialityToOrAdapter = new AssignmentSpecialityToOrAdapter(AssignSpecialitytoORActivityCMN.this, specialityOrAssignmentModels, orUsersModels);
                        listView.setAdapter(assignmentSpecialityToOrAdapter);
                    }
                });

        } else if (id == ThreadTaskIds.SAVE_SPECIALITY_OR_ASSIGNMENT) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
//                        if (response.getJSONObject("Result").getInt("Code") == 0) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(AssignSpecialitytoORActivityCMN
                                .this);
                        builder1.setTitle("Assignment");
                        builder1.setMessage(response.getJSONObject("Result").getString("Message"));
                        builder1.setCancelable(true);

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