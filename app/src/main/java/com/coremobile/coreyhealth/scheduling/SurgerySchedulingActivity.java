package com.coremobile.coreyhealth.scheduling;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.coremobile.coreyhealth.BaseActivityCMN;
import com.coremobile.coreyhealth.R;
import com.coremobile.coreyhealth.Utils;
import com.coremobile.coreyhealth.scheduling.schedulingcalender.CalenderActivity;

public class SurgerySchedulingActivity extends BaseActivityCMN {

    Button _viewSchedule, _requestSchedule, _requestDateSchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surgery_scheduling);


        _viewSchedule = (Button) findViewById(R.id.viewSchedule);
        _requestSchedule = (Button) findViewById(R.id.requestSchedule);
        _requestDateSchedule = (Button) findViewById(R.id.requestDateSchedule);

        _viewSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.viewSchedule = true;
                Utils.dateSchedule = false;
                startActivity(new Intent(SurgerySchedulingActivity.this, CalenderActivity.class));
            }
        });
        _requestSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.viewSchedule = false;
                Utils.dateSchedule = false;
                if (Utils.fromIntake) {
                    Intent intent = new Intent(SurgerySchedulingActivity.this, CalenderActivity.class);
                    startActivity(intent);
                } else {
                    startActivity(new Intent(SurgerySchedulingActivity.this, RequestScheduleActivity.class));
                }
            }
        });
        _requestDateSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.viewSchedule = false;
                Utils.dateSchedule = true;
                if (Utils.fromIntake) {
                    Intent intent = new Intent(SurgerySchedulingActivity.this, CalenderActivity.class);
                    startActivity(intent);
                } else {
                    startActivity(new Intent(SurgerySchedulingActivity.this, RequestScheduleActivity.class));
                }
            }
        });

        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setTitle("Surgery Scheduling");
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
