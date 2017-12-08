package com.coremobile.coreyhealth;

public class DeptDef {
	private String mId;

	private String mDispName;
	private String mPosition;
	
	public void setId(String id) {
		mId = id;
    }

    public String getId() {  
	return mId;
    }
   
    public void setDispName(String DispName) {
		mDispName = DispName;
    }

    public String getDispName() {  
	return mDispName;
    }
    public void setPosition(String Position) {
		mPosition = Position;
		
		
    }

    public int getPosition() {  
	
	int pos= Integer.parseInt(mPosition);
	
	return pos;
    }
}
