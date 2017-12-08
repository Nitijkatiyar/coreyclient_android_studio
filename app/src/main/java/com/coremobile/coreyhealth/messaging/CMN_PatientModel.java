package com.coremobile.coreyhealth.messaging;

import java.io.Serializable;
/**
 * @author Nitij Katiyar
 *
 */
public class CMN_PatientModel implements Serializable {
	String patientName, patientId;

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}
}
