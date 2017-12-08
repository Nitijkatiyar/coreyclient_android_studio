package com.coremobile.coreyhealth;

import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.coremobile.coreyhealth.googleAnalytics.CMN_Preferences;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import static android.content.Context.MODE_PRIVATE;
//import android.util.Log;

class VerifyUser extends AsyncTask<String, Void, JSONObject> {
    private HashMap<String, String> mData = null;// post data
    private IServerConnect mActivity = null;
    private String CMN_SERVER_BASE_URL_DEFINE;
    public static final String CURRENT_USER = "CurrentUser";
    SharedPreferences mcurrentUserPref;

    /**
     * constructor
     */
    VerifyUser(IServerConnect activity, HashMap<String, String> data) {
        mActivity = activity;
        mData = data;
        mcurrentUserPref = MyApplication.INSTANCE.getSharedPreferences(CURRENT_USER, MODE_PRIVATE);
        JSONHelperClass jsonHelperClass = new JSONHelperClass();
        CMN_SERVER_BASE_URL_DEFINE = jsonHelperClass.getBaseURL(mcurrentUserPref.getString("Organization", ""));

    }


    @Override
    protected JSONObject doInBackground(String... urls) {
        // TODO Auto-generated method stub
        if (urls == null)
            return null;
        // set up post data
        ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
        Iterator<String> it = mData.keySet().iterator();

        JSONObject jsonObject = new JSONObject();
        while (it.hasNext()) {
            String key = it.next();
            nameValuePair.add(new BasicNameValuePair(key, mData.get(key)));
            try {
                jsonObject.put(key, mData.get(key));
            } catch (JSONException e) {
                e.getMessage();
            }
        }
        JSONObject json = null;

        HttpRequest httpReq = new HttpRequest(mData, urls[0], nameValuePair, CMN_SERVER_BASE_URL_DEFINE);
        json = httpReq.doRequest();

        if (json == null)
            mActivity.throwToast("Unable to access the Network. Please check your Network connectivity and try again.");
        else if (json.has("uses_scheduling")) {
            CMN_Preferences.setUseScheduling(MyApplication.INSTANCE, Boolean.parseBoolean(json.optString("uses_scheduling")));
        }

        return json;
    }

    protected void onPostExecute(JSONObject json) {
        //Log.v(TAG, "onPostExecute json: " + json);

        mActivity.gotUserInfoFromServer(json);
    }

    protected void onPreExecute() {
        //Log.e(TAG, "Creating Dialog");
        mActivity.showDialog();
    }
}
