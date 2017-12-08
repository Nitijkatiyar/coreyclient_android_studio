package com.coremobile.coreyhealth.newui;

import android.app.Activity;

import com.coremobile.coreyhealth.ContextData;

public class ConfigSyncContextMenuItem extends AbstractContextMenuItem {
    public ConfigSyncContextMenuItem(Activity activity, ContextData coreyCxt) {
        super(activity, coreyCxt);
    }

    public void onClick() {
        ((MessageTabActivityCMN)mActivity).refreshConfiguration();
    }
}
