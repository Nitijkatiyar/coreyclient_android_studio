package com.coremobile.coreyhealth.journal;

import java.io.Serializable;

/**
 * Created by Aman on 7/12/2017.
 */

public class JournalItem implements Serializable{

    JournalItem(){

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    String title;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    String date;
}
