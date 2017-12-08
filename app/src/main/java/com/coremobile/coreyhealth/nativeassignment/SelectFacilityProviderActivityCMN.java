package com.coremobile.coreyhealth.nativeassignment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.coremobile.coreyhealth.MyApplication;
import com.coremobile.coreyhealth.R;
import com.coremobile.coreyhealth.base.CMN_AppBaseActivity;

import java.util.ArrayList;

public class SelectFacilityProviderActivityCMN extends CMN_AppBaseActivity {
    private static String TAG = "Corey_AssignmentsActivityCMN";
    MyApplication application;

    private SharedPreferences mCurrentUserPref;
    public static final String CURRENT_USER = "CurrentUser";
    private String organizationName, user_category;

    ListView assignmentListView;
    LinearLayout progressBar;
    ArrayList<FacilityAssignmentProviderModel> assignmentproviderModels;
    AllProvidersAdapter allProviderAdapter;
    Intent intent;
    ArrayList<AllProvidersModel> providersModels;
    int position = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignments);


        assignmentListView = (ListView) findViewById(R.id.assignmetnsListView);
        findViewById(R.id.row0).setVisibility(View.GONE);

        findViewById(R.id.progressbar).setVisibility(View.GONE);

        intent = getIntent();

        if (intent != null) {
            if (intent.hasExtra("data")) {
                providersModels = (ArrayList<AllProvidersModel>) intent.getSerializableExtra("data");
                position = intent.getIntExtra("position", -1);
            }
        }

        mCurrentUserPref = getSharedPreferences(CURRENT_USER, 0);
        organizationName = mCurrentUserPref.getString("Organization", null);

        progressBar = (LinearLayout) findViewById(R.id.progressbar);

        application = (MyApplication) getApplication();
        user_category = mCurrentUserPref.getString("user_category", null);

        getActionBar().setHomeButtonEnabled(true);
        if (getIntent() != null && getIntent().hasExtra("title")) {
            getActionBar().setTitle(
                    "" + getIntent().getStringExtra("title"));
        } else {
            getActionBar().setTitle(
                    "Select Provider");
        }


        allProviderAdapter = new

                AllProvidersAdapter(SelectFacilityProviderActivityCMN.this, providersModels);

        assignmentListView.setAdapter(allProviderAdapter);


        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_save);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.savesurveydata, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.action_save:
                AssignmentForFacilityAdapter.selectedProviders.put(position, allProviderAdapter.getData());
                finish();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }

    }

}



