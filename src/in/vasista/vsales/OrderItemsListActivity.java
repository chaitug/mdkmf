package in.vasista.vsales;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class OrderItemsListActivity extends DashboardActivity  {
	
	public void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);

		// Inflate your view 
		setContentView(R.layout.payslipitems_layout); 

		
/*        if (savedInstanceState == null) {
    		PayslipItemsListFragment orderItemsFragment = 
    				(PayslipItemsListFragment)getSupportFragmentManager().findFragmentById(R.id.orderitems_list_fragment);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.layout.orderitems_layout, orderItemsFragment).commit();
        }*/		
	}
	
	
}
