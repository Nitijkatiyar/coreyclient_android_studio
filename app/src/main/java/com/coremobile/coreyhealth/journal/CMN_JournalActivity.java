package com.coremobile.coreyhealth.journal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coremobile.coreyhealth.R;
import com.coremobile.coreyhealth.base.CMN_AppBaseActivity;
import com.coremobile.coreyhealth.googleAnalytics.CMN_Preferences;
import com.coremobile.coreyhealth.networkutils.NetworkTools;
import com.coremobile.coreyhealth.newui.MessageTabActivityCMN;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CMN_JournalActivity extends CMN_AppBaseActivity {

    String patientId, token;
    ListView journalListVw;
    String url;

    private static String DATA = "Data";
    private static String SERVER_DIALOG_RESPONSE = "No Data to display";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cmn__journal);

        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setTitle(
                "" + getResources().getString(R.string.view_journal_text));
        getActionBar().setDisplayHomeAsUpEnabled(true);
        if (getIntent().hasExtra("PATIENT_ID")) {
            patientId = getIntent().getExtras().getString("PATIENT_ID");
        }


        token = CMN_Preferences.getUserToken(getApplicationContext());
        journalListVw = (ListView) findViewById(R.id.journalListVw);

        url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE + "GetPatientJournalEntries.aspx?token=" +
                token + "&patientid=" + patientId;
        journalListVw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView title = (TextView) view.findViewById(R.id.titleTextVw);
                TextView date = (TextView) view.findViewById(R.id.dateTextVw);

                Intent intent = new Intent(getApplicationContext(), JournalDisplayActivity.class);
                intent.putExtra("journal", title.getText());
                intent.putExtra("date", date.getText());
                startActivity(intent);
            }
        });
    }

    public class JournalAsyncTask extends AsyncTask<String, Void, String> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //     items = new ArrayList();
            dialog = new ProgressDialog(CMN_JournalActivity.this);
            dialog.setMessage("Please Wait...");
            dialog.setCancelable(false);
            dialog.show();
        }


        @Override
        protected String doInBackground(String... params) {
            BufferedReader reader;
            StringBuffer buffer;
            String res = null;

            try {
                URL url = new URL(params[0]);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setReadTimeout(40000);
                con.setConnectTimeout(40000);
                con.setRequestMethod("GET");
                con.setRequestProperty("Content-Type", "application/json");
                int status = con.getResponseCode();
                InputStream inputStream;
                if (status == HttpURLConnection.HTTP_OK) {
                    inputStream = con.getInputStream();
                } else {
                    inputStream = con.getErrorStream();
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                res = buffer.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return res;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            System.out.println("JSON RESP:" + s);
            String response = s;
            try {

                JSONObject jsonObject
                        = new JSONObject(response);

                JSONObject dataJsonObject = jsonObject.getJSONObject(DATA);

                Map<String, String> map = new HashMap<>();
                Iterator iter = dataJsonObject.keys();
                while (iter.hasNext()) {
                    String key = (String) iter.next();
                    String value = dataJsonObject.getString(key);
                    map.put(key, value);
                   /* journalItemList.add(map);*/
                }

                if (map.size() > 0) {
                    dialog.dismiss();
                    journalListVw.setAdapter(new JournalRowViewAdapter(getApplicationContext(), map));

                } else {
                    dialog.dismiss();
                    Toast.makeText(CMN_JournalActivity.this, SERVER_DIALOG_RESPONSE, Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_journal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            //noinspection SimplifiableIfStatement

            case R.id.action_name:

                Intent intent = new Intent(getApplicationContext(), CMN_AddJournalActivity.class);
                intent.putExtra("PATIENT_ID", patientId);
                startActivity(intent);
                return true;


            case android.R.id.home:
                this.finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        NetworkTools networkTools = NetworkTools.getInstance();
        if (networkTools.checkNetworkConnection(CMN_JournalActivity.this)) {

            new JournalAsyncTask().execute(url);
        } else
            Toast.makeText(getApplicationContext(), CMN_AddJournalActivity.NETWORK_MESSAGE, Toast.LENGTH_LONG).show();

    }
}
