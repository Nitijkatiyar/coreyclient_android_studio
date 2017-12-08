package com.coremobile.coreyhealth.surgeonschedule;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.coremobile.coreyhealth.AsyncTaskCompleteListener;
import com.coremobile.coreyhealth.BaseAsyncTask;
import com.coremobile.coreyhealth.R;
import com.coremobile.coreyhealth.aesencryption.CMN_AES;
import com.coremobile.coreyhealth.errorhandling.CMN_ErrorMessages;
import com.coremobile.coreyhealth.googleAnalytics.CMN_Preferences;
import com.coremobile.coreyhealth.networkutils.NetworkTools;
import com.coremobile.coreyhealth.newui.MessageTabActivityCMN;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SurgeonScheduleActivity extends Activity implements AsyncTaskCompleteListener<String> {

    TextView selectedTextVw;
    Button calenderBtnBw;
    ListView listView;
    PatientInfoSurgeonScheduleRowViewAdapter patientInfoRowViewAdapter;
    ArrayList<SurgeonSchedulePateintModel> allPatients;
    ArrayList<SurgeonModel> allSurgeons;
    ArrayList<String> allSurgeonsNames;
    ArrayList<String> allPatientsName;


    Calendar myCalendar;

    DatePickerDialog.OnDateSetListener date;

    Spinner surgeonListSpinner;
    RelativeLayout patientspinnerRealativelayoutVw;
    ProgressDialog progressDialog;


    ArrayAdapter surgeonAdapter;
    ArrayAdapter pateintListAdapter;

    AsyncTaskCompleteListener<String> asyncTaskCompleteListener;
    ;

    String token;
    String SURGEON_URL;
    String PATIENT_LIST_URL;
    int selectedSurgeonPos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surgeon_schedule);

        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setTitle("Surgeon Schedules");
        getActionBar().setDisplayHomeAsUpEnabled(true);
