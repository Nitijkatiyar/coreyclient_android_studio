package com.coremobile.coreyhealth.messaging;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.coremobile.coreyhealth.R;
import com.coremobile.coreyhealth.Utils;
/**
 * @author Nitij Katiyar
 *
 */
public class CMN_AllMessagesFragment extends Fragment {
	ViewGroup root;
	String password;
	String userName;
	ListView listView;
	ImageView home, newMessage;

	TextView noMessages;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		root = (ViewGroup) inflater
				.inflate(R.layout.fragment_allmessages, null);

		noMessages = (TextView) root.findViewById(R.id.noMessages);

		newMessage = (ImageView) root.findViewById(R.id.button_newMessage);
		newMessage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(getActivity(),
						CMN_NeworViewMessagesActivity.class));
			}
		});

		listView = (ListView) root.findViewById(R.id.listView);


	 	syncMessages();
		return root;

	}

	public void syncMessages() {
		if (Utils.checkInternetConnection()) {
			CMN_GetAllMessagesWebService allMessages = new CMN_GetAllMessagesWebService(
					getActivity(), listView,noMessages);
			allMessages.execute();
		} else {
		}
	}
}
