package com.coremobile.coreyhealth.newui;

public class FirstLevelObjects {

    private String sDisplayName;
    private String sDisplaySubscript;
    public static boolean isEditable;
    public static boolean isParent;
    private int sParentID;
    private int sObjectID;
    
    private SecondLevelObjects sSecondLevelObjects;

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

    public void setObjectid(int ObjectID) {
        sObjectID = ObjectID;
    }

    public int getObjectid() {
        return sObjectID;
    }
    
   public void setDisplaySubscript(String displaySubscript) {
        sDisplaySubscript = displaySubscript;
    }

    public String getDisplaySubscript() {
        return sDisplaySubscript;
    }
    
    
    public void setChildObjects(SecondLevelObjects slo){
        sSecondLevelObjects = slo;
    }
    
    public SecondLevelObjects getChildObjects(){
        return sSecondLevelObjects;
    }
    

}
