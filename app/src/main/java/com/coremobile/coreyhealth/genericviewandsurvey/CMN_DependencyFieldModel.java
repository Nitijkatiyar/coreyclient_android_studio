package com.coremobile.coreyhealth.genericviewandsurvey;

import java.util.List;

/**
 * Created by nitij on 07-10-2016.
 */
public class CMN_DependencyFieldModel {
   String DependentFieldID;
    List<String> DependentValues;

    public String getDependentFieldID() {
        return DependentFieldID;
    }

    public void setDependentFieldID(String dependentFieldID) {
        DependentFieldID = dependentFieldID;
    }

    public List<String> getDependentValues() {
        return DependentValues;
    }

    public void setDependentValues(List<String> dependentValues) {
        DependentValues = dependentValues;
    }
}
