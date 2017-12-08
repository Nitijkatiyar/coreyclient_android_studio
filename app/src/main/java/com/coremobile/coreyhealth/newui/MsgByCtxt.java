package com.coremobile.coreyhealth.newui;

import java.util.ArrayList;
import java.util.HashMap;

public class MsgByCtxt {
	String mCtxtName;
	String mCtxtId;
	ArrayList<MsgByUsr> mMsgByUsrList =new ArrayList<MsgByUsr>();
	 HashMap<String, MsgByUsr> mMsgByUsrMap = new  HashMap<String, MsgByUsr>();
	 
	public MsgByUsr getMsgByUsr(String usrid)
	{
		return(mMsgByUsrMap.get(usrid));
	}
	public void addMsgByUsr(String usrid,MsgByUsr msgbysur )
	{
		mMsgByUsrMap.put(usrid, msgbysur);
	}
	public void setCtxtName(String Ctxtname)
	{
		mCtxtName=Ctxtname;
	}
	
	public void setCtxtId(String Ctxtid)
	{
		mCtxtId=Ctxtid;
	}
	
	public String getCtxtName()
	{
		return mCtxtName;
	}
	
	public String getCtxtId()
	{
		return mCtxtId;
	}
}
