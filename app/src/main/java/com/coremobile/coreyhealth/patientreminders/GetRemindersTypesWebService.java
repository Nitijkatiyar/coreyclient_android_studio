package com.coremobile.coreyhealth.patientreminders;

import android.app.Activity;
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
public class GetRemindersTypesWebService extends
        AsyncTask<String, String, JSONObject> {
    ProgressDialog dialog;
    Activity activity;
    private String TAG = "Corey_GetReminderTypes";
    ArrayList<ReminderTypesModel> reminderTypeList = new ArrayList<ReminderTypesModel>();
    ListView listView;
    String toLoad;

    public GetRemindersTypesWebService(Activity activity, ListView listView, String toLoad) {
        this.activity = activity;
        this.listView = listView;
        this.toLoad = toLoad;
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

        url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                + "getremindertypes.aspx?token=" + CMN_Preferences.getUserToken(activity);

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
            if (json.getJSONObject("result").getInt("code") == 0) {
                JSONArray types = json.getJSONArray("reminderType");
                for (int i = 0; i < types.length(); i++) {
                    JSONObject object = types.getJSONObject(i);
                    ReminderTypesModel typesModel = new ReminderTypesModel();
                    typesModel.setId(object.getInt("id"));
                    typesModel.setDescription(object.getString("description"));
                    typesModel.setImageUrl(object.getString("imageURL"));
                    reminderTypeList.add(typesModel);
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
        } finally {
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
            ReminderTypesAdapter adapter = new ReminderTypesAdapter(activity,
                    reminderTypeList,toLoad);
            listView.setAdapter(adapter);
        }
    }
}
