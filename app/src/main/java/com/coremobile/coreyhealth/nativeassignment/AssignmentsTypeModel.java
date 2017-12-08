package com.coremobile.coreyhealth.nativeassignment;

import java.io.Serializable;

/**
 * Created by nitij on 06-06-2016.
 */

public class AssignmentsTypeModel implements Serializable{

    int id, position;
    String name;
    String AssignmentsOptions;


    public String getAssignmentsOptions() {
        return AssignmentsOptions;
    }

    public void setAssignmentsOptions(String assignmentsOptions) {
        AssignmentsOptions = assignmentsOptions;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
