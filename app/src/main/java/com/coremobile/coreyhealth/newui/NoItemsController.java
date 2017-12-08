package com.coremobile.coreyhealth.newui;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.coremobile.coreyhealth.ContextData;
import com.coremobile.coreyhealth.PatientAnalyticsActivityCMN;
import com.coremobile.coreyhealth.R;
import com.coremobile.coreyhealth.Utils;
import com.coremobile.coreyhealth.WebViewActivityCMN;
import com.coremobile.coreyhealth.eproresponses.CMN_EproResponseListActivity;
import com.coremobile.coreyhealth.genericviewandsurvey.CMN_GenericViewActivity;
import com.coremobile.coreyhealth.googleAnalytics.CMN_Preferences;
import com.coremobile.coreyhealth.native_epro.CMN_EproListActivity;
import com.coremobile.coreyhealth.patientactivitylog.ShowActivityLogCMN;
import com.coremobile.coreyhealth.scheduling.PatientLookupActivity;
import com.coremobile.coreyhealth.surgeonschedule.SurgeonScheduleActivity;
import com.coremobile.coreyhealth.surveylog.CMN_ViewSurveyLogActivity;
import com.coremobile.coreyhealth.uninvite_reinvite.CMN_ReUnInviteActivity;

public class NoItemsController extends BaseAdapter implements IContextController {
    private Context mContext;
    private ContextData mCoreyContext;
    private String mActionURL;
    private IContextAction mContextAction;

    public NoItemsController(Context context, ContextData coreyCxt, IContextAction contextAction) {
        mContext = context;
        mCoreyContext = coreyCxt;
        mContextAction = contextAction;
        if (!Utils.isEmpty(mCoreyContext.mURL) && !mCoreyContext.mName.equalsIgnoreCase("GettingStarted")) {
            mActionURL = mCoreyContext.mURL;
        } else if (!Utils.isEmpty(mCoreyContext.mHelpURL)) {
            mActionURL = mCoreyContext.mHelpURL;
        } else {
            mActionURL = null;
        }
    }

    @Override
    public int getCount() {
        return (mActionURL != null) ? 1 : 0;
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View messageView;
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            messageView = infalInflater.inflate(R.layout.msg_list_adapterlayout, null);
        } else {
            messageView = convertView;
        }

        messageView.findViewById(R.id.layer_1).setVisibility(View.GONE);
        TextView tv = (TextView) messageView.findViewById(R.id.layer_2);
        tv.setVisibility(View.VISIBLE);
        tv.setText(mCoreyContext.mDisplayText);
        return messageView;
    }

    @Override
    public View getHeaderView(boolean isExpanded, View convertView, ViewGroup parent) {
        return ContextUtils.getDefaultHeaderView(mContext, mCoreyContext,
                isExpanded, convertView, parent, -1, mContextAction);
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        System.out.println("NoItemsController " + mCoreyContext.mDisplayText + ", " + childPosition + ", " + mActionURL);
        if (mCoreyContext.mName.equalsIgnoreCase("PeriOp Manager")) {
            Intent myIntent = new Intent(mContext, PatientAnalyticsActivityCMN.class);
            myIntent.putExtra("source", "LHSMENU");
            myIntent.putExtra("Patientid", "all");
            mContext.startActivity(myIntent);
            return true;
        } else if (mCoreyContext.mName.equalsIgnoreCase("Add Patients")) {
            mContext.startActivity(new Intent(mContext, CMN_GenericViewActivity.class).putExtra("data", mCoreyContext));
            return true;
        } else if (mCoreyContext.mName.equalsIgnoreCase("ViewPatActivity")) {
            mContext.startActivity(new Intent(mContext, ShowActivityLogCMN.class));
            return true;
        } else if (mCoreyContext.mName.equalsIgnoreCase("epros")) {
            mContext.startActivity(new Intent(mContext, CMN_EproListActivity.class));
            return true;
        } else if (mCoreyContext.mName.equalsIgnoreCase("epeProsResponsesros") || mCoreyContext.mDisplayText.equalsIgnoreCase("ePRO Responses")) {
            Intent myIntent = new Intent(MessageTabActivityCMN.getInstance(), CMN_EproResponseListActivity.class);
            myIntent.putExtra("objname", mCoreyContext.mDisplayText);
            mContext.startActivity(myIntent);
            return true;
        } else if (mCoreyContext.mName.equalsIgnoreCase("SearchPatients") || mCoreyContext.mDisplayText.equalsIgnoreCase("Search for a Patient")) {
            Intent myIntent = new Intent(MessageTabActivityCMN.getInstance(), PatientLookupActivity.class);
            myIntent.putExtra("isSearchPatient", true);
            mContext.startActivity(myIntent);
            return true;
        } else if (mCoreyContext.mName.equalsIgnoreCase("ReqSchedule") || mCoreyContext.mDisplayText.equalsIgnoreCase("Schedule")) {
            Utils.showSchedulingPopup(mContext);
            return true;
        } else if (mCoreyContext.mName.equalsIgnoreCase("ViewSurveyLog") || mCoreyContext.mDisplayText.equalsIgnoreCase("View Survey Log")) {
            Intent myIntent = new Intent(MessageTabActivityCMN.getInstance(), CMN_ViewSurveyLogActivity.class);
            mContext.startActivity(myIntent);
            return true;
        } else if(mCoreyContext.mName.equalsIgnoreCase("Un-invitePatients")){
            Intent myIntent = new Intent(mContext, CMN_ReUnInviteActivity.class);
            mContext.startActivity(myIntent);
            return true;
        } else if(mCoreyContext.mName.equalsIgnoreCase("SurgeonSchedules")){
            Intent myIntent = new Intent(mContext, SurgeonScheduleActivity.class);
            mContext.startActivity(myIntent);
            return true;
        } else {
            String url = mActionURL;
            if (!url.startsWith("http")) {
                url = Utils.getServerBaseUrl() + "/" + url;
            }
            System.out.println("NoItemsController url : " + url);
            url = url
                    .replace("token==%@", "token=" + CMN_Preferences.getUserToken(mContext));
            Intent urlIntent = new Intent(mContext, WebViewActivityCMN.class);
            urlIntent.putExtra("myurl", url);
            urlIntent.putExtra("objname", mCoreyContext.mDisplayText);
            mContext.startActivity(urlIntent);
            return true;
        }
    }


}
