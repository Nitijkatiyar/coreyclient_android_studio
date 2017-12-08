package com.coremobile.coreyhealth.scheduling.schedulingmodels;

/**
 * Created by BumbleBee on 8/30/2017.
 */

public class Schedules {

    private String ScheduleId;
    private String ColorCode;
    private String IsRequested;
    private String IsConfirmed;
    private String IsCancelled;
    private String IsAvailable;
    private String IsNotUsable;
    private String ScheduledStart;
    private String ScheduledEnd;
    private String ActualStart;
    private String ActualEnd;
    private String Specialty;
    private String Procedure;
    private String Exam;
    private String Modality;
    private String ProcRoomId;
    private String ORId;
    private String Surgeon;
    private String PatientId;
    private String PatName;
    private String PatAnonName;
    private String PatAnonId;
    private String Anesthesiologist;

    public String getScheduleId() {
        return ScheduleId;
    }

    public void setScheduleId(String scheduleId) {
        ScheduleId = scheduleId;
    }

    public String getColorCode() {
        return ColorCode;
    }

    public void setColorCode(String colorCode) {
        ColorCode = colorCode;
    }

    public String getIsRequested() {
        return IsRequested;
    }

    public void setIsRequested(String isRequested) {
        IsRequested = isRequested;
    }

    public String getIsConfirmed() {
        return IsConfirmed;
    }

    public void setIsConfirmed(String isConfirmed) {
        IsConfirmed = isConfirmed;
    }

    public String getIsCancelled() {
        return IsCancelled;
    }

    public void setIsCancelled(String isCancelled) {
        IsCancelled = isCancelled;
    }

    public String getIsAvailable() {
        return IsAvailable;
    }

    public void setIsAvailable(String isAvailable) {
        IsAvailable = isAvailable;
    }

    public String getIsNotUsable() {
        return IsNotUsable;
    }

    public void setIsNotUsable(String isNotUsable) {
        IsNotUsable = isNotUsable;
    }

    public String getScheduledStart() {
        return ScheduledStart;
    }

    public void setScheduledStart(String scheduledStart) {
        ScheduledStart = scheduledStart;
    }

    public String getScheduledEnd() {
        return ScheduledEnd;
    }

    public void setScheduledEnd(String scheduledEnd) {
        ScheduledEnd = scheduledEnd;
    }

    public String getActualStart() {
        return ActualStart;
    }

    public void setActualStart(String actualStart) {
        ActualStart = actualStart;
    }

    public String getActualEnd() {
        return ActualEnd;
    }

    public void setActualEnd(String actualEnd) {
        ActualEnd = actualEnd;
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

    public String getExam() {
        return Exam;
    }

    public void setExam(String exam) {
        Exam = exam;
    }

    public String getModality() {
        return Modality;
    }

    public void setModality(String modality) {
        Modality = modality;
    }

    public String getProcRoomId() {
        return ProcRoomId;
    }

    public void setProcRoomId(String procRoomId) {
        ProcRoomId = procRoomId;
    }

    public String getORId() {
        return ORId;
    }

    public void setORId(String ORId) {
        this.ORId = ORId;
    }

    public String getSurgeon() {
        return Surgeon;
    }

    public void setSurgeon(String surgeon) {
        Surgeon = surgeon;
    }

    public String getPatientId() {
        return PatientId;
    }

    public void setPatientId(String patientId) {
        PatientId = patientId;
    }

    public String getPatName() {
        return PatName;
    }

    public void setPatName(String patName) {
        PatName = patName;
    }

    public String getPatAnonName() {
        return PatAnonName;
    }

    public void setPatAnonName(String patAnonName) {
        PatAnonName = patAnonName;
    }

    public String getPatAnonId() {
        return PatAnonId;
    }

    public void setPatAnonId(String patAnonId) {
        PatAnonId = patAnonId;
    }

    public String getAnesthesiologist() {
        return Anesthesiologist;
    }

    public void setAnesthesiologist(String anesthesiologist) {
        Anesthesiologist = anesthesiologist;
    }
}
