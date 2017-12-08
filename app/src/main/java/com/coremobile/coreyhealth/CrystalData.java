package com.coremobile.coreyhealth;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

public class CrystalData
{
    public enum ContextListType {
        LEFT,
        RIGHT
    }

    public String mCrystalHelp;
    public String mIsNewColour;
    public String mUpdateBtnColour;
    public int mSyncTimeoutTime;
    public int mAutoLogoutTime;
    public FirstScreenRow0 mFSRow0 = new FirstScreenRow0() ;
    public ArrayList<ContextData> mContextData = new ArrayList<ContextData>();
    public ArrayList<TabDtl> mTabData = new ArrayList<TabDtl>();
    public ArrayList<ContextData> mLeftContextData = new ArrayList<ContextData>();
    public ArrayList<ContextData> mRightContextData = new ArrayList<ContextData>();
 //   public JSONObject pcpdata = new JSONObject();
    public JSONArray pcpdata = new JSONArray();
    HashMap<String,String> UrlList = new HashMap<String,String>();
    HashMap<String,String> HelpList = new HashMap<String,String>();
    HashMap<String, ArrayList<String>> DefaultcredList =  new HashMap<String, ArrayList<String>>();
    HashMap<String, ArrayList<String>> AppAttrList =  new HashMap<String, ArrayList<String>>();
    HashMap<String, HashMap<String, String>> mStaticData = new HashMap<String, HashMap<String, String>>();
    ArrayList<OrganizationData> mOrganizationData = new ArrayList<OrganizationData>();

    public ContextData getContext(String name, ContextListType whichList) {
        ArrayList<ContextData> list;
        if (whichList == ContextListType.LEFT) {
            list = mLeftContextData;
        } else { // RIGHT
            list = mRightContextData;
        }
        for (ContextData context: list) {
            if (name.equals(context.mName)) {
                return context;
            }
        }
        return null;
    }
}
