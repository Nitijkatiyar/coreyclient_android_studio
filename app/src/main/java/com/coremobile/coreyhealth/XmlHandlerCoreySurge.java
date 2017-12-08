package com.coremobile.coreyhealth;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import com.coremobile.coreyhealth.newui.MsgByCtxt;
import com.coremobile.coreyhealth.newui.MsgByUsr;
import com.coremobile.coreyhealth.newui.OneMsg;
import com.coremobile.coreyhealth.progressdb.ProgressInfoDBControl;
import com.coremobile.coreyhealth.progressdb.ProgressObjectModel;

import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class XmlHandlerCoreySurge extends DefaultHandler {

    private static final String TAG = "Corey_XmlHandlerCoreySurge";
    private boolean in_messages = false;
    private boolean in_field = false;
    private boolean is_auth_xml = false;
    private boolean in_login = false;
    private boolean in_password = false;
    private boolean in_context = false;
    private boolean foundPlanAheadMsgs = false;
    private boolean foundMarketTrackerMsgs = false;
    private String Application_isEditable = "";
    private String Application_ishasStatus = "false";
    private String Application_AddField = "";
    private String ApplicationFIELD_ID = "";
    private boolean foundtododMsgs = false;

    private String Message_ID = "";
    private String Message_Purged = "";
    private String Message_Subject = "";
    private String Message_Time = "";
    private String Meeting_StartTime = "";
    private String Meeting_EndTime = "";
    private String Meeting_allTime = "";
    private String Object_Name = "";
    private String Object_Status = "";
    private String Message_FieldName = "";
    private String Message_FieldValue = "";
    private String Application_Text = "";
    private String Application_Title = "";
    private String Application_LogoUrl = "";
    private String Application_status = "";
    private String Application_GroupId = null;
    private String Application_ServerId = null;
    private String Object_DisplayText = "";
    private boolean IsLoading = false;
    private String Object_NeedsTimeValue = "";
    private String Object_GraphType = "";
    private String Object_GraphID = "";
    private String Field_DisplayText = "";
    private String Field_MultiSelect = "";
    private String Field_ImageURL = "";
    private String Field_DisplayBox = "";
    private String Message_Type = "";
    private int Application_ID = 0;
    // private int Object_ID = 10;
    private String Object_ID = "";
    String ApplicationName = "";
    private String IsNew = "";
    private String Object_Is_New = "";
    private String Object_AssignedUserId;
    private String OpenUrl = "";
    private String convertTime = "";
    private String MSG_Context = "";
    private boolean IsParent = false;
    private String parentId = "";
    private String Display_Quadrant;
    private String imageurl1 = null;
    private String Object_MultiselecUi;
    private String Object_ListValue;
    private String Object_fullImage;
    private String detailviewedittext;
    private String objType = "";
    private String objectStatus = "";
    private String objToType = "";
    private String objToTypeDispVal = "";

    private String FieldEmailBody;
    private String FieldEmailSubject;

    /*
     * List item variables 
     */
    private String mListName = "";
    private String mListScrollDir = "";
    private String mListOnTouch = "";
    private String mListCommonTag = "";
    private String mListnameID = "";
    private String mListImage = "";

    private String mListItemValue = "";
    private String mdisplayText = "";
    private String mListItemImage = "";
    private String mListItemSubscript = "";
    private String mListItemBkgColour = "";
    private String mListItemDescription = "";
    private String mListItemId = "";
    private String mListItemOpenUrl;
    private String mListItemadd1;
    private String mListItemSelected;
    private String mListItemadd2;
    private String mListItemadd3;


    private String ProgressObjectId = "";
    private String ProgressStageText = "";
    private String ProgressStageValue = "";
    private String ProgressStageColor = "";

    String Login = "";
//    String Password = "";
    String Context = "";

    // private int index = 0;
    private Context mContext;
    SharedPreferences mcurrentUserPref;
    public static final String CURRENT_USER = "CurrentUser";
    SharedPreferences.Editor editor;
    ArrayList<ContentProviderOperation> insert;
    ArrayList<ContentValues> valuesList;
    ArrayList<String> whereList;
    private CoreyDBHelper mCoreyDBHelper;
    private String Note_Update_Time;
    private String App_Display_Row;
    private String App_DIsplay_Col;
    private boolean isDebug = true;
    private String Display_Subscript;
    private String listValue;
    private String editableListValue;
    private String isAddField;
    private String addtype;
    private String FieldType;
    private int ReplaceType;  //0--> No action , 1,2,3,4 --> replace all message, message, application,object,field
  /*  
    static final int REPLACE_NONE=0;
    static final int REPLACE_MSGS =1;
    static final int REPLACE_SINGLE_MSG =2;
    static final int REPLACE_APPLICATION =3;
    static final int REPLACE_OBJECT =4;
    static final int REPLACE_FIELD =5; */

    Boolean ReplaceNone = false;
    Boolean ReplaceMessages = false;
    Boolean ReplaceSingleMessage = false;
    Boolean ReplaceApplication = false;
    Boolean ReplaceObject = false;
    Boolean ReplaceField = false;
    Boolean ReplaceListItems = false;
    Boolean ReplaceSingleListItem = false;
    Boolean ReplaceItem = false;
    Boolean ReplaceNode = false;
    /*
     * One2one messaages variables
     */
    private String MsgContext;
    private Boolean InOne2OneMsg = false;
    private String MsgContextId;
    private String UsrName;
    private String UsrId;
    private String OneMsgId;
    private String TmeStamp;
    private String MsgTo;
    private String MsgFrom;
    private String One2OneMsg = "";
    private String SignedUsr;

    HashMap<String, MsgByCtxt> allmsgsmap;
    MsgByCtxt msgbyctxt;
    MsgByUsr msgbyusr;
    OneMsg onemsg;

    private Boolean InStaffLst = false;
    private String StaffName;
    private String StaffId;
    HashMap<String, String> stafflist;
    private MyApplication application;

    JSONObject jsonAttrField;
    JSONObject jsonAttrObject;
    private String ObjAttributes;
    private String FieldAttributes;

    private String ObjectTitle = null;
    ProgressInfoDBControl progressInfoDBControl;


    Boolean IsMessageUpdated = false;

    public XmlHandlerCoreySurge(Context context) {
        this.mContext = context;
        mcurrentUserPref = mContext.getSharedPreferences(CURRENT_USER, 0);
        editor = mcurrentUserPref.edit();
        SignedUsr = mcurrentUserPref.getString("Username", null);
        insert = new ArrayList<ContentProviderOperation>();
        valuesList = new ArrayList<ContentValues>();
        whereList = new ArrayList<String>();
        mCoreyDBHelper = new CoreyDBHelper(mContext);
        progressInfoDBControl = new ProgressInfoDBControl(mContext);
        MyApplication.INSTANCE.ImagedownloadCount = 0;

    }

    @Override
    public void startDocument() throws SAXException {
    }

    @Override
    public void endDocument() throws SAXException {
    }

    /**
     * Gets be called on opening tags like: <tag> Can provide attribute(s), when
     * xml was like: <tag attribute="attributeValue">
     */
    @Override
    public void startElement(String namespaceURI, String localName,
                             String qName, Attributes atts) throws SAXException {
//        Log.e("XMLPARSER", "..." + localName);
        if (localName.equalsIgnoreCase("Messages")) {
            String Message_Count = atts.getValue("count");
            if ((atts.getValue("Replace")) != null) {
                if ((atts.getValue("Replace")).equals("true")) {
                    ReplaceMessages = true;
                    ReplaceNode = true;
//                    Log.i("TAG", "Replace all Messages");
                } else ReplaceNode = false;
            }
//            Log.i("TAG", "startElement Messages");
//            Log.i("TAG", "message count: " + Message_Count);

            this.in_messages = true;
            this.foundPlanAheadMsgs = false;
            this.foundtododMsgs = false;
        } else if (localName.equalsIgnoreCase("Message")) {
            // Log.i("TAG", "startElement Message");
            MSG_Context = atts.getValue("context");
            if (ReplaceNode == false && ((atts.getValue("Replace")) != null)) {
                if ((atts.getValue("Replace")).equals("true")) {
                    whereList.add(mCoreyDBHelper.makeWhereStringbyContext(MSG_Context, null, null, null));
                    ReplaceSingleMessage = true;
                    ReplaceNode = true;
                    IsMessageUpdated = true;
//                    Log.i("TAG", "Replace single Messages, context=" + MSG_Context);
                }
            }
        /*
        if(ReplaceType==0)
		{
		if((atts.getValue("Replace")).equals("true"))
			{
			ReplaceType=2;
			//add statement for deleting the existing message here(
			whereList.add(mCoreyDBHelper.makeWhereStringbyContext(MSG_Context,null,null,null));
			}
	    	else ReplaceType=0;
		}
		*/
            Application_ID = 0;
            // Message_ID = atts.getValue("id");
            // Log.i(TAG, "id: " + Message_ID);
        /*
         * If all messages to be replaced(old pull api) existing message in db will have
		 * old message id with same context which is not desired
		 */
            if (!ReplaceMessages) {
                Message_ID = mCoreyDBHelper.getMsgIdbyContext(MSG_Context, "instant_gratification");
                if (Message_ID == null) Message_ID = atts.getValue("id");
            } else Message_ID = atts.getValue("id");

//            Log.e("MessageId....",""+Message_ID);

            Message_Purged = atts.getValue("Purged");

            // Log.i("TAG", "purged: " + Message_Purged);

            Message_Subject = atts.getValue("subject");

            MSG_Context = atts.getValue("context");

            Message_Time = atts.getValue("time");

            if (Message_Time == null)
                Message_Time = "";
            Meeting_StartTime = atts.getValue("MeetingStart");
            if (Meeting_StartTime == null)
                Meeting_StartTime = "";
            Meeting_EndTime = atts.getValue("MeetingEnd");
            if (Meeting_EndTime == null)
                Meeting_EndTime = "";
            Meeting_allTime = formatMeeting(Meeting_StartTime, Meeting_EndTime);
            Message_Type = atts.getValue("Type");
            if (Message_Type == null)
                Message_Type = "message";

            if (Message_Subject == null)
                Message_Subject = "Subject for Msg " + Message_ID;

            if (Message_Type.equals(DatabaseProvider.MSG_TYPE_MARKET_TRACKER)) {
                mCoreyDBHelper.removeOldMessage(Message_Subject);
            }
        /*
         * at this level ReplaceType=0 means neither messages nor message need to be replaced and
	     * only application, object etc need to be replaced. So skip adding to db
	     */
            if (ReplaceNode) {
                valuesList.add(mCoreyDBHelper.makeMessageContentValues(Message_ID,
                        0, null, null, null, null, "Message", null, null, "Purged",
                        null, Message_Purged, false, Message_Type, null, null,
                        "false", null, null, null, null, null, null, null, null,
                        null, MSG_Context, false, null, null, null, null, null, null, null, null, null, null, null, null, null, false));
                valuesList.add(mCoreyDBHelper.makeMessageContentValues(Message_ID,
                        0, null, null, null, null, "Message", null, null, "Subject",
                        null, Message_Subject, false, Message_Type, null, null,
                        "false", null, null, null, null, null, null, null, null,
                        null, MSG_Context, false, null, null, null, null, null, null, null, null, null, null, null, null, null, false));
                valuesList.add(mCoreyDBHelper.makeMessageContentValues(Message_ID,
                        0, null, null, null, null, "Message", null, null, "Time",
                        null, Message_Time, false, Message_Type, null, null,
                        "false", null, null, null, null, null, null, null, null,
                        null, MSG_Context, false, null, null, null, null, null, null, null, null, null, null, null, null, null, false));
                valuesList.add(mCoreyDBHelper.makeMessageContentValues(Message_ID,
                        0, null, null, null, null, "Message", null, null,
                        "MeetingStart", null, Meeting_StartTime, false,
                        Message_Type, null, null, "false", null, null, null, null,
                        null, null, null, null, null, MSG_Context, false, null,
                        null, null, null, null, null, null, null, null, null, null, null, null, false));
                valuesList.add(mCoreyDBHelper.makeMessageContentValues(Message_ID,
                        0, null, null, null, null, "Message", null, null,
                        "MeetingEnd", null, Meeting_EndTime, false, Message_Type,
                        null, null, "false", null, null, null, null, null, null,
                        null, null, null, MSG_Context, false, null, null, null,
                        null, null, null, null, null, null, null, null, null, null, false));
                valuesList.add(mCoreyDBHelper.makeMessageContentValues(Message_ID,
                        0, null, null, null, null, "Message", null, null,
                        "AllMeetingTime", null, Meeting_allTime, false,
                        Message_Type, null, null, "false", null, null, null, null,
                        null, null, null, null, null, MSG_Context, false, null,
                        null, null, null, null, null, null, null, null, null, null, null, null, false));
                valuesList.add(mCoreyDBHelper.makeMessageContentValues(Message_ID,
                        0, null, null, null, null, "Message", null, null,
                        "MessageType", null, Message_Type, false, Message_Type,
                        null, null, "false", null, null, null, null, null, null,
                        null, null, null, MSG_Context, false, null, null, null,
                        null, null, null, null, null, null, null, null, null, null, false));
            } else {
                //find the messageid and replace here
            }
            if (!foundPlanAheadMsgs
                    && Message_Type
                    .equals(DatabaseProvider.MSG_TYPE_INSTANT_GRATIFICATION)) {
                foundPlanAheadMsgs = true;
            }
            if (!foundtododMsgs
                    && Message_Type.equals(DatabaseProvider.MSG_TYPE_TODO)) {
                foundtododMsgs = true;
            }

            if (!foundMarketTrackerMsgs
                    && Message_Type
                    .equals(DatabaseProvider.MSG_TYPE_MARKET_TRACKER)) {
                foundMarketTrackerMsgs = true;
            }
//            Log.d("TAG", "Inserted Message " + Message_ID + ": "
//                    + Message_Subject);
        } else if (localName.equalsIgnoreCase("Application")) {
            // Log.i("TAG","startElement Application");
            if (in_messages) {
                if (atts.getLength() > 0) {
                    //  Object_ID = 10;
                    Object_ID = "";
                    Application_Text = atts.getValue("name");
                    Application_Title = atts.getValue("title");
                } else {
                    Application_Text = "";
                }
                if (ReplaceNode == false && ((atts.getValue("Replace")) != null)) {
                    if ((atts.getValue("Replace")).equals("true")) {
                        whereList.add(mCoreyDBHelper.makeWhereStringbyContext(MSG_Context, Application_Text, null, null));
                        ReplaceApplication = true;
                        ReplaceNode = true;
                        IsMessageUpdated = true;
//                        Log.i("TAG", "Replace single Messages, app title=" + Application_Title);
                    }
                }
        /*
        if(ReplaceType==0)
		{
		if((atts.getValue("Replace")).equals("true"))
			{
			ReplaceType=3;
			//add statement for deleting the existing message here(
			whereList.add(mCoreyDBHelper.makeWhereStringbyContext(MSG_Context,Application_Text,null,null));
			}
	    	else ReplaceType=0;
		 Message_ID=mCoreyDBHelper.getMsgIdbyContext(MSG_Context);
		    if(Message_ID==null)
		    	{
		    	Message_ID = atts.getValue("id");
		    	Log.d(TAG, "MessageID does not exist, will not work");
		    	}
		}
*/
            } else {
                ApplicationName = atts.getValue("Name");
                Application_Title = atts.getValue("title");

                editor.putString("ApplicationName", ApplicationName);
                is_auth_xml = true;
            }
//            Log.i("TAG", "startElement application = " + ApplicationName);
            Display_Quadrant = atts.getValue("DisplayQuadrant");

        } else if (localName.equalsIgnoreCase("Object")) {
            //Log.i("TAG","startElement Object");
            Object_Status = atts.getValue("objectStatus");
            Object_Name = atts.getValue("name");
            Object_NeedsTimeValue = atts.getValue("NeedsTimeValue");
//            Log.e("Object_NeedsTimeValue",""+Object_NeedsTimeValue);
            Object_DisplayText = atts.getValue("displayText");

            Object_ID = atts.getValue("ID");
            if (atts.getValue("GraphType") != null) {
                Object_GraphType = atts.getValue("GraphType");
            } else {
                Object_GraphType = "";
            }
            if (atts.getValue("Graphid") != null) {
                Object_GraphID = atts.getValue("Graphid");
            } else {
                Object_GraphID = "";
            }

            Object_MultiselecUi = atts.getValue("MultiSelectUI");
            Object_fullImage = atts.getValue("displayFullImage");
//            Log.d(TAG, "Object_MultiselecUi= " + Object_MultiselecUi);
            Object_ListValue = atts.getValue("listValue");
//            Log.d(TAG, "ObjectTitle before parsing =" + ObjectTitle);
            ObjectTitle = atts.getValue("Title");
            parentId = atts.getValue("parentObjectID");
            detailviewedittext = atts.getValue("detailviewedittext");
            if (atts.getValue("isParent") != null)
                IsParent = Boolean.valueOf(atts.getValue("isParent"));
            else IsParent = false;

            if (atts.getValue("IsLoading") != null)
                IsLoading = Boolean.valueOf(atts.getValue("IsLoading"));
            else IsLoading = false;


//            if (ObjectTitle != null) Log.d(TAG, "ObjectTitle length =" + ObjectTitle.length());
//            else Log.d(TAG, "ObjectTitle  =" + ObjectTitle);


            objType = atts.getValue("Type");

            objToType = atts.getValue("ToType");
            objToTypeDispVal = atts.getValue("ToTypeDispVal");
            try {
                jsonAttrObject = new JSONObject();

                if (ObjectTitle != null) jsonAttrObject.accumulate("ObjEditText", ObjectTitle);

                if (Object_Name != null) jsonAttrObject.accumulate("ObjName", Object_Name);
                if (Object_DisplayText != null)
                    jsonAttrObject.accumulate("ObjDispText", Object_DisplayText);

                if (Object_NeedsTimeValue != null)
                    jsonAttrObject.accumulate("NeedsTimeValue", Object_NeedsTimeValue);
                if (Object_ID != null) jsonAttrObject.accumulate("ObjId", Object_ID);
                if (Object_GraphID != null)
                    jsonAttrObject.accumulate("ObjGraphId", Object_GraphID);
                if (Object_GraphType != null)
                    jsonAttrObject.accumulate("ObjGraphType", Object_GraphType);


                if (Object_MultiselecUi != null)
                    jsonAttrObject.accumulate("ObjMultiselecUi", Object_MultiselecUi);
                if (Object_ListValue != null)
                    jsonAttrObject.accumulate("ObjListValue", Object_ListValue);
                if (Object_fullImage != null)
                    jsonAttrObject.accumulate("DispFullImage", Object_fullImage);
                if (detailviewedittext != null)
                    jsonAttrObject.accumulate("detailviewedittext", detailviewedittext);
                if (objType != null) jsonAttrObject.accumulate("objType", objType);
                if (objToType != null) jsonAttrObject.accumulate("objToType", objToType);
                if (objToTypeDispVal != null)
                    jsonAttrObject.accumulate("objToTypeDispVal", objToTypeDispVal);
                if (objectStatus != null) {
                    jsonAttrObject.accumulate("objectStatus", objectStatus);
                }
            /* Log.d(TAG, "ObjEditText ="+jsonAttrObject.getString("ObjEditText"));
             Log.d(TAG, "ObjDispText ="+jsonAttrObject.getString("ObjDispText"));
	    	 Log.d(TAG, "ObjName ="+jsonAttrObject.getString("ObjName")); */
                ObjAttributes = jsonAttrObject.toString();
            } catch (JSONException e) {
                e.getMessage();
            }

//            Log.i("TAG", "startElement Object = " + Object_Name);
//            Log.i("TAG", "startElement Object disp text = " + Object_DisplayText);
            if (ReplaceNode == false && ((atts.getValue("Replace")) != null)) {
                if ((atts.getValue("Replace")).equals("true")) {
                    //	whereList.add(mCoreyDBHelper.makeWhereStringbyContext(MSG_Context,Application_Text,Object_DisplayText,null));

                    whereList.add(mCoreyDBHelper.makeWherebyContextObjectId(MSG_Context, Application_Text, Object_ID, null));
                    if (IsParent)
                        whereList.add(mCoreyDBHelper.makeWherebyContextParentObjectId(MSG_Context, Application_Text, Object_ID, null));
                    ReplaceObject = true;
                    ReplaceNode = true;
                    IsMessageUpdated = true;
//                    Log.i("TAG", "Replace single Messages, Object_Namee=" + Object_Name);
                }
            }
        /*
        if(ReplaceType==0)
		{
		if((atts.getValue("Replace")).equals("true"))
			{
			ReplaceType=4;
			//add statement for deleting the existing message here(
			whereList.add(mCoreyDBHelper.makeWhereStringbyContext(MSG_Context,Application_Text,Object_Name,null));
			}
	    	else ReplaceType=0;
		} */


            Application_LogoUrl = atts.getValue("imageURL");

            Application_isEditable = atts.getValue("isEditable");

            Object_Is_New = atts.getValue("isNew");
            Object_AssignedUserId = atts.getValue("AssignedUserId");

            if (Application_LogoUrl != null && !Application_LogoUrl.isEmpty()) {
                Uri uri = Uri.parse(Application_LogoUrl);
                String fileName = uri.getLastPathSegment();
//                Object_Is_New = atts.getValue("isNew");
                Object_AssignedUserId = atts.getValue("AssignedUserId");
//                Log.d(TAG, "Object_AssignedUserId =" + Object_AssignedUserId);
                //if(ReplaceObject)Object_Is_New="true";
                if (isDebug)
//                    Log.d("MyLogs", "  XML  Application_LogoUrl  "
//                            + Application_LogoUrl + "     fileName  "
//                            + fileName);

                    if (fileName != null) {

                        fileName = fileName.toString().toLowerCase()
                                .replaceAll("\\s+", "").replaceAll("-", "")
                                .replaceAll("/", "").replaceAll("[(+^)_]*", "");

                        if (MyApplication.mFilesSet != null
                                && !MyApplication.mFilesSet.contains(fileName)) {
                            ImageDownloader imageDownloader = new ImageDownloader(
                                    mContext);
                            imageDownloader.download(Application_LogoUrl, fileName);
//                        Log.d(TAG, "ImagedownloadCount inside if condition= " + MyApplication.INSTANCE.ImagedownloadCount);
                            MyApplication.INSTANCE.ImagedownloadCount++;
                            MyApplication.mFilesSet.add(fileName);
                        }
                    }
            }
//            Log.d(TAG, "ImagedownloadCount= " + MyApplication.INSTANCE.ImagedownloadCount);
            if (atts.getValue("appid") != null)
                Application_ID = Integer.valueOf(atts.getValue("appid"));
            if (atts.getValue("displaySubscript") != null)
                Display_Subscript = atts.getValue("displaySubscript");
      /*
        if (atts.getValue("ID") != null)
	//	Object_ID = Integer.valueOf(atts.getValue("ID"));
	    	Object_ID=atts.getValue("ID");
	    else
		//Object_ID++;
	    	Object_ID=""; */
            App_Display_Row = atts.getValue("displayRow");
            App_DIsplay_Col = atts.getValue("displayCol");

            imageurl1 = atts.getValue("imageURL1");
            OpenUrl = atts.getValue("openURL");
            if (imageurl1 != null && !imageurl1.isEmpty()) {
                Uri uri = Uri.parse(imageurl1);
                String fileName = uri.getLastPathSegment();

                if (isDebug)
//                    Log.d("MyLogs", "  XML  imageurl1  " + imageurl1
//                            + "     fileName  " + fileName);

                    if (fileName != null) {

                        fileName = fileName.toString().toLowerCase()
                                .replaceAll("\\s+", "").replaceAll("-", "")
                                .replaceAll("/", "").replaceAll("[(+^)_]*", "");
                        if (MyApplication.mFilesSet != null
                                && !MyApplication.mFilesSet.contains(fileName)) {
                            ImageDownloader imageDownloader = new ImageDownloader(
                                    mContext);
                            imageDownloader.download(imageurl1, fileName);
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.getMessage();
                            }

                            MyApplication.mFilesSet.add(fileName);
                        }
                    }
            }
/*
        if (atts.getValue("isParent") != null)
		IsParent = Boolean.valueOf(atts.getValue("isParent"));
	    else
		IsParent = false; */
            //   if(atts.getValue("currentStatus") != null)
            //    {
            Application_status = atts.getValue("currentStatus");
//            Log.e("XmlHandler>status", "" + atts.getValue("currentStatus") + ".." + atts.getValue("name"));
            //  }

            parentId = atts.getValue("parentObjectID");
            if (ReplaceNode) {
//                Log.e(TAG, "ObjAttributes in valuelist" + Object_GraphType + "...." + Object_GraphID);
                valuesList.add(mCoreyDBHelper.makeMessageContentValues(Message_ID,
                        Application_ID, Application_Text, ObjAttributes,
                        Application_ServerId, Object_ID, Object_Name,
                        Application_Title, Object_DisplayText, Message_FieldName,
                        Field_DisplayText, Message_FieldValue, false, Message_Type,
                        Application_LogoUrl, Application_status,
                        Application_isEditable, ApplicationFIELD_ID,
                        Application_ishasStatus, Application_AddField, Object_Is_New,
                        OpenUrl, convertTime, Note_Update_Time, App_Display_Row,
                        App_DIsplay_Col, MSG_Context, IsParent, parentId,
                        Display_Quadrant, imageurl1, Object_AssignedUserId, null, null, null, null, Display_Subscript, Object_GraphType, Object_GraphID, Object_NeedsTimeValue, Field_DisplayText, IsLoading));
            }

        } else if (localName.equalsIgnoreCase("field")) {

            String attr_DisplayText = atts.getValue("name");
            if (ReplaceNode == false && ((atts.getValue("Replace")) != null)) {
                if ((atts.getValue("Replace")).equals("true")) {
                    //	whereList.add(mCoreyDBHelper.makeWhereStringbyContext(MSG_Context,Application_Text,Object_DisplayText,attr_DisplayText));
                    ReplaceField = true;
                    ReplaceNode = true;
                    IsMessageUpdated = true;
//                    Log.i("TAG", "Replace single field, field_Namee=" + attr_DisplayText);
                }
            }
        /*
        if(ReplaceType==0)
		{
		if((atts.getValue("Replace")).equals("true"))
			{
			ReplaceType=5;
			//add statement for deleting the existing message here(
			whereList.add(mCoreyDBHelper.makeWhereStringbyContext(MSG_Context,Application_Text,Object_Name,attr_DisplayText));
			}
	    	else ReplaceType=0;
		} */

            Message_FieldName = attr_DisplayText;

            Field_DisplayText = atts.getValue("displayText");
            Field_MultiSelect = atts.getValue("MultiSelectUI");

            Application_status = atts.getValue("status");

            Application_ishasStatus = atts.getValue("hasstatus");

            IsNew = atts.getValue("isNew");
//            Log.d(TAG, "ReplaceField=" + ReplaceField);
//            Log.d(TAG, "Field_DisplayText=" + Field_DisplayText);
//            Log.d(TAG, "IsNew=" + IsNew);

            Application_isEditable = atts.getValue("isEditable");

            ApplicationFIELD_ID = atts.getValue("ID");

            Application_AddField = atts.getValue("IsAddField");
            convertTime = atts.getValue("ConvertTime");
            OpenUrl = atts.getValue("openURL");
            //   if (atts.getValue("listValue")!= null)
            listValue = atts.getValue("listValue");
            //   if (atts.getValue("editableListValue")!= null)
            editableListValue = atts.getValue("editableListValue");
            //    if (atts.getValue("addtype")!= null)
            addtype = atts.getValue("addtype");
            FieldType = atts.getValue("type");
//            Log.d(TAG, "FieldType = " + FieldType);
            //   if (atts.getValue("isAddField")!= null)
            FieldEmailBody = atts.getValue("Body");
            FieldEmailSubject = atts.getValue("Subject");

            isAddField = atts.getValue("isAddField");
            Field_ImageURL = atts.getValue("imageURL");

            if (Field_ImageURL != null && !Field_ImageURL.isEmpty()) {
                Uri uri = Uri.parse(Field_ImageURL);
                String imgName = uri.getLastPathSegment();
                if (isDebug)
//                    Log.d("MyLogs", "  XML  Field_ImageURL  "
//                            + Field_ImageURL + "     fileName  "
//                            + imgName);

                    if (imgName != null) {

                        imgName = imgName.toString().toLowerCase()
                                .replaceAll("\\s+", "").replaceAll("-", "")
                                .replaceAll("/", "").replaceAll("[(+^)_]*", "");

                        if (MyApplication.mFilesSet != null
                                && !MyApplication.mFilesSet.contains(imgName)) {
                            ImageDownloader imageDownloader = new ImageDownloader(
                                    mContext);
                            imageDownloader.download(Field_ImageURL, imgName);
//                        Log.d(TAG, "ImagedownloadCount inside if condition= " + MyApplication.INSTANCE.ImagedownloadCount);
                            MyApplication.INSTANCE.ImagedownloadCount++;
                            MyApplication.mFilesSet.add(imgName);
                        }
                    }
            }

            Field_DisplayBox = atts.getValue("isDisplayBox");
            try {
                jsonAttrField = new JSONObject();
                jsonAttrField.accumulate("openURL", OpenUrl);

                jsonAttrField.accumulate("ConvertTime", convertTime);
                jsonAttrField.accumulate("IsAddField", Application_AddField);
                jsonAttrField.accumulate("FldId", ApplicationFIELD_ID);
                jsonAttrField.accumulate("FldType", FieldType);
                jsonAttrField.accumulate("EmailBody", FieldEmailBody);
                jsonAttrField.accumulate("EmailSubject", FieldEmailSubject);
                jsonAttrField.accumulate("imageURL", Field_ImageURL);
                jsonAttrField.accumulate("isDisplayBox", Field_DisplayBox);
                FieldAttributes = jsonAttrField.toString();
//                Log.d(TAG, "FieldAttributes = " + FieldAttributes.toString());
            } catch (JSONException e) {
                e.getMessage();
            }

            this.in_field = true;
        } else if (localName.equalsIgnoreCase("ListItems")) {

            Application_Text = "ListItems";
            if (ReplaceNode == false && ((atts.getValue("Replace")) != null)) {
                if ((atts.getValue("Replace")).equals("true")) {
                    whereList.add(mCoreyDBHelper.makeListItemWhereStringbyContext(MSG_Context, Application_Text, null));
                    ReplaceListItems = true;
                    ReplaceNode = true;
                    IsMessageUpdated = true;
//                    Log.i("TAG", "Replace ListItems,  name=" + Application_Text);
                }
            }

        } else if (localName.equalsIgnoreCase("ListItem")) {
            mListName = atts.getValue("name");
            ;
            if (ReplaceNode == false && ((atts.getValue("Replace")) != null)) {
                if ((atts.getValue("Replace")).equals("true")) {
                    whereList.add(mCoreyDBHelper.makeListItemWhereStringbyContext(MSG_Context, Application_Text, mListName));
                    ReplaceSingleListItem = true;
                    ReplaceNode = true;
                    IsMessageUpdated = true;
//                    Log.i("TAG", "Replace ListItem, ListItem name=" + mListName);
                }
            }
            mListScrollDir = atts.getValue("scrollDirection");
            mListOnTouch = atts.getValue("onTouch");
            mListCommonTag = atts.getValue("isCommon");
            mListnameID = atts.getValue("id");

        } else if (localName.equalsIgnoreCase("Item")) {
            mListItemValue = atts.getValue("id");
            if (ReplaceNode == false && ((atts.getValue("Replace")) != null)) {
                if ((atts.getValue("Replace")).equals("true")) {
                    whereList.add(mCoreyDBHelper.makeListItemWhereStringbyContext(MSG_Context, Application_Text, mListItemValue));
                    ReplaceItem = true;
                    ReplaceNode = true;
                    IsMessageUpdated = true;
                }
            } else if (ReplaceNode && !ReplaceListItems)
                whereList.add(mCoreyDBHelper.makeListItemWhereStringbyContext(MSG_Context, Application_Text, mListItemValue));

            mdisplayText = atts.getValue("displayText");
            mListItemImage = atts.getValue("image");
            mListItemSubscript = atts.getValue("subscript");
            mListItemBkgColour = atts.getValue("backgroundColor");
            mListItemDescription = atts.getValue("description");
            mListItemId = atts.getValue("id");
            mListItemOpenUrl = atts.getValue("openURL");
            ;
            mListItemSelected = atts.getValue("isSelected");
            ;
            mListItemadd2 = atts.getValue("openURL");
            ;
            mListItemadd3 = atts.getValue("openURL");
            ;

        } else if (localName.equalsIgnoreCase("AllMessages")) {
            InOne2OneMsg = true;
//            Log.d(TAG, "Allmessages entered");
        } else if (localName.equalsIgnoreCase("Context")) {
            MsgContext = atts.getValue("name");
            MsgContextId = atts.getValue("id");
            allmsgsmap = MyApplication.INSTANCE.AllMsgsMap;
            // MyApplication application = (MyApplication) getApplication();
            //allmsgsmap=(HashMap<String, MsgByCtxt>)getApplication().AllMsgsMap;
            if (allmsgsmap.get(MsgContextId) != null) {
                msgbyctxt = allmsgsmap.get(MsgContextId);

            } else {
                msgbyctxt = new MsgByCtxt();
                allmsgsmap.put(MsgContextId, msgbyctxt);
            }
            msgbyctxt.setCtxtName(MsgContext);
            msgbyctxt.setCtxtId(MsgContextId);
//            Log.d(TAG, "Msgcontext=" + MsgContext);
        } else if (localName.equalsIgnoreCase("MyMessages")) {
            UsrName = atts.getValue("with");
            UsrId = atts.getValue("withid");

            if (msgbyctxt.getMsgByUsr(UsrId) != null) {
                msgbyusr = msgbyctxt.getMsgByUsr(UsrId);

            } else {
                msgbyusr = new MsgByUsr();
                msgbyctxt.addMsgByUsr(UsrId, msgbyusr);
            }
            msgbyusr.setUsrName(UsrName);
            msgbyusr.setUsrId(UsrId);
//            Log.d(TAG, "UsrName=" + UsrName);
        } else if (localName.equalsIgnoreCase("One2OneMessage")) {
            OneMsgId = atts.getValue("ID");
            String tmestmp = atts.getValue("timestamp");
            //  TmeStamp= atts.getValue("timestamp");
            TmeStamp = formatMeetingTime(tmestmp);
            MsgTo = atts.getValue("to");
            MsgFrom = atts.getValue("from");
            this.in_field = true;
//            Log.d(TAG, "UsrName=" + OneMsgId);
        } else if (localName.equalsIgnoreCase("AllStaffMembers")) {
            InStaffLst = true;
            stafflist = MyApplication.INSTANCE.StaffList;
        } else if (localName.equalsIgnoreCase("StaffMember")) {

            StaffName = atts.getValue("displayname");
            StaffId = atts.getValue("id");
        } else if (localName.equalsIgnoreCase("Login")) {
//            Log.i("TAG", "startElement Login");
            in_login = true;
        } else if (localName.equalsIgnoreCase("Password")) {
//            Log.i("TAG", "startElement Password");
            in_password = true;
        } else if (localName.equalsIgnoreCase("Context")) {
//            Log.i("TAG", "startElement Context");
            in_context = true;
        } else if (localName.equalsIgnoreCase("ProgressBar")) {
            if (atts.getValue("ObjectID").equalsIgnoreCase("-1")) {
                ProgressObjectId = "0";
            } else {
                ProgressObjectId = atts.getValue("ObjectID");
            }
            for (int i = 0; i < progressInfoDBControl.getProgressBarObjects(ProgressObjectId).size(); i++) {
//                if (progressInfoDBControl.getProgressBarObjects().get(i).getObjectId().equalsIgnoreCase(ProgressObjectId)) {
            progressInfoDBControl.deleteSingleProgressBarObjects(progressInfoDBControl.getProgressBarObjects(ProgressObjectId).get(i));
                }
//            }
        } else if (localName.equalsIgnoreCase("Stage")) {
            ProgressStageColor = atts.getValue("Color");
            ProgressStageText = atts.getValue("DisplayText");
            ProgressStageValue = atts.getValue("Status");
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
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm zzz");
        String startTimeString = format.format(startDate).toString();
        return startTimeString;

    }

    /**
     * Gets be called on closing tags like: </tag>
     */
    @Override
    public void endElement(String namespaceURI, String localName, String qName)
            throws SAXException {
//        Log.e("XMLPARSEREnd", "..." + localName);
        if (localName.equalsIgnoreCase("Messages")) {
            // Log.i("TAG","endElement Messages");
            if (ReplaceMessages) {
                if (foundPlanAheadMsgs) {
                     mCoreyDBHelper
                            .deleteAllMessages(DatabaseProvider.MSG_TYPE_INSTANT_GRATIFICATION);
                }
                if (foundtododMsgs) {
                     mCoreyDBHelper
                            .deleteAllMessages(DatabaseProvider.MSG_TYPE_TODO);
                }
                if (foundMarketTrackerMsgs
                        && mCoreyDBHelper.getAllMsgids().contains(Message_ID)) {
                    // mCoreyDBHelper.deleteAllMessages(DatabaseProvider.MSG_TYPE_MARKET_TRACKER);
                }
                mCoreyDBHelper.insertMessages(valuesList);
            } else {
//                Log.d(TAG, "contentvalues=" + valuesList.size());
                if (!whereList.isEmpty()) {
                    try {
                        mCoreyDBHelper.deleteByWheres(whereList);
                    } catch (Exception e) {
                        e.getMessage();
                    }
                    mCoreyDBHelper.insertMessages(valuesList);
                    MyApplication.INSTANCE.MsgIdprsr = Message_ID;
                }
            }
            this.in_messages = false;

        } else if (localName.equalsIgnoreCase("Message")) {
            Application_ID = 0;

            if (ReplaceSingleMessage == true) {
                ReplaceSingleMessage = false;
                ReplaceNode = false;
            } else if (IsMessageUpdated) {
//                Log.d(TAG, "IsMessageUpdated= " + IsMessageUpdated);
//                Log.d(TAG, "MessageUpdated= " + Message_ID);
                mCoreyDBHelper.markMessageAsUnRead(Message_ID);
            }
            IsMessageUpdated = false;
            // Log.i("TAG","endElement Message");
        } else if (localName.equalsIgnoreCase("Application")) {
            Object_ID = "";
            // Log.i("TAG","endElement Application");
            if (ReplaceApplication == true) {
                ReplaceApplication = false;
                ReplaceNode = false;
            }
            this.is_auth_xml = false;
        } else if (localName.equalsIgnoreCase("Object")) {
/*
        valuesList.add(mCoreyDBHelper.makeMessageContentValues(Message_ID,
		    Application_ID, Application_Text, Application_GroupId,
		    Application_ServerId, Object_ID, Object_Name,
		    Application_Title, Object_DisplayText, Message_FieldName,
		    Field_DisplayText, Message_FieldValue, false, Message_Type,
		    Application_LogoUrl, Application_status,
		    Application_isEditable, ApplicationFIELD_ID,
		    Application_ishasStatus, Application_AddField, IsNew,
		    OpenUrl, convertTime, Note_Update_Time, App_Display_Row,
		    App_DIsplay_Col, MSG_Context, IsParent, parentId,
		    Display_Quadrant, imageurl1, null));
		*/
            if (ReplaceObject == true) {
                ReplaceObject = false;
                ReplaceNode = false;
//                Log.d(TAG, "contentvalues in replaceobject=" + valuesList.size());
            }
            Application_LogoUrl = null;
            imageurl1 = null;
            //    Log.d(TAG,"contentvalues in m="+valuesList.size());
        } else if (localName.equalsIgnoreCase("field")) {

            if (ReplaceNode) {
                if (Message_FieldName.equalsIgnoreCase("StartTime")
                        || Message_FieldName.equalsIgnoreCase("EndTime")) {

                    Message_FieldValue = formatMeetingTime(Message_FieldValue);
                }
                if (Message_FieldName.equalsIgnoreCase("patDOS")) {
//                    Log.d(TAG, "Message_FieldValue in patDOS = " + Message_FieldValue);
                    if (Message_FieldValue != null && !Message_FieldValue.equalsIgnoreCase("")) {
                        Message_FieldValue = Utils.converttimeutc2local(Message_FieldValue);
                    }
                }
                if (ReplaceField) {

                    mCoreyDBHelper.updateField(Integer.parseInt(Message_ID), Object_ID, Message_FieldName, Message_FieldValue);
                } else {
                    valuesList.add(mCoreyDBHelper.makeMessageContentValues(Message_ID,
                            Application_ID, Application_Text, Application_GroupId,
                            FieldAttributes, Object_ID, Object_Name,
                            Application_Title, Object_DisplayText, Message_FieldName,
                            Field_DisplayText, Message_FieldValue, false, Message_Type,
                            Application_LogoUrl, Application_status,
                            Application_isEditable, ApplicationFIELD_ID,
                            Application_ishasStatus, Application_AddField, IsNew,
                            OpenUrl, convertTime, Note_Update_Time, App_Display_Row,
                            App_DIsplay_Col, MSG_Context, IsParent, parentId,
                            Display_Quadrant, imageurl1, null, listValue, editableListValue,
                            isAddField, addtype, Display_Subscript, Object_GraphType, Object_GraphID, Object_NeedsTimeValue, Field_MultiSelect,IsLoading));
//                    Log.d(TAG, "contentvalues in field=" + valuesList.size());
                }
                if (!foundPlanAheadMsgs
                        && Message_Type
                        .equals(DatabaseProvider.MSG_TYPE_INSTANT_GRATIFICATION)) {
                    foundPlanAheadMsgs = true;
                }

                if (!foundtododMsgs
                        && Message_Type.equals(DatabaseProvider.MSG_TYPE_TODO)) {
                    foundtododMsgs = true;
                }

                if (!foundMarketTrackerMsgs
                        && Message_Type
                        .equals(DatabaseProvider.MSG_TYPE_MARKET_TRACKER)) {
                    foundMarketTrackerMsgs = true;
                }
                // Log.i("TAG","inserted field value");
                this.in_field = false;
                Message_FieldValue = "";
            }
            if (ReplaceField == true) {
                ReplaceField = false;
                ReplaceNode = false;
            }
        } else if (localName.equalsIgnoreCase("ListItems")) {
        /*
         * List items will not have any separate row
		 */
            if (ReplaceListItems == true) {
                ReplaceListItems = false;
                ReplaceNode = false;
//                Log.d(TAG, "contentvalues in ReplaceListItems=" + valuesList.size());
            }
        } else if (localName.equalsIgnoreCase("ListItem")) {
            String nodeLevel = "ListItem";
            String fieldtype = "attribute";  //alternatively it will be value
//            Log.d(TAG, "mListName=" + mListName);
        /*
           if(ReplaceSingleListItem==true)
		    {
			   
		    	ReplaceNode=false;
		    	Log.d(TAG,"contentvalues in ReplacesingleListItems="+valuesList.size());
		    }   */
            if (ReplaceNode) {
                valuesList.add(mCoreyDBHelper.makeMessageContentValues(Message_ID,
                        0, Application_Text, null, null, null, nodeLevel, mListName, null, "name",
                        mListName, "dummy", false, Message_Type, null, null,
                        "false", null, null, null, null, null, null, null, null,
                        null, MSG_Context, false, null, null, null, null, null, null, null, null, null, null, null, null, null, false));
    /*	valuesList.add(mCoreyDBHelper.makeMessageContentValues(Message_ID,
                    0, null, null, null, null, null, null, null,
				    "name", mListName, null, false, fieldtype,
				    null, null, null, null, null, null, null, null, null,
				    null, null, null, MSG_Context, false, null, null, null,
				    null, null, null, null, null, null)); */
                valuesList.add(mCoreyDBHelper.makeMessageContentValues(Message_ID,
                        0, Application_Text, null, null, null, nodeLevel, mListName, null,
                        "scrollDirection", mListScrollDir, "dummy", false, Message_Type,
                        null, null, null, null, null, null, null, null, null,
                        null, null, null, MSG_Context, false, null, null, null,
                        null, null, null, null, null, null, null, null, null, null, false));
                valuesList.add(mCoreyDBHelper.makeMessageContentValues(Message_ID,
                        0, Application_Text, null, null, null, nodeLevel, mListName, null,
                        "onTouch", mListOnTouch, "dummy", false, Message_Type,
                        null, null, null, null, null, null, null, null, null,
                        null, null, null, MSG_Context, false, null, null, null,
                        null, null, null, null, null, null, null, null, null, null, false));
                valuesList.add(mCoreyDBHelper.makeMessageContentValues(Message_ID,
                        0, Application_Text, null, null, null, nodeLevel, mListName, null,
                        "id", mListnameID, "dummy", false, Message_Type,
                        null, null, null, null, null, null, null, null, null,
                        null, null, null, MSG_Context, false, null, null, null,
                        null, null, null, null, null, null, null, null, null, null, false));
            }
            if (ReplaceSingleListItem == true) {
                ReplaceSingleListItem = false;
                ReplaceNode = false;
            }
        } else if (localName.equalsIgnoreCase("Item")) {
            String nodeLevel = "Item";
            String fieldtype = "attribute";  //alternatively it will be value
		 /*
		  * Name of the listitem in the previous level is saved here in parent
		  * context(KEY_PARENT_ID_CONTEXT) to get only the items belongs to that
		  * This will serve as the link for searching
		  */
	/*	 if(ReplaceSingleListItem==true)
		    {
			 Log.d(TAG,"contentvalues in ReplacetItems="+valuesList.size());
		    	ReplaceNode=false;
		    } */
            if (ReplaceNode) {
                valuesList.add(mCoreyDBHelper.makeMessageContentValues(Message_ID,
                        0, Application_Text, null, null, null, nodeLevel, mListItemValue, null,
                        "value", mListItemValue, "dummy", false, Message_Type,
                        null, null, null, null, null, null, null, null, null,
                        null, null, null, MSG_Context, false, mListName, null, null,
                        null, null, null, null, null, null, null, null, null, null, false));
                valuesList.add(mCoreyDBHelper.makeMessageContentValues(Message_ID,
                        0, Application_Text, null, null, null, nodeLevel, mListItemValue, null,
                        "displayText", mdisplayText, "dummy", false, Message_Type,
                        null, null, null, null, null, null, null, null, null,
                        null, null, null, MSG_Context, false, mListName, null, null,
                        null, null, null, null, null, null, null, null, null, null, false));
                valuesList.add(mCoreyDBHelper.makeMessageContentValues(Message_ID,
                        0, Application_Text, null, null, null, nodeLevel, mListItemValue, null,
                        "image", mListItemImage, "dummy", false, Message_Type,
                        null, null, null, null, null, null, null, null, null,
                        null, null, null, MSG_Context, false, mListName, null, null,
                        null, null, null, null, null, null, null, null, null, null, false));
                valuesList.add(mCoreyDBHelper.makeMessageContentValues(Message_ID,
                        0, Application_Text, null, null, null, nodeLevel, mListItemValue, null,
                        "subscript", mListItemSubscript, "dummy", false, Message_Type,
                        null, null, null, null, null, null, null, null, null,
                        null, null, null, MSG_Context, false, mListName, null, null,
                        null, null, null, null, null, null, null, null, null, null, false));
                valuesList.add(mCoreyDBHelper.makeMessageContentValues(Message_ID,
                        0, Application_Text, null, null, null, nodeLevel, mListItemValue, null,
                        "backgroundColor", mListItemBkgColour, "dummy", false, Message_Type,
                        null, null, null, null, null, null, null, null, null,
                        null, null, null, MSG_Context, false, mListName, null, null,
                        null, null, null, null, null, null, null, null, null, null, false));
                valuesList.add(mCoreyDBHelper.makeMessageContentValues(Message_ID,
                        0, Application_Text, null, null, null, nodeLevel, mListItemValue, null,
                        "Description", mListItemDescription, "dummy", false, Message_Type,
                        null, null, null, null, null, null, null, null, null,
                        null, null, null, MSG_Context, false, mListName, null, null,
                        null, null, null, null, null, null, null, null, null, null, false));
                valuesList.add(mCoreyDBHelper.makeMessageContentValues(Message_ID,
                        0, Application_Text, null, null, null, nodeLevel, mListItemValue, null,
                        "Id", mListItemId, "dummy", false, Message_Type,
                        null, null, null, null, null, null, null, null, null,
                        null, null, null, MSG_Context, false, mListName, null, null,
                        null, null, null, null, null, null, null, null, null, null, false));
                valuesList.add(mCoreyDBHelper.makeMessageContentValues(Message_ID,
                        0, Application_Text, null, null, null, nodeLevel, mListItemValue, null,
                        "OpenUrl", mListItemOpenUrl, "dummy", false, Message_Type,
                        null, null, null, null, null, null, null, null, null,
                        null, null, null, MSG_Context, false, mListName, null, null,
                        null, null, null, null, null, null, null, null, null, null, false));
                valuesList.add(mCoreyDBHelper.makeMessageContentValues(Message_ID,
                        0, Application_Text, null, null, null, nodeLevel, mListItemValue, null,
                        "isSelected", mListItemSelected, "dummy", false, Message_Type,
                        null, null, null, null, null, null, null, null, null,
                        null, null, null, MSG_Context, false, mListName, null, null,
                        null, null, null, null, null, null, null, null, null, null, false));
                valuesList.add(mCoreyDBHelper.makeMessageContentValues(Message_ID,
                        0, Application_Text, null, null, null, nodeLevel, mListItemValue, null,
                        "listitem2", mListItemOpenUrl, "dummy", false, Message_Type,
                        null, null, null, null, null, null, null, null, null,
                        null, null, null, MSG_Context, false, mListName, null, null,
                        null, null, null, null, null, null, null, null, null, null, false));
                valuesList.add(mCoreyDBHelper.makeMessageContentValues(Message_ID,
                        0, Application_Text, null, null, null, nodeLevel, mListItemValue, null,
                        "listitem3", mListItemOpenUrl, "dummy", false, Message_Type,
                        null, null, null, null, null, null, null, null, null,
                        null, null, null, MSG_Context, false, mListName, null, null,
                        null, null, null, null, null, null, null, null, null, null, false));

            }
            if (ReplaceItem == true) {
                ReplaceItem = false;
                ReplaceNode = false;
            }
        } else if (localName.equalsIgnoreCase("AllMessages")) {

        } else if (localName.equalsIgnoreCase("Context")) {

        } else if (localName.equalsIgnoreCase("MyMessages")) {

        } else if (localName.equalsIgnoreCase("One2OneMessage")) {
            One2OneMsg = Message_FieldValue;
            onemsg = new OneMsg(Message_FieldValue);
            if (SignedUsr.equalsIgnoreCase(MsgFrom)) onemsg.setMine(true);
            else onemsg.setMine(false);
            onemsg.setMsgFrom(MsgFrom);
            onemsg.setMsgId(OneMsgId);
            onemsg.setMsgTimestamp(TmeStamp);
            onemsg.setMsgTo(MsgTo);
            msgbyusr.addMsg(onemsg);
            this.in_field = false;
            Message_FieldValue = "";

        } else if (localName.equalsIgnoreCase("AllStaffMembers")) {
            InStaffLst = false;
        } else if (localName.equalsIgnoreCase("StaffMember")) {
            stafflist.put(StaffName, StaffId);
        } else if (localName.equalsIgnoreCase("Login")) {
            // Log.i("TAG","endElement Object");
            this.in_login = false;
        } else if (localName.equalsIgnoreCase("Password")) {
            // Log.i("TAG","endElement Object");
            this.in_password = false;
        } else if (localName.equalsIgnoreCase("Context")) {
            // Log.i("TAG","endElement Object");
            this.in_context = false;
        } else if (localName.equalsIgnoreCase("Stage")) {
            ProgressObjectModel progressObjectModel = new ProgressObjectModel();
            progressObjectModel.setStatus(ProgressStageValue);
            progressObjectModel.setColor(ProgressStageColor);
            progressObjectModel.setObjectId(ProgressObjectId);
            progressObjectModel.setStageName(ProgressStageText);

            progressInfoDBControl.insertProgressBarObject(progressObjectModel);
        }

    }

    /**
     * Gets be called on the following structure: <tag>characters</tag>
     */
    @Override
    public void characters(char ch[], int start, int length) {
        // Log.i("TAG","characters" + new String(ch, start, length));
        if (this.in_field) {
            Message_FieldValue = Message_FieldValue.concat(String.copyValueOf(
                    ch, start, length));
        } else if (this.in_login) {
            if (is_auth_xml) {
                Login = new String(ch, start, length);
                editor.putString(ApplicationName + "Login", Login);
                editor.commit();
            }

        } else if (this.in_password) {
//            if (is_auth_xml) {
//                Password = new String(ch, start, length);
//                editor.putString(ApplicationName + "Password", Password);
//                editor.commit();
//            }

        } else if (this.in_context) {
            if (is_auth_xml) {
                Context = new String(ch, start, length);
                editor.putString(ApplicationName + "Context", Context);
                editor.commit();
            }

        }

    }

}