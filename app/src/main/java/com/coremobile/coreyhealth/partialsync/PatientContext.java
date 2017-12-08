package com.coremobile.coreyhealth.partialsync;

import java.io.Serializable;

/**
 * Created by nitij on 29-03-2016.
 */
public class PatientContext implements Serializable {

    String contextId, name;

    public String getContextId() {
        return contextId;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
