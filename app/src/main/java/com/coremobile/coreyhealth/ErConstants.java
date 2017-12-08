package com.coremobile.coreyhealth;

public class ErConstants implements IAppConstants{
	public String getRefreshCallsListIntent()
    {
    	return("com.coremobile. .refresh_callslist");
    }
	
	public  String getAppFilesPath()
	{
		return("/data/data/com.coremobile.coreyer/files/");
	}
	
	public  String getDatabaseProviderContentUri()
	{
		return("content://com.coremobile.coreyer.databaseprovider/messages");
	}
	
	public  String getDatabaseProviderContentUriPhoneCall()
	{
		return("content://com.coremobile.coreyer.databaseprovider/phonecalls");
	}
	
	public  String getDatabaseProviderContentUriMatcherMessages()
	{ 
		return("com.coremobile.coreyer.databaseprovider");
	
	}
	
	public  String getDatabaseProviderContentUriMatcherMessagesHash()
	{
		return("com.coremobile.provider.coreyhealth"); //Is this legacy problem?
	}
	
	public  String getDatabaseProviderContentUriMatcherPhoneCalls()
	{
		return("com.coremobile.coreyer.databaseprovider");
	}
	
	public  String getPushNotificationReceivedIntent()
	{
		return("com.coremobile.coreyer.pushnotification");
	}
	
	public  String getDownloadMessageServiceControlTimerFilter()
	{
		return("com.coremobile.coreyer.timercontrol");
	}
	
	public  String getDownloadCompleteIntent()
	{
		return("com.coremobile.coreyer.downloadcomplete");
	}
	public  String getUploadCompleteIntent()
	{
		return("com.coremobile.coreyer.updatecompleted");
	}
	public  String getOne2OneMsgRecvdIntent()
	{
		return("com.coremobile.coreyer.One2OneMsgRecvd");
	}
	public  String getReceivedPushIntent()
	{
		return("com.coremobile.coreyer.receivedpush");
	}
	
	public  String getMessageTabActivityControlTimerFilter()
	{
		return("com.coremobile.coreyer.timercontrol");
	}
	
	public  String getDownloadMessageServiceActionCompleteIntent()
	{
		return("com.coreyer.downloader.action.COMPLETE");
	}
	/* For Urban Airship */
	public  String getGcmSender()
	{
		return("52368178325");
	}
	
	public  String getPSharpGcmSender()
	{
		return("52368178325");
	}
	public String getProductionAppKey()
	{
		return("vEoMXP-jSXC5YFtHclukeA");
	}
	
	public String getProductionAppSecret()
	{
		return("G-J8UQy1S56F0V_xGEtmWg");
	}
	public String getDevelopmentAppKey()
	{
		//return("zy9AlcnbQxSNbJkgomdw4g");
		return("");
	}
	
	public String getDevelopmentAppSecret()
	{
//		return("2jh0dLdaToW974IBV2-2DQ");
				return("");
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
	public String getAppName()
	{
		return("COREYER");
	};
	public String DefaultCredentialsAppstring()
	{
		return("COREYER");
	}
	public int getRow0DrawableId()
	{
		return(R.drawable.patient_icon);
	}  
	public int getSplashId()
	{
		return(R.drawable.splash_fgh);
		
	}  
	public String getAboutText1()
	{
		return("CoreyER™ enables perioperative services team members to seamlessly communicate with one another, and take action, right from their mobile device. Location-based intelligence and predictive analytics enable workflow issues to be solved before they occur via push notifications to administrators, who can then mitigate in real time.");
	}
	public String getAboutText2()
	{
		return("Key performance indicators can also be monitored in real time.  Coreyer™ also gives providers mobile access to just the information they need right now, based on a number of contexts (schedule, phone calls, etc.).");
	}
	public String getAppVersionInfo()
	{
		return("Corey™ version %1$s\nCopyright Core Mobile, Inc.\n U.S. Patent No. 8,606,923");
	}
	public String getAppAboutTitle()
	{
		return("About Corey");
	}
	public String getCompanyName()
	{
		return("Core Mobile, Inc");
	}

}
