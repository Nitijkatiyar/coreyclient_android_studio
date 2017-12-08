package com.coremobile.coreyhealth;

import java.util.List;

public interface IDashboardListItem
{

    boolean isLeaf();
    boolean isNew = false;

    // leaf methods
    String getMainText();
    void setMainText(String txt);
    String getSubscript();
    void setSubscript(String txt);
    int getApplicationId();
    void setApplicationId(int id);
    int getMessageId();
    void setMessageId(int id);
    
    boolean isParent();


     boolean isEditable() ;

    public void setIsEditable(boolean editable) ;
   

    // group methods
    void addItem(IDashboardListItem item);
    List<IDashboardListItem> getAllItems();
    int size();
}
