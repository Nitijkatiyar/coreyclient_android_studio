package com.coremobile.coreyhealth.scheduling.schedulingmodels;

/**
 * Created by BumbleBee on 9/5/2017.
 */

public class ScheduleListModel {

   private String Id;
    private String StartTime;
    private String EndTime;
    private String RequestedForDate;
    private String RequestedOnDate;
    private String IsConfirmed;
    private String IsRequested;
    private String Room;
    private String Specialty;
    private String Modality;
    private String Procedure;
    private String Exam;
    private String Patient;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public String getRequestedForDate() {
        return RequestedForDate;
    }

    public void setRequestedForDate(String requestedForDate) {
        RequestedForDate = requestedForDate;
    }

    public String getRequestedOnDate() {
        return RequestedOnDate;
    }

    public void setRequestedOnDate(String requestedOnDate) {
        RequestedOnDate = requestedOnDate;
    }

    public String getIsConfirmed() {
        return IsConfirmed;
    }

    public void setIsConfirmed(String isConfirmed) {
        IsConfirmed = isConfirmed;
    }

    public String getIsRequested() {
        return IsRequested;
    }

    public void setIsRequested(String isRequested) {
        IsRequested = isRequested;
    }

    public String getRoom() {
        return Room;
    }

    public void setRoom(String room) {
        Room = room;
    }

    public String getSpecialty() {
        return Specialty;
    }

    public void setSpecialty(String specialty) {
        Specialty = specialty;
    }

    public String getModality() {
        return Modality;
    }

    public void setModality(String modality) {
        Modality = modality;
    }

    public String getProcedure() {
        return Procedure;
    }

    public void setProcedure(String procedure) {
        Procedure = procedure;
    }

    public String getExam() {
        return Exam;
    }

    public void setExam(String exam) {
        Exam = exam;
    }

    public String getPatient() {
        return Patient;
    }

    public void setPatient(String patient) {
        Patient = patient;
    }
}
