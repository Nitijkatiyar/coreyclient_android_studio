package com.coremobile.coreyhealth;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.fingerprint.FingerprintManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coremobile.coreyhealth.googleAnalytics.CMN_Preferences;
import com.coremobile.coreyhealth.newui.MessageTabActivityCMN;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.ArrayList;

public class SumMenuActivity extends Activity implements IDownloadJSON {
    String TAG = "Corey_SumMenuActivity";
    ImageView mlogoview;
    JSONHelperClass jsonHelperClass;
    private static String APP_PATH;
    private String path;
    public static String mDisplayName;
    private SharedPreferences mCurrentUserPref;
    public static final String CURRENT_USER = "CurrentUser";
    private ActionBar mActionBar;
    private String UnReadMessgeCount = "0";
    String mUserName;
    String mPassword;
    String organizationName;
    String user_category;
    private String CMN_SERVER_BASE_URL_DEFINE;
    ArrayList<TabDtl> mTabDtl;
    TextView MsgcountText;
    private ProgressDialog mProgressDialog;
    AlertDialog alertDialogEnableTouchId = null;
    boolean ifsettingOpened = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.summerymenu);
        jsonHelperClass = new JSONHelperClass();
        mTabDtl = jsonHelperClass.getTabDtl();
        mCurrentUserPref = getSharedPreferences(CURRENT_USER, 0);

        organizationName = mCurrentUserPref.getString("Organization", "");

        CMN_SERVER_BASE_URL_DEFINE = jsonHelperClass.getBaseURL(organizationName);
        APP_PATH = MyApplication.INSTANCE.AppConstants.getAppFilesPath();
        mlogoview = (ImageView) findViewById(R.id.row0_leftimage1);

        mDisplayName = mCurrentUserPref.getString("DisplayName",
                organizationName);
        mUserName = mCurrentUserPref.getString("Username", null);
        mPassword = mCurrentUserPref.getString("Password", null);
        user_category = mCurrentUserPref.getString("user_category", "");
        Log.d(TAG, "username = " + mUserName + " Organization = " + organizationName);
        mActionBar = getActionBar();
        mProgressDialog = new ProgressDialog(this);
    /*	mProgressDialog.setTitle("Downloading Images");
        mProgressDialog.setMessage("Please Wait ..");
		mProgressDialog.setCancelable(false);
		mProgressDialog.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		mProgressDialog.show(); */

        Log.d(TAG, "Imagedownload before while loop =" + MyApplication.INSTANCE.ImagedownloadCount);
        new downloadImages().execute("dummystring");


        mActionBar.setTitle(mDisplayName);
        MsgcountText = (TextView) findViewById(R.id.msgcount);
        String img1 = null;
        img1 = jsonHelperClass.getFSRow0().mImageUrl;
        //    mContactmail = jsonHelperClass.getAppAttributes("MERITUS").get(1);
        Log.d(TAG, "Logo Image is " + img1);
        if (img1 != null && !TextUtils.isEmpty(img1)) {
            Uri uri = Uri.parse(img1);
            String url = uri.getLastPathSegment();
            img1 = url.toString()
                    .toLowerCase().replaceAll("\\s+", "")
                    .replaceAll("-", "")
                    .replaceAll("/", "")
                    .replaceAll("[(+^)_]*", "");
            url = null;
        }
        if (!TextUtils.isEmpty(img1) && img1 != null) {
            path = APP_PATH
                    + img1.toString().toLowerCase()
                    .replaceAll("\\s+", "").replaceAll("-", "").replaceAll("/", "")
                    .replaceAll("[(+^)_]*", "").replace(".jpg", ".png");

        }

        FileInputStream in;
        BufferedInputStream buf;
        try {
            Log.d(TAG, "path in file reading =" + path);

            in = new FileInputStream(path);
            buf = new BufferedInputStream(in);
            Bitmap bMap = BitmapFactory.decodeStream(buf);
            Log.d(TAG, "bMap" + bMap);
            Log.d(TAG, "bMap size" + bMap);
            mlogoview.setImageBitmap(bMap);
            if (in != null) {
                in.close();
            }
            if (buf != null) {
                buf.close();
            }
        } catch (Exception e) {
            Log.d("Error reading file", e.toString());

            Log.d(TAG, "path =" + path);
        }
        TextView row0text = (TextView) findViewById(R.id.row0_disp);
        ArrayList<String> disparray = jsonHelperClass.getFSRow0().mData;
        StringBuilder sb = new StringBuilder();
        for (String str : disparray) {
            sb.append(str);
            sb.append("\n");
        }
        row0text.setText(sb);
        RelativeLayout menuBtn1 = (RelativeLayout) findViewById(R.id.btn_lyt1);
        menuBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "callingActivity: " + getLocalClassName());
                Intent nextActivity = new Intent(SumMenuActivity.this, MessageTabActivityCMN.class);
                nextActivity.putExtra("Tabno", 0);
                nextActivity.putExtra("CallerTab", 0);
                nextActivity.putExtra("CallingActivity", getLocalClassName());
                nextActivity.setAction("com.for.view");
                startActivity(nextActivity);
                finish();
            }
        });
        TextView PatientText = (TextView) findViewById(R.id.menu_item1);
        TextView MsgText = (TextView) findViewById(R.id.menu_item2);
        TextView AnalyticsText = (TextView) findViewById(R.id.menu_item3);


        try {
            PatientText.setText(mTabDtl.get(0).getTabDispText());
            MsgText.setText(mTabDtl.get(1).getTabDispText());
            AnalyticsText.setText(mTabDtl.get(2).getTabDispText());
        } catch (Exception e) {
            e.getMessage();
        }
        RelativeLayout menuBtn2 = (RelativeLayout) findViewById(R.id.btn_lyt2);
        menuBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "callingActivity: " + getLocalClassName());
                Intent nextActivity = new Intent(SumMenuActivity.this, MessageTabActivityCMN.class);

                nextActivity.putExtra("Tabno", 1);
                nextActivity.putExtra("CallerTab", 1);
                nextActivity.putExtra("CallingActivity", getLocalClassName());
                nextActivity.setAction("com.for.view");
                startActivity(nextActivity);
                finish();
            }
        });


        RelativeLayout menuBtn3 = (RelativeLayout) findViewById(R.id.btn_lyt3);
        menuBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "callingActivity: " + getLocalClassName());
                Intent nextActivity = new Intent(SumMenuActivity.this, MessageTabActivityCMN.class);

                nextActivity.putExtra("Tabno", 2);
                nextActivity.putExtra("CallerTab", 2);
                nextActivity.putExtra("CallingActivity", getLocalClassName());
                nextActivity.setAction("com.for.view");

                //	myIntent.putExtra("isFromList", true);


                startActivity(nextActivity);
                finish();
            }
        });

        MyApplication.INSTANCE.mSumMenuActivity = this;
        loadMessageCount();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sum_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.action_scheduling);
        if (user_category.equalsIgnoreCase("provider")) {
            menuItem.setVisible(true);
        } else {
            menuItem.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!CMN_Preferences.getisUserEnabledFingerPrint(SumMenuActivity.this)) {
                return;
            }
            final FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(Context.FINGERPRINT_SERVICE);
            if (fingerprintManager.isHardwareDetected() && !CMN_Preferences.getisUserLoggedOut(SumMenuActivity.this)) {
                if (ifsettingOpened && fingerprintManager.hasEnrolledFingerprints()) {
                    CMN_Preferences.setisFingerPrintAuthEnabled(SumMenuActivity.this, true);
                    return;
                }
                if (alertDialogEnableTouchId != null) {
                    return;
                }
                if (CMN_Preferences.getisFingerPrintAuthEnabled(SumMenuActivity.this)) {
                    return;
                }
                alertDialogEnableTouchId = new AlertDialog.Builder(SumMenuActivity.this)
                        .setTitle("TouchId")
                        .setCancelable(false)
                        .setMessage("Your device supports fingerprint based Authentication, would you like to use it ?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @TargetApi(Build.VERSION_CODES.M)
                            public void onClick(DialogInterface dialog, int which) {
                                if (!fingerprintManager.hasEnrolledFingerprints()) {
                                    AlertDialog alertDialogNoFingerRegistered = new AlertDialog.Builder(SumMenuActivity.this)
                                            .setTitle("TouchId")
                                            .setCancelable(false)
                                            .setMessage("No fingerprint is registered, please register at least one fingerprint")
                                            .setNeutralButton("Go to Settings", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                                                }
                                            })

                                            .setIcon(R.drawable.ic_fp_40px)
                                            .show();
                                    alertDialogEnableTouchId = null;
                                    ifsettingOpened = true;
                                } else {
                                    CMN_Preferences.setisFingerPrintAuthEnabled(SumMenuActivity.this, true);

                                }
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                CMN_Preferences.setisFingerPrintAuthEnabled(SumMenuActivity.this, false);
                                CMN_Preferences.setisUserEnabledFingerPrint(SumMenuActivity.this, false);
                            }
                        })
                        .setIcon(R.drawable.ic_fp_40px)
                        .show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.INSTANCE.mSumMenuActivity = null;
        Log.d(TAG, " summenu activity destroyed");
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_home) {
            Utils.signout();
//            finish();
        } else if (id == R.id.action_scheduling) {
            Utils.showSchedulingPopup(SumMenuActivity.this);
        }

	/*	if (id == R.id.action_settings) {
            return true;
		} */
        return super.onOptionsItemSelected(item);
    }

    public void setRow0Image() {
        String img1 = null;
        img1 = jsonHelperClass.getFSRow0().mImageUrl;
        //    mContactmail = jsonHelperClass.getAppAttributes("MERITUS").get(1);
        Log.d(TAG, "Logo Image is " + img1);
        if (img1 != null && !TextUtils.isEmpty(img1)) {
            Uri uri = Uri.parse(img1);
            String url = uri.getLastPathSegment();
            img1 = url.toString()
                    .toLowerCase().replaceAll("\\s+", "")
                    .replaceAll("-", "")
                    .replaceAll("/", "")
                    .replaceAll("[(+^)_]*", "");
            url = null;
        }
        if (!TextUtils.isEmpty(img1) && img1 != null) {
            path = APP_PATH
                    + img1.toString().toLowerCase()
                    .replaceAll("\\s+", "").replaceAll("-", "").replaceAll("/", "")
                    .replaceAll("[(+^)_]*", "").replace(".jpg", ".png");

        }

        FileInputStream in;
        BufferedInputStream buf;
        try {
            Log.d(TAG, "path in file reading =" + path);

            in = new FileInputStream(path);
            buf = new BufferedInputStream(in);
            Bitmap bMap = BitmapFactory.decodeStream(buf);
            Log.d(TAG, "bMap" + bMap);
            Log.d(TAG, "bMap size" + bMap);
            mlogoview.setImageBitmap(bMap);
            if (in != null) {
                in.close();
            }
            if (buf != null) {
                buf.close();
            }
        } catch (Exception e) {
            Log.d("Error reading file", e.toString());

            Log.d(TAG, "path =" + path);
        }
    }

    public void signout() {

        AlertDialog.Builder signoutBuilder = new AlertDialog.Builder(
                this);
        signoutBuilder
                .setTitle("Signout")
                .setMessage("Do you want to signout ?")
                .setPositiveButton(android.R.string.yes,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                                Utils.signout();
                            }
                        }).setNegativeButton(android.R.string.no, null).show();

    }

    public void gotUnreadmessage(String count) {
        UnReadMessgeCount = count;
    }

    @Override
    public void buildUI(JSONObject jsonObject) {
        try {
            if (jsonObject != null && jsonObject.getJSONObject("Result").getInt("Code") == 0) {
                Log.d(TAG, " code - " + jsonObject.getJSONObject("Result").getInt("Code"));
                Log.d(TAG, " code - " + jsonObject.getString("Data"));
                UnReadMessgeCount = jsonObject.getString("Data");
                MsgcountText.setText(UnReadMessgeCount);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.getMessage();
        }

    }

    protected void loadMessageCount() {
        //  CMN_SERVER_BASE_URL_DEFINE = CMN_SERVER_TEST_URL_DEFINE;
        Log.d(TAG, "Load configuration called here");
//        String UsrName = "";
//        String Password = "";
//        String orgname = "";
        /*try {
            UsrName = URLEncoder.encode(mUserName, "UTF-8");
            Password = URLEncoder.encode(mPassword, "UTF-8");
            orgname = URLEncoder.encode(CMN_Preferences.getOrganizationName(SumMenuActivity.this), "UTF-8");
        } catch (UnsupportedEncodingException e1) {

            // TODO Auto-generated catch block
            e1.getMessage();

        }*/
        String url = String.format(CMN_SERVER_BASE_URL_DEFINE + "GetUnreadMessageCount.aspx?" + "token=%s", CMN_Preferences.getUserToken(SumMenuActivity.this));
//        String url = CMN_Preferences.getBaseUrl(SumMenuActivity.this) + URLEncoder.encode("GetUnreadMessageCount.aspx?username=" + mUserName + "&password="
//                + mPassword + "&organization=" + CMN_Preferences.getOrganizationName(SumMenuActivity.this));
        // String url = String.format(CMN_SERVER_BASE_URL_DEFINE + "getConfiguration.aspx?" + "username=%s&password=%s",UsrName,Password);
        //    String url = String.format("https://www.coremobile.us/ServerIntegration_Appstore/" + "getConfiguration.aspx?" + "username=%s&password=%s",UsrName,Password);
        if (Utils.checkInternetConnection()) {
            Log.d(TAG, "start json download");
            try {
                new GetJSON(this, this).execute(url);

            } catch (Exception e) {
                Log.v(TAG, "GetJSON failed to download crystal.json");
            }
        }
    }

    class downloadImages extends AsyncTask<String, Void, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressDialog.setTitle("downloading images");
            mProgressDialog.setMessage("Please Wait ..");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected Integer doInBackground(String... arg0) {
            while (MyApplication.INSTANCE.ImagedownloadCount >= 1) {
                //	Log.d(TAG,"Imagedownload in progress ="+MyApplication.INSTANCE.ImagedownloadCount);
            }
            return 1;

        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            setRow0Image();
            if (!SumMenuActivity.this.isFinishing() && mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }

        }
    }

    @Override
    public void onBackPressed() {
        // consume back pressed, either press home or signout to get out of the
        // app
    }


}
