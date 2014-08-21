package in.vasista.hr.attendance;

import in.vasista.vsales.AttendanceActivity;
import in.vasista.vsales.R;
import in.vasista.vsales.adapter.AttendanceAdapter;
import in.vasista.vsales.db.AttendanceDataSource;
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

public class AttendanceListFragment extends ListFragment {
	public static final String module = AttendanceListFragment.class.getName();	
	List<Attendance> punchItems; 
	AttendanceAdapter adapter;
	AttendanceDataSource datasource;
	
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);		
		if (adapter == null) {			
    	    datasource = new AttendanceDataSource(getActivity());   
    	    datasource.open();
    	    punchItems = datasource.getAllPunches();
		}
		final ListView listView = getListView();
		final AttendanceListFragment attendanceListFragment = this; 		
		ImageButton button = (ImageButton)attendanceListFragment.getActivity().findViewById(R.id.syncAttendanceButton);
		button.setOnClickListener(new OnClickListener() { 
			public void onClick(View view) {
					ProgressBar progressBar = (ProgressBar) attendanceListFragment.getActivity().findViewById(R.id.attendanceRefreshProgress);
					progressBar.setVisibility(View.VISIBLE);
					ServerSync serverSync = new ServerSync(getActivity());
					serverSync.fetchEmployeeAttendance(progressBar, attendanceListFragment);		
			} 
		}); 
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
	
    @Override
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
}
