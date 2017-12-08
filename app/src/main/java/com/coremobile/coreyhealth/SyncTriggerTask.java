package com.coremobile.coreyhealth;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.coremobile.coreyhealth.googleAnalytics.CMN_Preferences;
import com.coremobile.coreyhealth.newui.MessageTabActivityCMN;

import org.json.JSONException;
import org.json.JSONObject;

public class SyncTriggerTask extends AsyncTask<String, Void, Integer> implements IPullDataFromServer {
    public static int TRIGGER_RESULT_NONE = 0;
    public static int TRIGGER_RESULT_JSON0 = 1;
    public static int TRIGGER_RESULT_JSON1 = 2;

    protected final int INITIAL_WAIT_TIME = IAppConstants.INITIAL_WAIT_TIME;
    protected final int LOOP_WAIT_TIME = IAppConstants.LOOP_WAIT_TIME;

    private static final String TAG = "Corey_SyncTriggerTask";

    protected Activity mActivity;
    protected ContextData mCoreyCxt;
    protected int mErrorStringId;

    private ProgressDialog mDialog;
    private String mSyncDescription;
    private String mUrlSuffix;
    private String mPullStatus;
    private int mTriggerResultType;

    public SyncTriggerTask(Activity activity, ContextData coreyCxt, String syncDescription,
                           String urlSuffix, int triggerResultType) {
        mActivity = activity;
        mCoreyCxt = coreyCxt;
        mSyncDescription = syncDescription;
        mUrlSuffix = urlSuffix;
        mTriggerResultType = triggerResultType;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        // *** HACK hardcoded class check *** close navigation drawer
        if (mActivity instanceof MessageTabActivityCMN) {
            MessageTabActivityCMN msgTabActivity = (MessageTabActivityCMN) mActivity;
            Log.d(TAG, "hiding navigation drawer");
            msgTabActivity.closeDrawer();
        }

        Log.d(TAG, "Starting sync progress bar");
        if (mDialog == null || !mDialog.isShowing()) {
            mDialog = ProgressDialog.show(
                    mActivity, // context
                    null, // title
                    mSyncDescription + " in progress ...", // message
                    true, // indeterminate
                    false // cancelable
            );
        }
        // stop timer
        Utils.stopAutoPullTimer(mActivity);
    }

    @Override
    protected Integer doInBackground(String... params) {
        if (!Utils.checkInternetConnection()) {
            mErrorStringId = R.string.no_internet_connection;
            return -1;
        }
        try {
            ///////////////////////////
            // trigger sync
            ///////////////////////////
            Log.d(TAG, "Triggering sync: " + mSyncDescription);
            String result = Utils.httpPost(
                    Utils.getServerBaseUrl() + mUrlSuffix,
                    "token", CMN_Preferences.getUserToken(mActivity),

                    "context", CMN_Preferences.getCurrentContextId(mActivity),
                    "__list__", params
            );
            Log.d(TAG, "Result-Str: '" + result + "'");

            if (mTriggerResultType == TRIGGER_RESULT_JSON0 ||
                    mTriggerResultType == TRIGGER_RESULT_JSON1) {
                JSONObject json = null;

                if (result != null) {
                    json = new JSONObject(result);
                }

                if (json != null) {
                    String jsonResult = json.getString("result");
                    Log.d(TAG, mSyncDescription + " : json response : " + jsonResult);
                    String expected =
                            (mTriggerResultType == TRIGGER_RESULT_JSON0) ? "0" : "1";
//                    if (!jsonResult.equals(expected)) {
//                        // sync failed
//                        mErrorStringId = R.string.sync_req_failed;
//                        return -1;
//                    }
                } else {
                    mErrorStringId = R.string.sync_req_failed;
                    return -1;
                }
            }

            ///////////////////////////
            // wait for sync completion
            ///////////////////////////
            Log.d(TAG, "Waiting for sync completion: " + mSyncDescription);
            int status;
            Thread.sleep(INITIAL_WAIT_TIME);
            while ((status = isSyncComplete()) == 0) {
                Thread.sleep(LOOP_WAIT_TIME);
            }

            if (status != 1) {
                mErrorStringId = R.string.sync_status_check_failed;
                return -1;
            }

            ///////////////////////////
            // pull
            ///////////////////////////
            Log.d(TAG, "Waiting for pull completion: " + mSyncDescription);
            mPullStatus = null;
            mActivity.runOnUiThread(new Runnable() {
                public void run() {
                    Log.d(TAG, "pullData req on ui thread");

                    if (mDialog != null) {
                        mDialog.dismiss();
                    }
                }
            });
            while (mPullStatus == null) {
                Thread.sleep(LOOP_WAIT_TIME);
            }

            Log.d(TAG, "Pull status " + mPullStatus);

            if (!"success".equals(mPullStatus)) {
                mErrorStringId = R.string.pull_req_failed;
                return -1;
            }
        } catch (Exception e) {
            e.getMessage();
            mErrorStringId = R.string.sync_failed;
            return -1;
        }
        return 1;
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
        if (mDialog != null) {
            mDialog.dismiss();
        }

        if (result != 1) {
            // show error dialog
            Log.d(TAG, "Operation failed " + mSyncDescription);
        }

        // start timer
        Utils.startAutoPullTimer(mActivity);

        Intent intent = new Intent(MyApplication.INSTANCE.AppConstants.ACTION_SYNC_COMPLETE);
        intent.putExtra(MyApplication.INSTANCE.AppConstants.EXTRA_COREY_CONTEXT, mCoreyCxt.mName);
        mActivity.sendBroadcast(intent);
    }

    private int isSyncComplete() throws JSONException {
        if (!Utils.checkInternetConnection()) {
            mErrorStringId = R.string.no_internet_connection;
            return -1;
        }

        String result = Utils.httpPost(
                Utils.getServerBaseUrl() + "GetTriggerInProgress.aspx",
                "username", LocalPrefs.INSTANCE.username(),
                "password", LocalPrefs.INSTANCE.password(),
                "organization", LocalPrefs.INSTANCE.organization()
        );
        JSONObject json = null;
        if (result != null) {
            json = new JSONObject(result);
        }

        if (json != null) {
            String jsonResult = json.getString("result");
            Log.d(TAG, "isCoreyfySyncComplete json response: "
                    + jsonResult);
            Log.d(TAG, "isCoreyfySyncComplete json response length: "
                    + jsonResult.length());
            Log.d(TAG, "isCoreyfySyncComplete json response empty: "
                    + jsonResult.isEmpty());
            if (jsonResult.length() == 4) {
                return 1;
            }
        }
        return 0;
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
        // nothing to do
    }

    @Override
    public void closeDialog() {
        // nothing to do
    }
}
