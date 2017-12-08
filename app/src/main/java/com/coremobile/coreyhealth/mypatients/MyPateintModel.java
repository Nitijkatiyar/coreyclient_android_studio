package com.coremobile.coreyhealth.mypatients;

import java.io.Serializable;

/**
 * Created by Your name on 10/11/2017.
 */

public class MyPateintModel  implements Serializable{

    String Id;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getDisplayName() {
        return DisplayName;
    }

    public void setDisplayName(String displayName) {
        DisplayName = displayName;
    }

    public String getOR() {
        return OR;
    }

    public void setOR(String OR) {
        this.OR = OR;
    }

    public String getModality() {
        return Modality;
    }

    public void setModality(String modality) {
        Modality = modality;
    }

    public String getAnonName() {
        return AnonName;
    }

    public void setAnonName(String anonName) {
        AnonName = anonName;
    }

    public String getAnonId() {
        return AnonId;
    }

    public void setAnonId(String anonId) {
        AnonId = anonId;
    }

    public String getSurgeon() {
        return Surgeon;
    }

    public void setSurgeon(String surgeon) {
        Surgeon = surgeon;
    }

    public String getDateOfProcedure() {
        return DateOfProcedure;
    }

    public void setDateOfProcedure(String dateOfProcedure) {
        DateOfProcedure = dateOfProcedure;
    }

    public String getTimeOfProcedure() {
        return TimeOfProcedure;
    }

    public void setTimeOfProcedure(String timeOfProcedure) {
        TimeOfProcedure = timeOfProcedure;
    }

    public String getDisplayColor() {
        return DisplayColor;
    }

    public void setDisplayColor(String displayColor) {
        DisplayColor = displayColor;
    }

    String DisplayName;
    String OR;
    String Modality;
    String AnonName;
    String AnonId;
    String Surgeon;
    String DateOfProcedure;
    String TimeOfProcedure;
    String DisplayColor;
    String Specialty;

    public String getSpeciality() {
        return Specialty;
    }

    public void setSpeciality(String speciality) {
        Specialty = speciality;
    }

    public String getProcedureRoom() {
        return ProcedureRoom;
    }

    public void setProcedureRoom(String procedureRoom) {
        ProcedureRoom = procedureRoom;
    }

    String ProcedureRoom;

    @Override
    public String toString() {
        return  DisplayName + " " + OR + " "
                + AnonName + " " + Surgeon;
    }
}
