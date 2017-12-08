package com.coremobile.coreyhealth.patientreminders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.coremobile.coreyhealth.R;
import com.coremobile.coreyhealth.imageloader.ImageLoader;

import java.util.List;

/**
 * @author Nitij Katiyar
 */
public class ReminderTypesAdapter extends BaseAdapter {
    LayoutInflater inflater;
    List<ReminderTypesModel> reminderTypes;
    Activity activity;
    String toLoad;

    public ReminderTypesAdapter(Activity activity,
                                List<ReminderTypesModel> reminderTypes, String toLoad) {
        this.reminderTypes = reminderTypes;
        this.activity = activity;
        this.toLoad = toLoad;
        this.inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return reminderTypes.size();
    }

    @Override
    public Object getItem(int arg0) {
        return reminderTypes.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final ReminderTypesModel typesModel = reminderTypes.get(position);
        if (view == null) {
            view = inflater.inflate(R.layout.remindertypes_listitem, null);
        }

        TextView name;
        view.findViewById(R.id.checkBox1).setVisibility(View.GONE);

        name = (TextView) view.findViewById(R.id.reminderTypeName);
        ImageView image = (ImageView) view.findViewById(R.id.image);
        String image_url = "" + typesModel.getImageUrl().toString().trim();
        ImageLoader imgLoader = new ImageLoader(activity);
        try {
            imgLoader.DisplayImage(image_url, R.drawable.analytic_placeholder,
                    image);
        } catch (Exception e) {
            e.getMessage();
        }
        name.setText(typesModel.getDescription());

        view.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {


                    Intent intent = new Intent(activity,
                            RemindersActivityCMN.class);
                    intent.putExtra("dataValue", reminderTypes.get(position));
                    activity.startActivity(intent);

            }
        });

        return view;
    }
}
