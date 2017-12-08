package com.coremobile.coreyhealth.surveylog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.coremobile.coreyhealth.R;

import java.util.List;

public class SurveyDataAdapter extends BaseAdapter {
    private Context context;
    private List<SurveyLogEntity> objects;
    public SurveyDataAdapter(Context context, List<SurveyLogEntity> objects) {
        this.context = context;
        this.objects = objects;
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public SurveyLogEntity getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView==null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.view_survey_row_layout, parent, false);
            holder.nameText = (TextView) convertView.findViewById(R.id.nameTextVw);
            holder.dateText = (TextView) convertView.findViewById(R.id.dateTextVw);
            holder.surgeonText=(TextView) convertView.findViewById(R.id.surgeonTextVw);


            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        SurveyLogEntity surveyLogEntity=getItem(position);
        holder.nameText.setText(surveyLogEntity.getName());
        holder.dateText.setText(surveyLogEntity.getDateofSurgery());
        holder.surgeonText.setText(surveyLogEntity.getSurgeon());


        return convertView;
    }

    class ViewHolder {
        TextView nameText;
        TextView dateText,surgeonText;


    }
}