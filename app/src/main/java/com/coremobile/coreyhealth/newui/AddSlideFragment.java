package com.coremobile.coreyhealth.newui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.coremobile.coreyhealth.CoreyDBHelper;
import com.coremobile.coreyhealth.DatabaseProvider;
import com.coremobile.coreyhealth.MessageItem;
import com.coremobile.coreyhealth.MyApplication;
import com.coremobile.coreyhealth.R;

import java.util.ArrayList;



@SuppressLint("ValidFragment")
public class AddSlideFragment extends Fragment implements OnPageChangeListener
{
    ViewGroup mIndecator = null;
    ViewPager mPager = null;

    ArrayList<MessageItem> MmarketTrackerItems = new ArrayList<MessageItem>();

    CoreyDBHelper mCoreyDBHelper;

    TestFragmentAdapter mTestFragmentAdapter;

    public static int Mymsgid = 0;

    private int mMessageId;

    private int mPosition;
    Bundle mBundle;

    boolean isFromList = false;



    @SuppressLint("ValidFragment")
    public AddSlideFragment(ArrayList<MessageItem> mmarketTrackerItems)
 {

        MmarketTrackerItems = mmarketTrackerItems;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity().getBaseContext();
        mCoreyDBHelper = new CoreyDBHelper(mContext);
        mBundle = getArguments();
        if (getArguments() != null)
        {
            mPosition = getArguments().getInt("position");
            isFromList = getArguments().getBoolean("isFromList");

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        return inflater.inflate(R.layout.addslide_layout, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onViewCreated(view, savedInstanceState);
        mPager = (ViewPager) view.findViewById(R.id.pager);
        mPager.setOnPageChangeListener(this);
        mIndecator = (ViewGroup) view.findViewById(R.id.view_indecator);

    }

    Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mTestFragmentAdapter = new TestFragmentAdapter(getChildFragmentManager());
        new setAdapterTask().execute();
    }

    @Override
    public void onResume()
    {
        // TODO Auto-generated method stub
        super.onResume();
  //      mTestFragmentAdapter = new TestFragmentAdapter(getFragmentManager());

     //   new setAdapterTask().execute();

    }

    private class setAdapterTask extends AsyncTask<Void, Void, Void>
    {
        protected Void doInBackground(Void... params)
        {

            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            try
            {
                mPager.setAdapter(mTestFragmentAdapter);
                mPager.getAdapter().notifyDataSetChanged();
                if (isFromList)
                    mPager.setCurrentItem(mPosition);
                isFromList = false;
                if (isAdded())
                    initIndicator();
            }
            catch (IllegalArgumentException e)
            {
                Log.d("Error", "Something went wrong in AddSlideFrageMent");
            }
        }

    }

    public void initIndicator()
    {
        mPager.getAdapter().notifyDataSetChanged();

        int index = mPager.getCurrentItem();
        mIndecator.removeAllViews();
        for (int i = 0; i < mPager.getAdapter().getCount(); i++)
        {
            ImageView image = new ImageView(mContext);
            if (i == index)
            {
                image.setImageDrawable(getResources().getDrawable(
                                           R.drawable.smallcircle_red));
            }
            else
            {
                image.setImageDrawable(getResources().getDrawable(
                                           R.drawable.smallcircle_black));
            }
            image.setPadding(3, 3, 3, 3);
            mIndecator.addView(image);
        }

    }

    class TestFragmentAdapter extends FragmentStatePagerAdapter
    {

        public TestFragmentAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {

            MessageItem msg = MmarketTrackerItems.get(position);
            mMessageId = msg.msgid;
            //Log.i("TestFragmentAdapter", "getitem position="+position);
            //Log.i("TestFragmentAdapter", "getitem mMessagid="+mMessageId);
            String content;
            if (msg.type != DatabaseProvider.MSG_TYPE_PHONE_CALL) {
            //    content = mCoreyDBHelper.getPatientDetails(mMessageId);
                content = msg.PatientDetails;
            } else {
                content = "<b>Caller: " + msg.subject + "</b><br/>" +
                          "Call time: " + msg.meetingTime;
            }
            return TestFragment.newInstance(
                       content,
                       MmarketTrackerItems, position);
        }

        @Override
        public int getCount()
        {
            return 1;
        }

        @Override
        public CharSequence getPageTitle(int position)
        {

            return (CharSequence) MmarketTrackerItems.get(position).toString();
        }

        public void setCount(int count)
        {
            if (count > 0 && count <= 20)
            {
                notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageSelected(int position)
    {
        // TODO Auto-generated method stub
        updateIndicator();
        mMessageId = MmarketTrackerItems.get(position).msgid;
        mTestFragmentAdapter.notifyDataSetChanged();
        MyApplication.INSTANCE.MsgIdAvty=String.valueOf(mMessageId);
        Intent intent = new Intent("my-event");
        intent.putExtra("message", String.valueOf(mMessageId));
        intent.putExtra("position", position);
        Log.d("addslideframe","Onpage selected Position=" +position);
        Log.d("addslideframe","Onpage selected mMessageid=" +mMessageId);
        mPosition = position;
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);

    }

    public void updateIndicator()
    {
        mPager.getAdapter().notifyDataSetChanged();
        int index = mPager.getCurrentItem();

        if (mIndecator != null && index <= mIndecator.getChildCount())
        {
            for (int i = 0; i < mIndecator.getChildCount(); i++)
            {
                ImageView image = (ImageView) mIndecator.getChildAt(i);
                if (i == index)
                {
                    image.setImageDrawable(getResources().getDrawable(
                                               R.drawable.smallcircle_red));
                }
                else
                {
                    image.setImageDrawable(getResources().getDrawable(
                                               R.drawable.smallcircle_black));
                }
            }

        }

    }
}
