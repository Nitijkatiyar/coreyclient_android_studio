package com.coremobile.coreyhealth.newui;

import android.view.Menu;

public interface IContextMenuItem {
    int getId();
    void addToMenu(Menu menu);
    void onClick();
}
