package in.vasista.vsales.sync;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import in.vasista.hr.attendance.AttendanceListFragment;
import in.vasista.vsales.EmployeeActivity;
import in.vasista.vsales.EmployeeDetailsActivity;
import in.vasista.vsales.HRDashboardActivity;
import in.vasista.vsales.LeaveActivity;
import in.vasista.vsales.SalesDashboardActivity;
import in.vasista.vsales.MyEmployeeDetailsActivity;
import in.vasista.vsales.catalog.CatalogListFragment;
import in.vasista.vsales.catalog.Product;
import in.vasista.vsales.db.AttendanceDataSource;
import in.vasista.vsales.db.EmployeeDataSource;
import in.vasista.vsales.db.FacilityDataSource;
import in.vasista.vsales.db.IndentsDataSource;
import in.vasista.vsales.db.LeavesDataSource;
import in.vasista.vsales.db.OrdersDataSource;
import in.vasista.vsales.db.PaymentsDataSource;
import in.vasista.vsales.db.PayslipDataSource;
import in.vasista.vsales.db.ProductsDataSource;
import in.vasista.vsales.employee.Employee;
import in.vasista.vsales.employee.EmployeeListFragment;
import in.vasista.vsales.facility.Facility;
import in.vasista.vsales.facility.FacilityListFragment;
import in.vasista.vsales.indent.Indent;
import in.vasista.vsales.indent.IndentItem;
import in.vasista.vsales.indent.IndentItemsListFragment;
import in.vasista.vsales.indent.IndentListFragment;
import in.vasista.vsales.order.OrderListFragment;
import in.vasista.vsales.payment.Payment;
import in.vasista.vsales.payment.PaymentListFragment;
import in.vasista.vsales.sync.xmlrpc.XMLRPCApacheAdapter;
import in.vasista.vsales.sync.xmlrpc.XMLRPCMethodCallback;
import in.vasista.vsales.util.DateUtil;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;

public class ServerSync {

	public static final String module = ServerSync.class.getName();	
	
	
	private Context context;
	//private MySQLiteHelper dbHelper;
	
	public ServerSync(Context context) {
		this.context = context; 
	    //dbHelper = new MySQLiteHelper(context); 		
	}
	
	public void uploadIndent(final Indent indent, ProgressBar progressBar, final IndentItemsListFragment listFragment) {
		final IndentsDataSource datasource = new IndentsDataSource(context);
		datasource.open();
		if (indent == null || !indent.getStatus().equals("Created")) {
			progressBar.setVisibility(View.INVISIBLE);
			return;
		}
		Map[] indentItems = datasource.getXMLRPCSerializedIndentItems(indent.getId());
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String storeId = prefs.getString("storeId", "");		
		Map paramMap = new HashMap();
		paramMap.put("boothId", storeId);
		Date supplyDate = indent.getSupplyDate();
		paramMap.put("supplyDate", supplyDate.getTime());	
		paramMap.put("subscriptionTypeId", indent.getSubscriptionType());	
	    paramMap.put("indentItems", indentItems);	  
		try {   
			XMLRPCApacheAdapter adapter = new XMLRPCApacheAdapter(context);
			adapter.call("processChangeIndentApi", paramMap, progressBar, new XMLRPCMethodCallback() {
				public void callFinished(Object result, ProgressBar progressBar) {
					if (result != null) {
				    	Map indentResults = (Map)((Map)result).get("indentResults");
				    	Log.d(module, "numIndentItems = " + indentResults.get("numIndentItems"));
				    	datasource.setIndentSyncStatus(indent.getId(), true);
				    	if (listFragment != null) {
				    		listFragment.notifyChange();   
				    	}
					}
					if (progressBar != null) {
						progressBar.setVisibility(View.INVISIBLE);
					}
					Toast.makeText( context, "Indent upload succeeded!", Toast.LENGTH_LONG ).show();	    		    			
				}
			});
		}
		catch (Exception e) {
			Log.e(module, "Exception: ", e);
			if (progressBar != null) {
				progressBar.setVisibility(View.INVISIBLE);
			}
			Toast.makeText( context, "Upload failed: " + e, Toast.LENGTH_LONG ).show();	    		    			
		}	
		datasource.close();
	}
	
