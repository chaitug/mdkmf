package in.vasista.hr.payslip;

import in.vasista.vsales.OrderItemsListActivity;
import in.vasista.vsales.PayslipItemsListActivity;
import in.vasista.vsales.R;
import in.vasista.vsales.adapter.PaymentAdapter;
import in.vasista.vsales.adapter.PayslipAdapter;
import in.vasista.vsales.db.PaymentsDataSource;
import in.vasista.vsales.db.PayslipDataSource;
import in.vasista.vsales.order.Order;
import in.vasista.vsales.payment.Payment;
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

public class PayslipListFragment extends ListFragment {
	public static final String module = PayslipListFragment.class.getName();	
	List<Payslip> payslipItems; 
	PayslipAdapter adapter;
	PayslipDataSource datasource;
	
	public void onActivityCreated(Bundle savedInstanceState) {
		
		super.onActivityCreated(savedInstanceState);
		
		if (adapter == null) {			
    	    datasource = new PayslipDataSource(getActivity());   
    	    datasource.open();
    	    payslipItems = datasource.getAllPayslips();
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
	    payslipItems = datasource.getAllPayslips();
	    
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
	    payslipItems = datasource.getAllPayslips();
	    adapter.clear();
	    adapter.addAll(payslipItems);
    }	
}
