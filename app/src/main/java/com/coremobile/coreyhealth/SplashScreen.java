package com.coremobile.coreyhealth;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;

import com.coremobile.coreyhealth.Checkfornotification.CMN_AlarmReceiver;
import com.coremobile.coreyhealth.base.CMN_AppBaseActivity;
import com.coremobile.coreyhealth.errorhandling.ActivityPackage;

public class SplashScreen extends CMN_AppBaseActivity {
    private static final String TAG = "Corey_Splash Screen";
    protected int _splashTime = 200000; // time to display the splash screen in ms
    public static final String CURRENT_USER = "CurrentUser";
    private static final int MISSING_CONFIGURATION = 5;
    JSONHelperClass jsonHelperClass;

    SharedPreferences mcurrentUserPref;

    public Dialog onCreateDialog(int id) {
        switch (id) {
            case MISSING_CONFIGURATION:
                AlertDialog.Builder missingDialog = new AlertDialog.Builder(this);
                missingDialog.setTitle("Message");
                missingDialog.setMessage("Failed to load basic configuration from the server. Please check your network settings and perform Refresh Configuration from the Menu");
                missingDialog.setCancelable(false);
                missingDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        gotoNextScreen();
                    }
                });

                return missingDialog.create();
        }


        return null;
    }

    protected void gotoNextScreen() {
        finish();
        //Log.i(TAG, "GotoNextScreen of SplashScreen");

        boolean signup = false;
        //  if ((mUserNameStr.length() == 0) && (passwordStr.length() == 0) && (OrganizationStr.length() == 0)) signup = true;

        if (signup) {
            startActivity(new Intent().setClassName(getPackageName(), ActivityPackage.SignupActivity));
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                startActivity(new Intent().setClassName(getPackageName(), ActivityPackage.SigninWithFingerprintActivity));
            } else {
                startActivity(new Intent().setClassName(getPackageName(), ActivityPackage.SigninActivity));
            }
        }
        if (!mcurrentUserPref.getBoolean("UserSignedIn", false) ||
                (MyApplication.organizationData == null) ||
                (MyApplication.crystalData == null) ||
                (MyApplication.crystalData.mOrganizationData == null) ||
                (MyApplication.crystalData.mOrganizationData.size() == 0)) {
        } else {
            Intent messageActivityIntent = new Intent().setClassName(getPackageName(), ActivityPackage.MessageTabActivity);

            messageActivityIntent.putExtra("CallingActivity", getLocalClassName());
            startActivity(messageActivityIntent);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);
        ImageView Splash = (ImageView) findViewById(R.id.imageView1);

        mcurrentUserPref = getSharedPreferences(CURRENT_USER, 0);

        AlarmManager alarmManager=(AlarmManager)  getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(SplashScreen.this, CMN_AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(SplashScreen.this, 0, intent, 0);
        alarmManager.cancel(pendingIntent);

        // Splash.setImageDrawable(getResources().getDrawable(R.drawable.splash_fg));
        Splash.setImageDrawable(getResources().getDrawable(MyApplication.INSTANCE.AppConstants.getSplashId()));
        Thread splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    while (MyApplication._parsing && (waited < _splashTime)) {
                        //Log.i(TAG, "MyApplication._parsing && (waited < _splashTime)" + MyApplication._parsing + _splashTime);
                        sleep(100);
                        if (MyApplication._parsing) {
                            ////Log.i(TAG,"if(MyApplication._parsing) ");
                            waited += 100;
                        }
                    }
                } catch (InterruptedException e) {
                    // do nothing
                } finally {
                    ////Log.i(TAG,"YES CONFIGURATION");
                    //Log.i(TAG, "MyApplication._parsing = " + MyApplication._parsing + " _splashTime)" + _splashTime);
                    gotoNextScreen();

                }
            }
        };
        splashTread.start();


    }
}
