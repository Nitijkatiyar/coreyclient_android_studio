package com.coremobile.coreyhealth.native_epro;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
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
import com.coremobile.coreyhealth.base.CMN_AppBaseActivity;
import com.coremobile.coreyhealth.errorhandling.ActivityPackage;
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
 * Created by nitij on 06-01-2017.
 */
public class CMN_EproListActivity extends CMN_AppBaseActivity implements NetworkCallBack, AsyncTaskCompleteListener<String> {

    static ListView listView;
    ProgressDialog progressDialog;
    ProgressDialog progressDialog1;
    TextView noMessages;
    public static List<CMN_eproModel> eproModels;
    CMN_EproListAdapter eproListAdapter;

    String vToken;
    public static String PATIENT_URL = null, SEND_URL = null;

    ArrayAdapter<String> spinnerDataAdapter;


    List<String> patientList = new ArrayList<>();
    List<String> patientIdList = new ArrayList<>();

    int deletePosition = -1;

    AsyncTaskCompleteListener<String> asyncTaskCompleteListener;

    static RelativeLayout relativeLayout;
    Spinner spinnerVw;
    Button okBthVw, cancelBtnVw;
    static ImageView dimImageVw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eproslist);

        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setTitle(
                "" + getResources().getString(R.string.epro));
        getActionBar().setDisplayHomeAsUpEnabled(true);

        noMessages = (TextView) findViewById(R.id.noMessages);
        listView = (ListView) findViewById(R.id.listview);

        relativeLayout = (RelativeLayout) findViewById(R.id.patientLayoutVw);
        dimImageVw = (ImageView) findViewById(R.id.dimImageVw);

        spinnerVw = (Spinner) findViewById(R.id.selectPatientSpinnerVw);
        okBthVw = (Button) findViewById(R.id.btnOkVw);
        cancelBtnVw = (Button) findViewById(R.id.btnCancelVw);
        asyncTaskCompleteListener = new CMN_EproListActivity();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClassName(getPackageName(), ActivityPackage.AddNewEproActivity);
                intent.putExtra("dataModel", eproModels.get(position));
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(CMN_EproListActivity.this)
                        .setTitle("Delete ePRO")
                        .setMessage("Are you sure you want to delete this ePRO?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                deletePosition = position;

                                NetworkAsyncTask networkAsyncTask = NetworkAsyncTask.getInstance();
                                try {
                                    networkAsyncTask.startNetworkCall(ThreadTaskIds.DELETE_EPROS, CMN_EproListActivity.this);
                                } catch (NetworkException e) {
                                    e.getMessage();
                                }
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_menu_delete)
                        .show();
                return false;
            }
        });

        NetworkAsyncTask networkAsyncTask = NetworkAsyncTask.getInstance();
        try {
            networkAsyncTask.startNetworkCall(ThreadTaskIds.GET_EPROS_LIST, CMN_EproListActivity.this);
        } catch (NetworkException e) {
            e.getMessage();
        }

        /**
         * PatientList fetch
         */
        vToken = CMN_Preferences.getUserToken(getApplicationContext());
        PATIENT_URL = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE + "GetAllPatients.aspx?token=" + vToken+"&all=false";
        SEND_URL = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE + "SendInstantMessage.aspx?token=" + vToken;

        NetworkTools networkTools = NetworkTools.getInstance();

        if (networkTools.checkNetworkConnection(CMN_EproListActivity.this)) {
            BaseAsyncTask baseAsyncTask = new BaseAsyncTask(getApplicationContext(), "GET", this, null);
            baseAsyncTask.execute(CMN_EproListActivity.PATIENT_URL);

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
                    object.put("Type", "EPRO");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                NetworkTools networkTools = NetworkTools.getInstance();


                if (networkTools.checkNetworkConnection(CMN_EproListActivity.this)) {
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


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (CMN_Preferences.getePROadded(CMN_EproListActivity.this)) {
            NetworkAsyncTask networkAsyncTask = NetworkAsyncTask.getInstance();
            try {
                networkAsyncTask.startNetworkCall(ThreadTaskIds.GET_EPROS_LIST, CMN_EproListActivity.this);
            } catch (NetworkException e) {
                e.getMessage();
            }
            CMN_Preferences.setePROadded(CMN_EproListActivity.this, false);

        }

        relativeLayout.setVisibility(View.GONE);
        dimImageVw.setVisibility(View.GONE);
        listView.setEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_erpo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;

            case R.id.add_epro:
                Intent intent = new Intent(CMN_EproListActivity.this, CMN_AddNewEproActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void beforeNetworkCall(int taskId) {
        progressDialog = new ProgressDialog(CMN_EproListActivity.this);
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
        if (taskId == ThreadTaskIds.GET_EPROS_LIST) {
            String url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                    + "GetAllePROS.aspx?token=" + CMN_Preferences.getUserToken(CMN_EproListActivity.this);


            NetworkTools networkTools = NetworkTools.getInstance();
            if (networkTools.checkNetworkConnection(CMN_EproListActivity.this) && url.length() > 0) {
                JSONObject response = networkTools.getJsonData(CMN_EproListActivity.this, url);
                if (response != null) {
                    try {
                        parseJsonData(response, taskId);
                    } catch (JSONException e) {
                        e.getMessage();
                    }
                }
            } else {
                Toast.makeText(CMN_EproListActivity.this, CMN_ErrorMessages.getInstance().getValue(12), Toast.LENGTH_SHORT).show();
            }
        } else if (taskId == ThreadTaskIds.DELETE_EPROS) {
            String url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                    + "SaveEPRO.aspx?token=" + CMN_Preferences.getUserToken(CMN_EproListActivity.this) + "&action=delete";


            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("ID", eproModels.get(deletePosition).getID());
            } catch (JSONException e) {
                e.getMessage();
            }

            NetworkTools networkTools = NetworkTools.getInstance();
            if (networkTools.checkNetworkConnection(CMN_EproListActivity.this) && url.length() > 0) {
                JSONObject response = networkTools.postJsonData(CMN_EproListActivity.this, url, jsonObject);
                if (response != null) {
                    try {
                        parseJsonData(response, taskId);
                    } catch (JSONException e) {
                        e.getMessage();
                    }
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CMN_EproListActivity.this, CMN_ErrorMessages.getInstance().getValue(12), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }
        return o;
    }


    private void parseJsonData(final JSONObject response, int taskId) throws JSONException {
        if (taskId == ThreadTaskIds.GET_EPROS_LIST) {
            eproModels = new ArrayList<>();
            if (response.getJSONObject("Result").getInt("Code") == 0) {

                JSONObject data = response.getJSONObject("Data");
                JSONArray eprosArray = data.getJSONArray("EPROS");
                for (int i = 0; i < eprosArray.length(); i++) {
                    JSONObject eproObject = eprosArray.getJSONObject(i);
                    CMN_eproModel model = new CMN_eproModel();
                    model.setID(eproObject.getString("ID"));
                    model.setFrequency(eproObject.getString("Frequency"));
                    model.setQuestion(eproObject.getString("Question"));
                    model.setifDependent(eproObject.getBoolean("IsDependent"));
                    model.setFreeTextTitle(eproObject.getString("FreeTextTitle"));
                    model.setifHasFreeText(eproObject.getBoolean("HasFreeText"));
                    model.setMessageToPat(eproObject.getString("MessageToPat"));
                    model.setNotificationID(eproObject.getString("NotificationID"));
                    model.setifNotifyProvider(eproObject.getBoolean("NotifyProvider"));
                    model.setOptionsCount(eproObject.getString("OptionsCount"));
                    model.setPatientId(eproObject.getString("PatientId"));
                    model.setPatientName(eproObject.getString("PatientName"));
                    model.setifPatientNeedsReminder(eproObject.getBoolean("PatientNeedsReminder"));
                    model.setReminderCategory(eproObject.getString("ReminderCategory"));
                    if (eproObject.has("PatientReminderMessage")) {
                        model.setPatientReminderMessage(eproObject.getString("PatientReminderMessage"));
                    }
                    model.setProcedure(eproObject.getString("Procedure"));
                    model.setProviderNotificationOptions(eproObject.getString("ProviderNotificationOptions"));
                    model.setReminderFreq(eproObject.getString("ReminderFreq"));
                    model.setReminderFreqDisplayName(eproObject.getString("ReminderFreqDisplayName"));
                    model.setReminderCategory(eproObject.getString("ReminderCategory"));
                    model.setReminderTime(eproObject.getString("ReminderTime"));
                    model.setReminderType(eproObject.getString("ReminderType"));
                    model.setStage(eproObject.getString("Stage"));
                    model.setifStopOnSuccess(eproObject.getBoolean("StopOnSuccess"));
                    model.setTimeOfEvent(eproObject.getString("TimeOfEvent"));

                    JSONArray options = eproObject.getJSONArray("Options");
                    List<String> stringList = new ArrayList<>();
                    for (int j = 0; j < options.length(); j++) {
                        stringList.add(options.get(j).toString());
                    }
                    model.setOptions(stringList);

                    JSONArray dependency = eproObject.getJSONArray("Dependencies");
                    List<CMN_ePROsDependentModel> ePROsDependentModels = new ArrayList<>();
                    for (int k = 0; k < dependency.length(); k++) {
                        JSONObject object = dependency.getJSONObject(k);
                        CMN_ePROsDependentModel emodel = new CMN_ePROsDependentModel();
                        emodel.setId(object.getInt("Id"));
                        emodel.setQuestion(object.getString("Question"));
                        emodel.setDependentResponseOption(Integer.parseInt(object.getString("DependentResponseOption")));

                        ePROsDependentModels.add(emodel);
                    }
                    model.setePROsDependentModels(ePROsDependentModels);

                    eproModels.add(model);
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (eproModels.size() > 0) {
                        eproListAdapter = new CMN_EproListAdapter(CMN_EproListActivity.this, eproModels);
                        listView.setAdapter(eproListAdapter);
                        listView.setVisibility(View.VISIBLE);
                        noMessages.setVisibility(View.GONE);

                    } else {
                        listView.setVisibility(View.GONE);
                        noMessages.setVisibility(View.VISIBLE);
                    }
                }
            });
        } else if (taskId == ThreadTaskIds.DELETE_EPROS) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        new AlertDialog.Builder(CMN_EproListActivity.this)
                                .setTitle("Delete ePRO")
                                .setMessage(response.getJSONObject("Result").getString("Message"))
                                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        try {
                                            if (response.getJSONObject("Result").getInt("Code") == 0) {
                                                eproModels.remove(deletePosition);
                                                eproListAdapter.notifyDataSetChanged();
                                            }
                                        } catch (JSONException e) {
                                            e.getMessage();
                                        }
                                        dialog.dismiss();
                                    }
                                })
                                .setIcon(android.R.drawable.ic_menu_delete)
                                .show();
                    } catch (JSONException e) {
                        e.getMessage();
                    }

                }
            });
        }
    }

    @Override
    public Object onNetworkError(int taskId, NetworkException o) {
        return null;
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
        progressDialog1 = new ProgressDialog(CMN_EproListActivity.this);
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

    public static void invokeActivityMethod(int pos) {
        dimImageVw.setVisibility(View.VISIBLE);
        relativeLayout.setVisibility(View.VISIBLE);
        listView.setEnabled(false);
        eproId = eproModels.get(pos).getID();

    }

    private class UploadDataAsyncTask extends AsyncTask<Void, Integer, String> {

        String noteText;

        UploadDataAsyncTask(String text) {

            noteText = text;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(CMN_EproListActivity.this);
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
        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(CMN_EproListActivity.this, android.R.style.Theme_Dialog));
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
