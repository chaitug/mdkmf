package in.vasista.vsales.db;

import in.vasista.hr.attendance.Attendance;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class AttendanceDataSource {
	public static final String module = AttendanceDataSource.class.getName();	
	private Context context;	
	  // Database fields
	  private SQLiteDatabase database;
	  private MySQLiteHelper dbHelper;
	  private String[] allColumns = { MySQLiteHelper.COLUMN_EMPLOYEE_ATTENDANCE_ID,
	      MySQLiteHelper.COLUMN_EMPLOYEE_ID,
	      MySQLiteHelper.COLUMN_EMPLOYEE_ATTENDANCE_IN_TIME,
	      MySQLiteHelper.COLUMN_EMPLOYEE_ATTENDANCE_OUT_TIME,
	      MySQLiteHelper.COLUMN_EMPLOYEE_ATTENDANCE_DURATION};
	  
	  public AttendanceDataSource(Context context) {
		  this.context = context;		  
	    dbHelper = new MySQLiteHelper(context); 
	  }

	  public void open() throws SQLException {
	    database = dbHelper.getWritableDatabase();
	  }

	  public void close() {
	    dbHelper.close();
	  }	  
	  
	  long insertPunch(Attendance attendance) {
		    ContentValues values = new ContentValues();
		    values.put(MySQLiteHelper.COLUMN_EMPLOYEE_ID, attendance.getEmployeeId());
		    values.put(MySQLiteHelper.COLUMN_EMPLOYEE_ATTENDANCE_IN_TIME, attendance.getInTime());		    		    
		    values.put(MySQLiteHelper.COLUMN_EMPLOYEE_ATTENDANCE_OUT_TIME, attendance.getOutTime());		    		    
		    values.put(MySQLiteHelper.COLUMN_EMPLOYEE_ATTENDANCE_DURATION, attendance.getDuration());		    		    
		    return database.insert(MySQLiteHelper.TABLE_EMPLOYEE_ATTENDANCE, null, values);
	  } 	
	  
	  public List<Attendance> getAllPunches() {
		    List<Attendance> punches = new ArrayList<Attendance>();
		    String orderBy =  MySQLiteHelper.COLUMN_EMPLOYEE_ATTENDANCE_ID + " ASC";
		    Cursor cursor = database.query(MySQLiteHelper.TABLE_EMPLOYEE_ATTENDANCE,
		        allColumns, null, null, null, null, orderBy);

		    cursor.moveToFirst();
		    while (!cursor.isAfterLast()) {
		      Attendance punch = cursorToPunch(cursor);
		      punches.add(punch);
		      cursor.moveToNext();
		    }
		    // Make sure to close the cursor
		    cursor.close();
		    return punches;
	  }
	  
	  private Attendance cursorToPunch(Cursor cursor) {
		    Attendance punch = new Attendance(cursor.getString(1), 
		    			cursor.getString(2),
		    			cursor.getString(3),
		    			cursor.getString(4));
		    return punch;
	  }	  
	  
	  public void deleteAllPunches() {
		  database.delete(MySQLiteHelper.TABLE_EMPLOYEE_ATTENDANCE, null, null);
	  }
	  
	  public void insertPunches(String employeeId, Object[] punches) {
		  SimpleDateFormat  format = new SimpleDateFormat("yyyy-MM-dd");  		    	  
		  for (int i = 0; i < punches.length; ++i) {
			  Map punchMap = (Map)punches[i];
			  if (punchMap != null) {
				  String inTime = (String)punchMap.get("inTimestamp");
				  String outTime = (String)punchMap.get("outTimestamp");				  
				  String duration = (String)punchMap.get("duration");	
				  Attendance punch = new Attendance(employeeId, inTime, outTime,
						  duration);
					  insertPunch(punch);

			  }
		  }
		   		  
	  }	  
}
