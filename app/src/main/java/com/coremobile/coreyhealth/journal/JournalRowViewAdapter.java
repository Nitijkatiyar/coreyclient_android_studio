package com.coremobile.coreyhealth.journal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.coremobile.coreyhealth.R;

import java.util.Map;

public class JournalRowViewAdapter extends BaseAdapter {
    private Context context;
    private Map<String,String>objects;
    private String[] mKeys;
    public JournalRowViewAdapter(Context context, Map<String,String> objects) {
        this.context = context;
        this.objects = objects;
        mKeys = objects.keySet().toArray(new String[objects.size()]);
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public String getItem(int position) {
        return objects.get(mKeys[position]);
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
            convertView = LayoutInflater.from(context).inflate(R.layout.journal_row_layout, parent, false);
            holder.titleText = (TextView) convertView.findViewById(R.id.titleTextVw);
            holder.dateText = (TextView) convertView.findViewById(R.id.dateTextVw);
            holder.imageVw=(ImageView)convertView.findViewById(R.id.nextImageVw);


            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.titleText.setText(getItem(position).toString());
        holder.dateText.setText(mKeys[position]);

        return convertView;
    }

    class ViewHolder {
        TextView titleText;
        TextView dateText;
        ImageView imageVw;

    }
}