package com.coremobile.coreyhealth;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.coremobile.coreyhealth.base.CMN_AppBaseActivity;
import com.coremobile.coreyhealth.clientcertificate.CMN_HTTPSClient;
import com.coremobile.coreyhealth.googleAnalytics.CMN_Preferences;
import com.coremobile.coreyhealth.newui.MessageTabActivityCMN;
import com.coremobile.coreyhealth.widget.CoreyEditText;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class RatingSelectActivityCMN extends CMN_AppBaseActivity {

    Context context = null;
    String ObjectId;
    CoreyEditText editText;
    JSONHelperClass mJSONHelperClass;
    //	double Appluse=0.0;
    private ProgressDialog mProgressDialog;
    ListView lv = null;
    String SelectedItems;
    String jsonResult = "";
    EditText edtSearch = null;
    LinearLayout llContainer = null;
    Button btnOK = null;
    CoreyDBHelper mCoreyDBHelper;
    RelativeLayout rlPBContainer = null;
    RatingAdapter mRatingAdapter;

    private static String TAG = "Corey_RatingSelectActivityCMN";
    public static final String CURRENT_USER = "CurrentUser";
    Timer UpdateTimer;
    Intent mIntent;
    String mMsgId = "123";
    String mMsgContext;
    String listitemname;
    String listid;
    int mesid;
    String mOrgname;
    //   ArrayList<VideoLink> mVideoList = new ArrayList<VideoLink>();
    ArrayList<ListItem> mListItems = new ArrayList<ListItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;
        mCoreyDBHelper = new CoreyDBHelper(this);
        setContentView(R.layout.rating_sel_container);
        mIntent = getIntent();
        listitemname = mIntent.getStringExtra("listvaluename");
        ObjectId = mIntent.getStringExtra("objectId");
        mesid = mIntent.getIntExtra("msgid", 1);
        //  mMsgContext=MsgScheduleTabActivity.getCache().getObject(ObjectId).getMsgContext();
        llContainer = (LinearLayout) findViewById(R.id.data_container);
        btnOK = (Button) findViewById(R.id.ok_button);
        mJSONHelperClass = new JSONHelperClass();
        SharedPreferences mcurrentUserPref = getSharedPreferences(
                CURRENT_USER, 0);
        mOrgname = mcurrentUserPref.getString("Organization", null);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP
                    | ActionBar.DISPLAY_SHOW_CUSTOM
                    | ActionBar.DISPLAY_SHOW_HOME);


            actionBar.setCustomView(R.layout.action_save);

            actionBar.setTitle("Provide Feedback");
            View actionBarCustomView = actionBar.getCustomView();
            if (actionBarCustomView != null) {
                Button save = (Button) actionBarCustomView
                        .findViewById(R.id.button2);//
                save.setText("SAVE");
                save.setBackgroundColor(getResources().getColor(R.color.dark_green));
                save.setVisibility(View.VISIBLE);
                // save.setOnClickListener(DetailedApplicationData.this);
                TextView appTextView = (TextView) actionBarCustomView
                        .findViewById(R.id.apptextView1);
                appTextView.setVisibility(View.VISIBLE);
                //	appTextView.setText(mAppName);
                appTextView.setText("Provide Feedback");
                //	appTextView.setSingleLine();
                appTextView.setTextSize(18);
                appTextView.setEllipsize(appTextView.getEllipsize());
                //	appTextView.getEllipsize();
            }
        }
        PopulateList();


        mRatingAdapter = new RatingAdapter(context, mListItems, this);
        lv = new ListView(context);

        lv.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                llContainer.addView(lv);
            }
        });
        lv.setAdapter(mRatingAdapter);
        editText = (CoreyEditText)
                findViewById(R.id.line2edit);
        editText.setFocusable(true);
        editText.setCursorVisible(true);
        //  editText.setBackgroundColor(Color.BLACK);
        TextView textView = (TextView)
                findViewById(R.id.line1);
        textView.setText("How else this app be made more useful for you");

        TextView feedbacktext = (TextView) findViewById(R.id.feedbacktext);
        feedbacktext.setText("Your Feedback is valuable to us.  We constantly strive to improve our services"
                + " based on your feedback.  "
                + "Please visit above url for further feedback\n"
                + "Thank You");
        Utils.makeTextViewMultiLine(feedbacktext);

        TextView feedbackurl = (TextView) findViewById(R.id.feedbackurl);
        String feedbkkurl = mJSONHelperClass.getFeedbackUrl(mOrgname);
        feedbackurl.setText(feedbkkurl);
        Utils.makeTextViewMultiLine(feedbackurl);
        Linkify.addLinks(feedbackurl, Linkify.ALL);

        mProgressDialog = new ProgressDialog(RatingSelectActivityCMN.this);
    }

    public void PopulateList() {
        ListItem litem;

        litem = new ListItem();
        litem.setDescription("How easy was it for you to use this App");
        litem.setdisplayText("How easy was it for you to use this App");
        mListItems.add(litem);
        litem = new ListItem();
        litem.setDescription("How useful do you find this App");
        litem.setdisplayText("How useful do you find this App");
        mListItems.add(litem);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void Updatetimout() {
        if (UpdateTimer != null) {
            UpdateTimer.cancel();
            UpdateTimer = null;

        }
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                Toast.makeText(RatingSelectActivityCMN.this,
                        "Update timed out", Toast.LENGTH_LONG).show();

            }
        });


    }

    public void save(View v1) {

   /* 	getSelectedItems();

    	 if (Utils.isEmpty(SelectedItems)) {
             Toast.makeText(context, "Select atleast one item",
                     Toast.LENGTH_SHORT).show();
         } else {
  
         	SelectedItems = SelectedItems.substring(0, SelectedItems.length() - 1); */
        UpdateTimer = new Timer();
        UpdateTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG, "Update timed out");
                Updatetimout();
            }
        }, CMN_Preferences.getUpdateWaitout(this));

        new MultiUpdateAsyncTask().execute("dummystring");
        //   new RateUpdateAsyncTask().execute("dummystring");
        //   new MultiUpdateAsyncTask1().execute("dummystring");
        //      Toast.makeText(context, "Selected items : " + SelectedItems,
        //              Toast.LENGTH_SHORT).show();

        //   }
  
	/*
     UpdateTimer= new Timer();
	UpdateTimer.schedule(new TimerTask()
    {
        @Override
        public void run()
        {
            Log.d(TAG,"Update timed out");
            Updatetimout();
        }
    }, 30000 ); 
	
	new MultiUpdateAsyncTask().execute("dummystring"); */

    }

    private JSONObject getSelectedItems() {
        // TODO Auto-generated method stub
        JSONObject jObject = new JSONObject();
        String comment = editText.getText().toString();

        double appuse = mListItems.get(0).getRating();

        double appeasy = mListItems.get(1).getRating();
        try {
            jObject.accumulate("Comment", comment);
            jObject.accumulate("AppUsefullnessRating", appuse);
            jObject.accumulate("AppEaseOfUseRating", appeasy);

        } catch (JSONException e) {
            e.getMessage();
        }
        return jObject;

    }


    class MultiUpdateAsyncTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressDialog.setTitle("Updating");
            mProgressDialog.setMessage("Please Wait ..");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected Integer doInBackground(String... arg0) {

            SharedPreferences mcurrentUserPref = getSharedPreferences(
                    CURRENT_USER, 0);
            String url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                    + "AddFeedback.aspx?token=" + CMN_Preferences.getUserToken(RatingSelectActivityCMN.this);
            Log.d(TAG, "url: %%%%%%%% " + url);


            if (Utils.checkInternetConnection()) {
                CloseableHttpClient httpclient = new CMN_HTTPSClient(MyApplication.INSTANCE, url).getClient();
                HttpPost httppost = new HttpPost(url);
                JSONArray jarray = new JSONArray();
                JSONObject jFieldObject = null;
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs
                        .add(new BasicNameValuePair("type", "web_server"));
                try {
                    JSONObject jObject = new JSONObject();
                    jFieldObject = getSelectedItems();

			/*	nameValuePairs.add(new BasicNameValuePair("Comment",
    		    		jFieldObject.getString("Comment")));
    		    nameValuePairs.add(new BasicNameValuePair("AppUsefullnessRating",
    		    		jFieldObject.getString("AppUsefullnessRating")));
    		    nameValuePairs.add(new BasicNameValuePair("AppEaseOfUseRating",
    		    		jFieldObject.getString("AppEaseOfUseRating"))); 
    		  */

                    //    jarray.put(jFieldObject);
                    //    jObject.accumulate("Data", jarray);
                    //    nameValuePairs.add(new BasicNameValuePair("Data",
                    //		    jObject.toString()));
                    nameValuePairs.add(new BasicNameValuePair("Data",
                            jFieldObject.toString()));
                    Log.d(TAG, "nameValuePairs =" + nameValuePairs);

                    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
                            nameValuePairs);


                    Log.d(TAG, "entity = " + entity);

                    entity.setContentEncoding(HTTP.UTF_8);

                    //    httppost.setEntity(entity);
                    httppost.setEntity(new StringEntity(jFieldObject.toString()));
    		/*    httppost.getParams().setParameter("userName", userName);
                httppost.getParams().setParameter("password", password);
                httppost.getParams().setParameter("Organization", mOrganization);*/

                    //   httppost.setHeader("Content-Type",
                    //	    "application/x-www-form-urlencoded");
                    httppost.setHeader("Accept", "application/json");
                    httppost.setHeader("Content-type", "application/json");
                    Log.d(TAG, "httppost uri" + httppost.getURI().toString());
                    //	Log.d(TAG, "httppost parametrs username"+httppost.getParams().getParameter("username"));
                    HttpResponse response = httpclient.execute(httppost);
                    Log.d(TAG, "ratingapi POST response: "
                            + response.getStatusLine());
                    JSONObject json = null;
                    json = new JSONObject(EntityUtils.toString(response.getEntity()));
                    if (json != null) {

                        jsonResult = json.getString("result");
                        Log.d(TAG,
                                "Rating api %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% json response: "
                                        + jsonResult);
                        if (UpdateTimer != null) {
                            UpdateTimer.cancel();
                            UpdateTimer = null;

                        }
                        Log.d(TAG,
                                "Rating api %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% json error: "
                                        + json.getString("Error"));

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

                if (jsonResult.equals("0")) return 0;
                else return -1;
            } else return -1;

        }


        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            Log.d(TAG, "APi resutlt=" + result);
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            //   Toast.makeText(context, "Selected items : " + SelectedItems,
            //                 Toast.LENGTH_SHORT).show();
            if (result == 0) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        Toast.makeText(RatingSelectActivityCMN.this,
                                "Update success", Toast.LENGTH_LONG).show();

                    }
                });

            } else {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        Toast.makeText(RatingSelectActivityCMN.this,
                                "Update failure", Toast.LENGTH_LONG).show();

                    }
                });
            }

            finish();
        }
    }

    class MultiUpdateAsyncTask1 extends AsyncTask<String, Void, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressDialog.setTitle("Updating");
            mProgressDialog.setMessage("Please Wait ..");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected Integer doInBackground(String... arg0) {

            SharedPreferences mcurrentUserPref = getSharedPreferences(
                    CURRENT_USER, 0);
            String url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                    + "AddFeedback.aspx";
            String url1 = url + "?" + "token=" + CMN_Preferences.getUserToken(RatingSelectActivityCMN.this);
            Log.d(TAG, "url: %%%%%%%% " + url);


            if (Utils.checkInternetConnection()) {
                CloseableHttpClient httpclient = new CMN_HTTPSClient(MyApplication.INSTANCE, url1).getClient();
                HttpPost httppost = new HttpPost(url1);
                JSONArray jarray = new JSONArray();
                JSONObject jFieldObject = null;
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs
                        .add(new BasicNameValuePair("type", "web_server"));
                try {
                    JSONObject jObject = new JSONObject();
                    jFieldObject = getSelectedItems();

    		/*    nameValuePairs.add(new BasicNameValuePair("Comment",
    		    		jFieldObject.getString("Comment")));
    		    nameValuePairs.add(new BasicNameValuePair("AppUsefullnessRating",
    		    		jFieldObject.getString("AppUsefullnessRating")));
    		    nameValuePairs.add(new BasicNameValuePair("AppEaseOfUseRating",
    		    		jFieldObject.getString("AppEaseOfUseRating"))); 
    		  */

                    //    jarray.put(jFieldObject);
                    //    jObject.accumulate("Data", jarray);
                    //    nameValuePairs.add(new BasicNameValuePair("Data",
                    //		    jObject.toString()));
                    nameValuePairs.add(new BasicNameValuePair("Data",
                            jFieldObject.toString()));
                    Log.d(TAG, "nameValuePairs =" + nameValuePairs);

                    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
                            nameValuePairs);


                    Log.d(TAG, "entity = " + entity);

                    entity.setContentEncoding(HTTP.UTF_8);

                    //    httppost.setEntity(entity);
                    //    httppost.setEntity(new StringEntity(jFieldObject.toString()));
    		/*    httppost.getParams().setParameter("userName", userName);
                httppost.getParams().setParameter("password", password);
                httppost.getParams().setParameter("Organization", mOrganization);*/

                    //   httppost.setHeader("Content-Type",
                    //	    "application/x-www-form-urlencoded");
                    httppost.setHeader("Accept", "application/json");
                    httppost.setHeader("Content-type", "application/json");
                    Log.d(TAG, "httppost uri" + httppost.getURI().toString());
                    //	Log.d(TAG, "httppost parametrs username"+httppost.getParams().getParameter("username"));
                    HttpResponse response = httpclient.execute(httppost);
                    Log.d(TAG, "ratingapi POST response: "
                            + response.getStatusLine());
                    JSONObject json = null;
                    json = new JSONObject(EntityUtils.toString(response.getEntity()));
                    if (json != null) {

                        jsonResult = json.getString("result");
                        Log.d(TAG,
                                "Rating api %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% json response: "
                                        + jsonResult);
                        Log.d(TAG,
                                "Rating api %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% json error: "
                                        + json.getString("Error"));

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

                if (jsonResult.equals("0")) return 0;
                else return -1;
            } else return -1;

        }


        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            Log.d(TAG, "APi resutlt=" + result);
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            //   Toast.makeText(context, "Selected items : " + SelectedItems,
            //                 Toast.LENGTH_SHORT).show();
            if (result == 0) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        Toast.makeText(RatingSelectActivityCMN.this,
                                "Update success", Toast.LENGTH_LONG).show();

                    }
                });

            } else {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        Toast.makeText(RatingSelectActivityCMN.this,
                                "Update failure", Toast.LENGTH_LONG).show();

                    }
                });
            }

            finish();
        }
    }

    class RateUpdateAsyncTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressDialog.setTitle("Sending...");
            mProgressDialog.setMessage("Please Wait ..");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @SuppressWarnings("deprecation")
        @Override
        protected Integer doInBackground(String... arg0) {

            SharedPreferences mcurrentUserPref = getSharedPreferences(
                    CURRENT_USER, 0);
            String userName = mcurrentUserPref.getString("Username", null);
            String password = mcurrentUserPref.getString("Password", null);
            String mOrganization = mcurrentUserPref.getString("Organization", null);
            String url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                    + "AddFeedback.aspx";
            String url1 = url + "?" + "username=" + userName + "&Password=" + password + "&Organization=" + mOrganization;
//    	    Log.i(TAG, "url: %%%%%%%% " + url1);

            if (Utils.checkInternetConnection()) {
                CloseableHttpClient httpclient = new CMN_HTTPSClient(MyApplication.INSTANCE, url1).getClient();
                HttpPost httppost = new HttpPost(url1);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs
                        .add(new BasicNameValuePair("type", "JSON"));

                Object responseString;
                try {

                    MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                    builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

                    builder.addTextBody("token", CMN_Preferences.getUserToken(RatingSelectActivityCMN.this));
                    JSONObject jObject = new JSONObject();
                    jObject = getSelectedItems();
                    //	ContentBody contentbody= new ContentBody();
                    builder.addTextBody("data", jObject.toString());
                    //		builder.addPart("data", jObject.);
                    HttpEntity entity = builder.build();


                    long totalSize = entity.getContentLength();
                    Log.d(TAG, "entity =" + entity);
                    httppost.setEntity(entity);
            /*    httppost.getParams().setParameter("userName", "p1");
                httppost.getParams().setParameter("password", password);
                httppost.getParams().setParameter("Organization", mOrganization); */
                    httppost.setHeader("Accept", "application/json");
                    httppost.setHeader("Content-type", "application/json");
                    // Making server call
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity r_entity = response.getEntity();

                    int statusCode = response.getStatusLine().getStatusCode();
                    if (statusCode == 200) {
                        // Server response
                        responseString = EntityUtils.toString(r_entity);
                        Log.d(TAG, "reponse=" + responseString);
                    } else {
                        responseString = "Error occurred! Http Status Code: "
                                + statusCode;
                        Log.d(TAG, "error =" + responseString);
                    }

                } catch (ClientProtocolException e) {
                    responseString = e.toString();
                    Log.d(TAG, "Clientprotocal exception error =" + responseString);
                } catch (IOException e) {
                    responseString = e.toString();
                    Log.d(TAG, "IOexceptionerror =" + responseString);
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
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        if (UpdateTimer != null) {
            UpdateTimer.cancel();
            UpdateTimer = null;

        }
        Log.d(TAG, "onDestroy");

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

}
