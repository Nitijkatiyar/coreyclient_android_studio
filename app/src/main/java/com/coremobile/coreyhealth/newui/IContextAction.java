package com.coremobile.coreyhealth.newui;

import android.view.View;

public interface IContextAction extends View.OnClickListener {
    /** returns id of the icon resource to be shown in the navigator */
    int getIconResourceId();
}
