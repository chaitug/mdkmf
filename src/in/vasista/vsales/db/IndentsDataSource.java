package in.vasista.vsales.db;

import in.vasista.vsales.catalog.Product;
import in.vasista.vsales.indent.Indent;
import in.vasista.vsales.indent.IndentItem;
import in.vasista.vsales.util.DateUtil;

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

public class IndentsDataSource {
	public static final String module = IndentsDataSource.class.getName();	
	private Context context;	
	  // Database fields
	  private SQLiteDatabase database;
	  private MySQLiteHelper dbHelper;
	  private String[] allColumns = { MySQLiteHelper.COLUMN_INDENT_ID,
	      MySQLiteHelper.COLUMN_INDENT_CRDATE,
	      MySQLiteHelper.COLUMN_INDENT_SUPPLYDATE,
	      MySQLiteHelper.COLUMN_INDENT_SUBSCRIPTIONTYPE,	      
	      MySQLiteHelper.COLUMN_INDENT_STATUS,
	      MySQLiteHelper.COLUMN_INDENT_IS_SYNCED,	      
	      MySQLiteHelper.COLUMN_INDENT_TOTAL};

	  private String[] allIndentItemColumns = { MySQLiteHelper.COLUMN_INDENT_ITEM_ID,
		      MySQLiteHelper.COLUMN_INDENT_ID,
		      MySQLiteHelper.COLUMN_PRODUCT_ID,
		      MySQLiteHelper.COLUMN_INDENT_ITEM_QTY,
		      MySQLiteHelper.COLUMN_INDENT_ITEM_UNIT_PRICE};	  
	  
	  public IndentsDataSource(Context context) {
		  this.context = context;		  
	    dbHelper = new MySQLiteHelper(context); 
	  }

	  public void open() throws SQLException {
	    database = dbHelper.getWritableDatabase();
	  }

	  public void close() {
	    dbHelper.close();
	  }

	  public void deleteAllIndents() {
		  database.delete(MySQLiteHelper.TABLE_INDENT, null, null);
	  }
	  
	  static public void insertIndent(SQLiteDatabase database, String indentStatus, double indentTotal) {
		    ContentValues values = new ContentValues();
		    values.put(MySQLiteHelper.COLUMN_INDENT_CRDATE, System.currentTimeMillis());
		    values.put(MySQLiteHelper.COLUMN_INDENT_STATUS, indentStatus);			    
		    values.put(MySQLiteHelper.COLUMN_INDENT_IS_SYNCED, 0);			    
		    values.put(MySQLiteHelper.COLUMN_INDENT_TOTAL, indentTotal);		    
		    database.insert(MySQLiteHelper.TABLE_INDENT, null, values);
	  }

	  public long insertIndent(String indentStatus, double indentTotal, String subscriptionType) {
		  	Date now = new Date();
			Date supplyDate = DateUtil.addDays(now, 1);
			return insertIndent(indentStatus, indentTotal, subscriptionType, supplyDate);
	  }  
	  
	  public long insertIndent(String indentStatus, double indentTotal, String subscriptionType, Date supplyDate) {
		    ContentValues values = new ContentValues();
		    values.put(MySQLiteHelper.COLUMN_INDENT_CRDATE, System.currentTimeMillis());
		    values.put(MySQLiteHelper.COLUMN_INDENT_SUPPLYDATE, supplyDate.getTime());
		    values.put(MySQLiteHelper.COLUMN_INDENT_SUBSCRIPTIONTYPE, subscriptionType);		    		    
		    values.put(MySQLiteHelper.COLUMN_INDENT_STATUS, indentStatus);	
		    values.put(MySQLiteHelper.COLUMN_INDENT_IS_SYNCED, 0);			    
		    values.put(MySQLiteHelper.COLUMN_INDENT_TOTAL, indentTotal);		    
		    return database.insert(MySQLiteHelper.TABLE_INDENT, null, values);
	  }  
	  
