package com.coremobile.coreyhealth.messaging;

import java.io.Serializable;
import java.util.List;
/**
 * @author Nitij Katiyar
 *
 */
public class CMN_MessagesModel implements Serializable {

	List<CMN_MessageDataModel> messages;
	int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<CMN_MessageDataModel> getMessages() {
		return messages;
	}

	public void setMessages(List<CMN_MessageDataModel> messages) {
		this.messages = messages;
	}
}
