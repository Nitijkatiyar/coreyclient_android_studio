package com.coremobile.coreyhealth.native_epro;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.coremobile.coreyhealth.providerreminders.ReminderFrequencyModel;
import com.coremobile.coreyhealth.providerreminders.ReminderStageModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by nitij on 05-01-2017.
 */
public class CMN_AddNewEproActivity extends CMN_AppBaseActivity implements View.OnClickListener, NetworkCallBack {

    LinearLayout _messageLayout, _timeLayout, _stageLayout, _frequencyLayout, _noofoptionLayout, _dependenteprosLayout;
    TextView _message, _times, _frequency, _stage, _noofoption, _notifySelection, _dependentepros;
    ToggleButton _enableFreeText, _enableProviderNotification;
    EditText _freetext;
    RadioGroup __enableProviderNotificationRadioGroup;
    RadioButton __enableProviderNotificationYes, __enableProviderNotificationNo;
    int optionCount = 2;

    private static int OPTION_VALUES = 1, MESSAGE = 2, STAGE = 3, TYPE = 4, FREQUENCY = 5, DEPENDENCY = 6, TIME = 7;

    private List<ReminderStageModel> reminderStageModel = null;
    private ReminderFrequencyModel reminderFrequencyModel;

    String[] times = null;
    Intent intent;

    CMN_eproModel model;
    boolean isEdit = false;

    ProgressDialog progressDialog;

