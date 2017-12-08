package com.coremobile.coreyhealth.newui;

import java.util.ArrayList;

public class RowDisplayObject {

    private String sDisplayName;
    private String sObjectName;
    private String sDisplaySubscript;
    public boolean isEditable;
    public boolean isParent;

    public boolean isLoading;
    public int sCursorPos;
    /*    private int sParentID;
        private int sObjectID;
        */
    private String sParentID;
    private String sObjectID;
    public ArrayList<RowDisplayObject> dashboardobj = new ArrayList<RowDisplayObject>();
    public ArrayList<Fields> fieldsList = new ArrayList<Fields>();
    RowDisplayObject sRowDisplayObject;
    private String sImageUrl;
    private String sImageUrl1;
    private String sAppName;
    private String sAppTitle;
    private String sMsgContext;
    private String sObjectStatus;
    private Boolean sIsNew;
    private String sAppId;
    private String sOpenUrl;
    private String sAppStatus; //Redundant not required
    private int sDispCol;
    private int sDispRow;
    private int sDispPos;
    private String sAssignedUserId;
    private String sGraphType;
    private String sGraphId;
    private String sEditText;
    private String sMultiselecUi = "false";
    private String sListValue;
    private Boolean IsFullImage;
    private String sDetailViewActionBarButton;
    private String sobjType;
    private String sobjToType;
    private String sobjToTypeDispVal;
    private String sNeedTimeStamp;

    public void setObjType(String objType) {
        sobjType = objType;
    }

    public String getObjType() {
        return sobjType;
    }

    public void setObjToType(String objToType) {
        sobjToType = objToType;
    }

    public String getObjToType() {
        return sobjToType;
    }

    public void setObjToTypeDispVal(String ObjToTypeDispVal) {
        sobjToTypeDispVal = ObjToTypeDispVal;
    }

    public String getObjToTypeDispVal() {
        return sobjToTypeDispVal;
    }

    public void setDetailViewABarBtn(String detailviewbutton) {
        sDetailViewActionBarButton = detailviewbutton;
    }

    public String getDetailViewABarBtn() {
        return sDetailViewActionBarButton;
    }

    public void setMultiselecUi(String MultiselecUi) {
        sMultiselecUi = MultiselecUi;
    }

    public String getMultiselecUi() {
        return sMultiselecUi;
    }

    public Boolean IsMultiselecUi() {
//        Log.d("rowdsiplayobj", "sMultiselecUi=" + sMultiselecUi);
        if (sMultiselecUi != null) {
            if (sMultiselecUi.equalsIgnoreCase("true")) return true;
            else return false;
        } else return false;

    }

    public Boolean getIsFullImage() {
        if (IsFullImage != null) return IsFullImage;
        else return false;
    }

    public void setIsFullImage(String Isfull) {
//        Log.d("RowdisplayObject", "IsNew =" + Isfull);
//        Log.d("Rosdisplayobject", "Objectname=" + sDisplayName);
        if (Isfull != null) {
            if (Isfull.equalsIgnoreCase("true")) {
                IsFullImage = true;
            } else IsFullImage = false;
        } else IsFullImage = false;

    }


    public void setListValue(String ListValue) {
        sListValue = ListValue;
    }

    public String getListValue() {
        return sListValue;
    }

    public void setEditText(String EditText) {
        sEditText = EditText;
    }

    public String getEditText() {
        return sEditText;
    }

    public void setDispCol(int DispCol) {
        sDispCol = DispCol;
    }

    public int getDispCol() {
        return sDispCol;
    }

    public void setDispPos(int DispPos) {
        sDispCol = DispPos;
    }

    public int getDispPos() {
        return sDispPos;
    }

    public void setDispRow(int DispRow) {
        sDispRow = DispRow;
    }

    public int getDispRow() {
        return sDispRow;
    }

    public void setAssignedUserId(String AssignedUserId) {
        sAssignedUserId = AssignedUserId;
    }

    public String getAssignedUserId() {
        return sAssignedUserId;
    }

    public void setAppStatus(String AppStatus) { //Redundant not required
        sAppStatus = AppStatus;
    }

    public String getAppStatus() {  //Redundant not required
        return sAppStatus;
    }

    public void setAppId(String AppId) {
        sAppId = AppId;
    }

    public String getAppId() {
        return sAppId;
    }

    public void setDisplayName(String displayName) {
        sDisplayName = displayName;
    }

    public String getDisplayName() {
        return sDisplayName;
    }

    public void setObjectStatus(String ObjectStatus) {
        sObjectStatus = ObjectStatus;
    }

    public String getObjectStatus() {
        return sObjectStatus;
    }

    public void setIsNew(String IsNew) {
//        Log.d("RowdisplayObject", "IsNew =" + IsNew);
//        Log.d("Rosdisplayobject", "Objectname=" + sDisplayName);
        if (IsNew != null) {
            if (IsNew.equals("true")) {
                sIsNew = true;
            } else sIsNew = false;
        } else sIsNew = false;

    }

