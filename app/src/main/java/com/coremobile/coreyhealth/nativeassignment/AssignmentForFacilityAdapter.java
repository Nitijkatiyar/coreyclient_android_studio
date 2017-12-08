package com.coremobile.coreyhealth.nativeassignment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.coremobile.coreyhealth.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Nitij Katiyar
 */
public class AssignmentForFacilityAdapter extends BaseAdapter {

    private ArrayList<FacilityAssignmentModel> items;


    private LayoutInflater inflater;
    Activity context;
    ArrayList<AllProvidersModel> assignmentProviderModel;
    public static HashMap<Integer, List<AllProvidersModel>> selectedProviders;
    ArrayList<AssignmentHospitalsModel> assignmentRolesModels;
    HashMap<Integer, ArrayList<AllProvidersModel>> providersAsPerHospitalDepartment = new HashMap<>();
    ViewHolder viewHolder = new ViewHolder();

    public AssignmentForFacilityAdapter(Activity context,
                                        ArrayList<FacilityAssignmentModel> strings, ArrayList<AllProvidersModel> providers, ArrayList<AssignmentHospitalsModel> assignmentRolesModels) {

        this.items = strings;
        this.assignmentProviderModel = providers;
        this.assignmentRolesModels = assignmentRolesModels;

        selectedProviders = new HashMap<>();
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
    }

    @Override
    public int getCount() {
        return assignmentRolesModels.size();
    }

    @Override
    public Object getItem(int arg0) {
        return assignmentRolesModels.get(arg0);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup arg2) {
        final AssignmentHospitalsModel item = assignmentRolesModels.get(position);
        if (convertView == null) {
            convertView = inflater.inflate(
                    R.layout.assignmnt_by_facility_listitem, null);
            viewHolder = new ViewHolder();
            viewHolder.patientName = (TextView) convertView.findViewById(R.id.providerName);
            viewHolder.hDeptName = (TextView) convertView.findViewById(R.id.hDeptName);
            viewHolder.roles = (TextView) convertView.findViewById(R.id.selectRole);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        ArrayList<String> assignmentProviders = new ArrayList<>();
        assignmentProviders.add("Select new staff");
        final ArrayList<AllProvidersModel> models = new ArrayList<>();
        List<String> categories = new ArrayList<>();
//        List<String> items = Arrays.asList(item.getRoleCategory().split("\\s*,\\s*"));
//        for (int i = 0; i < assignmentRolesModels.size(); i++) {
//            if (item.getRoleCategory() == assignmentProviderModel.get(i).getRoleCategory()) {
        categories = item.getRoleCategory();
//            }
//        }
        for (int i = 0; i < assignmentProviderModel.size(); i++) {
            for (int j = 0; j < categories.size(); j++) {
                if (categories.get(j).equalsIgnoreCase("" + assignmentProviderModel.get(i).getRoleCategory())) {
                    assignmentProviders.add(assignmentProviderModel.get(i).getProviderName());
                    models.add(assignmentProviderModel.get(i));
                }
            }
            providersAsPerHospitalDepartment.put(position, models);
        }
        ArrayAdapter _patientNamesAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item, assignmentProviders);
        _patientNamesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        viewHolder.roles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, SelectFacilityProviderActivityCMN.class).putExtra("data", models).putExtra("position", position));
            }
        });
        viewHolder.patientName.setText(item.getHdeptName());
        if (selectedProviders.get(position) != null) {
            viewHolder.hDeptName.setText("" + selectedProviders.get(position).size() + " Providers selected");
        } else {
            viewHolder.hDeptName.setText("Staff not selected");
            for (int i = 0; i < items.size(); i++) {
                if (item.getHdeptName().equalsIgnoreCase(items.get(i).getHdeptname())) {
                    viewHolder.hDeptName.setText("" + items.get(i).getFacilityAssignmentProviderModels().size() + " Providers assigned");
                }
            }

        }


        return convertView;
    }

    public class ViewHolder {
        TextView patientName, hDeptName, roles;

    }

}
