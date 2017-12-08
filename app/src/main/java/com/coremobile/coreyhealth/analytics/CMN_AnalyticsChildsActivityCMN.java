package com.coremobile.coreyhealth.analytics;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.coremobile.coreyhealth.R;
import com.coremobile.coreyhealth.base.CMN_AppBaseActivity;
import com.coremobile.coreyhealth.errorhandling.ActivityPackage;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Nitij Katiyar
 */
public class CMN_AnalyticsChildsActivityCMN extends CMN_AppBaseActivity {
    ViewGroup root;
    String TAG = "Corey_CMN_AnalyticsFragment";
    GridView gridView;
    ArrayList<CMN_AnalyticGraphModel> childs;
    CMN_AnalyticsAdapter adapter;
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_analytics);


        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setTitle(
                "" + getResources().getString(R.string.analytics));
        getActionBar().setDisplayHomeAsUpEnabled(true);

        intent = getIntent();
        gridView = (GridView) findViewById(R.id.gridView);
        if (intent != null && intent.hasExtra("data")) {
            childs = (ArrayList<CMN_AnalyticGraphModel>) intent.getSerializableExtra("data");
        }


        adapter = new CMN_AnalyticsAdapter(CMN_AnalyticsChildsActivityCMN.this, childs);
        gridView.setAdapter(adapter);


        gridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                CMN_AnalyticGraphModel model = (CMN_AnalyticGraphModel) arg0
                        .getAdapter().getItem(arg2);
                if (model.getOpenUrl() != null && !model.getOpenUrl().isEmpty()) {
                    //Log.d(TAG, "webview activity" + model.getOpenUrl());
                    //Log.d(TAG, "webview activity" + model.getDisplayText());
                    Intent urlIntent = new Intent();
                    urlIntent.setClassName(getPackageName(), ActivityPackage.WebViewActivity);
                    urlIntent.putExtra("myurl", model.getOpenUrl());
                    urlIntent.putExtra("objname", "Analytics");
                    startActivity(urlIntent);
                } else if (model.getGraphType() != null && !model.getGraphType().isEmpty()) {
                    Intent intent = new Intent();
                    intent.setClassName(getPackageName(), ActivityPackage.AnalyticsGraphActivity);
                    //Log.d(TAG, "graphtype selected");
                    intent.putExtra("data", (Serializable) model);
                    startActivity(intent);
                } else {
                    if (model.getGraphID() != null) {
                        CMN_GetGraphDataWebService dataWebService = new CMN_GetGraphDataWebService(CMN_AnalyticsChildsActivityCMN.this);
                        dataWebService.execute( "" + model.getGraphID(),model.getDisplayText());
                    }
                }


            }
        });

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
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
