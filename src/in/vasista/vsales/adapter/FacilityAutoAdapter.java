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


public class FacilityAutoAdapter extends ArrayAdapter<Facility> {
	  int resource;
	  private List<Facility> facilitylist = null;
	  private ArrayList<Facility> arraylist;
	    
	  public FacilityAutoAdapter(Context context,
	                         int resource,
	                         List<Facility> items) {
	    super(context, resource, items);
	    this.resource = resource;
        this.facilitylist = items;
        this.arraylist = new ArrayList<Facility>();
        this.arraylist.addAll(facilitylist);
	  }

	    @Override
	    public int getCount() {
	        return facilitylist.size();
	    }
	 
	    @Override
	    public Facility getItem(int position) {
	        return facilitylist.get(position);
	    }
	 
	    @Override
	    public long getItemId(int position) {
	        return position;
	    }
	    
	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    View facilityView;

	    Facility item = getItem(position);
	    String id = item.getId();
	    String name = item.getName();  
	    

	    if (convertView == null) { 
	    	facilityView = new LinearLayout(getContext());
	      String inflater = Context.LAYOUT_INFLATER_SERVICE;
	      LayoutInflater li;
	      li = (LayoutInflater)getContext().getSystemService(inflater);
	      facilityView = li.inflate(resource, null);
	    } else {
	    	facilityView = (View) convertView;
	    }

	    TextView tview = (TextView)facilityView.findViewById(R.id.autoCompleteRetailer);
	    tview.setText(id + " [" + name + "]");
	    return facilityView;
	  }
	  
	    
	    @Override
	    public Filter getFilter() {
	        return new Filter() {

	            @Override
	            protected FilterResults performFiltering(CharSequence constraint) {
	                final FilterResults oReturn = new FilterResults();
	    	        facilitylist.clear();
	                if (constraint != null) {
	        	        if (constraint.length() == 0) {
	        	        	facilitylist.addAll(arraylist);
	        	        } 
	        	        else {
	        	            for (Facility facility : arraylist) {
	        	                if (facility.getId().toLowerCase(Locale.getDefault()).contains(constraint) ||
	        	                		facility.getName().toLowerCase(Locale.getDefault()).contains(constraint)	) {
	        	                	facilitylist.add(facility); 
	        	                }
	        	            } 
	        	        }
	                    oReturn.values = facilitylist;
	                }
	                return oReturn;
	            }

	            @SuppressWarnings("unchecked")
	            @Override
	            protected void publishResults(CharSequence constraint,
	                    FilterResults results) {
	                facilitylist = (ArrayList<Facility>) results.values;
	                notifyDataSetChanged();
	            }
	        };
	    }	    
}
