package com.coremobile.coreyhealth;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.coremobile.coreyhealth.googleAnalytics.CMN_Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class JSONParser {

    final String TAG = "Corey_JSONParser";

    public ArrayList<OrganizationData> buildOrganizations(JSONObject _rootObject, ArrayList<OrganizationData> organizationData, CrystalData _crystalData, Context ctx) {
        MyApplication.INSTANCE.ImagedownloadCount = 0;
        Log.d(TAG, "imagedownloadcount starting = " + MyApplication.INSTANCE.ImagedownloadCount);
        try {
            //Log.i("TAG", "buildUI start");
            // Getting the main JSONObject
            JSONObject mainObject = _rootObject.getJSONObject("Crystal");

            // Getting the sign in help text - 1st screen help text
            String appHelp = mainObject.getString("Help");
            _crystalData.mCrystalHelp = appHelp;
        /*    String isnewColor = mainObject.getString("IsNewColor");
            _crystalData.mIsNewColour=isnewColor; */
            if (mainObject.has("iButtonURL")) {
                JSONArray UrlArray = mainObject
                        .getJSONArray("iButtonURL");

                try {
                    // JSONArray UrlArray1=UrlArray.getJSONArray(0);
                    //	 Log.i("jsonparser","afterurlArray1"+UrlArray1);
                    JSONObject URL = UrlArray.getJSONObject(0);
                    Log.d("jsonparser", "afterurl" + URL);
                    String URLstring = URL.getString("URL");
                    Log.d("jsonparser", "afterurlstring" + URLstring);
                    Log.d("jsonparser", "URL url=" + URLstring);

                    //    JSONObject SURG = UrlArray.getJSONObject(1);
                    //    Log.i("jsonparser","after SURG=" +SURG);

                    String SURGstring = URL.getString("SURG");
                    Log.d("jsonparser", "surg url=" + SURGstring);

                    _crystalData.UrlList.put("URL", URLstring);
                    _crystalData.UrlList.put("SURG", SURGstring);
                    _crystalData.UrlList.put("SALES", URL.getString("SALES"));
                    _crystalData.UrlList.put("FINANCE", URL.getString("FINANCE"));
                    _crystalData.UrlList.put("HEALTH", URL.getString("HEALTH"));
                    _crystalData.UrlList.put("PATIENT", URL.getString("PATIENT"));
                    _crystalData.UrlList.put("PERIOPGO", URL.getString("PERIOPGO"));
                    _crystalData.UrlList.put("MERITUS", URL.getString("MERITUS"));
                } catch (JSONException e) {
                    Log.d("jsonparser Exeption", "surg url");
                }


            }

            if (mainObject.has("DemoAccounts")) {
                JSONArray DemoAccountArray = mainObject
                        .getJSONArray("DemoAccounts");

                try {
                    for (int i = 0; i < DemoAccountArray.length(); i++) {

                        String key = DemoAccountArray.getJSONObject(i).getString("AppType");
                        _crystalData.DefaultcredList.put(key, new ArrayList<String>());
                        _crystalData.DefaultcredList.get(key).add(DemoAccountArray.getJSONObject(i).getString("LoginId"));
                        _crystalData.DefaultcredList.get(key).add(DemoAccountArray.getJSONObject(i).getString("password"));
                        _crystalData.DefaultcredList.get(key).add(DemoAccountArray.getJSONObject(i).getString("organization"));
                    }
                } catch (JSONException e) {
                    Log.d("jsonparser Exeption", "Demo acounts");
                }


            }

            if (mainObject.has("AboutContent")) {
                try {
                    JSONObject AboutObj = mainObject.getJSONObject("AboutContent");
                    Iterator<?> keys = AboutObj.keys();
                    while (keys.hasNext()) {
                        String key = (String) keys.next();
                        _crystalData.HelpList.put(key, AboutObj.getString(key));
                    }

                } catch (JSONException e) {
                    Log.d("jsonparser Exeption", "About contents");
                }
                Log.d(TAG, "meritus helptext = " + _crystalData.HelpList.get("MERITUS"));
            }
            if (mainObject.has("AppAttributes")) {
                JSONArray DemoAccountArray = mainObject
                        .getJSONArray("AppAttributes");

                try {
                    for (int i = 0; i < DemoAccountArray.length(); i++) {

                        String key = DemoAccountArray.getJSONObject(i).getString("AppType");
                        _crystalData.AppAttrList.put(key, new ArrayList<String>());

                        String LogoUrl = DemoAccountArray.getJSONObject(i).getString("Logo");
                        if (LogoUrl != null && !LogoUrl.isEmpty()) {
                            Uri uri = Uri.parse(LogoUrl);
                            String fileName = uri.getLastPathSegment();


                            Log.d(TAG, "  json  Application_LogoUrl  "
                                    + LogoUrl + "     fileName  "
                                    + fileName);

                            if (fileName != null) {

                                fileName = fileName.toString().toLowerCase()
                                        .replaceAll("\\s+", "").replaceAll("-", "")
                                        .replaceAll("/", "").replaceAll("[(+^)_]*", "");

                                if (MyApplication.mFilesSet != null
                                        && !MyApplication.mFilesSet.contains(fileName)) {

                                    ImageDownloader imageDownloader = new ImageDownloader(
                                            ctx);
                                    imageDownloader.download(LogoUrl, fileName);

                                    MyApplication.mFilesSet.add(fileName);
                                    MyApplication.INSTANCE.ImagedownloadCount++;
                                    Log.d(TAG, "imagedownloadcount inside condition = " + MyApplication.INSTANCE.ImagedownloadCount);
                                }
                            }
                        }
                        _crystalData.AppAttrList.get(key).add(DemoAccountArray.getJSONObject(i).getString("Logo"));
                        _crystalData.AppAttrList.get(key).add(DemoAccountArray.getJSONObject(i).getString("contactemail"));

                    }
                } catch (JSONException e) {
                    Log.d("jsonparser Exeption", "Demo acounts");
                }


            }

            // Getting the JSONArray containing data for different organizations
            // (citrix,NEI etc.)
            JSONArray organization = mainObject.getJSONArray("Organization");
            //Log.i("JSONParser", "No. of entries: " + organization.length());

            // Parsing the JSONArray for organization
            for (int i = 0; i < organization.length(); i++) {
                OrganizationData orgData = new OrganizationData();
                // Log.i(TAG, "parsing org" + i);
                // Getting organization data
                JSONObject orgObject = organization.getJSONObject(i);

                // Getting the data inside orgObject
                // JSONArray availableObjects = orgObject.names();
                // Log.i(JSONParser.class.getName(), "names: " +
                // availableObjects);

                JSONObject orgAttributes = orgObject
                        .getJSONObject("@attributes");
                // Log.i("JSONParser", "names" + orgAttributes.names());

                String orgName = orgAttributes.getString("name");

                String baseURL = "";
                String alternateURL = "";


                if (orgAttributes.has("baseURL"))
                    baseURL = orgAttributes.getString("baseURL");
                if (orgAttributes.has("alternateURL"))
                    alternateURL = orgAttributes.getString("alternateURL");
                String strictSecurity = "";
                if (orgAttributes.has("strictSecurity"))
                    strictSecurity = orgAttributes.getString("strictSecurity");

                // Log.d(TAG, "DETAILS =" + orgAttributes.getString("Details")
                // );
                if (orgAttributes.has("CanAcceptClientCertificate") && orgAttributes.getString("CanAcceptClientCertificate").equalsIgnoreCase("Yes")) {
                    orgData.mCanAcceptClientCertificate = true;
                } else {
                    orgData.mCanAcceptClientCertificate = false;
                }
                orgData.mOrgName = orgName;
                orgData.mOrgBaseURL = baseURL;
                orgData.mOrgAlternateURL = alternateURL;
                orgData.mStrictSecurity = strictSecurity;
                organizationData.add(orgData);


            }


        } catch (Exception e) {
            e.getMessage();
            return null;

        }
        return organizationData;
    }

    @SuppressLint("DefaultLocale")
    public CrystalData buildUI(JSONObject _rootObject, CrystalData _crystalData, Context ctx) {
        MyApplication.INSTANCE.ImagedownloadCount = 0;
        try {
            //Log.i("TAG", "buildUI start");
            // Getting the main JSONObject
            JSONObject mainObject = _rootObject.getJSONObject("Crystal");

            // Getting the sign in help text - 1st screen help text
            String appHelp = mainObject.getString("Help");
            _crystalData.mCrystalHelp = appHelp;
            String isnewColor;
            if (mainObject.has("IsNewColor")) {
                isnewColor = mainObject.getString("IsNewColor");
            } else isnewColor = "ffff00";

            _crystalData.mIsNewColour = isnewColor;

            if (mainObject.has("UpdateTimeoutTime")) {
                CMN_Preferences.setUpdateWaitout(ctx, Long.valueOf(String.valueOf(1000 * Integer.parseInt(mainObject.getString("UpdateTimeoutTime")))));
            } else {
                CMN_Preferences.setUpdateWaitout(ctx, 10000l);
            }

            if (mainObject.has("WaitTimeBetweenUpdates")) {
                CMN_Preferences.setUpdateWaittime(ctx, Long.valueOf(String.valueOf(1000 * Integer.parseInt(mainObject.getString("WaitTimeBetweenUpdates")))));
            } else {
                CMN_Preferences.setUpdateWaittime(ctx, 10000l);
            }

            if (mainObject.has("NotificationCheckFrequencyGeneric")) {
                CMN_Preferences.setCheckNotificationtime(ctx, Long.valueOf(String.valueOf(1000 * Integer.parseInt(mainObject.getString("NotificationCheckFrequencyGeneric")))));
            } else {
                CMN_Preferences.setCheckNotificationtime(ctx, 60000l);
            }

            if (mainObject.has("APIAcknowledgementTmeout")) {
                CMN_Preferences.setApiAcknowleedgeTimeOut(ctx, mainObject.getString("APIAcknowledgementTmeout"));
            } else {
                CMN_Preferences.setApiAcknowleedgeTimeOut(ctx, "10");
            }

            String UpdateBtnColor;
            if (mainObject.has("UpdateBtnColor")) {
                UpdateBtnColor = mainObject.getString("UpdateBtnColor");
            } else UpdateBtnColor = "4579CA";
            _crystalData.mUpdateBtnColour = UpdateBtnColor;

            String SyncTimeoutTime;
            if (mainObject.has("SyncTimeoutTime")) {
                SyncTimeoutTime = mainObject.getString("SyncTimeoutTime");


            } else SyncTimeoutTime = "5";
            _crystalData.mSyncTimeoutTime = Integer.parseInt(SyncTimeoutTime);

            String AutoLogoutTime;
            if (mainObject.has("AutoLogoutTime")) {
                AutoLogoutTime = mainObject.getString("AutoLogoutTime");
                MyApplication.IDLE_COUNTER_THRESHOLD = Integer.parseInt(AutoLogoutTime);
                Log.d(TAG, "Autologout Timer value = " + MyApplication.IDLE_COUNTER_THRESHOLD);
            } else AutoLogoutTime = "5";
            _crystalData.mAutoLogoutTime = Integer.parseInt(AutoLogoutTime);

            if (mainObject.has("TabData")) {
                parseTabData(mainObject.getJSONArray("TabData"), _crystalData.mTabData);
            }
            if (mainObject.has("Contexts")) {
                parseContextData(mainObject.getJSONArray("Contexts"), _crystalData.mContextData);
            }
            if (mainObject.has("LeftContext")) {
                parseContextData(mainObject.getJSONArray("LeftContext"), _crystalData.mLeftContextData);
            }
            if (mainObject.has("RightContext")) {
                parseContextData(mainObject.getJSONArray("RightContext"), _crystalData.mRightContextData);
            }
            Log.d(TAG, " checking for FirstscreenRow0");
            if (mainObject.has("FirstScreenRow0")) {
                Log.d(TAG, " entering FirstscreenRow0");
                JSONObject jRow0Obj = mainObject.getJSONObject("FirstScreenRow0");
                Log.d(TAG, " _crystalData.mFSRow0 = " + _crystalData.mFSRow0);
                _crystalData.mFSRow0.mImageUrl = jRow0Obj.getString("imageurl");
                String FSRowImage = jRow0Obj.getString("imageurl");
                ;
                Log.d(TAG, "imagedownloadcount here = " + MyApplication.INSTANCE.ImagedownloadCount);
                if (FSRowImage != null && !FSRowImage.isEmpty()) {
                    Uri uri = Uri.parse(FSRowImage);
                    String fileName = uri.getLastPathSegment();


                    Log.d(TAG, "  json  Application_LogoUrl  "
                            + FSRowImage + "     fileName  "
                            + fileName);

                    if (fileName != null) {

                        fileName = fileName.toString().toLowerCase()
                                .replaceAll("\\s+", "").replaceAll("-", "")
                                .replaceAll("/", "").replaceAll("[(+^)_]*", "");

                        if (MyApplication.mFilesSet != null
                                && !MyApplication.mFilesSet.contains(fileName)) {

                            ImageDownloader imageDownloader = new ImageDownloader(
                                    ctx);
                            imageDownloader.download(FSRowImage, fileName);
                            MyApplication.INSTANCE.ImagedownloadCount++;
                            Log.d(TAG, "downloading " + fileName.toString());
                            Log.d(TAG, "imagedownloadcount inside condition = " + MyApplication.INSTANCE.ImagedownloadCount);

                            MyApplication.mFilesSet.add(fileName);
                        }
                    }
                }
                JSONArray mRow0Data = jRow0Obj.getJSONArray("data");
                for (int k = 0; k < mRow0Data.length(); k++) {
                    _crystalData.mFSRow0.mData.add(mRow0Data.getString(k));
                }
                Log.d(TAG, "row0 data = " + _crystalData.mFSRow0.mData);
            }
            String displayName = "";
            String NvpairKey = "";
            String NvpairValue = "";
            String currentKey = "";

            // Getting the JSONArray containing data for different organizations
            // (citrix,NEI etc.)
            JSONArray organization = mainObject.getJSONArray("Organization");
            //Log.i("JSONParser", "No. of entries: " + organization.length());

            JSONArray addattr = new JSONArray();
            JSONArray jStaticDataArray = new JSONArray();
            JSONArray imageURlArray = new JSONArray();
            JSONObject jStaticobject;

            // Parsing the JSONArray for organization
            for (int i = 0; i < organization.length(); i++) {
                OrganizationData orgData = new OrganizationData();
                //Log.i(TAG, "parsing org" + i);
                // Getting organization data
                JSONObject orgObject = organization.getJSONObject(i);

                // Getting the data inside orgObject
                //JSONArray availableObjects = orgObject.names();
                //Log.i(JSONParser.class.getName(), "names: " + availableObjects);

                // Parsing org objects
                /*
                 * Help text
                 */
                if (orgObject.has("Help")) {
                    String dashboardHelp = orgObject.getString("Help");
                    Log.d("JSONParser", "dashboardHelp =" + dashboardHelp);
                    orgData.mOrgHelp = dashboardHelp;
                }
             /*   String isnewColor = orgObject.getString("IsNewColor");
                Log.i("JSONParser", "isnewColor =" +isnewColor);
                orgData.mIsNewColour = isnewColor; */

                //Log.i("JSONParser", dashboardHelp);
                /*
                 * Help URL
                 */
                if (orgObject.has("HelpURL")) {
                    String helpurl = orgObject.getString("HelpURL");
                    Log.d("JSONParser", "orgData.mHelpUrl =" + helpurl);
                    orgData.mHelpUrl = helpurl;
                }
                Log.d("JSONParser", "orgData.mHelpUrl =" + orgData.mHelpUrl);
                
                /*
                 * Attributes
                 */
                JSONObject orgAttributes = orgObject
                        .getJSONObject("@attributes");
                //Log.i("JSONParser", "names" + orgAttributes.names());

                String orgName = orgAttributes.getString("name");
                String baseURL = "";

                if (orgAttributes.has("baseURL"))
                    baseURL = orgAttributes.getString("baseURL");


                if (orgAttributes.has("displayName"))
                    displayName = orgAttributes.getString("displayName");
                if (orgAttributes.has("RequireAutoSync")) {
                    if (orgAttributes.getString("RequireAutoSync").equals("true")) {
                        orgData.mRequireAutoSync = true;
                    } else orgData.mRequireAutoSync = false;

                }
                if (orgAttributes.has("imageURLs"))
                    imageURlArray = orgAttributes.getJSONArray("imageURLs");

                if (orgAttributes.has("FeedbackURL")) {
                    orgData.mFeedbackUrl = orgAttributes.getString("FeedbackURL");
                }
                String strictSecurity = "";
                if (orgAttributes.has("strictSecurity"))
                    strictSecurity = orgAttributes.getString("strictSecurity");

                orgData.mOrgName = orgName;
                orgData.mOrgBaseURL = baseURL;
                orgData.mStrictSecurity = strictSecurity;
                //Log.i("JSONParser", "name: " + orgName);
                orgData.mDiplayName = displayName;
                /*
                 * ListItems
                 */
                if (orgObject.has("ListItems")) {
                    JSONArray jListItemObjArray = orgObject.getJSONArray("ListItems");
                    for (int list = 0; list < jListItemObjArray.length(); list++) {
                        ListItemObj listitemobj = new ListItemObj();
                        JSONObject jListitemObj = jListItemObjArray.getJSONObject(list);
                        listitemobj.setName(jListitemObj.getString("Name"));
                        listitemobj.setScrollDirection(jListitemObj.getString("ScrollDirection"));
                        listitemobj.setOnTouch(jListitemObj.getString("OnTouch"));
                        JSONArray jItemArray = jListitemObj.getJSONArray("Items");
                        for (int litem = 0; litem < jItemArray.length(); litem++) {
                            JSONObject jListItem = jItemArray.getJSONObject(litem);
                            ListItem listitem = new ListItem();
                            listitem.setValue(jListItem.getString("Value"));
                            listitem.setImage(jListItem.getString("Image"));
                            listitem.setSubscript(jListItem.getString("Subscript"));
                            listitem.setBackgroundColor(jListItem.getString("BackgroundColor"));
                            listitemobj.ListItemArray.add(listitem);
                        }
                        //	orgData.ListItemObjArray.add(listitemobj);
                        orgData.ListItemObjMap.put(jListitemObj.getString("Name"), listitemobj);
                    }
                }
                //   Log.d(TAG,"ListItemObjMap"+orgData.ListItemObjMap);
                //  Log.d(TAG,"ListItemObjMap status array"+orgData.ListItemObjMap.get("DepartMentStatus").ListItemArray);
                /*
                 * Application
                 */
                if (orgObject.has("Application")) {
                    JSONArray applicationsArray = orgObject
                            .getJSONArray("Application");
                    Log.d("JSONParser",
                            "Applications: " + applicationsArray.length());

                    for (int app = 0; app < applicationsArray.length(); app++) {
                        Log.d(TAG, "parsing app" + app);
                        ApplicationData appData = new ApplicationData();
                        JSONObject application = applicationsArray
                                .getJSONObject(app);

                        JSONObject appAuth = null;
                        try {
                            appAuth = application.getJSONObject("Authentication");
                        } catch (JSONException e) {
                            Log.d(TAG, "Authentication exception " + appData.mApplicationName);
                        }
                        ;

                        if (appAuth != null) {
                            appData.hasAuthentication = true;

                            JSONObject authAttr = appAuth
                                    .getJSONObject("@attributes");
                            try {
                                appData.mAuthHelp = authAttr.getString("Help");
                                Log.d(TAG, "help exception " + appData.mApplicationName);
                            } catch (JSONException e) {
                            }
                            ;
                            appData.mAuthType = authAttr.getString("type");
                            if (appData.mAuthType.equals("traditional")) {
                                appData.mCredentialCount = authAttr
                                        .getString("credentialCount");
                                appData.mHint = authAttr.getString("hint");
                            } else if (appData.mAuthType.equals("oauth2")) {
                                appData.mConsumerKey = authAttr
                                        .getString("oauth_consumerKey");
                                appData.mConsumerSecret = authAttr
                                        .getString("oauth_consumerSecret");
                                appData.mCallback = authAttr
                                        .getString("oauth_callback");
                                appData.mAuthorizeURL = authAttr
                                        .getString("oauth_authorizeURL");
                                appData.mRequestTokenURL = authAttr
                                        .getString("oauth_requestTokenURL");
                                appData.mHint = authAttr.getString("hint");
                            } else if (appData.mAuthType.equals("oauth")) {
                                //Log.i(TAG,"authAttr length:" + authAttr.length() + " names " + authAttr.names());
                                //if (authAttr.length() == 10){
                                try {
                                    appData.mScope = authAttr.getString("oauth_scope");
                                } catch (JSONException e) {
                                    Log.d(TAG, "oauth scope exception " + appData.mApplicationName);
                                }
                                ;
                                //}
                                appData.mConsumerKey = authAttr
                                        .getString("oauth_consumerKey");
                                appData.mConsumerSecret = authAttr
                                        .getString("oauth_consumerSecret");
                                appData.mCallback = authAttr
                                        .getString("oauth_callback");
                                appData.mAuthorizeURL = authAttr
                                        .getString("oauth_authorizeURL");
                                appData.mRequestTokenURL = authAttr
                                        .getString("oauth_requestTokenURL");
                                appData.mAccessTokenURL = authAttr
                                        .getString("oauth_accessTokenURL");

                                appData.mHint = authAttr.getString("hint");
                            }
                            //if (appAuth.length() == 3) {
                            JSONArray authCredentials = null;
                            try {
                                authCredentials = appAuth.getJSONArray("credential");
                            } catch (JSONException e) {
                                Log.d(TAG, "credential exception " + appData.mApplicationName);
                            }
                            ;

                            for (int crt = 0; ((authCredentials != null) && (crt < authCredentials.length())); crt++) {
                                //Log.i(TAG, "parsing crt" + crt);
                                JSONObject crtObject = authCredentials
                                        .getJSONObject(crt);
                                JSONObject crtAttr = crtObject
                                        .getJSONObject("@attributes");
                                CredentialData crtData = new CredentialData();
                                crtData.mCredentialName = crtAttr
                                        .getString("name");
                                crtData.mCredentialDisplayText = crtAttr
                                        .getString("displayText");
                                crtData.mCredentialSecured = crtAttr
                                        .getInt("secured");
                                //Log.i(TAG, "crtAttr length" + crtAttr.length());
                      /*      try
                            {
                                crtData.mDefault = crtAttr.getString("default");
                            }
                            catch (Exception e)
                            {
                                crtData.mDefault = "";
                                Log.d(TAG, "default exception " + appData.mApplicationName);
                            } */
                                // Log.i(TAG,"crtData" +
                                // crtData.mCredentialDisplayText +
                                // crtData.mCredentialDisplayText +
                                // crtData.mCredentialSecured +
                                // crtData.mDefault);
                                appData.credentialData.add(crtData);

                            }
                            //}
                        } else {
                            appData.hasAuthentication = false;
                        }

                        JSONObject appAttr = application
                                .getJSONObject("@attributes");

                        appData.mApplicationName = appAttr.getString("name");

                        Log.d(TAG, "application added here " + appData.mApplicationName);
                        orgData.applications.add(appData);

                    }  //application for loop end?

                }// application has check end?
       /* 
        * Changing orgattrbute with orgObject
        */
                if (orgObject.has("AdditionalContexts")) {
                    addattr = orgObject.getJSONArray("AdditionalContexts");
                }

                orgData.additionalContext.add(addattr.toString());
             
              /*
               * StaticData
               */
                if (orgObject.has("StaticData")) {
                    jStaticDataArray = orgObject.getJSONArray("StaticData");

                    for (int k = 0; k < jStaticDataArray.length(); k++) {
                        jStaticobject = jStaticDataArray.getJSONObject(k);
                        Iterator StaticObjectIter = jStaticobject.keys();
                        while (StaticObjectIter.hasNext()) {
                            currentKey = (String) StaticObjectIter.next();
                            Log.d(TAG, "currentKey =  " + currentKey);
                            HashMap<String, String> NvPair = new HashMap<String, String>();
                            _crystalData.mStaticData.put(currentKey, NvPair);
                            JSONObject jStaticobjectSub = jStaticobject.getJSONObject(currentKey);
                            Iterator StaticObjectItersub = jStaticobjectSub.keys();

                            while (StaticObjectItersub.hasNext()) {
                                NvpairKey = (String) StaticObjectItersub.next();
                                Log.d(TAG, "NvpairKey =  " + NvpairKey);

                                NvpairValue = jStaticobjectSub.getString(NvpairKey);
                                Log.d(TAG, "NvpairValue =  " + NvpairValue);
                                _crystalData.mStaticData.get(currentKey).put(NvpairKey, NvpairValue);
                            }
                        }

                    }
                    Log.d(TAG, "staticdata =  " + _crystalData.mStaticData);
                }
              /*  end of additional contexts and static data */
                _crystalData.mOrganizationData.add(orgData);
            } //Organization for loop


        }//Try end?
        catch (Exception e) {
            e.getMessage();
            return null;
        }

        System.out.println("Contexts: " + _crystalData.mContextData);
        System.out.println("LeftContexts: " + _crystalData.mLeftContextData);
        System.out.println("RightContexts: " + _crystalData.mRightContextData);
        System.out.println("Tabdate: " + _crystalData.mTabData);
        System.out.println("Tabdata size: " + _crystalData.mTabData.size());
        return _crystalData;
    }

    private void parseContextData(JSONArray contextsArray,
                                  ArrayList<ContextData> outContextData) throws JSONException {
        for (int i = 0; i < contextsArray.length(); ++i) {
            JSONObject contextJson = contextsArray.getJSONObject(i);
            ContextData context = new ContextData();
            if (contextJson.has("Name")) context.mName = contextJson.getString("Name");
            if (contextJson.has("DisplayText"))
                context.mDisplayText = contextJson.getString("DisplayText");
            if (contextJson.has("HelpURL")) context.mHelpURL = contextJson.getString("HelpURL");
            if (contextJson.has("URL")) context.mURL = contextJson.getString("URL");
            if (contextJson.has("MessageType"))
                context.mMessageType = contextJson.getString("MessageType");
            if (contextJson.has("NeedsTab"))
                context.mNeedsTab = "true".equalsIgnoreCase(contextJson.getString("NeedsTab"));
            if (contextJson.has("TabText")) context.mTabText = contextJson.getString("TabText");
            if (contextJson.has("Position")) context.mPosition = contextJson.getInt("Position");
            try {
                if (contextJson.has("NeedsGenericView"))
                    context.mNeedsGenericView = contextJson.getBoolean("NeedsGenericView");
            } catch (JSONException e) {
                e.getMessage();
            }
            outContextData.add(context);
        }

        for (ContextData c : outContextData) {
            Log.d(TAG, "ContextData: " + c.mName + ", " + c.mMessageType);
        }
    }

    private void parseTabData(JSONArray TabsArray,
                              ArrayList<TabDtl> outTabData) throws JSONException {
        for (int i = 0; i < TabsArray.length(); ++i) {
            JSONObject tabJson = TabsArray.getJSONObject(i);
            TabDtl tabdata = new TabDtl();
            if (tabJson.has("Name")) tabdata.mName = tabJson.getString("Name");
            if (tabJson.has("DisplayText")) tabdata.mDisplayText = tabJson.getString("DisplayText");

            if (tabJson.has("URL")) tabdata.mUrl = tabJson.getString("URL");

            outTabData.add(tabdata);
        }

        for (TabDtl c : outTabData) {
            Log.d(TAG, "TabData: " + c.mName + ", " + c.mUrl);
        }
    }

}
