package com.coremobile.coreyhealth.scheduling;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.coremobile.coreyhealth.R;
import com.coremobile.coreyhealth.scheduling.schedulingmodels.ScheduleListModel;

import java.util.List;

/**
 * Created by Nitij on 1/21/2017.
 */

public class ScheduleListAdapter extends BaseAdapter {
    List<ScheduleListModel> list;
    Activity activity;
    LayoutInflater inflater;

    public ScheduleListAdapter(Activity activity, List<ScheduleListModel> list) {
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
        final ScheduleListModel activityModel = list.get(position);
        if (view == null) {
            view = inflater.inflate(R.layout.schedule_list_listitem, null);
        }

        TextView patientName, procedure,time;
        ImageView imageView;

        patientName = (TextView) view.findViewById(R.id.patientlog_patName);
        procedure = (TextView) view.findViewById(R.id.procedure);
        time = (TextView) view.findViewById(R.id.time);
        imageView = (ImageView) view.findViewById(R.id.image);

        if(Boolean.parseBoolean(activityModel.getIsConfirmed())){
            imageView.setVisibility(View.VISIBLE);
        }else{
            imageView.setVisibility(View.GONE);
        }

        patientName.setText(activityModel.getPatient());
        procedure.setText(activityModel.getRoom()+" "+activityModel.getProcedure());
        time.setText("Start - End : "+activityModel.getStartTime().split(" ")[1]+" - "+activityModel.getEndTime().split(" ")[1]);


        return view;
    }


}
