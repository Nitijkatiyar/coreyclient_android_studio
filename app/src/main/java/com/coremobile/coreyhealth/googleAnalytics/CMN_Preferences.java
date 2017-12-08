package com.coremobile.coreyhealth.googleAnalytics;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * @author iRESLab
 */
public class CMN_Preferences {

    private static final String COREY_IS_FIRST_TIME = "firsttimelogin";
    static SharedPreferences naavis_preference;

    public static final String KEY_DEFAULT_PREFERENCE = "com.versaworks.cv";
    public static final String COREY_PREF = "cascadevalleypreferences";


    public static SharedPreferences getDefaultPref(Context context) {
        if (naavis_preference == null) {
            naavis_preference = context.getApplicationContext().getSharedPreferences(
                    KEY_DEFAULT_PREFERENCE, Context.MODE_PRIVATE);
        }
        return naavis_preference;
    }

    public static void clearpreferences(Context context) {
        Editor editor = getDefaultPref(context).edit();
        editor.clear();
        editor.commit();
    }


    public static final String COREY_GOOGLE_FIT = "GOOGLEFIT";

    public static void setGoogleFitEnable(Context context, boolean isEnable) {

        Editor editor = getDefaultPref(context).edit();
        editor.putBoolean(COREY_GOOGLE_FIT, isEnable);
        editor.commit();
    }

    public static boolean isGoogleFitEnable(Context context) {
        return getDefaultPref(context).getBoolean(COREY_GOOGLE_FIT, true);
    }


    public static final String COREY_CONTEXT_ID = "contextid";

    public static void setContextId(Context context, String id) {

        Editor editor = getDefaultPref(context).edit();
        editor.putString(COREY_CONTEXT_ID, id);
        editor.commit();
    }

    public static String getContextId(Context context) {
        return getDefaultPref(context).getString(COREY_CONTEXT_ID, "");
    }


    public static final String COREY_CURRENT_CONTEXT_ID = "currentcontextid";

    public static void setCurrentContextId(Context context, String id) {
        Editor editor = getDefaultPref(context).edit();
        editor.putString(COREY_CURRENT_CONTEXT_ID, id);
        editor.commit();
    }

    public static String getCurrentContextId(Context context) {
        return getDefaultPref(context).getString(COREY_CURRENT_CONTEXT_ID, "").toUpperCase();
    }

    public static final String COREY_LAST_VIEWED_CONTEXT_ID = "lastviewedcontextid";

    public static void setLastViewedContextId(Context context, String id) {
        Editor editor = getDefaultPref(context).edit();
        editor.putString(COREY_LAST_VIEWED_CONTEXT_ID, id);
        editor.commit();
    }

    public static String getLastViewedContextId(Context context) {
        return getDefaultPref(context).getString(COREY_LAST_VIEWED_CONTEXT_ID, "").toUpperCase();
    }

    public static final String COREY_SCHEDULE_CONTEXT_ID = "schedulecontextid";

    public static void setScheduleContextId(Context context, String id) {

        Editor editor = getDefaultPref(context).edit();
        editor.putString(COREY_SCHEDULE_CONTEXT_ID, id);
        editor.commit();
    }

    public static String getScheduleContextId(Context context) {
        return getDefaultPref(context).getString(COREY_SCHEDULE_CONTEXT_ID, "").toUpperCase();
    }


    public static final String COREY_ORGANIZATION_NAME = "organizationName";

    public static void setOrganizationName(Context context, String id) {

        Editor editor = getDefaultPref(context).edit();
        editor.putString(COREY_ORGANIZATION_NAME, id);
        editor.commit();
    }

    public static String getOrganizationName(Context context) {
        return getDefaultPref(context).getString(COREY_ORGANIZATION_NAME, "");
    }

    public static final String COREY_BASE_URL = "BaseUrl";

    public static void setBaseUrl(Context context, String id) {

        Editor editor = getDefaultPref(context).edit();
        editor.putString(COREY_BASE_URL, id);
        editor.commit();
    }

