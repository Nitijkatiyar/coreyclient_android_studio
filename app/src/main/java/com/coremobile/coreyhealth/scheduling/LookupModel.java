package com.coremobile.coreyhealth.scheduling;

import java.io.Serializable;

/**
 * Created by BumbleBee on 8/21/2017.
 */

public class LookupModel implements Serializable {

    String displayName;
    String dateOfSurery;
    String anonId;
    String ID;
    String surgeon;

    public boolean getSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean selected;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDateOfSurery() {
        return dateOfSurery;
    }

    public void setDateOfSurery(String dateOfSurery) {
        this.dateOfSurery = dateOfSurery;
    }

    public String getAnonId() {
        return anonId;
    }

    public void setAnonId(String anonId) {
        this.anonId = anonId;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getSurgeon() {
        return surgeon;
    }

    public void setSurgeon(String surgeon) {
        this.surgeon = surgeon;
    }
}
