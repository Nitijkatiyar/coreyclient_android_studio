package com.coremobile.coreyhealth;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.coremobile.coreyhealth.newui.Fields;
import com.coremobile.coreyhealth.newui.Row;
import com.coremobile.coreyhealth.newui.RowDisplayObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MsgDataCache {
    private static final String TAG = "Corey_MsgDataCache";

    // map:appName -> (map:key -> list-of-values)
    // where key,value are extracted from message table.
    // Keys are determined by main and subscript text defined for the
    // applications involved (which is captured in ApplicationData)
    private HashMap<String, HashMap<String, ArrayList<String>>> mCache;

    private HashMap<String, RowDisplayObject> mObjIndex = new HashMap<String, RowDisplayObject>();

    private HashMap<String, HashMap<String, ArrayList<String>>> mSalesCache;

    private Activity mActivity;

    public ArrayList<Row> mRows;

    private int messageid;

    public HashMap<String, String> mImageSet = new HashMap<String, String>();

    public MsgDataCache(Activity activity) {
        mCache = new HashMap<String, HashMap<String, ArrayList<String>>>();
        mActivity = activity;

    }

    public void populateData(int msgId, String msgType) {
        // Which arraylist to be returned, currently I am returning String , we
        // need to return hash map of class objects
        mRows = new ArrayList<Row>();
        messageid = msgId;
        //Log.d("Ramesh_MsgDataCache", "I am called msgId " + msgId);
        //Log.d(TAG, "population  started");
        for (int mDisplayRow = 1; mDisplayRow <= 4; mDisplayRow++) {
            Row DisplayRow = new Row();
            //Log.d(TAG, "query started");
            Cursor objectCursor = mActivity.getContentResolver().query(
                    DatabaseProvider.CONTENT_URI,
                    new String[]{DatabaseProvider.KEY_ID,
                            DatabaseProvider.KEY_OBJECT_DISPLAYTEXT,
                            DatabaseProvider.KEY_OBJECT_ID,
                            DatabaseProvider.KEY_ISEDITABLE,
                            DatabaseProvider.KEY_ISPARENT_CONTEXT,
                            DatabaseProvider.KEY_OBJECT_NAME,
                            DatabaseProvider.KEY_PARENT_ID_CONTEXT,
                            DatabaseProvider.KEY_FIELD_DISPLAYTEXT,
                            DatabaseProvider.KEY_FIELD_VALUE,
                            DatabaseProvider.KEY_APP_COMMONLOGO,
                            DatabaseProvider.KEY_DISPLAY_ICON,
                            DatabaseProvider.KEY_MSG_CONTEXT,
                            DatabaseProvider.KEY_APPLICATION,
                            DatabaseProvider.KEY_OPENURL,
                            DatabaseProvider.KEY_APPLICATION_TITLE,
                            DatabaseProvider.KEY_APP_STATUS,
                            DatabaseProvider.KEY_FIELD_ID,
                            DatabaseProvider.KEY_ISNEW,
                            DatabaseProvider.KEY_CONVERTTIME,
                            DatabaseProvider.KEY_APPLICATION_ID,
                            DatabaseProvider.KEY_LISTVALUE,
                            DatabaseProvider.KEY_EDITABLE_LISTVALUE,
                            DatabaseProvider.KEY_OBJECT_DISPLAY_SUBSCRIPT, //22
                            DatabaseProvider.KEY_FIELD_NAME,
                            DatabaseProvider.KEY_DISPLAY_COLOUMN,
                            DatabaseProvider.KEY_FUTURE_USE_4_CONTEXT,
                            DatabaseProvider.KEY_HASSTATUS,
                            DatabaseProvider.KEY_APP_GROUP_ID, //27 object attributes
                            DatabaseProvider.KEY_APP_SERVER_ID,
                            DatabaseProvider.KEY_DISPLAY_ROW,
                            DatabaseProvider.KEY_OBJECT_GraphType,
                            DatabaseProvider.KEY_OBJECT_GraphId,
                            DatabaseProvider.KEY_OBJECT_NeedTimeValue,
                            DatabaseProvider.KEY_FIELD_MULTISELECT,
                            DatabaseProvider.KEY_ISLOADING_CONTEXT
                    },
                    DatabaseProvider.KEY_MSGID + " =? AND "
                            + DatabaseProvider.KEY_DISP_QUADRANT_CONTEXT
                            + " =? ",
                    new String[]{String.valueOf(msgId),
                            String.valueOf(mDisplayRow)}, null);
            //Log.d(TAG, "query end");
            //   Log.d("Ramesh_MsgDataCache", "I am called objectCursor "
            //    + objectCursor.getCount());

            if (objectCursor != null && objectCursor.getCount() > 0) {
                //int preObjectId = -1;
                String preObjectId = "-1";
                while (objectCursor.moveToNext()) {
                    RowDisplayObject rowObjects = new RowDisplayObject();
                    //    int objID = Integer.valueOf(objectCursor.getString(2));
                    String objID = objectCursor.getString(2);
                    String parentObjID = objectCursor.getString(6);
                    if (parentObjID == null || parentObjID.isEmpty()) {
                        //	if (objID != preObjectId) {
                        if (!(objID.equals(preObjectId))) {
                            String displayName = objectCursor.getString(1);
                            rowObjects.setObjectid(objID);
                            rowObjects.setDisplayName(displayName);
                            rowObjects.setCursorPos(objectCursor.getPosition());
                            rowObjects.setEditable(Boolean.valueOf(objectCursor
                                    .getString(3)));
                            //    Log.d("MyLogs", " is editable msgdatacacahe "
                            //	    + objectCursor.getString(3));
                            rowObjects.setParent(Boolean.valueOf(objectCursor
                                    .getString(4)));
                            rowObjects.setObjectName(objectCursor.getString(5));
                            rowObjects.setMsgContext(objectCursor.getString(11));
                            rowObjects.setAppName(objectCursor.getString(12));
                            rowObjects.setOpenUrl(objectCursor.getString(13));
                            rowObjects.setAppTitle(objectCursor.getString(14));
                            rowObjects.setObjectStatus(objectCursor.getString(15));
                            rowObjects.setIsNew(objectCursor.getString(17));
                            rowObjects.setAppId(objectCursor.getString(19));
                            rowObjects.setDisplaySubscript(objectCursor.getString(22));
                            rowObjects.setsGraphType(objectCursor.getString(30));
                            rowObjects.setsGraphId(objectCursor.getString(31));
                            rowObjects.setLoading(Boolean.valueOf(objectCursor.getString(34)));
                            rowObjects.setsNeedTimeStamp(objectCursor.getString(32));
                            int dispcol;
                            int disprow;
                            int disppos;
                            String DispCol = objectCursor.getString(24);
                            if (DispCol != null) {
                                dispcol = Integer.parseInt(DispCol);
                                //  Log.d(TAG,"dispcol= "+dispcol);
                                rowObjects.setDispCol(dispcol);
                            } else {
                                rowObjects.setDispCol(0);
                                dispcol = 0;
                            }
                            String dispRow = objectCursor.getString(29);
                            if (dispRow != null) {
                                rowObjects.setDispRow(Integer.parseInt(dispRow));
                                disprow = Integer.parseInt(dispRow);
                            } else {
                                rowObjects.setDispRow(0);
                                disprow = 0;
                            }

                            //  Log.d(TAG, "displayrow =" +dispRow);
                            disppos = (disprow << 8) + dispcol;
                            //  Log.d(TAG, "disppos =" +disppos);
                            rowObjects.setDispPos(disppos);
                            rowObjects.setAssignedUserId(objectCursor.getString(25));
                            // 	Log.d(TAG,"Assigneduserid ="+objectCursor.getString(25));
                            String ObjAttr = objectCursor.getString(27);
                            //	Log.d(TAG, "ObjAttr"+ObjAttr);
                            if (ObjAttr != null) {
                                try {
                                    JSONObject ObjAttributes = new JSONObject(ObjAttr);
                                    if (ObjAttributes.has("ObjEditText"))
                                        rowObjects.setEditText(ObjAttributes.getString("ObjEditText"));

                                    if (ObjAttributes.has("NeedsTimeValue"))
                                        rowObjects.setsNeedTimeStamp(ObjAttributes.getString("NeedsTimeValue"));
                                    if (ObjAttributes.has("ObjName"))
                                        rowObjects.setObjectName(ObjAttributes.getString("ObjName"));
                                    if (ObjAttributes.has("DispFullImage"))
                                        rowObjects.setIsFullImage(ObjAttributes.getString("DispFullImage"));
                                    if (ObjAttributes.has("ObjMultiselecUi"))
                                        rowObjects.setMultiselecUi(ObjAttributes.getString("ObjMultiselecUi"));
                                    if (ObjAttributes.has("ObjListValue"))
                                        rowObjects.setListValue(ObjAttributes.getString("ObjListValue"));
                                    if (ObjAttributes.has("detailviewedittext"))
                                        rowObjects.setDetailViewABarBtn(ObjAttributes.getString("detailviewedittext"));
                                    if (ObjAttributes.has("objType"))
                                        rowObjects.setObjType(ObjAttributes.getString("objType"));
                                    if (ObjAttributes.has("objToType"))
                                        rowObjects.setObjToType(ObjAttributes.getString("objToType"));
                                    if (ObjAttributes.has("objToTypeDispVal"))
                                        rowObjects.setObjToTypeDispVal(ObjAttributes.getString("objToTypeDispVal"));
                                    //Log.d(TAG, "objType" + ObjAttributes.getString("objType"));
                                    //	Log.d(TAG, "ObjName"+ObjAttributes.getString("ObjName"));

                                } catch (JSONException e) {
                                    //	Log.d(TAG, "json exception for dashboard object");
                                    e.getMessage();
                                }
                            }

                            String url = objectCursor.getString(9);
                            if (url != null && !TextUtils.isEmpty(url)) {
                                rowObjects.setImageUrl(url.toString());

                            }
                            url = objectCursor.getString(10);
                            if (url != null && !TextUtils.isEmpty(url)) {
                                rowObjects.setImageUrl1(url.toString());

                            }
                            DisplayRow.add(rowObjects);
                            mObjIndex
                                    .put(objectCursor.getString(2), rowObjects);
                            preObjectId = objID;
                        }
                    }

                }

            }

            for (int i = 0; i < DisplayRow.size(); i++) {
                // 	Log.d(TAG, "forloop of populateData object name ="+DisplayRow.getObject(i).getDisplayName());
                populateObjects(DisplayRow.getObject(i), objectCursor);
            }
            mRows.add(DisplayRow);
            if (objectCursor != null) objectCursor.close();
        }
        // Following block only generate logs
        //Log.d(TAG, "population  Ended");
    }

    private void populateObjects(RowDisplayObject rowObjects2,
                                 Cursor objectCursor) {

        objectCursor.moveToPosition(-1);
        if (rowObjects2.isParent) {

            //    int preObjid = -1;
            String preObjid = "-1";
            //Log.d(TAG, "Inside Populate data object name =" + rowObjects2.getDisplayName());
            while (objectCursor.moveToNext()) {
                //	int parentobjid = rowObjects2.getObjectid();
                String parentobjid = rowObjects2.getObjectid();
                //	int objectid = Integer.valueOf(objectCursor.getString(2));
                String objectid = objectCursor.getString(2);
                if ((objectCursor.getString(6) == null) || (objectCursor.getString(6).isEmpty())) {
                    continue;
                }

                //	int Childparentobjectid = Integer.valueOf(objectCursor
                //		.getString(6));
                String Childparentobjectid = objectCursor.getString(6);
                //	Log.d(TAG, "Childparentobjectid="+Childparentobjectid);
                if (Childparentobjectid.equals(parentobjid)) {
                    //    if (objectid != preObjid) {
                    if (!objectid.equals(preObjid)) {
                        RowDisplayObject sndobject = new RowDisplayObject();
                        String displayName = objectCursor.getString(1);
                        sndobject.setDisplayName(displayName);
                        //	Log.d(TAG, "child object display name ="+displayName);
                        sndobject.setObjectid(objectid);
                        sndobject.setParent(Boolean.valueOf(objectCursor
                                .getString(4)));
                        sndobject.setLoading(Boolean.valueOf(objectCursor.getString(34)));
                        sndobject.setCursorPos(objectCursor.getPosition());
                        sndobject.setAppName(objectCursor.getString(12));
                        sndobject.setEditable(Boolean.valueOf(objectCursor
                                .getString(3)));
                        sndobject.setObjectName(objectCursor.getString(5));
                        sndobject.setMsgContext(objectCursor.getString(11));
                        sndobject.setOpenUrl(objectCursor.getString(13));
                        sndobject.setsNeedTimeStamp(objectCursor.getString(32));
                        sndobject.setObjectStatus(objectCursor.getString(15));
                        //Log.e("MsgDataCache>setObjectStatus", "" + objectCursor.getString(15));
                        sndobject.setIsNew(objectCursor.getString(17));
                        sndobject.setAppId(objectCursor.getString(19));
                        sndobject.setDisplaySubscript(objectCursor.getString(22));
                        sndobject.setAssignedUserId(objectCursor.getString(25));
                        //	Log.d(TAG,"Assigneduserid ="+objectCursor.getString(25));

                        int dispcol;
                        int disprow;
                        int disppos;

                        String DispCol = objectCursor.getString(24);
                        if (DispCol != null) {
                            dispcol = Integer.parseInt(DispCol);
                            //   Log.d(TAG,"dispcol= "+dispcol);
                            sndobject.setDispCol(dispcol);
                        } else {
                            sndobject.setDispCol(0);
                            dispcol = 0;
                        }


                        String ObjAttr = objectCursor.getString(27);
                        String dispRow = objectCursor.getString(29);

                        if (dispRow != null) {
                            sndobject.setDispRow(Integer.parseInt(dispRow));
                            disprow = Integer.parseInt(dispRow);
                        } else {
                            sndobject.setDispRow(0);
                            disprow = 0;
                        }

                        //   Log.d(TAG, "displayrow =" +dispRow);
                        disppos = (disprow << 8) + dispcol;
                        //   Log.d(TAG, "disppos =" +disppos);
                        sndobject.setDispPos(disppos);
                        //Log.d(TAG, "ObjAttr" + ObjAttr);
                        if (ObjAttr != null) {
                            try {
                                JSONObject sndObjAttributes = new JSONObject(ObjAttr);
                                if (sndObjAttributes.has("ObjEditText"))
                                    sndobject.setEditText(sndObjAttributes.getString("ObjEditText"));
                                if (sndObjAttributes.has("ObjName"))
                                    sndobject.setObjectName(sndObjAttributes.getString("ObjName"));
                                if (sndObjAttributes.has("NeedsTimeValue"))
                                    sndobject.setsNeedTimeStamp(sndObjAttributes.getString("NeedsTimeValue"));
                                if (sndObjAttributes.has("DispFullImage"))
                                    sndobject.setIsFullImage(sndObjAttributes.getString("DispFullImage"));
                                if (sndObjAttributes.has("ObjMultiselecUi"))
                                    sndobject.setMultiselecUi(sndObjAttributes.getString("ObjMultiselecUi"));
                                if (sndObjAttributes.has("ObjListValue"))
                                    sndobject.setListValue(sndObjAttributes.getString("ObjListValue"));
                                if (sndObjAttributes.has("detailviewedittext"))
                                    sndobject.setDetailViewABarBtn(sndObjAttributes.getString("detailviewedittext"));
                                if (sndObjAttributes.has("objType"))
                                    sndobject.setObjType(sndObjAttributes.getString("objType"));
                                if (sndObjAttributes.has("objToType"))
                                    sndobject.setObjToType(sndObjAttributes.getString("objToType"));
                                if (sndObjAttributes.has("objToTypeDispVal"))
                                    sndobject.setObjToTypeDispVal(sndObjAttributes.getString("objToTypeDispVal"));
                                //	Log.d(TAG, "ObjEditText"+ObjAttributes.getString("ObjEditText"));
                                //Log.d(TAG, "ObjName = " + sndObjAttributes.getString("ObjName"));
//                                if (sndObjAttributes.has("objType"))
                                //Log.d(TAG, "objType=  " + sndObjAttributes.getString("ObjMultiselecUi"));

                            } catch (JSONException e) {
                                //	Log.d(TAG, "json exception for secondary object");
                                e.getMessage();
                            }
                        }
                        String url = objectCursor.getString(9);
                        if (url != null) {
                            sndobject.setImageUrl(url);
                        }
                        url = objectCursor.getString(10);
                        if (url != null) {
                            sndobject.setImageUrl1(url);
                        }
                        preObjid = objectid;
                        rowObjects2.add(sndobject);
                        mObjIndex.put(objectCursor.getString(2), sndobject);
                    }
                }
            }

            for (RowDisplayObject rowDisplayObject : rowObjects2.dashboardobj) {

                populateObjects(rowDisplayObject, objectCursor);

            }

        } else {
            // objectCursor.moveToPosition(rowObjects2.getCursorPos()-1);
            objectCursor.moveToPosition(rowObjects2.getCursorPos());
            while (objectCursor.moveToNext()) {
                //	int objectid = Integer.valueOf(objectCursor.getString(2));
                String objectid = objectCursor.getString(2);
                if (rowObjects2.getObjectid().equals(objectid)) {
                    Fields myFields = new Fields();
                    //  myFields.setFieldName(objectCursor.getString(7));
                    myFields.setFieldName(objectCursor.getString(23));
                    myFields.setFieldValue(objectCursor.getString(8));
                    myFields.setEditable(Boolean.valueOf(objectCursor
                            .getString(3)));
                    myFields.setAppName(objectCursor.getString(12));
                    myFields.setMsgContext(objectCursor.getString(11));
                    myFields.setOpenUrl(objectCursor.getString(13));
                    //   myFields.setFieldDispText(objectCursor.getString(1));
                    myFields.setFieldDispText(objectCursor.getString(7));
//                    asaas
                    myFields.setsMultiSelect(objectCursor.getString(33));
                    myFields.setFieldStatus(objectCursor.getString(15));
                    myFields.setIsNew(objectCursor.getString(17));
                    myFields.setConvertTime(objectCursor.getString(18));
                    myFields.setFieldId(objectCursor.getString(16));
                    myFields.setlistValue(objectCursor.getString(20));
                    myFields.seteditableListValue(objectCursor.getString(21));
                    myFields.setHasStatus(objectCursor.getString(26));
                    //   Log.d(TAG, "hasstatus="+objectCursor.getString(26));
                    String FieldAttr = objectCursor.getString(28);
                    //  Log.d(TAG, "Fielddisptext"+objectCursor.getString(7));
                    //	Log.d(TAG, "FieldAttr"+FieldAttr);
                    if (FieldAttr != null) {
                        try {
                            JSONObject FldAttributes = new JSONObject(FieldAttr);
                            if (FldAttributes.has("FldType"))
                                myFields.setFieldType(FldAttributes.getString("FldType"));
                            if (FldAttributes.has("EmailBody"))
                                myFields.setEmailBody(FldAttributes.getString("EmailBody"));
                            if (FldAttributes.has("EmailSubject"))
                                myFields.setEmailSubject(FldAttributes.getString("EmailSubject"));

                            String url1 = null;
                            if (FldAttributes.has("imageURL"))
                                url1 = FldAttributes.getString("imageURL");
                            if (url1 != null && !TextUtils.isEmpty(url1)) {
                                Uri uri = Uri.parse(url1);
                               // url1 = uri.getLastPathSegment();
                                myFields.setImageUrl(url1.toString());
                                url1 = null;
                            }

                            //	Log.d(TAG, "FldType"+ObjAttributes.getString("FldType"));

                        } catch (JSONException e) {
                            //	Log.d(TAG, "json exception for field");
                            e.getMessage();
                        }
                    }
                    //  Log.d("msgdatacache","Fieldobject Fieldid =" +objectCursor.getString(16));
                    rowObjects2.addField(myFields);
                    //    Log.d("msgdatacache","Fieldname ="+myFields.getFieldName());
                    //    Log.d("msgdatacache","Fieldvalue ="+myFields.getFieldValue());
                } else
                    break;
            }

        }

    }

    public RowDisplayObject getObject(String ObjectID) {
        final RowDisplayObject rdobject = mObjIndex.get(ObjectID);
        return (rdobject);
    }


    public void initialize(int msgId, ArrayList<ApplicationData> allAppsData) {
        mCache.clear();

        for (ApplicationData appData : allAppsData) {
            HashMap<String, ArrayList<String>> appCache = new HashMap<String, ArrayList<String>>();
            appCache.put("APPLICATION_ID", new ArrayList<String>());
            mCache.put(appData.mApplicationName.toUpperCase(), appCache);
        }

        String[] projection = new String[]{
                "UPPER(" + DatabaseProvider.KEY_APPLICATION + ")",
                DatabaseProvider.KEY_APPLICATION_ID,
                "UPPER(" + DatabaseProvider.KEY_OBJECT_NAME
                        + ") || '/' || UPPER("
                        + DatabaseProvider.KEY_FIELD_NAME + ") as KEY",
                DatabaseProvider.KEY_FIELD_VALUE,
                DatabaseProvider.KEY_FIELD_DISPLAYTEXT};
        String selection = _constructSelect(msgId, allAppsData);

        //Log.i(TAG, "initialize: projection: " + Arrays.toString(projection));
        //Log.i(TAG, "initialize: selection: " + selection);

        Cursor cursor = mActivity.getContentResolver()
                .query(DatabaseProvider.CONTENT_URI, projection, selection,
                        null, null);

        // mActivity.startManagingCursor(cursor);
        int prevAppId = -1;
        while (cursor.moveToNext()) {
            String application = cursor.getString(0);
            int appId = cursor.getInt(1);

            String key = cursor.getString(2);
            String value = cursor.getString(3);
            HashMap<String, ArrayList<String>> appCache = mCache
                    .get(application);
            if (!appCache.containsKey(key)) {
                appCache.put(key, new ArrayList<String>());
            }
            //    Log.d(TAG, "appId=" + appId);
            //    Log.d(TAG, "prevAppId=" + prevAppId);
            appCache.get(key).add(value);
            if (prevAppId != appId) {
                appCache.get("APPLICATION_ID").add(Integer.toString(appId));
                // Log.d(TAG, "AppCache appId=" +
                // appCache.get("APPLICATION_ID").add(Integer.toString(appId))
                // );
            }
            prevAppId = appId;
        }
    }

    public String getValue(String appName, String key, int at) {

        ArrayList<String> values = mCache.get(appName.toUpperCase()).get(
                key.toUpperCase());
        return (values != null && values.size() > at) ? values.get(at) : null;
        // return (values.get(at));
    }

    // ////////// PRIVATE /////////////

    private String _constructSelect(int msgId,
                                    ArrayList<ApplicationData> allAppsData) {
        String select = null;
        for (ApplicationData appData : allAppsData) {
            ArrayList<String> variables = new ArrayList<String>();
            boolean rolling = appData.isRolling();
            for (RowData rdata : appData.rowData) {
                _extractVariables(rdata.mMaintextText, variables);
                _extractVariables(rdata.mSubscriptText, variables);
                if (rolling) {
                    // for rolling data (repeating rows)
                    // just first RowData is enough
                    break;
                }
            }
            if (variables.size() > 0) {
                if (select == null) {
                    select = "";
                } else {
                    select = select + " OR ";
                }

                select = select
                        + "("
                        + "UPPER("
                        + DatabaseProvider.KEY_APPLICATION
                        + ") = '"
                        + appData.mApplicationName.toUpperCase()
                        + "' AND "
                        + "KEY IN "
                        + variables.toString().replace('[', '(')
                        .replace(']', ')') + ")";
            }
        }

        if (select != null) {
            select = DatabaseProvider.KEY_MSGID + "=" + msgId + " AND ("
                    + select + ")";
        }

        return select;
    }

    private void _extractVariables(String template, ArrayList<String> variables) {
        String[] fields = template.split("[\\[\\]]");

        boolean isVariable = false;
        for (String f : fields) {
            if (isVariable) {
                String ucf = "'" + f.toUpperCase() + "'";
                if (!variables.contains(ucf)) {
                    variables.add(ucf);
                }
            }
            isVariable = !isVariable;
        }
    }

    public int getMsgId() {
        return messageid;
    }

    public String getRow0ObjectId() {
        //	String row0ObjectId = mRows.get(0).getObject(0).getObjectId();
        //return row0ObjectId;
        /*Fix for COREY-2253: Crash happens due to race condition
         * When the user swipes msgdatacache is instantiated.
		 * At the same time a click(without user knowledge) is 
		 * recognized and it tries get the object id which may not have
		 * been created yet.  So this function will return null in that case
		 * The user of this function should check null and take appropriate action
		 * Testfragment use this.
		 */

        if (mRows.get(0) != null && mRows.get(0).size() > 0) {
            if (mRows.get(0).getObject(0) != null) {
                return (mRows.get(0).getObject(0).getObjectId());
            } else return null;
        } else return null;
    }
}
