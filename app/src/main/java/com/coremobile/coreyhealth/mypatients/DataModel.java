package com.coremobile.coreyhealth.mypatients;

import java.io.Serializable;

/**
 * Created by Your name on 10/11/2017.
 */

public class DataModel implements Serializable{


    public String getDisplayText() {
        return DisplayText;
    }

    public void setDisplayText(String displayText) {
        DisplayText = displayText;
    }

    public String getAttributeName() {
        return AttributeName;
    }

    public void setAttributeName(String attributeName) {
        AttributeName = attributeName;
    }

    String DisplayText;
    String AttributeName;
}
