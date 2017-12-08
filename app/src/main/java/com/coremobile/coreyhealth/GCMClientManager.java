package com.coremobile.coreyhealth;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

/**
 * Class to manage Google Cloud Messaging as a client
 * reference: https://developer.android.com/google/gcm/client.html
 */
public class GCMClientManager {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	private static final String TAG = "Corey_GoogleCloudMessaging.GCMClientManager";
 //   private static final String SENDER_ID = "340144732350";
	//private static final String SENDER_ID = "425886649714";
	private static final String SENDER_ID = MyApplication.INSTANCE.AppConstants.getPSharpGcmSender();
    private final static String CMN_PUSH_SHARP_REGISTER_API = "registerdeviceid2.aspx?";

    private GoogleCloudMessaging gcm;
    private String regid;

	/**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    public static boolean checkPlayServices(Activity activity) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.w(TAG, "This device is not supported.");
            }
            return false;
        }
        return true;
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    public String getRegistrationId(Context context) {
        String regid = LocalPrefs.INSTANCE.deviceId();
        if (Utils.isEmpty(regid)) {
            Log.d(TAG, "Registration not found.");
            return "";
        }

        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = LocalPrefs.INSTANCE.appVersion();
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.d(TAG, "App version changed.");
            return "";
        }
        return regid;
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    public void registerInBackground(final Context context) {
        new AsyncTask<Object, Void, String>() {
            @Override
            protected String doInBackground(Object... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    storeRegistrationId(context, regid);
                    sendRegistrationIdToBackend(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.d(TAG, "registration response: " + msg);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null, null, null);
    }


    /////////////////////////////////////////////////////////
    /////////////////  PRIVATE METHODS  /////////////////////
    /////////////////////////////////////////////////////////
    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        int appVersion = getAppVersion(context);
        Log.d(TAG, "Saving regId on app version " + appVersion + ", " + regId);
        LocalPrefs.INSTANCE.setDeviceId(regId);
        LocalPrefs.INSTANCE.setAppVersion(appVersion);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private void sendRegistrationIdToBackend(final Context context, String regid) {
        Log.d(TAG, "sendRegistrationIdToBackend " + regid);
        if(Utils.checkInternetConnection()) {
            if (Utils.isEmpty(Utils.getServerBaseUrl())) {
                Log.w(TAG, "BaseUrl not available yet");
                return;
            }
            String appName = null;
            appName=MyApplication.INSTANCE.AppConstants.getAppName();
            String deviceId = LocalPrefs.INSTANCE.deviceId();
            Log.d(TAG,"GCM DEVICE ID: " + deviceId);
            String url = Utils.getServerBaseUrl() + CMN_PUSH_SHARP_REGISTER_API;
            HashMap<String, String> data = new HashMap<String, String>();
            data.put("username", LocalPrefs.INSTANCE.username());
            data.put("password", LocalPrefs.INSTANCE.password());
            data.put("deviceid", deviceId);
            data.put("devicetype", "Android");
            data.put("organization", LocalPrefs.INSTANCE.organization());
            data.put("appname", appName);
            data.put("AppVersion", context.getResources().getString(R.string.app_version));
            Log.d(TAG,"registration url: " + url);
            new VerifyUser(new IServerConnect() {
                    @Override
                    public void gotUserInfoFromServer(JSONObject json) {
                        // empty body
                        Log.d(TAG, "sendRegistrationIdToBackend gotUserInfoFromServer: " + json);
                    }

                    @Override
                    public void showDialog() {
                        // empty body
                    }

                    @Override
                    public void closeDialog() {
                        // empty body
                    }

                    @Override
                    public void throwToast(String stringToToast) {
                        // empty body
                    }
                },
                data).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
        } else {
            Log.w(TAG, "Unable to access the Network. Please check your Network connectivity and try again.");
        }
    }
}
