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
import java.util.List;

/**
 * @author Nitij Katiyar
 */
public class AssignNursetoORAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    Activity context;
    public HashMap<String, Integer> selectedRolePosition;
    ViewHolder viewHolder;
    ArrayList<AllProvidersModel> providers;
    ArrayList<NurseToOrMergedModel> mergedModels;
    HashMap<Integer, Integer> selectedPosition;
    HashMap<Integer, ArrayList<AllProvidersModel>> providersAsPerHospitalDepartment = new HashMap<>();

    public AssignNursetoORAdapter(Activity context,
                                  ArrayList<AllProvidersModel> assignmentProvidersModels, ArrayList<NurseToOrMergedModel> mergedModels) {

        selectedPosition = new HashMap<>();
        this.mergedModels = mergedModels;
        selectedRolePosition = new HashMap<>();
        providers = assignmentProvidersModels;


        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
    }

    @Override
    public int getCount() {
        return mergedModels.size();
    }

    @Override
    public Object getItem(int arg0) {
        return mergedModels.get(arg0);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup arg2) {
        final NurseToOrMergedModel item = mergedModels.get(position);
        if (convertView == null) {

            convertView = inflater.inflate(
                    R.layout.assign_nurse_to_or_listitem, null);

            viewHolder = new ViewHolder();
            viewHolder.patientName = (TextView) convertView.findViewById(R.id.providerName);
            viewHolder.hDeptName = (TextView) convertView.findViewById(R.id.hDeptName);
            viewHolder.roles = (Spinner) convertView.findViewById(R.id.selectRole);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ArrayList<String> assignmentProviders = new ArrayList<>();
        assignmentProviders.add("Select new staff");
        ArrayList<AllProvidersModel> models = new ArrayList<>();

        for (int i = 0; i < providers.size(); i++) {
            List<String> categories = mergedModels.get(position).getRoleCategory();

            for (int j = 0; j < categories.size(); j++) {
                if (categories.get(j).equalsIgnoreCase("" + providers.get(i).getRoleCategory())) {
                    assignmentProviders.add(providers.get(i).getProviderName());
                    models.add(providers.get(i));
                }
            }
            providersAsPerHospitalDepartment.put(position, models);
        }


        ArrayAdapter _patientNamesAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item, assignmentProviders);
        _patientNamesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        viewHolder.roles.setAdapter(_patientNamesAdapter);


        viewHolder.roles.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                       @Override
                                                       public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                                                           selectedPosition.put(position, pos);

//                                                           NurseToOrMergedModel nurseToOrMergedModel = new NurseToOrMergedModel();
//                                                           nurseToOrMergedModel.setHdeptId(item.getHdeptId());
//                                                           nurseToOrMergedModel.setHdeptName(item.getHdeptName());
//                                                           nurseToOrMergedModel.setAssignedPerOR(item.isAssignedPerOR());
//                                                           nurseToOrMergedModel.setAssignedPerOrg(item.isAssignedPerOrg());
//                                                           nurseToOrMergedModel.setAssignedPerPatient(item.isAssignedPerPatient());
//                                                           nurseToOrMergedModel.sethDeptId(item.gethDeptId());
//
//                                                           nurseToOrMergedModel.setAssignmentProviderId(providers.get(pos).getProviderId());
//                                                           nurseToOrMergedModel.setAssignmentProviderName(providers.get(pos).getProviderName());
//                                                           nurseToOrMergedModel.sethDeptId(item.gethDeptId());
//                                                           nurseToOrMergedModel.sethDeptName(item.gethDeptName());
//                                                           nurseToOrMergedModel.sethDeptNameToShow(item.gethDeptNameToShow());
//                                                           nurseToOrMergedModel.setORId(item.getORId());
//                                                           nurseToOrMergedModel.setEdited(true);
//                                                           mergedModels.remove(position);
//                                                           mergedModels.add(position, nurseToOrMergedModel);
//
//                                                           notifyDataSetChanged();
                                                       }


                                                       @Override
                                                       public void onNothingSelected(AdapterView<?> parent) {
                                                       }
                                                   }

        );

        viewHolder.patientName.setText(mergedModels.get(position).getHdeptName());
        viewHolder.hDeptName.setText(mergedModels.get(position).gethDeptNameToShow());

        if (selectedPosition.get(position) != null
                && selectedPosition.get(position) > 0) {
            viewHolder.roles.setSelection(selectedPosition.get(position));
        } else {
            viewHolder.roles.setSelection(0);
        }

        return convertView;
    }


    static class ViewHolder {
        TextView patientName, hDeptName;
        Spinner roles;
    }
}
