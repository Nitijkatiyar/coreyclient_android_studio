package com.coremobile.coreyhealth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class C2DMPushReceiver extends BroadcastReceiver
{

    private static final String TAG = "Corey_C2DMPushReceiver";
    @Override
    public void onReceive(Context arg0, Intent arg1)
    {
        Log.d(TAG, "In C2DMPushReceiver ");


    }

}
