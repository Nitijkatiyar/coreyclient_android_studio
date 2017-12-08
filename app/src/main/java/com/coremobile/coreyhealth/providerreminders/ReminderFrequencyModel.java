package com.coremobile.coreyhealth.providerreminders;

import java.io.Serializable;

/**
 * Created by nitij on 12-05-2016.
 */
public class ReminderFrequencyModel implements Serializable {

    private String value,displaytext;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDisplaytext() {
        return displaytext;
    }

    public void setDisplaytext(String displaytext) {
        this.displaytext = displaytext;
    }
}
