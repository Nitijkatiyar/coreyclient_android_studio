package com.coremobile.coreyhealth.scheduling;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.coremobile.coreyhealth.BaseActivityCMN;
import com.coremobile.coreyhealth.R;
import com.coremobile.coreyhealth.errorhandling.ActivityPackage;

public class PatientLookupActivity extends BaseActivityCMN {

    RelativeLayout _byPatientName, _byExistingPatient, _bySurgeon_PatientName, _byDateofProcedure, _byAnonymizedId, _byMobileNumber;
    boolean isSearchPatient = false;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_lookup);

        ActivityPackage.setActivityList(this);

        intent = getIntent();
        if(getIntent() != null && getIntent().hasExtra("isSearchPatient")){
            isSearchPatient = getIntent().getBooleanExtra("isSearchPatient",false);
        }

        _byPatientName = (RelativeLayout) findViewById(R.id.lookupbypatientname);
        _bySurgeon_PatientName = (RelativeLayout) findViewById(R.id.lookupbysurgeonandpatient);
        _byAnonymizedId = (RelativeLayout) findViewById(R.id.lookupbyanonymizedid);
        _byDateofProcedure = (RelativeLayout) findViewById(R.id.lookupbydateofprocedure);
        _byExistingPatient = (RelativeLayout) findViewById(R.id.lookupbyexistingpatient);
        _byMobileNumber = (RelativeLayout) findViewById(R.id.lookupbymobilenumber);

        _byPatientName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PatientLookupActivity.this, LookupByPatientName.class).putExtra("isSearchPatient",isSearchPatient));
            }
        });
        _bySurgeon_PatientName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PatientLookupActivity.this, LookupBySurgeonandPatient.class).putExtra("isSearchPatient",isSearchPatient));
            }
        });
        _byAnonymizedId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PatientLookupActivity.this, LookupByAnonymizedPatientId.class).putExtra("isSearchPatient",isSearchPatient));
            }
        });
        _byDateofProcedure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PatientLookupActivity.this, LookupByDateofProcedure.class).putExtra("isSearchPatient",isSearchPatient));
            }
        });
        _byMobileNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PatientLookupActivity.this, LookupByMobilePhoneNumber.class).putExtra("isSearchPatient",isSearchPatient));
            }
        });
        _byExistingPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PatientLookupActivity.this, LookupByExistingPatient.class).putExtra("isSearchPatient",isSearchPatient));
            }
        });

        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setTitle("Select an Option");
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
