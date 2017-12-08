package com.coremobile.coreyhealth.providerreminders;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.coremobile.coreyhealth.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author iRESLab
 */
public class RemindersTimesListViewAdapter extends BaseAdapter {

    private String[] items;
    private LayoutInflater inflater;
    Activity context;
    HashMap<Integer, Boolean> isSelected;
    String[] mTimesStringArray;


    public RemindersTimesListViewAdapter(Activity context, String[] strings,
                                         String[] timesStringArray) {
        this.items = strings;
        isSelected = new HashMap<Integer, Boolean>();

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mTimesStringArray = timesStringArray;

        if (mTimesStringArray != null) {

            for (int i = 0; i < strings.length; i++) {
                for (int j = 0; j < timesStringArray.length; j++) {

                    if (strings[i].trim().equalsIgnoreCase(
                            timesStringArray[j].trim())) {
                        isSelected.put(i, true);

                    }

                }

            }
        }
    }

    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public Object getItem(int arg0) {
        return items[arg0];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup arg2) {
        final String item = items[position];
        View myview = convertView;
        if (convertView == null) {

            myview = inflater.inflate(
                    R.layout.bloodpressurereminder_frequency_listitem, null);
        }

        TextView frequency, delete;
        final CheckBox checkBox;

        frequency = (TextView) myview.findViewById(R.id.textView1);

        checkBox = (CheckBox) myview.findViewById(R.id.checkBox1);
        checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                isSelected.put(position, isChecked);
                checkBox.setChecked(isChecked);

            }
        });
        myview.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (checkBox.isChecked()) {
                    checkBox.setChecked(false);
                } else {
                    checkBox.setChecked(true);
                }
                isSelected.put(position, checkBox.isChecked());
                // checkBox.setChecked(checkBox.isChecked());

                notifyDataSetChanged();

            }
        });
        if (isSelected.get(position) == null
                || isSelected.get(position) == false) {
            checkBox.setChecked(false);
        } else {
            checkBox.setChecked(true);
        }

        frequency.setText(item);

        return myview;
    }

    public String[] getData() {
        ArrayList<String> data = new ArrayList<String>();
        for (int i = 0; i < getCount(); i++) {
            if (isSelected.get(i) != null && isSelected.get(i) == true) {
                data.add(items[i]);
            }
        }

        return data.toArray(new String[data.size()]);
    }


}