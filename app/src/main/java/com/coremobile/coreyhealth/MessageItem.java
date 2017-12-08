package com.coremobile.coreyhealth;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class MessageItem implements Parcelable
{
    private static final String TAG = "Corey_MessageItem";
    public String subject;
    public int msgid;
    public String startTime;
    public String endTime;
    public String meetingTime;
    public String type;
    public boolean isRead = false;
    public boolean isDeleted = false;
    public String name;
    public String phonenumber;
    public String company;
    public String PatientDetails;
    public String ImageUrl1;
    public String ImageUrl2;
    public String MsgObjId;
    public String ContextId;
    public MessageItem()
    {

    }

    public MessageItem(Parcel in)
    {
        subject = in.readString();
        msgid = in.readInt();
        startTime = in.readString();
        ContextId = in.readString();
        endTime = in.readString();
        meetingTime = in.readString();
        type = in.readString();
        isRead = (in.readByte() == 1);
    }

    public static final Creator<MessageItem> CREATOR
    = new Creator<MessageItem>()
    {
        public MessageItem createFromParcel(Parcel in)
        {
            Log.d (TAG, "createFromParcel()");
            return new MessageItem(in);
        }

        public MessageItem[] newArray (int size)
        {
            Log.d (TAG, "createFromParcel() newArray ");
            return new MessageItem[size];
        }
    };

    @Override
    public int describeContents()
    {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(subject);
        dest.writeString(ContextId);
        dest.writeInt(msgid);
        dest.writeString(startTime);
        dest.writeString(endTime);
        dest.writeString(meetingTime);
        dest.writeString(type);
        dest.writeByte((byte) (isRead? 1 : 0));
    }

    public String toString()
    {
        return String.format(subject);
    }

}
