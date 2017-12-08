package com.coremobile.coreyhealth.genericviewandsurvey;

import java.util.List;

/**
 * Created by nitij on 20-04-2016.
 */
public class CMN_GenericModel {

    String Viewname, ButtonTitle;
    List<CMN_GenericCategoriesModel> categories;

    public String getViewname() {
        return Viewname;
    }

    public void setViewname(String viewname) {
        Viewname = viewname;
    }

    public String getButtonTitle() {
        return ButtonTitle;
    }

    public void setButtonTitle(String buttonTitle) {
        ButtonTitle = buttonTitle;
    }

    public List<CMN_GenericCategoriesModel> getCategories() {
        return categories;
    }

    public void setCategories(List<CMN_GenericCategoriesModel> categories) {
        this.categories = categories;
    }
}
