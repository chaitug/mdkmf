package in.vasista.vsales.db;

import in.vasista.hr.leave.Leave;
import in.vasista.hr.payslip.Payslip;
import in.vasista.hr.payslip.PayslipItem;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class LeavesDataSource {
	public static final String module = LeavesDataSource.class.getName();	
	private Context context;	
	  // Database fields
	  private SQLiteDatabase database;
	  private MySQLiteHelper dbHelper;
	  private String[] allColumns = { MySQLiteHelper.COLUMN_EMPLOYEE_LEAVE_ID,
	      MySQLiteHelper.COLUMN_EMPLOYEE_ID,
	      MySQLiteHelper.COLUMN_EMPLOYEE_LEAVE_TYPE_ID,	      
	      MySQLiteHelper.COLUMN_EMPLOYEE_LEAVE_STATUS,
	      MySQLiteHelper.COLUMN_EMPLOYEE_LEAVE_FROM_DATE,
	      MySQLiteHelper.COLUMN_EMPLOYEE_LEAVE_THRU_DATE};
	  
	  public LeavesDataSource(Context context) {
		  this.context = context;		  
	    dbHelper = new MySQLiteHelper(context); 
	  }

	  public void open() throws SQLException {
	    database = dbHelper.getWritableDatabase();
	  }

	  public void close() {
	    dbHelper.close();
	  }	  
	  
	  long insertLeave(Leave leave) {
		    ContentValues values = new ContentValues();
		    values.put(MySQLiteHelper.COLUMN_EMPLOYEE_ID, leave.getEmployeeId());
		    values.put(MySQLiteHelper.COLUMN_EMPLOYEE_LEAVE_TYPE_ID, leave.getLeaveTypeId());		    		    
		    values.put(MySQLiteHelper.COLUMN_EMPLOYEE_LEAVE_STATUS, leave.getLeaveStatus());		    		    
		    values.put(MySQLiteHelper.COLUMN_EMPLOYEE_LEAVE_FROM_DATE, leave.getFromDate().getTime());		    		    
		    values.put(MySQLiteHelper.COLUMN_EMPLOYEE_LEAVE_THRU_DATE, leave.getThruDate().getTime());		    		    
		    return database.insert(MySQLiteHelper.TABLE_EMPLOYEE_LEAVE, null, values);
	  } 	
	  
	  public List<Leave> getAllLeaves() {
		    List<Leave> leaves = new ArrayList<Leave>();
		    String orderBy =  MySQLiteHelper.COLUMN_EMPLOYEE_LEAVE_FROM_DATE + " DESC";
		    Cursor cursor = database.query(MySQLiteHelper.TABLE_EMPLOYEE_LEAVE,
		        allColumns, null, null, null, null, orderBy);

		    cursor.moveToFirst();
		    while (!cursor.isAfterLast()) {
		      Leave leave = cursorToLeave(cursor);
		      leaves.add(leave);
		      cursor.moveToNext();
		    }
		    // Make sure to close the cursor
		    cursor.close();
		    return leaves;
	  }
	  
	  private Leave cursorToLeave(Cursor cursor) {
		    Leave leave = new Leave(cursor.getString(1), 
		    			cursor.getString(2),
		    			cursor.getString(3),
		    			new Date(cursor.getLong(4)),
		    			new Date(cursor.getLong(5)));
		    return leave;
	  }	  
	  
	  public void deleteAllLeaves() {
		  database.delete(MySQLiteHelper.TABLE_EMPLOYEE_LEAVE, null, null);
	  }
	  
	  public void insertLeaves(String employeeId, Object[] leaves) {
		  SimpleDateFormat  format = new SimpleDateFormat("yyyy-MM-dd"); 
		  database.beginTransaction();
		  try{		  
			  for (int i = 0; i < leaves.length; ++i) {
				  Map leaveMap = (Map)leaves[i];
				  if (leaveMap != null) {
					  String leaveType = (String)leaveMap.get("leaveTypeId");
					  String leaveStatus = (String)leaveMap.get("leaveStatus");	
					  try  {
						  Date fromDate = format.parse((String)leaveMap.get("leaveFromDate"));
						  Date thruDate = format.parse((String)leaveMap.get("leaveThruDate"));
						  Leave leave = new Leave(employeeId, leaveType, leaveStatus,
								  fromDate, thruDate);
						  ContentValues values = new ContentValues();
						  values.put(MySQLiteHelper.COLUMN_EMPLOYEE_ID, leave.getEmployeeId());
						  values.put(MySQLiteHelper.COLUMN_EMPLOYEE_LEAVE_TYPE_ID, leave.getLeaveTypeId());		    		    
						  values.put(MySQLiteHelper.COLUMN_EMPLOYEE_LEAVE_STATUS, leave.getLeaveStatus());		    		    
						  values.put(MySQLiteHelper.COLUMN_EMPLOYEE_LEAVE_FROM_DATE, leave.getFromDate().getTime());		    		    
						  values.put(MySQLiteHelper.COLUMN_EMPLOYEE_LEAVE_THRU_DATE, leave.getThruDate().getTime());		    		    
						  database.insert(MySQLiteHelper.TABLE_EMPLOYEE_LEAVE, null, values);
					  } catch (ParseException e) {  
						  // do nothing for now
					  }	
				  }
			  }
			  database.setTransactionSuccessful();
	  	  } catch(Exception e){
	  			Log.d(module, "leaves insert into db failed: " + e);	
	  	  } finally{
	  		 database.endTransaction();
	  	  }
		   		  
	  }	  
}
