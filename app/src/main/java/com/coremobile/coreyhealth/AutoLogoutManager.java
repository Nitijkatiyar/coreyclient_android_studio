package com.coremobile.coreyhealth;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.coremobile.coreyhealth.googleAnalytics.CMN_Preferences;

import java.util.Timer;

// AutoLogoutManager is a singleton class
// mIdleCounter keeps ticking and any touch clears this counter from the respective activities dispatchTouchEvent.
public enum AutoLogoutManager {
    INSTANCE;

    private static final String TAG = "Corey_AutoLogoutManager";
    private static final int INTERVAL_MILLIS = 60 * 1000;
    //  private static final int IDLE_COUNTER_THRESHOLD = 15; // number of INTERVAL_MILLIS
    private static final int IDLE_COUNTER_THRESHOLD5 = 5; // number of INTERVAL_MILLIS
    private static final int IDLE_COUNTER_THRESHOLD10 = 10; // number of INTERVAL_MILLIS
    private static final int IDLE_COUNTER_THRESHOLD20 = 20; // number of INTERVAL_MILLIS

    private static final int IDLE_COUNTER_THRESHOLD = MyApplication.IDLE_COUNTER_THRESHOLD;
    //    public static  int IDLE_COUNTER_THRESHOLD = 15;
    public int mIdleCounter = 0;
    private Timer mTimer;
    private AutoLogoutReceiver mAutoLogoutReceiver;

    public void start() {
        Log.d(TAG, "start");

        if (mAutoLogoutReceiver != null) {
            Log.d(TAG, "start: already started");
            return;
        }

        IntentFilter filter = new IntentFilter(AutoLogoutReceiver.AUTO_LOGOUT_ALARM);
        mAutoLogoutReceiver = new AutoLogoutReceiver();
        MyApplication.INSTANCE.registerReceiver(mAutoLogoutReceiver, filter );

        AlarmManager alarmManager =
                (AlarmManager) MyApplication.INSTANCE.getSystemService(Context.ALARM_SERVICE);
        PendingIntent alarmIntent =
                PendingIntent.getBroadcast(
                        MyApplication.INSTANCE,
                        0,
                        new Intent(AutoLogoutReceiver.AUTO_LOGOUT_ALARM),
                        0);
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + INTERVAL_MILLIS, INTERVAL_MILLIS, alarmIntent);
    }

    public void stop() {
        Log.d(TAG, "stop");
        if (mAutoLogoutReceiver != null) {
            MyApplication.INSTANCE.unregisterReceiver(mAutoLogoutReceiver);
            mAutoLogoutReceiver = null;
            Log.d(TAG, "unregistered");
        }
    }

    private class AutoLogoutReceiver extends BroadcastReceiver {
        public static final String AUTO_LOGOUT_ALARM = "com.coremobile.coreyhealth.AUTO_LOGOUT_ALARM";

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean hasUserSignedIn = LocalPrefs.INSTANCE.hasUserSignedIn();
            Log.d(TAG, "AutoLogoutManager timer trigger " + hasUserSignedIn + ", " + mIdleCounter);
            if (hasUserSignedIn) {
                ++mIdleCounter;

                if (mIdleCounter > IDLE_COUNTER_THRESHOLD) {
                    Log.d(TAG, "AutoLogoutManager TIMEOUT perform-auto-logout");
                    if (CMN_Preferences.getEnableAutoLogout(context) || CMN_Preferences.getEnableTimeInactivityLogout(context)) {
                        MyApplication.INSTANCE.mCurActivity.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                Toast.makeText(MyApplication.INSTANCE.mCurActivity,
                                        "Your session expired due to inactivity for long time", Toast.LENGTH_LONG).show();

                            }
                        });
                        Utils.signout();
                        mIdleCounter = 0;
                    }
                }
            }
        }
    }
}
