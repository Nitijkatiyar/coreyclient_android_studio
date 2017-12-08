package com.coremobile.coreyhealth;

import java.util.ArrayList;
import java.util.HashMap;



public class PatientAnalyticsdDat {
	private String mPatientId;
	private String mName;
	private String mCurDeptId;
	private String mCurDepEstCompTime;
	private String mEstDischargeTime;
	public HashMap <String,String>Dept2StatusMap = new HashMap <String,String>();
	
	public ArrayList<DeptStatus4AnalyticsObj> DeptStatusList = new ArrayList<DeptStatus4AnalyticsObj>();
	
	public void setPatientId(String PatientId) {
		mPatientId = PatientId;
    }

    public String getPatientId() {  
	return mPatientId;
    }
    public void setName(String Name) {
		mName = Name;
    }
    public String getName() {  
    	return mName;
     }
       

    public void setCurDeptId(String CurDeptId) {
    	mCurDeptId = CurDeptId;
    }

    public String getCurDeptId() {  
	return mCurDeptId;
    }
    
    public void setCurDepEstCompTime(String CurDepEstCompTime) {
		mCurDepEstCompTime = CurDepEstCompTime;
    }

    public String getCurDepEstCompTime() {  
	return mCurDepEstCompTime;
    }
    public void setEstDischargeTime(String EstDischargeTime) {
		mEstDischargeTime = EstDischargeTime;
    }

    public String getEstDischargeTime() {  
	return mEstDischargeTime;
    }
   
	
}
