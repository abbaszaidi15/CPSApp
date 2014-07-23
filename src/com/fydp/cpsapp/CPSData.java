package com.fydp.cpsapp;

public class CPSData {

	private int id;
	private String cps_user_id;
	private String device_id;
	private String tap_type;
	private String latitude;
	private String longitude;
	private String location;
	private String zonal_reg;
	private String cost;
	private String timeStamp;
	
	public CPSData(){}
	
	public CPSData(String cps_user_id, String device_id, String tap_type, 
			String latitude, String longitude, String location, 
			String zonal_reg, String cost, String timeStamp){
		
		super();
		this.cps_user_id = cps_user_id;
		this.device_id = device_id;
		this.tap_type = tap_type;
		this.latitude = latitude;
		this.longitude = longitude;
		this.location = location;
		this.zonal_reg = zonal_reg;
		this.cost = cost;
		this.timeStamp = timeStamp;
	}
	
	@Override
	public String toString(){
		return "Data [id=" + id + ", cps_user_id=" + ", device_id=" + device_id + 
				", tap_type=" + tap_type + ", latitude=" + latitude + ", longitude=" + 
				longitude + ", location=" + location + ", zonal_reg=" + zonal_reg + 
				", cost=" + cost + ", timeStamp=" + timeStamp  + "]";
	}
}
