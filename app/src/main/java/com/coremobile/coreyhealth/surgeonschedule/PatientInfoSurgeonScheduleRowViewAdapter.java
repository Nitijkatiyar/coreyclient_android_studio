package com.coremobile.coreyhealth.surgeonschedule;

import android.content.Context;
import android.text.Html;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.coremobile.coreyhealth.R;
import com.coremobile.coreyhealth.scheduling.schedulingmodels.LookupModel;

import java.util.ArrayList;

public class PatientInfoSurgeonScheduleRowViewAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<LookupModel> objects;
    private String[] mKeys;
  public SparseBooleanArray mCheckStates;
    public PatientInfoSurgeonScheduleRowViewAdapter(Context context, ArrayList<LookupModel> objects) {
        this.context = context;
        this.objects = objects;
        mCheckStates = new SparseBooleanArray(objects.size());
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public LookupModel getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        final View result;
        if(convertView==null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.patient_info_row_layout, parent, false);
            holder.titleText = (TextView) convertView.findViewById(R.id.nameTextVw);
            holder.dateText = (TextView) convertView.findViewById(R.id.DOSTextVw);
            holder.surgeonText=(TextView)convertView.findViewById(R.id.surgeonTextVw);
            holder.dobText=(TextView)convertView.findViewById(R.id.dobTextVw);


            holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkVw);

            holder.checkBox.setVisibility(View.GONE);

            result=convertView;
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
            result=convertView;
        }
/*

        holder.checkBox.setTag(R.integer.btnplusview, convertView);
        holder.checkBox.setTag( position);
        holder.checkBox.setTag(position);
        holder.checkBox.setChecked(mCheckStates.get(position, false));
*/

/*
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCheckStates.put((Integer) buttonView.getTag(), isChecked);

                View tempview = (View) holder.checkBox.getTag(R.integer.btnplusview);
                Integer pos = (Integer)  holder.checkBox.getTag();
                Toast.makeText(context, "Checkbox "+pos+" clicked!", Toast.LENGTH_SHORT).show();

                if(objects.get(pos).getSelected()){
                    objects.get(pos).setSelected(false);
                }else {
                    objects.get(pos).setSelected(true);
                }


            }
        });
*/
        /*holder.titleText.setText(Html.fromHtml("<b>Name:</b>")+getItem(position).getDisplayName());
        holder.dateText.setText(Html.fromHtml("<b>DOS:</b>")+getItem(position).getDateOfSurery());
        holder.surgeonText.setText(Html.fromHtml("<b>Surgeon:</b>")+getItem(position).getSurgeon());
        holder.dobText.setText(Html.fromHtml("<b>DOB:</b>")+getItem(position).getAnonId());*/

        holder.titleText.setText(Html.fromHtml("<b>Name:</b>"));
        holder.dateText.setText(Html.fromHtml("<b>DOS:</b>"));
        holder.surgeonText.setText(Html.fromHtml("<b>Surgeon:</b>"));
        holder.dobText.setText(Html.fromHtml("<b>DOB:</b>"));

        return result;
    }

    public boolean isChecked(int position) {
        return mCheckStates.get(position, false);
    }

    public void setChecked(int position, boolean isChecked) {
        mCheckStates.put(position, isChecked);

    }

    public void toggle(int position) {
        setChecked(position, !isChecked(position));

    }
    class ViewHolder {
        TextView titleText;
        TextView dateText;
        TextView surgeonText;

        TextView dobText;
        CheckBox checkBox;

    }
}