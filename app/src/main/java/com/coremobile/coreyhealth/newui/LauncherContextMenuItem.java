package com.coremobile.coreyhealth.newui;

import android.app.Activity;
import android.content.Intent;

import com.coremobile.coreyhealth.ContextData;

public class LauncherContextMenuItem extends AbstractContextMenuItem {
    Intent mIntentToLaunch;
    public LauncherContextMenuItem(Activity activity, ContextData coreyCxt, Intent action) {
        super(activity, coreyCxt);
        mIntentToLaunch = action;
    }

    public void onClick() {
        mActivity.startActivity(mIntentToLaunch);
    }
}
