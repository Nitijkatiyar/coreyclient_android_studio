package com.coremobile.coreyhealth.native_epro;

import java.io.Serializable;
import java.util.List;

/**
 * Created by nitij on 06-01-2017.
 */
public class CMN_eproModel implements Serializable {

    String ID;
    String NotificationID;
    String Question;
    String OptionsCount;
    String MessageToPat;
    String PatientReminderMessage;
    String ReminderType;
    String ProviderNotificationOptions;
    String FreeTextTitle;
    String ReminderFreqDisplayName;
    String Stage;
    String Procedure;
    String PatientName;
    String ReminderCategory;
    String TimeOfEvent;
    String ReminderTime;
    String Frequency;
    String ReminderFreq;
    String PatientId;

    List<String> Options;
    List<CMN_ePROsDependentModel> ePROsDependentModels;

    boolean HasFreeText;
    boolean NotifyProvider;
    boolean IsDependent;
    boolean StopOnSuccess;
    boolean PatientNeedsReminder;

    public List<String> getOptions() {
        return Options;
    }

    public void setOptions(List<String> options) {
        this.Options = options;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getNotificationID() {
        return NotificationID;
    }

    public void setNotificationID(String notificationID) {
        NotificationID = notificationID;
    }

    public String getQuestion() {
        return Question;
    }

    public void setQuestion(String question) {
        Question = question;
    }

    public String getOptionsCount() {
        return OptionsCount;
    }

    public void setOptionsCount(String optionsCount) {
        OptionsCount = optionsCount;
    }

    public String getMessageToPat() {
        return MessageToPat;
    }

    public void setMessageToPat(String messageToPat) {
        MessageToPat = messageToPat;
    }

    public String getPatientReminderMessage() {
        return PatientReminderMessage;
    }

    public void setPatientReminderMessage(String patientReminderMessage) {
        PatientReminderMessage = patientReminderMessage;
    }

    public String getReminderType() {
        return ReminderType;
    }

    public void setReminderType(String reminderType) {
        ReminderType = reminderType;
    }

    public String getProviderNotificationOptions() {
        return ProviderNotificationOptions;
    }

    public void setProviderNotificationOptions(String providerNotificationOptions) {
        ProviderNotificationOptions = providerNotificationOptions;
    }

    public String getFreeTextTitle() {
        return FreeTextTitle;
    }

    public void setFreeTextTitle(String freeTextTitle) {
        FreeTextTitle = freeTextTitle;
    }

    public String getReminderFreqDisplayName() {
        return ReminderFreqDisplayName;
    }

    public void setReminderFreqDisplayName(String reminderFreqDisplayName) {
        ReminderFreqDisplayName = reminderFreqDisplayName;
    }

    public String getStage() {
        return Stage;
    }

    public void setStage(String stage) {
        Stage = stage;
    }

    public String getProcedure() {
        return Procedure;
    }

    public void setProcedure(String procedure) {
        Procedure = procedure;
    }

    public String getPatientName() {
        return PatientName;
    }

    public void setPatientName(String patientName) {
        PatientName = patientName;
    }

    public String getReminderCategory() {
        return ReminderCategory;
    }

    public void setReminderCategory(String reminderCategory) {
        ReminderCategory = reminderCategory;
    }

    public String getTimeOfEvent() {
        return TimeOfEvent;
    }

    public void setTimeOfEvent(String timeOfEvent) {
        TimeOfEvent = timeOfEvent;
    }

    public String getReminderTime() {
        return ReminderTime;
    }

    public void setReminderTime(String reminderTime) {
        ReminderTime = reminderTime;
    }

    public String getFrequency() {
        return Frequency;
    }

    public void setFrequency(String frequency) {
        Frequency = frequency;
    }

    public String getReminderFreq() {
        return ReminderFreq;
    }

    public void setReminderFreq(String reminderFreq) {
        ReminderFreq = reminderFreq;
    }

    public String getPatientId() {
        return PatientId;
    }

    public void setPatientId(String patientId) {
        PatientId = patientId;
    }

    public boolean isHasFreeText() {
        return HasFreeText;
    }

    public void setifHasFreeText(boolean hasFreeText) {
        HasFreeText = hasFreeText;
    }

    public boolean isNotifyProvider() {
        return NotifyProvider;
    }

    public void setifNotifyProvider(boolean notifyProvider) {
        NotifyProvider = notifyProvider;
    }

    public boolean isDependent() {
        return IsDependent;
    }

    public void setifDependent(boolean dependent) {
        IsDependent = dependent;
    }

    public boolean isStopOnSuccess() {
        return StopOnSuccess;
    }

    public void setifStopOnSuccess(boolean stopOnSuccess) {
        StopOnSuccess = stopOnSuccess;
    }

    public boolean isPatientNeedsReminder() {
        return PatientNeedsReminder;
    }

    public void setifPatientNeedsReminder(boolean patientNeedsReminder) {
        PatientNeedsReminder = patientNeedsReminder;
    }

    public List<CMN_ePROsDependentModel> getePROsDependentModels() {
        return ePROsDependentModels;
    }

    public void setePROsDependentModels(List<CMN_ePROsDependentModel> ePROsDependentModels) {
        this.ePROsDependentModels = ePROsDependentModels;
    }
}
