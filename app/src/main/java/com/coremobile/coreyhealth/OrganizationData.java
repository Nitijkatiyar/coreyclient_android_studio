package com.coremobile.coreyhealth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class OrganizationData
{


    String mOrgHelp;
     String mIsNewColour;
    String mOrgName;
    String mOrgImage;
    String mOrgBaseURL;
    String mOrgAlternateURL;
    Boolean mOrgBaseURLFailed;
    Boolean mOrgAltURLFailed;
    String mStrictSecurity;
    Boolean mCanAcceptClientCertificate;
    String mDiplayName;
    String mHelpUrl;
    String mIButtonurl;
    Boolean mRequireAutoSync;
    String mFeedbackUrl;
    
    ArrayList<ApplicationData> applications = new ArrayList<ApplicationData>();
    public Set<String> additionalContext = new HashSet<String>();
    
    public  String mImageHashMapString ;
    
    ArrayList<ListItemObj> ListItemObjArray = new ArrayList<ListItemObj>();
    HashMap<String, ListItemObj> ListItemObjMap =  new HashMap<String, ListItemObj>();
}
