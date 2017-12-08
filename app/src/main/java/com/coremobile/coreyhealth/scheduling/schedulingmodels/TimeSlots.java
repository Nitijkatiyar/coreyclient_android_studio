package com.coremobile.coreyhealth.scheduling.schedulingmodels;

/**
 * Created by BumbleBee on 8/30/2017.
 */

public class TimeSlots {

    private int Id;
    private String StartTime;
    private String EndTime;
    private String startDateToShow;
    private String endDateToShow;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public String getStartDateToShow() {
        return startDateToShow;
    }

    public void setStartDateToShow(String startDateToShow) {
        this.startDateToShow = startDateToShow;
    }

    public String getEndDateToShow() {
        return endDateToShow;
    }

    public void setEndDateToShow(String endDateToShow) {
        this.endDateToShow = endDateToShow;
    }
}
