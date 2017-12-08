package com.coremobile.coreyhealth;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class RetrieveCallerInfoTask extends   AsyncTask<Void, Void, Integer>
    implements IPullDataFromServer {
    private static final String TAG = "Corey_RetrieveCallerInfoTask";

    private static final int ERROR_NO_CONNECTION = -2;
    private static final int ERROR_CALLER_PULL_FAILED = -3;
    private static final int ERROR_DATA_PULL_FAILED = -4;
    private static final int ERROR_EXCEPTION = -5;

    private Activity mActivity;
    private String mName;
    private String mPhoneNumber;
    private String mCompany;
    private ProgressDialog mDlg = null;
    private String mPullStatus;

    public RetrieveCallerInfoTask(Activity activity, String name, String phonenum, String company) {
        mActivity = activity;
        mName = name;
        mPhoneNumber = phonenum;
        mCompany = company;
    }

    protected void onPreExecute() {
        mDlg = ProgressDialog
                .show(mActivity,
                    "",
                    "Retrieving messages from server...",
                    true, false);
    }

    protected Integer doInBackground(Void... params) {
        try {
            if (!Utils.checkInternetConnection()) {
                return ERROR_NO_CONNECTION;
            }

            // trigger sync caller data, if needed 
            if (!MyApplication.INSTANCE.isCallerSyncInitiated(mPhoneNumber)) {
                MyApplication.INSTANCE.pullDataForCaller(mName, mPhoneNumber, mCompany);
                if (!MyApplication.INSTANCE.isCallerSyncInitiated(mPhoneNumber)) {
                    return ERROR_CALLER_PULL_FAILED;
                }
                // sleep for caller info to be ready
                Thread.sleep(IAppConstants.INITIAL_WAIT_TIME);
            }

            // pull data
            Log.d(TAG, "Waiting for pull completion");
            mPullStatus = null;
//            mActivity.runOnUiThread(new Runnable() {
//                public void run() {
//                    MyApplication.INSTANCE.pullData(RetrieveCallerInfoTask.this);
//                }
//            });
            while (mPullStatus == null) {
                Thread.sleep(IAppConstants.LOOP_WAIT_TIME);
            }

            Log.d(TAG, "Pull status " + mPullStatus);

            if (!"success".equals(mPullStatus)) {
                return ERROR_DATA_PULL_FAILED;
            }
            return new CoreyDBHelper(mActivity)
                    .getMsgIdForCaller(mPhoneNumber);
        } catch(Exception e) {
            e.getMessage();
            return ERROR_EXCEPTION;
        }
    }

    protected void onPostExecute(Integer msgId) {
        if (mDlg != null) {
            mDlg.dismiss();
        }

        if (msgId == -1) {
            Toast.makeText(
                mActivity,
                "Data still being pulled from server. Please try again after a short while",
                Toast.LENGTH_LONG).show();
        } else if (msgId < 0) {
            Toast.makeText(
                mActivity,
                "Unable to pull caller data. Error code: " + msgId,
                Toast.LENGTH_LONG).show();
        }
    }

    // IPullDataFromServer implementation
    @Override
    public void finishedParsing(String status) {
        if (status == null) {
            status = "pull failed";
        }
        Log.d(TAG, "pulldata returned: " + status);
        mPullStatus = status;
    }

    @Override
    public void showDialog() {

    }

    @Override
    public void closeDialog() {
    }
}
