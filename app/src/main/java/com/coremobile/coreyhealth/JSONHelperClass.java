package com.coremobile.coreyhealth;

import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

//import android.util.Log;

public class JSONHelperClass {

    CrystalData mCrystalData;
    String TAG = "Corey_jsonhelperclass";

    public JSONHelperClass() {
        //Log.i("JSONHelperClass","constructor MyApplication.crystalData" + MyApplication.crystalData.mCrystalHelp);
        mCrystalData = MyApplication.crystalData;
    }

    public int getOrgIndex(String _orgName) {
        int orgIndex = 0;
        for (int i = 0; i < mCrystalData.mOrganizationData.size(); i++) {
            if ((mCrystalData.mOrganizationData.get(i).mOrgName).equals(_orgName)) {
                orgIndex = i;
            }
        }
        return orgIndex;
    }

    public int getAppIndex(String _appName, String _orgName) {
        int appIndex = 0;
        int orgIndex = getOrgIndex(_orgName);
        for (int i = 0; i < mCrystalData.mOrganizationData.get(orgIndex).applications.size(); i++) {
            if ((mCrystalData.mOrganizationData.get(orgIndex).applications.get(i).mApplicationName).equals(_appName)) {
                appIndex = i;
            }
        }
        return appIndex;
    }

    public ArrayList<ApplicationData> getAllApplicationData(String _organizationName) {
        int orgIndex = getOrgIndex(_organizationName);
        if (orgIndex >= mCrystalData.mOrganizationData.size()) return null;
        //Log.i("JSONHelperClass","org count: " + mCrystalData.mOrganizationData.size());
        return mCrystalData.mOrganizationData.get(orgIndex).applications;
    }

    public ApplicationData getApplicationData(String _appName, String _orgName) {
        int orgIndex = getOrgIndex(_orgName);
        int appIndex = getAppIndex(_appName, _orgName);
        return mCrystalData.mOrganizationData.get(orgIndex).applications.get(appIndex);
    }

    public String getApplicationMainTextForRow(String _appName, String _orgName, int row) {

        ApplicationData appData = getApplicationData(_appName, _orgName);
        if (appData.isRolling()) {
            return appData.rowData.get(0).mMaintextText;
        } else {
            return appData.rowData.get(row).mMaintextText;
        }

    }

    public String getApplicationSubscriptForRow(String _appName, String _orgName, int row) {

        ApplicationData appData = getApplicationData(_appName, _orgName);
        if (appData.isRolling()) {
            return appData.rowData.get(0).mSubscriptText;
        } else {
            return appData.rowData.get(row).mSubscriptText;
        }

    }

    public ApplicationData getApplicationData(int _index, String _orgName) {
        Log.d("JEELANI", "getApplicationData (1)");
        int orgIndex = getOrgIndex(_orgName);
        return mCrystalData.mOrganizationData.get(orgIndex).applications.get(_index);
    }

    public OrganizationData getOrganization(String _orgName) {
        for (int i = 0; i < mCrystalData.mOrganizationData.size(); i++) {
            if (mCrystalData.mOrganizationData.get(i).mOrgName.equalsIgnoreCase(_orgName)) {
                return mCrystalData.mOrganizationData.get(i);
            }
        }
        return null;
    }

    public ApplicationData getApplicationData(int _appIndex, int _orgIndex) {
        Log.d("JEELANI", "getApplicationData (2)");
        return mCrystalData.mOrganizationData.get(_orgIndex).applications.get(_appIndex);
    }

    public String getBaseURL(String _orgName) {
        for (int i = 0; i < MyApplication.organizationData.size(); i++) {
            if (MyApplication.organizationData.get(i).mOrgName.equalsIgnoreCase(_orgName)) {
         //       AppConfig.isClientCertificateEnabled = MyApplication.organizationData.get(i).mCanAcceptClientCertificate;
                return MyApplication.organizationData.get(i).mOrgBaseURL;
            }
        }

        return null;
    }


    public Boolean isBaseURLFailed(String _orgName) {
        for (int i = 0; i < MyApplication.organizationData.size(); i++) {
            if (MyApplication.organizationData.get(i).mOrgName.equalsIgnoreCase(_orgName)) {
                return MyApplication.organizationData.get(i).mOrgBaseURLFailed;
            }
        }
        return false;
    }

    public Boolean isAltURLFailed(String _orgName) {
        for (int i = 0; i < MyApplication.organizationData.size(); i++) {
            if (MyApplication.organizationData.get(i).mOrgName.equalsIgnoreCase(_orgName)) {
                return MyApplication.organizationData.get(i).mOrgAltURLFailed;
            }
        }
        return false;
    }

    public void setBaseURLFailed(String _orgName) {
        for (int i = 0; i < MyApplication.organizationData.size(); i++) {
            if (MyApplication.organizationData.get(i).mOrgName.equalsIgnoreCase(_orgName)) {
                MyApplication.organizationData.get(i).mOrgBaseURLFailed = true;
            }
        }
    }

    public void resetBaseURLFailed(String _orgName) {
        for (int i = 0; i < MyApplication.organizationData.size(); i++) {
            if (MyApplication.organizationData.get(i).mOrgName.equalsIgnoreCase(_orgName)) {
                MyApplication.organizationData.get(i).mOrgBaseURLFailed = false;
            }
        }
    }

    public void setAltURLFailed(String _orgName) {
        for (int i = 0; i < MyApplication.organizationData.size(); i++) {
            if (MyApplication.organizationData.get(i).mOrgName.equalsIgnoreCase(_orgName)) {
                MyApplication.organizationData.get(i).mOrgAltURLFailed = true;
            }
        }
    }

