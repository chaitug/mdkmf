package in.vasista.vsales.catalog;

import java.util.ArrayList;
import java.util.List;

import in.vasista.vsales.SalesDashboardActivity;
import in.vasista.vsales.R;
import in.vasista.vsales.adapter.IndentAdapter;
import in.vasista.vsales.adapter.ProductAdapter;
import in.vasista.vsales.db.ProductsDataSource;
import in.vasista.vsales.indent.Indent;
import in.vasista.vsales.indent.IndentListFragment;
import in.vasista.vsales.sync.ServerSync;
import android.app.ListFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


public class CatalogListFragment extends ListFragment {
	public static final String module = CatalogListFragment.class.getName();	
	
	ProductAdapter adapter;
	ProductsDataSource datasource;
	List<Product> catalogItems;   
 
	
	
	public void onActivityCreated(Bundle savedInstanceState) {
		
		super.onActivityCreated(savedInstanceState);  
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
    	String retailerId = prefs.getString("storeId", "");
		TextView retailerIdView = (TextView)getActivity().findViewById(R.id.retailerId);
		retailerIdView.setText(retailerId + " : Catalog");  		
		final ListView listView = getListView();
		final CatalogListFragment catalogListFragment = this; 
		if (listView.getHeaderViewsCount() == 0) {           
			ImageButton button = (ImageButton)catalogListFragment.getActivity().findViewById(R.id.refreshCatalogButton);
			button.setOnClickListener(new OnClickListener() { 
				public void onClick(View view) {   
					//Toast.makeText( getActivity(), "Updating product catalog...", Toast.LENGTH_SHORT ).show();	    		    
					ProgressBar progressBar = (ProgressBar) catalogListFragment.getActivity().findViewById(R.id.productsRefreshProgress);
					progressBar.setVisibility(View.VISIBLE);                       
					ServerSync serverSync = new ServerSync(getActivity());
					serverSync.updateProducts(progressBar, catalogListFragment);					
				}
			});			
			View headerView2 = getActivity().getLayoutInflater().inflate(R.layout.catalog_header, null);
			listView.addHeaderView(headerView2);
		}
		if (adapter == null) { 
    	    datasource = new ProductsDataSource(getActivity());
    	    datasource.open();
    	    catalogItems = datasource.getAllSaleProducts();
    	    
		    adapter = new ProductAdapter(getActivity(), 
                    R.layout.cataloglist_item,
                    catalogItems);
		}
		setListAdapter(adapter);
	}

	/**
	 * Brute force update of list
	 */
	public void notifyChange() {
		setListAdapter(null);
	    datasource.open();
	    catalogItems = datasource.getAllSaleProducts();
    	Log.d(module, "catalogItems.size() = " + catalogItems.size());
	    
	    adapter = new ProductAdapter(getActivity(),
                R.layout.cataloglist_item,
                catalogItems);	
		setListAdapter(adapter);
	}
	
	public void onDestroyView() {
		super.onDestroyView();
		setListAdapter(null);
	}

}
