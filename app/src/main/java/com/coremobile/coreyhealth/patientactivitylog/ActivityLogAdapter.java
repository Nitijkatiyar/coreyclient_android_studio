package com.coremobile.coreyhealth.patientactivitylog;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.toolkit.LongPressAwareTableDataAdapter;

/**
 * Created by Nitij on 1/21/2017.
 */

public class ActivityLogAdapter extends LongPressAwareTableDataAdapter<ActivityModel> {
    List<ActivityModel> list;
    Activity activity;
    LayoutInflater inflater;

    private static final int TEXT_SIZE = 14;

    public ActivityLogAdapter(final Context context, List<ActivityModel> data,final TableView<ActivityModel> tableView) {
        super(context, data, tableView);

    }


   /* @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View view = convertView;
        final ActivityModel activityModel = list.get(position);
        if (view == null) {
            view = inflater.inflate(R.layout.patient_activity_log_listitem, null);
        }

        TextView patientName, DoS, lastLogin, firstInvite, reminders;

        patientName = (TextView) view.findViewById(R.id.patientlog_patName);
        DoS = (TextView) view.findViewById(R.id.patientlog_dos);
        lastLogin = (TextView) view.findViewById(R.id.patientlog_lastLogin);
        firstInvite = (TextView) view.findViewById(R.id.patientlog_firstInvite);
        reminders = (TextView) view.findViewById(R.id.patientlog_InviteReminders);

        patientName.setText(activityModel.getPatientName());
        if (activityModel.getDosToConvert()) {
            DoS.setText(convertDate(activityModel.getDos()));
        } else {
            DoS.setText(activityModel.getDos());
        }
        if (activityModel.getLastLoginToConvert()) {
            lastLogin.setText(convertDate(activityModel.getLastLogin()));
        } else {
            lastLogin.setText(activityModel.getLastLogin());
        }
        if (activityModel.getFirstInvitationToConvert()) {
            firstInvite.setText(convertDate(activityModel.getFirstInvitation()));
        } else {
            firstInvite.setText(activityModel.getFirstInvitation());
        }

        if (activityModel.getInvitationRemindersToConvert()
                && activityModel.getInvitationReminders().length() > 0) {

            List<String> items = Arrays.asList(activityModel.getInvitationReminders().split("\\s*,\\s*"));
            List<String> dates = new ArrayList<>();
            for (int i = 0; i < items.size(); i++) {
                dates.add(convertDate(items.get(i)));
            }
            String reminderTimes = StringUtils.join(dates, ", ");
            reminders.setText(reminderTimes);
        } else {
            reminders.setText(activityModel.getInvitationReminders());
        }
        return view;
    }
*/
    public String convertDate(String date) {
        if(date == null || date.length()==0){
            return "";
        }
        SimpleDateFormat simpleDateFormatW3C = new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.US);
        simpleDateFormatW3C.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date dateServer = null;
        try {
            dateServer = simpleDateFormatW3C.parse(date);
        } catch (ParseException e) {
            e.getMessage();
        }

        TimeZone deviceTimeZone = TimeZone.getDefault();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        simpleDateFormat.setTimeZone(deviceTimeZone);

        String formattedDate = simpleDateFormat.format(dateServer);

        return formattedDate;
    }


    @Override
    public View getDefaultCellView(int rowIndex, int columnIndex, ViewGroup parentView) {
        final ActivityModel activityModel = getRowData(rowIndex);
        View renderedView = null;

        switch (columnIndex) {
            case 0:
                //              renderedView = renderProducerLogo(car, parentView);
                renderedView = renderPatient(activityModel);
                break;
            case 1:
                renderedView = renderLastLogin(activityModel);
                break;
            case 2:
                renderedView = renderDOSurgery(activityModel);
                break;
            case 3:
                renderedView = renderFirstInvitation(activityModel);
                break;

            case 4:
                renderedView = renderReminder(activityModel);
                break;
        }

        return renderedView;
    }

    @Override
    public View getLongPressCellView(int rowIndex, int columnIndex, ViewGroup parentView) {
        final ActivityModel car = getRowData(rowIndex);
        View renderedView = null;

        switch (columnIndex) {
            case 1:
                break;
            default:
                renderedView = getDefaultCellView(rowIndex, columnIndex, parentView);
        }

        return renderedView;
    }


    private View renderPatient(final ActivityModel car) {


       /* if (car.getPrice() < 50000) {
            textView.setTextColor(ContextCompat.getColor(getContext(), R.color.table_price_low));
        } else if (car.getPrice() > 100000) {
            textView.setTextColor(ContextCompat.getColor(getContext(), R.color.table_price_high));
        }*/

        return renderString(car.getPatientName());
    }

    private View renderFirstInvitation(final ActivityModel car) {
        /*final View view = getLayoutInflater().inflate(R.layout.table_cell_power, parentView, false);
        final TextView kwView = (TextView) view.findViewById(R.id.surgeonVw);

        kwView.setText(car.getSurgeon());
        return view;*/
        return renderString(convertDate(car.getFirstInvitation()));
    }

    private View renderDOSurgery(final ActivityModel car) {

        return renderString(convertDate(car.getDos()));
    }

    private View renderLastLogin(final ActivityModel car) {


       /* if (car.getPrice() < 50000) {
            textView.setTextColor(ContextCompat.getColor(getContext(), R.color.table_price_low));
        } else if (car.getPrice() > 100000) {
            textView.setTextColor(ContextCompat.getColor(getContext(), R.color.table_price_high));
        }*/

        return renderString(convertDate(car.getLastLogin()));
    }
    private View renderReminder(final ActivityModel activityModel) {


        if (activityModel.getInvitationRemindersToConvert()
                && activityModel.getInvitationReminders().length() > 0) {

            List<String> items = Arrays.asList(activityModel.getInvitationReminders().split("\\s*,\\s*"));
            List<String> dates = new ArrayList<>();
            for (int i = 0; i < items.size(); i++) {
                dates.add(convertDate(items.get(i)));
            }
            String reminderTimes = StringUtils.join(dates, ", ");
            return renderString(reminderTimes);
        } else {
            return renderString(activityModel.getInvitationReminders());
        }
    }
/*
    private View renderProducerLogo(final Car car, final ViewGroup parentView) {
        final View view = getLayoutInflater().inflate(R.layout.table_cell_image, parentView, false);
        final ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        imageView.setImageResource(car.getProducer().getLogo());
        return view;
    }*/

    private View renderString(final String value) {
        final TextView textView = new TextView(getContext());
        textView.setText(value);
        textView.setPadding(20, 10, 20, 10);
        textView.setTextSize(TEXT_SIZE);
        textView.setTextColor(Color.parseColor("#000000"));
        return textView;
    }




}
