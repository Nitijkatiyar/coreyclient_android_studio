package com.coremobile.coreyhealth.mypatients;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.coremobile.coreyhealth.AsyncTaskCompleteListener;
import com.coremobile.coreyhealth.BaseAsyncTask;
import com.coremobile.coreyhealth.R;
import com.coremobile.coreyhealth.aesencryption.CMN_AES;
import com.coremobile.coreyhealth.errorhandling.CMN_ErrorMessages;
import com.coremobile.coreyhealth.googleAnalytics.CMN_Preferences;
import com.coremobile.coreyhealth.journal.CMN_AddJournalActivity;
import com.coremobile.coreyhealth.networkutils.NetworkTools;
import com.coremobile.coreyhealth.newui.MessageTabActivityCMN;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CMN_MyPatientActivity extends Activity implements AsyncTaskCompleteListener<String>, AlertDialogRadio.AlertPositiveListener {

    MyPatientAdapter dataAdapter = null;
    ProgressDialog progressDialog;

    static String vToken;
    public static String PATIENT_URL = null, FILTER_URL = null;
    Button searchBtnVw;
    ListView listView;
    List<MyPateintModel> myPateintModelsList = new ArrayList<>();

    List<MyPateintModel> myPateintModelsListOriginal = new ArrayList<>();
    List<DataModel> filterList = new ArrayList<>();

    int position = 0;
    boolean filterFlag = true;

    boolean filterEnabled = false;
    static int filterCriteria = 0;


    String setFilterStr = null;
    RelativeLayout filterRelativeLayout;
    ListView filterListVw;
    EditText myFilter;
    List filterItems = new ArrayList();

    Button okBtn, cancelBtn;
    ArrayAdapter<String> filterAdapter;

    View dimView;
    Boolean isParsePateintListcompelted = false;
    Boolean isFilterListcompelted = false;
    Boolean isGetFiltered = false;
    Boolean isSetFiltered = false;


    AsyncTaskCompleteListener<String> asyncTaskCompleteListener;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_pateint_menu, menu);

        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cmn__my_patient);


        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setTitle("My Patient List");
        getActionBar().setDisplayHomeAsUpEnabled(true);
        myFilter = (EditText) findViewById(R.id.myFilter);
        searchBtnVw = (Button) findViewById(R.id.searchBtnVw);

        filterListVw = (ListView) findViewById(R.id.filterListVw);
        filterRelativeLayout = (RelativeLayout) findViewById(R.id.relativeFilterLayoutVw);

        dimView = (View) findViewById(R.id.dimView);
        okBtn = (Button) findViewById(R.id.okBtn);
        cancelBtn = (Button) findViewById(R.id.cancelBtn);
        progressDialog = new ProgressDialog(CMN_MyPatientActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        filterRelativeLayout.setVisibility(View.GONE);
        //Generate list View from ArrayList
        NetworkTools networkTools = NetworkTools.getInstance();

        vToken = CMN_Preferences.getUserToken(getApplicationContext());
        PATIENT_URL = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE + "GetAllContexts_s.aspx?token=" + vToken;
        FILTER_URL = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE + "GetFilterCriteria.aspx?token=" + vToken + "&FilterFor=PatientList";


        if (networkTools.checkNetworkConnection(CMN_MyPatientActivity.this)) {
            BaseAsyncTask baseAsyncTask = new BaseAsyncTask(getApplicationContext(), "GET", this, null);
            baseAsyncTask.execute(PATIENT_URL);

        } else {
            Toast.makeText(getApplicationContext(), CMN_AddJournalActivity.NETWORK_MESSAGE, Toast.LENGTH_LONG).show();

            return;
        }

        listView = (ListView) findViewById(R.id.listView1);


        //enables filtering for the contents of the given ListView
        listView.setTextFilterEnabled(true);


        filterListVw.setChoiceMode(ListView.CHOICE_MODE_SINGLE);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

            }
        });


        myFilter.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                dataAdapter.getFilter().filter(s.toString());
            }
        });


        searchBtnVw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!filterEnabled) {
                    Toast.makeText(getApplicationContext(), "Select filter first.", Toast.LENGTH_SHORT).show();
                    return;
                }
                dataAdapter.clear();
                myPateintModelsList.clear();
                for (MyPateintModel d : myPateintModelsListOriginal) {

                    String filter = filterList.get(filterCriteria).getAttributeName();


                    String filterText = myFilter.getText().toString().toLowerCase();
                    if (filter.equalsIgnoreCase("DisplayName")) {

                        if (d.getDisplayName() != null && d.getDisplayName().toString().toLowerCase().contains(filterText)) {
                            Log.e("KK", d.getDisplayName());
                            myPateintModelsList.add(d);
                        }
                    } else if (filter.equalsIgnoreCase("OR")) {
                        if (d.getOR() != null && d.getOR().toLowerCase().contains(filterText)) {

                            myPateintModelsList.add(d);
                        }
                    } else if (filter.equalsIgnoreCase("Modality")) {
                        if (d.getModality() != null && d.getModality().toLowerCase().contains(filterText)) {

                            myPateintModelsList.add(d);
                        }
                    } else if (filter.equalsIgnoreCase("Specialty")) {

                        if (d.getSpeciality() != null && d.getSpeciality().toLowerCase().contains(filterText)) {
                            Log.e("KK", d.getDisplayName());
                            myPateintModelsList.add(d);
                        }
                    } else if (filter.equalsIgnoreCase("AnonId")) {
                        if (d.getAnonId() != null && d.getAnonId().toLowerCase().contains(filterText)) {

                            myPateintModelsList.add(d);
                        }
                    } else if (filter.equalsIgnoreCase("Surgeon")) {

                        if (d.getSurgeon() != null && d.getSurgeon().toLowerCase().contains(filterText)) {
                            Log.e("KK", d.getDisplayName());
                            myPateintModelsList.add(d);
                        }

                    } else if (filter.equalsIgnoreCase("DateOfProcedure")) {
                        if (d.getDateOfProcedure() != null && d.getDateOfProcedure().toLowerCase().contains(filterText)) {

                            myPateintModelsList.add(d);
                        }

                    } else if (filter.equalsIgnoreCase("TimeOfProcedure")) {
                        if (d.getTimeOfProcedure() != null && d.getTimeOfProcedure().toLowerCase().contains(filterText)) {
                            myPateintModelsList.add(d);
                        }
                    } else if (filter.equalsIgnoreCase("ProcedureRoom")) {
                        if (d.getProcedureRoom() != null && d.getProcedureRoom().toLowerCase().contains(filterText)) {
                            Log.e("KK", d.getDisplayName());
                            myPateintModelsList.add(d);
                        }
                    }
                    //something here
                }
                dataAdapter.updateitems(myPateintModelsList);
                // dataAdapter.notifyDataSetChanged();
            }
        });

        filterListVw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                filterCriteria = position;
            }
        });
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                filterRelativeLayout.setVisibility(View.GONE);
                dimView.setVisibility(View.GONE);

                JSONArray jsonArray = new JSONArray();
                JSONObject jsonObject = new JSONObject();

                try {
                    jsonObject.put("Prefname", "FilterInPatientList");
                    jsonObject.put("Value", filterList.get(filterCriteria).getAttributeName());
                    jsonArray.put(0, jsonObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                        + "SetUserPreference.aspx?token=" + CMN_Preferences.getUserToken(CMN_MyPatientActivity.this);

                NetworkTools networkTools = NetworkTools.getInstance();

                if (networkTools.checkNetworkConnection(CMN_MyPatientActivity.this) && url.length() > 0) {


                    isSetFiltered=true;
                    asyncTaskCompleteListener = CMN_MyPatientActivity.this;
                    new BaseAsyncTask(getApplicationContext(), "POST", asyncTaskCompleteListener, jsonArray.toString()).execute(url);
                } else {
                    Toast.makeText(getApplicationContext(), CMN_ErrorMessages.getInstance().getValue(12), Toast.LENGTH_SHORT).show();
                }

            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (filterCriteria == 0)
                    filterCriteria = 0;
                filterRelativeLayout.setVisibility(View.GONE);
                dimView.setVisibility(View.GONE);
            }
        });

        String url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                + "GetUserPreferences.aspx?token=" + CMN_Preferences.getUserToken(CMN_MyPatientActivity.this);

        if (networkTools.checkNetworkConnection(CMN_MyPatientActivity.this) && url.length() > 0) {


            new BaseAsyncTask(getApplicationContext(), "GET", this, "").execute(url);
        } else {
            Toast.makeText(getApplicationContext(), CMN_ErrorMessages.getInstance().getValue(12), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();


    }


    private void showFilterDialog() {


        filterRelativeLayout.setVisibility(View.VISIBLE);
        dimView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_filter:
                applyfilter();
                return true;
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void applyfilter() {


        if (!isFilterListcompelted) {
            NetworkTools networkTools = NetworkTools.getInstance();

            if (networkTools.checkNetworkConnection(CMN_MyPatientActivity.this)) {
                BaseAsyncTask baseAsyncTask = new BaseAsyncTask(getApplicationContext(), "GET", this, null);
                baseAsyncTask.execute(FILTER_URL);
                filterEnabled = true;
            } else {
                Toast.makeText(getApplicationContext(), CMN_AddJournalActivity.NETWORK_MESSAGE, Toast.LENGTH_LONG).show();

                return;
            }
        } else
            showFilterDialog();
    }

    /**
     * Parsing the feed results and get the list
     *
     * @param result
     */
    private String parseResult(String result) {
        String message = null;
        try {
            JSONObject response;
            response = new JSONObject(CMN_AES.decryptData(result, CMN_Preferences.getUserToken(CMN_MyPatientActivity.this)));


            JSONArray jsonArray = response.getJSONArray("Data");
            Gson gson = new Gson();

            for (int i = 0; i < jsonArray.length(); i++) {

                MyPateintModel myPateintModel = gson.fromJson(jsonArray.getJSONObject(i).toString(), MyPateintModel.class);
                myPateintModelsList.add(myPateintModel);

            }

            //create an ArrayAdaptar from the String Array
            dataAdapter = new MyPatientAdapter(getApplicationContext(),
                    R.layout.country_list, myPateintModelsList);

            // Assign adapter to ListView
            listView.setAdapter(dataAdapter);


        } catch (JSONException e) {
            e.printStackTrace();
        } finally {

            myPateintModelsListOriginal.addAll(myPateintModelsList);

            //   displayListView();

//            dataAdapter.notifyDataSetChanged();

            return message;
        }
    }

    private String parseResultforFilter(String result) {
        String message = null;
        try {
            JSONObject response;
            response = new JSONObject(result);


            JSONArray jsonArray = response.getJSONArray("Data");
            Gson gson = new Gson();

            for (int i = 0; i < jsonArray.length(); i++) {
                DataModel filterModel = gson.fromJson(jsonArray.getJSONObject(i).toString(), DataModel.class);
                filterList.add(filterModel);
            }

            prepareFilterListView();
            isFilterListcompelted = true;
        } catch (JSONException e) {
            e.printStackTrace();
            isFilterListcompelted = false;
//            Toast.makeText(CMN_MyPatientActivity.this, "We are facing issue in getting filter list. Please try again later", Toast.LENGTH_SHORT).show();
        } finally {
//            dataAdapter.notifyDataSetChanged();

            return message;
        }
    }

    public void prepareFilterListView() {
        /**
         * filter list view
         */

        filterRelativeLayout.setVisibility(View.VISIBLE);
        dimView.setVisibility(View.VISIBLE);

        String[] filterItems1 = new String[filterList.size()];
        for (int i = 0; i < filterList.size(); i++) {
            filterItems1[i] = filterList.get(i).getDisplayText();
        }

        filterItems = Arrays.asList(filterItems1);

        filterAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_single_choice,
                android.R.id.text1, filterItems);
        filterListVw.setAdapter(filterAdapter);

        for (int i = 0; i < filterList.size(); i++) {
            DataModel d = filterList.get(i);
            if (d.getAttributeName().equalsIgnoreCase(setFilterStr)) {
                filterCriteria = i;/*
                filterListVw.post(new Runnable() {
                    @Override
                    public void run() {
                        filterListVw.clearFocus();
                        filterAdapter.notifyDataSetChanged();
                        filterListVw.requestFocusFromTouch();
                        filterListVw.setSelection(filterCriteria);
                    }
                });*/
                filterListVw.setItemChecked(filterCriteria, true);
                return;
            }
        }

    }


    @Override
    public void onTaskStart() {
        if (progressDialog != null && !progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    @Override
    public void onTaskComplete(String result) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

      /*  Log.e("dd", result);*/

        if (isParsePateintListcompelted == false) {
            parseResult(result);
            isParsePateintListcompelted = true;
        } else if (isGetFiltered == false) {
            parseResultAlreadySetFilters(result);
            isGetFiltered = true;

        } else if(isSetFiltered==true) {


            parseResultForSetFilter(result);
        }
        else{

            parseResultforFilter(result);



        }

    }

    public void parseResultForSetFilter(String result) {

        try {
            isSetFiltered = false;
            Gson gson = new Gson();
            JSONObject jsonObject = new JSONObject(result);

            JSONObject jsonObjectResult = jsonObject.getJSONObject("Result");
            int code = jsonObjectResult.getInt("Code");
            String message = jsonObjectResult.getString("Message");

            if (code == -1) {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                return;
            }

        }

        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onPositiveClick(int position) {

        this.position = position;

        filterCriteria = position;

        String filter = filterList.get(filterCriteria).getAttributeName();
        dataAdapter.setFilterName(filter);
    }

    void parseResultAlreadySetFilters(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("Data");
            for (int i = 0; i < jsonArray.length(); i++) {
                if (jsonArray.getJSONObject(i).getString("Code").equalsIgnoreCase("FilterInPatientList")) {

                    if (!jsonArray.getJSONObject(i).getString("Value").isEmpty() || jsonArray.getJSONObject(i).getString("Value") != null) {
                        setFilterStr = jsonArray.getJSONObject(i).getString("Value");

                    } else {
                        // fromDatePickerDialog.show();
                    }
                }
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

}