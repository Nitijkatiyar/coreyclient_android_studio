package com.coremobile.coreyhealth.messaging;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.coremobile.coreyhealth.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Nitij Katiyar
 *
 */
public class CMN_MessagePatientAdapter extends BaseAdapter {

	private ArrayList<CMN_PatientModel> items;
	private LayoutInflater inflater;
	Activity context;
	HashMap<Integer, Boolean> isSelected;

	public CMN_MessagePatientAdapter(Activity context,
									 ArrayList<CMN_PatientModel> strings) {

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
		final CMN_PatientModel item = items.get(position);
		View myview = convertView;
		if (convertView == null) {

			myview = inflater.inflate(
					R.layout.bloodpressurereminder_frequency_listitem, null);
		}

		TextView frequency;
		final CheckBox checkBox;

		frequency = (TextView) myview.findViewById(R.id.textView1);
		checkBox = (CheckBox) myview.findViewById(R.id.checkBox1);
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				isSelected.put(position, isChecked);
				checkBox.setChecked(isChecked);

			}
		});

		myview.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (checkBox.isChecked()) {
					checkBox.setChecked(false);
				} else {
					checkBox.setChecked(true);
				}
				isSelected.put(position, checkBox.isChecked());
				// checkBox.setChecked(checkBox.isChecked());

				notifyDataSetChanged();

			}
		});

		if (isSelected.get(position) == null
				|| isSelected.get(position) == false) {
			checkBox.setChecked(false);
		} else {
			checkBox.setChecked(true);
		}
		frequency.setText(item.getPatientName());

		return myview;
	}

	public ArrayList<CMN_PatientModel> getData() {
		ArrayList<CMN_PatientModel> data = new ArrayList<CMN_PatientModel>();
		for (int i = 0; i < getCount(); i++) {
			if (isSelected.get(i) != null && isSelected.get(i) == true) {
				data.add(items.get(i));
			}
		}
		return data;
	}

}
