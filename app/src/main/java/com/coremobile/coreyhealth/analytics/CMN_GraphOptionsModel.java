package com.coremobile.coreyhealth.analytics;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ireslab on 1/6/2016.
 */
public class CMN_GraphOptionsModel implements Serializable {
    String XAxisTitle;
    String YAxisTitle;
    String Title;
    String Type;
    List<String> colors;


    public List<String> getColors() {
        return colors;
    }

    public void setColors(List<String> colors) {
        this.colors = colors;
    }

    public String getXAxisTitle() {
        return XAxisTitle;
    }

    public void setXAxisTitle(String XAxisTitle) {
        this.XAxisTitle = XAxisTitle;
    }

    public String getYAxisTitle() {
        return YAxisTitle;
    }

    public void setYAxisTitle(String YAxisTitle) {
        this.YAxisTitle = YAxisTitle;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }
}
