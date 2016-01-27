package in.vasista.vsales.db;

import in.vasista.vsales.facility.Facility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class FacilityDataSource {
	public static final String module = FacilityDataSource.class.getName();	  
	  
	  // Database fields 
	  private SQLiteDatabase database;
	  private MySQLiteHelper dbHelper;
	  private String[] allColumns = { MySQLiteHelper.COLUMN_FACILITY_ID,
	      MySQLiteHelper.COLUMN_FACILITY_NAME,
	      MySQLiteHelper.COLUMN_FACILITY_CAT,
	      MySQLiteHelper.COLUMN_FACILITY_PHONE_NUM,	 
	      MySQLiteHelper.COLUMN_FACILITY_SALESREP,	 
	      MySQLiteHelper.COLUMN_FACILITY_AM_ROUTE,
	      MySQLiteHelper.COLUMN_FACILITY_PM_ROUTE,
	      MySQLiteHelper.COLUMN_FACILITY_LATITUDE,
	      MySQLiteHelper.COLUMN_FACILITY_LONGITUDE};
	  
	  public FacilityDataSource(Context context) {
	    dbHelper = new MySQLiteHelper(context); 
	  } 

	  public void open() throws SQLException {
	    database = dbHelper.getWritableDatabase();
	  }

	  public void close() {
	    dbHelper.close();
	  }
	  
	  public void insertFacility(Facility facility) {
		    ContentValues values = new ContentValues();
		    values.put(MySQLiteHelper.COLUMN_FACILITY_ID, facility.getId());
		    values.put(MySQLiteHelper.COLUMN_FACILITY_NAME, facility.getName());
		    values.put(MySQLiteHelper.COLUMN_FACILITY_CAT, facility.getCategory());	
		    values.put(MySQLiteHelper.COLUMN_FACILITY_PHONE_NUM, facility.getOwnerPhone());	
		    values.put(MySQLiteHelper.COLUMN_FACILITY_SALESREP, facility.getSalesRep());		    		    		    
		    values.put(MySQLiteHelper.COLUMN_FACILITY_AM_ROUTE, facility.getAmRouteId());
		    values.put(MySQLiteHelper.COLUMN_FACILITY_PM_ROUTE, facility.getPmRouteId());
		    values.put(MySQLiteHelper.COLUMN_FACILITY_LATITUDE, facility.getLatitude());		    		    
		    values.put(MySQLiteHelper.COLUMN_FACILITY_LONGITUDE, facility.getLongitude());		    		    
		    
		    database.insert(MySQLiteHelper.TABLE_FACILITY, null, values);
	  }

	  public void insertFacilities(List<Facility> facilities) {    
		  database.beginTransaction();
		  try{
			  for (int i = 0; i < facilities.size(); ++i) {
				  Facility facility = facilities.get(i);
				    ContentValues values = new ContentValues();
				    values.put(MySQLiteHelper.COLUMN_FACILITY_ID, facility.getId());
				    values.put(MySQLiteHelper.COLUMN_FACILITY_NAME, facility.getName());
				    values.put(MySQLiteHelper.COLUMN_FACILITY_CAT, facility.getCategory());	
				    values.put(MySQLiteHelper.COLUMN_FACILITY_PHONE_NUM, facility.getOwnerPhone());	
				    values.put(MySQLiteHelper.COLUMN_FACILITY_SALESREP, facility.getSalesRep());		    		    		    
				    values.put(MySQLiteHelper.COLUMN_FACILITY_AM_ROUTE, facility.getAmRouteId());
				    values.put(MySQLiteHelper.COLUMN_FACILITY_PM_ROUTE, facility.getPmRouteId());
				    values.put(MySQLiteHelper.COLUMN_FACILITY_LATITUDE, facility.getLatitude());		    		    
				    values.put(MySQLiteHelper.COLUMN_FACILITY_LONGITUDE, facility.getLongitude());		    		    
				    
				    database.insert(MySQLiteHelper.TABLE_FACILITY, null, values);  
			  }
			  database.setTransactionSuccessful();
	  	  } catch(Exception e){
	  			Log.d(module, "facilities insert into db failed: " + e);	
	  	  } finally{
	  		 database.endTransaction();
	  	  }
	  }	
	  
	  public void deleteAllFacilities() {
		  database.delete(MySQLiteHelper.TABLE_FACILITY, null, null);
	  }

	  public Facility getFacilityDetails(String facilityId) {
		    Cursor cursor = database.query(MySQLiteHelper.TABLE_FACILITY,
		        allColumns, MySQLiteHelper.COLUMN_FACILITY_ID + " = '" + facilityId + "'", null, null, null, null);

		    cursor.moveToFirst();
		    Facility facility = cursorToFacility(cursor);
		    // Make sure to close the cursor
		    cursor.close();
		    return facility;  
	  }
	  
	  public List<Facility> getAllFacilities() {
		List<Facility> facilities = new ArrayList<Facility>();
	    String orderBy =  MySQLiteHelper.COLUMN_FACILITY_ID + " ASC";

	    Cursor cursor = database.query(MySQLiteHelper.TABLE_FACILITY,
	        allColumns, null, null, null, null, orderBy);

	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	    	Facility product = cursorToFacility(cursor);
	    	facilities.add(product);
	      cursor.moveToNext();
	    }
	    // Make sure to close the cursor
	    cursor.close();
	    return facilities;
	  }

	  private Facility cursorToFacility(Cursor cursor) {
		  Facility facility = new Facility(cursor.getString(0),
	    		cursor.getString(1),
	    		cursor.getString(2),
	    		cursor.getString(3),
	    		cursor.getString(4),	    		
	    		cursor.getString(5),
	    		cursor.getString(6),
	    		cursor.getString(7),
	    		cursor.getString(8)); 
	    return facility;
	  }
}
