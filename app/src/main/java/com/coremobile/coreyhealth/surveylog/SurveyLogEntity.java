package com.coremobile.coreyhealth.surveylog;

/**
 * Created by Aman on 9/1/2017.
 */

public class SurveyLogEntity {

    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateofSurgery() {
        return dateofSurgery;
    }

    public void setDateofSurgery(String dateofSurgery) {
        this.dateofSurgery = dateofSurgery;
    }

    public String getSurgeon() {
        return surgeon;
    }

    public void setSurgeon(String surgeon) {
        this.surgeon = surgeon;
    }

    String dateofSurgery;
    String surgeon;
}
