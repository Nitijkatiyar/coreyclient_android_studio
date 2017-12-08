package com.coremobile.coreyhealth.newui;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.coremobile.coreyhealth.ContextData;
import com.coremobile.coreyhealth.MessageItem;
import com.coremobile.coreyhealth.MessageItemAdapter;
import com.coremobile.coreyhealth.R;
import com.coremobile.coreyhealth.genericviewandsurvey.CMN_GenericViewActivity;
import com.coremobile.coreyhealth.googleAnalytics.CMN_Preferences;

import java.util.List;

import static com.coremobile.coreyhealth.newui.MsgScheduleTabActivity.loadingDataFromServer;

@SuppressLint("ViewConstructor")
public class ContextController extends MessageItemAdapter implements IContextController {
    private ContextData mCoreyContext;
    private int mUnreadMsgCount;
    private int mMsgCount;

    private IContextAction mContextAction;

    public ContextController(Context _context, int _resource, List<MessageItem> _items,
                             ContextData coreyCxt, IContextAction contextAction) {
        super(_context, _resource, _items);
        mCoreyContext = coreyCxt;
        mContextAction = contextAction;
        mMsgCount = _items.size();
        // determine number of unread messages
        mUnreadMsgCount = -1;
        if (_items != null && _items.size() > 0) {
            mUnreadMsgCount = 0;
            for (MessageItem msg : _items) {
                if (!msg.isRead) {
                    ++mUnreadMsgCount;
                }
            }
        }

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = super.getView(position, convertView, parent);

        // show layer-1 hide layer-2 which is used when no items are present in the list.
        itemView.findViewById(R.id.layer_1).setVisibility(View.VISIBLE);
        itemView.findViewById(R.id.layer_2).setVisibility(View.GONE);
        return itemView;
    }

    /*
     * Changing  mUnreadMsgCount by mMsgCount so that total count of messages will be displayed
     * always instead of unread messages
     */
    @Override
    public View getHeaderView(boolean isExpanded, View convertView, ViewGroup parent) {
        return ContextUtils.getDefaultHeaderView(getContext(), mCoreyContext,
                isExpanded, convertView, parent, mMsgCount, mContextAction);
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        int tabId = MessageTabActivityCMN.getTabId(mCoreyContext.mName);
        System.out.println("ContextController.onChildClick " + mCoreyContext.mName + ", " +
                mCoreyContext.mDisplayText + ", " + childPosition + ", " + tabId);

        if (tabId < 0) {
            System.out.println("ContextController.onChildClick ERROR: No target tab available in message tabs view");
            return false;
        }
        if (mCoreyContext.mNeedsGenericView) {
            getContext().startActivity(new Intent(getContext(), CMN_GenericViewActivity.class).putExtra("data", mCoreyContext));
        } else {
            MessageTabActivityCMN.shouldCallTriggerAPI = true;
            // build intent
            CMN_Preferences.setisPatientDisplayedonDashboard(getContext(), false);
            MessageTabActivityCMN.patientPosition = childPosition;
            Intent myIntent = new Intent(getContext(), MessageTabActivityCMN.class);
            myIntent.putExtra("isPatient", true);
            myIntent.setAction("com.for.view");
            myIntent.putExtra("CallerTab", tabId);
            myIntent.putExtra("Position", childPosition);
            myIntent.putExtra("isFromList", true);
            loadingDataFromServer = new ProgressDialog(MessageTabActivityCMN.activity);
            loadingDataFromServer.setMessage("Please wait...");
            loadingDataFromServer.setCancelable(false);
            loadingDataFromServer.show();
            // launch intent
            getContext().startActivity(myIntent);
        }
        return true;
    }
}
