package com.coremobile.coreyhealth.native_epro;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.coremobile.coreyhealth.R;

import java.util.List;

/**
 * Created by nitij on 06-01-2017.
 */
public class CMN_EproListAdapter extends BaseAdapter {
    Activity activity;
    List<CMN_eproModel> eproModels;
    LayoutInflater layoutInflater;
    Context mContext;

    public CMN_EproListAdapter(Activity activity, List<CMN_eproModel> eproModels) {
        this.activity = activity;
        this.eproModels = eproModels;
        layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = this.activity.getApplicationContext();

    }

    @Override
    public int getCount() {
        return eproModels.size();
    }

    @Override
    public Object getItem(int position) {
        return eproModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        CMN_eproModel model = eproModels.get(position);

        if (view == null) {
            view = layoutInflater.inflate(R.layout.epros_listitem, null);
        }
        TextView question, options, frequency, times;
        Button instantButtonVw;

        question = (TextView) view.findViewById(R.id.epro_question);
        options = (TextView) view.findViewById(R.id.epro_options);
        frequency = (TextView) view.findViewById(R.id.epro_frequency);
        times = (TextView) view.findViewById(R.id.epro_time);

        instantButtonVw = (Button) view.findViewById(R.id.intsantButtonVw);

        question.setText(model.getQuestion());
        options.setText("Options : " + model.getOptions().toString().replaceAll("[\\s\\[\\]]", ""));
        frequency.setText("Frequency : " + model.getReminderFreqDisplayName());
        times.setText("Time of event : " + model.getTimeOfEvent());


        instantButtonVw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                CMN_EproListActivity.invokeActivityMethod(position);
            }
        });
        return view;
    }
}