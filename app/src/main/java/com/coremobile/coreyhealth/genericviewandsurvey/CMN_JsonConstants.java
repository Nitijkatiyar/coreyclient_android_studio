package com.coremobile.coreyhealth.genericviewandsurvey;

import java.util.HashMap;

/**
 * Created by nitij on 20-04-2016.
 */
public class CMN_JsonConstants {

    public static String SURVEY_VIEWNAME = "Viewname";
    public static String SURVEY_BUTTONTITLE = "ButtonTitle";
    public static String SURVEY_CATEGORY = "categories";
    public static String SURVEY_NAME = "Name";
    public static String SURVEY_DISPLAYNAME = "DisplayName";
    public static String SURVEY_POSITION = "Position";
    public static String SURVEY_FIELDS = "fields";
    public static String SURVEY_ID = "Id";
    public static String SURVEY_DISPLAYTEXT = "DisplayText";
    public static String SURVEY_VALIDATION = "Validation";
    public static String SURVEY_DISPLAY = "Display";
    public static String SURVEY_ISEDIT = "IsEdit";
    public static String SURVEY_ISMANDATORY = "IsMandatory";
    public static String SURVEY_MAXLIMIT = "MaxLimit";
    public static String SURVEY_LISTVALUEDATA = "listValuesData";
    public static String SURVEY_LISTVALUEDEPENDENTDATA = "dependencydata";
    public static String SURVEY_ATTRIBUTES = "attributes";
    public static String SURVEY_TYPE = "Type";
    public static String SURVEY_FIELDID = "fieldid";
    public static String SURVEY_DEFAULTVALUE = "DefaultValue";
    public static String SURVEY_VALUE = "Value";
    public static String SURVEY_DATA = "Data";

    public static HashMap<String,String> eprosMessages = new HashMap<>();

    public static String msgId = "";

    public enum ViewType {

        TextField, Switch, ListValue, DateTimePicker, DateTimeYearPicker,checkbox,DatePicker;

    }
}
