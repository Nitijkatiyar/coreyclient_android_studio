package com.coremobile.coreyhealth.base;

import android.app.ListActivity;
import android.os.Bundle;

/**
 * Created by Nitij on 3/6/2017.
 */

public class CMN_AppListBaseActivity extends ListActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
