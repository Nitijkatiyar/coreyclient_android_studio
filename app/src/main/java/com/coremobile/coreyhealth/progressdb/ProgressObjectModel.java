package com.coremobile.coreyhealth.progressdb;

import java.io.Serializable;

/**
 * Created by nitij on 18-05-2016.
 */
public class ProgressObjectModel implements Serializable{

    private int id;
    private String stageName, objectId, status, color;

    public String getStageName() {
        if (stageName == null) {
            stageName = "";
        }
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
