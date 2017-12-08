package com.coremobile.coreyhealth.journal;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.coremobile.coreyhealth.R;

public class JournalDisplayActivity extends Activity {

    TextView dateTextVw, journalTextVw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_display);
        dateTextVw = (TextView) findViewById(R.id.dateTextVw);

        journalTextVw = (TextView) findViewById(R.id.journaltextVw);

        if (getIntent().hasExtra("journal"))
            journalTextVw.setText(getIntent().getExtras().getString("journal"));
        if (getIntent().hasExtra("date"))
            dateTextVw.setText(getIntent().getExtras().getString("date"));

        getActionBar().setTitle(
                "" + getResources().getString(R.string.journal_details_text));

    }

}
