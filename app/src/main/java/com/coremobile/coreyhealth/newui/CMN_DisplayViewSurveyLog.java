package com.coremobile.coreyhealth.newui;

import android.os.Bundle;
import android.app.Activity;
import android.widget.ListView;

import com.coremobile.coreyhealth.R;

public class CMN_DisplayViewSurveyLog extends Activity {

    ListView listVw;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cmn__display_view_survey_log);
        listVw=(ListView)findViewById(R.id.listVw);


    }

}
