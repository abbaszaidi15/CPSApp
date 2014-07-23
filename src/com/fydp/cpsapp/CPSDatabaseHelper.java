/**
 * 
 */
package com.fydp.cpsapp;
import android.content.Context;
import android.database.sqlite.*;

/**
 * @author Gautham
 *
 */
public class CPSDatabaseHelper extends SQLiteOpenHelper{
	
	//Database Name
	private static final String DATABASE_NAME = "CPSDB";
	//Database version
	private static int DATABASE_VERSION = 1;

	//Parking System Data table name
	private static final String TABLE_PSD = "PSD";
	
	public CPSDatabaseHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		
	}
	
	@Override
	public void onCreate(SQLiteDatabase db){
		//SQL statement to create Parking System Data table
		String createPSDTable = "CREATE TABLE PSD (id INTEGER PRIMARY KEY AUTOINCREMENT," + 
								"cps_user_id TEXT, device_id TEXT, tap_type TEXT, latitude TEXT" + 
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
		String sql = "Drop TABLE IF EXISTS PSD";
		
		try{
			db.execSQL(sql);
			this.onCreate(db);
		} catch (SQLiteException e) {
			System.out.println(e.getMessage());
		}
	}

}
