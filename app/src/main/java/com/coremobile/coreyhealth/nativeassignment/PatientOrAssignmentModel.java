package com.coremobile.coreyhealth.nativeassignment;

/**
 * Created by nitij on 13-06-2016.
 */

public class PatientOrAssignmentModel {

    String patientId;
    String ORid;
    String orname;
    String patientName;
    String surgeryTime;

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getSurgeryTime() {
        return surgeryTime;
    }

    public void setSurgeryTime(String surgeryTime) {
        this.surgeryTime = surgeryTime;
    }

    public String getOrname() {
        return orname;
    }

    public void setOrname(String orname) {
        this.orname = orname;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getORid() {
        return ORid;
    }

    public void setORid(String ORid) {
        this.ORid = ORid;
    }
}
