package in.vasista.vsales.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import in.vasista.vsales.catalog.Product;
public class ProductsDataSource {
  
	  // Database fields 
	  private SQLiteDatabase database;
	  private MySQLiteHelper dbHelper;
	  private String[] allColumns = { MySQLiteHelper.COLUMN_PRODUCT_ID,
	      MySQLiteHelper.COLUMN_PRODUCT_NAME,
	      MySQLiteHelper.COLUMN_PRODUCT_DESC,
	      MySQLiteHelper.COLUMN_PRODUCT_SEQUENCE_NUM,	      
	      MySQLiteHelper.COLUMN_PRODUCT_PRICE,
	      MySQLiteHelper.COLUMN_PRODUCT_MRP_PRICE,
	      MySQLiteHelper.COLUMN_PRODUCT_CATEGORY_ID,
	      MySQLiteHelper.COLUMN_PRODUCT_TRACK_INVENTORY,
	      MySQLiteHelper.COLUMN_PRODUCT_TRACK_SALES};
	  
	  public ProductsDataSource(Context context) {
	    dbHelper = new MySQLiteHelper(context); 
	  } 

	  public void open() throws SQLException {
	    database = dbHelper.getWritableDatabase();
	  }

	  public void close() {
	    dbHelper.close();
	  }

	  static public void insertProduct(SQLiteDatabase database, Product product) {
		    ContentValues values = new ContentValues();
		    values.put(MySQLiteHelper.COLUMN_PRODUCT_ID, product.getId());
		    values.put(MySQLiteHelper.COLUMN_PRODUCT_NAME, product.getName());
		    values.put(MySQLiteHelper.COLUMN_PRODUCT_DESC, product.getDescription());	
		    values.put(MySQLiteHelper.COLUMN_PRODUCT_SEQUENCE_NUM, product.getSequenceNum());		    		    
		    values.put(MySQLiteHelper.COLUMN_PRODUCT_PRICE, product.getPrice());
		    values.put(MySQLiteHelper.COLUMN_PRODUCT_MRP_PRICE, product.getMrpPrice());	
		    values.put(MySQLiteHelper.COLUMN_PRODUCT_CATEGORY_ID, product.getProductCategoryId());		    
		    values.put(MySQLiteHelper.COLUMN_PRODUCT_TRACK_INVENTORY, product.isTrackInventory() ? 1 :0);		    
		    values.put(MySQLiteHelper.COLUMN_PRODUCT_TRACK_SALES, product.isTrackSales() ? 1 :0);		    
		    database.insert(MySQLiteHelper.TABLE_PRODUCT, null, values);
	  }
	  
	  public void insertProduct(Product product) {    
		    ContentValues values = new ContentValues();
		    values.put(MySQLiteHelper.COLUMN_PRODUCT_ID, product.getId());
		    values.put(MySQLiteHelper.COLUMN_PRODUCT_NAME, product.getName());
		    values.put(MySQLiteHelper.COLUMN_PRODUCT_DESC, product.getDescription());
		    values.put(MySQLiteHelper.COLUMN_PRODUCT_SEQUENCE_NUM, product.getSequenceNum());		    		    		    
		    values.put(MySQLiteHelper.COLUMN_PRODUCT_PRICE, product.getPrice());
		    values.put(MySQLiteHelper.COLUMN_PRODUCT_MRP_PRICE, product.getMrpPrice());	
		    values.put(MySQLiteHelper.COLUMN_PRODUCT_CATEGORY_ID, product.getProductCategoryId());		    
		    values.put(MySQLiteHelper.COLUMN_PRODUCT_TRACK_INVENTORY, product.isTrackInventory() ? 1 :0);		    
		    values.put(MySQLiteHelper.COLUMN_PRODUCT_TRACK_SALES, product.isTrackSales() ? 1 :0);		    
		    database.insert(MySQLiteHelper.TABLE_PRODUCT, null, values);    
	  }
	  
	  public void deleteAllProducts() {
		  database.delete(MySQLiteHelper.TABLE_PRODUCT, null, null);
	  }

	  public void deleteAllSaleProducts() {
		  database.delete(MySQLiteHelper.TABLE_PRODUCT,  MySQLiteHelper.COLUMN_PRODUCT_TRACK_SALES + " = " + 1, null);
	  }

	  public void deleteAllInventoryProducts() {
		  database.delete(MySQLiteHelper.TABLE_PRODUCT,  MySQLiteHelper.COLUMN_PRODUCT_TRACK_INVENTORY + " = " + 1, null);
	  }
	  
	  public List<Product> getAllProducts() {
		List<Product> products = new ArrayList<Product>();
	    String orderBy =  MySQLiteHelper.COLUMN_PRODUCT_SEQUENCE_NUM + " ASC";

	    Cursor cursor = database.query(MySQLiteHelper.TABLE_PRODUCT,
	        allColumns, null, null, null, null, orderBy);

	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) { 
	      Product product = cursorToProduct(cursor);
	      products.add(product);
	      cursor.moveToNext();
	    }
	    // Make sure to close the cursor
	    cursor.close();
	    return products;
	  }

	  public List<Product> getAllSaleProducts() {
		List<Product> products = new ArrayList<Product>();
	    String orderBy =  MySQLiteHelper.COLUMN_PRODUCT_SEQUENCE_NUM + " ASC";

	    Cursor cursor = database.query(MySQLiteHelper.TABLE_PRODUCT,
	        allColumns, MySQLiteHelper.COLUMN_PRODUCT_TRACK_SALES + " = " + 1, null, null, null, orderBy);

	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) { 
	      Product product = cursorToProduct(cursor);
	      products.add(product);
	      cursor.moveToNext();
	    }
	    // Make sure to close the cursor
	    cursor.close();
	    return products;
	  }

	  public List<Product> getAllInventoryProducts() {
		List<Product> products = new ArrayList<Product>();
	    String orderBy =  MySQLiteHelper.COLUMN_PRODUCT_DESC + " ASC";

	    Cursor cursor = database.query(MySQLiteHelper.TABLE_PRODUCT,
	        allColumns, MySQLiteHelper.COLUMN_PRODUCT_TRACK_INVENTORY + " = " + 1, null, null, null, orderBy);

	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) { 
	      Product product = cursorToProduct(cursor);
	      products.add(product);
	      cursor.moveToNext();
	    }
	    // Make sure to close the cursor
	    cursor.close();
	    return products;
	  }
	  
	  private Product cursorToProduct(Cursor cursor) {
	    Product product = new Product(cursor.getString(0),
	    		cursor.getString(1),
	    		cursor.getString(2),
	    		cursor.getInt(3), 
	    		(float)cursor.getDouble(4), 
	    		(float)cursor.getDouble(5),
	    		cursor.getString(6),
	    		cursor.getInt(7) == 1 ? true : false,
	    		cursor.getInt(8) == 1 ? true : false);
	    return product;
	  }
	   
	  public Map getSaleProductMap() {
		  List<Product> products = getAllSaleProducts();
		  Map result = new HashMap<String, Product> ();
		  for (int i = 0; i < products.size(); ++i) {
			  Product product = products.get(i);
			  result.put(product.getId(), product);
		  }
		  return result;
	  }
}
