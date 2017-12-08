package com.coremobile.coreyhealth.analytics;

import com.coremobile.coreyhealth.newui.Fields;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Nitij Katiyar
 * 
 */
public class CMN_AnalyticGraphModel implements Serializable {

	String name, id, displayCol, displayRow, displayText, appId,
			displaySubscript, imageUrl, isNew, isEditable, openUrl, title,
			graphType, dataFile, xAxisTitle, yAxisTitle,sMultiselecUi,sAssignedUserId,
			sParentID,sObjectID,graphID;
	Boolean isParent;
	public ArrayList<CMN_AnalyticGraphModel> ChildObjects = new ArrayList<CMN_AnalyticGraphModel>();
	public ArrayList<Fields> fieldsList = new ArrayList<Fields>();

	public String getDataFile() {
		return dataFile;
	}


	public void setDataFile(String dataFile) {
		this.dataFile = dataFile;
	}

	public String getxAxisTitle() {
		return xAxisTitle;
	}

	public void setxAxisTitle(String xAxisTitle) {
		this.xAxisTitle = xAxisTitle;
	}

	public String getyAxisTitle() {
		return yAxisTitle;
	}

	public void setyAxisTitle(String yAxisTitle) {
		this.yAxisTitle = yAxisTitle;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDisplayCol() {
		return displayCol;
	}

	public void setDisplayCol(String displayCol) {
		this.displayCol = displayCol;
	}

	public String getDisplayRow() {
		return displayRow;
	}

	public void setDisplayRow(String displayRow) {
		this.displayRow = displayRow;
	}

	public String getDisplayText() {
		return displayText;
	}

	public void setDisplayText(String displayText) {
		this.displayText = displayText;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getDisplaySubscript() {
		return displaySubscript;
	}

	public void setDisplaySubscript(String displaySubscript) {
		this.displaySubscript = displaySubscript;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getIsNew() {
		return isNew;
	}

	public void setIsNew(String isNew) {
		this.isNew = isNew;
	}

	public String getIsEditable() {
		return isEditable;
	}

	public void setIsEditable(String isEditable) {
		this.isEditable = isEditable;
	}

	public String getOpenUrl() {
		return openUrl;
	}

	public void setOpenUrl(String openUrl) {
		this.openUrl = openUrl;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getGraphType() {
		return graphType;
	}

	public void setGraphType(String graphType) {
		this.graphType = graphType;
	}
	public void setMultiselecUi(String MultiselecUi) {
		sMultiselecUi = MultiselecUi;
	}

	public String getMultiselecUi() {
		return sMultiselecUi;
	}
	public Boolean IsMultiselecUi() {
		//Log.d("rowdsiplayobj", "sMultiselecUi=" + sMultiselecUi);
		if(sMultiselecUi!=null)
		{
			if(sMultiselecUi.equalsIgnoreCase("true")) return true;
			else return false;
		}
		else return false;

	}
	public void setAssignedUserId(String AssignedUserId) {
		sAssignedUserId = AssignedUserId;
	}

	public String getAssignedUserId() {
		return sAssignedUserId;
	}

	public void setIsParent(String IsNew) {

		if(IsNew!=null)
		{
			if(IsNew.equals("true"))
			{
				isParent=true;
			}
			else isParent=false;
		}
		else isParent =false;

	}

	public Boolean getIsParent() {
		if(isParent!=null)
		{
			return isParent;
		}else return false;

	}
	public void setParentid(String parentID) {
		sParentID = parentID;
	}

	public String getParentid() {
		return sParentID;
	}

	public String getGraphID() {
		return graphID;
	}

	public void setGraphID(String graphID) {
		this.graphID = graphID;
	}
}
