package com.coremobile.coreyhealth.messaging;

import java.io.Serializable;
/**
 * @author Nitij Katiyar
 *
 */
public class CMN_MessageToModel implements Serializable {

	String code, name;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
