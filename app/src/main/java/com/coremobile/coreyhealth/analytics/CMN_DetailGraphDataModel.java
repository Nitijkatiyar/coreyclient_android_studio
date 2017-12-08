package com.coremobile.coreyhealth.analytics;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ireslab on 1/6/2016.
 */
public class CMN_DetailGraphDataModel implements Serializable {

    List<CMN_GraphDataValuesModel> CNMGraphDataValuesModels;

    public List<CMN_GraphDataValuesModel> getCNMGraphDataValuesModels() {
        return CNMGraphDataValuesModels;
    }

    public void setCNMGraphDataValuesModels(List<CMN_GraphDataValuesModel> CNMGraphDataValuesModels) {
        this.CNMGraphDataValuesModels = CNMGraphDataValuesModels;
    }
}
