package com.coremobile.coreyhealth.networkutils;


/**
 * Created by Nitij on 20/04/2016.
 */
public interface NetworkCallBack {

    void beforeNetworkCall(int taskId) ;

    Object afterNetworkCall(int taskId, Object o);

    Object onNetworkCall(int taskId, Object o) throws JsonParsingException, JsonParsingException ;


    Object onNetworkError(int taskId, NetworkException o);

}