    public void resetAltURLFailed(String _orgName) {
        for (int i = 0; i < MyApplication.organizationData.size(); i++) {
            if (MyApplication.organizationData.get(i).mOrgName.equalsIgnoreCase(_orgName)) {
                MyApplication.organizationData.get(i).mOrgAltURLFailed = false;
            }
        }
    }

    public void updateBaseURL(String _orgName, String _url) {
        for (int i = 0; i < MyApplication.organizationData.size(); i++) {
            if (MyApplication.organizationData.get(i).mOrgName.equalsIgnoreCase(_orgName)) {
                MyApplication.organizationData.get(i).mOrgBaseURL = _url;

            }
        }

    }

    public String getAlternateURL(String _orgName) {
        for (int i = 0; i < MyApplication.organizationData.size(); i++) {
            if (MyApplication.organizationData.get(i).mOrgName.equalsIgnoreCase(_orgName)) {
                return MyApplication.organizationData.get(i).mOrgAlternateURL;
            }
        }
        return null;
    }

    public String getiButtonURL(String url) {
        String url1 = mCrystalData.UrlList.get(url);
        if (url1 == null || url1.equals("")) {
            return mCrystalData.UrlList.get("URL");
        } else return url1;
        //	return mCrystalData.UrlList.get(url);

    }

    public ArrayList<String> getDefaultCredentials(String Application) {
        return mCrystalData.DefaultcredList.get(Application);
    }

    public ArrayList<String> getAppAttributes(String Application) {
        return mCrystalData.AppAttrList.get(Application);
    }

    public HashMap<String, String> getStatusColourMap() {
        return mCrystalData.mStaticData.get("DepartMentStatus");
    }

    public ListItemObj getListItemObj(String ListItemObjName, String _orgName) {
        int orgIndex = getOrgIndex(_orgName);
        // 	  Log.d("JsonHelper","ListItemObjName"+ListItemObjName);
        //	  Log.d("JsonHelper","ListItemObj"+mCrystalData.mOrganizationData.get(orgIndex).ListItemObjMap.get(ListItemObjName));
        return mCrystalData.mOrganizationData.get(orgIndex).ListItemObjMap.get(ListItemObjName);
    }

    public ArrayList<String> getListItemArray(String ListItemObjName, String _orgName) {
        int orgIndex = getOrgIndex(_orgName);
        Log.d("Jsonhelper", "ListItemObjName" + ListItemObjName);
        if (mCrystalData.mOrganizationData.get(orgIndex).ListItemObjMap.get(ListItemObjName) != null) {
            ListItemObj listitemobj = mCrystalData.mOrganizationData.get(orgIndex).ListItemObjMap.get(ListItemObjName);
            Log.d("Jsonhelper", "ListItemObjName" + listitemobj.mName);
            Log.d("Jsonhelper", "ListItemObjName" + listitemobj.ListItemArray.get(0));
            ArrayList<ListItem> listitemarray = listitemobj.ListItemArray;
            ArrayList<String> ltarray = new ArrayList<String>();
            for (int i = 0; i < listitemarray.size(); i++) {
                ltarray.add(listitemarray.get(i).getValue());
            }
            Log.d("Jsonhelper", "ltarray" + ltarray);
            return ltarray;
        } else return null;
    }

    public int getIsNewColour(String _orgName) {
        String color = "#ffff00";
        String colorRaw;
        int orgIndex = getOrgIndex(_orgName);
        colorRaw = mCrystalData.mOrganizationData.get(orgIndex).mIsNewColour;
        if (colorRaw != null) {
            color = "#" + colorRaw;
        }

        int hex = Color.parseColor(color);
        return hex;
    }

    public int getIsNewColour() {
        String color = "#ffff00";
        String colorRaw;
        // int orgIndex = getOrgIndex(_orgName);
        colorRaw = mCrystalData.mIsNewColour;
        if (colorRaw != null) {
            color = "#" + colorRaw;
            Log.d(TAG, "isNewcolor =" + color);
        }

        int hex = Color.parseColor(color);
        Log.d(TAG, "Isnewcolorhex= " + hex);
        return hex;
    }

    public String getUpdateBtnColour() {
        String color = "#4579CA";
        String colorRaw;
        // int orgIndex = getOrgIndex(_orgName);
        colorRaw = mCrystalData.mUpdateBtnColour;
        if (colorRaw != null) {
            color = "#" + colorRaw;
        }

        //	int hex = Color.parseColor(color);
        //	return hex;
        Log.d(TAG, "updatebuttoncolor =" + color);
        return color;
    }

    public int getAutoLogoutTime() {
        return mCrystalData.mSyncTimeoutTime;
    }

    public int getSyncTimeoutTime() {
        return mCrystalData.mAutoLogoutTime;
    }

    public String getFeedbackUrl(String _orgName) {
        int orgIndex = getOrgIndex(_orgName);

        return mCrystalData.mOrganizationData.get(orgIndex).mFeedbackUrl;
    }

    public ArrayList<ContextData> getTabContexts() {

        ArrayList<ContextData> mctxdata = mCrystalData.mContextData;
        ArrayList<ContextData> ctxdata = new ArrayList<ContextData>();
        for (ContextData ctxd : mctxdata) {
            if (ctxd.isNeedTab()) ctxdata.add(ctxd);
        }
        return ctxdata;
    }

    public ArrayList<TabDtl> getTabDtl() {
        Log.d(TAG, "tabdata size  = " + mCrystalData.mTabData);
        return mCrystalData.mTabData;
    }

    public FirstScreenRow0 getFSRow0() {
        Log.d(TAG, "MFSrow0image = " + mCrystalData.mFSRow0.mImageUrl);
        return mCrystalData.mFSRow0;
    }

}
