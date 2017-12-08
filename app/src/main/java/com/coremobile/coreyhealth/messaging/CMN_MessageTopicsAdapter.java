package com.coremobile.coreyhealth.messaging;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.coremobile.coreyhealth.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Nitij Katiyar
 *
 */
public class CMN_MessageTopicsAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	Activity context;
	HashMap<Integer, Boolean> isSelected;
	ArrayList<CMN_MessageTopicsModel> mTopicsList;
	CheckBox checkBox;
	int currentIndex = -1;

	public CMN_MessageTopicsAdapter(Activity context,
									ArrayList<CMN_MessageTopicsModel> frequencyList) {

		mTopicsList = frequencyList;
		isSelected = new HashMap<Integer, Boolean>();

		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		this.context = context;
	}

	@Override
	public int getCount() {
		return mTopicsList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mTopicsList.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup arg2) {
		final CMN_MessageTopicsModel item = mTopicsList.get(position);
		View myview = convertView;
		if (convertView == null) {

			myview = inflater.inflate(
					R.layout.bloodpressurereminder_frequency_listitem, null);
		}

		TextView frequency;

		frequency = (TextView) myview.findViewById(R.id.textView1);
		checkBox = (CheckBox) myview.findViewById(R.id.checkBox1);
		checkBox.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				currentIndex = position;

				notifyDataSetChanged();
			}
		});

		myview.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				currentIndex = position;

				notifyDataSetChanged();

			}
		});

		/*
		 * checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		 * 
		 * @Override public void onCheckedChanged(CompoundButton buttonView,
		 * boolean isChecked) {
		 * 
		 * isSelected.put(position, isChecked); checkBox.setChecked(isChecked);
		 * 
		 * } });
		 */

		/*
		 * if (isSelected.get(position) == null || isSelected.get(position) ==
		 * false) { checkBox.setChecked(false); } else {
		 * checkBox.setChecked(true); }
		 */

		if (currentIndex == position) {
			isSelected.put(position, true);
			checkBox.setChecked(true);
		} else {
			isSelected.put(position, false);
			checkBox.setChecked(false);
		}

		frequency.setText(item.getTopicName());
		return myview;
	}

	public CMN_MessageTopicsModel getData() {
		CMN_MessageTopicsModel data = new CMN_MessageTopicsModel();
		for (int i = 0; i < getCount(); i++) {
			if (isSelected.get(i) != null && isSelected.get(i) == true) {
				data = mTopicsList.get(i);
			}
		}

		return data;
	}
}