package in.vasista.vsales;


import in.vasista.rest.ServerRestSync;
import in.vasista.vsales.adapter.FacilityAutoAdapter;
import in.vasista.vsales.db.FacilityDataSource;
import in.vasista.vsales.db.IndentsDataSource;
import in.vasista.vsales.db.OrdersDataSource;
import in.vasista.vsales.db.PaymentsDataSource;
import in.vasista.vsales.db.ProductsDataSource;
import in.vasista.vsales.facility.Facility;
import in.vasista.vsales.preference.FragmentPreferences;
import in.vasista.vsales.sync.ServerSync;

import java.util.HashMap; 
import java.util.List;
import java.util.Map;
       
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends DashboardActivity  {   
	public static final String module = MainActivity.class.getName();

    static final private int MENU_PREFERENCES = Menu.FIRST+1;
    private static final int SHOW_PREFERENCES = 1;
    
    public static final String RETAILER_DB_PERM = "MOB_RTLR_DB_VIEW";    
    public static final String SALESREP_DB_PERM = "MOB_SREP_DB_VIEW";
    public static final String HR_DB_PERM = "MOB_HR_DB_VIEW";
    public static final String INVENTORY_DB_PERM = "MOB_INVENTORY_DB_VIEW";
    
    
	private Map facilityMap = new HashMap<String, Facility> ();
	
    void setupDashboard() {
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this); 
		SharedPreferences.Editor prefEditor = prefs.edit();
    	
    	String retailerPerm = prefs.getString(RETAILER_DB_PERM, "N");
    	String salesRepPerm = prefs.getString(SALESREP_DB_PERM, "N");
    	String hrPerm = prefs.getString(HR_DB_PERM, "N");   
    	String inventoryPerm = prefs.getString(INVENTORY_DB_PERM, "N");   
inventoryPerm = "Y"; //::TODO::
    	prefEditor.putString("onlySalesDashboard", "N");
    	prefEditor.putString("onlyHRDashboard", "N");
    	prefEditor.commit();   		

    	if ((salesRepPerm.equals("Y") || retailerPerm.equals("Y")) && hrPerm.equals("N") && inventoryPerm.equals("N")) {
        	prefEditor.putString("onlySalesDashboard", "Y");
        	prefEditor.commit();   		
    	    startActivity (new Intent(getApplicationContext(), SalesDashboardActivity.class));
            finish();    	    
    	    return;
    	}
    	if ((salesRepPerm.equals("N") && retailerPerm.equals("N")) && inventoryPerm.equals("N") && hrPerm.equals("Y")) {
        	prefEditor.putString("onlyHRDashboard", "Y");
        	prefEditor.commit();   	    		
    	    startActivity (new Intent(getApplicationContext(), HRDashboardActivity.class));
            finish();    	    
    	    return;
    	}   
    	
	    setContentView(R.layout.activity_home);
    	if (salesRepPerm.equals("N") && retailerPerm.equals("N")) {    		
    		Button button = (Button)findViewById(R.id.home_btn_sales);
    		button.setVisibility(View.GONE); 
    	}
    	if (hrPerm.equals("N")) {    		
    		Button button = (Button)findViewById(R.id.home_btn_hr);
    		button.setVisibility(View.GONE); 
    	}  
    	if (inventoryPerm.equals("N")) {    		
    		Button button = (Button)findViewById(R.id.home_btn_inventory);
    		button.setVisibility(View.GONE); 
    	}      	
    }
	
	/**
	 * onCreate - called when the activity is first created.
	 * Called when the activity is first created. 
	 * This is where you should do all of your normal static set up: create views, bind data to lists, etc. 
	 * This method also provides you with a Bundle containing the activity's previously frozen state, if there was one.
	 * 
	 * Always followed by onStart().   
	 *
	 */

	protected void onCreate(Bundle savedInstanceState) 
	{   
	    super.onCreate(savedInstanceState);   

    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor prefEditor = prefs.edit();
    	String serverURL;
    	serverURL = prefs.getString("serverURL", "");	    	
    	if (serverURL.isEmpty()) {
    		serverURL = "http://motherdairykmf.vasista.in:58080/webtools/control/xmlrpc";
    		prefEditor.putString("serverURL", serverURL);
    		prefEditor.commit();
    	}
    	String tenantId;
    	tenantId = prefs.getString("tenantId", "");	    	
    	if (tenantId.isEmpty()) {
    		tenantId = "MDKMF";
    		prefEditor.putString("tenantId", tenantId);
    		prefEditor.commit();
    	}  
	    setupDashboard();
	    
//	    ServerRestSync sync = new ServerRestSync(this);
//	    sync.fetchMaterials(); 
	}
	    
	/**
	 * onDestroy
	 * The final call you receive before your activity is destroyed. 
	 * This can happen either because the activity is finishing (someone called finish() on it, 
	 * or because the system is temporarily destroying this instance of the activity to save space. 
	 * You can distinguish between these two scenarios with the isFinishing() method.
	 *
	 */

	protected void onDestroy ()
	{
	   super.onDestroy ();
	}

	/**
	 * onPause
	 * Called when the system is about to start resuming a previous activity. 
	 * This is typically used to commit unsaved changes to persistent data, stop animations 
	 * and other things that may be consuming CPU, etc. 
	 * Implementations of this method must be very quick because the next activity will not be resumed 
	 * until this method returns.
	 * Followed by either onResume() if the activity returns back to the front, 
	 * or onStop() if it becomes invisible to the user.
	 *
	 */

	protected void onPause ()
	{
	   super.onPause ();
	}

	/**
	 * onRestart
	 * Called after your activity has been stopped, prior to it being started again.
	 * Always followed by onStart().
	 *
	 */

	protected void onRestart ()
	{
	   super.onRestart ();
	}

	/**
	 * onResume
	 * Called when the activity will start interacting with the user. 
	 * At this point your activity is at the top of the activity stack, with user input going to it.
	 * Always followed by onPause().
	 *
	 */

	protected void onResume ()
	{
	   super.onResume ();
	}

	/**
	 * onStart
	 * Called when the activity is becoming visible to the user.
	 * Followed by onResume() if the activity comes to the foreground, or onStop() if it becomes hidden.
	 *
	 */

	protected void onStart ()
	{
	   super.onStart ();
	}

	/**
	 * onStop
	 * Called when the activity is no longer visible to the user
	 * because another activity has been resumed and is covering this one. 
	 * This may happen either because a new activity is being started, an existing one 
	 * is being brought in front of this one, or this one is being destroyed.
	 *
	 * Followed by either onRestart() if this activity is coming back to interact with the user, 
	 * or onDestroy() if this activity is going away.
	 */
 
	protected void onStop () 
	{
	   super.onStop ();  
	}

/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        
        menu.add(0, MENU_PREFERENCES, Menu.NONE, R.string.menu_preferences);        
        return true; 
    }
    
    public boolean onOptionsItemSelected(MenuItem item){
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
          case (MENU_PREFERENCES): {
            Intent i = new Intent(this, FragmentPreferences.class);
            startActivityForResult(i, SHOW_PREFERENCES);
            return true;
          }
        }
        return false;
    } */
    
}
