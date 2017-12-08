package com.coremobile.coreyhealth.messaging;

import java.io.Serializable;
/**
 * @author Nitij Katiyar
 *
 */
public class CMN_MessageDataModel implements Serializable {

	int Id;
	String contextId;
	String ContextDisplayName;
	String Topic;
	int FromUsrID;
	String FromDisplayName;
	String ToUsers;
	String Message;
	int threadId;
	String TimeStamp;
	boolean HasRead;
	String ToUserIds;
	int topicId;

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public String getContextId() {
		return contextId;
	}

	public void setContextId(String contextId) {
		this.contextId = contextId;
	}

	public String getContextDisplayName() {
		return ContextDisplayName;
	}

	public void setContextDisplayName(String contextDisplayName) {
		ContextDisplayName = contextDisplayName;
	}

	public String getTopic() {
		return Topic;
	}

	public void setTopic(String topic) {
		Topic = topic;
	}

	public int getFromUsrID() {
		return FromUsrID;
	}

	public void setFromUsrID(int fromUsrID) {
		FromUsrID = fromUsrID;
	}

	public String getFromDisplayName() {
		return FromDisplayName;
	}

	public void setFromDisplayName(String fromDisplayName) {
		FromDisplayName = fromDisplayName;
	}

	public String getToUsers() {
		return ToUsers;
	}

	public void setToUsers(String toUsers) {
		ToUsers = toUsers;
	}

	public String getMessage() {
		return Message;
	}

	public void setMessage(String message) {
		Message = message;
	}

	public int getThreadId() {
		return threadId;
	}

	public void setThreadId(int threadId) {
		this.threadId = threadId;
	}

	public String getTimeStamp() {
		return TimeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		TimeStamp = timeStamp;
	}

	public boolean isHasRead() {
		return HasRead;
	}

	public void setHasRead(boolean hasRead) {
		HasRead = hasRead;
	}

	public String getToUserIds() {
		return ToUserIds;
	}

	public void setToUserIds(String toUserIds) {
		ToUserIds = toUserIds;
	}

	public int getTopicId() {
		return topicId;
	}

	public void setTopicId(int topicId) {
		this.topicId = topicId;
	}

}
