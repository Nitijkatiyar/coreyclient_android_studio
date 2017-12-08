package com.coremobile.coreyhealth.native_epro;

import java.io.Serializable;

/**
 * Created by ArUnKaWdIYa on 1/12/2017.
 */
public class CMN_ePROsDependentModel implements Serializable{
    private int id;
    private String question;
    private int DependentResponseOption;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public int getDependentResponseOption() {
        return DependentResponseOption;
    }

    public void setDependentResponseOption(int dependentResponseOption) {
        DependentResponseOption = dependentResponseOption;
    }
}
