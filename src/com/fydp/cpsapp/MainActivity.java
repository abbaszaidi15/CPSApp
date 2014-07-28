package com.fydp.cpsapp;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.app.Dialog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

//
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.location.Address;
import android.location.Geocoder;
import android.nfc.FormatException;
import android.nfc.NfcAdapter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.util.Log;




//

//Database Classes
//It is a public class in the same package, do not need to import it here.
//just call it below.
//
public class MainActivity extends Activity {
	
	public static final String MIME_TEXT_PLAIN = "text/plain";
    public static final String TAG = "NfcDemo";
 
    private EditText outstring;
    private NfcAdapter mNfcAdapter;
    
    Tag mytag;
    
    int mode;
    int START = 1;
    int STOP = 0;
    
    

 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
	
        mode = STOP;
 
        //-------Start of DB implementation
        CPSDatabaseHelper db = new CPSDatabaseHelper(this);
        
        //add data entries
        db.addCPSData(new CPSData("CPS-000-001", "1X1-A56-$68", "START","","","","","","07/2014"));
        db.addCPSData(new CPSData("CPS-000-001", "1X1-A56-$68", "STOP","","","","","","08/2014"));
        db.addCPSData(new CPSData("CPS-000-002", "1T8-H38-$93", "START","","","","","","07/2015"));
        db.addCPSData(new CPSData("CPS-000-002", "1T8-H38-$93", "STOP","","","","","","08/2015"));
        // get all cps data
       // List<CPSData> cpsDataList = db.getAllCPSData();
 
        // delete one book
        //db.deleteBook(list.get(0));
 
        // get all data
        //db.getAllCPSData();
        
        //-------End of DB implementation
        
        outstring = (EditText) findViewById(R.id.editText1);
 
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
 
        if (mNfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
            return;
 
        }
     
        if (!mNfcAdapter.isEnabled()) {
            outstring.setText("NFC is disabled.");
        } else {
            outstring.setText("NFC is enabled.");
        }
        
        //-------------PARSE WATERLOO COORDINATES INTO ARRAY-------------//
        String next[] = {};
        final List<String[]> list = new ArrayList<String[]>();

