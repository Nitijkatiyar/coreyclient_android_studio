package com.coremobile.coreyhealth.nativeassignment;

import java.util.List;

/**
 * Created by nitij on 13-06-2016.
 */

public class NurseToOrMergedModel {

    private String HdeptId;
    private String HdeptName;
    private boolean AssignedPerPatient;
    private boolean AssignedPerOR;
    private boolean AssignedPerOrg;
    private List<String> RoleCategory;
    private String providerId;
    private String providerName;
    private String hDeptId;
    private String hDeptName;
    private String ORId;
    private String hDeptNameToShow;
    private boolean isEdited;


    public String getHdeptId() {
        return HdeptId;
    }

    public void setHdeptId(String hdeptId) {
        HdeptId = hdeptId;
    }

    public String getHdeptName() {
        return HdeptName;
    }

    public void setHdeptName(String hdeptName) {
        HdeptName = hdeptName;
    }

    public boolean isAssignedPerPatient() {
        return AssignedPerPatient;
    }

    public void setAssignedPerPatient(boolean assignedPerPatient) {
        AssignedPerPatient = assignedPerPatient;
    }

    public boolean isAssignedPerOR() {
        return AssignedPerOR;
    }

    public void setAssignedPerOR(boolean assignedPerOR) {
        AssignedPerOR = assignedPerOR;
    }

    public boolean isAssignedPerOrg() {
        return AssignedPerOrg;
    }

    public void setAssignedPerOrg(boolean assignedPerOrg) {
        AssignedPerOrg = assignedPerOrg;
    }

    public List<String> getRoleCategory() {
        return RoleCategory;
    }

    public void setRoleCategory(List<String> roleCategory) {
        RoleCategory = roleCategory;
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
