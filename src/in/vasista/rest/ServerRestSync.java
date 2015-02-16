package in.vasista.rest;

import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import in.vasista.inventory.InventoryDetail;
import in.vasista.inventory.InventoryDetailsActivity;
import in.vasista.inventory.InventoryListFragment;
import in.vasista.vsales.catalog.Product;
import in.vasista.vsales.db.ProductsDataSource;
import in.vasista.vsales.indent.IndentListFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

public class ServerRestSync {
	public static final String module = ServerRestSync.class.getName();	
	private RestAdapter restAdapter;
	private VbizAPI api; 		
	private Context context;

	//private MySQLiteHelper dbHelper;
	
	public ServerRestSync(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String urlStr = prefs.getString("serverURL", "");
		urlStr = urlStr + "/rest";
		final String userName = prefs.getString("userName", "");
		final String password = prefs.getString("password", "");
		final String tenantId = prefs.getString("tenantId", "");		
		Log.d( module, "serverURL=" + urlStr +"; userName=" + userName + "; password=" + password +
				";tenantid=" + tenantId); 
		RequestInterceptor requestInterceptor = new RequestInterceptor() {
			  @Override
			  public void intercept(RequestFacade request) {
			    request.addHeader("login.username", userName);
			    request.addHeader("login.password", password);
			    request.addHeader("tenantId", tenantId);			    
			  }
		};
		restAdapter = new RestAdapter.Builder()
	    	.setEndpoint("https://milkosoft.motherdairykmf.in/rest")
	    	.setRequestInterceptor(requestInterceptor)
	    	.build();
		api = restAdapter.create(VbizAPI.class);
		this.context = context; 
	}
	
	public void fetchMaterials() {
		List<Product> products = api.fetchMaterials();
			Log.d(module, "products.size = " + products.size());
			final ProductsDataSource datasource = new ProductsDataSource(context);
	    	datasource.open();
	    	datasource.deleteAllInventoryProducts();
	    	datasource.insertProducts(products);
			datasource.close();
	    	Log.d(module, "products loaded into db");	
	    	
	}
	
	public void fetchMaterialInventory(String productId, final ProgressBar progressBar, final InventoryDetailsActivity activity) {
		api.fetchMaterialInventory( productId, new Callback <InventoryDetail>() {
		    @Override  
		    public void failure(RetrofitError retrofitError) {
		    	Log.d(module, "retrofitError = " + retrofitError);
				if (progressBar != null) {
					progressBar.setVisibility(View.INVISIBLE);
				}
		    }

			@Override
			public void success(InventoryDetail inventoryDetail, Response response) {
		    	Log.d(module, "inventoryDetail = " + inventoryDetail);

		    	if (progressBar != null) {
		    		progressBar.setVisibility(View.INVISIBLE);
		    	}				
		    	if (activity != null && inventoryDetail != null) {
		    		activity.updateInventoryDetail(inventoryDetail);
		    	}
			}			
		});
	}	
}
