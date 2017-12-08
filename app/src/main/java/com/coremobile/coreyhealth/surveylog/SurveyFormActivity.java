package com.coremobile.coreyhealth.surveylog;

import android.os.Bundle;
import android.app.Activity;
import android.view.MenuItem;
import android.webkit.WebView;

import com.coremobile.coreyhealth.R;

public class SurveyFormActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_form);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setTitle(
                "" + getResources().getString(R.string.title_view_survey_log));
        getActionBar().setDisplayHomeAsUpEnabled(true);


        WebView webView = (WebView) findViewById(R.id.webView);

        if(getIntent().hasExtra("url")){
            webView.getSettings().setJavaScriptEnabled(true);
            webView.loadUrl(getIntent().getExtras().getString("url"));

            webView.setHorizontalScrollBarEnabled(false);

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
