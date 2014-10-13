package in.vasista.location;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.LocationClient;

public class LocationService extends IntentService {

	private String TAG = this.getClass().getSimpleName();
	public LocationService() {
		super("Fused Location"); 
	}
	public LocationService(String name) {
		super("Fused Location");
	}
	@Override
	protected void onHandleIntent(Intent intent) {
		Location location = intent.getParcelableExtra(LocationClient.KEY_LOCATION_CHANGED);
		if(location !=null){
			Log.i(TAG, "onHandleIntent Current Location " + location.getLatitude() + "," + location.getLongitude());
		}
	}

}