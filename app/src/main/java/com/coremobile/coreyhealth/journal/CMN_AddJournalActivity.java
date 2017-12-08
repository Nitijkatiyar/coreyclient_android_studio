package com.coremobile.coreyhealth.journal;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.coremobile.coreyhealth.R;
import com.coremobile.coreyhealth.googleAnalytics.CMN_Preferences;
import com.coremobile.coreyhealth.networkutils.NetworkTools;
import com.coremobile.coreyhealth.newui.MessageTabActivityCMN;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class CMN_AddJournalActivity extends Activity {


    EditText noteEditTextVw;
    Button saveButtonVw;
    String token, patientid, UPLOAD_URL;
    ProgressBar progressBar;

    private static String RESULT="Result";
    private static String MESSAGE="Message";
    public static String NETWORK_MESSAGE="Please check your Internet connection";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cmn__add_journal);


        getActionBar().setTitle(
                "" + getResources().getString(R.string.view_add_journal_text));
        noteEditTextVw = (EditText) findViewById(R.id.noteEditTextVw);
        saveButtonVw = (Button) findViewById(R.id.saveButtonVw);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        if (getIntent().hasExtra("PATIENT_ID"))
            patientid = getIntent().getExtras().getString("PATIENT_ID");
        saveButtonVw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NetworkTools networkTools = NetworkTools.getInstance();
                if (networkTools.checkNetworkConnection(CMN_AddJournalActivity.this)) {

                    new UploadDataAsyncTask(noteEditTextVw.getText().toString()).execute();
                } else
                    Toast.makeText(getApplicationContext(), NETWORK_MESSAGE, Toast.LENGTH_LONG).show();

            }
        });
    }

    private class UploadDataAsyncTask extends AsyncTask<Void, Integer, String> {


        String noteText;

        UploadDataAsyncTask(String text) {

            token = CMN_Preferences.getUserToken(getApplicationContext());


            UPLOAD_URL = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE + "AddPatientJournalEntry.aspx?" +
                    "token=" + token;

            noteText = text;
        }

        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            saveButtonVw.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
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
        private String uploadData(String textNote) throws UnsupportedEncodingException {

            Integer result = 0;
            String message = null;
            try {
                Log.e("URL", UPLOAD_URL);
                // Create Apache HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(UPLOAD_URL);
                JSONObject object = new JSONObject();
                String postBody = null;
                try {

                    object.put("Patientid", patientid);
                    object.put("Note", textNote);

                    postBody = object.toString();

                } catch (Exception ex) {

                }

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

            progressBar.setVisibility(View.GONE);
            // showing the server response in an alert dialog
            showAlert(result);

            saveButtonVw.setEnabled(true);
            super.onPostExecute(result);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
        saveButtonVw.setEnabled(true);
    }

    /**
     * Method to show alert dialog
     */
    private void showAlert(String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(CMN_AddJournalActivity.this, android.R.style.Theme_Dialog));
        builder.setMessage(message).setTitle("Message")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();

                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

  /*  @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/


    /**
     * Parsing the feed results and get the list
     *
     * @param result
     */
    private String parseResult(String result) {
        String message = null;
        try {
            JSONObject response = new JSONObject(result);
            JSONObject posts = response.getJSONObject(RESULT);
            message = posts.getString(MESSAGE);

        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            return message;
        }
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
}