        try {
            CSVReader reader = new CSVReader(new InputStreamReader(getAssets().open("WaterlooGrid.csv")));
            while((next = reader.readNext()) != null){
            	list.add(next);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
      //-------------END OF PARSE WATERLOO COORDINATES INTO ARRAY-------------//
        
      //----------- DETECT ZONAL RATE FROM GPS COORDINATES---------//
        
        //Sample street location: 295 Lester, Waterloo, ON
        String testlocation = "$GPRMC,180341.000,A,4328.5502,N,08032.1746,W,0.00,225.94,090714,,,A*7D<END>";
        String[] locationArray =  testlocation.split(",");
        
        //extract latitude
        String latdegrees = locationArray[3].substring(0,2);
        String latminutes = locationArray[3].substring(2,9);
        Double latitude1 = Double.parseDouble(latdegrees) + ((Double.parseDouble(latminutes))/60);
        latitude1 = (double) Math.round(latitude1*100000) / 100000;
       
        if (locationArray[4].equals("S")){
        	latitude1 *= -1;
        }
        
        //extract longitude
        String longdegrees = locationArray[5].substring(0,3);
        String longminutes = locationArray[5].substring(3,10);
        Double longitude1 = Double.parseDouble(longdegrees) + ((Double.parseDouble(longminutes))/60);
        longitude1 = (double) Math.round(longitude1*100000) / 100000;
        
        if (locationArray[6].equals("W")){
        	longitude1 *= -1;
        }
        Log.e("GPS",String.valueOf(latitude1));
        Log.e("GPS",String.valueOf(longitude1));
        
        //Sample coordinates in Mississauga
        //latitude1 = 43.611080;
        //longitude1 = -79.650621;
        
        //check if location is in Waterloo
        Boolean inWaterloo = getMyLocationAddress(latitude1,longitude1);
        boolean entryadded;
        //if location is in Waterloo, find zonal rate
        if (inWaterloo){
	        int column=0;
	        int row=0;
	        
	        int rownum;
	        int columnnum;
	        for (columnnum=1; columnnum <17; columnnum++){
	        	if ( (longitude1 > Double.parseDouble(list.get(0)[columnnum])) && (longitude1 < Double.parseDouble(list.get(0)[columnnum+1]))){
	        		column = columnnum+1;
	        		break;
	        	}
	        }
	        for (rownum=1; rownum <17; rownum++){
	        	if ( (latitude1 < Double.parseDouble(list.get(rownum)[0])) && (latitude1 > Double.parseDouble(list.get(rownum+1)[0]))){
	        		row = rownum+1;
	        		break;
	        	}
	        }
	        Log.e("Zone",list.get(row)[column]);
	        
	        //ADD CODE TO ADD TO DATABASE
	        
	        //set successful entry flag
	        entryadded = true;
    	}
        
        //else log error
        else{
        	Log.e("Address","Not in Waterloo");
        	entryadded = false;
        }
       /*
        String entryalerttitle;
        String entrymessage;
        if (entryadded){
        	entryalerttitle = "Success";
        	entrymessage = "The data has been successfully added";        	
        }
        else{
        	entryalerttitle = "Error";
        	entrymessage = "Unfortunately, the data could not be added. Please try again";
        	
        }
        
        //inform user whether data was added successfully
        new AlertDialog.Builder(MainActivity.this)
        .setTitle(entryalerttitle)
        .setMessage(entrymessage)
        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) { 
                // continue with delete
            }
         })
        .setIcon(android.R.drawable.ic_dialog_alert)
        .show();
        
        */
        //----------- END OF DETECT ZONAL RATE FROM GPS COORDINATES---------//
        
        
        handleIntent(getIntent());
    }
    
    //----------------------REVERSE GEOCODING-------------------------//
    public boolean getMyLocationAddress(double latitude, double longitude) {
        
        Geocoder geocoder= new Geocoder(this, Locale.ENGLISH);
        String city = "";
        try {
               
              //Place your latitude and longitude
              List<Address> addresses = geocoder.getFromLocation(latitude,longitude, 1);
              
              if(addresses != null) {
               
                  Address fetchedAddress = addresses.get(0);
                  StringBuilder strAddress = new StringBuilder();
                
                  for(int i=0; i<fetchedAddress.getMaxAddressLineIndex(); i++) {
                        strAddress.append(fetchedAddress.getAddressLine(i)).append(", ");
                  }
                  String[] address =  strAddress.toString().split(",");
                  city = address[1].replaceAll("\\s+","");
                  Log.e("Address","Your current city is: " + city);
              }
              else
            	  Log.e("Address","Address not found");
    
        } 
        catch (IOException e) {
                 // TODO Auto-generated catch block
                 e.printStackTrace();
                 Toast.makeText(getApplicationContext(),"Could not get address..!", Toast.LENGTH_LONG).show();
        }
        if (city.equals("Waterloo")){
        	return true;
        }
        else{
        	return false;
        }
    }
    //----------------------END OF REVERSE GEOCODING-----------------------//
     
    @Override
    protected void onResume() {
        super.onResume();
         
        /**
         * It's important, that the activity is in the foreground (resumed). Otherwise
         * an IllegalStateException is thrown. 
         */
        setupForegroundDispatch(this, mNfcAdapter);
    }
     
    @Override
    protected void onPause() {
        /**
         * Call this before onPause, otherwise an IllegalArgumentException is thrown as well.
         */
        stopForegroundDispatch(this, mNfcAdapter);
         
        super.onPause();
    }
     
    @Override
    protected void onNewIntent(Intent intent) { 
        /**
         * This method gets called, when a new Intent gets associated with the current activity instance.
         * Instead of creating a new activity, onNewIntent will be called. For more information have a look
         * at the documentation.
         * 
         * In our case this method gets called, when the user attaches a Tag to the device.
         */
    	mytag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        handleIntent(intent);
    }
     
    private void handleIntent(Intent intent) {
        
    	String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
             
            String type = intent.getType();
            if (MIME_TEXT_PLAIN.equals(type)) {
     
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                new NdefReaderTask().execute(tag);
                 
            } else {
                Log.d(TAG, "Wrong mime type: " + type);
            }
        } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
             
            // In case we would still use the Tech Discovered Intent
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String[] techList = tag.getTechList();
            String searchedTech = Ndef.class.getName();
             
            for (String tech : techList) {
                if (searchedTech.equals(tech)) {
                    new NdefReaderTask().execute(tag);
                    break;
                }
            }
        }
    	
    }
     
    /**
     * @param activity The corresponding {@link Activity} requesting the foreground dispatch.
     * @param adapter The {@link NfcAdapter} used for the foreground dispatch.
     */
    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
 
        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);
 
        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};
 
        // Notice that this is the same filter as in our manifest.
        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType(MIME_TEXT_PLAIN);
        } catch (MalformedMimeTypeException e) {
            throw new RuntimeException("Check your mime type.");
        }
         
        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }
 
    /**
     * @param activity The corresponding {@link BaseActivity} requesting to stop the foreground dispatch.
     * @param adapter The {@link NfcAdapter} used for the foreground dispatch.
     */
    public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }
 
    public void viewParkingHistory(View view){
    	Intent intent = new Intent(MainActivity.this, ParkingHistory.class);
    	startActivity(intent);
    }
    private class NdefReaderTask extends AsyncTask<Tag, Void, String> {
    	 
        @Override
        protected String doInBackground(Tag... params) {
            Tag tag = params[0];
             
            Ndef ndef = Ndef.get(tag);
            if (ndef == null) {
                // NDEF is not supported by this Tag. 
                return null;
            }
     
            NdefMessage ndefMessage = ndef.getCachedNdefMessage();
     
            NdefRecord[] records = ndefMessage.getRecords();
            for (NdefRecord ndefRecord : records) {
                if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                    try {
                        return readText(ndefRecord);
                    } catch (UnsupportedEncodingException e) {
                        Log.e(TAG, "Unsupported Encoding", e);
                    }
                }
            }
     
            return null;
        }
         
        private String readText(NdefRecord record) throws UnsupportedEncodingException {
            /*
             * See NFC forum specification for "Text Record Type Definition" at 3.2.1 
             * 
             * http://www.nfc-forum.org/specs/
             * 
             * bit_7 defines encoding
             * bit_6 reserved for future use, must be 0
             * bit_5..0 length of IANA language code
             */
     
            byte[] payload = record.getPayload();
     
            // Get the Text Encoding
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
     
            // Get the Language Code
            int languageCodeLength = payload[0] & 0063;
             
            // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
            // e.g. "en"
             
            // Get the Text
            return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        }
         
        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
            	
            	//outstring.setText("Read content: " + result);
            	//determine if user has the application
            	if(result.equals("Please download the CPS App from www.CPSApp.com")){
            
            		//determine the tag UID
            		CharSequence UID =  bin2hex(mytag.getId());
            		
            		//ensure the the UID matches
            		if(UID.equals("08ABCDEF")){            		
	            		
		 
		                //outstring.setText("Read content: " + result);
		                String writeMessage;
						try {
							if(mytag==null){
								//Toast.makeText(this, "error detected, no tag", Toast.LENGTH_LONG ).show();
								writeMessage = "error detected, no tag";
							}else{
								if(mode == START){
									write("stop parking",mytag);	
								}
								else if(mode == STOP){
									write("start parking",mytag);
								}
							}
								
								writeMessage = "write successful";
								//Toast.makeText(this, "writing successful", Toast.LENGTH_LONG ).show();
							
						} catch (IOException e) {
							//Toast.makeText(this, "error writing", Toast.LENGTH_LONG ).show();
							writeMessage = "error writing, try again";
							e.printStackTrace();
						} catch (FormatException e) {
							//Toast.makeText(this, "error writing" , Toast.LENGTH_LONG ).show();
							writeMessage = "error writing, try again";
							e.printStackTrace();
						}
						
						if(writeMessage.equals("write successful")){
							if(mode == START) {
								mode = STOP;
								writeMessage = "Parking Transation Stopped";
							}
							else if(mode == STOP) {
								mode = START;
								writeMessage = "Parking Transation Started";
							}
			           	}

			           	new AlertDialog.Builder(MainActivity.this)
		                .setTitle("Parking Entry")
		                .setMessage(writeMessage)
		                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
		                    public void onClick(DialogInterface dialog, int which) { 
		                        // continue with delete
		                    }
		                 })
		                .setIcon(android.R.drawable.ic_dialog_alert)
		                .show();
			           	
			           	
			           	
            		}
            		else{
		            	new AlertDialog.Builder(MainActivity.this)
		                .setTitle("Parking Entry")
		                .setMessage("UID is incorrect")
		                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
		                    public void onClick(DialogInterface dialog, int which) { 
		                        // continue with delete
		                    }
		                 })
		                .setIcon(android.R.drawable.ic_dialog_alert)
		                .show();
            		
            		}
            	}

            	

            }
            //send message
        }
        
    	private NdefRecord createRecord(String text) throws UnsupportedEncodingException {
    		String lang       = "en";
    		byte[] textBytes  = text.getBytes();
    		byte[] langBytes  = lang.getBytes("US-ASCII");
    		int    langLength = langBytes.length;
    		int    textLength = textBytes.length;
    		byte[] payload    = new byte[1 + langLength + textLength];

    		// set status byte (see NDEF spec for actual bits)
    		payload[0] = (byte) langLength;

    		// copy langbytes and textbytes into payload
    		System.arraycopy(langBytes, 0, payload, 1,              langLength);
    		System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength);

    		NdefRecord recordNFC = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,  NdefRecord.RTD_TEXT,  new byte[0], payload);

    		return recordNFC;
    	}
        
        private String bin2hex(byte [] inarray) {
    	    int i, j, in;
    	    String [] hex = {"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"};
    	    String out= "";
    	 
    	    for(j = 0 ; j < inarray.length ; ++j) 
    	        {
    	        in = (int) inarray[j] & 0xff;
    	        i = (in >> 4) & 0x0f;
    	        out += hex[i];
    	        i = in & 0x0f;
    	        out += hex[i];
    	        }
    	    return out;
    	}
        
        private void write(String message , Tag tag) throws IOException, FormatException {
            NdefRecord[] records = { createRecord(message) };
            NdefMessage  ndefMessage = new NdefMessage(records);

            Log.e("rajan", "writing tag 5");
            // Get an instance of Ndef for the tag.
            Ndef ndef = Ndef.get(tag);

            // Enable I/O
            ndef.connect();

            // Write the message
            ndef.writeNdefMessage(ndefMessage);

            // Close the connection
            ndef.close();
            Log.e("rajan", "writing tag 6");
        }

    }
}


