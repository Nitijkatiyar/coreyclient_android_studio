package com.coremobile.coreyhealth;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.coremobile.coreyhealth.base.CMN_AppBaseActivity;
import com.coremobile.coreyhealth.googleAnalytics.CMN_Preferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class PatientAnalyticsActivityCMN extends CMN_AppBaseActivity implements IPullDataFromServer {
	private String TAG = "Corey_AnalyticsActivityCMN";
	public HashMap<String, String> PatientList = new HashMap<String, String>();
	public ArrayList<PatientAnalyticsdDat> PatientAnalyticsList; 
	public ArrayList<DeptDef> DeptList; 
	public ArrayList<statusDefObj> StatusDefList;
	private ActionBar mActionBar;
	 public boolean pullXmlInProgress = false;
	 SharedPreferences mcurrentUserPref;
	 public static final String CURRENT_USER = "CurrentUser";
	 private String CMN_SERVER_BASE_URL_DEFINE;
	 public int PullXmlState =0;
	 public  HashMap <String,String> AnalDeptSts2ColorMap;
	 private String AnalDeptId;
	 private String AnalStatusId;
	 private String AnalColour;
	 
	 private String mSource;
	 private String mPatientId;
	 private ProgressDialog mProgressDialog;
	 
	 Timer AnalyticsdlTimer;
	 int rowCount;
	 int columnCount;
	 
	 int Column0Height=50;
	 int Row0Height=50;
	 int Column0Width=50;
	 int Row0Width=150;
	 int ColumnMinus2Width=200;
	 int ColumnMinus2Height=50;
	 int InterRowWidth=150;
	 int InterRowHeight=10;
	 int DataRowHeight=150;
	 int DataRowWidth=50;
	 int CellWidth=150;
	 int CellHeight=25;
	 float scale;
	/** Called when the activity is first created. */
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	/*	String[] row = { "Patient1", "Patient2", "Patient3", "Patient4",
				"Patient 5", "Patient6",

				"Row 7", "ROW8", "ROW9", "Row10", "Row11", "Row 12", "Row 13",

				"Row 14", "Row15", "Row16", "Row 17", "Row 18",

				"Row 19" };

		String[] column = { "Checkin", "COLUMN2", "COLUMN3", "COLUMN4",

		"COLUMN5", "COLUMN6", "COLUMN7", "COLUMN8", "COLUMN9", "COLUMN10",

		"COLUMN11", "COLUMN12" };
	*/
		Intent mIntent;
		mIntent = getIntent();
		mPatientId =mIntent.getStringExtra("Patientid");
		
		Log.d(TAG, "patientid="+mPatientId);
		mcurrentUserPref = this.getSharedPreferences(CURRENT_USER, 0);
		mProgressDialog = new ProgressDialog(PatientAnalyticsActivityCMN.this);
		mProgressDialog.setTitle("Retrieving Analytics data");
	    mProgressDialog.setMessage("Please Wait ..");
	    mProgressDialog.setCancelable(false);
	    mProgressDialog.show();
	    scale = this.getResources().getDisplayMetrics().density;
	    
	   Log.d(TAG, "scale= "+scale);
	    AnalyticsdlTimer= new Timer();
		   AnalyticsdlTimer.schedule(new TimerTask()
		    {
		        @Override
		        public void run()
		        {
		            Log.d(TAG,"Update timed out");
		            Analyticsdltimout();
		        }
		    }, CMN_Preferences.getUpdateWaitout(this) );
		//PopulateAnalyticsData();
		PopulateAnalyticsData1();
		StatusDefList= MyApplication.INSTANCE.StatusDefList;
		DeptList=MyApplication.INSTANCE.DeptList;
		PatientAnalyticsList=MyApplication.INSTANCE.PatientAnalyticsList;
		AnalDeptSts2ColorMap=MyApplication.INSTANCE.DeptSts2ColorMap;
/*
		int rl = row.length;
		int cl = column.length;

		Log.d("--", "R-Lenght--" + rl + "   " + "C-Lenght--" + cl);
*/
		
//		ScrollView sv = new ScrollView(this);
		Log.d(TAG,"PullXmlState before whileloop ="+PullXmlState);
/*
		while(PullXmlState!=3)
		{
			//Wait here until download complete
			Log.d(TAG,"PullXmlState inside whileloop ="+PullXmlState);
		}  */
		
		 ActionBar actionBar = getActionBar();
	 		if (actionBar != null) 
	 		{
	 	//		actionBar.setIcon(R.drawable.app_icon);
				    actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME
					    | ActionBar.DISPLAY_SHOW_TITLE
					    | ActionBar.DISPLAY_HOME_AS_UP);
				    actionBar.show();

				actionBar.setTitle("Patient Analytics");
	 		}
	/*	rowCount = (PatientAnalyticsList.size() * 2) + 1;
		columnCount = DeptList.size() + 2;*/
