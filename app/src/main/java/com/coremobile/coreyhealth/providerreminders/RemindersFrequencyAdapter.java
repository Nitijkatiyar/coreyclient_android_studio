package com.coremobile.coreyhealth.providerreminders;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.coremobile.coreyhealth.R;

import java.util.HashMap;
import java.util.List;

/**
 * @author iRESLab
 */
public class RemindersFrequencyAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    Activity context;
    HashMap<Integer, Boolean> isSelected;
    int currentIndex = -1;
    List<ReminderFrequencyModel> reminderFrequencyModels;

    public RemindersFrequencyAdapter(Activity context, List<ReminderFrequencyModel> reminderFrequencyModels, String data) {

        this.reminderFrequencyModels = reminderFrequencyModels;
        isSelected = new HashMap<Integer, Boolean>();
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (int i = 0; i < reminderFrequencyModels.size(); i++) {
            if (reminderFrequencyModels.get(i).getDisplaytext().equalsIgnoreCase(
                    data) || reminderFrequencyModels.get(i).getValue().equalsIgnoreCase(
                    data)) {
                currentIndex = i;
            }
        }

        this.context = context;
    }


    @Override
    public int getCount() {
        return reminderFrequencyModels.size();
    }

    @Override
    public Object getItem(int arg0) {
        return reminderFrequencyModels.get(arg0);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup arg2) {
        ReminderFrequencyModel reminderFrequencyModel = reminderFrequencyModels.get(position);
        View myview = convertView;
        if (convertView == null) {
            myview = inflater.inflate(
                    R.layout.bloodpressurereminder_frequency_listitem, null);
        }

        TextView frequency;
        final CheckBox checkBox;

        frequency = (TextView) myview.findViewById(R.id.textView1);

        checkBox = (CheckBox) myview.findViewById(R.id.checkBox1);
        checkBox.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                currentIndex = position;

                notifyDataSetChanged();
            }
        });

        myview.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                currentIndex = position;

                notifyDataSetChanged();

            }
        });

        if (currentIndex == position) {
            isSelected.put(position, true);
            checkBox.setChecked(true);
        } else {
            isSelected.put(position, false);
            checkBox.setChecked(false);
        }

        frequency.setText(reminderFrequencyModel.getDisplaytext());

        return myview;
    }

    public ReminderFrequencyModel getData() {
        ReminderFrequencyModel reminderFrequencyModel = null;
        for (int i = 0; i < getCount(); i++) {
            if (isSelected.get(i) != null && isSelected.get(i) == true) {
                reminderFrequencyModel = reminderFrequencyModels.get(i);
            }
        }

        return reminderFrequencyModel;
    }

}