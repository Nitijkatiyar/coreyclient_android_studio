package com.coremobile.coreyhealth.patientreminders;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Nitij Katiyar
 */
public class GetPatientResponseWebService extends
        AsyncTask<String, String, JSONObject> {
    ProgressDialog dialog;
    Activity activity;
    private String TAG = "Corey_GetPatientResponse";
    ArrayList<ReminderResponseModel> reminderList = new ArrayList<ReminderResponseModel>();
    ListView listView;
    TextView noMessages;


    public GetPatientResponseWebService(Activity activity, ListView listView, TextView noMessages) {
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

        String url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                + "getpatientresponses.aspx?token=" + CMN_Preferences.getUserToken(activity) + "&type=" + params[1] + "&reminderid=" + params[2];
        try {
            url = url + "&patientid=" + URLEncoder.encode(params[0], "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
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
            json = new JSONObject(EntityUtils.toString(response.getEntity()));
            if (json.getJSONObject("Result").getInt("Code") == 0) {
                JSONArray reminderarray = json.getJSONArray("Data");
                for (int i = 0; i < reminderarray.length(); i++) {
                    JSONObject jsonObject = reminderarray.getJSONObject(i);
                    JSONArray array = jsonObject.getJSONArray("ResponseDetails");
                    for (int j = 0; j < array.length(); j++) {
                        JSONObject object = array.getJSONObject(j);
                        ReminderResponseModel reminderModel = new ReminderResponseModel();
                        reminderModel.setId(jsonObject.getInt("ReminderID"));
                        reminderModel.setReminderName(jsonObject.getString("ReminderName"));
                        reminderModel.setDate(object.getString("datetime"));
                        reminderModel.setResponse(object.getString("response"));

                        reminderList.add(reminderModel);
                    }


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
            if (reminderList.size() > 0) {
                ReminderResponseAdapter responseAdapter = new ReminderResponseAdapter(activity, reminderList);
                listView.setAdapter(responseAdapter);
                noMessages.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
            } else {
                noMessages.setVisibility(View.VISIBLE);
                noMessages.setText("There are no responses to display");
                listView.setVisibility(View.GONE);

            }


        }
    }
}