    public Boolean getIsNew() {
        if (sIsNew != null) {
            return sIsNew;
        } else return false;

    }

    public void setMsgContext(String MsgContext) {
        sMsgContext = MsgContext;
    }

    public String getMsgContext() {
        return sMsgContext;
    }

    public String getObjectId() {
        return sObjectID;
    }

    public void setEditable(boolean editable) {
        isEditable = editable;
    }

    public void setParent(boolean parent) {
        isParent = parent;
    }

    public void setParentid(String parentID) {
        sParentID = parentID;
    }

    public String getParentid() {
        return sParentID;
    }

    /*    public void setParentid(int parentID) {
            sParentID = parentID;
            }

            public int getParentid() {
            return sParentID;
            }
            */
/*
    public void setObjectid(int ObjectID) {
	sObjectID = ObjectID;
    }

    public int getObjectid() {
	return sObjectID;
    }
*/
    public void setObjectid(String ObjectID) {
        sObjectID = ObjectID;
    }

    public String getObjectid() {
        return sObjectID;
    }

    public void setObjectName(String ObjectName) {
        sObjectName = ObjectName;
    }

    public String getObjectName() {
        return sObjectName;
    }

    public void setDisplaySubscript(String displaySubscript) {
        sDisplaySubscript = displaySubscript;
    }

    public String getDisplaySubscript() {
        return sDisplaySubscript;
    }

    public RowDisplayObject getRowDispObject() {
        return sRowDisplayObject;
    }

    public void setRowDispObject(RowDisplayObject rdo) {
        sRowDisplayObject = rdo;
    }

    // @Override
    public void addAttributes(String key, String value) {
        // TODO Auto-generated method stub

    }

    // @Override
    public void retrieveAttributes(String key) {
        // TODO Auto-generated method stub

    }

    // @Override
    public void retrieveObjectList() {
        // TODO Auto-generated method stub

    }

    public void add(RowDisplayObject dashBoardObjects) {

        dashboardobj.add(dashBoardObjects);
    }

    public void addField(Fields field) {

        fieldsList.add(field);
    }

    public ArrayList<RowDisplayObject> getobjectlist() {
        return dashboardobj;
    }

    public ArrayList<Fields> getField() {
        return fieldsList;
    }

    public RowDisplayObject getObject(int index) {
        return (dashboardobj.get(index)); // TODO Auto-generated method stub

    }

    public int size() {
        int size = dashboardobj.size();
        return size;
    }

    public int getCursorPos() {
        return (sCursorPos);
    }

    public void setCursorPos(int cursorPos) {
        sCursorPos = cursorPos;
    }

    public boolean Isparent() {
        return isParent;
    }

    public void setImageUrl(String imageUrl) {
        sImageUrl = imageUrl;
    }

    public String getImageUrl() {
        return sImageUrl;
    }

    public void setImageUrl1(String imageUrl1) {
        sImageUrl1 = imageUrl1;
    }

    public String getImageUrl1() {
        return sImageUrl1;
    }

    public void setAppName(String appName) {
        sAppName = appName;
    }

    public String getAppName() {
        return sAppName;
    }

    public void setAppTitle(String appTitle) {
        sAppTitle = appTitle;
    }

    public String getAppTitle() {
        return sAppTitle;
    }

    public String getOpenUrl() {
        return sOpenUrl;
    }

    public void setOpenUrl(String openUrl) {
        this.sOpenUrl = openUrl;

    }

    public String getStsListName() {

        String lstval = null;
        for (Fields flst : fieldsList) {

            if (flst.getHasStatus().equalsIgnoreCase("true")) {
                lstval = flst.getlistValue();
                break;
            }
        }


        return lstval;
    }

    public int getStsFieldIndex() {

        int indxx = 0;
        for (Fields flst : fieldsList) {

            if (flst.getHasStatus().equalsIgnoreCase("true")) {

                break;
            }
            indxx++;
        }


        return indxx;
    }

    public String getsGraphType() {
        return sGraphType;
    }

    public void setsGraphType(String sGraphType) {
        this.sGraphType = sGraphType;
    }

    public String getsGraphId() {
        return sGraphId;
    }

    public void setsGraphId(String sGraphId) {
        this.sGraphId = sGraphId;
    }


    public String getsNeedTimeStamp() {
        return sNeedTimeStamp;
    }

    public void setsNeedTimeStamp(String sGraphId) {
        this.sNeedTimeStamp = sGraphId;
    }


    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        else if (obj instanceof RowDisplayObject
                && getObjectId().equalsIgnoreCase(((RowDisplayObject) obj).getObjectId())
                && getDisplayName().equalsIgnoreCase(((RowDisplayObject) obj).getDisplayName()))
            return true;
        else return false;
    }
}
