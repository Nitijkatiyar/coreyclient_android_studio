package com.coremobile.coreyhealth.scheduling.schedulingmodels;

import java.util.List;

/**
 * Created by BumbleBee on 8/31/2017.
 */

public class RoomSchedules {

    private List<BlockDetails> blockDetailsList;
    private boolean IsUnusedOR;
    private String Name;
    private String RoomId;
    private List<Schedules> schedules;

    public List<BlockDetails> getBlockDetailsList() {
        return blockDetailsList;
    }

    public void setBlockDetailsList(List<BlockDetails> blockDetailsList) {
        this.blockDetailsList = blockDetailsList;
    }

    public boolean isUnusedOR() {
        return IsUnusedOR;
    }

    public void setUnusedOR(boolean unusedOR) {
        IsUnusedOR = unusedOR;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getRoomId() {
        return RoomId;
    }

    public void setRoomId(String roomId) {
        RoomId = roomId;
    }

    public List<Schedules> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<Schedules> schedules) {
        this.schedules = schedules;
    }
}
