package com.coremobile.coreyhealth;

public class MeritusConstants implements IAppConstants{
	 public String getRefreshCallsListIntent()
	    {
	    	return("com.coremobile.coreymeritus.refresh_callslist");
	    }
		
		public  String getAppFilesPath()
		{
			return("/data/data/com.coremobile.coreymeritus/files/");
		}
		
		public  String getDatabaseProviderContentUri()
		{
			return("content://com.coremobile.coreymeritus.databaseprovider/messages");
		}
		
		public  String getDatabaseProviderContentUriPhoneCall()
		{
			return("content://com.coremobile.coreymeritus.databaseprovider/phonecalls");
		}
		
		public  String getDatabaseProviderContentUriMatcherMessages()
		{ 
			return("com.coremobile.coreymeritus.databaseprovider");
		
		}
		
		public  String getDatabaseProviderContentUriMatcherMessagesHash()
		{
			return("com.coremobile.provider.coreyhealth"); //Is this legacy problem?
		}
		
		public  String getDatabaseProviderContentUriMatcherPhoneCalls()
		{
			return("com.coremobile.coreymeritus.databaseprovider");
		}
		
		public  String getPushNotificationReceivedIntent()
		{
			return("com.coremobile.coreymeritus.pushnotification");
		}
		
		public  String getDownloadMessageServiceControlTimerFilter()
		{
			return("com.coremobile.coreymeritus.timercontrol");
		}
		
		public  String getDownloadCompleteIntent()
		{
			return("com.coremobile.coreymeritus.downloadcomplete");
		}
		
		public  String getUploadCompleteIntent()
		{
			return("com.coremobile.coreymeritus.updatecompleted");
		}
		
		public  String getReceivedPushIntent()
		{
			return("com.coremobile.coreymeritus.receivedpush");
		}
		
		public  String getMessageTabActivityControlTimerFilter()
		{
			return("com.coremobile.coreymeritus.timercontrol");
		}
		
		public  String getDownloadMessageServiceActionCompleteIntent()
		{
			return("com.coreymeritus.downloader.action.COMPLETE");
		}
		/* For Urban Airship */
		public  String getGcmSender()
		{
			return("578933389559");  
		}
		
		public  String getPSharpGcmSender()
		{
			return("578933389559");
		}
		
		public String getProductionAppKey()
		{
			return("AIzaSyAY1ZAAfZkbvbh0wcC3Re0gFReD3bNZB7w");
		}
		
		public String getProductionAppSecret()
		{
			return("AIzaSyAY1ZAAfZkbvbh0wcC3Re0gFReD3bNZB7w");
		}
		public String getDevelopmentAppKey()
		{
			return("AIzaSyAY1ZAAfZkbvbh0wcC3Re0gFReD3bNZB7w");
		}
		
		public String getDevelopmentAppSecret()
		{
			return("AIzaSyAY1ZAAfZkbvbh0wcC3Re0gFReD3bNZB7w");
		}
		public String getAppName()
		{
			return("MERITUS");
		};
		public String DefaultCredentialsAppstring()
		{
			return("MERITUS");
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
			return("coreymeritus™ enables Health services team members to seamlessly communicate with one another, and take action, right from their mobile device. Location-based intelligence and predictive analytics enable workflow issues to be solved before they occur via push notifications to administrators, who can then mitigate in real time.");
		}
		public String getAboutText2()
		{
			return("Key performance indicators can also be monitored in real time.  coreymeritus™ also gives providers mobile access to just the information they need right now, based on a number of contexts (schedule, phone calls, etc.). /n www.coremobileinc.com");
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
			return("Powered By Core Mobile, Inc.\n U.S. Patent No. 8,606,923");
		}
		public  String getOne2OneMsgRecvdIntent()
		{
			return("com.coremobile.coreymeritus.One2OneMsgRecvd");
		}
		public String getDashboardEditTextRow1()
		{
			return("Contact");
		}
		
		public String getDashboard121msgTextRow1()
		{
			return("Contact");
		}
		public String getDashboardEditTextRow2()
		{
			return("update");
		}
}
