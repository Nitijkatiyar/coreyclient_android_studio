package com.coremobile.coreyhealth;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import com.coremobile.coreyhealth.googleAnalytics.CMN_Preferences;

// AutoLogoutManager is a singleton class
// mIdleCounter keeps ticking and any touch clears this counter from the respective activities dispatchTouchEvent.
public enum NetworkChangeManager {
    INSTANCE;

    private static final String TAG = "Corey_NetworkChangeManager";

    private NetworkChangeReceiver mNetworkChangeReceiver;
    boolean isWiFi;
    boolean isMobile;
    ConnectivityManager cm;
    WifiManager wifiMan;
    String wifiName;
    Boolean FirstTime = true;

    public void start() {
        Log.d(TAG, "start");

        if (mNetworkChangeReceiver != null) {
            Log.d(TAG, "start: already started");
            return;
        }

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        mNetworkChangeReceiver = new NetworkChangeReceiver();
        MyApplication.INSTANCE.registerReceiver(mNetworkChangeReceiver, filter );
        //   cm= ConnectivityManager();

        wifiName = Utils.getWifiName();
        isWiFi = Utils.isWifi();
        isMobile = Utils.ismobile();
        FirstTime = true;
    }

    public void stop() {
        Log.d(TAG, "stop");
        if (mNetworkChangeReceiver != null) {
            MyApplication.INSTANCE.unregisterReceiver(mNetworkChangeReceiver);
            mNetworkChangeReceiver = null;
        }
    }

    public void networkToast() {
        MyApplication.INSTANCE.mCurActivity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                Toast.makeText(MyApplication.INSTANCE.mCurActivity,
                        "Your session expired since your network connection changed", Toast.LENGTH_LONG).show();

            }
        });
    }

    private class NetworkChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (!action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                return;
            }
            Log.d(TAG, "Firstime- isWiFi" + FirstTime);
            if (!FirstTime) {


                if (isMobile) {
                    if (CMN_Preferences.getEnableAutoLogout(context)|| CMN_Preferences.getEnableNetworkChangeLogout(context)) {
                        if (Utils.ismobile()) return;
                        else networkToast();
                        Log.d(TAG, "mobile connection lostt");
                        Utils.signout();
                    }
                } else if (isWiFi) {
                    if (CMN_Preferences.getEnableAutoLogout(context)|| CMN_Preferences.getEnableNetworkChangeLogout(context)) {
                        if (Utils.getWifiName().equalsIgnoreCase(wifiName)) return;
                        else networkToast();
                        Log.d(TAG, "wifi changed");
                        Utils.signout();
                    }

                } else if (Utils.checkInternetConnection()|| CMN_Preferences.getEnableNetworkChangeLogout(context)) {
                    if (CMN_Preferences.getEnableAutoLogout(context)) {
                        networkToast();
                        Utils.signout();
                        Log.d(TAG, "Internet connectionlost");
                    }
                }
        /*
                int networkType = intent.getExtras().getInt(ConnectivityManager.EXTRA_NETWORK_TYPE);
                 isWiFi = networkType == ConnectivityManager.TYPE_WIFI;
                 isMobile = networkType == ConnectivityManager.TYPE_MOBILE; */

                Log.d(TAG, "Network changed - isWiFi" + isWiFi + "isMobile" + isMobile);


                //    Utils.signout();


            } else FirstTime = false;
        }
    }
}
