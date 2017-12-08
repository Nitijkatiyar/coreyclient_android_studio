package com.coremobile.coreyhealth;

public class HealthConstants  implements IAppConstants{
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
		public  String getOne2OneMsgRecvdIntent()
		{
			return("com.coremobile.coreyhealth.One2OneMsgRecvd");
		}
		public  String getDatabaseProviderContentUriMatcherMessagesHash()
		{
			return("com.coremobile.provider.coreyhealth"); //Is this legacy problem?
		}
		
		public  String getDatabaseProviderContentUriMatcherPhoneCalls()
		{
			return("com.coremobile.coreyhealth.databaseprovider");
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
			return("566949794243");
		}
		
		public  String getPSharpGcmSender()
		{
			return("960864575756");
		}
		public String getProductionAppKey()
		{
			return("zy9AlcnbQxSNbJkgomdw4g");
		}
		
		public String getProductionAppSecret()
		{
			return("2jh0dLdaToW974IBV2-2DQ");
		}
		public String getDevelopmentAppKey()
		{
			return("a6Q1UZwVSg-Y49WTF5WVOg");
		}
		
		public String getDevelopmentAppSecret()
		{
			return("M9HuPGxMSVC7tX8T3K386Q");
		}
		public String getAppName()
		{
			return("HEALTH");
		};
		public String DefaultCredentialsAppstring()
		{
			return("HEALTH");
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
			return("CoreyHealth™ enables Health services team members to seamlessly communicate with one another, and take action, right from their mobile device. Location-based intelligence and predictive analytics enable workflow issues to be solved before they occur via push notifications to administrators, who can then mitigate in real time.");
		}
		public String getAboutText2()
		{
			return("Key performance indicators can also be monitored in real time.  CoreyHealth™ also gives providers mobile access to just the information they need right now, based on a number of contexts (schedule, phone calls, etc.). www.coremobileinc.com");
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
