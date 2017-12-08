package com.coremobile.coreyhealth.newui;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.coremobile.coreyhealth.ContextData;
import com.coremobile.coreyhealth.IDialogListener;
import com.coremobile.coreyhealth.MyApplication;
import com.coremobile.coreyhealth.R;
import com.coremobile.coreyhealth.SyncTriggerTask;
import com.coremobile.coreyhealth.Utils;

import org.json.JSONArray;

public class CoreyfySync extends BaseContextAction implements IDialogListener {
    private static final String LISTENER_ID = "CoreyfySync";
    public static final String SYNC_API = "TriggerCorefy.aspx";

    public CoreyfySync(Activity activity, ContextData coreyCxt) {
        super(activity, coreyCxt);
        MyApplication.INSTANCE.putDialogListener(LISTENER_ID, this);
    }

    @Override
    public int getIconResourceId() {
        return R.drawable.nav_coreyfy_icon;
    }

    @Override
    public void doPositveClick(DialogInterface dialog, int dialogId, View body) {
        System.out.println("CoreyfySync - doPositveClick " + body);
        EditText textView = (EditText) body.findViewById(R.id.contextedittext);
        String contextStr = textView.getText().toString().trim();
        System.out.println("CoreyfySync - doPositveClick contextStr " + contextStr);

        // hide softkeypad
        InputMethodManager inputManager =
                (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(textView.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

        SyncTriggerTask triggerTask =
                new SyncTriggerTask(mActivity, mCoreyCxt, mCoreyCxt.mDisplayText,
                        SYNC_API, SyncTriggerTask.TRIGGER_RESULT_JSON0);
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(contextStr);
        triggerTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "Context", jsonArray.toString());

    }

    @Override
    public void doPrepareDialog(int dialogId, View body) {
        // nothing to do
    }

    @Override
    public void doNegativeClick(DialogInterface dialog, int dialogId, View body) {
        // noting to do
    }

    @Override
    public void onClick(View v) {
        Utils.showAlertDialog(mActivity, R.string.sc_dlg_title, LISTENER_ID, 0, R.layout.prompt);
    }
}
