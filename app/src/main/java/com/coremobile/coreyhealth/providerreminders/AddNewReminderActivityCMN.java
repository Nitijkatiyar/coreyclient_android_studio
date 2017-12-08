package com.coremobile.coreyhealth.providerreminders;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
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
import com.coremobile.coreyhealth.patientreminders.ReminderModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by nitij on 12-05-2016.
 */
public class AddNewReminderActivityCMN extends CMN_AppBaseActivity implements NetworkCallBack, View.OnClickListener {

    private static int TITLE = 1, MESSAGE = 2, STAGE = 3, TYPE = 4, FREQUENCY = 5, DATE = 6, TIME = 7;


    private LinearLayout reminderTitle, reminderMessage, reminderType, reminderStage, reminderFrequecy, reminderDate, reminderTime, togglelayout;
    private ToggleButton togglereminderStoponSuccess, togglereminderNotifynonCompliance, togglereminderActivateFuturePatient, togglereminderAddtoList;
    private TextView textViewTitle, textViewMessage, textViewType, textViewStage, textViewFrequecy, textViewDate, textViewTime;
    String
            timesSelected = "";
    String dateSelected="";
    private ReminderTypesModel reminderTypesModel;
    private List<ReminderStageModel> reminderStageModel = null;
    private ReminderFrequencyModel reminderFrequencyModel;

    String notifyCompliance = "None";

