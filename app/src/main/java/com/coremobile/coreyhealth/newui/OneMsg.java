package com.coremobile.coreyhealth.newui;

public class OneMsg {
	/**
	 * The content of the message
	 */
	String message;
	/**
	 * boolean to determine, who is sender of this message
	 */
	boolean isMine;
	/**
	 * boolean to determine, whether the message is a status message or not.
	 * it reflects the changes/updates about the sender is writing, have entered text etc
	 */
	boolean isStatusMessage;
	
	String mTo;
	String mFrom;
	String mId;
	String mTimeStamp;
	/**
	 * Constructor to make a Message object
	 */
	public OneMsg(String message, boolean isMine) {
		super();
		this.message = message;
		this.isMine = isMine;
		this.isStatusMessage = false;
	}
	/**
	 * Constructor to make a status Message object
	 * consider the parameters are swaped from default Message constructor,
	 *  not a good approach but have to go with it.
	 */
	public OneMsg( String message) {
		super();
		this.message = message;
		this.isMine = false;
//		this.isStatusMessage = status; 
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public boolean isMine() {
		return isMine;
	}
	public void setMine(boolean isMine) {
		this.isMine = isMine;
	}
	public boolean isStatusMessage() {
		return isStatusMessage;
	}
	public void setStatusMessage(boolean isStatusMessage) {
		this.isStatusMessage = isStatusMessage;
	}
	
	public void setMsgTo(String to)
	{
		 mTo=to;
	
	}
	public void setMsgFrom(String from)
	{
		 mFrom= from;
			
	}
	public void setMsgTimestamp(String timestamp)
	{
		
		 mTimeStamp=timestamp;
	}
	public void setMsgId(String id)
	{
		
		mId=id;
	}
	
	public String getMsgTo()
	{
		return mTo;
	}
	public String getMsgFrom()
	{
		return mFrom;
	}
	public String getMsgTimestamp()
	{
		return mTimeStamp;
	}
	public String getMsgId()
	{
		
		return mId;
	}
	public int getMsgIdInt()
	{
		int id= Integer.parseInt(mId);
		return id;
	}
}
