package com.coremobile.coreyhealth.newui;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.coremobile.coreyhealth.CoreyDBHelper;
import com.coremobile.coreyhealth.MessageItem;
import com.coremobile.coreyhealth.MsgDataCache;
import com.coremobile.coreyhealth.R;

import java.util.ArrayList;

public class PhoneAddSlideFragment extends Fragment implements OnPageChangeListener
{
    ViewGroup mIndecator = null;
    ViewPager mPager = null;

    ArrayList<MessageItem> MmarketTrackerItems;

    MsgDataCache mMsgDataCache;
    CoreyDBHelper mCoreyDBHelper;

    public static int Mymsgid = 0;

    private int mMessageId;
    public PhoneAddSlideFragment()
    {
        super();
    }

    public PhoneAddSlideFragment(ArrayList<MessageItem> mmarketTrackerItems)
    {

        MmarketTrackerItems = mmarketTrackerItems;
        mMsgDataCache = new MsgDataCache(getActivity());


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity().getBaseContext();
        mCoreyDBHelper = new CoreyDBHelper(mContext);
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


    }

    @Override
    public void onResume()
    {
        // TODO Auto-generated method stub
        super.onResume();

        new setAdapterTask().execute();

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

            mPager.setAdapter(new TestFragmentAdapter(getFragmentManager()));
            initIndicator();
        }

    }



    public void initIndicator()
    {
        int index = mPager.getCurrentItem();
        mIndecator.removeAllViews();
        for (int i = 0; i < mPager.getAdapter().getCount(); i++)
        {
            ImageView image = new ImageView(getActivity());
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
        protected final String[] CONTENT = new String[] { "This", "Is", "A",
                "Test",
                                                        };

        public TestFragmentAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            mMessageId = MmarketTrackerItems.get(position).msgid;
            return TestFragment.newInstance(MmarketTrackerItems.get(position).toString(),MmarketTrackerItems,position);
        }

        @Override
        public int getCount()
        {
            return MmarketTrackerItems.size();
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            return (CharSequence) MmarketTrackerItems.get(position).toString();
        }

        public void setCount(int count)
        {
            if (count > 0 && count <= 10)
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

        Intent intent = new Intent("my-event");
        // add data
        intent.putExtra("message", String.valueOf(mMessageId));
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);




    }

    public void updateIndicator()
    {
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
