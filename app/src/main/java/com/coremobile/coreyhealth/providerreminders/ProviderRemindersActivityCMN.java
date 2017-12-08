package com.coremobile.coreyhealth.providerreminders;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.coremobile.coreyhealth.AsyncTaskCompleteListener;
import com.coremobile.coreyhealth.BaseAsyncTask;
import com.coremobile.coreyhealth.R;
import com.coremobile.coreyhealth.base.CMN_AppBaseActivity;
import com.coremobile.coreyhealth.errorhandling.CMN_ErrorMessages;
import com.coremobile.coreyhealth.googleAnalytics.CMN_Preferences;
import com.coremobile.coreyhealth.journal.CMN_AddJournalActivity;
import com.coremobile.coreyhealth.networkutils.JsonParsingException;
import com.coremobile.coreyhealth.networkutils.NetworkAsyncTask;
import com.coremobile.coreyhealth.networkutils.NetworkCallBack;
import com.coremobile.coreyhealth.networkutils.NetworkException;
import com.coremobile.coreyhealth.networkutils.NetworkTools;
import com.coremobile.coreyhealth.networkutils.ThreadTaskIds;
import com.coremobile.coreyhealth.newui.MessageTabActivityCMN;
import com.coremobile.coreyhealth.patientreminders.PatientRemindersAdapter;
import com.coremobile.coreyhealth.patientreminders.ReminderModel;
import com.coremobile.coreyhealth.patientreminders.ReminderTypesModel;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Nitij Katiyar
 */

public class ProviderRemindersActivityCMN extends CMN_AppBaseActivity implements NetworkCallBack, AsyncTaskCompleteListener<String> {

    private SharedPreferences mCurrentUserPref;
    public static final String CURRENT_USER = "CurrentUser";
    private String organizationName;
    String context, user_category, context_name;

    Intent intent;
    static ListView listView;
    com.coremobile.coreyhealth.patientreminders.ReminderTypesModel typesModel;
    TextView noMessages;
    private boolean isEdit = false;
    private boolean isUseForNewReminder = false;
    PatientRemindersAdapter adapter;
    String reminderIDs = "";
    ProgressDialog progressDialog;
    String reminderToDelete = "";

    AsyncTaskCompleteListener<String> asyncTaskCompleteListener;

