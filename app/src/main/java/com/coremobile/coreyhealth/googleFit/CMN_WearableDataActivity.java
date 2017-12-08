package com.coremobile.coreyhealth.googleFit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.coremobile.coreyhealth.R;
import com.coremobile.coreyhealth.errorhandling.CMN_ErrorMessages;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataDeleteRequest;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class CMN_WearableDataActivity extends FragmentActivity {

    public static final String TAG = "Corey_BasicHistoryApi";
    private static final int REQUEST_OAUTH = 1;
    private static final String DATE_FORMAT = "yyyy.MM.dd HH:mm:ss";

    /**
     * Track whether an authorization activity is stacking over the current activity, i.e. when
     * a known auth error is being resolved, such as showing the account chooser or presenting a
     * consent dialog. This avoids common duplications as might happen on screen rotations, etc.
     */
    private static final String AUTH_PENDING = "auth_state_pending";
    private boolean authInProgress = false;

    private GoogleApiClient mClient = null;

    LinearLayout stepsCount;

    public static ArrayList<CMN_StepsModel> stepsModels = new ArrayList<CMN_StepsModel>();
    private SharedPreferences mCurrentUserPref;
    public static final String CURRENT_USER = "CurrentUser";
    String organizationName;


    public static String lastupdatedtime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wearabledataactivity);

        stepsCount = (LinearLayout) findViewById(R.id.layoutStepCount);
        stepsCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CMN_WearableDataActivity.stepsModels != null && CMN_WearableDataActivity.stepsModels.size() > 0) {
                    startActivity(new Intent(CMN_WearableDataActivity.this, CMN_StepsCountActivity.class));
                } else {
                    Toast.makeText(CMN_WearableDataActivity.this, CMN_ErrorMessages.getInstance().getValue(137), Toast.LENGTH_SHORT).show();
                }
            }
        });
        //retrieve the status of auth.
        if (savedInstanceState != null) {
            authInProgress = savedInstanceState.getBoolean(AUTH_PENDING);
        }
        mCurrentUserPref = getSharedPreferences(CURRENT_USER, 0);
        organizationName = mCurrentUserPref.getString("Organization", null);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setTitle(
                "" + getResources().getString(R.string.wearablesdata));
        getActionBar().setDisplayHomeAsUpEnabled(true);

        buildFitnessClient();

        CMN_GetWearableDataLastUpdatedTimeWebService updatedTimeWebService = new CMN_GetWearableDataLastUpdatedTimeWebService(CMN_WearableDataActivity.this);
        updatedTimeWebService.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.wearablesdataupload, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.action_upload:
                UploadWearableData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    /**
     * Build a {@link GoogleApiClient} that will authenticate the user and allow the application
     * to connect to Fitness APIs. The scopes included should match the scopes your app needs
     * (see documentation for details). Authentication will occasionally fail intentionally,
     * and in those cases, there will be a known resolution, which the OnConnectionFailedListener()
     * can address. Examples of this include the user never having signed in before, or
     * having multiple accounts on the device and needing to specify which account to use, etc.
     */
    private void buildFitnessClient() {
        // Create the Google API Client
        mClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addConnectionCallbacks(
                        new GoogleApiClient.ConnectionCallbacks() {
                            @Override
                            public void onConnected(Bundle bundle) {
                                Log.d(TAG, "Connected!!!");
                                // Now you can make calls to the Fitness APIs.  What to do?
                                // Look at some data!!
                                new InsertAndVerifyDataTask().execute();
                            }

                            @Override
                            public void onConnectionSuspended(int i) {
                                // If your connection to the sensor gets lost at some point,
                                // you'll be able to determine the reason and react to it here.
                                if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                    Log.d(TAG, "Connection lost.  Cause: Network Lost.");
                                } else if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                    Log.d(TAG, "Connection lost.  Reason: Service Disconnected");
                                }
                            }
                        }
                )
                .enableAutoManage(CMN_WearableDataActivity.this, 0, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.d(TAG, "Google Play services connection failed. Cause: " +
                                result.toString());
                        Log.d(TAG,
                                "Exception while connecting to Google Play services: " +
                                        result.getErrorMessage());
                    }
                })
                .build();
    }

    /**
     * Create a {@link DataSet} to insert data into the History API, and
     * then create and execute a {@link DataReadRequest} to verify the insertion succeeded.
     * By using an {@link AsyncTask}, we can schedule synchronous calls, so that we can query for
     * data after confirming that our insert was successful. Using asynchronous calls and callbacks
     * would not guarantee that the insertion had concluded before the read request was made.
     * An example of an asynchronous call using a callback can be found in the example
     * on deleting data below.
     */
    private class InsertAndVerifyDataTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            //First, create a new dataset and insertion request.
            DataSet dataSet = insertFitnessData();

            // [START insert_dataset]
            // Then, invoke the History API to insert the data and await the result, which is
            // possible here because of the {@link AsyncTask}. Always include a timeout when calling
            // await() to prevent hanging that can occur from the service being shutdown because
            // of low memory or other conditions.
            Log.d(TAG, "Inserting the dataset in the History API");
            com.google.android.gms.common.api.Status insertStatus =
                    Fitness.HistoryApi.insertData(mClient, dataSet)
                            .await(1, TimeUnit.MINUTES);

            // Before querying the data, check to see if the insertion succeeded.
            if (!insertStatus.isSuccess()) {
                Log.d(TAG, "There was a problem inserting the dataset.");
                return null;
            }

            // At this point, the data has been inserted and can be read.
            Log.d(TAG, "Data insert was successful!");
            // [END insert_dataset]

            // Begin by creating the query.
            DataReadRequest readRequest = queryFitnessData();

            // [START read_dataset]
            // Invoke the History API to fetch the data with the query and await the result of
            // the read request.
            DataReadResult dataReadResult =
                    Fitness.HistoryApi.readData(mClient, readRequest).await(1, TimeUnit.MINUTES);
            // [END read_dataset]

            // For the sake of the sample, we'll print the data so we can see what we just added.
            // In general, logging fitness information should be avoided for privacy reasons.
            printData(dataReadResult);

            return null;
        }
    }

    /**
     * Create and return a {@link DataSet} of step count data for the History API.
     */
    private DataSet insertFitnessData() {
        Log.d(TAG, "Creating a new data insert request");

        // [START build_insert_data_request]
        // Set a start and end time for our data, using a start time of 1 hour before this moment.
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.HOUR_OF_DAY, -1);
        long startTime = cal.getTimeInMillis();

        // Create a data source
        DataSource dataSource = new DataSource.Builder()
                .setAppPackageName(this)
                .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .setName(TAG + " - step count")
                .setType(DataSource.TYPE_RAW)
                .build();

        // Create a data set
        int stepCountDelta = 1000;
        DataSet dataSet = DataSet.create(dataSource);
        // For each data point, specify a start time, end time, and the data value -- in this case,
        // the number of new steps.
        DataPoint dataPoint = dataSet.createDataPoint()
                .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS);
        dataPoint.getValue(Field.FIELD_STEPS).setInt(stepCountDelta);
        dataSet.add(dataPoint);
        // [END build_insert_data_request]

        return dataSet;
    }

    /**
     * Return a {@link DataReadRequest} for all step count changes in the past week.
     */
    private DataReadRequest queryFitnessData() {
        // [START build_read_data_request]
        // Setting a start and end date using a range of 1 week before this moment.
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        long startTime = cal.getTimeInMillis();


        DataReadRequest readRequest = new DataReadRequest.Builder()
                // The data request can specify multiple data types to return, effectively
                // combining multiple data queries into one call.
                // In this example, it's very unlikely that the request is for several hundred
                // datapoints each consisting of a few steps and a timestamp.  The more likely
                // scenario is wanting to see how many steps were walked per day, for 7 days.
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                        // Analogous to a "Group By" in SQL, defines how data should be aggregated.
                        // bucketByTime allows for a time span, whereas bucketBySession would allow
                        // bucketing by "sessions", which would need to be defined in code.
                .bucketByTime(1, TimeUnit.HOURS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();
        // [END build_read_data_request]

        return readRequest;
    }

    /**
     * Log a record of the query result. It's possible to get more constrained data sets by
     * specifying a data source or data type, but for demonstrative purposes here's how one would
     * dump all the data. In this sample, logging also prints to the device screen, so we can see
     * what the query returns, but your app should not log fitness information as a privacy
     * consideration. A better option would be to dump the data you receive to a local data
     * directory to avoid exposing it to other applications.
     */
    private void printData(DataReadResult dataReadResult) {
        // [START parse_read_data_result]
        // If the DataReadRequest object specified aggregated data, dataReadResult will be returned
        // as buckets containing DataSets, instead of just DataSets.
        stepsModels = new ArrayList<CMN_StepsModel>();

        if (dataReadResult.getBuckets().size() > 0) {
            for (Bucket bucket : dataReadResult.getBuckets()) {
                List<DataSet> dataSets = bucket.getDataSets();
                for (DataSet dataSet : dataSets) {
                    dumpDataSet(dataSet);
                }
            }
        } else if (dataReadResult.getDataSets().size() > 0) {
            for (DataSet dataSet : dataReadResult.getDataSets()) {
                dumpDataSet(dataSet);
            }
        }
        // [END parse_read_data_result]
    }

    // [START parse_dataset]
    private void dumpDataSet(DataSet dataSet) {
        Log.d(TAG, "Data returned for Data type: " + dataSet.getDataType().getName());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


        for (DataPoint dp : dataSet.getDataPoints()) {
            CMN_StepsModel stepsModel = new CMN_StepsModel();
            Log.d(TAG, "\tType: " + dp.getDataType().getName());
            Log.d(TAG, "\tStart: " + simpleDateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            Log.d(TAG, "\tEnd: " + simpleDateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
            stepsModel.setStartDate(simpleDateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            stepsModel.setEndDate(simpleDateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));

            for (Field field : dp.getDataType().getFields()) {
                Log.d(TAG, "\tField: " + field.getName() +
                        " Value: " + dp.getValue(field));
                stepsModel.setStepsCount("" + dp.getValue(field));
            }

            stepsModels.add(stepsModel);
        }
        Log.d("ListSize", "" + stepsModels.size());
    }
    // [END parse_dataset]

    /**
     * Delete a {@link DataSet} from the History API. In this example, we delete all
     * step count data for the past 24 hours.
     */
    private void deleteData() {
        Log.d(TAG, "Deleting today's step count data");

        // [START delete_dataset]
        // Set a start and end time for our data, using a start time of 1 day before this moment.
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        long startTime = cal.getTimeInMillis();

        //  Create a delete request object, providing a data type and a time interval
        DataDeleteRequest request = new DataDeleteRequest.Builder()
                .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS)
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .build();

        // Invoke the History API with the Google API client object and delete request, and then
        // specify a callback that will check the result.
        Fitness.HistoryApi.deleteData(mClient, request)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            Log.d(TAG, "Successfully deleted today's step count data");
                        } else {
                            // The deletion will fail if the requesting app tries to delete data
                            // that it did not insert.
                            Log.d(TAG, "Failed to delete today's step count data");
                        }
                    }
                });
        // [END delete_dataset]
    }

    public void UploadWearableData() {
        if (CMN_WearableDataActivity.stepsModels != null && CMN_WearableDataActivity.stepsModels.size() > 0) {
        } else {
            Toast.makeText(CMN_WearableDataActivity.this, CMN_ErrorMessages.getInstance().getValue(62), Toast.LENGTH_SHORT).show();
            return;
        }

        if (stepsModels.get(stepsModels.size() - 1).getStartDate().equalsIgnoreCase(lastupdatedtime)) {
            Toast.makeText(CMN_WearableDataActivity.this, CMN_ErrorMessages.getInstance().getValue(62), Toast.LENGTH_SHORT).show();
            return;
        }
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < stepsModels.size(); i++) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("AcquiredDateTime", "" + stepsModels.get(i).getStartDate());
                jsonObject.put("ActivityDateTime", "" + stepsModels.get(i).getStartDate());
                jsonObject.put("Value", "" + stepsModels.get(i).getStepsCount());
                jsonObject.put("Category", "StepCount");
                jsonObject.put("SourceType", "Android");

                jsonArray.put(jsonObject);
            } catch (Exception e) {
                e.getMessage();
            }
        }
        Log.d("jsonArray", "" + jsonArray);
        CMN_UploadWearableDataWebService uploadWearableDataWebService = new CMN_UploadWearableDataWebService(CMN_WearableDataActivity.this);
        uploadWearableDataWebService.execute("" + jsonArray);
    }

}


