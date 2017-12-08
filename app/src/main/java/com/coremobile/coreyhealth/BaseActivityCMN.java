package com.coremobile.coreyhealth;

import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.coremobile.coreyhealth.base.CMN_AppBaseActivity;

public class BaseActivityCMN extends CMN_AppBaseActivity
{
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_UP)
        {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            View v = getCurrentFocus();
            if (imm != null && v != null)
            {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }
        return false;
    }
}
