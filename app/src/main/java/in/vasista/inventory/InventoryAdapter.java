package in.vasista.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import in.vasista.milkosoft.mdkmf.R;
import in.vasista.vsales.catalog.Product;
import in.vasista.vsales.employee.Employee;
import in.vasista.vsales.facility.Facility;
import in.vasista.vsales.facility.FacilityListFragment;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.TextView;  


public class InventoryAdapter extends ArrayAdapter<Product> {
	public static final String module = InventoryAdapter.class.getName();	
	
	  int resource;
	  public ArrayList<Product> items = new ArrayList<Product>();;
	    public ArrayList<Product> filtered = new ArrayList<Product>();;
	    private Context context;
	    private Filter filter;

	    public InventoryAdapter(Context context, int resource, List<Product> items)
	    {
	        super(context, resource, items);
		    this.resource = resource;	        
	        this.filtered.addAll(items);
	        this.items.addAll(items);
	        this.context = context;
	        this.filter = new InventoryAutoFilter();
	    }
	    
	    @Override
	    public int getCount() {
	        return filtered.size();
	    }
	 
	    @Override
	    public Product getItem(int position) {
	        return filtered.get(position);
	    }	    

	    @Override
	    public View getView(int pos, View convertView, ViewGroup parent)
	    {
		    LinearLayout inventoryView;

		    Product item = getItem(pos);
		    String id = item.getId();
		    String name = item.getName();
		    String category = item.getProductCategoryId();	    
		    String description = item.getDescription();	     
		    

		    if (convertView == null) { 
		    	inventoryView = new LinearLayout(getContext());
		      String inflater = Context.LAYOUT_INFLATER_SERVICE;
		      LayoutInflater li;
		      li = (LayoutInflater)getContext().getSystemService(inflater);
		      li.inflate(resource, inventoryView, true);
		    } else {
		    	inventoryView = (LinearLayout) convertView;  
		    }

		    //TextView idView = (TextView)employeeView.findViewById(R.id.employeeRowId);
		    TextView nameView = (TextView)inventoryView.findViewById(R.id.inventoryRowName);	    
		    TextView descriptionView = (TextView)inventoryView.findViewById(R.id.inventoryRowDescription);
		    TextView categoryView = (TextView)inventoryView.findViewById(R.id.inventoryRowCategory);		    

		    //idView.setText(id);
		    nameView.setText(name);
		    descriptionView.setText(description);
		    categoryView.setText(category);
		    return inventoryView;
	    }


	    @Override
	    public Filter getFilter()
	    {
	        if(filter == null)
	            filter = new InventoryAutoFilter();
	        return filter;
	    }

	    private class InventoryAutoFilter extends Filter
	    {

	        @Override
	        protected FilterResults performFiltering(CharSequence constraint) {
	            // NOTE: this function is *always* called from a background thread, and
	            // not the UI thread.
	            FilterResults result = new FilterResults();
	            if(constraint != null && constraint.toString().length() > 0)
	            {
	                ArrayList<Product> filt = new ArrayList<Product>();
	                ArrayList<Product> lItems = new ArrayList<Product>();
	                //synchronized (this)
	                {
	                    lItems.addAll(items);
	                }	                
	                for(int i = 0, l = lItems.size(); i < l; i++)
	                {
	                	Product product = lItems.get(i); 
    	                if (product.getName().toLowerCase(Locale.getDefault()).contains(constraint.toString().toLowerCase(Locale.getDefault())) ||
    	                		product.getDescription().toLowerCase(Locale.getDefault()).contains(constraint.toString().toLowerCase(Locale.getDefault())) ||
    	                		product.getProductCategoryId().toLowerCase(Locale.getDefault()).contains(constraint.toString().toLowerCase(Locale.getDefault()))) {
    	                	filt.add(product); 
    	                }
	                }
	                result.count = filt.size();
	                result.values = filt;
	            }
	            else
	            {
	                //synchronized(this)
	                {
	                    result.values = items;
	                    result.count = items.size();
	                }
	            }
	            return result;
	        }

	        @SuppressWarnings("unchecked")
	        @Override
	        protected void publishResults(CharSequence constraint, FilterResults results) {
	            // NOTE: this function is *always* called from the UI thread.
                if (results != null && results.count > 0) {
                	filtered = (ArrayList<Product>)results.values;
                	clear();
                	addAll(filtered);
                }
	        }
	    }
}
