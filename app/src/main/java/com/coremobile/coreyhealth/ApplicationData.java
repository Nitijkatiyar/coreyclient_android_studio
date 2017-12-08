package com.coremobile.coreyhealth;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class ApplicationData
{

    public String mApplicationName;
    public String mDisplayText;
    public String mDomain;
    public String mImage;
    public String mAppHelp;
    Bitmap mAppImage;
    // String mDisplayQuadrant;

    //Authentication Data fields
    boolean hasAuthentication;
    boolean isAsynchronous;
    String mAuthType;
    String mCredentialCount;
    String mHint;
    String mAuthHelp;
    String mConsumerKey;
    String mConsumerSecret;
    String mCallback;
    String mAuthorizeURL;
    String mRequestTokenURL;
    String mAccessTokenURL;
    String mAsynchronous;
    String mAsyncText;
    String mScope;

    ArrayList<CredentialData> credentialData = new ArrayList<CredentialData>();

    //QSubset data fields
    String mRolling;
    public String mSelectObject;
    String mHeadlineDynamic;
    String mHeadlineText;
    public String mRowCount;
    String mFiller;
    String mStatus;

    public ArrayList<RowData> rowData = new ArrayList<RowData>();

    public boolean isRolling()
    {
        return !mRolling.equals("0");
    }

    public boolean hasAuthentication()
    {

        return hasAuthentication;
    }
    
    
    
    


}
