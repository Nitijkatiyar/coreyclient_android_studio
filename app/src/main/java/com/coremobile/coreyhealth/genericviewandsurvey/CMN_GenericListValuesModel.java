package com.coremobile.coreyhealth.genericviewandsurvey;

import java.util.List;

/**
 * Created by nitij on 20-04-2016.
 */
public class CMN_GenericListValuesModel {

    String Id, value;
    List<CMN_DependencyFieldModel> dependencyFieldModels;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<CMN_DependencyFieldModel> getDependencyFieldModels() {
        return dependencyFieldModels;
    }

    public void setDependencyFieldModels(List<CMN_DependencyFieldModel> dependencyFieldModels) {
        this.dependencyFieldModels = dependencyFieldModels;
    }
}
