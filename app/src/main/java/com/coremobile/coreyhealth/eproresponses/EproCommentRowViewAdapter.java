package com.coremobile.coreyhealth.eproresponses;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.coremobile.coreyhealth.R;

import org.json.JSONArray;
import org.json.JSONObject;

public class EproCommentRowViewAdapter extends BaseAdapter {
    private Context context;
    private JSONArray eproResponseArray;
    public EproCommentRowViewAdapter(Context context, JSONArray str) {
        this.context = context;
        this.eproResponseArray=str;
    }

    @Override
    public int getCount() {
        return this.eproResponseArray.length();
    }

    @Override
    public JSONObject getItem(int position) {
        try {
           return  (JSONObject) eproResponseArray.get(position);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView==null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.epro_comment_row_layout, parent, false);
            holder.responseTimeTextVw = (TextView) convertView.findViewById(R.id.dateTextVw);
            holder.responseTextVw = (TextView) convertView.findViewById(R.id.responseTextVw);
            holder.commentTextVw=(TextView) convertView.findViewById(R.id.commentTextVw);


            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        try {
            JSONObject jsonObject=getItem(position);

            holder.responseTimeTextVw.setText(jsonObject.getString("ResponseTime"));
            holder.responseTextVw.setText(jsonObject.getString("Response"));
            if(!jsonObject.getString("Comments").equalsIgnoreCase(""))
            holder.commentTextVw.setText(Html.fromHtml("<i>Comment:</i>  " + jsonObject.getString("Comments")));

        }
        catch(Exception e){
            e.printStackTrace();
        }
        return convertView;
    }

    class ViewHolder {
        TextView responseTimeTextVw;
        TextView responseTextVw;
        TextView commentTextVw;

    }
}