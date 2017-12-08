package com.coremobile.coreyhealth;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.MailTo;
import android.net.Uri;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

public class WebViewActivityCMN extends AppCompatActivity {

    WebView webView;
    private ProgressDialog mProgressDialog;
    private static String TAG = "Corey_WebviewActivity";
    DownloadManager manager;
    private MyApplication application;
    File destinationDir;
    String mMimetype;
    String html5page = "";
    String mAppTitle;
    Intent mIntent;
    private String CMN_SERVER_BASE_URL_DEFINE;
    private final static String CMN_SERVER_ADDPROVIDER_API = "AddUser.aspx?";
    SharedPreferences mcurrentUserPref;
    public static final String CURRENT_USER = "CurrentUser";
    public static final String GOOGLE_DOCS = "http://docs.google.com/viewer?url=";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // getWindow().requestFeature(Window.FEATURE_PROGRESS);

        setContentView(R.layout.activity_web_view);
        application = (MyApplication) getApplication();
        mIntent = getIntent();
        mAppTitle = mIntent.getStringExtra("objname");
        String mver = mIntent.getStringExtra("verno");

        mcurrentUserPref = getSharedPreferences(CURRENT_USER, 0);
        String Orgname = mcurrentUserPref.getString("Organization", "");

        ActionBar actionBar = getSupportActionBar();
/*	if (mAppTitle==null ||mAppTitle.equalsIgnoreCase(""))
    {
		mAppTitle=mcurrentUserPref.getString("DisplayName", null);
	} */
        if (mAppTitle == null || mAppTitle.equalsIgnoreCase("")) {
            mAppTitle = "Corey";
        }
        if (mver != null) mAppTitle = mAppTitle + mver;
        Log.d(TAG, "mAppTitle=" + mAppTitle);
    /*
    if(application.AppConstants.getAppName().equals("PERIOP"))
	{
		mAppTitle= "Periop-On-the-Go";	
	}
	else mAppTitle= "Corey"; */
        if (actionBar != null) {
            actionBar.setTitle(mAppTitle);
            //    actionBar.setIcon(R.drawable.app_icon);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME
                    | ActionBar.DISPLAY_SHOW_TITLE
                    | ActionBar.DISPLAY_HOME_AS_UP);
            actionBar.show();

        }
/*	mcurrentUserPref = getSharedPreferences(CURRENT_USER, 0);
    String Orgname=mcurrentUserPref.getString("Organization",""); */
        JSONHelperClass jsonHelperClass = new JSONHelperClass();
        CMN_SERVER_BASE_URL_DEFINE = jsonHelperClass.getBaseURL(Orgname);
        String myurl = getIntent().getExtras().getString("myurl");
        if (AppConfig.isAppCoreyPatient && myurl.contains("i_button_corey_health")) {
            myurl = myurl.replace("i_button_corey_health", "i_button_corey_patient");
        }
        if (getIntent().hasExtra("functionality")) {
            html5page = getIntent().getExtras().getString("functionality");
        }
        webView = (WebView) findViewById(R.id.webView1);

        manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        destinationDir = new File(getApplicationContext().getFilesDir() + "Corey");

        webView.setInitialScale(1);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(false);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        // mIFloatCall.getSettings().setUseWideViewPort(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.setInitialScale(160);
//        webView.addJavascriptInterface(this, "webservice");

        if (!destinationDir.exists()) {
            destinationDir.mkdir(); // Don't forget to make the directory if
            // it's not there
        }

        if (myurl.endsWith("csv") /* || myurl.endsWith("xml") */
                || myurl.endsWith("docx") || myurl.endsWith("ppt")
                || myurl.endsWith("pptx") || myurl.endsWith("txt")
                || myurl.endsWith("xls")) {
            // declare the dialog as a member field of your activity
            // instantiate it within the onCreate method
            mProgressDialog = new ProgressDialog(WebViewActivityCMN.this);
            mProgressDialog.setMessage("Downloading");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(false);


        }

/*	else if(html5page!= null)
    {
		if(html5page.equals("Add Provider") ) 
		{
			myurl="file:///android_asset/addprovider.html";
			OpenWebSiteInWebView opensite = new OpenWebSiteInWebView();
		    opensite.execute(myurl);
		}
		else
		{
			   OpenWebSiteInWebView opensite = new OpenWebSiteInWebView();
			    opensite.execute(myurl);			
		}
    } */

        else {
            if (myurl.endsWith("pdf")) {
                myurl = GOOGLE_DOCS + myurl;
            }
            webView.setWebViewClient(new MyWebViewClient());
            webView.loadUrl(myurl);
/*		OpenWebSiteInWebView opensite = new OpenWebSiteInWebView();
        opensite.execute(myurl); */

        }
    }

    private class OpenWebSiteInWebView extends AsyncTask<String, Void, String> {

        @SuppressLint("SetJavaScriptEnabled")
        @Override
        protected String doInBackground(final String... params) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    webView.setWebViewClient(new MyWebViewClient());
                    webView.loadUrl(params[0]);
                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }

        @Override
        protected void onPreExecute() {

            mProgressDialog = new ProgressDialog(WebViewActivityCMN.this);
            mProgressDialog.setMessage("Please wait loading...");
            mProgressDialog.show();

        }

    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
            handler.proceed();
            final AlertDialog.Builder builder = new AlertDialog.Builder(WebViewActivityCMN.this);
            builder.setMessage("The Webpage you are requesting is secure, would you like to proceed?");
            builder.setPositiveButton("continue", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handler.proceed();
                }
            });
            builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handler.cancel();
                }
            });
            final AlertDialog dialog = builder.create();
            dialog.show();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String myurl) {
            if (myurl.startsWith("mailto:")) {
                MailTo mt = MailTo.parse(myurl);
                Intent i = newEmailIntent(WebViewActivityCMN.this, mt.getTo(), mt.getSubject(), mt.getBody(), mt.getCc());
                startActivity(i);
                view.reload();
                return true;
            } else if (myurl.endsWith("txt")) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(myurl));
                startActivity(i);
            } else {
                view.loadUrl(myurl);
            }

            return true;
        }

        public Intent newEmailIntent(Context context, String address, String subject, String body, String cc) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{address});
            intent.putExtra(Intent.EXTRA_TEXT, body);
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            intent.putExtra(Intent.EXTRA_CC, cc);
            intent.setType("message/rfc822");
            return intent;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
     /*   if (mProgressDialog.isShowing()) {
        mProgressDialog.dismiss();
	    } */
            //

        }
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

    @Override
    public void onPause() {
        super.onPause();

        try {
            Class.forName("android.webkit.WebView")
                    .getMethod("onPause", (Class[]) null)
                    .invoke(webView, (Object[]) null);

        } catch (ClassNotFoundException cnfe) {
            Log.d(TAG, "Classnotfound exception");
        } catch (NoSuchMethodException nsme) {
            Log.d(TAG, "NoSuchMethodException");
        } catch (InvocationTargetException ite) {
            Log.d(TAG, "InvocationTargetException");
        } catch (IllegalAccessException iae) {
            Log.d(TAG, "IllegalAccessException");
        }
    }
}