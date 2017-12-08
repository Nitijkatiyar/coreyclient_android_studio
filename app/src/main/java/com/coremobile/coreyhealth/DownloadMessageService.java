package com.coremobile.coreyhealth;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.coremobile.coreyhealth.newui.IUpdateDataSyncListener;
import com.coremobile.coreyhealth.partialsync.GetAllContextWebService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class DownloadMessageService extends Service implements
        IPullDataFromServer {
    private String CMN_SERVER_BASE_URL_DEFINE;

    private static final String TAG = "Corey_DownloadMessageService";
    BroadcastReceiver pushNotificationReceiver;
    IntentFilter pushNotificationFilter;
    IntentFilter controlTimerFilter;
    BroadcastReceiver controlTimerReceiver;
    SharedPreferences mcurrentUserPref;
    String url;
    MyApplication application;
    public static final String CURRENT_USER = "CurrentUser";
    boolean isParsing = false;
    Timer timer;
    public ProgressDialog mDialog;
    private int prevLastMessageId = 0;
    public static Boolean mAutosync;
    private ArrayList<IUpdateDataSyncListener> mUpdateDataSyncListeners = new ArrayList<IUpdateDataSyncListener>();

    public class LocalBinder extends Binder {
        public DownloadMessageService getService() {
            return DownloadMessageService.this;
        }
    }

    // This is the object that receives interactions from clients.
    private final IBinder mBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = (MyApplication) getApplication();
        Log.d(TAG, "startService");
        mcurrentUserPref = getSharedPreferences(CURRENT_USER, 0);
        // JSONHelperClass jsonHelperClass = new JSONHelperClass();
        // CMN_SERVER_BASE_URL_DEFINE =
        // jsonHelperClass.getBaseURL(mcurrentUserPref.getString("Organization",
        // ""));
        CMN_SERVER_BASE_URL_DEFINE = mcurrentUserPref.getString(
                "CMN_SERVER_BASE_URL_DEFINE", "");
        Log.d(TAG, "Base url: " + CMN_SERVER_BASE_URL_DEFINE);
        // Log.i(TAG,"organization: " +
        // mcurrentUserPref.getString("Organization", ""));
        // Log.i(TAG,"base URL: " + CMN_SERVER_BASE_URL_DEFINE);
        mAutosync = mcurrentUserPref.getBoolean("RequireAutoSync", false);
        if (Utils.checkInternetConnection()) {

            pullData(false);


        }

        pushNotificationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                Log.d(TAG,
                        "Push notification received, stop any running Timers");
                Intent intent = new Intent("com.coremobile.corey.timercontrol");
                intent.putExtra("EXTRA_ACTION", "STOP");
                sendBroadcast(intent);

                if (Utils.checkInternetConnection()) {
                    Log.d(TAG, "PULL DATA CALLED");
                    pullData(false);
                    // }
                } else {
                    Toast.makeText(
                            getApplicationContext(),
                            "Operation cannot be performed now.Check if your cellular data/wifi is turned ON.",
                            Toast.LENGTH_SHORT).show();
                }
            }

        };

        controlTimerReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (timer == null) {
                    Log.d(TAG,
                            "control timer broadcast received, But NO Timer running.");
                    return;
                }
                String action = intent.getStringExtra("EXTRA_ACTION");
                Log.d(TAG, "control timer broadcast received action=" + action);
                if (action.equals("STOP")) {
                    timer.cancel();
                } else {
                    // schedulePullTask();
                }
            }
        };

        pushNotificationFilter = new IntentFilter();
        pushNotificationFilter.addAction(application.AppConstants.getPushNotificationReceivedIntent());
        //     .addAction("com.coremobile.coreyhealth.pushnotification");
        registerReceiver(pushNotificationReceiver, pushNotificationFilter );
        controlTimerFilter = new IntentFilter();
        //   controlTimerFilter.addAction("com.coremobile.coreyhealth.timercontrol");
        controlTimerFilter.addAction(application.AppConstants.getDownloadMessageServiceControlTimerFilter());
        registerReceiver(controlTimerReceiver, controlTimerFilter );

        // schedulePullTask();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "stopping service");
        unregisterReceiver(pushNotificationReceiver);
        unregisterReceiver(controlTimerReceiver);
        if (timer != null)
            timer.cancel();

    }

    private int getLastMessageid() {
        //  MyApplication application = (MyApplication) getApplication();
        return application.getLastMessageid();
    }

    @Override
    public void finishedParsing(String _status) {
        Log.d(TAG, "finishedParsing");
        MyApplication.INSTANCE.MsgTabActivityState++;
        closeDialog();
        //   MyApplication application = (MyApplication) getApplication();
        boolean newUser = mcurrentUserPref.getBoolean("NewUser", true);
        Log.d(TAG, "lastmessageid: " + prevLastMessageId
                + " getLastMessageid: " + getLastMessageid()
                + " different user: " + newUser);
        if (prevLastMessageId != getLastMessageid() || newUser) {
            Log.d(TAG, "lastMessageId != getLastMessageid()");
            //   Intent broadcastIntent = new Intent(
            //  "com.coremobile.coreyhealth.downloadcomplete");
           /*For tabs*/ 
       /*     Intent broadcastIntent = new Intent(
                    application.AppConstants.getDownloadCompleteIntent());
            sendBroadcast(broadcastIntent);
            */
            Log.d(TAG,
                    "messageListShown: "
                            + mcurrentUserPref.getBoolean("IsMessageListShown",
                            false));
            if (prevLastMessageId > 0
                    && prevLastMessageId != getLastMessageid()
                    && !(mcurrentUserPref.getBoolean("IsMessageListShown",
                    false))) {

            }

        }
        isParsing = false;
        //  sendBroadcast(new Intent(ACTION_COMPLETE));
     /*   try {
            Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.getMessage();
		}  */
        while (MyApplication.INSTANCE.ImagedownloadCount >= 1) {
            Log.d(TAG, "Imagedownload in progress =" + MyApplication.INSTANCE.ImagedownloadCount);
        }
        sendBroadcast(new Intent(application.AppConstants.getDownloadMessageServiceActionCompleteIntent()));
        for (IUpdateDataSyncListener listener : mUpdateDataSyncListeners) {
            if (listener != null) {
                listener.updateStatus(true);
            }
        }
    }

    //  public static final String ACTION_COMPLETE = "com.coreyhealth.downloader.action.COMPLETE";

    public void pullData(boolean b) {
        if (!mAutosync) {
            if (MyApplication.INSTANCE.inDemoMode()) {
                // in demo-mode (local calendar) pullData happens after uploading
                // local calendar events
                Log.d(TAG, "pullData(): inDemoMode, resetting prevLastMessageId to 0");
                if (mAutosync) prevLastMessageId = -1;

                //    sendBroadcast(new Intent(application.AppConstants.getDownloadMessageServiceActionCompleteIntent()));
                //    return;
            }
            prevLastMessageId = getLastMessageid();
        } else prevLastMessageId = getLastMessageid();
        MyApplication application = (MyApplication) getApplication();
        if (GetAllContextWebService.patientsContexts.size() > 1) {
            application.pullPartialData(this);
        } else {
            application.pullPartialData(this);
        }

    }

    public void showDialog() {
        mDialog = ProgressDialog.show(this, "", "Retrieving messages...", true);
    }

    public void closeDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    private void schedulePullTask() {
        if (timer != null) {
            // stop cur timer
            timer.cancel();
        }

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (Utils.checkInternetConnection()) {
                    Log.d(TAG, "pullDataTask triggered " + this);
                    pullData(false);
                }
            }
        }, 1000 * 60, 1000 * 60 * 5);
    }

    public void addUpdateDataSyncListener(
            IUpdateDataSyncListener iUpdateDataSyncListener) {

        if (!mUpdateDataSyncListeners.contains(iUpdateDataSyncListener)) {
            mUpdateDataSyncListeners.add(iUpdateDataSyncListener);
        }
    }

    public void removeUpdateDataSyncListener(
            IUpdateDataSyncListener iUpdateDataSyncListener) {
        if (mUpdateDataSyncListeners.contains(iUpdateDataSyncListener)) {
            mUpdateDataSyncListeners.remove(iUpdateDataSyncListener);
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

    private void stopTimer() {
        Log.d(TAG, "stopTimer");
        //    Intent intent = new Intent("com.coremobile.coreyhealth.timercontrol");
        Intent intent = new Intent(application.AppConstants.getDownloadMessageServiceControlTimerFilter());
        intent.putExtra("EXTRA_ACTION", "STOP");
        sendBroadcast(intent);
    }
}
