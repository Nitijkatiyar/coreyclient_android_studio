package com.coremobile.coreyhealth.surveylog;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.coremobile.coreyhealth.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Aman.
 */

public class SurveyLogAdapter extends BaseAdapter {
    List<SurveyModel> list;
    Activity activity;
    LayoutInflater inflater;

    public SurveyLogAdapter(Activity activity, List<SurveyModel> list) {
        this.activity = activity;
        this.list = list;
        this.inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View view = convertView;
        final SurveyModel activityModel = list.get(position);
        if (view == null) {
            view = inflater.inflate(R.layout.patient_survey_log_listitem, null);
        }

        TextView patientName, DoS, dateofsurvey, viewform,surgeon;

        patientName = (TextView) view.findViewById(R.id.patientlog_patName);
        DoS = (TextView) view.findViewById(R.id.patientlog_dos);
        dateofsurvey = (TextView) view.findViewById(R.id.patientlog_lastLogin);
        viewform = (TextView) view.findViewById(R.id.patientlog_firstInvite);
        surgeon = (TextView) view.findViewById(R.id.patientlog_InviteReminders);

        patientName.setText(activityModel.getPatientName());
        if (activityModel.getDosToConvert()) {
            DoS.setText(convertDate(activityModel.getDos()));
        } else {
            DoS.setText(activityModel.getDos());
        }
        if (activityModel.getLastLoginToConvert()) {
            dateofsurvey.setText(convertDate(activityModel.getDateofsurvey()));
        } else {
            dateofsurvey.setText(activityModel.getDateofsurvey());
        }

        surgeon.setText(activityModel.getSurgeon());
        viewform.setText(activityModel.getViewForm());


        return view;
    }

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
        String formattedDate;
        if(dateServer!=null)
            formattedDate = simpleDateFormat.format(dateServer);

        else

            formattedDate="";
        return formattedDate;
    }
}
