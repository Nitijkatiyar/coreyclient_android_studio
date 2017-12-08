package com.coremobile.coreyhealth;

public class VideoLink {
	private String VideoDescription;
	private String VideoLink;
	private boolean selected;
	
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
