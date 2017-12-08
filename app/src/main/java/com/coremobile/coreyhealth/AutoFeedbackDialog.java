package com.coremobile.coreyhealth;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.coremobile.coreyhealth.base.CMN_AppBaseActivity;
import com.coremobile.coreyhealth.clientcertificate.CMN_HTTPSClient;
import com.coremobile.coreyhealth.genericviewandsurvey.CMN_JsonConstants;
import com.coremobile.coreyhealth.googleAnalytics.CMN_Preferences;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

public class AutoFeedbackDialog extends CMN_AppBaseActivity {

    private String mAlertString;
    private String TAG = "Corey_AutoFeedbackDialog";
    public static final String CURRENT_USER = "CurrentUser";
    private String mUrl, mMedication;
    private String mOrg;
    private String CMN_SERVER_BASE_URL_DEFINE;
    private String mtype;
    private String alert_msg;
    private String mInstructionId;
    private String Appname;
    private String userResponse;
    CharSequence[] items;
    String[] alrtmsg;
    private boolean mAlertwResponse;
    TextView title;
    Button submit, cancel;
    EditText freeText;
    RadioGroup radioGroup;
    String jsonString;
    TextToSpeech t1;
    Boolean neededSound = false;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialogue_auto_feedback);
        title = (TextView) findViewById(R.id.title);
        submit = (Button) findViewById(R.id.submit);
        cancel = (Button) findViewById(R.id.cancel);

        title.setMovementMethod(new ScrollingMovementMethod());

        freeText = (EditText) findViewById(R.id.freetext);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        SharedPreferences mCurrentUserPref = getSharedPreferences(CURRENT_USER,
                0);
        mOrg = mCurrentUserPref.getString("Organization", null);


        JSONHelperClass jsonHelperClass = new JSONHelperClass();
        CMN_SERVER_BASE_URL_DEFINE = jsonHelperClass.getBaseURL(mOrg);
        if (CMN_SERVER_BASE_URL_DEFINE == null) {
            CMN_SERVER_BASE_URL_DEFINE = mCurrentUserPref.getString("CMN_SERVER_BASE_URL_DEFINE", null);
        }
        //   mUrl = "https://www.coremobile.us/ServerIntegration_Appstore/SetPatientSurveyResponse.aspx";
        mUrl = CMN_SERVER_BASE_URL_DEFINE + "SetPatientSurveyResponse.aspx";

        mAlertString = getIntent().getStringExtra("Alert");
        mtype = getIntent().getStringExtra("type");
        //--------------------- Fortify scan issue fixes----------------
        if (getIntent().hasExtra("jsonString")) {
            jsonString = getIntent().getStringExtra("jsonString");
        }
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = (RadioButton) findViewById(selectedId);
                if (CMN_SERVER_BASE_URL_DEFINE == null || CMN_SERVER_BASE_URL_DEFINE.isEmpty()) {
                } else {
                    if (radioButton == null) {
                        new GetJSON(freeText.getText().toString().trim(), null).execute(mUrl);
                    } else {
                        new GetJSON(freeText.getText().toString().trim(), radioButton.getTag().toString()).execute(mUrl);
                    }
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            //--------------------- Fortify scan issue fixes----------------
            if (jsonObject.getBoolean("HasFreeText")) {
                if (jsonObject.has("FreeTextTitle")) {
                    freeText.setHint(jsonObject.getString("FreeTextTitle"));
                    freeText.setVisibility(View.VISIBLE);
                    freeText.clearFocus();
                }
            } else {
                freeText.setVisibility(View.GONE);
            }
            if (jsonObject.has("NeedsSound") && jsonObject.getString("NeedsSound").equalsIgnoreCase("T")) {
                neededSound = true;
            } else {
                neededSound = false;
            }
        } catch (JSONException e) {
            e.getMessage();
        }


        if (mtype.equalsIgnoreCase("FeedbackSurvey")) {

            int InstrIdIndex = mAlertString.indexOf("|");
            Log.d(TAG, "FeedbackSurveyIndex" + InstrIdIndex);
            if (InstrIdIndex != -1) {
                mAlertwResponse = true;
                mInstructionId = mAlertString.substring(InstrIdIndex);
                mInstructionId = mInstructionId.replace("|", "");
                mInstructionId.trim();
                //	alert_msg = mAlertString.replace("$", " ");
                alrtmsg = mAlertString.split("\\|");
                alert_msg = alrtmsg[0];
                title.setText(alert_msg);

                t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status == TextToSpeech.SUCCESS) {
                            t1.setLanguage(Locale.US);
                            if (neededSound) {
                                t1.speak(alert_msg, TextToSpeech.QUEUE_FLUSH, null, null);
                            }
                        }
                    }
                });
                mInstructionId = alrtmsg[1];
                Log.d(TAG, "mAlertString = " + mAlertString);
                Log.d(TAG, "alert_msg[] = " + alrtmsg.toString());
                Log.d(TAG, "alert_msg[0] = " + alrtmsg[0]);
                int totoptions = alrtmsg.length;
                items = new CharSequence[totoptions - 2];
                for (int ii = 2; ii < (totoptions); ii++) {
                    items[ii - 2] = alrtmsg[ii];
                }
