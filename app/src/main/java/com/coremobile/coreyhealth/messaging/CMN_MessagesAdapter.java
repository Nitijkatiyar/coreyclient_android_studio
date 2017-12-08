package com.coremobile.coreyhealth.messaging;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.coremobile.coreyhealth.R;
import com.coremobile.coreyhealth.Utils;

import java.util.List;

/**
 * @author Nitij Katiyar
 * 
 */
public class CMN_MessagesAdapter extends BaseAdapter {
	LayoutInflater inflater;
	List<CMN_MessagesModel> messages;
	FragmentActivity activity;

	public CMN_MessagesAdapter(FragmentActivity activity,
							   List<CMN_MessagesModel> messages) {
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
		final CMN_MessagesModel messagesItem = messages.get(position);
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

		if (messagesItem.getMessages().size() > 1) {
			messageArrow.setVisibility(View.VISIBLE);
			boolean allRead = false;
			for (int i = 0; i < messagesItem.getMessages().size(); i++) {
				if (messagesItem.getMessages().get(i).HasRead) {
					allRead = true;
				} else {
					allRead = false;
					break;
				}

			}
			if (!allRead) {
				from.setTypeface(null, Typeface.BOLD);
			}
		} else {
			messageArrow.setVisibility(View.GONE);
			if (!messagesItem.getMessages().get(0).HasRead) {
				from.setTypeface(null, Typeface.BOLD);
			}
		}
		from.setText(messagesItem.getMessages().get(0).getFromDisplayName());
		topic.setText(messagesItem.getMessages().get(0).getTopic());
		message.setText(messagesItem.getMessages().get(0).getMessage());
		time.setText(Utils.converttimeutc2local(messagesItem.getMessages()
				.get(0).getTimeStamp()));

		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (messagesItem.getMessages().size() > 1) {
					CMN_ThreadsMessagesFragment fragment = new CMN_ThreadsMessagesFragment(
							messagesItem.getMessages());
					FragmentManager fragmentManager = activity
							.getSupportFragmentManager();
					FragmentTransaction transaction = fragmentManager
							.beginTransaction();

					transaction.replace(R.id.container, fragment);
					transaction.addToBackStack(null);
					transaction.commitAllowingStateLoss();

				} else {
					Intent intent = new Intent(activity,
							CMN_NeworViewMessagesActivity.class);
					intent.putExtra("dataValue", messagesItem.getMessages()
							.get(0));
					activity.startActivity(intent);

				}
			}
		});

		return view;
	}
}
