package in.vasista.vsales.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import in.vasista.vsales.R;
import in.vasista.vsales.employee.Employee;
import in.vasista.vsales.facility.Facility;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.TextView;  


public class EmployeeAdapter extends ArrayAdapter<Employee> {
	  int resource;
	  public ArrayList<Employee> items = new ArrayList<Employee>();;
	    public ArrayList<Employee> filtered = new ArrayList<Employee>();;
	    private Context context;
	    private Filter filter;

	    public EmployeeAdapter(Context context, int resource, List<Employee> items)
	    {
	        super(context, resource, items);
		    this.resource = resource;	        
	        this.filtered.addAll(items);
	        this.items.addAll(items);
	        this.context = context;
	        this.filter = new EmployeeAutoFilter();
	    }
	    
	    @Override
	    public int getCount() {
	        return filtered.size();
	    }
	 
	    @Override
	    public Employee getItem(int position) {
	        return filtered.get(position);
	    }	    

	    @Override
	    public View getView(int pos, View convertView, ViewGroup parent)
	    {
		    LinearLayout employeeView;

		    Employee item = getItem(pos);
		    String id = item.getId();
		    String name = item.getName();
		    String department = item.getDept();	    
		    String position = item.getPosition();	     
		    

		    if (convertView == null) { 
		    	employeeView = new LinearLayout(getContext());
		      String inflater = Context.LAYOUT_INFLATER_SERVICE;
		      LayoutInflater li;
		      li = (LayoutInflater)getContext().getSystemService(inflater);
		      li.inflate(resource, employeeView, true);
		    } else {
		    	employeeView = (LinearLayout) convertView;  
		    }

		    //TextView idView = (TextView)employeeView.findViewById(R.id.employeeRowId);
		    TextView nameView = (TextView)employeeView.findViewById(R.id.employeeRowName);	    
		    TextView departmentView = (TextView)employeeView.findViewById(R.id.employeeRowDept);
		    TextView positionView = (TextView)employeeView.findViewById(R.id.employeeRowPosition);		    

		    //idView.setText(id);
		    nameView.setText(name);
		    departmentView.setText(department);
		    positionView.setText(position);
		    return employeeView;
	    }


	    @Override
	    public Filter getFilter()
	    {
	        if(filter == null)
	            filter = new EmployeeAutoFilter();
	        return filter;
	    }

	    private class EmployeeAutoFilter extends Filter
	    {

	        @Override
	        protected FilterResults performFiltering(CharSequence constraint) {
	            // NOTE: this function is *always* called from a background thread, and
	            // not the UI thread.
	            FilterResults result = new FilterResults();
	            if(constraint != null && constraint.toString().length() > 0)
	            {
	                ArrayList<Employee> filt = new ArrayList<Employee>();
	                ArrayList<Employee> lItems = new ArrayList<Employee>();
	                //synchronized (this)
	                {
	                    lItems.addAll(items);
	                }
	                for(int i = 0, l = lItems.size(); i < l; i++)
	                {
	                	Employee employee = lItems.get(i); 
    	                if (employee.getName().toLowerCase(Locale.getDefault()).contains(constraint.toString().toLowerCase(Locale.getDefault())) ||
    	                		employee.getDept().toLowerCase(Locale.getDefault()).contains(constraint.toString().toLowerCase(Locale.getDefault())) ||
    	                		employee.getPosition().toLowerCase(Locale.getDefault()).contains(constraint.toString().toLowerCase(Locale.getDefault()))) {
    	                	filt.add(employee); 
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
                	filtered = (ArrayList<Employee>)results.values;
                	clear();
                	addAll(filtered);
                }
	        }
	    }
}
