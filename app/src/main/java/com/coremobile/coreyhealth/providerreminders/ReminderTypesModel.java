package com.coremobile.coreyhealth.providerreminders;

import java.io.Serializable;

/**
 * Created by nitij on 12-05-2016.
 */
public class ReminderTypesModel implements Serializable {

    private String description, image, name;
    private int id;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
