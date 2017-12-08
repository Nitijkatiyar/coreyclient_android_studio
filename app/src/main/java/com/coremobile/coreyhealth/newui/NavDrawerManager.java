package com.coremobile.coreyhealth.newui;

import android.content.Context;
import android.content.res.Configuration;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.coremobile.coreyhealth.CoreyDBHelper;
import com.coremobile.coreyhealth.MessageItem;
import com.coremobile.coreyhealth.MyApplication;
import com.coremobile.coreyhealth.R;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

public class NavDrawerManager {
    // slide menu items
    private DrawerLayout mDrawerLayout;
    private View mDrawerRoot;
    private ExpandableListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private MessageTabActivityCMN mActivity;
    private CoreyDBHelper mCoreyDBHelper;
    private AllItemsAdapter mAllItemsAdapter;

    public NavDrawerManager(MessageTabActivityCMN activity) {
        System.out.println("NavDrawerManager constructor");
        mActivity = activity;
        mDrawerLayout = (DrawerLayout) mActivity.findViewById(R.id.drawer_layout);
        mDrawerRoot = mActivity.findViewById(R.id.drawer_root);
        //  mDrawerRoot.se
        mDrawerList = (ExpandableListView) mActivity.findViewById(R.id.drawer_list);
        mCoreyDBHelper = new CoreyDBHelper(mActivity);

        mDrawerToggle = new ActionBarDrawerToggle(mActivity, mDrawerLayout,
                R.drawable.ic_drawer,   // nav menu toggle icon
                R.string.app_name,      // nav drawer open - description for accessibility
                R.string.app_name       // nav drawer close - description for accessibility
        ) {
            public void onDrawerClosed(View view) {
                mActivity.invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                Map<String, ArrayList<MessageItem>> allMessagesMap =
                        mCoreyDBHelper.getAllMessagesMap(mActivity);
                if (mAllItemsAdapter == null ||
                        !areEqual(mAllItemsAdapter.getAllMessagesMap(), allMessagesMap)) {
                    System.out.println("NavDrawerManager (re)building AllItemsAdapter");
                    mAllItemsAdapter =
                            new AllItemsAdapter(
                                    NavDrawerManager.this,
                                    mActivity,
                                    allMessagesMap,
                                    MyApplication.crystalData.mLeftContextData
                            );
                    mDrawerList.setAdapter(mAllItemsAdapter);
                    mDrawerList.setOnChildClickListener(mAllItemsAdapter);

                    // when a group is expanded collapse all other groups
                    mDrawerList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                        @Override
                        public void onGroupExpand(int groupPosition) {
                            final int numHeaders = mAllItemsAdapter.getGroupCount();
                            for (int i = 0; i < numHeaders; ++i) {
                                if (i != groupPosition) {
                                    mDrawerList.collapseGroup(i);
                                }
                            }
                        }
                    });

                    mAllItemsAdapter.notifyDataSetChanged();
                    mActivity.invalidateOptionsMenu();
                } else {
                    System.out.println("NavDrawerManager Reusing AllItemsAdapter");
                    updateMessageReadStatus(mAllItemsAdapter.getAllMessagesMap(), allMessagesMap);
                    mAllItemsAdapter.notifyDataSetChanged();
                    mActivity.invalidateOptionsMenu();
                }
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item);
    }

    public void closeDrawer() {
        try {
            mDrawerLayout.closeDrawer(mDrawerRoot);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout.isDrawerOpen(mDrawerRoot);
    }

    public void syncState() {
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /*** PRIVATE ***/
    /**
     * returns true if twMessageItemmaps are equal
     */
    private boolean areEqual(Map<String, ArrayList<MessageItem>> curMessagesMap,
                             Map<String, ArrayList<MessageItem>> newMessagesMap) {
        if (curMessagesMap.size() != newMessagesMap.size()) {
            return false;
        }

        for (Entry<String, ArrayList<MessageItem>> entry : curMessagesMap.entrySet()) {
            if (newMessagesMap.containsKey(entry.getKey())) {
                ArrayList<MessageItem> curMessages = entry.getValue();
                ArrayList<MessageItem> newMessages = newMessagesMap.get(entry.getKey());
                if (curMessages.size() == newMessages.size()) {
                    for (int i = 0; i < curMessages.size(); ++i) {
                        MessageItem curMessage = curMessages.get(i);
                        MessageItem newMessage = newMessages.get(i);
                        if (curMessage.msgid != newMessage.msgid) {
                            return false;
                        }
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }

        }
        return true;
    }

    // areEqual() should have been called and asserted before calling this method
    private void updateMessageReadStatus(Map<String, ArrayList<MessageItem>> dstMap,
                                         Map<String, ArrayList<MessageItem>> srcMap) {
        for (Entry<String, ArrayList<MessageItem>> entry : srcMap.entrySet()) {
            ArrayList<MessageItem> srcMessages = entry.getValue();
            ArrayList<MessageItem> dstMessages = dstMap.get(entry.getKey());
            for (int i = 0; i < srcMessages.size(); ++i) {
                dstMessages.get(i).isRead = srcMessages.get(i).isRead;
            }
        }
    }

    // used for testing ExpandableListView
    public class MyListAdapter extends BaseExpandableListAdapter {

        private Context mContext;

        public MyListAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public Object getChild(int groupPosition, int childPosititon) {
            return (1 + groupPosition) + "->" + (1 + childPosititon);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(int groupPosition, final int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {

            final String childText = (String) getChild(groupPosition, childPosition);

            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this.mContext
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.nav_list_item, null);
            }

            TextView mainTextView = (TextView) convertView
                    .findViewById(R.id.main_text);

            mainTextView.setText(childText);
            return convertView;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return 1 + groupPosition;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return (1 + groupPosition) + ":";
        }

        @Override
        public int getGroupCount() {
            return 5;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            String headerTitle = (String) getGroup(groupPosition);
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this.mContext
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.nav_list_header, null);
            }

            TextView headerView = (TextView) convertView
                    .findViewById(R.id.title);
            headerView.setText(headerTitle);

            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}
