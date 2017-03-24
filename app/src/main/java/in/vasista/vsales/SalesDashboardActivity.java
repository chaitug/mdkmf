package in.vasista.vsales;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.opengl.Visibility;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.vasista.location.MapsActivity;
import in.vasista.milkosoft.mdkmf.R;

import in.vasista.global.GlobalApplication;
import in.vasista.vsales.adapter.FacilityAutoAdapter;
import in.vasista.vsales.db.FacilityDataSource;
import in.vasista.vsales.db.IndentsDataSource;
import in.vasista.vsales.db.OrdersDataSource;
import in.vasista.vsales.db.PaymentsDataSource;
import in.vasista.vsales.db.ProductsDataSource;
import in.vasista.vsales.facility.Facility;
import in.vasista.vsales.preference.FragmentPreferences;
import in.vasista.vsales.sync.ServerSync;

public class SalesDashboardActivity extends DrawerCompatActivity  {
	public static final String module = SalesDashboardActivity.class.getName();

    static final private int MENU_PREFERENCES = Menu.FIRST+1;
    private static final int SHOW_PREFERENCES = 1;
    
    public static final String RETAILER_DB_PERM = "MOB_RTLR_DB_VIEW";    
    public static final String SALESREP_DB_PERM = "MOB_SREP_DB_VIEW"; 
    
	private Map facilityMap = new HashMap<String, Facility> ();
	AutoCompleteTextView actv;
	boolean settings_menu = true;
    
