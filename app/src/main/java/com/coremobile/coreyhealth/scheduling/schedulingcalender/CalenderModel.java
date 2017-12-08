package com.coremobile.coreyhealth.scheduling.schedulingcalender;

/**
 * Created by BumbleBee on 8/23/2017.
 */

public class CalenderModel {
    String date;
    String status;
    String allAvailable, noneAvailable, fewAvailable,unknown;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAllAvailable() {
        return allAvailable;
    }

    public void setAllAvailable(String allAvailable) {
        this.allAvailable = allAvailable;
    }

    public String getNoneAvailable() {
        return noneAvailable;
    }

    public void setNoneAvailable(String noneAvailable) {
        this.noneAvailable = noneAvailable;
    }

    public String getFewAvailable() {
        return fewAvailable;
    }

    public void setFewAvailable(String fewAvailable) {
        this.fewAvailable = fewAvailable;
    }

    public String getUnknown() {
        return unknown;
    }

    public void setUnknown(String unknown) {
        this.unknown = unknown;
    }
}
