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
import android.widget.TextView;

import com.coremobile.coreyhealth.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author iRESLab
 */
public class RemindersStageAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    Activity context;
    HashMap<Integer, Boolean> isSelected;
    List<ReminderStageModel> reminderStageModels;

    public RemindersStageAdapter(Activity context, List<ReminderStageModel> reminderStageModels, String data) {

        this.reminderStageModels = reminderStageModels;
        isSelected = new HashMap<Integer, Boolean>();
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (int i = 0; i < reminderStageModels.size(); i++) {
            if (reminderStageModels.get(i).getName().equalsIgnoreCase(
                    data)) {
                isSelected.put(i, true);
            }
        }

        this.context = context;
    }


    @Override
    public int getCount() {
        return reminderStageModels.size();
    }

    @Override
    public Object getItem(int arg0) {
        return reminderStageModels.get(arg0);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup arg2) {
        ReminderStageModel reminderStageModel = reminderStageModels.get(position);
        View myview = convertView;
        if (convertView == null) {
            myview = inflater.inflate(
                    R.layout.bloodpressurereminder_frequency_listitem, null);
        }

        TextView frequency;
        final CheckBox checkBox;

        frequency = (TextView) myview.findViewById(R.id.textView1);

        checkBox = (CheckBox) myview.findViewById(R.id.checkBox1);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

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

        frequency.setText(reminderStageModel.getName());

        return myview;
    }

    public List<ReminderStageModel> getData() {
        List<ReminderStageModel> models = new ArrayList<ReminderStageModel>();
        for (int i = 0; i < getCount(); i++) {
            if (isSelected.get(i) != null && isSelected.get(i) == true) {
                models.add(reminderStageModels.get(i));
            }
        }

        return models;
    }

}