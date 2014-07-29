/*
 * Copyright (C) 2011 Wglxy.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package in.vasista.vsales;


import java.text.SimpleDateFormat;
import java.util.Date;

import in.vasista.vsales.db.EmployeeDataSource;
import in.vasista.vsales.employee.Employee;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;

/**
 * This is the activity for feature 1 in the dashboard application.
 * It displays some text and provides a way to get back to the home activity.
 *
 */

public class LeaveActivity extends DashboardActivity 
{

/**
 * onCreate
 *
 * Called when the activity is first created. 
 * This is where you should do all of your normal static set up: create views, bind data to lists, etc. 
 * This method also provides you with a Bundle containing the activity's previously frozen state, if there was one.
 * 
 * Always followed by onStart().
 *
 * @param savedInstanceState Bundle
 */

protected void onCreate(Bundle savedInstanceState) 
{
    super.onCreate(savedInstanceState);
    //setContentView (R.layout.activity_f1);
    //setTitleFromActivityLabel (R.id.title_text); 
	setContentView(R.layout.leave_layout);  
	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this); 
	String employeeId = "";		
	prefs = PreferenceManager.getDefaultSharedPreferences(this);
	employeeId = prefs.getString("employeeId", "");
	    	
	Employee employee =null;
	if (! employeeId.isEmpty()) { 
		EmployeeDataSource datasource = new EmployeeDataSource(this);  
		datasource.open();
		employee = datasource.getEmployeeDetails(employeeId);		
		datasource.close();
	}
	TextView leaveBalanceDateView = (TextView)findViewById(R.id.employeeLeaveBalanceHeader);
	
	if (employee == null) {    
		leaveBalanceDateView.setText("<No employee mapping found>");				
		return;
	}
	
    Date leaveBalanceDate = employee.getLeaveBalanceDate(); 
    if (leaveBalanceDate != null ) {
    	SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM, yyyy");	
    	String dateStr = dateFormat.format(leaveBalanceDate);
    	leaveBalanceDateView.setText("Leave Balance as of " + dateStr);	 	    
    }	
	TextView elView = (TextView)findViewById(R.id.employeeEarnedLeave);
	elView.setText(String.format("%.1f", employee.getEarnedLeave())); 	
	TextView clView = (TextView)findViewById(R.id.employeeCasualLeave);
	clView.setText(String.format("%.1f", employee.getCasualLeave()));
	TextView hplView = (TextView)findViewById(R.id.employeeHalfPayLeave);
	hplView.setText(String.format("%.1f", employee.getHalfPayLeave()));		
}
    
} // end class
