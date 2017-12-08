package com.coremobile.coreyhealth.scheduling.schedulingcalender;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.coremobile.coreyhealth.BaseActivityCMN;
import com.coremobile.coreyhealth.R;
import com.coremobile.coreyhealth.errorhandling.ActivityPackage;


public class CalenderActivity extends BaseActivityCMN {

    public static String speciality = "-1";
    public static String modality = "-1";
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);

        ActivityPackage.setActivityList(this);

        intent = getIntent();
         if(intent.hasExtra("speciality")){
            speciality = intent.getStringExtra("speciality");
        }if(intent.hasExtra("modality")){
            modality = intent.getStringExtra("modality");
        }
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.main_container, new MaterialCalendarFragment()).commit();
        }

        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setTitle("View Schedule");
        getActionBar().setDisplayHomeAsUpEnabled(true);

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
