package com.coremobile.coreyhealth.newui;

import android.app.Activity;
import android.view.Menu;

import com.coremobile.coreyhealth.ContextData;

public abstract class AbstractContextMenuItem implements IContextMenuItem {
    Activity mActivity;
    ContextData mCoreyContext;

    public AbstractContextMenuItem(Activity activity, ContextData coreyCxt) {
        mActivity = activity;
        mCoreyContext = coreyCxt;
    }

    public int getId() {
        return mCoreyContext.mPosition;
    }

    public void addToMenu(Menu menu) {
        menu.add(0, mCoreyContext.mPosition, mCoreyContext.mPosition,
            mCoreyContext.mDisplayText);
    }
}