    private DatePicker datePicker;
    private Calendar calendar;
    private int year, month, day;
    private boolean istitleEmpty = true;
    private boolean ismessageEmpty = true;
    private boolean isfrequencyEmpty = true;
    private boolean istimeEmpty = true;
    private boolean istypeEmpty = true;
    private boolean isstageEmpty = true;
    private boolean isdateSelected = false;
    String[] times = null;
    Intent intent;
    ReminderModel reminderModel;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addnewreminder);


        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setTitle(
                "" + getResources().getString(R.string.addreminders));
        getActionBar().setDisplayHomeAsUpEnabled(true);


        reminderTitle = (LinearLayout) findViewById(R.id.layoutTitle);
        reminderMessage = (LinearLayout) findViewById(R.id.layoutMessage);
        reminderType = (LinearLayout) findViewById(R.id.layoutType);
        reminderStage = (LinearLayout) findViewById(R.id.layoutStage);
        reminderFrequecy = (LinearLayout) findViewById(R.id.layoutFrequency);
        reminderDate = (LinearLayout) findViewById(R.id.layoutDate);
        reminderTime = (LinearLayout) findViewById(R.id.layoutTimes);
        reminderTime = (LinearLayout) findViewById(R.id.layoutTimes);
        togglelayout = (LinearLayout) findViewById(R.id.togglelayout);

        textViewTitle = (TextView) findViewById(R.id.reminderTitle);
        textViewMessage = (TextView) findViewById(R.id.reminderMessage);
        textViewStage = (TextView) findViewById(R.id.reminderStage);
        textViewType = (TextView) findViewById(R.id.reminderType);
        textViewFrequecy = (TextView) findViewById(R.id.reminderFrequency);
        textViewDate = (TextView) findViewById(R.id.reminderDate);
        textViewTime = (TextView) findViewById(R.id.reminderTimes);

        togglereminderStoponSuccess = (ToggleButton) findViewById(R.id.toggelStoponSucess);
        togglereminderActivateFuturePatient = (ToggleButton) findViewById(R.id.toggelActivateforFuture);
        togglereminderAddtoList = (ToggleButton) findViewById(R.id.toggelAddforFuture);
        togglereminderNotifynonCompliance = (ToggleButton) findViewById(R.id.toggelNotifyCompliance);
        togglereminderNotifynonCompliance.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    notifyCompliance = "Noon";
                } else {
                    notifyCompliance = "None";
                }
            }
        });

        reminderDate.setEnabled(false);

        reminderTitle.setOnClickListener(this);
        reminderMessage.setOnClickListener(this);
        reminderType.setOnClickListener(this);
        reminderStage.setOnClickListener(this);
        reminderFrequecy.setOnClickListener(this);
        reminderDate.setOnClickListener(this);
        reminderTime.setOnClickListener(this);

        intent = getIntent();
        if (intent != null && intent.hasExtra("data")) {
            reminderModel = (ReminderModel) intent.getSerializableExtra("data");

            NetworkAsyncTask networkAsyncTask = NetworkAsyncTask.getInstance();
            try {
                networkAsyncTask.startNetworkCall(ThreadTaskIds.GET_REMINDER_TYPE, AddNewReminderActivityCMN.this);
            } catch (NetworkException e) {
                e.getMessage();
            }

            istypeEmpty = false;
            istimeEmpty = false;
            isstageEmpty = false;
            isfrequencyEmpty = false;
            ismessageEmpty = false;
            istitleEmpty = false;

            togglelayout.setVisibility(View.GONE);


        }
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
    }

    //    5\/13\/2016 12:30:07 AM
    private String convertDate(String s) {
        DateFormat originalFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aaa", Locale.ENGLISH);
        DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = originalFormat.parse(s);
        } catch (ParseException e) {
            e.getMessage();
        }
        String formattedDate = targetFormat.format(date);
        return formattedDate;
    }

    @Override
    public void onClick(View v) {
        if (v.equals(reminderTitle)) {
            Intent intent = new Intent();
            intent.setClassName(getPackageName(), ActivityPackage.AddReminderInputDataActivity);
            intent.putExtra("Title", "Title");
            intent.putExtra("data", textViewTitle.getText().toString().trim());
            startActivityForResult(intent, TITLE);
        } else if (v.equals(reminderMessage)) {
            Intent intent = new Intent();
            intent.setClassName(getPackageName(), ActivityPackage.AddReminderInputDataActivity);
            intent.putExtra("Title", "Message");
            intent.putExtra("data", textViewMessage.getText().toString().trim());
            startActivityForResult(intent, MESSAGE);
        } else if (v.equals(reminderType)) {
            Intent intent = new Intent();
            intent.setClassName(getPackageName(), ActivityPackage.AddReminderInputDataActivity);
            intent.putExtra("Title", "Type");
            intent.putExtra("data", textViewType.getText().toString().trim());
            startActivityForResult(intent, TYPE);
        } else if (v.equals(reminderStage)) {
            Intent intent = new Intent();
            intent.setClassName(getPackageName(), ActivityPackage.AddReminderInputDataActivity);
            intent.putExtra("Title", "Stage");
            intent.putExtra("data", textViewStage.getText().toString().trim());
            startActivityForResult(intent, STAGE);
        } else if (v.equals(reminderFrequecy)) {
            Intent intent = new Intent();
            intent.setClassName(getPackageName(), ActivityPackage.AddReminderInputDataActivity);
            intent.putExtra("Title", "Frequency");
            intent.putExtra("data", textViewFrequecy.getText().toString().trim());
            startActivityForResult(intent, FREQUENCY);
        } else if (v.equals(reminderDate)) {
            showDialog(1);
        } else if (v.equals(reminderTime)) {
            Intent intent = new Intent();
            intent.setClassName(getPackageName(), ActivityPackage.AddReminderInputDataActivity);
            intent.putExtra("Title", "Times");
            intent.putExtra("data", textViewTime.getText().toString().trim());
            startActivityForResult(intent, TIME);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TITLE && resultCode == RESULT_OK) {
            textViewTitle.setText("");
            textViewTitle.setText(data.getStringExtra("data"));
            istitleEmpty = false;
        } else if (requestCode == MESSAGE && resultCode == RESULT_OK) {
            textViewMessage.setText("");
            textViewMessage.setText(data.getStringExtra("data"));
            ismessageEmpty = false;
        } else if (requestCode == FREQUENCY && resultCode == RESULT_OK) {
            textViewFrequecy.setText("");
            reminderFrequencyModel = (ReminderFrequencyModel) data.getSerializableExtra("data");
            textViewFrequecy.setText(reminderFrequencyModel.getDisplaytext());
            if (reminderFrequencyModel.getValue().equalsIgnoreCase("SpecificDate")) {
                reminderDate.setEnabled(true);
                isdateSelected = true;
            } else {
                isdateSelected = false;
                reminderDate.setEnabled(false);
                textViewDate.setText("");
                dateSelected = "";
            }
            isfrequencyEmpty = false;
        } else if (requestCode == STAGE && resultCode == RESULT_OK) {
            textViewStage.setText("");
            reminderStageModel = (List<ReminderStageModel>) data.getSerializableExtra("data");
            for (int i = 0; i < reminderStageModel.size(); i++) {
                if (i == reminderStageModel.size() - 1) {
                    textViewStage.setText("" + textViewStage.getText().toString() + reminderStageModel.get(i).getName());
                } else {
                    textViewStage.setText("" + textViewStage.getText().toString() + reminderStageModel.get(i).getName() + ",");
                }
            }
            isstageEmpty = false;
        } else if (requestCode == TIME && resultCode == RESULT_OK) {
            textViewTime.setText("");
            times = data.getStringArrayExtra("data");
            for (int i = 0; i < times.length; i++) {

                Date date = null;
                if (isdateSelected) {
                    try {
                        date = new SimpleDateFormat("yyyy-MM-dd").parse(textViewDate.getText().toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    date = new Date();
                }
                String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
                Log.e("currentDate", "" + currentDate);
                String dateTemp = currentDate + "_" + times[i];
                Log.e("dateTemp", "" + dateTemp);
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd_hh:mm a");
                Date date1 = null;
                try {
                    date1 = outputFormat.parse(dateTemp);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                outputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                String newDat = outputFormat.format(date1);
                if (isdateSelected) {
                    dateSelected = newDat.split("_")[0];
                } else {
                    dateSelected = "";
                }
                if (i == times.length - 1) {
                    timesSelected = timesSelected + "" + newDat.split("_")[1];
                    textViewTime.setText("" + textViewTime.getText().toString() + times[i]);
                } else {
                    timesSelected = timesSelected + "" + newDat.split("_")[1] + ",";
                    textViewTime.setText("" + textViewTime.getText().toString() + times[i] + ",");
                }

            }
            istimeEmpty = false;
        } else if (requestCode == TYPE && resultCode == RESULT_OK) {
            textViewType.setText("");
            reminderTypesModel = (ReminderTypesModel) data.getSerializableExtra("data");
            textViewType.setText(reminderTypesModel.getName());
            istypeEmpty = false;
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 1) {
            return new DatePickerDialog(this, myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            year = arg1;
            month = arg2;
            day = arg3;
            String monthString = "" + month, dayString = "" + day;
            if ((month + 1) < 10) {
                monthString = "0" + (month + 1);
            }
            if (day < 10) {
                dayString = "0" + day;
            }
            textViewDate.setText(new StringBuilder().append(year).append("-")
                    .append(monthString).append("-").append(dayString));
            isdateSelected = true;
        }
    };

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

                if (istypeEmpty || istimeEmpty || isstageEmpty || isfrequencyEmpty || ismessageEmpty || istitleEmpty) {
                    Toast.makeText(AddNewReminderActivityCMN.this, CMN_ErrorMessages.getInstance().getValue(163), Toast.LENGTH_SHORT).show();
                    return false;
                }
                NetworkAsyncTask networkAsyncTask = NetworkAsyncTask.getInstance();
                try {
                    networkAsyncTask.startNetworkCall(ThreadTaskIds.SAVE_REMINDER, AddNewReminderActivityCMN.this);
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
        progressDialog = new ProgressDialog(AddNewReminderActivityCMN.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

    }

    @Override
    public Object afterNetworkCall(int taskId, Object o) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        return null;
    }

    @Override
    public Object onNetworkCall(int taskId, Object o) throws JsonParsingException, JsonParsingException {
        if (taskId == ThreadTaskIds.SAVE_REMINDER) {

            String remFrequency = "";
            if (reminderFrequencyModel != null) {
                remFrequency = reminderFrequencyModel.getValue();
            } else {
                remFrequency = textViewFrequecy.getText().toString();
            }
            String reminderData = "&remTitle=" + textViewTitle.getText().toString().trim()
                    + "&remMsg=" + textViewMessage.getText().toString().trim()
                    + "&remStage=" + textViewStage.getText().toString()
                    + "&specificDateVal=" + dateSelected.trim()
                    + "&remFreq=" + remFrequency
                    + "&remFreqTime=" + timesSelected.trim()
                    + "&provNotificationPolicy=" + notifyCompliance
                    + "&isDefault=" + togglereminderActivateFuturePatient.isChecked()
                    + "&addToGlobal=" + togglereminderAddtoList.isChecked()
                    + "&stopOnSuccess=" + togglereminderStoponSuccess.isChecked();
            String url = null;
            String reminderId = "";
            if (intent.hasExtra("data")) {
                reminderId = "&remid=" + reminderModel.getId();
            }
            url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                    + "SavePatientReminder.aspx?token=" + CMN_Preferences.getUserToken(AddNewReminderActivityCMN.this) + "&patientid=" + CMN_Preferences.getCurrentContextId(AddNewReminderActivityCMN.this) + "&type=" + reminderTypesModel.getId() + "" + reminderId
                    + reminderData.replace(" ", "%20")/*URLEncoder.encode(reminderData, "UTF-8")*/;

            NetworkTools networkTools = NetworkTools.getInstance();
            if (networkTools.checkNetworkConnection(AddNewReminderActivityCMN.this) && url.length() > 0) {
                final JSONObject response = networkTools.getJsonData(AddNewReminderActivityCMN.this, url);

                if (response != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                parseJsonData(response, ThreadTaskIds.SAVE_REMINDER);
                            } catch (JSONException e) {
                                e.getMessage();
                            }
                        }
                    });
                }

            } else {
                Toast.makeText(AddNewReminderActivityCMN.this, CMN_ErrorMessages.getInstance().getValue(12), Toast.LENGTH_SHORT).show();
            }
        } else if (taskId == ThreadTaskIds.GET_REMINDER_TYPE) {

            String url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                    + "getremindertypes.aspx?token=" + CMN_Preferences.getUserToken(AddNewReminderActivityCMN.this);

            NetworkTools networkTools = NetworkTools.getInstance();
            if (networkTools.checkNetworkConnection(AddNewReminderActivityCMN.this) && url.length() > 0) {
                final JSONObject response = networkTools.getJsonData(AddNewReminderActivityCMN.this, url);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (response != null) {
                                parseJsonData(response, ThreadTaskIds.GET_REMINDER_TYPE);
                            }
                        } catch (JSONException e) {
                            e.getMessage();
                        }
                    }
                });

            } else {
                Toast.makeText(AddNewReminderActivityCMN.this, CMN_ErrorMessages.getInstance().getValue(12), Toast.LENGTH_SHORT).show();
            }
        }
        return null;
    }

    private void parseJsonData(JSONObject response, int type) throws JSONException {
//        {"message":"Reminder successfully saved.","retCode":"0"}
        if (type == ThreadTaskIds.SAVE_REMINDER) {
            if (response.getString("retCode").equalsIgnoreCase("0")) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(AddNewReminderActivityCMN.this);
                builder1.setTitle("Reminder");
                builder1.setMessage(response.getString("message"));
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
            } else {
                Toast.makeText(AddNewReminderActivityCMN.this, response.getString("message"), Toast.LENGTH_SHORT).show();
            }
        } else if (type == ThreadTaskIds.GET_REMINDER_TYPE) {
//            List<ReminderTypesModel> reminderTypesModels = new ArrayList<ReminderTypesModel>();
            JSONArray reminderType = response.getJSONArray("reminderType");
            for (int i = 0; i < reminderType.length(); i++) {
                JSONObject jsonObject = reminderType.getJSONObject(i);
                ReminderTypesModel remindertypesmodel = new ReminderTypesModel();
                remindertypesmodel.setName(jsonObject.getString("name"));
                remindertypesmodel.setDescription(jsonObject.getString("description"));
                remindertypesmodel.setId(jsonObject.getInt("id"));
                remindertypesmodel.setImage(jsonObject.getString("imageURL"));
                if (reminderModel.getRemindertypeData().equalsIgnoreCase("" + jsonObject.getInt("id"))) {
                    textViewType.setText(jsonObject.getString("name"));
                    reminderTypesModel = remindertypesmodel;
                }

//                reminderTypesModels.add(reminderTypesModel);
            }
            textViewTitle.setText(reminderModel.getTitleData());
            textViewMessage.setText(reminderModel.getMsgToPatientData());
            textViewFrequecy.setText(reminderModel.getFrequencyData());
            textViewStage.setText(reminderModel.getStageData());

            textViewTime.setText(reminderModel.getTimesData());
            textViewDate.setText("" + convertDate("" + reminderModel.getPrescribedOnData()));

            togglereminderStoponSuccess.setChecked(reminderModel.isStopOnSuccess());
            notifyCompliance = reminderModel.getProviderNotificationPolicyData();
            if (reminderModel.getProviderNotificationPolicyData().equalsIgnoreCase("Noon")) {
                togglereminderNotifynonCompliance.setChecked(true);
            } else if (reminderModel.getProviderNotificationPolicyData().equalsIgnoreCase("None")) {
                togglereminderNotifynonCompliance.setChecked(false);
            }


        }
    }

    @Override
    public Object onNetworkError(int taskId, NetworkException o) {
        return null;
    }


}
