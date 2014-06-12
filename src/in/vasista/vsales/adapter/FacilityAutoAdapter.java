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
import android.widget.TextView;  


public class FacilityAutoAdapter extends ArrayAdapter<Facility> {
	  public ArrayList<Facility> items = new ArrayList<Facility>();;
	    public ArrayList<Facility> filtered = new ArrayList<Facility>();;
	    private Context context;
	    private Filter filter;

	    public FacilityAutoAdapter(Context context, int textViewResourceId, List<Facility> items)
	    {
	        super(context, textViewResourceId, items);
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
	        View facilityView = convertView;
	        if(facilityView == null)
	        {
	            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            facilityView = vi.inflate(R.layout.autocomplete_item, null);
	        }

	        Facility facility = getItem(position);
	        if(facility != null)
	        {
	    	    TextView tview = (TextView)facilityView.findViewById(R.id.facilityAutocompleteLabel);
	    	    tview.setText(facility.getId() + " [" + facility.getName() + "]");
	        }
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
