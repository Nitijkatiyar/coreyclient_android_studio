package com.coremobile.coreyhealth.newui;

import com.coremobile.coreyhealth.MyApplication;

import java.util.HashMap;

public class MsgHelper {
	
	 HashMap <String,MsgByCtxt> all_msgs_map;
	 
	 public MsgHelper()
	 {
		 all_msgs_map=MyApplication.INSTANCE.AllMsgsMap; 
	 }
	 
	 public HashMap <String,MsgByCtxt> getAllCtxtMsgs()
	 {
		 return all_msgs_map;
	 }
	 
	 public MsgByCtxt getMsgsByCtxt(String CtxtId)
	 {
		
		 return all_msgs_map.get(CtxtId);
	 }
	 public MsgByUsr getMsgsByUsr(String CtxtId, String Usrid)
	 {
		
		 return all_msgs_map.get(CtxtId).mMsgByUsrMap.get(Usrid);
	 }
	 
	 public void addMsgsByCtxt(String CtxtId, MsgByCtxt msgbyctxt)
	 {
		 all_msgs_map.put(CtxtId, msgbyctxt);
	 }
}
