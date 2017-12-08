package com.coremobile.coreyhealth.Checkfornotification;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CMN_AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        new CMN_IsNotificationPendingWebService(context).execute();
//        if (!isAppIsInBackground(context)) {
//            if (CMN_Preferences.getisWenttoBackground(MyApplication.INSTANCE)) {
//                new CMN_AppUsageWebService(context).execute("FG");
//                CMN_Preferences.setisWenttoBackground(MyApplication.INSTANCE, false);
//            }
//        } else {
//            if (!CMN_Preferences.getisUserLoggedOut(MyApplication.INSTANCE) && !CMN_Preferences.getisWenttoBackground(MyApplication.INSTANCE)) {
//                new CMN_AppUsageWebService(context).execute("BG");
//                CMN_Preferences.setisWenttoBackground(MyApplication.INSTANCE, true);
//            }
//        }
    }

//    private boolean isAppIsInBackground(Context context) {
//        boolean isInBackground = true;
//        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
//            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
//            if (runningProcesses == null) {
//                return false;
//            }
//            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
//                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
//                    for (String activeProcess : processInfo.pkgList) {
//                        if (activeProcess.equals(context.getPackageName())) {
//                            isInBackground = false;
//                        }
//                    }
//                }
//            }
//        } else {
//            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
//            ComponentName componentInfo = taskInfo.get(0).topActivity;
//            if (componentInfo.getPackageName().equals(context.getPackageName())) {
//                isInBackground = false;
//            }
//        }

//        return isInBackground;
//    }
}