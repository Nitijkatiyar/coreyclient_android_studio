
package com.coremobile.coreyhealth;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.coremobile.coreyhealth.genericviewandsurvey.CMN_JsonConstants;
import com.coremobile.coreyhealth.googleAnalytics.CMN_Preferences;
import com.coremobile.coreyhealth.partialsync.GetAllContextWebService;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;

public class CoreyDBHelper {
    private static final String TAG = "Corey_CoreyDBHelper";
    private Context mContext;

    public CoreyDBHelper(Context cxt) {
        mContext = cxt;
    }

    public String getListId(String Lname, int MsgId) {
        Cursor ItemCursor = mContext.getContentResolver().query(
                DatabaseProvider.CONTENT_URI,
                new String[]{DatabaseProvider.KEY_ID,
                        DatabaseProvider.KEY_FIELD_NAME,
                        DatabaseProvider.KEY_FIELD_DISPLAYTEXT,
                        DatabaseProvider.KEY_FIELD_VALUE,
                        DatabaseProvider.KEY_PARENT_ID_CONTEXT
                },
                DatabaseProvider.KEY_MSGID + " =? AND "
                        + DatabaseProvider.KEY_APPLICATION
                        + " =? AND "
                        + DatabaseProvider.KEY_OBJECT_NAME
                        + " =? AND "
                        + DatabaseProvider.KEY_APPLICATION_TITLE
                        + " =?  ",
                new String[]{String.valueOf(MsgId), "ListItems", "ListItem", Lname}, null);
        //	String ItemName=ItemCursor.getString(2);
        int i = 0;
        String Lstname = "";
        String id = "";
        String Scrolldir = "";
        String touch = "";
        String iscommon = "";
        while (ItemCursor.moveToNext()) {

            switch (i) {
                case 0:
                    Lstname = ItemCursor.getString(2);
                    break;
                case 1:
                    Scrolldir = ItemCursor.getString(2);
                    break;
                case 2:
                    touch = ItemCursor.getString(2);
                    break;
                case 3:
                    id = ItemCursor.getString(2);
                    break;
                default:
                    break;
            }
            i++;
        }
        if (ItemCursor != null) ItemCursor.close();
        return id;
    }

    public void markMessageAsRead(MessageItem msg) {
        ContentValues values = new ContentValues(1);
        values.put(DatabaseProvider.KEY_READ_FIELD, true);

        final String subject = "SUBJECT";
        final String message = "MESSAGE";

        String where = DatabaseProvider.KEY_MSGID + "=? AND "
                + "UPPER(" + DatabaseProvider.KEY_FIELD_NAME + ")=? AND "
                + "UPPER(" + DatabaseProvider.KEY_OBJECT_NAME + ")=?";

        int count = mContext.getContentResolver().update(
                DatabaseProvider.CONTENT_URI, values, where, new String[]{String.valueOf(msg.msgid), subject, message});
        if (count == 1) {
            msg.isRead = true;
            //Log.i(TAG, "markMsgAsRead msgid=" + msg.msgid);
        } else {
            //Log.e(TAG, "Internal error: markMsgAsRead msgid=" + msg.msgid
//                    + ", count is expected to be 1 but got " + count);
        }
    }

    public void markMessageAsUnRead(String msgid) {
        ContentValues values = new ContentValues(1);
        values.put(DatabaseProvider.KEY_READ_FIELD, false);

        final String subject = "SUBJECT";
        final String message = "MESSAGE";

        String where = DatabaseProvider.KEY_MSGID + "=? AND "
                + "UPPER(" + DatabaseProvider.KEY_FIELD_NAME + ")=? AND "
                + "UPPER(" + DatabaseProvider.KEY_OBJECT_NAME + ")=?";

        int count = mContext.getContentResolver().update(
                DatabaseProvider.CONTENT_URI, values, where, new String[]{String.valueOf(msgid), subject, message});
        if (count == 1) {
            //   msg.isRead = false;
            //Log.i(TAG, "markMsgAs un Read msgid=" + msgid);
        } else {
            //Log.e(TAG, "Internal error: markMsgAsRead msgid=" + msgid
//                    + ", count is expected to be 1 but got " + count);
        }
    }


    public ContentValues makeMessageContentValues(String msgid, int appid,
                                                  String app, String appGroupId, String appServerId, String objid,
                                                  String objname, String objtitle, String objDispTxt,
                                                  String fieldName, String fieldDisplayText, String fieldValue,
                                                  boolean read, String type, String common_logo,
                                                  String display_quadrant, String isEditable, String fieldid,
                                                  String hasstatus, String AddNote, String isNew, String openurl,
                                                  String convertTime, String time, String display_row,
                                                  String display_col, String msg_context, boolean isParent,
                                                  String future1, String future2, String future3, String future4,
                                                  String listValue, String editableListValue, String isAddField,
                                                  String addtype, String objDisplaySubscript, String graphType, String graphId, String needtimevalue, String isMultiselect,boolean IsLoading) {

        ContentValues values = new ContentValues();
        values.put(DatabaseProvider.KEY_MSGID, msgid);
        values.put(DatabaseProvider.KEY_APPLICATION_ID, appid);
        values.put(DatabaseProvider.KEY_APPLICATION, app);
        values.put(DatabaseProvider.KEY_APP_GROUP_ID, appGroupId);
        values.put(DatabaseProvider.KEY_APP_SERVER_ID, appServerId);
        values.put(DatabaseProvider.KEY_OBJECT_ID, objid);
        values.put(DatabaseProvider.KEY_OBJECT_NAME, objname);
        values.put(DatabaseProvider.KEY_APPLICATION_TITLE, objtitle);
        values.put(DatabaseProvider.KEY_OBJECT_DISPLAYTEXT, objDispTxt);
        values.put(DatabaseProvider.KEY_FIELD_NAME, fieldName);
        values.put(DatabaseProvider.KEY_FIELD_DISPLAYTEXT, fieldDisplayText);
        values.put(DatabaseProvider.KEY_FIELD_VALUE, fieldValue);
        values.put(DatabaseProvider.KEY_READ_FIELD, read);
        values.put(DatabaseProvider.KEY_MESSAGE_TYPE, type);
        values.put(DatabaseProvider.KEY_APP_COMMONLOGO, common_logo);
        values.put(DatabaseProvider.KEY_APP_STATUS, display_quadrant);
        values.put(DatabaseProvider.KEY_ISEDITABLE, isEditable);
        values.put(DatabaseProvider.KEY_FIELD_ID, fieldid);
        values.put(DatabaseProvider.KEY_HASSTATUS, hasstatus);
        values.put(DatabaseProvider.KEY_ADDFILED, AddNote);
        values.put(DatabaseProvider.KEY_ISNEW, isNew);
        values.put(DatabaseProvider.KEY_OPENURL, openurl);
        values.put(DatabaseProvider.KEY_CONVERTTIME, convertTime);
        values.put(DatabaseProvider.KEY_TIME, time);
        values.put(DatabaseProvider.KEY_DISPLAY_ROW, display_row);
        values.put(DatabaseProvider.KEY_DISPLAY_COLOUMN, display_col);
        values.put(DatabaseProvider.KEY_MSG_CONTEXT, msg_context);
        values.put(DatabaseProvider.KEY_ISPARENT_CONTEXT, isParent);
        values.put(DatabaseProvider.KEY_ISLOADING_CONTEXT, IsLoading);
        values.put(DatabaseProvider.KEY_PARENT_ID_CONTEXT, future1);
        values.put(DatabaseProvider.KEY_DISP_QUADRANT_CONTEXT, future2);
        values.put(DatabaseProvider.KEY_DISPLAY_ICON, future3);
        values.put(DatabaseProvider.KEY_FUTURE_USE_4_CONTEXT, future4);
        values.put(DatabaseProvider.KEY_LISTVALUE, listValue);
        values.put(DatabaseProvider.KEY_EDITABLE_LISTVALUE, editableListValue);
        values.put(DatabaseProvider.KEY_ISADDFIELD, isAddField);
        values.put(DatabaseProvider.KEY_ADDTYPE, addtype);
        values.put(DatabaseProvider.KEY_OBJECT_DISPLAY_SUBSCRIPT, objDisplaySubscript);
        values.put(DatabaseProvider.KEY_OBJECT_GraphType, graphType);
        values.put(DatabaseProvider.KEY_OBJECT_GraphId, graphId);
        values.put(DatabaseProvider.KEY_OBJECT_NeedTimeValue, needtimevalue);
        values.put(DatabaseProvider.KEY_FIELD_MULTISELECT, isMultiselect);

        return values;
    }


