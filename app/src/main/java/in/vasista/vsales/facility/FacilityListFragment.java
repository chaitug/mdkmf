package in.vasista.vsales.facility;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.List;

import in.vasista.vsales.FacilityDetailsActivity;
import in.vasista.vsales.R;
import in.vasista.vsales.adapter.FacilityAdapter;
import in.vasista.vsales.db.FacilityDataSource;
import in.vasista.vsales.sync.ServerSync;


public class FacilityListFragment extends ListFragment {
	public static final String module = FacilityListFragment.class.getName();	
	
	FacilityAdapter adapter;
	FacilityDataSource datasource;
	List<Facility> facilityItems;

	final FacilityListFragment facilityListFragment = this;

	public void onActivityCreated(Bundle savedInstanceState) {
		
		super.onActivityCreated(savedInstanceState);  
		
		final ListView listView = getListView();

		if (listView.getHeaderViewsCount() == 0) {           
						
			View headerView2 = getActivity().getLayoutInflater().inflate(R.layout.facility_header, null);
			listView.addHeaderView(headerView2);
		}
		if (adapter == null) {  
			/*
    		// Create the array list of to do items
    		ArrayList<Product> catalogItems = new ArrayList<Product>();
    	    catalogItems.add(new Product("B01", "BUTTER-C 500G",130));	     
    	    catalogItems.add(new Product("G05", "GHEE 1LTR (TIN)",290));	
    	    */
			
    	    datasource = new FacilityDataSource(getActivity());
    	    datasource.open();
    	    facilityItems = datasource.getAllFacilities();
    	    
		    adapter = new FacilityAdapter(getActivity(),
                    R.layout.facilitylist_item,
                    facilityItems);
		}
		setListAdapter(adapter);
		final EditText inputSearch = (EditText) getActivity().findViewById(R.id.inputSearch);
		final FrameLayout inputSearchFrame = (FrameLayout) getActivity().findViewById(R.id.inputSearchFrame);
		
		listView.setClickable(true);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		  @Override
		  public void onItemClick(AdapterView<?> listView, View arg1, int position, long arg3) {

		    Facility facility = (Facility)listView.getItemAtPosition(position);
            if (facility != null) { 
            	Intent facilityDetailsIntent = new Intent(getActivity(), FacilityDetailsActivity.class);
            	facilityDetailsIntent.putExtra("facilityId", facility.getId());
            	startActivity(facilityDetailsIntent);
            } 
		  }
		});	
		
		inputSearch.addTextChangedListener(new TextWatcher() {
            
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
            	//Toast.makeText( getActivity(), "inputSearch: cs=" + cs, Toast.LENGTH_SHORT ).show();
            	facilityListFragment.adapter.getFilter().filter(cs);   
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
		
		inputSearchFrame.setVisibility(View.GONE);
		  
	}

	/**
	 * Brute force update of list
	 */
	public void notifyChange() {
		setListAdapter(null);
	    datasource.open();
	    facilityItems = datasource.getAllFacilities();
    	Log.d(module, "catalogItems.size() = " + facilityItems.size());
	    
	    adapter = new FacilityAdapter(getActivity(),
                R.layout.facilitylist_item,
                facilityItems);	
		setListAdapter(adapter);
	}
	
	public void onDestroyView() {
		super.onDestroyView();
		setListAdapter(null);
	}
	public void syncOutlets(MenuItem menuItem){

		ProgressBar p=(ProgressBar)menuItem.getActionView().findViewById(R.id.menuitem_progress);
		menuItem.expandActionView();
		ServerSync serverSync = new ServerSync(getActivity());
		serverSync.updateFacilities(menuItem, p, facilityListFragment);
	}
}
