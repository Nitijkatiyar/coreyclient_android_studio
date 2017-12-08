package com.coremobile.coreyhealth;

import com.coremobile.coreyhealth.R.drawable;

public class SalesConstants implements IAppConstants{
	/*
	private static final String mRefreshCallsListIntent="com.coremobile.coreyhealth.refresh_callslist";
	
	private static final String mAppFilesPath ="/data/data/com.coremobile.coreyhealth/files/";
	
	private static final String mDatabaseProviderContentUri="content://com.coremobile.coreyhealth.databaseprovider/messages";
	
	private static final String mDatabaseProviderContentUriPhoneCalls="content://com.coremobile.coreyhealth.databaseprovider/phonecalls";
	
	private static final String mDatabaseProviderContentUriMatcherMessages= "com.coremobile.coreyhealth.databaseprovider";
	
	private static final String mDatabaseProviderContentUriMatcherMessagesHash="com.coremobile.provider.coreyhealth"; //Is this legacy problem?
	
	private static final String mDatabaseProviderContentUriMatcherPhoneCalls= "com.coremobile.coreyhealth.databaseprovider";
	
	private static final String mPushNotificationReceivedIntent = "com.coremobile.coreyhealth.pushnotification";
	
	private static final String mDownloadMessageServiceControlTimerFilter= "com.coremobile.coreyhealth.timercontrol";
	
	private static final String mDownloadCompleteIntent ="com.coremobile.coreyhealth.downloadcomplete";
	
	private static final String mReceivedPushIntent ="com.coremobile.coreyhealth.receivedpush";
	
	private static final String mMessageTabActivityControlTimerFilter= "com.coremobile.coreyhealth.timercontrol";
	
	private static final String mDownloadMessageServiceActionCompleteIntent="com.coreyhealth.downloader.action.COMPLETE";
	
	public static String GcmSenderForCoreyHealth = "566949794243";

	public static String GcmSenderForCoreySurg = "350169441553";
	
	public static String GcmSenderForCoreySales = "889183290689";
	*/
	



	    /*
	     * Following application key and application secret key to be used in
	     * MyApplicaion.java , the keys are required for push notification . The
	     * keys are generated from UrbanAirship. We need to add the key generated
	     * from Google developer console (after registering application ), in
	     * respective UA application under Android / GCM  , under servcies 
	     * the appkey and appsecret key will be generated 
	     */
	    
	    /*
	     * CoreySurg Keys 
	     */
	/*
	    public static String DevelopmentAppSecretKeyForCoreySurgProduction = "WVFQ2JUVTs6j_Ud3oDf2DQ";

	    public static String DevelopmentAppKeyForCoreySurgProduction = "3wBXb71EQgydQZkMuMlVLQ";
	   
	    public static String DevelopmentAppSecretKeyForCoreySurgDev = "yB5uM01hRxKR9j2pBoHxEw";

	    public static String DevelopmentAppKeyForCoreySurgDev = "0OhxKJusT0SKCL7ip1KOBA";
	    
	    public static String DevelopmentAppKeyForCoreySurgCtx = "mziMIDm3T42s_gnxakSIrA";

	    public static String DevelopmentAppSecretKeyForCoreySurgCtx = "-MRs-xL9S-mkdHgUaxBhhg";
	    */
	    
	    /*
	     * CoreyHealth Keys 
	     */
	 /*   
	    public static String DevelopmentAppSecretKeyForCoreyHealthProduction = "2jh0dLdaToW974IBV2-2DQ";

	    public static String DevelopmentAppKeyForCoreyHealthProduction = "zy9AlcnbQxSNbJkgomdw4g";
	    */
	    /*
	     * CoreySales Keys 
	     */
	/*
	    public static String CoreySalesproductionAppKey = "e3f-JguJQFerAbzjQD3fRg";
	    public static String CoreySalesproductionAppSecret  = "rIW6JDNESeeI4PCQIeEgzw"; 
	    public static String CoreySalesdevelopmentAppKey = "STCHoMWcSE6TzwfX3ZZhsQ";
	    public static String CoreySalesdevelopmentAppSecret = "d4YJYGtvQfaLw59pF-swpw";
	    */
	    /*
	     * CoreyFinance Keys 
	     */

	    /*
	     * CoreyPatient Keys 
	     */
	    