    public static String getBaseUrl(Context context) {
        return getDefaultPref(context).getString(COREY_BASE_URL, "");
    }

    public static final String COREY_LOGGEDIN_USER_ID = "loggedinuserid";

    public static void setLoggedInUserId(Context context, String id) {

        Editor editor = getDefaultPref(context).edit();
        editor.putString(COREY_LOGGEDIN_USER_ID, id);
        editor.commit();
    }

    public static String getLoggedInUserId(Context context) {
        return getDefaultPref(context).getString(COREY_LOGGEDIN_USER_ID, "");
    }


    public static final String COREY_ONE_TOUCH_ICON_TOUCHED = "icontouch";

    public static void setisOneTouch(Context context, boolean id) {

        Editor editor = getDefaultPref(context).edit();
        editor.putBoolean(COREY_ONE_TOUCH_ICON_TOUCHED, id);
        editor.commit();
    }

    public static boolean getisOneTouch(Context context) {
        return getDefaultPref(context).getBoolean(COREY_ONE_TOUCH_ICON_TOUCHED, false);
    }

    public static final String COREY_ACK_TIME_OUT = "timeouttime";

    public static void setApiAcknowleedgeTimeOut(Context context, String id) {

        Editor editor = getDefaultPref(context).edit();
        editor.putString(COREY_ACK_TIME_OUT, id);
        editor.commit();
    }

    public static String getApiAcknowleedgeTimeOut(Context context) {
        return getDefaultPref(context).getString(COREY_ACK_TIME_OUT, "");
    }

    public static final String COREY_UPDATE_TIME_WAIT = "updatewaittime";

    public static void setUpdateWaittime(Context context, Long id) {

        Editor editor = getDefaultPref(context).edit();
        editor.putLong(COREY_UPDATE_TIME_WAIT, id);
        editor.commit();
    }

    public static Long getUpdateWaittime(Context context) {
        return getDefaultPref(context).getLong(COREY_UPDATE_TIME_WAIT, 00);
    }

    public static final String COREY_UPDATE_TIME_OUT = "updatewaitout";

    public static void setUpdateWaitout(Context context, Long id) {

        Editor editor = getDefaultPref(context).edit();
        editor.putLong(COREY_UPDATE_TIME_OUT, id);
        editor.commit();
    }

    public static Long getUpdateWaitout(Context context) {
        return getDefaultPref(context).getLong(COREY_UPDATE_TIME_OUT, 00);
    }


    public static final String COREY_NEXT_TIME_TO_UPDATE = "nxttimetoupdate";

    public static void setNexttimeToUpdate(Context context, Long id) {

        Editor editor = getDefaultPref(context).edit();
        editor.putLong(COREY_NEXT_TIME_TO_UPDATE, id);
        editor.commit();
    }

    public static Long getNexttimeToUpdate(Context context) {
        return getDefaultPref(context).getLong(COREY_NEXT_TIME_TO_UPDATE, 00);
    }

    public static final String COREY_NEW_PATIENT_ADDED = "newpatientadded";

    public static void setisNewPatientAdded(Context context, boolean id) {

        Editor editor = getDefaultPref(context).edit();
        editor.putBoolean(COREY_NEW_PATIENT_ADDED, id);
        editor.commit();
    }

    public static Boolean getisNewPatientAdded(Context context) {
        return getDefaultPref(context).getBoolean(COREY_NEW_PATIENT_ADDED, false);
    }


    public static final String COREY_ENABLE_AUTO_LOGOUT = "enable_auto_logout";

    public static void setEnableAutoLogout(Context context, boolean id) {

        Editor editor = getDefaultPref(context).edit();
        editor.putBoolean(COREY_ENABLE_AUTO_LOGOUT, id);
        editor.commit();
    }

    public static Boolean getEnableAutoLogout(Context context) {
        return getDefaultPref(context).getBoolean(COREY_ENABLE_AUTO_LOGOUT, true);
    }

