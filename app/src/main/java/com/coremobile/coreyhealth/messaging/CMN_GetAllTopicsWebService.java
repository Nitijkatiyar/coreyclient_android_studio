package com.coremobile.coreyhealth.messaging;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;
import android.widget.Spinner;

import com.coremobile.coreyhealth.MyApplication;
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
public class CMN_GetAllTopicsWebService extends
        AsyncTask<String, String, JSONObject> {
    ProgressDialog dialog;
    CMN_ToUserListActivity activity;
    private String TAG = "Corey_GetAllTopics";
    ArrayList<CMN_MessageTopicsModel> topicsList = new ArrayList<CMN_MessageTopicsModel>();
    ListView listView;
    Spinner messageSubject;
    boolean shouldOpenSpinner;
    String topic;

    public CMN_GetAllTopicsWebService(CMN_ToUserListActivity activity, String string,
                                      ListView listView) {
        this.activity = activity;
        this.listView = listView;
        topic = string;
    }

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(activity);
        dialog.setMessage(CMN_ErrorMessages.getInstance().getValue(131));
        dialog.setCancelable(false);
        try {
            dialog.show();
        } catch (Exception e) {
            e.getMessage();
        }

        super.onPreExecute();
    }

    @Override
    protected JSONObject doInBackground(String... params) {

        String url = null;

        url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                + "GetListItems.aspx?listname=MESSAGES_TOPIC_LIST&&token="
                + CMN_Preferences.getUserToken(activity);

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
            json = new JSONObject(EntityUtils.toString(response.getEntity()));
            if (json.getJSONObject("Result").getInt("Code") == 0) {

                JSONObject data = json.getJSONObject("Data");
                JSONArray topicsArray = data
                        .getJSONArray("MESSAGES_TOPIC_LIST");
                for (int i = 0; i < topicsArray.length(); i++) {
                    JSONObject jsonObject = topicsArray.getJSONObject(i);
                    CMN_MessageTopicsModel topicModel = new CMN_MessageTopicsModel();
                    topicModel.setCode(jsonObject.getString("Code"));
                    topicModel.setTopicName(jsonObject.getString("Name"));
                    topicsList.add(topicModel);

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

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    protected void onPostExecute(JSONObject result) {
        super.onPostExecute(result);
        if (dialog.isShowing()) {
            dialog.dismiss();
            if (topicsList == null) {
                topicsList = new ArrayList<CMN_MessageTopicsModel>();
            }
            activity.refreshSubjectList(topicsList);
        }
    }
}