    public void insertMessages(List<ContentValues> messages) {

        int count = mContext.getContentResolver().bulkInsert(
                DatabaseProvider.CONTENT_URI,
                messages.toArray(new ContentValues[0]));

        //Log.e(TAG, "insertMessages num_messages=" + messages.size()
//                + ", num_inserted_rows=" + count);
    }


    public String makeWhereStringbyContext(String MsgContext, String ApplicationName, String ObjectName, String FieldName) {
        String msgCtxStr = "";
        String AppNameStr = "";
        String ObjNameStr = "";
        String FieldNameStr = "";
        if (MsgContext != null)
            msgCtxStr = DatabaseProvider.KEY_MSG_CONTEXT + "='" + MsgContext + "'";
        if (ApplicationName != null)
            AppNameStr = " AND " + DatabaseProvider.KEY_APPLICATION + "='" + ApplicationName + "'";
        if (ObjectName != null)
            ObjNameStr = " AND " + DatabaseProvider.KEY_OBJECT_DISPLAYTEXT + "='" + ObjectName + "'";
        if (FieldName != null)
            FieldNameStr = " AND " + DatabaseProvider.KEY_FIELD_NAME + "='" + FieldName + "'";
        String where = msgCtxStr + AppNameStr + ObjNameStr + FieldNameStr;
        //Log.d(TAG, "makeWhereString:where= " + where);
        return (where);
    }


    public void deletedb(Context mContext) {


        String path = "/data/data/com.coremobile.coreyhealth/files/databases/corey";
        File file = new File(path);
        file.delete();
        // SQLiteDatabase.deleteDatabase(new File(path));
        // SQLiteDatabase.

    }

    /*  public String getDBPath(Context mContext)

      {
         DatabaseHelper dbhelp = new DatabaseProvider(mContext);
          String pathh = mContext.getDatabasePath(DatabaseHelper.)
      } */
    public String makeWherebyContextObjectId(String MsgContext, String ApplicationName, String ObjectId, String FieldName) {
        String msgCtxStr = "";
        String AppNameStr = "";
        String ObjNameStr = "";
        String FieldNameStr = "";
        if (MsgContext != null)
            msgCtxStr = DatabaseProvider.KEY_MSG_CONTEXT + "='" + MsgContext + "'";
        if (ApplicationName != null)
            AppNameStr = " AND " + DatabaseProvider.KEY_APPLICATION + "='" + ApplicationName + "'";
        if (ObjectId != null)
            ObjNameStr = " AND " + DatabaseProvider.KEY_OBJECT_ID + "='" + ObjectId + "'";
        if (FieldName != null)
            FieldNameStr = " AND " + DatabaseProvider.KEY_FIELD_NAME + "='" + FieldName + "'";
        String where = msgCtxStr + AppNameStr + ObjNameStr + FieldNameStr;
        //Log.d(TAG, "makeWhereString:where= " + where);
        return (where);
    }

    public String makeWherebyContextParentObjectId(String MsgContext, String ApplicationName, String ObjectId, String FieldName) {
        String msgCtxStr = "";
        String AppNameStr = "";
        String ObjNameStr = "";
        String FieldNameStr = "";
        if (MsgContext != null)
            msgCtxStr = DatabaseProvider.KEY_MSG_CONTEXT + "='" + MsgContext + "'";
        if (ApplicationName != null)
            AppNameStr = " AND " + DatabaseProvider.KEY_APPLICATION + "='" + ApplicationName + "'";
        if (ObjectId != null)
            ObjNameStr = " AND " + DatabaseProvider.KEY_PARENT_ID_CONTEXT + "='" + ObjectId + "'";
        if (FieldName != null)
            FieldNameStr = " AND " + DatabaseProvider.KEY_FIELD_NAME + "='" + FieldName + "'";
        String where = msgCtxStr + AppNameStr + ObjNameStr + FieldNameStr;
        //Log.d(TAG, "makeWhereString:where= " + where);
        return (where);
    }

    public String makeListItemWhereStringbyContext(String MsgContext, String ApplicationName, String ListName) {
        String msgCtxStr = "";
        String AppNameStr = "";
        String ListStr = "";

        if (MsgContext != null)
            msgCtxStr = DatabaseProvider.KEY_MSG_CONTEXT + "='" + MsgContext + "'";
        if (ApplicationName != null)
            AppNameStr = " AND " + DatabaseProvider.KEY_APPLICATION + "='" + ApplicationName + "'";
        if (ListName != null)
            ListStr = " AND " + DatabaseProvider.KEY_APPLICATION_TITLE + "='" + ListName + "'";

        String where = msgCtxStr + AppNameStr + ListStr;
        //Log.d(TAG, "makeListItemWhereString:where= " + where);
        return (where);
    }

    public void deleteByWhere(String where) {

        int count = mContext.getContentResolver().delete(
                DatabaseProvider.CONTENT_URI, where, null);
        //Log.i(TAG, " where=(" + where + "), num_deleted_rows=" + count);
    }

