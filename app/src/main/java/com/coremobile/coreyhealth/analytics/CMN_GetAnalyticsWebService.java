package com.coremobile.coreyhealth.analytics;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import com.coremobile.coreyhealth.errorhandling.CMN_ErrorMessages;
import com.coremobile.coreyhealth.googleAnalytics.CMN_Preferences;
import com.coremobile.coreyhealth.newui.MessageTabActivityCMN;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

public class CMN_GetAnalyticsWebService extends AsyncTask<String, String, String> {
    private FragmentActivity mContext;
    GridView gridView;
    TextView textView;

    public CMN_GetAnalyticsWebService(FragmentActivity context, GridView gridView, TextView text) {
        mContext = context;
        this.gridView = gridView;
        textView = text;
    }

    ProgressDialog pbar;
    static final String KEY_NAME = "name", KEY_ID = "ID",
            KEY_DISPLAYCOL = "displayCol", KEY_DISPLAYROW = "displayRow",
            KEY_DISPLAYTEXT = "displayText",
            KEY_DISPLAYSUBSCRIPT = "displaySubscript", KEY_APPID = "appid",
            KEY_GRAPHTYPE = "graphType", KEY_GRAPHID = "Graphid", KEY_OPEN_URL = "openURL",
            KEY_IMAGEURL = "imageURL", KEY_ISNEW = "isNew", KEY_DATAFILE = "dataFile",
            KEY_ISEDITABLE = "isEditable", KEY_PARENT_OBJECT_ID = "parentObjectID";


    static final String KEY_OBJECT = "Object";
    List<CMN_AnalyticGraphModel> analyticsGraph_list;
    CMN_AnalyticsAdapter adapter;
    public static final String TAG = "Corey_XmlLoaderTask";


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        analyticsGraph_list = new ArrayList<CMN_AnalyticGraphModel>();

        pbar = new ProgressDialog(mContext);
        pbar.setCancelable(false);

        pbar.setMessage(CMN_ErrorMessages.getInstance().getValue(131));

        pbar.show();

    }

    @Override
    protected String doInBackground(String... params) {

        String url = null;

        url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                + "GetAnalyticsDashboard.aspx?token=" + CMN_Preferences.getUserToken(mContext);


        //Log.e(TAG, "url.." + url);

        CMN_XMLParser parser = new CMN_XMLParser();
        String xml = parser.getXmlFromUrl(url);
        Document doc = null;
        try {
            doc = parser.getDomElement(xml, mContext);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        //Log.e(TAG, "xml.." + xml);
        if (doc != null) {
            NodeList nl = doc.getElementsByTagName(KEY_OBJECT);
            CMN_AnalyticsFragment.childsData = new ArrayList<CMN_AnalyticGraphModel>();
            for (int i = 0; i < nl.getLength(); i++) {
                Element e = (Element) nl.item(i);

                CMN_AnalyticGraphModel graphModel = new CMN_AnalyticGraphModel();
                graphModel.setAppId(e.getAttribute(KEY_APPID));
                graphModel.setDisplayCol(e.getAttribute(KEY_DISPLAYCOL));
                graphModel.setDisplayRow(e.getAttribute(KEY_DISPLAYROW));
                graphModel
                        .setDisplaySubscript(e.getAttribute(KEY_DISPLAYSUBSCRIPT));
                graphModel.setDisplayText(e.getAttribute(KEY_DISPLAYTEXT));
                graphModel.setGraphID(e.getAttribute(KEY_GRAPHID));
                graphModel.setGraphType(e.getAttribute(KEY_GRAPHTYPE));
                graphModel.setId(e.getAttribute(KEY_ID));
                graphModel.setImageUrl(e.getAttribute(KEY_IMAGEURL));
                graphModel.setIsEditable(e.getAttribute(KEY_ISEDITABLE));
                graphModel.setIsNew(e.getAttribute(KEY_ISNEW));
                graphModel.setName(e.getAttribute(KEY_NAME));
                graphModel.setOpenUrl(e.getAttribute(KEY_OPEN_URL));
                graphModel.setDataFile(e.getAttribute(KEY_DATAFILE));
                graphModel.setParentid(e.getAttribute(KEY_PARENT_OBJECT_ID));
                if (!e.getAttribute(KEY_PARENT_OBJECT_ID).equalsIgnoreCase("")) {
                    CMN_AnalyticsFragment.childsData.add(graphModel);
                } else {
                    analyticsGraph_list.add(graphModel);
                }


            }

        }
        return xml;

    }

    public static String getCharacterDataFromElement(Element e) {
        Node child = e.getFirstChild();
        if (child instanceof CharacterData) {
            CharacterData cd = (CharacterData) child;
            return cd.getData();
        }
        return "";
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (pbar != null && pbar.isShowing()) {
            pbar.dismiss();
        }
        if (analyticsGraph_list.size() > 0) {
            adapter = new CMN_AnalyticsAdapter(mContext, analyticsGraph_list);
            gridView.setAdapter(adapter);
            textView.setVisibility(View.GONE);
            gridView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.VISIBLE);
            gridView.setVisibility(View.GONE);
        }

    }

}