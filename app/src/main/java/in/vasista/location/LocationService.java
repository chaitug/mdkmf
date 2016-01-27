package in.vasista.location;

import android.app.IntentService;
import android.content.Intent;

public class LocationService extends IntentService {
	/**
	 * Creates an IntentService.  Invoked by your subclass's constructor.
	 *
	 * @param name Used to name the worker thread, important only for debugging.
	 */
	public LocationService(String name) {
		super(name);
	}



	private String TAG = this.getClass().getSimpleName();
	public LocationService() {
		super("Fused Location");
	}
	//	public LocationService(String name) {
//		super("Fused Location");
//	}
	@Override
	protected void onHandleIntent(Intent intent) {
//		Location location = intent.getParcelableExtra(LocationClient.KEY_LOCATION_CHANGED);
//		if(location !=null){
//			Log.i(TAG, "onHandleIntent Current Location " + location.getLatitude() + "," + location.getLongitude());
//		}
	}

}