package com.coremobile.coreyhealth;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class XmlHandlerAnalytics extends DefaultHandler {
    private static final String TAG = "Corey_XmlHandlerAnalytics";
    private Context mContext;
    SharedPreferences mcurrentUserPref;
    public static final String CURRENT_USER = "CurrentUser";
    SharedPreferences.Editor editor;
    private String SignedUsr;
    private boolean in_messages = false;
    private boolean in_field = false;
    private boolean in_roles = false;
    private boolean in_status = false;
    private boolean in_analytics = false;
    private boolean in_analsts = false;
    private String Message_FieldValue = "";
    HashMap<String, String> ErrorList;
    public HashMap<String, String> xTaskStatusList;
    public HashMap<String, String> xDeptSts2ColorMap;
    public ArrayList<PatientAnalyticsdDat> xPatientAnalyticsList;
    public ArrayList<DeptDef> xDeptList;
    public ArrayList<statusDefObj> xStatusDefList;

    public DeptDef xdeptdef;
    public statusDefObj xstatusDefObj;
    public PatientAnalyticsdDat xPatientAnalyticsdDat;
    public DeptStatus4AnalyticsObj xDeptStatus4AnalyticsObj;

    String EstDischargeTime;
    String CurDeptDischargeTime;
    String EstDischargeTimelocal;
    String CurDeptDischargeTimelocal;

    public XmlHandlerAnalytics(Context context) {
        this.mContext = context;
        mcurrentUserPref = mContext.getSharedPreferences(CURRENT_USER, 0);
        editor = mcurrentUserPref.edit();
        SignedUsr = mcurrentUserPref.getString("Username", null);
        ErrorList = MyApplication.INSTANCE.ErrorList;
        xTaskStatusList = MyApplication.INSTANCE.TaskStatusList;
        xPatientAnalyticsList = MyApplication.INSTANCE.PatientAnalyticsList;
        xDeptList = MyApplication.INSTANCE.DeptList;
        xStatusDefList = MyApplication.INSTANCE.StatusDefList;
        xDeptSts2ColorMap = MyApplication.INSTANCE.DeptSts2ColorMap;
        Log.d(TAG, "constructor of xmlhandleranalytics called");
    }

    @Override
    public void startDocument() throws SAXException {
    }

    @Override
    public void endDocument() throws SAXException {
    }

    @Override
    public void startElement(String namespaceURI, String localName,
                             String qName, Attributes atts) throws SAXException {
        if (localName.equalsIgnoreCase("result")) {
            Log.d(TAG, "inside result and code=" + atts.getValue("code"));
            if (!atts.getValue("code").equals("0")) {
                ErrorList.put("Apiresult", atts.getValue("message"));
                Log.d(TAG, "Error in xml=" + atts.getValue("message"));
            }

        } else if (localName.equalsIgnoreCase("Roles")) {
            in_roles = true;
            Log.d(TAG, "XDeptList size total=" + xDeptList);
        } else if (localName.equalsIgnoreCase("role")) {
            xdeptdef = new DeptDef();
            xdeptdef.setId(atts.getValue("id"));
            xdeptdef.setDispName(atts.getValue("dispname"));
            xdeptdef.setPosition(atts.getValue("position"));
            xDeptList.add(xdeptdef);
            Log.d(TAG, "Department name =" + atts.getValue("dispname"));
            Log.d(TAG, "XDeptList size=" + xDeptList.size());
        } else if (localName.equalsIgnoreCase("RoleStatuses")) {
            in_status = true;

        } else if (localName.equalsIgnoreCase("status")) {

            xstatusDefObj = new statusDefObj();
            xstatusDefObj.setName(atts.getValue("name"));
            xstatusDefObj.setId(atts.getValue("id"));
            xstatusDefObj.setDispName(atts.getValue("dispname"));
            xstatusDefObj.setcolor(atts.getValue("rgbcolor"));
            xStatusDefList.add(xstatusDefObj);
            xDeptSts2ColorMap.put(atts.getValue("id"), atts.getValue("rgbcolor"));
        } else if (localName.equalsIgnoreCase("Analytics")) {
            in_analytics = true;

        } else if (localName.equalsIgnoreCase("PatientAnalyticData")) {
            xPatientAnalyticsdDat = new PatientAnalyticsdDat();
            xPatientAnalyticsdDat.setPatientId(atts.getValue("id"));
            xPatientAnalyticsdDat.setName(atts.getValue("name"));
            EstDischargeTime = atts.getValue("estimateddischargetime");
            EstDischargeTimelocal = formatMeetingTime(EstDischargeTime);
            xPatientAnalyticsdDat.setEstDischargeTime(EstDischargeTimelocal);

            xPatientAnalyticsdDat.setCurDeptId(atts.getValue("curdeptid"));
            CurDeptDischargeTime = atts.getValue("curdeptestimatedcompletiontime");
            CurDeptDischargeTimelocal = formatMeetingTime(CurDeptDischargeTime);
            xPatientAnalyticsdDat.setCurDepEstCompTime(CurDeptDischargeTimelocal);
        } else if (localName.equalsIgnoreCase("Statuses")) {
            in_analsts = true;

        } else if (localName.equalsIgnoreCase("DeptStatus")) {
            in_analsts = true;
            xDeptStatus4AnalyticsObj = new DeptStatus4AnalyticsObj();
            xDeptStatus4AnalyticsObj.setDeptId(atts.getValue("deptid"));
            xDeptStatus4AnalyticsObj.setStatusId(atts.getValue("statusid"));
            xPatientAnalyticsdDat.Dept2StatusMap.put(atts.getValue("deptid"), atts.getValue("statusid"));
        }
    }

    @SuppressWarnings("deprecation")
    private String formatMeeting(String startTime, String endTime) {
        // TODO Auto-generated method stub
        String meetingTime = "";
        if (Utils.isEmpty(startTime) && Utils.isEmpty(endTime)) {
            // cases like market tracker which don't have any meeting time

            return meetingTime;
        }

        GregorianCalendar cal = new GregorianCalendar();
        Date startDate = new Date();
        Date endDate = new Date();

        if (startTime != null && startTime.length() > 0) {
            if (!startTime.contains("GMT")) {

                startDate = new Date(startTime);
                long startTime1 = startDate.getTime();
                cal.setTimeInMillis(startTime1);
                startDate = new Date(startTime1 + cal.get(Calendar.ZONE_OFFSET)
                        + cal.get(Calendar.DST_OFFSET));
            }
        } else {

            startDate = new Date();
        }
        if (endTime != null && endTime.length() > 0) {
            if (!endTime.contains("GMT")) {

                endDate = new Date(endTime);

                long endTime1 = endDate.getTime();
                cal.setTimeInMillis(endTime1);
                endDate = new Date(endTime1 + cal.get(Calendar.ZONE_OFFSET)
                        + cal.get(Calendar.DST_OFFSET));
            }
        } else {

            endDate = new Date();
        }
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm zzz");

        String startTimeString = format.format(startDate).toString();

        String[] starttokens = startTimeString.split("\\s");

        String endTimeString = format.format(endDate).toString();

        String[] endtokens = endTimeString.split("\\s");

        meetingTime = null;
        if (endtokens.length == 3) {
            if (starttokens[0].equals(endtokens[0])) {
                meetingTime = (starttokens[0].concat(" " + starttokens[1])
                        .concat(" " + "-" + " " + endtokens[1]).concat(" "
                                + endtokens[2]));
            }
        } else if (starttokens.length == 2 && endtokens.length == 2) {
            meetingTime = (starttokens[0].concat(" " + starttokens[1])
                    .concat(" " + "-" + " " + endtokens[1]));
        }

        if (meetingTime == null) {
            // do generic format
            // startdate starttime - enddate endtime timezone
            StringBuilder sb = new StringBuilder();
            sb.append(starttokens[0]).append(" "); // start date
            sb.append(starttokens[1]).append(" - "); // start time
            sb.append(endtokens[0]).append(" "); // end date
            sb.append(endtokens[1]).append(" "); // end time
            if (starttokens.length > 2) {
                sb.append(starttokens[2]); // time zone
            } else if (endtokens.length > 2) {
                sb.append(endtokens[2]);
            }
            meetingTime = sb.toString();
        }
        return meetingTime;

    }

    private String formatMeetingTime(String time) {
        if (Utils.isEmpty(time)) {
            return "";
        }
        Date startDate = new Date(time);
        if (!time.contains("GMT")) {
            // no gmt offset present, explicitly add offset to local time
            long startTime = startDate.getTime();
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTimeInMillis(startTime);
            startDate = new Date(startTime + cal.get(Calendar.ZONE_OFFSET)
                    + cal.get(Calendar.DST_OFFSET));
        }
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm ");
        String startTimeString = format.format(startDate).toString();
        return startTimeString;

    }

    @Override
    public void endElement(String namespaceURI, String localName, String qName)
            throws SAXException {
        if (localName.equalsIgnoreCase("result")) {


        } else if (localName.equalsIgnoreCase("Roles")) {
            in_roles = true;
        } else if (localName.equalsIgnoreCase("role")) {


        } else if (localName.equalsIgnoreCase("RoleStatuses")) {
            in_status = false;

        } else if (localName.equalsIgnoreCase("status")) {


        } else if (localName.equalsIgnoreCase("Analytics")) {
            in_analytics = false;

        } else if (localName.equalsIgnoreCase("PatientAnalyticData")) {
            xPatientAnalyticsList.add(xPatientAnalyticsdDat);
        } else if (localName.equalsIgnoreCase("Statuses")) {
            in_analsts = false;

        } else if (localName.equalsIgnoreCase("DeptStatus")) {

            xPatientAnalyticsdDat.DeptStatusList.add(xDeptStatus4AnalyticsObj);
        }
    }

    @Override
    public void characters(char ch[], int start, int length) {
        // Log.i("TAG","characters" + new String(ch, start, length));
        if (this.in_field) {
            Message_FieldValue = Message_FieldValue.concat(String.copyValueOf(
                    ch, start, length));
        }

    }
}
