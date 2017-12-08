package com.coremobile.coreyhealth.surgeonschedule;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.coremobile.coreyhealth.R;
import com.coremobile.coreyhealth.Utils;
import com.coremobile.coreyhealth.errorhandling.CMN_ErrorMessages;
import com.coremobile.coreyhealth.googleAnalytics.CMN_Preferences;
import com.coremobile.coreyhealth.networkutils.JsonParsingException;
import com.coremobile.coreyhealth.networkutils.NetworkAsyncTask;
import com.coremobile.coreyhealth.networkutils.NetworkCallBack;
import com.coremobile.coreyhealth.networkutils.NetworkException;
import com.coremobile.coreyhealth.networkutils.NetworkTools;
import com.coremobile.coreyhealth.networkutils.ThreadTaskIds;
import com.coremobile.coreyhealth.scheduling.ViewScheduleActivity;
import com.coremobile.coreyhealth.scheduling.schedulingcalender.CalenderModel;
import com.coremobile.coreyhealth.scheduling.schedulingcalender.MaterialCalendarAdapter;
import com.coremobile.coreyhealth.scheduling.schedulingcalender.SavedEventsAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Maximilian on 9/1/14.
 */
public class MaterialCalendarFragmentS extends Fragment implements View.OnClickListener, GridView.OnItemClickListener, NetworkCallBack {
    // Variables
    //Views
    ImageView mPrevious;
    ImageView mNext;
    TextView mMonthName;
    GridView mCalendar;
    ArrayList<CalenderModel> calendarModels;
    ProgressDialog progressDialog;
    public static MaterialCalendarFragmentS calendarFragment;

    // Calendar Adapter
    private MaterialCalendarAdapter mMaterialCalendarAdapter;

    // Saved Events Adapter
    protected static SavedEventsAdapter mSavedEventsAdapter;
    protected static ListView mSavedEventsListView;

    protected static ArrayList<HashMap<String, Integer>> mSavedEventsPerDay;
    protected static ArrayList<Integer> mSavedEventDays;

