package com.coremobile.coreyhealth;

import org.json.JSONObject;

public interface IServerConnect
{

    //Member functions
    void gotUserInfoFromServer(JSONObject json);
    void showDialog();
    void closeDialog();
    public void throwToast(String stringToToast);
  
}