    public static final String COREY_ENABLE_NETWORK_CHANGE_LOGOUT = "enable_network_change_logout";

    public static void setEnableNetworkChangeLogout(Context context, boolean id) {

        Editor editor = getDefaultPref(context).edit();
        editor.putBoolean(COREY_ENABLE_NETWORK_CHANGE_LOGOUT, id);
        editor.commit();
    }

    public static Boolean getEnableNetworkChangeLogout(Context context) {
        return getDefaultPref(context).getBoolean(COREY_ENABLE_NETWORK_CHANGE_LOGOUT, true);
    }

    public static final String COREY_ENABLE_TIME_INACTIVITY_LOGOUT = "enable_time_inactivity_logout";

    public static void setEnableTimeInactivityLogout(Context context, boolean id) {

        Editor editor = getDefaultPref(context).edit();
        editor.putBoolean(COREY_ENABLE_TIME_INACTIVITY_LOGOUT, id);
        editor.commit();
    }

    public static Boolean getEnableTimeInactivityLogout(Context context) {
        return getDefaultPref(context).getBoolean(COREY_ENABLE_TIME_INACTIVITY_LOGOUT, true);
    }

    public static final String COREY_USER_LOGGED_OUT = "is_user_logged_out";

    public static void setisUserLoggedOut(Context context, boolean id) {

        Editor editor = getDefaultPref(context).edit();
        editor.putBoolean(COREY_USER_LOGGED_OUT, id);
        editor.commit();
    }

    public static Boolean getisUserLoggedOut(Context context) {
        return getDefaultPref(context).getBoolean(COREY_USER_LOGGED_OUT, false);
    }

    public static final String COREY_SEND_DEVICE_LOG = "send_device_log";

    public static void setisSendDeviceLog(Context context, boolean id) {

        Editor editor = getDefaultPref(context).edit();
        editor.putBoolean(COREY_SEND_DEVICE_LOG, id);
        editor.commit();
    }

    public static Boolean getisSendDeviceLog(Context context) {
        return getDefaultPref(context).getBoolean(COREY_SEND_DEVICE_LOG, false);
    }

    public static final String COREY_USE_FINGER_PRINT_AUTH = "useFingerPrint";

    public static void setisFingerPrintAuthEnabled(Context context, boolean id) {

        Editor editor = getDefaultPref(context).edit();
        editor.putBoolean(COREY_USE_FINGER_PRINT_AUTH, id);
        editor.commit();
    }

    public static Boolean getisFingerPrintAuthEnabled(Context context) {
        return getDefaultPref(context).getBoolean(COREY_USE_FINGER_PRINT_AUTH, false);
    }

    public static final String COREY_USEE_ENABLED_FINGER_PRINT_AUTH = "useDeniedFingerPrint";

    public static void setisUserEnabledFingerPrint(Context context, boolean id) {

        Editor editor = getDefaultPref(context).edit();
        editor.putBoolean(COREY_USEE_ENABLED_FINGER_PRINT_AUTH, id);
        editor.commit();
    }

    public static Boolean getisUserEnabledFingerPrint(Context context) {
        return getDefaultPref(context).getBoolean(COREY_USEE_ENABLED_FINGER_PRINT_AUTH, true);
    }

    public static final String COREY_APP_WENT_BACKGROUND = "wenttobackground";

    public static void setisWenttoBackground(Context context, boolean id) {

        Editor editor = getDefaultPref(context).edit();
        editor.putBoolean(COREY_APP_WENT_BACKGROUND, id);
        editor.commit();
    }

    public static Boolean getisWenttoBackground(Context context) {
        return getDefaultPref(context).getBoolean(COREY_APP_WENT_BACKGROUND, false);
    }

    public static final String COREY_APP_CAME_TO_FOREGROUND = "cametoforeground";

