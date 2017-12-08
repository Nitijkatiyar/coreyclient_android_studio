package com.coremobile.coreyhealth.googleFit;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import com.coremobile.coreyhealth.R;
import com.coremobile.coreyhealth.base.CMN_AppBaseActivity;


public class CMN_StepsCountActivity extends CMN_AppBaseActivity {

    ListView stepslistview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wearabledata_stepsactivity);


        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setTitle(
                "" + getResources().getString(R.string.wearablesdata_stepscount));
        getActionBar().setDisplayHomeAsUpEnabled(true);

        stepslistview = (ListView) findViewById(R.id.stepslistview);
        CMN_StepsCountAdapter stepsCountAdapter = new CMN_StepsCountAdapter(CMN_StepsCountActivity.this, CMN_WearableDataActivity.stepsModels);
        stepslistview.setAdapter(stepsCountAdapter);
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
