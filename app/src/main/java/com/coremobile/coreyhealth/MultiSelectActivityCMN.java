package com.coremobile.coreyhealth;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
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
import com.coremobile.coreyhealth.newui.MsgScheduleTabActivity;

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
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MultiSelectActivityCMN extends CMN_AppBaseActivity {

    Context context = null;
    String ObjectId;
    MultiselectAdapter objAdapter;
    private ProgressDialog mProgressDialog;
    ListView lv = null;
    String SelectedItems;
    String reasons;
    EditText edtSearch = null;
    LinearLayout llContainer = null;
    Button btnOK = null;
    CoreyDBHelper mCoreyDBHelper;
    RelativeLayout rlPBContainer = null;
    MultiselectAdapter mMultiselAdapter;
    String jsonResult = "";
    private static String TAG = "Corey_MultiSelectActivityCMN";
    public static final String CURRENT_USER = "CurrentUser";
    Timer UpdateTimer;
    Intent mIntent;
    String mMsgId = "123";
    String mMsgContext;
    String listitemname;
    public static String listid;
    String title;
    String fieldId;
    int mesid;
    //   ArrayList<VideoLink> mVideoList = new ArrayList<VideoLink>();
    ArrayList<ListItem> mListItems = new ArrayList<ListItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;
        mCoreyDBHelper = new CoreyDBHelper(this);
        setContentView(R.layout.multi_sel_container);
        mIntent = getIntent();
        listitemname = mIntent.getStringExtra("listvaluename");
        title = mIntent.getStringExtra("title");
        ObjectId = mIntent.getStringExtra("ObjID");
        mesid = mIntent.getIntExtra("msgid", 1);
        fieldId = mIntent.getStringExtra("fieldId");
        if (ObjectId == null) {
            return;
        }
        mMsgContext = MsgScheduleTabActivity.getCache().getObject(ObjectId).getMsgContext();
        llContainer = (LinearLayout) findViewById(R.id.data_container);
        btnOK = (Button) findViewById(R.id.ok_button);

        SharedPreferences mcurrentUserPref = getSharedPreferences(
                CURRENT_USER, 0);

        ActionBar actionBar = getActionBar();
        Button save = null;
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP
                    | ActionBar.DISPLAY_SHOW_CUSTOM
                    | ActionBar.DISPLAY_SHOW_HOME);


            actionBar.setCustomView(R.layout.action_save);

            actionBar.setTitle(listitemname);
            View actionBarCustomView = actionBar.getCustomView();

            if (actionBarCustomView != null) {
                save = (Button) actionBarCustomView
                        .findViewById(R.id.button2);//
                save.setText("SAVE");
                save.setVisibility(View.VISIBLE);
                // save.setOnClickListener(DetailedApplicationData.this);
                TextView appTextView = (TextView) actionBarCustomView
                        .findViewById(R.id.apptextView1);
                appTextView.setVisibility(View.VISIBLE);
                //	appTextView.setText(mAppName);
                appTextView.setText(title);
                //	appTextView.setSingleLine();
                appTextView.setTextSize(18);
                appTextView.setEllipsize(appTextView.getEllipsize());
                //	appTextView.getEllipsize();
            }
        }

        if (listitemname == null || (getListItem(listitemname) == 0)) {
            TextView emptyTextView = (TextView) findViewById(R.id.empty);
            emptyTextView.setGravity(Gravity.CENTER_VERTICAL);
            emptyTextView.setText("No data available");
            emptyTextView.setVisibility(View.VISIBLE);
            if (save != null) {
                save.setVisibility(View.GONE);
            }

        } else {

            listid = mCoreyDBHelper.getListId(listitemname, mesid);

            List<ListItem> list = mListItems;

            HashMap<String, ListItem> fieldmap = new HashMap<>();
            for (int i = 0; i < list.size(); i++) {
                fieldmap.put(list.get(i).getId(), list.get(i));
            }
            mListItems = new ArrayList<ListItem>(fieldmap.values());


            mMultiselAdapter = new MultiselectAdapter(context, mListItems, this);
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
            lv.setAdapter(mMultiselAdapter);

        }

        mProgressDialog = new ProgressDialog(MultiSelectActivityCMN.this);
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
                Toast.makeText(MultiSelectActivityCMN.this,
                        "Update timed out", Toast.LENGTH_LONG).show();

            }
        });


    }

    public void save(View v1) {
        getSelectedItems();

        if (Utils.isEmpty(SelectedItems)) {
            Toast.makeText(context, "Select atleast one item",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        SelectedItems = SelectedItems.substring(0, SelectedItems.length() - 1);
        reasons = reasons.substring(0, reasons.length() - 1);

        if (fieldId != null && fieldId.equalsIgnoreCase("211") || fieldId.equalsIgnoreCase("233")) {
            Intent intent = new Intent();
            intent.putExtra("data", SelectedItems);
            intent.putExtra("reason", reasons);
            setResult(RESULT_OK, intent);
            finish();
        } else {


            UpdateTimer = new Timer();
            UpdateTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Log.d(TAG, "Update timed out");
                    Updatetimout();
                }
            }, CMN_Preferences.getUpdateWaitout(this));

            new MultiUpdateAsyncTask().execute("dummystring");
            //      Toast.makeText(context, "Selected items : " + SelectedItems,
            //              Toast.LENGTH_SHORT).show();

        }
  
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

    private void getSelectedItems() {
        // TODO Auto-generated method stub

        StringBuffer sb = new StringBuffer();
        StringBuffer sb1 = new StringBuffer();

        for (ListItem bean : mListItems) {

            if (bean.isSelected()) {
                sb.append(bean.getId());
                sb.append(",");
                sb1.append(bean.getdisplayText());
                sb1.append(",");
            }
        }

        SelectedItems = sb.toString().trim();
        reasons = sb1.toString().trim();
        Log.d(TAG, "SelectedItems=" + SelectedItems);
 /*
        if (Utils.isEmpty(SelectedItems)) {
            Toast.makeText(context, "Select atleast one item",
                    Toast.LENGTH_SHORT).show();
        } else {
 
        	SelectedItems = SelectedItems.substring(0, SelectedItems.length() - 1);
            Toast.makeText(context, "Selected items : " + SelectedItems,
                    Toast.LENGTH_SHORT).show();
 
        } */

    }

    public int getListItem(String litem) {
         /*
          * populate Item level variables next
    	  * Get all the rows with item as object name.
    	  * Extract the list from the rows which has the definit listname
    	  *
    	  */
        Log.d(TAG, "litem =" + litem);
        Cursor ItemCursor = this.getContentResolver().query(
                DatabaseProvider.CONTENT_URI,
                new String[]{DatabaseProvider.KEY_ID,
                        DatabaseProvider.KEY_FIELD_NAME,
                        DatabaseProvider.KEY_FIELD_DISPLAYTEXT,
                        DatabaseProvider.KEY_FIELD_VALUE,
                        DatabaseProvider.KEY_PARENT_ID_CONTEXT
                },
                DatabaseProvider.KEY_MSGID + " =? AND "
                        + DatabaseProvider.KEY_APPLICATION
                        + " =? AND "
                        + DatabaseProvider.KEY_OBJECT_NAME
                        + " =? AND "
                        + DatabaseProvider.KEY_PARENT_ID_CONTEXT
                        + " =?  ",
                new String[]{String.valueOf(mesid), "ListItems", "Item", litem}, null);
        //	String ItemName=ItemCursor.getString(2);
        //	Log.d(TAG,"ItemName outside loop="+ItemName);
        if (ItemCursor != null && ItemCursor.getCount() > 0) {
            int i = 0;
            int index = 0;
            String ListItemName = "";
            String PrevListItemName = "";
            String selection;

            while (ItemCursor.moveToNext()) {
                int switchval = i % 11;
                Log.d(TAG, "switchval" + switchval);
                switch (switchval) {
                    case 0:
                        index = i / 11;
                        ListItem listitem = new ListItem();
                        listitem.setValue(ItemCursor.getString(2));
                        //	Log.d(TAG,"Value="+ItemCursor.getString(2));
                        mListItems.add(listitem);
                        //	Log.d(TAG,"ListItemName=" +ListItemName);
                        //	Log.d(TAG,"ListItemValue=" +ItemCursor.getString(2));
                        //	Log.d(TAG, "i/5 =" +index);
                        //	Log.d(TAG,"value written in =" +ListItemObjMap.get(ListItemName).ListItemArray.get(index).getValue());
                        //	Log.d(TAG,"value written in case 0 with index0 =" +ListItemObjMap.get(ListItemName).ListItemArray.get(0).getValue());
                        break;
                    case 1:    //Log.d(TAG,"value written in case 1 with index0 before next write=" +ListItemObjMap.get(ListItemName).ListItemArray.get(0).getValue());
                        mListItems.get(index).setdisplayText(ItemCursor.getString(2));

                        break;
                    case 2:
                        mListItems.get(index).setImage(ItemCursor.getString(2));
                        //Log.d(TAG,"image="+ItemCursor.getString(2));
                        break;
                    case 3:
                        mListItems.get(index).setSubscript(ItemCursor.getString(2));
                        //Log.d(TAG,"subscript="+ItemCursor.getString(2));
                        break;
                    case 4:
                        mListItems.get(index).setBackgroundColor(ItemCursor.getString(2));
                        //Log.d(TAG,"bkgclr="+ItemCursor.getString(2));
                        break;
                    case 5:
                        mListItems.get(index).setDescription(ItemCursor.getString(2));
                        //Log.d(TAG,"bkgclr="+ItemCursor.getString(2));
                        break;
                    case 6:
                        mListItems.get(index).setId(ItemCursor.getString(2));
                        //Log.d(TAG,"bkgclr="+ItemCursor.getString(2));
                        break;
                    case 7:
                        mListItems.get(index).setOpenUrl(ItemCursor.getString(2));
                        // Log.d(TAG,"openurl="+ItemCursor.getString(2));
                        break;
                    case 8:
                        selection = ItemCursor.getString(2);
//                        Log.d(TAG, "isSelected from db= " + selection);
                        if (selection != null) {
                            if (selection.equalsIgnoreCase("yes"))
                                mListItems.get(index).setSelected(true);
                            else mListItems.get(index).setSelected(false);
                        } else mListItems.get(index).setSelected(false);
                        Log.d(TAG, "isSelected= " + mListItems.get(index).isSelected());
                        break;
                    case 9:
                        break;
                    case 10:
                        break;
                    default:
                        Log.d(TAG, "in  switch default");
                        break;
                }
                i++;
                //Log.d(TAG,"i in Itemcursor loop" +i);
            }
            if (ItemCursor != null) ItemCursor.close();
        }


        Log.d(TAG, "mListItems.size()" + mListItems.size());
        return (mListItems.size());
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
                    + "SetMultiSelectListValues.aspx?token=" + CMN_Preferences.getUserToken(MultiSelectActivityCMN.this);

            Log.d(TAG, "url: %%%%%%%%%%%%%%%%%%%%%%%%%%" + url);

            if (Utils.checkInternetConnection()) {
                CloseableHttpClient httpclient = new CMN_HTTPSClient(MyApplication.INSTANCE, url).getClient();
                HttpPost httppost = new HttpPost(url);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs
                        .add(new BasicNameValuePair("type", "web_server"));
                try {

                    nameValuePairs.add(new BasicNameValuePair("Context",
                            mMsgContext));
                    nameValuePairs.add(new BasicNameValuePair("listid",
                            listid));
                    nameValuePairs.add(new BasicNameValuePair("selection",
                            SelectedItems));

                    Log.d(TAG, "nameValuePairs =" + nameValuePairs);

                    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
                            nameValuePairs);
                    Log.d(TAG, "entity = " + entity);
                    entity.setContentEncoding(HTTP.UTF_8);
                    httppost.setEntity(entity);
                    httppost.setHeader("Content-Type",
                            "application/x-www-form-urlencoded");

                    HttpResponse response = httpclient.execute(httppost);
                    Log.d(TAG, "SetMultiSelectListValues POST response: "
                            + response.getStatusLine());
                    JSONObject json = null;
                    json = new JSONObject(EntityUtils.toString(response.getEntity()));
                    if (json != null) {

                        jsonResult = json.getString("result");
                        Log.d(TAG,
                                "SetMultiSelectListValues %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% json response: "
                                        + jsonResult);
                        Log.d(TAG,
                                "SetMultiSelectListValues api %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% json error: "
                                        + json.getString("Error"));
                        UpdateTimer.cancel();
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

                        Toast.makeText(MultiSelectActivityCMN.this,
                                "Update success", Toast.LENGTH_LONG).show();

                    }
                });

            } else {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        Toast.makeText(MultiSelectActivityCMN.this,
                                "Update failure", Toast.LENGTH_LONG).show();

                    }
                });
            }

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
