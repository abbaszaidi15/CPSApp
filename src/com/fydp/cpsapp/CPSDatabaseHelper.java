/**
 * 
 */
package com.fydp.cpsapp;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;
import android.util.Log;

/**
 * @author Gautham
 *
 */
public class CPSDatabaseHelper extends SQLiteOpenHelper{
	
	//Database Name
	private static final String DATABASE_NAME = "CPSDB";
	//Database version
	public static int DATABASE_VERSION = 1;

	//Parking System Data table name
	private static final String TABLE_PSD = "PSD";
	
	//Parking System Data columns
	private static final String KEY_ID = "id";
	private static final String KEY_USER = "cps_user_id";
	private static final String KEY_DEVICE = "device_id";
	private static final String KEY_STATE = "tap_type";
	private static final String KEY_LAT = "latitude";
	private static final String KEY_LONG = "longitude";
	private static final String KEY_LOC = "location";
	private static final String KEY_REG = "zonal_reg";
	private static final String KEY_COST = "cost";
	private static final String KEY_TIME = "timeStamp";
	
	//columns
	private static final String[] COLUMNS_PSD = {KEY_ID,KEY_USER,KEY_DEVICE,KEY_STATE,KEY_LAT,
												KEY_LONG,KEY_LOC,KEY_REG,KEY_COST,KEY_TIME};
	
