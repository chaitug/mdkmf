package in.vasista.vsales.db;

import in.vasista.vsales.indent.Indent;
import in.vasista.vsales.payment.Payment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class PaymentsDataSource {
	public static final String module = IndentsDataSource.class.getName();	
	private Context context;	
	  // Database fields
	  private SQLiteDatabase database;
	  private MySQLiteHelper dbHelper;
	  private String[] allColumns = { MySQLiteHelper.COLUMN_PAYMENT_ID,
	      MySQLiteHelper.COLUMN_PAYMENT_DATE,
	      MySQLiteHelper.COLUMN_PAYMENT_METHOD,	      
	      MySQLiteHelper.COLUMN_PAYMENT_AMOUNT};
	  
	  public PaymentsDataSource(Context context) {
		  this.context = context;		  
	    dbHelper = new MySQLiteHelper(context); 
	  }

	  public void open() throws SQLException {
	    database = dbHelper.getWritableDatabase();
	  }

	  public void close() {
	    dbHelper.close();
	  }	  
	  
	  public long insertPayment(String id, Date paymentDate,  String paymentMethodType, double amount ) {
		    ContentValues values = new ContentValues();
		    values.put(MySQLiteHelper.COLUMN_PAYMENT_ID, id);
		    values.put(MySQLiteHelper.COLUMN_PAYMENT_DATE, paymentDate.getTime());
		    values.put(MySQLiteHelper.COLUMN_PAYMENT_METHOD, paymentMethodType);		    		    		    
		    values.put(MySQLiteHelper.COLUMN_PAYMENT_AMOUNT, amount);		    
		    return database.insert(MySQLiteHelper.TABLE_PAYMENT, null, values);
	  }  	
	  
	  public List<Payment> getAllPayments() {
		    List<Payment> payments = new ArrayList<Payment>();
		    String orderBy =  MySQLiteHelper.COLUMN_PAYMENT_DATE + " DESC";
		    Cursor cursor = database.query(MySQLiteHelper.TABLE_PAYMENT,
		        allColumns, null, null, null, null, orderBy);

		    cursor.moveToFirst();
		    while (!cursor.isAfterLast()) {
		      Payment payment = cursorToPayment(cursor);
		      payments.add(payment);
		      cursor.moveToNext();
		    }
		    // Make sure to close the cursor
		    cursor.close();
		    return payments;
	  }
	  
	  private Payment cursorToPayment(Cursor cursor) {
		    Payment payment = new Payment(cursor.getString(0), 
		    		new Date(cursor.getLong(1)),
		    				cursor.getString(2),   				
		    				cursor.getDouble(3));
		    return payment;
	  }	  
	  
	  public void deleteAllPayments() {
		  database.delete(MySQLiteHelper.TABLE_PAYMENT, null, null);
	  }
}
