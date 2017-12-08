package com.coremobile.coreyhealth.scheduling.schedulingmodels;

import java.util.List;

/**
 * Created by BumbleBee on 8/31/2017.
 */

public class TableData {

    private List<RoomSchedules> roomSchedules;
    private List<TimeSlots> timeSlotsList;

    public void setRoomSchedules(List<RoomSchedules> roomSchedules) {
        this.roomSchedules = roomSchedules;
    }

    public void setTimeSlotsList(List<TimeSlots> timeSlotsList) {
        this.timeSlotsList = timeSlotsList;
    }

    public List<RoomSchedules> getRoomSchedules() {
        return roomSchedules;
    }

    public List<TimeSlots> getTimeSlotsList() {
        return timeSlotsList;
    }
}
