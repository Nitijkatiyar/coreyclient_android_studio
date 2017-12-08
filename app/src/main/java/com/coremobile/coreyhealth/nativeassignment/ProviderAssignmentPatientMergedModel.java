package com.coremobile.coreyhealth.nativeassignment;

/**
 * Created by nitij on 15-06-2016.
 */

public class ProviderAssignmentPatientMergedModel {
    String providerid,providername;
    String roleCategory;
    private String assignmentproviderId;
    private String assignmentproviderName;
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
        return assignmentproviderId;
    }

    public void setAssignmentProviderId(String assignmentproviderId) {
        this.assignmentproviderId = assignmentproviderId;
    }

    public String getProviderName() {
        return assignmentproviderName;
    }

    public void setAssignmentProviderName(String assignmentproviderName) {
        this.assignmentproviderName = assignmentproviderName;
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
    public String getRoleCategory() {
        return roleCategory;
    }

    public void setRoleCategory(String roleCategory) {
        this.roleCategory = roleCategory;
    }

    public String getproviderid() {
        return providerid;
    }

    public void setproviderid(String Providerid) {
        providerid = Providerid;
    }

    public String getProvidername() {
        return providername;
    }

    public void setProvidername(String Providername) {
        providername = Providername;
    }
}
