package com.coremobile.coreyhealth.newui;

import android.app.Activity;
import android.view.View;

import com.coremobile.coreyhealth.ContextData;
import com.coremobile.coreyhealth.R;

public class BaseContextAction implements IContextAction {
    protected Activity mActivity;
    protected ContextData mCoreyCxt;

    public BaseContextAction(Activity activity, ContextData coreyCxt) {
        mActivity = activity;
        mCoreyCxt = coreyCxt;
    }

    @Override
    public int getIconResourceId() {
        return R.drawable.nav_refresh_icon;
    }

    @Override
    public void onClick(View v) {
        System.out.println("XXXYYY BaseContextAction action button clicked " + mCoreyCxt.mDisplayText);
    }
}

