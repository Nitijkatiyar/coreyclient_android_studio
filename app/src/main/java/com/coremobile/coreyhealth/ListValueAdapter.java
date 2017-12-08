package com.coremobile.coreyhealth;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class ListValueAdapter extends BaseAdapter
{
	 HashMap<String, String> mStatusColorHashMap = new HashMap<String, String>();
	    private Context mContext;
	    ArrayList<String> mStatusColor; 
	    ArrayList<ListItem> mListItemArray;
	    public ListValueAdapter(Context context, ArrayList<ListItem> listitemarray)
	    {
	        // TODO Auto-generated constructor stub
	        mContext = context;
	   /*     JSONHelperClass jsonHelperClass = new JSONHelperClass();
	        mStatusColorHashMap=jsonHelperClass.getStatusColourMap(); */
	     //   Set<String> mStatusSet = mStatusColorHashMap.keySet();
	      //  Object[] mStatusArray =  mStatusSet.toArray();
	        mListItemArray =listitemarray; 
	        int Length = mListItemArray.size();
	        
	    //    int Length = mStatusColorHashMap.size();
	      //  ArrayList<String> mStatusColour;
	       
	    //    mStatusColor = new ArrayList<String>(mStatusColorHashMap.keySet());
	        
	  
	    }

	    @Override
	    public int getCount()
	    {
	  
	    	return mListItemArray.size();
	    }

	    @Override
	    public Object getItem(int pos)
	    {
	        // TODO Auto-generated method stub
	     //   return mStatusColor[pos];
	    //	Set<String> mStatusSet = mStatusColorHashMap.keySet();
	   // String mStatusArray[] = (String) mStatusSet.toArray();
	   /* 	int count=0;
	    	Object mStatus = null;
	    	Iterator it = mStatusColorHashMap.entrySet().iterator();
	    	while (it.hasNext())
	    	{
	    		HashMap.Entry pairs = (HashMap.Entry)it.next();
	    		 mStatus = pairs.getKey();
	    		if(count==pos)break;
	    	} 
	    	return mStatus;*/
	    	return mListItemArray.get(pos);
	    }

	    @Override
	    public long getItemId(int arg0)
	    {
	        // TODO Auto-generated method stub
	        return 0;
	    }

	    @Override
	    public View getView(int position, View convertView, ViewGroup viewgroup)
	    {
	        // TODO Auto-generated method stub
	        StatusViewHolder holder;
	        LayoutInflater liService = LayoutInflater.from(mContext);

	        if (convertView == null)
	        {
	            convertView = liService.inflate(R.layout.listvalue_adapter, null);
	            holder = new StatusViewHolder();

	            holder.statTextView = (TextView) convertView
	                                  .findViewById(R.id.valueText);

	            convertView.setTag(holder);
	        }
	        else
	        {
	            holder = (StatusViewHolder) convertView.getTag();
	        }
	        

	     //   holder.statTextView.setText(mListItemArray.get(position).getValue());
	        holder.statTextView.setText(mListItemArray.get(position).getdisplayText());
	        holder.statTextView.setTypeface(Typeface.DEFAULT_BOLD);
	        holder.statTextView.setGravity(Gravity.CENTER_VERTICAL);
	      //  convertView.setBackgroundColor(Color
	      //                                 .parseColor(mStatusColorHashMap.get(mStatusColor[position])));
	        String colour = "#"+mListItemArray.get(position).getBackgroundColor();
	        Log.d("ListValueAdapter","colour =" +colour);
	     //   convertView.setBackgroundColor(Color
	       //         .parseColor(mStatusColorHashMap.get(mStatusColor.get(position))));
	        convertView.setBackgroundColor(Color
	                .parseColor(colour));
	        holder.statTextView.setTag(position);
	        return convertView;

	    }


	    class StatusViewHolder
	    {

	        TextView statTextView;

	    }


}
