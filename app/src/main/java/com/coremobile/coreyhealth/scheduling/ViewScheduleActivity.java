package com.coremobile.coreyhealth.scheduling;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.coremobile.coreyhealth.R;
import com.coremobile.coreyhealth.Utils;
import com.coremobile.coreyhealth.errorhandling.ActivityPackage;
import com.coremobile.coreyhealth.errorhandling.CMN_ErrorMessages;
import com.coremobile.coreyhealth.genericviewandsurvey.CMN_GenericViewActivity;
import com.coremobile.coreyhealth.googleAnalytics.CMN_Preferences;
import com.coremobile.coreyhealth.networkutils.JsonParsingException;
import com.coremobile.coreyhealth.networkutils.NetworkAsyncTask;
import com.coremobile.coreyhealth.networkutils.NetworkCallBack;
import com.coremobile.coreyhealth.networkutils.NetworkException;
import com.coremobile.coreyhealth.networkutils.NetworkTools;
import com.coremobile.coreyhealth.networkutils.ThreadTaskIds;
import com.coremobile.coreyhealth.scheduling.schedulingcalender.CalenderActivity;
import com.coremobile.coreyhealth.scheduling.schedulingmodels.BlockDetails;
import com.coremobile.coreyhealth.scheduling.schedulingmodels.RoomSchedules;
import com.coremobile.coreyhealth.scheduling.schedulingmodels.Schedules;
import com.coremobile.coreyhealth.scheduling.schedulingmodels.TableData;
import com.coremobile.coreyhealth.scheduling.schedulingmodels.TimeSlots;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ViewScheduleActivity extends Activity implements NetworkCallBack {

    ProgressDialog progressDialog;
    boolean mySchedule;
    int speciality = -1;
    int modality = -1;
    String date = "";
    Intent intent;
    DisplayMetrics displayMetrics;
    TableLayout table;
    HorizontalScrollView tableLayout;
    float density1;
    String action = "";
    TimeSlots timeSlots;
    RoomSchedules clickedroomSchedules;
    Schedules schedules;
    TableData tableData;
    boolean isMoveSelected;
    String startTimetoMoveSchedule;
    public static JSONObject jsonObject;
    TextView noData;
    ScrollView scheduleView;
    public static int timeSlotDifference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_schedule);
        ActivityPackage.setActivityList(this);

        intent = getIntent();

        displayMetrics = getResources().getDisplayMetrics();
        density1 = displayMetrics.density;

        if (intent.hasExtra("mySchedule")) {
            mySchedule = intent.getBooleanExtra("mySchedule", false);
        }
        noData = (TextView) findViewById(R.id.noData);
        speciality = Integer.parseInt(CalenderActivity.speciality);
        modality = Integer.parseInt(CalenderActivity.modality);
        scheduleView = (ScrollView) findViewById(R.id.layout);
        if (intent.hasExtra("modality")) {

        }
        if (intent.hasExtra("date")) {
            date = intent.getStringExtra("date");
        }


        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setTitle("View Schedule");
        getActionBar().setDisplayHomeAsUpEnabled(true);

        tableLayout = (HorizontalScrollView) findViewById(R.id.table_layout);

        table = (TableLayout) findViewById(R.id.table);

        progressDialog = new ProgressDialog(ViewScheduleActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);


        NetworkAsyncTask networkAsyncTask = NetworkAsyncTask.getInstance();
        try {
            networkAsyncTask.startNetworkCall(ThreadTaskIds.GET_SCHEDULE_DATA, ViewScheduleActivity.this);
        } catch (NetworkException e) {
            e.getMessage();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.schedulelist, menu);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;

            case R.id.action_schedulelist:
                Intent intent = new Intent(ViewScheduleActivity.this, ViewScheduleListActivity.class);
                intent.putExtra("SchType", "" + Utils.schType);
                intent.putExtra("date", "" + date);
                intent.putExtra("mySchedule", mySchedule);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void beforeNetworkCall(int taskId) {
        if (progressDialog != null && !progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    @Override
    public Object afterNetworkCall(int taskId, Object o) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        return null;
    }


    @Override
    public Object onNetworkCall(int taskId, Object o) throws
            JsonParsingException, JsonParsingException {
        if (taskId == ThreadTaskIds.GET_SCHEDULE_DATA) {
            String url = CMN_Preferences.getBaseUrl(ViewScheduleActivity.this)
                    + "GetSchedules.aspx?token=" + CMN_Preferences.getUserToken(ViewScheduleActivity.this);

            NetworkTools networkTools = NetworkTools.getInstance();
            if (networkTools.checkNetworkConnection(ViewScheduleActivity.this) && url.length() > 0) {
                JSONObject response = networkTools.postJsonData(ViewScheduleActivity.this, url, generateJson());
                try {
                    if (response != null) {
                        parseJsonData(response, ThreadTaskIds.GET_SCHEDULE_DATA);
                    }
                } catch (JSONException e) {
                    e.getMessage();
                }
            } else {
                Toast.makeText(ViewScheduleActivity.this, CMN_ErrorMessages.getInstance().getValue(12), Toast.LENGTH_SHORT).show();
            }
        } else if (taskId == ThreadTaskIds.UPDATE_SCHEDULE_DATA) {
            String url = CMN_Preferences.getBaseUrl(ViewScheduleActivity.this)
                    + "UpdateSchedule.aspx?token=" + CMN_Preferences.getUserToken(ViewScheduleActivity.this) + "&id=" + schedules.getScheduleId() + "&action=" + action;

            NetworkTools networkTools = NetworkTools.getInstance();
            if (networkTools.checkNetworkConnection(ViewScheduleActivity.this) && url.length() > 0) {
                JSONObject response;
                if (action.equalsIgnoreCase("move")) {
                    response = networkTools.postJsonData(ViewScheduleActivity.this, url, generateUpdatedJson());
                } else {
                    response = networkTools.getJsonData(ViewScheduleActivity.this, url);
                }
                try {
                    if (response != null) {
                        parseJsonData(response, ThreadTaskIds.UPDATE_SCHEDULE_DATA);
                    }
                } catch (JSONException e) {
                    e.getMessage();
                }
            } else {
                Toast.makeText(ViewScheduleActivity.this, CMN_ErrorMessages.getInstance().getValue(12), Toast.LENGTH_SHORT).show();
            }
        }


        return null;
    }

    private void parseJsonData(final JSONObject response, int id) throws JSONException {
        if (id == ThreadTaskIds.UPDATE_SCHEDULE_DATA) {
            if (response.getJSONObject("Result").getInt("Code") == 0) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ViewScheduleActivity.this, "Schedule updated successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Toast.makeText(ViewScheduleActivity.this, response.getJSONObject("Result").getString("Message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
        if (id == ThreadTaskIds.GET_SCHEDULE_DATA) {
            if (response.getJSONObject("Result").getInt("Code") == 0) {
                tableData = new TableData();
                final JSONObject data = response.getJSONObject("Data");
                JSONArray roomSchedules = data.getJSONArray("RoomSchedules");
                List<RoomSchedules> roomSchedulesList = new ArrayList<>();
                roomSchedulesList.add(new RoomSchedules());
                for (int i = 0; i < roomSchedules.length(); i++) {
                    JSONObject jsonObject = roomSchedules.getJSONObject(i);
                    RoomSchedules roomSchedulesModel = new RoomSchedules();
                    roomSchedulesModel.setName("" + jsonObject.optString("Name"));
                    roomSchedulesModel.setRoomId("" + jsonObject.optString("RoomId"));
                    roomSchedulesModel.setUnusedOR(jsonObject.optBoolean("IsUnusedOR"));

                    List<BlockDetails> detailsList = new ArrayList<>();
                    JSONArray blockList = jsonObject.getJSONArray("BlockDetails");
                    for (int j = 0; j < blockList.length(); j++) {
                        JSONObject jsonObject1 = blockList.getJSONObject(j);
                        BlockDetails blockDetails = new BlockDetails();
                        blockDetails.setColorCode("" + jsonObject1.optString("ColorCode"));
                        blockDetails.setEndTime("" + jsonObject1.optString("EndTime"));
                        blockDetails.setStartTime("" + jsonObject1.optString("StartTime"));
                        JSONArray blockedFor = jsonObject1.getJSONArray("BlockedFor");
                        HashMap<Integer, String> hashMap = new HashMap<>();
                        for (int k = 0; k < blockedFor.length(); k++) {
                            JSONObject jsonObject2 = blockedFor.getJSONObject(k);
                            hashMap.put(jsonObject2.optInt("Key"), jsonObject2.optString("Value"));
                        }
                        blockDetails.setBlockedFor(hashMap);
                        detailsList.add(blockDetails);
                    }
                    roomSchedulesModel.setBlockDetailsList(detailsList);

                    List<Schedules> schedulesList = new ArrayList<>();
                    JSONArray schedules = jsonObject.getJSONArray("Schedules");
                    for (int j = 0; j < schedules.length(); j++) {
                        JSONObject jsonObject1 = schedules.getJSONObject(j);
                        Schedules schedulesModel = new Schedules();
                        schedulesModel.setColorCode("" + jsonObject1.optString("ColorCode"));
                        schedulesModel.setActualEnd("" + jsonObject1.optString("ActualStart"));
                        schedulesModel.setActualStart("" + jsonObject1.optString("ActualEnd"));
                        schedulesModel.setAnesthesiologist("" + jsonObject1.optString("Anesthesiologist"));
                        schedulesModel.setExam("" + jsonObject1.optString("Exam"));
                        schedulesModel.setIsAvailable("" + jsonObject1.optString("IsAvailable"));
                        schedulesModel.setIsCancelled("" + jsonObject1.optString("IsCancelled"));
                        schedulesModel.setIsConfirmed("" + jsonObject1.optString("IsConfirmed"));
                        schedulesModel.setIsNotUsable("" + jsonObject1.optString("IsNotUsable"));
                        schedulesModel.setIsRequested("" + jsonObject1.optString("IsRequested"));
                        schedulesModel.setModality("" + jsonObject1.optString("Modality"));
                        schedulesModel.setORId("" + jsonObject1.optString("ORId"));
                        schedulesModel.setPatAnonId("" + jsonObject1.optString("PatAnonId"));
                        schedulesModel.setPatAnonName("" + jsonObject1.optString("PatAnonName"));
                        schedulesModel.setPatientId("" + jsonObject1.optString("PatientId"));
                        schedulesModel.setPatName("" + jsonObject1.optString("PatName"));
                        schedulesModel.setProcedure("" + jsonObject1.optString("Procedure"));
                        schedulesModel.setProcRoomId("" + jsonObject1.optString("ProcRoomId"));
                        schedulesModel.setScheduledEnd("" + jsonObject1.optString("ScheduledEnd"));
                        schedulesModel.setScheduledStart("" + jsonObject1.optString("ScheduledStart"));
                        schedulesModel.setScheduleId("" + jsonObject1.optString("ScheduleId"));
                        schedulesModel.setSpecialty("" + jsonObject1.optString("Specialty"));
                        schedulesModel.setSurgeon("" + jsonObject1.optString("Surgeon"));
                        schedulesList.add(schedulesModel);
                    }
                    roomSchedulesModel.setSchedules(schedulesList);
                    roomSchedulesList.add(roomSchedulesModel);
                }
                ArrayList<TimeSlots> slotsArrayList = new ArrayList<>();
                slotsArrayList.add(new TimeSlots());
                JSONArray timeSlotArray = data.getJSONArray("TimeSlots");
                for (int i = 0; i < timeSlotArray.length(); i++) {
                    JSONObject jsonObject = timeSlotArray.getJSONObject(i);
                    TimeSlots timeSlotsModel = new TimeSlots();
                    timeSlotsModel.setEndTime("" + jsonObject.optString("EndTime"));
                    timeSlotsModel.setStartTime("" + jsonObject.optString("StartTime"));
                    timeSlotsModel.setStartDateToShow(Utils.converttimeutc2local(date + " " + jsonObject.optString("StartTime")));
                    timeSlotsModel.setEndDateToShow(Utils.converttimeutc2local(date + " " + jsonObject.optString("EndTime")));
                    timeSlotsModel.setId(jsonObject.optInt("Id"));
                    slotsArrayList.add(timeSlotsModel);
                }
                tableData.setRoomSchedules(roomSchedulesList);
                tableData.setTimeSlotsList(slotsArrayList);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (tableData.getTimeSlotsList().size() == 0) {
                            noData.setVisibility(View.VISIBLE);
                            scheduleView.setVisibility(View.GONE);
                            return;
                        } else {
                            noData.setVisibility(View.GONE);
                            scheduleView.setVisibility(View.VISIBLE);
                        }

                        for (int i = 0; i < tableData.getTimeSlotsList().size(); i++) {
                            TableRow row = new TableRow(ViewScheduleActivity.this);
                            row.setLayoutParams(new ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT, (int) density1 * 50));
                            for (int j = 0; j < tableData.getRoomSchedules().size(); j++) {
                                final TextView textView = new TextView(ViewScheduleActivity.this);
                                TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                                        (int) density1 * 60, (int) density1 * 50);
                                layoutParams.setMargins(2, 2, 2, 2);
                                textView.setLayoutParams(layoutParams);
                                textView.setGravity(Gravity.CENTER);

                                if (i == 0 && j == 0) {
                                    textView.setText("Time");
                                    textView.setBackgroundColor(Color.parseColor("#012085"));
                                    textView.setTextColor(Color.parseColor("#ffffff"));
                                } else if (j == 0) {
                                    textView.setText("" + tableData.getTimeSlotsList().get(i).getStartDateToShow().split(" ")[1]);
                                    textView.setBackgroundColor(Color.parseColor("#012085"));
                                    textView.setTextColor(Color.parseColor("#ffffff"));
                                } else if (i == 0) {
                                    textView.setText("" + tableData.getRoomSchedules().get(j).getName());
                                    textView.setBackgroundColor(Color.parseColor("#012085"));
                                    textView.setTextColor(Color.parseColor("#ffffff"));
                                } else {

                                    SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.US);
                                    List<Schedules> schedulesList = tableData.getRoomSchedules().get(j).getSchedules();
                                    textView.setTag("" + i + "$" + j);
                                    if (schedulesList == null)
                                        return;
                                    for (int k = 0; k < schedulesList.size(); k++) {
                                        Schedules schedules = schedulesList.get(k);
                                        try {
                                            Date SchedulesStartdate = dateFormatter.parse(schedules.getScheduledStart());
                                            Date SchedulesEnddate = dateFormatter.parse(schedules.getScheduledEnd());
                                            Date timeslotStartdate = dateFormatter.parse(date + " " + tableData.getTimeSlotsList().get(i).getStartTime());
                                            if (timeslotStartdate.getTime() >= SchedulesStartdate.getTime() && timeslotStartdate.getTime() <= SchedulesEnddate.getTime()) {
                                                textView.setTag("" + i + "$" + j + "#" + k);
                                                if (schedules.getPatName() == null || schedules.getPatName().equalsIgnoreCase("null")) {
                                                    textView.setText("");
                                                } else {
                                                    textView.setText(schedules.getPatName());
                                                }
                                                textView.setBackgroundColor(Color.parseColor(schedules.getColorCode()));

                                            }
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                    List<BlockDetails> blockDetailsList = tableData.getRoomSchedules().get(j).getBlockDetailsList();
                                    if (blockDetailsList == null)
                                        return;
                                    if (blockDetailsList.size() > 0) {
                                        BlockDetails blockDetails = tableData.getRoomSchedules().get(j).getBlockDetailsList().get(0);
//
                                        Date timeslotStartdate = null,timeslotEnddate = null, scheduleStartdate = null, scheduleEnddate = null;
                                        try {
                                            timeslotStartdate = dateFormatter.parse(date + " " + tableData.getTimeSlotsList().get(i).getStartTime());
                                            timeslotEnddate = dateFormatter.parse(date + " " + tableData.getTimeSlotsList().get(i).getEndTime());

                                            long milliSeconds1 = timeslotStartdate.getTime();
                                            long milliSeconds2 = timeslotEnddate.getTime();
                                            long periodSeconds = (milliSeconds2 - milliSeconds1) / 1000;
                                           timeSlotDifference  = (int) (periodSeconds / 60);

                                            scheduleStartdate = dateFormatter.parse(blockDetails.getStartTime());
                                            scheduleEnddate = dateFormatter.parse(blockDetails.getEndTime());
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }

                                        if (timeslotStartdate.getTime() >= scheduleStartdate.getTime() && timeslotStartdate.getTime() <= scheduleEnddate.getTime()) {
                                            textView.setBackgroundColor(Color.parseColor(tableData.getRoomSchedules().get(j).getBlockDetailsList().get(0).getColorCode()));
                                            textView.setTextColor(Color.parseColor("#ffffff"));
                                            if (tableData.getRoomSchedules().get(j).getBlockDetailsList().get(0).getBlockedFor() != null) {
                                                Iterator it = tableData.getRoomSchedules().get(j).getBlockDetailsList().get(0).getBlockedFor().entrySet().iterator();
                                                while (it.hasNext()) {
                                                    Map.Entry pair = (Map.Entry) it.next();
                                                    System.out.println(pair.getKey() + " = " + pair.getValue());
                                                    textView.setText(textView.getText() + " " + pair.getValue());
                                                    it.remove(); // avoids a ConcurrentModificationException
                                                }
                                            }
                                        }


                                    }
                                    textView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            if (isMoveSelected) {
                                                isMoveSelected = false;
                                                final String tag = (String) view.getTag();
                                                AlertDialog.Builder builder1 = new AlertDialog.Builder(ViewScheduleActivity.this);
                                                builder1.setTitle("Schedule");
                                                builder1.setMessage("What you want to do");
                                                builder1.setCancelable(true);

                                                builder1.setPositiveButton(
                                                        "Move here",
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int id) {
                                                                int i = Integer.parseInt(tag.substring(0, tag.indexOf("$")));
                                                                int j = Integer.parseInt(tag.substring((tag.indexOf("$") + 1), tag.indexOf("#")));
                                                                int k = Integer.parseInt(tag.split("#")[1]);
                                                                startTimetoMoveSchedule = date + " " + tableData.getTimeSlotsList().get(i).getStartTime() + ":00";
                                                                clickedroomSchedules = tableData.getRoomSchedules().get(j);
                                                                // schedules = clickedroomSchedules.getSchedules().get(k);
                                                                updateSchedule("Move");
                                                                dialog.cancel();

                                                            }
                                                        });
                                                builder1.setNegativeButton(
                                                        "Schedule",
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int id) {
                                                                dialog.cancel();
                                                                Toast.makeText(ViewScheduleActivity.this, "Under development", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                AlertDialog alert11 = builder1.create();
                                                alert11.show();


                                            } else {
                                                String tag = (String) view.getTag();
                                                int i = Integer.parseInt(tag.substring(0, tag.indexOf("$")));
                                                int j = Integer.parseInt(tag.substring((tag.indexOf("$") + 1), tag.indexOf("#")));
                                                int k = Integer.parseInt(tag.split("#")[1]);
                                                showSchedulingPopup(ViewScheduleActivity.this, i, j, k);
                                            }
                                        }
                                    });
                                }

                                row.addView(textView);
                            }
                            table.addView(row);
                        }
                    }
                });
            }

        }

    }

    @Override
    public Object onNetworkError(int taskId, NetworkException o) {
        return null;
    }

    public JSONObject generateJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Modality", "" + modality);
            jsonObject.put("Specialty", "" + speciality);
            jsonObject.put("SchType", "" + Utils.schType);
            jsonObject.put("Date", "" + date);
            jsonObject.put("MyScheduleOnly", mySchedule);

        } catch (JSONException e) {
            e.getMessage();
        }

        return jsonObject;
    }

    public JSONObject generateUpdatedJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("StartDate", "" + startTimetoMoveSchedule);
            jsonObject.put("RoomId", "" + clickedroomSchedules.getRoomId());
            jsonObject.put("SchType", "" + Utils.schType);

        } catch (JSONException e) {
            e.getMessage();
        }

        return jsonObject;
    }

    public void showSchedulingPopup(final Context context, int timeslotindex, int roomscheduleindex, int scheduleindex) {

        timeSlots = tableData.getTimeSlotsList().get(timeslotindex);
        clickedroomSchedules = tableData.getRoomSchedules().get(roomscheduleindex);
        schedules = clickedroomSchedules.getSchedules().get(scheduleindex);

        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.US);

        final String SelectedStartTime = timeSlots.getStartDateToShow() + ":00";
        final String SelectedEndTime = timeSlots.getEndDateToShow() + ":00";


        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        View popupView = layoutInflater.inflate(R.layout.popup, null);

        final PopupWindow popup = new PopupWindow(popupView,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT,
                true);

        if (Boolean.parseBoolean(schedules.getIsNotUsable())) {
            return;
        } else if (Boolean.parseBoolean(schedules.getIsAvailable())) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(ViewScheduleActivity.this);
            builder1.setTitle("Schedule");
            builder1.setMessage("Do you want to request a schedule");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            jsonObject = new JSONObject();
                            try {
                                jsonObject.put("SchType", Utils.schType);
                                jsonObject.put("ScheduleId", schedules.getScheduleId());
                                jsonObject.put("OR", schedules.getORId());
                                jsonObject.put("Modality", modality);
                                jsonObject.put("Specialty", speciality);
                                jsonObject.put("StartTime", SelectedStartTime);
                                jsonObject.put("ProcRoomId", schedules.getProcRoomId());
                                jsonObject.put("EndTime", SelectedEndTime);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if (Utils.viewSchedule && !Utils.fromIntake) {
                                Intent intent = new Intent(ViewScheduleActivity.this, RequestScheduleActivity.class);
                                intent.putExtra("scheduling", "" + jsonObject);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(ViewScheduleActivity.this, CMN_GenericViewActivity.class);
                                intent.putExtra("scheduling", "" + jsonObject);

                                startActivity(intent);
                                dialog.cancel();
                            }
                        }
                    });
            builder1.setNegativeButton(
                    "No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert11 = builder1.create();
            alert11.show();

        } else {
            popup.showAtLocation(popupView, Gravity.CENTER, 0, 0);
        }
        Button move = (Button) popupView.findViewById(R.id.buttonMoveSchedule);
        Button view = (Button) popupView.findViewById(R.id.buttonViewSchedule);
        Button confirm = (Button) popupView.findViewById(R.id.buttonConfirmSchedule);
        confirm.setVisibility(View.GONE);
        Button edit = (Button) popupView.findViewById(R.id.buttonEditSchedule);
        Button delete = (Button) popupView.findViewById(R.id.buttonDeleteSchedule);
        Button close = (Button) popupView.findViewById(R.id.buttonCancel);
        close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                popup.dismiss();
            }
        });

        move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isMoveSelected = true;
                popup.dismiss();
                AlertDialog.Builder builder1 = new AlertDialog.Builder(ViewScheduleActivity.this);
                builder1.setTitle("Schedule");
                builder1.setMessage("Schedule copied");
                builder1.setCancelable(true);

                builder1.setNeutralButton(
                        "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jsonObject = new JSONObject();
                try {
                    jsonObject.put("SchType", Utils.schType);
                    jsonObject.put("ScheduleId", Integer.parseInt(schedules.getScheduleId()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(ViewScheduleActivity.this, CMN_GenericViewActivity.class);
                intent.putExtra("scheduling", "" + jsonObject);
                intent.putExtra("isViewing",true);
                startActivity(intent);
                popup.dismiss();

            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSchedule("Confirm");
                popup.dismiss();
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jsonObject = new JSONObject();
                try {
                    jsonObject.put("SchType", Utils.schType);
                    jsonObject.put("ScheduleId", Integer.parseInt(schedules.getScheduleId()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                Intent intent = new Intent(ViewScheduleActivity.this, CMN_GenericViewActivity.class);
                intent.putExtra("scheduling", "" + jsonObject);
                intent.putExtra("petientId", schedules.getPatientId());
                startActivity(intent);
                popup.dismiss();
//                Toast.makeText(ViewScheduleActivity.this, "Under development", Toast.LENGTH_SHORT).show();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSchedule("delete");
                popup.dismiss();
            }
        });
    }

    private void updateSchedule(String act) {
        action = act;
        NetworkAsyncTask networkAsyncTask = NetworkAsyncTask.getInstance();
        try {
            networkAsyncTask.startNetworkCall(ThreadTaskIds.UPDATE_SCHEDULE_DATA, ViewScheduleActivity.this);
        } catch (NetworkException e) {
            e.getMessage();
        }
    }
}

