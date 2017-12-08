package com.coremobile.coreyhealth;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class MessageItemAdapter extends ArrayAdapter<MessageItem>
{

    int resource;

    public MessageItemAdapter(Context _context, int _resource,
                              List<MessageItem> _items)
    {
        super(_context, _resource, _items);
        resource = _resource;
    }




    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View messageView;
        MessageItem item = getItem(position);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            messageView = infalInflater.inflate(resource, null);
        } else {
            messageView = convertView;
        }

        TextView subject = (TextView) messageView.findViewById(R.id.line1);
        TextView meetingTime = (TextView) messageView
                               .findViewById(R.id.line2);
        View v = (View)messageView.findViewById(R.id.dividerL);
        v.setVisibility(View.GONE);
        subject.setText(item.subject);
        if(item.type.equalsIgnoreCase("todo"))
        {
            meetingTime.setText(item.endTime);
            if(item.endTime.length() ==0)
            {
                meetingTime.setVisibility(View.GONE);
            }
        }
        else
        {
            meetingTime.setText(item.meetingTime);
        }
        parent.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

        // set text color based on msg-read-status
        int mainTextTypeface, subTextTypeface;
        int itemBg;
        if (item.isRead)
        {
            itemBg = R.color.mli_readmsg_bg;
            mainTextTypeface = Typeface.NORMAL;
            subTextTypeface = Typeface.NORMAL;
        }
        else
        {
            itemBg = R.color.mli_newmsg_bg;
            mainTextTypeface = Typeface.BOLD;
            subTextTypeface = Typeface.BOLD;
        }
        messageView.setBackgroundResource(itemBg);
        subject.setTypeface(null, mainTextTypeface);
        meetingTime.setTypeface(null, subTextTypeface);
        return messageView;
    }
}
