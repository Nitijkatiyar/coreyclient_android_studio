package com.coremobile.coreyhealth;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.coremobile.coreyhealth.base.CMN_AppBaseActivity;
import com.coremobile.coreyhealth.clientcertificate.CMN_HTTPSClient;
import com.coremobile.coreyhealth.googleAnalytics.CMN_Preferences;
import com.coremobile.coreyhealth.newui.MessageTabActivityCMN;

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
import java.util.List;

public class AddStaffActivityCMN extends CMN_AppBaseActivity {


    String TAG = "Corey_AddStaffActivityCMN";
    public static final String CURRENT_USER = "CurrentUser";
    int LoginIDChoice = 1;
    EditText mStaffNameField;
    String mOrganization;
    com.coremobile.coreyhealth.widget.InstantCompleteTextView mPhoneField;
    com.coremobile.coreyhealth.widget.InstantCompleteTextView mEmailField;
    Boolean IsAdmin = false;
    Button mContacts;
    Button mInvite;
    String mStaffname = null;
    ArrayList<String> mPhoneNoList = new ArrayList<String>();
    ArrayList<String> mEmailList = new ArrayList<String>();
    String mEmailId;
    ArrayAdapter<String> mPhonefieldAdaptor;
    ArrayAdapter<String> mEmailfieldAdaptor;
    CheckBox chkAdmin;
    private ProgressDialog mProgressDialog;

