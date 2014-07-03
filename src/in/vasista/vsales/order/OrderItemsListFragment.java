package in.vasista.vsales.order;

import in.vasista.vsales.R;
import in.vasista.vsales.adapter.IndentItemAdapter;
import in.vasista.vsales.adapter.OrderItemAdapter;
import in.vasista.vsales.catalog.Product;
import in.vasista.vsales.db.OrdersDataSource;
import in.vasista.vsales.db.ProductsDataSource;
import in.vasista.vsales.db.IndentsDataSource;
import in.vasista.vsales.sync.ServerSync;
import in.vasista.vsales.ui.SwipeDetector;
import in.vasista.vsales.util.DateUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


public class OrderItemsListFragment extends ListFragment{
	public static final String module = OrderItemsListFragment.class.getName();	
	
	OrderItemAdapter adapter;
    List<OrderItem> orderItems;	
    Order order;              
	OrdersDataSource datasource;   
	boolean isEditableList;
	ListView listView;      
	public void onActivityCreated(Bundle savedInstanceState) {
		
		super.onActivityCreated(savedInstanceState);  
		Intent orderItemsIntent= getActivity().getIntent();
		int orderId = -1;
		orderId = orderItemsIntent.getIntExtra("orderId", orderId);	 
		final String retailerId = orderItemsIntent.getStringExtra("retailerId");	 			
		if (adapter == null) {		
			datasource = new OrdersDataSource(getActivity()); 
			datasource.open();
			order = datasource.getOrderDetails(orderId);
			orderItems = datasource.getOrderItems(orderId);  
		}
	
		listView = getListView();
		final OrderItemsListFragment orderItemsListFragment = this;

		if (listView.getHeaderViewsCount() == 0) {	
			TextView orderDetailsTitle = (TextView)getActivity().findViewById(R.id.orderDetailsTitle);
			orderDetailsTitle.setText(retailerId + ": Order Details"); 
			final View headerView2 = getActivity().getLayoutInflater().inflate(R.layout.orderitems_header, null);
			listView.addHeaderView(headerView2);
		}

		if (adapter == null) {		
		    adapter = new OrderItemAdapter(getActivity(),
                    R.layout.orderitems_item,
                    orderItems);
		    updateOrderHeaderView();
		}
		setListAdapter(adapter);
	}
	
	public void onDestroyView() { 
		super.onDestroyView();
		setListAdapter(null);     
	}
	
	public void updateOrderHeaderView() {
		updateOrderHeaderViewInternal(order);	
	}
	
	void updateOrderHeaderViewInternal(Order order) {
		double total = order.getTotal();
		String totalStr = "Total: Rs " + total;
		String orderIdStr = "";
		Date date = DateUtil.addDays(new Date(), 1);
		String orderSupply = "";
		if (order != null) {  
			orderIdStr = "Order Id: " + Integer.toString(order.getId());
			date = order.getSupplyDate();    
			orderSupply = "Supply: " + order.getSubscriptionType();
		}
	    SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM, yyyy");  
	    String orderDateStr = dateFormat.format(date);
		TextView totalView = (TextView)listView.getRootView().findViewById(R.id.orderitemsTotal);
		if (totalView != null) {
			totalView.setText(totalStr);   
		}
//		TextView orderIdView = (TextView)listView.getRootView().findViewById(R.id.orderId);	
//		if (orderIdView != null) {
//			orderIdView.setText(orderIdStr);
//		}	
		TextView orderSupplyView = (TextView)listView.getRootView().findViewById(R.id.orderSupply);	
		if (orderSupplyView != null) {
			orderSupplyView.setText(orderSupply);
		}			
		TextView orderDateView = (TextView)listView.getRootView().findViewById(R.id.orderDate);	
		if (orderDateView != null) {
			orderDateView.setText(orderDateStr);
		}	
	}		
	

	
	/**
	 * Brute force update of list
	 */
	public void notifyChange() {
		if (order == null) {
			// nothing to do 
			return;
		}
		setListAdapter(null);
	    datasource.open();
	    orderItems = datasource.getOrderItems(order.getId());
	    
	    adapter = new OrderItemAdapter(getActivity(),
                R.layout.orderitems_item,
                orderItems);	
		setListAdapter(adapter);
	}

}
