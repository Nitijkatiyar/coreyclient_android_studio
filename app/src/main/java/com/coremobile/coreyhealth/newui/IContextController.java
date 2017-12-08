package com.coremobile.coreyhealth.newui;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;

public interface IContextController extends ListAdapter {
    View getHeaderView(boolean isExpanded, View convertView, ViewGroup parent);
    boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id);
}
