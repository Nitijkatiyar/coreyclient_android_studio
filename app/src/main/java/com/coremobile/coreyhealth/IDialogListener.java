package com.coremobile.coreyhealth;

import android.content.DialogInterface;
import android.view.View;

public interface IDialogListener {
    /** initializes dialog body */
    void doPrepareDialog(int dialogId, View body);
    void doPositveClick(DialogInterface dialog, int dialogId, View body);
    void doNegativeClick(DialogInterface dialog, int dialogId, View body);
}
