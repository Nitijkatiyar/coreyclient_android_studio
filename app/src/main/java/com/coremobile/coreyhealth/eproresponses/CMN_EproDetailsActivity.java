package com.coremobile.coreyhealth.eproresponses;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import com.coremobile.coreyhealth.R;

import org.json.JSONArray;
import org.json.JSONObject;

public class CMN_EproDetailsActivity extends Activity {

    ListView listVw;
    String question;
    JSONArray jsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cmn__epro_details);

        String eproStr = getIntent().getExtras().getString("epro");

        listVw = (ListView) findViewById(R.id.listVw);

        jsonArray = parseEproDetails(eproStr);


        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setTitle("" + question);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        EproCommentRowViewAdapter eproCommentRowViewAdapter = new EproCommentRowViewAdapter(getApplicationContext(), jsonArray);
        listVw.setAdapter(eproCommentRowViewAdapter);

    }

    JSONArray parseEproDetails(String eproStr) {

        try {
            JSONObject jsonObject = new JSONObject(eproStr);
            question = jsonObject.getString("Question");

            JSONArray jsonArray = jsonObject.getJSONArray("ResponseDetails");

            return jsonArray;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
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
