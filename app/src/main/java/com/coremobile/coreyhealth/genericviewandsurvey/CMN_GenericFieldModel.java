package com.coremobile.coreyhealth.genericviewandsurvey;

import java.util.List;

/**
 * Created by nitij on 20-04-2016.
 */
public class CMN_GenericFieldModel {
    int Id, maxLimmit;
    String key;
    boolean Display, IsEdit, IsMandatory;
    String DisplayText, Type, Validation, Value;
    List<CMN_GenericListValuesModel> listValuesData;
    List<CMN_GenericAttributesModel> attributes;


    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getMaxLimmit() {
        return maxLimmit;
    }

    public void setMaxLimmit(int maxLimmit) {
        this.maxLimmit = maxLimmit;
    }

    public boolean isDisplay() {
        return Display;
    }

    public void setDisplay(boolean display) {
        Display = display;
    }

    public boolean isEdit() {
        return IsEdit;
    }

    public void setIsEdit(boolean isEdit) {
        IsEdit = isEdit;
    }

    public boolean isMandatory() {
        return IsMandatory;
    }

    public void setIsMandatory(boolean isMandatory) {
        IsMandatory = isMandatory;
    }

    public String getDisplayText() {
        return DisplayText;
    }

    public void setDisplayText(String displayText) {
        DisplayText = displayText;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getValidation() {
        return Validation;
    }

    public void setValidation(String validation) {
        Validation = validation;
    }

    public String getValue() {
        return Value;
    }

    public void setValue(String value) {
        Value = value;
    }

    public List<CMN_GenericListValuesModel> getListValuesData() {
        return listValuesData;
    }

    public void setListValuesData(List<CMN_GenericListValuesModel> listValuesData) {
        this.listValuesData = listValuesData;
    }

    public List<CMN_GenericAttributesModel> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<CMN_GenericAttributesModel> attributes) {
        this.attributes = attributes;
    }
}
