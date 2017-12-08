package com.coremobile.coreyhealth.patientreminders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.coremobile.coreyhealth.R;

import java.util.List;

/**
 * @author Nitij Katiyar
 */
public class RemindersResponseAdapter extends BaseAdapter {
    LayoutInflater inflater;
    List<ReminderResponseModel> reminders;
    Activity activity;

    public RemindersResponseAdapter(Activity activity,
                                    List<ReminderResponseModel> reminders) {
        this.reminders = reminders;
        this.activity = activity;
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
        final ReminderResponseModel reminderModel = reminders.get(position);
        if (view == null) {
            view = inflater.inflate(R.layout.reminders_listitem, null);
        }

        TextView name;

        name = (TextView) view.findViewById(R.id.reminderName);
        name.setText(reminderModel.getReminderName());

        view.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(activity,
                        PatientResponseActivityCMN.class);
                intent.putExtra("dataValue", reminders.get(position));
                intent.putExtra("typeId",reminders.get(position).getId());
                activity.startActivity(intent);

            }
        });

        return view;
    }
}
