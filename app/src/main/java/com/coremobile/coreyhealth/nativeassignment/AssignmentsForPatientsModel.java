package com.coremobile.coreyhealth.nativeassignment;

/**
 * Created by nitij on 28-05-2016.
 */

public class AssignmentsForPatientsModel {
    private String providerId;
    private String providerName;
    private String hDeptId;
    private String hDeptName;
    private boolean isLead;
    private String hDeptNameToShow;
    private boolean isEdited;


    public boolean isEdited() {
        return isEdited;
    }

    public void setEdited(boolean edited) {
        isEdited = edited;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String gethDeptId() {
        return hDeptId;
    }

    public void sethDeptId(String hDeptId) {
        this.hDeptId = hDeptId;
    }

    public String gethDeptName() {
        return hDeptName;
    }

    public void sethDeptName(String hDeptName) {
        this.hDeptName = hDeptName;
    }

    public boolean isLead() {
        return isLead;
    }

    public void setLead(boolean lead) {
        isLead = lead;
    }

    public String gethDeptNameToShow() {
        return hDeptNameToShow;
    }

    public void sethDeptNameToShow(String hDeptNameToShow) {
        this.hDeptNameToShow = hDeptNameToShow;
    }
}
