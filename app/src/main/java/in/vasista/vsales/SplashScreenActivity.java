package in.vasista.vsales;

import java.util.HashMap;
import java.util.Map;

import in.vasista.milkosoft.mdkmf.R;
import in.vasista.vsales.sync.ServerSync;
import in.vasista.vsales.sync.xmlrpc.XMLRPCApacheAdapter;
import in.vasista.vsales.sync.xmlrpc.XMLRPCMethodCallback;
import android.app.Activity;  
import android.app.ProgressDialog;  
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;  
import android.os.Bundle;   
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

public class SplashScreenActivity extends Activity  
{  
	public static final String module = SplashScreenActivity.class.getName();		
	private ProgressBar progressBar;  

	/** Called when the activity is first created. */  
	@Override  
	public void onCreate(Bundle savedInstanceState)     
	{   
		super.onCreate(savedInstanceState);  
		setContentView(R.layout.activity_splash);
		//Initialize a LoadViewTask object and call the execute() method  
		new LoadViewTask().execute();         

	}  

	//To use the AsyncTask, it must be subclassed  
	private class LoadViewTask extends AsyncTask<Void, Integer, Void>  
	{  
		//Before running code in separate thread   
		@Override  
		protected void onPreExecute()  
		{  
			progressBar = (ProgressBar)findViewById(R.id.splashProgress);
			progressBar.setVisibility(View.VISIBLE);   
		}  

		//The code to be executed in a background thread.  
		@Override  
		protected Void doInBackground(Void... params)  
		{  
			/* This is just a code that delays the thread execution 4 times, 
			 * during 850 milliseconds and updates the current progress. This 
			 * is where the code that is going to be executed on a background 
			 * thread must be placed. 
			 */  
			try  
			{  
				//Get the current thread's token  
				synchronized (this)  
				{
					Map paramMap = new HashMap();
					SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
					SharedPreferences.Editor prefEditor = prefs.edit();
					XMLRPCApacheAdapter adapter = new XMLRPCApacheAdapter(getBaseContext());
					Object result = adapter.callSync("getMobilePermissions", paramMap);	
					if (result != null) { 
						Map  permissions = (Map)((Map)result).get("permissionResults");
						if (permissions != null) {
							Log.d(module, "permissions.size() = " + permissions.size());
							Object[]  permissionList = (Object[] )((Map)permissions).get("permissionList");
							if (permissionList != null)  {
								Log.d(module, "permissionList.length = " + permissionList.length);	
						    	String retailerPerm = "N";
						    	String salesRepPerm = "N";
						    	String hrPerm = "N";
						    	String inventoryPerm = "N";
						    	
								for (int i = 0; i < permissionList.length; ++i) {
									String dashboardPermission = (String)permissionList[i];
									Log.d(module, "dashboardPermission = " + dashboardPermission);
									if (MainActivity.RETAILER_DB_PERM.equals(dashboardPermission)) {
										retailerPerm = "Y";
									}
									else if (MainActivity.SALESREP_DB_PERM.equals(dashboardPermission)) {
										salesRepPerm = "Y";
									}
									else if (MainActivity.HR_DB_PERM.equals(dashboardPermission)) {
										hrPerm = "Y";
									}
									else if (MainActivity.INVENTORY_DB_PERM.equals(dashboardPermission)) {
										inventoryPerm = "Y";
									}									
								}
								// refresh permissions prefs

					    		prefEditor.putString(MainActivity.RETAILER_DB_PERM, retailerPerm);
					    		prefEditor.putString(MainActivity.SALESREP_DB_PERM, salesRepPerm);
					    		prefEditor.putString(MainActivity.HR_DB_PERM, hrPerm);		
					    		prefEditor.putString(MainActivity.INVENTORY_DB_PERM, inventoryPerm);					    		  		
							}
						}
					}

					String name = (String)((Map)result).get("name");

					if(name != null)
						prefEditor.putString(MainActivity.USER_FULLNAME, name);

					String contactNumber = (String)((Map)result).get("contactNumber");
					if (contactNumber != null)
						prefEditor.putString(MainActivity.USER_MOBILE, contactNumber);

					prefEditor.apply();
					/*
					//Initialize an integer (that will act as a counter) to zero  
					int counter = 0;  
					//While the counter is smaller than four  
					while(counter <= 4)  
					{  
						this.wait(500);  
						//Increment the counter  
						counter++;  

					}  
					*/
				}  
			}  
/*			catch (InterruptedException e)  
			{  
				e.printStackTrace();  
			} */ 
			catch (Exception e) {
				e.printStackTrace();  				
			}
			return null;  
		}  

		//Update the progress  
		@Override  
		protected void onProgressUpdate(Integer... values)  
		{  

		}  

		//after executing the code in the thread  
		@Override  
		protected void onPostExecute(Void result)  
		{  
  
			progressBar.setVisibility(View.INVISIBLE);
			Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
            startActivity(i); 
            
         // close this activity
            finish();
		}  
	}  
}  

