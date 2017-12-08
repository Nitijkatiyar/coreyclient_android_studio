package com.coremobile.coreyhealth.loggerUtility;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.util.Log;

import com.coremobile.coreyhealth.MyApplication;
import com.coremobile.coreyhealth.R;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Created by NLSVM on 11/22/2017.
 */

public class Logger  {


    public static void Error(Context context,String Tag,String message){
        Log.e(context.getClass().getName()+"...TAG..."+Tag,message);
        logToFile(context,Tag,message);

    }

    public static void Verbose(Context context,String Tag,String message){
        Log.v(context.getClass().getName()+"..."+Tag,message);
        logToFile(context,Tag,message);
    }

    public static void Debug(Context context,String Tag,String message){
        Log.d(context.getClass().getName()+"..."+Tag,message);
        logToFile(context,Tag,message);
    }
    public static void Info(Context context,String Tag,String message){
        Log.i(context.getClass().getName()+"..."+Tag,message);
        logToFile(context,Tag,message);
    }

    private static void logToFile(Context context, String logMessageTag, String logMessage) {

        try {
            // Gets the log file from the root of the primary storage. If it does
            // not exist, the file is created.
            File appDirectory = new File(Environment.getExternalStorageDirectory() + "/CoreyLogs");
            File logDirectory = new File(appDirectory + "/log");
            File logFile = new File(logDirectory, "" + MyApplication.INSTANCE.getString(R.string.app_name) + ""
                    + MyApplication.INSTANCE.getResources().getString(R.string.app_version) + ".txt");

            if (!logFile.exists())
                logFile.createNewFile();
            // Write the message to the log with a timestamp
            BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true));
            writer.write(String.format("%1s [%2s]:%3s\r\n",
                    getDateTimeStamp(), logMessageTag, logMessage));
            writer.close();
            // Refresh the data so it can seen when the device is plugged in a
            // computer. You may have to unplug and replug to see the latest
            // changes
            MediaScannerConnection.scanFile(MyApplication.INSTANCE,
                    new String[]{logFile.toString()},
                    null,
                    null);
        } catch (IOException e) {
            Log.e("com.cindypotvin.Log", "Unable to log exception to file.");
        }
    }
    private static String getDateTimeStamp() {
        return new SimpleDateFormat("dd-yyyy-MM hh:mm:ss").format(new java.util.Date());
    }
}
