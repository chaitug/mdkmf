package in.vasista.vsales;


import java.text.SimpleDateFormat;
import java.util.Date;

import in.vasista.vsales.db.EmployeeDataSource;
import in.vasista.vsales.employee.Employee;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class EmployeeDetailsActivity extends DashboardActivity  {
	
	public void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);

		// Inflate your view    
		setContentView(R.layout.employeedetails_layout);    
		
		Intent employeeDetailsIntent= getIntent();
		String employeeId = "";
		employeeId = employeeDetailsIntent.getStringExtra("employeeId");	
		final Employee employee;
		
		EmployeeDataSource datasource = new EmployeeDataSource(this);  
		datasource.open();
		employee = datasource.getEmployeeDetails(employeeId);		
		datasource.close();
		if (employee == null) {        
			return;
		} 
		TextView employeeHeaderView = (TextView)findViewById(R.id.employeeIdHeader);
		//employeeHeaderView.setText(employee.getName());		
		TextView idView = (TextView)findViewById(R.id.employeeId);
		idView.setText(employeeId);
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
		
		
		Button callBtn = (Button) findViewById(R.id.callButton);
   
		if (employee.getPhoneNum() == null || employee.getPhoneNum().isEmpty()) {
			callBtn.setVisibility(View.GONE);
			return; 
		} 
		// add PhoneStateListener for monitoring
		MyPhoneListener phoneListener = new MyPhoneListener();
		TelephonyManager telephonyManager = 
			(TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		// receive notifications of telephony state changes 
		telephonyManager.listen(phoneListener,PhoneStateListener.LISTEN_CALL_STATE);
				
		callBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					// set the data
					String uri = "tel:"+employee.getPhoneNum();
					Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(uri));
					
					startActivity(callIntent);
				}catch(Exception e) {
					Toast.makeText(getApplicationContext(),"Your call has failed...",
						Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
			}
		});		
		


	}
	
	private class MyPhoneListener extends PhoneStateListener {
		 
		private boolean onCall = false;
 
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
 
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				// phone ringing...
				break;
			
			case TelephonyManager.CALL_STATE_OFFHOOK:
				// one call exists that is dialing, active, or on hold
				//because user answers the incoming call
				onCall = true;
				break;

			case TelephonyManager.CALL_STATE_IDLE: 
				// in initialization of the class and at the end of phone call 
				  
				// detect flag from CALL_STATE_OFFHOOK
				if (onCall == true) {    
 
					// restart our application
					Intent restart = getBaseContext().getPackageManager().
						getLaunchIntentForPackage(getBaseContext().getPackageName());
					restart.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(restart);
 
					onCall = false;
				}
				break;
			default:
				break;   
			}
			
		}
	}	
}