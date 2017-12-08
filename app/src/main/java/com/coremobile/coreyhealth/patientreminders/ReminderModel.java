package com.coremobile.coreyhealth.patientreminders;

import java.io.Serializable;

/**
 * Created by ireslab on 12/23/2015.
 */
public class ReminderModel implements Serializable {

    private String id;
    private String applyByDefault;
    private String frequanyColHeading, frequencyData;
    private String msgToPatientColHeading, msgToPatientData;
    private String prescribedOnColHeading, prescribedOnData;
    private String providerNotificationPolicyColHeading, providerNotificationPolicyData;
    private String timesColHeading, timesData;
    private String titleColHeading, titleData;
    private String stageColHeading, stageData;
    private String remindertypeColHeading, remindertypeData;
    boolean stopOnSuccess;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApplyByDefault() {
        return applyByDefault;
    }

    public void setApplyByDefault(String applyByDefault) {
        this.applyByDefault = applyByDefault;
    }

    public String getFrequanyColHeading() {
        return frequanyColHeading;
    }

    public void setFrequanyColHeading(String frequanyColHeading) {
        this.frequanyColHeading = frequanyColHeading;
    }

    public String getFrequencyData() {
        return frequencyData;
    }

    public void setFrequencyData(String frequencyData) {
        this.frequencyData = frequencyData;
    }

    public String getMsgToPatientColHeading() {
        return msgToPatientColHeading;
    }

    public void setMsgToPatientColHeading(String msgToPatientColHeading) {
        this.msgToPatientColHeading = msgToPatientColHeading;
    }

    public String getMsgToPatientData() {
        return msgToPatientData;
    }

    public void setMsgToPatientData(String msgToPatientData) {
        this.msgToPatientData = msgToPatientData;
    }

    public String getPrescribedOnColHeading() {
        return prescribedOnColHeading;
    }

    public void setPrescribedOnColHeading(String prescribedOnColHeading) {
        this.prescribedOnColHeading = prescribedOnColHeading;
    }

    public String getPrescribedOnData() {
        return prescribedOnData;
    }

    public void setPrescribedOnData(String prescribedOnData) {
        this.prescribedOnData = prescribedOnData;
    }

    public String getProviderNotificationPolicyColHeading() {
        return providerNotificationPolicyColHeading;
    }

    public void setProviderNotificationPolicyColHeading(String providerNotificationPolicyColHeading) {
        this.providerNotificationPolicyColHeading = providerNotificationPolicyColHeading;
    }

    public String getProviderNotificationPolicyData() {
        return providerNotificationPolicyData;
    }

    public void setProviderNotificationPolicyData(String providerNotificationPolicyData) {
        this.providerNotificationPolicyData = providerNotificationPolicyData;
    }

    public String getTimesColHeading() {
        return timesColHeading;
    }

    public void setTimesColHeading(String timesColHeading) {
        this.timesColHeading = timesColHeading;
    }

    public String getTimesData() {
        return timesData;
    }

    public void setTimesData(String timesData) {
        this.timesData = timesData;
    }

    public String getTitleColHeading() {
        return titleColHeading;
    }

    public void setTitleColHeading(String titleColHeading) {
        this.titleColHeading = titleColHeading;
    }

    public String getTitleData() {
        return titleData;
    }

    public void setTitleData(String titleData) {
        this.titleData = titleData;
    }

    public String getStageColHeading() {
        return stageColHeading;
    }

    public void setStageColHeading(String stageColHeading) {
        this.stageColHeading = stageColHeading;
    }

    public String getStageData() {
        return stageData;
    }

    public void setStageData(String stageData) {
        this.stageData = stageData;
    }

    public String getRemindertypeColHeading() {
        return remindertypeColHeading;
    }

    public void setRemindertypeColHeading(String remindertypeColHeading) {
        this.remindertypeColHeading = remindertypeColHeading;
    }

    public String getRemindertypeData() {
        return remindertypeData;
    }

    public void setRemindertypeData(String remindertypeData) {
        this.remindertypeData = remindertypeData;
    }

    public boolean isStopOnSuccess() {
        return stopOnSuccess;
    }

    public void setStopOnSuccess(boolean stopOnSuccess) {
        this.stopOnSuccess = stopOnSuccess;
    }
}
