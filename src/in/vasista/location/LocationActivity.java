package in.vasista.location;

import java.text.SimpleDateFormat;


import in.vasista.vsales.sync.ServerSync;
import in.vasista.vsales.db.LocationsDataSource;
import in.vasista.vsales.R;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class LocationActivity extends Activity implements
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener, LocationListener{

	   LocationClient mLocationClient;
	   LocationRequest mLocationRequest;
	   
	   private TextView locationLabel;
	   private Button getLocationBtn;
	   private Button disconnectBtn;
	   private Button connectBtn;

	   @Override
	   protected void onCreate(Bundle savedInstanceState) {
	      super.onCreate(savedInstanceState);
	      setContentView(R.layout.activity_location);

	      locationLabel = (TextView) findViewById(R.id.locationLabel);
	      getLocationBtn = (Button) findViewById(R.id.getLocation);

	      getLocationBtn.setOnClickListener(new View.OnClickListener() {
	         public void onClick(View view) {
	            displayCurrentLocation();
	         }
	      });
	      disconnectBtn = (Button) findViewById(R.id.disconnect);  
	      disconnectBtn.setOnClickListener(new View.OnClickListener() {
	         public void onClick(View view) {
	            mLocationClient.disconnect();
	            locationLabel.setText("Got disconnected....");
	         }
	      });
	      connectBtn = (Button) findViewById(R.id.connect);  
	      connectBtn.setOnClickListener(new View.OnClickListener() {
	         public void onClick(View view) {
	            mLocationClient.connect();
	            locationLabel.setText("Got connected....");
	         }
	      });	
	      	final LocationActivity thisActivity = this;
			final Button button = (Button) findViewById(R.id.syncLocationsButton);
			button.setOnClickListener(new OnClickListener() { 
				public void onClick(View view) {
					//Toast.makeText( getActivity(), "Updating product catalog...", Toast.LENGTH_SHORT ).show();	    		    
					ProgressBar progressBar = (ProgressBar) thisActivity.findViewById(R.id.locationsSyncProgress);
					progressBar.setVisibility(View.VISIBLE);
					ServerSync serverSync = new ServerSync(thisActivity);
					serverSync.syncLocations(progressBar);					
				}
			});
			
	      // Create the LocationRequest object
	      mLocationClient = new LocationClient(this, this, this);	
	      
	      mLocationRequest = LocationRequest.create();
	        // Use high accuracy
	        mLocationRequest.setPriority(
	                LocationRequest.PRIORITY_HIGH_ACCURACY);
	        // Set the update interval to 30 seconds
	        mLocationRequest.setInterval(30*1000); //::TODO::
	        // Set the fastest update interval to 1 second
	        mLocationRequest.setFastestInterval(5*1000); //::TODO::
	        mLocationRequest.setSmallestDisplacement(10); //::TODO::
	   }
	   
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		// TODO Auto-generated method stub
	      // Display the error code on failure
	      Toast.makeText(this, "Connection Failure : " + 
	      connectionResult.getErrorCode(),
	      Toast.LENGTH_SHORT).show();		
	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
	      // Display the connection status
	      Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();		
	      
          mLocationClient.requestLocationUpdates(mLocationRequest, this);

	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
	      // Display the connection status
	      Toast.makeText(this, "Disconnected. Please re-connect.",
	      Toast.LENGTH_SHORT).show();		
		
	}
	   public void displayCurrentLocation() {
		      // Get the current location's latitude & longitude
		      Location currentLocation = mLocationClient.getLastLocation();
		      String msg = "Current Location: " +
		      Double.toString(currentLocation.getLatitude()) + "," +
		      Double.toString(currentLocation.getLongitude());
		     
		      // Display the current location in the UI
		      locationLabel.setText(msg);
		      
		   }

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
        // Report to the UI that the location was updated
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
	    LocationsDataSource datasource = new LocationsDataSource(this);
	    datasource.open();
	    long locationId = datasource.insertLocation(location.getLatitude(), location.getLongitude(), location.getTime());
	    datasource.close();
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
		
	}
}
