package com.coremobile.coreyhealth.patientreminders;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coremobile.coreyhealth.R;

import java.util.List;

/**
 * @author Nitij Katiyar
 */
public class RemindersAdapter extends BaseAdapter {
    LayoutInflater inflater;
    List<ReminderModel> reminders;
    Activity activity;
    String typeId;


    public RemindersAdapter(Activity activity,
                            List<ReminderModel> reminders, String typeId) {
        this.reminders = reminders;
        this.activity = activity;
        this.typeId = typeId;
        this.inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return reminders.size();
    }

    @Override
    public Object getItem(int arg0) {
        return reminders.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final ReminderModel reminderModel = reminders.get(position);
        if (view == null) {
            view = inflater.inflate(R.layout.reminders_listitem, null);
        }

        TextView name, desc, col1, col1data, col2, col2data;
        RelativeLayout parentLayout;
        Button instantButtonVw;

        parentLayout = (RelativeLayout) view.findViewById(R.id.parentLayout);
        name = (TextView) view.findViewById(R.id.reminderName);
        desc = (TextView) view.findViewById(R.id.reminderDesc);
        col1 = (TextView) view.findViewById(R.id.reminderCol1);
        col1data = (TextView) view.findViewById(R.id.reminderCol1Data);
        col2 = (TextView) view.findViewById(R.id.reminderCol2);
        col2data = (TextView) view.findViewById(R.id.reminderCol2Data);
        instantButtonVw = (Button) view.findViewById(R.id.intsantButtonReminders);
        instantButtonVw.setVisibility(View.GONE);

        name.setText(reminderModel.getTitleData());
        desc.setText(reminderModel.getMsgToPatientData());
        col1.setText(reminderModel.getFrequanyColHeading() + ":");
        col1data.setText(reminderModel.getFrequencyData());
        col2.setText(reminderModel.getTimesColHeading() + ":");
        col2data.setText(reminderModel.getTimesData());


//        parentLayout.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//
//
//                Intent intent = new Intent(activity,
//                        PatientResponseActivityCMN.class);
//                intent.putExtra("dataValue", reminders.get(position));
//                intent.putExtra("typeId", typeId);
//                activity.startActivity(intent);
//
//            }
//        });

        instantButtonVw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                RemindersActivityCMN.invokeActivityMethod(reminderModel);
            }
        });

        return view;
    }

}
