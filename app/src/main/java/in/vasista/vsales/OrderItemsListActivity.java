package in.vasista.vsales;

import android.os.Bundle;

public class OrderItemsListActivity extends DashboardAppCompatActivity  {
	
	public void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);

		// Inflate your view 
		setContentChildView(R.layout.orderitems_layout);
		setSalesDashboardTitle(R.string.title_feature2_list);

		
/*        if (savedInstanceState == null) {
    		OrderItemsListFragment orderItemsFragment = 
    				(OrderItemsListFragment)getSupportFragmentManager().findFragmentById(R.id.orderitems_list_fragment);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.layout.orderitems_layout, orderItemsFragment).commit();
        }*/		
	}
	
	
}
