package in.vasista.inventory;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import in.vasista.rest.ServerRestSync;
import in.vasista.vsales.DashboardAppCompatActivity;
import in.vasista.vsales.R;
import in.vasista.vsales.catalog.Product;

public class InventoryDetailsActivity extends DashboardAppCompatActivity {
	
	private String productId = "";
	
	public void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);

		// Inflate your view    
		setContentChildView(R.layout.inventorydetails_layout);
		actionBarHomeEnabled();
		Intent inventoryDetailsIntent= getIntent();
		Product product = (new Gson()).fromJson(inventoryDetailsIntent.getStringExtra("product"), Product.class);
		if (product != null) {   
			TextView materialCodeView = (TextView)findViewById(R.id.materialCode);
			materialCodeView.setText(product.getName());
			TextView materialNameView = (TextView)findViewById(R.id.materialName);
			materialNameView.setText(product.getDescription());	 
			TextView materialCategoryView = (TextView)findViewById(R.id.materialCategory);
			materialCategoryView.setText(product.getProductCategoryId());	 			
		}
		ProgressBar progressBar = (ProgressBar) findViewById(R.id.inventoryDetailsProgress);
		progressBar.setVisibility(View.VISIBLE);
		ServerRestSync serverSync = new ServerRestSync(this);
		serverSync.fetchMaterialInventory(product.getId(), progressBar, this);	
	}
	
	 
	public void updateInventoryDetail (InventoryDetail inventoryDetail) 
	{
		//TextView inventoryDetailHeaderView = (TextView)findViewById(R.id.inventoryDetailsHeader);
		//inventoryDetailHeaderView.setText(inventoryDetail.getName() + " [" + inventoryDetail.getDescription() + "]");		
		TextView materialCodeView = (TextView)findViewById(R.id.materialCode);
		materialCodeView.setText(inventoryDetail.getName());
		TextView materialNameView = (TextView)findViewById(R.id.materialName);
		materialNameView.setText(inventoryDetail.getDescription());	 
		TextView materialCategoryView = (TextView)findViewById(R.id.materialCategory);
		materialCategoryView.setText(inventoryDetail.getCategoryId());	 		

		TextView inventoryQtyView = (TextView)findViewById(R.id.inventoryQty); 
		String inventoryCountStr = ""; 
		if (inventoryDetail.getInventoryCount() != null) {
			float inventoryCount = inventoryDetail.getInventoryCount().setScale(2,BigDecimal.ROUND_HALF_UP).floatValue();
			inventoryCountStr = String.format("%.2f (%s)", inventoryCount, inventoryDetail.getUom());   
		}
		inventoryQtyView.setText(inventoryCountStr);	
		TextView inventoryValueView = (TextView)findViewById(R.id.inventoryValue);
		String inventoryValueStr = "";
		if (inventoryDetail.getInventoryCost() != null) {
			float inventoryValue = inventoryDetail.getInventoryCost().setScale(2,BigDecimal.ROUND_HALF_UP).floatValue();
			inventoryValueStr = String.format("Rs %.2f", inventoryValue);
		}
	    inventoryValueView.setText(inventoryValueStr);	
		
		TextView supplierNameView = (TextView)findViewById(R.id.supplierName);
		supplierNameView.setText(inventoryDetail.getSupplierName() + " [" + inventoryDetail.getSupplierId() + "]"); 
		TextView supplierRateView = (TextView)findViewById(R.id.supplierRate);
		String supplierRateStr = "";
		if (inventoryDetail.getSupplierRate() != null) {
			float supplierRate = inventoryDetail.getSupplierRate().setScale(2,BigDecimal.ROUND_HALF_UP).floatValue();
	    	supplierRateStr = String.format("Rs %.2f", supplierRate);
		}
	    supplierRateView.setText(supplierRateStr);	
		TextView lastSupplyDateView = (TextView)findViewById(R.id.lastSupplyDate);
		lastSupplyDateView.setText(inventoryDetail.getLastSupplyDate()); 
	    
		TextView specificationView = (TextView)findViewById(R.id.specification);
		specificationView.setText(inventoryDetail.getSpecification()); 
		 
		//String currentDateTimeString = DecimalFormat.getInstance().format(new Date());
		Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String text = "Stock Position as of: " + df.format(c.getTime());		
		TextView inventoryDetailsLastFetchTimeView = (TextView)findViewById(R.id.inventoryDetailsLastFetchTime);
		inventoryDetailsLastFetchTimeView.setText(text);   
		
		/*
		if (!punchTime.isEmpty()) {
			TextView employeePunchTimeView = (TextView)findViewById(R.id.employeeLastPunchTime);
			String text = "Today's Last Punch: " + punchTime + " (" + inOut + ")";
			employeePunchTimeView.setText(text);
		}
		*/
	}
}
