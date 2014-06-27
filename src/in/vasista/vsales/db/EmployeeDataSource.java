package in.vasista.vsales.db;

import in.vasista.vsales.employee.Employee;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class EmployeeDataSource {
	  
	  // Database fields 
	  private SQLiteDatabase database;
	  private MySQLiteHelper dbHelper;
	  private String[] allColumns = { MySQLiteHelper.COLUMN_EMPLOYEE_ID,
	      MySQLiteHelper.COLUMN_EMPLOYEE_NAME,
	      MySQLiteHelper.COLUMN_EMPLOYEE_DEPT,
	      MySQLiteHelper.COLUMN_EMPLOYEE_POSITION,	 
	      MySQLiteHelper.COLUMN_EMPLOYEE_PHONE_NUM,	 
	      MySQLiteHelper.COLUMN_EMPLOYEE_JOIN_DATE};
	  
	  public EmployeeDataSource(Context context) {
	    dbHelper = new MySQLiteHelper(context); 
	  } 

	  public void open() throws SQLException {
	    database = dbHelper.getWritableDatabase();
	  }

	  public void close() {
	    dbHelper.close();
	  }
	  
	  public void insertEmployee(Employee employee) {
		    ContentValues values = new ContentValues();
		    values.put(MySQLiteHelper.COLUMN_EMPLOYEE_ID, employee.getId());
		    values.put(MySQLiteHelper.COLUMN_EMPLOYEE_NAME, employee.getName());
		    values.put(MySQLiteHelper.COLUMN_EMPLOYEE_DEPT, employee.getDept());	
		    values.put(MySQLiteHelper.COLUMN_EMPLOYEE_POSITION, employee.getPosition());	
		    values.put(MySQLiteHelper.COLUMN_EMPLOYEE_PHONE_NUM, employee.getPhoneNum());		    		    		    
		    values.put(MySQLiteHelper.COLUMN_EMPLOYEE_JOIN_DATE, employee.getJoinDate().getTime());	    		    
		    
		    database.insert(MySQLiteHelper.TABLE_EMPLOYEE, null, values);
	  }
	  
	  public void deleteAllEmployees() {
		  database.delete(MySQLiteHelper.TABLE_EMPLOYEE, null, null);
	  }

	  public Employee getEmployeeDetails(String employeeId) {
		    Cursor cursor = database.query(MySQLiteHelper.TABLE_EMPLOYEE,
		        allColumns, MySQLiteHelper.COLUMN_EMPLOYEE_ID + " = '" + employeeId + "'", null, null, null, null);

		    cursor.moveToFirst();
		    Employee facility = cursorToEmployee(cursor);
		    // Make sure to close the cursor
		    cursor.close();
		    return facility;  
	  }
	  
	  public List<Employee> getAllEmployees() {
		List<Employee> employees = new ArrayList<Employee>();
	    String orderBy =  MySQLiteHelper.COLUMN_EMPLOYEE_NAME + " ASC";

	    Cursor cursor = database.query(MySQLiteHelper.TABLE_EMPLOYEE,
	        allColumns, null, null, null, null, orderBy);

	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	    	Employee employee = cursorToEmployee(cursor);
	    	employees.add(employee);
	      cursor.moveToNext();
	    }
	    // Make sure to close the cursor
	    cursor.close();
	    return employees;
	  }

	  private Employee cursorToEmployee(Cursor cursor) {
		  Employee employee = new Employee(cursor.getString(0),
	    		cursor.getString(1),
	    		cursor.getString(2),
	    		cursor.getString(3),
	    		cursor.getString(4),	    		
	    		new Date(cursor.getLong(5))); 
	    return employee;
	  }
}
