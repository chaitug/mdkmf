package in.vasista.location;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import in.vasista.milkosoft.mdkmf.R;
import in.vasista.vsales.db.LocationsDataSource;
import in.vasista.vsales.sync.ServerSync;

public class LocationActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
	public static String TAG = "LocationActivity";

	//private LocationClient mLocationClient;
	private GoogleApiClient mGoogleApiClient;
	private LocationRequest mLocationRequest;
	private PendingIntent mPendingIntent;
	private String Currentaddress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location);

		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addApi(LocationServices.API)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.build();

		Intent intentService = new Intent(this, LocationService.class);
		mPendingIntent = PendingIntent.getService(this, 1, intentService, 0);
		mLocationRequest = LocationRequest.create();
		// Use high accuracy
		mLocationRequest.setPriority(
				LocationRequest.PRIORITY_HIGH_ACCURACY);
		// Set the update interval to 30 seconds
		mLocationRequest.setInterval(30*1000); //::TODO::
		// Set the fastest update interval to 1 second
		mLocationRequest.setFastestInterval(5*1000); //::TODO::
		mLocationRequest.setSmallestDisplacement(10); //::TODO::

		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return;
		}
		LocationServices.FusedLocationApi.requestLocationUpdates(
				mGoogleApiClient, mLocationRequest, this);

		int resp =GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if(resp == ConnectionResult.SUCCESS){
//			mLocationClient = new LocationClient(this,this,this);
//			mLocationClient.connect();
			mGoogleApiClient.connect();
		}
		else{
			Toast.makeText(this, "Google Play Service Error " + resp, Toast.LENGTH_LONG).show();
		}
		final LocationActivity thisActivity = this;

		final Button recordNoteBtn;
		recordNoteBtn = (Button) findViewById(R.id.recordNote);

		recordNoteBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
//				if(mLocationClient != null && mLocationClient.isConnected()){
//					// Get the current location's latitude & longitude
//					TextView noteName = (TextView)recordNoteBtn.getRootView().findViewById(R.id.noteName);
//					String noteNameStr = noteName.getText().toString();
//					TextView noteInfo = (TextView)recordNoteBtn.getRootView().findViewById(R.id.noteInfo);
//					String noteInfoStr = noteInfo.getText().toString();
//					Location location = mLocationClient.getLastLocation();
//					String msg = "Current Location: " +
//							Double.toString(location.getLatitude()) + "," +
//							Double.toString(location.getLongitude());
//					LocationsDataSource datasource = new LocationsDataSource(thisActivity);
//					datasource.open();
//					long locationId = datasource.insertLocation(location.getLatitude(), location.getLongitude(),
//							location.getTime(), noteNameStr, noteInfoStr);
//					datasource.close();
//					Toast.makeText(thisActivity, msg, Toast.LENGTH_SHORT).show();
//				}
			}
		});

		Button getLocationBtn;
		getLocationBtn = (Button) findViewById(R.id.getLocation);

		getLocationBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
//				if(mLocationClient != null && mLocationClient.isConnected()){
//					displayCurrentLocation();
//				}
			}
		});

		Button start = (Button) findViewById(R.id.start);
		start.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
//				if(mLocationClient != null && mLocationClient.isConnected()){
//					Log.i(TAG, "start button handler ");
//					mLocationClient.requestLocationUpdates(mLocationRequest, mPendingIntent);
//				}
			}
		});

		Button stop = (Button) findViewById(R.id.stop);
		stop.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
//				if(mLocationClient != null && mLocationClient.isConnected()){
//					Log.i(TAG, "stop button handler ");
//					mLocationClient.removeLocationUpdates(mPendingIntent);
//				}
			}
		});

		final Button button = (Button) findViewById(R.id.syncLocationsButton);
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				//Toast.makeText( getActivity(), "Updating product catalog...", Toast.LENGTH_SHORT ).show();	    		    
				ProgressBar progressBar = (ProgressBar) thisActivity.findViewById(R.id.locationsSyncProgress);
				progressBar.setVisibility(View.VISIBLE);
				ServerSync serverSync = new ServerSync(thisActivity);
				serverSync.syncLocations(null,progressBar,null);
			}
		});
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
	}

	@Override
	public void onConnectionSuspended(int i) {
		Toast.makeText(this, "Disconnected. Please re-connect.",
				Toast.LENGTH_SHORT).show();
	}

	public void displayCurrentLocation() {
		// Get the current location's latitude & longitude
//		Location currentLocation = mLocationClient.getLastLocation();
//		String msg = "Current Location: " +
//				Double.toString(currentLocation.getLatitude()) + "," +
//				Double.toString(currentLocation.getLongitude());
//
//		// Display the current location in the UI
//		TextView locationLabel;
//		locationLabel = (TextView) findViewById(R.id.locationLabel);
//		locationLabel.setText(msg);
	}

	@Override
	public void onLocationChanged(Location location) {
		// Report to the UI that the location was updated
		String msg = "Updated Location: " +
				Double.toString(location.getLatitude()) + "," +
				Double.toString(location.getLongitude());
		LocationsDataSource datasource = new LocationsDataSource(this);
		datasource.open();

		Geocoder geocoder;
        Double lat=location.getLatitude();
		Double lng=location.getLongitude();
		geocoder = new Geocoder(this, Locale.getDefault());
		try {
			List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
			String address = addresses.get(0).getAddressLine(0) + "," +addresses.get(0).getAddressLine(1); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
			String city = addresses.get(0).getLocality();
			String state = addresses.get(0).getAdminArea();
			String country = addresses.get(0).getCountryName();
			String postalCode = addresses.get(0).getPostalCode();
			String knownName = addresses.get(0).getAddressLine(2);
			String Currentaddress= address+" , "+ knownName+" , "+city+" , "+state+" , "+country+" , "+postalCode;
			Log.v("current address",Currentaddress);
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		long locationId = datasource.insertLocation(location.getLatitude(), location.getLongitude(),Currentaddress,location.getTime());
		datasource.close();
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
//		if(mLocationClient != null)
//			mLocationClient.disconnect();
	}
}