	public void updateProducts(ProgressBar progressBar, final CatalogListFragment listFragment) {
		Map paramMap = new HashMap();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String storeId = prefs.getString("storeId", "");			
		paramMap.put("boothId", storeId);
		try {   
			XMLRPCApacheAdapter adapter = new XMLRPCApacheAdapter(context);
			adapter.call("getProductPrices", paramMap, progressBar, new XMLRPCMethodCallback() {
				public void callFinished(Object result, ProgressBar progressBar) {
					if (result != null) {
						final ProductsDataSource datasource = new ProductsDataSource(context);
				    	Map priceResults = (Map)((Map)result).get("productsPrice");
				    	Log.d(module, "priceResults.size() = " + priceResults.size());
				    	datasource.open();
				    	datasource.deleteAllProducts();
				    	Iterator entries = priceResults.entrySet().iterator();
				    	while (entries.hasNext()) {
				    	  Entry thisEntry = (Entry) entries.next(); 
				    	  String productId = (String)thisEntry.getKey();
				    	  Map value = (Map)thisEntry.getValue();
				    	  String name = (String)value.get("name"); 
				    	  String description = (String)value.get("description");	
				    	  int sequenceNum = ((Long)value.get("sequenceNum")).intValue();				    	  
				    	  float price = ((BigDecimal)value.get("totalAmount")).setScale(2,BigDecimal.ROUND_HALF_UP).floatValue();
				    	  float mrpPrice = ((BigDecimal)value.get("mrpPrice")).setScale(2,BigDecimal.ROUND_HALF_UP).floatValue();
				    	  Product product = new Product(productId, name, description, sequenceNum, price, mrpPrice);
				    	  //Log.d(module, "product = " + product);		
				    	  datasource.insertProduct(product);
				    	} 
				    	datasource.close();
				    	if (listFragment != null) {
					    	Log.d(module, "calling listFragment notifyChange..." + listFragment.getClass().getName());						    		
				    		listFragment.notifyChange();
				    	}
					}
					if (progressBar != null) {
						progressBar.setVisibility(View.INVISIBLE);
					}
					Toast.makeText( context, "Updated product catalog!", Toast.LENGTH_SHORT ).show();	    		    			
				}
			});
		}
		catch (Exception e) {
			Log.e(module, "Exception: ", e);
			if (progressBar != null) {
				progressBar.setVisibility(View.INVISIBLE);
			}
			Toast.makeText( context, "Update product prices failed: " + e, Toast.LENGTH_SHORT ).show();	    		    			
		}	
	}	

	public void fetchActiveIndents(ProgressBar progressBar, final IndentListFragment listFragment) {
		Map paramMap = new HashMap();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String storeId = prefs.getString("storeId", "");			
		paramMap.put("facilityId", storeId);
	  	Date now = new Date();
		final Date supplyDate = DateUtil.addDays(now, 1);
		paramMap.put("supplyDate", supplyDate.getTime());	
    	Log.d(module, "supplyDate = " + supplyDate);	
		final Map productsMap = new HashMap();
		ProductsDataSource prodDataSource = new ProductsDataSource (context);
		prodDataSource.open();
		List<Product> products = prodDataSource.getAllProducts();
		for (int i =0; i < products.size(); ++i) {
			Product product = products.get(i);
			productsMap.put(product.getId(), product);
		}
		prodDataSource.close();
		if (productsMap.isEmpty()) {
			// don't do anything if we don't have any products
			Toast.makeText( context, "No products available, first fetch products.", Toast.LENGTH_SHORT ).show();	    		    						
			return;
		}    	
		try {   
			XMLRPCApacheAdapter adapter = new XMLRPCApacheAdapter(context);
			adapter.call("getFacilityIndent", paramMap, progressBar, new XMLRPCMethodCallback() {	
				public void callFinished(Object result, ProgressBar progressBar) {
					SimpleDateFormat  format = new SimpleDateFormat("yyyy-MM-dd");  		    	  
					if (result != null) {						
						IndentsDataSource indentDataSource = new IndentsDataSource(context);
						indentDataSource.open();  
						indentDataSource.deleteAllIndents();
				    	List<IndentItem> amIndentItems = new ArrayList<IndentItem>();
				    	List<IndentItem> pmIndentItems = new ArrayList<IndentItem>();
				    	Map indentResults = (Map)((Map)result).get("indentResults");
				    	Log.d(module, "indentResults.size() = " + indentResults.size());
				    	if (indentResults.size() > 0) {
				    		if (indentResults.get("AM") != null) {
								Date amSupplyDate = supplyDate;
								if (indentResults.get("amSupplyDate") != null) {
									String amSupplyDateStr = (String)indentResults.get("amSupplyDate");
									try {
										amSupplyDate = format.parse(amSupplyDateStr);
									} catch (ParseException e) {
										// just go with default date for now
									}
								}
					    		Object[] indentResultsAM = (Object[])indentResults.get("AM");
						    	Log.d(module, "indentResultsAM.size() = " + indentResultsAM.length);	
				    		  	for (int i=0; i < indentResultsAM.length; ++i) {
				    		  		Map prdQtyMap = (Map)indentResultsAM[i];
							    	String productId = (String)prdQtyMap.get("cProductId");
							    	float productQty = ((BigDecimal)prdQtyMap.get("cQuantity")).floatValue();
							    	Log.d(module, "productId = " + productId + "; productQty = " + productQty);
							    	Product product = (Product)productsMap.get(productId);
							    	IndentItem item = new IndentItem(productId, product.getName(), (int)productQty, product.getPrice());
							    	amIndentItems.add(item); 
				    		  	}   
				    		  	indentDataSource.insertIndentAndItems(amSupplyDate, "AM", amIndentItems);				    		  	
				    		}
				    		if (indentResults.get("PM") != null) {
								Date pmSupplyDate = supplyDate;
								if (indentResults.get("pmSupplyDate") != null) {
									String pmSupplyDateStr = (String)indentResults.get("pmSupplyDate");
									try {
										pmSupplyDate = format.parse(pmSupplyDateStr);
									} catch (ParseException e) {
										// just go with default date for now
									}
								}
					    		Object[] indentResultsPM = (Object[])indentResults.get("PM");
						    	Log.d(module, "indentResultsPM.size() = " + indentResultsPM.length);	
				    		  	for (int i=0; i < indentResultsPM.length; ++i) {
				    		  		Map prdQtyMap = (Map)indentResultsPM[i];
							    	String productId = (String)prdQtyMap.get("cProductId");
							    	float productQty = ((BigDecimal)prdQtyMap.get("cQuantity")).floatValue();
							    	Log.d(module, "productId = " + productId + "; productQty = " + productQty);
							    	Product product = (Product)productsMap.get(productId);
							    	IndentItem item = new IndentItem(productId, product.getName(), (int)productQty, product.getPrice());
							    	pmIndentItems.add(item);
				    		  	}   
				    		  	indentDataSource.insertIndentAndItems(pmSupplyDate, "PM", pmIndentItems);		    		  					    		  	
				    		}					    		
				    	}			
		    		  	indentDataSource.close();
				    	if (listFragment != null) {
					    	//Log.d(module, "calling listFragment notifyChange..." + listFragment.getClass().getName());						    		
				    		listFragment.notifyChange();
				    	}
					}
					if (progressBar != null) {
						progressBar.setVisibility(View.INVISIBLE);
					}
				}
			});
		}
		catch (Exception e) { 
			Log.e(module, "Exception: ", e);
			if (progressBar != null) {
				progressBar.setVisibility(View.INVISIBLE);
			}
			Toast.makeText( context, "getFacilityIndent failed: " + e, Toast.LENGTH_SHORT ).show();	    		    			
		}		
    	if (listFragment != null) {
	    	//Log.d(module, "calling listFragment notifyChange..." + listFragment.getClass().getName());						    		
    		listFragment.notifyChange();
    	}
	}	

