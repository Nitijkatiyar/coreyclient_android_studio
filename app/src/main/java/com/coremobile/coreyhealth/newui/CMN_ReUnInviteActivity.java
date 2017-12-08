package com.coremobile.coreyhealth.newui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.coremobile.coreyhealth.AsyncTaskCompleteListener;
import com.coremobile.coreyhealth.BaseAsyncTask;
import com.coremobile.coreyhealth.R;
import com.coremobile.coreyhealth.errorhandling.CMN_ErrorMessages;
import com.coremobile.coreyhealth.googleAnalytics.CMN_Preferences;
import com.coremobile.coreyhealth.networkutils.NetworkTools;
import com.coremobile.coreyhealth.scheduling.schedulingmodels.LookupModel;
import com.coremobile.coreyhealth.uninvite_reinvite.CMN_PatientDisplayActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CMN_ReUnInviteActivity extends Activity implements AsyncTaskCompleteListener<String> {

    Button byDateBtn, byPatientBtn, fetchBtn;
    LinearLayout dateLinearLayout;
    Spinner patientListSpinner;
    EditText fromDate, toDate;
    RadioGroup radio;
    RadioButton uninviteRadio,reinviteRadio;
    String Clicked = null;
    RelativeLayout patientspinnerRealativelayoutVw;

    ArrayAdapter _adapterRe, _adapterUn;
    String fromDatestr, toDateStr;
    Calendar myCalendar;
    ProgressDialog progressDialog;
    DatePickerDialog.OnDateSetListener date;
    AsyncTaskCompleteListener<String> asyncTaskCompleteListener;
    ArrayList<LookupModel> lookupModels;
    ArrayList<LookupModel> lookupModelsByPateintRe = new ArrayList<>();
    ArrayList<LookupModel> lookupModelsByPateintUn = new ArrayList<>();


    List<String> allPatients;
    LinearLayout spinnerLinearlayoutVw;

    String SelectedButton = "patient";


    static String deactivate_reactivate_patient_search_url = null;

    static boolean radioSelected;
    static boolean fetchClicked=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_re_un_invite);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setTitle("Re-Invite/Un-Invite");
        getActionBar().setDisplayHomeAsUpEnabled(true);

        byDateBtn = (Button) findViewById(R.id.bydateButtonVw);
        byPatientBtn = (Button) findViewById(R.id.bypatientButtonVw);
        fetchBtn = (Button) findViewById(R.id.fetchbtnVw);
        dateLinearLayout = (LinearLayout) findViewById(R.id.dateLinearLayoutVw);
        fromDate = (EditText) findViewById(R.id.fromEditTextVw);
        toDate = (EditText) findViewById(R.id.toEditTextVw);
        patientListSpinner = (Spinner) findViewById(R.id.patientSpinner);

        patientspinnerRealativelayoutVw = (RelativeLayout) findViewById(R.id.patientSpinnerLayout);

        spinnerLinearlayoutVw = (LinearLayout) findViewById(R.id.spinerLayoutVw);


        patientspinnerRealativelayoutVw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                patientListSpinner.performClick();
            }
        });


        byDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               /* if(radio.is==false&&uninviteRadio.isChecked()==false){
                    Toast.makeText(getApplicationContext(),"Select option first",Toast.LENGTH_SHORT).show();
                    return;

                }*/

                spinnerLinearlayoutVw.setVisibility(View.GONE);
                dateLinearLayout.setVisibility(View.VISIBLE);
                fetchBtn.setVisibility(View.VISIBLE);
                SelectedButton = "date";
            }
        });

        byPatientBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(lookupModelsByPateintUn.size()==0&&lookupModelsByPateintRe.size()==0){
                       Toast.makeText(getApplicationContext(),"Select option first",Toast.LENGTH_SHORT).show();
                        return;

                }
                dateLinearLayout.setVisibility(View.GONE);
                spinnerLinearlayoutVw.setVisibility(View.VISIBLE);
                fetchBtn.setVisibility(View.VISIBLE);

                SelectedButton = "patient";

            }
        });

        fetchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showLoading();

                if (SelectedButton.equalsIgnoreCase("date")) {

                    if(toDate.getText().toString().isEmpty()){
                        Toast.makeText(getApplicationContext(), "To date can not be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(fromDate.getText().toString().isEmpty()){
                        Toast.makeText(getApplicationContext(), "from date can not be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("SelectedToDate", toDate.getText().toString());
                        jsonObject.put("SelectedFromDate", fromDate.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    deactivate_reactivate_patient_search_url = CMN_Preferences.getBaseUrl(CMN_ReUnInviteActivity.this)
                            + "searchPatientsToDeactivateReactivate.aspx?token=" + CMN_Preferences.getUserToken(CMN_ReUnInviteActivity.this) + "&jsondata=" + jsonObject.toString() + "&deactivate=" + radioSelected;

                    NetworkTools networkTools = NetworkTools.getInstance();
                    if (networkTools.checkNetworkConnection(CMN_ReUnInviteActivity.this) && deactivate_reactivate_patient_search_url.length() > 0) {

                        fetchClicked=true;
                        asyncTaskCompleteListener = CMN_ReUnInviteActivity.this;
                        new BaseAsyncTask(getApplicationContext(), "GET", asyncTaskCompleteListener, null).execute(deactivate_reactivate_patient_search_url);
                    } else {
                        Toast.makeText(CMN_ReUnInviteActivity.this, CMN_ErrorMessages.getInstance().getValue(12), Toast.LENGTH_SHORT).show();
                    }
                } else if (SelectedButton.equalsIgnoreCase("patient")) {

                    Intent intent = new Intent(getApplicationContext(), CMN_PatientDisplayActivity.class);

                    if (radioSelected == false) {
                        ArrayList<LookupModel> list = new ArrayList<LookupModel>();
                        list.add(lookupModelsByPateintRe.get(patientListSpinner.getSelectedItemPosition()));
                        intent.putExtra("DATA", list);
                    } else if(radioSelected==true){
                        ArrayList<LookupModel> list = new ArrayList<LookupModel>();
                        list.add(lookupModelsByPateintUn.get(patientListSpinner.getSelectedItemPosition()));
                        intent.putExtra("DATA", list);
                    }
                    intent.putExtra("radio", radioSelected);


                    startActivity(intent);
                }


            }
        });

        radio = (RadioGroup) findViewById(R.id.radioGroup);
        reinviteRadio=(RadioButton)findViewById(R.id.reinvite);

        uninviteRadio=(RadioButton)findViewById(R.id.uninvite);
        radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                View radioButton = radio.findViewById(checkedId);
                int index = radio.indexOfChild(radioButton);

                // Add logic here

                switch (index) {
                    case 0: // first button

                        radioSelected = false;

                        if (lookupModelsByPateintRe.size() == 0)
                            invokePateintDropDownApi();

                        else {
                            _adapterRe.notifyDataSetChanged();

                            patientListSpinner.setAdapter(_adapterRe);
                        }
                        break;
                    case 1: // secondbutton

                        radioSelected = true;
                        if (lookupModelsByPateintUn.size() == 0)
                            invokePateintDropDownApi();
                        else {
                            _adapterUn.notifyDataSetChanged();
                            patientListSpinner.setAdapter(_adapterUn);
                        }
                        break;
                }
            }
        });

        myCalendar = Calendar.getInstance();

        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        fromDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(CMN_ReUnInviteActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                Clicked = "from";
            }
        });

        toDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(CMN_ReUnInviteActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                Clicked = "to";
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        dateLinearLayout.setVisibility(View.GONE);
        spinnerLinearlayoutVw.setVisibility(View.GONE);
        fetchBtn.setVisibility(View.GONE);
        lookupModelsByPateintRe.clear();
        lookupModelsByPateintUn.clear();


         SelectedButton = "patient";

        fetchClicked=false;
        //radio.clearCheck();




    }

    @Override
    protected void onStop() {
        super.onStop();

        fetchClicked=false;
      /*  reinviteRadio.setChecked(false);
        uninviteRadio.setChecked(false);*/
      radio.clearCheck();

    }

    public void invokePateintDropDownApi() {
        showLoading();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("SelectedToDate", null);
            jsonObject.put("SelectedFromDate", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        deactivate_reactivate_patient_search_url = CMN_Preferences.getBaseUrl(CMN_ReUnInviteActivity.this)
                + "searchPatientsToDeactivateReactivate.aspx?token=" + CMN_Preferences.getUserToken(CMN_ReUnInviteActivity.this) + "&jsondata=" + jsonObject.toString() + "&deactivate=" + radioSelected;

        NetworkTools networkTools = NetworkTools.getInstance();
        if (networkTools.checkNetworkConnection(CMN_ReUnInviteActivity.this) && deactivate_reactivate_patient_search_url.length() > 0) {

            asyncTaskCompleteListener = CMN_ReUnInviteActivity.this;
            new BaseAsyncTask(getApplicationContext(), "GET", asyncTaskCompleteListener, null).execute(deactivate_reactivate_patient_search_url);
        } else {
            Toast.makeText(CMN_ReUnInviteActivity.this, CMN_ErrorMessages.getInstance().getValue(12), Toast.LENGTH_SHORT).show();
        }


    }

    private void updateLabel() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        if (Clicked.equalsIgnoreCase("from"))
            fromDate.setText(sdf.format(myCalendar.getTime()));
        else if (Clicked.equalsIgnoreCase("to"))
            toDate.setText(sdf.format(myCalendar.getTime()));

    }

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

        if(result==null){
            return;
        }
        if (SelectedButton.equalsIgnoreCase("date")) {
            try {
                parseJsonData(new JSONObject(result), 0);
                Intent intent = new Intent(getApplicationContext(), CMN_PatientDisplayActivity.class);
                intent.putExtra("DATA", lookupModels);
                intent.putExtra("radio", radioSelected);
                if(fetchClicked==true)
                startActivity(intent);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {


            try {
                parseJsonData(new JSONObject(result), 0);

                if (lookupModelsByPateintRe.size() == 0 || lookupModelsByPateintUn.size() == 0)
                    parseJsonData(new JSONObject(result));

            } catch (Exception e) {
                e.printStackTrace();
                ;
            }
        }
    }

    private void parseJsonData(JSONObject response) throws JSONException {

        if (response.getJSONObject("Result").getInt("Code") == 0) {

            if (radioSelected == false)
                lookupModelsByPateintRe = new ArrayList<>();
            else if (radioSelected == true) {
                lookupModelsByPateintUn = new ArrayList<>();
            }
            LookupModel patientModel;

            JSONObject dataObject = response.getJSONObject("Data");
            JSONArray valuesJsonArray = dataObject.getJSONArray("Values");
            for (int i = 0; i < valuesJsonArray.length(); i++) {
                JSONObject jsonObject = valuesJsonArray.getJSONObject(i);
                patientModel = new LookupModel();
                JSONArray rowJsonArray = jsonObject.getJSONArray("RowValues");
                patientModel.setDisplayName(rowJsonArray.getJSONObject(0).getString("Value"));
                patientModel.setDateOfSurery(rowJsonArray.getJSONObject(1).getString("Value"));
                patientModel.setAnonId(rowJsonArray.getJSONObject(2).getString("Value"));
                patientModel.setSurgeon(rowJsonArray.getJSONObject(3).getString("Value"));

                patientModel.setID(jsonObject.getString("RowId"));
                if (radioSelected == false) {
                    lookupModelsByPateintRe.add(patientModel);
                } else if (radioSelected == true) {
                    lookupModelsByPateintUn.add(patientModel);
                }
            }
            if (radioSelected == false) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        allPatients = new ArrayList<String>();
                        for (int i = 0; i < lookupModelsByPateintRe.size(); i++) {
                            allPatients.add(lookupModelsByPateintRe.get(i).getDisplayName());
                        }
                        _adapterRe = new ArrayAdapter<String>(CMN_ReUnInviteActivity.this,
                                android.R.layout.simple_spinner_item, allPatients);
                        _adapterRe.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        patientListSpinner.setAdapter(_adapterRe);
                    }
                });
            } else if (radioSelected == true) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        allPatients = new ArrayList<String>();
                        for (int i = 0; i < lookupModelsByPateintUn.size(); i++) {
                            allPatients.add(lookupModelsByPateintUn.get(i).getDisplayName());
                        }
                        _adapterUn = new ArrayAdapter<String>(CMN_ReUnInviteActivity.this,
                                android.R.layout.simple_spinner_item, allPatients);
                        _adapterUn.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        patientListSpinner.setAdapter(_adapterUn);
                    }
                });
            }
        }
    }

    private void parseJsonData(JSONObject response, int id) throws JSONException {

        if (response.getJSONObject("Result").getInt("Code") == 0) {
            lookupModels = new ArrayList<>();
            LookupModel patientModel;

            JSONObject dataObject = response.getJSONObject("Data");
            JSONArray valuesJsonArray = dataObject.getJSONArray("Values");
            for (int i = 0; i < valuesJsonArray.length(); i++) {
                JSONObject jsonObject = valuesJsonArray.getJSONObject(i);
                patientModel = new LookupModel();
                JSONArray rowJsonArray = jsonObject.getJSONArray("RowValues");
                patientModel.setDisplayName(rowJsonArray.getJSONObject(0).getString("Value"));
                patientModel.setDateOfSurery(rowJsonArray.getJSONObject(1).getString("Value"));
                patientModel.setAnonId(rowJsonArray.getJSONObject(2).getString("Value"));
                patientModel.setSurgeon(rowJsonArray.getJSONObject(3).getString("Value"));
                patientModel.setID(jsonObject.getString("RowId"));

                lookupModels.add(patientModel);
            }

        }


    }

    public void showLoading() {
        progressDialog = new ProgressDialog(CMN_ReUnInviteActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
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
