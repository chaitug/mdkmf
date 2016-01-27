package in.vasista.location;

import in.vasista.vsales.db.LocationsDataSource;

import java.text.SimpleDateFormat;
import java.util.List;

import android.Manifest;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

public class LocationSaveService extends IntentService {
	public static String TAG = "LOCATION_SAVE_SERVICE";

	public LocationSaveService() {
		super("LocationSaveService");
	}

	public LocationSaveService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		addNewLocation();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private void addNewLocation() {
		Context context = getApplicationContext();
		Log.d(TAG, "Alarm!!!!!");

		LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		List<String> providers = manager.getAllProviders();
		android.location.Location latestLocation = null;

		for (int i = 0; i < providers.size(); i++) {
			android.location.Location loc;
			if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED  ) {
				// TODO: Consider calling
				//    ActivityCompat#requestPermissions
				// here to request the missing permissions, and then overriding
				//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
				//                                          int[] grantResults)
				// to handle the case where the user grants the permission. See the documentation
				// for ActivityCompat#requestPermissions for more details.
				return;
			}
			loc = manager.getLastKnownLocation(providers.get(i));
			if (loc == null) {
	    		  continue;
	    	  }
	          if (latestLocation == null) {
	        	  latestLocation = loc;
	          }
	          if (loc.getTime() > latestLocation.getTime()) {
	        	  latestLocation = loc;
	          }
	      }
	      if (latestLocation != null) {
	    	  SimpleDateFormat timeFormat = new SimpleDateFormat("dd MMM, yyyy HH:mm:ss");
	    	  String timeStr = timeFormat.format(latestLocation.getTime());
	    	  Log.d(TAG, "Alarm!!! " + latestLocation.getLatitude() + " " + latestLocation.getLongitude() + " " + timeStr);
	    	  LocationsDataSource datasource = new LocationsDataSource(context);
	    	  datasource.open();
	    	  long locationId = datasource.insertLocation(latestLocation.getLatitude(), latestLocation.getLongitude(), latestLocation.getTime());
	    	  datasource.close();
	      }
	  }
	  
	  @Override
	  public int onStartCommand(Intent intent, int flags, int startId) {
	    // Retrieve the shared preferences  
	    Context context = getApplicationContext();
	    SharedPreferences prefs = 
	      PreferenceManager.getDefaultSharedPreferences(context);

	    int updateFreq = Integer.parseInt(prefs.getString("PREF_LOCATION_SAVE_FREQ", "15"));
	    boolean autoSaveChecked = prefs.getBoolean("PREF_LOCATION_AUTO_SAVE", false);

	    if (autoSaveChecked) {
	      int alarmType = AlarmManager.ELAPSED_REALTIME_WAKEUP;
	      long timeToRefresh = SystemClock.elapsedRealtime() +
	                           updateFreq*60*1000;
	      alarmManager.setInexactRepeating(alarmType, timeToRefresh,
	                                       updateFreq*60*1000, alarmIntent); 
	    }
	    else {
	      alarmManager.cancel(alarmIntent);    
	    }
	    return super.onStartCommand(intent, flags, startId);
	  };


	  private AlarmManager alarmManager;
	  private PendingIntent alarmIntent;

	  @Override
	  public void onCreate() {  
	    super.onCreate();
	    alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

	    String ALARM_ACTION =
	      LocationAlarmReceiver.ACTION_SAVE_LOCATION_ALARM;  
	    Intent intentToFire = new Intent(ALARM_ACTION);
	    alarmIntent =
	      PendingIntent.getBroadcast(this, 0, intentToFire, 0);
	  }

}
