package com.coremobile.coreyhealth.providerreminders;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.coremobile.coreyhealth.R;
import com.coremobile.coreyhealth.imageloader.ImageLoader;

import java.util.HashMap;
import java.util.List;

/**
 * @author iRESLab
 */
public class RemindersTypesListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    Activity context;
    HashMap<Integer, Boolean> isSelected;
    int currentIndex = -1;
    List<ReminderTypesModel> reminderTypesModels;

    public RemindersTypesListAdapter(Activity context, List<ReminderTypesModel> reminderTypesModels, String data) {

        this.reminderTypesModels = reminderTypesModels;
        isSelected = new HashMap<Integer, Boolean>();
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        for (int i = 0; i < reminderTypesModels.size(); i++) {
            if (reminderTypesModels.get(i).getName().trim()
                    .equalsIgnoreCase(data.trim()) || String.valueOf(reminderTypesModels.get(i).getId()).trim()
                    .equalsIgnoreCase(data.trim())) {
                currentIndex = i;
                break;
            }
        }


        this.context = context;
    }


    @Override
    public int getCount() {
        return reminderTypesModels.size();
    }

    @Override
    public Object getItem(int arg0) {
        return reminderTypesModels.get(arg0);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup arg2) {
        ReminderTypesModel reminderTypesModel = reminderTypesModels.get(position);
        View myview = convertView;
        if (convertView == null) {
            myview = inflater.inflate(
                    R.layout.remindertypes_listitem, null);
        }


        final CheckBox checkBox;

        checkBox = (CheckBox) myview.findViewById(R.id.checkBox1);
        checkBox.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                currentIndex = position;

                notifyDataSetChanged();
            }
        });

        myview.findViewById(R.id.arrowright).setVisibility(View.GONE);

        TextView name = (TextView) myview.findViewById(R.id.reminderTypeName);
        ImageView image = (ImageView) myview.findViewById(R.id.image);
        String image_url = "" + reminderTypesModel.getImage().toString().trim();
        ImageLoader imgLoader = new ImageLoader(context);
        try {
            imgLoader.DisplayImage(image_url, R.drawable.analytic_placeholder,
                    image);
        } catch (Exception e) {
            e.getMessage();
        }
        name.setText(reminderTypesModel.getDescription());


        myview.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                currentIndex = position;

                notifyDataSetChanged();

            }
        });


        if (currentIndex == position) {
            isSelected.put(position, true);
            checkBox.setChecked(true);
        } else {
            isSelected.put(position, false);
            checkBox.setChecked(false);
        }

        name.setText(reminderTypesModel.getName());

        return myview;
    }

    public ReminderTypesModel getData() {
        ReminderTypesModel reminderTypesModel = null;
        for (int i = 0; i < getCount(); i++) {
            if (isSelected.get(i) != null && isSelected.get(i) == true) {
                reminderTypesModel = reminderTypesModels.get(i);
            }
        }

        return reminderTypesModel;
    }

}