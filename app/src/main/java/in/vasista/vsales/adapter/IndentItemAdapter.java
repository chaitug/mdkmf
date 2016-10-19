package in.vasista.vsales.adapter;


import in.vasista.milkosoft.mdkmf.R;
import in.vasista.vsales.indent.IndentItem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
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


public class IndentItemAdapter extends ArrayAdapter<IndentItem>{
	public static final String module = IndentItemAdapter.class.getName();
	
	  int resource;
	  boolean isEditable = false;
	  List<IndentItem> myItems;
	  public IndentItemAdapter(Context context,
	                         int resource,
	                         List<IndentItem> items) {
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
		
	  @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    LinearLayout indentItemView;

	    IndentItem item = getItem(position);

	    String productId = item.getProductId();
	    String productName = item.getProductName();
	    String qty = Integer.toString(item.getQty());
	    double unitPrice = item.getUnitPrice();
		double itemTotal = Math.round(item.getQty()*unitPrice * 100.0) / 100.0;
	    String amount = String.format("%.2f", itemTotal);
	    if(item.getQty() == -1) {
	    	qty = "";
	    	amount = "";
	    }
		//Log.d( module, "item=" + item); 		  

	    if (convertView == null) {
	      indentItemView = new LinearLayout(getContext());
	      String inflater = Context.LAYOUT_INFLATER_SERVICE;
	      LayoutInflater li;
	      li = (LayoutInflater)getContext().getSystemService(inflater);
	      li.inflate(resource, indentItemView, true);
	    } else {
	    	indentItemView = (LinearLayout) convertView;
	    }

	    TextView nameView = (TextView)indentItemView.findViewById(R.id.indentitemRowProductName);
	    final TextView qtyView = (TextView)indentItemView.findViewById(R.id.indentitemRowQty);
	    final TextView amountView = (TextView)indentItemView.findViewById(R.id.indentitemRowAmount);

	    nameView.setText(productName);
	    qtyView.setText(qty);  
	    
	    if (!isEditable)  {
	    	qtyView.setFocusable(false);
	    	int sdk = android.os.Build.VERSION.SDK_INT;
	    	if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
	    		qtyView.setBackgroundDrawable(null); 
	    	} else {
	    		qtyView.setBackground(null);
	    	}
	    }
        
	    amountView.setText(amount);
	    return indentItemView;
	  }
}

