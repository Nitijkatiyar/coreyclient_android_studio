package com.coremobile.coreyhealth.messaging;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.coremobile.coreyhealth.AppConfig;
import com.coremobile.coreyhealth.MyApplication;
import com.coremobile.coreyhealth.aesencryption.CMN_AES;
import com.coremobile.coreyhealth.clientcertificate.CMN_HTTPSClient;
import com.coremobile.coreyhealth.errorhandling.CMN_ErrorMessages;
import com.coremobile.coreyhealth.googleAnalytics.CMN_Preferences;
import com.coremobile.coreyhealth.newui.MessageTabActivityCMN;

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
import java.util.ArrayList;
import java.util.List;

/**
 * @author Nitij Katiyar
 */
public class CMN_GetAllMessagesWebService extends
        AsyncTask<String, String, JSONObject> {
    ProgressDialog dialog;
    FragmentActivity activity;
    private String TAG = "Corey_GetAllMessages";
    ArrayList<CMN_MessagesModel> messagesList = new ArrayList<CMN_MessagesModel>();
    ListView listView;
    TextView noMessages;

    public CMN_GetAllMessagesWebService(FragmentActivity activity, ListView listView, TextView noMessages) {
        this.activity = activity;
        this.listView = listView;
        this.noMessages = noMessages;
    }

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(activity);
        dialog.setMessage(CMN_ErrorMessages.getInstance().getValue(131));
        dialog.setCancelable(false);
        dialog.show();

        super.onPreExecute();
    }

    @Override
    protected JSONObject doInBackground(String... params) {

        String url = null;
        if (AppConfig.isAESEnabled) {
            url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                    + "GetAllMessages_s.aspx?token=" + CMN_Preferences.getUserToken(activity);
        } else {
            url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                    + "GetAllMessages.aspx?token=" + CMN_Preferences.getUserToken(activity);
        }
        Log.d(TAG, "url" + url);
        CloseableHttpClient httpclient = new CMN_HTTPSClient(MyApplication.INSTANCE, url).getClient();
        HttpGet httpGet = new HttpGet(url);
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("type", "web_server"));

        JSONObject json = null;
        try {

            httpGet.setHeader("Content-Type",
                    "application/x-www-form-urlencoded");
            HttpResponse response = httpclient.execute(httpGet);
            Log.d(TAG, "POST response: " + response.getStatusLine());

            json = new JSONObject(CMN_AES.decryptData(EntityUtils.toString(response.getEntity()),CMN_Preferences.getUserToken(activity)));

            if (json.getJSONObject("Result").getInt("Code") == 0) {
                JSONArray messagesArray = json.getJSONArray("Data");
                for (int i = 0; i < messagesArray.length(); i++) {
                    JSONObject jsonObject = messagesArray.getJSONObject(i);
                    CMN_MessagesModel messages = new CMN_MessagesModel();
                    messages.setId(jsonObject.getInt("ID"));
                    JSONArray jsonArray = jsonObject.getJSONArray("Messages");
                    ArrayList<CMN_MessageDataModel> messageDatas = new ArrayList<CMN_MessageDataModel>();
                    for (int j = 0; j < jsonArray.length(); j++) {
                        CMN_MessageDataModel data = new CMN_MessageDataModel();
                        JSONObject object = jsonArray.getJSONObject(j);
                        data.setContextId(object.getString("ContextId"));
                        data.setContextDisplayName(object
                                .getString("ContextDisplayName"));
                        data.setFromDisplayName(object
                                .getString("FromDisplayName"));
                        data.setFromUsrID(object.getInt("FromUserID"));
                        data.setHasRead(object.getBoolean("HasRead"));
                        data.setId(object.getInt("Id"));
                        data.setMessage(object.getString("MessageText"));
                        data.setTimeStamp(object.getString("CreationTime"));
                        data.setTopic(object.getString("Topic"));
                        data.setTopicId(object.getInt("TopicId"));
                        data.setToUserIds(object.getString("ToUserIDs"));
                        data.setToUsers(object.getString("ToUsers"));
                        data.setThreadId(object.getInt("ThreadId"));

                        messageDatas.add(data);
                    }
                    messages.setMessages(messageDatas);
                    messagesList.add(messages);
                }
            }

        } catch (UnsupportedEncodingException e1) {
            e1.getMessage();
        } catch (IllegalStateException e) {
            e.getMessage();
        } catch (ClientProtocolException e) {
            e.getMessage();
        } catch (IOException e) {
            e.getMessage();
        } catch (JSONException e) {
            e.getMessage();
        }finally {
            if (httpclient != null) {
                try {
                    httpclient.close();
                } catch (IOException e) {
                    e.getMessage();
                }
            }
        }

        return json;
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        super.onPostExecute(result);
        if (dialog.isShowing()) {
            dialog.dismiss();
            if (messagesList.size() > 0) {
                CMN_MessagesAdapter adapter = new CMN_MessagesAdapter(activity,
                        messagesList);
                listView.setAdapter(adapter);
                noMessages.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
            } else {
                noMessages.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);

            }
        }
    }
}
