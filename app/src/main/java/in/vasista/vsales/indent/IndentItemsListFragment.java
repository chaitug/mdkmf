package in.vasista.vsales.indent;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import in.vasista.milkosoft.mdkmf.R;
import in.vasista.vsales.adapter.IndentItemAdapter;
import in.vasista.vsales.catalog.Product;
import in.vasista.vsales.db.IndentsDataSource;
import in.vasista.vsales.db.ProductsDataSource;
import in.vasista.vsales.sync.ServerSync;
import in.vasista.vsales.util.DateUtil;


public class IndentItemsListFragment extends ListFragment{
	public static final String module = IndentItemsListFragment.class.getName();	
	
	IndentItemAdapter adapter;
    List<IndentItem> indentItems;	
    Indent indent;   
	IndentsDataSource datasource;
	boolean isEditableList;
	ListView listView;
	final IndentItemsListFragment indentItemsListFragment = this;
	public void onActivityCreated(Bundle savedInstanceState) {
		
		super.onActivityCreated(savedInstanceState);  
		Intent indentItemsIntent= getActivity().getIntent();
		int indentId = -1;
		indentId = indentItemsIntent.getIntExtra("indentId", indentId);	
		final String retailerId = indentItemsIntent.getStringExtra("retailerId");	 			
		if (adapter == null) {		  
			datasource = new IndentsDataSource(getActivity());
			if (indentId == -1) {  
				indentItems = new ArrayList<IndentItem>();	
				initializeIndentItems();
			}
			else {  
				datasource.open();
				indent = datasource.getIndentDetails(indentId);
				indentItems = datasource.getIndentItems(indentId);
			}
		}

		if (indentId != -1) {
			datasource.open();
			indent = datasource.getIndentDetails(indentId);
		} 		
		listView = getListView();
		

		if (listView.getHeaderViewsCount() == 0) {

			
			if (indent != null) {           
					
				
			}
			else { 
				isEditableList = true;		
									
			} 
			
			final View headerView2 = getActivity().getLayoutInflater().inflate(R.layout.indentitems_header, null);
			listView.addHeaderView(headerView2);
		}

		if (adapter == null) {		
		    adapter = new IndentItemAdapter(getActivity(),
                    R.layout.indentitems_item,
                    indentItems);
		    updateIndentHeaderView();
		}
		adapter.setEditable(isEditableList);		
		setListAdapter(adapter);

		// Handle Complete Order Button
		

					 
		if (indent != null) { 
			SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
			boolean isActiveIndent =  fmt.format(new Date()).compareTo(fmt.format(indent.getSupplyDate())) <= 0;
	    	//Log.d(module, "now = " + fmt.format(new Date()));
	    	//Log.d(module, "supplyDate = " + fmt.format(indent.getSupplyDate()));
//			if (!isActiveIndent) {
//				editButton.setVisibility(View.GONE);
//				uploadButton.setVisibility(View.GONE);
//			}
		}

		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long arg) {
				if (!isEditableList) {
					return;
				}
				AlertDialog.Builder alert = new AlertDialog.Builder(
						getActivity());
				final IndentItem item = (IndentItem) listView
						.getItemAtPosition(position);

				alert.setTitle("Enter quantity for "  + item.getProductName());
				alert.setMessage("Quantity (Pkts)"); 

				// Set an EditText view to get user input
				final EditText input = new EditText(getActivity());
				input.setInputType(InputType.TYPE_CLASS_NUMBER);
				String qtyStr = Integer.toString(item.getQty());
				if (item.getQty() == -1) { 
					qtyStr = "";
				} 
				input.setText(qtyStr);
				input.requestFocus();
				input.selectAll();
				alert.setView(input);

				alert.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								String value = input.getText().toString();
								int newQty = -1;
								try {
									newQty = Integer.parseInt(value);
								} catch (NumberFormatException e) {
									//
								}
								item.setQty(newQty);
								adapter.notifyDataSetChanged();
								updateIndentHeaderView();
							}
						});

				alert.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								// Canceled.
							}
						});

				alert.show();
			}

		});

	}
	
	public void onDestroyView() { 
		super.onDestroyView();
		setListAdapter(null);     
	}
	
	public double getTotal() {                       
		double retVal = 0;
		for (int i = 0; i < indentItems.size(); ++i) {
			IndentItem item = indentItems.get(i);
			if (item.getQty() == -1) {
				continue;               
			}
			retVal += item.getUnitPrice() * item.getQty();
		}
		retVal = Math.round(retVal * 100.0) / 100.0;
		return retVal;
	}
	
	public void updateIndentHeaderView() {
		updateIndentHeaderViewInternal(indent);	
	}
	
	void updateIndentHeaderViewInternal(Indent indent) {
		double total = getTotal();
		String totalStr = "Total: Rs " + total;
		Date date = DateUtil.addDays(new Date(), 1);
		String indentSupply = "";
		String synced = ""; 
		if (indent != null) {  
			indent.setTotal(total); 
			date = indent.getSupplyDate();    
			indentSupply = "Supply: " + indent.getSubscriptionType();  
			synced = "Synced: " + (indent.isSynced() ? "Y":"N"); 
		}
	    SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM, yyyy");   
	    String indentDateStr = dateFormat.format(date);
		TextView totalView = (TextView)listView.getRootView().findViewById(R.id.indentitemsTotal);
		if (totalView != null) {   
			totalView.setText(totalStr);        
		}
	
		TextView indentSupplyView = (TextView)listView.getRootView().findViewById(R.id.indentSupply);	
		if (indentSupplyView != null) {
			indentSupplyView.setText(indentSupply);  
		}	
		//TextView indentSyncedView = (TextView)listView.getRootView().findViewById(R.id.indentSynced);	
		//if (indentSyncedView != null) {
		//	indentSyncedView.setText(synced); 
		//}			
		TextView indentDateView = (TextView)listView.getRootView().findViewById(R.id.indentDate);	
		if (indentDateView != null) {
			indentDateView.setText(indentDateStr);
		}	
	}		
	
	void initializeIndentItems() {  
		ProductsDataSource prodsDatasource = new ProductsDataSource(getActivity());
		prodsDatasource.open();
		List<Product> products = prodsDatasource.getAllSaleProducts();
		for (int i = 0; i < products.size(); ++i) {
			Product product = products.get(i);
			IndentItem item = new IndentItem(product.getId(), product.getName(), -1, product.getPrice());
			indentItems.add(item);
		}
	}

	void makeIndentItemsEditable() {  
		if (indent == null) {
			return;
		}
		List <IndentItem> newItemsList = new ArrayList<IndentItem>();
		ProductsDataSource prodsDatasource = new ProductsDataSource(getActivity());
		prodsDatasource.open();
		List<Product> products = prodsDatasource.getAllSaleProducts();
		for (int i = 0; i < products.size(); ++i) {
			Product product = products.get(i);
			boolean productExists = false;
			for (int j = 0; j < indentItems.size(); ++j) {
				if (product.getId().equals(indentItems.get(j).getProductId())) {
					productExists = true;
					newItemsList.add(indentItems.get(j));              
				}         
			}                
			if (!productExists) {
				IndentItem item = new IndentItem(product.getId(), product.getName(), -1, product.getPrice());
				newItemsList.add(item);
			}
		}
		setListAdapter(null);    
	    indentItems = newItemsList;
	    
	    adapter = new IndentItemAdapter(getActivity(),
                R.layout.indentitems_item,
                indentItems);	
	    adapter.setEditable(isEditableList);
		setListAdapter(adapter);
	}
	
	/**
	 * Brute force update of list
	 */
	public void notifyChange() {
		if (indent == null) {
			// nothing to do 
			return;
		}
		setListAdapter(null);
	    datasource.open();
	    indentItems = datasource.getIndentItems(indent.getId());
	    
	    adapter = new IndentItemAdapter(getActivity(),
                R.layout.indentitems_item,
                indentItems);	
	    adapter.setEditable(isEditableList);
		setListAdapter(adapter);
	}
	public void editIndentAction(){
		isEditableList = true;
		datasource.open();
		makeIndentItemsEditable();
		updateIndentHeaderViewInternal(indent);

	}
	public void doneIndentAction(){
		datasource.open();
		if (indent == null) {
			long indentId = datasource.insertIndent("Created", getTotal(), "AM");
			datasource.insertIndentItems(indentId, indentItems);
			indent = datasource.getIndentDetails(indentId);
		}
		else {
			indent.setSynced(false);
			indent.setTotal(getTotal());
			datasource.updateIndentAndIndentItems(indent, indentItems);
		}

		notifyChange();
		updateIndentHeaderViewInternal(indent);
		datasource.close();
		isEditableList = false;
	}
	public void uploadIndentAction(final MenuItem menuItem){
		AlertDialog.Builder alert = new AlertDialog.Builder(
				getActivity());
		alert.setTitle("Upload Indent?");
		alert.setPositiveButton("Ok",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
										int whichButton) {
						ProgressBar progressBar = null;
						if(menuItem != null) {
							menuItem.setActionView(R.layout.progressbar);
//						ProgressBar progressBar = (ProgressBar) listView.getRootView().findViewById(R.id.indentUploadProgress);
//						progressBar.setVisibility(View.VISIBLE);
							progressBar = (ProgressBar) menuItem.getActionView().findViewById(R.id.menuitem_progress);
						}
						ServerSync serverSync = new ServerSync(getActivity());
						serverSync.uploadIndent(menuItem,indent, progressBar, indentItemsListFragment);
					}
				});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
										int whichButton) {
						// Canceled.
					}
				});
		alert.show();
	}
}
