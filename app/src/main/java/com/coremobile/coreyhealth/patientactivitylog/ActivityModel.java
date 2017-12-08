package com.coremobile.coreyhealth.patientactivitylog;

/**
 * Created by Nitij on 1/21/2017.
 */

public class ActivityModel {

    private String patientName;
    private String dos;
    private String lastLogin;
    private String firstInvitation;
    private String invitationReminders;
    private Boolean dosToConvert;
    private Boolean lastLoginToConvert;
    private Boolean firstInvitationToConvert;
    private Boolean invitationRemindersToConvert;



    public String getInvitationReminders() {
        return invitationReminders;
    }

    public void setInvitationReminders(String invitationReminders) {
        this.invitationReminders = invitationReminders;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getDos() {
        return dos;
    }

    public void setDos(String dos) {
        this.dos = dos;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getFirstInvitation() {
        return firstInvitation;
    }

    public void setFirstInvitation(String firstInvitation) {
        this.firstInvitation = firstInvitation;
    }

    public Boolean getDosToConvert() {
        return dosToConvert;
    }

    public void setDosToConvert(Boolean dosToConvert) {
        this.dosToConvert = dosToConvert;
    }

    public Boolean getLastLoginToConvert() {
        return lastLoginToConvert;
    }

    public void setLastLoginToConvert(Boolean lastLoginToConvert) {
        this.lastLoginToConvert = lastLoginToConvert;
    }

    public Boolean getFirstInvitationToConvert() {
        return firstInvitationToConvert;
    }

    public void setFirstInvitationToConvert(Boolean firstInvitationToConvert) {
        this.firstInvitationToConvert = firstInvitationToConvert;
    }

    public Boolean getInvitationRemindersToConvert() {
        return invitationRemindersToConvert;
    }

    public void setInvitationRemindersToConvert(Boolean invitationRemindersToConvert) {
        this.invitationRemindersToConvert = invitationRemindersToConvert;
    }
}