/*
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.main_container, new MaterialCalendarFragmentS()).commit();
        }
*/

        selectedTextVw = (TextView) findViewById(R.id.selectedDateTextVw);
        calenderBtnBw = (Button) findViewById(R.id.bydateButtonVw);
        listView = (ListView) findViewById(R.id.pateintListVw);

        surgeonListSpinner = (Spinner) findViewById(R.id.patientSpinner);

        patientspinnerRealativelayoutVw = (RelativeLayout) findViewById(R.id.patientSpinnerLayout);

        myCalendar = Calendar.getInstance();

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date2 = new Date();
        selectedTextVw.setText(dateFormat.format(date2).toString());
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

        calenderBtnBw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new DatePickerDialog(SurgeonScheduleActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        patientspinnerRealativelayoutVw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                surgeonListSpinner.performClick();
                selectedSurgeonPos=surgeonListSpinner.getSelectedItemPosition();


            }
        });


        surgeonListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {

                selectedSurgeonPos=position;
                  NetworkTools networkTools = NetworkTools.getInstance();

                if (networkTools.checkNetworkConnection(SurgeonScheduleActivity.this) && PATIENT_LIST_URL.length() > 0) {

                    asyncTaskCompleteListener = SurgeonScheduleActivity.this;
                    new BaseAsyncTask(getApplicationContext(), "POST", asyncTaskCompleteListener, generateJson().toString()).execute(PATIENT_LIST_URL);
                } else {
                    Toast.makeText(SurgeonScheduleActivity.this, CMN_ErrorMessages.getInstance().getValue(12), Toast.LENGTH_SHORT).show();
                }


            } // to close the onItemSelected
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
        allPatients = new ArrayList();

        allPatientsName = new ArrayList();
        allSurgeons = new ArrayList();
        allSurgeonsNames = new ArrayList();


        surgeonAdapter = new ArrayAdapter<String>(SurgeonScheduleActivity.this,
                android.R.layout.simple_spinner_item, allSurgeonsNames);
        surgeonAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        surgeonListSpinner.setAdapter(surgeonAdapter);


        pateintListAdapter = new ArrayAdapter(SurgeonScheduleActivity.this,
                R.layout.patient_text_view, allPatientsName);

        listView.setAdapter(pateintListAdapter);


        token = CMN_Preferences.getUserToken(getApplicationContext());
        SURGEON_URL = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE + "GetSurgeonsForScheduler.aspx?token=" + token;

        NetworkTools networkTools = NetworkTools.getInstance();
        if (networkTools.checkNetworkConnection(SurgeonScheduleActivity.this) && SURGEON_URL.length() > 0) {

            asyncTaskCompleteListener = SurgeonScheduleActivity.this;
            new BaseAsyncTask(getApplicationContext(), "GET", asyncTaskCompleteListener, null).execute(SURGEON_URL);
        } else {
            Toast.makeText(SurgeonScheduleActivity.this, CMN_ErrorMessages.getInstance().getValue(12), Toast.LENGTH_SHORT).show();
        }

        /*
        for pateint list view
         */

         PATIENT_LIST_URL = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE + "SurgeonSchedules.aspx?token=" + token;


       /* patientInfoRowViewAdapter = new PatientInfoSurgeonScheduleRowViewAdapter(getApplicationContext(), allPatientsName);
        listView.setAdapter(patientInfoRowViewAdapter);*/

       listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

               AlertDialog.Builder builder;
               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                   builder = new AlertDialog.Builder(SurgeonScheduleActivity.this,android.R.style.Theme_Material_Light_Dialog_Alert);
               } else {
                   builder = new AlertDialog.Builder(SurgeonScheduleActivity.this);
               }
               builder.setTitle("Patient Info")
                       .setMessage(Html.fromHtml("<b>Patient Name :  </b> " +  allPatients.get(position).getName()+"<br/>"
                       +"<b>D.O.S Start : </b>" + allPatients.get(position).getDOSStart()+"<br/>"

                                       +"<b>D.O.S End : </b>" + allPatients.get(position).getDOSEnd()+"<br/>"
                                       +"<b>Surgeon :   </b>" +  allPatients.get(position).getSurgeon()+"<br/>"
                               + "<b>Speciality :  </b>" +allPatients.get(position).getSpecialty()+"<br/>"
                                       + "<b>Procedure :  </b>" +allPatients.get(position).getProcedure())
                       )

                       .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                           public void onClick(DialogInterface dialog, int which) {

                       dialog.dismiss();                           }
                       })

                       .show();

           }
       });

    }

    private void updateLabel() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        selectedTextVw.setText(sdf.format(myCalendar.getTime()));
        NetworkTools networkTools = NetworkTools.getInstance();

        if (networkTools.checkNetworkConnection(SurgeonScheduleActivity.this) && PATIENT_LIST_URL.length() > 0) {

            asyncTaskCompleteListener = SurgeonScheduleActivity.this;
            new BaseAsyncTask(getApplicationContext(), "POST", asyncTaskCompleteListener, generateJson().toString()).execute(PATIENT_LIST_URL);
        } else {
            Toast.makeText(SurgeonScheduleActivity.this, CMN_ErrorMessages.getInstance().getValue(12), Toast.LENGTH_SHORT).show();
        }

    }

    public void showLoading() {
        progressDialog = new ProgressDialog(SurgeonScheduleActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
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


    @Override
    public void onTaskComplete(String result) {

        if (progressDialog != null) {
            progressDialog.dismiss();
        }


        if (isSurgeonParsing)
            parseSurgeonList(result);
        else
            parseResult(result);

    }


    boolean isSurgeonParsing = true;

    @Override
    public void onTaskStart() {

        showLoading();
    }

    public void parseSurgeonList(String result) {

        try {
            isSurgeonParsing = false;
            Gson gson = new Gson();
            JSONObject jsonObject = new JSONObject(result);

            JSONObject jsonObjectResult=jsonObject.getJSONObject("Result");
            int code=jsonObjectResult.getInt("Code");
           String message=jsonObjectResult.getString("Message");

            if(code==-1){
                showAlert(message,"surgeonList");
                return;
            }


            JSONArray jsonArray = jsonObject.getJSONArray("Data");
            for (int i = 0; i < jsonArray.length(); i++) {
                SurgeonModel surgeonModel = gson.fromJson(jsonArray.get(i).toString(), SurgeonModel.class);
                allSurgeons.add(surgeonModel);
            }

            for (int i = 0; i < allSurgeons.size(); i++) {
                allSurgeonsNames.add(allSurgeons.get(i).getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("No resource found.","surgeonList");
        }

        finally {
            surgeonAdapter.notifyDataSetChanged();/*
            NetworkTools networkTools = NetworkTools.getInstance();

            if (networkTools.checkNetworkConnection(SurgeonScheduleActivity.this) && PATIENT_LIST_URL.length() > 0) {

                asyncTaskCompleteListener = SurgeonScheduleActivity.this;
                new BaseAsyncTask(getApplicationContext(), "POST", asyncTaskCompleteListener, generateJson().toString()).execute(PATIENT_LIST_URL);
            } else {
                Toast.makeText(SurgeonScheduleActivity.this, CMN_ErrorMessages.getInstance().getValue(12), Toast.LENGTH_SHORT).show();
            }*/

        }

    }

    public JSONObject generateJson() {
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("ToDOSDate", "" + selectedTextVw.getText());
            jsonObject.put("FromDOSDate", "" + selectedTextVw.getText());
            jsonObject.put("SurgeonId", "" + allSurgeons.get(selectedSurgeonPos).getID());


        } catch (JSONException e) {
            e.getMessage();
        }

        return jsonObject;
    }

    private void parseResult(String result) {

       allPatients.clear();
        allPatientsName.clear();
        String message = null;
        try {
            JSONObject response;
            response = new JSONObject(CMN_AES.decryptData(result, CMN_Preferences.getUserToken(SurgeonScheduleActivity.this)));


            JSONObject jsonObjectResult=response.getJSONObject("Result");
            int code=jsonObjectResult.getInt("Code");
             message=jsonObjectResult.getString("Message");

            if(code==-1){
                showAlert(message,"Patient");
                pateintListAdapter.notifyDataSetChanged();
                return;
            }

            JSONArray jsonArray = response.getJSONArray("Data");
            Gson gson = new Gson();

            for (int i = 0; i < jsonArray.length(); i++) {

                SurgeonSchedulePateintModel myPateintModel = gson.fromJson(jsonArray.getJSONObject(i).toString(), SurgeonSchedulePateintModel.class);
                allPatients.add(myPateintModel);

            }


            for (int i = 0; i < allPatients.size(); i++) {


                if (allPatients.get(i).getName()!=null)
                allPatientsName.add(allPatients.get(i).getName());

            }


            if(!allPatientsName.isEmpty())
            pateintListAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to show alert dialog
     */
    private void showAlert(String message, final String camefrom) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(SurgeonScheduleActivity.this, android.R.style.Theme_Dialog));
        builder.setMessage(message).setTitle("Message")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        if(camefrom.equalsIgnoreCase("surgeonList")){
                            finish();
                        }

                        dialog.cancel();


                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

}



