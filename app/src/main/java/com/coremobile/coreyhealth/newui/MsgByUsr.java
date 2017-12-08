package com.coremobile.coreyhealth.newui;

import java.util.ArrayList;

public class MsgByUsr {
	String mUsrName;
	String mUsrId;
	ArrayList<OneMsg> mMessages =new ArrayList<OneMsg>();
	
	public void addMsg(OneMsg onemsg)
	{
		mMessages.add(onemsg);
	}
	
	public ArrayList<OneMsg> getMsgList()
	{
		return mMessages;
	}
	
	public void setUsrName(String usrname)
	{
		mUsrName=usrname;
	}
	
	public void setUsrId(String usrid)
	{
		mUsrId=usrid;
	}
	
	public String getUsrName()
	{
		return mUsrName;
	}
	
	public String getUsrId()
	{
		return mUsrId;
	}
	
	
	public String getLastMsgId()
	{
		if(mMessages.size()!=0)
		{
		return(mMessages.get(mMessages.size()-1).getMsgId());
		}
		else return "0";
			
	}
}
