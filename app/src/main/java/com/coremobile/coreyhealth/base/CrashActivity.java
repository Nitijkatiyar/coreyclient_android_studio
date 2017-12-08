package com.coremobile.coreyhealth.base;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;

import com.coremobile.coreyhealth.R;

public class CrashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash);
        AlertDialog.Builder signoutBuilder = new AlertDialog.Builder(
                CrashActivity.this);
        signoutBuilder
                .setTitle("Error");
        signoutBuilder.setMessage("App has stopped working, Try deleting the app data from settings and try again, if problem still persist try uninstalling and installing app again.");
        signoutBuilder.setPositiveButton("Proceed to Clear data",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                        finish();
                    }
                });
        signoutBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        dialog.dismiss();
                        finish();
                    }
                });
        signoutBuilder.show();

    }

}
