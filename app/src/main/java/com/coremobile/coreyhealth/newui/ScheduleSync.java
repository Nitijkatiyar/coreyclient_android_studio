package com.coremobile.coreyhealth.newui;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.View;

import com.coremobile.coreyhealth.ContextData;
import com.coremobile.coreyhealth.IDialogListener;
import com.coremobile.coreyhealth.MyApplication;
import com.coremobile.coreyhealth.R;
import com.coremobile.coreyhealth.SyncTriggerTask;
import com.coremobile.coreyhealth.Utils;

public class ScheduleSync extends BaseContextAction implements IDialogListener {
    private static final String LISTENER_ID = "ScheduleSync";
    public  static final String SYNC_API = "TriggerInstantGratification.aspx";

    public ScheduleSync(Activity activity, ContextData coreyCxt) {
        super(activity, coreyCxt);
        MyApplication.INSTANCE.putDialogListener(LISTENER_ID, this);
    }

    @Override
    public void doPrepareDialog(int dialogId, View body) {
	// nothing to do
    }

    @Override
    public void doPositveClick(DialogInterface dialog, int dialogId, View body) {
        SyncTriggerTask triggerTask =
        	new SyncTriggerTask(mActivity, mCoreyCxt, mCoreyCxt.mDisplayText + " sync", SYNC_API,
                    SyncTriggerTask.TRIGGER_RESULT_JSON1);
        triggerTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void doNegativeClick(DialogInterface dialog, int dialogId, View body) {
        // noting to do
    }

    @Override
    public void onClick(View v) {
        Utils.showAlertDialog(mActivity, R.string.sc_dlg_title, R.string.sc_dlg_msg, LISTENER_ID, 0);
    }
}
