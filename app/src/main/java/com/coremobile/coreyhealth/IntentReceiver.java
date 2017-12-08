package com.coremobile.coreyhealth;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.coremobile.coreyhealth.googleAnalytics.CMN_Preferences;
import com.urbanairship.push.PushManager;
import com.urbanairship.push.PushPreferences;

import org.json.JSONObject;

import java.util.HashMap;

public class IntentReceiver extends BroadcastReceiver implements IServerConnect {

    static final String logTag = "IntentReceiver";
    static final String TAG = "Corey_IntentReceiver";
    Context myContext;
    ProgressDialog mProgressDialog;
    SharedPreferences mPrefs;
    public static final String CURRENT_USER = "CurrentUser";
    boolean isUserSignedIn;
    String appname;
    MyApplication application;
    Boolean mAutosync;

    @Override
    public void onReceive(final Context context, Intent intent) {
        myContext = context;

        mPrefs = myContext.getSharedPreferences(CURRENT_USER, 0);
        isUserSignedIn = mPrefs.getBoolean("UserSignedIn", false);
        mAutosync = mPrefs.getBoolean("RequireAutoSync", false);
        application = (MyApplication) context
                .getApplicationContext();
        Log.d(logTag, "Received intent: " + intent.toString());
        String action = intent.getAction();
        appname = application.AppConstants.getAppName();
        Log.d(logTag, "In on Receive of IntentReceiver action =" + action);

        IntentReceiverHelper helper = new IntentReceiverHelper(context);

        if (action.equals(PushManager.ACTION_PUSH_RECEIVED)) {
            Log.d(logTag, "Push received: " + intent.getExtras());
            /*Set<String> keys = intent.getExtras().keySet();
            for (String key : keys) {
                Log.i(logTag,"extra key: " + "Push Notification Extra: ["+key+" : " + intent.getStringExtra(key) + "]");

            }*/

            int id = intent.getIntExtra(PushManager.EXTRA_NOTIFICATION_ID, 0);

            String alert_msg = intent.getStringExtra(PushManager.EXTRA_ALERT);
            @SuppressWarnings("deprecation")
            String payload = intent.getStringExtra(PushManager.EXTRA_STRING_EXTRA);

            helper.onPushReceived(alert_msg, payload, "");

            Log.d(logTag,
                    "Received push notification. Alert: "
                            + intent.getStringExtra(PushManager.EXTRA_ALERT)
                            + ". Payload: "
                            + intent.getStringExtra(PushManager.EXTRA_STRING_EXTRA)
                            + ". NotificationID=" + id);


        } else if (action.equals(PushManager.ACTION_NOTIFICATION_OPENED)) {

            Log.d(logTag, "User clicked notification. Message: " + intent.getStringExtra(PushManager.EXTRA_ALERT)
                    + ". Payload: " + intent.getStringExtra(PushManager.EXTRA_STRING_EXTRA));


            String payload = intent.getStringExtra(PushManager.EXTRA_STRING_EXTRA);
            helper.onNotificationOpened(payload);
        } else if (action.equals(PushManager.ACTION_REGISTRATION_FINISHED)) {
            Log.d(logTag, "Registration complete. APID:" + intent.getStringExtra(PushManager.EXTRA_APID)
                    + ". Valid: " + intent.getBooleanExtra(PushManager.EXTRA_REGISTRATION_VALID, false));
            Thread t = new Thread(intent.getStringExtra(PushManager.EXTRA_APID)) {
                @Override
                public void run() {
                    String CMN_SERVER_REGISTER_API;
                    if (AppConfig.isAESEnabled) {
                        CMN_SERVER_REGISTER_API = "registerdeviceid_s.aspx?";
                    } else {
                        CMN_SERVER_REGISTER_API = "registerdeviceid.aspx?";
                    }
                    SharedPreferences mcurrentUserPref;
                    String CURRENT_USER = "CurrentUser";
                    PushPreferences prefs = PushManager.shared().getPreferences();
                    mcurrentUserPref = myContext.getSharedPreferences(CURRENT_USER, 0);
                    SharedPreferences.Editor editor = mcurrentUserPref.edit();
                    editor.putString("deviceId", prefs.getPushId());
                    editor.commit();

                    String CMN_SERVER_BASE_URL_DEFINE = mcurrentUserPref.getString("CMN_SERVER_BASE_URL_DEFINE", null);
                    //    String deviceId = getName();
                    String deviceId = prefs.getPushId();
                    if ((CMN_Preferences.getUserToken(context) == null) || (CMN_Preferences.getUserToken(context).length() == 0)) {
                        Log.d(logTag, "No user yet. Not registering the deviceid.");
                        return;
                    } else {
                        Log.d(logTag, "Registering deviceid " + deviceId + " for user : " + CMN_Preferences.getUserToken(context));
                    }


                    //String url = String.format(CMN_SERVER_BASE_URL_DEFINE + CMN_SERVER_REGISTER_API + "username=%s&password=%s&deviceid=%s&devicetype=Android",mUserName,mPassword, deviceId);
                    String url = CMN_SERVER_BASE_URL_DEFINE + CMN_SERVER_REGISTER_API;
                    HashMap<String, String> data = new HashMap<String, String>();
                    data.put("token", CMN_Preferences.getUserToken(context));
                    data.put("deviceid", deviceId);
                    data.put("devicetype", "Android");
                    data.put("appname", appname);
                    data.put("AppVersion", myContext.getResources().getString(R.string.app_version));
                    if (Utils.checkInternetConnection()) {
                        new VerifyUser(IntentReceiver.this, data).execute(url);
                    }
                }
            };
            t.start();
        }

    }

    @Override
    public void gotUserInfoFromServer(JSONObject json) {
        // TODO Auto-generated method stub

    }

    @Override
    public void showDialog() {
        // TODO Auto-generated method stub

    }

    @Override
    public void closeDialog() {
        // TODO Auto-generated method stub

    }

    @Override
    public void throwToast(String stringToToast) {
        // TODO Auto-generated method stub

    }


}
