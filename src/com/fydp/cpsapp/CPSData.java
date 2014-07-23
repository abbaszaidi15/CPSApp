package com.fydp.cpsapp;

public class CPSData {

	private int id;
	private String userID;
	private String deviceID;
	private String tapType;
	private String latitude;
	private String longitude;
	private String location;
	private String zonalReg;
	private String cost;
	private String timeStamp;
	
	public CPSData(){}
	
	public CPSData(String userID, String deviceID, String tapType, 
			String latitude, String longitude, String location, 
			String zonalReg, String cost, String timeStamp){
		
		super();
		this.userID = userID;
		this.deviceID = deviceID;
		this.tapType = tapType;
		this.latitude = latitude;
		this.longitude = longitude;
		this.location = location;
		this.zonalReg = zonalReg;
		this.cost = cost;
		this.timeStamp = timeStamp;
	}
	
	String eol = System.getProperty("line.separator"); 
	
	@Override
	public String toString(){
		return "ID:" + id + eol + " CPS USER ID:" + userID + eol + 
				"Device ID:" + deviceID + eol + "Tap Type:" + tapType + eol + 
				"Latitude:" + latitude + eol + "Longitude:" + longitude + eol +
				"Location:" + location + eol + "Zonal Region:" + zonalReg + eol + 
				"Cost:" + cost + eol + "Time Stamp:" + timeStamp;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the userID
	 */
	public String getUserID() {
		return userID;
	}

	/**
	 * @param userID the userID to set
	 */
	public void setUserID(String userID) {
		this.userID = userID;
	}

	/**
	 * @return the deviceID
	 */
	public String getDeviceID() {
		return deviceID;
	}

	/**
	 * @param deviceID the deviceID to set
	 */
	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}

	/**
	 * @return the tapType
	 */
	public String getTapType() {
		return tapType;
	}

	/**
	 * @param tapType the tapType to set
	 */
	public void setTapType(String tapType) {
		this.tapType = tapType;
	}

	/**
	 * @return the latitude
	 */
	public String getLatitude() {
		return latitude;
	}

	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	/**
	 * @return the longitude
	 */
	public String getLongitude() {
		return longitude;
	}

	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * @return the zonal_reg
	 */
	public String getZonalReg() {
		return zonalReg;
	}

	/**
	 * @param zonal_reg the zonal_reg to set
	 */
	public void setZonalReg(String zonalReg) {
		this.zonalReg = zonalReg;
	}

	/**
	 * @return the cost
	 */
	public String getCost() {
		return cost;
	}

	/**
	 * @param cost the cost to set
	 */
	public void setCost(String cost) {
		this.cost = cost;
	}

	/**
	 * @return the timeStamp
	 */
	public String getTimeStamp() {
		return timeStamp;
	}

	/**
	 * @param timeStamp the timeStamp to set
	 */
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
}
