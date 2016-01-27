package in.vasista.hr.leave;

import in.vasista.vsales.LeaveActivity;
import in.vasista.vsales.R;
import in.vasista.vsales.adapter.LeaveAdapter;
import in.vasista.vsales.db.LeavesDataSource;
import in.vasista.vsales.order.Order;
import in.vasista.vsales.payment.Payment;
import in.vasista.vsales.payment.PaymentListFragment;
import in.vasista.vsales.sync.ServerSync;

import java.util.List;
 
import android.app.ListFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class LeaveListFragment extends ListFragment {
	public static final String module = LeaveListFragment.class.getName();	
	List<Leave> leaveItems; 
	LeaveAdapter adapter;
	LeavesDataSource datasource;
	
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);		
		if (adapter == null) {			
    	    datasource = new LeavesDataSource(getActivity());   
    	    datasource.open();
    	    leaveItems = datasource.getAllLeaves();
		}
		final ListView listView = getListView();
		final LeaveListFragment leaveListFragment = this; 		
		ImageButton button = (ImageButton)leaveListFragment.getActivity().findViewById(R.id.syncLeavesButton);
		button.setOnClickListener(new OnClickListener() { 
			public void onClick(View view) {
					ProgressBar progressBar = (ProgressBar) leaveListFragment.getActivity().findViewById(R.id.leavesRefreshProgress);
					progressBar.setVisibility(View.VISIBLE);
					ServerSync serverSync = new ServerSync(getActivity());
					serverSync.fetchEmployeeRecentLeaves(progressBar, (LeaveActivity)leaveListFragment.getActivity());		
			} 
		}); 
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
}
