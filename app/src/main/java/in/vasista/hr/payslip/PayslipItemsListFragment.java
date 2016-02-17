package in.vasista.hr.payslip;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import in.vasista.vsales.R;
import in.vasista.vsales.adapter.PayslipItemAdapter;
import in.vasista.vsales.db.PayslipDataSource;


public class PayslipItemsListFragment extends ListFragment{
	public static final String module = PayslipItemsListFragment.class.getName();	
	
	PayslipItemAdapter adapter;
    List<PayslipItem> payslipItems;	
    Payslip payslip;    
	PayslipDataSource datasource;   
	boolean isEditableList;
	ListView listView;      
	public void onActivityCreated(Bundle savedInstanceState) {
		
		super.onActivityCreated(savedInstanceState);  
		Intent payslipItemsIntent= getActivity().getIntent();
		String payrollHeaderId = payslipItemsIntent.getStringExtra("payrollHeaderId");
		if (adapter == null) {		
			datasource = new PayslipDataSource(getActivity()); 
			datasource.open();
			payslip = datasource.getPayslipDetails(payrollHeaderId);
			payslipItems = datasource.getPayslipItems(payrollHeaderId);  
		}  
	
		listView = getListView();
		//final PayslipItemsListFragment payslipItemsListFragment = this;

		if (listView.getHeaderViewsCount() == 0) {	
			
			final View headerView2 = getActivity().getLayoutInflater().inflate(R.layout.payslipitems_header, null);
			listView.addHeaderView(headerView2);
		}

		if (adapter == null) {		
		    adapter = new PayslipItemAdapter(getActivity(),
                    R.layout.payslipitems_item,
                    payslipItems);
		    updatePayslipHeaderView();
		}
		setListAdapter(adapter);
	}
	
	public void onDestroyView() { 
		super.onDestroyView();
		setListAdapter(null);     
	}
	
	public void updatePayslipHeaderView() {
		updatePayslipHeaderViewInternal(payslip);	
	}
	
	void updatePayslipHeaderViewInternal(Payslip payslip) {
		float netAmount = payslip.getNetAmount();
		String netAmountStr = "Net: Rs " + netAmount;
		//String payrollPeriod = payslip.getPayrollPeriod();

		TextView netAmountView = (TextView)listView.getRootView().findViewById(R.id.payslipNetAmount);
		if (netAmountView != null) {
			netAmountView.setText(netAmountStr);   
		} 
		float benefitsAmount = 0;
		float deductionsAmount = 0;  
		for (int i = 0; i < payslipItems.size(); ++ i) {
			PayslipItem item = payslipItems.get(i);
			if (item.getPayheadAmount() < 0) {
				deductionsAmount += item.getPayheadAmount(); 
			}
			else { 
				benefitsAmount += item.getPayheadAmount();				
			}
		}
		String earningStr = "Earnings: Rs " + benefitsAmount;
		TextView earningsView = (TextView)listView.getRootView().findViewById(R.id.payslipEarnings);
		if (earningsView != null) {
			earningsView.setText(earningStr);   
		} 
		String deductionsStr = "Deductions: Rs " + -deductionsAmount;
		TextView deductionsView = (TextView)listView.getRootView().findViewById(R.id.payslipDeductions);
		if (deductionsView != null) {
			deductionsView.setText(deductionsStr);   
		}		
		Log.d(module, "Earnings=" + benefitsAmount + "; Deductions=" + deductionsAmount);	 

/*		TextView payrollPeriodView = (TextView)listView.getRootView().findViewById(R.id.payslipPeriod);	
		if (payrollPeriodView != null) {
			payrollPeriodView.setText(payrollPeriod);
		}*/			
	}		
	

	
	/**
	 * Brute force update of list
	 */
	public void notifyChange() {
		if (payslip == null) {
			// nothing to do 
			return;
		}
		setListAdapter(null);
	    datasource.open();
		payslipItems = datasource.getPayslipItems(payslip.getPayrollHeaderId());  	    
	    adapter = new PayslipItemAdapter(getActivity(),
                R.layout.payslipitems_item,
                payslipItems);	
		setListAdapter(adapter);
	}

}
