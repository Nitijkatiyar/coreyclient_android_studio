package com.coremobile.coreyhealth;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.coremobile.coreyhealth.clientcertificate.CMN_HTTPSClient;
import com.coremobile.coreyhealth.genericviewandsurvey.CMN_JsonConstants;
import com.coremobile.coreyhealth.googleAnalytics.CMN_Preferences;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MedicationDialog extends Activity {

    private String mAlertString;
    private String TAG = "MedicationDialog";
    public static final String CURRENT_USER = "CurrentUser";
    private String mUrl, mMedication;

    private String mOrg;
    private String CMN_SERVER_BASE_URL_DEFINE;
    private String mtype;
    private String alert_msg;
    private String mInstructionId;
    private boolean mAlertwResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        mAlertString = getIntent().getStringExtra("Alert");
        mtype = getIntent().getStringExtra("type");
        SharedPreferences mCurrentUserPref = getSharedPreferences(CURRENT_USER,
                0);
        mOrg = mCurrentUserPref.getString("Organization", null);
        if (mtype.equalsIgnoreCase("medication")) {
            alert_msg = mAlertString.replace("$", " ");
            mMedication = mAlertString.substring(mAlertString.indexOf("(") + 1,
                    mAlertString.indexOf(")"));
        } else if (mtype.equalsIgnoreCase("instruction_compliance")) {

            int InstrIdIndex = mAlertString.indexOf("|");
            Log.d(TAG, "InstrIdIndex" + InstrIdIndex);
            if (InstrIdIndex != -1) {
                mAlertwResponse = true;
                mInstructionId = mAlertString.substring(InstrIdIndex);
                mInstructionId = mInstructionId.replace("|", "");
                mInstructionId.trim();
                String[] alrtmsg = mAlertString.split("[|]");
                mInstructionId = alrtmsg[1];
                CMN_JsonConstants.eprosMessages.put(mInstructionId, mAlertString);
                //	alert_msg = mAlertString.replace("$", " ");
                alert_msg = alrtmsg[0];

                Log.d(TAG, "alert_msg=" + alert_msg);
            } else {
                mAlertwResponse = false;
                alert_msg = mAlertString;
            }
        }
        JSONHelperClass jsonHelperClass = new JSONHelperClass();
        CMN_SERVER_BASE_URL_DEFINE = jsonHelperClass.getBaseURL(mOrg);
        if (CMN_SERVER_BASE_URL_DEFINE == null) {
            CMN_SERVER_BASE_URL_DEFINE = mCurrentUserPref.getString("CMN_SERVER_BASE_URL_DEFINE", null);
        }
        //    mUrl = "https://www.coremobile.us/ServerIntegration_Appstore/medicationstatus.aspx";
        /*
         * + userName + "&password=" + pwd + "&organization=" + org +
         * "&response=yes" +"&medication="+mMedication;
         */
        if (mtype.equalsIgnoreCase("medication")) {
            mUrl = CMN_SERVER_BASE_URL_DEFINE + "medicationstatus.aspx?token=" + CMN_Preferences.getUserToken(MedicationDialog.this);
        } else if (mtype.equalsIgnoreCase("instruction_compliance")) {
            mUrl = CMN_SERVER_BASE_URL_DEFINE + "ComplianceToInstructionStatus.aspx?token=" + CMN_Preferences.getUserToken(MedicationDialog.this);
        }
        if (mAlertwResponse) {
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    MedicationDialog.this);
            builder.setTitle(R.string.app_name);
            builder.setMessage(alert_msg);
            builder.setPositiveButton("Yes", new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    try {
                        new GetJSON().execute(mUrl);
                        finish();
                        dialog.cancel();
                    } catch (Exception e) {
                        Log.v("MyAPp", "GetJSON failed to download crystal.json");
                    }
                }
            });
            builder.setNegativeButton("No", new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    dialog.cancel();
                    finish();

                }
            });
            builder.setOnCancelListener(new OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    // TODO Auto-generated method stub
                    dialog.dismiss();
                    finish();
                }
            });
            builder.show();

        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    MedicationDialog.this);
            builder.setTitle(R.string.app_name);
            builder.setMessage(alert_msg);
            builder.setPositiveButton("Ok", new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    try {
                        dialog.dismiss();
                        finish();
                    } catch (Exception e) {
                        Log.v("MyAPp", "GetJSON failed to download crystal.json");
                    }
                }
            });
            builder.show();
        }
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        finish();
    }

    public class GetJSON extends AsyncTask<String, Void, JSONObject> {

        // static final String TAG = "Corey_GETJSON";

        @Override
        protected JSONObject doInBackground(String... params) {
            // TODO Auto-generated method stub
            // Log.i("TAG","doInBackground");
            //   MyApplication._parsing = true;
            StringBuilder builder = new StringBuilder();
            JSONObject jsonObject = null;
            CloseableHttpClient httpclient = new CMN_HTTPSClient(MyApplication.INSTANCE, mUrl).getClient();
            try {

                HttpPost httppost = new HttpPost(mUrl);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs
                        .add(new BasicNameValuePair("type", "web_server"));
                if (mtype.equalsIgnoreCase("medication")) {
                    nameValuePairs.add(new BasicNameValuePair("response", "yes"));
                    nameValuePairs.add(new BasicNameValuePair("medication",
                            mMedication));
                } else if (mtype.equalsIgnoreCase("instruction_compliance")) {
                    nameValuePairs.add(new BasicNameValuePair("instructionid", mInstructionId));
                }

                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
                        nameValuePairs);
                entity.setContentEncoding(HTTP.UTF_8);
                httppost.setEntity(entity);
                httppost.setHeader("Content-Type",
                        "application/x-www-form-urlencoded");
                HttpResponse response = httpclient.execute(httppost);
                Log.d("Response", " AT most  " + nameValuePairs + " response "
                        + response.getStatusLine());

            } catch (ClientProtocolException e) {
                e.getMessage();
                // Log.i(TAG,"ClientProtocolException");
            } catch (IOException e) {
                e.getMessage();
                // Log.i(TAG,"IOException");
            } catch (IllegalStateException e) {
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
            // Log.i("TAG","onPostExecute"+json);
            //   MyApplication._parsing = false;
            finish();
        }

    }

}