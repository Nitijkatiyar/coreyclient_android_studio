package com.coremobile.coreyhealth.Checkfornotification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.coremobile.coreyhealth.GCMIntentService;
import com.coremobile.coreyhealth.IntentReceiverHelper;
import com.coremobile.coreyhealth.JSONHelperClass;
import com.coremobile.coreyhealth.LocalPrefs;
import com.coremobile.coreyhealth.MyApplication;
import com.coremobile.coreyhealth.R;
import com.coremobile.coreyhealth.Utils;
import com.coremobile.coreyhealth.clientcertificate.CMN_HTTPSClient;
import com.coremobile.coreyhealth.genericviewandsurvey.CMN_JsonConstants;
import com.coremobile.coreyhealth.googleAnalytics.CMN_Preferences;
import com.coremobile.coreyhealth.loggerUtility.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by nitij on 17-10-2016.
 */
public class CMN_IsNotificationPendingWebService extends AsyncTask<String, String, JSONObject> {
    private Context mContext;
    public static final String TAG = "Corey_CheckForNotifications";
    String OrganizationStr;
    SharedPreferences mcurrentUserPref;
    public static final String CURRENT_USER = "CurrentUser";
    private String CMN_SERVER_BASE_URL_DEFINE;
    JSONHelperClass jsonHelperClass;
    JSONArray data;

    public CMN_IsNotificationPendingWebService(Context context) {
        mContext = context;
        mcurrentUserPref = mContext.getSharedPreferences(CURRENT_USER, 0);
        OrganizationStr = mcurrentUserPref.getString("Organization", "");
        jsonHelperClass = new JSONHelperClass();
        CMN_SERVER_BASE_URL_DEFINE = jsonHelperClass.getBaseURL(OrganizationStr);
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();


    }

    @Override
    protected JSONObject doInBackground(String... params) {

        String url = null;

        try {
            url = CMN_SERVER_BASE_URL_DEFINE
                    + "CheckForNotifications.aspx?token=" + CMN_Preferences.getUserToken(mContext)
                    + "&deviceid=" + URLEncoder.encode(LocalPrefs.INSTANCE.deviceId(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.getMessage();
        }
        Logger.Debug(mContext, "Pending Notification", url);
        JSONObject json = null;
        HttpResponse response = null;
        CloseableHttpClient httpclient = new CMN_HTTPSClient(MyApplication.INSTANCE, url).getClient();
        if (url != null) {
            HttpGet httpGet = new HttpGet(url);

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("type", "web_server"));

            try {

                httpGet.setHeader("Content-Type",
                        "application/x-www-form-urlencoded");
                response = httpclient.execute(httpGet);
                //    Log.e(TAG, "POST response: " + response.getStatusLine());
                String jsonResult = EntityUtils.toString(response.getEntity());
                Logger.Debug(mContext, "Response", jsonResult);
                json = new JSONObject(jsonResult);
                if (json.getJSONObject("Result").getInt("Code") == 0) {
                    data = json.getJSONArray("Data");

                } else if (json.getJSONObject("Result").getString("Message").equalsIgnoreCase("Invalid Token")) {
                    IntentReceiverHelper helper = new IntentReceiverHelper(MyApplication.INSTANCE);
                    helper.onPushReceived("Someone is logged in with other device, Please login again", "Invalid Token", "");
                    sendNotification("Someone is logged in with other device, Please login again", "Invalid Token");
                }
            } catch (JSONException e) {
                e.getMessage();
            } catch (ClientProtocolException e) {
                e.getMessage();
            } catch (IOException e) {
                e.getMessage();
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
        return json;

    }


    @Override
    protected void onPostExecute(JSONObject result) {
        super.onPostExecute(result);
        if (data != null && data.length() > 0) {

            for (int i = 0; i < data.length(); i++) {
                try {
                    JSONObject jsonMsg = data.getJSONObject(i);
                    String alert_msg = "";
                    if (jsonMsg.has("alert")) {
                        alert_msg = jsonMsg.getString("alert");
                    } else if (jsonMsg.has("Message")) {
                        alert_msg = jsonMsg.getString("Message");
                    }
                    if (jsonMsg.has("context")) {
                        CMN_Preferences.setIntakeContext(mContext, jsonMsg.getString("context"));
                    }
                    String payload = jsonMsg.getString("MessageType");
                    String mInstructionId = "";
                    if (payload.equalsIgnoreCase("FeedbackSurvey") || payload.equalsIgnoreCase("instruction_compliance")) {

                        String[] alrtmsg = alert_msg.split("\\|");
                        mInstructionId = alrtmsg[1];
                    }

                    if (CMN_JsonConstants.eprosMessages.get(mInstructionId) == null) {
                        IntentReceiverHelper helper = new IntentReceiverHelper(MyApplication.INSTANCE);
                        helper.onPushReceived(alert_msg, payload, jsonMsg.toString());
                        sendNotification(alert_msg, payload);
                    }
                } catch (JSONException e) {
                    e.getMessage();
                }
                JSONObject jsonMsg = null;

            }
        }
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg, String payload) {
        Log.d(TAG, "sendNotification: " + msg + ", " + payload);
        // post to notification bar only if there's a payload
        if (!Utils.isEmpty(payload)) {
            Intent intent =
                    new Intent(mContext, GCMIntentService.class)
                            .putExtra("isNotificationOpenedIntent", true)
                            .putExtra("payload", payload);
            PendingIntent pIntent = PendingIntent.getService(mContext, 0, intent, 0);

            final Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + mContext.getPackageName() + "/raw/notification");


            NotificationCompat.BigTextStyle notiStyle = new       NotificationCompat.BigTextStyle();
            notiStyle.setBigContentTitle("" + mContext.getResources().getString(R.string.app_name));
            notiStyle.bigText(msg);

            final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                    mContext);

            final int icon = R.drawable.app_icon_white;
            Notification notification;
            notification = mBuilder.setSmallIcon(icon).setTicker("" + mContext.getResources().getString(R.string.app_name)).setWhen(0)
                    .setAutoCancel(true)
                    .setContentTitle("" + mContext.getResources().getString(R.string.app_name))
                    .setContentIntent(pIntent)
                    .setSound(alarmSound)
                    .setStyle(notiStyle)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setSmallIcon(R.drawable.app_icon_white)
                    .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.xcon))
                    .setContentText(msg)
                    .build();

            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(new Random().nextInt(), notification);

        }
    }
}
