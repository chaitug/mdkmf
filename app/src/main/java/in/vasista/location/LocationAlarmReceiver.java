package in.vasista.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class LocationAlarmReceiver extends BroadcastReceiver {

	public static final String ACTION_SAVE_LOCATION_ALARM = 
		      "in.vasista.vpos.location.ACTION_SAVE_LOCATION_ALARM";
	@Override
	public void onReceive(Context context, Intent intent) {
Toast.makeText(context, "LocationAlarmReceiver Started", Toast.LENGTH_SHORT).show();
	    Intent startIntent = new Intent(context, LocationSaveService.class);
	    context.startService(startIntent);  
	}

}
