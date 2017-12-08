package com.coremobile.coreyhealth.networkutils;

import android.os.AsyncTask;


/**
 * Created by Nitij on 20/04/2016.
 */
public class NetworkAsyncTask extends AsyncTask<Object, Object, Object> {
    NetworkCallBack mNetworkCallBack;
    int mTaskId;

    /**
     * From this method class instance can be configurable
     *
     * @return new instance of NetworkAsyncTask
     */
    public static NetworkAsyncTask getInstance() {
        return new NetworkAsyncTask();
    }

    private NetworkAsyncTask() {

    }

    public void startNetworkCall(int taskId, NetworkCallBack networkCallBack) throws NetworkException {
        mNetworkCallBack = networkCallBack;
        mTaskId = taskId;
        execute();
    }

    @Override
    protected Object doInBackground(Object... params) {
        try {
            return mNetworkCallBack.onNetworkCall(mTaskId, params);
        } catch (JsonParsingException e) {
            e.getMessage();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mNetworkCallBack.beforeNetworkCall(mTaskId);
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        mNetworkCallBack.afterNetworkCall(mTaskId, o);
    }
}
