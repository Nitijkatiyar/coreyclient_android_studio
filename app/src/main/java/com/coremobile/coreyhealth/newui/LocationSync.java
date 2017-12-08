package com.coremobile.coreyhealth.newui;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.View;

import com.coremobile.coreyhealth.ContextData;
import com.coremobile.coreyhealth.IDialogListener;
import com.coremobile.coreyhealth.LocalPrefs;
import com.coremobile.coreyhealth.LocationSyncTriggerTask;
import com.coremobile.coreyhealth.MyApplication;
import com.coremobile.coreyhealth.R;
import com.coremobile.coreyhealth.SyncTriggerTask;
import com.coremobile.coreyhealth.Utils;

public class LocationSync extends BaseContextAction implements IDialogListener {
    private static final String LISTENER_ID = "LocationSync";
    public  static final String SYNC_API = "updatelocation.aspx";

    public LocationSync(Activity activity, ContextData coreyCxt) {
        super(activity, coreyCxt);
        MyApplication.INSTANCE.putDialogListener(LISTENER_ID, this);
    }

    @Override
    public void doPrepareDialog(int dialogId, View body) {
	// nothing to do
    }

    @Override
    public void doPositveClick(DialogInterface dialog, int dialogId, View body) {
		LocalPrefs.INSTANCE.setLocationSync(true);
        performLocationSync();
    }

    @Override
    public void doNegativeClick(DialogInterface dialog, int dialogId, View body) {
        // noting to do
    }

    @Override
    public void onClick(View v) {
        if (LocalPrefs.INSTANCE.locationSync()) {
            performLocationSync();
        } else {
            Utils.showAlertDialog(mActivity, R.string.ls_dlg_title, R.string.ls_dlg_msg, LISTENER_ID, 0);
        }
    }

    private void performLocationSync() {
        SyncTriggerTask triggerTask =
        	new LocationSyncTriggerTask(mActivity, mCoreyCxt, mCoreyCxt.mDisplayText + " sync", SYNC_API,
                    SyncTriggerTask.TRIGGER_RESULT_NONE);
        triggerTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}

