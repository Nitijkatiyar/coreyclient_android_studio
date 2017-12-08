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
import java.util.List;

/**
 * @author Nitij Katiyar
 */
public class CRNAToOrPreviousAssignmentAdapter extends BaseAdapter {

    private ArrayList<CRNAtoORPreviousAssignmentModel> items;
    private LayoutInflater inflater;
    Activity context;
    HashMap<Integer, Boolean> isSelected;


    public CRNAToOrPreviousAssignmentAdapter(Activity context,
                                             ArrayList<CRNAtoORPreviousAssignmentModel> strings) {

        this.items = strings;

        isSelected = new HashMap<Integer, Boolean>();
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
        final CRNAtoORPreviousAssignmentModel item = items.get(position);
        if (convertView == null) {
            convertView = inflater.inflate(
                    R.layout.assignmnt_crna_to_or_listitem, null);
        }
        TextView orName, roleName;
        final CheckBox checkBox;

        orName = (TextView) convertView.findViewById(R.id.orName);
        roleName = (TextView) convertView.findViewById(R.id.roleName);
        checkBox = (CheckBox) convertView.findViewById(R.id.checkbox);

        if (item.getOrName().isEmpty() || item.getOrName().length() == 0) {
            orName.setText("OR not selected");
        } else {
            orName.setText(item.getOrName());
        }
        roleName.setText(item.getProviderName());


        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                isSelected.put(position, isChecked);
                checkBox.setChecked(isChecked);

            }
        });
        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()) {
                    checkBox.setChecked(false);
                } else {
                    checkBox.setChecked(true);
                }
                isSelected.put(position, checkBox.isChecked());
                notifyDataSetChanged();

            }
        });

        if (isSelected.get(position) == null
                || isSelected.get(position) == false) {
            checkBox.setChecked(false);
        } else {
            checkBox.setChecked(true);
        }

        return convertView;
    }

    public List<CRNAtoORPreviousAssignmentModel> getData() {
        List<CRNAtoORPreviousAssignmentModel> models = new ArrayList<CRNAtoORPreviousAssignmentModel>();
        for (int i = 0; i < getCount(); i++) {
            if (isSelected.get(i) != null && isSelected.get(i) == true) {
                models.add(items.get(i));
            }
        }

        return models;
    }

}
