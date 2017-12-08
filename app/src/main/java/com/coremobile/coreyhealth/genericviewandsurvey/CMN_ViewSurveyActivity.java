package com.coremobile.coreyhealth.genericviewandsurvey;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
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
import com.coremobile.coreyhealth.newui.Fields;
import com.coremobile.coreyhealth.newui.MessageTabActivityCMN;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class CMN_ViewSurveyActivity extends CMN_AppBaseActivity implements NetworkCallBack {

    LinearLayout progressBar;
    private SharedPreferences mCurrentUserPref;
    public static final String CURRENT_USER = "CurrentUser";
    String organizationName,   context;
    LinearLayout mainLayout;
    public static HashMap<String, CMN_ViewFieldModel> viewMap = new HashMap<>();
    JSONArray jsonArray;
    CMN_GenericModel surveyModel;
    ProgressDialog progressDialog;
    boolean shouldupdate = false;
    TextView noData, dateTimePicker, dateTimeYearPicker;
    CMN_GenericFieldModel dateTimeModel, dateTimeYearModel;
    Fields fields;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewsurvey_activity);

        progressBar = (LinearLayout) findViewById(R.id.progressbar);

        mainLayout = (LinearLayout) findViewById(R.id.layoutRoot);

        noData = (TextView) findViewById(R.id.nodataTextview);
        noData.setVisibility(View.GONE);


        findViewById(R.id.listview).setVisibility(View.GONE);

        intent = getIntent();
        if (intent != null) {
            fields = (Fields) intent.getSerializableExtra("model");
        }
        mCurrentUserPref = getSharedPreferences(CURRENT_USER, 0);
        organizationName = mCurrentUserPref.getString("Organization", null);
         context = mCurrentUserPref.getString("context", null);

        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setTitle(
                "" + getResources().getString(R.string.view_survey));
        getActionBar().setDisplayHomeAsUpEnabled(true);

        NetworkAsyncTask networkAsyncTask = NetworkAsyncTask.getInstance();
        try {
            networkAsyncTask.startNetworkCall(ThreadTaskIds.GET_SURVEY, CMN_ViewSurveyActivity.this);
        } catch (NetworkException e) {
            e.getMessage();
        }


    }

    private JSONObject createResponseJson() {

        Iterator it = viewMap.entrySet().iterator();
        jsonArray = new JSONArray();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();

            try {
                final CMN_ViewFieldModel model = (CMN_ViewFieldModel) pair.getValue();
                if (model.getView() instanceof EditText) {
                    JSONObject jsonObject = new JSONObject();
                    EditText text = (EditText) model.getView();
                    if (!model.getInitialValue().equalsIgnoreCase(text.getText().toString().trim())) {
//                        if (model.getSurveyFieldModel().isMandatory() && text.getText().toString().length() == 0) {
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Toast.makeText(CMN_ViewSurveyActivity.this, "Please enter text for " + model.getSurveyFieldModel().getDisplayText(), Toast.LENGTH_SHORT).show();
//                                    return;
//                                }
//                            });
//                        } else
                        if (text.getText().toString().length() != 0) {
                            jsonObject.put(CMN_JsonConstants.SURVEY_FIELDID, "" + pair.getKey());
                            jsonObject.put(CMN_JsonConstants.SURVEY_VALUE, text.getText());
                            jsonArray.put(jsonObject);
                        }
                    }
                } else if (model.getView() instanceof Spinner) {
                    Spinner spinner = (Spinner) model.getView();
                    if (!model.getInitialValue().equalsIgnoreCase("" + spinner.getSelectedItem())) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put(CMN_JsonConstants.SURVEY_VALUE, "" + spinner.getSelectedItem());
                        jsonObject.put(CMN_JsonConstants.SURVEY_FIELDID, "" + pair.getKey());
                        jsonArray.put(jsonObject);
                    }
                } else if (model.getView() instanceof ToggleButton) {
                    ToggleButton toggleButton = (ToggleButton) model.getView();
                    if (!model.getInitialValue().equalsIgnoreCase("" + toggleButton.isChecked())) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put(CMN_JsonConstants.SURVEY_VALUE, "" + toggleButton.isChecked());
                        jsonObject.put(CMN_JsonConstants.SURVEY_FIELDID, "" + pair.getKey());
                        jsonArray.put(jsonObject);
                    }
                }

            } catch (JSONException e) {
                e.getMessage();
            }
        }

        JSONObject jsonObject = new JSONObject();
        try

        {
            jsonObject.put(CMN_JsonConstants.SURVEY_VIEWNAME, surveyModel.getViewname().toLowerCase());
            jsonObject.put(CMN_JsonConstants.SURVEY_DATA, jsonArray);
        } catch (
                JSONException e
                )

        {
            e.getMessage();
        }

        Log.d("jsonObject", "" + jsonObject);
        viewMap = new HashMap<>();
        return jsonObject;
    }


    @Override
    public void beforeNetworkCall(int taskId) {
        if (taskId == ThreadTaskIds.GET_SURVEY) {
            progressBar.setVisibility(View.VISIBLE);
        } else if (taskId == ThreadTaskIds.SAVE_SURVEY) {
            progressDialog = new ProgressDialog(CMN_ViewSurveyActivity.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }

    @Override
    public Object afterNetworkCall(int taskId, Object o) {
        if (taskId == ThreadTaskIds.GET_SURVEY) {
            progressBar.setVisibility(View.GONE);
            invalidateOptionsMenu();
        } else if (taskId == ThreadTaskIds.SAVE_SURVEY) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
        return o;
    }

    @Override
    public Object onNetworkCall(int taskId, Object o) throws JsonParsingException, JsonParsingException {
        if (taskId == ThreadTaskIds.GET_SURVEY) {

            String url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                    + "GetViewData.aspx?token=" + CMN_Preferences.getUserToken(CMN_ViewSurveyActivity.this) + "&patientid=" + CMN_Preferences.getCurrentContextId(CMN_ViewSurveyActivity.this) + "&viewname=" + fields.getOpenUrl().split("&viewname=")[1];

            NetworkTools networkTools = NetworkTools.getInstance();
            if (networkTools.checkNetworkConnection(CMN_ViewSurveyActivity.this) && url.length() > 0) {
                JSONObject response = networkTools.getJsonData(CMN_ViewSurveyActivity.this, url);
                try {
                    if (response != null) {
                        parseJsonData(response);
                    }
                } catch (JSONException e) {
                    e.getMessage();
                }
            } else {
                Toast.makeText(CMN_ViewSurveyActivity.this, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
            }
        } else if (taskId == ThreadTaskIds.SAVE_SURVEY) {
            String url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                    + "UpdateViewData.aspx?token=" + CMN_Preferences.getUserToken(CMN_ViewSurveyActivity.this) + "&patientid=" + CMN_Preferences.getCurrentContextId(CMN_ViewSurveyActivity.this) + "&viewname=" + surveyModel.getViewname().toLowerCase() /* + "&jsondata=" + URLEncoder.encode("" + createResponseJson())*/;


            NetworkTools networkTools = NetworkTools.getInstance();
            if (networkTools.checkNetworkConnection(CMN_ViewSurveyActivity.this) && url.length() > 0) {
                final JSONObject response = networkTools.postJsonData(CMN_ViewSurveyActivity.this, url, createResponseJson());
                try {
                    if (response != null) {
                        Log.d("response", "" + response);
                        if (response.getJSONObject("Result").getInt("Code") == 0) {
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        new AlertDialog.Builder(CMN_ViewSurveyActivity.this)
                                                .setTitle(surveyModel.getViewname())
                                                .setMessage(response.getJSONObject("Result").getString("Message"))
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
                } catch (JSONException e) {
                    e.getMessage();
                }
            } else {
                Toast.makeText(CMN_ViewSurveyActivity.this, CMN_ErrorMessages.getInstance().getValue(12) + " : ", Toast.LENGTH_SHORT).show();
            }
        }
        return null;
    }

    private void parseJsonData(final JSONObject response) throws JSONException {
        if (response.getJSONObject("Result").getInt("Code") == 0 /*|| response.getJSONObject("Result").getString("Message").equalsIgnoreCase("SUCCESS")*/) {

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
                    generateView(surveyModel);
                    initDateTimePicker();
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

    private void generateView(CMN_GenericModel surveyModel) {

        if (surveyModel.getCategories().size() == 0) {
            return;
        }
        getActionBar().setTitle(surveyModel.getCategories().get(0).getDisplayName());

        ArrayList<CMN_GenericCategoriesModel> surveycategoriesModels = (ArrayList<CMN_GenericCategoriesModel>) surveyModel.getCategories();
        for (int i = 0; i < surveycategoriesModels.size(); i++) {
            CMN_GenericCategoriesModel surveycategoriesModel = surveycategoriesModels.get(i);
            LinearLayout parent = new LinearLayout(CMN_ViewSurveyActivity.this);
            parent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            parent.setOrientation(LinearLayout.VERTICAL);

            ArrayList<CMN_GenericFieldModel> surveyFieldModels = (ArrayList<CMN_GenericFieldModel>) surveycategoriesModel.getFields();
            for (int j = 0; j < surveyFieldModels.size(); j++) {
                CMN_GenericFieldModel surveyFieldModel = surveyFieldModels.get(j);
                parent.addView(CMN_GenerateViewComponents.getComponentGenerator().createComponent(CMN_ViewSurveyActivity.this, surveyFieldModel));

                View view = new View(CMN_ViewSurveyActivity.this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
                layoutParams.setMargins(0, 10, 0, 10);
                view.setLayoutParams(layoutParams);
                view.setBackgroundColor(Color.GRAY);

                parent.addView(view);


            }

            mainLayout.addView(parent);
        }
    }

    @Override
    public Object onNetworkError(int taskId, NetworkException o) {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(CMN_ViewSurveyActivity.this, "Some error occured, please try again later.", Toast.LENGTH_SHORT).show();
        return null;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_save);
        if (!shouldupdate) {
            item.setVisible(false);
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
                NetworkAsyncTask networkAsyncTask = NetworkAsyncTask.getInstance();
                try {
                    networkAsyncTask.startNetworkCall(ThreadTaskIds.SAVE_SURVEY, CMN_ViewSurveyActivity.this);
                } catch (NetworkException e) {
                    e.getMessage();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
                    dateTimePicker = (TextView) model.getView();
                }
            }
            if (model.getSurveyFieldModel().getType().equalsIgnoreCase(CMN_JsonConstants.ViewType.DateTimeYearPicker.toString())) {
                dateTimeYearModel = model.getSurveyFieldModel();
                if (model.getView() instanceof TextView) {
                    dateTimeYearPicker = (TextView) model.getView();
                }
            }
        }
        if (dateTimePicker != null) {
            dateTimePicker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final View dialogView = View.inflate(CMN_ViewSurveyActivity.this, R.layout.date_time_picker, null);
                    final AlertDialog alertDialog = new AlertDialog.Builder(CMN_ViewSurveyActivity.this).create();

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
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
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
        if (dateTimeYearPicker != null) {
            dateTimeYearPicker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final View dialogView = View.inflate(CMN_ViewSurveyActivity.this, R.layout.date_dlg, null);
                    final AlertDialog alertDialog = new AlertDialog.Builder(CMN_ViewSurveyActivity.this).create();

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
                            dateTimeYearPicker.setText(dateString);

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