	public void fetchPayments(ProgressBar progressBar, final PaymentListFragment listFragment) {
		Map paramMap = new HashMap();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String storeId = prefs.getString("storeId", "");			
		paramMap.put("facilityId", storeId);
	  	final Date thruDate = new Date();
		final Date fromDate = DateUtil.addDays(thruDate, -31);
		paramMap.put("fromDate", fromDate.getTime());					
		paramMap.put("thruDate", thruDate.getTime());			
		//::TODO:: add logic to first fetch active indents from server
		try {   
			XMLRPCApacheAdapter adapter = new XMLRPCApacheAdapter(context);
			adapter.call("getFacilityPayments", paramMap, progressBar, new XMLRPCMethodCallback() {	
				public void callFinished(Object result, ProgressBar progressBar) {
					if (result != null) {
						final PaymentsDataSource paymentsDataSource = new PaymentsDataSource (context);
						paymentsDataSource.open();  
				    	Map paymentsResult = (Map)((Map)result).get("paymentsResult");
				    	Log.d(module, "paymentsResult.size() = " + paymentsResult.size());	
				    	paymentsDataSource.deleteAllPayments();
				    	if (paymentsResult.size() > 0) {
				    		if (paymentsResult.get("paymentsList") != null) {
					    		Object[] boothPaymentsList = (Object[])paymentsResult.get("paymentsList");
						    	Log.d(module, "paymentsList.size() = " + boothPaymentsList.length);	
				    		  	for (int i=0; i < boothPaymentsList.length; ++i) {
				    		  		Map paymentMap = (Map)boothPaymentsList[i];				    		  		
							    	String paymentId = (String)paymentMap.get("paymentId");
							    	Date paymentDate = (Date)paymentMap.get("paymentDate");
							    	String paymentMethodTypeId = (String)paymentMap.get("paymentMethodTypeId");							    	
							    	double amount = ((BigDecimal)paymentMap.get("amount")).doubleValue();
							    	Log.d(module, "paymentId = " + paymentId + "; paymentMethod =" + paymentMethodTypeId 
							    			+ "; paymentDate=" + paymentDate + "; amount = " + amount);	
							    	paymentsDataSource.insertPayment(paymentId, paymentDate, paymentMethodTypeId, amount);
				    		  	}   
				    		}					    		
				    	}				    		  	
		    		  	paymentsDataSource.close();
				    	if (listFragment != null) {
					    	//Log.d(module, "calling listFragment notifyChange..." + listFragment.getClass().getName());						    		
				    		listFragment.notifyChange();
				    	}
					}
					if (progressBar != null) {
						progressBar.setVisibility(View.INVISIBLE);
					}
					Toast.makeText( context, "Updated payments!", Toast.LENGTH_SHORT ).show();	    		    			
				}
			});
		}
		catch (Exception e) {
			Log.e(module, "Exception: ", e);
			if (progressBar != null) {
				progressBar.setVisibility(View.INVISIBLE);
			}
			Toast.makeText( context, "getFacilityPayments failed: " + e, Toast.LENGTH_SHORT ).show();	    		    			
		}		
    	if (listFragment != null) {
	    	//Log.d(module, "calling listFragment notifyChange..." + listFragment.getClass().getName());						    		
    		listFragment.notifyChange();
    	}
	}		
	
