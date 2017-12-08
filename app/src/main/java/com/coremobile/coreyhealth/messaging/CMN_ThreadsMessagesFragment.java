package com.coremobile.coreyhealth.messaging;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.coremobile.coreyhealth.R;
import com.coremobile.coreyhealth.Utils;

import java.util.List;
/**
 * @author Nitij Katiyar
 *
 */
public class CMN_ThreadsMessagesFragment extends Fragment {
	ViewGroup root;
	private SharedPreferences mCurrentUserPref;
	public static final String CURRENT_USER = "CurrentUser";
	private String organizationName;
	String password;
	String userName;
	ListView listView;
	ImageView home, newMessage;
	List<CMN_MessageDataModel> messages;

	public CMN_ThreadsMessagesFragment(List<CMN_MessageDataModel> messages) {
		this.messages = messages;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		root = (ViewGroup) inflater
				.inflate(R.layout.fragment_allmessages, null);

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

		CMN_ThreadsMessagesAdapter adapter = new CMN_ThreadsMessagesAdapter(
				getActivity(), messages);
		listView.setAdapter(adapter);
		return root;

	}

	public void syncMessages() {
		if (Utils.checkInternetConnection()) {
			CMN_GetAllMessagesWebService allMessages = new CMN_GetAllMessagesWebService(
					getActivity(), listView, null);
			allMessages.execute(userName, password, organizationName);
		} else {
		}
	}
}
