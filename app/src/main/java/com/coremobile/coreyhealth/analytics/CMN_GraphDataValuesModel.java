package com.coremobile.coreyhealth.analytics;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ireslab on 1/6/2016.
 */
public class CMN_GraphDataValuesModel implements Serializable {

    String rowName;
    List<CMN_GraphDataRowValuesModel> CNMGraphDataRowValuesModelList;

    public String getRowName() {
        return rowName;
    }

    public void setRowName(String rowName) {
        this.rowName = rowName;
    }

    public List<CMN_GraphDataRowValuesModel> getCNMGraphDataRowValuesModelList() {
        return CNMGraphDataRowValuesModelList;
    }

    public void setCNMGraphDataRowValuesModelList(List<CMN_GraphDataRowValuesModel> CNMGraphDataRowValuesModelList) {
        this.CNMGraphDataRowValuesModelList = CNMGraphDataRowValuesModelList;
    }
}
