package in.vasista.vsales;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import in.vasista.vsales.db.EmployeeDataSource;
import in.vasista.vsales.employee.Employee;

public class MyEmployeeDetailsActivity extends DashboardAppCompatActivity  {
	public static final String module = MyEmployeeDetailsActivity.class.getName();	
	
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
		TextView employeeWeeklyOffView = (TextView)findViewById(R.id.employeeWeeklyOff);
		employeeWeeklyOffView.setText(employee.getWeeklyOff());			
		TextView phoneView = (TextView)findViewById(R.id.employeePhone); 
		phoneView.setText(employee.getPhoneNum()); 	 
			
	}
	
	public void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);

		// Inflate your view   
		setContentChildView(R.layout.myemployeedetails_layout);
		actionBarHomeEnabled();
		final Activity myActivity = this; 
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this); 
		String employeeId = "";		
    	prefs = PreferenceManager.getDefaultSharedPreferences(this);
    	employeeId = prefs.getString("employeeId", "");
   	    	
Log.d(module, "employeeId=" + employeeId);	    	
		Employee employee =null;
		if (! employeeId.isEmpty()) { 
			EmployeeDataSource datasource = new EmployeeDataSource(this);  
			datasource.open();
			employee = datasource.getEmployeeDetails(employeeId);		
			datasource.close();
		}
		if (employee == null) {    
			TextView nameView = (TextView)findViewById(R.id.employeeName);
			nameView.setText("<No employee mapping found>");				
			return;
		}   
		updateEmployeeDetails(employee);		
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
}
