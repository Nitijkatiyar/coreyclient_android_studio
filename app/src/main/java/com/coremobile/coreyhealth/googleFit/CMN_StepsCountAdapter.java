package com.coremobile.coreyhealth.googleFit;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.coremobile.coreyhealth.R;

import java.util.ArrayList;

/**
 * @author Nitij Katiyar
 */
public class CMN_StepsCountAdapter extends BaseAdapter {

    private ArrayList<CMN_StepsModel> items;
    private LayoutInflater inflater;
    Activity context;

    public CMN_StepsCountAdapter(Activity context,
                                 ArrayList<CMN_StepsModel> strings) {

        this.items = strings;

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.context = context;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int arg0) {
        return items.get(arg0);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup arg2) {
        final CMN_StepsModel item = items.get(position);
        View myview = convertView;
        if (convertView == null) {

            myview = inflater.inflate(
                    R.layout.stepscountitem, null);
        }

        TextView stepsCount, stepsDate;

        stepsCount = (TextView) myview.findViewById(R.id.stepscount);
        stepsDate = (TextView) myview.findViewById(R.id.stepsdate);

        stepsCount.setText(item.getStepsCount()+" Steps");
        stepsDate.setText(item.getStartDate());
        return myview;
    }


}
