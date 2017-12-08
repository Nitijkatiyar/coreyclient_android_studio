package com.coremobile.coreyhealth.analytics;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.coremobile.coreyhealth.R;
import com.coremobile.coreyhealth.Utils;
import com.coremobile.coreyhealth.WebViewActivityCMN;
import com.coremobile.coreyhealth.googleFit.CMN_WearableDataActivity;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Nitij Katiyar
 */
public class CMN_AnalyticsFragment extends Fragment {
    ViewGroup root;
    String TAG = "Corey_CMN_AnalyticsFragment";

    GridView gridView;
    TextView noData;
    public static ArrayList<CMN_AnalyticGraphModel> childsData = new ArrayList<CMN_AnalyticGraphModel>();

    private GoogleApiClient mClient = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        root = (ViewGroup) inflater.inflate(R.layout.fragment_analytics, null);

        gridView = (GridView) root.findViewById(R.id.gridView);

        noData = (TextView) root.findViewById(R.id.nodataTextView);

        getAnalytics(noData);

        gridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                CMN_AnalyticGraphModel model = (CMN_AnalyticGraphModel) arg0
                        .getAdapter().getItem(arg2);

                ArrayList<CMN_AnalyticGraphModel> childs = new ArrayList<CMN_AnalyticGraphModel>();
                for (int i = 0; i < childsData.size(); i++) {
                    if (childsData.get(i).getParentid().equalsIgnoreCase(model.getId())) {
                        childs.add(childsData.get(i));
                        //Log.e("addingData", "childAdding");
                    }
                }
                if (model.getId().equalsIgnoreCase("1001") && model.getDisplayText().equalsIgnoreCase("Data from Wearables")) {

                    Intent intent = new Intent(getActivity(), CMN_WearableDataActivity.class);
                    getActivity().startActivity(intent);

                } else if (childs.size() > 0) {

                    Intent intent = new Intent(getActivity(), CMN_AnalyticsChildsActivityCMN.class);
                    intent.putExtra("data", childs);
                    getActivity().startActivity(intent);

                } else if (model.getOpenUrl() != null && !model.getOpenUrl().isEmpty()) {
                    //Log.d(TAG, "webview activity" + model.getOpenUrl());
                    //Log.d(TAG, "webview activity" + model.getDisplayText());
                    Intent urlIntent = new Intent(getActivity(),
                            WebViewActivityCMN.class);
                    urlIntent.putExtra("myurl", model.getOpenUrl());
                    urlIntent.putExtra("objname", "Analytics");
                    startActivity(urlIntent);
                } else if (model.getGraphType() != null && !model.getGraphType().isEmpty()) {
                    Intent intent = new Intent(getActivity(),
                            CMN_AnalyticsGraphActivityCMN.class);
                    //Log.d(TAG, "graphtype selected");
                    intent.putExtra("data", (Serializable) model);
                    startActivity(intent);
                } else {
                    if (model.getGraphID() != null) {
                        CMN_GetGraphDataWebService dataWebService = new CMN_GetGraphDataWebService(getActivity());
                        dataWebService.execute("" + model.getGraphID(), model.getDisplayText());
                    }
                }


            }
        });


        return root;

    }

    public void getAnalytics(TextView textview) {
        if (Utils.checkInternetConnection()) {
            CMN_GetAnalyticsWebService allMessages = new CMN_GetAnalyticsWebService(
                    getActivity(), gridView, textview);
            allMessages.execute();
        } else {
        }
    }


}
