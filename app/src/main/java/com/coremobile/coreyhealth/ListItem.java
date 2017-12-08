package com.coremobile.coreyhealth;

public class ListItem {
	private String mValue;
	private String mdisplayText;
	private String mImage;
	private String mSubscript;
	private String mBackgroundColor;
	private String mId;
	private String mDescription;
	private boolean selected;
	private String mOpenUrl;
	private double mrating;
	
	public void setRating(double rate)
	{
		mrating=rate;
	}
	
	public double getRating()
	{
		return mrating;
	}
	public void setOpenUrl(String OpenUrl)
	{
		mOpenUrl=OpenUrl;
	}
	public String getOpenUrl()
	{
		return mOpenUrl;
	}
	
	
	public void setId(String id)
	{
		mId=id;
	}
	public String getId()
	{
		return mId;
	}
	
	public void setDescription(String desc)
	{
		mDescription=desc;
	}
	public String getVideoDescription()
	{
		return mDescription;
	}
	public void setValue(String Value) { 
    	mValue = Value;
    }

    public String getValue() { 
	return mValue;
    }
    public void setdisplayText(String displayText) { 
    	mdisplayText = displayText;
    }

    public String getdisplayText() { 
	return mdisplayText;
    }
    public void setImage(String Image) { 
    	mImage = Image;
    }

    public String getImage() { 
	return mImage;
    }
    public void setSubscript(String Subscript) { 
    	mSubscript = Subscript;
    }

    public String getSubscript() { 
	return mSubscript;
    }
    public void setBackgroundColor(String BackgroundColor) { 
    	mBackgroundColor = BackgroundColor;
    }

    public String getBackgroundColor() { 
	return mBackgroundColor;
    }
    
    public boolean isSelected() {
        return selected;
    }
 
    public void setSelected(boolean selected) {
        this.selected = selected;
 	}
}
