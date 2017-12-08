package com.coremobile.coreyhealth.nativeassignment;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.coremobile.coreyhealth.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Nitij Katiyar
 */
public class AssignmentTypesAdapter extends BaseAdapter {

    private ArrayList<AssignmentsTypeModel> items;
    private LayoutInflater inflater;
    Activity context;
    ViewHolder viewHolder;


    public AssignmentTypesAdapter(Activity context,
                                  List<AssignmentsTypeModel> strings) {

        this.items = (ArrayList<AssignmentsTypeModel>) strings;
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
        final AssignmentsTypeModel item = items.get(position);
        if (convertView == null) {

            convertView = inflater.inflate(
                    R.layout.assignmenttype_listitem, null);

            viewHolder = new ViewHolder();
            viewHolder.assignmentName = (TextView) convertView.findViewById(R.id.assignmentName);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        viewHolder.assignmentName.setText(item.getName());


        return convertView;
    }


    static class ViewHolder {
        TextView assignmentName;
    }
}
