package com.coremobile.coreyhealth;

import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Iterator;

public class BaseUrlMgr {
	
	SharedPreferences mcurrentUserPref;
    public static final String CURRENT_USER = "CurrentUser";
	public static String[] SERVER_URLS = {
		"https://www.coremobile.us/organizations/organizations_s.json",
		"https://www.coremobnet.com/ServerIntegration_Appstore/resources/organizations_s.json"
	};
	private ArrayList<String> mServerUrls = new ArrayList<String>();
	private Iterator<String> mIter;
 	IDownloadJSON mHandler;
	public BaseUrlMgr(IDownloadJSON handler) {
		mHandler = handler;
		reset();
	}
	
	public void reset() {
		
		mIter = mServerUrls.iterator();
	}
	
	public void tryNextUrl() {
		if (mIter.hasNext()) {
			String url = mIter.next();
			System.out.println("OrgsLoader: Trying Url: " + url);
			new GetJSON(mHandler).execute(url);
		} else {
			System.out.println("OrgsLoader: no more urls to try");
		}
	}
}
