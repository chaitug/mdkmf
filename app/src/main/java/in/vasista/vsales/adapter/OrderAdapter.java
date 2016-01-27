package in.vasista.vsales.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import in.vasista.vsales.R;
import in.vasista.vsales.indent.Indent;
import in.vasista.vsales.order.Order;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;



public class OrderAdapter extends ArrayAdapter<Order> {
	  int resource;

	  public OrderAdapter(Context context,
	                         int resource,
	                         List<Order> items) {
	    super(context, resource, items);
	    this.resource = resource;
	  } 

	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    LinearLayout orderView;

	   Order item = getItem(position);

	    String id = Integer.toString(item.getId());
	    Date date = item.getSupplyDate();
	    SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM, yyyy");
	    String dateStr = dateFormat.format(date);
	    String subscriptionType = item.getSubscriptionType();
	    String total = String.format("%.2f", item.getTotal());


	    if (convertView == null) {
	    	orderView = new LinearLayout(getContext());
	      String inflater = Context.LAYOUT_INFLATER_SERVICE;
	      LayoutInflater li;
	      li = (LayoutInflater)getContext().getSystemService(inflater);
	      li.inflate(resource, orderView, true);
	    } else {
	    	orderView = (LinearLayout) convertView;                 
	    }     

	    //TextView idView = (TextView)orderView.findViewById(R.id.orderRowId);
	    TextView dateView = (TextView)orderView.findViewById(R.id.orderRowDate);
	    TextView subscriptionTypeView = (TextView)orderView.findViewById(R.id.orderRowSupply);	    
	    TextView totalView = (TextView)orderView.findViewById(R.id.orderRowTotal);
	    
	    //idView.setText(id);
	    dateView.setText(dateStr);
	    subscriptionTypeView.setText(subscriptionType);
	    totalView.setText(total);
	    return orderView;
	  }
}
