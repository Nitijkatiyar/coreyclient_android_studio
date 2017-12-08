package com.coremobile.coreyhealth;


import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.coremobile.coreyhealth.base.CMN_AppBaseActivity;

public class MyLocation extends CMN_AppBaseActivity implements LocationListener{
	protected LocationManager locationManager;
	protected LocationListener locationListener;
	protected Context context;
	TextView txtLat;
	String lat;
	String provider;
	protected String latitude,longitude; 
	protected boolean gps_enabled,network_enabled;

	private ProgressDialog mProgressDialog;
	private String TAG = "Corey_MyLocation";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_location);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		//locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		//locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.my_location, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onLocationChanged(Location location) {
	txtLat = (TextView) findViewById(R.id.textview1);
	//txtLat.setText("Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude());
	}

	@Override
	public void onProviderDisabled(String provider) {
	Log.d("Latitude","disable");
	}

	@Override
	public void onProviderEnabled(String provider) {
	Log.d("Latitude","enable");
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	Log.d("Latitude","status");
	}

	
}
