package in.vasista.vsales.adapter;


import in.vasista.vsales.R;
import in.vasista.vsales.indent.IndentItem;
import in.vasista.vsales.order.OrderItem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.res.Configuration;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


public class OrderItemAdapter extends ArrayAdapter<OrderItem>{
	public static final String module = OrderItemAdapter.class.getName();
	
	  int resource;
	  boolean isEditable = false;
	  List<OrderItem> myItems;
	  public OrderItemAdapter(Context context,
	                         int resource,
	                         List<OrderItem> items) {
	    super(context, resource, items);
	    this.myItems = items;
	    this.resource = resource;
	  }

	  public boolean isEditable() {
		  return isEditable;
	  }

		public void setEditable(boolean isEditable) {
			this.isEditable = isEditable;
		}
		
	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    LinearLayout orderItemView;

	    OrderItem item = getItem(position);

	    String productId = item.getProductId();
	    String productName = item.getProductName();
	    String qty = Integer.toString(item.getQty());
	    double unitPrice = item.getUnitPrice();
		double itemTotal = Math.round(unitPrice * 100.0) / 100.0;
	    String amount = String.format("%.2f", itemTotal);
	    if(item.getQty() == -1) {
	    	qty = "";
	    	amount = "";
	    }
		//Log.d( module, "item=" + item); 		  

	    if (convertView == null) {
	    	orderItemView = new LinearLayout(getContext());
	      String inflater = Context.LAYOUT_INFLATER_SERVICE;
	      LayoutInflater li;
	      li = (LayoutInflater)getContext().getSystemService(inflater);
	      li.inflate(resource, orderItemView, true);
	    } else {
	    	orderItemView = (LinearLayout) convertView;
	    }

	    TextView nameView = (TextView)orderItemView.findViewById(R.id.orderitemRowProductName);
	    final TextView qtyView = (TextView)orderItemView.findViewById(R.id.orderitemRowQty);
	    final TextView amountView = (TextView)orderItemView.findViewById(R.id.orderitemRowAmount);

	    nameView.setText(productName);
	    qtyView.setText(qty);  
	    
        
	    amountView.setText(amount);
	    return orderItemView;
	  }
}

