package com.coremobile.coreyhealth;

import java.util.ArrayList;
import java.util.List;

public class MultiDashboardListItem implements IDashboardListItem
{
    private ArrayList<IDashboardListItem> mItems;
    
    private boolean isEditable;

    public MultiDashboardListItem()
    {
        mItems = new ArrayList<IDashboardListItem>();
    }

    //---------------------------------------------
    // IDashboardListItem interface implementation
    //---------------------------------------------
    public boolean isLeaf()
    {
        return false;
    }

    // leaf methods
    public String getMainText()
    {
        return mItems.get(0).getMainText();
    }

    public void setMainText(String txt)
    {
        mItems.get(0).setMainText(txt);
    }

    public String getSubscript()
    {
        return mItems.get(0).getSubscript();
    }

    public void setSubscript(String txt)
    {
        mItems.get(0).setSubscript(txt);
    }

    public int getApplicationId()
    {
        return mItems.get(0).getApplicationId();
    }

    public void setApplicationId(int id)
    {
        mItems.get(0).setApplicationId(id);
    }

    public int getMessageId()
    {
        return mItems.get(0).getMessageId();
    }

    public void setMessageId(int id)
    {
        mItems.get(0).setMessageId(id);
    }


    // group methods
    public void addItem(IDashboardListItem item)
    {
        mItems.add(item);
    }

    public List<IDashboardListItem> getAllItems()
    {
        return mItems;
    }

    public int size()
    {
        return mItems.size();
    }

    @Override
    public boolean isParent() {
        return false;
    }

    @Override
    public boolean isEditable() {
        return isEditable;
    }

    @Override
    public void setIsEditable(boolean editable) {
       isEditable = editable;
        
    }
}
