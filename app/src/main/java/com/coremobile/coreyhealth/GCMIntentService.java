package com.coremobile.coreyhealth;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class GCMIntentService extends IntentService implements IPullDataFromServer {
    public static final int NOTIFICATION_ID = 1;
    private static final String TAG = "Corey_GoogleCloudMessaging.GCMIntentService";
    SharedPreferences mcurrentUserPref;
    public static final String CURRENT_USER = "CurrentUser";
    MyApplication application = MyApplication.INSTANCE;

    public GCMIntentService() {
        super("GCMIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // example extras:
        // Bundle[
        //     {
        //         android={
        //             "MessageType":"instant_gratification",
        //             "alert":"Sync completed. Please wait a few seconds so that handset is refreshed with the latest information."
        //         },
        //         from=340144732350,
        //         apids= [
        //             "apa91bh9rbkolyoguuem1jej5xljcqjnyw3gkuiovu6hoblzvvuqz7njp2yhs9unjy4zngh5gdz_w08wwkede5nypi4mrhze1x_ezivlofmz0g7p2usmgdq0mxkf9xqou1hw1utts9wlztsv7egb4ogpuys3ukiqkireclzbuw-luzxbsyifi-k"
        //         ],
        //         android.support.content.wakelockid=2,
        //         collapse_key=do_not_collapse
        //     }
        // ]
        Bundle extras = intent.getExtras();

        if (extras.getBoolean("isNotificationOpenedIntent", false)) {
            // handle notification opened
            IntentReceiverHelper helper = new IntentReceiverHelper(this);
            helper.onNotificationOpened(extras.getString("payload", ""));
        } else {
            // handle push notification from Google Cloud
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
            String messageType = gcm.getMessageType(intent);

            Log.d(TAG, "GCMBroadcastReceiver received: " + messageType + ", " + extras.toString());

            if (!extras.isEmpty()) {
                if (GoogleCloudMessaging.
                        MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                    sendNotification("Send error: " + extras.toString(), null);
                } else if (GoogleCloudMessaging.
                        MESSAGE_TYPE_DELETED.equals(messageType)) {
                    sendNotification("Deleted messages on server: " +
                            extras.toString(), null);
                } else if (GoogleCloudMessaging.
                        MESSAGE_TYPE_MESSAGE.equals(messageType)) { // regular GCM message
                    try {
                        String jsonMsgStr = extras.getString("android");
                        if (!Utils.isEmpty(jsonMsgStr)) {
                            Log.d(TAG, "jsonMsgStr=" + jsonMsgStr);
                            JSONObject jsonMsg = new JSONObject(jsonMsgStr);
                            String alert_msg = jsonMsg.getString("alert");
                            String payload = jsonMsg.getString("MessageType");

                            IntentReceiverHelper helper = new IntentReceiverHelper(this);
                            helper.onPushReceived(alert_msg, payload, jsonMsgStr);
                            sendNotification(alert_msg, payload);
                        } else {
                            Log.d(TAG, "jsonMsgStr is empty/null");
                        }
                    } catch (JSONException e) {
                        e.getMessage();
                    }
                }
            }
            // Release the wake lock provided by the WakefulBroadcastReceiver.
            GCMBroadcastReceiver.completeWakefulIntent(intent);
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
                    new Intent(this, GCMIntentService.class)
                            .putExtra("isNotificationOpenedIntent", true)
                            .putExtra("payload", payload);
            PendingIntent pIntent = PendingIntent.getService(this, 0, intent, 0);

            final Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + getPackageName() + "/raw/notification");


            NotificationCompat.BigTextStyle notiStyle = new       NotificationCompat.BigTextStyle();
            notiStyle.setBigContentTitle("" + getResources().getString(R.string.app_name));
            notiStyle.bigText(msg);

            final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                    GCMIntentService.this);

            final int icon = R.drawable.app_icon_white;
            Notification notification;
            notification = mBuilder.setSmallIcon(icon).setTicker("" + getResources().getString(R.string.app_name)).setWhen(0)
                    .setAutoCancel(true)
                    .setContentTitle("" + getResources().getString(R.string.app_name))
                    .setContentIntent(pIntent)
                    .setSound(alarmSound)
                    .setStyle(notiStyle)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setSmallIcon(R.drawable.app_icon_white)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.xcon))
                    .setContentText(msg)
                    .build();

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(new Random().nextInt(), notification);

        }
    }

    @Override
    public void finishedParsing(String _status) {
        Intent pushintent = new Intent(
                MyApplication.INSTANCE.AppConstants.getDownloadCompleteIntent());
        Log.d(TAG, "sending update complete intent");
        String alert_msg = "DownloadCompleted";
        //   pushintent.putExtra("Message", alert_msg);
        pushintent.putExtra("Status", "success");  //Messaglistactivity expects a success to refresh ui
        GCMIntentService.this.sendBroadcast(pushintent);
        if (LocalPrefs.INSTANCE.autoSync()) {
            Intent UpdateIntent = new Intent(MyApplication.INSTANCE.AppConstants.getUploadCompleteIntent());
            GCMIntentService.this.sendBroadcast(UpdateIntent);
        }
    }

    @Override
    public void showDialog() {
        // empty body
    }

    @Override
    public void closeDialog() {
        // empty body
    }




}
