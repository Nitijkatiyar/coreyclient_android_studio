package com.coremobile.coreyhealth.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;

public class CoreyEditText extends EditText{
	public CoreyEditText(Context context) {
        super(context);
       
    }

    public CoreyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        
    }

    public CoreyEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
      
    }
    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK )
        {
            clearFocus();
        }
        else if(keyCode == KeyEvent.FLAG_EDITOR_ACTION)
        {
        	 clearFocus();
        
        }
        
        return super.onKeyPreIme(keyCode, event);
    }
    
    /*
     *Done button not visible for edit text fix.  REfer the following
	 *http://stackoverflow.com/questions/5014219/multiline-edittext-with-done-softinput-action-label-on-2-3
     */
    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs)
    {
        InputConnection conn = super.onCreateInputConnection(outAttrs);
        outAttrs.imeOptions &= ~EditorInfo.IME_FLAG_NO_ENTER_ACTION;
        return conn;
    }

}
