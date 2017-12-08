package com.coremobile.coreyhealth.googleAnalytics;


import java.io.Serializable;
import java.util.Date;

public class CMN_AccountWeightInfo implements Serializable {
	private Integer weightId;
	private Integer accountId;
	private Date date;
	private String weight;
	private String weightUnits;
	private String dateAdded;
	public String getDateAdded() {
		return dateAdded;
	}
	public void setDateAdded(String dateAdded) {
		this.dateAdded = dateAdded;
	}
	public Integer getWeightId() {
		return weightId;
	}
	public void setWeightId(Integer weightId) {
		this.weightId = weightId;
	}
	public Integer getAccountId() {
		return accountId;
	}
	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getWeight() {
		return weight;
	}
	public void setWeight(String weight) {
		this.weight = weight;
	}
	public String getWeightUnits() {
		return weightUnits;
	}
	public void setWeightUnits(String weightUnits) {
		this.weightUnits = weightUnits;
	}
	
}
