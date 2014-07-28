package com.fydp.cpsapp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class DetailParkingHistory extends Activity {
	
	private TextView dataOut;
	private CharSequence text;
/*	public DetailParkingHistory()
	{
	}
*/	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_detailed_history_entry);
	    
	    dataOut = (TextView) findViewById(R.id.details);
		int id = Integer.parseInt(getIntent().getExtras().getString("dataID"));
		CPSDatabaseHelper db = new CPSDatabaseHelper(getApplicationContext());
		CPSData data = db.getCPSData(id);
	    text = data.toString();
		dataOut.setText(text);
	    
	}
}
