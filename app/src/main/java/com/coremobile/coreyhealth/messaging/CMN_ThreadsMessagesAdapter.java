package com.coremobile.coreyhealth.messaging;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.coremobile.coreyhealth.R;

import java.util.List;
/**
 * @author Nitij Katiyar
 *
 */
public class CMN_ThreadsMessagesAdapter extends BaseAdapter {
	LayoutInflater inflater;
	List<CMN_MessageDataModel> messages;
	Activity activity;

	public CMN_ThreadsMessagesAdapter(Activity activity,
									  List<CMN_MessageDataModel> messages2) {
		this.messages = messages2;
		this.activity = activity;
		this.inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return messages.size();
	}

	@Override
	public Object getItem(int arg0) {
		messages.get(arg0);
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		final CMN_MessageDataModel messagesItem = messages.get(position);
		if (view == null) {
			view = inflater.inflate(R.layout.message_listitem, null);
		}

		TextView from, topic, message, time;
		ImageView messageArrow;

		from = (TextView) view.findViewById(R.id.messageFrom);
		topic = (TextView) view.findViewById(R.id.messageTopic);
		message = (TextView) view.findViewById(R.id.messageText);
		time = (TextView) view.findViewById(R.id.messageTimestamp);
		messageArrow = (ImageView) view.findViewById(R.id.messageArrow);

		messageArrow.setVisibility(View.GONE);

		from.setText(messagesItem.getFromDisplayName());
		topic.setText(messagesItem.getTopic());
		message.setText(messagesItem.getMessage());
		time.setText(messagesItem.getTimeStamp());

		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(activity,
						CMN_NeworViewMessagesActivity.class);
				intent.putExtra("dataValue", messagesItem);
				activity.startActivity(intent);
			}
		});
		if (!messagesItem.HasRead) {
			from.setTypeface(null, Typeface.BOLD);
		}

		return view;
	}
}
