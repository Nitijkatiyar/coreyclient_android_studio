package com.coremobile.coreyhealth.newui;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;

import com.coremobile.coreyhealth.ContextData;
import com.coremobile.coreyhealth.MessageItem;

import java.util.ArrayList;
import java.util.Map;

public class AllItemsAdapter extends BaseExpandableListAdapter implements ExpandableListView.OnChildClickListener {
    private NavDrawerManager mNavDrawerManager;
    private Activity mActivity;
    private Map<String,ArrayList<MessageItem>> mMessagesMap;
    private ArrayList<ContextData> mCoreyContexts;

    private ArrayList<IContextController> mContextControllers; // one per one context
 
    /**
     * messagesMap - messageType to messages map
     * contexts - list of corey contexts for which messages are to be displayed
     */
    public AllItemsAdapter(
        NavDrawerManager navDrawerManager,
        Activity activity,
        Map<String,ArrayList<MessageItem>> messagesMap,
        ArrayList<ContextData> contexts) {
        mNavDrawerManager = navDrawerManager;
        mActivity = activity;
        mMessagesMap = messagesMap;
        mCoreyContexts = contexts;
        mContextControllers = buildAdapters();
    }
 
    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return null;
    }
 
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }
 
    @Override
    public View getChildView(int groupPosition, final int childPosition,
            boolean isLastChild, View convertView, ViewGroup parent) {
        return mContextControllers.get(groupPosition).getView(childPosition, convertView, parent);
    }
 
    @Override
    public int getChildrenCount(int groupPosition) {
        return mContextControllers.get(groupPosition).getCount();
    }
 
    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }
 
    @Override
    public int getGroupCount() {
        return mCoreyContexts.size();
    }
 
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }
 
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
            View convertView, ViewGroup parent) {
        return mContextControllers.get(groupPosition).getHeaderView(isExpanded, convertView, parent);
    }
 
    @Override
    public boolean hasStableIds() {
        return false;
    }
 
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public boolean onChildClick(
        ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        boolean handled = mContextControllers.get(groupPosition)
            .onChildClick(parent, v, groupPosition, childPosition, id);
        if (handled) {
            mNavDrawerManager.closeDrawer();
        }
        return handled;
    }

    public Map<String, ArrayList<MessageItem>> getAllMessagesMap() {
        return mMessagesMap;
    }

    /*** PRIVATE ***/

    private ArrayList<IContextController> buildAdapters() {
        ArrayList<IContextController> adapters = new ArrayList<IContextController>(mCoreyContexts.size());
        for (ContextData coreyCxt: mCoreyContexts) {
        	ArrayList<MessageItem> messages = null;
        	if (coreyCxt.mMessageType != null && mMessagesMap.containsKey(coreyCxt.mMessageType)) {
        		messages = mMessagesMap.get(coreyCxt.mMessageType);
        	}
        	
            adapters.add(
                ContextControllerFactory.getContextController(mActivity, coreyCxt, messages));
        }
        return adapters;
    }
    
}