	public void getFacilityDues(ProgressBar progressBar, final SalesDashboardActivity activity) {
		Map paramMap = new HashMap();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String storeId = prefs.getString("storeId", "");			
		paramMap.put("boothId", storeId);
			
		try {   
			XMLRPCApacheAdapter adapter = new XMLRPCApacheAdapter(context);
			adapter.call("getFacilityDues", paramMap, progressBar, new XMLRPCMethodCallback() {	
				public void callFinished(Object result, ProgressBar progressBar) {
					if (result != null) { 
				    	Map boothDues = (Map)((Map)result).get("boothDues");
				    	Map boothTotalDues = (Map)((Map)result).get("boothTotalDues");				    	
				    	Log.d(module, "boothDues.size() = " + boothDues.size());
				    	Log.d(module, "boothTotalDues.size() = " + boothTotalDues.size());					    	
				    	if (activity != null) {
					    	//Log.d(module, "calling listFragment notifyChange..." + listFragment.getClass().getName());						    		
				    		activity.updateDues(boothDues, boothTotalDues);
				    	}
					}
					if (progressBar != null) {
						progressBar.setVisibility(View.INVISIBLE);
					}
					//Toast.makeText( context, "got facility dues!", Toast.LENGTH_SHORT ).show();	    		    			
				}
			});
		}
		catch (Exception e) {
			Log.e(module, "Exception: ", e);
			if (progressBar != null) {
				progressBar.setVisibility(View.INVISIBLE);
			}
			Toast.makeText( context, "getFacilityDues failed: " + e, Toast.LENGTH_SHORT ).show();	    		    			
		}		
	}	
	
	public void fetchOrders(ProgressBar progressBar, final OrderListFragment listFragment) {
		Map paramMap = new HashMap();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String storeId = prefs.getString("storeId", "");			
		paramMap.put("facilityId", storeId);
	  	final Date thruDate = new Date();
		final Date fromDate = DateUtil.addDays(thruDate, -31);
		paramMap.put("fromDate", fromDate.getTime());	 				
		paramMap.put("thruDate", thruDate.getTime());			
		try {   
			XMLRPCApacheAdapter adapter = new XMLRPCApacheAdapter(context);
			adapter.call("getFacilityOrders", paramMap, progressBar, new XMLRPCMethodCallback() {	
				public void callFinished(Object result, ProgressBar progressBar) {
					if (result != null) {
						final OrdersDataSource ordersDataSource = new OrdersDataSource (context);
						ordersDataSource.open();  
				    	Map ordersResult = (Map)((Map)result).get("ordersResult");
				    	Log.d(module, "ordersResult.size() = " + ordersResult.size());	  
				    	ordersDataSource.deleteAllOrders();	 
				    	if (ordersResult.size() > 0) {
			    		  	ordersDataSource.insertOrderAndItems(ordersResult);				    		
				    	}						    	
		    		  	ordersDataSource.close();
				    	if (listFragment != null) {
					    	//Log.d(module, "calling listFragment notifyChange..." + listFragment.getClass().getName());						    		
				    		listFragment.notifyChange();
				    	}
					}
					if (progressBar != null) {
						progressBar.setVisibility(View.INVISIBLE);
					}
					Toast.makeText( context, "Updated orders!", Toast.LENGTH_SHORT ).show();	    		    			
				}
			});
		}
		catch (Exception e) {
			Log.e(module, "Exception: ", e);
			if (progressBar != null) {
				progressBar.setVisibility(View.INVISIBLE);
			}
			Toast.makeText( context, "fetchOrders failed: " + e, Toast.LENGTH_SHORT ).show();	    		    			
		}		
    	if (listFragment != null) {
	    	//Log.d(module, "calling listFragment notifyChange..." + listFragment.getClass().getName());						    		
    		listFragment.notifyChange();
    	}
	}		
	
