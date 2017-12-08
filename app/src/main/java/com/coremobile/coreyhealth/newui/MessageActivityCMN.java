package com.coremobile.coreyhealth.newui;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.coremobile.coreyhealth.IPullDataFromServer;
import com.coremobile.coreyhealth.JSONHelperClass;
import com.coremobile.coreyhealth.MyApplication;
import com.coremobile.coreyhealth.R;
import com.coremobile.coreyhealth.Utils;
import com.coremobile.coreyhealth.base.CMN_AppListBaseActivity;
import com.coremobile.coreyhealth.clientcertificate.CMN_HTTPSClient;
import com.coremobile.coreyhealth.errorhandling.CMN_ErrorMessages;
import com.coremobile.coreyhealth.googleAnalytics.CMN_Preferences;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * MessageActivityCMN is a main Activity to show a ListView containing Message items
 */
public class MessageActivityCMN extends CMN_AppListBaseActivity implements IPullDataFromServer {
    /**
     * Called when the activity is first created.
     */
    private static String TAG = "Corey_MessageActivityCMN";
    public static final String CURRENT_USER = "CurrentUser";
    public static String CMN_SERVER_BASE_URL_DEFINE;
    ArrayList<OneMsg> messages;
    ArrayList<OneMsg> messagesclone;
    MessagingAdapter adapter;
    MsgByUsr msgbyusr;
    MsgByCtxt msgbyctxt;
    EditText text;
    static Random rand = new Random();
    static String sender;
    Intent mIntent;
    MsgHelper msghelper;
    MyApplication application;
    IntentFilter mOne2OneMsgFilter;
    String SendApiError;

    private ProgressDialog mProgressDialog;

    String mAssignedUserId;
    String mCtxt;
    String newMessage;
    String mObjId;

    Timer MsgdlTimer;

    private SharedPreferences mCurrentUserPref;