/*	TableLayout tableLayout = createTableLayout1( rowCount, columnCount);

		HorizontalScrollView hsv = new HorizontalScrollView(this);

		hsv.addView(tableLayout);

		sv.addView(hsv);

		setContentView(sv);
		
		mActionBar = getActionBar();
		mActionBar.setTitle("Patient Analytics");
		*/
		
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
	/*	int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item); */
		
		switch (item.getItemId()) {
		case android.R.id.home:
		    finish();
		    return true;
		default:
		    return super.onOptionsItemSelected(item);
		}
		
	}
	public void makeCellEmpty(TableLayout tableLayout, int rowIndex,
			int columnIndex) {

		// get row from table with rowIndex

		TableRow tableRow = (TableRow) tableLayout.getChildAt(rowIndex);

		// get cell from row with columnIndex

		TextView textView = (TextView) tableRow.getChildAt(columnIndex);

		// make it black

		textView.setBackgroundColor(Color.BLACK);

	}

	public void setHeaderTitle(TableLayout tableLayout, int rowIndex,
			int columnIndex) {

		// get row from table with rowIndex

		TableRow tableRow = (TableRow) tableLayout.getChildAt(rowIndex);

		// get cell from row with columnIndex

		TextView textView = (TextView) tableRow.getChildAt(columnIndex);

		textView.setText("Hello");

	}

	
	private TableLayout createTableLayout1(
			int rowCount, int columnCount) {

		// 1) Create a tableLayout and its params
//		rowCount = (PatientAnalyticsList.size() * 2) + 1;
//		columnCount = DeptList.size() + 2;

		Log.d(TAG, "rowcount =" + rowCount);
		Log.d(TAG, "columnCount =" + columnCount);
		TableLayout.LayoutParams tableLayoutParams = new TableLayout.LayoutParams();

		TableLayout tableLayout = new TableLayout(this);

		tableLayout.setBackgroundColor(Color.BLACK);
	//	tableLayout.setBackgroundColor(Color.WHITE);
		//tableLayout.setColumnStretchable(columnIndex, isStretchable);

		// tableLayout.setd

		// 2) create tableRow params

		TableRow.LayoutParams tableRowParams;
		//= new TableRow.LayoutParams();
		

	//	tableRowParams.setMargins(1, 0, 1, 0);
//
	//	tableRowParams.weight = 1;

		// tableRowParams

		
		for (int i = 0; i < rowCount; i++) {

			// 3) create tableRow

			TableRow tableRow = new TableRow(this);

			tableRow.setBackgroundColor(Color.BLACK);
		//	tableLayout.setBackgroundColor(Color.WHITE);
			if(i==0)
			{
			//	final float scale = getContext().getResources().getDisplayMetrics().density;
		//		  scale = this.getResources().getDisplayMetrics().density;
				 CellWidth = (int) (Row0Width * scale + 0.5f);
				
			//	tableRowParams = new TableRow.LayoutParams(200, TableRow.LayoutParams.WRAP_CONTENT, 1f);
				tableRowParams = new TableRow.LayoutParams(CellWidth, TableRow.LayoutParams.WRAP_CONTENT, 1f);
				//tableRowParams = new TableRow.LayoutParams(200, 50, 1f);
				tableRowParams.setMargins(2, 0, 2, 0);
		//		tableRow.setBackgroundColor(Color.WHITE);

			}
			else
			{
				CellWidth = (int) (DataRowWidth * scale + 0.5f);
				CellHeight = (int) (DataRowHeight * scale + 0.5f);
			//	tableRowParams = new TableRow.LayoutParams(200, 100, 1f);
				tableRowParams = new TableRow.LayoutParams(CellWidth, CellHeight, 1f);
				tableRowParams.setMargins(2, 0, 2, 0);
			//	tableRow.setBackgroundColor(Color.BLACK);
			//	tableLayout.setBackgroundColor(Color.WHITE);
			}

			for (int j = 0; j < columnCount; j++) {

				// 4) create textView

				TextView textView = new TextView(this);

				// textView.setText(String.valueOf(j));

				textView.setBackgroundColor(Color.WHITE);

				textView.setGravity(Gravity.CENTER);
				//textView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

				String s1 = Integer.toString(i);

				String s2 = Integer.toString(j);

				String s3 = s1 + s2;

				int id = Integer.parseInt(s3);
				Log.d(TAG, "row no" + s1);
				Log.d(TAG, "Column no>" + s2);
				Log.d(TAG, "cellno>" + id);

				if (i == 0 && j == 0) 
				{

					textView.setText("       ");
					tableRowParams = new TableRow.LayoutParams(300, 100, 1f);

				} 
				else if (i == 0) 
				{
					

				//	tableRowParams = new TableRow.LayoutParams(100, TableRow.LayoutParams.WRAP_CONTENT, 1f);

					Log.d(TAG, "set Column Headers");
					if (j == (columnCount - 2)) {
						Log.d(TAG, "columnCount-2");
						textView.setText("Est Dept Completion time");
						textView.setTypeface(null, Typeface.BOLD);
						textView.setTextColor(Color.BLACK);
						
						CellWidth = (int) (ColumnMinus2Width * scale + 0.5f);
						CellHeight = (int) (ColumnMinus2Height * scale + 0.5f);
						tableRowParams = new TableRow.LayoutParams(CellWidth, TableRow.LayoutParams.WRAP_CONTENT, 1f);
					//	tableRowParams = new TableRow.LayoutParams(350, TableRow.LayoutParams.WRAP_CONTENT, 1f);
						tableRowParams.setMargins(2, 0, 2, 0);
						tableRow.setBackgroundColor(Color.WHITE);
					} else if (j == (columnCount - 1)) {
						Log.d(TAG, "columnCount-1");
						textView.setText("Est Discharge Time");
						textView.setTypeface(null, Typeface.BOLD);
						textView.setTextColor(Color.BLACK);
						CellWidth = (int) (ColumnMinus2Width * scale + 0.5f);
						CellHeight = (int) (ColumnMinus2Height * scale + 0.5f);
					//	tableRowParams = new TableRow.LayoutParams(350, TableRow.LayoutParams.WRAP_CONTENT, 1f);
						tableRowParams = new TableRow.LayoutParams(CellWidth, TableRow.LayoutParams.WRAP_CONTENT, 1f);
						tableRowParams.setMargins(2, 0, 2, 0);
						tableRow.setBackgroundColor(Color.WHITE);
					} else {
						Log.d(TAG, "column heading ="
								+ DeptList.get(j-1).getDispName());
						textView.setText(DeptList.get(j-1).getDispName());
						textView.setTypeface(null, Typeface.BOLD);
						textView.setTextColor(Color.BLACK);
						
						
					}

				} else if (i == 1) {

					Log.d(TAG, "set 1st row empty");
					CellWidth = (int) (InterRowWidth * scale + 0.5f);
					CellHeight = (int) (InterRowHeight * scale + 0.5f);
					tableRowParams = new TableRow.LayoutParams(CellWidth, CellHeight, 1f);
				//	tableRowParams = new TableRow.LayoutParams(300, 50, 1f);
					tableRowParams.setMargins(2, 0, 2, 0);
					textView.setText("");
					tableRow.setBackgroundColor(Color.WHITE);
				/*	if (j == 0)
					{
						textView.setBackgroundColor(Color.BLACK);
					} */

				} else if ((i % 2) == 1) {

					Log.d(TAG, "set odd rows empty");
					CellWidth = (int) (InterRowWidth * scale + 0.5f);
					CellHeight = (int) (InterRowHeight * scale + 0.5f);
					tableRowParams = new TableRow.LayoutParams(CellWidth, CellHeight, 1f);
				//	tableRowParams = new TableRow.LayoutParams(300, 50, 1f);
					tableRowParams.setMargins(2, 0, 2, 0);
					textView.setText("");
				    tableRow.setBackgroundColor(Color.WHITE);
			/*		if (j == 0)
					{
						textView.setBackgroundColor(Color.BLACK);
					} */

				} 
				else if (j == 0) 
				{

					Log.d("TAAG", "Set Row Headers");

					textView.setText(PatientAnalyticsList.get((i / 2) - 1)
							.getName());
					textView.setTypeface(null, Typeface.BOLD);
					textView.setTextColor(Color.BLACK);
					textView.setLines(2);
					//textView.setHeight(110);
					textView.setPadding(0, 0, 0, 0);
					CellWidth = (int) (Column0Width * scale + 0.5f);
					CellHeight = (int) (Column0Height * scale + 0.5f);
					tableRowParams = new TableRow.LayoutParams(CellWidth, CellHeight, 1f);
				//	tableRowParams = new TableRow.LayoutParams(300, 100, 1f);
				//	tableRowParams.setMargins(3, 3, 3, 3);
					textView.setBackgroundColor(Color.WHITE);
					tableRow.setBackgroundColor(Color.WHITE);
					textView.setGravity(Gravity.CENTER);
				} 
				else 
				{
				//	if(j>1) tableRow.setBackgroundColor(Color.BLACK);
					//textView.setGravity(Gravity.BOTTOM);
					tableRowParams = new TableRow.LayoutParams(300, 100, 1f);

					if (j == (columnCount - 2)) {
						String CurDepttime = PatientAnalyticsList.get(
								(i / 2) - 1).getEstDischargeTime();
						textView.setText(CurDepttime);
						CellWidth = (int) (Column0Width * scale + 0.5f);
						CellHeight = (int) (Column0Height * scale + 0.5f);
						tableRowParams = new TableRow.LayoutParams(CellWidth, CellHeight, 1f);
						//tableRowParams = new TableRow.LayoutParams(300, 100, 1f);
						tableRowParams.setMargins(2, 0, 2, 0);
					} else if (j == (columnCount - 1)) {
						String CurDepttime = PatientAnalyticsList.get(
								(i / 2) - 1).getEstDischargeTime();
						textView.setText(CurDepttime);
						CellWidth = (int) (Column0Width * scale + 0.5f);
						CellHeight = (int) (Column0Height * scale + 0.5f);
						tableRowParams = new TableRow.LayoutParams(CellWidth, CellHeight, 1f);
					//	tableRowParams = new TableRow.LayoutParams(300,100, 1f);
						tableRowParams.setMargins(2, 0, 2, 0);
					} else {
						Log.d(TAG, "deptstatuslist size ="+PatientAnalyticsList.get((i / 2) - 1).DeptStatusList.size());
						Log.d(TAG, "dept ="+PatientAnalyticsList.get((i / 2) - 1).DeptStatusList
								.get(j-1).getDeptId());
						// ArrayList<DeptStatusobj> deptstatus
						String Sts = PatientAnalyticsList.get((i / 2) - 1).DeptStatusList
								.get(j-1).getStatusId();
						Log.d(TAG, "Sts =" + Sts);
						
						  AnalDeptId =DeptList.get(j-1).getId();
						AnalStatusId = PatientAnalyticsList.get((i / 2) - 1).Dept2StatusMap.get(AnalDeptId);
						  AnalColour = AnalDeptSts2ColorMap.get(AnalStatusId);
						//  textView.setBackgroundColor(Integer.parseInt(AnalColour));
						  String colour = "#"+AnalColour;
						  textView.setBackgroundColor(Color.parseColor(colour));
						  textView.setTextColor(Color.BLACK);
						  if(AnalStatusId.equalsIgnoreCase("7")) textView.setText("Completed");
						  else if(AnalStatusId.equalsIgnoreCase("5")) textView.setText("In Progress");
						//  textView.setEllipsize(where);
						  tableRowParams.setMargins(2, 0, 2, 0);
						//  tableRow.setBackgroundColor(Color.BLACK);
					//	  tableRowParams = new TableRow.LayoutParams(300,100, 1f);
				
					}

				}

				// 5) add textView to tableRow
			//	Log.d (TAG, "Row no."+j);
			//	Log.d(TAG, "Layout params height"+tableRowParams.height);
				tableRow.addView(textView, tableRowParams);

			}

			// 6) add tableRow to tableLayout

			tableLayout.addView(tableRow, tableLayoutParams);

		}

		return tableLayout;

	}

	public void PopulateAnalyticsData() {
		PatientAnalyticsdDat PatAnal1 = new PatientAnalyticsdDat();
		PatientAnalyticsdDat PatAnal2 = new PatientAnalyticsdDat();
		String deptid = "0";
		String statusid = "100";

		String[] deptnames = { "Checkin   ", "Admitting ", "Pre-Op    ",
				"Anesthesia",

				"Surgery   ", "ICU       ", "CircNurse ", "PACUNurse ",
				"Dept1     ", "Dept2     " };

		/*
		 * Populate Departments
		 */

		for (int dept = 0; dept < 10; dept++) {
			deptid = Integer.toString(dept);
			DeptDef department = new DeptDef();
			department.setId(deptid);
			department.setDispName(deptnames[dept]);
			DeptList.add(department);
		}

		/*
		 * populate analytics for 2 patients
		 */
		for (int i = 0; i < 10; i++) {
			DeptStatus4AnalyticsObj DeptStatus = new DeptStatus4AnalyticsObj();
			deptid = Integer.toString(i);
			DeptStatus.setDeptId(deptid);
			if (i == 5) {
				statusid = "5";
			} else if (i < 5) {
				statusid = "7";
			} else if (i > 5) {
				statusid = "100";
			}
			DeptStatus.setStatusId(statusid);
			PatAnal1.DeptStatusList.add(DeptStatus);
			PatAnal1.setPatientId("001");
			PatAnal1.setName("TestPatient1");
			PatAnal1.setCurDeptId("5");
			PatAnal1.setCurDepEstCompTime("50");
			PatAnal1.setEstDischargeTime("50");
		}

		for (int i = 0; i < 10; i++) {
			DeptStatus4AnalyticsObj DeptStatus = new DeptStatus4AnalyticsObj();
			deptid = Integer.toString(i);
			DeptStatus.setDeptId(deptid);
			if (i == 7) {
				statusid = "5";
			} else if (i < 7) {
				statusid = "7";
			} else if (i > 7) {
				statusid = "100";
			}
			DeptStatus.setStatusId(statusid);
			PatAnal2.DeptStatusList.add(DeptStatus);

			PatAnal2.setPatientId("002");
			PatAnal2.setName("TestPatient2");
			PatAnal2.setCurDeptId("7");
			PatAnal2.setCurDepEstCompTime("60");
			PatAnal2.setEstDischargeTime("90");
		}

		PatientAnalyticsList.add(PatAnal1);
		PatientAnalyticsList.add(PatAnal2);
	}
	public void pullXml(int operation)
    {
    		Log.d(TAG,"Pull xml initiated for  "+operation);
        try
        {
            if (pullXmlInProgress)
            {
                Log.d(TAG,"Another Pull is in progress. Pls wait.");
                if (mProgressDialog != null && mProgressDialog.isShowing())
    	        {
    	            mProgressDialog.dismiss();
    	        }
                PullXmlState=3;
                return;
                
            }
            
            
       
            pullXmlInProgress = true;
              String baseURL = mcurrentUserPref.getString("CMN_SERVER_BASE_URL_DEFINE", "");
            if ((baseURL != null) && (baseURL.length() > 0)) CMN_SERVER_BASE_URL_DEFINE = baseURL;
            String url="";
            switch(operation)
            {
            case 0 :   url = CMN_SERVER_BASE_URL_DEFINE + "GetRoleList.aspx?";
            			break;
            case 1 :   url = CMN_SERVER_BASE_URL_DEFINE + "GetPatientStatusDetails.aspx?";
						break;
            case 2 :   url = CMN_SERVER_BASE_URL_DEFINE + "PatientAnalyticsData.aspx?";
					break;
            }
            Log.d(TAG, "after switch and url="+url);
            HashMap<String, String> data = new HashMap<String, String>();
            data.put("token", CMN_Preferences.getUserToken(PatientAnalyticsActivityCMN.this));
			data.put("context", CMN_Preferences.getCurrentContextId(this));
            if(mPatientId!=null)
            {
            if(!mPatientId.equalsIgnoreCase("all") )
            {
            	Log.d(TAG, "adding patientid="+mPatientId);
            	data.put("patientid", mPatientId); 
            }
            }

            new GetXML(getApplicationContext(), "Analytics", this,data).execute(url);
        }
        catch (Exception e)
        {
        	pullXmlInProgress = false;
            
            Log.d(TAG, "mPullDataCompleted opened on exception");
        }
    }

	@Override
	public void finishedParsing(String _status) {
		pullXmlInProgress = false;
        Log.d( TAG, "pull xml finished  ="+PullXmlState);
        if(PullXmlState==0)
        {
        	PullXmlState=1;
        	Collections.sort(DeptList, new ObjectComparator());
    		Collections.reverse(DeptList);
    		for( DeptDef deptt : DeptList)
    		{
    			Log.d(TAG, "Deptnames in sorted list ="+deptt.getDispName());
    		}
        	StartXmlPull(PullXmlState);
        }
        else if(PullXmlState==1)
        {
        	PullXmlState=2;
        	StartXmlPull(PullXmlState);
        }
        else
        {
        	PullXmlState=3;
        	if (mProgressDialog != null && mProgressDialog.isShowing()) 
		    {
			mProgressDialog.dismiss();
			if(AnalyticsdlTimer!=null)
	    	{
				AnalyticsdlTimer.cancel();
				AnalyticsdlTimer=null;
	    		 
	    	}
			Log.d( TAG, "pull xml inside else ="+PullXmlState);
		    }
        	ScrollView sv = new ScrollView(this);
        	rowCount = (PatientAnalyticsList.size() * 2) + 1;
    		columnCount = DeptList.size() + 3;
    	TableLayout tableLayout = createTableLayout1( rowCount, columnCount);

    		HorizontalScrollView hsv = new HorizontalScrollView(this);

    		hsv.addView(tableLayout);

    		sv.addView(hsv);

    		setContentView(sv);
    	/*	
    		mActionBar = getActionBar();
    		mActionBar.setTitle("Patient Analytics"); */
        }
		
	}

	@Override
	public void showDialog() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void closeDialog() {
		// TODO Auto-generated method stub
		
	}
	public void PopulateAnalyticsData1()
	{
		/*
		mProgressDialog = new ProgressDialog(PatientAnalyticsActivityCMN.this);
		mProgressDialog.setTitle("Retrieving Messages");
	    mProgressDialog.setMessage("Please Wait ..");
	    mProgressDialog.setCancelable(false);
	    mProgressDialog.show();
	    */
		Log.d(TAG, "Entering populate data");
/*	   AnalyticsdlTimer= new Timer();
	   AnalyticsdlTimer.schedule(new TimerTask()
	    {
	        @Override
	        public void run()
	        {
	            Log.d(TAG,"Update timed out");
	            Analyticsdltimout();
	        }
	    }, 30000 );  */
		/*
		 * Step1: Populate Department list
		 */
	   Log.d(TAG, "Starting xml pull with 0");
		StartXmlPull(0);
		/*
		 * Step2: Populate Status list
		 */
		
		/*
		 * Step3: Populate Status list
		 */
	}
	 private void StartXmlPull(int operation)
	    {
		 final int  opr=operation;
	        Thread XmlPullThread = new Thread()
	        {
	            @Override
	            public void run()
	            {
	                try
	                {
	                   // Log.i(TAG, "isCorefyInProgress =" + isCorefyInProgress);
	                 //   sleep(3000); // Wait for a few seconds...
	                    while (pullXmlInProgress)
	                    {
	                    	Log.d(TAG,"sleep in xmlpullthread when PullXmlState ="+PullXmlState);
	                    //      sleep(500);  
	                            continue;	                        
	                    }
	                    
	                  //  refreshMessages();
	                }
	//                catch (InterruptedException e)
	//                {
	//                    e.getMessage();
	//                }
	              finally
	                {

	                    Log.d(TAG,
	                          "sleep again before pulling the Msg $$$$$$$$$$$$$$$$");
	/*                    try {
							sleep(20);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.getMessage();
						} */
	                    Log.d(TAG,
	                            "start the message retrieval $$$$$$$$$$$$$$$$");
	                   
	                    pullXml( opr);
	                } 
	            }

	        };

	        XmlPullThread.start();

	    }
	 public void Analyticsdltimout()
		{
			if(AnalyticsdlTimer!=null)
	    	{
				AnalyticsdlTimer.cancel();
				AnalyticsdlTimer=null;
	    		 
	    	}
	    	if (mProgressDialog != null && mProgressDialog.isShowing())
	        {
	            mProgressDialog.dismiss();
	        }
	    	
	    	runOnUiThread(new Runnable()
	        {

	            @Override
	            public void run()
	            {
	                // TODO Auto-generated method stub
	            	Toast.makeText(PatientAnalyticsActivityCMN.this,
	        			    "Analytics download timeout", Toast.LENGTH_LONG).show();

	            }
	        });
			 
			 finishedParsing("dummy");
		}
	 @Override
	    protected void onDestroy() {
		super.onDestroy();
		 PatientAnalyticsList.clear(); 
		 DeptList.clear(); 
		StatusDefList.clear();
		AnalDeptSts2ColorMap.clear();
	 }
	 
	 public class ObjectComparator implements Comparator<DeptDef>{
    	 
	        @Override
	        public int compare(DeptDef o1, DeptDef o2) {
	            return ((o1.getPosition())>(o2.getPosition()) ? -1 : ((o1.getPosition())==(o1.getPosition()) ? 0 : 1));
	        }
	    } 
}
