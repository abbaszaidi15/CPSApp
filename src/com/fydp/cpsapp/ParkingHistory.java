package com.fydp.cpsapp;

import java.lang.reflect.Field;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;

public class ParkingHistory extends Activity {
	
	private ListView parkingHistory;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_viewhistory);
	    
	    //-----------------------Hiding the day picker
	    DatePicker picker;
	    picker = (DatePicker) findViewById(R.id.date);
	    try {
	        Field f[] = picker.getClass().getDeclaredFields();
	        for (Field field : f) {
	            if (field.getName().equals("mDaySpinner")) {
	                field.setAccessible(true);
	                Object dayPicker = new Object();
	                dayPicker = field.get(picker);
	                ((View) dayPicker).setVisibility(View.GONE);
	            }
	        }
	    } 
	    catch (SecurityException e) {
	        Log.d("ERROR", e.getMessage());
	    } 
	    catch (IllegalArgumentException e) {
	        Log.d("ERROR", e.getMessage());
	    } 
	    catch (IllegalAccessException e) {
	        Log.d("ERROR", e.getMessage());
	    }
	    //------------------End of Hide Day Picker
	    
	    //------------------Populate List View with query
	    parkingHistory = (ListView) findViewById(R.id.parkingHistory);
	    parkingHistory.setBackgroundColor(Color.CYAN);
	    populateList();
	    //-------------------
	    
	    //-------------------Click items in list view
		parkingHistory.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long arg){
				CPSData listItem = (CPSData) parent.getItemAtPosition(position);
				CPSDatabaseHelper db = new CPSDatabaseHelper(getApplicationContext());
				
				CPSData dataEntry = db.getCPSData(listItem.getId());
				
				if (dataEntry != null){
					
					Intent intent = new Intent(ParkingHistory.this, DetailParkingHistory.class);
					intent.putExtra("dataID", Integer.toString(dataEntry.getId()));
					startActivity(intent);
				}
			}
		});
		//-------------------
	 }
	
	protected void onListItemClick(ListView parkingHistory, View view, int position, long id){
		
	}
	
	public void populateList(){
		CPSDatabaseHelper db = new CPSDatabaseHelper(this.getApplicationContext());
		Log.i("list",db.getDatabaseName());
		List<CPSData> cpsDataList = db.getAllCPSData("","");
		 
		ArrayAdapter<CPSData> myAdapter = new ArrayAdapter<CPSData>(this, 
		android.R.layout.simple_list_item_1,cpsDataList);
		
		parkingHistory.setAdapter(myAdapter);
	}
	
	public void filterHistory(View view){
		DatePicker datePicker = (DatePicker) findViewById(R.id.date);
		int year = datePicker.getYear();
		int month = datePicker.getMonth();
		
		CPSDatabaseHelper db = new CPSDatabaseHelper(this.getApplicationContext());
		Log.i("list",db.getDatabaseName());
		List<CPSData> cpsDataList = db.getAllCPSData(Integer.toString(month),Integer.toString(year));
		 
		ArrayAdapter<CPSData> myAdapter = new ArrayAdapter<CPSData>(this, 
		android.R.layout.simple_list_item_1,cpsDataList);
		
		parkingHistory.setAdapter(myAdapter);
	}
	
	public void showDetails(){
		
	}
}
