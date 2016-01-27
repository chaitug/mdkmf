package in.vasista.tm;

import java.util.ArrayList;
import java.util.List;

import in.vasista.vsales.R;
import in.vasista.vsales.db.TicketsDataSource;
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


public class TicketListFragment extends ListFragment {
	public static final String module = TicketListFragment.class.getName();	
	
	TicketAdapter adapter;
	TicketsDataSource datasource;
	List<Ticket> ticketItems;   
 
	
	
	public void onActivityCreated(Bundle savedInstanceState) {
		
		super.onActivityCreated(savedInstanceState);  
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());		
		final ListView listView = getListView();
		final TicketListFragment catalogListFragment = this; 
		if (listView.getHeaderViewsCount() == 0) {           
			ImageButton button = (ImageButton)catalogListFragment.getActivity().findViewById(R.id.refreshTicketsButton);
			button.setOnClickListener(new OnClickListener() { 
				public void onClick(View view) {   
/*					ProgressBar progressBar = (ProgressBar) catalogListFragment.getActivity().findViewById(R.id.productsRefreshProgress);
					progressBar.setVisibility(View.VISIBLE);                       
					ServerSync serverSync = new ServerSync(getActivity());
					serverSync.updateProducts(progressBar, catalogListFragment);	*/				
				}
			});			
			View headerView2 = getActivity().getLayoutInflater().inflate(R.layout.catalog_header, null);
			listView.addHeaderView(headerView2);
		}
		if (adapter == null) { 
    	    datasource = new TicketsDataSource(getActivity());
    	    datasource.open();
    	    ticketItems = datasource.getAllTickets();
    	    
		    adapter = null; //new TicketAdapter(getActivity(), 
                   // R.layout.ticketlist_item,
                   // ticketItems);
		}
		//setListAdapter(adapter);
	}

	/**
	 * Brute force update of list
	 */
	public void notifyChange() {
		setListAdapter(null);
	    datasource.open();
	    ticketItems = datasource.getAllTickets();
    	Log.d(module, "ticketItems.size() = " + ticketItems.size());
	    
	    adapter = null; //new TicketAdapter(getActivity(),
                //R.layout.ticketlist_item,
                //ticketItems);	
		//setListAdapter(adapter);
	}
	
	public void onDestroyView() {
		super.onDestroyView();
		setListAdapter(null);
	}

}