    static RelativeLayout relativeLayout;
    Spinner spinnerVw;
    Button okBthVw, cancelBtnVw;
    static ImageView dimImageVw;
    static String vToken;
    public static String PATIENT_URL = null, SEND_URL = null;
    ArrayAdapter<String> spinnerDataAdapter;
    ProgressDialog progressDialog1;
    List<String> patientList = new ArrayList<>();
    List<String> patientIdList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patientreminderresponse);

        noMessages = (TextView) findViewById(R.id.noMessages);

        mCurrentUserPref = getSharedPreferences(CURRENT_USER, 0);
        organizationName = mCurrentUserPref.getString("Organization", null);
        user_category = mCurrentUserPref.getString("user_category", null);
        context = mCurrentUserPref.getString("context", null);
        context_name = mCurrentUserPref.getString("context_name", null);
        Log.d("patientId", "f..." + context);
        intent = getIntent();
        if (intent.hasExtra("dataValue")) {
            typesModel = (ReminderTypesModel) intent.getSerializableExtra("dataValue");
        }
        if (intent.hasExtra("isEdit")) {
            isEdit = true;
        } else if (intent.hasExtra("isUseForNewReminder")) {
            isUseForNewReminder = true;
        }


        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setTitle(
                "" + getResources().getString(R.string.reminders));
        getActionBar().setDisplayHomeAsUpEnabled(true);

        listView = (ListView) findViewById(R.id.listview);

        if (context.equalsIgnoreCase("") || context.isEmpty()) {
            context = CMN_Preferences.getCurrentContextId(ProviderRemindersActivityCMN.this);
        }

        relativeLayout = (RelativeLayout) findViewById(R.id.patientLayoutVw);
        dimImageVw = (ImageView) findViewById(R.id.dimImageVw);

        spinnerVw = (Spinner) findViewById(R.id.selectPatientSpinnerVw);
        okBthVw = (Button) findViewById(R.id.btnOkVw);
        cancelBtnVw = (Button) findViewById(R.id.btnCancelVw);
        asyncTaskCompleteListener = new ProviderRemindersActivityCMN();

        /**
         * PatientList fetch
         */
        vToken = CMN_Preferences.getUserToken(getApplicationContext());
        PATIENT_URL = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE + "GetAllPatients.aspx?token=" + vToken+"&all=false";
        SEND_URL = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE + "SendInstantMessage.aspx?token=" + vToken;

        NetworkTools networkTools = NetworkTools.getInstance();

        if (networkTools.checkNetworkConnection(ProviderRemindersActivityCMN.this)) {
            BaseAsyncTask baseAsyncTask = new BaseAsyncTask(getApplicationContext(), "GET", this, null);
            baseAsyncTask.execute(ProviderRemindersActivityCMN.PATIENT_URL);

        } else {
            Toast.makeText(getApplicationContext(), CMN_AddJournalActivity.NETWORK_MESSAGE, Toast.LENGTH_LONG).show();

            return;
        }

        spinnerDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, patientList);

        // Drop down layout style - list view with radio button
        spinnerDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinnerVw.setAdapter(spinnerDataAdapter);

        okBthVw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dimImageVw.setVisibility(View.GONE);
                relativeLayout.setVisibility(View.GONE);

                listView.setEnabled(true);
                JSONObject object = new JSONObject();
                try {

                    object.put("Id", eproId);
                    object.put("Context", patientIdList.get(spinnerVw.getSelectedItemPosition()));
                    object.put("Type", "REM");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                NetworkTools networkTools = NetworkTools.getInstance();


                if (networkTools.checkNetworkConnection(ProviderRemindersActivityCMN.this)) {
                    new ProviderRemindersActivityCMN.UploadDataAsyncTask(object.toString()).execute();
                } else {
                    Toast.makeText(getApplicationContext(), "Please check your internet connection.", Toast.LENGTH_SHORT).show();
                }
            }

        });
        cancelBtnVw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dimImageVw.setVisibility(View.GONE);
                relativeLayout.setVisibility(View.GONE);
                listView.setEnabled(true);

            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

        NetworkAsyncTask networkAsyncTask = NetworkAsyncTask.getInstance();
        try {
            networkAsyncTask.startNetworkCall(ThreadTaskIds.GET_REMINDER, ProviderRemindersActivityCMN.this);
        } catch (NetworkException e) {
            e.getMessage();
        }

        relativeLayout.setVisibility(View.GONE);
        dimImageVw.setVisibility(View.GONE);
        listView.setEnabled(true);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_save);
        if (!isUseForNewReminder) {
            item.setVisible(false);
        } else {
            item.setVisible(true);
        }
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
                if (adapter != null) {
                    Log.d("selectedReminderSize", "" + adapter.getData().size());
                    if (adapter.getData().size() > 0) {
                        for (int i = 0; i < adapter.getData().size(); i++) {
                            if (i > 0) {
                                reminderIDs = reminderIDs + "," + adapter.getData().get(i).getId();
                            } else {
                                reminderIDs = "" + adapter.getData().get(i).getId();
                            }
                        }
                        NetworkAsyncTask networkAsyncTask = NetworkAsyncTask.getInstance();
                        try {
                            networkAsyncTask.startNetworkCall(ThreadTaskIds.USE_EXISTING_REMINDER, ProviderRemindersActivityCMN.this);
                        } catch (NetworkException e) {
                            e.getMessage();
                        }
                        return true;
                    } else {
                        Toast.makeText(ProviderRemindersActivityCMN.this, CMN_ErrorMessages.getInstance().getValue(161), Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void beforeNetworkCall(int taskId) {
        progressDialog = new ProgressDialog(ProviderRemindersActivityCMN.this);
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
        if (taskId == ThreadTaskIds.USE_EXISTING_REMINDER) {
            String url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                    + "ApplyStandardReminderToPatient.aspx?token=" + CMN_Preferences.getUserToken(ProviderRemindersActivityCMN.this) + "&patientid=" + CMN_Preferences.getCurrentContextId(ProviderRemindersActivityCMN.this)
                    + "&remid=" + reminderIDs;


            NetworkTools networkTools = NetworkTools.getInstance();
            if (networkTools.checkNetworkConnection(ProviderRemindersActivityCMN.this) && url.length() > 0) {
                final JSONObject response = networkTools.getJsonData(ProviderRemindersActivityCMN.this, url);
                if (response != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                parseJsonData(response, ThreadTaskIds.USE_EXISTING_REMINDER);
                            } catch (JSONException e) {
                                e.getMessage();
                            }
                        }
                    });
                }
            } else {
                Toast.makeText(ProviderRemindersActivityCMN.this, CMN_ErrorMessages.getInstance().getValue(12), Toast.LENGTH_SHORT).show();

            }
        }
        if (taskId == ThreadTaskIds.GET_REMINDER) {
            String url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                    + "getreminders.aspx?token=" + CMN_Preferences.getUserToken(ProviderRemindersActivityCMN.this);
            if (!isUseForNewReminder) {
                url = url + "&patientid=" + CMN_Preferences.getCurrentContextId(ProviderRemindersActivityCMN.this);
            }
            url = url + "&type=All&current=All&stage=All";

            NetworkTools networkTools = NetworkTools.getInstance();
            if (networkTools.checkNetworkConnection(ProviderRemindersActivityCMN.this) && url.length() > 0) {
                final JSONObject response = networkTools.getJsonData(ProviderRemindersActivityCMN.this, url);

                if (response != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                parseJsonData(response, ThreadTaskIds.GET_REMINDER);
                            } catch (JSONException e) {
                                e.getMessage();
                            }
                        }
                    });
                }

            } else {
                Toast.makeText(ProviderRemindersActivityCMN.this, CMN_ErrorMessages.getInstance().getValue(12), Toast.LENGTH_SHORT).show();
            }
        }


        return null;
    }

    @Override
    public Object onNetworkError(int taskId, NetworkException o) {
        return null;
    }

    private void parseJsonData(JSONObject response, int Task) throws JSONException {
        if (Task == ThreadTaskIds.GET_REMINDER) {

            ArrayList<ReminderModel> reminderList = new ArrayList<ReminderModel>();
            JSONObject object = response.getJSONObject("reminders");
            JSONArray reminderarray = object.getJSONArray("reminder");
            for (int i = 0; i < reminderarray.length(); i++) {
                JSONObject jsonObject = reminderarray.getJSONObject(i);
                ReminderModel reminderModel = new ReminderModel();
                reminderModel.setId(jsonObject.getString("id"));
                reminderModel.setApplyByDefault(jsonObject.getString("applyByDefault"));

                JSONObject frequency = jsonObject.getJSONObject("frequency");
                reminderModel.setFrequanyColHeading(frequency.getString("colHeading"));
                reminderModel.setFrequencyData(frequency.getString("data"));

                JSONObject msgToPatient = jsonObject.getJSONObject("msgToPatient");
                reminderModel.setMsgToPatientColHeading(msgToPatient.getString("colHeading"));
                reminderModel.setMsgToPatientData(msgToPatient.getString("data"));

                JSONObject prescribedOn = jsonObject.getJSONObject("prescribedOn");
                reminderModel.setPrescribedOnColHeading(prescribedOn.getString("colHeading"));
                reminderModel.setPrescribedOnData(prescribedOn.getString("data"));

                JSONObject providerNotificationPolicy = jsonObject.getJSONObject("providerNotificationPolicy");
                reminderModel.setProviderNotificationPolicyColHeading(providerNotificationPolicy.getString("colHeading"));
                reminderModel.setProviderNotificationPolicyData(providerNotificationPolicy.getString("data"));

                JSONObject times = jsonObject.getJSONObject("times");
                reminderModel.setTimesColHeading(times.getString("colHeading"));
                reminderModel.setTimesData(times.getString("data"));

                JSONObject title = jsonObject.getJSONObject("title");
                reminderModel.setTitleColHeading(title.getString("colHeading"));
                reminderModel.setTitleData(title.getString("data"));

                JSONObject stage = jsonObject.getJSONObject("RemStage");
                reminderModel.setStageColHeading(stage.getString("colHeading"));
                reminderModel.setStageData(stage.getString("data"));

                JSONObject type = jsonObject.getJSONObject("typeid");
                reminderModel.setRemindertypeColHeading(type.getString("colHeading"));
                reminderModel.setRemindertypeData(type.getString("data"));

                JSONObject stoponsuccess = jsonObject.getJSONObject("stopOnSuccess");
                reminderModel.setStopOnSuccess(stoponsuccess.getBoolean("data"));

                reminderList.add(reminderModel);

                adapter = new PatientRemindersAdapter(ProviderRemindersActivityCMN.this,
                        reminderList, "", isEdit, isUseForNewReminder, true);
                listView.setAdapter(adapter);
                noMessages.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);

            }
//
        } else if (Task == ThreadTaskIds.USE_EXISTING_REMINDER) {
            if (response.getString("retCode").equalsIgnoreCase("0")) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(ProviderRemindersActivityCMN.this);
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
            }
        }

    }

    @Override
    public void onTaskComplete(String result) {

        parsePatientList(result);
        if (progressDialog1 != null && progressDialog1.isShowing()) {
            progressDialog1.dismiss();
        }
    }


    @Override
    public void onTaskStart() {
        progressDialog1 = new ProgressDialog(ProviderRemindersActivityCMN.this);
        progressDialog1.setMessage("Please wait...");
        progressDialog1.setCancelable(false);
        progressDialog1.show();
    }

    public Boolean parsePatientList(String str) {

        try {
            JSONObject jsonObject = new JSONObject(str);
            JSONObject jsonObject1 = jsonObject.getJSONObject("Result");
            if (jsonObject1.getString("Code").equalsIgnoreCase("0")) {

                JSONArray jsonArray = jsonObject.getJSONArray("Data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject2 = (JSONObject) jsonArray.get(i);
                    patientList.add(jsonObject2.getString("Name"));
                    patientIdList.add(jsonObject2.getString("Id"));

                }

                spinnerDataAdapter.notifyDataSetChanged();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return false;
    }


    static String eproId;

    public static void invokeActivityMethod(ReminderModel model) {
        dimImageVw.setVisibility(View.VISIBLE);
        relativeLayout.setVisibility(View.VISIBLE);
        listView.setEnabled(false);
        eproId = model.getId();

    }

    private class UploadDataAsyncTask extends AsyncTask<Void, Integer, String> {

        String noteText;

        UploadDataAsyncTask(String text) {

            noteText = text;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ProviderRemindersActivityCMN.this);
            progressDialog.setMessage("Processing..");
            progressDialog.setCancelable(false);
            progressDialog.show();

        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                return uploadData(noteText);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return "";
        }

        @SuppressWarnings("deprecation")
        private String uploadData(String postBody) throws UnsupportedEncodingException {

            Integer result = 0;
            String message = null;
            try {
                // Create Apache HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(SEND_URL);

                httppost.setEntity(new StringEntity(postBody, "UTF8"));
                httppost.setHeader("Content-type", "application/json");
                HttpResponse httpResponse = httpclient.execute(httppost);
                int statusCode = httpResponse.getStatusLine().getStatusCode();

                // 200 represents HTTP OK
                if (statusCode == 200) {
                    String response = streamToString(httpResponse.getEntity().getContent());
                    message = parseResult(response);
                } else {
                    message = "Failed";
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                return message;
            }

        }


        @Override
        protected void onPostExecute(String result) {
            Log.e("TAG", "Response from server: " + result);

            progressDialog.dismiss();
            // showing the server response in an alert dialog
            showAlert(result);

            /*saveButtonVw.setEnabled(true);
            */
            super.onPostExecute(result);
        }

    }


    /**
     * Method to show alert dialog
     */
    private void showAlert(String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(ProviderRemindersActivityCMN.this, android.R.style.Theme_Dialog));
        builder.setMessage(message).setTitle("Message")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    String streamToString(InputStream stream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
        String line;
        String result = "";
        while ((line = bufferedReader.readLine()) != null) {
            result += line;
        }
        // Close stream
        if (null != stream) {
            stream.close();
        }
        return result;
    }

    /**
     * Parsing the feed results and get the list
     *
     * @param result
     */
    private String parseResult(String result) {
        String message = null;
        try {
            JSONObject response = new JSONObject(result);

            JSONObject posts = response.getJSONObject("Result");
            message = posts.getString("Message");


        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            return message;
        }
    }

}
