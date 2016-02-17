package in.vasista.vsales;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import in.vasista.vsales.preference.FragmentPreferences;
import in.vasista.vsales.sync.ServerSync;

public class HRDashboardActivity extends DashboardAppCompatActivity  {   
	public static final String module = HRDashboardActivity.class.getName();

    static final private int MENU_PREFERENCES = Menu.FIRST+1;
    private static final int SHOW_PREFERENCES = 1;
	private boolean hideMenu = false;
	MenuItem menuItem;
	boolean settings_menu = true;
	
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
    	String onlyHRDashboard = prefs.getString("onlyHRDashboard", "N");	
Log.d(module, "onlyHRDashboard equals " + onlyHRDashboard);						    		    		
    	
    	if (onlyHRDashboard.equals("Y")) {
    	   // setContentView(R.layout.activity_hr_home);
			setContentChildView(R.layout.activity_hr_home);
			getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    	} 
    	else { 
//    	    setContentView(R.layout.activity_hr_home_alt);
			setContentChildView(R.layout.activity_hr_home_alt);
			settings_menu = false;
			actionBarHomeEnabled();
    	}

		ProgressBar progressBar = new ProgressBar(this);
		//progressBar.setVisibility(View.GONE);
		progressBar.setIndeterminate(true);
		getSupportActionBar().setDisplayShowCustomEnabled(true);
		ActionBar.LayoutParams params = new ActionBar.LayoutParams(
				ActionBar.LayoutParams.WRAP_CONTENT,
				ActionBar.LayoutParams.WRAP_CONTENT,
				Gravity.RIGHT);
		getSupportActionBar().setCustomView(progressBar,params);

//		ProgressBar progressBar = (ProgressBar) findViewById(R.id.myEmployeeRefreshProgress);
//		progressBar.setVisibility(View.VISIBLE);
		ServerSync serverSync = new ServerSync(this);

		serverSync.fetchMyEmployeeDetails(progressBar, this);

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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		menu.removeItem(R.id.homeSearch);
		menu.removeItem(R.id.action_refresh);
		if (!settings_menu){
			menu.removeItem(R.id.action_settings);
			menu.removeItem(R.id.action_about);
		}
		return super.onCreateOptionsMenu(menu);
	}
	public boolean onOptionsItemSelected(MenuItem item){
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
			case R.id.action_settings:
				Intent i = new Intent(this.getBaseContext(), FragmentPreferences.class);
				startActivityForResult(i, SHOW_PREFERENCES);
				return true;
			case R.id.action_about:
				onClickAbout();
				return true;
		}
		return false;
	}
	// Click Methods
    
    
    public void onClickFeature (View v)
    {
        int id = v.getId ();
        switch (id) {
          case R.id.home_btn_employeeprofile :
              startActivity (new Intent(getApplicationContext(), MyEmployeeDetailsActivity.class));           
               break;
          case R.id.home_btn_attendance : 
              startActivity (new Intent(getApplicationContext(), AttendanceActivity.class));  
               break;               
          case R.id.home_btn_leave :
              startActivity (new Intent(getApplicationContext(), LeaveActivity.class));           
               break; 
          case R.id.home_btn_payslip :
              startActivity (new Intent(getApplicationContext(), PayslipActivity.class));            
               break;
          case R.id.home_btn_emplsearch :
              startActivity (new Intent(getApplicationContext(), EmployeeActivity.class));  
               break;               
          default:    
        	   break;    
        }
    }    
    
}
