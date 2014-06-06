package in.vasista.vsales.payment;

import in.vasista.vsales.R;
import in.vasista.vsales.adapter.PaymentAdapter;
import in.vasista.vsales.db.PaymentsDataSource;
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

public class PaymentListFragment extends ListFragment {
	public static final String module = PaymentListFragment.class.getName();	
	List<Payment> paymentItems; 
	PaymentAdapter adapter;
	PaymentsDataSource datasource;
	
	public void onActivityCreated(Bundle savedInstanceState) {
		
		super.onActivityCreated(savedInstanceState);
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
    	String retailerId = prefs.getString("storeId", "");
		TextView retailerIdView = (TextView)getActivity().findViewById(R.id.retailerId);
		retailerIdView.setText(retailerId);  
		
		if (adapter == null) {			
    	    datasource = new PaymentsDataSource(getActivity());   
    	    datasource.open();
    	    paymentItems = datasource.getAllPayments();
		}
		final ListView listView = getListView();
		final PaymentListFragment paymentListFragment = this; 

		if (listView.getHeaderViewsCount() == 0) {	
			ImageButton button = (ImageButton)paymentListFragment.getActivity().findViewById(R.id.syncPaymentButton);
			button.setOnClickListener(new OnClickListener() { 
				public void onClick(View view) {
						//Toast.makeText( getActivity(), "Updating product catalog...", Toast.LENGTH_SHORT ).show();	    		    
						ProgressBar progressBar = (ProgressBar) paymentListFragment.getActivity().findViewById(R.id.paymentsRefreshProgress);
						progressBar.setVisibility(View.VISIBLE);
						ServerSync serverSync = new ServerSync(getActivity());
						serverSync.fetchPayments(progressBar, paymentListFragment);		
						//Toast.makeText( getActivity(), "Payments coming soon!", Toast.LENGTH_SHORT ).show();	    		    

				} 
			}); 
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
