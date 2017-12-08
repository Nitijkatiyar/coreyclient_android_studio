package com.coremobile.coreyhealth;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.coremobile.coreyhealth.googleAnalytics.CMN_Preferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class GeneralSettingsActivityCMN extends BaseActivityCMN implements IServerConnect {
    private static final String TAG = "Corey_GeneralSettingsActivityCMN";
    private static final String CURRENT_USER = "CurrentUser";
    public ProgressDialog mDialog;
    SharedPreferences mcurrentUserPref;
    boolean mValueChanged;

    CheckBox mTrackLocation;
    CheckBox mPushNotification;
    CheckBox mAutoPurge;
    CheckBox mAutoSync;
    CheckBox mIgnoreAppointments;
    CheckBox mPhoneCalls;
    CheckBox mLocationSync;
    EditText mReminder;
    EditText mNumPlanAhead;

    @Override
    protected void onResume() {
        super.onResume();
        if (MyApplication.shouldResetApp()) {
            finish();
            return;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        mcurrentUserPref = getSharedPreferences(CURRENT_USER, 0);
        mValueChanged = false;
        Utils.setAppTitle(this, "Settings");
        mTrackLocation = (CheckBox) findViewById(R.id.track_location_checkbox);
        mTrackLocation.setVisibility(View.GONE);
        mPushNotification = (CheckBox) findViewById(R.id.push_notification_checkbox);
        mAutoPurge = (CheckBox) findViewById(R.id.auto_purge_checkbox);
        mAutoSync = (CheckBox) findViewById(R.id.auto_sync_checkbox);
        mIgnoreAppointments = (CheckBox) findViewById(R.id.ignore_appointments_checkbox);
        mPhoneCalls = (CheckBox) findViewById(R.id.phone_calls_checkbox);
        mLocationSync = (CheckBox) findViewById(R.id.location_sync_checkbox);
        mReminder = (EditText) findViewById(R.id.reminder_edittext);
        mNumPlanAhead = (EditText) findViewById(R.id.numplanahead_edittext);
        mTrackLocation.setChecked(mcurrentUserPref.getBoolean("location", false));
        mPushNotification.setChecked(mcurrentUserPref.getBoolean("enable_push", true));
        mAutoPurge.setChecked(mcurrentUserPref.getBoolean("auto_delete_past_meeting", false));
        mAutoSync.setChecked(mcurrentUserPref.getBoolean("enable_weekly_auto_sync", true));
        mIgnoreAppointments.setChecked(mcurrentUserPref.getBoolean("ignore_appointments", false));
        mPhoneCalls.setChecked(LocalPrefs.INSTANCE.phoneCalls());
        mLocationSync.setChecked(LocalPrefs.INSTANCE.locationSync());
        mReminder.setText(mcurrentUserPref.getString("meeting_reminder", "20"));
        mNumPlanAhead.setText(mcurrentUserPref.getString("num_plan_ahead", "10"));
        Button mSubmit = (Button) findViewById(R.id.settings_submit);
        Button mCancel = (Button) findViewById(R.id.settings_cancel);

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick");
                if ((mPhoneCalls.isChecked() && !LocalPrefs.INSTANCE.phoneCalls()) ||
                        (mLocationSync.isChecked() && !LocalPrefs.INSTANCE.locationSync())) {
                    askUserConfirmation();
                } else {
                    updateSettingsToServer();
                }

            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void askUserConfirmation() {
        StringBuilder sb = new StringBuilder();
        String delim, phoneStr, locStr;

        if (mPhoneCalls.isChecked()) {
            phoneStr = "phone call logs";
            delim = " and ";
        } else {
            phoneStr = "";
            delim = "";
        }

        if (mLocationSync.isChecked()) {
            locStr = "location information";
        } else {
            locStr = "";
            delim = "";
        }

        sb.append("The changes in settings require permission to access your ")
                .append(phoneStr)
                .append(delim)
                .append(locStr)
                .append(".\n\nDo you allow Corey to access your ")
                .append(phoneStr)
                .append(delim)
                .append(locStr)
                .append("?");

        AlertDialog.Builder dlg = new AlertDialog.Builder(this);
        dlg.setTitle(R.string.gs_dlg_title);
        dlg.setMessage(sb.toString());
        dlg.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateSettingsToServer();
            }
        });
        dlg.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dlg.show();
    }

    private void updateSettingsToServer() {
        Log.d(TAG, "updateSettingsToServer");
        String location;
        String auto_delete_past_meeting;
        String enable_push;
        String enable_weekly_auto_sync;
        String ignore_appointments;
        String phone_calls;
        String location_sync;

        if (mTrackLocation.isChecked()) {
            location = "1";
        } else {
            location = "0";
        }
        if (mPushNotification.isChecked()) {
            enable_push = "1";
        } else {
            enable_push = "0";
        }
        if (mAutoSync.isChecked()) {
            enable_weekly_auto_sync = "1";
        } else {
            enable_weekly_auto_sync = "0";
        }
        if (mAutoPurge.isChecked()) {
            auto_delete_past_meeting = "1";
        } else {
            auto_delete_past_meeting = "0";
        }

        if (mIgnoreAppointments.isChecked()) {
            ignore_appointments = "1";
        } else {
            ignore_appointments = "0";
        }

        phone_calls = mPhoneCalls.isChecked() ? "1" : "0";
        location_sync = mLocationSync.isChecked() ? "1" : "0";

        String CMN_SERVER_BASE_URL_DEFINE = mcurrentUserPref.getString("CMN_SERVER_BASE_URL_DEFINE", "");
        String url = CMN_SERVER_BASE_URL_DEFINE + "UpdateUserConfigInfo.aspx?";
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("token",
                CMN_Preferences.getUserToken(GeneralSettingsActivityCMN.this));
        data.put("meeting_reminder", mReminder.getText().toString().trim());
        data.put("num_plan_ahead", mNumPlanAhead.getText().toString().trim());
        data.put("location", location);
        data.put("enable_push", enable_push);
        data.put("auto_delete_past_meeting", auto_delete_past_meeting);
        data.put("enable_weekly_auto_sync", enable_weekly_auto_sync);
        data.put("ignore_appointments", ignore_appointments);
        data.put("phone_calls", phone_calls);
        data.put("location_sync", location_sync);
        Log.d(TAG, "sign in url: " + url);
        Log.d(TAG, "meeting_reminder: " + mReminder.getText());
        Log.d(TAG, "num_plan_ahead: " + mNumPlanAhead.getText());
        Log.d(TAG, "location: " + location);
        Log.d(TAG, "enable_push: " + enable_push);
        Log.d(TAG, "auto_delete_past_meeting: " + auto_delete_past_meeting);
        Log.d(TAG, "enable_weekly_auto_sync: " + enable_weekly_auto_sync);
        Log.d(TAG, "phone_calls: " + phone_calls);
        Log.d(TAG, "location_sync: " + location_sync);
        if (Utils.checkInternetConnection()) {
            new VerifyUser(GeneralSettingsActivityCMN.this, data).execute(url);
        } else {
            Toast.makeText(getApplicationContext(), "Operation cannot be performed now.Check if your cellular data/wifi is turned ON.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void gotUserInfoFromServer(JSONObject json) {

        String resultText = "Failed to upload the Settings to the server. Please check your Network connection and try again.";

        if (json != null) {
            try {
                Log.d(TAG, "returned json: " + json.names());
                String result = json.getString("result");
                resultText = json.getString("text");
                if (result.equals("0")) {
                    SharedPreferences.Editor editor = mcurrentUserPref.edit();
                    if (mTrackLocation.isChecked() != mcurrentUserPref.getBoolean("location", false)) {
                        Log.d(TAG, "location changed: " + mcurrentUserPref.getBoolean("location", false) + " after: " + mTrackLocation.isChecked());
                        editor.putBoolean("location", mTrackLocation.isChecked());

                    }
                    if (mPushNotification.isChecked() != mcurrentUserPref.getBoolean("enable_push", false)) {
                        Log.d(TAG, "push changed: " + mcurrentUserPref.getBoolean("enable_push", false) + " after: " + mPushNotification.isChecked());
                        editor.putBoolean("enable_push", mPushNotification.isChecked());
                        /*
                        if(mPushNotification.isChecked()){
                            PushManager.enablePush();
                        }else{
                            PushManager.disablePush();
                        }
                        */
                    }

                    if (mIgnoreAppointments.isChecked() != mcurrentUserPref.getBoolean("ignore_appointments", false)) {
                        Log.d(TAG, "ignore appointments changed: " + mcurrentUserPref.getBoolean("ignore_appointments", false) + " after: " + mAutoPurge.isChecked());
                        editor.putBoolean("ignore_appointments", mIgnoreAppointments.isChecked());
                    }

                    LocalPrefs.INSTANCE.setPhoneCalls(mPhoneCalls.isChecked());
                    LocalPrefs.INSTANCE.setLocationSync(mLocationSync.isChecked());
                    if (mPhoneCalls.isChecked() || mLocationSync.isChecked()) {
                        LocalPrefs.INSTANCE.setHasPickedFeatures(true);
                    }

                    if (mPhoneCalls.isChecked()) {
                        MyApplication.INSTANCE.registerPhoneCallsListener();
                    } else {
                        MyApplication.INSTANCE.unregisterPhoneCallsListener();
                    }

                    if (mAutoPurge.isChecked() != mcurrentUserPref.getBoolean("auto_delete_past_meeting", false)) {
                        Log.d(TAG, "purge changed: " + mcurrentUserPref.getBoolean("auto_delete_past_meeting", false) + " after: " + mAutoPurge.isChecked());
                        editor.putBoolean("auto_delete_past_meeting", mAutoPurge.isChecked());
                    }
                    if (mAutoSync.isChecked() != mcurrentUserPref.getBoolean("enable_weekly_auto_sync", false)) {
                        Log.d(TAG, "sync changed before: " + mcurrentUserPref.getBoolean("enable_weekly_auto_sync", false) + " after: " + mAutoSync.isChecked());
                        editor.putBoolean("enable_weekly_auto_sync", mAutoSync.isChecked());
                    }
                    String reminder = mReminder.getText().toString();
                    if (!(reminder.equals(mcurrentUserPref.getString("meeting_reminder", "20")))) {
                        Log.d(TAG, "reminder before: " + mcurrentUserPref.getString("meeting_reminder", "20") + " after: " + mReminder.getText().toString());
                        editor.putString("meeting_reminder", mReminder.getText().toString());
                    }
                    String numPlanAhead = mNumPlanAhead.getText().toString();
                    if (!(numPlanAhead.equals(mcurrentUserPref.getString("num_plan_ahead", "10")))) {
                        Log.d(TAG, "reminder before: " + mcurrentUserPref.getString("num_plan_ahead", "10") + " after: " + mNumPlanAhead.getText().toString());
                        editor.putString("num_plan_ahead", mNumPlanAhead.getText().toString());
                    }

                    editor.commit();
                    //mValueChanged = false;

                    finish();
                    return;
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.getMessage();
            }
        }
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Error");
        dialog.setMessage(resultText);
        dialog.create();
        dialog.show();
    }


    @Override
    public void showDialog() {
        if (isFinishing()) {
            return;
        }
        if (mDialog == null) {
            //refreshData.setVisibility(View.INVISIBLE);
            mDialog = ProgressDialog.show(this, "",
                    "Updating settings..", true, true);
        }

    }

    @Override
    public void closeDialog() {
        if (isFinishing()) {
            return;
        }
        if (mDialog != null) {
            //Log.i(TAG,"dismissDialog");
            //refreshData.setVisibility(View.VISIBLE);
            mDialog.dismiss();
            mDialog = null;
        }

    }

    @Override
    public void throwToast(String stringToToast) {
        if (isFinishing()) {
            return;
        }
    }

}
