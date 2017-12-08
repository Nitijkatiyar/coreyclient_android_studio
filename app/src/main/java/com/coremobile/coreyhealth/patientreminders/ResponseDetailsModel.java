package com.coremobile.coreyhealth.patientreminders;

import java.io.Serializable;

/**
 * Created by ireslab on 12/22/2015.
 */
public class ResponseDetailsModel implements Serializable {
    String date;
    String response;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
