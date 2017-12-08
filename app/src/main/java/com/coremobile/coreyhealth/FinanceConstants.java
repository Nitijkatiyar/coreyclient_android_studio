package com.coremobile.coreyhealth;

public class FinanceConstants implements IAppConstants{
	  public String getRefreshCallsListIntent()
	    {
	    	return("com.coremobile.coreyfinance.refresh_callslist");
	    }
		
		public  String getAppFilesPath()
		{
			return("/data/data/com.coremobile.coreyfinance/files/");
		}
		
		public  String getDatabaseProviderContentUri()
		{
			return("content://com.coremobile.coreyfinance.databaseprovider/messages");
		}
		
		public  String getDatabaseProviderContentUriPhoneCall()
		{
			return("content://com.coremobile.coreyfinance.databaseprovider/phonecalls");
		}
		
		public  String getDatabaseProviderContentUriMatcherMessages()
		{ 
			return("com.coremobile.coreyfinance.databaseprovider");
		
		}
		
		public  String getDatabaseProviderContentUriMatcherMessagesHash()
		{
			return("com.coremobile.provider.coreyhealth"); //Is this legacy problem?
		}
		
		public  String getDatabaseProviderContentUriMatcherPhoneCalls()
		{
			return("com.coremobile.coreyfinance.databaseprovider");
		}
		
		public  String getPushNotificationReceivedIntent()
		{
			return("com.coremobile.coreyfinance.pushnotification");
		}
		
		public  String getDownloadMessageServiceControlTimerFilter()
		{
			return("com.coremobile.coreyfinance.timercontrol");
		}
		public  String getOne2OneMsgRecvdIntent()
		{
			return("com.coremobile.coreyfinance.One2OneMsgRecvd");
		}
		public  String getDownloadCompleteIntent()
		{
			return("com.coremobile.coreyfinance.downloadcomplete");
		}
		
		public  String getUploadCompleteIntent()
		{
			return("com.coremobile.coreyfinance.updatecompleted");
		}
		public  String getReceivedPushIntent()
		{
			return("com.coremobile.coreyfinance.receivedpush");
		}
		
		public  String getMessageTabActivityControlTimerFilter()
		{
			return("com.coremobile.coreyfinance.timercontrol");
		}
		
		public  String getDownloadMessageServiceActionCompleteIntent()
		{
			return("com.coreyfinance.downloader.action.COMPLETE");
		}
		/* For Urban Airship */
		public  String getGcmSender()
		{
			return("199174756866");
		}
		
		public  String getPSharpGcmSender()
		{
			return("855074335611");
		}
		public String getProductionAppKey()
		{
			return("EUzciR0DRje9Xy03UtQyng");
		}
		
		public String getProductionAppSecret()
		{
			return("jG8M9T9SRIK8ERq7ad3xCQ");
		}
		public String getDevelopmentAppKey()
		{
			return("L-7W7h__TH-40jaP0jauFA");
		}
		
		public String getDevelopmentAppSecret()
		{
			return("Pq0WkcQlRkm37pjZ1aSVUQ");
		}
		public String getAppName()
		{
			return("FINANCE");
		};
		public String DefaultCredentialsAppstring()
		{
			return("FINANCE");
		}
		public int getRow0DrawableId()
		{
			return(R.drawable.ic_calendar);			
		
		} 
		public int getSplashId()
		{
			return(R.drawable.splash_fg);
			
		}

		@Override
		public String getAboutText1() {
			// TODO Auto-generated method stub
			return ("Corey™ the only application that delivers just the relevent actionable information from Social Media, Cloud, Big Data and Enterprise applications to your mobile device");
		}

		@Override
		public String getAboutText2() {
			// TODO Auto-generated method stub
			return ("Core Mobile Inc. provides the latest innovations in context based personalization of information in real time for professionals in Financial services, Healthcare, Manufacturing. \n www.coremobileinc.com");
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
			return("Update");
		}
		public String getDashboardEditTextRow2()
		{
			return("Update");
			
		}
		public String getDashboard121msgTextRow1()
		{
			return("Update");
		}
}
