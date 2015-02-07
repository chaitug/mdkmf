package in.vasista.rest;

import java.util.List;

import org.apache.http.HttpStatus;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import in.vasista.vsales.catalog.Product;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

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
		api.fetchMaterials( new Callback <List<Product>>() {

		    @Override  
		    public void failure(RetrofitError retrofitError) {
		    	Log.d(module, "retrofitError = " + retrofitError);
		    }

			@Override
			public void success(List<Product> products, Response response) {
				// TODO Auto-generated method stub
				for (int i = 0; i < products.size(); ++i) {
					Log.d(module, "product = " + products.get(i));	
				}
		    	Log.d(module, "response = " + response);						    	
			}			
		});
	}
}
