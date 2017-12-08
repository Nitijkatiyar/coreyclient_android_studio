package com.coremobile.coreyhealth;

public class DeptStatus4AnalyticsObj {
	private String mDeptId;
	private String mStatusId;
	private String mStartTime;
	private String mEndTime;
	private String mTargetTime;
	
	public void setDeptId(String Deptid) {
		mDeptId = Deptid;
    }

    public String getDeptId() {  
	return mDeptId;
    }
    
    public void setStatusId(String StatusId) {
    	mStatusId = StatusId;
    }

    public String getStatusId() {  
	return mStatusId;
    }
    
    public void setStartTime(String StartTime) {
		mStartTime = StartTime;
    }

    public String getStartTime() {  
	return mStartTime;
    }
    public void setEndTime(String EndTime) {
		mEndTime = EndTime;
    }

    public String getEndTime() {  
	return mEndTime;
    }
    public void setTargetTime(String TargetTime) {
		mTargetTime = TargetTime;
    }

    public String getTargetTime() {  
	return mTargetTime;
    }
}
