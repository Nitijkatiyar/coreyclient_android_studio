package com.coremobile.coreyhealth.newui;

import android.util.Log;

import java.io.Serializable;

public class Fields  implements Serializable {
	String TAG = "Corey_Fields";
    String sFieldname;
    String sFieldvalue;
    public boolean isEditable;
    private String sAppName;
    private String sTempData=null;
    private String sMsgContext;
    private String sOpenUrl;
    private String sDisplayText;
    private String sMultiSelect;
    private String sAppDisplayText;
    private String sAppTitle;
    private String sFieldId;
    private Boolean sConvertTime;
    private String slistValue;
    private String seditableListValue;
    private String sFieldStatus;
    private String sHasStatus;
    private String sIsNew;
    private String sFieldType;
    private String sEmailBody;
    private String sEmailSubject;
    private String sDisplaybox;
    private String sImageUrl;
    
    public void setHasStatus(String hasStatus) { 
    	sHasStatus = hasStatus;
    }

    public String getHasStatus() { 
	return sHasStatus;
    }
    
    public void setFieldType(String FieldType) { 
    	sFieldType = FieldType;
//    	Log.d(TAG, "sFieldname= "+sFieldname);
//    	Log.d(TAG, "sFieldType= "+sFieldType);
    }

    public String getFieldType() { 
    	if(sFieldType!=null) return sFieldType;
    	else return "NotDefined";
    }
    public Boolean IsTypeDateTime()
    {
    	if( sFieldType!=null)
    	{
    		if(sFieldType.equalsIgnoreCase("Datetime")) return true;
    		else return false;
    	}
    	else return false;
    }
    public Boolean IsTypeDateTimeYear()
    {
    	if( sFieldType!=null)
    	{
    		if(sFieldType.equalsIgnoreCase("Datetimeyear")) return true;
    		else return false;
    	}
    	else return false;
    }
    public Boolean IsTypeAddress()
    {
    	if( sFieldType!=null)
    	{
    		if(sFieldType.equalsIgnoreCase("Address")) return true;
    		else return false;
    	}
    	else return false;
    }
    public Boolean IsTypeMail()
    {
    	if( sFieldType!=null)
    	{
    		if(sFieldType.equalsIgnoreCase("Mail")) return true;
    		else return false;
    	}
    	else return false;
    }
    public void setIsNew(String IsNew) { 
    	sIsNew = IsNew;
    }

    public String getIsNew() { 
	return sIsNew;
    }
    public void setFieldStatus(String FieldStatus) { 
    	sFieldStatus = FieldStatus;
    }

    public String getFieldStatus() { 
	return sFieldStatus;
    }
    
    public void setEmailBody(String emailbody) { 
    	 sEmailBody = emailbody;
    }

    public String getEmailBody() { 
	return sEmailBody;
    }
    
    public void setEmailSubject(String emailsub) { 
    	 sEmailSubject = emailsub;
    }

    public String getEmailSubject() { 
	return sEmailSubject;
    }
    
    public void seteditableListValue(String editlistValue) {
    	seditableListValue = editlistValue;
	    }
    public String geteditableListValue() {
	return seditableListValue;
    }
       
    public void setlistValue(String listValue) {
    	slistValue = listValue;
	    }
    public String getlistValue() {
	return slistValue;
    }
    
    public void setAppDispText(String AppDispText) {
    	 sAppDisplayText=AppDispText;
        }
    
    public String getAppDispText() {
    	return sAppDisplayText;
        }
    
    public void setAppTitle(String AppTitle) {
    	sAppTitle=AppTitle;
       }
   
   public String getAppTitle() {
   	return sAppTitle;
       }
   
   public void setConvertTime(String ConvertTime) {
   	if(ConvertTime!=(null))
   	{
	if(ConvertTime.equals("true"))
   	{
   		sConvertTime=true;
   	}else sConvertTime=false;
   	}else sConvertTime=false; 
      }
  
  public Boolean getConvertTime() {
  	return sConvertTime;
      }
  
   public void setFieldId(String FieldId) {
	   sFieldId=FieldId;
      }
  
  public String getFieldId() {
  	return sFieldId;
      }
  
  public void setFieldDispText(String dispText) {
		sDisplayText = dispText;
	    }
    public String getFieldDispText() {
	return sDisplayText;
    }

    public String getsMultiSelect() {
        return sMultiSelect;
    }

    public void setsMultiSelect(String sMultiSelect) {
        this.sMultiSelect = sMultiSelect;
    }

    public void setFieldName(String fieldname) {
	sFieldname = fieldname;
    }

    public String getFieldName() {
	return sFieldname;
    }

    public void setFieldValue(String fieldvalue) {
	sFieldvalue = fieldvalue;
    }

    public String getFieldValue() {
	return sFieldvalue;
    }

    public void addAttributes(String key, String value) {

    }

    public void retrieveAttributes(String key) {

    }

    public void retrieveObjectList() {

    }

    public void setEditable(boolean isEditable) {
	this.isEditable = isEditable;
    }

    public boolean getEditable() {
	return isEditable;
    }

    public void setAppName(String appName) {
	sAppName = appName;
    }

    public String getAppName() {
	return sAppName;
    }

    public String getTempData() {
	return sTempData;
    }

    public void setTempData(String tempData) {
	this.sTempData = tempData;
	Log.d(TAG,"Fieldname for tempdata ="+sDisplayText);
    }

    public String getMsgContext() {
	return sMsgContext;
    }

    public void setMsgContext(String msgContext) {
	this.sMsgContext = msgContext;

    }

    public String getOpenUrl() {
	return sOpenUrl;
    }

    public void setOpenUrl(String openUrl) {
	this.sOpenUrl = openUrl;

    }
    
    public String getImageUrl() {
    	return sImageUrl;
        }

        public void setImageUrl(String imageUrl) {
    	this.sImageUrl = imageUrl;

        }
    
    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	Fields other = (Fields) obj;
	if (sFieldname != other.sFieldname)
	    
	    return false;
	return true;
    }

}
