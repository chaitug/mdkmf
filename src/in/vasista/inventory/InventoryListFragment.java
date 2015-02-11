package in.vasista.inventory;

import java.util.ArrayList;
import java.util.List;

import in.vasista.rest.ServerRestSync;
import in.vasista.vsales.EmployeeDetailsActivity;
import in.vasista.vsales.FacilityDetailsActivity;
import in.vasista.vsales.IndentItemsListActivity;
import in.vasista.vsales.SalesDashboardActivity;
import in.vasista.vsales.OrderItemsListActivity;
import in.vasista.vsales.R;
import in.vasista.vsales.adapter.EmployeeAdapter;
import in.vasista.vsales.adapter.FacilityAdapter;
import in.vasista.vsales.catalog.Product;
import in.vasista.vsales.db.EmployeeDataSource;
import in.vasista.vsales.db.FacilityDataSource;
import in.vasista.vsales.db.ProductsDataSource;
import in.vasista.vsales.indent.Indent;
import in.vasista.vsales.order.Order;
import in.vasista.vsales.sync.ServerSync;
import android.app.ListFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


public class InventoryListFragment extends ListFragment {
	public static final String module = InventoryListFragment.class.getName();	
	
	InventoryAdapter adapter;
	ProductsDataSource datasource;
	List<Product> inventoryItems;      
 
	
	
	public void onActivityCreated(Bundle savedInstanceState) {
		
		super.onActivityCreated(savedInstanceState);  
		
		final ListView listView = getListView();
		final InventoryListFragment inventoryListFragment = this; 
		if (listView.getHeaderViewsCount() == 0) {           
			ImageButton button = (ImageButton)inventoryListFragment.getActivity().findViewById(R.id.refreshProductsButton);
			button.setOnClickListener(new OnClickListener() { 
				public void onClick(View view) {
					//Toast.makeText( getActivity(), "Updating product catalog...", Toast.LENGTH_SHORT ).show();	    		    
					ProgressBar progressBar = (ProgressBar) inventoryListFragment.getActivity().findViewById(R.id.productsRefreshProgress);
					progressBar.setVisibility(View.VISIBLE);                       
					ServerRestSync serverSync = new ServerRestSync(getActivity());
					serverSync.fetchMaterials(progressBar, inventoryListFragment);					
				}
			});			
			View headerView2 = getActivity().getLayoutInflater().inflate(R.layout.inventory_header, null);
			listView.addHeaderView(headerView2);
		}
		if (adapter == null) {  
    	    datasource = new ProductsDataSource(getActivity()); 
    	    datasource.open(); 
    	    inventoryItems = datasource.getAllInventoryProducts();   
    	    
		    adapter = new InventoryAdapter(getActivity(),
                    R.layout.inventorylist_item,
                    inventoryItems); 
		}
		setListAdapter(adapter);
		
		listView.setClickable(true);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		  @Override
		  public void onItemClick(AdapterView<?> listView, View arg1, int position, long arg3) {

		    Product product = (Product)listView.getItemAtPosition(position);
			Toast.makeText( getActivity(), "Clicked item [" +product.getId() + "]", Toast.LENGTH_SHORT ).show();	
            if (product != null) {
            	Intent inventoryItemsIntent = new Intent(getActivity(), InventoryDetailsActivity.class);
            	inventoryItemsIntent.putExtra("productId", product.getId());
            	startActivity(inventoryItemsIntent);
            }
		  }
		});
		
		final EditText inputSearch = (EditText) getActivity().findViewById(R.id.inputSearch);		
		inputSearch.addTextChangedListener(new TextWatcher() {
            
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
            	//Toast.makeText( getActivity(), "inputSearch: cs=" + cs, Toast.LENGTH_SHORT ).show();
            	inventoryListFragment.adapter.getFilter().filter(cs);   
            }
              
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                    int arg3) {
                // TODO Auto-generated method stub
                 
            }
             

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				
			}
        });
		
		ImageButton searchClearButton = (ImageButton)getActivity().findViewById(R.id.inputSearchClear);
		searchClearButton.setOnClickListener(new OnClickListener() { 
			public void onClick(View view) {  
				inputSearch.setText("");
			}
		});
		
	}

	/**
	 * Brute force update of list
	 */
	public void notifyChange() {
		setListAdapter(null);
	    datasource.open();
	    inventoryItems = datasource.getAllInventoryProducts();
    	Log.d(module, "inventoryItems.size() = " + inventoryItems.size());
	    
	    adapter = new InventoryAdapter(getActivity(),
                R.layout.inventorylist_item,
                inventoryItems);	
		setListAdapter(adapter); 
	}
	
	public void onDestroyView() {
		super.onDestroyView();
		setListAdapter(null);
	}

}
