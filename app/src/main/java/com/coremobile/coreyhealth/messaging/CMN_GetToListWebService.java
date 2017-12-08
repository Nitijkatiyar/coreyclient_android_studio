package com.coremobile.coreyhealth.messaging;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;

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
public class CMN_GetToListWebService extends AsyncTask<String, String, JSONObject> {
    ProgressDialog dialog;
    CMN_ToUserListActivity activity;
    CMN_NeworViewMessagesActivity activity1;
    private String TAG = "Corey_GetToList";
    ArrayList<CMN_MessageToModel> toList = new ArrayList<CMN_MessageToModel>();
    ListView listView;
    String topic, toMessage;

    public CMN_GetToListWebService(CMN_NeworViewMessagesActivity activity1,
                                   String string) {
        this.activity1 = activity1;
        toMessage = string;
        listView = null;
    }

    public CMN_GetToListWebService(CMN_ToUserListActivity activity, String string,
                                   ListView listView) {
        this.activity = activity;
        topic = string;
        this.listView = listView;
    }

    @Override
    protected void onPreExecute() {
        if (listView != null) {
            dialog = new ProgressDialog(activity);
        } else {
            dialog = new ProgressDialog(activity1);
        }
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
                + "GetListItems.aspx?listname=MESSAGES_TO_OPTION_LIST&token="
                + CMN_Preferences.getUserToken(activity);

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
            json = new JSONObject(EntityUtils.toString(response.getEntity()));
            if (json.getJSONObject("Result").getInt("Code") == 0) {

                JSONObject data = json.getJSONObject("Data");
                JSONArray topicsArray = data
                        .getJSONArray("MESSAGES_TO_OPTION_LIST");
                for (int i = 0; i < topicsArray.length(); i++) {
                    JSONObject jsonObject = topicsArray.getJSONObject(i);
                    CMN_MessageToModel topicModel = new CMN_MessageToModel();
                    topicModel.setCode(jsonObject.getString("Code"));
                    topicModel.setName(jsonObject.getString("Name"));
                    toList.add(topicModel);

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
            if (listView != null) {
                activity.refreshToList(toList);
            } else {
                String id = "";
                for (CMN_MessageToModel curVal : toList) {
                    if (curVal.getName().contains(toMessage)) {
                        id = curVal.getCode();
                    }
                }
                activity1.refreshToMessaeIds(id);
            }
        }
    }
}
