package com.coremobile.coreyhealth.errorhandling;

import android.app.Activity;

import com.coremobile.coreyhealth.MyApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nitij on 5/27/2017.
 */

public class ActivityPackage {
    public static String SelectPatientsActivity = MyApplication.INSTANCE.getPackageName() + ".nativeassignment.SelectPatientsActivityCMN";
    public static String WebViewActivity = MyApplication.INSTANCE.getPackageName() + ".WebViewActivityCMN";
    public static String AnalyticsGraphActivity = MyApplication.INSTANCE.getPackageName() + ".analytics.CMN_AnalyticsGraphActivityCMN";
    public static String AddNewEproActivity = MyApplication.INSTANCE.getPackageName() + ".native_epro.CMN_AddNewEproActivity";
    public static String ToUserListActivity = MyApplication.INSTANCE.getPackageName() + ".messaging.CMN_ToUserListActivity";
    public static String MessageActivityCMN = MyApplication.INSTANCE.getPackageName() + ".newui.MessageActivityCMN";
    public static String SplashScreen = MyApplication.INSTANCE.getPackageName() + ".SplashScreen";
    public static String SignupActivity = MyApplication.INSTANCE.getPackageName() + ".SignupActivityCMN";
    public static String SigninWithFingerprintActivity = MyApplication.INSTANCE.getPackageName() + ".SigninWithFingerprintActivityCMN";
    public static String SigninActivity = MyApplication.INSTANCE.getPackageName() + ".SigninActivityCMN";
    public static String MessageTabActivity = MyApplication.INSTANCE.getPackageName() + ".newui.MessageTabActivityCMN";
    public static String YoutubeVideoActivity = MyApplication.INSTANCE.getPackageName() + ".googleAnalytics.CMN_YoutubeVideoActivity";
    public static String SumMenuActivity = MyApplication.INSTANCE.getPackageName() + ".SumMenuActivity";
    public static String GoogleOauthActivity = MyApplication.INSTANCE.getPackageName() + ".GoogleOauthActivity";
    public static String SalesforceOAuthActivity = MyApplication.INSTANCE.getPackageName() + ".SalesforceOAuthActivity";
    public static String AddReminderInputDataActivity = MyApplication.INSTANCE.getPackageName() + ".providerreminders.AddReminderInputDataActivityCMN";
    public static String Epro_number_of_options = MyApplication.INSTANCE.getPackageName() + ".native_epro.CMN_Epro_number_of_options";
    public static String DependentePROsActivity = MyApplication.INSTANCE.getPackageName() + ".native_epro.CMN_DependentePROsActivity";

    public static List<Activity> activityList;

    public static List<Activity> getActivityList() {
        if(activityList == null){
            return new ArrayList<>();
        }
        return activityList;
    }

    public static void setActivityList(Activity activity) {
        if (activityList == null) {
            activityList = new ArrayList<>();
        }
        activityList.add(activity);
    }

}
