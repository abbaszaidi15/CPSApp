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
	
	private static final String DATABASE_NAME = null;
	public static int DATABASE_VERSION;

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
		
/*		int currentVersion = db.getVersion();
		if (currentVersion == oldVersion){
			currentVersion = newVersion;
		}
		else {currentVersion = oldVersion;}
*/
		try{
			db.execSQL(sql);
		} catch (SQLiteException e) {
			System.out.println(e.getMessage());
		}
		
		onCreate(db);
	}
}
