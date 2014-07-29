package in.vasista.vsales.indent;

import in.vasista.vsales.IndentItemsListActivity;
import in.vasista.vsales.SalesDashboardActivity;
import in.vasista.vsales.R;
import in.vasista.vsales.adapter.IndentAdapter;
import in.vasista.vsales.catalog.CatalogListFragment;
import in.vasista.vsales.db.IndentsDataSource;
import in.vasista.vsales.sync.ServerSync;
import in.vasista.vsales.util.DateUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


public class IndentListFragment extends ListFragment{
	public static final String module = IndentListFragment.class.getName();	
	List<Indent> indentItems; 
	IndentAdapter adapter;
	IndentsDataSource datasource;
	
	public void onActivityCreated(Bundle savedInstanceState) {
		
		super.onActivityCreated(savedInstanceState);
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
    	final String retailerId = prefs.getString("storeId", "");
		TextView retailerIdView = (TextView)getActivity().findViewById(R.id.retailerId);
		retailerIdView.setText(retailerId);  
 
		if (adapter == null) {			
    	    datasource = new IndentsDataSource(getActivity());
    	    datasource.open();
    	    indentItems = datasource.getAllIndents();
		}
		final ListView listView = getListView();
		final IndentListFragment indentListFragment = this; 

		if (listView.getHeaderViewsCount() == 0) {
			ImageButton button = (ImageButton)indentListFragment.getActivity().findViewById(R.id.syncIndentButton);
			button.setOnClickListener(new OnClickListener() { 
				public void onClick(View view) {  
						//Toast.makeText( getActivity(), "Updating product catalog...", Toast.LENGTH_SHORT ).show();	    		    
						ProgressBar progressBar = (ProgressBar) indentListFragment.getActivity().findViewById(R.id.indentsRefreshProgress);
						progressBar.setVisibility(View.VISIBLE);
						ServerSync serverSync = new ServerSync(getActivity());
						serverSync.fetchActiveIndents(progressBar, indentListFragment);											
				}
			}); 
			View headerView2 = getActivity().getLayoutInflater().inflate(R.layout.indent_header, null);
			listView.addHeaderView(headerView2);
		}
		if (adapter == null) {			   	    
		    adapter = new IndentAdapter(getActivity(),
                    R.layout.indentlist_item, 
                    indentItems);  
		}
		setListAdapter(adapter);				
		listView.setClickable(true);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		  @Override
		  public void onItemClick(AdapterView<?> listView, View arg1, int position, long arg3) {

		    Indent indent = (Indent)listView.getItemAtPosition(position);
			//Toast.makeText( getActivity(), "Clicked item [" +order.getId() + "]", Toast.LENGTH_SHORT ).show();	
            if (indent != null) {
            	Intent indentItemsIntent = new Intent(getActivity(), IndentItemsListActivity.class);
            	indentItemsIntent.putExtra("indentId", indent.getId());
            	indentItemsIntent.putExtra("retailerId", retailerId);
            	startActivity(indentItemsIntent);
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
	    indentItems = datasource.getAllIndents();
	    
	    adapter = new IndentAdapter(getActivity(),
                R.layout.indentlist_item,
                indentItems);	
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
	    indentItems = datasource.getAllIndents();
		//Toast.makeText( getActivity(), "onResume [" +indentItems.size() + "]", Toast.LENGTH_SHORT ).show();	    		    
	    adapter.clear();
	    adapter.addAll(indentItems);
    }	
}