//'NeedsSound' = 'F'
                for (int i = 0; i < (items.length); i++) {
                    Log.d(TAG, "items= " + items[i]);
                    RadioButton rdbtn = new RadioButton(this);
                    rdbtn.setText("  " + items[i]);
                    rdbtn.setId(i);
                    rdbtn.setTag(i + 1);
                    rdbtn.setButtonDrawable(R.drawable.radio_selector);
                    rdbtn.setTextColor(Color.parseColor("#909090"));
                    rdbtn.setTextSize(16);

                    radioGroup.addView(rdbtn);
                }

                radioGroup.check(0);

            } else {
                mAlertwResponse = false;
                alert_msg = mAlertString;
            }
            CMN_JsonConstants.eprosMessages.put(mInstructionId, mAlertString);
        }


    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (t1 != null) {
            t1.stop();
            t1.shutdown();
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        Log.d(TAG, "activity destroyed");
    }

    public class GetJSON extends AsyncTask<String, Void, JSONObject> {
        String comment;
        String selectedRadio;
        ProgressDialog progressDialog;

        public GetJSON(String trim, String text) {
            this.comment = trim;
            this.selectedRadio = text;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(AutoFeedbackDialog.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();

        }

        // static final String TAG = "Corey_GETJSON";

        @Override
        protected JSONObject doInBackground(String... params) {
            // TODO Auto-generated method stub
            // Log.i("TAG","doInBackground");
            //    MyApplication._parsing = true;
            StringBuilder builder = new StringBuilder();
            JSONObject jsonObject = null;


            CloseableHttpClient httpclient = new CMN_HTTPSClient(MyApplication.INSTANCE, mUrl).getClient();
            try {
                String url = mUrl + "?token=" + CMN_Preferences.getUserToken(AutoFeedbackDialog.this);

                url = url + "&id=" + mInstructionId;
                if (selectedRadio != null) {
                    url = url + "&response=" + selectedRadio.toString();

                } else {
                    url = url + "&response=";
                }
                if (!CMN_Preferences.getIntakeContext(AutoFeedbackDialog.this).isEmpty()) {
                    url = url + "&patientid=" + CMN_Preferences.getIntakeContext(AutoFeedbackDialog.this);
                }
                url = url + "&comments=" + comment;
                HttpGet httppost = new HttpGet(url);

                HttpResponse response = httpclient.execute(httppost);
                Log.d(TAG, "URL..." + url);

                JSONObject json = null;
                json = new JSONObject(EntityUtils.toString(response.getEntity()));
                final String message = json.getJSONObject("Result").getString("Message");
                if (json.getJSONObject("Result").getInt("Code") == 0) {
                    CMN_Preferences.setIntakeContext(AutoFeedbackDialog.this, "");
//                    CMN_JsonConstants.eprosMessages.clear();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AutoFeedbackDialog.this, message, Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (ClientProtocolException e) {
                e.getMessage();
                Log.d(TAG, "ClientProtocolException");
            } catch (IOException e) {
                e.getMessage();
                Log.d(TAG, "IOException");
            } catch (JSONException e) {
                e.getMessage();
            } finally {
                if (httpclient != null) {
                    try {
                        httpclient.close();
                    } catch (IOException e) {
                        e.getMessage();
                    }
                }
            }
            return jsonObject;
        }

        protected void onPostExecute(JSONObject json) {
            Log.d("TAG", "onPostExecute" + json);
            //   MyApplication._parsing = false;
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            finish();
        }

    }

    private static String convertStreamToString(InputStream is) {
        /*
         * To convert the InputStream to String we use the
    	 * BufferedReader.readLine() method. We iterate until the BufferedReader
    	 * return null which means there's no more data to read. Each line will
    	 * appended to a StringBuilder and returned as String.
    	 */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.getMessage();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.getMessage();
            }
        }
        return sb.toString();
    }
}