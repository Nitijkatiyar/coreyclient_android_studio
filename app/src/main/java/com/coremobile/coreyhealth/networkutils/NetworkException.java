package com.coremobile.coreyhealth.networkutils;

import android.accounts.NetworkErrorException;
import android.util.Log;

/**
 * Created by Akhilesh on 09/12/2015.
 */
public class NetworkException extends NetworkErrorException {

    private String message = null;

    public NetworkException(String message) {
        super(message);
        this.message = message;
        logMessage(message);
    }


    public NetworkException(String message, Exception cause) {
        super(message, cause);
        this.message = message;
        logMessage(message);

        //TODO check and handle type of exception
    }

    private void logMessage(String TAG, String error) {
        Log.d(TAG, error);
    }

    private void logMessage(String error) {
        Log.d("NetworkException::", error);
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

   /* @Override
    public void getMessage(PrintStream err) {
        super.getMessage(err);

    }*/
}
