package in.vasista.vsales.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import in.vasista.milkosoft.mdkmf.R;
import in.vasista.vsales.catalog.Product;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;


public class ProductAdapter extends ArrayAdapter<Product> {
	  int resource;

	  public ProductAdapter(Context context,
	                         int resource,
	                         List<Product> items) {
	    super(context, resource, items);
	    this.resource = resource;
	  }

	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    LinearLayout productView;

	    Product item = getItem(position);

	    String name = item.getName();
	    String description = item.getDescription();	    
	    String price = String.format("%.2f", item.getPrice()) ;
	    String mrpPrice = String.format("%.2f", item.getMrpPrice()) ;

	    if (convertView == null) { 
	      productView = new LinearLayout(getContext());
	      String inflater = Context.LAYOUT_INFLATER_SERVICE;
	      LayoutInflater li;
	      li = (LayoutInflater)getContext().getSystemService(inflater);
	      li.inflate(resource, productView, true);
	    } else {
	    	productView = (LinearLayout) convertView;
	    }

	    TextView nameView = (TextView)productView.findViewById(R.id.rowName);
	    TextView descriptionView = (TextView)productView.findViewById(R.id.rowDescription);	    
	    TextView priceView = (TextView)productView.findViewById(R.id.rowPrice);
	    TextView mrpPriceView = (TextView)productView.findViewById(R.id.rowMrpPrice);

	    nameView.setText(name);
	    descriptionView.setText(description);
	    priceView.setText(price);
	    mrpPriceView.setText(mrpPrice);  
	    return productView;
	  }
}
