package com.coremobile.coreyhealth;


import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class DemoItemAdapter extends ArrayAdapter<Integer[]>
{

    int mItemLayoutResource;
    LayoutInflater mInflater;

    int mLineOneRes, mLineTwoRes;
    boolean mIsBold;

    public DemoItemAdapter(Context _context, int _resource,
                           Integer[][] _items,
                           int lineOneRes, int lineTwoRes, boolean bold)
    {
        super(_context, _resource, _items);
        mItemLayoutResource = _resource;
        mLineOneRes = lineOneRes;
        mLineTwoRes = lineTwoRes;
        mIsBold = bold;
        mInflater = (LayoutInflater)_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View itemView;
        Integer[] item = getItem(position);

        if (convertView == null)
        {
            itemView = mInflater.inflate(mItemLayoutResource, parent, false);
        }
        else
        {
            itemView = convertView;
        }

        TextView lineOne = (TextView) itemView.findViewById(mLineOneRes);
        TextView lineTwo = (TextView) itemView.findViewById(mLineTwoRes);

        lineOne.setText(item[0]);
        lineTwo.setText(item[1]);

        if (mIsBold)
        {
            // keep the item unread (bright & bold)
            itemView.setBackgroundResource(R.color.mli_newmsg_bg);
            lineOne.setTypeface(null, Typeface.BOLD);
            lineTwo.setTypeface(null, Typeface.BOLD);
        }

        return itemView;
    }
}

