package in.vasista.vsales.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import in.vasista.milkosoft.mdkmf.R;
import in.vasista.vsales.indent.Indent;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;



public class IndentAdapter extends ArrayAdapter<Indent> {
	  int resource;

	  public IndentAdapter(Context context,
	                         int resource,
	                         List<Indent> items) {
	    super(context, resource, items);
	    this.resource = resource;
	  }  

	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    LinearLayout indentView;

	   Indent item = getItem(position);

	    String id = Integer.toString(item.getId());
	    Date date = item.getSupplyDate();
	    SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM, yyyy");
	    String dateStr = dateFormat.format(date);
	    String status = item.getStatus();
	    String subscriptionType = item.getSubscriptionType();
	    
	    boolean isSynced = item.isSynced();	 
	    String syncStr = isSynced?"Y":"N";	    
	    String total = String.format("%.2f", item.getTotal());


	    if (convertView == null) {
	    	indentView = new LinearLayout(getContext()); 
	      String inflater = Context.LAYOUT_INFLATER_SERVICE;
	      LayoutInflater li;
	      li = (LayoutInflater)getContext().getSystemService(inflater);
	      li.inflate(resource, indentView, true);
	    } else {
	    	indentView = (LinearLayout) convertView;  
	    }     

	    TextView dateView = (TextView)indentView.findViewById(R.id.indentRowDate);
	    TextView subscriptionTypeView = (TextView)indentView.findViewById(R.id.indentRowSupply);	    
	    //TextView syncedView = (TextView)indentView.findViewById(R.id.indentRowSynced);	    	    
	    TextView totalView = (TextView)indentView.findViewById(R.id.indentRowTotal);
	    
	    dateView.setText(dateStr);
	    subscriptionTypeView.setText(subscriptionType);
	    //syncedView.setText(syncStr);      	    
	    totalView.setText(total);        
	    return indentView;
	  }
}
