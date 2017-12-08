package com.coremobile.coreyhealth.genericviewandsurvey;

import java.util.List;

/**
 * Created by nitij on 20-04-2016.
 */
public class CMN_GenericCategoriesModel {

    String Name, DisplayName;
    int Position;
    List<CMN_GenericFieldModel> fields;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDisplayName() {
        return DisplayName;
    }

    public void setDisplayName(String displayName) {
        DisplayName = displayName;
    }

    public int getPosition() {
        return Position;
    }

    public void setPosition(int position) {
        Position = position;
    }

    public List<CMN_GenericFieldModel> getFields() {
        return fields;
    }

    public void setFields(List<CMN_GenericFieldModel> fields) {
        this.fields = fields;
    }
}
