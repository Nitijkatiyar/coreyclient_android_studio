package com.coremobile.coreyhealth;

public class PeriopConstants implements IAppConstants{
	public String getRefreshCallsListIntent()
    {
    	return("com.coremobile.periop.refresh_callslist");
    }
	
	public  String getAppFilesPath()
	{
		return("/data/data/com.coremobile.periop/files/");
	}
	
	public  String getDatabaseProviderContentUri()
	{
		return("content://com.coremobile.periop.databaseprovider/messages");
	}
	
	public  String getDatabaseProviderContentUriPhoneCall()
	{
		return("content://com.coremobile.periop.databaseprovider/phonecalls");
	}
	
	public  String getDatabaseProviderContentUriMatcherMessages()
	{ 
		return("com.coremobile.periop.databaseprovider");
	
	}
	
	public  String getDatabaseProviderContentUriMatcherMessagesHash()
	{
		return("com.coremobile.provider.coreyhealth"); //Is this legacy problem?
	}
	
	public  String getDatabaseProviderContentUriMatcherPhoneCalls()
	{
		return("com.coremobile.periop.databaseprovider");
	}
	
	public  String getPushNotificationReceivedIntent()
	{
		return("com.coremobile.periop.pushnotification");
	}
	
	public  String getDownloadMessageServiceControlTimerFilter()
	{
		return("com.coremobile.periop.timercontrol");
	}
	public  String getOne2OneMsgRecvdIntent()
	{
		return("com.coremobile.periop.One2OneMsgRecvd");
	}
	public  String getDownloadCompleteIntent()
	{
		return("com.coremobile.periop.downloadcomplete");
	}
	
	public  String getUploadCompleteIntent()
	{
		return("com.coremobile.periop.updatecompleted");
	}
	public  String getReceivedPushIntent()
	{
		return("com.coremobile.periop.receivedpush");
	}
	
	public  String getMessageTabActivityControlTimerFilter()
	{
		return("com.coremobile.periop.timercontrol");
	}
	
	public  String getDownloadMessageServiceActionCompleteIntent()
	{
		return("com.periop.downloader.action.COMPLETE");
	}
	/* For Urban Airship */
	public  String getGcmSender()
	{
		return("151747168713");
	}
	
	public  String getPSharpGcmSender()
	{
		return("151747168713");
	}
	public String getProductionAppKey()
	{
		return("BUPefwL3RP6kinklVBk8bQ");
	}
	
	public String getProductionAppSecret()
	{
		return("niIr9S7GS4mpZxGGk15iVg");
	}
	public String getDevelopmentAppKey()
	{
		//return("zy9AlcnbQxSNbJkgomdw4g");
		return("Xcon04YZRRSnYSHHiZWQ9w");
	}
	
	public String getDevelopmentAppSecret()
	{
//		return("2jh0dLdaToW974IBV2-2DQ");
				return("rV4pVTgQR3e1bicxpWQNsw");
	}
	public String getAppName()
	{
		return("PERIOPGO");
	};
	public String DefaultCredentialsAppstring()
	{
		return("PERIOPGO");
	}
	public int getRow0DrawableId()
	{
		return(R.drawable.patient_icon);
	}  
	public int getSplashId()
	{
		return(R.drawable.splash_fgp);
		
	}  
	
	public String getAboutText1()
	{
		return("PeriOp™ enables perioperative services team members to seamlessly communicate with one another while on the move, resulting in increased team efficiency and coordination.");
	}
	public String getAboutText2()
	{
		return("The solution enables 1:1 peer-to-peer communication and communication with an entire team, directly from a provider's mobile device.");
	}
	public String getAppVersionInfo()
	{
		return("Periop™ version %1$s\nCopyright Perioperative Informatics\n");
	}
	public String getAppAboutTitle()
	{
		return("About Periop");
	}
	public String getCompanyName()
	{
		return("Perioperative Informatics");
	}
	public String getDashboardEditTextRow1()
	{
		return("Message Team");
	}
	public String getDashboard121msgTextRow1()
	{
		return("Message 1:1");
	}
	public String getDashboardEditTextRow2()
	{
		return("Update");
	}
	
}
