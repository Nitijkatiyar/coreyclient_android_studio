package com.coremobile.coreyhealth.analytics;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.coremobile.coreyhealth.R;

import java.util.List;

/**
 * @author Nitij Katiyar
 */
public class CMN_HorizontalBarGraphAdapter extends BaseAdapter {

    List<CMN_GraphDataRowValuesModel> items;
    private LayoutInflater inflater;
    Context context;
    boolean shouldvisible;

    public CMN_HorizontalBarGraphAdapter(Context conxt,
                                         List<CMN_GraphDataRowValuesModel> item, boolean shouldvisible) {

        this.items = item;
        this.context = conxt;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.shouldvisible = shouldvisible;

    }

    @Override
    public int getCount() {
        //Log.e("count", "" + items.size());
        return items.size();

    }

    @Override
    public Object getItem(int arg0) {
        return arg0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {

        CMN_GraphDataRowValuesModel item = items.get(position);
        //Log.e("graph", "horizontalItem");
        View myview = convertView;
        if (convertView == null) {

            myview = inflater.inflate(
                    R.layout.hprizontalgraph_item_listitem, null);
        }
        TextView textView = (TextView) myview.findViewById(R.id.textView_bar);
        TextView textView1 = (TextView) myview.findViewById(R.id.textView_monthname);
        textView1.setText(item.getColumn());

        if (shouldvisible) {
            textView1.setVisibility(View.VISIBLE);
        } else {
            textView1.setVisibility(View.INVISIBLE);
        }

        if (item.getValue().equalsIgnoreCase("Completed")) {
            textView.setBackgroundColor(context.getResources().getColor(android.R.color.holo_green_dark));
        } else if (item.getValue().equalsIgnoreCase("Missed")) {
            textView.setBackgroundColor(context.getResources().getColor(android.R.color.holo_red_dark));
        } else {
            textView.setBackgroundColor(Color.TRANSPARENT);
        }

        return myview;
    }
}