	public CPSDatabaseHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		
	}
	
	@Override
	public void onCreate(SQLiteDatabase db){
/*		String sql = "Drop TABLE IF EXISTS PSD";// + DATABASE_NAME+"."+TABLE_PSD;
		
		try{
		db.execSQL(sql);
		} catch (SQLiteException e) {
			System.out.println(e.getMessage());
		}*/
		
		//SQL statement to create Parking System Data table
		String createPSDTable = "CREATE TABLE PSD (id INTEGER PRIMARY KEY AUTOINCREMENT," + 
								"cps_user_id TEXT, device_id TEXT, tap_type TEXT, latitude TEXT," + 
								"longitude TEXT, location TEXT, zonal_reg TEXT, cost TEXT," +  
								"timeStamp TEXT)" ;
		try{
		db.execSQL(createPSDTable);
		} catch (SQLiteException e) {
			System.out.println(e.getMessage());
		}
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
		String sql = "Drop TABLE IF EXISTS PSD";// + DATABASE_NAME+"."+TABLE_PSD;
		
		try{
			db.execSQL(sql);
			this.onCreate(db);
		} catch (SQLiteException e) {
			System.out.println(e.getMessage());
		}
	}
	
	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    onUpgrade(db, oldVersion, newVersion);
	}
	
	public void addCPSData(CPSData data){
		//logging
//		Log.d("addData", data.toString());
		
		//1. get reference to writable db
		SQLiteDatabase db = this.getWritableDatabase();
		
		//2. create ContentValues to add key "column"/value
		ContentValues values = new ContentValues();
		values.put(KEY_USER, data.getUserID());
		values.put(KEY_DEVICE, data.getDeviceID());
		values.put(KEY_STATE, data.getTapType());
		values.put(KEY_LAT, data.getLatitude());
		values.put(KEY_LONG, data.getLongitude());
		values.put(KEY_LOC, data.getLocation());
		values.put(KEY_REG, data.getZonalReg());
		values.put(KEY_COST, data.getCost());
		values.put(KEY_TIME, data.getTimeStamp());
		
		//3. insert the data into the db
		try{
			db.insert(TABLE_PSD, null, values);
		}catch(SQLiteException e) {
			System.out.println(e.getMessage());
		}
		db.close();
	}

	public CPSData getCPSData(int id){
		//1. get reference to readable DB
		SQLiteDatabase db  = this.getReadableDatabase();
		
		//2. build query 
		Cursor cursor = 
	            db.query(TABLE_PSD, // a. table
	            COLUMNS_PSD, // b. column names
	            " id = ?", // c. selections 
	            new String[] { String.valueOf(id) }, // d. selections args
	            null, // e. group by
	            null, // f. having
	            null, // g. order by
	            null); // h. limit
	 
	    // 3. if we got results get the first one
	    if (cursor != null)
	        cursor.moveToFirst();
	 
	    // 4. build CPSData object
	    CPSData data = new CPSData();
	    data.setId(Integer.parseInt(cursor.getString(0)));
	    data.setUserID(cursor.getString(1));
	    data.setDeviceID(cursor.getString(2));
	    data.setTapType(cursor.getString(3));
	    data.setLatitude(cursor.getString(4));
	    data.setLongitude(cursor.getString(5));
	    data.setLocation(cursor.getString(5));
	    data.setZonalReg(cursor.getString(6));
	    data.setCost(cursor.getString(7));
	    data.setTimeStamp(cursor.getString(8));
	   
	    //log 
	    Log.d("getData("+id+")", data.toString());
	 
	    // 5. return book
	    return data;
	}
	
	public List<CPSData> getAllCPSData() {
	       List<CPSData> allData = new LinkedList<CPSData>();
	 
	       // 1. build the query
	       String selectQuery = "SELECT  * FROM " + TABLE_PSD;
	 
	       // 2. get reference to writable DB
	       SQLiteDatabase db = this.getWritableDatabase();
	       Cursor cursor = db.rawQuery(selectQuery, null);
	 
	       // 3. go over each row, build book and add it to list
	       CPSData dataEntry = null;
	       if (cursor.moveToFirst()) {
	           do {
	               dataEntry = new CPSData();
	               dataEntry.setId(Integer.parseInt(cursor.getString(0)));
		       	   dataEntry.setUserID(cursor.getString(1));
		       	   dataEntry.setDeviceID(cursor.getString(2));
		       	   dataEntry.setTapType(cursor.getString(3));
		       	   dataEntry.setLatitude(cursor.getString(4));
		       	   dataEntry.setLongitude(cursor.getString(5));
		       	   dataEntry.setLocation(cursor.getString(5));
		       	   dataEntry.setZonalReg(cursor.getString(6));
		       	   dataEntry.setCost(cursor.getString(7));
		       	   dataEntry.setTimeStamp(cursor.getString(8));
	 
	               // Add data entry to allData
	               allData.add(dataEntry);
	           } while (cursor.moveToNext());
	       }
	 
	       Log.d("getAllCPSData()", allData.toString());
	 
	       // return entire list
	       return allData;
	   }
	
	public List<CPSData> getAllCPSData(int year){
		List<CPSData> allData = new LinkedList<CPSData>();
		
		 // 1. build the query
	       String selectQuery = "SELECT  * FROM " + TABLE_PSD + " where timestamp='" + year + "'";
	 
	       // 2. get reference to writable DB
	       SQLiteDatabase db = this.getWritableDatabase();
	       Cursor cursor = db.rawQuery(selectQuery, null);
	 
	       // 3. go over each row, build book and add it to list
	       CPSData dataEntry = null;
	       if (cursor.moveToFirst()) {
	           do {
	               dataEntry = new CPSData();
	               dataEntry.setId(Integer.parseInt(cursor.getString(0)));
		       	   dataEntry.setUserID(cursor.getString(1));
		       	   dataEntry.setDeviceID(cursor.getString(2));
		       	   dataEntry.setTapType(cursor.getString(3));
		       	   dataEntry.setLatitude(cursor.getString(4));
		       	   dataEntry.setLongitude(cursor.getString(5));
		       	   dataEntry.setLocation(cursor.getString(5));
		       	   dataEntry.setZonalReg(cursor.getString(6));
		       	   dataEntry.setCost(cursor.getString(7));
		       	   dataEntry.setTimeStamp(cursor.getString(8));
	               // Add data entry to allData
	               allData.add(dataEntry);
	           } while (cursor.moveToNext());
	       }
	 
	       // return entire list
	       return allData;
	   }
	
	public int updateCPSData(CPSData data){
	    // 1. get reference to writable DB
	    SQLiteDatabase db = this.getWritableDatabase();
	 
	    // 2. create ContentValues to add key "column"/value
	    ContentValues values = new ContentValues();
	    values.put(KEY_USER, data.getUserID());
		values.put(KEY_DEVICE, data.getDeviceID());
		values.put(KEY_STATE, data.getTapType());
		values.put(KEY_LAT, data.getLatitude());
		values.put(KEY_LONG, data.getLongitude());
		values.put(KEY_LOC, data.getLocation());
		values.put(KEY_REG, data.getZonalReg());
		values.put(KEY_COST, data.getCost());
		values.put(KEY_TIME, data.getTimeStamp());
		
	 
	    // 3. updating row
	    int i = db.update(TABLE_PSD, //table
	            values, // column/value
	            KEY_ID+" = ?", // selections
	            new String[] { String.valueOf(data.getId()) }); //selection args
	 
	    // 4. close
	    db.close();
	 
	    return i;
	}
	
	public void deleteCPSData(CPSData data) {
		 
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
 
        // 2. delete
        db.delete(TABLE_PSD, //table name
                KEY_ID+" = ?",  // selections
                new String[] { String.valueOf(data.getId()) }); //selections args
 
        // 3. close
        db.close();
 
        //log
        Log.d("deleteBook", data.toString());
    }
}
