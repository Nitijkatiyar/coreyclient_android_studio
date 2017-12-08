package com.coremobile.coreyhealth.nativeassignment;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.coremobile.coreyhealth.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Nitij Katiyar
 */
public class CNM_AdapterAssignNurseToPatient extends BaseAdapter {

    private ArrayList<PatientNurseAssignmentModel> items;
    private LayoutInflater inflater;
    Activity context;
    public HashMap<Integer, Boolean> selectedProviders;


    public CNM_AdapterAssignNurseToPatient(Activity context,
                                           ArrayList<PatientNurseAssignmentModel> strings) {
        this.items = strings;
        selectedProviders = new HashMap<>();
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int arg0) {
        return items.get(arg0);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup arg2) {
        final PatientNurseAssignmentModel item = items.get(position);
        if (convertView == null) {

            convertView = inflater.inflate(
                    R.layout.assign_nurse_to_patient_listitem, null);

        }
        TextView patientName, hDeptName, orName;
        final CheckBox checkBox;

        patientName = (TextView) convertView.findViewById(R.id.providerName);
        hDeptName = (TextView) convertView.findViewById(R.id.hDeptName);
        orName = (TextView) convertView.findViewById(R.id.orName);
        checkBox = (CheckBox) convertView.findViewById(R.id.checkbox);

        patientName.setText(item.getPatientName());
        if (!item.getProviderName().isEmpty()) {
            hDeptName.setText(item.getProviderName());
        } else {
            hDeptName.setText("Staff not selected");
        }

        orName.setText(item.getOrName());

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                selectedProviders.put(position, isChecked);
                checkBox.setChecked(isChecked);

            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (checkBox.isChecked()) {
                    checkBox.setChecked(false);
                } else {
                    checkBox.setChecked(true);
                }
                selectedProviders.put(position, checkBox.isChecked());

                notifyDataSetChanged();

            }
        });

        if (selectedProviders.get(position) == null
                || selectedProviders.get(position) == false) {
            checkBox.setChecked(false);
        } else {
            checkBox.setChecked(true);
        }

        return convertView;
    }


//    static class ViewHolder {
//
//    }

    public String getSelectedIds() {
        String ids = "";
        for (int i = 0; i < getCount(); i++) {
            if (selectedProviders.get(i) != null && selectedProviders.get(i) == true) {
                if (ids.isEmpty()) {
                    ids = "" + items.get(i).getPatientId();
                } else {
                    ids = ids + "," + items.get(i).getPatientId();
                }
            }
        }
        return ids;
    }
}