    String mloginid = "";
    String mDisplayname = "";
    String mEmailid = "";
    String mphoneno = "";
    String misAdmin = "NO";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_staff);
        chkAdmin = (CheckBox) findViewById(R.id.checkbox_admin);
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radiochoice);

        radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {

                    case R.id.radio_phone:
                        LoginIDChoice = 1;
                        break;
                    case R.id.radio_email:
                        LoginIDChoice = 2;
                        break;
                    case R.id.radio_name:
                        LoginIDChoice = 3;
                        break;
                }
            }
        });

        mStaffNameField = (EditText) findViewById(R.id.staff_name);
        mPhoneField = (com.coremobile.coreyhealth.widget.InstantCompleteTextView) findViewById(R.id.phoneno);
        mEmailField = (com.coremobile.coreyhealth.widget.InstantCompleteTextView) findViewById(R.id.email_id);
        addClearButton(mStaffNameField);
        addClearButton(mPhoneField);
        addClearButton(mEmailField);
        mContacts = (Button) findViewById(R.id.contacts);
        mInvite = (Button) findViewById(R.id.invite);
        mProgressDialog = new ProgressDialog(AddStaffActivityCMN.this);

        mInvite.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Boolean paramvalidity = true;
                mloginid = "";
                mDisplayname = mStaffNameField.getText().toString().trim();
                mEmailid = mEmailField.getText().toString().trim();
                mphoneno = mPhoneField.getText().toString().trim();
                if (IsAdmin) misAdmin = "YES";
                else misAdmin = "NO";
                if (mDisplayname != null && !mDisplayname.equals("")) {
                    switch (LoginIDChoice) {
                        case 1:
                            if (mphoneno == null || mphoneno.equals("")) paramvalidity = false;
                            mloginid = mphoneno;
                            break;
                        case 2:
                            if (mEmailid == null || !isEmailValid(mEmailid)) paramvalidity = false;
                            mloginid = mEmailid;
                            break;
                        case 3:
                            mloginid = mDisplayname;
                            break;

                    }
                    if (paramvalidity) {
                        new InviteAsyncTask().execute("dummy");

//                        Log.d(TAG, "user clicked Invite");
                    } else {
                        switch (LoginIDChoice) {
                            case 1:
                                Toast.makeText(getApplicationContext(),
                                        "Please enter proper phone no.", Toast.LENGTH_LONG).show();
                                break;
                            case 2:
                                Toast.makeText(getApplicationContext(),
                                        "Please enter proper Email Id.", Toast.LENGTH_LONG).show();
                                break;
                        }
                    }

                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter proper Name", Toast.LENGTH_LONG).show();
                }
            }
        });

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME
                    | ActionBar.DISPLAY_SHOW_TITLE
                    | ActionBar.DISPLAY_HOME_AS_UP);
            actionBar.show();

            actionBar.setTitle("Add Staff Member");
        }

        mPhonefieldAdaptor = new ArrayAdapter<String>(this, R.layout.autocompletelayout, mPhoneNoList);
        mPhoneField.setAdapter(mPhonefieldAdaptor);
        mEmailfieldAdaptor = new ArrayAdapter<String>(this, R.layout.autocompletelayout, mEmailList);
        mEmailField.setAdapter(mEmailfieldAdaptor);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void onAdminCheckboxClicked(View view) {
        if (chkAdmin.isChecked()) IsAdmin = true;
        else IsAdmin = false;
    }

    private void addClearButton(final EditText editText) {
        final Drawable x = getResources().getDrawable(R.drawable.presence_offline);//your x image, this one from standard android images looks pretty good actually
        x.setBounds(0, 0, x.getIntrinsicWidth(), x.getIntrinsicHeight());
        editText.setCompoundDrawables(null, null, editText.getText().toString().equals("") ? null : x, null);
        editText.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (editText.getCompoundDrawables()[2] == null) {
                    return false;
                }
                if (event.getAction() != MotionEvent.ACTION_UP) {
                    return false;
                }
                if (event.getX() > editText.getWidth() - editText.getPaddingRight() - x.getIntrinsicWidth()) {
                    editText.setText("");
                    editText.setCompoundDrawables(null, null, null, null);
                }
                return false;
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editText.setCompoundDrawables(null, null, editText.getText().toString().equals("") ? null : x, null);
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });
    }


    class InviteAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressDialog.setTitle("In Progress");
            mProgressDialog.setMessage("Please Wait ..");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(String... arg0) {

            String url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                    + "SendInvite.aspx?token=" + CMN_Preferences.getUserToken(AddStaffActivityCMN.this);

//            Log.i(TAG, "send invite url: " + url);

            if (Utils.checkInternetConnection()) {
                CloseableHttpClient httpclient = new CMN_HTTPSClient(MyApplication.INSTANCE, url).getClient();
                HttpPost httppost = new HttpPost(url);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                try {
                    nameValuePairs.add(new BasicNameValuePair("loginid", mloginid));
                    nameValuePairs.add(new BasicNameValuePair("Displayname", mDisplayname));
                    nameValuePairs.add(new BasicNameValuePair("Emailid", mEmailid));
                    nameValuePairs.add(new BasicNameValuePair("phoneno", mphoneno));
                    nameValuePairs.add(new BasicNameValuePair("isAdmin", misAdmin));


//                    Log.d(TAG, "nameValuePairs =" + nameValuePairs);

                    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
                            nameValuePairs);
                    entity.setContentEncoding(HTTP.UTF_8);
                    httppost.setEntity(entity);
                    httppost.setHeader("Content-Type",
                            "application/x-www-form-urlencoded");

                    HttpResponse response = httpclient.execute(httppost);

//                    Log.i(TAG, "UpdateApplicationData POST response: "
//                            + response.getStatusLine());

                    JSONObject json = null;
                    json = new JSONObject(EntityUtils.toString(response.getEntity()));
                    if (json != null) {

                        String jsonResult = json.getString("result");
                        String jsonError = json.getString("Error");
//                        Log.i(TAG, "Invite user json response: " + jsonResult);
                        if (jsonResult.equals("0")) return ("success");
                        else return (jsonError);
                    }

                } catch (UnsupportedEncodingException e1) {
                    e1.getMessage();
                    return "Please check Internet connection";
                } catch (ClientProtocolException e) {
                    e.getMessage();
                } catch (IOException e) {
                    e.getMessage();
                } catch (JSONException e) {
                    e.getMessage();
                }finally {
                    if(httpclient!=null){
                        try {
                            httpclient.close();
                        } catch (IOException e) {
                            e.getMessage();
                        }
                    }
                }

                return "Please check Internet connection";
            }
            return "unknown error";

        }

        @Override
        protected void onPostExecute(String result) {
            final String resulttxt = result;
            if (mProgressDialog != null && mProgressDialog.isShowing()) {

                mProgressDialog.dismiss();

            }
            if (result.equals("success")) {
                ClearScreen();

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        Toast.makeText(
                                getApplicationContext(),
                                "New Staff Member added",
                                Toast.LENGTH_LONG).show();

                    }
                });
            } else {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        Toast.makeText(
                                getApplicationContext(),
                                resulttxt,
                                Toast.LENGTH_LONG).show();

                    }
                });
            }
        }
    }


    private void ClearScreen() {
        mStaffname = null;
        mPhoneNoList.clear();
        mEmailList.clear();
        mStaffNameField.setText("");
        mPhoneField.setText("");
        mEmailField.setText("");
        mPhonefieldAdaptor.notifyDataSetChanged();
        mEmailfieldAdaptor.notifyDataSetChanged();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Log.i(TAG, "onDestroy");
        if (mProgressDialog != null && mProgressDialog.isShowing()) {

            mProgressDialog.dismiss();

        }
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
