package com.coremobile.coreyhealth.patientreminders;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import com.coremobile.coreyhealth.R;
import com.coremobile.coreyhealth.Utils;
import com.coremobile.coreyhealth.base.CMN_AppBaseActivity;

/**
 * @author Nitij Katiyar
 *
 */
public class RemindersTypeActivityCMN extends CMN_AppBaseActivity {

    private SharedPreferences mCurrentUserPref;
    public static final String CURRENT_USER = "CurrentUser";
    private String organizationName;
    String  context, user_category, context_name;

    ListView listView;
    String toLoad;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remindertypes);

        mCurrentUserPref = getSharedPreferences(CURRENT_USER, 0);
        organizationName = mCurrentUserPref.getString("Organization", null);
         user_category = mCurrentUserPref.getString("user_category", null);
        context = mCurrentUserPref.getString("context", null);
        context_name = mCurrentUserPref.getString("context_name", null);
        if(getIntent() !=null && getIntent().hasExtra("toLoad")){
            toLoad = getIntent().getStringExtra(toLoad);
        }

        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setTitle(
                "" + getResources().getString(R.string.reminders));
        getActionBar().setDisplayHomeAsUpEnabled(true);

        listView = (ListView) findViewById(R.id.listView);

        if (Utils.checkInternetConnection()) {
            GetRemindersTypesWebService allMessages = new GetRemindersTypesWebService(
                    RemindersTypeActivityCMN.this, listView,toLoad);
            allMessages.execute();
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
