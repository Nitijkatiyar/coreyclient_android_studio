package com.coremobile.coreyhealth;


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;

import com.coremobile.coreyhealth.base.CMN_AppBaseActivity;
import com.coremobile.coreyhealth.errorhandling.ActivityPackage;
import com.coremobile.coreyhealth.googleAnalytics.CMN_Preferences;

public class Home extends CMN_AppBaseActivity {
    private static final String TAG = "Corey_Home";
    SharedPreferences mGlobalPreferencePref;
    SharedPreferences mcurrentUserPref;
    protected boolean _active = true;
    protected int _splashTime = 15000; // time to display the splash screen in ms
    public static final String GLOBAL_PREFERENCE = "GlobalPreference";
    public static final String CURRENT_USER = "CurrentUser";
    public static final int PARSING_XML_COMPLETE = 2;


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.i(TAG, "onCreate Home");

        setContentView(R.layout.splashscreen);
        mGlobalPreferencePref = getSharedPreferences(GLOBAL_PREFERENCE, 0);
        mcurrentUserPref = getSharedPreferences(CURRENT_USER, 0);
        SharedPreferences.Editor editor = mGlobalPreferencePref.edit();
        boolean firstTime = mGlobalPreferencePref.getBoolean("FirstTime", true);
        ////Log.i(TAG,"firstTime: " + firstTime);


        boolean signup = false;
        if ((CMN_Preferences.getUserToken(Home.this).length() == 0))
            signup = true;

        //Log.i(TAG, "MyApplication.crystalData: " + MyApplication.crystalData.mOrganizationData.size());
        if (firstTime || MyApplication.crystalData.mOrganizationData.size() == 0) {
            //Log.i(TAG, "first time application starting");
            editor.putBoolean("FirstTime", false);
            editor.commit();
            finish();
            startActivity(new Intent().setClassName(getPackageName(), ActivityPackage.SplashScreen));
        } else if (!mcurrentUserPref.getBoolean("UserSignedIn", false) || (MyApplication.organizationData == null)) {
            //Log.i(TAG, "user not signed in");
            finish();
            if (signup) startActivity(new Intent().setClassName(getPackageName(), ActivityPackage.SignupActivity));
            else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    startActivity(new Intent().setClassName(getPackageName(), ActivityPackage.SigninWithFingerprintActivity));
                } else {
                    startActivity(new Intent().setClassName(getPackageName(), ActivityPackage.SigninActivity));
                }
            }
        } else {
            /*//Log.i(TAG,"MyApplication.crystalData: " + MyApplication.crystalData.mOrganizationData.size());
            do{
                //Log.i(TAG,"crystalData == null");
            }while(MyApplication.crystalData.mOrganizationData.size() == 0);*/
            finish();
            Intent messageActivityIntent = new Intent().setClassName(getPackageName(), ActivityPackage.MessageTabActivity);
            messageActivityIntent.putExtra("CallingActivity", getLocalClassName());
            startActivity(messageActivityIntent);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ////Log.i(TAG,"onDestroy");
        mGlobalPreferencePref.edit().clear();
        mGlobalPreferencePref.edit().commit();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

}
