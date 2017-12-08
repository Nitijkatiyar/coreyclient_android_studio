package com.coremobile.coreyhealth.patientreminders;

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
import java.util.ArrayList;
import java.util.List;

/**
 * @author Nitij Katiyar
 */
public class GetPatientRemindersWebService extends
        AsyncTask<String, String, JSONObject> {
    ProgressDialog dialog;
    PatientRemindersActivityCMN activity;
    private String TAG = "Corey_GetPatientResponse";
    public static ArrayList<ReminderModel> reminderList = new ArrayList<ReminderModel>();
    ListView listView;
    String typeId;
    boolean isPatient, showdialog;
    TextView noMessages;
    private boolean isEdit;
    private boolean isUseForNewReminder;
    PatientRemindersAdapter adapter;

    public GetPatientRemindersWebService(PatientRemindersActivityCMN activity, ListView listView, TextView noMessages, boolean isEdit, boolean isUseForNewReminder, PatientRemindersAdapter adapter) {
        this.activity = activity;
        this.noMessages = noMessages;
        this.listView = listView;
        this.isEdit = isEdit;
        this.isUseForNewReminder = isUseForNewReminder;
        this.adapter = adapter;
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
                + "getreminders.aspx?token=" + CMN_Preferences.getUserToken(activity)+ "&patientid=" + params[0] + "&type=" + params[1]+"&stage=All";
        Log.d("Url", "" + url);
        typeId = params[1];
        if (typeId.equalsIgnoreCase("all")) {
            showdialog = true;
        } else {
            showdialog = false;
        }

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
            JSONObject object = json.getJSONObject("reminders");
            JSONArray reminderarray = object.getJSONArray("reminder");
            for (int i = 0; i < reminderarray.length(); i++) {
                JSONObject jsonObject = reminderarray.getJSONObject(i);
                ReminderModel reminderModel = new ReminderModel();
                reminderModel.setId(jsonObject.getString("id"));
                reminderModel.setApplyByDefault(jsonObject.getString("applyByDefault"));

                JSONObject frequency = jsonObject.getJSONObject("frequency");
                reminderModel.setFrequanyColHeading(frequency.getString("colHeading"));
                reminderModel.setFrequencyData(frequency.getString("data"));

                JSONObject msgToPatient = jsonObject.getJSONObject("msgToPatient");
                reminderModel.setMsgToPatientColHeading(msgToPatient.getString("colHeading"));
                reminderModel.setMsgToPatientData(msgToPatient.getString("data"));

                JSONObject prescribedOn = jsonObject.getJSONObject("prescribedOn");
                reminderModel.setPrescribedOnColHeading(prescribedOn.getString("colHeading"));
                reminderModel.setPrescribedOnData(prescribedOn.getString("data"));

                JSONObject providerNotificationPolicy = jsonObject.getJSONObject("providerNotificationPolicy");
                reminderModel.setProviderNotificationPolicyColHeading(providerNotificationPolicy.getString("colHeading"));
                reminderModel.setProviderNotificationPolicyData(providerNotificationPolicy.getString("data"));

                JSONObject times = jsonObject.getJSONObject("times");
                reminderModel.setTimesColHeading(times.getString("colHeading"));
                reminderModel.setTimesData(times.getString("data"));

                JSONObject title = jsonObject.getJSONObject("title");
                reminderModel.setTitleColHeading(title.getString("colHeading"));
                reminderModel.setTitleData(title.getString("data"));

                JSONObject stage = jsonObject.getJSONObject("RemStage");
                reminderModel.setStageColHeading(stage.getString("colHeading"));
                reminderModel.setStageData(stage.getString("data"));

                JSONObject type = jsonObject.getJSONObject("typeid");
                reminderModel.setRemindertypeColHeading(type.getString("colHeading"));
                reminderModel.setRemindertypeData(type.getString("data"));

                JSONObject stoponsuccess = jsonObject.getJSONObject("stopOnSuccess");
                reminderModel.setStopOnSuccess(stoponsuccess.getBoolean("data"));

                reminderList.add(reminderModel);
            }
//            }

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
            if (reminderList.size() > 0) {
                adapter = new PatientRemindersAdapter(activity,
                        reminderList, typeId, isEdit, isUseForNewReminder,false);
                listView.setAdapter(adapter);
                noMessages.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
                activity.invalidateOptionsMenu();
            } else {
                noMessages.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);

            }

        }
    }
}
