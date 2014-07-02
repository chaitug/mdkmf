package in.vasista.vsales.db;

import in.vasista.hr.payslip.Payslip;
import in.vasista.vsales.catalog.Product;
import in.vasista.vsales.order.Order;
import in.vasista.vsales.order.OrderItem;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class PayslipDataSource {
	public static final String module = PayslipDataSource.class.getName();	
	private Context context;	
	  // Database fields
	  private SQLiteDatabase database;
	  private MySQLiteHelper dbHelper;
	  private String[] allColumns = { MySQLiteHelper.COLUMN_PAYROLL_EMPLOYEE_ID,
			  MySQLiteHelper.COLUMN_PAYROLL_HEADER_ID,
			  MySQLiteHelper.COLUMN_PAYROLL_DATE,
			  MySQLiteHelper.COLUMN_PAYROLL_PERIOD,	      
			  MySQLiteHelper.COLUMN_PAYROLL_NET_AMOUNT};

	  
	  public PayslipDataSource(Context context) {
		  this.context = context;		  
	    dbHelper = new MySQLiteHelper(context); 
	  }

	  public void open() throws SQLException {
	    database = dbHelper.getWritableDatabase();
	  }

	  public void close() {
	    dbHelper.close();
	  }
	  
	  public void deleteAllOrders() {
		  database.delete(MySQLiteHelper.TABLE_PAYROLL_HEADER, null, null);
	  }	  

	  
	  long insertPayslip(Payslip payslip) {
		    ContentValues values = new ContentValues();
		    values.put(MySQLiteHelper.COLUMN_PAYROLL_HEADER_ID, payslip.getPayrollHeaderId());
		    values.put(MySQLiteHelper.COLUMN_PAYROLL_DATE, payslip.getPayrollDate().getTime());		    		    
		    values.put(MySQLiteHelper.COLUMN_PAYROLL_PERIOD, payslip.getPayrollPeriod());
		    values.put(MySQLiteHelper.COLUMN_PAYROLL_EMPLOYEE_ID, payslip.getEmployeeId());		    
		    values.put(MySQLiteHelper.COLUMN_PAYROLL_NET_AMOUNT, payslip.getNetAmount());		    		    
		    return database.insert(MySQLiteHelper.TABLE_ORDER, null, values);
	  }  
	  
	  public Payslip getPayslipDetails(long payrollHeaderId) {
		    Cursor cursor = database.query(MySQLiteHelper.TABLE_PAYROLL_HEADER,
		        allColumns, MySQLiteHelper.COLUMN_PAYROLL_HEADER_ID + " = " + payrollHeaderId, null, null, null, null);

		    cursor.moveToFirst();
		    Payslip payslip = cursorToPayslip(cursor);
		    // Make sure to close the cursor
		    cursor.close();
		    return payslip;  
	  }
	  
	  public List<Payslip> getAllPayslips() {
	    List<Payslip> payslips = new ArrayList<Payslip>();
	    String orderBy =  MySQLiteHelper.COLUMN_PAYROLL_DATE + " DESC";
	    Cursor cursor = database.query(MySQLiteHelper.TABLE_PAYROLL_HEADER,
	        allColumns, null, null, null, null, orderBy);

	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	    	Payslip payslip = cursorToPayslip(cursor);
	    	payslips.add(payslip);
	    	cursor.moveToNext();
	    }
	    // Make sure to close the cursor
	    cursor.close();
	    return payslips;
	  }

	  
	  private Payslip cursorToPayslip(Cursor cursor) {
	    Payslip payslip = new Payslip(cursor.getString(0), 
	    		cursor.getString(1),
	    		new Date(cursor.getLong(2)),
	    		cursor.getString(3),    				
	    		cursor.getFloat(4));
	    return payslip;
	  }
	  
}
