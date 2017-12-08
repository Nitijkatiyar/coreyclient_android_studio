package com.coremobile.coreyhealth;

import java.util.List;

public class SingleDashboardListItem implements IDashboardListItem
{
    private String mMainText;
    private String mSubscript;
    private int mApplicationId;
    private int mMessageId;
    private boolean isEditable;

    //---------------------------------------------
    // IDashboardListItem interface implementation
    //---------------------------------------------
    public boolean isLeaf()
    {
        return true;
    }

    // leaf methods
    public String getMainText()
    {
        return mMainText;
    }

    public void setMainText(String txt)
    {
        mMainText = txt;
    }

    public String getSubscript()
    {
        return mSubscript;
    }

    public void setSubscript(String txt)
    {
        mSubscript = txt;
    }

    public int getApplicationId()
    {
        return mApplicationId;
    }

    public void setApplicationId(int id)
    {
        mApplicationId = id;
    }

    public int getMessageId()
    {
        return mMessageId;
    }

    public void setMessageId(int id)
    {
        mMessageId = id;
    }

    // group methods
    public void addItem(IDashboardListItem item)
    {
        throw new UnsupportedOperationException();
    }

    public List<IDashboardListItem> getAllItems()
    {
        throw new UnsupportedOperationException();
    }

    public int size()
    {
        throw new UnsupportedOperationException();
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