    public void deleteByWheres(ArrayList<String> wherelist) {
        if (!wherelist.isEmpty()) {
            for (String deletelist : wherelist) {
                deleteByWhere(deletelist);
            }
        }
    }

    public void deleteMessage(int msgId) {
        String where = DatabaseProvider.KEY_MSGID + "=?";
        int count = mContext.getContentResolver().delete(
                DatabaseProvider.CONTENT_URI, where, new String[]{String.valueOf(msgId)});
        //Log.i(TAG, "deleteMessage msgId=" + msgId + ", where=(" + where
//                + "), num_deleted_rows=" + count);
    }

    public void deleteAllMessages() {
        int count = mContext.getContentResolver().delete(
                DatabaseProvider.CONTENT_URI, null, null);
        //Log.i(TAG, "deleteAllMessages, ALL num_deleted_rows=" + count);
    }


    public void deleteAllMessages(String type) {
        String where = DatabaseProvider.KEY_MESSAGE_TYPE + "=?";
        int count = mContext.getContentResolver().delete(
                DatabaseProvider.CONTENT_URI, where, new String[]{type});
        //Log.i(TAG, "deleteAllMessages, where=(" + where
//                + "), num_deleted_rows=" + count);
    }

    // activity argument needed for managing cursors
    // TODO:mahesh startManagingCursor is deperecated. We have to switch to
    // CursorLoader
    public ArrayList<MessageItem> getAllMessages(Activity activity) {
        //Log.i(TAG, "getAllMessages");
        TreeMap<Integer, MessageItem> msgMap; // map: msgid -> msg

        msgMap = new TreeMap<Integer, MessageItem>();
        getAllMessageSubjects(activity, msgMap);
        getAllMessageStartTimes(activity, msgMap);
        getAllMessageEndTimes(activity, msgMap);

        formatMeetingTime(msgMap.values());
        ArrayList<MessageItem> allMessages = new ArrayList<MessageItem>(msgMap.values());
        allMessages.addAll(getAllPhoneCallMessages(activity));
        return allMessages;
    }

    // returns map: messagType -> messages
    public Map<String, ArrayList<MessageItem>> getAllMessagesMap(Activity activity) {
        Collection<MessageItem> allMessages = GetAllContextWebService.patientsContexts;
        Map<String, ArrayList<MessageItem>> allMessagesMap = new HashMap<String, ArrayList<MessageItem>>();

        for (MessageItem msg : allMessages) {
            if (!allMessagesMap.containsKey(msg.type)) {
                allMessagesMap.put(msg.type, new ArrayList<MessageItem>());
            }
            allMessagesMap.get(msg.type).add(msg);
        }

        return allMessagesMap;
    }

    public Set<String> getAllMsgids() {

        HashSet<String> msgidSet = new HashSet<String>();

        Cursor msgidCursor = mContext.getContentResolver().query(DatabaseProvider.CONTENT_URI, new String[]{DatabaseProvider.KEY_ID, DatabaseProvider.KEY_MSGID}, null, null, null);
        if (msgidCursor != null && msgidCursor.getCount() > 0) {
            while (msgidCursor.moveToNext()) {
                msgidSet.add(msgidCursor.getString(1));
            }
        }
        if (msgidCursor != null) msgidCursor.close();
        return msgidSet;

    }

