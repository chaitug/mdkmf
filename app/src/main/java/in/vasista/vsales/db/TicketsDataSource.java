package in.vasista.vsales.db;


import in.vasista.tm.Ticket;

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

public class TicketsDataSource {
	public static final String module = TicketsDataSource.class.getName();

	private Context context;
	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns = { MySQLiteHelper.COLUMN_TICKET_ID_INTERNAL,
			MySQLiteHelper.COLUMN_TICKET_ID,
			MySQLiteHelper.COLUMN_TICKET_CRDATE,
			MySQLiteHelper.COLUMN_TICKET_LAT,
			MySQLiteHelper.COLUMN_TICKET_LONG,
			MySQLiteHelper.COLUMN_TICKET_DESC,
			MySQLiteHelper.COLUMN_TICKET_TYPE_ID,	
			MySQLiteHelper.COLUMN_TICKET_STATUS,						
			MySQLiteHelper.COLUMN_TICKET_IS_SYNCED };

	public TicketsDataSource(Context context) {
		this.context = context;
		dbHelper = new MySQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	

	public long insertTicket(double latitude,
			double longitude, long time, String desc, String type) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_TICKET_CRDATE, time);
		values.put(MySQLiteHelper.COLUMN_TICKET_LAT, latitude);
		values.put(MySQLiteHelper.COLUMN_TICKET_LONG, longitude);
		values.put(MySQLiteHelper.COLUMN_TICKET_DESC, desc);
		values.put(MySQLiteHelper.COLUMN_TICKET_STATUS, "CREATED");		//::TODO::
		values.put(MySQLiteHelper.COLUMN_TICKET_TYPE_ID, type);		
		values.put(MySQLiteHelper.COLUMN_TICKET_IS_SYNCED, 0);
		return database.insert(MySQLiteHelper.TABLE_TICKET, null, values);
	}	
	public List<Ticket> getAllTickets() {
		List<Ticket> tickets = new ArrayList<Ticket>();
		String orderBy = MySQLiteHelper.COLUMN_TICKET_CRDATE + " DESC";
		Cursor cursor = database.query(MySQLiteHelper.TABLE_TICKET,
				allColumns, null, null, null, null, orderBy);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Ticket ticket = cursorToTicket(cursor);
			tickets.add(ticket);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return tickets;
	}

	private Ticket cursorToTicket(Cursor cursor) {
		Ticket ticket = new Ticket(cursor.getInt(0), cursor.getString(1),
				new Date(cursor.getLong(2)), cursor.getDouble(3), cursor.getDouble(4),
				cursor.getString(5), cursor.getString(6),
				cursor.getString(7), (cursor.getInt(8) == 1) ? true : false);
		return ticket;
	}
	
	public List<Ticket> getUnsyncedTickets() {
		List<Ticket> tickets = new ArrayList<Ticket>();
		Cursor cursor = database.query(MySQLiteHelper.TABLE_TICKET,
				allColumns, MySQLiteHelper.COLUMN_TICKET_IS_SYNCED + " = " + 0, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Ticket ticket = cursorToTicket(cursor);
			tickets.add(ticket);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return tickets;
	}	
	
	public Map[] getXMLRPCSerializedUnsyncedTickets() {
		  List<Ticket> ticketItems = getUnsyncedTickets();
		  if (ticketItems.isEmpty()) {
			  return null;
		  }
		Log.d( module, "ticketItems=" + ticketItems); 	
		Map [] result = new TreeMap[ticketItems.size()];
		/*
		  for (int i = 0; i < ticketItems.size(); ++i) {
			  Location loc = locationItems.get(i);
			  Map item = new TreeMap();
			  item.put("createdDate", loc.getCreatedDate());	    
			  item.put("latitude", loc.getLatitude());	   
			  item.put("longitude", loc.getLongitude());
			  item.put("noteName", loc.getNoteName());	   
			  item.put("noteInfo", loc.getNoteInfo());	   			  
			  result[i] = item;	
		  }	     
		  */
		  return result;
	  }
	  
	  public void changeTicketsSyncStatus() {
		    ContentValues values = new ContentValues();
		    values.put(MySQLiteHelper.COLUMN_TICKET_IS_SYNCED, 1);		    	    		    
		    final String[] whereArgs = { Integer.toString(0)};
		    int count = database.update(MySQLiteHelper.TABLE_TICKET, values, MySQLiteHelper.COLUMN_TICKET_IS_SYNCED + " = ?", whereArgs);
			Log.d( module, "changeTicketsSyncStatus: num rows updated:" + count); 		  		  	  
	  }
}
