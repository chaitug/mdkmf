package in.vasista.vsales.payment;

import android.app.ListFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.List;

import in.vasista.vsales.R;
import in.vasista.vsales.adapter.PaymentAdapter;
import in.vasista.vsales.db.PaymentsDataSource;
import in.vasista.vsales.sync.ServerSync;

public class PaymentListFragment extends ListFragment {
	public static final String module = PaymentListFragment.class.getName();	
	List<Payment> paymentItems; 
	PaymentAdapter adapter;
	PaymentsDataSource datasource;
	final PaymentListFragment paymentListFragment = this;
	
	public void onActivityCreated(Bundle savedInstanceState) { 
		
		super.onActivityCreated(savedInstanceState);
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
    	String retailerId = prefs.getString("storeId", "");
		
		if (adapter == null) {			
    	    datasource = new PaymentsDataSource(getActivity());   
    	    datasource.open();
    	    paymentItems = datasource.getAllPayments();
		}
		final ListView listView = getListView();


		if (listView.getHeaderViewsCount() == 0) {	
	
			View headerView2 = getActivity().getLayoutInflater().inflate(R.layout.payment_header, null);
			listView.addHeaderView(headerView2);
		}
		if (adapter == null) {			   	    
		    adapter = new PaymentAdapter(getActivity(),
                    R.layout.paymentlist_item, 
                    paymentItems);  
		}
		setListAdapter(adapter);					
	}
	public void syncPayments(MenuItem menuItem){

		ProgressBar p=(ProgressBar)menuItem.getActionView().findViewById(R.id.menuitem_progress);
		menuItem.expandActionView();
		ServerSync serverSync = new ServerSync(getActivity());
		serverSync.fetchPayments(menuItem, p, paymentListFragment);
	}
	/**
	 * Brute force update of list
	 */
	public void notifyChange() {
		setListAdapter(null);
	    datasource.open();
	    paymentItems = datasource.getAllPayments();
	    
	    adapter = new PaymentAdapter(getActivity(),
                R.layout.paymentlist_item,
                paymentItems);	
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
	    paymentItems = datasource.getAllPayments();
	    adapter.clear();
	    adapter.addAll(paymentItems);
    }	
}
