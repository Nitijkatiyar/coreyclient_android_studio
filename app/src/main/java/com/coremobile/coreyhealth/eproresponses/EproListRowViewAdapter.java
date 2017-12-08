package com.coremobile.coreyhealth.eproresponses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.coremobile.coreyhealth.R;

import org.json.JSONObject;

import java.util.List;

public class EproListRowViewAdapter extends BaseAdapter {
    private Context context;
    private List<JSONObject>objects;
    private String[] mKeys;
    public EproListRowViewAdapter(Context context, List<JSONObject> objects) {
        this.context = context;
        this.objects = objects;
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public JSONObject getItem(int position) {
        return objects.get(position);
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
            convertView = LayoutInflater.from(context).inflate(R.layout.epro_response_row_layout, parent, false);
            holder.titleText = (TextView) convertView.findViewById(R.id.titleTextVw);
            holder.responseText = (TextView) convertView.findViewById(R.id.responseTextVw);
            holder.imageVw=(ImageView)convertView.findViewById(R.id.nextImageVw);


            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        try {
            JSONObject jsonObject=getItem(position);


            holder.titleText.setText(jsonObject.getString("Question"));
            int size=jsonObject.getJSONArray("ResponseDetails").length();
            String response="Total Response: " +size;
            holder.responseText.setText(response);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return convertView;
    }

    class ViewHolder {
        TextView titleText;
        TextView responseText;
        ImageView imageVw;

    }
}