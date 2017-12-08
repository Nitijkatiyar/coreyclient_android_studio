package com.coremobile.coreyhealth.messaging;
/**
 * @author Nitij Katiyar
 *
 */
public class CMN_MessageThreads {

	int id;
	CMN_MessagesModel messages_perThread;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public CMN_MessagesModel getMessages_perThread() {
		return messages_perThread;
	}

	public void setMessages_perThread(CMN_MessagesModel messages_perThread) {
		this.messages_perThread = messages_perThread;
	}
}
