package in.vasista.vsales.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import in.vasista.vsales.R;
import in.vasista.vsales.facility.Facility;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.TextView;  


public class FacilityAdapter extends ArrayAdapter<Facility> {
	  int resource;
	  public ArrayList<Facility> items = new ArrayList<Facility>();;
	    public ArrayList<Facility> filtered = new ArrayList<Facility>();;
	    private Context context;
	    private Filter filter;

	    public FacilityAdapter(Context context, int resource, List<Facility> items)
	    {
	        super(context, resource, items);
		    this.resource = resource;	        
	        this.filtered.addAll(items);
	        this.items.addAll(items);
	        this.context = context;
	        this.filter = new FacilityAutoFilter();
	    }
	    
	    @Override
	    public int getCount() {
	        return filtered.size();
	    }
	 
	    @Override
	    public Facility getItem(int position) {
	        return filtered.get(position);
	    }	    

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent)
	    {
		    LinearLayout facilityView;

		    Facility item = getItem(position);
		    String id = item.getId();
		    String name = item.getName();
		    String category = item.getCategory();	    
		    String phoneNum = item.getOwnerPhone();	 
		    String amRouteId = item.getAmRouteId();	    
		    String pmRouteId = item.getPmRouteId();	     
		    

		    if (convertView == null) { 
		    	facilityView = new LinearLayout(getContext());
		      String inflater = Context.LAYOUT_INFLATER_SERVICE;
		      LayoutInflater li;
		      li = (LayoutInflater)getContext().getSystemService(inflater);
		      li.inflate(resource, facilityView, true);
		    } else {
		    	facilityView = (LinearLayout) convertView;
		    }

		    TextView idView = (TextView)facilityView.findViewById(R.id.facilityRowId);
		    TextView nameView = (TextView)facilityView.findViewById(R.id.facilityRowName);	    
		    TextView categoryView = (TextView)facilityView.findViewById(R.id.facilityRowCategory);
		    //TextView routeView = (TextView)facilityView.findViewById(R.id.facilityRowRoute);
		    //TextView phoneNumView = (TextView)facilityView.findViewById(R.id.facilityRowPhone);
		    

		    idView.setText(id);
		    nameView.setText(name);
		    categoryView.setText(category);
		    //routeView.setText(amRouteId + " " + pmRouteId);
		    //phoneNumView.setText(phoneNum);
		    return facilityView;
	    }


	    @Override
	    public Filter getFilter()
	    {
	        if(filter == null)
	            filter = new FacilityAutoFilter();
	        return filter;
	    }

	    private class FacilityAutoFilter extends Filter
	    {

	        @Override
	        protected FilterResults performFiltering(CharSequence constraint) {
	            // NOTE: this function is *always* called from a background thread, and
	            // not the UI thread.
	            FilterResults result = new FilterResults();
	            if(constraint != null && constraint.toString().length() > 0)
	            {
	                ArrayList<Facility> filt = new ArrayList<Facility>();
	                ArrayList<Facility> lItems = new ArrayList<Facility>();
	                //synchronized (this)
	                {
	                    lItems.addAll(items);
	                }
	                for(int i = 0, l = lItems.size(); i < l; i++)
	                {
	                	Facility facility = lItems.get(i);
    	                if (facility.getId().toLowerCase(Locale.getDefault()).contains(constraint) ||
    	                		facility.getName().toLowerCase(Locale.getDefault()).contains(constraint)	) {
    	                	filt.add(facility); 
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
                	filtered = (ArrayList<Facility>)results.values;
                	clear();
                	addAll(filtered);
                }
	        }
	    }
}