    public static List<CMN_ePROsDependentModel> dependentModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addnewepro);

        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setTitle(
                "" + getResources().getString(R.string.addepro));
        getActionBar().setDisplayHomeAsUpEnabled(true);

        _messageLayout = (LinearLayout) findViewById(R.id.epro_new_layoutMessage);
        _frequencyLayout = (LinearLayout) findViewById(R.id.epro_new_layoutFrequency);
        _noofoptionLayout = (LinearLayout) findViewById(R.id.epro_new_layoutNumberOfOptions);
        _stageLayout = (LinearLayout) findViewById(R.id.epro_new_layoutStage);
        _timeLayout = (LinearLayout) findViewById(R.id.epro_new_layoutTime);
        _dependenteprosLayout = (LinearLayout) findViewById(R.id.epro_new_layoutDepedentEpros);

        _message = (TextView) findViewById(R.id.epro_new_message);
        _frequency = (TextView) findViewById(R.id.epro_new_frequency);
        _noofoption = (TextView) findViewById(R.id.epro_new_numberofoptions);
        _stage = (TextView) findViewById(R.id.epro_new_stage);
        _times = (TextView) findViewById(R.id.epro_new_time);
        _notifySelection = (TextView) findViewById(R.id.epro_new_notification_selection);
        _dependentepros = (TextView) findViewById(R.id.epro_new_dependency);

        _freetext = (EditText) findViewById(R.id.epro_new_edittextfreetext);
        _freetext.clearFocus();

        dependentModels = new ArrayList<>();

        __enableProviderNotificationNo = (RadioButton) findViewById(R.id.epro_new_notifyonselection_no);
        __enableProviderNotificationYes = (RadioButton) findViewById(R.id.epro_new_notifyonselection_yes);

        __enableProviderNotificationRadioGroup = (RadioGroup) findViewById(R.id.epro_new_notifyonselection);

        _enableFreeText = (ToggleButton) findViewById(R.id.epro_new_toggelenablefreetext);
        _enableProviderNotification = (ToggleButton) findViewById(R.id.epro_new_toggelenableprovidernotification);

        _messageLayout.setOnClickListener(this);
        _frequencyLayout.setOnClickListener(this);
        _noofoptionLayout.setOnClickListener(this);
        _stageLayout.setOnClickListener(this);
        _timeLayout.setOnClickListener(this);
        _dependenteprosLayout.setOnClickListener(this);

        _enableFreeText.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    _freetext.setVisibility(View.VISIBLE);
                } else {
                    _freetext.setVisibility(View.GONE);
                }
            }
        });
        _enableProviderNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    __enableProviderNotificationRadioGroup.setVisibility(View.VISIBLE);
                    _notifySelection.setVisibility(View.VISIBLE);
                } else {
                    __enableProviderNotificationRadioGroup.setVisibility(View.GONE);
                    _notifySelection.setVisibility(View.GONE);
                }
            }
        });


        intent = getIntent();
        if (intent != null && intent.hasExtra("dataModel")) {
            isEdit = true;
            model = (CMN_eproModel) intent.getSerializableExtra("dataModel");

            if (model.isHasFreeText()) {
                _enableFreeText.setChecked(true);
                _freetext.setVisibility(View.VISIBLE);
                _freetext.setText("" + model.getFreeTextTitle());
            } else {
                _enableFreeText.setChecked(false);
                _freetext.setVisibility(View.GONE);
            }
            if (model.isNotifyProvider()) {
                _enableProviderNotification.setChecked(true);
                __enableProviderNotificationRadioGroup.setVisibility(View.VISIBLE);
            } else {
                _enableProviderNotification.setChecked(false);
                __enableProviderNotificationRadioGroup.setVisibility(View.GONE);
            }
            _message.setText(model.getQuestion());
            _frequency.setText(model.getReminderFreqDisplayName());
            _noofoption.setText(model.getOptions().toString().replaceAll("[\\s\\[\\]]", ""));
            _stage.setText(model.getStage());
            _times.setText(model.getReminderTime());
            _dependentepros.setText(model.getePROsDependentModels().size() + " Dependency");
            dependentModels = model.getePROsDependentModels();
            if (model.getProviderNotificationOptions().equalsIgnoreCase("1")) {
                __enableProviderNotificationYes.setChecked(true);
            } else {
                __enableProviderNotificationNo.setChecked(true);
            }
        }

    }

    @Override
    public void onClick(View v) {
        if (v.equals(_messageLayout)) {
            Intent intent = new Intent();
            intent.setClassName(getPackageName(), ActivityPackage.AddReminderInputDataActivity);
            intent.putExtra("Title", "Message");
            intent.putExtra("data", _message.getText().toString().trim());
            startActivityForResult(intent, MESSAGE);
        } else if (v.equals(_frequencyLayout)) {
            Intent intent = new Intent();
            intent.setClassName(getPackageName(), ActivityPackage.AddReminderInputDataActivity);
            intent.putExtra("Title", "Frequency");
            intent.putExtra("EproFrequency",true);
            intent.putExtra("data", _frequency.getText().toString().trim());
            startActivityForResult(intent, FREQUENCY);
        } else if (v.equals(_noofoptionLayout)) {
            Intent intent = new Intent();
            intent.setClassName(getPackageName(), ActivityPackage.Epro_number_of_options);
            if (model != null) {
                intent.putExtra("numberofoptions", Integer.parseInt(model.getOptionsCount()));
            }
            intent.putExtra("data", _noofoption.getText().toString().trim());
            startActivityForResult(intent, OPTION_VALUES);
        } else if (v.equals(_stageLayout)) {
            Intent intent = new Intent();
            intent.setClassName(getPackageName(), ActivityPackage.AddReminderInputDataActivity);
            intent.putExtra("Title", "Stage");
            intent.putExtra("data", _stage.getText().toString().trim());
            startActivityForResult(intent, STAGE);
        } else if (v.equals(_timeLayout)) {
            Intent intent = new Intent();
            intent.setClassName(getPackageName(), ActivityPackage.AddReminderInputDataActivity);
            intent.putExtra("Title", "Times");
            intent.putExtra("data", _times.getText().toString().trim());
            startActivityForResult(intent, TIME);
        } else if (v.equals(_dependenteprosLayout)) {
            if (CMN_EproListActivity.eproModels.size() > 0) {
                Intent intent = new Intent();
                intent.setClassName(getPackageName(), ActivityPackage.DependentePROsActivity);
                intent.putExtra("Title", "Select Dependency");
                intent.putExtra("isEdit", isEdit);
                startActivityForResult(intent, DEPENDENCY);
            } else {
                Toast.makeText(CMN_AddNewEproActivity.this, "There are no ePROs available.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MESSAGE && resultCode == RESULT_OK) {
            _message.setText("");
            _message.setText(data.getStringExtra("data"));
        } else if (requestCode == FREQUENCY && resultCode == RESULT_OK) {
            _frequency.setText("");
            reminderFrequencyModel = (ReminderFrequencyModel) data.getSerializableExtra("data");
            _frequency.setText(reminderFrequencyModel.getDisplaytext());
        } else if (requestCode == STAGE && resultCode == RESULT_OK) {
            _stage.setText("");
            reminderStageModel = (List<ReminderStageModel>) data.getSerializableExtra("data");
            for (int i = 0; i < reminderStageModel.size(); i++) {
                if (i == reminderStageModel.size() - 1) {
                    _stage.setText("" + _stage.getText().toString() + reminderStageModel.get(i).getName());
                } else {
                    _stage.setText("" + _stage.getText().toString() + reminderStageModel.get(i).getName() + ",");
                }
            }
        } else if (requestCode == TIME && resultCode == RESULT_OK) {
            _times.setText("");
            times = data.getStringArrayExtra("data");
            for (int i = 0; i < times.length; i++) {
                if (i == times.length - 1) {
                    _times.setText("" + _times.getText().toString() + times[i]);
                } else {
                    _times.setText("" + _times.getText().toString() + times[i] + ",");
                }
            }
        } else if (requestCode == OPTION_VALUES && resultCode == RESULT_OK) {
            _noofoption.setText(data.getStringExtra("optionsValue"));
            optionCount = data.getIntExtra("optionsCount", 2);
        } else if (requestCode == DEPENDENCY && resultCode == RESULT_OK) {
            _dependentepros.setText(dependentModels.size() + " Depedency");
        }
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
                    networkAsyncTask.startNetworkCall(ThreadTaskIds.SAVE_EPROS, CMN_AddNewEproActivity.this);
                } catch (NetworkException e) {
                    e.getMessage();
                }

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private JSONObject generateJson() {


        JSONObject jsonObject = new JSONObject();
        try {
            if (isEdit) {
                jsonObject.put("ID", "" + model.getID());
            } else {
                jsonObject.put("ID", "-1");
            }
            if (reminderFrequencyModel != null) {
                jsonObject.put("Frequency", reminderFrequencyModel.getValue());
            } else {
                jsonObject.put("Frequency", model.getFrequency());
            }
            jsonObject.put("TimeOfEvent", "" + _times.getText().toString());
            jsonObject.put("Question", "" + _message.getText().toString().trim());
            jsonObject.put("FreeTextTitle", "" + _freetext.getText().toString());
            jsonObject.put("HasFreeText", _enableFreeText.isChecked());
            jsonObject.put("Stage", "" + _stage.getText().toString());
            jsonObject.put("NotifyProvider", _enableProviderNotification.isChecked());
            jsonObject.put("OptionsCount", "" + optionCount);

            List<String> items = Arrays.asList(_noofoption.getText().toString().split("\\s*,\\s*"));
            JSONArray jsonArray = new JSONArray();
            for (int m = 0; m < items.size(); m++) {
                jsonArray.put(items.get(m));
            }
            jsonObject.put("Options", jsonArray);
            switch (__enableProviderNotificationRadioGroup.getCheckedRadioButtonId()) {
                case R.id.epro_new_notifyonselection_yes:
                    jsonObject.put("ProviderNotificationOptions", 1);
                    break;
                case R.id.epro_new_notifyonselection_no:
                    jsonObject.put("ProviderNotificationOptions", 2);
                    break;
            }

            JSONArray array = new JSONArray();
            for (int i = 0; i < dependentModels.size(); i++) {
                JSONObject object = new JSONObject();
                object.put("id", dependentModels.get(i).getId());
                object.put("DependentResponseOption", dependentModels.get(i).getDependentResponseOption());
                object.put("Question", dependentModels.get(i).getQuestion());
                array.put(object);
            }
            jsonObject.put("Dependencies", array);

        } catch (JSONException e) {
            e.getMessage();
        }
        return jsonObject;
    }

    @Override
    public void beforeNetworkCall(int taskId) {
        progressDialog = new ProgressDialog(CMN_AddNewEproActivity.this);
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
        if (taskId == ThreadTaskIds.SAVE_EPROS) {
            String url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                    + "SaveEPRO.aspx?token=" + CMN_Preferences.getUserToken(CMN_AddNewEproActivity.this);


            NetworkTools networkTools = NetworkTools.getInstance();
            if (networkTools.checkNetworkConnection(CMN_AddNewEproActivity.this) && url.length() > 0) {
                JSONObject response = networkTools.postJsonData(CMN_AddNewEproActivity.this, url, generateJson());
                if (response != null) {
                    try {
                        parseJsonData(response, taskId);
                    } catch (JSONException e) {
                        e.getMessage();
                    }
                }
            } else {
                Toast.makeText(CMN_AddNewEproActivity.this, CMN_ErrorMessages.getInstance().getValue(12), Toast.LENGTH_SHORT).show();
            }
        }
        return o;
    }

    @Override
    public Object onNetworkError(int taskId, NetworkException o) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        return null;
    }


    private void parseJsonData(final JSONObject response, int taskId) throws JSONException {
        if (taskId == ThreadTaskIds.SAVE_EPROS) {
            if (response.getJSONObject("Result").getInt("Code") == 0) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                        try {
                            new AlertDialog.Builder(CMN_AddNewEproActivity.this)
                                    .setTitle("Add ePRO")
                                    .setMessage(response.getJSONObject("Result").getString("Message"))
                                    .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            CMN_Preferences.setePROadded(CMN_AddNewEproActivity.this, true);
                                            finish();
                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_input_add)
                                    .show();
                        } catch (JSONException e) {
                            e.getMessage();
                        }
                    }
                });
            } else {
                Toast.makeText(CMN_AddNewEproActivity.this, response.getJSONObject("Result").getString("Message"), Toast.LENGTH_SHORT).show();
            }
        }

    }

}
