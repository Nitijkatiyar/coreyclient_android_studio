package com.coremobile.coreyhealth;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.coremobile.coreyhealth.adapters.GridViewAdapter;
import com.coremobile.coreyhealth.googleAnalytics.CMN_Preferences;
import com.coremobile.coreyhealth.networkutils.NetworkTools;
import com.coremobile.coreyhealth.newui.CMN_ImageDetailsActivity;
import com.coremobile.coreyhealth.newui.MessageTabActivityCMN;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class GridImageActivityCMN extends BaseActivityCMN {
    private static final String TAG = GridImageActivityCMN.class.getSimpleName();
    private GridView mGridView;
    private ProgressBar mProgressBar;
    private GridViewAdapter mGridAdapter;
    private ArrayList<GridItem> mGridData;
    String objid, patientid, token, providerId, objName;
    private String FEED_URL = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_picker_layout);

        if (getIntent().hasExtra("OBJECT_ID")) {
            objid = getIntent().getExtras().getString("OBJECT_ID");
            objName = getIntent().getExtras().getString("OBJECT_NAME");
        }


        if (getIntent().hasExtra("PATIENT_ID")) {
            patientid = getIntent().getExtras().getString("PATIENT_ID");
        }


        token = CMN_Preferences.getUserToken(getApplicationContext());

        mGridView = (GridView) findViewById(R.id.gridView);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setTitle("" + objName);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        //Initialize with empty data
        mGridData = new ArrayList<>();
        mGridAdapter = new GridViewAdapter(this, R.layout.image_grid_layout, mGridData);
        mGridView.setAdapter(mGridAdapter);
        //Start download
        if (objid.equalsIgnoreCase("114"))
            FEED_URL = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE + "GetPatientpictures.aspx?token=" + token;
        else if (objid.equalsIgnoreCase("450")) {
            FEED_URL = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE + "GetPatientpictures.aspx?token=" + token + "&uploadBy=Provider";
        } else if (objid.equalsIgnoreCase("451")) {
            FEED_URL = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE + "GetPatientpictures.aspx?token=" + token + "&uploadBy=Provider";
        }
        NetworkTools networkTools = NetworkTools.getInstance();
        if (networkTools.checkNetworkConnection(GridImageActivityCMN.this))
            new AsyncHttpTask().execute(FEED_URL);
        else {
            Toast.makeText(getApplicationContext(), "Please check your internet connection.", Toast.LENGTH_SHORT).show();
        }
        mProgressBar.setVisibility(View.VISIBLE);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
//Get item at position
                GridItem item = (GridItem) parent.getItemAtPosition(position);
//Pass the image title and url to DetailsActivity
                Intent intent = new Intent(GridImageActivityCMN.this, CMN_ImageDetailsActivity.class);
                intent.putExtra("title", item.getTitle());
                intent.putExtra("image", item.getUrl());
//Start details activity
                startActivity(intent);
            }
        });
    }

    //Downloading data asynchronously
    public class AsyncHttpTask extends AsyncTask<String, Void, Integer> {
        @Override
        protected Integer doInBackground(String... params) {
            Integer result = 0;
            try {
                Log.e("URL", params[0]);
                // Create Apache HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(params[0]);
                JSONObject object = new JSONObject();
                String postBody = null;
                try {

                    object.put("Patientid", patientid);

                    postBody = object.toString();

                } catch (Exception ex) {

                }

                httppost.setEntity(new StringEntity(postBody, "UTF8"));
                httppost.setHeader("Content-type", "application/json");
                HttpResponse httpResponse = httpclient.execute(httppost);
                int statusCode = httpResponse.getStatusLine().getStatusCode();
                // 200 represents HTTP OK
                if (statusCode == 200) {
                    String response = streamToString(httpResponse.getEntity().getContent());
                    parseResult(response);
                    result = 1; // Successful
                } else {
                    result = 0; //"Failed
                }
            } catch (Exception e) {
                Log.d(TAG, e.getLocalizedMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            // Download complete. Let us update UI
            if (result == 1) {
                mGridAdapter.setGridData(mGridData);
            } else {
                Toast.makeText(GridImageActivityCMN.this, "Failed to fetch data!", Toast.LENGTH_SHORT).show();
            }
            mProgressBar.setVisibility(View.GONE);
        }
    }

    String streamToString(InputStream stream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
        String line;
        String result = "";
        while ((line = bufferedReader.readLine()) != null) {
            result += line;
        }
        // Close stream
        if (null != stream) {
            stream.close();
        }
        return result;
    }

    /**
     * Parsing the feed results and get the list
     *
     * @param result
     */
    private void parseResult(String result) {
        try {
            JSONObject response = new JSONObject(result);
            JSONArray posts = response.optJSONArray("Data");
            GridItem item;
            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);
                String title = post.optString("Comment");
                item = new GridItem();
                item.setTitle(title);
                String url = post.getString("ResourceURL");

                item.setUrl(url);
                mGridData.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}