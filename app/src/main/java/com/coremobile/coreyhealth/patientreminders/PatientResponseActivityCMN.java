package com.coremobile.coreyhealth.patientreminders;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.coremobile.coreyhealth.AppConfig;
import com.coremobile.coreyhealth.R;
import com.coremobile.coreyhealth.Utils;
import com.coremobile.coreyhealth.base.CMN_AppBaseActivity;
import com.coremobile.coreyhealth.googleAnalytics.CMN_Preferences;
import com.coremobile.coreyhealth.newui.MessageTabActivityCMN;
import com.coremobile.coreyhealth.partialsync.GetAllContextWebService;

/**
 * @author Nitij Katiyar
 */
public class PatientResponseActivityCMN extends CMN_AppBaseActivity {

    private SharedPreferences mCurrentUserPref;
    public static final String CURRENT_USER = "CurrentUser";
    private String organizationName;
    String user_category, context_name;

    Intent intent;
    ListView listView;
    ReminderModel reminderModel;
    String typeId;
    TextView noMessages;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patientreminderresponse);

        noMessages = (TextView) findViewById(R.id.noMessages);

        mCurrentUserPref = getSharedPreferences(CURRENT_USER, 0);
        organizationName = mCurrentUserPref.getString("Organization", null);
        user_category = mCurrentUserPref.getString("user_category", null);

        if (AppConfig.isAppCoreyHealth) {
            context_name = GetAllContextWebService.patientsContexts.get(MessageTabActivityCMN.patientPosition).ContextId;
        } else {
            context_name = CMN_Preferences.getUserToken(PatientResponseActivityCMN.this);
        }

        intent = getIntent();
        if (intent.hasExtra("dataValue")) {
            reminderModel = (ReminderModel) intent.getSerializableExtra("dataValue");
            typeId = intent.getStringExtra("typeId");
        }
        listView = (ListView) findViewById(R.id.listview);

        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setTitle(
                "" + getResources().getString(R.string.response));
        getActionBar().setDisplayHomeAsUpEnabled(true);

        if (Utils.checkInternetConnection()) {
            GetPatientResponseWebService allMessages = new GetPatientResponseWebService(
                    PatientResponseActivityCMN.this, listView, noMessages);
            allMessages.execute(context_name, typeId, "" + reminderModel.getId());
        } else {
        }


    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        this.invalidateOptionsMenu();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.responsefilter, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Utils.checkInternetConnection()) {
            GetPatientResponseWebService allMessages = new GetPatientResponseWebService(
                    PatientResponseActivityCMN.this, listView, noMessages);
            allMessages.execute(context_name, typeId, "" + reminderModel.getId());
        } else {
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;

            case R.id.action_datesort:
//                Toast.makeText(PatientResponseActivityCMN.this, "Under Development", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_responsesort:
//                Toast.makeText(PatientResponseActivityCMN.this, "Under Development", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

}
