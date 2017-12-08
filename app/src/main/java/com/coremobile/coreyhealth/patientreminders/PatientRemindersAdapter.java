package com.coremobile.coreyhealth.patientreminders;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.coremobile.coreyhealth.AppConfig;
import com.coremobile.coreyhealth.R;
import com.coremobile.coreyhealth.errorhandling.CMN_ErrorMessages;
import com.coremobile.coreyhealth.googleAnalytics.CMN_Preferences;
import com.coremobile.coreyhealth.networkutils.JsonParsingException;
import com.coremobile.coreyhealth.networkutils.NetworkAsyncTask;
import com.coremobile.coreyhealth.networkutils.NetworkCallBack;
import com.coremobile.coreyhealth.networkutils.NetworkException;
import com.coremobile.coreyhealth.networkutils.NetworkTools;
import com.coremobile.coreyhealth.networkutils.ThreadTaskIds;
import com.coremobile.coreyhealth.newui.MessageTabActivityCMN;
import com.coremobile.coreyhealth.providerreminders.AddNewReminderActivityCMN;
import com.coremobile.coreyhealth.providerreminders.ProviderRemindersActivityCMN;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

/**
 * @author Nitij Katiyar
 */
public class PatientRemindersAdapter extends BaseAdapter implements NetworkCallBack {
    LayoutInflater inflater;
    List<ReminderModel> reminders;
    Activity activity;
    String typeId;
    private SharedPreferences mCurrentUserPref;
    public static final String CURRENT_USER = "CurrentUser";
    private String organizationName;
    String context, user_category, context_name;

    private boolean isEdit;
    private boolean isUseForNewReminder;
    HashMap<Integer, Boolean> isSelected;
    ProgressDialog progressDialog;
    String reminderToDelete = "";
    boolean providerReminder;

    public PatientRemindersAdapter(Activity activity,
                                   List<ReminderModel> reminders, String typeId, boolean isEdit, boolean isUseForNewReminder, boolean providerReminder) {
        isSelected = new HashMap<Integer, Boolean>();
        this.reminders = reminders;
        this.activity = activity;
        this.typeId = typeId;
        this.providerReminder = providerReminder;
        this.inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mCurrentUserPref = activity.getSharedPreferences(CURRENT_USER, 0);
        organizationName = mCurrentUserPref.getString("Organization", null);
        user_category = mCurrentUserPref.getString("user_category", null);
        context = mCurrentUserPref.getString("context", null);
        context_name = mCurrentUserPref.getString("context_name", null);

        this.isEdit = isEdit;
        this.isUseForNewReminder = isUseForNewReminder;
    }

    @Override
    public int getCount() {
        return reminders.size();
    }

