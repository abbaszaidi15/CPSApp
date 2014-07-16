/**
 * 
 */
package com.fydp.cpsapp;
import android.content.Context;
import android.database.*;
import android.database.sqlite.*;

/**
 * @author Gautham
 *
 */
public class CPSDatabase extends SQLiteOpenHelper{
	
	private static final String DATABASE_NAME = "sampleDB";
	public static int DATABASE_VERSION = 1;

	public CPSDatabase(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		
	}
	
	@Override
	public void onCreate(SQLiteDatabase db){
		String createSql = "Create TABLE sampleTable (FirstName TEXT Primary Key, LastName TEXT, PHONENUM INTEGER)" ;
		
		try{
		db.execSQL(createSql);
		} catch (SQLiteException e) {
			System.out.println(e.getMessage());
		}
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
		String sql = "Drop TABLE sampleDB.sampleTable;";
		String DB_FULL_PATH = "";
		
		int currentVersion = db.getVersion();
		
		if (currentVersion == oldVersion){
			currentVersion = newVersion;
		}
		else {currentVersion = oldVersion;}
		
		if (checkDataBase(DB_FULL_PATH)){
			
		} /*else{
			cpsDB.onCreate(cpsDB);d
		}*/

		try{
			db.execSQL(sql);
			onCreate(db);
		} catch (SQLiteException e) {
			System.out.println(e.getMessage());
		}
	}

	
	private boolean checkDataBase(String DB_FULL_PATH){
		SQLiteDatabase checkDB = null;
		try{
	        checkDB = SQLiteDatabase.openDatabase(DB_FULL_PATH, null,
	                SQLiteDatabase.OPEN_READONLY);
	        checkDB.close();
		} catch (SQLiteException e){
		// database doesn't exist yet
		}
		return checkDB != null ? true : false;
	}
}
