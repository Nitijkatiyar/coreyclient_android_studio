package com.coremobile.coreyhealth.patientreminders;

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
import android.widget.AdapterView;
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
import com.coremobile.coreyhealth.Utils;
import com.coremobile.coreyhealth.base.CMN_AppBaseActivity;
import com.coremobile.coreyhealth.googleAnalytics.CMN_Preferences;
import com.coremobile.coreyhealth.journal.CMN_AddJournalActivity;
import com.coremobile.coreyhealth.networkutils.NetworkTools;
import com.coremobile.coreyhealth.newui.MessageTabActivityCMN;

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
public class RemindersActivityCMN extends CMN_AppBaseActivity implements AsyncTaskCompleteListener<String> {

    private SharedPreferences mCurrentUserPref;
    public static final String CURRENT_USER = "CurrentUser";
    private String organizationName;
    String context, user_category, context_name;

    Intent intent;
    static ListView listView;
    ReminderTypesModel typesModel;
    boolean Patient = false;
    TextView noMessages;

    AsyncTaskCompleteListener<String> asyncTaskCompleteListener;

    static RelativeLayout relativeLayout;
    Spinner spinnerVw;
    Button okBthVw, cancelBtnVw;
    static ImageView dimImageVw;
    static String vToken;
    public static String PATIENT_URL = null, SEND_URL = null;
    ArrayAdapter<String> spinnerDataAdapter;
    ProgressDialog progressDialog, progressDialog1;
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

        relativeLayout = (RelativeLayout) findViewById(R.id.patientLayoutVw);
        dimImageVw = (ImageView) findViewById(R.id.dimImageVw);

        spinnerVw = (Spinner) findViewById(R.id.selectPatientSpinnerVw);
        okBthVw = (Button) findViewById(R.id.btnOkVw);
        cancelBtnVw = (Button) findViewById(R.id.btnCancelVw);
        asyncTaskCompleteListener = new RemindersActivityCMN();


        intent = getIntent();
        if (intent.hasExtra("dataValue")) {
            typesModel = (ReminderTypesModel) intent.getSerializableExtra("dataValue");
        }

        if (user_category.equalsIgnoreCase("patient")) {
            Patient = true;
        } else {
            Patient = false;
        }
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setTitle(
                "" + getResources().getString(R.string.reminders));
        getActionBar().setDisplayHomeAsUpEnabled(true);

        listView = (ListView) findViewById(R.id.listview);

        if (Utils.checkInternetConnection()) {
            GetRemindersWebService allMessages = new GetRemindersWebService(
                    RemindersActivityCMN.this, listView, noMessages);
            allMessages.execute(context, "" + typesModel.getId());
        } else {
        }

        /**
         * PatientList fetch
         */
        vToken = CMN_Preferences.getUserToken(getApplicationContext());
        PATIENT_URL = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE + "GetAllPatientsInOrg.aspx?token=" + vToken;
        SEND_URL = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE + "SendInstantMessage.aspx?token=" + vToken;

        NetworkTools networkTools = NetworkTools.getInstance();

        if (networkTools.checkNetworkConnection(RemindersActivityCMN.this)) {
            BaseAsyncTask baseAsyncTask = new BaseAsyncTask(getApplicationContext(), "GET", this, null);
            baseAsyncTask.execute(RemindersActivityCMN.PATIENT_URL);

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


                if (networkTools.checkNetworkConnection(RemindersActivityCMN.this)) {
                    new UploadDataAsyncTask(object.toString()).execute();
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(RemindersActivityCMN.this,
                        PatientResponseActivityCMN.class);
                intent.putExtra("dataValue", GetRemindersWebService.reminderList.get(position));
                intent.putExtra("typeId", typesModel.getId());
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        relativeLayout.setVisibility(View.GONE);
        dimImageVw.setVisibility(View.GONE);
        listView.setEnabled(true);
    }

    @Override
    public void onTaskComplete(String result) {

        parsePatientList(result);
        if (progressDialog1 != null && progressDialog1.isShowing()) {
            try {
                progressDialog1.dismiss();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onTaskStart() {
        progressDialog1 = new ProgressDialog(RemindersActivityCMN.this);
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
                    patientList.add(jsonObject2.getString("name"));
                    patientIdList.add(jsonObject2.getString("patientid"));

                }

                spinnerDataAdapter.notifyDataSetChanged();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return false;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        this.invalidateOptionsMenu();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

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
            progressDialog = new ProgressDialog(RemindersActivityCMN.this);
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
        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(RemindersActivityCMN.this, android.R.style.Theme_Dialog));
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
