package com.coremobile.coreyhealth.nativeassignment;

import java.util.List;

/**
 * Created by nitij on 28-05-2016.
 */

public class AssignmentHospitalsModel {

    String HdeptId, HdeptName;

    boolean AssignedPerPatient, AssignedPerOR, AssignedPerOrg;
    List<String> RoleCategory;


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
}
