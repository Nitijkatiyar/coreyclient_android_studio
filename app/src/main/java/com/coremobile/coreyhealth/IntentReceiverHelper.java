package com.coremobile.coreyhealth;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.coremobile.coreyhealth.googleAnalytics.CMN_Preferences;
import com.coremobile.coreyhealth.newui.MessageTabActivityCMN;
import com.coremobile.coreyhealth.partialsync.GetAllContextWebService;


public class IntentReceiverHelper implements IPullDataFromServer {
    static final String TAG = "Corey_GoogleCloudMessaging.IntentReceiverHelper";

    private Context mContext;

    public IntentReceiverHelper(Context context) {
        mContext = context;
    }

    public void onPushReceived(final String alert_msg, String payload, String jsonMsgStr) {
        boolean mAutosync = LocalPrefs.INSTANCE.autoSync();
        MyApplication application = MyApplication.INSTANCE;

        ////Log.d(TAG, "pushmanager_payload=" + payload + ", " + alert_msg);
        if (payload != null && payload.equalsIgnoreCase("medication")) {
            Intent mIntent = new Intent(/*mContext, MedicationDialog.class*/);
            mIntent.setClassName("" + mContext.getPackageName(), "" + mContext.getPackageName() + ".MedicationDialog");
            mIntent.putExtra("type", payload);
            mIntent.putExtra("Alert", alert_msg);
            mIntent.addCategory(Intent.CATEGORY_HOME);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(mIntent);


        } else if (payload != null && payload.equalsIgnoreCase("instruction_compliance")) {
            Intent mIntent = new Intent(/*mContext, MedicationDialog.class*/);
            mIntent.setClassName("" + mContext.getPackageName(), "" + mContext.getPackageName() + ".MedicationDialog");
            mIntent.putExtra("type", payload);
            mIntent.putExtra("Alert", alert_msg);
            mIntent.addCategory(Intent.CATEGORY_HOME);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(mIntent);

        } else if (payload != null && payload.equalsIgnoreCase("FeedbackSurvey")) {
            Intent mIntent = new Intent(/*mContext, AutoFeedbackDialog.class*/);
            mIntent.setClassName("" + mContext.getPackageName(), "" + mContext.getPackageName() + ".AutoFeedbackDialog");
            mIntent.putExtra("type", payload);
            mIntent.putExtra("Alert", alert_msg);
            mIntent.putExtra("jsonString", jsonMsgStr);
            mIntent.addCategory(Intent.CATEGORY_HOME);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(mIntent);

        } else if (payload != null && payload.equalsIgnoreCase("corefy")) {
            if (Utils.checkInternetConnection()) {
            } else {
                ////Log.w(TAG, "Operation cannot be performed now.Check if your cellular data/wifi is turned ON.");
            }

        } else if (payload != null && payload.equalsIgnoreCase("market_tracker")) {
            if (!mAutosync) {
                if (Utils.checkInternetConnection()) {
                } else {
                    ////Log.w(TAG, "Operation cannot be performed now.Check if your cellular data/wifi is turned ON.");
                }
            }

        } else if (payload != null && payload.equalsIgnoreCase("SyncStarted")) {
            Intent mIntent = new Intent(/*mContext, SyncAssignment.class*/);
            mIntent.setClassName("" + mContext.getPackageName(), "" + mContext.getPackageName() + ".SyncAssignment");
            mIntent.putExtra("Alert", alert_msg);
            mIntent.addCategory(Intent.CATEGORY_HOME);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(mIntent);

        } else if (payload != null && payload.equalsIgnoreCase("PatientInfoRequest")) {
            CMN_Preferences.setisPatientDisplayedonDashboard(mContext, true);

        } else if (payload != null && payload.contains("DataChanged") || payload.contains("datachanged")) {
            if (Utils.checkInternetConnection()) {
                application.pullPartialData(IntentReceiverHelper.this);
                CMN_Preferences.setisOneTouch(mContext, false);

            }
            if (alert_msg.contains("New patient added")) {
                CMN_Preferences.setisNewPatientAdded(mContext, true);
                MessageTabActivityCMN.getNewPatients();
                if (GetAllContextWebService.dialog != null && GetAllContextWebService.dialog.isShowing()) {
                    GetAllContextWebService.dialog.dismiss();
                }
            }

        } else if (payload.contains("datagenerated") || payload.contains("update") && alert_msg.equalsIgnoreCase("Update success for all fields. [1]")) {
            if (Utils.checkInternetConnection()) {
//                if (!CMN_Preferences.getisOneTouch(mContext)) {
                Log.e("Datachanged", "Datachanged");
                application.pullPartialData(IntentReceiverHelper.this);
//                }
                ////Log.d(TAG, "DataChanged notification, partial pull called");


            } else {
                ////Log.w(TAG, "Operation cannot be performed now.Check if your cellular data/wifi is turned ON.");


            }

        } else if (payload != null && payload.equalsIgnoreCase("One2OneMessage")) {
            MyApplication.INSTANCE.One2OneMsgRecvd = true;


        } else if (payload != null && payload.equalsIgnoreCase("duplicate_login") || payload.equalsIgnoreCase("Invalid Token")) {

            if (LocalPrefs.INSTANCE.hasUserSignedIn() && MyApplication.INSTANCE.mCurActivity != null) {

                MyApplication.INSTANCE.mCurActivity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        Toast.makeText(MyApplication.INSTANCE.mCurActivity,
                                alert_msg, Toast.LENGTH_LONG).show();

                    }
                });
                Utils.signout();
            }
        } else {

            Intent pushintent = new Intent(
                    application.AppConstants.getReceivedPushIntent());
            pushintent.putExtra("Message", alert_msg);

            mContext.sendBroadcast(pushintent);
        }
    }

    public void onNotificationOpened(String payload) {
        Intent launch = new Intent();
        if (payload != null && payload.equalsIgnoreCase("medication")) {
        } else {

            if (Utils.checkInternetConnection()) {


                if (LocalPrefs.INSTANCE.hasUserSignedIn()) {
                } else {
                }


            } else {
                ////Log.w(TAG, "Operation cannot be performed now.Check if your cellular data/wifi is turned ON.");


            }

        }
    }

    @Override
    public void finishedParsing(String _status) {
        Intent pushintent = new Intent(
                MyApplication.INSTANCE.AppConstants.getDownloadCompleteIntent());
        ////Log.d(TAG, "sending update complete intent");
        String alert_msg = "DownloadCompleted";
        pushintent.putExtra("Status", "success");  //Messaglistactivity expects a success to refresh ui
        mContext.sendBroadcast(pushintent);
        if (LocalPrefs.INSTANCE.autoSync()) {
            Intent UpdateIntent = new Intent(MyApplication.INSTANCE.AppConstants.getUploadCompleteIntent());
            mContext.sendBroadcast(UpdateIntent);
        }
    }

    @Override
    public void showDialog() {
        // empty body
    }

    @Override
    public void closeDialog() {
        // empty body
    }
}
