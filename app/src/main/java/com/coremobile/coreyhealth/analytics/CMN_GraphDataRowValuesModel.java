package com.coremobile.coreyhealth.analytics;

import java.io.Serializable;

/**
 * Created by ireslab on 1/6/2016.
 */
public class CMN_GraphDataRowValuesModel implements Serializable {
    String column;
    String value;

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