	public void updateFacilities(ProgressBar progressBar, final FacilityListFragment listFragment) {
		final FacilityDataSource datasource = new FacilityDataSource(context);
		datasource.open();  
		Map paramMap = new HashMap();
		try {   
			XMLRPCApacheAdapter adapter = new XMLRPCApacheAdapter(context);
			adapter.call("getAllRMFacilities", paramMap, progressBar, new XMLRPCMethodCallback() {
				public void callFinished(Object result, ProgressBar progressBar) {
					if (result != null) {
				    	Map facilitiesResult = (Map)((Map)result).get("facilitiesResult");
				    	Log.d(module, "facilitiesResult.size() = " + facilitiesResult.size());
				    	datasource.open();
				    	datasource.deleteAllFacilities();
				    	if (facilitiesResult.size() > 0) {
				    		if (facilitiesResult.get("boothsDetailsList") != null) {
					    		Object[] boothsDetailsList = (Object[])facilitiesResult.get("boothsDetailsList");
						    	Log.d(module, "boothsDetailsList.size() = " + boothsDetailsList.length);	
				    		  	for (int i=0; i < boothsDetailsList.length; ++i) {
				    		  		Map boothMap = (Map)boothsDetailsList[i];				    		  		
							    	String id = (String)boothMap.get("facilityId");
							    	String name = (String)boothMap.get("facilityName");
							    	String category = (String)boothMap.get("category");	
							    	String ownerPhone = (String)boothMap.get("ownerPhone");	
							    	String salesRep = (String)boothMap.get("salesRep");							    								    	
							    	String amRouteId = (String)boothMap.get("amRouteId");							    	
							    	String pmRouteId = (String)boothMap.get("pmRouteId");
							    	String latitude = (String)boothMap.get("latitude");
							    	String longitude = (String)boothMap.get("longitude");							    	
							    	Facility facility = new Facility(id, name, category, 
							    			ownerPhone, salesRep, amRouteId, pmRouteId, latitude, longitude);
							    	//Log.d(module, "facility = " + facility);	  
							    	datasource.insertFacility(facility);
				    		  	}   
				    		}	
				    	}
				    	datasource.close();
				    	if (listFragment != null) {
					    	//Log.d(module, "calling listFragment notifyChange..." + listFragment.getClass().getName());						    		
				    		listFragment.notifyChange();
				    	}
					}
					if (progressBar != null) {
						progressBar.setVisibility(View.INVISIBLE);
					}
					Toast.makeText( context, "Updated outlets!", Toast.LENGTH_SHORT ).show();
				}
			});
		}
		catch (Exception e) {
			Log.e(module, "Exception: ", e);
			if (progressBar != null) {
				progressBar.setVisibility(View.INVISIBLE);
			}
			Toast.makeText( context, "Update outlets failed: " + e, Toast.LENGTH_SHORT ).show();	    		    			
		}	
    	if (listFragment != null) {
	    	//Log.d(module, "calling listFragment notifyChange..." + listFragment.getClass().getName());						    		
    		listFragment.notifyChange();
    	}
	}		

