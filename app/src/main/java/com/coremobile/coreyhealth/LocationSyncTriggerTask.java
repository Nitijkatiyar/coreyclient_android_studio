package com.coremobile.coreyhealth;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

public class LocationSyncTriggerTask extends SyncTriggerTask {
    private static final String TAG = "Corey_LocationSyncTriggerTask";

    private Location mLocation;

    public LocationSyncTriggerTask(Activity activity, ContextData coreyCxt,
        String syncDescription, String urlSuffix, int triggerResultType) {
        super(activity, coreyCxt, syncDescription, urlSuffix, triggerResultType);
    }
    @Override
    protected Integer doInBackground(String... params) {
            getCurrentLocation();
            if (mLocation ==  null) {
                mErrorStringId = R.string.ls_no_location;
                return -1;
            }
            String latitude = Double.toString(mLocation.getLatitude());
            String longitude = Double.toString(mLocation.getLongitude());
            String altitude = Double.toString(mLocation.getAltitude());

            String[] newParams = new String[params.length+8];
            System.arraycopy(params, 0, newParams, 0, params.length);
            int offset = params.length;
            newParams[offset+0] = "Latitude";   newParams[offset+1] = latitude;
            newParams[offset+2] = "Longitude";  newParams[offset+3] = longitude;
            newParams[offset+4] = "Altitude";   newParams[offset+5] = altitude;
            newParams[offset+6] = "sync";       newParams[offset+7] = "1";
            return super.doInBackground(newParams);
    }

    private void getCurrentLocation() {
        mLocation = null;

        final LocationManager locationManager =
            (LocationManager)mActivity.getSystemService(Context.LOCATION_SERVICE);
        String provider = LocationManager.NETWORK_PROVIDER;;

        Log.d(TAG, "Using provider " + provider);
        locationManager.requestSingleUpdate(provider, new LocationListener() {
            // Called when the location has changed.
            public void	onLocationChanged(Location location) {
//                Log.d(TAG, "onLocationChanged " + location);
                mLocation = location;
            }

            // Called when the provider is disabled by the user.
            public void	onProviderDisabled(String provider) {
//                Log.d(TAG, "onProviderDisabled " + provider);
            }

            // Called when the provider is enabled by the user.
            public void	onProviderEnabled(String provider) {
//                Log.d(TAG, "onProviderEnabled " + provider);
            }

            // Called when the provider status changes.
            public void	onStatusChanged(String provider, int status, Bundle extras) {
//                Log.d(TAG, "onStatusChanged " + provider + ", " + status + ", " + extras);
            }
        },
        Looper.getMainLooper());

        try {
            Thread.sleep(INITIAL_WAIT_TIME);
            for (int i = 0; i < 10; ++i) {
                if (mLocation != null) {
                    return;
                }
                Log.d(TAG, "Waiting to know location ... " + i);
                Thread.sleep(LOOP_WAIT_TIME);
            }
        } catch (InterruptedException e) {
            Log.d(TAG, "WAIT sleep interrupted");
        }
    }
}
