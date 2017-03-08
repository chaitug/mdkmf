package in.vasista.vsales.db;

import in.vasista.location.Location;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class LocationsDataSource {
	public static final String module = LocationsDataSource.class.getName();

	private Context context;
	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns = { MySQLiteHelper.COLUMN_LOCATION_ID,
			MySQLiteHelper.COLUMN_LOCATION_CRDATE,
			MySQLiteHelper.COLUMN_LOCATION_LAT,
			MySQLiteHelper.COLUMN_LOCATION_LONG,
			MySQLiteHelper.COLUMN_LOCATION_NOTE_NAME,
			MySQLiteHelper.COLUMN_LOCATION_NOTE_INFO,
			MySQLiteHelper.COLUMN_LOCATION_ADDRESS,
			MySQLiteHelper.COLUMN_LOCATION_IS_SYNCED };

	public LocationsDataSource(Context context) {
		this.context = context;
		dbHelper = new MySQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public long insertLocation(double latitude,
			double longitude) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_LOCATION_CRDATE,
				System.currentTimeMillis());
		values.put(MySQLiteHelper.COLUMN_LOCATION_LAT, latitude);
		values.put(MySQLiteHelper.COLUMN_LOCATION_LONG, longitude);
		values.put(MySQLiteHelper.COLUMN_LOCATION_IS_SYNCED, 0);
		return database.insert(MySQLiteHelper.TABLE_LOCATION, null, values);
	}
	
	public long insertLocation(double latitude,
			double longitude,String address, long time) {
		String orderBy = MySQLiteHelper.COLUMN_LOCATION_CRDATE + " DESC";
		Cursor cursor = database.query(MySQLiteHelper.TABLE_LOCATION,
				allColumns, null, null, null, null, orderBy, "1");
		Location lastLocation = null;
		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			lastLocation = cursorToLocation(cursor);
		}
		cursor.close();
		if (lastLocation != null && lastLocation.getCreatedDate().getTime() == time) {
			// don't insert
			return lastLocation.getId();
		}
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_LOCATION_CRDATE, time);
		values.put(MySQLiteHelper.COLUMN_LOCATION_LAT, latitude);
		values.put(MySQLiteHelper.COLUMN_LOCATION_LONG, longitude);
		values.put(MySQLiteHelper.COLUMN_LOCATION_ADDRESS, address);
		values.put(MySQLiteHelper.COLUMN_LOCATION_IS_SYNCED, 0);
		return database.insert(MySQLiteHelper.TABLE_LOCATION, null, values);
	}	

	public long insertLocation(double latitude,
			double longitude, long time, String noteName, String noteInfo , String locAddress) {
		String orderBy = MySQLiteHelper.COLUMN_LOCATION_CRDATE + " DESC";
		Cursor cursor = database.query(MySQLiteHelper.TABLE_LOCATION,
				allColumns, null, null, null, null, orderBy, "1");
		Location lastLocation = null;
		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			lastLocation = cursorToLocation(cursor);
		}
		cursor.close();
		if (lastLocation != null && lastLocation.getCreatedDate().getTime() == time) {
			// don't insert
			return lastLocation.getId();
		}
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_LOCATION_CRDATE, time);
		values.put(MySQLiteHelper.COLUMN_LOCATION_LAT, latitude);
		values.put(MySQLiteHelper.COLUMN_LOCATION_LONG, longitude);
		values.put(MySQLiteHelper.COLUMN_LOCATION_NOTE_NAME, noteName);
		values.put(MySQLiteHelper.COLUMN_LOCATION_NOTE_INFO, noteInfo);
		values.put(MySQLiteHelper.COLUMN_LOCATION_ADDRESS, locAddress);
		values.put(MySQLiteHelper.COLUMN_LOCATION_IS_SYNCED, 0);
		return database.insert(MySQLiteHelper.TABLE_LOCATION, null, values);
	}	
	public List<Location> getAllLocations() {
		List<Location> locations = new ArrayList<Location>();
		String orderBy = MySQLiteHelper.COLUMN_LOCATION_CRDATE + " DESC";
		Cursor cursor = database.query(MySQLiteHelper.TABLE_LOCATION,
				allColumns, null, null, null, null, orderBy);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Location location = cursorToLocation(cursor);
			locations.add(location);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return locations;
	}

	private Location cursorToLocation(Cursor cursor) {
		Location location = new Location(cursor.getInt(0), new Date(
				cursor.getLong(1)), cursor.getDouble(2), cursor.getDouble(3),
				cursor.getString(4), cursor.getString(5),cursor.getString(6),
				(cursor.getInt(7) == 1) ? true : false);
		return location;
	}

	public List<Location> getSyncedLocations(){
		List<Location> locations = new ArrayList<Location>();
		String orderBy = MySQLiteHelper.COLUMN_LOCATION_CRDATE + " ASC";
		Cursor cursor = database.query(MySQLiteHelper.TABLE_LOCATION,
				allColumns, MySQLiteHelper.COLUMN_LOCATION_IS_SYNCED + " = " + 1, null, null, null, orderBy);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Location location = cursorToLocation(cursor);
			locations.add(location);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return locations;
	}

	public List<Location> getSyncedLocationsByCurrentDate(Date today){
		List<Location> locations = new ArrayList<Location>();
		String orderBy = MySQLiteHelper.COLUMN_LOCATION_CRDATE + " ASC";
		Cursor cursor = database.query(MySQLiteHelper.TABLE_LOCATION,
				allColumns, MySQLiteHelper.COLUMN_LOCATION_IS_SYNCED + " = " + 1 + " AND " + MySQLiteHelper.COLUMN_LOCATION_CRDATE + " = " + "'" +today+"'", null, null, null, orderBy);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Location location = cursorToLocation(cursor);
			locations.add(location);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return locations;
	}
	
	public List<Location> getUnsyncedLocations() {
		List<Location> locations = new ArrayList<Location>();
		Cursor cursor = database.query(MySQLiteHelper.TABLE_LOCATION,
				allColumns, MySQLiteHelper.COLUMN_LOCATION_IS_SYNCED + " = " + 0, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Location location = cursorToLocation(cursor);
			locations.add(location);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return locations;
	}	
	  public Map[] getXMLRPCSerializedUnsyncedLocations() {
		  List<Location> locationItems = getUnsyncedLocations();
		  if (locationItems.isEmpty()) {
			  return null;
		  }
		Log.d( module, "locationItems=" + locationItems);
		  Map [] result = new TreeMap[locationItems.size()];
		  for (int i = 0; i < locationItems.size(); ++i) {
			  Location loc = locationItems.get(i);
			  Map item = new TreeMap();
			  item.put("createdDate", loc.getCreatedDate());	    
			  item.put("latitude", loc.getLatitude());
			  item.put("longitude", loc.getLongitude());
			  item.put("noteName", loc.getNoteName());	   
			  item.put("noteInfo", loc.getNoteInfo());
			  item.put("address", loc.getAddress());
			  result[i] = item;	
		  }	     
		  return result;
	  }
	  
	  public void changeLocationsSyncStatus() {
		    ContentValues values = new ContentValues();
		    values.put(MySQLiteHelper.COLUMN_LOCATION_IS_SYNCED, 1);		    	    		    
		    final String[] whereArgs = { Integer.toString(0)};
		    int count = database.update(MySQLiteHelper.TABLE_LOCATION, values, MySQLiteHelper.COLUMN_LOCATION_IS_SYNCED + " = ?", whereArgs);
			Log.d( module, "changeLocationsSyncStatus: num rows updated:" + count); 		  		  	  
	  }

	public void updatePrevLocations(){
		database.delete(MySQLiteHelper.TABLE_LOCATION, MySQLiteHelper.COLUMN_LOCATION_IS_SYNCED + " = 1", null);

	}


	public long insertPrevLocation(double latitude,
								   double longitude, long time, String noteName, String noteInfo) {
		String orderBy = MySQLiteHelper.COLUMN_LOCATION_CRDATE + " DESC";
		Cursor cursor = database.query(MySQLiteHelper.TABLE_LOCATION,
				allColumns, null, null, null, null, orderBy, "1");
		Location lastLocation = null;
		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			lastLocation = cursorToLocation(cursor);
		}
		cursor.close();
		if (lastLocation != null && lastLocation.getCreatedDate().getTime() == time) {
			// don't insert
			return lastLocation.getId();
		}
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_LOCATION_CRDATE, time);
		values.put(MySQLiteHelper.COLUMN_LOCATION_LAT, latitude);
		values.put(MySQLiteHelper.COLUMN_LOCATION_LONG, longitude);
		values.put(MySQLiteHelper.COLUMN_LOCATION_NOTE_NAME, noteName);
		values.put(MySQLiteHelper.COLUMN_LOCATION_NOTE_INFO, noteInfo);
		values.put(MySQLiteHelper.COLUMN_LOCATION_IS_SYNCED, 1);
		return database.insert(MySQLiteHelper.TABLE_LOCATION, null, values);
	}
}
