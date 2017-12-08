package com.coremobile.coreyhealth.newui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.coremobile.coreyhealth.ContextData;
import com.coremobile.coreyhealth.Utils;

public class SignoutContextMenuItem extends AbstractContextMenuItem {
    public SignoutContextMenuItem(Activity activity, ContextData coreyCxt) {
        super(activity, coreyCxt);
    }

    public void onClick() {
        try {
            ((MessageTabActivityCMN) mActivity).signout();
        }catch (ClassCastException e){
            e.getMessage();
            AlertDialog.Builder signoutBuilder = new AlertDialog.Builder(
                    mActivity);
            signoutBuilder
                    .setTitle("Signout")
                    .setMessage("Do you want to signout ?")
                    .setPositiveButton(android.R.string.yes,
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                    Utils.signout();
                                }
                            }).setNegativeButton(android.R.string.no, null).show();

        }
    }
}
