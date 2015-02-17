package in.vasista.inventory;


import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import in.vasista.rest.ServerRestSync;
import in.vasista.vsales.DashboardActivity;
import in.vasista.vsales.R;
import in.vasista.vsales.catalog.Product;
import in.vasista.vsales.db.EmployeeDataSource;
import in.vasista.vsales.employee.Employee;
import in.vasista.vsales.sync.ServerSync;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import java.text.DateFormat;

import com.google.gson.Gson;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class InventoryDetailsActivity extends DashboardActivity  {
	
	private String productId = "";
	
	public void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);

		// Inflate your view    
		setContentView(R.layout.inventorydetails_layout);     		
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
		TextView uomView = (TextView)findViewById(R.id.uom);
		uomView.setText(inventoryDetail.getUom());			
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
		supplierNameView.setText(inventoryDetail.getSupplierName()); 
		TextView supplierRateView = (TextView)findViewById(R.id.supplierRate);
		String supplierRateStr = "";
		if (inventoryDetail.getSupplierRate() != null) {
			float supplierRate = inventoryDetail.getSupplierRate().setScale(2,BigDecimal.ROUND_HALF_UP).floatValue();
	    	supplierRateStr = String.format("Rs %.2f", supplierRate);
		}
	    supplierRateView.setText(supplierRateStr);	
	    
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
