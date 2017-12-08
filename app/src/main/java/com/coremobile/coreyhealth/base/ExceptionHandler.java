package com.coremobile.coreyhealth.base;

import android.app.Activity;
import android.content.Intent;

public class ExceptionHandler implements
        java.lang.Thread.UncaughtExceptionHandler {
    private final Activity myContext;
    private final String LINE_SEPARATOR = "\n";

    public ExceptionHandler(Activity context) {
        myContext = context;
    }

    public void uncaughtException(Thread thread, Throwable exception) {
        Intent intent = new Intent(myContext, CrashActivity.class);
        myContext.startActivity(intent);

    }

}