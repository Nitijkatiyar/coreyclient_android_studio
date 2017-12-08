package com.coremobile.coreyhealth;

public class SurgConstants implements IAppConstants{
	 public String getRefreshCallsListIntent()
	    {
	    	return("com.coremobile.coreysurg.refresh_callslist");
	    }
		
		public  String getAppFilesPath()
		{
			return("/data/data/com.coremobile.coreysurg/files/");
		}
		
		public  String getDatabaseProviderContentUri()
		{
			return("content://com.coremobile.coreysurg.databaseprovider/messages");
		}
		
		public  String getDatabaseProviderContentUriPhoneCall()
		{
			return("content://com.coremobile.coreysurg.databaseprovider/phonecalls");
		}
		
		public  String getDatabaseProviderContentUriMatcherMessages()
		{ 
			return("com.coremobile.coreysurg.databaseprovider");
		
		}
		
		public  String getDatabaseProviderContentUriMatcherMessagesHash()
		{
			return("com.coremobile.provider.coreyhealth"); //Is this legacy problem?
		}
		
		public  String getDatabaseProviderContentUriMatcherPhoneCalls()
		{
			return("com.coremobile.coreysurg.databaseprovider");
		}
		
		public  String getPushNotificationReceivedIntent()
		{
			return("com.coremobile.coreysurg.pushnotification");
		}
		
		public  String getDownloadMessageServiceControlTimerFilter()
		{
			return("com.coremobile.coreysurg.timercontrol");
		}
		
		public  String getDownloadCompleteIntent()
		{
			return("com.coremobile.coreysurg.downloadcomplete");
		}
		public  String getUploadCompleteIntent()
		{
			return("com.coremobile.coreysurg.updatecompleted");
		}
		public  String getOne2OneMsgRecvdIntent()
		{
			return("com.coremobile.coreysurg.One2OneMsgRecvd");
		}
		public  String getReceivedPushIntent()
		{
			return("com.coremobile.coreysurg.receivedpush");
		}
		
		public  String getMessageTabActivityControlTimerFilter()
		{
			return("com.coremobile.coreysurg.timercontrol");
		}
		
		public  String getDownloadMessageServiceActionCompleteIntent()
		{
			return("com.coreysurg.downloader.action.COMPLETE");
		}
		/* For Urban Airship */
		public  String getGcmSender()
		{
			return("350169441553");
		}
		
		public  String getPSharpGcmSender()
		{
			return("912894998207");
		}
		public String getProductionAppKey()
		{
			return("3wBXb71EQgydQZkMuMlVLQ");
		}
		
		public String getProductionAppSecret()
		{
			return("WVFQ2JUVTs6j_Ud3oDf2DQ");
		}
		public String getDevelopmentAppKey()
		{
			//return("zy9AlcnbQxSNbJkgomdw4g");
			return("Xcon04YZRRSnYSHHiZWQ9w");
		}
		
		public String getDevelopmentAppSecret()
		{
//			return("2jh0dLdaToW974IBV2-2DQ");
					return("rV4pVTgQR3e1bicxpWQNsw");
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
		public String getAppName()
		{
			return("SURG");
		};
		public String DefaultCredentialsAppstring()
		{
			return("SURG");
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
			return("CoreySurg™ enables perioperative services team members to seamlessly communicate with one another, and take action, right from their mobile device. Location-based intelligence and predictive analytics enable workflow issues to be solved before they occur via push notifications to administrators, who can then mitigate in real time.");
		}
		public String getAboutText2()
		{
			return("Key performance indicators can also be monitored in real time.  CoreySurg™ also gives providers mobile access to just the information they need right now, based on a number of contexts (schedule, phone calls, etc.).");
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
