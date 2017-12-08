package com.coremobile.coreyhealth;

import java.util.ArrayList;

public class ListItemObj {
	String mName;
	String mScrollDirection;
	String mOnTouch;
	String mCommonTag;
	String mImage;
	public ArrayList<ListItem> ListItemArray = new ArrayList<ListItem>();
	
	 public String getColor(String ListItemName)
	    {
	        	
	    	 String color=null;
	    	 for(ListItem litem: ListItemArray)
	    	 {
	    		 if(litem.getValue().equalsIgnoreCase(ListItemName))
	    		 {
	    			 color=litem.getBackgroundColor();
	    			 break;
	    		 }
	    	 }
	    			 
	    	
	    	return color;	
	    }
	public void setCommonTag(String CommonTag) { 
    	mCommonTag = CommonTag;
    }

    public String getCommonTag() { 
	return mCommonTag;
    }
    
	public void setName(String Name) { 
    	mName = Name;
    }

    public String getName() { 
	return mName;
    }
	public void setImage(String img) {
		mImage = img;
	}

	public String getImage() {
		return mImage;
	}
    
    public void setScrollDirection(String ScrollDirection) { 
    	mScrollDirection = ScrollDirection;
    }

    public String getScrollDirection() { 
	return mScrollDirection;
    }
    
    public void setOnTouch(String OnTouch) { 
    	mOnTouch = OnTouch;
    }

    public String getOnTouch() { 
	return mOnTouch;
    }

	public ArrayList<ListItem> getListItemArray() {
		return ListItemArray;
	}

	public void setListItemArray(ArrayList<ListItem> listItemArray) {
		ListItemArray = listItemArray;
	}
}
