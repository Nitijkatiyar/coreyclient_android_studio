package com.coremobile.coreyhealth;


import android.app.ActionBar;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.coremobile.coreyhealth.base.CMN_AppBaseActivity;

public class AboutActivityCMN extends CMN_AppBaseActivity
{
	String mContactmail;
	//GetTriggerInProgress
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aboutlayout);
        JSONHelperClass jsonHelperClass;
        jsonHelperClass = new JSONHelperClass();
        
        //Testing My First Commit. 
      
     //   mContactmail = jsonHelperClass.getAppAttributes("PATIENT").get(1);
        mContactmail= "support@coremobileinc.com";
        ((TextView)findViewById(R.id.textView2)).setText(
            String.format(
            		MyApplication.INSTANCE.AppConstants.getAppVersionInfo(),
         //      getResources().getString(R.string.app_version_info),
                getResources().getString(R.string.app_version))
        );
        ((TextView)findViewById(R.id.textView1)).setText(MyApplication.INSTANCE.AppConstants.getAppAboutTitle());
        
       ((TextView)findViewById(R.id.textView3)).setText(                
               		MyApplication.INSTANCE.AppConstants.getAboutText1());
      /*  ((TextView)findViewById(R.id.textView5)).setText(               
                		MyApplication.INSTANCE.AppConstants.getAboutText2()); */
        
        Button contactus = (Button) findViewById(R.id.feedbackButton);
        contactus.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Utils.performSendEmail(AboutActivityCMN.this,mContactmail);
            }
        });
        
        ActionBar actionBar = getActionBar();
		if (actionBar != null) 
		{
	
		    actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME
			    | ActionBar.DISPLAY_SHOW_TITLE
			    | ActionBar.DISPLAY_HOME_AS_UP);
		    actionBar.show();
		}

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	// TODO Auto-generated method stub
	switch (item.getItemId()) {
	case android.R.id.home:
	    finish();
	    return true;
	default:
	    return super.onOptionsItemSelected(item);
	}
    }

}