    protected static int mNumEventsOnDay = 0;
    static int months;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_calendar, container, false);
        calendarFragment = this;
        if (rootView != null) {
            // Get Calendar info
            // Get Calendar info
            MaterialCalendarS.getInitialCalendarInfo();

            months = MaterialCalendarS.mCurrentMonth;
            // Previous ImageView
            mPrevious = (ImageView) rootView.findViewById(R.id.material_calendar_previous);
            if (mPrevious != null) {
                mPrevious.setOnClickListener(this);
            }

            // Month name TextView
            mMonthName = (TextView) rootView.findViewById(R.id.material_calendar_month_name);
            if (mMonthName != null) {
                Calendar cal = Calendar.getInstance();
                if (cal != null) {
                    mMonthName.setText(cal.getDisplayName(Calendar.MONTH, Calendar.LONG,
                            Locale.getDefault()) + " " + cal.get(Calendar.YEAR));
                }
            }

            // Next ImageView
            mNext = (ImageView) rootView.findViewById(R.id.material_calendar_next);
            if (mNext != null) {
                mNext.setOnClickListener(this);
            }

            // GridView for custom Calendar
            mCalendar = (GridView) rootView.findViewById(R.id.material_calendar_gridView);
            if (mCalendar != null) {
                mCalendar.setOnItemClickListener(this);
            }

            // ListView for saved events in calendar
            mSavedEventsListView = (ListView) rootView.findViewById(R.id.saved_events_listView);
        }
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        NetworkAsyncTask networkAsyncTask = NetworkAsyncTask.getInstance();
        try {
            networkAsyncTask.startNetworkCall(ThreadTaskIds.GET_CALENDAR_DATA, MaterialCalendarFragmentS.this);
        } catch (NetworkException e) {
            e.getMessage();
        }


        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (mSavedEventsListView != null) {
            mSavedEventsAdapter = new SavedEventsAdapter(getActivity());
            mSavedEventsListView.setAdapter(mSavedEventsAdapter);
            mSavedEventsListView.setOnItemClickListener(this);
            Log.d("EVENTS_ADAPTER", "set adapter");


            // Show current day saved events on load
//            int today = MaterialCalendarS.mCurrentDay + 6 + MaterialCalendarS.mFirstDay;
//            showSavedEventsListView(today);
        }
    }

    @Override
    public void onClick(View view) {
        if (view != null) {
            switch (view.getId()) {
                case R.id.material_calendar_previous:
                    MaterialCalendarS.previousOnClick(mPrevious, mMonthName, mCalendar, mMaterialCalendarAdapter);
                    break;

                case R.id.material_calendar_next:
                    MaterialCalendarS.nextOnClick(mNext, mMonthName, mCalendar, mMaterialCalendarAdapter);
                    break;

                default:
                    break;
            }
        }
    }

    public static void refreshData(int month) {
        months = month;
        NetworkAsyncTask networkAsyncTask = NetworkAsyncTask.getInstance();
        try {
            networkAsyncTask.startNetworkCall(ThreadTaskIds.GET_CALENDAR_DATA, calendarFragment);
        } catch (NetworkException e) {
            e.getMessage();
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.material_calendar_gridView:
                String selectedDate = MaterialCalendarS.selectCalendarDay(mMaterialCalendarAdapter, position);
              //  showSchedulingPopup(selectedDate);
                Toast.makeText(getActivity(),selectedDate,Toast.LENGTH_SHORT).show();
                break;

            default:
                break;
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
        if (taskId == ThreadTaskIds.GET_CALENDAR_DATA) {
            String url = CMN_Preferences.getBaseUrl(getActivity())
                    + "GetSchedulingCalendar.aspx?token=" + CMN_Preferences.getUserToken(getActivity());

            NetworkTools networkTools = NetworkTools.getInstance();
            if (networkTools.checkNetworkConnection(getActivity()) && url.length() > 0) {
                JSONObject response = networkTools.postJsonData(getActivity(), url, generateJson());
                try {
                    if (response != null) {
                        parseJsonData(response, ThreadTaskIds.GET_CALENDAR_DATA);
                    }
                } catch (JSONException e) {
                    e.getMessage();
                }
            } else {
                Toast.makeText(getActivity(), CMN_ErrorMessages.getInstance().getValue(12), Toast.LENGTH_SHORT).show();
            }
        }


        return null;
    }

    private JSONObject generateJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Month", "" + (months + 1));
            jsonObject.put("Year", "" + MaterialCalendarS.mCurrentYear);
            jsonObject.put("SchType", "ORScheduler");
        } catch (JSONException e) {
            e.getMessage();
        }
        return jsonObject;
    }

    private void parseJsonData(final JSONObject response, int id) throws JSONException {
        if (id == ThreadTaskIds.GET_CALENDAR_DATA) {
            if (response.getJSONObject("Result").getInt("Code") == 0) {
                JSONObject data = response.getJSONObject("Data");
                JSONArray messagesArray = data.getJSONArray("Schedules");
                JSONObject colorcodes = data.getJSONObject("ColorCodes");
                calendarModels = new ArrayList<>();
                for (int i = 0; i < messagesArray.length(); i++) {
                    JSONObject jsonObject = messagesArray.getJSONObject(i);
                    CalenderModel lookupModel = new CalenderModel();
                    lookupModel.setDate(jsonObject.getString("Date"));
                    lookupModel.setStatus(jsonObject.getString("Status"));
                    lookupModel.setAllAvailable(colorcodes.optString("AllAvailable"));
                    lookupModel.setFewAvailable(colorcodes.optString("FewAvailable"));
                    lookupModel.setNoneAvailable(colorcodes.optString("NoneAvailable"));
                    lookupModel.setUnknown(colorcodes.optString("Unknown"));
                    calendarModels.add(lookupModel);
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        mMaterialCalendarAdapter = new MaterialCalendarAdapter(getActivity(), calendarModels);
                        mCalendar.setAdapter(mMaterialCalendarAdapter);


                        // Set current day to be auto selected when first opened
                        if (MaterialCalendarS.mCurrentDay != -1 && MaterialCalendarS.mFirstDay != -1) {
                            int startingPosition = 6 + MaterialCalendarS.mFirstDay;
                            int currentDayPosition = startingPosition + MaterialCalendarS.mCurrentDay;

                            Log.d("INITIAL_SELECTED_POSITION", String.valueOf(currentDayPosition));
                            mCalendar.setItemChecked(currentDayPosition, true);

                            if (mMaterialCalendarAdapter != null) {
                                mMaterialCalendarAdapter.notifyDataSetChanged();
                            }
                        }

                    }
                });

            } else {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                getActivity());
                        builder.setTitle("Schedule");
                        try {
                            builder.setMessage(response.getJSONObject("Result").getString("Message"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                try {
                                    dialog.dismiss();
                                    getActivity().finish();
                                } catch (Exception e) {

                                }
                            }
                        });
                        builder.show();

                    }
                });
                }
        }

    }

    @Override
    public Object onNetworkError(int taskId, NetworkException o) {
        return null;
    }

    private void showSchedulingPopup(final String selectedDate) {

        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);

        View popupView = layoutInflater.inflate(R.layout.scheduling_popup_window, null);

        final PopupWindow popup = new PopupWindow(popupView,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT,
                true);


        popup.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        Button viewMySchedule = (Button) popupView.findViewById(R.id.buttonSurgeryScheduling);
        viewMySchedule.setText("View My Schedule");
        viewMySchedule.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ViewScheduleActivity.class).putExtra("date", selectedDate).putExtra("mySchedule", true));
                popup.dismiss();
            }
        });


        Button viewSurgerySchedule = (Button) popupView.findViewById(R.id.buttonSpecialityScheduling);

        if (Utils.schType.equalsIgnoreCase("ProcRoomScheduler")) {
            viewSurgerySchedule.setText("View Speciality Schedule");
        } else if (Utils.schType.equalsIgnoreCase("ModalityScheduler")) {
            viewSurgerySchedule.setText("View Modality/Sub-speciality Schedule");
        } else {
            viewSurgerySchedule.setText("View Surgery Schedule");
        }

        viewSurgerySchedule.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(getActivity(), ViewScheduleActivity.class);
                intent.putExtra("date", selectedDate);
                intent.putExtra("mySchedule", false);
                startActivity(intent);
                popup.dismiss();
            }
        });

        Button unused = (Button) popupView.findViewById(R.id.buttonModalityScheduling);
        unused.setVisibility(View.GONE);

        Button close = (Button) popupView.findViewById(R.id.buttonCancel);
        close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                popup.dismiss();
            }
        });
    }

}


