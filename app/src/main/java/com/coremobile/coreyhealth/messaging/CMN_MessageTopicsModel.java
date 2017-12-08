package com.coremobile.coreyhealth.messaging;

import java.io.Serializable;
/**
 * @author Nitij Katiyar
 *
 */
public class CMN_MessageTopicsModel implements Serializable {

	String code, topicName;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}
}
