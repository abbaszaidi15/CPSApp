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
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
 
        //-------Start of DB implementation
        //ParkingDbHelper Db = new ParkingDbHelper(this);
        Context context = getApplicationContext();
        initDB(context);
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
        
        //test
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
      //----------- DETECT ZONAL RATE FROM GPS COORDINATES---------//
        //double samplelat = 43.49860;
        //double samplelong = -80.5700;
        double samplelat = 43.611080;
        double samplelong = -79.650621;
        Boolean inWaterloo = getMyLocationAddress(samplelat,samplelong);
        if (inWaterloo){
	        int column=0;
	        int row=0;
	        
	        int latitude;
	        int longitude;
	        for (longitude=1; longitude <17; longitude++){
	        	if ( (samplelong > Double.parseDouble(list.get(0)[longitude])) && (samplelong < Double.parseDouble(list.get(0)[longitude+1]))){
	        		column = longitude+1;
	        		break;
	        	}
	        }
	        for (latitude=1; latitude <17; latitude++){
	        	if ( (samplelat < Double.parseDouble(list.get(latitude)[0])) && (samplelat > Double.parseDouble(list.get(latitude+1)[0]))){
	        		row = latitude+1;
	        		break;
	        	}
	        }
	        Log.e("CSVtag",list.get(row)[column]);
	        
    	}
        else{
        	Log.e("Address","Not in Waterloo");
        	//ADD ALERT TO WARN USER THAT THEY ARE NOT IN WATERLOO
        }
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
                  Log.e("Address",city);
              }
              else
            	  Log.e("Address","address not found");
    
        } 
        catch (IOException e) {
                 // TODO Auto-generated catch block
                 e.printStackTrace();
                 Toast.makeText(getApplicationContext(),"Could not get address..!", Toast.LENGTH_LONG).show();
        }
        if (city=="Waterloo"){
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
    
    private void initDB(Context context){
    	String DB_FULL_PATH = "";
    	CPSDatabaseHelper cpsDB = new CPSDatabaseHelper(context);
    	
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
            	new AlertDialog.Builder(MainActivity.this)
                .setTitle("Parking Entry")
                .setMessage("Parking transaction started: " + result)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) { 
                        // continue with delete
                    }
                 })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
                //outstring.setText("Read content: " + result);
            }
            //send message
        }
    }
}


