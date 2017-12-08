package com.coremobile.coreyhealth.newui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

import com.coremobile.coreyhealth.IDialogListener;
import com.coremobile.coreyhealth.MyApplication;
import com.coremobile.coreyhealth.R;
import com.coremobile.coreyhealth.Utils;

public class WebViewDialogHelper implements IDialogListener {
    public static interface IListener {
        void onTextChanged(String text);
    }

    private static final String LISTENER_ID = "WebViewDialogHelper";

    private Activity mActivity;
    private String mURL;
    private IListener mListener;
    private boolean mIsEditable;
    private String mNote;

    public WebViewDialogHelper(boolean isEditable, String note, Activity activity, IListener listener) {
        mIsEditable = isEditable;
        mNote = note;
        mActivity = activity;
        mListener = listener;
        MyApplication.INSTANCE.putDialogListener(LISTENER_ID, this);
    }

    @Override
    public void doPrepareDialog(int dialogId, View body) {
        WebView webview = getWebView(body);
        //webview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.loadUrl(mURL);
        webview.setWebChromeClient(new WebChromeClient());
        webview.setWebViewClient(new WebViewClient() {
            private ProgressDialog mProgressDialog;
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
            super.onPageFinished(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
            mProgressDialog = ProgressDialog.show(
                mActivity, // context
                null, // title
                "loading...", // message
                true, // indeterminate
                false // cancelable
            );
            super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
            }
        });
        if (mIsEditable) {
            if (mNote != null) {
                getNoteView(body).setText(mNote);
                getNoteView(body).setSelection(mNote.length());
            }
        } else {
            getNoteView(body).setVisibility(View.GONE);
        }

    }

    @Override
    public void doPositveClick(DialogInterface dialog, int dialogId, View body) {
        getWebView(body).onPause();
        if (mIsEditable && mListener != null) {
            String note = getNoteView(body).getText().toString();
            mListener.onTextChanged(note);
        }
    }

    @Override
    public void doNegativeClick(DialogInterface dialog, int dialogId, View body) {
        getWebView(body).onPause();
    }

    public void showDialog(String url) {
        mURL = url;
        Utils.showAlertDialog(mActivity, -1, LISTENER_ID, 0, R.layout.webview_dialog_body, "DONE", "CANCEL");
    }

    private WebView getWebView(View body) {
        return (WebView)body.findViewById(R.id.webview);
    }

    private EditText getNoteView(View body) {
        return (EditText)body.findViewById(R.id.note);
    }
}
