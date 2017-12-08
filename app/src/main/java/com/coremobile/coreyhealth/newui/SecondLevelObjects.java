package com.coremobile.coreyhealth.newui;

public class SecondLevelObjects {

    private String sDisplayName;
    public static boolean isEditable;
    public static boolean isParent;
    private int sParentID;
    private int sObjectID;

    public void setDisplayName(String displayName) {
        sDisplayName = displayName;
    }

    public String getDisplayName() {
        return sDisplayName;
    }

    public void setEditable(boolean editable) {
        isEditable = editable;
    }

    public void setParent(boolean parent) {
        isParent = parent;
    }

    public void setParentid(int parentID) {
        sParentID = parentID;
    }

    public int getParentid() {
        return sParentID;
    }
    
    
    public void setObjectid(int objecttID) {
        sObjectID = objecttID;
    }

    public int getObjectid() {
        return sObjectID;
    }

}