	  /*
	   * Returns the indent if it exists for the supplydate and shift, else
	   * null
	   */
	  public Indent fetchIndent(Date supplyDate, String subscriptionType) {
		  Indent indent = null;
		  Cursor cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_INDENT 
				  + " WHERE " 
				  + MySQLiteHelper.COLUMN_INDENT_SUBSCRIPTIONTYPE + "= '" + subscriptionType + "'"
				  + " AND date(datetime(" 
				  + MySQLiteHelper.COLUMN_INDENT_SUPPLYDATE + " / 1000 , 'unixepoch','localtime')) = date(datetime(" 
				  + supplyDate.getTime()/1000 + ", 'unixepoch','localtime'))", null);
		  if ( cursor.moveToFirst()) {
			  indent = cursorToIndent(cursor);
		  }
		  cursor.close();
		  Log.d( module, "supplyDate=" + supplyDate); 	
		  Log.d( module, "subscriptionType=" + subscriptionType); 		  		  		  		  
		  Log.d( module, "indent=" + indent); 
		  return indent;
	  }
	  
	  public List<Indent> getAllIndents() {
	    List<Indent> indents = new ArrayList<Indent>();
	    String orderBy =  MySQLiteHelper.COLUMN_INDENT_ID + " DESC";
	    Cursor cursor = database.query(MySQLiteHelper.TABLE_INDENT,
	        allColumns, null, null, null, null, orderBy);

	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	      Indent indent = cursorToIndent(cursor);
	      indents.add(indent);
	      cursor.moveToNext();
	    }
	    // Make sure to close the cursor
	    cursor.close();
	    return indents;
	  }

	  public Indent getIndentDetails(long indentId) {
		    Cursor cursor = database.query(MySQLiteHelper.TABLE_INDENT,
		        allColumns, MySQLiteHelper.COLUMN_INDENT_ID + " = " + indentId, null, null, null, null);

		    cursor.moveToFirst();
		    Indent indent = cursorToIndent(cursor);
		    // Make sure to close the cursor
		    cursor.close();
		    return indent;  
	  }
	  
	  private Indent cursorToIndent(Cursor cursor) {
	    Indent indent = new Indent(cursor.getInt(0), 
	    		new Date(cursor.getLong(1)),
	    		new Date(cursor.getLong(2)),
	    				cursor.getString(3),
	    				cursor.getString(4),	    				
	    				(cursor.getInt(5) == 1) ? true : false,	    				
	    				cursor.getDouble(6));
	    return indent;
	  }
	  
	  public void updateIndentStatus(long indentId, String indentStatus) {
		    ContentValues values = new ContentValues();
		    values.put(MySQLiteHelper.COLUMN_INDENT_STATUS, indentStatus);		    	    
		    final String[] whereArgs = { Long.toString(indentId)};
		    database.update(MySQLiteHelper.TABLE_INDENT, values, MySQLiteHelper.COLUMN_INDENT_ID + " = ?", whereArgs);
	  }	  
	  
	  public void updateIndentTotal(long indentId, double indentTotal) {
		    ContentValues values = new ContentValues();
		    values.put(MySQLiteHelper.COLUMN_INDENT_TOTAL, indentTotal);		    	    
		    final String[] whereArgs = { Long.toString(indentId)};
		    database.update(MySQLiteHelper.TABLE_INDENT, values, MySQLiteHelper.COLUMN_INDENT_ID + " = ?", whereArgs);
	  }	 	  
	  
	  public void setIndentSyncStatus(long indentId, boolean isSynced) {
		    ContentValues values = new ContentValues();
		    values.put(MySQLiteHelper.COLUMN_INDENT_IS_SYNCED, isSynced?1:0);		    	    
		    final String[] whereArgs = { Long.toString(indentId)};
		    database.update(MySQLiteHelper.TABLE_INDENT, values, MySQLiteHelper.COLUMN_INDENT_ID + " = ?", whereArgs);
	  }

	  public void updateIndentAndIndentItems(Indent indent, List<IndentItem> indentItems) {
			Log.d( module, "indent=" + indent); 		  		  
		    ContentValues values = new ContentValues();
		    values.put(MySQLiteHelper.COLUMN_INDENT_IS_SYNCED, indent.isSynced()?1:0);		    	    		    
		    values.put(MySQLiteHelper.COLUMN_INDENT_STATUS, indent.getStatus());
		    values.put(MySQLiteHelper.COLUMN_INDENT_TOTAL, indent.getTotal());		    		    
		    final String[] whereArgs = { Long.toString(indent.getId())};
		    database.update(MySQLiteHelper.TABLE_INDENT, values, MySQLiteHelper.COLUMN_INDENT_ID + " = ?", whereArgs);

			database.delete(MySQLiteHelper.TABLE_INDENT_ITEM, MySQLiteHelper.COLUMN_INDENT_ID + " = ?", whereArgs);
		  	for (int i=0; i < indentItems.size(); ++i) {
		  		IndentItem indentItem = indentItems.get(i); 
		  		if (indentItem.getQty() == -1) {
		  			continue;
		  		}
		  		ContentValues itemValues = new ContentValues();
		  		itemValues.put(MySQLiteHelper.COLUMN_INDENT_ID, indent.getId());
		  		itemValues.put(MySQLiteHelper.COLUMN_PRODUCT_ID, indentItem.getProductId());		    		  		
		  		itemValues.put(MySQLiteHelper.COLUMN_INDENT_ITEM_QTY, indentItem.getQty());	
		  		itemValues.put(MySQLiteHelper.COLUMN_INDENT_ITEM_UNIT_PRICE, indentItem.getUnitPrice());		    		  		
		  		database.insert(MySQLiteHelper.TABLE_INDENT_ITEM, null, itemValues);
		  	}			
	  }	
	  
	  public void insertIndentItems(long indentId, List<IndentItem> indentItems) {
		  	for (int i=0; i < indentItems.size(); ++i) {
		  		IndentItem indentItem = indentItems.get(i);
		  		if (indentItem.getQty() == -1) {
		  			continue;
		  		}
		  		ContentValues values = new ContentValues();
		  		values.put(MySQLiteHelper.COLUMN_INDENT_ID, indentId);
		  		values.put(MySQLiteHelper.COLUMN_PRODUCT_ID, indentItem.getProductId());		    		  		
		  		values.put(MySQLiteHelper.COLUMN_INDENT_ITEM_QTY, indentItem.getQty());	
		  		values.put(MySQLiteHelper.COLUMN_INDENT_ITEM_UNIT_PRICE, indentItem.getUnitPrice());		    		  		
		  		database.insert(MySQLiteHelper.TABLE_INDENT_ITEM, null, values);
		  	}
	  }	
	  
	  public List<IndentItem> getIndentItems(int indentId) {
		  List<IndentItem> indentItems = new ArrayList<IndentItem>();	
		  Cursor cursor = database.query(MySQLiteHelper.TABLE_INDENT_ITEM,
		        allIndentItemColumns, MySQLiteHelper.COLUMN_INDENT_ID + " = " + indentId, null, null, null, null);
		  ProductsDataSource datasource = new ProductsDataSource(context);
		  datasource.open();
		  Map productMap = datasource.getProductMap();	
		  cursor.moveToFirst();
		  while (!cursor.isAfterLast()) {
			  IndentItem indentItem = cursorToIndentItem(cursor, productMap);
			  indentItems.add(indentItem);
		      cursor.moveToNext();
		  }
		  // Make sure to close the cursor
		  cursor.close();
		  return indentItems;
	  }
	  
	  public Map[] getXMLRPCSerializedIndentItems(int indentId) {
		  List<IndentItem> indentItems = getIndentItems(indentId);
		  if (indentItems.isEmpty()) {
			  return null;
		  }
		  // let's consolidate duplicate product ids
		  Map <String, IndentItem> consolidatedItems = new HashMap<String, IndentItem>();
		  for (int i = 0; i < indentItems.size(); ++i) {
			IndentItem item = consolidatedItems.get(indentItems.get(i).getProductId());
			if (item == null) {
				consolidatedItems.put(indentItems.get(i).getProductId(), indentItems.get(i));
			}
			else {
				item.setQty(item.getQty() + indentItems.get(i).getQty());
			}
		  }
		Log.d( module, "consolidatedItems=" + consolidatedItems); 		  
		  Map [] result = new TreeMap[consolidatedItems.size()];
		  int i = 0;		  
		  for (IndentItem ii : consolidatedItems.values()) {
			  Map item = new TreeMap();
			  item.put("indentId", indentId);
			  item.put("indentDate", System.currentTimeMillis());	    
			  item.put("productId", ii.getProductId());	   
			  item.put("qty", ii.getQty());	    
			  item.put("unitPrice", ii.getUnitPrice());
			  result[i] = item;	
			  ++i;	  
		  }	  
		  return result;
	  }
	  
	  private IndentItem cursorToIndentItem(Cursor cursor,  Map<String, Product> productMap) {
		  IndentItem indentItem = new IndentItem(cursor.getString(2),
				  productMap.get(cursor.getString(2)).getName(),
				  cursor.getInt(3),
				  cursor.getDouble(4));
		    return indentItem;
	  }	  	
	  
	  /*
	   * This method will insert a new indent and items for the given day and shift.
	   * Note: Any existing indent for the given day and shift will be deleted
	   */
	  public long insertIndentAndItems(Date supplyDate, String subscriptionType, List<IndentItem> indentItems) {

		double indentTotal = 0;
		for (int i = 0; i < indentItems.size(); ++i) {
			IndentItem indentItem = indentItems.get(i);
			indentTotal += indentItem.getUnitPrice()*indentItem.getQty();
		}
		indentTotal = Math.round(indentTotal * 100.0) / 100.0;
		long indentId = -1;
		Indent indent = fetchIndent(supplyDate, subscriptionType);
		if (indent != null) {
			indentId = indent.getId();
		    final String[] whereArgs = { Long.toString(indent.getId())};
			database.delete(MySQLiteHelper.TABLE_INDENT_ITEM, MySQLiteHelper.COLUMN_INDENT_ID + " = ?", whereArgs);
			updateIndentTotal(indentId, indentTotal);
		}
		else {
			indentId = insertIndent("Created", indentTotal, subscriptionType, supplyDate);
		}
		insertIndentItems(indentId, indentItems);
		return indentId;
	}
}
