package com.coremobile.coreyhealth.nativeassignment;

/**
 * Created by nitij on 13-06-2016.
 */

public class NurseToORAssignmentModel {
    private String providerId;
    private String providerName;
    private String hDeptId;
    private String hDeptName;
    private String ORId;
    private String hDeptNameToShow;
    private boolean isEdited;

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

    public String getORId() {
        return ORId;
    }

    public void setORId(String ORId) {
        this.ORId = ORId;
    }

    public String gethDeptNameToShow() {
        return hDeptNameToShow;
    }

    public void sethDeptNameToShow(String hDeptNameToShow) {
        this.hDeptNameToShow = hDeptNameToShow;
    }

    public boolean isEdited() {
        return isEdited;
    }

    public void setEdited(boolean edited) {
        isEdited = edited;
    }
}
