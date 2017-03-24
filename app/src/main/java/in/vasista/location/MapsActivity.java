package in.vasista.location;

import android.Manifest;
import android.content.Context;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import in.vasista.helper.DatePickerFragment;
import in.vasista.milkosoft.mdkmf.R;
import in.vasista.vsales.DashboardAppCompatActivity;
import in.vasista.vsales.adapter.FacilityAutoAdapter;
import in.vasista.vsales.db.FacilityDataSource;
import in.vasista.vsales.db.LocationsDataSource;
import in.vasista.vsales.facility.Facility;
import in.vasista.vsales.sync.ServerSync;


public class MapsActivity extends DashboardAppCompatActivity
        implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        MapItemFragment.OnListFragmentInteractionListener, CompoundButton.OnCheckedChangeListener {

//    int locationPermissionCheck = ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);

    private GoogleMap mMap;
    int currentPt;
    ArrayList<LatLng> latlngs;
    ArrayList<Marker> mo;

    List<Location> locations;

    View infoWindow;

    SimpleDateFormat dateFormat;

    ScrollView scrollView;
    View overLay;
    AutoCompleteTextView noteName;
    EditText noteInfo;

    FloatingActionButton fab;

    LocationsDataSource datasource;
    SupportMapFragment mapFragment;
    public MapItemFragment recyclerViewFragment;
    SwitchCompat switchView;

    GoogleApiClient googleApiClient;
    LocationRequest locationRequest;
    static final int REQUEST_CODE_FINELOCATION = 1;

    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the child view
        setContentChildView(R.layout.activity_maps);

        // Set the Actionbar properties.
        actionBarHomeEnabled();
        setTitle("Field Notes");
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }
            }
        }

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Get Location form GPS
        getLocationviaGPS();

        // New note view initialization.
        scrollView = (ScrollView) findViewById(R.id.noteEntryView);
        overLay = findViewById(R.id.overlay);
        noteName = (AutoCompleteTextView) findViewById(R.id.noteName);
        noteInfo = (EditText) findViewById(R.id.noteInfo);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        recyclerViewFragment = (MapItemFragment) getSupportFragmentManager().findFragmentById(R.id.maplist_fragment);

        // Date format for the UI rendering
        dateFormat = new SimpleDateFormat(this.getString(R.string.date_format), Locale.getDefault());

        // ************* FAB icon *************************
        fab = (FloatingActionButton) findViewById(R.id.fab);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_location_24dp, this.getTheme()));
        } else {
            fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_location_24dp));
        }

        fab.show();
        fab.setOnClickListener(this);
        // ************* FAB icon *************************

        findViewById(R.id.recordCancel).setOnClickListener(this);
        findViewById(R.id.recordNote).setOnClickListener(this);
        findViewById(R.id.clearFilter).setOnClickListener(this);

        // get the view of the Switch
        switchView = (SwitchCompat) findViewById(R.id
                .switch_view);
        // Get the user options that is saved in SharedPrefs
        boolean map = prefs.getBoolean("location_map",true);

        // Based on user option, set the state of the switch
        switchView.setChecked(map);

        // OnChecked change listener
        switchView.setOnCheckedChangeListener(this);

        // Update UI Based on user options "location_map"
        if (map){
            try {
                if (mapFragment.getView() != null) {
                    mapFragment.getView().setVisibility(View.VISIBLE);
                }
                if (recyclerViewFragment.getView() != null) {
                    recyclerViewFragment.getView().setVisibility(View.GONE);
                }
            }catch (NullPointerException e){
                e.printStackTrace();
            }

        }else{
            if (recyclerViewFragment.getView() != null) {
                recyclerViewFragment.getView().setVisibility(View.VISIBLE);
            }
            if (mapFragment.getView() != null) {
                mapFragment.getView().setVisibility(View.GONE);
            }
        }

    }

    @Override
    protected void onResume() {
        FacilityDataSource facilityDS = new FacilityDataSource(this);
        facilityDS.open();
        List<Facility> facilityList = facilityDS.getAllFacilities();
        facilityDS.close();
        final FacilityAutoAdapter adapter = new FacilityAutoAdapter(this, R.layout.autocomplete_item, facilityList);
        noteName.setAdapter(adapter);
        noteName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(noteName.getWindowToken(), 0);
                noteName.clearFocus();
                Facility retailer =  (Facility)parent.getItemAtPosition(position);
                noteName.setText(retailer.getId());

            }

        });
        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    // HERE WE CAN RESTRICT THE USE TO SHOW POPUP WITHOUT PERMISSIONS
                    Toast.makeText(getApplicationContext(), "Permission Denied, You cannot access data.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);

        // Mark Locations - null means "No Filter"
        Date today = Calendar.getInstance().getTime();
        markCurrentLocations(today);

    }

    /**
     * Draw a line between latlongs
     * @param listLocsToDraw List of LatLongs
     */
    private void drawPrimaryLinePath(ArrayList<LatLng> listLocsToDraw) {
        if (mMap == null) {
            return;
        }

        if (listLocsToDraw.size() < 2) {
            return;
        }

        PolylineOptions options = new PolylineOptions();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            options.color(getApplicationContext().getColor(R.color.colorAccent));
        } else {
            options.color( getApplicationContext().getResources().getColor(R.color.colorAccent));
        }

        options.width(3);
        options.visible(true);

        for (LatLng locRecorded : listLocsToDraw) {
            options.add(locRecorded);
        }

        mMap.addPolyline(options);

    }

    // Camera handling - zoom in camera to marker about 5 seconds with 16.0 zoom level
    GoogleMap.CancelableCallback MyCancelableCallback =
        new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {

                if (++currentPt < latlngs.size()) {

                    //Get the current location
                    android.location.Location startingLocation = new android.location.Location("starting point");
                    startingLocation.setLatitude(mMap.getCameraPosition().target.latitude);
                    startingLocation.setLongitude(mMap.getCameraPosition().target.longitude);

                    //Get the target location
                    android.location.Location endingLocation = new android.location.Location("ending point");
                    endingLocation.setLatitude(latlngs.get(currentPt).latitude);
                    endingLocation.setLongitude(latlngs.get(currentPt).longitude);

                    //Find the Bearing from current location to next location
                    float targetBearing = startingLocation.bearingTo(endingLocation);

                    LatLng targetLatLng = latlngs.get(currentPt);
                    float targetZoom = 16.0f;
                    mo.get(currentPt).showInfoWindow();
                    //Create a new CameraPosition
                    CameraPosition cameraPosition =
                            new CameraPosition.Builder()
                                    .target(targetLatLng)
                                    .bearing(targetBearing)
                                    .zoom(targetZoom)
                                    .build();

                    mMap.animateCamera(
                            CameraUpdateFactory.newCameraPosition(cameraPosition),
                            5000,
                            MyCancelableCallback);

                }
            }

            @Override
            public void onCancel() {

            }
        };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.refresh, menu);

        // Added new menu item for Location.
        menu.add(0,R.id.action_filter,1,R.string.action_filter);
        menu.getItem(1).setIcon(R.drawable.ic_filter)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        menu.getItem(0).setTitle("Update locations");
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.action_refresh:

                item.setActionView(R.layout.progressbar);
                ProgressBar progressBar=(ProgressBar)item.getActionView().findViewById(R.id.menuitem_progress);
                ServerSync serverSync = new ServerSync(this);
                serverSync.fetchPrevLocations(item,progressBar,this);

                // hide keyboard
                hideKeyboard();
                findViewById(R.id.clearFilter).setVisibility(View.GONE);
                //showDatePickerDialog();

                return true;

            case R.id.action_filter:
                item.setCheckable(true);
                item.setChecked(true);
                showDatePickerDialog();
                return true;
        }
        return false;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        return false;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.clearFilter:
                // Mark Locations
                markLocations(null);
                recyclerViewFragment.markLocations(null);
                v.setVisibility(View.GONE);
                break;
            case R.id.fab:
                fab.hide();
                noteName.setText("");
                noteInfo.setText("");
                overLay.setVisibility(View.VISIBLE);
                scrollView.setVisibility(View.VISIBLE);
                scrollView.setVisibility(View.VISIBLE);

                break;
            case R.id.recordCancel:
                // Reset errors.
                noteName.setError(null);
                noteInfo.setError(null);

                overLay.setVisibility(View.GONE);
                scrollView.setVisibility(View.GONE);

                fab.show();
                break;

            case R.id.recordNote:


                saveLocation();
                syncWithServer();
                overLay.setVisibility(View.GONE);
                scrollView.setVisibility(View.GONE);

                fab.show();
                break;

        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // Display the error code on failure
        Toast.makeText(this, "Connection Failure : " + connectionResult.getErrorCode(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnected(Bundle arg0) {
        // Display the connection status
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_FINELOCATION);
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(android.location.Location location) {
        // Report to the UI that the location was updated
//        String msg = "Updated Location: " +
//                Double.toString(location.getLatitude()) + "," +
//                Double.toString(location.getLongitude());
//        LocationsDataSource datasource = new LocationsDataSource(this);
//        datasource.open();
        //long locationId = datasource.insertLocation(location.getLatitude(), location.getLongitude(), location.getTime());
//        datasource.close();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }


    @Override
    public void onListFragmentInteraction(Location item) {
        // Get the index of the item based on logic
        mo.get(locations.size()-recyclerViewFragment.locations.indexOf(item)-1)
                .showInfoWindow();
        switchView.setChecked(true);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(item.getLatitude(), item.getLongitude()), 14));
    }

    // Switch change
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        SharedPreferences.Editor prefEditor = prefs.edit();
        prefEditor.putBoolean("location_map",isChecked);
        prefEditor.apply();
        fab.show();
        if (isChecked){
            try {
                if (mapFragment.getView() != null) {
                    mapFragment.getView().setVisibility(View.VISIBLE);
                }
                if (recyclerViewFragment.getView() != null) {
                    recyclerViewFragment.getView().setVisibility(View.GONE);
                }
            }catch (NullPointerException e){
                e.printStackTrace();
            }

        }else{
            if (recyclerViewFragment.getView() != null) {
                recyclerViewFragment.getView().setVisibility(View.VISIBLE);
            }
            if (mapFragment.getView() != null) {
                mapFragment.getView().setVisibility(View.GONE);
            }
        }
    }

    /**
     * Save note with location locally - DB
     */
    public void saveLocation(){

        String noteNameStr = noteName.getText().toString();
        String noteInfoStr = noteInfo.getText().toString();

        if (googleApiClient != null && googleApiClient.isConnected()) {
            try {
                getLocationviaGPS();

                /**
                 * Validation
                 */
                // Reset errors.
                noteName.setError(null);
                noteInfo.setError(null);

                // Store values at the time of the login attempt.
                String name = noteName.getText().toString();
                String detail = noteInfo.getText().toString();

                boolean cancel = false;
                View focusView = null;

                // Check for a valid email address.
                if (TextUtils.isEmpty(name)) {
                    noteName.setError(getString(R.string.error_field_required));
                    focusView = noteName;
                    cancel = true;
                } else if (TextUtils.isEmpty(detail)) {
                    noteInfo.setError(getString(R.string.error_field_required));
                    focusView = noteInfo;
                    cancel = true;
                }

                if (cancel) {
                    // There was an error; don't attempt login and focus the first
                    // form field with an error.
                    focusView.requestFocus();
                    return;
                }
                /**
                 * Validation ended
                 */

                if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                android.location.Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
//                String msg = "Current Location: " +
//                        Double.toString(location.getLatitude()) + "," +
//                        Double.toString(location.getLongitude());
                Geocoder geocoder;

                List<Address> addresses;
                geocoder = new Geocoder(this, Locale.getDefault());
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                String address = addresses.get(0).getAddressLine(0) + "," +addresses.get(0).getAddressLine(1); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getAddressLine(2);
                String Currentaddress= address+" , "+ knownName+" , "+city+" , "+state+" , "+country+" , "+postalCode;
                Log.v("current address",Currentaddress);
                LocationsDataSource datasource = new LocationsDataSource(MapsActivity.this);
                datasource.open();
                datasource.insertLocation(location.getLatitude(), location.getLongitude(),
                        location.getTime(), noteNameStr, noteInfoStr,Currentaddress);
                datasource.close();
//					Toast.makeText(thisActivity, msg + locationId, Toast.LENGTH_SHORT).show();

                // Hide the Keyboard
                hideKeyboard();

                // Show info at the bottom
                showSnackBar(getString(R.string.saved_location));


            }catch (Exception e){
                // Hide the Keyboard
                hideKeyboard();

                // Show info at the bottom
                showSnackBar(getString(R.string.exception_location));
            }

        }
    }

    /**
     * Sync saved note to server.
     */
    public void syncWithServer(){
        ServerSync serverSync = new ServerSync(this);
        serverSync.syncLocations(null,null,this);

        // hide keyboard
        hideKeyboard();
    }

    /**
     * Place markers on the Map based on filtering.
     * @param date - Date filtered
     */
    public final void markLocations(Date date){
        if(date!=null) {
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM");
            String format = formatter.format(date);
            setTitle("Field notes " + "("+format+")");
        }
        mMap.clear();


        MarkerOptions options = new MarkerOptions();
        latlngs = new ArrayList<>();
        mo = new ArrayList<>();

        ArrayList<Integer> indexes = new ArrayList<>();


        datasource = new LocationsDataSource(this);
        datasource.open();
        locations = datasource.getSyncedLocations();


        for (Location location:locations){

            if (date != null) {

                findViewById(R.id.clearFilter).setVisibility(View.VISIBLE);
                Calendar cal1 = Calendar.getInstance();
                Calendar cal2 = Calendar.getInstance();
                cal1.setTime(location.getCreatedDate());
                cal2.setTime(date);


                boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                        cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
                        &&  cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);


               if (!sameDay){
                  continue;
               }

            }
            // New indexes
            indexes.add(locations.indexOf(location));

            latlngs.add(new LatLng(location.getLatitude(), location.getLongitude()));

            options.position(new LatLng(location.getLatitude(), location.getLongitude()));

            Marker marker = mMap.addMarker(options);
            mo.add(marker);

        }
        if (latlngs.size() == 0){
            showSnackBar("No locations found.");
            return;
        }

        // Draw a line between markers.
        drawPrimaryLinePath(latlngs);

        // Update new locations
        ArrayList<Location> newLocations = new ArrayList<>();
        for (int index:indexes){
            newLocations.add(locations.get(index));
        }

        locations = newLocations;

        currentPt = -1;
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                new LatLng(latlngs.get(0).latitude, latlngs.get(0).longitude), 14);

        mMap.animateCamera(
                cameraUpdate,
                2000,
                MyCancelableCallback);

        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(mo));
        mMap.setOnMarkerClickListener(this);

    }


    public final void markCurrentLocations(Date date){
        if(date!=null) {
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM");
            String format = formatter.format(date);
            setTitle("Field notes " + "("+format+")");
        }
        mMap.clear();


        MarkerOptions options = new MarkerOptions();
        latlngs = new ArrayList<>();
        mo = new ArrayList<>();

        ArrayList<Integer> indexes = new ArrayList<>();


        datasource = new LocationsDataSource(this);
        datasource.open();
        locations = datasource.getSyncedLocations();


        for (Location location:locations){

            if (date != null) {

                findViewById(R.id.clearFilter).setVisibility(View.VISIBLE);
                Calendar cal1 = Calendar.getInstance();
                Calendar cal2 = Calendar.getInstance();
                cal1.setTime(location.getCreatedDate());
                cal2.setTime(date);


                boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                        cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
                        &&  cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);


                if (!sameDay){
                    continue;
                }

            }
            // New indexes
            indexes.add(locations.indexOf(location));

            latlngs.add(new LatLng(location.getLatitude(), location.getLongitude()));

            options.position(new LatLng(location.getLatitude(), location.getLongitude()));

            Marker marker = mMap.addMarker(options);
            mo.add(marker);

        }
        if (latlngs.size() == 0){
            showSnackBar("No locations found.");
            return;
        }

        // Draw a line between markers.
        drawPrimaryLinePath(latlngs);

        // Update new locations
        ArrayList<Location> newLocations = new ArrayList<>();
        for (int index:indexes){
            newLocations.add(locations.get(index));
        }

        locations = newLocations;

        currentPt = -1;
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                new LatLng(latlngs.get(0).latitude, latlngs.get(0).longitude), 14);

        mMap.animateCamera(
                cameraUpdate,
                2000,
                MyCancelableCallback);

        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(mo));
        mMap.setOnMarkerClickListener(this);

    }





    /**
     * Show Data Picker
     */
    public void showDatePickerDialog() {
        DialogFragment newFragment = DatePickerFragment.newInstance(this);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    /**
     * Get Location Vis GPS
     */
    private void getLocationviaGPS() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
            googleApiClient.connect();

            locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            locationRequest.setSmallestDisplacement(10);


            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            //**************************
            builder.setAlwaysShow(true); //this is the key ingredient
            //**************************

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());

            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(@NonNull LocationSettingsResult result) {

                    final Status status = result.getStatus();
//					final LocationSettingsStates state = result.getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            // All location settings are satisfied. The client can initialize location
                            // requests here.
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(
                                        MapsActivity.this, 1000);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            });
        }
    }


    /**
     * For custom InfoWindow view.
     */
    public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        ArrayList<Marker> markers;
        public CustomInfoWindowAdapter(ArrayList<Marker> mo) {
            this.markers = mo;
            infoWindow = getLayoutInflater().inflate(R.layout.maps_infowindow, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {

            int position = markers.indexOf(marker);
            int total = markers.size();

            Location location = locations.get(position);

            TextView tvTitle = ((TextView)infoWindow.findViewById(R.id.infoHeading));
            tvTitle.setText(String.format(getResources().getString(R.string.text_map_infowindow), (total-position), location.getNoteName()));

            TextView desc = ((TextView)infoWindow.findViewById(R.id.infoDesc));
            desc.setText(location.getNoteInfo());

            TextView date = ((TextView)infoWindow.findViewById(R.id.infodate));
            date.setText(dateFormat.format(location.getCreatedDate()));

            return infoWindow;
        }
    }

}