    public String getMsgIdbyContext(String MsgContext, String MsgType) {
        String msgid = null;
        //-----test fortify issue fix-------------
        String where = DatabaseProvider.KEY_MSG_CONTEXT + "= ? AND " + DatabaseProvider.KEY_MESSAGE_TYPE + "= ?";

//        String where = DatabaseProvider.KEY_MSG_CONTEXT + "='" + MsgContext + "'"
//                + " AND " + DatabaseProvider.KEY_MESSAGE_TYPE + "='" + MsgType + "'";
        //Log.d(TAG, "++++ In getMsgIDbyProvider  where = " + where);
        Cursor cursor = mContext.getContentResolver().query(
                DatabaseProvider.CONTENT_URI,
                new String[]{DatabaseProvider.KEY_MSGID}, where
                , new String[]{MsgContext, MsgType}, null);
        //Log.d(TAG, "cursor count =" + cursor.getCount());
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                msgid = cursor.getString(0);
//                Log.d(TAG, "msgid = " + msgid);
                break;
            }
        }
        cursor.close();
        return msgid;
    }

    public void deleteMessagesForCaller(String phoneNumber, boolean keepRecent) {
        //Log.i(TAG, "deleteMessagesForCaller phoneNumber=" + phoneNumber
//                + ", keepRecent=" + keepRecent);
        // find message ids for phoneNumber
        String where = DatabaseProvider.KEY_MESSAGE_TYPE + "='"
                + DatabaseProvider.MSG_TYPE_PHONE_CALL + "' AND " + "UPPER("
                + DatabaseProvider.KEY_OBJECT_NAME + ")='CALENDAR' AND (("
                + "UPPER(" + DatabaseProvider.KEY_FIELD_NAME
                + ")='PHONENUMBER' AND " + DatabaseProvider.KEY_FIELD_VALUE
                + "='" + phoneNumber + "') OR (" + "UPPER("
                + DatabaseProvider.KEY_FIELD_NAME + ")='ATTENDEE' AND "
                + DatabaseProvider.KEY_FIELD_VALUE + " LIKE '%" + phoneNumber
                + "'))";

        String sortOrder = DatabaseProvider.KEY_MSGID + " DESC";

        //Log.i(TAG, "deleteMessagesForCaller where = " + where);

        Cursor cursor = mContext.getContentResolver().query(
                DatabaseProvider.CONTENT_URI,
                new String[]{DatabaseProvider.KEY_MSGID}, where, null,
                sortOrder);

        ArrayList<String> msgIds = new ArrayList<String>();
        while (cursor.moveToNext()) {
            String id = cursor.getString(0);
            if (!msgIds.contains(id)) {
                msgIds.add(id);
            }
        }
        cursor.close();

//        Log.i(TAG, "deleteMessagesForCaller msgIds = " + msgIds);

        if (keepRecent && !msgIds.isEmpty()) {
            msgIds.remove(0);
        }

        if (!msgIds.isEmpty()) {
            where = DatabaseProvider.KEY_MSGID + " IN "
                    + "?";
//            Log.i(TAG, "deleteMessagesForCaller delWhere = " + where);
            int count = mContext.getContentResolver().delete(
                    DatabaseProvider.CONTENT_URI, where, new String[]{msgIds.toString().replace('[', '(').replace(']', ')')});
            //Log.i(TAG, "deleteMessagesForCaller phoneNumber=" + phoneNumber
//                    + ", num_deleted_rows=" + count);
        } else {
            //Log.i(TAG, "deleteMessagesForCaller NOTHING to delete phoneNumber="
//                    + phoneNumber);
        }
        if (cursor != null) cursor.close();
    }

    public void deleteMessagesForOtherCallers(
            Collection<String> phoneNumbersToKeep) {
//        Log.i(TAG, "deleteMessagesForOtherCallers phoneNumbersToKeep="
//                + phoneNumbersToKeep);
        // find message ids
        String where = DatabaseProvider.KEY_MESSAGE_TYPE
                + "='? AND "
                + "UPPER("
                + DatabaseProvider.KEY_OBJECT_NAME
                + ")=? AND "
                + "UPPER("
                + DatabaseProvider.KEY_FIELD_NAME
                + ")=? AND "
                + "UPPER("
                + DatabaseProvider.KEY_FIELD_VALUE
                + ") NOT IN "
                + "?";

//        Log.i(TAG, "deleteMessagesForOtherCallers where=" + where);
        Cursor cursor = mContext.getContentResolver().query(
                DatabaseProvider.CONTENT_URI,
                new String[]{DatabaseProvider.KEY_MSGID}, where, new String[]{DatabaseProvider.MSG_TYPE_PHONE_CALL, "CALENDAR", "PHONENUMBER", phoneNumbersToKeep.toString().replace('[', '(')
                        .replace(']', ')')}, null);

        ArrayList<String> idsToRemove = new ArrayList<String>();
        while (cursor.moveToNext()) {
            idsToRemove.add(cursor.getString(0));
        }
        cursor.close();

        // remove messages
//        Log.i(TAG, "deleteMessagesForOtherCallers idsToRemove=" + idsToRemove);
        if (!idsToRemove.isEmpty()) {
            where = DatabaseProvider.KEY_MSGID
                    + " IN "
                    + "?";
//            Log.i(TAG, "deleteMessagesForOtherCallers delWhere = " + where);
            int count = mContext.getContentResolver().delete(
                    DatabaseProvider.CONTENT_URI, where, new String[]{idsToRemove.toString().replace('[', '(')
                            .replace(']', ')')});
//            Log.i(TAG, "deleteMessagesForOtherCallers num_deleted_rows="
//                    + count);
        }
        if (cursor != null) cursor.close();
    }

    public int getMsgIdForCaller(String phoneNumber) {
        String where = "UPPER(" + DatabaseProvider.KEY_OBJECT_NAME
                + ")=? AND ((" + "UPPER("
                + DatabaseProvider.KEY_FIELD_NAME + ")=? AND "
                + DatabaseProvider.KEY_FIELD_VALUE + "=?"
                + "') OR (" + "UPPER(" + DatabaseProvider.KEY_FIELD_NAME
                + ")=? AND " + DatabaseProvider.KEY_FIELD_VALUE
                + " LIKE '%?'))";

        String sortOrder = DatabaseProvider.KEY_MSGID + " DESC ";
        Cursor cursor = mContext.getContentResolver().query(
                DatabaseProvider.CONTENT_URI,
                new String[]{DatabaseProvider.KEY_MSGID}, where, new String[]{"CALENDAR", "PHONENUMBER", phoneNumber, "ATTENDEE", phoneNumber},
                sortOrder);

        int msgId = -1;
        while (cursor.moveToNext()) {
            msgId = cursor.getInt(0);
            break;
        }
        if (cursor != null) cursor.close();
//        Log.i(TAG, "getMsgIdForCaller returning " + msgId);
        return msgId;
    }


    public String getObjID(int msgId, String appName, String Objname) {
        String objName = null;

        String where = DatabaseProvider.KEY_MSGID + "=" + msgId + " AND "
                + DatabaseProvider.KEY_APPLICATION + "= '" + appName + "'"
                + " AND " + DatabaseProvider.KEY_OBJECT_NAME + "= '"
                + Objname + "'";

//        Log.d("DetailedApplicationData", " the where is   " + where
//                + " fieldValue " + Objname + " appName  ");

        Cursor cursor = mContext.getContentResolver().query(
                DatabaseProvider.CONTENT_URI,
                new String[]{DatabaseProvider.KEY_OBJECT_ID}, where, null,
                null);
//        Log.d(TAG, "getObjID cursor size=" + cursor.getCount());
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                objName = cursor.getString(0);
//                Log.d("DetailedApplicationData",
//                        "In getObjectID getObjectID = " + objName + " where "
//                                + where);
                break;
            }
        }
        if (cursor != null) cursor.close();

        return objName;

    }


    /*
     * Get image url given quadrant name and object name
     */
    public String getObjImageUrlbyqdr(int msgId, int quadrant, String Objname) {
        String Imgurl = null;
        String Imgurl1 = null;

        String where = DatabaseProvider.KEY_MSGID + "=" + msgId + " AND "
                + DatabaseProvider.KEY_DISP_QUADRANT_CONTEXT + "= '" + quadrant + "'"
                + " AND " + DatabaseProvider.KEY_OBJECT_NAME + "= '"
                + Objname + "'";

//        Log.d("DetailedApplicationData", " the where is   " + where
//                + " fieldValue " + Objname + " appName  ");

        Cursor cursor = mContext.getContentResolver().query(
                DatabaseProvider.CONTENT_URI,
                new String[]{DatabaseProvider.KEY_APP_COMMONLOGO, DatabaseProvider.KEY_DISPLAY_ICON}, where, null,
                null);
//        Log.d(TAG, "getObjID cursor size=" + cursor.getCount());
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Imgurl = cursor.getString(0);
                Imgurl1 = cursor.getString(1);
//                Log.d("DetailedApplicationData",
//                        "In getObjImageurl  Imgurl= " + Imgurl + " where "
//                                + where);
//                Log.d("DetailedApplicationData",
//                        "In getObjImageurl  Imgurl1= " + Imgurl1 + " where "
//                                + where);
                break;
            }

        }
        if (cursor != null) cursor.close();
        if (!TextUtils.isEmpty(Imgurl1) && Imgurl1 != null) return Imgurl1;
        else return Imgurl;


    }


    public Collection<MessageItem> getAllPhoneCallMessages(Activity activity) {
        String[] projection = DatabaseProvider.COLS_phonecalls;
        String selection = "";
        String sortOrder = DatabaseProvider.KEY_CALLTIME + " DESC";

        Cursor cursor = mContext.getContentResolver().query(
                DatabaseProvider.CONTENT_URI_phonecalls, projection, selection,
                null, sortOrder);

        if (cursor == null) {
            //Log.w(TAG, "getAllPhoneCallMessages: null cursor");
            return null;
        }

        //	activity.startManagingCursor(cursor);

        ArrayList<MessageItem> phoneCalls = new ArrayList<MessageItem>();

        while (cursor.moveToNext()) {
            int msgid = cursor.getInt(0);
            String phonenumber = cursor.getString(1);
            String firstname = cursor.getString(2);
            String lastname = cursor.getString(3);
            String calltime = new Date(cursor.getLong(4)).toString();
            String company = cursor.getString(5);

            String subject = phonenumber + "   " + firstname + "  " + lastname;

            MessageItem msg = new MessageItem();
            msg.type = DatabaseProvider.MSG_TYPE_PHONE_CALL;
            msg.subject = subject;
            msg.msgid = msgid;
            msg.meetingTime = calltime;
            msg.name = firstname + "  " + lastname;
            msg.phonenumber = phonenumber;
            msg.company = company;
            msg.isRead = true;

            phoneCalls.add(msg);
        }
        if (cursor != null) cursor.close(); //ramesh
        //Log.i(TAG, "getAllPhoneCallMessages: calls.size=" + phoneCalls.size());
        return phoneCalls;
    }

    public void deleteAllPhoneCallLogs() {
        int count = mContext.getContentResolver().delete(
                DatabaseProvider.CONTENT_URI_phonecalls, null, null);
        //Log.i(TAG, "deleteAllPhoneCallLogs, ALL num_deleted_rows=" + count);
    }

    public void deletePhoneCallLog(int msgId) {
        String where = "id=" + msgId;
        int count = mContext.getContentResolver().delete(
                DatabaseProvider.CONTENT_URI_phonecalls, where, null);
        //Log.i(TAG, "deletePhoneCallMessage msgId=" + msgId + ", where=("
//                + where + "), num_deleted_rows=" + count);
    }


    /******
     * PRIVATE METHODS
     ******/
    private void getAllMessageSubjects(Activity activity,
                                       TreeMap<Integer, MessageItem> msgMap) {
        String[] projection = new String[]{DatabaseProvider.KEY_MESSAGE_TYPE,
                DatabaseProvider.KEY_FIELD_VALUE, DatabaseProvider.KEY_MSGID,
                DatabaseProvider.KEY_READ_FIELD, DatabaseProvider.KEY_MSG_CONTEXT
        };

        String selection = "UPPER(" + DatabaseProvider.KEY_OBJECT_NAME + ")"
                + "='MESSAGE' AND " + "UPPER("
                + DatabaseProvider.KEY_FIELD_NAME + ") " + "='SUBJECT'"
                + " AND " + DatabaseProvider.KEY_MESSAGE_TYPE + " != '" + DatabaseProvider.MSG_TYPE_PHONE_CALL + "'";

        //Log.i(TAG, "getAllMessageSubjects: " + selection);

        Cursor cursor = mContext.getContentResolver()
                .query(DatabaseProvider.CONTENT_URI, projection, selection,
                        null, null);

        if (cursor == null) {
            //Log.w(TAG, "getAllMessageSubjects: null cursor");
            return;
        }

//		activity.startManagingCursor(cursor);
        while (cursor.moveToNext()) {
            MessageItem msg = new MessageItem();
            msg.type = cursor.getString(0);
            msg.subject = cursor.getString(1);
            msg.msgid = cursor.getInt(2);
            msg.isRead = (cursor.getInt(3) == 1);
            msg.ContextId = cursor.getString(4);
            msgMap.put(msg.msgid, msg);
        }
        //Log.i(TAG, "getAllMessageSubjects: map.size=" + msgMap.size());
        if (cursor != null) cursor.close();  //ramesh
    }

    private void getAllMessageStartTimes(Activity activity,
                                         TreeMap<Integer, MessageItem> msgMap) {
        String[] projection = new String[]{DatabaseProvider.KEY_MESSAGE_TYPE,
                DatabaseProvider.KEY_FIELD_VALUE, DatabaseProvider.KEY_MSGID
        };

        String selection = "UPPER(" + DatabaseProvider.KEY_OBJECT_NAME + ")"
                + "='MESSAGE' AND " + "UPPER("
                + DatabaseProvider.KEY_FIELD_NAME + ") " + "='MEETINGSTART'"
                + " AND " + DatabaseProvider.KEY_MESSAGE_TYPE + " != '" + DatabaseProvider.MSG_TYPE_PHONE_CALL + "'";

        //Log.i(TAG, "getAllMessageStartTimes: " + selection);

        Cursor cursor = mContext.getContentResolver()
                .query(DatabaseProvider.CONTENT_URI, projection, selection,
                        null, null);

        if (cursor == null) {
            //Log.w(TAG, "getAllMessageStartTimes: null cursor");
            return;
        }

//		activity.startManagingCursor(cursor);
        while (cursor.moveToNext()) {
            int msgId = cursor.getInt(2);
            MessageItem msg = msgMap.get(msgId);
            if (msg == null) {
                //Log.w(TAG, "getAllMessageStartTimes: missing msg for msgId="
//                        + msgId);
                continue;
            }
            msg.startTime = cursor.getString(1);
        }
        if (cursor != null) cursor.close();  //ramesh
    }

    private void getAllMessageEndTimes(Activity activity,
                                       TreeMap<Integer, MessageItem> msgMap) {
        String[] projection = new String[]{DatabaseProvider.KEY_MESSAGE_TYPE,
                DatabaseProvider.KEY_FIELD_VALUE, DatabaseProvider.KEY_MSGID
        };

        String selection = "UPPER(" + DatabaseProvider.KEY_OBJECT_NAME + ")"
                + "='MESSAGE' AND " + "UPPER("
                + DatabaseProvider.KEY_FIELD_NAME + ") " + "='MEETINGEND'"
                + " AND " + DatabaseProvider.KEY_MESSAGE_TYPE + " != '" + DatabaseProvider.MSG_TYPE_PHONE_CALL + "'";

     /*   String selection = "UPPER(" + DatabaseProvider.KEY_OBJECT_NAME + ")"
                + "='MESSAGE' AND " + "UPPER("
                + DatabaseProvider.KEY_FIELD_NAME + ") " + "='MEETINGEND'"
                + " AND " + DatabaseProvider.KEY_MESSAGE_TYPE + " != '" + DatabaseProvider.MSG_TYPE_PHONE_CALL + "'";
*/
        //Log.i(TAG, "getAllMessageEndTimes: " + selection);

        Cursor cursor = mContext.getContentResolver()
                .query(DatabaseProvider.CONTENT_URI, projection, selection,
                        null, null);

        if (cursor == null) {
            //Log.w(TAG, "getAllMessageEndTimes: null cursor");
            return;
        }

//		activity.startManagingCursor(cursor);
        while (cursor.moveToNext()) {
            int msgId = cursor.getInt(2);
            MessageItem msg = msgMap.get(msgId);
            if (msg == null) {
                //Log.w(TAG, "getAllMessageEndTimes: missing msg for msgId="
//                        + msgId);
                continue;
            }
            msg.endTime = cursor.getString(1);
        }
        if (cursor != null) cursor.close();  //ramesh
    }

    private void formatMeetingTime(Collection<MessageItem> msgs) {
        //Log.i(TAG, "formatMeetingTime msgs.size=" + msgs.size()
//                + ", DefTimezone=" + TimeZone.getDefault().getDisplayName());
        for (MessageItem msg : msgs) {
            if (Utils.isEmpty(msg.startTime) && Utils.isEmpty(msg.endTime)) {
                // cases like market tracker which don't have any meeting time
                msg.meetingTime = "";
                continue;
            }

            GregorianCalendar cal = new GregorianCalendar();
            Date startDate = new Date();
            Date endDate = new Date();

            try {
                if (msg.startTime != null && msg.startTime.length() > 0) {
                    if (!msg.startTime.contains("GMT")) {

                        startDate = new Date(msg.startTime);
                        long startTime = startDate.getTime();
                        cal.setTimeInMillis(startTime);
                        startDate = new Date(startTime
                                + cal.get(Calendar.ZONE_OFFSET)
                                + cal.get(Calendar.DST_OFFSET));
                    }
                } else {

                    startDate = new Date();
                }
                if (msg.endTime != null && msg.endTime.length() > 0) {
                    if (!msg.endTime.contains("GMT")) {

                        endDate = new Date(msg.endTime);

                        long endTime = endDate.getTime();
                        cal.setTimeInMillis(endTime);
                        endDate = new Date(endTime
                                + cal.get(Calendar.ZONE_OFFSET)
                                + cal.get(Calendar.DST_OFFSET));
                    }
                } else {

                    endDate = new Date();
                }
                SimpleDateFormat format = new SimpleDateFormat(
                        "MM/dd/yyyy HH:mm zzz");

                String startTimeString = format.format(startDate).toString();

                String[] starttokens = startTimeString.split("\\s");

                String endTimeString = format.format(endDate).toString();

                String[] endtokens = endTimeString.split("\\s");

                msg.meetingTime = null;
                if (endtokens.length == 3) {
                    if (starttokens[0].equals(endtokens[0])) {
                        msg.meetingTime = (starttokens[0].concat(
                                " " + starttokens[1]).concat(
                                " " + "-" + " " + endtokens[1]).concat(" "
                                + endtokens[2]));
                    }
                } else if (starttokens.length == 2 && endtokens.length == 2) {
                    msg.meetingTime = (starttokens[0].concat(" "
                            + starttokens[1]).concat(" " + "-" + " "
                            + endtokens[1]));
                }

                if (msg.meetingTime == null) {
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
                    msg.meetingTime = sb.toString();
                }
            } catch (Exception e) {

                continue;
            }
        }
    }

    public void updateSFFielditem(String OpportunityName, String updateString, String FieldName, int msgId) {
        //Find all the messagid

        HashMap<String, HashSet<String>> messageIdmap = new HashMap<String, HashSet<String>>();
        // 	Set<Integer> messageIdList = new HashSet<Integer>();
        //Log.d(TAG, "updateSFFielditem OpportunityName=" + OpportunityName);
        //Log.d(TAG, "updateSFFielditem updateString=" + updateString);
        //Log.d(TAG, "updateSFFielditem FieldName=" + FieldName);
        //Log.d(TAG, "updateSFFielditem msgId=" + msgId);
        String[] projection = new String[]{DatabaseProvider.KEY_MSGID,
                DatabaseProvider.KEY_OBJECT_ID};

        String selection = DatabaseProvider.KEY_FIELD_VALUE + "='" + OpportunityName + "'";

        //Log.i(TAG, "UpdateSFFielditem: " + selection);

        Cursor cursor = mContext.getContentResolver()
                .query(DatabaseProvider.CONTENT_URI, projection, selection,
                        null, null);

        if (cursor == null) {
            //Log.d(TAG, "updateSFFielditem set of messageid: null cursor");
            return;
        } else {
            //Log.d(TAG, "Cursor count =" + cursor.getCount());
            while (cursor.moveToNext()) {

                int MsgId = cursor.getInt(0);
                String MsgIdstr = Integer.toString(MsgId);
                //   int ObjId = cursor.getInt(1);
                //  String ObjIdstr= Integer.toString(ObjId);
                String ObjIdstr = cursor.getString(1);
                //   String value = cursor.getString(3);

                if (!messageIdmap.containsKey(MsgIdstr)) {
                    messageIdmap.put(MsgIdstr, new HashSet<String>());
                }
                //Log.d(TAG, "MsgIdstr=" + MsgIdstr);
                //Log.d(TAG, "ObjIdstr=" + ObjIdstr);
                messageIdmap.get(MsgIdstr).add(ObjIdstr);

            }
        }
        //Log.d(TAG, "Messageidmap=" + messageIdmap);
        Iterator it = messageIdmap.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pairs = (HashMap.Entry) it.next();
            String MsgId = (String) pairs.getKey();
            HashSet<String> ObjIdSet = (HashSet) pairs.getValue();
            for (String Objidset : ObjIdSet) {
                int Messageid = Integer.parseInt(MsgId);
                //int ObjId= Integer.parseInt(Objidset);

                updateField(Messageid, Objidset, FieldName, updateString);
            }


        }


    }

    public void updateField(int MsgId, String ObjId, String fieldName, String fieldlValue) {
        //	String appName = "Salesforce";

        ContentValues values = new ContentValues();
        values.put(DatabaseProvider.KEY_FIELD_VALUE, fieldlValue);

        String where = DatabaseProvider.KEY_MSGID + "=" + MsgId
                + " AND " + DatabaseProvider.KEY_OBJECT_ID + "= '" + ObjId + "'"
                + " AND " + DatabaseProvider.KEY_FIELD_NAME + "= '" + fieldName + "'";

        int count = mContext.getContentResolver().update(
                DatabaseProvider.CONTENT_URI,
                values,
                where,
                null);

        //Log.d(TAG, "updateFieldcount =" + count);

    }

    public void updateObjIsNew(int MsgId, String ObjId, String IsNewValue) {
        //	String appName = "Salesforce";

        ContentValues values = new ContentValues();
        values.put(DatabaseProvider.KEY_ISNEW, IsNewValue);

        String where = DatabaseProvider.KEY_MSGID + "=? AND "
                + DatabaseProvider.KEY_OBJECT_ID + "=?";
        //  		  + " AND " +  DatabaseProvider.KEY_ISNEW + "= '" + fieldName +"'";

        int count = mContext.getContentResolver().update(
                DatabaseProvider.CONTENT_URI,
                values,
                where,
                new String[]{String.valueOf(MsgId), ObjId});

        //Log.d(TAG, "updateIsNewcount =" + count);

    }


    public String getPatientName(int msgId) {
        // TODO Auto-generated method stub
        StringBuilder pname = new StringBuilder();
        String appnamefromdispcolumn = getAppNameFromDispCol(msgId, 0);

        String where = DatabaseProvider.KEY_MSGID + "=? AND "
                + "UPPER(" + DatabaseProvider.KEY_OBJECT_NAME + ")=?";
        //Log.d(TAG, "In  where = " + where);

        Cursor cursor = mContext.getContentResolver().query(
                DatabaseProvider.CONTENT_URI,
                new String[]{DatabaseProvider.KEY_ID, DatabaseProvider.KEY_FIELD_VALUE}, where, new String[]{String.valueOf(msgId), appnamefromdispcolumn},
                null);

        //Log.d(TAG, "In  where = " + cursor.getCount());


        if (cursor != null && cursor.getCount() > 0) {
            int count = 0;
            while (cursor.moveToNext()) {
                count++;
                pname.append(cursor.getString(1));
                //Log.d(TAG, "In  name = " + pname);
                if (count == 2)
                    break;
            }
            if (cursor != null) cursor.close();  //ramesh
        }
        //	cursor.close();
        if (cursor != null) cursor.close();
        return pname.toString();
    }


    public String getPatientDetails(int msgId) {

        StringBuilder patientDetails = new StringBuilder();
        String data = "";

        String where = DatabaseProvider.KEY_MSGID + "= ? AND "
                + "UPPER(" + DatabaseProvider.KEY_OBJECT_NAME + ")=?";
        Cursor cursor = mContext.getContentResolver().query(
                DatabaseProvider.CONTENT_URI,
                new String[]{DatabaseProvider.KEY_FIELD_DISPLAYTEXT,
                        DatabaseProvider.KEY_FIELD_VALUE
                }, where, new String[]{String.valueOf(msgId), "CALENDAR"}, null);

        if (cursor != null && cursor.getCount() > 0) {
            //cursor.moveToNext();
            int count = 0;
            while (cursor.moveToNext()) {
                count++;
                if (cursor.isFirst()) {
                    cursor.moveToNext();
                    if (cursor.getString(0) != null && !cursor.getString(0).equalsIgnoreCase("")) {
                        patientDetails.append("<b> " + cursor.getString(0)
                                + " : " + cursor.getString(1) + "</b><br>");
                        data = "" + cursor.getString(0)
                                + " : " + cursor.getString(1);
                    } else patientDetails.append("<b> " + cursor.getString(1) + "</b><br>");


                } else {
                    if (cursor.getString(0).equalsIgnoreCase("Account Name"))
                        continue;

                    if (Utils.isEmpty(cursor.getString(1)))
                        continue;
                    if (cursor.getString(0) != null && !cursor.getString(0).equalsIgnoreCase("")) {
                        if (cursor.getString(0) != null && cursor.getString(0).equalsIgnoreCase("Date of surgery") || cursor.getString(0).equalsIgnoreCase("Procedure Date") || cursor.getString(0).contains("Date")) {


                            SimpleDateFormat simpleDateFormatW3C = new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.US);
                            simpleDateFormatW3C.setTimeZone(TimeZone.getTimeZone("UTC"));
                            Date dateServer = null;
                            try {
                                dateServer = simpleDateFormatW3C.parse(cursor.getString(1));
                            } catch (ParseException e) {
                                e.getMessage();
                            }

                            TimeZone deviceTimeZone = TimeZone.getDefault();
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            simpleDateFormat.setTimeZone(deviceTimeZone);

                            String formattedDate = simpleDateFormat.format(dateServer);


                            data = cursor.getString(0) + " : "
                                    + cursor.getString(1) + "";
                            patientDetails.append(cursor.getString(0) + " : "
                                    + formattedDate + "<br>");
                        } else {
                            data = cursor.getString(0) + " : "
                                    + cursor.getString(1) + "";
                            patientDetails.append(cursor.getString(0) + " : "
                                    + cursor.getString(1) + "<br>");
                        }

                    } else patientDetails.append("<b> " + cursor.getString(1) + "</b><br>");

                }
                //Log.e("patientrow0data", "" + data);

//                if (count == 4) {
//                    break;
//                }
            }
            count = 0;
            if (cursor != null) cursor.close();  //ramesh
        }
        //	cursor.close();

        String pdetailes = patientDetails.toString().replace("Date of Birth",
                "DoB");
        patientDetails = null;
        patientDetails = new StringBuilder();
        CMN_JsonConstants.msgId = "" + msgId;
        patientDetails.append(getPatientName(Integer.parseInt(CMN_JsonConstants.msgId)));
        //Log.d(TAG, "getPatientDetails =" + patientDetails.toString());
        CMN_Preferences.setrow0content(mContext, pdetailes.toString() + patientDetails.toString());
        return pdetailes.toString() + patientDetails.toString();

    }


    public String getPatientNamebyMsgId(int msgId) {
        // TODO Auto-generated method stub
        String pname = "";

        String where = DatabaseProvider.KEY_MSGID + "=" + msgId + " AND "
                + "UPPER(" + DatabaseProvider.KEY_OBJECT_NAME + ")='"
                + "CALENDAR" + "'" + " AND "
                + DatabaseProvider.KEY_FIELD_DISPLAYTEXT + "= '" + "Name" + "'";

        //Log.d(TAG, "In  where = " + where);

        Cursor cursor = mContext.getContentResolver().query(
                DatabaseProvider.CONTENT_URI,
                new String[]{DatabaseProvider.KEY_FIELD_VALUE}, where, null,
                null);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                pname = cursor.getString(0);
                //Log.d(TAG, "In  name = " + pname);
                break;
            }
            if (cursor != null) cursor.close();  //ramesh
        }
        //	cursor.close();
        if (cursor != null) cursor.close();
        return pname;

    }

    public void removeOldMessage(String message_Subject) {
        // TODO Auto-generated method stub
        ArrayList<String> msgids = new ArrayList<String>();
        String where = DatabaseProvider.KEY_MESSAGE_TYPE + " = ? AND "
                + DatabaseProvider.KEY_OBJECT_NAME + " = ? AND "
                + DatabaseProvider.KEY_FIELD_VALUE + " =?";

        Cursor cursor = mContext.getContentResolver()
                .query(DatabaseProvider.CONTENT_URI, new String[]{DatabaseProvider.KEY_ID, DatabaseProvider.KEY_MSGID}, where,
                        new String[]{DatabaseProvider.MSG_TYPE_MARKET_TRACKER, "Message", message_Subject}, null);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                msgids.add(cursor.getString(1));
            }
        }
        if (cursor != null) cursor.close();

        if (msgids.size() > 0) {
            for (int i = 0; i < msgids.size(); i++) {

                String deletewhere = DatabaseProvider.KEY_MSGID + " =?";
                mContext.getContentResolver().delete(DatabaseProvider.CONTENT_URI, deletewhere, new String[]{msgids.get(i)});

            }

        }

    }


    public String getAppNameFromDispCol(int msgid, int i) {
        String appName = null;

        Cursor appNameCursor = mContext.getContentResolver().query(
                DatabaseProvider.CONTENT_URI,
                new String[]{DatabaseProvider.KEY_ID,
                        DatabaseProvider.KEY_APPLICATION},
                DatabaseProvider.KEY_MSGID + " =?  AND "
                        + DatabaseProvider.KEY_DISPLAY_ROW + "= ?",
                new String[]{String.valueOf(msgid), String.valueOf(i)}, null);

        if (appNameCursor != null && appNameCursor.getCount() > 0) {

            appNameCursor.moveToFirst();
            appName = appNameCursor.getString(1);
            appNameCursor.close();
        }

        //Log.d(TAG, "mApplicationName " + appName);
        if (appNameCursor != null) appNameCursor.close();
        return appName;

    }


    private boolean equal(String[] a1, String[] a2) {
        for (int i = 0; i < a1.length; ++i) {
            if (!a1[i].equals(a2[i])) {
                return false;
            }
        }
        return true;
    }

    private void assign(String[] to, String[] from) {
        for (int i = 0; i < to.length; ++i) {
            to[i] = from[i];
        }
    }


    public HashMap<String, ListItemObj> getListItemObjMap(int mMessageId) {

        HashMap<String, ListItemObj> ListItemObjMap = new HashMap<String, ListItemObj>();
         /*
          * populate Itemlist level variables firs
    	  */

        Cursor ListItemCursor = mContext.getContentResolver().query(
                DatabaseProvider.CONTENT_URI,
                new String[]{DatabaseProvider.KEY_ID,
                        DatabaseProvider.KEY_FIELD_NAME,
                        DatabaseProvider.KEY_FIELD_DISPLAYTEXT,
                        DatabaseProvider.KEY_FIELD_VALUE,
                        DatabaseProvider.KEY_PARENT_ID_CONTEXT
                },
                DatabaseProvider.KEY_MSGID + " =? AND "
                        + DatabaseProvider.KEY_APPLICATION
                        + " =? AND "
                        + DatabaseProvider.KEY_OBJECT_NAME
                        + " =?  ",
                new String[]{String.valueOf(mMessageId), "ListItems", "ListItem"}, null);

        //   Log.d(TAG, "ListItemCursor" + ListItemCursor);
        //   Log.d(TAG, "ListItemCursor count" + ListItemCursor.getCount());
        if (ListItemCursor != null && ListItemCursor.getCount() > 0) {
            int i = 0;
            String ListItemName = "";
            //ListItemName=ListItemCursor.getString(2);
            //	Log.d(TAG,"ListItemName outside loop="+ListItemName);
            /*
             * This will retreive only the listitems.  Each listitme
			 * has 4 attributes saved as rows
			 */

            while (ListItemCursor.moveToNext()) {
                //	Log.d (TAG, "listitemvalue ="+ListItemCursor.getString(2));
                switch (i % 4) {
                    case 0:
                        ListItemObj listitemobj = new ListItemObj();
                        listitemobj.setName(ListItemCursor.getString(2));
                        ListItemName = ListItemCursor.getString(2);
                        ListItemObjMap.put(listitemobj.getName(), listitemobj);
                        //     Log.d(TAG, "ListItemName=" + ListItemName);
                        break;

                    case 1:
                        ListItemObjMap.get(ListItemName).setScrollDirection(ListItemCursor.getString(2));
                        break;
                    case 2:
                        ListItemObjMap.get(ListItemName).setOnTouch(ListItemCursor.getString(2));
                        break;
                    case 3:
                        ListItemObjMap.get(ListItemName).setCommonTag(ListItemCursor.getString(2));
                        break;
                }
                //  Log.d(TAG, "ListItemObjMap size = " + ListItemObjMap.size());
                //  Log.d(TAG, "ListItemObj size = " + ListItemObjMap.get(ListItemName).getName());
                i++;
            }
            if (ListItemCursor != null) ListItemCursor.close();
        }

        Cursor ItemCursor = mContext.getContentResolver().query(
                DatabaseProvider.CONTENT_URI,
                new String[]{DatabaseProvider.KEY_ID,
                        DatabaseProvider.KEY_FIELD_NAME,
                        DatabaseProvider.KEY_FIELD_DISPLAYTEXT,
                        DatabaseProvider.KEY_FIELD_VALUE,
                        DatabaseProvider.KEY_PARENT_ID_CONTEXT
                },
                DatabaseProvider.KEY_MSGID + " =? AND "
                        + DatabaseProvider.KEY_APPLICATION
                        + " =? AND "
                        + DatabaseProvider.KEY_OBJECT_NAME
                        + " =?  ",
                new String[]{String.valueOf(mMessageId), "ListItems", "Item"}, null);
        if (ItemCursor != null && ItemCursor.getCount() > 0) {
            int i = 0;
            int index = 0;
            String ListItemName = "";
            String PrevListItemName = "";

            while (ItemCursor.moveToNext()) {
                int switchval = i % 11;
                //   Log.d(TAG, "switchval" + switchval);
                switch (switchval) {
                    case 0:
                        ListItemName = ItemCursor.getString(4);
                        if (!ListItemName.equals(PrevListItemName)) {
                            i = 0;
                            PrevListItemName = ListItemName;
                        }
                        index = i / 11;
                        ListItem listitem = new ListItem();
                        if (ItemCursor.getString(2) != null)
                            listitem.setValue(ItemCursor.getString(2));
                        ListItemObjMap.get(ListItemName).ListItemArray.add(listitem);
                        break;
                    case 1:    //Log.d(TAG,"value written in case 1 with index0 before next write=" +ListItemObjMap.get(ListItemName).ListItemArray.get(0).getValue());
                        ListItemObjMap.get(ListItemName).ListItemArray.get(index).setdisplayText(ItemCursor.getString(2));
                        break;
                    case 2:
                        ListItemObjMap.get(ListItemName).ListItemArray.get(index).setImage(ItemCursor.getString(2));
                        break;
                    case 3:
                        ListItemObjMap.get(ListItemName).ListItemArray.get(index).setSubscript(ItemCursor.getString(2));
                        break;
                    case 4:
                        ListItemObjMap.get(ListItemName).ListItemArray.get(index).setBackgroundColor(ItemCursor.getString(2));
                        break;
                    case 5:
                        ListItemObjMap.get(ListItemName).ListItemArray.get(index).setDescription(ItemCursor.getString(2));
                        break;
                    case 6:
                        ListItemObjMap.get(ListItemName).ListItemArray.get(index).setId(ItemCursor.getString(2));
                        break;
                    case 7:
                        ListItemObjMap.get(ListItemName).ListItemArray.get(index).setOpenUrl(ItemCursor.getString(2));
                        break;
                    case 8:
                        break;
                    case 9:
                        break;
                    case 10:
                        break;

                    default:
                        break;
                }
            }
            if (ItemCursor != null) ItemCursor.close();
        }

        return ListItemObjMap;
    }
}
