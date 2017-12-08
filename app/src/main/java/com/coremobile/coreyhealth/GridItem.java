package com.coremobile.coreyhealth;

import java.io.Serializable;

/**
 * Created by Aman on 7/12/2017.
 */

public class GridItem implements Serializable{

    GridItem(){

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    String title;
    String url;
    String description;
}
