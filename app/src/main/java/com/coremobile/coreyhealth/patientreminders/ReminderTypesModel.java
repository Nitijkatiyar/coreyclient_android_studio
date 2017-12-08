package com.coremobile.coreyhealth.patientreminders;

import java.io.Serializable;

/**
 * @author Nitij Katiyar
 *
 */
public class ReminderTypesModel implements Serializable{

    int id;
    String description;
    String imageUrl;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