    @Override
    public ReminderModel getItem(int arg0) {
        return reminders.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final ReminderModel reminderModel = reminders.get(position);
        if (view == null) {
            view = inflater.inflate(R.layout.reminders_listitem, null);
        }

        TextView name, desc, col1, col1data, col2, col2data;
        final CheckBox checkBox;
        ImageView infoArrow;
        Button instantButtonVw;
        instantButtonVw = (Button) view.findViewById(R.id.intsantButtonReminders);
        if (AppConfig.isAppCoreyHealth && !isUseForNewReminder) {
            instantButtonVw.setVisibility(View.VISIBLE);
        } else {
            instantButtonVw.setVisibility(View.GONE);
        }
        name = (TextView) view.findViewById(R.id.reminderName);
        desc = (TextView) view.findViewById(R.id.reminderDesc);
        col1 = (TextView) view.findViewById(R.id.reminderCol1);
        col1data = (TextView) view.findViewById(R.id.reminderCol1Data);
        col2 = (TextView) view.findViewById(R.id.reminderCol2);
        col2data = (TextView) view.findViewById(R.id.reminderCol2Data);
        infoArrow = (ImageView) view.findViewById(R.id.infoIcon);

        name.setText(reminderModel.getTitleData());
        desc.setText(reminderModel.getMsgToPatientData());
        col1.setText(reminderModel.getFrequanyColHeading() + ":");
        col1data.setText(reminderModel.getFrequencyData());
        col2.setText(reminderModel.getTimesColHeading() + ":");
        col2data.setText(reminderModel.getTimesData());

        checkBox = (CheckBox) view.findViewById(R.id.checkBox1);

        if (isUseForNewReminder) {
            checkBox.setVisibility(View.VISIBLE);
            infoArrow.setVisibility(View.GONE);
        } else {
            checkBox.setVisibility(View.GONE);
            infoArrow.setVisibility(View.VISIBLE);
        }

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                isSelected.put(position, isChecked);
                checkBox.setChecked(isChecked);

            }
        });

        view.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isEdit) {
                    if (reminderModel.getRemindertypeData().equalsIgnoreCase("-1")) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(activity);
                        builder1.setTitle("Reminders");
                        builder1.setMessage("Auto applied reminders can not be edited");
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
                    } else {
                        Intent intent = new Intent(activity, AddNewReminderActivityCMN.class);
                        intent.putExtra("data", reminderModel);
                        activity.startActivity(intent);
                    }
                } else if (isUseForNewReminder) {
                    if (checkBox.isChecked()) {
                        checkBox.setChecked(false);
                    } else {
                        checkBox.setChecked(true);
                    }
                    isSelected.put(position, checkBox.isChecked());
                } else {
                    showDialogue(reminderModel);
                }
            }
        });
        instantButtonVw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ProviderRemindersActivityCMN.invokeActivityMethod(reminderModel);
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (isEdit) {
                    if (reminderModel.getRemindertypeData().equalsIgnoreCase("-1")) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(activity);
                        builder1.setTitle("Reminders");
                        builder1.setMessage("Auto applied reminders can not be deleted");
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
                    } else {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(activity);
                        builder1.setTitle("Reminders");
                        builder1.setMessage("Are you sure you want delete ?");
                        builder1.setCancelable(true);

                        builder1.setPositiveButton(
                                "YES",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        reminderToDelete = reminderModel.getId();

                                        NetworkAsyncTask networkAsyncTask = NetworkAsyncTask.getInstance();
                                        try {
                                            networkAsyncTask.startNetworkCall(ThreadTaskIds.DELETE_REMINDER, PatientRemindersAdapter.this);
                                        } catch (NetworkException e) {
                                            e.getMessage();
                                        }
                                        dialog.cancel();
                                    }
                                });
                        builder1.setNegativeButton(
                                "NO",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        reminderToDelete = "";
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alert11 = builder1.create();
                        alert11.show();
                    }
                }
                return false;
            }
        });

        if (isSelected.get(position) == null
                || isSelected.get(position) == false) {
            checkBox.setChecked(false);
        } else {
            checkBox.setChecked(true);
        }

        return view;
    }

    private void showDialogue(final ReminderModel reminderModel) {
        final View dialogView = View.inflate(activity, R.layout.date_time_picker, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(activity).create();

        dialogView.findViewById(R.id.date_time_set).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);
                TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.time_picker);

                Calendar calendar = new GregorianCalendar(datePicker.getYear(),
                        datePicker.getMonth(),
                        datePicker.getDayOfMonth(),
                        timePicker.getCurrentHour(),
                        timePicker.getCurrentMinute());

                long time = calendar.getTimeInMillis();

                SetPatientComplianceWebService setPatientComplianceWebService = new SetPatientComplianceWebService(activity);
                SimpleDateFormat sdf = new SimpleDateFormat();
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                long utcdate = new Date(time).getTime();
                setPatientComplianceWebService.execute(context, "" + reminderModel.getId(), "" + convertDate("" + utcdate, "yyyy-MM-dd hh:mm:ss"));
                alertDialog.dismiss();
            }
        });
        dialogView.findViewById(R.id.date_time_cancel).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog.dismiss();
            }
        });
        alertDialog.setView(dialogView);
        alertDialog.show();

    }

    public static String convertDate(String dateInMilliseconds, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        return DateFormat.format(dateFormat, Long.parseLong(dateInMilliseconds)).toString();
    }

    public ArrayList<ReminderModel> getData() {
        ArrayList<ReminderModel> data = new ArrayList<ReminderModel>();
        for (int i = 0; i < getCount(); i++) {
            if (isSelected.get(i) != null && isSelected.get(i) == true) {
                data.add(reminders.get(i));
            }
        }

        return data;
    }

    @Override
    public void beforeNetworkCall(int taskId) {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

    }

    @Override
    public Object afterNetworkCall(int taskId, Object o) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        return null;
    }


    @Override
    public Object onNetworkCall(int taskId, Object o) throws JsonParsingException, JsonParsingException {
        if (taskId == ThreadTaskIds.DELETE_REMINDER) {
            if (reminderToDelete.equalsIgnoreCase("") || reminderToDelete.isEmpty()) {
                return null;
            }
            String url = MessageTabActivityCMN.CMN_SERVER_BASE_URL_DEFINE
                    + "MarkReminderAsDeleted.aspx?token=" + CMN_Preferences.getUserToken(activity) + "&isStandard=false&remId=" + reminderToDelete;


            NetworkTools networkTools = NetworkTools.getInstance();
            if (networkTools.checkNetworkConnection(activity) && url.length() > 0) {
                final JSONObject response = networkTools.getJsonData(activity, url);

                if (response != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                parseJsonData(response, ThreadTaskIds.DELETE_REMINDER);
                            } catch (JSONException e) {
                                e.getMessage();
                            }
                        }
                    });
                }

            } else {
                Toast.makeText(activity, CMN_ErrorMessages.getInstance().getValue(12), Toast.LENGTH_SHORT).show();
            }
        }
        return null;
    }

    private void parseJsonData(JSONObject response, int deleteReminder) throws JSONException {
        if (response.getString("retCode").equalsIgnoreCase("0")) {
            removeObject(reminderToDelete);
            AlertDialog.Builder builder1 = new AlertDialog.Builder(activity);
            builder1.setTitle("Reminder");
            builder1.setMessage(response.getString("message"));
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
        } else {
            Toast.makeText(activity, "Some error occured. Please try again later", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public Object onNetworkError(int taskId, NetworkException o) {
        return null;
    }

    public void removeObject(String idtodelete) {
        for (int i = 0; i < reminders.size(); i++) {
            if (reminders.get(i).getId().equalsIgnoreCase(idtodelete)) {
                reminders.remove(i);
            }
        }

        notifyDataSetChanged();
    }
}
