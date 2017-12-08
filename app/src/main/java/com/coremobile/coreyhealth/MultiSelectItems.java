package com.coremobile.coreyhealth;

public class MultiSelectItems {
	private String mValue;
	private String mId;
	private String mDisplayText;
	private String mDescription;
	private String mBackgroundColour;
	
	private String VideoDescription;
	private String VideoLink;
	private boolean selected;
	
	public void setValue(String value)
	{
		mValue=value;
	}
	public String getValue()
	{
		return mValue;
	}
	
	public void setVideoDescription(String desc)
	{
		VideoDescription=desc;
	}
	public String getVideoDescription()
	{
		return VideoDescription;
	}
	public void setVideoLink(String link)
	{
		VideoLink=link;
	}
	public String  getVideoLink()
	{
		return VideoLink;
	}
	public boolean isSelected() {
	        return selected;
	 }
	 
	 public void setSelected(boolean selected) {
	        this.selected = selected;
	 }


}
