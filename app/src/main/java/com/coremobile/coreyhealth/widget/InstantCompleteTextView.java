package com.coremobile.coreyhealth.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.AutoCompleteTextView;

/**
 * InstantCompleteTextView extends AutoCompleteTextView to show the list of
 * suggestions ALWAYS. AutoCompleteTextView requires atleast one character to
 * be entered for showing suggestions list.
 */
public class InstantCompleteTextView extends AutoCompleteTextView {
	String TAG = "Corey_InstantCompleteTextView";
    public InstantCompleteTextView(Context context) {
        super(context);
    }

    public InstantCompleteTextView(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
    }

    public InstantCompleteTextView(Context arg0, AttributeSet arg1, int arg2) {
        super(arg0, arg1, arg2);
    }

    @Override
    public boolean enoughToFilter() {
        return true;
    }
    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event)
    {
        Log.d(TAG,"keyCode=" +keyCode);
        Log.d(TAG,"keyCode=" +event.getKeyCode());
        clearFocus();
   
    	if(keyCode == KeyEvent.KEYCODE_BACK )
        {
        	Log.d(TAG,"Keycode in back="+keyCode);
        	clearFocus();
        }
        else if(event.getKeyCode() == KeyEvent.FLAG_EDITOR_ACTION)
        {
        	 clearFocus();
        	Log.d(TAG,"Keycode in done="+event.getKeyCode());
        //	 keyCode = KeyEvent.KEYCODE_BACK;
        //	 event.changeFlags(event, KeyEvent.KEYCODE_BACK);
        }
        
        return super.onKeyPreIme(keyCode, event);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	if (!this.isPopupShowing()) {
            append("");
    		showDropDown();
    	}
        return super.onTouchEvent(event);
    }
}
