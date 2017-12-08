package com.coremobile.coreyhealth.providerreminders;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nitij on 12-05-2016.
 */
public class AddReminderInputDataActivityCMN extends CMN_AppBaseActivity implements NetworkCallBack {

    private EditText editText;
    private Intent intent;
    private String title = "";
    private ListView listView;
    private boolean islist = true;
    LinearLayout progressBar;
    private SharedPreferences mCurrentUserPref;
    public static final String CURRENT_USER = "CurrentUser";
    String organizationName, password, userName, context;
    private String[] times;
    List<ReminderFrequencyModel> reminderFrequencyModels;
    List<ReminderTypesModel> reminderTypesModels;
    List<ReminderStageModel> reminderStageModels;

    private RemindersTimesListViewAdapter remindersTimesListViewAdapter;
    private RemindersFrequencyAdapter remindersFrequencyAdapter;
    private RemindersTypesListAdapter reminderTypesAdapter;
    private RemindersStageAdapter remindersStageAdapter;

    private boolean isEpro = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addreminderinputdata);

        editText = (EditText) findViewById(R.id.inputdata);
        listView = (ListView) findViewById(R.id.listView);
        progressBar = (LinearLayout) findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);
        mCurrentUserPref = getSharedPreferences(CURRENT_USER, 0);
        organizationName = mCurrentUserPref.getString("Organization", null);
        userName = mCurrentUserPref.getString("Username", null);
        password = mCurrentUserPref.getString("Password", null);
        context = mCurrentUserPref.getString("context", null);

        ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle(
                "" + getResources().getString(R.string.addreminders));

        intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("Title")) {
                title = intent.getStringExtra("Title");
                getActionBar().setTitle(
                        "" + intent.getStringExtra("Title"));

                if (title.equalsIgnoreCase("Title") || title.equalsIgnoreCase("Message")) {
                    islist = false;
                }
            }

            if (intent.hasExtra("EproFrequency")) {
                isEpro = true;
            }

        }
        if (!islist) {
            listView.setVisibility(View.GONE);
            editText.setVisibility(View.VISIBLE);
        } else {
            listView.setVisibility(View.VISIBLE);
            editText.setVisibility(View.GONE);
        }


        if (title.equalsIgnoreCase("Title")) {
            editText.setText(intent.getStringExtra("data"));
        } else if (title.equalsIgnoreCase("Message")) {
            editText.setText(intent.getStringExtra("data"));
        } else if (title.equalsIgnoreCase("Type")) {
            NetworkAsyncTask networkAsyncTask = NetworkAsyncTask.getInstance();
            try {
                networkAsyncTask.startNetworkCall(ThreadTaskIds.GET_REMINDER_TYPE, AddReminderInputDataActivityCMN.this);
            } catch (NetworkException e) {
                e.getMessage();
            }
        } else if (title.equalsIgnoreCase("Stage")) {
            NetworkAsyncTask networkAsyncTask = NetworkAsyncTask.getInstance();
            try {
                networkAsyncTask.startNetworkCall(ThreadTaskIds.GET_REMINDER_STAGE, AddReminderInputDataActivityCMN.this);
            } catch (NetworkException e) {
                e.getMessage();
            }
        } else if (title.equalsIgnoreCase("Frequency")) {
            NetworkAsyncTask networkAsyncTask = NetworkAsyncTask.getInstance();
            try {
                networkAsyncTask.startNetworkCall(ThreadTaskIds.GET_REMINDER_FREQUENCY, AddReminderInputDataActivityCMN.this);
            } catch (NetworkException e) {
                e.getMessage();
            }
        } else if (title.equalsIgnoreCase("Times")) {
            String timesdata = intent.getStringExtra("data");
            String[] timesStringArray = timesdata.split(",");

            String[] strings = getResources().getStringArray(R.array.times_array);
            remindersTimesListViewAdapter = new RemindersTimesListViewAdapter(
                    this, strings, timesStringArray);
            listView.setAdapter(remindersTimesListViewAdapter);
        }


    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_save);
        item.setTitle("Done");
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
                Intent intent = new Intent();
                if (title.equalsIgnoreCase("Title")) {
                    if (editText.getText().toString().trim().length() > 0) {
                        intent.putExtra("data", "" + editText.getText().toString().trim());
                    } else {
                        Toast.makeText(AddReminderInputDataActivityCMN.this, CMN_ErrorMessages.getInstance().getValue(165), Toast.LENGTH_SHORT).show();
                        return false;
                    }
                } else if (title.equalsIgnoreCase("Message")) {
                    if (editText.getText().toString().trim().length() > 0) {
                        intent.putExtra("data", "" + editText.getText().toString().trim());
                    } else {
                        Toast.makeText(AddReminderInputDataActivityCMN.this, CMN_ErrorMessages.getInstance().getValue(165), Toast.LENGTH_SHORT).show();
                        return false;
                    }
                } else if (title.equalsIgnoreCase("Type")) {
                    if (reminderTypesAdapter.getData() != null) {
                        intent.putExtra("data", reminderTypesAdapter.getData());
                    } else {
                        Toast.makeText(AddReminderInputDataActivityCMN.this, CMN_ErrorMessages.getInstance().getValue(166), Toast.LENGTH_SHORT).show();
                        return false;
                    }
                } else if (title.equalsIgnoreCase("Stage")) {
                    if (remindersStageAdapter.getData() != null) {
                        intent.putExtra("data", (Serializable) remindersStageAdapter.getData());
                    } else {
                        Toast.makeText(AddReminderInputDataActivityCMN.this, CMN_ErrorMessages.getInstance().getValue(166), Toast.LENGTH_SHORT).show();
                        return false;
                    }
                } else if (title.equalsIgnoreCase("Frequency")) {
                    if (remindersFrequencyAdapter.getData() != null) {
                        intent.putExtra("data", remindersFrequencyAdapter.getData());
                    } else {
                        Toast.makeText(AddReminderInputDataActivityCMN.this, CMN_ErrorMessages.getInstance().getValue(166), Toast.LENGTH_SHORT).show();
                        return false;
                    }
                } else if (title.equalsIgnoreCase("Times")) {
                    if (remindersTimesListViewAdapter.getData().length > 0) {
                        times = remindersTimesListViewAdapter.getData();
                        if (times.length > 0) {
                            intent.putExtra("data", times);
                        }
                    } else {
                        Toast.makeText(AddReminderInputDataActivityCMN.this, CMN_ErrorMessages.getInstance().getValue(166), Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
                intent.putExtra("reminderTitle", title);
                setResult(RESULT_OK, intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void beforeNetworkCall(int taskId) {
        if (taskId == ThreadTaskIds.GET_REMINDER_TYPE || taskId == ThreadTaskIds.GET_REMINDER_STAGE || taskId == ThreadTaskIds.GET_REMINDER_FREQUENCY) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public Object afterNetworkCall(int taskId, Object o) {
        if (taskId == ThreadTaskIds.GET_REMINDER_TYPE || taskId == ThreadTaskIds.GET_REMINDER_STAGE || taskId == ThreadTaskIds.GET_REMINDER_FREQUENCY) {
            progressBar.setVisibility(View.GONE);
        }
        return null;
    }

    @Override
    public Object onNetworkCall(int taskId, Object o) throws JsonParsingException, JsonParsingException {

        if (taskId == ThreadTaskIds.GET_REMINDER_FREQUENCY) {
            String url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE;
            if (isEpro) {
                url = url + "getreminderfrequencies.aspx?token=" + CMN_Preferences.getUserToken(AddReminderInputDataActivityCMN.this) + "&category=EPROS";
            } else {
                url = url + "getreminderfrequencies.aspx?token=" + CMN_Preferences.getUserToken(AddReminderInputDataActivityCMN.this);
            }


            NetworkTools networkTools = NetworkTools.getInstance();
            if (networkTools.checkNetworkConnection(AddReminderInputDataActivityCMN.this) && url.length() > 0) {
                JSONObject response = networkTools.getJsonData(AddReminderInputDataActivityCMN.this, url);
                try {
                    if (response != null) {
                        parseJsonData(response, "Frequency");
                    }
                } catch (JSONException e) {
                    e.getMessage();
                }
            } else {
                Toast.makeText(AddReminderInputDataActivityCMN.this, CMN_ErrorMessages.getInstance().getValue(12), Toast.LENGTH_SHORT).show();
            }
        } else if (taskId == ThreadTaskIds.GET_REMINDER_TYPE) {

            String url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                    + "getremindertypes.aspx?token=" + CMN_Preferences.getUserToken(AddReminderInputDataActivityCMN.this);

            NetworkTools networkTools = NetworkTools.getInstance();
            if (networkTools.checkNetworkConnection(AddReminderInputDataActivityCMN.this) && url.length() > 0) {
                JSONObject response = networkTools.getJsonData(AddReminderInputDataActivityCMN.this, url);
                try {
                    if (response != null) {
                        parseJsonData(response, "Type");
                    }
                } catch (JSONException e) {
                    e.getMessage();
                }
            } else {
                Toast.makeText(AddReminderInputDataActivityCMN.this, CMN_ErrorMessages.getInstance().getValue(12), Toast.LENGTH_SHORT).show();
            }
        } else if (taskId == ThreadTaskIds.GET_REMINDER_STAGE) {

            String url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                    + "GetListItems.aspx?token=" + CMN_Preferences.getUserToken(AddReminderInputDataActivityCMN.this) + "&listname=patStages";

            NetworkTools networkTools = NetworkTools.getInstance();
            if (networkTools.checkNetworkConnection(AddReminderInputDataActivityCMN.this) && url.length() > 0) {
                JSONObject response = networkTools.getJsonData(AddReminderInputDataActivityCMN.this, url);
                try {
                    if (response != null) {
                        parseJsonData(response, "Stage");
                    }
                } catch (JSONException e) {
                    e.getMessage();
                }
            } else {
                Toast.makeText(AddReminderInputDataActivityCMN.this, CMN_ErrorMessages.getInstance().getValue(12), Toast.LENGTH_SHORT).show();
            }
        }

        return null;
    }

    @Override
    public Object onNetworkError(int taskId, NetworkException o) {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(AddReminderInputDataActivityCMN.this, CMN_ErrorMessages.getInstance().getValue(164), Toast.LENGTH_SHORT).show();
        return null;
    }

    private void parseJsonData(final JSONObject response, String value) throws JSONException {
        if (value.equalsIgnoreCase("Frequency")) {
            reminderFrequencyModels = new ArrayList<ReminderFrequencyModel>();
            JSONArray frequency = response.getJSONArray("Data");
            for (int i = 0; i < frequency.length(); i++) {
                JSONObject jsonObject = frequency.getJSONObject(i);
                ReminderFrequencyModel reminderFrequencyModel = new ReminderFrequencyModel();
                reminderFrequencyModel.setDisplaytext(jsonObject.getString("DisplayText"));
                reminderFrequencyModel.setValue(jsonObject.getString("Value"));
                reminderFrequencyModels.add(reminderFrequencyModel);
            }
            remindersFrequencyAdapter = new RemindersFrequencyAdapter(AddReminderInputDataActivityCMN.this, reminderFrequencyModels, intent.getStringExtra("data"));
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    listView.setAdapter(remindersFrequencyAdapter);
                }
            });

        } else if (value.equalsIgnoreCase("Type")) {
            reminderTypesModels = new ArrayList<ReminderTypesModel>();
            JSONArray reminderType = response.getJSONArray("reminderType");
            for (int i = 0; i < reminderType.length(); i++) {
                JSONObject jsonObject = reminderType.getJSONObject(i);
                ReminderTypesModel reminderTypesModel = new ReminderTypesModel();
                reminderTypesModel.setName(jsonObject.getString("name"));
                reminderTypesModel.setDescription(jsonObject.getString("description"));
                reminderTypesModel.setId(jsonObject.getInt("id"));
                reminderTypesModel.setImage(jsonObject.getString("imageURL"));
                reminderTypesModels.add(reminderTypesModel);
            }
            reminderTypesAdapter = new RemindersTypesListAdapter(AddReminderInputDataActivityCMN.this, reminderTypesModels, intent.getStringExtra("data"));
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    listView.setAdapter(reminderTypesAdapter);
                }
            });
        } else if (value.equalsIgnoreCase("Stage")) {
            reminderStageModels = new ArrayList<ReminderStageModel>();
            JSONObject jsonobject = response.getJSONObject("Data");
            JSONArray reminderType = jsonobject.getJSONArray("patStages");
            for (int i = 0; i < reminderType.length(); i++) {
                JSONObject jsonObject = reminderType.getJSONObject(i);
                ReminderStageModel reminderStageModel = new ReminderStageModel();
                reminderStageModel.setName(jsonObject.getString("Name"));
                reminderStageModel.setCode(jsonObject.getString("Code"));
                reminderStageModels.add(reminderStageModel);
            }
            remindersStageAdapter = new RemindersStageAdapter(AddReminderInputDataActivityCMN.this, reminderStageModels, intent.getStringExtra("data"));
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    listView.setAdapter(remindersStageAdapter);
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
