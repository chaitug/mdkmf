package in.vasista.vsales.order;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import in.vasista.milkosoft.mdkmf.R;
import in.vasista.vsales.adapter.OrderItemAdapter;
import in.vasista.vsales.db.OrdersDataSource;
import in.vasista.vsales.util.DateUtil;


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
