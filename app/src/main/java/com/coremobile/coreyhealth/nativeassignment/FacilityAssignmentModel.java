package com.coremobile.coreyhealth.nativeassignment;

import java.util.List;

/**
 * Created by nitij on 13-06-2016.
 */

public class FacilityAssignmentModel {

    String hdeptid, hdeptname;
    List<FacilityAssignmentProviderModel> facilityAssignmentProviderModels;

    public List<FacilityAssignmentProviderModel> getFacilityAssignmentProviderModels() {
        return facilityAssignmentProviderModels;
    }

    public void setFacilityAssignmentProviderModels(List<FacilityAssignmentProviderModel> facilityAssignmentProviderModels) {
        this.facilityAssignmentProviderModels = facilityAssignmentProviderModels;
    }

    public String gethDeptId() {
        return hdeptid;
    }

    public void sethDeptId(String hdeptid) {
        this.hdeptid = hdeptid;
    }

    public String getHdeptname() {
        return hdeptname;
    }

    public void setHdeptname(String hdeptname) {
        this.hdeptname = hdeptname;
    }
}