	public void updateEmployees(ProgressBar progressBar, final EmployeeListFragment listFragment) {
		Map paramMap = new HashMap();
		try {   
			XMLRPCApacheAdapter adapter = new XMLRPCApacheAdapter(context);
			adapter.call("getActiveEmployees", paramMap, progressBar, new XMLRPCMethodCallback() {
				public void callFinished(Object result, ProgressBar progressBar) {
					if (result != null) {
						SimpleDateFormat  format = new SimpleDateFormat("yyyy-MM-dd");  		    	  
				    	Map employeesResult = (Map)((Map)result).get("employeesResult");
				    	Log.d(module, "employeesResult.size() = " + employeesResult.size());
						final EmployeeDataSource datasource = new EmployeeDataSource(context);
				    	datasource.open();
				    	datasource.deleteAllEmployees();
				    	if (employeesResult.size() > 0) {
				    		if (employeesResult.get("employeeList") != null) {
					    		Object[] employeesList = (Object[])employeesResult.get("employeeList");
						    	Log.d(module, "employeesList.size() = " + employeesList.length);	
				    		  	for (int i=0; i < employeesList.length; ++i) {
				    		  		Map employeeMap = (Map)employeesList[i];				    		  		
							    	String id = (String)employeeMap.get("employeeId");
							    	String name = (String)employeeMap.get("name");
							    	String department = (String)employeeMap.get("department");	
							    	String position = (String)employeeMap.get("position");	
							    	String phoneNumber = (String)employeeMap.get("phoneNumber");
									Date joinDate = new Date();
									String joinDateStr = (String)employeeMap.get("joinDate");
									try {
										joinDate = format.parse(joinDateStr);
									} catch (ParseException e) {
										// just go with default date for now
									}	
							    	String weeklyOff = (String)employeeMap.get("weeklyOff");
									
							    	Employee employee = new Employee(id, name, department, 
							    			position, phoneNumber, joinDate, weeklyOff);
					    			if (employeeMap.get("leaveBalanceDate") != null) {
					    				Date leaveBalanceDate = (Date)employeeMap.get("leaveBalanceDate");
					    				//Log.d(module, "leaveBalanceDate=" + leaveBalanceDate);	
					    				BigDecimal earnedLeaveBalance = BigDecimal.ZERO;
					    				BigDecimal casualLeaveBalance = BigDecimal.ZERO;
					    				BigDecimal halfPayLeaveBalance = BigDecimal.ZERO;				    					
					    				if (employeeMap.get("earnedLeaveBalance") != null) {
					    					earnedLeaveBalance = (BigDecimal)employeeMap.get("earnedLeaveBalance");				    						
					    				}				    					
					    				if (employeeMap.get("casualLeaveBalance") != null) {
					    					casualLeaveBalance = (BigDecimal)employeeMap.get("casualLeaveBalance");				    						
					    				}
					    				if (employeeMap.get("halfPayLeaveBalance") != null) {
					    					halfPayLeaveBalance = (BigDecimal)employeeMap.get("halfPayLeaveBalance");				    						
					    				}	
					    				employee.setLeaveBalanceDate(leaveBalanceDate);
				    					employee.setEarnedLeave(earnedLeaveBalance.floatValue());
				    					employee.setCasualLeave(casualLeaveBalance.floatValue());
				    					employee.setHalfPayLeave(halfPayLeaveBalance.floatValue());	
					    			}
					    			datasource.insertEmployee(employee);
				    		  	}   
				    		}	
				    	}
				    	datasource.close();
				    	if (listFragment != null) {
					    	//Log.d(module, "calling listFragment notifyChange..." + listFragment.getClass().getName());						    		
				    		listFragment.notifyChange();
				    	}
					}
					if (progressBar != null) {
						progressBar.setVisibility(View.INVISIBLE);
					}
					Toast.makeText( context, "Updated employees!", Toast.LENGTH_SHORT ).show();
				}
			});
		}
		catch (Exception e) {
			Log.e(module, "Exception: ", e);
			if (progressBar != null) {
				progressBar.setVisibility(View.INVISIBLE);
			}
			Toast.makeText( context, "Update employees failed: " + e, Toast.LENGTH_SHORT ).show();	    		    			
		}	
    	if (listFragment != null) {
	    	//Log.d(module, "calling listFragment notifyChange..." + listFragment.getClass().getName());						    		
    		listFragment.notifyChange();
    	}
	}	
	
	public void fetchMyEmployeeDetails(ProgressBar progressBar, final HRDashboardActivity activity) {
		Map paramMap = new HashMap();		
		final EmployeeDataSource datasource = new EmployeeDataSource(context);
		datasource.open();
		try {   
			XMLRPCApacheAdapter adapter = new XMLRPCApacheAdapter(context);
			adapter.call("fetchEmployeeDetails", paramMap, progressBar, new XMLRPCMethodCallback() {	
				public void callFinished(Object result, ProgressBar progressBar) {
					if (result != null) { 
						SimpleDateFormat  format = new SimpleDateFormat("yyyy-MM-dd");  		    	  						
				    	Map employeeDetailsResult = (Map)((Map)result).get("employeeDetailsResult");
				    	Map employeeMap = (Map)((Map)employeeDetailsResult).get("employeeProfile");
				    	if (employeeMap != null) {
				    		String id = (String)employeeMap.get("employeeId");
				    		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
				    		SharedPreferences.Editor prefEditor = prefs.edit();
				    		prefEditor.putString("employeeId", id);
				    		prefEditor.commit(); 					    	
				    		String name = (String)employeeMap.get("name");
				    		String department = (String)employeeMap.get("department");	
				    		String position = (String)employeeMap.get("position");	
				    		String phoneNumber = (String)employeeMap.get("phoneNumber");
				    		Date joinDate = new Date();
				    		String joinDateStr = (String)employeeMap.get("joinDate");
				    		try {
				    			joinDate = format.parse(joinDateStr);
				    		} catch (ParseException e) {
				    			// just go with default date for now
				    		}
					    	String weeklyOff = (String)employeeMap.get("weeklyOff");				    		
				    		Employee employee = new Employee(id, name, department, 
				    				position, phoneNumber, joinDate, weeklyOff);
				    		if (employeeMap.get("leaveBalanceDate") != null) {
				    			Date leaveBalanceDate = (Date)employeeMap.get("leaveBalanceDate");
				    			//Log.d(module, "leaveBalanceDate=" + leaveBalanceDate);	
				    			BigDecimal earnedLeaveBalance = BigDecimal.ZERO;
				    			BigDecimal casualLeaveBalance = BigDecimal.ZERO;
				    			BigDecimal halfPayLeaveBalance = BigDecimal.ZERO;				    					
				    			if (employeeMap.get("earnedLeaveBalance") != null) {
				    				earnedLeaveBalance = (BigDecimal)employeeMap.get("earnedLeaveBalance");				    						
				    			}				    					
				    			if (employeeMap.get("casualLeaveBalance") != null) {
				    				casualLeaveBalance = (BigDecimal)employeeMap.get("casualLeaveBalance");				    						
				    			}
				    			if (employeeMap.get("halfPayLeaveBalance") != null) {
				    				halfPayLeaveBalance = (BigDecimal)employeeMap.get("halfPayLeaveBalance");				    						
				    			}	
				    			employee.setLeaveBalanceDate(leaveBalanceDate);
				    			employee.setEarnedLeave(earnedLeaveBalance.floatValue());
				    			employee.setCasualLeave(casualLeaveBalance.floatValue());
				    			employee.setHalfPayLeave(halfPayLeaveBalance.floatValue());
			    				//Log.d(module, "employeeId=" + employee.getId());	
			    				//Log.d(module, "leaveBalanceDate=" + employee.getLeaveBalanceDate());	
			    				//Log.d(module, "earnedLeaveBalance=" + employee.getEarnedLeave());				    				
				    		}
				    		datasource.updateEmployee(employee); 
				    		Object[] payslips = (Object[])((Map)employeeDetailsResult).get("payslips");
					    	if (payslips != null) {
					    		final PayslipDataSource payslipDS = new PayslipDataSource(context);
					    		payslipDS.open();
					    		payslipDS.deleteAllPayslips();
					    		payslipDS.insertPayslips(id, payslips);
					    		payslipDS.close();
					    	}
//					    	if (activity != null) {
//					    		activity.updateEmployeeDetails(employee);
//					    	}				    		
				    	}

				    	datasource.close();

					}
					if (progressBar != null) {
						progressBar.setVisibility(View.INVISIBLE);
					}
					//Toast.makeText( context, "got facility dues!", Toast.LENGTH_SHORT ).show();	    		    			
				}
			});
		}
		catch (Exception e) {
			Log.e(module, "Exception: ", e);
			if (progressBar != null) {
				progressBar.setVisibility(View.INVISIBLE);
			}
			Toast.makeText( context, "fetchMyEmployeeDetails failed: " + e, Toast.LENGTH_SHORT ).show();	    		    			
		}		
	}		
	
