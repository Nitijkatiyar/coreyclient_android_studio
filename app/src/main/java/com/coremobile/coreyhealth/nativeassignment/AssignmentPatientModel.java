package com.coremobile.coreyhealth.nativeassignment;

import java.io.Serializable;

/**
 * Created by nitij on 28-05-2016.
 */

public class AssignmentPatientModel implements Serializable {
    private String patientName;
    private String patientId;
    private String SurgeryTime;

    public String getSurgeryTime() {
        return SurgeryTime;
    }

    public void setSurgeryTime(String surgeryTime) {
        SurgeryTime = surgeryTime;
    }

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
