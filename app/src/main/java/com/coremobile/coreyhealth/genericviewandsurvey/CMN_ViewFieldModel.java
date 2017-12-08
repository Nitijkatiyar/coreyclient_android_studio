package com.coremobile.coreyhealth.genericviewandsurvey;

import android.view.View;

/**
 * Created by nitij on 21-04-2016.
 */
public class CMN_ViewFieldModel {
    View view;
    CMN_GenericFieldModel surveyFieldModel;
    String initialValue;

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public CMN_GenericFieldModel getSurveyFieldModel() {
        return surveyFieldModel;
    }

    public void setSurveyFieldModel(CMN_GenericFieldModel surveyFieldModel) {
        this.surveyFieldModel = surveyFieldModel;
    }

    public String getInitialValue() {
        return initialValue;
    }

    public void setInitialValue(String initialValue) {
        this.initialValue = initialValue;
    }
}
