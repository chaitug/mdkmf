package in.vasista.vsales.employee;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.List;

import in.vasista.vsales.EmployeeDetailsActivity;
import in.vasista.vsales.R;
import in.vasista.vsales.adapter.EmployeeAdapter;
import in.vasista.vsales.db.EmployeeDataSource;
import in.vasista.vsales.sync.ServerSync;


public class EmployeeListFragment extends ListFragment {
	public static final String module = EmployeeListFragment.class.getName();	
	
	EmployeeAdapter adapter;
	EmployeeDataSource datasource;
	List<Employee> employeeItems;


	final EmployeeListFragment employeeListFragment = this;
	public void onActivityCreated(Bundle savedInstanceState) {
		
		super.onActivityCreated(savedInstanceState);  
		
		final ListView listView = getListView();

		if (listView.getHeaderViewsCount() == 0) {           
				
			View headerView2 = getActivity().getLayoutInflater().inflate(R.layout.employee_header, null);
			listView.addHeaderView(headerView2);
		}
		if (adapter == null) {  
    	    datasource = new EmployeeDataSource(getActivity()); 
    	    datasource.open(); 
    	    employeeItems = datasource.getAllEmployees();   
    	    
		    adapter = new EmployeeAdapter(getActivity(),
                    R.layout.employeelist_item,
                    employeeItems);
		}
		setListAdapter(adapter);
		final EditText inputSearch = (EditText) getActivity().findViewById(R.id.inputSearch);
		listView.setClickable(true);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		  @Override
		  public void onItemClick(AdapterView<?> listView, View arg1, int position, long arg3) {

		    Employee employee = (Employee)listView.getItemAtPosition(position);
            if (employee != null) { 
            	Intent employeeDetailsIntent = new Intent(getActivity(), EmployeeDetailsActivity.class);
            	employeeDetailsIntent.putExtra("employeeId", employee.getId());
            	startActivity(employeeDetailsIntent);
            } 
		  }
		});	
		
		inputSearch.addTextChangedListener(new TextWatcher() {
            
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
            	//Toast.makeText( getActivity(), "inputSearch: cs=" + cs, Toast.LENGTH_SHORT ).show();
            	employeeListFragment.adapter.getFilter().filter(cs);   
            }
              
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                    int arg3) {
                // TODO Auto-generated method stub
                 
            }
             

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				
			}
        });
		
		ImageButton searchClearButton = (ImageButton)getActivity().findViewById(R.id.inputSearchClear);
		searchClearButton.setOnClickListener(new OnClickListener() { 
			public void onClick(View view) {  
				inputSearch.setText("");
			}
		});
		
	}

	/**
	 * Brute force update of list
	 */
	public void notifyChange() {
		setListAdapter(null);
	    datasource.open();
	    employeeItems = datasource.getAllEmployees();
    	Log.d(module, "employeeItems.size() = " + employeeItems.size());
	    
	    adapter = new EmployeeAdapter(getActivity(),
                R.layout.employeelist_item,
                employeeItems);	
		setListAdapter(adapter); 
	}
	
	public void onDestroyView() {
		super.onDestroyView();
		setListAdapter(null);
	}
	public void syncEmployees(MenuItem menuItem){
		ProgressBar progressBar=(ProgressBar)menuItem.getActionView().findViewById(R.id.menuitem_progress);
		menuItem.expandActionView();
		ServerSync serverSync = new ServerSync(getActivity());
		serverSync.updateEmployees(menuItem,progressBar, employeeListFragment);
	}

}
