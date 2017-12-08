package com.coremobile.coreyhealth;

/**
 * Created by Aman on 8/10/2017.
 */

public interface AsyncTaskCompleteListener<T> {

    public void onTaskComplete(T result);
    public void onTaskStart();

}