	public void fetchEmployeeRecentLeaves(ProgressBar progressBar, final LeaveActivity leaveActivity) {
		Map paramMap = new HashMap();		
		final EmployeeDataSource emplDatasource = new EmployeeDataSource(context);
		try {   
			XMLRPCApacheAdapter adapter = new XMLRPCApacheAdapter(context);
			adapter.call("fetchEmployeeRecentLeaves", paramMap, progressBar, new XMLRPCMethodCallback() {	
				public void callFinished(Object result, ProgressBar progressBar) {
					if (result != null) { 
						SimpleDateFormat  format = new SimpleDateFormat("yyyy-MM-dd");  		    	  						
				    	Map employeeLeavesResult = (Map)((Map)result).get("employeeLeavesResult");
			    		Object[] recentLeaves = (Object[])((Map)employeeLeavesResult).get("recentLeaves");
				    	if (recentLeaves != null) {
				    		String id = (String)employeeLeavesResult.get("employeeId");
				    		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
				    		SharedPreferences.Editor prefEditor = prefs.edit();
				    		prefEditor.putString("employeeId", id);
				    		prefEditor.commit(); 	
				    		emplDatasource.open();
				    		Employee employee = emplDatasource.getEmployeeDetails(id);		
				    		if (employee != null) {        
					    		if (employeeLeavesResult.get("leaveBalanceDate") != null) {
					    			Date leaveBalanceDate = (Date)employeeLeavesResult.get("leaveBalanceDate");
					    			//Log.d(module, "leaveBalanceDate=" + leaveBalanceDate);	
					    			BigDecimal earnedLeaveBalance = BigDecimal.ZERO;
					    			BigDecimal casualLeaveBalance = BigDecimal.ZERO;
					    			BigDecimal halfPayLeaveBalance = BigDecimal.ZERO;				    					
					    			if (employeeLeavesResult.get("earnedLeaveBalance") != null) {
					    				earnedLeaveBalance = (BigDecimal)employeeLeavesResult.get("earnedLeaveBalance");				    						
					    			}				    					
					    			if (employeeLeavesResult.get("casualLeaveBalance") != null) {
					    				casualLeaveBalance = (BigDecimal)employeeLeavesResult.get("casualLeaveBalance");				    						
					    			}
					    			if (employeeLeavesResult.get("halfPayLeaveBalance") != null) {
					    				halfPayLeaveBalance = (BigDecimal)employeeLeavesResult.get("halfPayLeaveBalance");				    						
					    			}	
					    			employee.setLeaveBalanceDate(leaveBalanceDate);
					    			employee.setEarnedLeave(earnedLeaveBalance.floatValue());
					    			employee.setCasualLeave(casualLeaveBalance.floatValue());
					    			employee.setHalfPayLeave(halfPayLeaveBalance.floatValue());
					    		}
						    	Object[] leaves = (Object[])((Map)employeeLeavesResult).get("recentLeaves");
						    	if (leaves != null) {
						    		final LeavesDataSource leavesDS = new LeavesDataSource(context);
						    		leavesDS.open();
						    		leavesDS.deleteAllLeaves();
						    		leavesDS.insertLeaves(id, leaves);
						    		leavesDS.close();		
						    	}
				    				//Log.d(module, "employeeId=" + employee.getId());	
				    				//Log.d(module, "leaveBalanceDate=" + employee.getLeaveBalanceDate());	
				    				//Log.d(module, "earnedLeaveBalance=" + employee.getEarnedLeave());				    				
					    		emplDatasource.updateEmployee(employee); 
					    		emplDatasource.close();
						    	if (leaveActivity != null) {
						    		leaveActivity.updateLeaveDetails(employee);
						    	}						    		
				    		}
			    		
				    	}
					}
					if (progressBar != null) {
						progressBar.setVisibility(View.INVISIBLE);
					}
					//Toast.makeText( context, "got facility dues!", Toast.LENGTH_SHORT ).show();	    		    			
				}
			});
		}
		catch (Exception e) {
			Log.e(module, "Exception: ", e);
			if (progressBar != null) {
				progressBar.setVisibility(View.INVISIBLE);
			}
			Toast.makeText( context, "fetchEmployeeRecentLeaves failed: " + e, Toast.LENGTH_SHORT ).show();	    		    			
		}		
	}	

	
	public void fetchEmployeeAttendance(ProgressBar progressBar, final AttendanceListFragment listFragment) {
		Map paramMap = new HashMap();		
		try {   
			XMLRPCApacheAdapter adapter = new XMLRPCApacheAdapter(context);
			adapter.call("fetchEmployeeAttendance", paramMap, progressBar, new XMLRPCMethodCallback() {	
				public void callFinished(Object result, ProgressBar progressBar) {
					if (result != null) {
				    	Map employeeAttendanceResult = (Map)((Map)result).get("employeeAttendanceResult");
			    		Object[] recentPunches = (Object[])((Map)employeeAttendanceResult).get("recentPunches");
				    	if (recentPunches != null) {
					    	Object[] punches = (Object[])((Map)employeeAttendanceResult).get("recentPunches");
					    	if (punches != null) {
					    		String id = (String)employeeAttendanceResult.get("employeeId");
					    		final AttendanceDataSource attendanceDS = new AttendanceDataSource(context);
					    		attendanceDS.open();
					    		attendanceDS.deleteAllPunches();
					    		attendanceDS.insertPunches(id, punches);
					    		attendanceDS.close();		
					    	} 
					    	if (listFragment != null) {
					    		listFragment.notifyChange();
					    	}
				    	}				    		  	
					} 
					if (progressBar != null) {
						progressBar.setVisibility(View.INVISIBLE);
					}
				}
			});
		}
		catch (Exception e) {
			Log.e(module, "Exception: ", e);
			if (progressBar != null) {
				progressBar.setVisibility(View.INVISIBLE);
			}
			Toast.makeText( context, "fetchEmployeeAttendance failed: " + e, Toast.LENGTH_SHORT ).show();	    		    			
		}		
	}	
	
