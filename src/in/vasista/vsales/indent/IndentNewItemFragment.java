package in.vasista.vsales.indent;

import in.vasista.vsales.R;
import in.vasista.vsales.catalog.Product;
import in.vasista.vsales.db.ProductsDataSource;

import java.util.List;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;


public class IndentNewItemFragment extends Fragment{

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.indent_new_item, container, false);
		addItemsToProductSpinner(view);
		return view;
	}
	
	  // add items into spinner dynamically
	  void addItemsToProductSpinner(View view) {	 
		Spinner spinner = (Spinner) view.findViewById(R.id.indentitemSpinnerProductId);
		ProductsDataSource datasource = new ProductsDataSource(getActivity());
	    datasource.open();
	    List<Product> catalogItems = datasource.getAllSaleProducts();		
		ArrayAdapter<Product> dataAdapter = new ArrayAdapter<Product>(getActivity(),
			android.R.layout.simple_spinner_item, catalogItems);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(dataAdapter);
		
	  }	
}
