package com.coremobile.coreyhealth;


import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract.Contacts;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
 
public class RatingAdapter extends BaseAdapter {
 
    Context mContext;
    LayoutInflater inflater;
    private List<ListItem> mainDataList = null;
    private ArrayList<ListItem> arraylist;
    private String TAG ="MultiselectAdapter";
    private Activity mActivity;
    
    public RatingAdapter(Context context, List<ListItem> mainDataList,Activity activity) {
     
        mContext = context;
        this.mainDataList = mainDataList;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<ListItem>();
        this.arraylist.addAll(mainDataList);
        mActivity=activity;
 
         
    }
 
    static class ViewHolder {
    	protected  RatingBar ratingbar;
        protected TextView description;
        protected TextView videolink;
        protected CheckBox check;
        protected ImageView image;
    }
 
    @Override
    public int getCount() {
        return mainDataList.size();
    }
 
    @Override
    public ListItem getItem(int position) {
        return mainDataList.get(position);
    }
 
    @Override
    public long getItemId(int position) {
        return position;
    }
 
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.rating_sel_row, null);
            
            holder.description = (TextView) view.findViewById(R.id.description );
            holder.ratingbar = (RatingBar) view.findViewById(R.id.ratingBar1 );
            holder.ratingbar.setOnRatingBarChangeListener(ratelistner);
 
            view.setTag(holder);
            view.setTag(R.id.ratingBar1, holder.ratingbar);
            view.setTag(R.id.description, holder.description);
 
            
 
        } else {
            holder = (ViewHolder) view.getTag();
        }
 
        holder.description.setText(mainDataList.get(position).getdisplayText());
     
     // holder.videolink.setTag(position);
        holder.ratingbar.setTag(position);
        return view;
    }
 
    RatingBar.OnRatingBarChangeListener ratelistner=
	          new RatingBar.OnRatingBarChangeListener() {
			public void onRatingChanged(RatingBar ratingBar,float rating, boolean fromTouch) {
				Integer myPosition=(Integer)ratingBar.getTag();
				ListItem litem=mainDataList.get(myPosition);
				litem.setRating(rating);
			
		}
	};  
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        mainDataList.clear();
        if (charText.length() == 0) {
            mainDataList.addAll(arraylist);
        } else {
            for (ListItem wp : arraylist) {
                if (wp.getVideoDescription().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    mainDataList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
 
    public Bitmap getByteContactPhoto(String contactId) {
        Uri contactUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, Long.parseLong(contactId));
        Uri photoUri = Uri.withAppendedPath(contactUri, Contacts.Photo.CONTENT_DIRECTORY);
        Cursor cursor = mContext.getContentResolver().query(photoUri,
                        new String[] {Contacts.Photo.DATA15}, null, null, null);
        if (cursor == null) {
            return null;
        }
        try {
            if (cursor.moveToFirst()) {
                byte[] data = cursor.getBlob(0);
                if (data != null) {
                    return BitmapFactory.decodeStream( new ByteArrayInputStream(data));
                }
            }
        } finally {
            cursor.close();
        }
 
        return null;
        } 
    
   
    View.OnClickListener mOnClickListener = new View.OnClickListener() {
    @Override
	public void onClick(View v) {
		Log.d (TAG, "onclick listener invoked");
	//	int idd =  v.getId();
		View mView=v;
		int pos=(Integer)mView.getTag();
		ListItem lItem1= mainDataList.get(pos);
		Log.d(TAG,"lItem1 name="+lItem1.getdisplayText());
		Log.d(TAG,"lItem1 url="+lItem1.getOpenUrl());
		Log.d(TAG,"lItem1 url="+lItem1.getVideoDescription());
		OnClickHelper(lItem1);	   
	}
    };
  
    public void OnClickHelper(ListItem lstItem1)
    {
    	
    	String openUrl = lstItem1.getOpenUrl();		
		Log.d(TAG,"openUrl ="+openUrl);
		
	
		/*
		 * If web page
		 * *****************************************************************
		 */
		if ( openUrl != null && openUrl.contains("http")) 
		{
		    String displayText = lstItem1.getdisplayText();
              
                    
			Intent urlIntent = new Intent(mActivity,
				WebViewActivityCMN.class);
		    
			if( openUrl != null)
			{
			     if(!openUrl.isEmpty()) 
			    	 {
			    	 urlIntent.putExtra("myurl",openUrl);
			    	 urlIntent.putExtra("objname",displayText);
			    	 }
			}
			else
			{
			    Log.d(TAG, "No urls present");
			}
	   
			mActivity.startActivity(urlIntent);
		    
		}
		
		
    } 
     
}
