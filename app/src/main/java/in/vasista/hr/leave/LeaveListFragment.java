package in.vasista.hr.leave;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ProgressBar;

import java.util.List;

import in.vasista.vsales.LeaveActivity;
import in.vasista.vsales.R;
import in.vasista.vsales.adapter.LeaveAdapter;
import in.vasista.vsales.db.LeavesDataSource;
import in.vasista.vsales.sync.ServerSync;

public class LeaveListFragment extends ListFragment {
	public static final String module = LeaveListFragment.class.getName();	
	List<Leave> leaveItems; 
	LeaveAdapter adapter;
	LeavesDataSource datasource;
	final LeaveListFragment leaveListFragment = this;
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);		
		if (adapter == null) {			
    	    datasource = new LeavesDataSource(getActivity());   
    	    datasource.open();
    	    leaveItems = datasource.getAllLeaves();
		}
		//final ListView listView = getListView();


		if (adapter == null) {			   	    
		    adapter = new LeaveAdapter(getActivity(),
                    R.layout.leavelist_item, 
                    leaveItems);  
		}
		setListAdapter(adapter);	
	}

	/**
	 * Brute force update of list
	 */
	public void notifyChange() {
		if (adapter == null) {
			return; // nothing to do since most probably fragment is not yet created
		}
		setListAdapter(null);
	    datasource.open();
	    leaveItems = datasource.getAllLeaves();
	    
	    adapter = new LeaveAdapter(getActivity(),
                R.layout.leavelist_item,
                leaveItems);	
		setListAdapter(adapter);
	}
	
	public void onDestroyView() {
		super.onDestroyView();
		setListAdapter(null);
	}
	
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        setUserVisibleHint(true);
    }
    
    @Override
    public void onResume() {
    	super.onResume();
	    datasource.open();
	    leaveItems = datasource.getAllLeaves();
	    adapter.clear();
	    adapter.addAll(leaveItems);
    }
	public void syncLeaves(MenuItem menuItem){
		ProgressBar progressBar=(ProgressBar)menuItem.getActionView().findViewById(R.id.menuitem_progress);
		menuItem.expandActionView();
		ServerSync serverSync = new ServerSync(getActivity());
		serverSync.fetchEmployeeRecentLeaves(menuItem, progressBar, (LeaveActivity) leaveListFragment.getActivity());
	}
}
