package in.vasista.vsales;


import java.text.SimpleDateFormat;
import java.util.Date;

import in.vasista.vsales.db.EmployeeDataSource;
import in.vasista.vsales.employee.Employee;
import in.vasista.vsales.sync.ServerSync;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MyEmployeeDetailsActivity extends DashboardActivity  {
	
	public void updateEmployeeDetails(Employee employee) {
		TextView idView = (TextView)findViewById(R.id.employeeId);
		idView.setText(employee.getId());
		TextView nameView = (TextView)findViewById(R.id.employeeName);
		nameView.setText(employee.getName());		 
		TextView deptView = (TextView)findViewById(R.id.employeeDept); 
		deptView.setText(employee.getDept());	
		TextView employeePositionView = (TextView)findViewById(R.id.employeePosition);
		employeePositionView.setText(employee.getPosition());			
		TextView joinDateView = (TextView)findViewById(R.id.employeeJoinDate);
	    Date date = employee.getJoinDate();
	    SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM, yyyy");
	    String dateStr = dateFormat.format(date);
		joinDateView.setText(dateStr);	
		TextView phoneView = (TextView)findViewById(R.id.employeePhone);
		phoneView.setText(employee.getPhoneNum()); 	
		TextView elView = (TextView)findViewById(R.id.employeeEarnedLeave);
		elView.setText(String.format("%.1f", employee.getEarnedLeave())); 	
		TextView clView = (TextView)findViewById(R.id.employeeCasualLeave);
		clView.setText(String.format("%.1f", employee.getCasualLeave()));
		TextView hplView = (TextView)findViewById(R.id.employeeHalfPayLeave);
		hplView.setText(String.format("%.1f", employee.getHalfPayLeave()));				
	}
	
	public void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);

		// Inflate your view   
		setContentView(R.layout.myemployeedetails_layout);    

		String employeeId = "";		
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    	employeeId = prefs.getString("employeeId", "");
		
		final Employee employee;
		EmployeeDataSource datasource = new EmployeeDataSource(this);  
		datasource.open();
		employee = datasource.getEmployeeDetails(employeeId);		
		datasource.close();
		if (employee == null) {        
			return;
		}   
		updateEmployeeDetails(employee);
		
		final Activity myActivity = this; 
		ImageButton employeeSearch = (ImageButton) findViewById(R.id.employeeSearch);
		employeeSearch.setClickable(true);
		employeeSearch.setOnClickListener(new OnClickListener() { 
			public void onClick(View view) {
            	Intent employeesIntent = new Intent(myActivity, EmployeeActivity.class);
            	startActivity(employeesIntent);
            }   
		});
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

		//::TODO:: temp code
		ProgressBar progressBar = (ProgressBar) findViewById(R.id.myEmployeeRefreshProgress);
		progressBar.setVisibility(View.VISIBLE);
		ServerSync serverSync = new ServerSync(this);
		serverSync.fetchMyEmployeeDetails(progressBar, this);	
	}
}
