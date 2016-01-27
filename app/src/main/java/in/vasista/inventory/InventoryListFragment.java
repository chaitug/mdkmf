package in.vasista.inventory;

import android.app.ListFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.gson.Gson;

import java.util.List;

import in.vasista.rest.ServerRestSync;
import in.vasista.vsales.R;
import in.vasista.vsales.catalog.Product;
import in.vasista.vsales.db.ProductsDataSource;


public class InventoryListFragment extends ListFragment {
	public static final String module = InventoryListFragment.class.getName();	
	
	InventoryAdapter adapter;
	ProductsDataSource datasource;
	List<Product> inventoryItems;
	ListView listView;
 
	
	
	public void onActivityCreated(Bundle savedInstanceState) {
		
		super.onActivityCreated(savedInstanceState);  
		
		listView = getListView();
		final InventoryListFragment inventoryListFragment = this; 
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
			//Toast.makeText( getActivity(), "Clicked item [" +product.getId() + "]", Toast.LENGTH_SHORT ).show();	
            if (product != null) {    
            	Intent inventoryItemsIntent = new Intent(getActivity(), InventoryDetailsActivity.class);
            	inventoryItemsIntent.putExtra("product", (new Gson()).toJson(product));
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

	public void syncInventory(final MenuItem menuItem) {
		//final ProgressBar progressBar;
		if (listView.getHeaderViewsCount() == 0) {
			//Toast.makeText( getActivity(), "Updating product catalog...", Toast.LENGTH_SHORT ).show();
			//final ProgressBar progressBar = (ProgressBar) inventoryListFragment.getActivity().findViewById(R.id.productsRefreshProgress);
			//progressBar.setVisibility(View.VISIBLE);
			//progressBar = (ProgressBar) menuItem.getActionView().findViewById(R.id.menuitem_progress);

			final class LoadMaterialsTask extends AsyncTask<String, Integer, String> {

				@Override
				protected void onPreExecute() {
					//progressBar.setVisibility(View.VISIBLE);
					menuItem.setActionView(R.layout.progressbar);
					menuItem.expandActionView();
				}

				@Override
				protected String doInBackground(String... urls) {
					ServerRestSync serverSync = new ServerRestSync(getActivity());
					serverSync.fetchMaterials();
					return "";
				}

				@Override
				protected void onProgressUpdate(Integer... values) {                                 // 4
				}

				@Override
				protected void onPostExecute(String result) {
					Log.v(module, "Post");
					notifyChange();
					//progressBar.setVisibility(View.INVISIBLE);
					menuItem.setActionView(null);
				}
			}
			LoadMaterialsTask task = new LoadMaterialsTask();

			task.execute("");

			View headerView2 = getActivity().getLayoutInflater().inflate(R.layout.inventory_header, null);
			listView.addHeaderView(headerView2);
		}else{
			menuItem.setActionView(null);
		}
	}
}