	private void setupFacilityDashboard() {
    	FacilityDataSource facilityDS = new FacilityDataSource(this);
    	facilityDS.open();
    	List<Facility> facilityList = facilityDS.getAllFacilities();
    	facilityDS.close(); 
		  for (int i = 0; i < facilityList.size(); ++i) {
			  Facility facility = facilityList.get(i);
			  facilityMap.put(facility.getId(), facility);   
		  }
		  
	    final String[] retailers = new String[facilityList.size()];
	    int index = 0;       
	    for (Facility facility : facilityList) { 
	    	retailers[index] = facility.getId();
	      index++;
	    }	               
	    final SalesDashboardActivity mainActivity = this;      
	    final FacilityAutoAdapter adapter = new FacilityAutoAdapter(this, R.layout.autocomplete_item, facilityList);
	    actv = (AutoCompleteTextView) findViewById(R.id.autoCompleteRetailer);
	    actv.setAdapter(adapter); 
	    actv.setVisibility(View.GONE);                   
	    
	    actv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	    	  @Override     
	    	  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	    	    InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	    	    in.hideSoftInputFromWindow(actv.getWindowToken(), 0);
	    	    actv.clearFocus();  
	    	    Facility retailer =  (Facility)parent.getItemAtPosition(position);	    	       		    			
	    	    mainActivity.initializeRetailer(retailer.getId(), true); 
	    	    actv.setText("");
				actv.setVisibility(View.GONE);		      			
	    	  }  
	    	  
	    });   
	    


	    initializeRetailer(null, false);		
	}
	
    void setupDashboard() {
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);    	
    	String retailerPerm = prefs.getString(RETAILER_DB_PERM, "N");
    	String salesRepPerm = prefs.getString(SALESREP_DB_PERM, "N");
		String locationPerm = prefs.getString(MainActivity.LOCATION_DB_PERM, "N");


		if (locationPerm.equals("N")) {
			Button locationLayout = (Button) findViewById(R.id.home_btn_location);
			//((LinearLayout)locationLayout.getParent()).removeView(locationLayout);
			locationLayout.setVisibility(View.GONE);

		}

    	if (salesRepPerm.equals("N") && retailerPerm.equals("N")) {
    		
    	    LinearLayout paymentsCatalogLayout = (LinearLayout) findViewById(R.id.paymentsCatalogLayout); 
    	    ((LinearLayout)paymentsCatalogLayout.getParent()).removeView(paymentsCatalogLayout); 

    	    LinearLayout indentsOrdersLayout = (LinearLayout) findViewById(R.id.indentsOrdersLayout); 
    	    ((LinearLayout)indentsOrdersLayout.getParent()).removeView(indentsOrdersLayout);  

    	    LinearLayout facilityHeader = (LinearLayout) findViewById(R.id.facilityHeader); 
    	    ((LinearLayout)facilityHeader.getParent()).removeView(facilityHeader);    
    	    
    	    AutoCompleteTextView actv = (AutoCompleteTextView) findViewById(R.id.autoCompleteRetailer); 
    	    ((LinearLayout)actv.getParent()).removeView(actv);    	    
    	    
    		ImageButton searchButton = (ImageButton)findViewById(R.id.homeSearch);
    		searchButton.setVisibility(View.GONE); 
    		Button outletsButton = (Button)findViewById(R.id.home_btn_outlets);
    		outletsButton.setVisibility(View.GONE);  
    	}
    	else if (salesRepPerm.equals("N")) {
    	    AutoCompleteTextView actv = (AutoCompleteTextView) findViewById(R.id.autoCompleteRetailer); 	     
    	    actv.setVisibility(View.GONE);
    		Button outletsButton = (Button)findViewById(R.id.home_btn_outlets);
    		outletsButton.setVisibility(View.GONE);     
    	    TextView accSum = (TextView) findViewById(R.id.accntSummary); 	     
    	    accSum.setClickable(false);      		
    	}		
    	
    	// Do facility dashboard initialization if required
    	if (salesRepPerm.equals("Y") || retailerPerm.equals("Y")) {
    		setupFacilityDashboard();
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
    	String onlySalesDashboard = prefs.getString("onlySalesDashboard", "N");	
Log.d(module, "onlySalesDashboard equals " + onlySalesDashboard);						    		    		    		    	
    	if (onlySalesDashboard.equals("Y")) {
    	    setContentChildView(R.layout.activity_sales_home);
			getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		} else {
			setContentChildView(R.layout.activity_sales_home_alt);
			actionBarHomeEnabled();

			// back button functionality
			toolbar.setNavigationOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});
    	}


		SharedPreferences.Editor prefEditor = prefs.edit(); 
    	String storeId; 
    	storeId = prefs.getString("storeId", "");	    	
    	if (storeId.isEmpty()) {
    		storeId = "B80504";
    		prefEditor.putString("storeId", storeId);
    		prefEditor.commit(); 
    	} 
	    setupDashboard();
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
	   super.onPause();
	}

	/**
	 * onRestart
	 * Called after your activity has been stopped, prior to it being started again.
	 * Always followed by onStart().
	 *
	 */

	protected void onRestart ()
	{
	   super.onRestart();
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
		super.onResume();
		setupDashboard();
		if(((GlobalApplication)getApplication()).isPrefChange()){
			((GlobalApplication)getApplication()).setPrefChange(false);
			Intent i = new Intent(this.getBaseContext(), SplashScreenActivity.class);
			startActivity(i);
			finish();
		}

	}

	/**
	 * onStart
	 * Called when the activity is becoming visible to the user.
	 * Followed by onResume() if the activity comes to the foreground, or onStop() if it becomes hidden.
	 *
	 */

	protected void onStart ()
	{
	   super.onStart();
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
	   super.onStop();
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
@Override
public boolean onCreateOptionsMenu(Menu menu) {
	getMenuInflater().inflate(R.menu.main, menu);
	menu.removeItem(R.id.action_about);
	menu.removeItem(R.id.action_refresh);
	if (!settings_menu){
		menu.removeItem(R.id.action_settings);
	}
	return super.onCreateOptionsMenu(menu);
}
	public boolean onOptionsItemSelected(MenuItem item){
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
			case R.id.homeSearch:
				if (actv.isShown()) {
					actv.setVisibility(View.GONE);
				}
				else {
					actv.setVisibility(View.VISIBLE);
				}
				return true;
		}
		return false;
	}
	// Click Methods
    public void onClick(View v) {
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    	String retailerId = prefs.getString("storeId", "");
        if (!retailerId.isEmpty()) { 
        	Intent facilityDetailsIntent = new Intent(this, FacilityDetailsActivity.class);
        	facilityDetailsIntent.putExtra("facilityId", retailerId);
        	startActivity(facilityDetailsIntent);
        } 
    } 
    
    
    public void onClickFeature (View v)
    {
        int id = v.getId ();
        switch (id) {
          case R.id.home_btn_indents :
              startActivity (new Intent(getApplicationContext(), IndentActivity.class));
               break;
          case R.id.home_btn_orders :
              startActivity (new Intent(getApplicationContext(), OrderActivity.class));
               break; 
          case R.id.home_btn_payments :
               startActivity (new Intent(getApplicationContext(), PaymentActivity.class));
               break;
          case R.id.home_btn_catalog :
               startActivity (new Intent(getApplicationContext(), CatalogActivity.class));
               break;
          case R.id.home_btn_outlets :
               startActivity (new Intent(getApplicationContext(), FacilityActivity.class));
               break;
			case R.id.home_btn_location :
				startActivity (new Intent(getApplicationContext(), MapsActivity.class));
				break;
          default:    
        	   break;    
        }
    }    
    
    public void cleanupRetailerProducts() {
    	ProductsDataSource productDS = new ProductsDataSource(this);
    	productDS.open();
    	productDS.deleteAllSaleProducts();
    	productDS.close();    	
    }

    public void cleanupRetailerPayments() {
    	PaymentsDataSource paymentDS = new PaymentsDataSource(this);
    	paymentDS.open();
    	paymentDS.deleteAllPayments();
    	paymentDS.close(); 	
    }    
    
    public void cleanupRetailerOrders() {
    	OrdersDataSource orderDS = new OrdersDataSource(this);
    	orderDS.open();
    	orderDS.deleteAllOrders();
    	orderDS.close();    	
    }

    public void cleanupRetailerIndents() {
    	IndentsDataSource indentDS = new IndentsDataSource(this);
    	indentDS.open();
    	indentDS.deleteAllIndents();
    	indentDS.close();    
    }     
    
    public void cleanupRetailerData() {
    	cleanupRetailerProducts();
    	cleanupRetailerPayments();
    	cleanupRetailerOrders();
    	cleanupRetailerIndents();
    }
    
    void initializeRetailer(String retailerId, boolean fetchProducts) {
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    	if (retailerId == null) { 
    		retailerId = prefs.getString("storeId", "");
    	}      
    	if (retailerId == null || retailerId.isEmpty()) { 
    		cleanupRetailerData();
    		return;
    	}
    	SharedPreferences.Editor prefEditor = prefs.edit();
    	prefEditor.putString("storeId", retailerId);  
    	prefEditor.apply();
    	
		TextView accountSummaryView = (TextView)findViewById(R.id.accntSummary);
		Facility facility = (Facility)facilityMap.get(retailerId);
		String facilityName = "";
		if (facility != null) {
			facilityName = facility.getName();
		} 
		else {
			// check if Store Name is set in preferences
			facilityName = prefs.getString("storeName", "");
		}
		accountSummaryView.setText("" + retailerId + 
				" [" + facilityName +  "] :");
		accountSummaryView.setPaintFlags(accountSummaryView.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
		
		ProgressBar progressBar = (ProgressBar) findViewById(R.id.getDuesProgress);
		progressBar.setVisibility(View.VISIBLE);
		ServerSync serverSync = new ServerSync(this);
		serverSync.getFacilityDues(progressBar, this);	   
		if (fetchProducts) {
			cleanupRetailerData();
			serverSync.updateProducts(null, progressBar, null);
		}    	
    }
    
    public void updateDues(Map boothDues, Map boothTotalDues) {
    	if (boothDues != null && boothDues.get("amount") != null) {  
    		double currentDues = (Double)boothDues.get("amount");
    		TextView amountView = (TextView)findViewById(R.id.currentDues);
    		amountView.setText(String.format("Rs %.2f", currentDues));
    	}
    	if (boothTotalDues != null && boothTotalDues.get("totalDueAmount") != null) {
    		double totalDues = (Double)boothTotalDues.get("totalDueAmount");
    		TextView amountView = (TextView)findViewById(R.id.totalDues);
    		amountView.setText(String.format("Rs %.2f", totalDues));
    	}	
    	if (boothTotalDues != null && boothTotalDues.get("fdrAmount") != null) { 
    		double fdrAmount = (Double)boothTotalDues.get("fdrAmount");
    		TextView amountView = (TextView)findViewById(R.id.fdrAmount);
    		amountView.setText(String.format("Rs %.2f", fdrAmount));  
    	}
		if (boothTotalDues != null && boothTotalDues.get("sdAmount") != null) {
			double fdrAmount = (Double)boothTotalDues.get("sdAmount");
			TextView sdAmountView = (TextView)findViewById(R.id.sdamount);
			sdAmountView.setText(String.format("Rs %.2f", fdrAmount));
		}
	}
}
