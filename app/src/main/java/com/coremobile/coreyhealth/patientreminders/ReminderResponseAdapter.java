package com.coremobile.coreyhealth.patientreminders;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.coremobile.coreyhealth.R;

import java.util.ArrayList;

/**
 * @author Nitij Katiyar
 */
public class ReminderResponseAdapter extends BaseAdapter {
    LayoutInflater inflater;
    ArrayList<ReminderResponseModel> responses;
    Activity activity;

    public ReminderResponseAdapter(Activity activity,
                                   ArrayList<ReminderResponseModel> responses) {
        this.responses = responses;
        this.activity = activity;
        this.inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return responses.size();
    }

    @Override
    public Object getItem(int arg0) {
        return responses.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final ReminderResponseModel responseModel = responses.get(position);
        if (view == null) {
            view = inflater.inflate(R.layout.remindersresponse_listitem, null);
        }

        TextView name, date;
        ImageView responseYes, responseNo;
        name = (TextView) view.findViewById(R.id.reminderName);
        date = (TextView) view.findViewById(R.id.reminderDate);
        responseYes = (ImageView) view.findViewById(R.id.reminderResponseYes);
        responseNo = (ImageView) view.findViewById(R.id.reminderResponseNo);

        name.setText(responses.get(position).getReminderName());
        date.setText("Datetime: " + responses.get(position).getDate());
        if (responses.get(position).getResponse().equalsIgnoreCase("yes")) {
            responseYes.setVisibility(View.VISIBLE);
            responseNo.setVisibility(View.INVISIBLE);
        } else {
            responseYes.setVisibility(View.INVISIBLE);
            responseNo.setVisibility(View.VISIBLE);
        }


        return view;
    }
}
