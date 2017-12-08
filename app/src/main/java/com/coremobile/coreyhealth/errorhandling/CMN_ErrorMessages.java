package com.coremobile.coreyhealth.errorhandling;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by nitij on 17-10-2016.
 */
public class CMN_ErrorMessages {
    public static ConcurrentHashMap<Integer, String> errorMessage;
    private static CMN_ErrorMessages _CMN_errorMessages;

    private CMN_ErrorMessages(){

    }

    public static synchronized CMN_ErrorMessages getInstance() {
        if (_CMN_errorMessages == null)
        {
            errorMessage = new ConcurrentHashMap<>();
            _CMN_errorMessages = new CMN_ErrorMessages();
        }
        return _CMN_errorMessages;
    }

    public ConcurrentHashMap<Integer, String> getMap() {
        return errorMessage;
    }

    public String getValue(int key) {
        if (errorMessage.get(key) != null) {
            return errorMessage.get(key);
        } else {
            return "";
        }
    }
}
