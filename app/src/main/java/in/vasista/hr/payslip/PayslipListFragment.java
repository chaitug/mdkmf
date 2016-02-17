package in.vasista.hr.payslip;

import android.app.ListFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.List;

import in.vasista.vsales.PayslipItemsListActivity;
import in.vasista.vsales.R;
import in.vasista.vsales.adapter.PayslipAdapter;
import in.vasista.vsales.db.PayslipDataSource;

public class PayslipListFragment extends ListFragment {
	public static final String module = PayslipListFragment.class.getName();	
	List<Payslip> payslipItems; 
	PayslipAdapter adapter;
	PayslipDataSource datasource;
	ProgressBar progressBar;
	public void onActivityCreated(Bundle savedInstanceState) {
		
		super.onActivityCreated(savedInstanceState);
		//SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
    	//prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

		if (adapter == null) {			
    	    datasource = new PayslipDataSource(getActivity());   
    	    datasource.open();
			progressBar= (ProgressBar) getActivity().findViewById(R.id.progress_sync);
    	    payslipItems = datasource.getAllPayslips(progressBar);
		}
		final ListView listView = getListView();

		if (adapter == null) {			   	    
		    adapter = new PayslipAdapter(getActivity(),
                    R.layout.paysliplist_item, 
                    payslipItems);  
		}
		setListAdapter(adapter);	
		listView.setClickable(true);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		  @Override
		  public void onItemClick(AdapterView<?> listView, View arg1, int position, long arg3) {

		    Payslip payslip = (Payslip)listView.getItemAtPosition(position);
            if (payslip != null) {  
            	Intent payslipItemsIntent = new Intent(getActivity(), PayslipItemsListActivity.class);
            	payslipItemsIntent.putExtra("payrollHeaderId", payslip.getPayrollHeaderId());
            	startActivity(payslipItemsIntent);
            }
		  }   
		});			
	}

	/**
	 * Brute force update of list
	 */
	public void notifyChange() {
		setListAdapter(null);
	    datasource.open();
	    payslipItems = datasource.getAllPayslips(progressBar);
	    
	    adapter = new PayslipAdapter(getActivity(),
                R.layout.paymentlist_item,
                payslipItems);	
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
	    payslipItems = datasource.getAllPayslips(progressBar);
	    adapter.clear();
	    adapter.addAll(payslipItems);
    }	
}
