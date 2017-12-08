package com.coremobile.coreyhealth;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

public class AlertDialogFragment extends DialogFragment {

    public static AlertDialogFragment newInstance(Bundle args) {
        AlertDialogFragment frag = new AlertDialogFragment();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        int title = args.getInt("title");
        String listenerId = args.getString("listener", null);
        String yesLabel = args.getString("yesLabel", "YES");
        String noLabel = args.getString("noLabel", "NO");
        String okLabel = args.getString("okLabel", "OK");

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View body = args.containsKey("body")?
            inflater.inflate(args.getInt("body"), null) : null;
        int message = (body == null)? args.getInt("message") :-1;

        final int id = args.getInt("id");
        final IDialogListener listener = MyApplication.INSTANCE.getDialogListener(listenerId);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (title >= 0) {
            builder = builder.setTitle(title);
        }
        if (listenerId != null) {
            builder = builder
                .setPositiveButton(yesLabel, //"YES",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            listener.doPositveClick(dialog, id, body);
                        }
                    }
                )
                .setNegativeButton(noLabel, //"NO",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            listener.doNegativeClick(dialog, id, body);
                        }
                    }
                );
        } else {
            builder = builder
                .setPositiveButton(okLabel, //"OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // nothing to do as there is no listener
                        }
                    }
                );
        }
        if (body != null && listener != null) {
            listener.doPrepareDialog(id, body);
            builder = builder.setView(body);
        } else {
            builder = builder.setMessage(message);
        }

        return builder.create();
    }
}