	public void fetchEmployeeLastPunch(String employeeId, final EmployeeDetailsActivity activity) {
		Map paramMap = new HashMap();		
		paramMap.put("partyId", employeeId); 
//Log.d( module, "paramMap =" + paramMap); 
		
		try {   
			XMLRPCApacheAdapter adapter = new XMLRPCApacheAdapter(context);
			adapter.call("fetchEmployeeLastPunch", paramMap, null, new XMLRPCMethodCallback() {	
				public void callFinished(Object result, ProgressBar progressBar) {
					if (result != null) {
				    	Map employeeLastPunchResult = (Map)((Map)result).get("employeeLastPunchResult");
				    	Map punchMap = (Map)((Map)employeeLastPunchResult).get("punch");
				    	if (punchMap != null) {
				    		String punchTime = (String)punchMap.get("punchTime");
				    		String inOut = (String)punchMap.get("inOut");
					    	if (activity != null && punchTime != null) {
					    		activity.updateLastPunchTime(punchTime, inOut);
					    	}
				    	}				    		  	
					} 
				}
			});
		}
		catch (Exception e) {
			Log.e(module, "Exception: ", e);
			//Toast.makeText( context, "fetchEmployeeLastPunch failed: " + e, Toast.LENGTH_SHORT ).show();	    		    			
		}		
	}		
	
	public static boolean isNetworkAvailable(Context context) 
	{
	    return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
	}	
}
