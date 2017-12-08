package com.coremobile.coreyhealth.nativeassignment;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.coremobile.coreyhealth.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Nitij Katiyar
 */
public class AssignmentSpecialityToOrAdapter extends BaseAdapter {

    private ArrayList<SpecialityOrAssignmentModel> items;
    private LayoutInflater inflater;
    Activity context;
    ArrayList<OrUsersModel> orUsersModels;
    ArrayList<String> orNames;
    public HashMap<Integer, Integer> selectedProviders;
    ViewHolder viewHolder;


    public AssignmentSpecialityToOrAdapter(Activity context,
                                           ArrayList<SpecialityOrAssignmentModel> strings, ArrayList<OrUsersModel> usersModels) {

        this.items = strings;
        this.orUsersModels = usersModels;
        orNames = new ArrayList<>();
        for (int i = 0; i < usersModels.size(); i++) {
            orNames.add(usersModels.get(i).getName());
        }
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
        final SpecialityOrAssignmentModel item = items.get(position);
        if (convertView == null) {

            convertView = inflater.inflate(
                    R.layout.assign_patient_to_or_listitem, null);

            viewHolder = new ViewHolder();
            viewHolder.patientName = (TextView) convertView.findViewById(R.id.providerName);
            viewHolder.hDeptName = (TextView) convertView.findViewById(R.id.hDeptName);
            viewHolder.surgeryTime = (TextView) convertView.findViewById(R.id.surgeryTime);
            viewHolder.roles = (Spinner) convertView.findViewById(R.id.selectRole);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        ArrayAdapter _patientNamesAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item, orNames);
        _patientNamesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        viewHolder.roles.setAdapter(_patientNamesAdapter);

        viewHolder.roles.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                       @Override
                                                       public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                                                           selectedProviders.put(position, pos);
                                                       }

                                                       @Override
                                                       public void onNothingSelected(AdapterView<?> parent) {

                                                       }
                                                   }

        );

        viewHolder.patientName.setText(item.getSpecialityName());
        viewHolder.hDeptName.setText(item.getORName());
        viewHolder.surgeryTime.setVisibility(View.GONE);

        if (selectedProviders.get(position) != null && selectedProviders.get(position) > 0) {
            viewHolder.roles.setSelection(selectedProviders.get(position));
        } else {
            viewHolder.roles.setSelection(0);
        }


        return convertView;
    }


    static class ViewHolder {
        TextView patientName, hDeptName, surgeryTime;
        Spinner roles;
    }
}
