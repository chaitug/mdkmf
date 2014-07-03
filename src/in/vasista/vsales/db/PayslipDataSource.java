package in.vasista.vsales.db;

import in.vasista.hr.payslip.Payslip;
import in.vasista.hr.payslip.PayslipItem;


import java.math.BigDecimal;
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

	  private String[] allPayslipItemColumns = { MySQLiteHelper.COLUMN_PAYROLL_HEADER_ITEM_ID,
		      MySQLiteHelper.COLUMN_PAYROLL_HEADER_ID,
		      MySQLiteHelper.COLUMN_PAYROLL_ITEM_NAME,
		      MySQLiteHelper.COLUMN_PAYROLL_ITEM_AMOUNT};	
	  
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
	  
	  public void deleteAllPayslips() {
		  database.delete(MySQLiteHelper.TABLE_PAYROLL_HEADER_ITEM, null, null);		  		  
		  database.delete(MySQLiteHelper.TABLE_PAYROLL_HEADER, null, null);
	  }	  

	  
	  long insertPayslip(Payslip payslip) {
		    ContentValues values = new ContentValues();
		    values.put(MySQLiteHelper.COLUMN_PAYROLL_HEADER_ID, payslip.getPayrollHeaderId());
		    values.put(MySQLiteHelper.COLUMN_PAYROLL_DATE, payslip.getPayrollDate().getTime());		    		    
		    values.put(MySQLiteHelper.COLUMN_PAYROLL_PERIOD, payslip.getPayrollPeriod());
		    values.put(MySQLiteHelper.COLUMN_PAYROLL_EMPLOYEE_ID, payslip.getEmployeeId());		    
		    values.put(MySQLiteHelper.COLUMN_PAYROLL_NET_AMOUNT, payslip.getNetAmount());		    		    
		    return database.insert(MySQLiteHelper.TABLE_PAYROLL_HEADER, null, values);
	  }  
	  
	  public Payslip getPayslipDetails(String payrollHeaderId) {
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

	  public void insertPayslips(String employeeId, Object[] payslips) {
		  for (int i = 0; i < payslips.length; ++i) {
			  Map payslipMap = (Map)payslips[i];
			  if (payslipMap != null) {
				  String payrollHeaderId = (String)payslipMap.get("payrollHeaderId");
				  Date payrollDate = (Date)payslipMap.get("payrollDate");
				  String payrollPeriod = (String)payslipMap.get("payrollPeriod");				  
				  float netAmount = ((BigDecimal)payslipMap.get("netAmount")).floatValue();	
				  Payslip payslip = new Payslip(employeeId, payrollHeaderId, payrollDate,
						  payrollPeriod, netAmount);
				  insertPayslip(payslip);
				  
				  List<PayslipItem> payslipItems = new ArrayList<PayslipItem>();
				  Object[] payrollItems = (Object[])payslipMap.get("payrollItems");
				  for (int j = 0; j < payrollItems.length; ++j) {
					  Map itemMap = (Map)payrollItems[j];
					  Map.Entry<String, BigDecimal> entry = (Entry<String, BigDecimal>) itemMap.entrySet().iterator().next();
					  PayslipItem payslipItem = new PayslipItem(entry.getKey(),
							  entry.getValue().floatValue());
					  payslipItems.add(payslipItem);
				  }
				  insertPayslipItems(payrollHeaderId, payslipItems);
			  }
		  }
		   		  
	  }
	  
	  public void insertPayslipItems(String payrollHeaderId, List<PayslipItem> payslipItems) {
		  	for (int i=0; i < payslipItems.size(); ++i) {
		  		PayslipItem payslipItem = payslipItems.get(i);

		  		ContentValues values = new ContentValues();
		  		values.put(MySQLiteHelper.COLUMN_PAYROLL_HEADER_ID, payrollHeaderId);
		  		values.put(MySQLiteHelper.COLUMN_PAYROLL_ITEM_NAME, payslipItem.getPayheadType());		    		  		
		  		values.put(MySQLiteHelper.COLUMN_PAYROLL_ITEM_AMOUNT, payslipItem.getPayheadAmount());		    		  		
		  		database.insert(MySQLiteHelper.TABLE_PAYROLL_HEADER_ITEM, null, values);
		  	}
	  }	
	  
	  public List<PayslipItem> getPayslipItems(String payrollHeaderId) {
		  List<PayslipItem> payslipItems = new ArrayList<PayslipItem>();	
		  Cursor cursor = database.query(MySQLiteHelper.TABLE_PAYROLL_HEADER_ITEM,
		        allPayslipItemColumns, MySQLiteHelper.COLUMN_PAYROLL_HEADER_ID + " = " + payrollHeaderId, null, null, null, null);
		  cursor.moveToFirst();
		  while (!cursor.isAfterLast()) {
			  PayslipItem payslipItem = cursorToPayslipItem(cursor);
			  payslipItems.add(payslipItem);
		      cursor.moveToNext();
		  }
		  // Make sure to close the cursor
		  cursor.close();
		  return payslipItems;
	  }
	  
	  private PayslipItem cursorToPayslipItem(Cursor cursor) {
		  PayslipItem payslipItem = new PayslipItem(cursor.getString(2),
				  cursor.getFloat(3));
		    return payslipItem;
	  }		  
}
