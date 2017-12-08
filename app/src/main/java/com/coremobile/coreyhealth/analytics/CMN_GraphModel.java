package com.coremobile.coreyhealth.analytics;

import java.io.Serializable;

/**
 * Created by ireslab on 1/6/2016.
 */
public class CMN_GraphModel implements Serializable {
    CMN_GraphOptionsModel graphOptions;
    CMN_DetailGraphDataModel detailGraphDataValuesModel;
    CMN_GraphDataModel graphDataValuesModel;

    public CMN_GraphOptionsModel getGraphOptions() {
        return graphOptions;
    }

    public void setGraphOptions(CMN_GraphOptionsModel graphOptions) {
        this.graphOptions = graphOptions;
    }

    public CMN_DetailGraphDataModel getDetailGraphDataValuesModel() {
        return detailGraphDataValuesModel;
    }

    public void setDetailGraphDataValuesModel(CMN_DetailGraphDataModel detailGraphDataValuesModel) {
        this.detailGraphDataValuesModel = detailGraphDataValuesModel;
    }

    public CMN_GraphDataModel getGraphDataValuesModel() {
        return graphDataValuesModel;
    }

    public void setGraphDataValuesModel(CMN_GraphDataModel graphDataValuesModel) {
        this.graphDataValuesModel = graphDataValuesModel;
    }
}
