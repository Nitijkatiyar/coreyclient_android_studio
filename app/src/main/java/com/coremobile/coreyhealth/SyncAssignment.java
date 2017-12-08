package com.coremobile.coreyhealth;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

public class SyncAssignment extends Activity implements IServerConnect, IPullDataFromServer
{
	 private static final String TAG = "Corey_SyncAssignment";
	 ProgressDialog mSyncDialog;
	
	 private IntentFilter          mPushNotificationFilter;
	 @Override
	    protected void onCreate(Bundle savedInstanceState) 
		 {
		 super.onCreate(savedInstanceState);

		 mSyncDialog= new ProgressDialog(SyncAssignment.this);
		// new SyncProgressAysncTask().execute();
		 mSyncDialog.setTitle("Syncing");
         mSyncDialog.setMessage("Please wait");
         mSyncDialog.setCancelable(false);
         mSyncDialog.show();
         MyApplication application = (MyApplication) getApplication();
         mPushNotificationFilter = new IntentFilter();
         mPushNotificationFilter.addAction(application.AppConstants.getReceivedPushIntent());
     //    .addAction("com.coremobile.coreyhealth.receivedpush");

         registerReceiver(mPushNotificationReceiver, mPushNotificationFilter );

		 }
	 @Override
	    public void onBackPressed() 
	 	 {
	        // TODO Auto-generated method stub
	        super.onBackPressed();
	        finish();
	    }
	 
	 
	 class SyncProgressAysncTask extends AsyncTask<Void, Void, Void> {

	        @Override
	        protected void onPreExecute() {
	            // TODO Auto-generated method stub
	            super.onPreExecute();
	            mSyncDialog.setTitle("Syncing");
	            mSyncDialog.setMessage("Please wait");
	            mSyncDialog.setCancelable(false);
	            mSyncDialog.show();
	        }

	        @Override
	        protected Void doInBackground(Void... params) {

	    //        performSync();

	            return null;
	        }

	        @Override
	        protected void onPostExecute(Void result) {
	            // TODO Auto-generated method stub
	            super.onPostExecute(result);
	            // TODO Auto-generated method stub

	        }

	    }
	 BroadcastReceiver mPushNotificationReceiver     = new BroadcastReceiver() {

         @Override
         public void onReceive(
                 Context arg0,
                 Intent intent) 
         {
             // TODO
             // Auto-generated
             // method stub
         	Log.d(TAG,"Pushnotificatin receiver");
             if (intent != null
                     && intent
                             .getExtras()
                             .getString(
                                     "Message")
                             .contains(
                                     "Sync completed")) 
             {            	 
            		 pullData();
            		 
            		 Log.d(TAG,"Pull called in onreceive");
             }
         }
                
     };
     public void pullData()
     {
        
  //       prevLastMessageId = getLastMessageid();
    	 if (Utils.checkInternetConnection()) 
         {
//         MyApplication application = (MyApplication) getApplication();
//         application.pullData(this);
         Log.d(TAG,"Pulldata called");
         } else 
         {
             Toast.makeText(
            		 getApplicationContext(),
                     "Operation cannot be performed now.Check if your cellular data/wifi is turned ON.",
                     Toast.LENGTH_SHORT).show();

         } 
     }
     @Override
     public void showDialog()
     {
         // TODO Auto-generated method stub

     }
     public void gotUserInfoFromServer(JSONObject json)
     {
         // TODO Auto-generated method stub

     }
     @Override
     public void closeDialog()
     {
         // TODO Auto-generated method stub

     }

     @Override
     public void throwToast(String stringToToast)
     {
         // TODO Auto-generated method stub

     }

     @Override
     public void finishedParsing(String _status) {
         // TODO Auto-generated method stub
         
    	 if (mSyncDialog != null
                 && mSyncDialog
                         .isShowing()) {
    		 mSyncDialog
                     .dismiss();
         }
    	 Log.d(TAG,"finished parsing called and dialogce dismissed");
    	 unregisterReceiver(mPushNotificationReceiver);
    	 finish();
     }

}

