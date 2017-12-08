package com.coremobile.coreyhealth.scheduling.schedulingmodels;

import java.util.HashMap;

/**
 * Created by BumbleBee on 8/31/2017.
 */

public class BlockDetails {

    private String StartTime;
    private String EndTime;
    private String ColorCode;
    private HashMap<Integer, String> BlockedFor;

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

    public String getColorCode() {
        return ColorCode;
    }

    public void setColorCode(String colorCode) {
        ColorCode = colorCode;
    }

    public HashMap<Integer, String> getBlockedFor() {
        return BlockedFor;
    }

    public void setBlockedFor(HashMap<Integer, String> blockedFor) {
        BlockedFor = blockedFor;
    }
}
