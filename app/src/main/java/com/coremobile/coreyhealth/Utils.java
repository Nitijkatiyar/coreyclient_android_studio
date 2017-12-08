package com.coremobile.coreyhealth;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.coremobile.coreyhealth.Checkfornotification.PendingNotificationService;
import com.coremobile.coreyhealth.base.CMN_AppUsageWebService;
import com.coremobile.coreyhealth.clientcertificate.CMN_HTTPSClient;
import com.coremobile.coreyhealth.genericviewandsurvey.CMN_JsonConstants;
import com.coremobile.coreyhealth.googleAnalytics.CMN_Preferences;
import com.coremobile.coreyhealth.newui.Fields;
import com.coremobile.coreyhealth.newui.MessageTabActivityCMN;
import com.coremobile.coreyhealth.newui.RowDisplayObject;
import com.coremobile.coreyhealth.scheduling.ModalitySchedulingActivity;
import com.coremobile.coreyhealth.scheduling.SpecialitySchedulingActivity;
import com.coremobile.coreyhealth.scheduling.SurgerySchedulingActivity;
import com.coremobile.coreyhealth.scheduling.schedulingcalender.CalenderActivity;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.TimeZone;

import static com.coremobile.coreyhealth.newui.MessageTabActivityCMN.patientPosition;

public class Utils {
    private static String TAG = "utils";
    public static String schType = "";
    public static boolean viewSchedule = false;
    public static boolean fromIntake = false;
    public static boolean dateSchedule = false;


    public static void setAppTitle(Activity activity, String title) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            activity.setTitle(title);
        } else {
            View v = activity.getLayoutInflater().inflate(R.layout.layoutheader, null);

            TextView header = (TextView) v.findViewById(R.id.layout_header);
            header.setText(title);
        }
    }

    public static String converttime2utc(String localtime) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        df.setTimeZone(TimeZone.getDefault());

        java.util.Date date = null;
        try {
            date = (java.util.Date) df.parse(localtime);
        } catch (ParseException e) {
            e.getMessage();
        }
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        String formattedDate = df.format(date);
//        Log.d(TAG, "formattedDate = " + formattedDate);
        return formattedDate;

    }

    public static String convertGenerictime2utc(String localtime) {

        String formattedDate;


        java.util.Date date = null;
        try {
            SimpleDateFormat df;
            if (localtime.split("-")[0].length() > 2)
                df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            else
                df = new SimpleDateFormat("MM-dd-yyyy HH:mm");
            df.setTimeZone(TimeZone.getDefault());
            date = (java.util.Date) df.parse(localtime);
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            formattedDate = df.format(date);
        } catch (ParseException e) {
            e.getMessage();
            SimpleDateFormat df;
            if (localtime.split("-")[0].length() > 2)
                df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            else
                df = new SimpleDateFormat("MM-dd-yyyy HH:mm");
            df.setTimeZone(TimeZone.getDefault());
            try {
                date = (java.util.Date) df.parse(localtime);
            } catch (ParseException e1) {
                e1.printStackTrace();
            }
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            formattedDate = df.format(date);

        }

//        Log.d(TAG, "formattedDate = " + formattedDate);
        return formattedDate;

    }

    public static String converttimeutc2local(String utctime) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat dflocal = new SimpleDateFormat("MM-dd-yyyy HH:mm");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
//        Log.d(TAG, "utctime = " + utctime);

        java.util.Date date = null;
        try {
            date = (java.util.Date) df.parse(utctime);
        } catch (ParseException e) {
            try {
                date = (java.util.Date) dflocal.parse(utctime);
            } catch (ParseException e1) {
                e1.printStackTrace();
            }
            e.getMessage();
        }
        dflocal.setTimeZone(TimeZone.getDefault());
        String formattedDate = dflocal.format(date);
