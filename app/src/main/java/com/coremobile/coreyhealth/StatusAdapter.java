package com.coremobile.coreyhealth;

import android.annotation.SuppressLint;
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

@SuppressLint("Instantiatable")
public class StatusAdapter extends BaseAdapter
{
/*
    String mStatusColor[] = { "Ready", "Not Ready", "Completed",
            "Not Available", "In Progress", "Busy", "Unassigned", "Assigned",
            "Error" }; */
    HashMap<String, String> mStatusColorHashMap = new HashMap<String, String>();
    private Context mContext;
    ArrayList<String> mStatusColor;
    public StatusAdapter(Context context)
    {
        // TODO Auto-generated constructor stub
        mContext = context;
        JSONHelperClass jsonHelperClass = new JSONHelperClass();
        mStatusColorHashMap=jsonHelperClass.getStatusColourMap();
     //   Set<String> mStatusSet = mStatusColorHashMap.keySet();
      //  Object[] mStatusArray =  mStatusSet.toArray();
        int Length = mStatusColorHashMap.size();
      //  ArrayList<String> mStatusColour;
       
        mStatusColor = new ArrayList<String>(mStatusColorHashMap.keySet());
        
   /*     if (mStatusColorHashMap != null && mStatusColorHashMap.size() == 0)
        {
            mStatusColorHashMap.put("Ready", "#008000");
            mStatusColorHashMap.put("Not Ready", "#90EE90");
            mStatusColorHashMap.put("Completed", "#800080");
            mStatusColorHashMap.put("Not Available", "#808080");
            mStatusColorHashMap.put("In Progress", "#FFFF00");
            mStatusColorHashMap.put("Busy", "#ffa500");
            mStatusColorHashMap.put("Unassigned", "#ADD8E6");
            mStatusColorHashMap.put("Assigned", "#00008B");
            mStatusColorHashMap.put("Error", "#8b0000");
        }
*/
    }

    @Override
    public int getCount()
    {
        // TODO Auto-generated method stub
    //    return mStatusColor.length;
    	return mStatusColorHashMap.size();
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
    	return mStatusColor.get(pos);
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
            convertView = liService.inflate(R.layout.status_adapter_layout, null);
            holder = new StatusViewHolder();

            holder.statTextView = (TextView) convertView
                                  .findViewById(R.id.statusText);

            convertView.setTag(holder);
        }
        else
        {
            holder = (StatusViewHolder) convertView.getTag();
        }
        

        holder.statTextView.setText(mStatusColor.get(position));
        holder.statTextView.setTypeface(Typeface.DEFAULT_BOLD);
        holder.statTextView.setGravity(Gravity.CENTER_VERTICAL);
      //  convertView.setBackgroundColor(Color
      //                                 .parseColor(mStatusColorHashMap.get(mStatusColor[position])));
        String colour = "#"+mStatusColorHashMap.get(mStatusColor.get(position));
        Log.d("statusadapter","colour =" +colour);
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
