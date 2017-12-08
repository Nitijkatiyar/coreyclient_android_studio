package com.coremobile.coreyhealth.base;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * Created by Nitij on 3/6/2017.
 */

public class CMN_AppFragmentBaseActivity extends FragmentActivity {
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
