package in.vasista.vsales;

import android.os.Bundle;
import in.vasista.milkosoft.mdkmf.R;

import in.vasista.hr.payslip.Payslip;
import in.vasista.vsales.db.PayslipDataSource;

public class PayslipItemsListActivity extends DashboardAppCompatActivity  {
	Payslip payslip;
	PayslipDataSource datasource;
	public void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);

		// Inflate your view 
		setContentChildView(R.layout.payslipitems_layout);
		String payrollHeaderId = getIntent().getStringExtra("payrollHeaderId");
		datasource = new PayslipDataSource(this);
		datasource.open();
		payslip = datasource.getPayslipDetails(payrollHeaderId);
		setPageTitle(payslip.getPayrollPeriod() + " Payslip");
		
/*        if (savedInstanceState == null) {
    		PayslipItemsListFragment orderItemsFragment = 
    				(PayslipItemsListFragment)getSupportFragmentManager().findFragmentById(R.id.orderitems_list_fragment);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.layout.orderitems_layout, orderItemsFragment).commit();
        }*/		
	}
	
	
}
