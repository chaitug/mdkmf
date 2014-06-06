package in.vasista.vsales.db;

import in.vasista.vsales.catalog.Product;
import in.vasista.vsales.indent.IndentItem;
import in.vasista.vsales.order.Order;
import in.vasista.vsales.order.OrderItem;
import in.vasista.vsales.util.DateUtil;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class OrdersDataSource {
	public static final String module = OrdersDataSource.class.getName();	
	private Context context;	
	  // Database fields
	  private SQLiteDatabase database;
	  private MySQLiteHelper dbHelper;
	  private String[] allColumns = { MySQLiteHelper.COLUMN_ORDER_ID,
	      MySQLiteHelper.COLUMN_ORDER_SUPPLYDATE,
	      MySQLiteHelper.COLUMN_ORDER_SUBSCRIPTIONTYPE,	      
	      MySQLiteHelper.COLUMN_ORDER_TOTAL};

	  private String[] allOrderItemColumns = { MySQLiteHelper.COLUMN_ORDER_ITEM_ID,
		      MySQLiteHelper.COLUMN_ORDER_ID,
		      MySQLiteHelper.COLUMN_PRODUCT_ID,
		      MySQLiteHelper.COLUMN_ORDER_ITEM_QTY,
		      MySQLiteHelper.COLUMN_ORDER_ITEM_UNIT_PRICE};	  
	  
	  public OrdersDataSource(Context context) {
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
		  database.delete(MySQLiteHelper.TABLE_ORDER_ITEM, null, null);		  
		  database.delete(MySQLiteHelper.TABLE_ORDER, null, null);
	  }	  

	  
	  long insertOrder(Date supplyDate,  String subscriptionType, double orderTotal) {
		    ContentValues values = new ContentValues();
		    values.put(MySQLiteHelper.COLUMN_ORDER_SUPPLYDATE, supplyDate.getTime());
		    values.put(MySQLiteHelper.COLUMN_ORDER_SUBSCRIPTIONTYPE, subscriptionType);		    		    
		    values.put(MySQLiteHelper.COLUMN_ORDER_TOTAL, orderTotal);		    
		    return database.insert(MySQLiteHelper.TABLE_ORDER, null, values);
	  }  
	  
	  public Order getOrderDetails(long orderId) {
		    Cursor cursor = database.query(MySQLiteHelper.TABLE_ORDER,
		        allColumns, MySQLiteHelper.COLUMN_ORDER_ID + " = " + orderId, null, null, null, null);

		    cursor.moveToFirst();
		    Order order = cursorToOrder(cursor);
		    // Make sure to close the cursor
		    cursor.close();
		    return order;  
	  }
	  
	  public List<Order> getAllOrders() {
	    List<Order> orders = new ArrayList<Order>();
	    String orderBy =  MySQLiteHelper.COLUMN_ORDER_SUPPLYDATE + " DESC";
	    Cursor cursor = database.query(MySQLiteHelper.TABLE_ORDER,
	        allColumns, null, null, null, null, orderBy);

	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	      Order order = cursorToOrder(cursor);
	      orders.add(order);
	      cursor.moveToNext();
	    }
	    // Make sure to close the cursor
	    cursor.close();
	    return orders;
	  }

	  
	  private Order cursorToOrder(Cursor cursor) {
	    Order order = new Order(cursor.getInt(0), 
	    		new Date(cursor.getLong(1)),
	    				cursor.getString(2),    				
	    				cursor.getDouble(3));
	    return order;
	  }
	  
	  void insertOrderItems(long orderId, List<OrderItem> orderItems) {
		  	for (int i=0; i < orderItems.size(); ++i) {
		  		OrderItem orderItem = orderItems.get(i);
		  		if (orderItem.getQty() == -1) {
		  			continue;
		  		}
		  		ContentValues values = new ContentValues();
		  		values.put(MySQLiteHelper.COLUMN_ORDER_ID, orderId);
		  		values.put(MySQLiteHelper.COLUMN_PRODUCT_ID, orderItem.getProductId());		    		  		
		  		values.put(MySQLiteHelper.COLUMN_ORDER_ITEM_QTY, orderItem.getQty());	
		  		values.put(MySQLiteHelper.COLUMN_ORDER_ITEM_UNIT_PRICE, orderItem.getUnitPrice());		    		  		
		  		database.insert(MySQLiteHelper.TABLE_ORDER_ITEM, null, values);
		  	}
	  }
	  
	  // populate order for one day, one shift
	  void insertOrderAndItemsInternal(String orderDateStr, String subscriptionType, Map<String, Object> orderMap) {
		  Map productsMap = new HashMap();
		  final ProductsDataSource prodDataSource = new ProductsDataSource (context);
		  prodDataSource.open();
		  List<Product> products = prodDataSource.getAllProducts();
		  for (int i =0; i < products.size(); ++i) {
			  Product product = products.get(i);
			  productsMap.put(product.getId(), product);
		  }
		  prodDataSource.close();
		  SimpleDateFormat  format = new SimpleDateFormat("yyyy-MM-dd");  		    	  
		  List<OrderItem> orderItems = new ArrayList<OrderItem>();		    		   
		  Iterator itemEntries = orderMap.entrySet().iterator();
		  double orderTotal = 0;
		  while (itemEntries.hasNext()) {
	    	  Entry itemEntry = (Entry) itemEntries.next(); 
	    	  String productId = (String)itemEntry.getKey();
	    	  Map productItem = (Map)itemEntry.getValue();
	    	  int quantity = ((BigDecimal)productItem.get("packetQuantity")).intValue();
	    	  double amount = ((BigDecimal)productItem.get("totalRevenue")).intValue();	
	    	  orderTotal += amount;
Log.d(module, "Order Item: [" + orderDateStr + ";" + productId + ";" + quantity + ";" + amount + "]");				    	  
	    	  Product product = (Product)productsMap.get(productId);
	    	  String productDesc;
	    	  if (product == null) { 
	    		  productDesc = (String)productItem.get("name");
	    	  } 
	    	  else {
	    		  productDesc = product.getDescription();
	    	  }
	    	  OrderItem orderItem = new OrderItem(productId, productDesc, quantity, amount);
	    	  orderItems.add(orderItem);
		  }
		  orderTotal = Math.round(orderTotal * 100.0) / 100.0;
		  try {  
		      Date orderDate = format.parse(orderDateStr);  
    		  long orderId =  insertOrder(orderDate, subscriptionType, orderTotal);
    		  insertOrderItems(orderId, orderItems); 
		  } catch (ParseException e) {  
		       // do nothing for now
		  }		    		  
	  }
	  
	  // iterate through orders for all days and all shifts
	  public void insertOrderAndItems(Map<String, Object> ordersResult) {
	    	Iterator orderEntries = ordersResult.entrySet().iterator();
	    	while (orderEntries.hasNext()) {
		    	  Entry orderEntry = (Entry) orderEntries.next(); 
		    	  String orderDateStr = (String)orderEntry.getKey();
		    	  Map val = (Map)orderEntry.getValue();
		    	  if (val != null && val.get("AM") != null) {
		    		  insertOrderAndItemsInternal(orderDateStr, "AM", (Map)val.get("AM"));	    
		    	  }
		    	  if (val != null && val.get("PM") != null) {
		    		  insertOrderAndItemsInternal(orderDateStr, "PM", (Map)val.get("PM"));	    
		    	  }		    	  
	    	}	    	
	  }	
	  
	  public List<OrderItem> getOrderItems(int orderId) {
		  List<OrderItem> orderItems = new ArrayList<OrderItem>();	
		  Cursor cursor = database.query(MySQLiteHelper.TABLE_ORDER_ITEM,
		        allOrderItemColumns, MySQLiteHelper.COLUMN_ORDER_ID + " = " + orderId, null, null, null, null);
		  ProductsDataSource datasource = new ProductsDataSource(context);
		  datasource.open();
		  Map productMap = datasource.getProductMap();	
		  cursor.moveToFirst();
		  while (!cursor.isAfterLast()) {
			  OrderItem orderItem = cursorToOrderItem(cursor, productMap);
			  orderItems.add(orderItem);
		      cursor.moveToNext();
		  }
		  // Make sure to close the cursor
		  cursor.close();
		  return orderItems;
	  }
	  
	  
	  private OrderItem cursorToOrderItem(Cursor cursor,  Map<String, Product> productMap) {
		  OrderItem orderItem = new OrderItem(cursor.getString(2),
				  productMap.get(cursor.getString(2)).getName(),
				  cursor.getInt(3),
				  cursor.getDouble(4));
		    return orderItem;
	  }	  	
	  
}
