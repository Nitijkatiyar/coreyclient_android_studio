package com.coremobile.coreyhealth.genericviewandsurvey;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.coremobile.coreyhealth.ContextData;
import com.coremobile.coreyhealth.R;
import com.coremobile.coreyhealth.Utils;
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
import com.coremobile.coreyhealth.scheduling.schedulingcalender.CalenderActivity;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class CMN_GenericViewActivity extends CMN_AppBaseActivity implements NetworkCallBack {

    LinearLayout progressBar;
    private SharedPreferences mCurrentUserPref;
    public static final String CURRENT_USER = "CurrentUser";
    String organizationName, password, userName, context;
    LinearLayout mainLayout;
    public static HashMap<String, CMN_ViewFieldModel> viewMap = new HashMap<>();
    public static HashMap<String, CMN_ViewFieldModel> viewTempMap = new HashMap<>();
    private HashMap<CMN_GenericFieldModel, View> mandatoryMap = new HashMap<>();
    JSONArray jsonArray;
    JSONObject jsonObjectRequest;
    CMN_GenericModel surveyModel;
    boolean shouldupdate = false;
    TextView noData, dateTimePicker, dateTimePicker1, datePicker, datePicker1;
    CMN_GenericFieldModel dateTimeModel, dateTimeYearModel;
    Intent intent;
    ContextData contextData;
    String viewForm = "";
    boolean isChangingPassword = false;
    int position;
    ListView listView;
    ScrollView scrollView;
    private boolean shouldshowlist = false;
    private boolean shouldshowbutton = true;
    Spinner _speciality, _procedure, _provider;
    List<CMN_GenericListValuesModel> _speciality_listValuesData, _procedure_listValuesData, _provider_listValuesData;
    public static HashMap<String, String> SpinnerIdsHashMap;
    public static HashMap<String, Boolean> loginIdCheckboxes = new HashMap<>();
    CheckBox nameAsLoginID, mobileAsLoginID, emailAsLoginID;
    boolean nameAsLoginBool, mobileAsLoginBool, emailAsLoginBool;
    JSONObject scheduleJSON;
    String prefURL = null;
    boolean isSchedule = false;
    boolean isForceSave = false;
    boolean isScheduleView = false;
    String scheduleId;
    TextView Password, confirmPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewsurvey_activity);
        SpinnerIdsHashMap = new HashMap<>();
        progressBar = (LinearLayout) findViewById(R.id.progressbar);

        mainLayout = (LinearLayout) findViewById(R.id.layoutRoot);

        noData = (TextView) findViewById(R.id.nodataTextview);
        noData.setVisibility(View.GONE);

        listView = (ListView) findViewById(R.id.listview);
        scrollView = (ScrollView) findViewById(R.id.scroll);


        getActionBar().setHomeButtonEnabled(true);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        intent = getIntent();
        if (intent != null && intent.hasExtra("data")) {
            contextData = (ContextData) intent.getSerializableExtra("data");
            getActionBar().setTitle(
                    "" + contextData.mDisplayText);
            if (contextData.mDisplayText.contains("Invite Patient")) {
                viewForm = "intake";
            } else if (contextData.mDisplayText.contains("Invite Staff")) {
                viewForm = "inviteprovider";
            } else if (contextData.mDisplayText.contains("My Profile")) {
                viewForm = "settings";
            } else if (contextData.mDisplayText.contains("Security Setting")) {
                viewForm = "SecuritySettings";
                isChangingPassword = true;
            }
        }
        if (intent != null && intent.hasExtra("position")) {
            position = intent.getIntExtra("position", 0);
            shouldshowlist = true;
        } else {
            position = 0;
        }

        if (intent != null && intent.hasExtra("scheduling")) {
            viewForm = "RequestSChedule";
            isSchedule = true;
            if (intent.hasExtra("isViewing")) {
                isScheduleView = intent.getBooleanExtra("isViewing", false);
            }
            try {
                scheduleJSON = new JSONObject(intent.getStringExtra("scheduling"));
                scheduleId = scheduleJSON.optString("ScheduleId");
            } catch (JSONException e) {
                scheduleJSON = new JSONObject();
                e.printStackTrace();
            }
        }
        if (intent != null && intent.hasExtra("prefURL")) {
            prefURL = intent.getStringExtra("prefURL");
        }

        mCurrentUserPref = getSharedPreferences(CURRENT_USER, 0);

        context = mCurrentUserPref.getString("context", null);

    }

    @Override
    protected void onResume() {
        super.onResume();
        NetworkAsyncTask networkAsyncTask = NetworkAsyncTask.getInstance();
        try {
            networkAsyncTask.startNetworkCall(ThreadTaskIds.GET_SURVEY, CMN_GenericViewActivity.this);
        } catch (NetworkException e) {
            e.getMessage();
        }
    }

    private JSONObject createResponseJson() {

        Iterator it = viewMap.entrySet().iterator();
        jsonArray = new JSONArray();
        jsonObjectRequest = new JSONObject();

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();

            try {
                final CMN_ViewFieldModel model = (CMN_ViewFieldModel) pair.getValue();
                if (model.getView() instanceof EditText) {
                    JSONObject jsonObject = new JSONObject();
                    EditText text = (EditText) model.getView();
                    if (!text.getText().toString().trim().isEmpty()) {
//                    if (model.getInitialValue().isEmpty() || !model.getInitialValue().equalsIgnoreCase(text.getText().toString().trim())) {
                        if (model.getSurveyFieldModel().isMandatory() && text.getText().toString().length() == 0) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(CMN_GenericViewActivity.this, CMN_ErrorMessages.getInstance().getValue(59) + " : " + model.getSurveyFieldModel().getDisplayText(), Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            });
                        }
                        jsonObject.put(CMN_JsonConstants.SURVEY_FIELDID, "" + pair.getKey());
                        if (text.getTag().toString().equalsIgnoreCase("Patient Name")) {
                            Log.d("PatientName", "PatientName");
                            String name = "";
                            String lastname = "", firstname = "", middlename = "";
                            String inputtext = text.getText().toString().replaceAll(", ", ",").replaceAll("; ", ";");

                            if (inputtext.contains(",")) {
                                lastname = "" + inputtext.split(",")[0].trim();
                                if (lastname.length() >= 3) {
                                    lastname = lastname.substring(0, 3);
                                }
                                List<String> items = Arrays.asList(inputtext.split(",")[1].split("\\s* \\s*"));
                                firstname = "" + items.get(0).charAt(0);
                                for (int k = 1; k < items.size(); k++) {
                                    String mName = items.get(k);
                                    if (mName.length() >= 3) {
                                        mName = mName.substring(0, 3);
                                    }
                                    middlename = middlename + " " + StringUtils.capitalize(mName);
                                }
                            } else if (inputtext.contains(";")) {
                                lastname = "" + inputtext.split(";")[0].trim();
                                if (lastname.length() >= 3) {
                                    lastname = lastname.substring(0, 3);
                                }
                                List<String> items = Arrays.asList(inputtext.split(";")[1].split("\\s* \\s*"));
                                firstname = "" + items.get(0).charAt(0);
                                for (int k = 1; k < items.size(); k++) {
                                    String mName = items.get(k);
                                    if (mName.length() >= 3) {
                                        mName = mName.substring(0, 3);
                                    }
                                    middlename = middlename + " " + StringUtils.capitalize(mName);
                                }
                            } else {
                                List<String> items = Arrays.asList(inputtext.split("\\s* \\s*"));
                                lastname = "" + items.get(items.size() - 1);
                                if (lastname.length() >= 3) {
                                    lastname = lastname.substring(0, 3);
                                }
                                firstname = "" + items.get(0).charAt(0);
                                for (int k = 1; k < items.size() - 1; k++) {
                                    String mName = items.get(k);
                                    if (mName.length() >= 3) {
                                        mName = mName.substring(0, 3);
                                    }
                                    middlename = middlename + " " + StringUtils.capitalize(mName);
                                }
                            }
                            name = StringUtils.capitalize(lastname) + ", " + StringUtils.capitalize(firstname) + "" + middlename;
                            jsonObject.put(CMN_JsonConstants.SURVEY_VALUE, name);
                        } else {
                            jsonObject.put(CMN_JsonConstants.SURVEY_VALUE, text.getText());
                        }
                        if (model.getSurveyFieldModel().getAttributes().size() > 0) {
                            JSONArray jsonArray1 = new JSONArray();
                            for (int i = 0; i < model.getSurveyFieldModel().getAttributes().size(); i++) {
                                JSONObject jsonObject1 = new JSONObject();
//                                jsonObject1.put("name", model.getSurveyFieldModel().getAttributes().get(i).getName());
//                                if (model.getSurveyFieldModel().getAttributes().get(i).getType().equalsIgnoreCase(CMN_JsonConstants.ViewType.checkbox.toString())) {
//
//                                    //CheckBox box = (CheckBox) model.getView();
//                                        jsonObject1.put("value", checkboxvaluemap.get(model.getSurveyFieldModel().getId()));
//                                //}
//                                } else {
                                jsonObject1.put("name", model.getSurveyFieldModel().getAttributes().get(i).getName());
                                if (loginIdCheckboxes.get(model.getSurveyFieldModel().getDisplayText()) != null) {
                                    jsonObject1.put("value", loginIdCheckboxes.get(model.getSurveyFieldModel().getDisplayText()));
                                } else {
                                    jsonObject1.put("value", model.getSurveyFieldModel().getAttributes().get(i).getDefaultValue());
                                }
//                                }
                                jsonArray1.put(jsonObject1);
                            }
                            jsonObject.put("attributes", jsonArray1);
                        }
                        jsonArray.put(jsonObject);
                    }
//                    }
                } else if (model.getView() instanceof Spinner) {
                    Spinner spinner = (Spinner) model.getView();
//                    if (!model.getInitialValue().equalsIgnoreCase("" + spinner.getSelectedItem())) {
                    JSONObject jsonObject = new JSONObject();
                    if (spinner.getSelectedItem() != null) {
                        Log.e("" + model.getSurveyFieldModel().getDisplayText(), "" + SpinnerIdsHashMap.get(spinner.getSelectedItem().toString()));
                        jsonObject.put(CMN_JsonConstants.SURVEY_VALUE, "" + SpinnerIdsHashMap.get(spinner.getSelectedItem().toString()));
                    } /*else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(CMN_GenericViewActivity.this, "Please select value for " + model.getSurveyFieldModel().getDisplayText(), Toast.LENGTH_SHORT).show();
                                }
                            });
                            return null;
                        }*/
                    jsonObject.put(CMN_JsonConstants.SURVEY_FIELDID, "" + pair.getKey());
                    jsonArray.put(jsonObject);
//                    }
                } else if (model.getView() instanceof ToggleButton) {
                    ToggleButton toggleButton = (ToggleButton) model.getView();
//                    if (!model.getInitialValue().equalsIgnoreCase("" + toggleButton.isChecked())) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(CMN_JsonConstants.SURVEY_VALUE, "" + toggleButton.isChecked());
                    jsonObject.put(CMN_JsonConstants.SURVEY_FIELDID, "" + pair.getKey());
                    jsonArray.put(jsonObject);
//                    }
                } else if (model.getView() instanceof TextView) {
                    TextView toggleButton = (TextView) model.getView();
                    if (model.getSurveyFieldModel().isMandatory() && toggleButton.getText().toString().length() == 0) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(CMN_GenericViewActivity.this, CMN_ErrorMessages.getInstance().getValue(59) + " : " + model.getSurveyFieldModel().getDisplayText(), Toast.LENGTH_SHORT).show();
                                return;
                            }
                        });
                    }
                    if (!pair.getKey().toString().contains("999")) {
                        JSONObject jsonObject = new JSONObject();
                        if (model.getSurveyFieldModel().getType().equalsIgnoreCase(CMN_JsonConstants.ViewType.DateTimePicker.toString())
                                || model.getSurveyFieldModel().getType().equalsIgnoreCase(CMN_JsonConstants.ViewType.DateTimeYearPicker.toString())) {

                            jsonObject.put(CMN_JsonConstants.SURVEY_VALUE, "" + Utils.convertGenerictime2utc(toggleButton.getText().toString()));
                        } else {
                            jsonObject.put(CMN_JsonConstants.SURVEY_VALUE, "" + toggleButton.getText());
                        }
                        jsonObject.put(CMN_JsonConstants.SURVEY_FIELDID, "" + pair.getKey());
                        jsonArray.put(jsonObject);
                    }
                }

            } catch (JSONException e) {
                e.getMessage();
            }
        }
        try

        {
            jsonObjectRequest.put(CMN_JsonConstants.SURVEY_VIEWNAME, surveyModel.getViewname().toLowerCase());
            jsonObjectRequest.put(CMN_JsonConstants.SURVEY_DATA, jsonArray);
        } catch (
                JSONException e
                )

        {
            e.getMessage();
        }

        Log.d("jsonObject", "" + jsonObjectRequest);
        return jsonObjectRequest;
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
        if (taskId == ThreadTaskIds.SAVE_SURVEY) {
            viewMap.clear();
        }
        return null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        viewMap.clear();
    }

    @Override
    public Object onNetworkCall(int taskId, Object o) throws JsonParsingException, JsonParsingException {
        if (taskId == ThreadTaskIds.GET_SURVEY) {
            String url;
            String patId;
            if (intent.hasExtra("petientId")) {
                patId = intent.getStringExtra("petientId");
            } else {
                patId = isSchedule ? CMN_Preferences.getScheduleContextId(CMN_GenericViewActivity.this) : CMN_Preferences.getCurrentContextId(CMN_GenericViewActivity.this);
            }

            if (prefURL != null) {
                url = prefURL.replace("PATIENTID==%@", "PATIENTID=" + patId).replace("TOKEN==%@", "TOKEN=" + CMN_Preferences.getUserToken(CMN_GenericViewActivity.this));
            } else {
                url = CMN_Preferences.getBaseUrl(CMN_GenericViewActivity.this)
                        + "GetViewData.aspx?token=" + CMN_Preferences.getUserToken(CMN_GenericViewActivity.this) + "&viewname=" + viewForm;
            }
            if (isSchedule) {
                url = url + "&patientid=" + patId;
                if (!scheduleId.equalsIgnoreCase("-1"))
                    url = url + "&ScheduleId=" + scheduleId;
            }
            NetworkTools networkTools = NetworkTools.getInstance();
            if (networkTools.checkNetworkConnection(CMN_GenericViewActivity.this) && url.length() > 0) {

                JSONObject response;
                if (viewForm != null && viewForm.equalsIgnoreCase("RequestSChedule")) {
                    response = networkTools.postJsonData(CMN_GenericViewActivity.this, url, scheduleJSON);
                } else {
                    response = networkTools.getJsonData(CMN_GenericViewActivity.this, url);
                }
                try {
                    if (response != null) {
                        parseJsonData(response);
                    }
                } catch (JSONException e) {
                    e.getMessage();
                }
            } else {
                Toast.makeText(CMN_GenericViewActivity.this, CMN_ErrorMessages.getInstance().getValue(12) + " : ", Toast.LENGTH_SHORT).show();
            }
        } else if (taskId == ThreadTaskIds.SAVE_SURVEY) {
            String patId = isSchedule ? CMN_Preferences.getScheduleContextId(CMN_GenericViewActivity.this) : CMN_Preferences.getCurrentContextId(CMN_GenericViewActivity.this);
            String url = CMN_Preferences.getBaseUrl(CMN_GenericViewActivity.this)
                    + "UpdateViewData.aspx?token=" + CMN_Preferences.getUserToken(CMN_GenericViewActivity.this) + "&viewname=" + surveyModel.getViewname().toLowerCase() /* + "&jsondata=" + URLEncoder.encode("" + createResponseJson())*/;
            if (isSchedule) {
                url = url + "&patientid=" + patId + "&SchType=" + Utils.schType + "&ScheduleId=" + scheduleId;
                if (isForceSave) {
                    url = url + "&forcesave=true";
                }

            }

            NetworkTools networkTools = NetworkTools.getInstance();
            if (networkTools.checkNetworkConnection(CMN_GenericViewActivity.this) && url.length() > 0) {

                if (isSchedule || viewMap.size() != 0) {
                    JSONObject jsonObject = null;
                    if (isSchedule && isForceSave) {
                        jsonObject = jsonObjectRequest;
                    } else {
                        jsonObject = createResponseJson();
                    }
                    final JSONObject response = networkTools.postJsonData(CMN_GenericViewActivity.this, url, jsonObject);
                    try {
                        if (response != null) {
                            Log.d("response", "" + response);
                            if (response.getJSONObject("Result").getInt("Code") == 0) {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            final String message = response.getJSONObject("Result").getString("Message");
                                            new AlertDialog.Builder(CMN_GenericViewActivity.this)
                                                    .setTitle(surveyModel.getViewname())
                                                    .setMessage(message)
                                                    .setCancelable(false)
                                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            if (isChangingPassword && !message.equalsIgnoreCase(CMN_ErrorMessages.getInstance().getValue(42))) {
                                                                Utils.signout();
                                                            } else if (message.equalsIgnoreCase(CMN_ErrorMessages.getInstance().getValue(42))) {

                                                            } else if (isSchedule) {
                                                                Intent intent = new Intent(CMN_GenericViewActivity.this, CalenderActivity.class);
                                                                startActivity(intent);
                                                                for (int i = 0; i < ActivityPackage.getActivityList().size(); i++) {
                                                                    ActivityPackage.getActivityList().get(i).finish();
                                                                }

                                                            } else if (viewForm.equalsIgnoreCase("intake")) {
                                                                if (CMN_Preferences.getUseScheduling(CMN_GenericViewActivity.this)) {
                                                                    new AlertDialog.Builder(CMN_GenericViewActivity.this)
                                                                            .setTitle("Schedule")
                                                                            .setMessage("Do you want to create schedule for the created patient?")
                                                                            .setCancelable(false)
                                                                            .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                                                                public void onClick(DialogInterface dialog, int which) {
                                                                                    dialog.dismiss();
                                                                                    finish();
                                                                                }
                                                                            })
                                                                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                                                public void onClick(DialogInterface dialog, int which) {
                                                                                    try {
                                                                                        JSONObject jsonObject1 = response.getJSONObject("Data");
                                                                                        String patId = jsonObject1.getJSONObject("Patient").getString("ID");
                                                                                        CMN_Preferences.setScheduleContextId(CMN_GenericViewActivity.this, patId);
                                                                                    } catch (JSONException e) {
                                                                                        e.printStackTrace();
                                                                                    }
                                                                                    Utils.fromIntake = true;
                                                                                    Utils.showSchedulingPopup(CMN_GenericViewActivity.this);


                                                                                }
                                                                            })
                                                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                                                            .show();

                                                                }
                                                            } else {
                                                                if (surveyModel.getViewname().toLowerCase().equalsIgnoreCase("intake")) {
                                                                    CMN_Preferences.setisNewPatientAdded(CMN_GenericViewActivity.this, true);
                                                                }
                                                                finish();
                                                            }

                                                        }
                                                    })
                                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                                    .show();
                                        } catch (JSONException e) {
                                            e.getMessage();
                                        }
                                    }
                                });
                            } else if (isSchedule && response.getJSONObject("Result").getInt("Code") == 100) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            final String message = response.getJSONObject("Result").getString("Message");
                                            new AlertDialog.Builder(CMN_GenericViewActivity.this)
                                                    .setTitle(surveyModel.getViewname())
                                                    .setMessage(message)
                                                    .setCancelable(false)
                                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            isForceSave = true;
                                                            NetworkAsyncTask networkAsyncTask = NetworkAsyncTask.getInstance();
                                                            try {
                                                                networkAsyncTask.startNetworkCall(ThreadTaskIds.SAVE_SURVEY, CMN_GenericViewActivity.this);
                                                            } catch (NetworkException e) {
                                                                e.getMessage();
                                                            }
                                                        }
                                                    })
                                                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            finish();
                                                        }
                                                    })
                                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                                    .show();
                                        } catch (JSONException e) {
                                            e.getMessage();
                                        }
                                    }
                                });

                            } else {
                                {

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                final String message = response.getJSONObject("Result").getString("Message");
                                                new AlertDialog.Builder(CMN_GenericViewActivity.this)
                                                        .setTitle(surveyModel.getViewname())
                                                        .setMessage(message)
                                                        .setCancelable(false)
                                                        .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                finish();

                                                            }
                                                        })
                                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                                        .show();
                                            } catch (JSONException e) {
                                                e.getMessage();
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.getMessage();
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CMN_GenericViewActivity.this, CMN_ErrorMessages.getInstance().getValue(164), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CMN_GenericViewActivity.this, CMN_ErrorMessages.getInstance().getValue(12), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }
        return null;
    }

    private void parseJsonData(final JSONObject response) throws JSONException {
        if (response.getJSONObject("Result").getInt("Code") == 0 /*&& response.getJSONObject("Result").getString("Message").equalsIgnoreCase("SUCCESS")*/) {

            JSONObject rootObject = response.getJSONObject("Data");
            surveyModel = new CMN_GenericModel();
            surveyModel.setViewname(rootObject.getString(CMN_JsonConstants.SURVEY_VIEWNAME));
            surveyModel.setButtonTitle(rootObject.getString(CMN_JsonConstants.SURVEY_BUTTONTITLE));

            JSONArray categoriesArray = rootObject.getJSONArray(CMN_JsonConstants.SURVEY_CATEGORY);
            List<CMN_GenericCategoriesModel> surveyCategoriesModelList = new ArrayList<CMN_GenericCategoriesModel>();
            for (int i = 0; i < categoriesArray.length(); i++) {
                JSONObject category = categoriesArray.getJSONObject(i);
                CMN_GenericCategoriesModel surveycategoriesModel = new CMN_GenericCategoriesModel();
                surveycategoriesModel.setName(category.getString(CMN_JsonConstants.SURVEY_NAME));
                surveycategoriesModel.setDisplayName(category.getString(CMN_JsonConstants.SURVEY_DISPLAYNAME));
                surveycategoriesModel.setPosition(category.getInt(CMN_JsonConstants.SURVEY_POSITION));

                //==============Field object parsing===================
                List<CMN_GenericFieldModel> surveyFieldModelList = new ArrayList<CMN_GenericFieldModel>();
                JSONArray fieldsArray = category.getJSONArray(CMN_JsonConstants.SURVEY_FIELDS);
                for (int j = 0; j < fieldsArray.length(); j++) {
                    JSONObject field = fieldsArray.getJSONObject(j);
                    CMN_GenericFieldModel surveyFieldModel = new CMN_GenericFieldModel();
                    surveyFieldModel.setDisplay(field.getBoolean(CMN_JsonConstants.SURVEY_DISPLAY));
                    surveyFieldModel.setDisplayText(field.getString(CMN_JsonConstants.SURVEY_DISPLAYTEXT));
                    surveyFieldModel.setId(field.getInt(CMN_JsonConstants.SURVEY_ID));
                    if (!shouldupdate) {
                        shouldupdate = field.getBoolean(CMN_JsonConstants.SURVEY_ISEDIT);
                    }
                    surveyFieldModel.setIsEdit(field.getBoolean(CMN_JsonConstants.SURVEY_ISEDIT));
                    surveyFieldModel.setIsMandatory(field.getBoolean(CMN_JsonConstants.SURVEY_ISMANDATORY));
                    surveyFieldModel.setMaxLimmit(field.getInt(CMN_JsonConstants.SURVEY_MAXLIMIT));
                    surveyFieldModel.setType(field.getString(CMN_JsonConstants.SURVEY_TYPE));
                    surveyFieldModel.setValidation(field.getString(CMN_JsonConstants.SURVEY_VALIDATION));
                    surveyFieldModel.setValue(field.getString(CMN_JsonConstants.SURVEY_VALUE));

                    //==============ListValues object parsing===================
                    List<CMN_GenericListValuesModel> surveyListValuesModelList = new ArrayList<CMN_GenericListValuesModel>();
                    try {
                        JSONArray listvalues = field.getJSONArray(CMN_JsonConstants.SURVEY_LISTVALUEDATA);
                        if (listvalues != null) {
                            for (int k = 0; k < listvalues.length(); k++) {
                                JSONObject listData = listvalues.getJSONObject(k);
                                CMN_GenericListValuesModel surveyListValuesModel = new CMN_GenericListValuesModel();
                                surveyListValuesModel.setValue(listData.getString(CMN_JsonConstants.SURVEY_VALUE));
                                surveyListValuesModel.setId(listData.getString(CMN_JsonConstants.SURVEY_ID));
                                List<CMN_DependencyFieldModel> dependencyFieldModels = new ArrayList<>();
                                if (listData.has(CMN_JsonConstants.SURVEY_LISTVALUEDEPENDENTDATA)) {
                                    try {
                                        JSONArray listdependentvalues = listData.getJSONArray(CMN_JsonConstants.SURVEY_LISTVALUEDEPENDENTDATA);
                                        for (int m = 0; m < listdependentvalues.length(); m++) {
                                            JSONObject object = listdependentvalues.getJSONObject(m);
                                            CMN_DependencyFieldModel dependencyFieldModel = new CMN_DependencyFieldModel();
                                            dependencyFieldModel.setDependentFieldID(object.getString("DependentFieldID"));
                                            JSONArray jsonArray1 = object.getJSONArray("values");
                                            List<String> depvalues = new ArrayList<>();
                                            for (int n = 0; n < jsonArray1.length(); n++) {
                                                JSONObject jsonObject = jsonArray1.getJSONObject(n);
                                                String value = jsonObject.getString("value");
                                                depvalues.add(value);
                                            }
                                            dependencyFieldModel.setDependentValues(depvalues);
                                            dependencyFieldModels.add(dependencyFieldModel);
                                        }
                                    } catch (Exception e) {
                                        e.getMessage();
                                    }
                                }
                                surveyListValuesModel.setDependencyFieldModels(dependencyFieldModels);
                                surveyListValuesModelList.add(surveyListValuesModel);
                            }
                            surveyFieldModel.setListValuesData(surveyListValuesModelList);
                        }
                    } catch (Exception e) {
                        e.getMessage();
                    }
                    //==============Attributes object parsing===================
                    List<CMN_GenericAttributesModel> surveyAttributesModelList = new ArrayList<CMN_GenericAttributesModel>();
                    try {
                        JSONArray attributeValues = field.getJSONArray(CMN_JsonConstants.SURVEY_ATTRIBUTES);
                        if (attributeValues != null) {
                            for (int k = 0; k < attributeValues.length(); k++) {
                                JSONObject attributeData = attributeValues.getJSONObject(k);
                                CMN_GenericAttributesModel surveyAttributesModel = new CMN_GenericAttributesModel();
                                surveyAttributesModel.setType(attributeData.getString(CMN_JsonConstants.SURVEY_TYPE));
                                surveyAttributesModel.setDefaultValue(attributeData.getString(CMN_JsonConstants.SURVEY_DEFAULTVALUE));
                                surveyAttributesModel.setName(attributeData.getString(CMN_JsonConstants.SURVEY_NAME));
                                surveyAttributesModelList.add(surveyAttributesModel);
                            }
                            surveyFieldModel.setAttributes(surveyAttributesModelList);
                        }
                    } catch (Exception e) {
                        e.getMessage();
                    }
                    surveyFieldModelList.add(surveyFieldModel);
                }
                surveycategoriesModel.setFields(surveyFieldModelList);
                surveyCategoriesModelList.add(surveycategoriesModel);
            }
            surveyModel.setCategories(surveyCategoriesModelList);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (shouldshowlist || surveyModel.getCategories().size() == 1) {
                        listView.setVisibility(View.GONE);
                        scrollView.setVisibility(View.VISIBLE);
                        generateView(surveyModel, position);

                        initDateTimePicker();
                        if (!isScheduleView)
                            initSpecialityProcedureSpinners();
                        initLoginIdCheckBoxes();
                        if (viewForm.equalsIgnoreCase("SecuritySettings")) {
                            setupSecuritySetting();
                        }
                        viewTempMap = viewMap;
                        Iterator it = viewTempMap.entrySet().iterator();
                        while (it.hasNext()) {
                            Map.Entry pair = (Map.Entry) it.next();

                            try {
                                final CMN_ViewFieldModel model = (CMN_ViewFieldModel) pair.getValue();
                                if (model.getView() instanceof EditText) {
                                    EditText editText = (EditText) model.getView();
                                    mandatoryMap.put(model.getSurveyFieldModel(), editText);
                                }
                                if (model.getView() instanceof TextView) {
                                    TextView textView = (TextView) model.getView();
                                    mandatoryMap.put(model.getSurveyFieldModel(), textView);
                                }
                            } catch (Exception e) {
                                e.getMessage();
                            }
                        }
                    } else {
                        shouldshowbutton = false;
                        invalidateOptionsMenu();
                        listView.setVisibility(View.VISIBLE);
                        scrollView.setVisibility(View.GONE);
                        List<String> values = new ArrayList<String>();
                        for (int i = 0; i < surveyModel.getCategories().size(); i++) {
                            values.add(surveyModel.getCategories().get(i).getDisplayName());
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(CMN_GenericViewActivity.this,
                                android.R.layout.simple_list_item_1, android.R.id.text1, values);

                        listView.setAdapter(adapter);

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent(CMN_GenericViewActivity.this, CMN_GenericViewActivity.class);
                                intent.putExtra("position", position);
                                intent.putExtra("data", contextData);
                                startActivity(intent);
                            }
                        });
                    }
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        noData.setText(response.getJSONObject("Result").getString("Message"));
                    } catch (JSONException e) {
                        e.getMessage();
                    }
                    noData.setVisibility(View.VISIBLE);
                }
            });

        }

    }

    private void generateView(CMN_GenericModel surveyModel, int pos) {

        if (surveyModel.getCategories() == null) {
            return;
        }
        getActionBar().setTitle(surveyModel.getCategories().get(pos).getDisplayName());

//        ArrayList<CMN_GenericCategoriesModel> surveycategoriesModels = (ArrayList<CMN_GenericCategoriesModel>) surveyModel.getCategories();
//        for (int i = 0; i < surveycategoriesModels.size(); i++) {
        CMN_GenericCategoriesModel surveycategoriesModel = surveyModel.getCategories().get(pos);
        LinearLayout parent = new LinearLayout(CMN_GenericViewActivity.this);
        parent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        parent.setOrientation(LinearLayout.VERTICAL);

        ArrayList<CMN_GenericFieldModel> surveyFieldModels = (ArrayList<CMN_GenericFieldModel>) surveycategoriesModel.getFields();
        for (int j = 0; j < surveyFieldModels.size(); j++) {
            CMN_GenericFieldModel surveyFieldModel = surveyFieldModels.get(j);
            parent.addView(CMN_GenerateGenericViewComponents.getComponentGenerator().createComponent(CMN_GenericViewActivity.this, surveyFieldModel));
            if (surveyFieldModel.getAttributes().size() > 0) {
                for (int k = 0; k < surveyFieldModel.getAttributes().size(); k++) {
                    CMN_GenericFieldModel surveyFieldModel1 = new CMN_GenericFieldModel();
                    if (surveyFieldModel.getAttributes().get(k).getType().equalsIgnoreCase(CMN_JsonConstants.ViewType.checkbox.toString())) {
                        surveyFieldModel1.setDisplayText(surveyFieldModel.getAttributes().get(k).getName());
                        surveyFieldModel1.setKey(surveyFieldModel.getDisplayText());
                        surveyFieldModel1.setType(surveyFieldModel.getAttributes().get(k).getType());
                        surveyFieldModel1.setValue(surveyFieldModel.getAttributes().get(k).getDefaultValue());
                        surveyFieldModel1.setId(Integer.parseInt("" + 999 + "" + surveyFieldModel.getId()));
                        parent.addView(CMN_GenerateGenericViewComponents.getComponentGenerator().createComponent(CMN_GenericViewActivity.this, surveyFieldModel1));
                    }
                }
            }
            View view = new View(CMN_GenericViewActivity.this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
            layoutParams.setMargins(0, 10, 0, 10);
            view.setLayoutParams(layoutParams);
            view.setBackgroundColor(Color.GRAY);

            parent.addView(view);


        }

        mainLayout.addView(parent);
//        }
    }

    @Override
    public Object onNetworkError(int taskId, NetworkException o) {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(CMN_GenericViewActivity.this, CMN_ErrorMessages.getInstance().getValue(12), Toast.LENGTH_SHORT).show();
        return null;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_save);
        if (!shouldshowbutton || isScheduleView) {
            item.setVisible(false);
        } else {
            item.setVisible(true);
        }


        if (surveyModel != null && surveyModel.getButtonTitle() != null)
            item.setTitle(surveyModel.getButtonTitle());
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
                if (Password != null && confirmPassword != null && !Password.getText().toString().trim().equals(confirmPassword.getText().toString().trim())) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            CMN_GenericViewActivity.this);
                    builder.setTitle("Settings");
                    builder.setMessage("Passwords does not match");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                    return false;
                }
                Iterator it = mandatoryMap.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();
                    CMN_GenericFieldModel fieldModel = (CMN_GenericFieldModel) pair.getKey();
                    if (pair.getValue() instanceof EditText) {
                        EditText editText = (EditText) pair.getValue();
                        if (fieldModel.IsMandatory && editText.getText().toString().length() <= 0) {
                            Toast.makeText(CMN_GenericViewActivity.this, CMN_ErrorMessages.getInstance().getValue(59) + " : " + fieldModel.getDisplayText(), Toast.LENGTH_SHORT).show();
                            return false;
//                        break;
                        }
                    } else if (pair.getValue() instanceof TextView) {
                        TextView textView = (TextView) pair.getValue();
                        if (fieldModel.IsMandatory && !fieldModel.getType().equalsIgnoreCase(CMN_JsonConstants.ViewType.Switch.toString()) && textView.getText().toString().length() <= 0) {
                            Toast.makeText(CMN_GenericViewActivity.this, CMN_ErrorMessages.getInstance().getValue(59) + " : " + fieldModel.getDisplayText(), Toast.LENGTH_SHORT).show();
                            return false;
//                        break;
                        }
                    }
                }
                NetworkAsyncTask networkAsyncTask = NetworkAsyncTask.getInstance();
                try {
                    networkAsyncTask.startNetworkCall(ThreadTaskIds.SAVE_SURVEY, CMN_GenericViewActivity.this);
                } catch (NetworkException e) {
                    e.getMessage();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void initLoginIdCheckBoxes() {
        Iterator it = viewMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();

            final CMN_ViewFieldModel model = (CMN_ViewFieldModel) pair.getValue();
            if (model.getSurveyFieldModel().getType().equalsIgnoreCase(CMN_JsonConstants.ViewType.checkbox.toString())) {

                if (model.getView() instanceof CheckBox) {
                    if (model.getView().getTag() != null && model.getView().getTag().toString().contains("Patient Name") || model.getView().getTag().toString().contains("Name") || model.getView().getTag().toString().contains("Medical Record Number")) {
                        nameAsLoginID = (CheckBox) model.getView();
                        nameAsLoginID.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked) {
                                    loginIdCheckboxes.put(nameAsLoginID.getTag().toString(), isChecked);
                                    if (mobileAsLoginID != null) {
                                        mobileAsLoginID.setChecked(false);
                                        loginIdCheckboxes.put(mobileAsLoginID.getTag().toString(), false);
                                    }
                                    if (emailAsLoginID != null) {
                                        emailAsLoginID.setChecked(false);
                                        loginIdCheckboxes.put(emailAsLoginID.getTag().toString(), false);
                                    }
                                }
                            }
                        });
                    } else if (model.getView().getTag() != null && model.getView().getTag().toString().contains("Phone Number") || model.getView().getTag().toString().contains("Mobile Phone Number")) {
                        mobileAsLoginID = (CheckBox) model.getView();
                        mobileAsLoginID.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked) {
                                    loginIdCheckboxes.put(mobileAsLoginID.getTag().toString(), isChecked);
                                    if (nameAsLoginID != null) {
                                        nameAsLoginID.setChecked(false);
                                        loginIdCheckboxes.put(nameAsLoginID.getTag().toString(), false);
                                    }
                                    if (emailAsLoginID != null) {
                                        emailAsLoginID.setChecked(false);
                                        loginIdCheckboxes.put(emailAsLoginID.getTag().toString(), false);
                                    }
                                }
                            }
                        });
                    } else if (model.getView().getTag() != null && model.getView().getTag().toString().contains("Email Address") || model.getView().getTag().toString().contains("Email")) {
                        emailAsLoginID = (CheckBox) model.getView();
                        emailAsLoginID.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked) {
                                    loginIdCheckboxes.put(emailAsLoginID.getTag().toString(), isChecked);
                                    if (nameAsLoginID != null) {
                                        nameAsLoginID.setChecked(false);
                                        loginIdCheckboxes.put(nameAsLoginID.getTag().toString(), false);
                                    }
                                    if (mobileAsLoginID != null) {
                                        mobileAsLoginID.setChecked(false);
                                        loginIdCheckboxes.put(mobileAsLoginID.getTag().toString(), false);
                                    }
                                }
                            }
                        });
                    }
                }
            }

        }
    }

    public void initSpecialityProcedureSpinners() {
        Iterator it = viewMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();

            final CMN_ViewFieldModel model = (CMN_ViewFieldModel) pair.getValue();
            if (model.getSurveyFieldModel().getType().equalsIgnoreCase(CMN_JsonConstants.ViewType.ListValue.toString())) {

                if (model.getView() instanceof Spinner) {
                    if (model.getView().getTag() != null && model.getView().getTag().toString().equalsIgnoreCase("Scheduled Procedure")) {
                        _procedure_listValuesData = model.getSurveyFieldModel().getListValuesData();
                        _procedure = (Spinner) model.getView();
                    } else if (model.getView().getTag() != null && model.getView().getTag().toString().equalsIgnoreCase("Surgeon")) {
                        _provider_listValuesData = model.getSurveyFieldModel().getListValuesData();
                        _provider = (Spinner) model.getView();
                    } else if (model.getView().getTag() != null && model.getView().getTag().toString().equalsIgnoreCase("Speciality")) {
                        _speciality_listValuesData = model.getSurveyFieldModel().getListValuesData();
                        _speciality = (Spinner) model.getView();
                        _speciality.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                final List<String> _procedureNames = new ArrayList<>();
                                final List<String> _providerNames = new ArrayList<>();
                                if (_procedure_listValuesData != null) {
                                    for (int i = 0; i < _procedure_listValuesData.size(); i++) {
                                        List<CMN_DependencyFieldModel> fieldModels = _procedure_listValuesData.get(i).getDependencyFieldModels();
                                        for (int j = 0; j < fieldModels.size(); j++) {
                                            List<String> list = fieldModels.get(j).getDependentValues();
                                            for (int k = 0; k < list.size(); k++) {
                                                if (_speciality.getSelectedItem().toString().trim().equalsIgnoreCase(list.get(k).toString().toString())) {
                                                    _procedureNames.add(_procedure_listValuesData.get(i).getValue());
                                                }
                                            }
                                        }
                                    }
                                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(CMN_GenericViewActivity.this, android.R.layout.simple_spinner_item, _procedureNames);
                                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    _procedure.setAdapter(dataAdapter);
                                }
                                if (_provider_listValuesData != null) {
                                    for (int i = 0; i < _provider_listValuesData.size(); i++) {
                                        List<CMN_DependencyFieldModel> fieldModels = _provider_listValuesData.get(i).getDependencyFieldModels();
                                        for (int j = 0; j < fieldModels.size(); j++) {
                                            List<String> list = fieldModels.get(j).getDependentValues();
                                            for (int k = 0; k < list.size(); k++) {
                                                if (_speciality.getSelectedItem().toString().trim().equalsIgnoreCase(list.get(k).toString().toString())) {
                                                    _providerNames.add(_provider_listValuesData.get(i).getValue());
                                                }
                                            }
                                        }
                                    }
                                    ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(CMN_GenericViewActivity.this, android.R.layout.simple_spinner_item, _providerNames);
                                    dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    _provider.setAdapter(dataAdapter1);
                                }

                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }
                }
            }
        }
    }

    public void setupSecuritySetting() {
        Iterator it = viewMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();

            final CMN_ViewFieldModel model = (CMN_ViewFieldModel) pair.getValue();
            if (model.getSurveyFieldModel().getType().equalsIgnoreCase(CMN_JsonConstants.ViewType.TextField.toString())) {
                if (Password == null) {
                    Password = (TextView) model.getView();
                } else {
                    confirmPassword = (TextView) model.getView();
                }
            }

        }
    }


    public void initDateTimePicker() {
        Iterator it = viewMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();

            final CMN_ViewFieldModel model = (CMN_ViewFieldModel) pair.getValue();
            if (model.getSurveyFieldModel().getType().equalsIgnoreCase(CMN_JsonConstants.ViewType.DateTimePicker.toString())) {
                dateTimeModel = model.getSurveyFieldModel();
                if (model.getView() instanceof TextView) {
                    if (dateTimePicker == null) {
                        dateTimePicker = (TextView) model.getView();
                    } else if (dateTimePicker1 == null) {
                        dateTimePicker1 = (TextView) model.getView();
                    }
                }
            }

            if (model.getSurveyFieldModel().getType().equalsIgnoreCase(CMN_JsonConstants.ViewType.DatePicker.toString())) {
                dateTimeYearModel = model.getSurveyFieldModel();
                if (model.getView() instanceof TextView) {
                    if (datePicker == null) {
                        datePicker = (TextView) model.getView();
                    } else {
                        datePicker1 = (TextView) model.getView();
                    }
                }
            }
        }
        if (/*!isSchedule &&*/ dateTimePicker != null) {
            dateTimePicker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final View dialogView = View.inflate(CMN_GenericViewActivity.this, R.layout.date_time_picker, null);
                    final AlertDialog alertDialog = new AlertDialog.Builder(CMN_GenericViewActivity.this).create();

                    final DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);
                    final TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.time_picker);
                    try {
                        // 2016 - 04 - 21 09:52:08
                        String tempdate = dateTimeModel.getValue();
                        String year, month, day, hour, minutes;

                        year = tempdate.split("-")[0].toString().trim();
                        tempdate = tempdate.split("-")[1].toString().trim();

                        month = tempdate.split("-")[0].toString().trim();
                        tempdate = tempdate.split("-")[1].toString().trim();

                        day = tempdate.split("-")[0].toString().trim();
                        tempdate = tempdate.split("-")[1].toString().trim();

                        hour = tempdate.split(":")[0].toString().trim();
                        tempdate = tempdate.split(":")[1].toString().trim();

                        minutes = tempdate.split(":")[0].toString().trim();
                        tempdate = tempdate.split(":")[1].toString().trim();

                        datePicker.updateDate(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
                        timePicker.setCurrentHour(Integer.parseInt(hour));
                        timePicker.setCurrentMinute(Integer.parseInt(minutes));

                    } catch (Exception e) {
                        e.getMessage();
                    }

                    dialogView.findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Calendar calendar = new GregorianCalendar(datePicker.getYear(),
                                    datePicker.getMonth(),
                                    datePicker.getDayOfMonth(),
                                    timePicker.getCurrentHour(),
                                    timePicker.getCurrentMinute());

                            long time = calendar.getTimeInMillis();
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
                            String dateString = formatter.format(new Date(time));
                            dateTimePicker.setText(dateString);

                            alertDialog.dismiss();
                        }
                    });

                    dialogView.findViewById(R.id.date_time_cancel).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.setView(dialogView);
                    alertDialog.show();
                }
            });
        }
        if (/*!isSchedule && */datePicker != null) {
            datePicker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final View dialogView = View.inflate(CMN_GenericViewActivity.this, R.layout.date_dlg, null);
                    final AlertDialog alertDialog = new AlertDialog.Builder(CMN_GenericViewActivity.this).create();

                    dialogView.findViewById(R.id.setcancellayout).setVisibility(View.VISIBLE);
                    dialogView.findViewById(R.id.datebtnOk).setVisibility(View.GONE);

                    final DatePicker dPicker = (DatePicker) dialogView.findViewById(R.id.datePicker2);

                    try {
                        String tempdate = dateTimeModel.getValue();
                        String year, month, day, hour, minutes;

                        year = tempdate.split("-")[0].toString().trim();
                        tempdate = tempdate.split("-")[1].toString().trim();

                        month = tempdate.split("-")[0].toString().trim();
                        tempdate = tempdate.split("-")[1].toString().trim();

                        day = tempdate.split("-")[0].toString().trim();
                        tempdate = tempdate.split("-")[1].toString().trim();

                        hour = tempdate.split(":")[0].toString().trim();
                        tempdate = tempdate.split(":")[1].toString().trim();

                        minutes = tempdate.split(":")[0].toString().trim();
                        tempdate = tempdate.split(":")[1].toString().trim();

                        dPicker.updateDate(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));

                    } catch (Exception e) {
                        e.getMessage();
                    }


                    dialogView.findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Calendar calendar = new GregorianCalendar(dPicker.getYear(),
                                    dPicker.getMonth(),
                                    dPicker.getDayOfMonth());

                            long time = calendar.getTimeInMillis();
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                            String dateString = formatter.format(new Date(time));
                            datePicker.setText(dateString);

                            alertDialog.dismiss();
                        }
                    });

                    dialogView.findViewById(R.id.date_time_cancel).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.setView(dialogView);
                    alertDialog.show();
                }
            });
        }
        if (/*!isSchedule &&*/ dateTimePicker1 != null) {
            dateTimePicker1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final View dialogView = View.inflate(CMN_GenericViewActivity.this, R.layout.date_time_picker, null);
                    final AlertDialog alertDialog = new AlertDialog.Builder(CMN_GenericViewActivity.this).create();

                    final DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);
                    final TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.time_picker);
                    try {
                        // 2016 - 04 - 21 09:52:08
                        String tempdate = dateTimeModel.getValue();
                        String year, month, day, hour, minutes;

                        year = tempdate.split("-")[0].toString().trim();
                        tempdate = tempdate.split("-")[1].toString().trim();

                        month = tempdate.split("-")[0].toString().trim();
                        tempdate = tempdate.split("-")[1].toString().trim();

                        day = tempdate.split("-")[0].toString().trim();
                        tempdate = tempdate.split("-")[1].toString().trim();

                        hour = tempdate.split(":")[0].toString().trim();
                        tempdate = tempdate.split(":")[1].toString().trim();

                        minutes = tempdate.split(":")[0].toString().trim();
                        tempdate = tempdate.split(":")[1].toString().trim();

                        datePicker.updateDate(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
                        timePicker.setCurrentHour(Integer.parseInt(hour));
                        timePicker.setCurrentMinute(Integer.parseInt(minutes));

                    } catch (Exception e) {
                        e.getMessage();
                    }

                    dialogView.findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Calendar calendar = new GregorianCalendar(datePicker.getYear(),
                                    datePicker.getMonth(),
                                    datePicker.getDayOfMonth(),
                                    timePicker.getCurrentHour(),
                                    timePicker.getCurrentMinute());

                            long time = calendar.getTimeInMillis();
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
                            String dateString = formatter.format(new Date(time));
                            dateTimePicker1.setText(dateString);

                            alertDialog.dismiss();
                        }
                    });

                    dialogView.findViewById(R.id.date_time_cancel).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.setView(dialogView);
                    alertDialog.show();
                }
            });
        }
        if (/*!isSchedule &&*/ datePicker1 != null) {
            datePicker1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final View dialogView = View.inflate(CMN_GenericViewActivity.this, R.layout.date_dlg, null);
                    final AlertDialog alertDialog = new AlertDialog.Builder(CMN_GenericViewActivity.this).create();

                    dialogView.findViewById(R.id.setcancellayout).setVisibility(View.VISIBLE);
                    dialogView.findViewById(R.id.datebtnOk).setVisibility(View.GONE);

                    final DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.datePicker2);

                    try {
                        String tempdate = dateTimeModel.getValue();
                        String year, month, day, hour, minutes;

                        year = tempdate.split("-")[0].toString().trim();
                        tempdate = tempdate.split("-")[1].toString().trim();

                        month = tempdate.split("-")[0].toString().trim();
                        tempdate = tempdate.split("-")[1].toString().trim();

                        day = tempdate.split("-")[0].toString().trim();
                        tempdate = tempdate.split("-")[1].toString().trim();

                        hour = tempdate.split(":")[0].toString().trim();
                        tempdate = tempdate.split(":")[1].toString().trim();

                        minutes = tempdate.split(":")[0].toString().trim();
                        tempdate = tempdate.split(":")[1].toString().trim();

                        datePicker.updateDate(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));

                    } catch (Exception e) {
                        e.getMessage();
                    }


                    dialogView.findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Calendar calendar = new GregorianCalendar(datePicker.getYear(),
                                    datePicker.getMonth(),
                                    datePicker.getDayOfMonth());

                            long time = calendar.getTimeInMillis();
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                            String dateString = formatter.format(new Date(time));
                            datePicker1.setText(dateString);

                            alertDialog.dismiss();
                        }
                    });

                    dialogView.findViewById(R.id.date_time_cancel).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.setView(dialogView);
                    alertDialog.show();
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