    public static void setisCametoForeground(Context context, boolean id) {

        Editor editor = getDefaultPref(context).edit();
        editor.putBoolean(COREY_APP_CAME_TO_FOREGROUND, id);
        editor.commit();
    }

    public static Boolean getisCametoForeground(Context context) {
        return getDefaultPref(context).getBoolean(COREY_APP_CAME_TO_FOREGROUND, false);
    }

    public static void setisUsingAppFirstTime(Context context, boolean id) {

        Editor editor = getDefaultPref(context).edit();
        editor.putBoolean(COREY_IS_FIRST_TIME, id);
        editor.commit();
    }

    public static Boolean getisUsingAppFirstTime(Context context) {
        return getDefaultPref(context).getBoolean(COREY_IS_FIRST_TIME, true);
    }


    public static final String COREY_CHECK_NOTIFICATION_TIME = "checknotificationtime";

    public static void setCheckNotificationtime(Context context, Long id) {

        Editor editor = getDefaultPref(context).edit();
        editor.putLong(COREY_CHECK_NOTIFICATION_TIME, id);
        editor.commit();
    }

    public static Long getCheckNotificationime(Context context) {
        return getDefaultPref(context).getLong(COREY_CHECK_NOTIFICATION_TIME, 60000);
    }

    public static final String COREY_PATIENT_DISPLAY_DASHBOARD = "displaydashboard";

    public static void setisPatientDisplayedonDashboard(Context context, boolean id) {

        Editor editor = getDefaultPref(context).edit();
        editor.putBoolean(COREY_PATIENT_DISPLAY_DASHBOARD, id);
        editor.commit();
    }

    public static Boolean getisPatientDisplayedonDashboard(Context context) {
        return getDefaultPref(context).getBoolean(COREY_PATIENT_DISPLAY_DASHBOARD, false);
    }

    public static final String COREY_LOGGED_IN_USER_TOKEN = "loogedInUserToken";

    public static void setUserToken(Context context, String token) {

        Editor editor = getDefaultPref(context).edit();
        editor.putString(COREY_LOGGED_IN_USER_TOKEN, token);
        editor.commit();
    }

    public static String getUserToken(Context context) {
        return getDefaultPref(context).getString(COREY_LOGGED_IN_USER_TOKEN, "");
    }


    public static final String COREY_NEW_EPRO_ADDED = "neweproadded";

    public static void setePROadded(Context context, boolean token) {

        Editor editor = getDefaultPref(context).edit();
        editor.putBoolean(COREY_NEW_EPRO_ADDED, token);
        editor.commit();
    }

    public static boolean getePROadded(Context context) {
        return getDefaultPref(context).getBoolean(COREY_NEW_EPRO_ADDED, false);
    }

    public static final String COREY_ROW0_CONTENT = "row0content";

    public static void setrow0content(Context context, String id) {

        Editor editor = getDefaultPref(context).edit();
        editor.putString(COREY_ROW0_CONTENT, id);
        editor.commit();
    }

    public static String getrow0content(Context context) {
        return getDefaultPref(context).getString(COREY_ROW0_CONTENT, "");
    }

    public static final String COREY_USE_SCHEDULING = "uses_scheduling";

    public static void setUseScheduling(Context context, boolean id) {

        Editor editor = getDefaultPref(context).edit();
        editor.putBoolean(COREY_USE_SCHEDULING, id);
        editor.commit();
    }

    public static boolean getUseScheduling(Context context) {
        return getDefaultPref(context).getBoolean(COREY_USE_SCHEDULING, false);
    }

    public static final String COREY_INTAKE_CONTEXT = "intake_context";

    public static void setIntakeContext(Context context, String id) {

        Editor editor = getDefaultPref(context).edit();
        editor.putString(COREY_INTAKE_CONTEXT, id);
        editor.commit();
    }

    public static String getIntakeContext(Context context) {
        return getDefaultPref(context).getString(COREY_INTAKE_CONTEXT, "");
    }

}
