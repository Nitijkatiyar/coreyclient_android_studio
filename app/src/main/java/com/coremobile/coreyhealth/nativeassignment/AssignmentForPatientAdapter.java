package com.coremobile.coreyhealth.nativeassignment;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.coremobile.coreyhealth.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Nitij Katiyar
 */
public class AssignmentForPatientAdapter extends BaseAdapter {

    private ArrayList<ProviderAssignmentPatientMergedModel> items;
    private LayoutInflater inflater;
    Activity context;
    ArrayList<AssignmentHospitalsModel> assignmentRolesModels;
    public HashMap<Integer, Integer> selectedRoles;
    public HashMap<Integer, Boolean> selectedLeads;
    HashMap<Integer, ArrayList<AssignmentHospitalsModel>> providersAsPerHospitalDepartment = new HashMap<>();


    public AssignmentForPatientAdapter(Activity context,
                                       ArrayList<ProviderAssignmentPatientMergedModel> strings, ArrayList<AssignmentHospitalsModel> assignmentrolesModels) {

        this.items = strings;
        this.assignmentRolesModels = assignmentrolesModels;

        selectedRoles = new HashMap<>();
        selectedLeads = new HashMap<>();

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
        final ProviderAssignmentPatientMergedModel item = items.get(position);
        if (convertView == null) {

            convertView = inflater.inflate(
                    R.layout.assignmnt_by_taskpatient_listitem, null);
        }
        TextView patientName, hDeptName;
        final ToggleButton isLead;
        Spinner roles;

        patientName = (TextView) convertView.findViewById(R.id.providerName);
        hDeptName = (TextView) convertView.findViewById(R.id.hDeptName);
        isLead = (ToggleButton) convertView.findViewById(R.id.isLeadToggle);
        roles = (Spinner) convertView.findViewById(R.id.selectRole);

        ArrayList<String> assignmentroles = new ArrayList<>();
        assignmentroles.add("Select new role");
        ArrayList<AssignmentHospitalsModel> models = new ArrayList<>();

        for (int i = 0; i < assignmentRolesModels.size(); i++) {
            List<String> categories = assignmentRolesModels.get(i).getRoleCategory();
            if (categories != null) {

                for (int j = 0; j < categories.size(); j++) {
                    if (categories.get(j).equalsIgnoreCase("" + items.get(position).getRoleCategory())) {
                        assignmentroles.add(assignmentRolesModels.get(i).getHdeptName());
                        models.add(assignmentRolesModels.get(i));
                    }
                }
                providersAsPerHospitalDepartment.put(position, models);
            }
        }


        ArrayAdapter _patientNamesAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item, assignmentroles);
        _patientNamesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roles.setAdapter(_patientNamesAdapter);


        roles.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                            @Override
                                            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                                                selectedRoles.put(position, pos);
                                            }

                                            @Override
                                            public void onNothingSelected(AdapterView<?> parent) {

                                            }
                                        }

        );
        isLead.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()

                                          {
                                              @Override
                                              public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                  selectedLeads.put(position, isLead.isChecked());
                                              }
                                          }

        );


        patientName.setText(item.getProvidername());
        hDeptName.setText(item.gethDeptNameToShow());
        isLead.setChecked(item.isLead());

        if (selectedRoles.get(position) != null && selectedRoles.get(position) > 0) {
            roles.setSelection(selectedRoles.get(position));
        } else {
            roles.setSelection(0);
        }

        if (selectedLeads.get(position) != null && selectedLeads.get(position)) {
            isLead.setChecked(true);
        } else {
            isLead.setChecked(false);
        }

        return convertView;
    }


}
