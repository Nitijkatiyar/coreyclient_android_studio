package com.coremobile.coreyhealth.analytics;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.coremobile.coreyhealth.R;
import com.coremobile.coreyhealth.imageloader.ImageLoader;

import java.util.List;

/**
 * @author Nitij Katiyar
 * 
 */
public class CMN_AnalyticsAdapter extends BaseAdapter {
	LayoutInflater inflater;
	List<CMN_AnalyticGraphModel> messages;
	Activity activity;

	public CMN_AnalyticsAdapter(Activity activity,
								List<CMN_AnalyticGraphModel> messages) {
		this.messages = messages;
		this.activity = activity;
		this.inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	@Override
	public int getCount() {
		return messages.size();
	}

	@Override
	public CMN_AnalyticGraphModel getItem(int arg0) {
		return messages.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		final CMN_AnalyticGraphModel messagesItem = messages.get(position);
		if (view == null) {
			view = inflater.inflate(R.layout.analytics_listitem, null);
		}

		TextView name, subscript;
		ImageView image;

		name = (TextView) view.findViewById(R.id.analyticsName);
		// subscript = (TextView) view.findViewById(R.id.analyticsScript);
		image = (ImageView) view.findViewById(R.id.analyticsimage);

		name.setText(messagesItem.getDisplayText());
		// subscript.setText(messagesItem.getDisplaySubscript());

		String image_url = "" + messagesItem.getImageUrl().toString().trim();
		ImageLoader imgLoader = new ImageLoader(activity);
		try {
			imgLoader.DisplayImage(image_url, R.drawable.analytic_placeholder,
					image);
		} catch (Exception e) {
			//e.getMessage();
		}

		return view;
	}
}
