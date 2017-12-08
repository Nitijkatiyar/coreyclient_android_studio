package com.coremobile.coreyhealth.Checkfornotification;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class PendingNotificationService extends Service {

    private static final String TAG = "PendingNotificationService";
    private final int INTERVAL = 15 * 1000;
    private Timer timer = new Timer();
    public static boolean isRunning = false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Service#onCreate() Called by the system when the service
     * is first created. Do not call this method directly.
     */
    @Override
    public void onCreate() {
        isRunning = true;
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                new CMN_IsNotificationPendingWebService(PendingNotificationService.this).execute();
            }
        }, 0, INTERVAL);
        return super.onStartCommand(intent, flags, startId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Service#onDestroy() Service is destroyed when user stop
     * the service
     */
    @Override
    public void onDestroy() {
//        Toast.makeText(this, "Stop Service", Toast.LENGTH_SHORT).show();
        if (timer != null) {
            timer.cancel();
        }
        isRunning = false;
        super.onDestroy();
    }
}