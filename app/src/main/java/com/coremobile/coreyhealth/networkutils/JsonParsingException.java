package com.coremobile.coreyhealth.networkutils;

import android.util.Log;

import org.json.JSONException;

/**
 * Created by Akhilesh on 26/10/2015.
 */
public class JsonParsingException extends JSONException {
    private String message = null;

    public JsonParsingException(String message) {
        super(message);
        this.message = message;
        logMessage(message);
    }

    private void logMessage(String TAG, String error) {
        Log.d(TAG, error);
    }

    private void logMessage(String error) {
        Log.d("JsonParsingException::", error);
    }

    private void logMessage(String message, Throwable cause) {
        Log.d(message, cause.getMessage());
    }

    @Override
    public String toString() {
        return message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    /*@Override
    public void getMessage(PrintStream err) {
        super.getMessage(err);
    }*/
}