//        Log.d(TAG, "formattedDate = " + formattedDate);
        return formattedDate;

    }


    public static void makeTextViewMultiLine(TextView tv) {
        tv.setMaxLines(Integer.MAX_VALUE);
        tv.setEllipsize(null);
    }

    public static String makeAuthStatusString(Context cxt, int msgRes, String appname) {
        return String.format(cxt.getResources().getString(msgRes), appname);
    }

    public static boolean isEmpty(String s) {
        return ((s == null) || s.isEmpty());
    }

    public static boolean checkInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) MyApplication.INSTANCE.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        return (
                cm != null &&
                        cm.getActiveNetworkInfo() != null &&
                        cm.getActiveNetworkInfo().isAvailable() &&
                        cm.getActiveNetworkInfo().isConnected()
        );
    }

    public static boolean isWifi() {
        ConnectivityManager cm = (ConnectivityManager) MyApplication.INSTANCE.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        //wifi == true;  mobile=false;
        return (cm.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI);

    }

    public static boolean ismobile() {
        ConnectivityManager cm = (ConnectivityManager) MyApplication.INSTANCE.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        //wifi == true;  mobile=false;
        boolean isconnected;
        if (cm.getActiveNetworkInfo() != null) {
            isconnected = (cm.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_MOBILE);
        } else {
            isconnected = false;
        }
        return isconnected;

    }

    public static String getWifiName() {
        WifiManager wifiMan = (WifiManager) MyApplication.INSTANCE.getSystemService(
                Context.WIFI_SERVICE);
        WifiInfo wifiInf = wifiMan.getConnectionInfo();

        return (wifiInf.getSSID());
    }

    public static String getWifirMacAddr() {
        WifiManager wifiMan = (WifiManager) MyApplication.INSTANCE.getSystemService(
                Context.WIFI_SERVICE);
        WifiInfo wifiInf = wifiMan.getConnectionInfo();
        return (wifiInf.getMacAddress());

    }

    public static String getWifiRouterbssid() {
        WifiManager wifiMan = (WifiManager) MyApplication.INSTANCE.getSystemService(
                Context.WIFI_SERVICE);
        WifiInfo wifiInf = wifiMan.getConnectionInfo();

        return (wifiInf.getBSSID());

    }

    public static void performSendEmail(Context context, String email) {
        Intent send = new Intent(Intent.ACTION_SENDTO);
        String uriText;

     /*   uriText = "mailto:support@coremobileinc.com" +
                  "?subject=Corey Feedback" +
                  "&body=Please type your Comments/Feedback : \n"; */
        uriText = "mailto:" + email +
                "?subject=Corey Feedback" +
                "&body=Please type your Comments/Feedback : \n";
        uriText = uriText.replace(" ", "%20");
        Uri uri = Uri.parse(uriText);

        send.setData(uri);
        context.startActivity(Intent.createChooser(send, "Send email"));
    }

    public static void dump(InputStream is) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.getMessage();
        }
    }

    public static String convertStreamToString(InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is, HTTP.UTF_8).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public static String httpPost(String url, Object... kvPairs) {
        CloseableHttpClient httpclient = null;
        try {
//            Log.e("Utils.httpPost url=", "" + url);
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("type", "web_server"));
            for (int i = 0; i < kvPairs.length; i += 2) {
                String key = (String) kvPairs[i];
                // TODO:mahesh remove this printing k,v once local calendar is stabilized
                if ("__list__".equals(key) && kvPairs[i + 1] != null) {
                    // special case - an embedded list of key-value pairs
                    String[] arr = (String[]) kvPairs[i + 1];
                    for (int j = 0; j < arr.length; j += 2) {
                        printPair("Utils.httpPost KV>: ", arr[j], arr[j + 1]);
                        nameValuePairs.add(new BasicNameValuePair(arr[j], arr[j + 1]));
                    }
                } else {
                    printPair("Utils.httpPost KV: ", key, (String) kvPairs[i + 1]);
                    nameValuePairs.add(new BasicNameValuePair(key, (String) kvPairs[i + 1]));
                }
            }
//            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValuePairs);
//            entity.setContentEncoding(HTTP.UTF_8);

            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));

            httpclient = new CMN_HTTPSClient(MyApplication.INSTANCE, url).getClient();
            HttpResponse response = httpclient.execute(httpPost);
            return EntityUtils.toString(response.getEntity());

        } catch (UnsupportedEncodingException e1) {
            e1.getMessage();
            return null;
        } catch (IllegalStateException e) {
            e.getMessage();
            return null;
        } catch (ClientProtocolException e) {
            e.getMessage();
            return null;
        } catch (IOException e) {
            e.getMessage();
            return null;
        } finally {
            if (httpclient != null) {
                try {
                    httpclient.close();
                } catch (IOException e) {
                    e.getMessage();
                }
            }
        }
    }

    public static String null2empty(String s) {
        return (s != null) ? s : "";
    }

    public static String getServerBaseUrl() {
        JSONHelperClass jsonHelperClass = new JSONHelperClass();
        return jsonHelperClass.getBaseURL(LocalPrefs.INSTANCE.organization());
    }

    public static void showAlertDialog(
            Activity activity, int titleId, int messageId) {
        Bundle args = new Bundle();
        args.putInt("title", titleId);
        args.putInt("message", messageId);

        DialogFragment frag = AlertDialogFragment.newInstance(args);
        frag.show(activity.getFragmentManager(), "InfoDialog");
    }

    public static void showAlertDialog(
            Activity activity, int titleId, int messageId, String listenerId, int id) {
        Bundle args = new Bundle();
        args.putInt("title", titleId);
        args.putInt("message", messageId);
        args.putString("listener", listenerId);
        args.putInt("id", id);

        DialogFragment frag = AlertDialogFragment.newInstance(args);
        frag.show(activity.getFragmentManager(), listenerId); // listenerId is used as tag also
    }

    public static void showAlertDialog(
            Activity activity, int titleId, String listenerId, int id, int bodyViewId) {
        showAlertDialog(activity, titleId, listenerId, id, bodyViewId, "YES", "NO");
    }

    public static void showAlertDialog(
            Activity activity, int titleId, String listenerId, int id, int bodyViewId,
            String yesLabel, String noLabel) {
        Bundle args = new Bundle();
        args.putInt("title", titleId);
        args.putInt("body", bodyViewId);
        args.putString("listener", listenerId);
        args.putInt("id", id);
        args.putString("yesLabel", yesLabel);
        args.putString("noLabel", noLabel);

        DialogFragment frag = AlertDialogFragment.newInstance(args);
        frag.show(activity.getFragmentManager(), listenerId); // listenerId is used as tag also
    }

    public static void startAutoPullTimer(Context context) {
        System.out.println("Utils.startAutoPullTimer");
        Intent intent = new Intent("com.coremobile.corey.timercontrol");
        intent.putExtra("EXTRA_ACTION", "START");
        context.sendBroadcast(intent);
    }

    public static void stopAutoPullTimer(Context context) {
        System.out.println("Utils.stopAutoPullTimer");
        Intent intent = new Intent("com.coremobile.corey.timercontrol");
        intent.putExtra("EXTRA_ACTION", "STOP");
        context.sendBroadcast(intent);
    }

    public static void printPair(String prefix, String key, String value) {
        if (key.equalsIgnoreCase("password")) {
            value = "****";
        }
//        Log.e("" + prefix + key + "=", "" + value);
    }

    public static Intent makeWebViewIntent(Context context, ContextData data) {
        String url = data.mURL;
        if (!url.startsWith("http")) {
            url = Utils.getServerBaseUrl() + "/" + url;
        }
     /*   if (url.endsWith("pdf"))
        {
        	url= "https://docs.google.com/viewer?url="+url;
        } */
        System.out.println("makeWebViewIntent url : " + url);
        url = url
                .replace("token==%@", "token=" + CMN_Preferences.getUserToken(context));

        Intent urlIntent = new Intent(context, WebViewActivityCMN.class);
        urlIntent.putExtra("myurl", url);
        urlIntent.putExtra("objname", data.mDisplayText);
        return urlIntent;
    }

    public static void forceOverflowMenu(Context context) {
        try {
            ViewConfiguration config = ViewConfiguration.get(context);
            java.lang.reflect.Field menuKeyField =
                    ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                //System.out.println(
                //"forceOverflowMenu: has permanent menu key. forcing overflow menu");
                //menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            } else {
                System.out.println(
                        "forceOverflowMenu: NO permanent menu key.");
            }
        } catch (NoSuchFieldException e) {
            System.out.println("Exception: forceOverflowMenu " + e);
            System.out.println(
                    "forceOverflowMenu: NO permanent menu key.");

        } catch (Exception e) {
            System.out.println("Exception: forceOverflowMenu " + e);
            e.getMessage();
        }
    }

    public static Context sDownloadMessageServiceStarterContext = null;

    public static void startDownloadMessageService(Context context) {
//        Log.d("utils", "Start service");
        sDownloadMessageServiceStarterContext = context;
        Intent serviceIntent =
                new Intent(sDownloadMessageServiceStarterContext, DownloadMessageService.class);
        sDownloadMessageServiceStarterContext.startService(serviceIntent);
    }

    public static void stopDownloadMessageService(Context context) {
        sDownloadMessageServiceStarterContext = context;
        if (sDownloadMessageServiceStarterContext != null) {
//            Log.d("utils", "Stop service");
            Intent serviceIntent =
                    new Intent(sDownloadMessageServiceStarterContext, DownloadMessageService.class);
            sDownloadMessageServiceStarterContext.stopService(serviceIntent);
            //  sDownloadMessageServiceStarterContext = null;
        }
    }

    public static void stopDownloadMessageService1() {
        if (sDownloadMessageServiceStarterContext != null) {
//            Log.d("utils", "Stop service");
            Intent serviceIntent =
                    new Intent(sDownloadMessageServiceStarterContext, DownloadMessageService.class);
            sDownloadMessageServiceStarterContext.stopService(serviceIntent);
            sDownloadMessageServiceStarterContext = null;
        }
    }

    public static void showSchedulingPopup(final Context context) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        View popupView = layoutInflater.inflate(R.layout.scheduling_popup_window, null);

        final PopupWindow popup = new PopupWindow(popupView,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT,
                true);


        popup.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        Button addReminder = (Button) popupView.findViewById(R.id.buttonSurgeryScheduling);
        addReminder.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                CalenderActivity.speciality = "-1";
                CalenderActivity.modality = "-1";
                context.startActivity(new Intent(context, SurgerySchedulingActivity.class));
                schType = "ORScheduler";
                popup.dismiss();
            }
        });

        Button editReminder = (Button) popupView.findViewById(R.id.buttonSpecialityScheduling);
        editReminder.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SpecialitySchedulingActivity.class);
                context.startActivity(intent);
                schType = "ProcRoomScheduler";
                popup.dismiss();
            }
        });

        Button useReminder = (Button) popupView.findViewById(R.id.buttonModalityScheduling);
        useReminder.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ModalitySchedulingActivity.class);
                context.startActivity(intent);
                schType = "ModalityScheduler";
                popup.dismiss();
            }
        });

        Button close = (Button) popupView.findViewById(R.id.buttonCancel);
        close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                popup.dismiss();
            }
        });
    }

    public static void signout() {
        System.out.println("Utils.signout");
        Utils.stopDownloadMessageService1();
        MyApplication.INSTANCE.unregisterPhoneCallsListener();
        CMN_JsonConstants.eprosMessages.clear();

        new CMN_AppUsageWebService(MyApplication.INSTANCE).execute("LO");

//        AlarmManager alarmManager = (AlarmManager) MyApplication.INSTANCE.getSystemService(Context.ALARM_SERVICE);
//        Intent intent = new Intent(MyApplication.INSTANCE, CMN_AlarmReceiver.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(MyApplication.INSTANCE, 0, intent, 0);
//        alarmManager.cancel(pendingIntent);
        MyApplication.INSTANCE.stopService(new Intent(MyApplication.INSTANCE, PendingNotificationService.class));
        // clear shared preferences
        LocalPrefs.INSTANCE.setHasUserSignedIn(false);
        MyApplication.INSTANCE.getSharedPreferences(Home.GLOBAL_PREFERENCE, 0)
                .edit().clear().commit();

        SharedPreferences prefs =
                MyApplication.INSTANCE.getSharedPreferences(MyApplication.CURRENT_USER, 0);
        // > make a note of the items to retain, clear all and restore only those items
        String[] stringKeysToRetain = {};
        String[] PatientstringKeysToRetain = {
                "Username",
                "Organization",
                "InfoUrl",
                "deviceId",
                "gcmDeviceId",
                "Password",
                "CMN_SERVER_BASE_URL_DEFINE",
                "logourl",
        };
        String[] OtherstringKeysToRetain = {
                "Username",
                "Organization",
                "InfoUrl",
                "deviceId",
                "gcmDeviceId",
                "CMN_SERVER_BASE_URL_DEFINE",
                "logourl",
        };
        if (MyApplication.INSTANCE.AppConstants.getAppName().equalsIgnoreCase("PATIENT") ||
                MyApplication.INSTANCE.AppConstants.getAppName().equalsIgnoreCase("HEALTH")) {
            stringKeysToRetain = PatientstringKeysToRetain;
        } else stringKeysToRetain = OtherstringKeysToRetain;

        String[] stringValsToRetain = new String[stringKeysToRetain.length];
        for (int i = 0; i < stringKeysToRetain.length; ++i) {
            String key = stringKeysToRetain[i];
            stringValsToRetain[i] = prefs.getString(key, null);
        }

        String[] integerKeysToRetain = {
                "gcmAppVersion",
        };
        Integer[] integerValsToRetain = new Integer[integerKeysToRetain.length];
        for (int i = 0; i < integerKeysToRetain.length; ++i) {
            String key = integerKeysToRetain[i];
            integerValsToRetain[i] = prefs.getInt(key, Integer.MIN_VALUE);
        }
        CMN_Preferences.setisSendDeviceLog(MyApplication.INSTANCE, true);
        ///////////////////////////
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        ///////////////////////////

        for (int i = 0; i < stringKeysToRetain.length; ++i) {
            if (!Utils.isEmpty(stringValsToRetain[i])) {
                System.out.println("signout: retaining str:" + stringKeysToRetain[i] + " => " + stringValsToRetain[i]);
                editor.putString(stringKeysToRetain[i], stringValsToRetain[i]);
            }
        }
        for (int i = 0; i < integerKeysToRetain.length; ++i) {
            if (integerValsToRetain[i] != Integer.MIN_VALUE) {
                System.out.println("signout: retaining int:" + integerKeysToRetain[i] + " => " + integerValsToRetain[i]);
                editor.putInt(integerKeysToRetain[i], integerValsToRetain[i]);
            }
        }
        editor.commit();

        // clear database tables
        CoreyDBHelper dbHelper = new CoreyDBHelper(MyApplication.INSTANCE);
        dbHelper.deleteAllMessages();
        dbHelper.deleteAllPhoneCallLogs();
        AutoLogoutManager.INSTANCE.stop();
        // launch signin activity
        if (MyApplication.INSTANCE.mCurActivity != null) {
            Intent signinActivityIntent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                signinActivityIntent = new Intent(
                        MyApplication.INSTANCE.mCurActivity,
                        SigninWithFingerprintActivityCMN.class);
            } else {
                signinActivityIntent = new Intent(
                        MyApplication.INSTANCE.mCurActivity,
                        SigninActivityCMN.class);
            }
            // AutoLogoutManager.INSTANCE.stop();
            NetworkChangeManager.INSTANCE.stop();
            if (MyApplication.INSTANCE.mMessageTabActivity != null) {
                MyApplication.INSTANCE.mMessageTabActivity.finish();
            }
            if (MyApplication.INSTANCE.mSumMenuActivity != null) {
                MyApplication.INSTANCE.mSumMenuActivity.finish();
            }
            //  MyApplication.INSTANCE.deletedb();
            MyApplication.INSTANCE.mCurActivity.startActivity(signinActivityIntent);

            MyApplication.INSTANCE.mCurActivity.finish();
         /*   if (MyApplication.INSTANCE.mMessageTabActivity != null) {
                MyApplication.INSTANCE.mMessageTabActivity.finish();
            } */
        }
        //   AutoLogoutManager.INSTANCE.stop();
        patientPosition = 0;
        CMN_Preferences.setisUserLoggedOut(MyApplication.INSTANCE.mCurActivity, true);
        CMN_Preferences.setCurrentContextId(MessageTabActivityCMN.activity, "");
    }

    public static class ObjectComparator implements Comparator<RowDisplayObject> {

        @Override
        public int compare(RowDisplayObject o1, RowDisplayObject o2) {
            return (Integer.valueOf("" + "" + o1.getDispRow() + "" + o1.getDispCol()).compareTo(Integer.valueOf("" + "" + o2.getDispRow() + "" + o2.getDispCol())));
        }
    }

    public static class ObjectComparatorbyPos implements Comparator<RowDisplayObject> {

        @Override
        public int compare(RowDisplayObject o1, RowDisplayObject o2) {
            return ((o1.getDispPos()) > (o2.getDispPos()) ? -1 : ((o1.getDispPos()) == (o1.getDispPos()) ? 0 : 1));
        }
    }

    public static class FieldsComparator implements Comparator<Fields> {

        @Override
        public int compare(Fields o1, Fields o2) {
            return ((o1.getFieldId() + "" + o1.getFieldValue()).compareTo(o2.getFieldId() + "" + o2.getFieldValue()));
        }
    }

    public static class ObjectComparatorbyDispName implements Comparator<RowDisplayObject> {

        @Override
        public int compare(RowDisplayObject o1, RowDisplayObject o2) {
            return (o1.getDisplayName()).compareTo(o2.getDisplayName());
        }
    }
}
