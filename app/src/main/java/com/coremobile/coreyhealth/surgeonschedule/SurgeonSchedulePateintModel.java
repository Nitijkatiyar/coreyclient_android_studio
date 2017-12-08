package com.coremobile.coreyhealth.surgeonschedule;

public class SurgeonSchedulePateintModel {

    public String getAnonName() {
        return AnonName;
    }

    public void setAnonName(String anonName) {
        AnonName = anonName;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAnonId() {
        return AnonId;
    }

    public void setAnonId(String anonId) {
        AnonId = anonId;
    }

    public String getDOSStart() {
        return DOSStart;
    }

    public void setDOSStart(String DOSStart) {
        this.DOSStart = DOSStart;
    }

    public String getDOSEnd() {
        return DOSEnd;
    }

    public void setDOSEnd(String DOSEnd) {
        this.DOSEnd = DOSEnd;
    }

    public String getSurgeon() {
        return Surgeon;
    }

    public void setSurgeon(String surgeon) {
        Surgeon = surgeon;
    }

    public String getSpecialty() {
        return Specialty;
    }

    public void setSpecialty(String specialty) {
        Specialty = specialty;
    }

    public String getProcedure() {
        return Procedure;
    }

    public void setProcedure(String procedure) {
        Procedure = procedure;
    }

    public String getOR() {
        return OR;
    }

    public void setOR(String OR) {
        this.OR = OR;
    }

    String AnonName;
    String Name;
    String AnonId;
    String DOSStart;
    String DOSEnd;
    String Surgeon;
    String Specialty;
    String Procedure;
    String OR;

}