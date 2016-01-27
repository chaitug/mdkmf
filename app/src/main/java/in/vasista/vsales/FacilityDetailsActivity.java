package in.vasista.vsales;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.maps.SupportMapFragment;


import in.vasista.vsales.db.FacilityDataSource;
import in.vasista.vsales.facility.Facility;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class FacilityDetailsActivity extends DashboardActivity {

	MapView mapView;
	GoogleMap map;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Inflate your view 
		setContentView(R.layout.facilitydetails_layout);

		Intent facilityDetailsIntent = getIntent();
		String facilityId = "";
		facilityId = facilityDetailsIntent.getStringExtra("facilityId");
		final Facility facility;
		FacilityDataSource datasource = new FacilityDataSource(this);
		datasource.open();
		facility = datasource.getFacilityDetails(facilityId);
		datasource.close();
		if (facility == null) {
			return;
		}
		TextView retailerHeaderView = (TextView) findViewById(R.id.retailerIdHeader);
		retailerHeaderView.setText(facilityId + " Details");
		TextView idView = (TextView) findViewById(R.id.facilityId);
		idView.setText(facilityId);
		TextView nameView = (TextView) findViewById(R.id.facilityName);
		nameView.setText(facility.getName());
		TextView categoryView = (TextView) findViewById(R.id.facilityCategory);
		categoryView.setText(facility.getCategory());
		TextView salesRepView = (TextView) findViewById(R.id.facilitySalesRep);
		salesRepView.setText(facility.getSalesRep());
		TextView amRouteView = (TextView) findViewById(R.id.facilityAmRoute);
		amRouteView.setText(facility.getAmRouteId());
		TextView pmRouteView = (TextView) findViewById(R.id.facilityPmRoute);
		pmRouteView.setText(facility.getPmRouteId());
		TextView phoneView = (TextView) findViewById(R.id.facilityPhone);
		phoneView.setText(facility.getOwnerPhone());

		map = ((com.google.android.gms.maps.MapFragment) getFragmentManager().findFragmentById(R.id.map_view)).getMap();
		if (map != null) {
			String latStr = facility.getLatitude();
			String longStr = facility.getLongitude();
			double latitude = 13.095042; // default MD coordinates
			double longitude = 77.573120;
			if (latStr != null && !latStr.isEmpty() && longStr != null && !longStr.isEmpty()) {
				latitude = Double.valueOf(latStr);
				longitude = Double.valueOf(longStr);
				map.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude))
						.title(facilityId)).showInfoWindow();
			}
			//map.getUiSettings().setMyLocationButtonEnabled(false);
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
			map.setMyLocationEnabled(true);
			MapsInitializer.initialize(this);
			CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 12);
			map.animateCamera(cameraUpdate);
		}
		
		Button callBtn = (Button) findViewById(R.id.callButton);
   
		if (facility.getOwnerPhone() == null) {
			callBtn.setVisibility(View.GONE);
			return; 
		} 
		// add PhoneStateListener for monitoring
//		MyPhoneListener phoneListener = new MyPhoneListener();
//		TelephonyManager telephonyManager = 
//			(TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		// receive notifications of telephony state changes 
//		telephonyManager.listen(phoneListener,PhoneStateListener.LISTEN_CALL_STATE);
				
		callBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					// set the data
					String uri = "tel:" + "+91" + facility.getOwnerPhone();
					Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(uri));
					
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
