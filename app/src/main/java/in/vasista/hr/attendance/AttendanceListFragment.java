package in.vasista.hr.attendance;

import android.annotation.SuppressLint;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.List;

import in.vasista.milkosoft.mdkmf.R;
import in.vasista.vsales.adapter.AttendanceAdapter;
import in.vasista.vsales.db.AttendanceDataSource;
import in.vasista.vsales.sync.ServerSync;

public class AttendanceListFragment extends ListFragment {
	public static final String module = AttendanceListFragment.class.getName();	
	List<Attendance> punchItems; 
	AttendanceAdapter adapter;
	AttendanceDataSource datasource;
	final AttendanceListFragment attendanceListFragment = this;

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);		
		if (adapter == null) {			
    	    datasource = new AttendanceDataSource(getActivity());   
    	    datasource.open();
    	    punchItems = datasource.getAllPunches();
		}
		final ListView listView = getListView(); 
		if (adapter == null) {			   	    
		    adapter = new AttendanceAdapter(getActivity(),
                    R.layout.attendancelist_item, 
                    punchItems);  
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
	    punchItems = datasource.getAllPunches();
	    
	    adapter = new AttendanceAdapter(getActivity(),
                R.layout.attendancelist_item,
                punchItems);	
		setListAdapter(adapter);
	}
	
	public void onDestroyView() {
		super.onDestroyView();
		setListAdapter(null);
	}
	
    @SuppressLint("NewApi") @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        setUserVisibleHint(true);
    }
     
    @Override
    public void onResume() {
    	super.onResume();
	    datasource.open();
	    punchItems = datasource.getAllPunches();
	    adapter.clear();
	    adapter.addAll(punchItems);
    }
	public void syncAttendance(MenuItem menuItem){

		ProgressBar progressBar=(ProgressBar)menuItem.getActionView().findViewById(R.id.menuitem_progress);
		menuItem.expandActionView();
		ServerSync serverSync = new ServerSync(getActivity());
		serverSync.fetchEmployeeAttendance(menuItem,progressBar, attendanceListFragment);
	}
}
