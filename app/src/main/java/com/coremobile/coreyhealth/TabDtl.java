package com.coremobile.coreyhealth;

public class TabDtl {
	public String mName;
	public String mDisplayText;
	public String mUrl;
	
	public void setTabDispText(String dispText) {
		mDisplayText = dispText;
	    }
    public String getTabDispText() {
	return mDisplayText;
    }
    
    public void setTabName(String fieldname) {
    	mName = mName;
    }

    public String getTabName() {
	return mName;
    }
    
    public String getOpenUrl() {
    	return mUrl;
     }

    public void setOpenUrl(String openUrl) {
    	this.mUrl = openUrl;

        }
}
