package com.coremobile.coreyhealth.surveylog;

/**
 * created by Aman
 */

public class SurveyModel {

    private String patientName;
    private String dos;

    public String getSurgeon() {
        return surgeon;
    }

    public void setSurgeon(String surgeon) {
        this.surgeon = surgeon;
    }

    private String surgeon;


    public String getDateofsurvey() {
        return dateofsurvey;
    }

    public void setDateofsurvey(String dateofsurvey) {
        this.dateofsurvey = dateofsurvey;
    }

    private String dateofsurvey;
    private String firstInvitation;

    public String getViewForm() {
        return viewForm;
    }

    public void setViewForm(String viewForm) {
        this.viewForm = viewForm;
    }

    private String viewForm;
    private Boolean dosToConvert;
    private Boolean lastLoginToConvert;
    private Boolean firstInvitationToConvert;
    private Boolean invitationRemindersToConvert;



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
