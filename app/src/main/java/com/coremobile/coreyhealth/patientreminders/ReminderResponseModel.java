package com.coremobile.coreyhealth.patientreminders;

import java.io.Serializable;

/**
 * Created by ireslab on 12/22/2015.
 */
public class ReminderResponseModel implements Serializable {

    int id;
    String reminderName;
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

    public String getReminderName() {
        return reminderName;
    }

    public void setReminderName(String reminderName) {
        this.reminderName = reminderName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


}
