package com.coremobile.coreyhealth.newui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.coremobile.coreyhealth.ContextData;
import com.coremobile.coreyhealth.R;

/**
 * Utility methods for IContextController implementations and context in
 * general.
 */
public class ContextUtils {
    // if count >= 0 it is shown, otherwise count field is not shown
    public static View getDefaultHeaderView(Context context, ContextData coreyContext,
        boolean isExpanded, View convertView, ViewGroup parent, int count, IContextAction contextAction) {
        String headerTitle = coreyContext.mDisplayText;
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.nav_list_header, null);
        }
 
        TextView headerView = (TextView) convertView
                .findViewById(R.id.title);
        headerView.setText(headerTitle);

        TextView countView = (TextView)convertView.findViewById(R.id.count);
        if (count >= 0) {
            countView.setText(Integer.toString(count));
            countView.setVisibility(View.VISIBLE);
        } else {
            countView.setVisibility(View.GONE);
        }

        ImageView actionView = (ImageView)convertView.findViewById(R.id.action);
        if (contextAction != null) {
            actionView.setImageResource(contextAction.getIconResourceId());
            actionView.setOnClickListener(contextAction);
            actionView.setVisibility(View.VISIBLE);
        } else {
            actionView.setOnClickListener(null);
            actionView.setVisibility(View.GONE);
        }
        return convertView;
    }


}
