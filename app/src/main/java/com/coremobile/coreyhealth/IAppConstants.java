package com.coremobile.coreyhealth;

public interface IAppConstants {
	public String getRefreshCallsListIntent();
    
	public  String getAppFilesPath();
	
	public  String getDatabaseProviderContentUri();	
	
	public  String getDatabaseProviderContentUriPhoneCall();	
	
	public  String getDatabaseProviderContentUriMatcherMessages();
	
	public  String getDatabaseProviderContentUriMatcherMessagesHash();	
	
	public  String getDatabaseProviderContentUriMatcherPhoneCalls();	
	
	public  String getPushNotificationReceivedIntent();	
	
	public  String getDownloadMessageServiceControlTimerFilter();	
	
	public  String getDownloadCompleteIntent();	
	
	public  String getReceivedPushIntent();	
	
	public  String getMessageTabActivityControlTimerFilter();	
	
	public  String getDownloadMessageServiceActionCompleteIntent();
	
	public  String getUploadCompleteIntent();
	
	/* For Urban Airship */
	public  String getGcmSender();	
	
	public String getProductionAppKey();	
	
	public String getProductionAppSecret();
	
	public String getDevelopmentAppKey();	
	
	public String getDevelopmentAppSecret();
	
	public String getAppName();
	
	public String DefaultCredentialsAppstring();

	public int getRow0DrawableId();
	
	public int getSplashId();
	
	public String getAboutText1();
	
	public String getAboutText2();
	
	public String getAppVersionInfo();
	
	public String getAppAboutTitle();
	
	public String getCompanyName();
	
	public String getOne2OneMsgRecvdIntent();
	
	public String getDashboardEditTextRow1();
	
	public String getDashboardEditTextRow2();
	
	public String getDashboard121msgTextRow1();
	
	public  String getPSharpGcmSender();


    // ----------------------------
    // constants common to all apps
    // ----------------------------
    public final static String ACTION_SYNC_COMPLETE = "com.coremobile.corey.SYNC_COMPLETE";
    public final static String EXTRA_COREY_CONTEXT = "com.coremobile.corey.CONTEXT";

    // when waiting for a pull or sync to complete, sleep times to be used
    public final static int INITIAL_WAIT_TIME = 3000; // millis
    public final static int LOOP_WAIT_TIME = 10000; // millis
}
