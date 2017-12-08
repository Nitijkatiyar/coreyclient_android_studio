package com.coremobile.coreyhealth.googleFit;

import java.io.Serializable;

/**
 * Created by nitij on 11-03-2016.
 */
public class CMN_StepsModel implements Serializable {

    String startDate, endDate, stepsCount;

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStepsCount() {
        return stepsCount;
    }

    public void setStepsCount(String stepsCount) {
        this.stepsCount = stepsCount;
    }
}
