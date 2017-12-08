package com.coremobile.coreyhealth;

import android.os.Bundle;
import android.view.Menu;

import com.coremobile.coreyhealth.base.CMN_AppBaseActivity;

public class PhoneCallActivityCMN extends CMN_AppBaseActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phonecall);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.phone_call, menu);
        return true;
    }

}