	    /*
	     * The application name are used in SignActivity.java and these are passed
	     * to the server 
	     */
	  /*  
	    public static String AppNameCoreySurgDevelopment="SURG2";
	    
	    public static String AppNameCoreySurgProduction="SURG";
	    
	    public static String AppNameCoreyHealthProduction="HEALTH";
	    */
	    
	    /* For intents, push notification across the app */ 
	    
	    public String getRefreshCallsListIntent()
	    {
	    	return("com.coremobile.coreyhealth.refresh_callslist");
	    }
		
		public  String getAppFilesPath()
		{
			return("/data/data/com.coremobile.coreyhealth/files/");
		}
		
		public  String getDatabaseProviderContentUri()
		{
			return("content://com.coremobile.coreyhealth.databaseprovider/messages");
		}
		
		public  String getDatabaseProviderContentUriPhoneCall()
		{
			return("content://com.coremobile.coreyhealth.databaseprovider/phonecalls");
		}
		
		public  String getDatabaseProviderContentUriMatcherMessages()
		{ 
			return("com.coremobile.coreyhealth.databaseprovider");
		
		}
		
		public  String getDatabaseProviderContentUriMatcherMessagesHash()
		{
			return("com.coremobile.provider.coreyhealth"); //Is this legacy problem?
		}
		
		public  String getDatabaseProviderContentUriMatcherPhoneCalls()
		{
			return("com.coremobile.coreyhealth.databaseprovider");
		}
		public  String getOne2OneMsgRecvdIntent()
		{
			return("com.coremobile.coreyhealth.One2OneMsgRecvd");
		}
		public  String getPushNotificationReceivedIntent()
		{
			return("com.coremobile.coreyhealth.pushnotification");
		}
		
		public  String getDownloadMessageServiceControlTimerFilter()
		{
			return("com.coremobile.coreyhealth.timercontrol");
		}
		
		public  String getDownloadCompleteIntent()
		{
			return("com.coremobile.coreyhealth.downloadcomplete");
		}
		
		public  String getUploadCompleteIntent()
		{
			return("com.coremobile.coreyhealth.updatecompleted");
		}
		public  String getReceivedPushIntent()
		{
			return("com.coremobile.coreyhealth.receivedpush");
		}
		
		public  String getMessageTabActivityControlTimerFilter()
		{
			return("com.coremobile.coreyhealth.timercontrol");
		}
		
		public  String getDownloadMessageServiceActionCompleteIntent()
		{
			return("com.coreyhealth.downloader.action.COMPLETE");
		}
		/* For Urban Airship */
		public  String getGcmSender()
		{
			return("889183290689");
		}
		public  String getPSharpGcmSender()
		{
			return("425886649714");
		}
		
		public String getProductionAppKey()
		{
			return("e3f-JguJQFerAbzjQD3fRg");
		}
		
		public String getProductionAppSecret()
		{
			return("rIW6JDNESeeI4PCQIeEgzw");
		}
		public String getDevelopmentAppKey()
		{
			return("STCHoMWcSE6TzwfX3ZZhsQ");
		}
		
		public String getDevelopmentAppSecret()
		{
			return("d4YJYGtvQfaLw59pF-swpw");
		}
		public String getAppName()
		{
			return("SALES");
		};
		public String DefaultCredentialsAppstring()
		{
			return("SALES");			
		}
		
		public int getRow0DrawableId()
		{
			return(drawable.ic_calendar);
			
		//	return(R.drawable.patient_icon);
		}  
		public int getSplashId()
		{
			return(drawable.splash_fg);
			
		}  
		@Override
		public String getAboutText1() {
			// TODO Auto-generated method stub
			return ("Corey� the only application that delivers just the relevent actionable information from Social Media, Cloud, Big Data and Enterprise applications to your mobile device");
		}

		@Override
		public String getAboutText2() {
			// TODO Auto-generated method stub
			return ("Core Mobile Inc. provides the latest innovations in context based personalization of information in real time for professionals in Financial services, Healthcare, Manufacturing \n www.coremobileinc.com");
		}  
		public String getAppVersionInfo()
		{
			return("Corey� version %1$s\nCopyright Core Mobile, Inc.\n U.S. Patent No. 8,606,923");
		}
		public String getAppAboutTitle()
		{
			return("About Corey");
		}
		public String getCompanyName()
		{
			return("Core Mobile, Inc");
		}
		public String getDashboardEditTextRow1()
		{
			return("Message");
		}
		public String getDashboardEditTextRow2()
		{
			return("Update");
		}
		
		public String getDashboard121msgTextRow1()
		{
			return("Message");
		}
}
