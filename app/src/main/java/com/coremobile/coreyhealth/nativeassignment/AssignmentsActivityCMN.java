package com.coremobile.coreyhealth.nativeassignment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coremobile.coreyhealth.CoreyDBHelper;
import com.coremobile.coreyhealth.DatabaseProvider;
import com.coremobile.coreyhealth.MsgDataCache;
import com.coremobile.coreyhealth.MyApplication;
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
import com.coremobile.coreyhealth.newui.MsgScheduleTabActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AssignmentsActivityCMN extends CMN_AppBaseActivity implements NetworkCallBack {
    private static String TAG = "Corey_AssignmentsActivityCMN";
    MyApplication application;
    int mMsgId;
    CoreyDBHelper mCoreyDBHelper;
    Boolean isPortrait;
    private SharedPreferences mCurrentUserPref;
    public static final String CURRENT_USER = "CurrentUser";
    private String   user_category;

    TextView textView;
    ListView assignmentListView;
    LinearLayout progressBar;
    List<AssignmentsTypeModel> assignmentsTypeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignments);
        mCoreyDBHelper = new CoreyDBHelper(this);


        assignmentListView = (ListView) findViewById(R.id.assignmetnsListView);

        progressBar = (LinearLayout) findViewById(R.id.progressbar);

        mCurrentUserPref = getSharedPreferences(CURRENT_USER, 0);


        application = (MyApplication) getApplication();
        user_category = mCurrentUserPref.getString("user_category", null);
        textView = (TextView) findViewById(R.id.nodata);
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            isPortrait = false;

        }
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            isPortrait = true;
        }


        mMsgId = MsgScheduleTabActivity.mMessageId;
        MsgDataCache mMsgDataCache = new MsgDataCache(AssignmentsActivityCMN.this);
        mMsgDataCache.populateData(mMsgId, DatabaseProvider.MSG_TYPE_INSTANT_GRATIFICATION);




        NetworkAsyncTask networkAsyncTask = NetworkAsyncTask.getInstance();
        try {
            networkAsyncTask.startNetworkCall(ThreadTaskIds.GET_ASSIGNMENT_TYPES, AssignmentsActivityCMN.this);
        } catch (NetworkException e) {
            e.getMessage();
        }

        getActionBar().setHomeButtonEnabled(true);

        getActionBar().setTitle(
                "Assignment");

        getActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    public void beforeNetworkCall(int taskId) {
        if (progressBar.getVisibility() == View.GONE) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public Object afterNetworkCall(int taskId, Object o) {
        if (progressBar.getVisibility() == View.VISIBLE) {
            progressBar.setVisibility(View.GONE);
        }
        return null;
    }


    @Override
    public Object onNetworkCall(int taskId, Object o) throws JsonParsingException, JsonParsingException {
        if (taskId == ThreadTaskIds.GET_ASSIGNMENT_TYPES) {
            String url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                    + "GetAssignmentsOptions.aspx?token="+ CMN_Preferences.getUserToken(AssignmentsActivityCMN.this);

            NetworkTools networkTools = NetworkTools.getInstance();
            if (networkTools.checkNetworkConnection(AssignmentsActivityCMN.this) && url.length() > 0) {
                JSONObject response = networkTools.getJsonData(AssignmentsActivityCMN.this, url);
                try {
                    if (response != null) {
                        parseJsonData(response, ThreadTaskIds.GET_ASSIGNMENT_TYPES);
                    }
                } catch (JSONException e) {
                    e.getMessage();
                }
            } else {
                Toast.makeText(AssignmentsActivityCMN.this, CMN_ErrorMessages.getInstance().getValue(12), Toast.LENGTH_SHORT).show();
            }
        }


        return null;
    }

    private void parseJsonData(JSONObject response, int getPatientByProvider) throws JSONException {
        if (response.getJSONObject("Result").getInt("Code") == 0) {
            JSONArray messagesArray = response.getJSONArray("Data");
            assignmentsTypeList = new ArrayList<>();
            for (int i = 0; i < messagesArray.length(); i++) {
                AssignmentsTypeModel assignmentsTypeModel = new AssignmentsTypeModel();
                JSONObject jsonObject = messagesArray.getJSONObject(i);
                assignmentsTypeModel.setId(jsonObject.getInt("Id"));
                assignmentsTypeModel.setPosition(jsonObject.getInt("Position"));
                assignmentsTypeModel.setName(jsonObject.getString("DisplayName"));
                assignmentsTypeModel.setAssignmentsOptions(jsonObject.getString("AssignmentOption"));
                assignmentsTypeList.add(assignmentsTypeModel);
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Collections.sort(assignmentsTypeList, new Comparator<AssignmentsTypeModel>() {
                        @Override
                        public int compare(AssignmentsTypeModel o1, AssignmentsTypeModel o2) {
                            return String.valueOf(o1.getPosition()).compareTo(String.valueOf(o2.getPosition()));
                        }
                    });

                    final AssignmentTypesAdapter assignmentTypesAdapter = new AssignmentTypesAdapter(AssignmentsActivityCMN.this, assignmentsTypeList);
                    assignmentListView.setAdapter(assignmentTypesAdapter);

                    assignmentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            AssignmentsTypeModel model = (AssignmentsTypeModel) assignmentTypesAdapter.getItem(position);
                            if (model.getId() == 0 || model.getName().equalsIgnoreCase("Self Assignments")) {
                                startActivity(new Intent(AssignmentsActivityCMN.this, ChangeAssignmentsbyStaffActivityCMN.class).putExtra("title", model).putExtra("SelfAssignment", true));
                            } else if (model.getId() == 1 || model.getName().equalsIgnoreCase("Assign nurses to an OR")) {
                                startActivity(new Intent(AssignmentsActivityCMN.this, AssignNursestoORActivityCMN.class).putExtra("title", model));
                            } else if (model.getId() == 2 || model.getName().equalsIgnoreCase("Assign Patient(s) to OR")) {
                                startActivity(new Intent(AssignmentsActivityCMN.this, AssignPatienttoORActivityCMN.class).putExtra("title", model));
                            } else if (model.getId() == 3 || model.getName().equalsIgnoreCase("Facility wide Assignments")) {
                                startActivity(new Intent(AssignmentsActivityCMN.this, FacilitywiseAssignmentActivityCMN.class).putExtra("title", model));
                            } else if (model.getId() == 4 || model.getName().equalsIgnoreCase("Change Assignments by Patient")) {
                                startActivity(new Intent(AssignmentsActivityCMN.this, ChangeAssignmentsbyPatientActivityCMN.class).putExtra("title", model));
                            } else if (model.getId() == 5 || model.getName().equalsIgnoreCase("Change Assignments by Staff Member")) {
                                startActivity(new Intent(AssignmentsActivityCMN.this, ChangeAssignmentsbyStaffActivityCMN.class).putExtra("title", model));
                            } else if (model.getId() == 6 || model.getName().equalsIgnoreCase("Assign Preop Nurse(s)")) {
                                startActivity(new Intent(AssignmentsActivityCMN.this, AssignPreOpNursesActivityCMN.class).putExtra("title", model));
//                                Toast.makeText(AssignmentsActivityCMN.this, "Work in Progress", Toast.LENGTH_SHORT).show();
                            } else if (model.getId() == 7 || model.getName().equalsIgnoreCase("Assign PACU Nurse(s)")) {
//                                Toast.makeText(AssignmentsActivityCMN.this, "Work in Progress", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(AssignmentsActivityCMN.this, AssignPACUNursesActivityCMN.class).putExtra("title", model));
                            } else if (model.getId() == 8 || model.getName().equalsIgnoreCase("Set Auto Assignments")) {
                                startActivity(new Intent(AssignmentsActivityCMN.this, AutoAssignmentsActivityCMN.class).putExtra("title", model));
                            } else if (model.getId() == 9 || model.getName().equalsIgnoreCase("Assign speciality to OR")) {
                                startActivity(new Intent(AssignmentsActivityCMN.this, AssignSpecialitytoORActivityCMN.class).putExtra("title", model));
                            } else if (model.getId() == 10 || model.getName().equalsIgnoreCase("Assign CRNAs to OR")) {
                                startActivity(new Intent(AssignmentsActivityCMN.this, AssignCRNAtoORActivityCMN.class).putExtra("title", model));
                            }
                        }
                    });
                }
            });
        }

    }

    @Override
    public Object onNetworkError(int taskId, NetworkException o) {
        return null;
    }
}