    private String organizationName;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.msg_screen);

        text = (EditText) this.findViewById(R.id.text);

        mCurrentUserPref = getSharedPreferences(CURRENT_USER, 0);
        organizationName = mCurrentUserPref.getString("Organization", null);
        JSONHelperClass jsonHelperClass = new JSONHelperClass();
        CMN_SERVER_BASE_URL_DEFINE = jsonHelperClass
                .getBaseURL(organizationName);
        msghelper = new MsgHelper();
        mIntent = getIntent();

        application = (MyApplication) getApplication();


        //	sender = Utility.sender[rand.nextInt( Utility.sender.length-1)];
        //	this.setTitle(sender);
        //	messages = new ArrayList<OneMsg>();
        mAssignedUserId = mIntent.getExtras().getString("usr");
        mCtxt = mIntent.getExtras().getString("ctxt");
        mObjId = mIntent.getExtras().getString("objid");
        Log.d(TAG, "mAssignedUserId =" + mAssignedUserId);
        Log.d(TAG, "mCtxt" + mCtxt);
        msgbyctxt = msghelper.getMsgsByCtxt(mCtxt);
        if (msgbyctxt != null) {
            msgbyusr = msghelper.getMsgsByUsr(mCtxt, mAssignedUserId);

        } else {
            msgbyctxt = new MsgByCtxt();
            msghelper.addMsgsByCtxt(mCtxt, msgbyctxt);
            msgbyusr = new MsgByUsr();
            msgbyctxt.addMsgByUsr(mAssignedUserId, msgbyusr);
        }
        //Check for availability of context and if not create
        if (msgbyusr != null) {
            messages = msgbyusr.mMessages;
        } else {
            msgbyusr = new MsgByUsr();
            msgbyctxt.addMsgByUsr(mAssignedUserId, msgbyusr);
            messages = msgbyusr.mMessages;

        }
        //if(MyApplication.INSTANCE.One2OneMsgRecvd)
        //{
        mProgressDialog = new ProgressDialog(MessageActivityCMN.this);
        mProgressDialog.setTitle("Retrieving Messages");
        mProgressDialog.setMessage("Please Wait ..");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        getLatestMsgs();
        MyApplication.INSTANCE.One2OneMsgRecvd = false;
        MsgdlTimer = new Timer();
        MsgdlTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG, "Update timed out");
                Msgdltimout();
            }
        }, CMN_Preferences.getUpdateWaitout(this));
        //}
        /*
         * Set Action bar
		 */
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setIcon(R.drawable.app_icon);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME
                    | ActionBar.DISPLAY_SHOW_TITLE
                    | ActionBar.DISPLAY_HOME_AS_UP);
            actionBar.show();

            actionBar.setTitle("Messages");
        }

        adapter = new MessagingAdapter(this, messages);
        setListAdapter(adapter);

    }

    public void sendMessage(View v) {
        newMessage = text.getText().toString().trim();
        if (newMessage.length() > 0) {
            text.setText("");
            OneMsg newmsg = new OneMsg(newMessage, true);
            newmsg.setMsgTimestamp("");
            addNewMessage(newmsg);

            //	new SendMessage().execute();
            new SendMsgAsyncTask().execute();

        }
    }

    public void Msgdltimout() {
        if (MsgdlTimer != null) {
            MsgdlTimer.cancel();
            MsgdlTimer = null;

        }
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                Toast.makeText(MessageActivityCMN.this,
                        CMN_ErrorMessages.getInstance().getValue(79), Toast.LENGTH_LONG).show();

            }
        });

        finishedParsing("dummy");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
    /*	int id = item.getItemId();
        if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item); */

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    void addNewMessage(OneMsg m) {
        //messages.add(m);
        adapter.notifyDataSetChanged();
        getListView().setSelection(messages.size() - 1);
    }

    class SendMsgAsyncTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressDialog.setTitle("Sending..");
            mProgressDialog.setMessage("Please Wait ..");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected Integer doInBackground(String... arg0) {


            String url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                    + "SendMessage.aspx?token=" +CMN_Preferences.getUserToken(MessageActivityCMN.this);

            Log.d(TAG,
                    "url: %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% "
                            + url);

            if (Utils.checkInternetConnection()) {
                CloseableHttpClient httpclient = new CMN_HTTPSClient(MyApplication.INSTANCE, url).getClient();
                HttpPost httppost = new HttpPost(url);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs
                        .add(new BasicNameValuePair("type", "web_server"));
                try {

                    JSONObject jObject = new JSONObject();
                    jObject.accumulate("To", mAssignedUserId);
                    jObject.accumulate("Message", newMessage);
                    jObject.accumulate("Context", mCtxt);


                    nameValuePairs.add(new BasicNameValuePair("msgdata",
                            jObject.toString()));

                    Log.d(TAG, "nameValuePairs =" + nameValuePairs);
                    Log.d(TAG, "Multi update Json object =" + jObject);
                    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
                            nameValuePairs);
                    // Log.d(TAG, "entity = " + entity);
                    entity.setContentEncoding(HTTP.UTF_8);
                    httppost.setEntity(entity);
                    httppost.setHeader("Content-Type",
                            "application/x-www-form-urlencoded");

                    HttpResponse response = httpclient.execute(httppost);
                    Log.d(TAG, "UpdateApplicationData POST response: "
                            + response.getStatusLine());
                    JSONObject json = null;
                    json = new JSONObject(EntityUtils.toString(response.getEntity()));
                    if (json != null) {

                        String jsonResult = json.getString("result");
                        String jsonError = json.getString("Error");
                        SendApiError = jsonError;
                        Log.d(TAG,
                                "SendMessageAPI %%%%%%%%%%%%%%%%%%%%%%%%%% json response: "
                                        + jsonResult);
                        if (jsonResult.equals("0")) return (0);
                        else return (-1);
                    }

                } catch (UnsupportedEncodingException e1) {
                    e1.getMessage();
                    return -1;
                } catch (ClientProtocolException e) {
                    e.getMessage();
                } catch (IOException e) {
                    e.getMessage();
                } catch (JSONException e) {
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

                return -1;
            }
            return -1;

        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            if (result == 0)
                Toast.makeText(MessageActivityCMN.this, CMN_ErrorMessages.getInstance().getValue(157), Toast.LENGTH_LONG).show();
            else Toast.makeText(MessageActivityCMN.this, SendApiError, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private static String convertStreamToString(InputStream is) {
			/*
			 * To convert the InputStream to String we use the
			 * BufferedReader.readLine() method. We iterate until the BufferedReader
			 * return null which means there's no more data to read. Each line will
			 * appended to a StringBuilder and returned as String.
			 */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.getMessage();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.getMessage();
            }
        }
        return sb.toString();
    }

    BroadcastReceiver One2oneMsgReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "One2one push notification received");
            // finishedParsing("dummy");
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication application = (MyApplication) getApplication();
        mOne2OneMsgFilter = new IntentFilter();
        //	mUpdateCompleteFilter.addAction( application.AppConstants.getDownloadCompleteIntent());
        mOne2OneMsgFilter.addAction(application.AppConstants.getUploadCompleteIntent());
        registerReceiver(One2oneMsgReceiver, mOne2OneMsgFilter );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        if (One2oneMsgReceiver != null) {
            unregisterReceiver(One2oneMsgReceiver);
            One2oneMsgReceiver = null;
        }
    }

    private void getLatestMsgs() {

        Thread retrieveMsgThread = new Thread() {
            @Override
            public void run() {
                try {
                    // Log.i(TAG, "isCorefyInProgress =" + isCorefyInProgress);
                    //   sleep(3000); // Wait for a few seconds...
                    while (MyApplication.INSTANCE.pullInProgress) {
                        sleep(500);
                        continue;
                    }

                    //  refreshMessages();
                } catch (InterruptedException e) {
                    e.getMessage();
                } finally {

                    Log.d(TAG,
                            "sleep again before pulling the Msg $$$$$$$$$$$$$$$$");
                    try {
                        sleep(2000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.getMessage();
                    }
                    Log.d(TAG,
                            "start the message retrieval $$$$$$$$$$$$$$$$");

                    RetrieveMessage();
                }
            }

        };

        retrieveMsgThread.start();

    }

    private void RetrieveMessage() {
        String LastMsgID = msgbyusr.getLastMsgId();
        Log.d(TAG, "Last messageId =" + LastMsgID);

        application.getOne2OneMessages(MessageActivityCMN.this, mCtxt, mObjId, LastMsgID, false);
    }

    @Override
    public void showDialog() {
        // TODO Auto-generated method stub

    }

    @Override
    public void closeDialog() {
        // TODO Auto-generated method stub

    }

    @Override
    public void finishedParsing(String _status) {
        Log.d(TAG, "finishedParsing");
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            if (MsgdlTimer != null) {
                MsgdlTimer.cancel();
                MsgdlTimer = null;

            }

        }
        if (messages != null) Collections.sort(messages, new MsgComparator());
        Collections.reverse(messages);
        adapter.notifyDataSetChanged();
        getListView().setSelection(messages.size() - 1);

    }

    public class MsgComparator implements Comparator<OneMsg> {

        @Override
        public int compare(OneMsg o1, OneMsg o2) {
            return ((o1.getMsgIdInt()) > (o2.getMsgIdInt()) ? -1 : ((o1.getMsgIdInt()) == (o1.getMsgIdInt()) ? 0 : 1));
        }
    }
}