package com.coremobile.coreyhealth;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class TelListener extends PhoneStateListener
{

    CoreyDBHelper mCoreyDBHelper;
    Context mContext;
    public TelListener(Context context)
    {
        super();
        this.mContext = context;
        mCoreyDBHelper = new CoreyDBHelper(context);
    }
    public void onCallStateChanged(int state, String incomingNumber)
    {
        super.onCallStateChanged(state, incomingNumber);

        Log.v("Phone State", "state:"+state);
        switch (state)
        {
        case TelephonyManager.CALL_STATE_IDLE:
            Log.v("Phone State",
                  "incomingNumber:"+incomingNumber+" ended");
            break;

        case TelephonyManager.CALL_STATE_OFFHOOK:
            Log.v("Phone State",
                  "incomingNumber:"+incomingNumber+" picked up");
            break;

        case TelephonyManager.CALL_STATE_RINGING:
            Log.v("Phone State",
                  "incomingNumber:"+incomingNumber+" received");

            if (incomingNumber == null)
            {
                Log.v("Phone State",
                      "incoming number is null so skipping the call");
                break;
            }

            //mCoreyDBHelper.updateCallerInfo(incomingNumber,CMN_Preferences.getUserToken(mContext));
            break;

        default:
            break;
        }
    }
}
